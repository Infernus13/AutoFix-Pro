package com.autofix.vista;

import com.autofix.controlador.CitaController;
import com.autofix.modelo.Cita;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ReportesPanel extends JPanel {

    private final CitaController citaController;

    private final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private final Color COLOR_FONDO = new Color(249, 250, 251);
    private final Color COLOR_BLANCO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(31, 41, 55);
    private final Color COLOR_VERDE = new Color(34, 197, 94);
    private final Color COLOR_AMARILLO = new Color(234, 179, 8);
    private final Color COLOR_AZUL = new Color(59, 130, 246);
    private final Color COLOR_MORADO = new Color(139, 92, 246);
    private final Color COLOR_ROJO = new Color(239, 68, 68);
    private final Color COLOR_GRIS_CLARO = new Color(243, 244, 246);
    private final Color COLOR_NARANJA = new Color(249, 115, 22);

    private JList<String> listaFechas;
    private DefaultListModel<String> modeloListaFechas;
    private JTable tablaCitasPrincipal;
    private DefaultTableModel modeloTablaCitas;
    private JTable tablaTrabajosActivos;
    private DefaultTableModel modeloTablaTrabajosActivos;
    private JTable tablaCanceladas;
    private DefaultTableModel modeloTablaCanceladas;
    private JComboBox<String> cmbFiltroEstado;

    private JLabel lblTotalCompletadas, lblIngresosDelDia, lblTrabajosEnProceso;
    private JLabel lblTrabajosPendientes, lblIngresosTotales, lblTotalCitas, lblTotalCanceladas;

    private List<Cita> todasLasCitas;
    private List<Integer> idsCitasTablaPrincipal = new ArrayList<>();
    private List<Integer> idsCitasTablaCanceladas = new ArrayList<>();
    private Map<String, List<Cita>> citasPorFecha;
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoFechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ReportesPanel() {
        this.citaController = new CitaController();
        this.citasPorFecha = new HashMap<>();

        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        add(crearPanelSuperior(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(crearPanelIzquierdo());
        splitPane.setRightComponent(crearPanelDerecho());
        splitPane.setDividerLocation(280);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel lblTitulo = new JLabel("📊 Panel de Reportes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_TEXTO);
        panel.add(lblTitulo, BorderLayout.WEST);

        JPanel panelEstadisticas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelEstadisticas.setBackground(COLOR_FONDO);

        panelEstadisticas.add(crearTarjetaEstadistica("Total Citas", "0", COLOR_PRIMARIO, "totalCitas"));
        panelEstadisticas.add(crearTarjetaEstadistica("Ingresos", "0.00 €", COLOR_MORADO, "ingresosTotales"));
        panelEstadisticas.add(crearTarjetaEstadistica("Canceladas", "0", COLOR_ROJO, "totalCanceladas"));

        JButton btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnActualizar.setBackground(COLOR_PRIMARIO);
        btnActualizar.setForeground(COLOR_BLANCO);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(e -> cargarDatos());
        panelEstadisticas.add(btnActualizar);

        panel.add(panelEstadisticas, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearTarjetaEstadistica(String titulo, String valor, Color color, String id) {
        JPanel tarjeta = new JPanel(new BorderLayout(5, 2));
        tarjeta.setBackground(COLOR_BLANCO);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(color, 2, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTitulo.setForeground(Color.GRAY);
        tarjeta.add(lblTitulo, BorderLayout.NORTH);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValor.setForeground(color);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        if ("totalCitas".equals(id)) lblTotalCitas = lblValor;
        else if ("ingresosTotales".equals(id)) lblIngresosTotales = lblValor;
        else if ("totalCanceladas".equals(id)) lblTotalCanceladas = lblValor;

        return tarjeta;
    }

    private JPanel crearChip(String titulo, String valor, Color color) {
        JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        chip.setBackground(COLOR_BLANCO);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTitulo.setForeground(Color.GRAY);
        chip.add(lblTitulo);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblValor.setForeground(color);
        chip.add(lblValor);

        return chip;
    }

    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(COLOR_FONDO);
        panel.add(crearPanelCarpetasFechas(), BorderLayout.CENTER);
        panel.add(crearPanelTrabajosActivos(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelCarpetasFechas() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(COLOR_BLANCO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_GRIS_CLARO, 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitulo = new JLabel("📁 Historial por Día");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(lblTitulo, BorderLayout.NORTH);

        modeloListaFechas = new DefaultListModel<>();
        listaFechas = new JList<>(modeloListaFechas);
        listaFechas.setFixedCellHeight(40);
        listaFechas.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String[] partes = value.toString().split("\\|");
                setText("📅 " + partes[0] + " (" + partes[1] + " citas)");
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                if (isSelected) {
                    setBackground(COLOR_PRIMARIO);
                    setForeground(COLOR_BLANCO);
                }
                return this;
            }
        });
        listaFechas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarCitasDelDiaSeleccionado();
        });

        JScrollPane scroll = new JScrollPane(listaFechas);
        scroll.setPreferredSize(new Dimension(250, 200));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelTrabajosActivos() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(COLOR_BLANCO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_NARANJA, 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(250, 220));

        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(COLOR_BLANCO);

        JLabel lblTitulo = new JLabel("⚡ Trabajos Activos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(COLOR_NARANJA);
        panelTitulo.add(lblTitulo, BorderLayout.WEST);

        JPanel panelContadores = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelContadores.setBackground(COLOR_BLANCO);

        JPanel chipPend = crearChip("Pend:", "0", COLOR_AMARILLO);
        lblTrabajosPendientes = (JLabel) chipPend.getComponent(1);
        panelContadores.add(chipPend);

        JPanel chipProc = crearChip("Proc:", "0", COLOR_AZUL);
        lblTrabajosEnProceso = (JLabel) chipProc.getComponent(1);
        panelContadores.add(chipProc);

        panelTitulo.add(panelContadores, BorderLayout.EAST);
        panel.add(panelTitulo, BorderLayout.NORTH);

        String[] cols = {"Estado", "Cliente", "Vehículo"};
        modeloTablaTrabajosActivos = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaTrabajosActivos = new JTable(modeloTablaTrabajosActivos);
        tablaTrabajosActivos.setRowHeight(28);
        tablaTrabajosActivos.getTableHeader().setBackground(COLOR_NARANJA);
        tablaTrabajosActivos.getTableHeader().setForeground(COLOR_BLANCO);

        panel.add(new JScrollPane(tablaTrabajosActivos), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(COLOR_FONDO);
        panel.add(crearPanelResumenYFiltro(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.addTab("📋 Citas", crearPanelTablaCitas());
        tabs.addTab("❌ Canceladas", crearPanelCanceladas());
        tabs.addTab("📁 Historico", new HistoricoPanel());
        panel.add(tabs, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelResumenYFiltro() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setBackground(COLOR_FONDO);

        // Resumen
        JPanel panelResumen = new JPanel(new BorderLayout(10, 10));
        panelResumen.setBackground(COLOR_BLANCO);
        panelResumen.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_VERDE, 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTit = new JLabel("📅 Resumen del Día");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTit.setForeground(COLOR_VERDE);
        panelResumen.add(lblTit, BorderLayout.NORTH);

        JPanel datos = new JPanel(new GridLayout(1, 2, 10, 0));
        datos.setBackground(COLOR_BLANCO);

        JPanel f1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        f1.setBackground(COLOR_BLANCO);
        f1.add(new JLabel("✅"));
        lblTotalCompletadas = new JLabel("0");
        lblTotalCompletadas.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalCompletadas.setForeground(COLOR_VERDE);
        f1.add(lblTotalCompletadas);
        datos.add(f1);

        JPanel f2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        f2.setBackground(COLOR_BLANCO);
        f2.add(new JLabel("💰"));
        lblIngresosDelDia = new JLabel("0.00 €");
        lblIngresosDelDia.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblIngresosDelDia.setForeground(COLOR_MORADO);
        f2.add(lblIngresosDelDia);
        datos.add(f2);

        panelResumen.add(datos, BorderLayout.CENTER);
        panel.add(panelResumen);

        // Filtro
        JPanel panelFiltro = new JPanel(new BorderLayout(10, 10));
        panelFiltro.setBackground(COLOR_BLANCO);
        panelFiltro.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_PRIMARIO, 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblFiltro = new JLabel("🔍 Filtrar");
        lblFiltro.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFiltro.setForeground(COLOR_PRIMARIO);
        panelFiltro.add(lblFiltro, BorderLayout.NORTH);

        cmbFiltroEstado = new JComboBox<>(new String[]{"📋 Todas", "✅ Completadas", "🔧 En Proceso", "⏳ Pendientes", "❌ Canceladas"});
        cmbFiltroEstado.addActionListener(e -> aplicarFiltro());
        panelFiltro.add(cmbFiltroEstado, BorderLayout.CENTER);
        panel.add(panelFiltro);

        return panel;
    }

    private JPanel crearPanelTablaCitas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BLANCO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Cliente", "Vehículo", "Matrícula", "Servicios", "Empleado", "Fecha", "Estado", "Precio"};
        modeloTablaCitas = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaCitasPrincipal = new JTable(modeloTablaCitas);
        tablaCitasPrincipal.setRowHeight(32);

        // Renderizador de colores para la columna Estado (Índice 6)
        tablaCitasPrincipal.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                if (value != null) {
                    String estado = value.toString().toLowerCase();
                    if (estado.contains("pendiente")) label.setForeground(COLOR_AMARILLO);
                    else if (estado.contains("proceso") || estado.contains("en_proceso")) label.setForeground(COLOR_AZUL);
                    else if (estado.contains("completada")) label.setForeground(COLOR_VERDE);
                    else if (estado.contains("cancelada")) label.setForeground(COLOR_ROJO);
                }
                if (isSelected) label.setBackground(new Color(219, 234, 254));
                else label.setBackground(COLOR_BLANCO);
                return label;
            }
        });

        tablaCitasPrincipal.getTableHeader().setBackground(COLOR_PRIMARIO);
        tablaCitasPrincipal.getTableHeader().setForeground(COLOR_BLANCO);

        // Configuración del doble clic
        tablaCitasPrincipal.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) verDetallesCita();
            }
        });

        panel.add(new JScrollPane(tablaCitasPrincipal), BorderLayout.CENTER);
        return panel;
    }
    private JPanel crearPanelCanceladas() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(COLOR_BLANCO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("<html><b>❌ Registro de Cancelaciones</b> - Motivo, quién canceló y cuándo</html>");
        lblInfo.setForeground(COLOR_ROJO);
        lblInfo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_ROJO, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        lblInfo.setBackground(new Color(254, 226, 226));
        lblInfo.setOpaque(true);
        panel.add(lblInfo, BorderLayout.NORTH);

        String[] cols = {"Cliente", "Vehículo", "Fecha Cita", "Cancelado Por", "Fecha Cancel.", "Motivo"};
        modeloTablaCanceladas = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCanceladas = new JTable(modeloTablaCanceladas);
        tablaCanceladas.setRowHeight(40);
        tablaCanceladas.getTableHeader().setBackground(COLOR_ROJO);
        tablaCanceladas.getTableHeader().setForeground(COLOR_BLANCO);
        tablaCanceladas.getColumnModel().getColumn(5).setPreferredWidth(300);

        tablaCanceladas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) verDetalleCancelacion();
            }
        });

        panel.add(new JScrollPane(tablaCanceladas), BorderLayout.CENTER);
        return panel;
    }

    public void cargarReporte() { cargarDatos(); }

    public void cargarDatos() {
        todasLasCitas = citaController.obtenerTodas();
        organizarCitasPorFecha();
        cargarListaFechas();
        cargarTrabajosActivos();
        cargarCitasCanceladas();
        calcularEstadisticas();

        if (cmbFiltroEstado != null) {
            cmbFiltroEstado.setSelectedIndex(0);
        }
        if (modeloListaFechas.size() > 0) {
            listaFechas.setSelectedIndex(0);

            cargarCitasDelDiaSeleccionado();
        }
    }

    private void organizarCitasPorFecha() {
        citasPorFecha.clear();
        if (todasLasCitas == null) return;

        for (Cita c : todasLasCitas) {
            if (c.getFecha() != null) {
                String f = formatoFecha.format(c.getFecha());
                citasPorFecha.computeIfAbsent(f, k -> new ArrayList<>()).add(c);
            }
        }
    }

    private void cargarListaFechas() {
        modeloListaFechas.clear();
        List<String> fechas = new ArrayList<>(citasPorFecha.keySet());
        fechas.sort((a, b) -> {
            try {
                return formatoFecha.parse(b).compareTo(formatoFecha.parse(a));
            } catch (Exception e) { return 0; }
        });
        for (String f : fechas) {
            modeloListaFechas.addElement(f + "|" + citasPorFecha.get(f).size());
        }
    }

    private void cargarTrabajosActivos() {
        modeloTablaTrabajosActivos.setRowCount(0);
        int pend = 0, proc = 0;
        for (Cita c : todasLasCitas) {
            if (!c.isArchivada()) {
                if ("pendiente".equals(c.getEstado())) {
                    modeloTablaTrabajosActivos.addRow(new Object[]{"⏳ Pend.", c.getNombreCliente(), c.getModeloCoche()});
                    pend++;
                } else if ("en_proceso".equals(c.getEstado())) {
                    modeloTablaTrabajosActivos.addRow(new Object[]{"🔧 Proc.", c.getNombreCliente(), c.getModeloCoche()});
                    proc++;
                }
            }
        }
        lblTrabajosPendientes.setText(String.valueOf(pend));
        lblTrabajosEnProceso.setText(String.valueOf(proc));
    }

    private void cargarCitasCanceladas() {
        modeloTablaCanceladas.setRowCount(0);
        idsCitasTablaCanceladas.clear(); // Limpiamos la lista oculta

        for (Cita c : todasLasCitas) {
            if ("cancelada".equals(c.getEstado())) {
                idsCitasTablaCanceladas.add(c.getId()); // Guardamos el ID en la lista oculta

                String fechaCita = c.getFecha() != null ? formatoFecha.format(c.getFecha()) : "-";
                String fechaCancel = c.getFechaCancelacion() != null ? formatoFechaHora.format(c.getFechaCancelacion()) : "-";
                String motivo = c.getMotivoCancelacion() != null ? c.getMotivoCancelacion() : "-";
                String canceladoPor = c.getCanceladoPor() != null ? c.getCanceladoPor() : "-";

                // No incluimos el ID en el addRow
                modeloTablaCanceladas.addRow(new Object[]{
                        c.getNombreCliente(), c.getModeloCoche(),
                        fechaCita, canceladoPor, fechaCancel, motivo
                });
            }
        }
    }

    private void calcularEstadisticas() {
        int total = 0, canceladas = 0;
        double ingresos = 0;
        for (Cita c : todasLasCitas) {
            if ("completada".equals(c.getEstado())) {
                total++;
                ingresos += c.getPrecioFinal();
            } else if ("cancelada".equals(c.getEstado())) {
                canceladas++;
            }
        }
        lblTotalCitas.setText(String.valueOf(total));
        lblIngresosTotales.setText(String.format("%.2f €", ingresos));
        lblTotalCanceladas.setText(String.valueOf(canceladas));
    }

    private void cargarCitasDelDiaSeleccionado() {

        aplicarFiltro();

        String sel = listaFechas.getSelectedValue();
        if (sel != null) {
            String fecha = sel.split("\\|")[0].trim();
            List<Cita> citas = citasPorFecha.get(fecha);
            if (citas != null) {
                double ing = 0;
                int completadasDia = 0;
                for (Cita c : citas) {
                    if ("completada".equalsIgnoreCase(c.getEstado().replace("_", " ").trim())) {
                        ing += c.getPrecioFinal();
                        completadasDia++;
                    }
                }
                lblTotalCompletadas.setText(String.valueOf(completadasDia));
                lblIngresosDelDia.setText(String.format("%.2f €", ing));
            }
        }
    }

    private void aplicarFiltro() {
        String sel = listaFechas.getSelectedValue();
        // Si no hay fecha seleccionada, vaciamos la tabla y salimos
        if (sel == null) {
            modeloTablaCitas.setRowCount(0);
            return;
        }

        String fechaSel = sel.split("\\|")[0].trim();
        List<Cita> citasDelDia = citasPorFecha.get(fechaSel);

        modeloTablaCitas.setRowCount(0);
        idsCitasTablaPrincipal.clear();

        if (citasDelDia == null) return;


        Object comboSel = cmbFiltroEstado.getSelectedItem();
        String filtroTexto = (comboSel != null) ? comboSel.toString().toLowerCase() : "todas";

        for (Cita c : citasDelDia) {

            String estadoDB = (c.getEstado() != null) ? c.getEstado().toLowerCase().replace("_", " ").trim() : "";
            boolean pasaFiltro = false;


            if (filtroTexto.contains("todas")) {
                pasaFiltro = true;
            } else if (filtroTexto.contains("completada") && estadoDB.equals("completada")) {
                pasaFiltro = true;
            } else if (filtroTexto.contains("proceso") && estadoDB.contains("proceso")) {
                pasaFiltro = true;
            } else if (filtroTexto.contains("pendiente") && estadoDB.equals("pendiente")) {
                pasaFiltro = true;
            } else if (filtroTexto.contains("cancelada") && estadoDB.equals("cancelada")) {
                pasaFiltro = true;
            }

            if (pasaFiltro) {
                idsCitasTablaPrincipal.add(c.getId());
                modeloTablaCitas.addRow(new Object[]{
                        c.getNombreCliente(), c.getModeloCoche(), c.getMatricula(),
                        c.getNombreServicio(), c.getNombreUsuario(),
                        c.getFecha() != null ? formatoFecha.format(c.getFecha()) : "-",
                        c.getEstado(), // Aquí se verá el texto original
                        String.format("%.2f €", c.getPrecioFinal())
                });
            }
        }
    }

    private void verDetallesCita() {
        int fila = tablaCitasPrincipal.getSelectedRow();
        if (fila < 0) return;

        // IMPORTANTE: Obtenemos el ID de la lista oculta usando el índice de la fila seleccionada
        int id = idsCitasTablaPrincipal.get(fila);

        // Buscamos la cita en nuestra lista completa de objetos
        Cita c = todasLasCitas.stream().filter(x -> x.getId() == id).findFirst().orElse(null);

        if (c != null) {
            String mensaje = String.format(
                    "📝 DETALLES DE LA CITA\n" +
                            "━━━━━━━━━━━━━━━━━━━━━\n" +
                            "🆔 ID: %d\n" +
                            "👤 Cliente: %s\n" +
                            "🚗 Vehículo: %s\n" +
                            "🔢 Matrícula: %s\n" +
                            "🛠️ Servicio: %s\n" +
                            "👨‍🔧 Empleado: %s\n" +
                            "📅 Fecha: %s\n" +
                            "📊 Estado: %s\n" +
                            "💰 Precio: %.2f €",
                    c.getId(), c.getNombreCliente(), c.getModeloCoche(), c.getMatricula(),
                    c.getNombreServicio(), c.getNombreUsuario(),
                    formatoFecha.format(c.getFecha()), c.getEstado(), c.getPrecioFinal()
            );

            JOptionPane.showMessageDialog(this, mensaje, "Información de Cita #" + id, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void verDetalleCancelacion() {
        int fila = tablaCanceladas.getSelectedRow();
        if (fila < 0 || fila >= idsCitasTablaCanceladas.size()) return;

        // Obtenemos el ID de la lista interna
        int id = idsCitasTablaCanceladas.get(fila);

        // Buscamos el objeto cita original para tener todos los datos
        Cita c = todasLasCitas.stream().filter(x -> x.getId() == id).findFirst().orElse(null);

        if (c != null) {
            String mensaje = String.format(
                    "═══════════════════════════════════\n" +
                            "      DETALLE DE CANCELACIÓN #%d\n" +
                            "═══════════════════════════════════\n\n" +
                            "👤 Cliente: %s\n" +
                            "🚗 Vehículo: %s\n" +
                            "📅 Fecha de la cita: %s\n\n" +
                            "❌ INFORMACIÓN DE CANCELACIÓN:\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "👷 Cancelado por: %s\n" +
                            "🕐 Fecha/Hora: %s\n\n" +
                            "📝 Motivo:\n%s",
                    c.getId(), c.getNombreCliente(), c.getModeloCoche(),
                    formatoFecha.format(c.getFecha()), c.getCanceladoPor(),
                    formatoFechaHora.format(c.getFechaCancelacion()), c.getMotivoCancelacion()
            );
            JOptionPane.showMessageDialog(this, mensaje, "Detalle de Cancelación", JOptionPane.WARNING_MESSAGE);
        }
    }
}