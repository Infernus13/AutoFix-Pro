package com.autofix.controlador;

import com.autofix.dao.ClienteDAO;
import com.autofix.modelo.Cliente;
import java.util.List;

public class ClienteController {

    private ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    public List<Cliente> obtenerTodos() {
        return clienteDAO.obtenerTodos();
    }

    public Cliente obtenerPorId(int id) {
        return clienteDAO.obtenerPorId(id);
    }

    public boolean guardar(Cliente cliente) {
        if (cliente.getId() == 0) {
            return clienteDAO.insertar(cliente);
        } else {
            return clienteDAO.actualizar(cliente);
        }
    }

    public boolean eliminar(int id) {
        return clienteDAO.eliminar(id);
    }

    public boolean validarCliente(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            return false;
        }
        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            return false;
        }
        return true;
    }
}