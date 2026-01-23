package com.autofix.util;

import com.autofix.dao.DetalleCitaDAO;
import com.autofix.modelo.Cita;
import com.autofix.modelo.Cliente;
import com.autofix.modelo.DetalleCita;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GeneradorFactura {

    private static final Font FONT_TITULO = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, new BaseColor(31, 41, 55));
    private static final Font FONT_SUBTITULO = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(37, 99, 235));
    private static final Font FONT_NORMAL = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, new BaseColor(55, 65, 81));
    private static final Font FONT_NEGRITA = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, new BaseColor(31, 41, 55));
    private static final Font FONT_GRANDE = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(31, 41, 55));
    private static final Font FONT_TOTAL = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, new BaseColor(34, 197, 94));

    public static String generarFactura(Cita cita, Cliente cliente, String nombreEmpleado) { {

        // Obtener detalles de la cita
        DetalleCitaDAO detalleCitaDAO = new DetalleCitaDAO();
        List<DetalleCita> detalles = detalleCitaDAO.obtenerPorCita(cita.getId());

        // Crear carpeta de facturas si no existe
        String carpetaFacturas = System.getProperty("user.home") + "/AutoFixPro/Facturas";
        File directorio = new File(carpetaFacturas);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        // Nombre del archivo
        String numeroFactura = generarNumeroFactura(cita.getId());
        String nombreArchivo = carpetaFacturas + "/Factura_" + numeroFactura + ".pdf";

        try {
            Document documento = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();

            // === CABECERA ===
            PdfPTable tablaCabecera = new PdfPTable(2);
            tablaCabecera.setWidthPercentage(100);
            tablaCabecera.setWidths(new float[]{60, 40});

            // Logo/Nombre empresa
            PdfPCell celdaEmpresa = new PdfPCell();
            celdaEmpresa.setBorder(Rectangle.NO_BORDER);
            celdaEmpresa.setPaddingBottom(20);

            Paragraph nombreEmpresa = new Paragraph("AutoFix Pro", FONT_TITULO);
            Paragraph direccionEmpresa = new Paragraph("Taller Mecanico Profesional\nCalle Principal 123\n14005 Córdoba\nTel: 912 912 912\nCIF: B12345678", FONT_NORMAL);
            direccionEmpresa.setSpacingBefore(10);

            celdaEmpresa.addElement(nombreEmpresa);
            celdaEmpresa.addElement(direccionEmpresa);

            // Info factura
            PdfPCell celdaFactura = new PdfPCell();
            celdaFactura.setBorder(Rectangle.NO_BORDER);
            celdaFactura.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaFactura.setPaddingBottom(20);

            Paragraph tituloFactura = new Paragraph("FACTURA", FONT_SUBTITULO);
            tituloFactura.setAlignment(Element.ALIGN_RIGHT);

            Paragraph numFactura = new Paragraph("N°: " + numeroFactura, FONT_GRANDE);
            numFactura.setAlignment(Element.ALIGN_RIGHT);
            numFactura.setSpacingBefore(10);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Paragraph fechaFactura = new Paragraph("Fecha: " + sdf.format(new Date()), FONT_NORMAL);
            fechaFactura.setAlignment(Element.ALIGN_RIGHT);

            Paragraph fechaCita = new Paragraph("Fecha servicio: " + cita.getFecha().toString(), FONT_NORMAL);
            fechaCita.setAlignment(Element.ALIGN_RIGHT);

            celdaFactura.addElement(tituloFactura);
            celdaFactura.addElement(numFactura);
            celdaFactura.addElement(fechaFactura);
            celdaFactura.addElement(fechaCita);

            tablaCabecera.addCell(celdaEmpresa);
            tablaCabecera.addCell(celdaFactura);
            documento.add(tablaCabecera);

            // Linea separadora
            documento.add(new Paragraph(" "));
            PdfPTable lineaSeparadora = new PdfPTable(1);
            lineaSeparadora.setWidthPercentage(100);
            PdfPCell celdaLinea = new PdfPCell();
            celdaLinea.setBorder(Rectangle.BOTTOM);
            celdaLinea.setBorderColor(new BaseColor(229, 231, 235));
            celdaLinea.setFixedHeight(1);
            lineaSeparadora.addCell(celdaLinea);
            documento.add(lineaSeparadora);
            documento.add(new Paragraph(" "));

            // === DATOS DEL CLIENTE ===
            Paragraph tituloCliente = new Paragraph("DATOS DEL CLIENTE", FONT_SUBTITULO);
            tituloCliente.setSpacingBefore(10);
            documento.add(tituloCliente);

            PdfPTable tablaCliente = new PdfPTable(2);
            tablaCliente.setWidthPercentage(100);
            tablaCliente.setSpacingBefore(10);
            tablaCliente.setWidths(new float[]{30, 70});

            agregarFilaDato(tablaCliente, "Nombre:", cliente.getNombre());
            agregarFilaDato(tablaCliente, "Telefono:", cliente.getTelefono());
            agregarFilaDato(tablaCliente, "Email:", cliente.getEmail() != null ? cliente.getEmail() : "-");
            agregarFilaDato(tablaCliente, "Direccion:", cliente.getDireccion() != null ? cliente.getDireccion() : "-");

            documento.add(tablaCliente);
            documento.add(new Paragraph(" "));

            // === DATOS DEL VEHICULO ===
            Paragraph tituloVehiculo = new Paragraph("DATOS DEL VEHICULO", FONT_SUBTITULO);
            tituloVehiculo.setSpacingBefore(10);
            documento.add(tituloVehiculo);

            PdfPTable tablaVehiculo = new PdfPTable(2);
            tablaVehiculo.setWidthPercentage(100);
            tablaVehiculo.setSpacingBefore(10);
            tablaVehiculo.setWidths(new float[]{30, 70});

            agregarFilaDato(tablaVehiculo, "Matricula:", cita.getMatricula() != null ? cita.getMatricula() : "-");
            agregarFilaDato(tablaVehiculo, "Modelo:", cita.getModeloCoche() != null ? cita.getModeloCoche() : "-");

            documento.add(tablaVehiculo);
            documento.add(new Paragraph(" "));

            // === ATENDIDO POR ===

            // === ATENDIDO POR ===
            Paragraph tituloEmpleado = new Paragraph("ATENDIDO POR", FONT_SUBTITULO);
            tituloEmpleado.setSpacingBefore(10);
            documento.add(tituloEmpleado);

            Paragraph empleado = new Paragraph(nombreEmpleado, FONT_NORMAL);
            empleado.setSpacingBefore(5);
            documento.add(empleado);
            documento.add(new Paragraph(" "));

            // === DETALLE DE SERVICIOS ===
            Paragraph tituloDetalle = new Paragraph("DETALLE DE SERVICIOS", FONT_SUBTITULO);
            tituloDetalle.setSpacingBefore(20);
            documento.add(tituloDetalle);

            PdfPTable tablaDetalle = new PdfPTable(4);
            tablaDetalle.setWidthPercentage(100);
            tablaDetalle.setSpacingBefore(10);
            tablaDetalle.setWidths(new float[]{45, 15, 20, 20});

            // Cabecera de la tabla
            agregarCeldaCabecera(tablaDetalle, "Descripcion");
            agregarCeldaCabecera(tablaDetalle, "Cantidad");
            agregarCeldaCabecera(tablaDetalle, "Precio Unit.");
            agregarCeldaCabecera(tablaDetalle, "Subtotal");

            // Filas de servicios
            double subtotalGeneral = 0;
            for (DetalleCita detalle : detalles) {
                agregarCeldaContenido(tablaDetalle, detalle.getNombreServicio(), Element.ALIGN_LEFT);
                agregarCeldaContenido(tablaDetalle, String.valueOf(detalle.getCantidad()), Element.ALIGN_CENTER);
                agregarCeldaContenido(tablaDetalle, String.format("%.2f €", detalle.getPrecio()), Element.ALIGN_RIGHT);
                agregarCeldaContenido(tablaDetalle, String.format("%.2f €", detalle.getSubtotal()), Element.ALIGN_RIGHT);
                subtotalGeneral += detalle.getSubtotal();
            }

            documento.add(tablaDetalle);

            // === TOTALES ===
            PdfPTable tablaTotales = new PdfPTable(2);
            tablaTotales.setWidthPercentage(40);
            tablaTotales.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaTotales.setSpacingBefore(20);
            tablaTotales.setWidths(new float[]{50, 50});

            double baseImponible = subtotalGeneral / 1.21;
            double iva = subtotalGeneral - baseImponible;

            agregarFilaTotal(tablaTotales, "Base Imponible:", String.format("%.2f €", baseImponible), FONT_NORMAL);
            agregarFilaTotal(tablaTotales, "IVA (21%):", String.format("%.2f €", iva), FONT_NORMAL);
            agregarFilaTotal(tablaTotales, "TOTAL:", String.format("%.2f €", subtotalGeneral), FONT_TOTAL);

            documento.add(tablaTotales);

            // === NOTAS ===
            if (cita.getNotas() != null && !cita.getNotas().isEmpty()) {
                Paragraph tituloNotas = new Paragraph("OBSERVACIONES", FONT_SUBTITULO);
                tituloNotas.setSpacingBefore(30);
                documento.add(tituloNotas);

                Paragraph notas = new Paragraph(cita.getNotas(), FONT_NORMAL);
                notas.setSpacingBefore(10);
                documento.add(notas);
            }

            // === PIE DE PAGINA ===
            Paragraph pie = new Paragraph("Gracias por confiar en AutoFix Pro", FONT_NORMAL);
            pie.setAlignment(Element.ALIGN_CENTER);
            pie.setSpacingBefore(50);
            documento.add(pie);

            documento.close();

            return nombreArchivo;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }}
    }
    private static String generarNumeroFactura(int idCita) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        return sdf.format(new Date()) + "-" + String.format("%04d", idCita);
    }

    private static void agregarFilaDato(PdfPTable tabla, String etiqueta, String valor) {
        PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta, FONT_NEGRITA));
        celdaEtiqueta.setBorder(Rectangle.NO_BORDER);
        celdaEtiqueta.setPadding(5);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor, FONT_NORMAL));
        celdaValor.setBorder(Rectangle.NO_BORDER);
        celdaValor.setPadding(5);

        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
    }

    private static void agregarCeldaCabecera(PdfPTable tabla, String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FONT_NEGRITA));
        celda.setBackgroundColor(new BaseColor(249, 250, 251));
        celda.setPadding(10);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setBorderColor(new BaseColor(229, 231, 235));
        tabla.addCell(celda);
    }

    private static void agregarCeldaContenido(PdfPTable tabla, String texto, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FONT_NORMAL));
        celda.setPadding(10);
        celda.setHorizontalAlignment(alineacion);
        celda.setBorderColor(new BaseColor(229, 231, 235));
        tabla.addCell(celda);
    }

    private static void agregarFilaTotal(PdfPTable tabla, String etiqueta, String valor, Font font) {
        PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta, FONT_NEGRITA));
        celdaEtiqueta.setBorder(Rectangle.NO_BORDER);
        celdaEtiqueta.setPadding(5);
        celdaEtiqueta.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor, font));
        celdaValor.setBorder(Rectangle.NO_BORDER);
        celdaValor.setPadding(5);
        celdaValor.setHorizontalAlignment(Element.ALIGN_RIGHT);

        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
    }
}