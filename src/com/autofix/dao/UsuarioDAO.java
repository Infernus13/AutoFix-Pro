package com.autofix.dao;

import com.autofix.modelo.Usuario;
import com.autofix.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Verifica login
    public Usuario login(String email, String password) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("rol"),
                        rs.getTimestamp("fecha_alta")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error en login: " + e.getMessage());
        }
        return null;
    }

    // Obtiene todos los usuarios
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("rol"),
                        rs.getTimestamp("fecha_alta")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    // Obtiene usuario por ID
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("rol"),
                        rs.getTimestamp("fecha_alta")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener usuario: " + e.getMessage());
        }
        return null;
    }

    // Inserta nuevo usuario
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, email, password, rol) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getPassword());
            stmt.setString(4, usuario.getRol());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
        }
        return false;
    }

    // Actualiza usuario
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, email = ?, password = ?, rol = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getPassword());
            stmt.setString(4, usuario.getRol());
            stmt.setInt(5, usuario.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
        }
        return false;
    }

    // Elimina usuario
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
        }
        return false;
    }
}