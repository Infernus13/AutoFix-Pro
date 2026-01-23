package com.autofix.dao;

import com.autofix.modelo.Cita;
import com.autofix.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            return false;
        }
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
        return cita;
    }

    public List<Cita> obtenerPorEstado(String estado) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.*, cl.nombre as nombre_cliente, u.nombre as nombre_usuario, " +
                "GROUP_CONCAT(s.nombre SEPARATOR ', ') as nombre_servicio " +
                "FROM citas c " +
                "JOIN clientes cl ON c.id_cliente = cl.id " +
                "JOIN usuarios u ON c.id_usuario = u.id " +
                "LEFT JOIN detalle_citas dc ON c.id = dc.id_cita " +
                "LEFT JOIN servicios s ON dc.id_servicio = s.id " +
                "WHERE c.estado = ? " +
                "GROUP BY c.id " +
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
}