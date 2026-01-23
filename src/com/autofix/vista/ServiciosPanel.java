package com.autofix.vista;

import com.autofix.dao.ServicioDAO;
import com.autofix.modelo.Servicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class ServiciosPanel extends JPanel {

    private ServicioDAO servicioDAO;
    private List<Integer> idsServicios = new ArrayList<>();
    private JTable tablaServicios;
    private DefaultTableModel modeloTabla;

    private static final Color COLOR_BG = new Color(249, 250, 251);
    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private static final Color COLOR_TEXTO = new Color(31, 41, 55);
    private static final Color COLOR_GRIS = new Color(107, 114, 128);

    public ServiciosPanel() {
        servicioDAO = new ServicioDAO();
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

        JLabel titulo = new JLabel("Gestion de Servicios");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(COLOR_TEXTO);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setBackground(Color.WHITE);

        JButton btnNuevo = new JButton("+ Nuevo Servicio");
        btnNuevo.setBackground(COLOR_PRIMARIO);
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        btnNuevo.setBorderPainted(false);
        btnNuevo.setFont(new Font("Arial", Font.BOLD, 13));
        btnNuevo.setPreferredSize(new Dimension(160, 38));
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevo.addActionListener(e -> abrirNuevoServicio());

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

        String[] columnas = {"Nombre", "Descripcion", "Precio", "Duracion"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaServicios = new JTable(modeloTabla);
        tablaServicios.setRowHeight(45);
        tablaServicios.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaServicios.setGridColor(new Color(243, 244, 246));
        tablaServicios.setSelectionBackground(new Color(219, 234, 254));

        // Header
        tablaServicios.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaServicios.getTableHeader().setBackground(new Color(249, 250, 251));
        tablaServicios.getTableHeader().setForeground(COLOR_GRIS);
        tablaServicios.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Centrar contenido
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tablaServicios.getColumnCount(); i++) {
            tablaServicios.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Menu contextual
        JPopupMenu menuContextual = new JPopupMenu();

        JMenuItem itemEditar = new JMenuItem("Editar servicio");
        itemEditar.addActionListener(e -> editarServicioSeleccionado());
        menuContextual.add(itemEditar);

        JMenuItem itemEliminar = new JMenuItem("Eliminar servicio");
        itemEliminar.addActionListener(e -> eliminarServicioSeleccionado());
        menuContextual.add(itemEliminar);

        tablaServicios.setComponentPopupMenu(menuContextual);

        tablaServicios.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = tablaServicios.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tablaServicios.getRowCount()) {
                    tablaServicios.setRowSelectionInterval(row, row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaServicios);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableContainer.add(scrollPane, BorderLayout.CENTER);
        panel.add(tableContainer, BorderLayout.CENTER);

        return panel;
    }

    public void cargarDatos() {
        modeloTabla.setRowCount(0);
        idsServicios.clear();

        List<Servicio> servicios = servicioDAO.obtenerTodos();
        for (Servicio s : servicios) {
            idsServicios.add(s.getId());
            Object[] fila = {
                    s.getNombre(),
                    s.getDescripcion() != null ? s.getDescripcion() : "",
                    String.format("%.2f €", s.getPrecio()),
                    s.getDuracionMin() + " min"
            };
            modeloTabla.addRow(fila);
        }
    }

    private void abrirNuevoServicio() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        NuevoServicioDialog dialog = new NuevoServicioDialog(parent);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarDatos();
        }
    }

    private void editarServicioSeleccionado() {
        int fila = tablaServicios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idServicio = idsServicios.get(fila);
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        EditarServicioDialog dialog = new EditarServicioDialog(parent, idServicio);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarDatos();
        }
    }

    private void eliminarServicioSeleccionado() {
        int fila = tablaServicios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idServicio = idsServicios.get(fila);
        String nombre = modeloTabla.getValueAt(fila, 0).toString();

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el servicio '" + nombre + "'?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            if (servicioDAO.eliminar(idServicio)) {
                JOptionPane.showMessageDialog(this, "Servicio eliminado", "Exito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}