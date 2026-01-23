package com.autofix.vista;

import com.autofix.dao.UsuarioDAO;
import com.autofix.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NuevoUsuarioDialog extends JDialog {

    private JTextField txtNombre;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmarPassword;
    private JComboBox<String> cmbRol;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private boolean guardado = false;
    private UsuarioDAO usuarioDAO;

    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private static final Color COLOR_FONDO = new Color(249, 250, 251);
    private static final Color COLOR_TEXTO = new Color(31, 41, 55);

    public NuevoUsuarioDialog(Frame parent) {
        super(parent, "Nuevo Usuario", true);
        usuarioDAO = new UsuarioDAO();

        configurarDialogo();
        crearComponentes();
    }

    private void configurarDialogo() {
        setSize(450, 560);
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

        JLabel lblTitulo = new JLabel("Nuevo Usuario");
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
        panelForm.add(crearLabel("Nombre completo *"));
        txtNombre = new JTextField();
        configurarTextField(txtNombre);
        panelForm.add(txtNombre);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Email
        panelForm.add(crearLabel("Email *"));
        txtEmail = new JTextField();
        configurarTextField(txtEmail);
        panelForm.add(txtEmail);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password
        panelForm.add(crearLabel("Contrasena *"));
        txtPassword = new JPasswordField();
        configurarTextField(txtPassword);
        panelForm.add(txtPassword);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Confirmar Password
        panelForm.add(crearLabel("Confirmar contrasena *"));
        txtConfirmarPassword = new JPasswordField();
        configurarTextField(txtConfirmarPassword);
        panelForm.add(txtConfirmarPassword);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Rol
        panelForm.add(crearLabel("Rol *"));
        cmbRol = new JComboBox<>(new String[]{"trabajador", "administrador"});
        cmbRol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cmbRol.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbRol.setBackground(Color.WHITE);
        panelForm.add(cmbRol);

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
        btnGuardar.addActionListener(e -> guardarUsuario());

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

    private void guardarUsuario() {
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmar = new String(txtConfirmarPassword.getPassword());
        String rol = (String) cmbRol.getSelectedItem();

        // Validaciones
        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return;
        }
        if (email.isEmpty()) {
            mostrarError("El email es obligatorio");
            return;
        }
        if (!email.contains("@")) {
            mostrarError("El email no es valido");
            return;
        }
        if (password.isEmpty()) {
            mostrarError("La contrasena es obligatoria");
            return;
        }
        if (password.length() < 4) {
            mostrarError("La contrasena debe tener al menos 4 caracteres");
            return;
        }
        if (!password.equals(confirmar)) {
            mostrarError("Las contrasenas no coinciden");
            return;
        }

        // Crear usuario
        Usuario usuario = new Usuario(nombre, email, password, rol);

        // Guardar en BD
        if (usuarioDAO.insertar(usuario)) {
            guardado = true;
            JOptionPane.showMessageDialog(this, "Usuario creado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            mostrarError("Error al guardar. Puede que el email ya exista.");
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isGuardado() {
        return guardado;
    }
}