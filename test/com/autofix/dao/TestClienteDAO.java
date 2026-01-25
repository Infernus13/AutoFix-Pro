package com.autofix.dao;

import com.autofix.modelo.Cliente;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestClienteDAO {

    private ClienteDAO clienteDAO;
    private static int idClienteCreado;

    @Before
    public void setUp() {
        clienteDAO = new ClienteDAO();
    }

    @Test
    public void test1_ObtenerTodos() {
        List<Cliente> clientes = clienteDAO.obtenerTodos();
        assertNotNull("La lista no debe ser null", clientes);
        assertTrue("Debe haber al menos un cliente", clientes.size() > 0);
        System.out.println("✓ ObtenerTodos: " + clientes.size() + " clientes");
    }

    @Test
    public void test2_Insertar() {
        Cliente nuevo = new Cliente();
        nuevo.setNombre("Cliente Test JUnit");
        nuevo.setTelefono("600999888");
        nuevo.setEmail("junit@test.com");
        nuevo.setDireccion("Calle Test 123");

        boolean resultado = clienteDAO.insertar(nuevo);
        assertTrue("Debe insertar correctamente", resultado);
        assertTrue("Debe asignar ID", nuevo.getId() > 0);

        idClienteCreado = nuevo.getId();
        System.out.println("✓ Insertar: Cliente creado con ID " + idClienteCreado);
    }

    @Test
    public void test3_ObtenerPorId() {
        if (idClienteCreado == 0) {
            test2_Insertar();
        }

        Cliente cliente = clienteDAO.obtenerPorId(idClienteCreado);
        assertNotNull("Debe encontrar el cliente", cliente);
        assertEquals("El nombre debe coincidir", "Cliente Test JUnit", cliente.getNombre());
        System.out.println("✓ ObtenerPorId: Encontrado " + cliente.getNombre());
    }

    @Test
    public void test4_Actualizar() {
        if (idClienteCreado == 0) {
            test2_Insertar();
        }

        Cliente cliente = clienteDAO.obtenerPorId(idClienteCreado);
        cliente.setNombre("Cliente Modificado JUnit");
        cliente.setTelefono("611222333");

        boolean resultado = clienteDAO.actualizar(cliente);
        assertTrue("Debe actualizar correctamente", resultado);

        Cliente verificar = clienteDAO.obtenerPorId(idClienteCreado);
        assertEquals("Cliente Modificado JUnit", verificar.getNombre());
        assertEquals("611222333", verificar.getTelefono());
        System.out.println("✓ Actualizar: Cliente modificado correctamente");
    }

    @Test
    public void test5_Eliminar() {
        if (idClienteCreado == 0) {
            test2_Insertar();
        }

        boolean resultado = clienteDAO.eliminar(idClienteCreado);
        assertTrue("Debe eliminar correctamente", resultado);

        Cliente verificar = clienteDAO.obtenerPorId(idClienteCreado);
        assertNull("No debe encontrar el cliente eliminado", verificar);
        System.out.println("✓ Eliminar: Cliente eliminado correctamente");
    }

    @Test
    public void test6_ObtenerIdInexistente() {
        Cliente cliente = clienteDAO.obtenerPorId(99999);
        assertNull("No debe encontrar ID inexistente", cliente);
        System.out.println("✓ ID inexistente retorna null correctamente");
    }
}