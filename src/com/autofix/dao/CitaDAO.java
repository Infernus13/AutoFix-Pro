package com.autofix.dao;

import com.autofix.modelo.Cita;
import com.autofix.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitaDAO {

    public List<Cita> obtenerTodas() {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.*, cl.nombre as nombre_cliente, u.nombre as nombre_usuario, " +
                "(SELECT GROUP_CONCAT(s.nombre SEPARATOR ', ') FROM detalle_citas dc " +
                "INNER JOIN servicios s ON dc.id_servicio = s.id WHERE dc.id_cita = c.id) as nombre_servicio " +
                "FROM citas c " +
                "INNER JOIN clientes cl ON c.id_cliente = cl.id " +
                "INNER JOIN usuarios u ON c.id_usuario = u.id " +
                "ORDER BY c.fecha DESC, c.hora DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cita cita = extraerCita(rs);
                citas.add(cita);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener citas: " + e.getMessage());
        }
        return citas;
    }

    public List<Cita> obtenerPorUsuario(int idUsuario) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.*, cl.nombre as nombre_cliente, u.nombre as nombre_usuario, " +
                "(SELECT GROUP_CONCAT(s.nombre SEPARATOR ', ') FROM detalle_citas dc " +
                "INNER JOIN servicios s ON dc.id_servicio = s.id WHERE dc.id_cita = c.id) as nombre_servicio " +
                "FROM citas c " +
                "INNER JOIN clientes cl ON c.id_cliente = cl.id " +
                "INNER JOIN usuarios u ON c.id_usuario = u.id " +
                "WHERE c.id_usuario = ? " +
                "ORDER BY c.fecha DESC, c.hora DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cita cita = extraerCita(rs);
                citas.add(cita);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener citas por usuario: " + e.getMessage());
        }
        return citas;
    }

    public Cita obtenerPorId(int id) {
        String sql = "SELECT c.*, cl.nombre as nombre_cliente, u.nombre as nombre_usuario, " +
                "(SELECT GROUP_CONCAT(s.nombre SEPARATOR ', ') FROM detalle_citas dc " +
                "INNER JOIN servicios s ON dc.id_servicio = s.id WHERE dc.id_cita = c.id) as nombre_servicio " +
                "FROM citas c " +
                "INNER JOIN clientes cl ON c.id_cliente = cl.id " +
                "INNER JOIN usuarios u ON c.id_usuario = u.id " +
                "WHERE c.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extraerCita(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener cita: " + e.getMessage());
        }
        return null;
    }

    public int insertar(Cita cita) {
        String sql = "INSERT INTO citas (id_cliente, id_usuario, matricula, modelo_coche, fecha, hora, estado, precio_final, notas) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'pendiente', ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, cita.getIdCliente());
            stmt.setInt(2, cita.getIdUsuario());
            stmt.setString(3, cita.getMatricula());
            stmt.setString(4, cita.getModeloCoche());
            stmt.setDate(5, cita.getFecha());
            stmt.setTime(6, cita.getHora());
            stmt.setDouble(7, cita.getPrecioFinal());
            stmt.setString(8, cita.getNotas());

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar cita: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(Cita cita) {
        String sql = "UPDATE citas SET id_cliente = ?, id_usuario = ?, matricula = ?, modelo_coche = ?, " +
                "fecha = ?, hora = ?, estado = ?, precio_final = ?, notas = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cita.getIdCliente());
            stmt.setInt(2, cita.getIdUsuario());
            stmt.setString(3, cita.getMatricula());
            stmt.setString(4, cita.getModeloCoche());
            stmt.setDate(5, cita.getFecha());
            stmt.setTime(6, cita.getHora());
            stmt.setString(7, cita.getEstado());
            stmt.setDouble(8, cita.getPrecioFinal());
            stmt.setString(9, cita.getNotas());
            stmt.setInt(10, cita.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar cita: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(int id, String estado) {
        String sql = "UPDATE citas SET estado = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, estado);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarPrecioTotal(int idCita, double precioTotal) {
        String sql = "UPDATE citas SET precio_final = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, precioTotal);
            stmt.setInt(2, idCita);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar precio: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM citas WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar cita: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cancela una cita registrando el motivo, quién la canceló y cuándo
     * @param id ID de la cita
     * @param motivo Motivo de la cancelación
     * @param canceladoPor Nombre de quien cancela
     * @return true si se canceló correctamente
     */
    public boolean cancelarCita(int id, String motivo, String canceladoPor) {
        String sql = "UPDATE citas SET estado = 'cancelada', " +
                "motivo_cancelacion = ?, cancelado_por = ?, fecha_cancelacion = NOW() " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, motivo);
            stmt.setString(2, canceladoPor);
            stmt.setInt(3, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al cancelar cita: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene todas las citas canceladas con información de cancelación
     * @return Lista de citas canceladas
     */
    public List<Cita> obtenerCitasCanceladas() {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.*, cl.nombre as nombre_cliente, u.nombre as nombre_usuario, " +
                "(SELECT GROUP_CONCAT(s.nombre SEPARATOR ', ') FROM detalle_citas dc " +
                "INNER JOIN servicios s ON dc.id_servicio = s.id WHERE dc.id_cita = c.id) as nombre_servicio " +
                "FROM citas c " +
                "INNER JOIN clientes cl ON c.id_cliente = cl.id " +
                "INNER JOIN usuarios u ON c.id_usuario = u.id " +
                "WHERE c.estado = 'cancelada' " +
                "ORDER BY c.fecha_cancelacion DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cita cita = extraerCita(rs);
                citas.add(cita);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener citas canceladas: " + e.getMessage());
        }
        return citas;
    }

    public int contarCitasHoy() {
        String sql = "SELECT COUNT(*) FROM citas WHERE fecha = CURDATE()";
        return contarConSQL(sql);
    }

    public int contarPendientes() {
        String sql = "SELECT COUNT(*) FROM citas WHERE estado = 'pendiente'";
        return contarConSQL(sql);
    }

    public int contarCompletadas() {
        String sql = "SELECT COUNT(*) FROM citas WHERE estado = 'completada'";
        return contarConSQL(sql);
    }

    public int contarCanceladas() {
        String sql = "SELECT COUNT(*) FROM citas WHERE estado = 'cancelada'";
        return contarConSQL(sql);
    }

    public double calcularIngresos() {
        String sql = "SELECT COALESCE(SUM(precio_final), 0) FROM citas WHERE estado = 'completada'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al calcular ingresos: " + e.getMessage());
        }
        return 0;
    }

    private int contarConSQL(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al contar: " + e.getMessage());
        }
        return 0;
    }

    private Cita extraerCita(ResultSet rs) throws SQLException {
        Cita cita = new Cita();
        cita.setId(rs.getInt("id"));
        cita.setIdCliente(rs.getInt("id_cliente"));
        cita.setIdUsuario(rs.getInt("id_usuario"));
        cita.setMatricula(rs.getString("matricula"));
        cita.setModeloCoche(rs.getString("modelo_coche"));
        cita.setFecha(rs.getDate("fecha"));
        cita.setHora(rs.getTime("hora"));
        cita.setEstado(rs.getString("estado"));
        cita.setPrecioFinal(rs.getDouble("precio_final"));
        cita.setNotas(rs.getString("notas"));
        cita.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        cita.setNombreCliente(rs.getString("nombre_cliente"));
        cita.setNombreUsuario(rs.getString("nombre_usuario"));
        cita.setNombreServicio(rs.getString("nombre_servicio"));
        cita.setArchivada(rs.getBoolean("archivada"));

        // Campos de cancelación (pueden ser null)
        try {
            cita.setMotivoCancelacion(rs.getString("motivo_cancelacion"));
            cita.setCanceladoPor(rs.getString("cancelado_por"));
            cita.setFechaCancelacion(rs.getTimestamp("fecha_cancelacion"));
        } catch (SQLException e) {
            // Los campos pueden no existir en consultas anteriores
        }

        return cita;
    }


      //Obtiene todas las citas con un estado específico

    public List<Cita> obtenerPorEstado(String estado) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.*, cl.nombre as nombre_cliente, u.nombre as nombre_usuario, " +
                "(SELECT GROUP_CONCAT(s.nombre SEPARATOR ', ') FROM detalle_citas dc " +
                "INNER JOIN servicios s ON dc.id_servicio = s.id WHERE dc.id_cita = c.id) as nombre_servicio " +
                "FROM citas c " +
                "INNER JOIN clientes cl ON c.id_cliente = cl.id " +
                "INNER JOIN usuarios u ON c.id_usuario = u.id " +
                "WHERE c.estado = ? " +
                "ORDER BY c.fecha DESC, c.hora DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, estado);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                citas.add(extraerCita(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener citas por estado: " + e.getMessage());
        }
        return citas;
    }

    public boolean cambiarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE citas SET estado = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al cambiar estado: " + e.getMessage());
        }
        return false;
    }

    public boolean archivar(int id) {
        String sql = "UPDATE citas SET archivada = TRUE WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al archivar cita: " + e.getMessage());
        }
        return false;
    }

    public List<Cita> obtenerCompletadasPorFecha(java.sql.Date fecha) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.*, cl.nombre as nombre_cliente, u.nombre as nombre_usuario, " +
                "(SELECT GROUP_CONCAT(s.nombre SEPARATOR ', ') FROM detalle_citas dc " +
                "INNER JOIN servicios s ON dc.id_servicio = s.id WHERE dc.id_cita = c.id) as nombre_servicio " +
                "FROM citas c " +
                "INNER JOIN clientes cl ON c.id_cliente = cl.id " +
                "INNER JOIN usuarios u ON c.id_usuario = u.id " +
                "WHERE c.estado = 'completada' AND c.fecha = ? " +
                "ORDER BY c.hora ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, fecha);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                citas.add(extraerCita(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener citas completadas por fecha: " + e.getMessage());
        }
        return citas;
    }

    public List<java.sql.Date> obtenerFechasConCitasCompletadas() {
        List<java.sql.Date> fechas = new ArrayList<>();
        String sql = "SELECT DISTINCT fecha FROM citas " +
                "WHERE estado = 'completada' " +
                "ORDER BY fecha DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fechas.add(rs.getDate("fecha"));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener fechas: " + e.getMessage());
        }
        return fechas;
    }

    public int contarEnProceso() {
        String sql = "SELECT COUNT(*) FROM citas WHERE estado = 'en_proceso' AND archivada = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al contar citas en proceso: " + e.getMessage());
        }
        return 0;
    }

    public double calcularIngresosPorFecha(java.sql.Date fecha) {
        String sql = "SELECT COALESCE(SUM(precio_final), 0) FROM citas " +
                "WHERE estado = 'completada' AND fecha = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, fecha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al calcular ingresos por fecha: " + e.getMessage());
        }
        return 0;
    }

    public Map<String, Object> obtenerEstadisticasDia(java.sql.Date fecha) {
        Map<String, Object> estadisticas = new HashMap<>();

        String sql = "SELECT COUNT(*) as total, COALESCE(SUM(precio_final), 0) as ingresos " +
                "FROM citas WHERE estado = 'completada' AND fecha = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, fecha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                estadisticas.put("totalCitas", rs.getInt("total"));
                estadisticas.put("ingresos", rs.getDouble("ingresos"));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener estadísticas del día: " + e.getMessage());
        }
        return estadisticas;
    }
}