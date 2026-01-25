package com.autofix.vista;

import com.autofix.dao.CitaDAO;
import com.autofix.dao.ClienteDAO;
import com.autofix.dao.DetalleCitaDAO;
import com.autofix.dao.ServicioDAO;
import com.autofix.dao.UsuarioDAO;
import com.autofix.modelo.Cita;
import com.autofix.modelo.Cliente;
import com.autofix.modelo.DetalleCita;
import com.autofix.modelo.Servicio;
import com.autofix.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class NuevaCitaDialog extends JDialog {

    private JComboBox<Cliente> cmbCliente;
    private JComboBox<Usuario> cmbUsuario;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JTextArea txtNotas;
    private JComboBox<Servicio> cmbServicio;
    private JTable tablaServicios;
    private DefaultTableModel modeloTablaServicios;
    private JLabel lblTotal;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JTextField txtMatricula;
    private JTextField txtModeloCoche;

    private List<DetalleCita> serviciosAgregados;
    private boolean guardado = false;
    private Usuario usuarioActual;

    private ClienteDAO clienteDAO;
    private ServicioDAO servicioDAO;
    private UsuarioDAO usuarioDAO;
    private CitaDAO citaDAO;
    private DetalleCitaDAO detalleCitaDAO;

    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private static final Color COLOR_FONDO = new Color(249, 250, 251);
    private static final Color COLOR_TEXTO = new Color(31, 41, 55);

    public NuevaCitaDialog(Frame parent, Usuario usuarioActual) {
        super(parent, "Nueva Cita", true);

        this.usuarioActual = usuarioActual;
        clienteDAO = new ClienteDAO();
        servicioDAO = new ServicioDAO();
        usuarioDAO = new UsuarioDAO();
        citaDAO = new CitaDAO();
        detalleCitaDAO = new DetalleCitaDAO();
        serviciosAgregados = new ArrayList<>();

        configurarDialogo();
        crearComponentes();
        cargarDatos();
    }

    // Constructor alternativo para compatibilidad
    public NuevaCitaDialog(Frame parent) {
        this(parent, null);
    }

    private void configurarDialogo() {
        setSize(600, 750);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
    }

    private void crearComponentes() {
        setLayout(new BorderLayout());

        // Panel titulo
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(COLOR_PRIMARIO);
        panelTitulo.setBorder(new EmptyBorder(20, 25, 20, 25));
        panelTitulo.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel lblTitulo = new JLabel("Nueva Cita");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);

        add(panelTitulo, BorderLayout.NORTH);

        // Panel formulario
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(COLOR_FONDO);
        panelForm.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Cliente
        panelForm.add(crearLabel("Cliente *"));
        cmbCliente = new JComboBox<>();
        configurarCombo(cmbCliente);
        panelForm.add(cmbCliente);
        panelForm.add(Box.createRigidArea(new Dimension(0, 12)));

        // Empleado asignado - Solo visible/editable para admin
        panelForm.add(crearLabel("Empleado Asignado *"));
        cmbUsuario = new JComboBox<>();
        configurarCombo(cmbUsuario);

        // Si es trabajador, deshabilitar el combo
        if (usuarioActual != null && !"administrador".equals(usuarioActual.getRol())) {
            cmbUsuario.setEnabled(false);
        }

        panelForm.add(cmbUsuario);
        panelForm.add(Box.createRigidArea(new Dimension(0, 12)));

        // Matricula y Modelo
        JPanel panelVehiculo = new JPanel(new GridLayout(1, 2, 15, 0));
        panelVehiculo.setBackground(COLOR_FONDO);
        panelVehiculo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JPanel panelMatricula = new JPanel();
        panelMatricula.setLayout(new BoxLayout(panelMatricula, BoxLayout.Y_AXIS));
        panelMatricula.setBackground(COLOR_FONDO);
        panelMatricula.add(crearLabel("Matricula *"));
        txtMatricula = new JTextField();
        txtMatricula.setToolTipText("Ej: 1234 ABC");
        configurarTextField(txtMatricula);
        panelMatricula.add(txtMatricula);

        JPanel panelModelo = new JPanel();
        panelModelo.setLayout(new BoxLayout(panelModelo, BoxLayout.Y_AXIS));
        panelModelo.setBackground(COLOR_FONDO);
        panelModelo.add(crearLabel("Modelo Coche *"));
        txtModeloCoche = new JTextField();
        txtModeloCoche.setToolTipText("Ej: Ford Focus 1.6");
        configurarTextField(txtModeloCoche);
        panelModelo.add(txtModeloCoche);

        panelVehiculo.add(panelMatricula);
        panelVehiculo.add(panelModelo);
        panelForm.add(panelVehiculo);
        panelForm.add(Box.createRigidArea(new Dimension(0, 12)));

        // Fecha y Hora
        JPanel panelFechaHora = new JPanel(new GridLayout(1, 2, 15, 0));
        panelFechaHora.setBackground(COLOR_FONDO);
        panelFechaHora.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JPanel panelFecha = new JPanel();
        panelFecha.setLayout(new BoxLayout(panelFecha, BoxLayout.Y_AXIS));
        panelFecha.setBackground(COLOR_FONDO);
        panelFecha.add(crearLabel("Fecha * (AAAA-MM-DD)"));
        txtFecha = new JTextField();
        txtFecha.setText(java.time.LocalDate.now().toString());
        configurarTextField(txtFecha);
        panelFecha.add(txtFecha);

        JPanel panelHora = new JPanel();
        panelHora.setLayout(new BoxLayout(panelHora, BoxLayout.Y_AXIS));
        panelHora.setBackground(COLOR_FONDO);
        panelHora.add(crearLabel("Hora * (HH:MM)"));
        txtHora = new JTextField();
        txtHora.setText("09:00");
        configurarTextField(txtHora);
        panelHora.add(txtHora);

        panelFechaHora.add(panelFecha);
        panelFechaHora.add(panelHora);
        panelForm.add(panelFechaHora);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Seccion de servicios
        panelForm.add(crearLabel("Agregar Servicios *"));

        JPanel panelAgregarServicio = new JPanel(new BorderLayout(10, 0));
        panelAgregarServicio.setBackground(COLOR_FONDO);
        panelAgregarServicio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        cmbServicio = new JComboBox<>();
        cmbServicio.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton btnAgregar = new JButton("+ Agregar");
        btnAgregar.setBackground(new Color(34, 197, 94));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setBorderPainted(false);
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 12));
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgregar.setPreferredSize(new Dimension(100, 35));
        btnAgregar.addActionListener(e -> agregarServicio());

        panelAgregarServicio.add(cmbServicio, BorderLayout.CENTER);
        panelAgregarServicio.add(btnAgregar, BorderLayout.EAST);
        panelForm.add(panelAgregarServicio);
        panelForm.add(Box.createRigidArea(new Dimension(0, 10)));

        // Tabla de servicios agregados
        String[] columnas = {"Servicio", "Precio", "Quitar"};
        modeloTablaServicios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        tablaServicios = new JTable(modeloTablaServicios);
        tablaServicios.setRowHeight(35);
        tablaServicios.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaServicios.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tablaServicios.getTableHeader().setBackground(new Color(249, 250, 251));

        // Boton quitar en la tabla
        tablaServicios.getColumnModel().getColumn(2).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton btn = new JButton("X");
            btn.setBackground(new Color(239, 68, 68));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 10));
            return btn;
        });

        tablaServicios.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int column = tablaServicios.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / tablaServicios.getRowHeight();
                if (row < tablaServicios.getRowCount() && column == 2) {
                    quitarServicio(row);
                }
            }
        });

        JScrollPane scrollServicios = new JScrollPane(tablaServicios);
        scrollServicios.setPreferredSize(new Dimension(0, 120));
        scrollServicios.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        panelForm.add(scrollServicios);
        panelForm.add(Box.createRigidArea(new Dimension(0, 10)));

        // Total
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.setBackground(COLOR_FONDO);
        panelTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel lblTotalTexto = new JLabel("TOTAL: ");
        lblTotalTexto.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalTexto.setForeground(COLOR_TEXTO);

        lblTotal = new JLabel("0.00 €");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(new Color(34, 197, 94));

        panelTotal.add(lblTotalTexto);
        panelTotal.add(lblTotal);
        panelForm.add(panelTotal);
        panelForm.add(Box.createRigidArea(new Dimension(0, 10)));

        // Notas
        panelForm.add(crearLabel("Notas"));
        txtNotas = new JTextArea(2, 20);
        txtNotas.setFont(new Font("Arial", Font.PLAIN, 13));
        txtNotas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);
        JScrollPane scrollNotas = new JScrollPane(txtNotas);
        scrollNotas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panelForm.add(scrollNotas);

        add(panelForm, BorderLayout.CENTER);

        // Panel botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.setBorder(new EmptyBorder(15, 25, 20, 25));

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
        btnGuardar.addActionListener(e -> guardarCita());

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
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        combo.setFont(new Font("Arial", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
    }

    private void configurarTextField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    private void cargarDatos() {
        // Cargar clientes
        List<Cliente> clientes = clienteDAO.obtenerTodos();
        for (Cliente c : clientes) {
            cmbCliente.addItem(c);
        }

        // Cargar servicios
        List<Servicio> servicios = servicioDAO.obtenerActivos();
        for (Servicio s : servicios) {
            cmbServicio.addItem(s);
        }

        // Cargar usuarios/empleados
        if (usuarioActual != null && !"administrador".equals(usuarioActual.getRol())) {
            // Si es trabajador, solo mostrar su propio usuario
            cmbUsuario.addItem(usuarioActual);
            cmbUsuario.setSelectedItem(usuarioActual);
        } else {
            // Si es admin, mostrar todos los usuarios
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();
            for (Usuario u : usuarios) {
                cmbUsuario.addItem(u);
            }
        }
    }

    private void agregarServicio() {
        Servicio servicio = (Servicio) cmbServicio.getSelectedItem();
        if (servicio == null) {
            return;
        }

        // Crear detalle
        DetalleCita detalle = new DetalleCita(servicio.getId(), servicio.getPrecio(), 1);
        detalle.setNombreServicio(servicio.getNombre());
        serviciosAgregados.add(detalle);

        // Agregar a la tabla
        Object[] fila = {
                servicio.getNombre(),
                String.format("%.2f €", servicio.getPrecio()),
                "X"
        };
        modeloTablaServicios.addRow(fila);

        actualizarTotal();
    }

    private void quitarServicio(int row) {
        if (row >= 0 && row < serviciosAgregados.size()) {
            serviciosAgregados.remove(row);
            modeloTablaServicios.removeRow(row);
            actualizarTotal();
        }
    }

    private void actualizarTotal() {
        double total = 0;
        for (DetalleCita detalle : serviciosAgregados) {
            total += detalle.getSubtotal();
        }
        lblTotal.setText(String.format("%.2f €", total));
    }

    private double calcularTotal() {
        double total = 0;
        for (DetalleCita detalle : serviciosAgregados) {
            total += detalle.getSubtotal();
        }
        return total;
    }

    private void guardarCita() {
        // Validar campos
        if (cmbCliente.getSelectedItem() == null) {
            mostrarError("Seleccione un cliente");
            return;
        }
        if (cmbUsuario.getSelectedItem() == null) {
            mostrarError("Seleccione un empleado");
            return;
        }
        if (txtMatricula.getText().trim().isEmpty()) {
            mostrarError("La matricula es obligatoria");
            return;
        }
        if (txtModeloCoche.getText().trim().isEmpty()) {
            mostrarError("El modelo de coche es obligatorio");
            return;
        }
        if (serviciosAgregados.isEmpty()) {
            mostrarError("Agregue al menos un servicio");
            return;
        }

        try {
            Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
            Usuario usuario = (Usuario) cmbUsuario.getSelectedItem();

            Date fecha = Date.valueOf(txtFecha.getText().trim());
            Time hora = Time.valueOf(txtHora.getText().trim() + ":00");
            double precioTotal = calcularTotal();
            String notas = txtNotas.getText().trim();

            // Crear cita
            Cita cita = new Cita();
            cita.setIdCliente(cliente.getId());
            cita.setIdUsuario(usuario.getId());
            cita.setMatricula(txtMatricula.getText().trim().toUpperCase());
            cita.setModeloCoche(txtModeloCoche.getText().trim());
            cita.setFecha(fecha);
            cita.setHora(hora);
            cita.setPrecioFinal(precioTotal);
            cita.setNotas(notas.isEmpty() ? null : notas);

            // Guardar cita y obtener ID
            int idCita = citaDAO.insertar(cita);

            if (idCita > 0) {
                // Guardar detalles
                for (DetalleCita detalle : serviciosAgregados) {
                    detalle.setIdCita(idCita);
                    detalleCitaDAO.insertar(detalle);
                }

                guardado = true;
                JOptionPane.showMessageDialog(this,
                        "Cita creada correctamente con " + serviciosAgregados.size() + " servicio(s)",
                        "Exito",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                mostrarError("Error al guardar la cita");
            }

        } catch (IllegalArgumentException e) {
            mostrarError("Formato de fecha u hora incorrecto.\nFecha: AAAA-MM-DD\nHora: HH:MM");
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isGuardado() {
        return guardado;
    }
}