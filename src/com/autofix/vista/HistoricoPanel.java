package com.autofix.vista;

import com.autofix.dao.HistoricoDAO;

// Imports para iTextPDF 5
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Panel para consultar los períodos históricos
 * y exportar informes en PDF.
 */
public class HistoricoPanel extends JPanel {

    // Colores del tema
    private final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private final Color COLOR_FONDO = new Color(249, 250, 251);
    private final Color COLOR_BLANCO = java.awt.Color.WHITE;
    private final Color COLOR_TEXTO = new Color(31, 41, 55);
    private final Color COLOR_GRIS = new Color(107, 114, 128);
    private final Color COLOR_GRIS_CLARO = new Color(243, 244, 246);
    private final Color COLOR_VERDE = new Color(34, 197, 94);
    private final Color COLOR_ROJO = new Color(239, 68, 68);
    private final Color COLOR_AZUL = new Color(59, 130, 246);
    private final Color COLOR_AMARILLO = new Color(234, 179, 8);

    private HistoricoDAO historicoDAO;

    private JList<String> listaPeriodos;
    private DefaultListModel<String> modeloListaPeriodos;
    private List<Map<String, Object>> periodosCargados;

    private JLabel lblNombrePeriodo, lblFechas, lblTotalCitas;
    private JLabel lblCompletadas, lblCanceladas, lblIngresos, lblCerradoPor;

    private JTable tablaCitas;
    private DefaultTableModel modeloTablaCitas;

    public HistoricoPanel() {
        this.historicoDAO = new HistoricoDAO();
        setLayout(new BorderLayout(15, 0));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        inicializarComponentes();
        cargarPeriodos();
    }

    private void inicializarComponentes() {
        // ========== PANEL IZQUIERDO ==========
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 10));
        panelIzquierdo.setBackground(COLOR_BLANCO);
        panelIzquierdo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_GRIS_CLARO, 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panelIzquierdo.setPreferredSize(new Dimension(250, 0));

        JLabel lblTituloPeriodos = new JLabel("Periodos Anteriores");
        lblTituloPeriodos.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTituloPeriodos.setForeground(COLOR_TEXTO);
        panelIzquierdo.add(lblTituloPeriodos, BorderLayout.NORTH);

        modeloListaPeriodos = new DefaultListModel<>();
        listaPeriodos = new JList<>(modeloListaPeriodos);
        listaPeriodos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaPeriodos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaPeriodos.setFixedCellHeight(40);
        listaPeriodos.setCellRenderer(new PeriodoListRenderer());
        listaPeriodos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarDetallePeriodo();
        });

        JScrollPane scrollPeriodos = new JScrollPane(listaPeriodos);
        scrollPeriodos.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelIzquierdo.add(scrollPeriodos, BorderLayout.CENTER);

        // Panel de botones inferiores
        JPanel panelBotonesIzq = new JPanel(new GridLayout(2, 1, 0, 5));
        panelBotonesIzq.setBackground(COLOR_BLANCO);
        panelBotonesIzq.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton btnExportarPDF = new JButton("Exportar PDF");
        btnExportarPDF.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExportarPDF.setBackground(COLOR_PRIMARIO);
        btnExportarPDF.setForeground(COLOR_BLANCO);
        btnExportarPDF.setFocusPainted(false);
        btnExportarPDF.setBorderPainted(false);
        btnExportarPDF.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportarPDF.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnExportarPDF.addActionListener(e -> exportarPDF());
        panelBotonesIzq.add(btnExportarPDF);

        JButton btnEliminar = new JButton("Eliminar Periodo");
        btnEliminar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnEliminar.setBackground(new Color(254, 226, 226));
        btnEliminar.setForeground(COLOR_ROJO);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnEliminar.addActionListener(e -> eliminarPeriodoSeleccionado());
        panelBotonesIzq.add(btnEliminar);

        panelIzquierdo.add(panelBotonesIzq, BorderLayout.SOUTH);
        add(panelIzquierdo, BorderLayout.WEST);

        // ========== PANEL DERECHO ==========
        JPanel panelDerecho = new JPanel(new BorderLayout(0, 10));
        panelDerecho.setBackground(COLOR_FONDO);

        // Tarjetas de resumen
        JPanel panelResumen = new JPanel(new GridLayout(1, 4, 10, 0));
        panelResumen.setBackground(COLOR_FONDO);

        // Tarjeta Info
        JPanel tarjetaInfo = crearTarjeta(COLOR_PRIMARIO);
        lblNombrePeriodo = new JLabel("Selecciona un periodo");
        lblNombrePeriodo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombrePeriodo.setForeground(COLOR_TEXTO);
        lblFechas = new JLabel("-");
        lblFechas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFechas.setForeground(COLOR_GRIS);
        lblCerradoPor = new JLabel("");
        lblCerradoPor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCerradoPor.setForeground(COLOR_GRIS);
        tarjetaInfo.add(new JLabel("Periodo"));
        tarjetaInfo.add(lblNombrePeriodo);
        tarjetaInfo.add(lblFechas);
        tarjetaInfo.add(lblCerradoPor);

        // Tarjeta Citas
        JPanel tarjetaCitas = crearTarjeta(COLOR_AZUL);
        lblTotalCitas = new JLabel("0");
        lblTotalCitas.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotalCitas.setForeground(COLOR_AZUL);
        lblCompletadas = new JLabel("0 completadas");
        lblCompletadas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCompletadas.setForeground(COLOR_GRIS);
        tarjetaCitas.add(new JLabel("Total Citas"));
        tarjetaCitas.add(lblTotalCitas);
        tarjetaCitas.add(lblCompletadas);

        // Tarjeta Canceladas
        JPanel tarjetaCanceladas = crearTarjeta(COLOR_ROJO);
        lblCanceladas = new JLabel("0");
        lblCanceladas.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblCanceladas.setForeground(COLOR_ROJO);
        tarjetaCanceladas.add(new JLabel("Canceladas"));
        tarjetaCanceladas.add(lblCanceladas);

        // Tarjeta Ingresos
        JPanel tarjetaIngresos = crearTarjeta(COLOR_VERDE);
        lblIngresos = new JLabel("0.00 EUR");
        lblIngresos.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblIngresos.setForeground(COLOR_VERDE);
        tarjetaIngresos.add(new JLabel("Ingresos"));
        tarjetaIngresos.add(lblIngresos);

        panelResumen.add(tarjetaInfo);
        panelResumen.add(tarjetaCitas);
        panelResumen.add(tarjetaCanceladas);
        panelResumen.add(tarjetaIngresos);
        panelDerecho.add(panelResumen, BorderLayout.NORTH);

        // Tabla de citas
        String[] columnas = {"Fecha", "Hora", "Cliente", "Vehiculo", "Servicios", "Estado", "Precio"};
        modeloTablaCitas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaCitas = new JTable(modeloTablaCitas);
        tablaCitas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaCitas.setRowHeight(32);
        tablaCitas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaCitas.getTableHeader().setBackground(COLOR_PRIMARIO);
        tablaCitas.getTableHeader().setForeground(COLOR_BLANCO);
        tablaCitas.setSelectionBackground(new Color(219, 234, 254));
        tablaCitas.setGridColor(COLOR_GRIS_CLARO);

        // Renderer estado
        tablaCitas.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String estado = value.toString().toLowerCase();
                    switch (estado) {
                        case "completada": setForeground(COLOR_VERDE); break;
                        case "cancelada": setForeground(COLOR_ROJO); break;
                        case "pendiente": setForeground(COLOR_AMARILLO); break;
                        case "en_proceso": setForeground(COLOR_AZUL); break;
                        default: setForeground(COLOR_TEXTO);
                    }
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                return c;
            }
        });

        // Renderer precio
        tablaCitas.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                return c;
            }
        });

        tablaCitas.getColumnModel().getColumn(0).setPreferredWidth(90);
        tablaCitas.getColumnModel().getColumn(1).setPreferredWidth(60);
        tablaCitas.getColumnModel().getColumn(2).setPreferredWidth(130);
        tablaCitas.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaCitas.getColumnModel().getColumn(4).setPreferredWidth(180);
        tablaCitas.getColumnModel().getColumn(5).setPreferredWidth(90);
        tablaCitas.getColumnModel().getColumn(6).setPreferredWidth(80);

        JScrollPane scrollTabla = new JScrollPane(tablaCitas);
        scrollTabla.setBorder(new LineBorder(COLOR_GRIS_CLARO, 1, true));

        JPanel panelTabla = new JPanel(new BorderLayout(0, 8));
        panelTabla.setBackground(COLOR_FONDO);
        JLabel lblTituloTabla = new JLabel("Citas del Periodo");
        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloTabla.setForeground(COLOR_TEXTO);
        panelTabla.add(lblTituloTabla, BorderLayout.NORTH);
        panelTabla.add(scrollTabla, BorderLayout.CENTER);

        panelDerecho.add(panelTabla, BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.CENTER);
    }

    private JPanel crearTarjeta(Color colorAccento) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new javax.swing.BoxLayout(tarjeta, javax.swing.BoxLayout.Y_AXIS));
        tarjeta.setBackground(COLOR_BLANCO);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_GRIS_CLARO, 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        return tarjeta;
    }

    public void cargarPeriodos() {
        modeloListaPeriodos.clear();
        periodosCargados = historicoDAO.obtenerPeriodos();

        if (periodosCargados.isEmpty()) {
            modeloListaPeriodos.addElement("No hay periodos cerrados");
        } else {
            for (Map<String, Object> periodo : periodosCargados) {
                String nombre = (String) periodo.get("nombre");
                int totalCitas = (int) periodo.get("totalCitas");
                modeloListaPeriodos.addElement(nombre + " (" + totalCitas + " citas)");
            }
        }
        limpiarDetalle();
    }

    private void cargarDetallePeriodo() {
        int index = listaPeriodos.getSelectedIndex();
        if (index < 0 || periodosCargados == null || index >= periodosCargados.size()) return;

        Map<String, Object> periodo = periodosCargados.get(index);

        lblNombrePeriodo.setText((String) periodo.get("nombre"));

        Object fechaInicio = periodo.get("fechaInicio");
        Object fechaFin = periodo.get("fechaFin");
        if (fechaInicio != null && fechaFin != null) {
            lblFechas.setText("Del " + fechaInicio + " al " + fechaFin);
        } else {
            lblFechas.setText("Sin fechas registradas");
        }

        lblCerradoPor.setText("Cerrado por: " + periodo.get("cerradoPor"));
        lblTotalCitas.setText(String.valueOf(periodo.get("totalCitas")));
        lblCompletadas.setText(periodo.get("totalCompletadas") + " completadas");
        lblCanceladas.setText(String.valueOf(periodo.get("totalCanceladas")));
        lblIngresos.setText(String.format("%.2f EUR", periodo.get("ingresosTotales")));

        int idPeriodo = (int) periodo.get("id");
        List<Map<String, Object>> citas = historicoDAO.obtenerCitasPorPeriodo(idPeriodo);

        modeloTablaCitas.setRowCount(0);
        for (Map<String, Object> cita : citas) {
            modeloTablaCitas.addRow(new Object[]{
                    cita.get("fecha"),
                    cita.get("hora"),
                    cita.get("clienteNombre"),
                    cita.get("matricula") + " - " + cita.get("modeloCoche"),
                    cita.get("servicios") != null ? cita.get("servicios") : "-",
                    cita.get("estado"),
                    String.format("%.2f EUR", cita.get("precioFinal"))
            });
        }
    }

    // ==================== EXPORTAR PDF ====================

    private void exportarPDF() {
        int index = listaPeriodos.getSelectedIndex();
        if (index < 0 || periodosCargados == null || index >= periodosCargados.size()) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un periodo de la lista para exportar.",
                    "Sin seleccion", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Map<String, Object> periodo = periodosCargados.get(index);
        int idPeriodo = (int) periodo.get("id");
        List<Map<String, Object>> citas = historicoDAO.obtenerCitasPorPeriodo(idPeriodo);

        // Elegir donde guardar
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar informe PDF");
        String nombreArchivo = "Historico_" + ((String) periodo.get("nombre")).replace(" ", "_") + ".pdf";
        fileChooser.setSelectedFile(new File(nombreArchivo));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF", "pdf"));

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File archivo = fileChooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            archivo = new File(archivo.getAbsolutePath() + ".pdf");
        }

        try {
            generarPDF(archivo, periodo, citas);
            JOptionPane.showMessageDialog(this,
                    "PDF exportado correctamente:\n" + archivo.getAbsolutePath(),
                    "Exito", JOptionPane.INFORMATION_MESSAGE);

            // Abrir el PDF automáticamente
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(archivo);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el PDF:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void generarPDF(File archivo, Map<String, Object> periodo,
                            List<Map<String, Object>> citas) throws Exception {

        Document doc = new Document(PageSize.A4, 40, 40, 50, 40);
        PdfWriter.getInstance(doc, new FileOutputStream(archivo));
        doc.open();

        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdfFechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Fuentes
        BaseFont bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, false);
        BaseFont bfNormal = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, false);

        com.itextpdf.text.Font fTitulo = new com.itextpdf.text.Font(bfBold, 20, com.itextpdf.text.Font.NORMAL, new BaseColor(37, 99, 235));
        com.itextpdf.text.Font fSubtitulo = new com.itextpdf.text.Font(bfBold, 14, com.itextpdf.text.Font.NORMAL, new BaseColor(31, 41, 55));
        com.itextpdf.text.Font fNormal = new com.itextpdf.text.Font(bfNormal, 10);
        com.itextpdf.text.Font fNormalBold = new com.itextpdf.text.Font(bfBold, 10);
        com.itextpdf.text.Font fPequena = new com.itextpdf.text.Font(bfNormal, 8, com.itextpdf.text.Font.NORMAL, BaseColor.GRAY);
        com.itextpdf.text.Font fCabecera = new com.itextpdf.text.Font(bfBold, 9, com.itextpdf.text.Font.NORMAL, BaseColor.WHITE);
        com.itextpdf.text.Font fCelda = new com.itextpdf.text.Font(bfNormal, 8);
        com.itextpdf.text.Font fCeldaBold = new com.itextpdf.text.Font(bfBold, 8);

        // ===== TITULO =====
        Paragraph titulo = new Paragraph("AUTOFIX PRO", fTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);

        Paragraph subtitulo = new Paragraph("Informe Historico de Periodo", fSubtitulo);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(20);
        doc.add(subtitulo);

        // ===== DATOS DEL PERIODO =====
        PdfPTable tablaInfo = new PdfPTable(2);
        tablaInfo.setWidthPercentage(100);
        tablaInfo.setWidths(new float[]{1, 2});
        tablaInfo.setSpacingAfter(15);

        agregarFilaInfo(tablaInfo, "Periodo:", (String) periodo.get("nombre"), fNormalBold, fNormal);

        Object fechaInicio = periodo.get("fechaInicio");
        Object fechaFin = periodo.get("fechaFin");
        String rangoFechas = (fechaInicio != null && fechaFin != null)
                ? sdfFecha.format(fechaInicio) + " - " + sdfFecha.format(fechaFin)
                : "Sin fechas";
        agregarFilaInfo(tablaInfo, "Rango de fechas:", rangoFechas, fNormalBold, fNormal);

        String desc = (String) periodo.get("descripcion");
        if (desc != null && !desc.isEmpty()) {
            agregarFilaInfo(tablaInfo, "Descripcion:", desc, fNormalBold, fNormal);
        }

        agregarFilaInfo(tablaInfo, "Cerrado por:", (String) periodo.get("cerradoPor"), fNormalBold, fNormal);

        Object fechaCierre = periodo.get("fechaCierre");
        agregarFilaInfo(tablaInfo, "Fecha de cierre:",
                fechaCierre != null ? sdfFechaHora.format(fechaCierre) : "-", fNormalBold, fNormal);

        doc.add(tablaInfo);

        // ===== ESTADISTICAS =====
        PdfPTable tablaStats = new PdfPTable(4);
        tablaStats.setWidthPercentage(100);
        tablaStats.setSpacingAfter(20);

        agregarCeldaEstadistica(tablaStats, "Total Citas", String.valueOf(periodo.get("totalCitas")),
                new BaseColor(59, 130, 246), fPequena, fSubtitulo);
        agregarCeldaEstadistica(tablaStats, "Completadas", String.valueOf(periodo.get("totalCompletadas")),
                new BaseColor(34, 197, 94), fPequena, fSubtitulo);
        agregarCeldaEstadistica(tablaStats, "Canceladas", String.valueOf(periodo.get("totalCanceladas")),
                new BaseColor(239, 68, 68), fPequena, fSubtitulo);
        agregarCeldaEstadistica(tablaStats, "Ingresos", String.format("%.2f EUR", periodo.get("ingresosTotales")),
                new BaseColor(139, 92, 246), fPequena, fSubtitulo);

        doc.add(tablaStats);

        // ===== TABLA DE CITAS =====
        Paragraph tituloCitas = new Paragraph("Detalle de Citas Archivadas", fSubtitulo);
        tituloCitas.setSpacingAfter(10);
        doc.add(tituloCitas);

        if (citas.isEmpty()) {
            doc.add(new Paragraph("No hay citas registradas en este periodo.", fNormal));
        } else {
            PdfPTable tablaCitasPdf = new PdfPTable(7);
            tablaCitasPdf.setWidthPercentage(100);
            tablaCitasPdf.setWidths(new float[]{1.2f, 0.7f, 1.5f, 1.5f, 2f, 1f, 1f});

            // Cabeceras
            BaseColor colorCabecera = new BaseColor(37, 99, 235);
            String[] cabeceras = {"Fecha", "Hora", "Cliente", "Vehiculo", "Servicios", "Estado", "Precio"};
            for (String cab : cabeceras) {
                PdfPCell celda = new PdfPCell(new Phrase(cab, fCabecera));
                celda.setBackgroundColor(colorCabecera);
                celda.setPadding(6);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaCitasPdf.addCell(celda);
            }

            // Filas
            boolean alternar = false;
            for (Map<String, Object> cita : citas) {
                BaseColor bgColor = alternar ? new BaseColor(249, 250, 251) : BaseColor.WHITE;
                alternar = !alternar;

                String fecha = cita.get("fecha") != null ? sdfFecha.format(cita.get("fecha")) : "-";
                String hora = cita.get("hora") != null ? sdfHora.format(cita.get("hora")) : "-";
                String cliente = cita.get("clienteNombre") != null ? (String) cita.get("clienteNombre") : "-";
                String vehiculo = (cita.get("matricula") != null ? cita.get("matricula") : "") + " " +
                        (cita.get("modeloCoche") != null ? cita.get("modeloCoche") : "");
                String servicios = cita.get("servicios") != null ? (String) cita.get("servicios") : "-";
                String estado = cita.get("estado") != null ? (String) cita.get("estado") : "-";
                String precio = String.format("%.2f EUR", cita.get("precioFinal"));

                agregarCeldaCita(tablaCitasPdf, fecha, bgColor, fCelda, Element.ALIGN_CENTER);
                agregarCeldaCita(tablaCitasPdf, hora, bgColor, fCelda, Element.ALIGN_CENTER);
                agregarCeldaCita(tablaCitasPdf, cliente, bgColor, fCelda, Element.ALIGN_LEFT);
                agregarCeldaCita(tablaCitasPdf, vehiculo.trim(), bgColor, fCelda, Element.ALIGN_LEFT);
                agregarCeldaCita(tablaCitasPdf, servicios, bgColor, fCelda, Element.ALIGN_LEFT);

                // Estado con color
                com.itextpdf.text.Font fEstado;
                if ("completada".equals(estado)) {
                    fEstado = new com.itextpdf.text.Font(bfBold, 8, com.itextpdf.text.Font.NORMAL, new BaseColor(34, 197, 94));
                } else if ("cancelada".equals(estado)) {
                    fEstado = new com.itextpdf.text.Font(bfBold, 8, com.itextpdf.text.Font.NORMAL, new BaseColor(239, 68, 68));
                } else {
                    fEstado = fCeldaBold;
                }
                agregarCeldaCita(tablaCitasPdf, estado.toUpperCase(), bgColor, fEstado, Element.ALIGN_CENTER);
                agregarCeldaCita(tablaCitasPdf, precio, bgColor, fCeldaBold, Element.ALIGN_RIGHT);
            }

            doc.add(tablaCitasPdf);
        }

        // ===== PIE DE PAGINA =====
        doc.add(new Paragraph("\n"));
        Paragraph pie = new Paragraph(
                "Documento generado automaticamente por AutoFix Pro - " +
                        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()),
                fPequena);
        pie.setAlignment(Element.ALIGN_CENTER);
        doc.add(pie);

        doc.close();
    }

    // Métodos auxiliares para el PDF
    private void agregarFilaInfo(PdfPTable tabla, String etiqueta, String valor,
                                 com.itextpdf.text.Font fEtiqueta, com.itextpdf.text.Font fValor) {
        PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta, fEtiqueta));
        celdaEtiqueta.setBorder(Rectangle.NO_BORDER);
        celdaEtiqueta.setPaddingBottom(5);
        tabla.addCell(celdaEtiqueta);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor, fValor));
        celdaValor.setBorder(Rectangle.NO_BORDER);
        celdaValor.setPaddingBottom(5);
        tabla.addCell(celdaValor);
    }

    private void agregarCeldaEstadistica(PdfPTable tabla, String titulo, String valor,
                                         BaseColor color, com.itextpdf.text.Font fTit, com.itextpdf.text.Font fVal) {
        PdfPCell celda = new PdfPCell();
        celda.setBorderColor(new BaseColor(230, 230, 230));
        celda.setPadding(10);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph pTitulo = new Paragraph(titulo, fTit);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        celda.addElement(pTitulo);

        com.itextpdf.text.Font fValorColor = new com.itextpdf.text.Font(
                fVal.getBaseFont(), 16, com.itextpdf.text.Font.BOLD, color);
        Paragraph pValor = new Paragraph(valor, fValorColor);
        pValor.setAlignment(Element.ALIGN_CENTER);
        celda.addElement(pValor);

        tabla.addCell(celda);
    }

    private void agregarCeldaCita(PdfPTable tabla, String texto, BaseColor bgColor,
                                  com.itextpdf.text.Font fuente, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBackgroundColor(bgColor);
        celda.setPadding(5);
        celda.setHorizontalAlignment(alineacion);
        celda.setBorderColor(new BaseColor(230, 230, 230));
        tabla.addCell(celda);
    }

    // ==================== OTROS MÉTODOS ====================

    private void eliminarPeriodoSeleccionado() {
        int index = listaPeriodos.getSelectedIndex();
        if (index < 0 || periodosCargados == null || index >= periodosCargados.size()) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un periodo de la lista para eliminarlo.",
                    "Sin seleccion", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Map<String, Object> periodo = periodosCargados.get(index);
        String nombre = (String) periodo.get("nombre");
        int idPeriodo = (int) periodo.get("id");

        int opcion = JOptionPane.showConfirmDialog(this,
                "Eliminar permanentemente el periodo '" + nombre + "'?\n\n" +
                        "Todas las citas archivadas de este periodo se perderan.",
                "Confirmar Eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            if (historicoDAO.eliminarPeriodo(idPeriodo)) {
                JOptionPane.showMessageDialog(this,
                        "Periodo eliminado correctamente.",
                        "Exito", JOptionPane.INFORMATION_MESSAGE);
                cargarPeriodos();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar el periodo.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarDetalle() {
        lblNombrePeriodo.setText("Selecciona un periodo");
        lblFechas.setText("-");
        lblCerradoPor.setText("");
        lblTotalCitas.setText("0");
        lblCompletadas.setText("0 completadas");
        lblCanceladas.setText("0");
        lblIngresos.setText("0.00 EUR");
        modeloTablaCitas.setRowCount(0);
    }

    private class PeriodoListRenderer extends DefaultListCellRenderer {
        @Override
        public java.awt.Component getListCellRendererComponent(JList<?> list, Object value,
                                                               int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            if (isSelected) {
                label.setBackground(new Color(219, 234, 254));
                label.setForeground(COLOR_PRIMARIO);
            }
            label.setText(value.toString());
            return label;
        }
    }
}