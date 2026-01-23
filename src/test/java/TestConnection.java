package com.autofix.util;

import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {
        System.out.println("Probando conexion a AutoFix Pro...");

        Connection conn = DatabaseConnection.getConnection();

        if (conn != null) {
            System.out.println("La conexion funciona correctamente!");
        } else {
            System.out.println("No se pudo conectar.");
        }


    }
}