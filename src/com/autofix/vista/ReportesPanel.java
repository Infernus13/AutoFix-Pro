package com.autofix.vista;

import com.autofix.dao.CitaDAO;
import com.autofix.modelo.Cita;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class ReportesPanel extends JPanel {

    private CitaDAO citaDAO;
    private List<Integer> idsCitas = new ArrayList<>();

    private JTable tablaReporte;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbTipoReporte;
    private JLabel lblTotalRegistros;
    private JLabel lblTotalIngresos;

    private static final Color COLOR_BG = new Color(249, 250, 251);
    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private static final Color COLOR_TEXTO = new Color(31, 41, 55);
    private static final Color COLOR_GRIS = new Color(107, 114, 128);

    public ReportesPanel() {
        citaDAO = new CitaDAO();
        configurarPanel();
        crearComponentes();
        cargarReporte();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
    }

    private void crearComponentes() {
        add(crearToolbar(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
    }

    private JPanel crearToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)),
                new EmptyBorder(18, 25, 18, 25)
        ));

        JLabel titulo = new JLabel("Reportes");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(COLOR_TEXTO);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setBackground(Color.WHITE);

        JLabel lblTipo = new JLabel("Tipo de reporte:");
        lblTipo.setFont(new Font("Arial", Font.PLAIN, 13));

        cmbTipoReporte = new JComboBox<>(new String[]{
                "Todas las Citas",
                "Citas Completadas",
                "Citas Pendientes",
                "Citas Canceladas"
        });
        cmbTipoReporte.setPreferredSize(new Dimension(180, 38));
        cmbTipoReporte.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbTipoReporte.addActionListener(e -> cargarReporte());

        rightPanel.add(lblTipo);
        rightPanel.add(cmbTipoReporte);

        toolbar.add(titulo, BorderLayout.WEST);
        toolbar.add(rightPanel, BorderLayout.EAST);

        return toolbar;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        panel.add(crearPanelEstadisticas(), BorderLayout.NORTH);
        panel.add(crearPanelTabla(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        statsPanel.setBackground(COLOR_BG);
        statsPanel.setPreferredSize(new Dimension(0, 100));

        // Tarjeta Total Registros
        JPanel cardRegistros = new JPanel(new BorderLayout());
        cardRegistros.setBackground(Color.WHITE);
        cardRegistros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(20, 25, 20, 25)
        ));

        JLabel lblTituloReg = new JLabel("Total Registros");
        lblTituloReg.setFont(new Font("Arial", Font.PLAIN, 13));
        lblTituloReg.setForeground(COLOR_GRIS);

        lblTotalRegistros = new JLabel("0");
        lblTotalRegistros.setFont(new Font("Arial", Font.BOLD, 32));
        lblTotalRegistros.setForeground(COLOR_PRIMARIO);

        JPanel leftReg = new JPanel();
        leftReg.setLayout(new BoxLayout(leftReg, BoxLayout.Y_AXIS));
        leftReg.setBackground(Color.WHITE);
        leftReg.add(lblTituloReg);
        leftReg.add(Box.createRigidArea(new Dimension(0, 8)));
        leftReg.add(lblTotalRegistros);

        cardRegistros.add(leftReg, BorderLayout.WEST);

        // Tarjeta Total Ingresos
        JPanel cardIngresos = new JPanel(new BorderLayout());
        cardIngresos.setBackground(Color.WHITE);
        cardIngresos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(20, 25, 20, 25)
        ));

        JLabel lblTituloIng = new JLabel("Total Ingresos");
        lblTituloIng.setFont(new Font("Arial", Font.PLAIN, 13));
        lblTituloIng.setForeground(COLOR_GRIS);

        lblTotalIngresos = new JLabel("0.00 €");
        lblTotalIngresos.setFont(new Font("Arial", Font.BOLD, 32));
        lblTotalIngresos.setForeground(new Color(34, 197, 94));

        JPanel leftIng = new JPanel();
        leftIng.setLayout(new BoxLayout(leftIng, BoxLayout.Y_AXIS));
        leftIng.setBackground(Color.WHITE);
        leftIng.add(lblTituloIng);
        leftIng.add(Box.createRigidArea(new Dimension(0, 8)));
        leftIng.add(lblTotalIngresos);

        cardIngresos.add(leftIng, BorderLayout.WEST);

        statsPanel.add(cardRegistros);
        statsPanel.add(cardIngresos);

        return statsPanel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));

        String[] columnas = {"Cliente", "Vehiculo", "Fecha", "Empleado", "Servicio", "Estado", "Precio"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaReporte = new JTable(modeloTabla);
        tablaReporte.setRowHeight(40);
        tablaReporte.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaReporte.setGridColor(new Color(243, 244, 246));
        tablaReporte.setSelectionBackground(new Color(219, 234, 254));

        tablaReporte.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaReporte.getTableHeader().setBackground(new Color(249, 250, 251));
        tablaReporte.getTableHeader().setForeground(COLOR_GRIS);
        tablaReporte.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Centrar contenido
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tablaReporte.getColumnCount(); i++) {
            tablaReporte.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Renderizador estado (columna 5)
        tablaReporte.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);

                String estado = value.toString();
                switch (estado) {
                    case "completada":
                        label.setForeground(new Color(34, 197, 94));
                        break;
                    case "pendiente":
                        label.setForeground(new Color(234, 179, 8));
                        break;
                    case "cancelada":
                        label.setForeground(new Color(239, 68, 68));
                        break;
                    case "en_proceso":
                        label.setForeground(new Color(59, 130, 246));
                        break;
                    default:
                        label.setForeground(COLOR_TEXTO);
                }
                label.setFont(new Font("Arial", Font.BOLD, 12));
                return label;
            }
        });

        // Doble clic para ver detalles
        tablaReporte.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaReporte.getSelectedRow();
                    if (fila >= 0) {
                        int idCita = idsCitas.get(fila);
                        Frame parent = (Frame) SwingUtilities.getWindowAncestor(ReportesPanel.this);
                        VerCitaDialog dialog = new VerCitaDialog(parent, idCita);
                        dialog.setVisible(true);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaReporte);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public void cargarReporte() {
        modeloTabla.setRowCount(0);
        idsCitas.clear();

        String filtro = (String) cmbTipoReporte.getSelectedItem();
        List<Cita> citas;

        switch (filtro) {
            case "Citas Completadas":
                citas = citaDAO.obtenerPorEstado("completada");
                break;
            case "Citas Pendientes":
                citas = citaDAO.obtenerPorEstado("pendiente");
                break;
            case "Citas Canceladas":
                citas = citaDAO.obtenerPorEstado("cancelada");
                break;
            default:
                citas = citaDAO.obtenerTodas();
        }

        double totalIngresos = 0;

        for (Cita c : citas) {
            idsCitas.add(c.getId());

            String vehiculo = "";
            if (c.getMatricula() != null && c.getModeloCoche() != null) {
                vehiculo = c.getMatricula() + " - " + c.getModeloCoche();
            } else if (c.getMatricula() != null) {
                vehiculo = c.getMatricula();
            }

            Object[] fila = {
                    c.getNombreCliente(),
                    vehiculo,
                    c.getFecha().toString(),
                    c.getNombreUsuario(),
                    c.getNombreServicio(),
                    c.getEstado(),
                    String.format("%.2f €", c.getPrecioFinal())
            };
            modeloTabla.addRow(fila);

            if ("completada".equals(c.getEstado())) {
                totalIngresos += c.getPrecioFinal();
            }
        }

        lblTotalRegistros.setText(String.valueOf(citas.size()));
        lblTotalIngresos.setText(String.format("%.2f €", totalIngresos));
    }
}