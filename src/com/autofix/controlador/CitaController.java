package com.autofix.controlador;

import com.autofix.dao.CitaDAO;
import com.autofix.dao.DetalleCitaDAO;
import com.autofix.modelo.Cita;
import com.autofix.modelo.DetalleCita;

import java.util.List;
import java.util.Map;

public class CitaController {

    private CitaDAO citaDAO;
    private DetalleCitaDAO detalleCitaDAO;

    public CitaController() {
        this.citaDAO = new CitaDAO();
        this.detalleCitaDAO = new DetalleCitaDAO();
    }

    public List<Cita> obtenerTodas() {
        return citaDAO.obtenerTodas();
    }

    public List<Cita> obtenerPorUsuario(int idUsuario) {
        return citaDAO.obtenerPorUsuario(idUsuario);
    }

    public List<Cita> obtenerPorEstado(String estado) {
        return citaDAO.obtenerPorEstado(estado);
    }

    public Cita obtenerPorId(int id) {
        return citaDAO.obtenerPorId(id);
    }

    public boolean guardar(Cita cita) {
        if (cita.getId() == 0) {
            int id = citaDAO.insertar(cita);
            return id > 0;
        } else {
            return citaDAO.actualizar(cita);
        }
    }

    public boolean eliminar(int id) {
        // En lugar de eliminar, archivamos la cita
        return citaDAO.archivar(id);
    }

    public boolean cambiarEstado(int id, String nuevoEstado) {
        return citaDAO.cambiarEstado(id, nuevoEstado);
    }

    /**
     * Cancela una cita con motivo y nombre de quien cancela
     * @param id ID de la cita
     * @param motivo Motivo de la cancelación
     * @param canceladoPor Nombre de quien cancela
     * @return true si se canceló correctamente
     */
    public boolean cancelarCita(int id, String motivo, String canceladoPor) {
        return citaDAO.cancelarCita(id, motivo, canceladoPor);
    }

    /**
     * Obtiene todas las citas canceladas
     * @return Lista de citas canceladas con info de cancelación
     */
    public List<Cita> obtenerCitasCanceladas() {
        return citaDAO.obtenerCitasCanceladas();
    }

    public boolean puedeEliminar(Cita cita) {
        return cita != null && !"completada".equals(cita.getEstado());
    }

    public boolean puedeEditar(Cita cita) {
        return cita != null && !"completada".equals(cita.getEstado()) && !"cancelada".equals(cita.getEstado());
    }

    public boolean puedeCancelar(Cita cita) {
        return cita != null && !"completada".equals(cita.getEstado()) && !"cancelada".equals(cita.getEstado());
    }

    // Estadísticas
    public int contarPendientes() {
        return citaDAO.contarPendientes();
    }

    public int contarCompletadas() {
        return citaDAO.contarCompletadas();
    }

    public int contarCanceladas() {
        return citaDAO.contarCanceladas();
    }

    public int contarEnProceso() {
        return citaDAO.contarEnProceso();
    }

    public double calcularIngresos() {
        return citaDAO.calcularIngresos();
    }

    // Servicios de la cita
    public List<DetalleCita> obtenerServicios(int idCita) {
        return detalleCitaDAO.obtenerPorCita(idCita);
    }

    public boolean agregarServicio(DetalleCita detalle) {
        return detalleCitaDAO.insertar(detalle);
    }

    public double calcularTotalCita(int idCita) {
        return detalleCitaDAO.calcularTotalCita(idCita);
    }

    public List<Cita> obtenerCompletadasPorFecha(java.sql.Date fecha) {
        return citaDAO.obtenerCompletadasPorFecha(fecha);
    }

    public List<java.sql.Date> obtenerFechasConCitasCompletadas() {
        return citaDAO.obtenerFechasConCitasCompletadas();
    }

    public double calcularIngresosPorFecha(java.sql.Date fecha) {
        return citaDAO.calcularIngresosPorFecha(fecha);
    }

    public Map<String, Object> obtenerEstadisticasDia(java.sql.Date fecha) {
        return citaDAO.obtenerEstadisticasDia(fecha);
    }
}