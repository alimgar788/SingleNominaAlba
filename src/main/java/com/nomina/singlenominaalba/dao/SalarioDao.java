package com.nomina.singlenominaalba.dao;

import com.nomina.singlenominaalba.connection.Connector;
import com.nomina.singlenominaalba.exceptions.CierreRecursosException;
import com.nomina.singlenominaalba.exceptions.EmpleadoNoEncontradoException;
import com.nomina.singlenominaalba.model.Empleado;
import com.nomina.singlenominaalba.model.Nomina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * La clase SalarioDao proporciona métodos para realizar operaciones relacionadas con el salario en la base de datos.
 */
public class SalarioDao {
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet rs;

    /**
     * Muestra el salario correspondiente a un empleado con un determinado DNI.
     *
     * @param dni El DNI del empleado cuyo salario se va a consultar.
     * @return El salario del empleado.
     * @throws CierreRecursosException       Si ocurre un error al cerrar los recursos.
     * @throws SQLException                  Si ocurre un error de SQL.
     * @throws EmpleadoNoEncontradoException Si no se encuentra un empleado con el DNI especificado.
     */
    public Double muestraSalarioPorDni(String dni) throws CierreRecursosException, SQLException, EmpleadoNoEncontradoException {
        if (dni == null) {
            throw new EmpleadoNoEncontradoException("El DNI proporcionado es nulo");
        }
        connection = obtenerConexion();
        Double salario = 0.0;
        dni = dni.toUpperCase();

        try {
            String sql = "SELECT salario from nominas n JOIN empleados e ON n.dni = e.dni  WHERE n.dni like ? AND e.estadoEmpleado = 1";
            //String sql = "SELECT salario from nominas WHERE dni like ? ";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, dni);

            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                salario = rs.getDouble("salario");
            } else {
                throw new EmpleadoNoEncontradoException("No se encontro empleado con el DNI: " + dni + " o salario asociado");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar los datos de las nominas de la base de datos");
        } finally {
            cierraRecursosUtilizadosEnDB();
        }
        return salario;
    }

    /**
     * Inserta el salario de un empleado en la base de datos.
     *
     * @param empl El objeto Empleado del cual se calculará y se insertará el salario.
     * @return true si se realiza la inserción correctamente, de lo contrario false.
     * @throws SQLException            Si ocurre un error de SQL.
     * @throws CierreRecursosException Si ocurre un error al cerrar los recursos.
     */
    public boolean insertarSalario(Empleado empl) throws SQLException, CierreRecursosException {
        connection = obtenerConexion();
        Nomina nomina = new Nomina();
        try {
            double salario = nomina.sueldo(empl, empl.getCategoria(), empl.getAnyos());

            String sql = "INSERT INTO nominas (dni, salario) VALUES (?, ?)";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, empl.getDni());
            preparedStatement.setDouble(2, salario);

            int filasInsertadas = preparedStatement.executeUpdate();
            return filasInsertadas > 0;
        } finally {
            cierraRecursosUtilizadosEnDB();
        }
    }

    /**
     * Actualiza el salario de un empleado en la base de datos.
     *
     * @param empl El objeto Empleado del cual se actualizará el salario.
     * @return true si se realiza la actualización correctamente, de lo contrario false.
     * @throws SQLException            Si ocurre un error de SQL.
     * @throws CierreRecursosException Si ocurre un error al cerrar los recursos.
     */
    public boolean actualizaSalario(Empleado empl) throws SQLException, CierreRecursosException {

        try {
            connection = obtenerConexion();
            Nomina nomina = new Nomina();
            int nuevaCategoria = empl.getCategoria();
            double nuevoAnyo = empl.getAnyos();
            double salario = nomina.sueldo(empl, nuevaCategoria, nuevoAnyo);
            String sql = "UPDATE nominas SET salario = ? WHERE dni = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, salario);
            preparedStatement.setString(2, empl.getDni());

            int filasAfectadas = preparedStatement.executeUpdate();
            return filasAfectadas > 0;
        } finally {
            cierraRecursosUtilizadosEnDB();
        }
    }

    /**
     * Obtiene una conexión a la base de datos.
     *
     * @return Una conexión a la base de datos.
     * @throws SQLException Si ocurre un error al intentar establecer la conexión.
     */
    private Connection obtenerConexion() throws SQLException {
        return Connector.getConnection();
    }

    /**
     * Cierra los recursos utilizados en la base de datos.
     *
     * @throws CierreRecursosException Si ocurre un error al intentar cerrar los recursos.
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
