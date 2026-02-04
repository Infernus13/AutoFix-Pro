package com.autofix.dao;

import com.autofix.modelo.Servicio;
import com.autofix.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {

    // Obtiene todos los servicios
    public List<Servicio> obtenerTodos() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicios ORDER BY nombre";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                servicios.add(new Servicio(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getInt("duracion_min"),
                        rs.getBoolean("activo")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener servicios: " + e.getMessage());
        }
        return servicios;
    }

    // Obtiene solo servicios activos
    public List<Servicio> obtenerActivos() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicios WHERE activo = true ORDER BY nombre";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                servicios.add(new Servicio(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getInt("duracion_min"),
                        rs.getBoolean("activo")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener servicios activos: " + e.getMessage());
        }
        return servicios;
    }

    // Obtiene servicio por ID
    public Servicio obtenerPorId(int id) {
        String sql = "SELECT * FROM servicios WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Servicio servicio = new Servicio();
                servicio.setId(rs.getInt("id"));
                servicio.setNombre(rs.getString("nombre"));
                servicio.setDescripcion(rs.getString("descripcion"));
                servicio.setPrecio(rs.getDouble("precio"));
                servicio.setDuracionMin(rs.getInt("duracion_min"));
                servicio.setActivo(rs.getBoolean("activo"));
                return servicio;
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener servicio: " + e.getMessage());
        }
        return null;
    }

    // Cuenta el total de servicios
    public int contarTotal() {
        String sql = "SELECT COUNT(*) FROM servicios WHERE activo = true";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al contar servicios: " + e.getMessage());
        }
        return 0;
    }

    // Inserta un nuevo servicio
    public boolean insertar(Servicio servicio) {
        String sql = "INSERT INTO servicios (nombre, descripcion, precio, duracion_min, activo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, servicio.getNombre());
            stmt.setString(2, servicio.getDescripcion());
            stmt.setDouble(3, servicio.getPrecio());
            stmt.setInt(4, servicio.getDuracionMin());
            stmt.setBoolean(5, servicio.isActivo());

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    servicio.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar servicio: " + e.getMessage());
        }
        return false;
    }

    // Actualiza el servicio
    public boolean actualizar(Servicio servicio) {
        String sql = "UPDATE servicios SET nombre = ?, descripcion = ?, precio = ?, duracion_min = ?, activo = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, servicio.getNombre());
            stmt.setString(2, servicio.getDescripcion());
            stmt.setDouble(3, servicio.getPrecio());
            stmt.setInt(4, servicio.getDuracionMin());
            stmt.setBoolean(5, servicio.isActivo());
            stmt.setInt(6, servicio.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar servicio: " + e.getMessage());
        }
        return false;
    }

    // Elimina el servicio (o desactivar)
    public boolean eliminar(int id) {
        String sql = "UPDATE servicios SET activo = false WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar servicio: " + e.getMessage());
        }
        return false;
    }
}