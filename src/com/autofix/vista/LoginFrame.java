package com.autofix.vista;

import com.autofix.dao.UsuarioDAO;
import com.autofix.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblMensaje;

    // Colores
    private static final Color COLOR_FONDO = new Color(249, 250, 251);
    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private static final Color COLOR_TEXTO = new Color(31, 41, 55);
    private static final Color COLOR_GRIS = new Color(107, 114, 128);

    public LoginFrame() {
        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        setTitle("AutoFix Pro - Iniciar Sesion");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
    }

    private void crearComponentes() {
        setLayout(new BorderLayout());

        // Panel central
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(COLOR_FONDO);
        panelCentral.setBorder(new EmptyBorder(50, 50, 50, 50));

        // Logo/Titulo
        JLabel lblLogo = new JLabel("AutoFix Pro");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 28));
        lblLogo.setForeground(COLOR_PRIMARIO);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Sistema de Gestion de Taller");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(COLOR_GRIS);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Espaciador
        panelCentral.add(lblLogo);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10)));
        panelCentral.add(lblSubtitulo);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 40)));

        // Campo Email
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(new Font("Arial", Font.BOLD, 12));
        lblEmail.setForeground(COLOR_TEXTO);
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtEmail = new JTextField();
        txtEmail.setMaximumSize(new Dimension(300, 40));
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        panelCentral.add(lblEmail);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 8)));
        panelCentral.add(txtEmail);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Campo Password
        JLabel lblPassword = new JLabel("Contrasena");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 12));
        lblPassword.setForeground(COLOR_TEXTO);
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(300, 40));
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        panelCentral.add(lblPassword);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 8)));
        panelCentral.add(txtPassword);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 30)));

        // Boton Login
        btnLogin = new JButton("Iniciar Sesion");
        btnLogin.setMaximumSize(new Dimension(300, 45));
        btnLogin.setBackground(COLOR_PRIMARIO);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Accion del boton
        btnLogin.addActionListener(e -> iniciarSesion());

        // Permitir login con Enter
        txtPassword.addActionListener(e -> iniciarSesion());

        panelCentral.add(btnLogin);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Mensaje de error/exito
        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelCentral.add(lblMensaje);

        // Agregar panel central
        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior con credenciales de prueba
        JPanel panelInferior = new JPanel();
        panelInferior.setBackground(COLOR_FONDO);
        panelInferior.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblDemo = new JLabel("Demo: juan.martinez@autofix.com / 1234");
        lblDemo.setFont(new Font("Arial", Font.ITALIC, 11));
        lblDemo.setForeground(COLOR_GRIS);

        panelInferior.add(lblDemo);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void iniciarSesion() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Validar campos vacios
        if (email.isEmpty() || password.isEmpty()) {
            lblMensaje.setText("Por favor, complete todos los campos");
            lblMensaje.setForeground(Color.RED);
            return;
        }

        // Intentar login
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.login(email, password);

        if (usuario != null) {
            lblMensaje.setText("Bienvenido, " + usuario.getNombre());
            lblMensaje.setForeground(new Color(34, 197, 94));

            // Abrir ventana principal
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame(usuario);
                mainFrame.setVisible(true);
                this.dispose();
            });
        } else {
            lblMensaje.setText("Email o contrasena incorrectos");
            lblMensaje.setForeground(Color.RED);
            txtPassword.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}