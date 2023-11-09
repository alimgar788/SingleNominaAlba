package com.nomina.singlenominaalba.dao;

import com.nomina.singlenominaalba.connection.Connector;
import com.nomina.singlenominaalba.exceptions.CierreRecursosException;
import com.nomina.singlenominaalba.model.Administrador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * La clase AdministradorDao se encarga de realizar operaciones relacionadas con la autenticación y acceso de administradores.
 */
public class AdministradorDao {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet rs;

    /**
     * Intenta acceder a la sesión para un administrador específico.
     *
     * @param admin El objeto Administrador que contiene los datos de inicio de sesión.
     * @return true si el administrador puede acceder a la sesión, de lo contrario false.
     * @throws SQLException            Si ocurre un error al intentar acceder a la base de datos.
     * @throws CierreRecursosException Si hay un error al cerrar los recursos de base de datos.
     */
    public boolean accederSesion(Administrador admin) throws SQLException, CierreRecursosException {
        connection = obtenerConexion();
        preparedStatement = null;
        rs = null;

        try {
            String sql = "SELECT * FROM administradores WHERE dni = ? AND email = ? AND contrasenya = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, admin.getDni());
            preparedStatement.setString(2, admin.getEmail());
            preparedStatement.setString(3, admin.getContrasenya());
            rs = preparedStatement.executeQuery();

            return rs.next();

        } finally {
            cierraRecursosUtilizadosEnDB();
        }
    }

    /**
     * Obtiene una conexión a la base de datos.
     *
     * @return La conexión a la base de datos.
     * @throws SQLException Si ocurre un error al intentar obtener la conexión.
     */
    private Connection obtenerConexion() throws SQLException {
        return Connector.getConnection();
    }

    /**
     * Cierra los recursos utilizados en la base de datos.
     *
     * @throws CierreRecursosException Si hay un error al cerrar los recursos de la base de datos.
     */
    private void cierraRecursosUtilizadosEnDB() throws CierreRecursosException {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new CierreRecursosException("Los recursos utilizados para la base de datos no se han podido cerrar correctamente");
        }
    }
}
