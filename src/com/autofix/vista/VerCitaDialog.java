package com.autofix.vista;

import com.autofix.dao.CitaDAO;
import com.autofix.dao.ClienteDAO;
import com.autofix.dao.DetalleCitaDAO;
import com.autofix.dao.UsuarioDAO;
import com.autofix.modelo.Cita;
import com.autofix.modelo.Cliente;
import com.autofix.modelo.DetalleCita;
import com.autofix.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class VerCitaDialog extends JDialog {

    private int idCita;

    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private static final Color COLOR_FONDO = new Color(249, 250, 251);
    private static final Color COLOR_TEXTO = new Color(31, 41, 55);
    private static final Color COLOR_GRIS = new Color(107, 114, 128);

    public VerCitaDialog(Frame parent, int idCita) {
        super(parent, "Detalle de Cita", true);
        this.idCita = idCita;

        configurarDialogo();
        crearComponentes();
    }

    private void configurarDialogo() {
        setSize(550, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
    }

    private void crearComponentes() {
        setLayout(new BorderLayout());

        // Obtener datos
        CitaDAO citaDAO = new CitaDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        DetalleCitaDAO detalleCitaDAO = new DetalleCitaDAO();

        Cita cita = citaDAO.obtenerPorId(idCita);
        if (cita == null) {
            JOptionPane.showMessageDialog(this, "Cita no encontrada", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        Cliente cliente = clienteDAO.obtenerPorId(cita.getIdCliente());
        Usuario empleado = usuarioDAO.obtenerPorId(cita.getIdUsuario());
        List<DetalleCita> servicios = detalleCitaDAO.obtenerPorCita(idCita);

        // Panel titulo
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(COLOR_PRIMARIO);
        panelTitulo.setBorder(new EmptyBorder(20, 25, 20, 25));
        panelTitulo.setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("Cita #" + String.format("%03d", idCita));
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblEstado = new JLabel(cita.getEstado().toUpperCase());
        lblEstado.setFont(new Font("Arial", Font.BOLD, 14));
        lblEstado.setForeground(getColorEstado(cita.getEstado()));
        lblEstado.setOpaque(true);
        lblEstado.setBackground(Color.WHITE);
        lblEstado.setBorder(new EmptyBorder(5, 15, 5, 15));

        panelTitulo.add(lblTitulo, BorderLayout.WEST);
        panelTitulo.add(lblEstado, BorderLayout.EAST);

        add(panelTitulo, BorderLayout.NORTH);

        // Panel contenido
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(COLOR_FONDO);
        panelContenido.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Seccion Cliente
        panelContenido.add(crearSeccion("CLIENTE"));
        panelContenido.add(crearCampo("Nombre:", cliente != null ? cliente.getNombre() : "-"));
        panelContenido.add(crearCampo("Telefono:", cliente != null ? cliente.getTelefono() : "-"));
        panelContenido.add(Box.createRigidArea(new Dimension(0, 15)));

        // Seccion Vehiculo
        panelContenido.add(crearSeccion("VEHICULO"));
        panelContenido.add(crearCampo("Matricula:", cita.getMatricula() != null ? cita.getMatricula() : "-"));
        panelContenido.add(crearCampo("Modelo:", cita.getModeloCoche() != null ? cita.getModeloCoche() : "-"));
        panelContenido.add(Box.createRigidArea(new Dimension(0, 15)));

        // Seccion Cita
        panelContenido.add(crearSeccion("DETALLES DE LA CITA"));
        panelContenido.add(crearCampo("Fecha:", cita.getFecha().toString()));
        panelContenido.add(crearCampo("Hora:", cita.getHora().toString().substring(0, 5)));
        panelContenido.add(crearCampo("Empleado:", empleado != null ? empleado.getNombre() : "-"));
        panelContenido.add(Box.createRigidArea(new Dimension(0, 15)));

        // Seccion Servicios
        panelContenido.add(crearSeccion("SERVICIOS A REALIZAR"));
        if (servicios.isEmpty()) {
            panelContenido.add(crearCampo("", "Sin servicios registrados"));
        } else {
            for (DetalleCita detalle : servicios) {
                String servicio = "• " + detalle.getNombreServicio() + "  -  " +
                        String.format("%.2f €", detalle.getPrecio());
                panelContenido.add(crearCampoServicio(servicio));
            }
        }
        panelContenido.add(Box.createRigidArea(new Dimension(0, 10)));

        // Total
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.setBackground(COLOR_FONDO);
        panelTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lblTotal = new JLabel("TOTAL: " + String.format("%.2f €", cita.getPrecioFinal()));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(new Color(34, 197, 94));
        panelTotal.add(lblTotal);
        panelContenido.add(panelTotal);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 10)));

        // Notas
        if (cita.getNotas() != null && !cita.getNotas().isEmpty()) {
            panelContenido.add(crearSeccion("NOTAS"));
            JTextArea txtNotas = new JTextArea(cita.getNotas());
            txtNotas.setEditable(false);
            txtNotas.setLineWrap(true);
            txtNotas.setWrapStyleWord(true);
            txtNotas.setFont(new Font("Arial", Font.PLAIN, 13));
            txtNotas.setBackground(new Color(243, 244, 246));
            txtNotas.setBorder(new EmptyBorder(10, 10, 10, 10));
            txtNotas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            panelContenido.add(txtNotas);
        }

        JScrollPane scroll = new JScrollPane(panelContenido);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Boton cerrar
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.setBackground(COLOR_FONDO);
        panelBoton.setBorder(new EmptyBorder(10, 25, 15, 25));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setPreferredSize(new Dimension(100, 40));
        btnCerrar.setBackground(COLOR_PRIMARIO);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> dispose());

        panelBoton.add(btnCerrar);
        add(panelBoton, BorderLayout.SOUTH);
    }

    private JLabel crearSeccion(String titulo) {
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(COLOR_PRIMARIO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        return lbl;
    }

    private JPanel crearCampo(String etiqueta, String valor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        panel.setBackground(COLOR_FONDO);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (!etiqueta.isEmpty()) {
            JLabel lblEtiqueta = new JLabel(etiqueta + " ");
            lblEtiqueta.setFont(new Font("Arial", Font.BOLD, 13));
            lblEtiqueta.setForeground(COLOR_GRIS);
            panel.add(lblEtiqueta);
        }

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.PLAIN, 13));
        lblValor.setForeground(COLOR_TEXTO);
        panel.add(lblValor);

        return panel;
    }

    private JPanel crearCampoServicio(String texto) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        panel.setBackground(COLOR_FONDO);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setForeground(COLOR_TEXTO);
        panel.add(lbl);

        return panel;
    }

    private Color getColorEstado(String estado) {
        switch (estado) {
            case "completada": return new Color(34, 197, 94);
            case "en_proceso": return new Color(59, 130, 246);
            case "pendiente": return new Color(234, 179, 8);
            case "cancelada": return new Color(239, 68, 68);
            default: return COLOR_TEXTO;
        }
    }
}