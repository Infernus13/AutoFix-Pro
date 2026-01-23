package com.autofix.modelo;

import java.sql.Timestamp;

public class Cliente {

    private int id;
    private String nombre;
    private String telefono;
    private String email;
    private String direccion;
    private Timestamp fechaAlta;

    // Constructor vacío
    public Cliente() {}

    // Constructor completo
    public Cliente(int id, String nombre, String telefono, String email, String direccion, Timestamp fechaAlta) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.fechaAlta = fechaAlta;
    }

    // Constructor sin ID (para crear nuevos clientes)
    public Cliente(String nombre, String telefono, String email, String direccion) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Timestamp getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Timestamp fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    @Override
    public String toString() {
        return nombre;
    }
}