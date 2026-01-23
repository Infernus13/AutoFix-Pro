package com.autofix.vista;

import com.autofix.dao.CitaDAO;
import com.autofix.dao.ClienteDAO;
import com.autofix.dao.UsuarioDAO;
import com.autofix.modelo.Cita;
import com.autofix.modelo.Cliente;
import com.autofix.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class EditarCitaDialog extends JDialog {

    private JComboBox<Cliente> cmbCliente;
    private JComboBox<Usuario> cmbUsuario;
    private JComboBox<String> cmbEstado;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JTextField txtPrecio;
    private JTextArea txtNotas;
    private JTextField txtMatricula;
    private JTextField txtModeloCoche;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private int idCita;
    private Cita citaActual;
    private boolean guardado = false;

    private ClienteDAO clienteDAO;
    private UsuarioDAO usuarioDAO;
    private CitaDAO citaDAO;

    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private static final Color COLOR_FONDO = new Color(249, 250, 251);
    private static final Color COLOR_TEXTO = new Color(31, 41, 55);

    public EditarCitaDialog(Frame parent, int idCita) {
        super(parent, "Editar Cita", true);
        this.idCita = idCita;

        clienteDAO = new ClienteDAO();
        usuarioDAO = new UsuarioDAO();
        citaDAO = new CitaDAO();

        configurarDialogo();
        crearComponentes();
        cargarDatos();
        cargarCitaActual();
    }

    private void configurarDialogo() {
        setSize(500, 680);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
    }

    private void crearComponentes() {
        setLayout(new BorderLayout());

        // Panel titulo
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(234, 179, 8));
        panelTitulo.setBorder(new EmptyBorder(20, 25, 20, 25));
        panelTitulo.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel lblTitulo = new JLabel("Editar Cita #" + String.format("%03d", idCita));
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);

        add(panelTitulo, BorderLayout.NORTH);

        // Panel formulario
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(COLOR_FONDO);
        panelForm.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Cliente
        panelForm.add(crearLabel("Cliente *"));
        cmbCliente = new JComboBox<>();
        configurarCombo(cmbCliente);
        panelForm.add(cmbCliente);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Empleado asignado
        panelForm.add(crearLabel("Empleado Asignado *"));
        cmbUsuario = new JComboBox<>();
        configurarCombo(cmbUsuario);
        panelForm.add(cmbUsuario);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Matricula y Modelo
        JPanel panelVehiculo = new JPanel(new GridLayout(1, 2, 15, 0));
        panelVehiculo.setBackground(COLOR_FONDO);
        panelVehiculo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JPanel panelMatricula = new JPanel();
        panelMatricula.setLayout(new BoxLayout(panelMatricula, BoxLayout.Y_AXIS));
        panelMatricula.setBackground(COLOR_FONDO);
        panelMatricula.add(crearLabel("Matricula *"));
        txtMatricula = new JTextField();
        configurarTextField(txtMatricula);
        panelMatricula.add(txtMatricula);

        JPanel panelModelo = new JPanel();
        panelModelo.setLayout(new BoxLayout(panelModelo, BoxLayout.Y_AXIS));
        panelModelo.setBackground(COLOR_FONDO);
        panelModelo.add(crearLabel("Modelo Coche *"));
        txtModeloCoche = new JTextField();
        configurarTextField(txtModeloCoche);
        panelModelo.add(txtModeloCoche);

        panelVehiculo.add(panelMatricula);
        panelVehiculo.add(panelModelo);
        panelForm.add(panelVehiculo);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Estado
        panelForm.add(crearLabel("Estado *"));
        cmbEstado = new JComboBox<>(new String[]{"pendiente", "en_proceso", "completada", "cancelada"});
        configurarCombo(cmbEstado);
        panelForm.add(cmbEstado);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Fecha y Hora
        JPanel panelFechaHora = new JPanel(new GridLayout(1, 2, 15, 0));
        panelFechaHora.setBackground(COLOR_FONDO);
        panelFechaHora.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel panelFecha = new JPanel();
        panelFecha.setLayout(new BoxLayout(panelFecha, BoxLayout.Y_AXIS));
        panelFecha.setBackground(COLOR_FONDO);
        panelFecha.add(crearLabel("Fecha * (AAAA-MM-DD)"));
        txtFecha = new JTextField();
        configurarTextField(txtFecha);
        panelFecha.add(txtFecha);

        JPanel panelHora = new JPanel();
        panelHora.setLayout(new BoxLayout(panelHora, BoxLayout.Y_AXIS));
        panelHora.setBackground(COLOR_FONDO);
        panelHora.add(crearLabel("Hora * (HH:MM)"));
        txtHora = new JTextField();
        configurarTextField(txtHora);
        panelHora.add(txtHora);

        panelFechaHora.add(panelFecha);
        panelFechaHora.add(panelHora);
        panelForm.add(panelFechaHora);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Precio
        panelForm.add(crearLabel("Precio (€)"));
        txtPrecio = new JTextField();
        configurarTextField(txtPrecio);
        txtPrecio.setEditable(false);
        txtPrecio.setBackground(new Color(243, 244, 246));
        panelForm.add(txtPrecio);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Notas
        panelForm.add(crearLabel("Notas"));
        txtNotas = new JTextArea(3, 20);
        txtNotas.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNotas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);
        JScrollPane scrollNotas = new JScrollPane(txtNotas);
        scrollNotas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panelForm.add(scrollNotas);

        add(panelForm, BorderLayout.CENTER);

        // Panel botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.setBorder(new EmptyBorder(15, 30, 20, 30));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 40));
        btnCancelar.setBackground(new Color(107, 114, 128));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dispose());

        btnGuardar = new JButton("Guardar");
        btnGuardar.setPreferredSize(new Dimension(100, 40));
        btnGuardar.setBackground(COLOR_PRIMARIO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 13));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarCambios());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(COLOR_TEXTO);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));
        return label;
    }

    private void configurarCombo(JComboBox<?> combo) {
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
    }

    private void configurarTextField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    private void cargarDatos() {
        // Cargar clientes
        List<Cliente> clientes = clienteDAO.obtenerTodos();
        for (Cliente c : clientes) {
            cmbCliente.addItem(c);
        }

        // Cargar usuarios
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        for (Usuario u : usuarios) {
            cmbUsuario.addItem(u);
        }
    }

    private void cargarCitaActual() {
        citaActual = citaDAO.obtenerPorId(idCita);

        if (citaActual == null) {
            JOptionPane.showMessageDialog(this, "Cita no encontrada", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // Seleccionar cliente
        for (int i = 0; i < cmbCliente.getItemCount(); i++) {
            if (cmbCliente.getItemAt(i).getId() == citaActual.getIdCliente()) {
                cmbCliente.setSelectedIndex(i);
                break;
            }
        }

        // Seleccionar usuario
        for (int i = 0; i < cmbUsuario.getItemCount(); i++) {
            if (cmbUsuario.getItemAt(i).getId() == citaActual.getIdUsuario()) {
                cmbUsuario.setSelectedIndex(i);
                break;
            }
        }

        // Matricula y modelo
        txtMatricula.setText(citaActual.getMatricula() != null ? citaActual.getMatricula() : "");
        txtModeloCoche.setText(citaActual.getModeloCoche() != null ? citaActual.getModeloCoche() : "");

        // Estado
        cmbEstado.setSelectedItem(citaActual.getEstado());

        // Fecha y hora
        txtFecha.setText(citaActual.getFecha().toString());
        txtHora.setText(citaActual.getHora().toString().substring(0, 5));

        // Precio (no editable, viene de los servicios)
        txtPrecio.setText(String.format("%.2f", citaActual.getPrecioFinal()));

        // Notas
        if (citaActual.getNotas() != null) {
            txtNotas.setText(citaActual.getNotas());
        }
    }

    private void guardarCambios() {
        try {
            Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
            Usuario usuario = (Usuario) cmbUsuario.getSelectedItem();
            String estado = (String) cmbEstado.getSelectedItem();

            Date fecha = Date.valueOf(txtFecha.getText().trim());
            Time hora = Time.valueOf(txtHora.getText().trim() + ":00");
            String notas = txtNotas.getText().trim();

            if (txtMatricula.getText().trim().isEmpty()) {
                mostrarError("La matricula es obligatoria");
                return;
            }
            if (txtModeloCoche.getText().trim().isEmpty()) {
                mostrarError("El modelo de coche es obligatorio");
                return;
            }

            citaActual.setIdCliente(cliente.getId());
            citaActual.setIdUsuario(usuario.getId());
            citaActual.setMatricula(txtMatricula.getText().trim().toUpperCase());
            citaActual.setModeloCoche(txtModeloCoche.getText().trim());
            citaActual.setEstado(estado);
            citaActual.setFecha(fecha);
            citaActual.setHora(hora);
            citaActual.setNotas(notas.isEmpty() ? null : notas);

            if (citaDAO.actualizar(citaActual)) {
                guardado = true;
                JOptionPane.showMessageDialog(this, "Cita actualizada correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar la cita", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha u hora incorrecto", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isGuardado() {
        return guardado;
    }
}