package com.autofix.vista;

import com.autofix.controlador.UsuarioController;
import com.autofix.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class UsuariosPanel extends JPanel {

    private UsuarioController usuarioController;
    private List<Integer> idsUsuarios = new ArrayList<>();
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;

    private static final Color COLOR_BG = new Color(249, 250, 251);
    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private static final Color COLOR_TEXTO = new Color(31, 41, 55);
    private static final Color COLOR_GRIS = new Color(107, 114, 128);

    public UsuariosPanel() {
        usuarioController = new UsuarioController();
        configurarPanel();
        crearComponentes();
        cargarDatos();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
    }

    private void crearComponentes() {
        add(crearToolbar(), BorderLayout.NORTH);
        add(crearPanelTabla(), BorderLayout.CENTER);
    }

    private JPanel crearToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)),
                new EmptyBorder(18, 25, 18, 25)
        ));

        JLabel titulo = new JLabel("Gestion de Usuarios");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(COLOR_TEXTO);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setBackground(Color.WHITE);

        JButton btnNuevo = new JButton("+ Nuevo Usuario");
        btnNuevo.setBackground(COLOR_PRIMARIO);
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        btnNuevo.setBorderPainted(false);
        btnNuevo.setFont(new Font("Arial", Font.BOLD, 13));
        btnNuevo.setPreferredSize(new Dimension(160, 38));
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevo.addActionListener(e -> abrirNuevoUsuario());

        rightPanel.add(btnNuevo);

        toolbar.add(titulo, BorderLayout.WEST);
        toolbar.add(rightPanel, BorderLayout.EAST);

        return toolbar;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));

        String[] columnas = {"Nombre", "Email", "Rol", "Fecha Alta"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setRowHeight(45);
        tablaUsuarios.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaUsuarios.setGridColor(new Color(243, 244, 246));
        tablaUsuarios.setSelectionBackground(new Color(219, 234, 254));

        // Header
        tablaUsuarios.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaUsuarios.getTableHeader().setBackground(new Color(249, 250, 251));
        tablaUsuarios.getTableHeader().setForeground(COLOR_GRIS);
        tablaUsuarios.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Centrar contenido
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tablaUsuarios.getColumnCount(); i++) {
            tablaUsuarios.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Renderizador para columna Rol (colores)
        tablaUsuarios.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);

                String rol = value.toString();
                if ("administrador".equals(rol)) {
                    label.setForeground(new Color(37, 99, 235));
                } else {
                    label.setForeground(new Color(34, 197, 94));
                }
                label.setFont(new Font("Arial", Font.BOLD, 12));
                return label;
            }
        });

        // Menu contextual
        JPopupMenu menuContextual = new JPopupMenu();

        JMenuItem itemEditar = new JMenuItem("Editar usuario");
        itemEditar.addActionListener(e -> editarUsuarioSeleccionado());
        menuContextual.add(itemEditar);

        JMenuItem itemEliminar = new JMenuItem("Eliminar usuario");
        itemEliminar.addActionListener(e -> eliminarUsuarioSeleccionado());
        menuContextual.add(itemEliminar);

        tablaUsuarios.setComponentPopupMenu(menuContextual);

        tablaUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = tablaUsuarios.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tablaUsuarios.getRowCount()) {
                    tablaUsuarios.setRowSelectionInterval(row, row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableContainer.add(scrollPane, BorderLayout.CENTER);
        panel.add(tableContainer, BorderLayout.CENTER);

        return panel;
    }

    public void cargarDatos() {
        modeloTabla.setRowCount(0);
        idsUsuarios.clear();

        List<Usuario> usuarios = usuarioController.obtenerTodos();
        for (Usuario u : usuarios) {
            idsUsuarios.add(u.getId());
            Object[] fila = {
                    u.getNombre(),
                    u.getEmail(),
                    u.getRol(),
                    u.getFechaAlta() != null ? u.getFechaAlta().toString().substring(0, 10) : ""
            };
            modeloTabla.addRow(fila);
        }
    }

    private void abrirNuevoUsuario() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        NuevoUsuarioDialog dialog = new NuevoUsuarioDialog(parent);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarDatos();
        }
    }

    private void editarUsuarioSeleccionado() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = idsUsuarios.get(fila);
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        EditarUsuarioDialog dialog = new EditarUsuarioDialog(parent, idUsuario);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarDatos();
        }
    }

    private void eliminarUsuarioSeleccionado() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = idsUsuarios.get(fila);
        String nombre = modeloTabla.getValueAt(fila, 0).toString();

        // No permitir eliminar al propio usuario o si solo queda uno
        if (idsUsuarios.size() <= 1) {
            JOptionPane.showMessageDialog(this, "Debe haber al menos un usuario en el sistema", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el usuario '" + nombre + "'?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            if (usuarioController.eliminar(idUsuario)) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado", "Exito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}