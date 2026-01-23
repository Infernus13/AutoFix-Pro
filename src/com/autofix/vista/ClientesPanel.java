package com.autofix.vista;

import com.autofix.dao.ClienteDAO;
import com.autofix.modelo.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class ClientesPanel extends JPanel {

    private ClienteDAO clienteDAO;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private List<Integer> idsClientes = new ArrayList<>();

    private static final Color COLOR_BG = new Color(249, 250, 251);
    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private static final Color COLOR_TEXTO = new Color(31, 41, 55);
    private static final Color COLOR_GRIS = new Color(107, 114, 128);

    public ClientesPanel() {
        clienteDAO = new ClienteDAO();
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

        JLabel titulo = new JLabel("Gestion de Clientes");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(COLOR_TEXTO);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setBackground(Color.WHITE);

        // Campo busqueda
        txtBuscar = new JTextField(20);
        txtBuscar.setPreferredSize(new Dimension(250, 38));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                buscarClientes();
            }
        });

        // Boton nuevo
        JButton btnNuevo = new JButton("+ Nuevo Cliente");
        btnNuevo.setBackground(COLOR_PRIMARIO);
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        btnNuevo.setBorderPainted(false);
        btnNuevo.setFont(new Font("Arial", Font.BOLD, 13));
        btnNuevo.setPreferredSize(new Dimension(150, 38));
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevo.addActionListener(e -> abrirNuevoCliente());

        rightPanel.add(txtBuscar);
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

        String[] columnas = {"Nombre", "Telefono", "Email", "Direccion"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setRowHeight(45);
        tablaClientes.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaClientes.setGridColor(new Color(243, 244, 246));
        tablaClientes.setSelectionBackground(new Color(219, 234, 254));

        // Header
        tablaClientes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaClientes.getTableHeader().setBackground(new Color(249, 250, 251));
        tablaClientes.getTableHeader().setForeground(COLOR_GRIS);
        tablaClientes.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Centrar contenido
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tablaClientes.getColumnCount(); i++) {
            tablaClientes.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Menu contextual
        JPopupMenu menuContextual = new JPopupMenu();

        JMenuItem itemEditar = new JMenuItem("Editar cliente");
        itemEditar.addActionListener(e -> editarClienteSeleccionado());
        menuContextual.add(itemEditar);

        JMenuItem itemEliminar = new JMenuItem("Eliminar cliente");
        itemEliminar.addActionListener(e -> eliminarClienteSeleccionado());
        menuContextual.add(itemEliminar);

        tablaClientes.setComponentPopupMenu(menuContextual);

        tablaClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = tablaClientes.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tablaClientes.getRowCount()) {
                    tablaClientes.setRowSelectionInterval(row, row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableContainer.add(scrollPane, BorderLayout.CENTER);
        panel.add(tableContainer, BorderLayout.CENTER);

        return panel;
    }

    public void cargarDatos() {
        modeloTabla.setRowCount(0);
        idsClientes.clear();

        List<Cliente> clientes = clienteDAO.obtenerTodos();
        for (Cliente c : clientes) {
            idsClientes.add(c.getId());
            Object[] fila = {
                    c.getNombre(),
                    c.getTelefono(),
                    c.getEmail() != null ? c.getEmail() : "",
                    c.getDireccion() != null ? c.getDireccion() : ""
            };
            modeloTabla.addRow(fila);
        }
    }

    private void buscarClientes() {
        String busqueda = txtBuscar.getText().trim();
        modeloTabla.setRowCount(0);

        List<Cliente> clientes;
        if (busqueda.isEmpty()) {
            clientes = clienteDAO.obtenerTodos();
        } else {
            clientes = clienteDAO.buscarPorNombre(busqueda);
        }

        for (Cliente c : clientes) {
            Object[] fila = {
                    c.getId(),
                    c.getNombre(),
                    c.getTelefono(),
                    c.getEmail() != null ? c.getEmail() : "-",
                    c.getDireccion() != null ? c.getDireccion() : "-"
            };
            modeloTabla.addRow(fila);
        }
    }

    private void abrirNuevoCliente() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        NuevoClienteDialog dialog = new NuevoClienteDialog(parent);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarDatos();
        }
    }

    private void editarClienteSeleccionado() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCliente = idsClientes.get(fila);
        String nombre = modeloTabla.getValueAt(fila, 0).toString();
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        EditarClienteDialog dialog = new EditarClienteDialog(parent, idCliente);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarDatos();
        }
    }

    private void eliminarClienteSeleccionado() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCliente = (int) modeloTabla.getValueAt(fila, 0);
        String nombre = modeloTabla.getValueAt(fila, 1).toString();

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el cliente '" + nombre + "'?\nSe eliminaran tambien sus citas.",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            if (clienteDAO.eliminar(idCliente)) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado", "Exito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar: el cliente tiene citas asociadas", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}