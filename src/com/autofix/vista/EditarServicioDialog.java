package com.autofix.vista;

import com.autofix.dao.ServicioDAO;
import com.autofix.modelo.Servicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditarServicioDialog extends JDialog {

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtPrecio;
    private JTextField txtDuracion;
    private JCheckBox chkActivo;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private int idServicio;
    private Servicio servicioActual;
    private boolean guardado = false;
    private ServicioDAO servicioDAO;

    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private static final Color COLOR_FONDO = new Color(249, 250, 251);
    private static final Color COLOR_TEXTO = new Color(31, 41, 55);

    public EditarServicioDialog(Frame parent, int idServicio) {
        super(parent, "Editar Servicio", true);
        this.idServicio = idServicio;
        servicioDAO = new ServicioDAO();

        configurarDialogo();
        crearComponentes();
        cargarServicio();
    }

    private void configurarDialogo() {
        setSize(450, 520);
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

        JLabel lblTitulo = new JLabel("Editar Servicio");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);

        add(panelTitulo, BorderLayout.NORTH);

        // Panel formulario
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(COLOR_FONDO);
        panelForm.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Nombre
        panelForm.add(crearLabel("Nombre *"));
        txtNombre = new JTextField();
        configurarTextField(txtNombre);
        panelForm.add(txtNombre);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Descripcion
        panelForm.add(crearLabel("Descripcion"));
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setFont(new Font("Arial", Font.PLAIN, 14));
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panelForm.add(scrollDesc);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Precio y Duracion
        JPanel panelPrecioDuracion = new JPanel(new GridLayout(1, 2, 15, 0));
        panelPrecioDuracion.setBackground(COLOR_FONDO);
        panelPrecioDuracion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel panelPrecio = new JPanel();
        panelPrecio.setLayout(new BoxLayout(panelPrecio, BoxLayout.Y_AXIS));
        panelPrecio.setBackground(COLOR_FONDO);
        panelPrecio.add(crearLabel("Precio (€) *"));
        txtPrecio = new JTextField();
        configurarTextField(txtPrecio);
        panelPrecio.add(txtPrecio);

        JPanel panelDuracion = new JPanel();
        panelDuracion.setLayout(new BoxLayout(panelDuracion, BoxLayout.Y_AXIS));
        panelDuracion.setBackground(COLOR_FONDO);
        panelDuracion.add(crearLabel("Duracion (min) *"));
        txtDuracion = new JTextField();
        configurarTextField(txtDuracion);
        panelDuracion.add(txtDuracion);

        panelPrecioDuracion.add(panelPrecio);
        panelPrecioDuracion.add(panelDuracion);
        panelForm.add(panelPrecioDuracion);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Checkbox Activo
        chkActivo = new JCheckBox("Servicio activo");
        chkActivo.setBackground(COLOR_FONDO);
        chkActivo.setFont(new Font("Arial", Font.BOLD, 13));
        chkActivo.setForeground(COLOR_TEXTO);
        panelForm.add(chkActivo);

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

    private void configurarTextField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    private void cargarServicio() {
        servicioActual = servicioDAO.obtenerPorId(idServicio);

        if (servicioActual == null) {
            JOptionPane.showMessageDialog(this, "Servicio no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        txtNombre.setText(servicioActual.getNombre());
        txtDescripcion.setText(servicioActual.getDescripcion() != null ? servicioActual.getDescripcion() : "");
        txtPrecio.setText(String.format("%.2f", servicioActual.getPrecio()));
        txtDuracion.setText(String.valueOf(servicioActual.getDuracionMin()));
        chkActivo.setSelected(servicioActual.isActivo());
    }

    private void guardarCambios() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String precioStr = txtPrecio.getText().trim().replace(",", ".");
        String duracionStr = txtDuracion.getText().trim();

        // Validar
        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return;
        }
        if (precioStr.isEmpty()) {
            mostrarError("El precio es obligatorio");
            return;
        }
        if (duracionStr.isEmpty()) {
            mostrarError("La duracion es obligatoria");
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            int duracion = Integer.parseInt(duracionStr);

            if (precio <= 0) {
                mostrarError("El precio debe ser mayor que 0");
                return;
            }
            if (duracion <= 0) {
                mostrarError("La duracion debe ser mayor que 0");
                return;
            }

            // Actualizar servicio
            servicioActual.setNombre(nombre);
            servicioActual.setDescripcion(descripcion.isEmpty() ? null : descripcion);
            servicioActual.setPrecio(precio);
            servicioActual.setDuracionMin(duracion);
            servicioActual.setActivo(chkActivo.isSelected());

            if (servicioDAO.actualizar(servicioActual)) {
                guardado = true;
                JOptionPane.showMessageDialog(this, "Servicio actualizado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                mostrarError("Error al actualizar el servicio");
            }

        } catch (NumberFormatException e) {
            mostrarError("Precio o duracion no validos");
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isGuardado() {
        return guardado;
    }
}