package com.autofix.modelo;

public class DetalleCita {

    private int id;
    private int idCita;
    private int idServicio;
    private double precio;
    private int cantidad;

    // Para mostrar en la interfaz
    private String nombreServicio;

    // Constructores
    public DetalleCita() {}

    public DetalleCita(int idServicio, double precio, int cantidad) {
        this.idServicio = idServicio;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public DetalleCita(int idCita, int idServicio, double precio, int cantidad) {
        this.idCita = idCita;
        this.idServicio = idServicio;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public double getSubtotal() {
        return precio * cantidad;
    }

    @Override
    public String toString() {
        return nombreServicio + " x" + cantidad + " - " + String.format("%.2f €", getSubtotal());
    }
}