package com.autofix.controlador;

import com.autofix.dao.UsuarioDAO;
import com.autofix.modelo.Usuario;
import java.util.List;

public class UsuarioController {

    private UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public Usuario login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        if (password == null || password.trim().isEmpty()) {
            return null;
        }
        return usuarioDAO.login(email, password);
    }

    public List<Usuario> obtenerTodos() {
        return usuarioDAO.obtenerTodos();
    }

    public Usuario obtenerPorId(int id) {
        return usuarioDAO.obtenerPorId(id);
    }

    public boolean guardar(Usuario usuario) {
        if (usuario.getId() == 0) {
            return usuarioDAO.insertar(usuario);
        } else {
            return usuarioDAO.actualizar(usuario);
        }
    }

    public boolean eliminar(int id) {
        return usuarioDAO.eliminar(id);
    }

    public boolean validarUsuario(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            return false;
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            return false;
        }
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            return false;
        }
        if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean esAdmin(Usuario usuario) {
        return usuario != null && "administrador".equals(usuario.getRol());
    }
}