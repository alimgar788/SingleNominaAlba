package com.nomina.singlenominaalba.connection;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * La clase Connector proporciona métodos para establecer y cerrar conexiones a la base de datos.
 */
public class Connector {
    /**
     * Establece una conexión a la base de datos.
     *
     * @return Una conexión a la base de datos especificada.
     * @throws SQLException Si ocurre un error al intentar establecer la conexión.
     */
    @SneakyThrows
    public static Connection getConnection() throws SQLException {
        final String USER = "root";
        final String PASS = "123456";
        final String DB_NAME = "empleados";
        final String CONN_URL = "jdbc:mariadb://localhost:3306/" + DB_NAME;
        Connection conn = null;
        Class.forName("org.mariadb.jdbc.Driver");
        conn = DriverManager.getConnection(CONN_URL, USER, PASS);
        return conn;
    }
}
