package com.autofix.dao;

import com.autofix.modelo.Cita;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class TestCitaDAO {

    private CitaDAO citaDAO;

    @Before
    public void setUp() {
        citaDAO = new CitaDAO();
    }

    @Test
    public void testObtenerTodas() {
        List<Cita> citas = citaDAO.obtenerTodas();
        assertNotNull("La lista no debe ser null", citas);
        System.out.println("✓ ObtenerTodas: " + citas.size() + " citas");
    }

    @Test
    public void testObtenerPorEstadoCompletada() {
        List<Cita> citas = citaDAO.obtenerPorEstado("completada");
        assertNotNull(citas);
        for (Cita c : citas) {
            assertEquals("completada", c.getEstado());
        }
        System.out.println("✓ Filtro completadas: " + citas.size() + " citas");
    }

    @Test
    public void testObtenerPorEstadoPendiente() {
        List<Cita> citas = citaDAO.obtenerPorEstado("pendiente");
        assertNotNull(citas);
        for (Cita c : citas) {
            assertEquals("pendiente", c.getEstado());
        }
        System.out.println("✓ Filtro pendientes: " + citas.size() + " citas");
    }

    @Test
    public void testContarPendientes() {
        int count = citaDAO.contarPendientes();
        assertTrue("Contador debe ser >= 0", count >= 0);
        System.out.println("✓ Contar pendientes: " + count);
    }

    @Test
    public void testContarCompletadas() {
        int count = citaDAO.contarCompletadas();
        assertTrue("Contador debe ser >= 0", count >= 0);
        System.out.println("✓ Contar completadas: " + count);
    }

    @Test
    public void testCalcularIngresos() {
        double ingresos = citaDAO.calcularIngresos();
        assertTrue("Ingresos debe ser >= 0", ingresos >= 0);
        System.out.println("✓ Calcular ingresos: " + ingresos + "€");
    }

    @Test
    public void testObtenerPorIdInexistente() {
        Cita cita = citaDAO.obtenerPorId(99999);
        assertNull("No debe encontrar cita inexistente", cita);
        System.out.println("✓ ID inexistente retorna null");
    }
}