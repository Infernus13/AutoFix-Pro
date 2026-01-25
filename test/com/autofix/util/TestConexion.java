package com.autofix.util;

import org.junit.Test;
import static org.junit.Assert.*;
import java.sql.Connection;

public class TestConexion {

    @Test
    public void testConexion() {
        Connection conn = DatabaseConnection.getConnection();
        assertNotNull("La conexión no debe ser null", conn);
        System.out.println("Conexión exitosa a la base de datos");
    }
}