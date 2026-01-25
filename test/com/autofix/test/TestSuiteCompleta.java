package com.autofix.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.autofix.util.TestConexion;
import com.autofix.dao.TestClienteDAO;
import com.autofix.dao.TestServicioDAO;
import com.autofix.dao.TestUsuarioDAO;
import com.autofix.dao.TestCitaDAO;

@RunWith(Suite.class)
@SuiteClasses({
        TestConexion.class,
        TestClienteDAO.class,
        TestServicioDAO.class,
        TestUsuarioDAO.class,
        TestCitaDAO.class
})
public class TestSuiteCompleta {
    // Esta clase ejecuta todos los tests
}

