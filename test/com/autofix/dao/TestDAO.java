package com.autofix.dao;

import com.autofix.modelo.Cliente;
import com.autofix.modelo.Servicio;
import com.autofix.modelo.Usuario;
import com.autofix.modelo.Cita;
import java.util.List;

public class TestDAO {

    public static void main(String[] args) {
        System.out.println("=== TEST DE DAOs ===\n");

        // Test ClienteDAO
        System.out.println("--- Test ClienteDAO ---");
        ClienteDAO clienteDAO = new ClienteDAO();
        List<Cliente> clientes = clienteDAO.obtenerTodos();
        System.out.println("Clientes encontrados: " + clientes.size());
        for (Cliente c : clientes) {
            System.out.println("  - " + c.getNombre() + " | " + c.getTelefono());
        }

        // Test ServicioDAO
        System.out.println("\n--- Test ServicioDAO ---");
        ServicioDAO servicioDAO = new ServicioDAO();
        List<Servicio> servicios = servicioDAO.obtenerTodos();
        System.out.println("Servicios encontrados: " + servicios.size());
        for (Servicio s : servicios) {
            System.out.println("  - " + s.getNombre() + " | " + s.getPrecio() + " EUR");
        }

        // Test UsuarioDAO
        System.out.println("\n--- Test UsuarioDAO ---");
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        System.out.println("Usuarios encontrados: " + usuarios.size());
        for (Usuario u : usuarios) {
            System.out.println("  - " + u.getNombre() + " | " + u.getRol());
        }

        // Test CitaDAO
        System.out.println("\n--- Test CitaDAO ---");
        CitaDAO citaDAO = new CitaDAO();
        List<Cita> citas = citaDAO.obtenerTodas();
        System.out.println("Citas encontradas: " + citas.size());
        for (Cita c : citas) {
            System.out.println("  - " + c.getNombreCliente() + " | " + c.getFecha() + " | " + c.getEstado());
        }

        System.out.println("\n=== TESTS COMPLETADOS ===");
    }
}