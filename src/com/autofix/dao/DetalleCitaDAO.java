package com.autofix.dao;

import com.autofix.modelo.DetalleCita;
import com.autofix.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleCitaDAO {

    public List<DetalleCita> obtenerPorCita(int idCita) {
        List<DetalleCita> detalles = new ArrayList<>();
        String sql = "SELECT d.*, s.nombre as nombre_servicio " +
                "FROM detalle_citas d " +
                "INNER JOIN servicios s ON d.id_servicio = s.id " +
                "WHERE d.id_cita = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCita);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DetalleCita detalle = new DetalleCita();
                detalle.setId(rs.getInt("id"));
                detalle.setIdCita(rs.getInt("id_cita"));
                detalle.setIdServicio(rs.getInt("id_servicio"));
                detalle.setPrecio(rs.getDouble("precio"));
                detalle.setCantidad(rs.getInt("cantidad"));
                detalle.setNombreServicio(rs.getString("nombre_servicio"));
                detalles.add(detalle);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener detalles: " + e.getMessage());
        }
        return detalles;
    }

    public boolean insertar(DetalleCita detalle) {
        String sql = "INSERT INTO detalle_citas (id_cita, id_servicio, precio, cantidad) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detalle.getIdCita());
            stmt.setInt(2, detalle.getIdServicio());
            stmt.setDouble(3, detalle.getPrecio());
            stmt.setInt(4, detalle.getCantidad());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar detalle: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarPorCita(int idCita) {
        String sql = "DELETE FROM detalle_citas WHERE id_cita = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCita);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar detalles: " + e.getMessage());
            return false;
        }
    }

    public double calcularTotalCita(int idCita) {
        String sql = "SELECT SUM(precio * cantidad) as total FROM detalle_citas WHERE id_cita = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCita);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.out.println("Error al calcular total: " + e.getMessage());
        }
        return 0;
    }
}