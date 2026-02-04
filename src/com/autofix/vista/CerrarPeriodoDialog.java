package com.autofix.vista;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Diálogo para cerrar un período y archivar citas.
 * Solo archiva citas completadas y canceladas.
 * Los clientes, servicios y usuarios se conservan intactos.
 */
public class CerrarPeriodoDialog extends JDialog {

    private final Color COLOR_ROJO = new Color(220, 38, 38);
    private final Color COLOR_BLANCO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(31, 41, 55);
    private final Color COLOR_GRIS_CLARO = new Color(243, 244, 246);
    private final Color COLOR_FONDO = new Color(249, 250, 251);
    private final Color COLOR_NARANJA_FONDO = new Color(255, 247, 237);
    private final Color COLOR_NARANJA_BORDE = new Color(251, 146, 60);
    private final Color COLOR_VERDE_FONDO = new Color(240, 253, 244);
    private final Color COLOR_VERDE_BORDE = new Color(34, 197, 94);

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JCheckBox chkConfirmo;

    private boolean confirmado = false;
    private String nombre;
    private String descripcion;

    public CerrarPeriodoDialog(Frame parent, int totalCitas, int totalClientes, double ingresos) {
        super(parent, "Cerrar Periodo", true);
        setSize(550, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
        inicializarComponentes(totalCitas, totalClientes, ingresos);
    }

    private void inicializarComponentes(int totalCitas, int totalClientes, double ingresos) {
        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 0));
        panelPrincipal.setBackground(COLOR_FONDO);

        // Cabecera
        JPanel panelCabecera = new JPanel(new BorderLayout());
        panelCabecera.setBackground(COLOR_ROJO);
        panelCabecera.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel lblTitulo = new JLabel("Cerrar Periodo y Archivar Citas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_BLANCO);
        panelCabecera.add(lblTitulo, BorderLayout.WEST);
        panelPrincipal.add(panelCabecera, BorderLayout.NORTH);

        // Contenido
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(COLOR_BLANCO);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));

        // Aviso: Que se archiva
        JPanel panelAviso = new JPanel(new BorderLayout());
        panelAviso.setBackground(COLOR_NARANJA_FONDO);
        panelAviso.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_NARANJA_BORDE, 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        panelAviso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel lblAviso = new JLabel(String.format(
                "<html><b>Se archivaran las citas completadas y canceladas:</b><br>" +
                        "- <b>%d</b> citas totales en el sistema<br>" +
                        "- Ingresos registrados: <b>%.2f EUR</b></html>",
                totalCitas, ingresos));
        lblAviso.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelAviso.add(lblAviso);
        panelContenido.add(panelAviso);
        panelContenido.add(Box.createVerticalStrut(12));

        // Info: Que se conserva
        JPanel panelConserva = new JPanel(new BorderLayout());
        panelConserva.setBackground(COLOR_VERDE_FONDO);
        panelConserva.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_VERDE_BORDE, 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        panelConserva.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel lblConserva = new JLabel(String.format(
                "<html><b>Se conservan intactos:</b><br>" +
                        "- <b>%d</b> clientes, todos los servicios y usuarios<br>" +
                        "- Las citas pendientes y en proceso tambien se mantienen</html>",
                totalClientes));
        lblConserva.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelConserva.add(lblConserva);
        panelContenido.add(panelConserva);
        panelContenido.add(Box.createVerticalStrut(20));

        // Campo: Nombre del período
        JLabel lblNombre = new JLabel("Nombre del periodo: *");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombre.setForeground(COLOR_TEXTO);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelContenido.add(lblNombre);
        panelContenido.add(Box.createVerticalStrut(8));

        txtNombre = new JTextField();
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_GRIS_CLARO, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        txtNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtNombre.setToolTipText("Ej: Enero 2025, Primer trimestre, Temporada verano...");
        panelContenido.add(txtNombre);
        panelContenido.add(Box.createVerticalStrut(15));

        // Campo: Descripción
        JLabel lblDescripcion = new JLabel("Descripcion (opcional):");
        lblDescripcion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDescripcion.setForeground(COLOR_TEXTO);
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelContenido.add(lblDescripcion);
        panelContenido.add(Box.createVerticalStrut(8));

        txtDescripcion = new JTextArea(3, 30);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setBorder(new LineBorder(COLOR_GRIS_CLARO, 1, true));
        scrollDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panelContenido.add(scrollDesc);
        panelContenido.add(Box.createVerticalStrut(15));

        // Checkbox
        chkConfirmo = new JCheckBox("Entiendo que las citas completadas y canceladas se archivaran");
        chkConfirmo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkConfirmo.setBackground(COLOR_BLANCO);
        chkConfirmo.setForeground(COLOR_ROJO);
        chkConfirmo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelContenido.add(chkConfirmo);

        panelPrincipal.add(panelContenido, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_GRIS_CLARO));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancelar.setBackground(COLOR_GRIS_CLARO);
        btnCancelar.setForeground(COLOR_TEXTO);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dispose());
        panelBotones.add(btnCancelar);

        JButton btnConfirmar = new JButton("Cerrar Periodo y Archivar");
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConfirmar.setBackground(COLOR_ROJO);
        btnConfirmar.setForeground(COLOR_BLANCO);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btnConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirmar.addActionListener(e -> confirmarCierre());
        panelBotones.add(btnConfirmar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        setContentPane(panelPrincipal);
    }

    private void confirmarCierre() {
        String nombrePeriodo = txtNombre.getText().trim();
        if (nombrePeriodo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debes indicar un nombre para el periodo (ej: 'Enero 2025').",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        if (!chkConfirmo.isSelected()) {
            JOptionPane.showMessageDialog(this,
                    "Debes marcar la casilla de confirmacion para continuar.",
                    "Confirmacion requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "ULTIMA CONFIRMACION\n\n" +
                        "Vas a cerrar el periodo '" + nombrePeriodo + "'.\n\n" +
                        "Las citas completadas y canceladas se archivaran\n" +
                        "y se eliminaran de la vista activa.\n\n" +
                        "Clientes, servicios y citas pendientes se mantienen.\n\n" +
                        "Estas seguro?",
                "Confirmacion Final",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            this.nombre = nombrePeriodo;
            this.descripcion = txtDescripcion.getText().trim();
            this.confirmado = true;
            dispose();
        }
    }

    public boolean isConfirmado() { return confirmado; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}


































