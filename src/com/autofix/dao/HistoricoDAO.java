package com.autofix.dao;

import com.autofix.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para gestionar el histórico de períodos.
 * Permite archivar citas operativas (completadas y canceladas)
 * antes de limpiarlas, conservando clientes, servicios y usuarios.
 */
public class HistoricoDAO {

    /**
     * Ejecuta el cierre de período:
     * 1. Crea el registro del período con estadísticas
     * 2. Copia SOLO las citas completadas y canceladas al histórico
     * 3. Elimina esas citas de la tabla operativa
     *
     * NO elimina: clientes, servicios ni usuarios (son datos maestros)
     *
     * @param nombre Nombre del período (ej: "Enero 2025")
     * @param descripcion Descripción opcional
     * @param cerradoPor Nombre del admin que ejecuta el cierre
     * @return true si todo salió bien
     */
    public boolean cerrarPeriodo(String nombre, String descripcion, String cerradoPor) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Calcula estadísticas SOLO de citas completadas y canceladas
            int totalCitas = contarTotal(conn,
                    "SELECT COUNT(*) FROM citas WHERE estado IN ('completada', 'cancelada')");
            int totalCompletadas = contarTotal(conn,
                    "SELECT COUNT(*) FROM citas WHERE estado = 'completada'");
            int totalCanceladas = contarTotal(conn,
                    "SELECT COUNT(*) FROM citas WHERE estado = 'cancelada'");
            int totalClientes = contarTotal(conn,
                    "SELECT COUNT(DISTINCT id_cliente) FROM citas WHERE estado IN ('completada', 'cancelada')");
            double ingresos = calcularIngresos(conn);
            Date fechaInicio = obtenerFecha(conn,
                    "SELECT MIN(fecha) FROM citas WHERE estado IN ('completada', 'cancelada')");
            Date fechaFin = obtenerFecha(conn,
                    "SELECT MAX(fecha) FROM citas WHERE estado IN ('completada', 'cancelada')");

            if (totalCitas == 0) {
                conn.rollback();
                return false;
            }

            // Crea registro del período
            String sqlPeriodo = "INSERT INTO historico_periodos " +
                    "(nombre, descripcion, total_citas, total_completadas, total_canceladas, " +
                    "total_clientes, ingresos_totales, fecha_inicio, fecha_fin, cerrado_por) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            int idPeriodo;
            try (PreparedStatement stmt = conn.prepareStatement(sqlPeriodo, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, nombre);
                stmt.setString(2, descripcion);
                stmt.setInt(3, totalCitas);
                stmt.setInt(4, totalCompletadas);
                stmt.setInt(5, totalCanceladas);
                stmt.setInt(6, totalClientes);
                stmt.setDouble(7, ingresos);
                stmt.setDate(8, fechaInicio);
                stmt.setDate(9, fechaFin);
                stmt.setString(10, cerradoPor);
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idPeriodo = rs.getInt(1);
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // Copia citas completadas y canceladas al histórico
            String sqlCopiar = "INSERT INTO historico_citas " +
                    "(id_periodo, cliente_nombre, empleado_nombre, servicios, matricula, " +
                    "modelo_coche, fecha, hora, estado, precio_final, notas, " +
                    "motivo_cancelacion, cancelado_por, fecha_cancelacion, fecha_creacion) " +
                    "SELECT ?, cl.nombre, u.nombre, " +
                    "(SELECT GROUP_CONCAT(s.nombre SEPARATOR ', ') FROM detalle_citas dc " +
                    "INNER JOIN servicios s ON dc.id_servicio = s.id WHERE dc.id_cita = c.id), " +
                    "c.matricula, c.modelo_coche, c.fecha, c.hora, c.estado, c.precio_final, " +
                    "c.notas, c.motivo_cancelacion, c.cancelado_por, c.fecha_cancelacion, " +
                    "c.fecha_creacion " +
                    "FROM citas c " +
                    "INNER JOIN clientes cl ON c.id_cliente = cl.id " +
                    "INNER JOIN usuarios u ON c.id_usuario = u.id " +
                    "WHERE c.estado IN ('completada', 'cancelada')";

            try (PreparedStatement stmt = conn.prepareStatement(sqlCopiar)) {
                stmt.setInt(1, idPeriodo);
                stmt.executeUpdate();
            }

            //Elimina SOLO las citas archivadas (completadas y canceladas)

            try (Statement stmt = conn.createStatement()) {
                // Borra detalles de citas completadas/canceladas
                stmt.executeUpdate(
                        "DELETE FROM detalle_citas WHERE id_cita IN " +
                                "(SELECT id FROM citas WHERE estado IN ('completada', 'cancelada'))");

                // Borra las citas completadas/canceladas
                stmt.executeUpdate(
                        "DELETE FROM citas WHERE estado IN ('completada', 'cancelada')");
            }

            // NO se borran: clientes, servicios, usuarios
            // Son datos fundamentales del negocio que se acumulan

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al cerrar periodo: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }


      //Obtiene la lista de todos los períodos históricos

    public List<Map<String, Object>> obtenerPeriodos() {
        List<Map<String, Object>> periodos = new ArrayList<>();
        String sql = "SELECT * FROM historico_periodos ORDER BY fecha_cierre DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> periodo = new HashMap<>();
                periodo.put("id", rs.getInt("id"));
                periodo.put("nombre", rs.getString("nombre"));
                periodo.put("descripcion", rs.getString("descripcion"));
                periodo.put("totalCitas", rs.getInt("total_citas"));
                periodo.put("totalCompletadas", rs.getInt("total_completadas"));
                periodo.put("totalCanceladas", rs.getInt("total_canceladas"));
                periodo.put("totalClientes", rs.getInt("total_clientes"));
                periodo.put("ingresosTotales", rs.getDouble("ingresos_totales"));
                periodo.put("fechaInicio", rs.getDate("fecha_inicio"));
                periodo.put("fechaFin", rs.getDate("fecha_fin"));
                periodo.put("cerradoPor", rs.getString("cerrado_por"));
                periodo.put("fechaCierre", rs.getTimestamp("fecha_cierre"));
                periodos.add(periodo);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener periodos: " + e.getMessage());
        }
        return periodos;
    }


      //Obtiene las citas archivadas de un período específico

    public List<Map<String, Object>> obtenerCitasPorPeriodo(int idPeriodo) {
        List<Map<String, Object>> citas = new ArrayList<>();
        String sql = "SELECT * FROM historico_citas WHERE id_periodo = ? ORDER BY fecha DESC, hora DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPeriodo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> cita = new HashMap<>();
                cita.put("id", rs.getInt("id"));
                cita.put("clienteNombre", rs.getString("cliente_nombre"));
                cita.put("empleadoNombre", rs.getString("empleado_nombre"));
                cita.put("servicios", rs.getString("servicios"));
                cita.put("matricula", rs.getString("matricula"));
                cita.put("modeloCoche", rs.getString("modelo_coche"));
                cita.put("fecha", rs.getDate("fecha"));
                cita.put("hora", rs.getTime("hora"));
                cita.put("estado", rs.getString("estado"));
                cita.put("precioFinal", rs.getDouble("precio_final"));
                cita.put("notas", rs.getString("notas"));
                cita.put("motivoCancelacion", rs.getString("motivo_cancelacion"));
                cita.put("canceladoPor", rs.getString("cancelado_por"));
                cita.put("fechaCancelacion", rs.getTimestamp("fecha_cancelacion"));
                cita.put("fechaCreacion", rs.getTimestamp("fecha_creacion"));
                citas.add(cita);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener citas del periodo: " + e.getMessage());
        }
        return citas;
    }


      //Elimina un período histórico y todas sus citas asociadas

    public boolean eliminarPeriodo(int idPeriodo) {
        String sql = "DELETE FROM historico_periodos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPeriodo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar periodo: " + e.getMessage());
        }
        return false;
    }

    // Métodos auxiliares

    private int contarTotal(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private double calcularIngresos(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(SUM(precio_final), 0) FROM citas WHERE estado = 'completada'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    private Date obtenerFecha(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDate(1);
        }
        return null;
    }
}