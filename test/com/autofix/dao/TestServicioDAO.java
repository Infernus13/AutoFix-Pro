package com.autofix.dao;

import com.autofix.modelo.Servicio;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestServicioDAO {

    private ServicioDAO servicioDAO;
    private static int idServicioCreado;

    @Before
    public void setUp() {
        servicioDAO = new ServicioDAO();
    }

    @Test
    public void test1_ObtenerTodos() {
        List<Servicio> servicios = servicioDAO.obtenerTodos();
        assertNotNull("La lista no debe ser null", servicios);
        assertTrue("Debe haber servicios", servicios.size() > 0);
        System.out.println("✓ ObtenerTodos: " + servicios.size() + " servicios");
    }

    @Test
    public void test2_Insertar() {
        Servicio nuevo = new Servicio();
        nuevo.setNombre("Servicio Test JUnit");
        nuevo.setDescripcion("Descripcion de prueba");
        nuevo.setPrecio(99.99);
        nuevo.setDuracionMin(60);
        nuevo.setActivo(true);

        boolean resultado = servicioDAO.insertar(nuevo);
        assertTrue("Debe insertar correctamente", resultado);

        idServicioCreado = nuevo.getId();
        System.out.println("✓ Insertar: Servicio creado con ID " + idServicioCreado);
    }

    @Test
    public void test3_ObtenerPorId() {
        if (idServicioCreado == 0) {
            test2_Insertar();
        }

        Servicio servicio = servicioDAO.obtenerPorId(idServicioCreado);
        assertNotNull("Debe encontrar el servicio", servicio);
        assertEquals("Servicio Test JUnit", servicio.getNombre());
        assertEquals(99.99, servicio.getPrecio(), 0.01);
        System.out.println("✓ ObtenerPorId: " + servicio.getNombre() + " - " + servicio.getPrecio() + "€");
    }

    @Test
    public void test4_Actualizar() {
        if (idServicioCreado == 0) {
            test2_Insertar();
        }

        Servicio servicio = servicioDAO.obtenerPorId(idServicioCreado);
        servicio.setNombre("Servicio Modificado JUnit");
        servicio.setPrecio(150.00);

        boolean resultado = servicioDAO.actualizar(servicio);
        assertTrue("Debe actualizar correctamente", resultado);

        Servicio verificar = servicioDAO.obtenerPorId(idServicioCreado);
        assertEquals("Servicio Modificado JUnit", verificar.getNombre());
        assertEquals(150.00, verificar.getPrecio(), 0.01);
        System.out.println("✓ Actualizar: Servicio modificado correctamente");
    }

    @Test
    public void test5_Eliminar() {
        if (idServicioCreado == 0) {
            test2_Insertar();
        }

        boolean resultado = servicioDAO.eliminar(idServicioCreado);
        assertTrue("Debe eliminar correctamente", resultado);
        System.out.println("✓ Eliminar: Servicio eliminado correctamente");
    }
}