package com.autofix.modelo;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Cita {

    private int id;
    private int idCliente;
    private int idServicio;
    private int idUsuario;
    private String matricula;
    private String modeloCoche;
    private Date fecha;
    private Time hora;
    private String estado;
    private double precioFinal;
    private String notas;
    private Timestamp fechaCreacion;

    // Campos adicionales para mostrar nombres (no IDs)
    private String nombreCliente;
    private String nombreServicio;
    private String nombreUsuario;

    // Constructor vacío
    public Cita() {}

    // Constructor completo
    public Cita(int id, int idCliente, int idServicio, int idUsuario, Date fecha, Time hora,
                String estado, double precioFinal, String notas, Timestamp fechaCreacion) {
        this.id = id;
        this.idCliente = idCliente;
        this.idServicio = idServicio;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.precioFinal = precioFinal;
        this.notas = notas;
        this.fechaCreacion = fechaCreacion;
    }

    // Constructor para crear nueva cita
    public Cita(int idCliente, int idServicio, int idUsuario, Date fecha, Time hora, double precioFinal, String notas) {
        this.idCliente = idCliente;
        this.idServicio = idServicio;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = "pendiente";
        this.precioFinal = precioFinal;
        this.notas = notas;
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

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    @Override
    public String toString() {
        return "Cita{" +
                "id=" + id +
                ", cliente='" + nombreCliente + '\'' +
                ", servicio='" + nombreServicio + '\'' +
                ", fecha=" + fecha +
                ", hora=" + hora +
                ", estado='" + estado + '\'' +
                ", precio=" + precioFinal +
                '}';
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
}