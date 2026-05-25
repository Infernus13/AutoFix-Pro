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

        // --- CARGA DE DATOS ---
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

        // --- PANEL TÍTULO ---
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(COLOR_PRIMARIO);
        panelTitulo.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblTitulo = new JLabel("Cita #" + String.format("%03d", idCita));
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblEstado = new JLabel(cita.getEstado().toUpperCase());
        lblEstado.setFont(new Font("Arial", Font.BOLD, 12));
        lblEstado.setForeground(getColorEstado(cita.getEstado()));
        lblEstado.setOpaque(true);
        lblEstado.setBackground(Color.WHITE);
        lblEstado.setBorder(new EmptyBorder(4, 12, 4, 12));

        panelTitulo.add(lblTitulo, BorderLayout.WEST);
        panelTitulo.add(lblEstado, BorderLayout.EAST);
        add(panelTitulo, BorderLayout.NORTH);

        // --- PANEL DE CONTENIO ---
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(COLOR_FONDO);
        panelContenido.setBorder(new EmptyBorder(20, 30, 10, 30));

        //Cliente y Vehículo (Dos Columnas)
        JPanel panelGrid = new JPanel(new GridLayout(1, 2, 40, 0));
        panelGrid.setOpaque(false);
        panelGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Columna Cliente
        JPanel colCliente = new JPanel();
        colCliente.setLayout(new BoxLayout(colCliente, BoxLayout.Y_AXIS));
        colCliente.setOpaque(false);
        colCliente.add(crearSeccion("CLIENTE"));
        colCliente.add(crearCampo("Nombre:", cliente != null ? cliente.getNombre() : "-"));
        colCliente.add(crearCampo("Teléfono:", cliente != null ? cliente.getTelefono() : "-"));

        // Columna Vehículo
        JPanel colVehiculo = new JPanel();
        colVehiculo.setLayout(new BoxLayout(colVehiculo, BoxLayout.Y_AXIS));
        colVehiculo.setOpaque(false);
        colVehiculo.add(crearSeccion("VEHÍCULO"));
        colVehiculo.add(crearCampo("Matrícula:", cita.getMatricula() != null ? cita.getMatricula() : "-"));
        colVehiculo.add(crearCampo("Modelo:", cita.getModeloCoche() != null ? cita.getModeloCoche() : "-"));

        panelGrid.add(colCliente);
        panelGrid.add(colVehiculo);
        panelContenido.add(panelGrid);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 20)));

        //Detalles Cita
        panelContenido.add(crearSeccion("DETALLES DE LA CITA"));
        JPanel panelCitaHoriz = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelCitaHoriz.setOpaque(false);
        panelCitaHoriz.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCitaHoriz.add(crearCampo("Fecha:", cita.getFecha().toString()));
        panelCitaHoriz.add(Box.createRigidArea(new Dimension(30, 0)));
        panelCitaHoriz.add(crearCampo("Hora:", cita.getHora().toString().substring(0, 5)));
        panelCitaHoriz.add(Box.createRigidArea(new Dimension(30, 0)));
        panelCitaHoriz.add(crearCampo("Empleado:", empleado != null ? empleado.getNombre() : "-"));
        panelContenido.add(panelCitaHoriz);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 20)));

        // Servicios
        panelContenido.add(crearSeccion("SERVICIOS A REALIZAR"));
        JPanel panelServicios = new JPanel();
        panelServicios.setLayout(new BoxLayout(panelServicios, BoxLayout.Y_AXIS));
        panelServicios.setBackground(Color.WHITE);
        panelServicios.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelServicios.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(10, 15, 10, 15)
        ));

        if (servicios.isEmpty()) {
            panelServicios.add(new JLabel("Sin servicios registrados"));
        } else {
            for (DetalleCita detalle : servicios) {
                panelServicios.add(crearCampoServicio("• " + detalle.getNombreServicio() + " (" + String.format("%.2f €", detalle.getPrecio()) + ")"));
            }
        }
        panelContenido.add(panelServicios);

        //Total
        JPanel wrapTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        wrapTotal.setOpaque(false);
        wrapTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblTotal = new JLabel("TOTAL: " + String.format("%.2f €", cita.getPrecioFinal()));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(new Color(34, 197, 94));
        wrapTotal.add(lblTotal);
        panelContenido.add(wrapTotal);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 10)));

        // Notas
        if (cita.getNotas() != null && !cita.getNotas().isEmpty()) {
            panelContenido.add(crearSeccion("NOTAS"));
            JTextArea txtNotas = new JTextArea(cita.getNotas());
            txtNotas.setEditable(false);
            txtNotas.setLineWrap(true);
            txtNotas.setWrapStyleWord(true);
            txtNotas.setFont(new Font("Arial", Font.ITALIC, 13));
            txtNotas.setBackground(new Color(243, 244, 246));
            txtNotas.setBorder(new EmptyBorder(8, 10, 8, 10));

            JScrollPane scrollNotas = new JScrollPane(txtNotas);
            scrollNotas.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));
            scrollNotas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            scrollNotas.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
            scrollNotas.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelContenido.add(scrollNotas);
        }

        panelContenido.add(Box.createVerticalGlue());

        add(panelContenido, BorderLayout.CENTER);

        //BOTÓN CERRAR
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.setBackground(COLOR_FONDO);
        panelBoton.setBorder(new EmptyBorder(5, 25, 15, 25));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setPreferredSize(new Dimension(100, 35));
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
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblEtiqueta = new JLabel(etiqueta + " ");
        lblEtiqueta.setFont(new Font("Arial", Font.BOLD, 13));
        lblEtiqueta.setForeground(COLOR_GRIS);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.PLAIN, 13));
        lblValor.setForeground(COLOR_TEXTO);

        panel.add(lblEtiqueta);
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