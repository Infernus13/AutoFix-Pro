package com.autofix.util;

import com.autofix.dao.CitaDAO;
import com.autofix.dao.ClienteDAO;
import com.autofix.dao.ServicioDAO;
import com.autofix.dao.UsuarioDAO;
import com.autofix.modelo.Cita;
import com.autofix.modelo.Cliente;
import com.autofix.modelo.Servicio;
import com.autofix.modelo.Usuario;

import java.util.List;

public class TestDAO {

    public static void main(String[] args) {
        System.out.println("=== PROBANDO DAOs ===\n");

        // Probar UsuarioDAO
        System.out.println("-- USUARIOS --");
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        for (Usuario u : usuarios) {
            System.out.println(u);
        }

        // Probar login
        System.out.println("\n-- LOGIN --");
        Usuario admin = usuarioDAO.login("juan.martinez@autofix.com", "1234");
        if (admin != null) {
            System.out.println("Login correcto: " + admin.getNombre() + " (" + admin.getRol() + ")");
        } else {
            System.out.println("Login fallido");
        }

        // Probar ClienteDAO
        System.out.println("\n-- CLIENTES --");
        ClienteDAO clienteDAO = new ClienteDAO();
        List<Cliente> clientes = clienteDAO.obtenerTodos();
        for (Cliente c : clientes) {
            System.out.println(c);
        }

        // Probar ServicioDAO
        System.out.println("\n-- SERVICIOS --");
        ServicioDAO servicioDAO = new ServicioDAO();
        List<Servicio> servicios = servicioDAO.obtenerTodos();
        for (Servicio s : servicios) {
            System.out.println(s);
        }

        // Probar CitaDAO
        System.out.println("\n-- CITAS --");
        CitaDAO citaDAO = new CitaDAO();
        List<Cita> citas = citaDAO.obtenerTodas();
        for (Cita c : citas) {
            System.out.println(c);
        }

        // Probar estadísticas del Dashboard
        System.out.println("\n-- ESTADISTICAS DASHBOARD --");
        System.out.println("Citas hoy: " + citaDAO.contarCitasHoy());
        System.out.println("Pendientes: " + citaDAO.contarPendientes());
        System.out.println("Completadas: " + citaDAO.contarCompletadas());
        System.out.println("Ingresos: " + citaDAO.calcularIngresos() + " EUR");

        System.out.println("\n=== TODO FUNCIONA ===");
    }
}