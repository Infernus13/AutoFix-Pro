package com.autofix.dao;

import com.autofix.modelo.Usuario;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestUsuarioDAO {

    private UsuarioDAO usuarioDAO;
    private static int idUsuarioCreado;

    @Before
    public void setUp() {
        usuarioDAO = new UsuarioDAO();
    }

    @Test
    public void test1_ObtenerTodos() {
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        assertNotNull("La lista no debe ser null", usuarios);
        assertTrue("Debe haber usuarios", usuarios.size() > 0);
        System.out.println("✓ ObtenerTodos: " + usuarios.size() + " usuarios");
    }

    @Test
    public void test2_Insertar() {
        Usuario nuevo = new Usuario();
        nuevo.setNombre("Usuario Test JUnit");
        nuevo.setEmail("junit" + System.currentTimeMillis() + "@autofix.com");
        nuevo.setPassword("test123");
        nuevo.setRol("trabajador");

        boolean resultado = usuarioDAO.insertar(nuevo);
        assertTrue("Debe insertar correctamente", resultado);

        idUsuarioCreado = nuevo.getId();
        System.out.println("✓ Insertar: Usuario creado con ID " + idUsuarioCreado);
    }

    @Test
    public void test3_LoginCorrecto() {
        Usuario usuario = usuarioDAO.login("cristian.sanchez@gmail.com", "1308");
        assertNotNull("Debe validar credenciales correctas", usuario);
        System.out.println("✓ Login correcto: " + usuario.getNombre());
    }

    @Test
    public void test4_LoginIncorrecto() {
        Usuario usuario = usuarioDAO.login("noexiste@test.com", "wrongpass");
        assertNull("No debe validar credenciales incorrectas", usuario);
        System.out.println("✓ Login incorrecto rechazado correctamente");
    }

    @Test
    public void test5_Eliminar() {
        if (idUsuarioCreado > 0) {
            boolean resultado = usuarioDAO.eliminar(idUsuarioCreado);
            assertTrue("Debe eliminar correctamente", resultado);
            System.out.println("✓ Eliminar: Usuario eliminado correctamente");
        } else {
            System.out.println("✓ Eliminar: No hay usuario de test para eliminar");
        }
    }
}