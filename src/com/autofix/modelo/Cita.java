package com.autofix.modelo;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Modelo que representa una cita del taller
 * Incluye campos para gestión de cancelaciones
 *
 * @author Cristian Sánchez
 * @version 2.0
 */
public class Cita {

    private int id;
    private int idCliente;
    private int idUsuario;
    private String matricula;
    private String modeloCoche;
    private Date fecha;
    private Time hora;
    private String estado;
    private double precioFinal;
    private String notas;
    private boolean archivada;
    private Timestamp fechaCreacion;

    // Campos para cancelación
    private String motivoCancelacion;
    private String canceladoPor;
    private Timestamp fechaCancelacion;

    // Campos auxiliares para mostrar en tablas (no se guardan en BD)
    private String nombreCliente;
    private String nombreUsuario;
    private String nombreServicio;

    // Constructor vacío
    public Cita() {}

    // Constructor con parámetros básicos
    public Cita(int idCliente, int idUsuario, String matricula, String modeloCoche,
                Date fecha, Time hora, double precioFinal, String notas) {
        this.idCliente = idCliente;
        this.idUsuario = idUsuario;
        this.matricula = matricula;
        this.modeloCoche = modeloCoche;
        this.fecha = fecha;
        this.hora = hora;
        this.precioFinal = precioFinal;
        this.notas = notas;
        this.estado = "pendiente";
        this.archivada = false;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getModeloCoche() {
        return modeloCoche;
    }

    public void setModeloCoche(String modeloCoche) {
        this.modeloCoche = modeloCoche;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Time getHora() {
        return hora;
    }

    public void setHora(Time hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(double precioFinal) {
        this.precioFinal = precioFinal;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public boolean isArchivada() {
        return archivada;
    }

    public void setArchivada(boolean archivada) {
        this.archivada = archivada;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters para cancelación
    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public String getCanceladoPor() {
        return canceladoPor;
    }

    public void setCanceladoPor(String canceladoPor) {
        this.canceladoPor = canceladoPor;
    }

    public Timestamp getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void setFechaCancelacion(Timestamp fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    // Getters y Setters auxiliares
    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    @Override
    public String toString() {
        return "Cita{" +
                "id=" + id +
                ", cliente=" + nombreCliente +
                ", vehiculo=" + modeloCoche +
                ", fecha=" + fecha +
                ", estado=" + estado +
                '}';
    }
}