package com.autofix.controlador;

import com.autofix.dao.ServicioDAO;
import com.autofix.modelo.Servicio;
import java.util.List;

public class ServicioController {

    private ServicioDAO servicioDAO;

    public ServicioController() {
        this.servicioDAO = new ServicioDAO();
    }

    public List<Servicio> obtenerTodos() {
        return servicioDAO.obtenerTodos();
    }

    public List<Servicio> obtenerActivos() {
        return servicioDAO.obtenerActivos();
    }

    public Servicio obtenerPorId(int id) {
        return servicioDAO.obtenerPorId(id);
    }

    public boolean guardar(Servicio servicio) {
        if (servicio.getId() == 0) {
            return servicioDAO.insertar(servicio);
        } else {
            return servicioDAO.actualizar(servicio);
        }
    }

    public boolean eliminar(int id) {
        return servicioDAO.eliminar(id);
    }

    public boolean validarServicio(Servicio servicio) {
        if (servicio.getNombre() == null || servicio.getNombre().trim().isEmpty()) {
            return false;
        }
        if (servicio.getPrecio() <= 0) {
            return false;
        }
        if (servicio.getDuracionMin() <= 0) {
            return false;
        }
        return true;
    }
}