package com.nomina.singlenominaalba.dao;

import com.nomina.singlenominaalba.connection.Connector;
import com.nomina.singlenominaalba.exceptions.CierreRecursosException;
import com.nomina.singlenominaalba.exceptions.DatosNoCorrectosException;
import com.nomina.singlenominaalba.model.Empleado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * La clase EmpleadoDao se encarga de gestionar operaciones relacionadas con la tabla de empleados en la base de datos.
 */
public class EmpleadoDao {
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet rs;

    /**
     * Obtiene una lista de empleados según el campo y valor proporcionados.
     *
     * @param campo El campo por el que se filtrará la búsqueda.
     * @param valor El valor que se utilizará en el filtro.
     * @return Una lista de objetos Empleado que satisfacen los criterios de búsqueda.
     * @throws SQLException            Si ocurre un error al intentar acceder a la base de datos.
     * @throws CierreRecursosException Si hay un error al cerrar los recursos de base de datos.
     */
    public List<Empleado> obtenerListaEmpleados(String campo, Object valor) throws SQLException, CierreRecursosException {
        connection = obtenerConexion();
        List<Empleado> listaEmpleados = new ArrayList<>();
        String sql = "SELECT * FROM empleados e WHERE estadoEmpleado = 1";
        preparedStatement = connection.prepareStatement(sql);

        try {
            if (campo != null && !campo.equals("") && valor != null && !valor.equals("")) {
                String columna = getColumn(campo);
                String operador = getOperador(campo);
                sql = "SELECT e.*, n.salario FROM empleados e JOIN nominas n ON e.dni = n.dni WHERE e.estadoEmpleado = 1 AND " + columna + operador;
                preparedStatement = connection.prepareStatement(sql);
                setPreparedStatementValue(preparedStatement, campo, valor);
            }

            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Empleado empl = obtenerEmpleadoDeResultSet(rs);
                listaEmpleados.add(empl);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar la lista de empleados en la base de datos");
        } catch (DatosNoCorrectosException e) {
            throw new RuntimeException(e);
        } finally {
            cierraRecursosUtilizadosEnDB();
        }
        return listaEmpleados;
    }

    /**
     * Actualiza la información de un empleado en la base de datos.
     *
     * @param empl El objeto Empleado con la información actualizada.
     * @return true si la actualización se realizó correctamente, de lo contrario false.
     * @throws SQLException            Si ocurre un error al intentar acceder a la base de datos.
     * @throws CierreRecursosException Si hay un error al cerrar los recursos de base de datos.
     */
    public boolean actualizarEmpleado(Empleado empl) throws SQLException, CierreRecursosException {

        connection = obtenerConexion();
        preparedStatement = null;

        try {
            StringBuilder sqlBuilder = new StringBuilder("UPDATE empleados SET ");
            List<Object> params = new ArrayList<>();

            if (empl.getNombre() != null) {
                sqlBuilder.append("nombre = ?, ");
                params.add(empl.getNombre());
            }
            if (empl.getSexo() != null) {
                sqlBuilder.append("sexo = ?, ");
                params.add(String.valueOf(empl.getSexo()));
            }
            if (empl.getCategoria() > 0 && empl.getCategoria() < 10) {
                sqlBuilder.append("categoria = ?, ");
                params.add(empl.getCategoria());
            }
            if (empl.getAnyos() >= 0.0) {
                sqlBuilder.append("anyos = ?, ");
                params.add(empl.getAnyos());
            }

            sqlBuilder.setLength(sqlBuilder.length() - 2); //así eliminamos la última coma y el último espacio para que no de error la consulta

            sqlBuilder.append(" WHERE dni = ?");
            params.add(empl.getDni());

            String sql = sqlBuilder.toString();

            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }

            Boolean filasAfectadas = preparedStatement.executeUpdate() > 0;
            if (filasAfectadas) {
                SalarioDao salarioDao = new SalarioDao();
                salarioDao.actualizaSalario(empl);
            }
            return filasAfectadas;

        } finally {
            cierraRecursosUtilizadosEnDB();
        }
    }

    /**
     * Obtiene un empleado de la base de datos según el DNI proporcionado.
     *
     * @param dni El DNI del empleado a buscar.
     * @return El objeto Empleado correspondiente al DNI especificado.
     * @throws CierreRecursosException Si hay un error al cerrar los recursos de base de datos.
     * @throws SQLException            Si ocurre un error al intentar acceder a la base de datos.
     */
    public Empleado obtenerEmpleadoPorDni(String dni) throws CierreRecursosException, SQLException {
        connection = obtenerConexion();
        preparedStatement = null;

        ResultSet rs = null;
        Empleado empl = null;

        try {
            String sql = "SELECT * from empleados WHERE dni=?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, dni);

            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("nombre");
                Character sexo = rs.getString("sexo").charAt(0);
                int categoria = rs.getInt("categoria");
                double anyos = rs.getDouble("anyos");

                empl = new Empleado(nombre, dni, sexo, categoria, anyos);
            } else {
                System.out.println("No se encontro un empleado con el DNI: " + dni);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar los datos de los empleados de la base de datos");
        } catch (DatosNoCorrectosException e) {
            throw new RuntimeException(e);
        } finally {
            cierraRecursosUtilizadosEnDB();
        }
        return empl;
    }

    /**
     * Inserta un nuevo empleado en la base de datos.
     *
     * @param empl El objeto Empleado que se va a insertar.
     * @return true si la inserción se realizó correctamente, de lo contrario false.
     * @throws SQLException            Si ocurre un error al intentar acceder a la base de datos.
     * @throws CierreRecursosException Si hay un error al cerrar los recursos de base de datos.
     */
    public boolean insertarEmpleado(Empleado empl) throws SQLException, CierreRecursosException {
        connection = obtenerConexion();

        String checkSql = "SELECT COUNT(*) AS count FROM empleados WHERE dni = ?";

        try (PreparedStatement checkSqlStatement = connection.prepareStatement(checkSql)) {
            checkSqlStatement.setString(1, empl.getDni());
            try (ResultSet rs = checkSqlStatement.executeQuery()) {
                rs.next();
                int count = rs.getInt("count");

                if (count > 0) {
                    System.out.println("El empleado con DNI " + empl.getDni() + " ya está registrado en la base de datos.");
                    return false;
                }
            }
        }

        String sql = "INSERT INTO empleados(nombre, dni, sexo, categoria, anyos) VALUE (?, ?, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, empl.getNombre());
            preparedStatement.setString(2, empl.getDni());
            preparedStatement.setString(3, empl.getSexo().toString());
            preparedStatement.setInt(4, empl.getCategoria());
            preparedStatement.setDouble(5, empl.getAnyos());

            int filasInsertadas = preparedStatement.executeUpdate();
            return filasInsertadas > 0;
        } finally {
            cierraRecursosUtilizadosEnDB();
        }
    }

    /**
     * Elimina un empleado de la base de datos según el DNI proporcionado.
     *
     * @param dni El DNI del empleado que se va a eliminar.
     * @return true si la eliminación se realizó correctamente, de lo contrario false.
     * @throws SQLException            Si ocurre un error al intentar acceder a la base de datos.
     * @throws CierreRecursosException Si hay un error al cerrar los recursos de base de datos.
     */
    public boolean eliminarEmpleado(String dni) throws SQLException, CierreRecursosException {
        connection = obtenerConexion();
        preparedStatement = null;
        String sql = "UPDATE empleados SET estadoEmpleado = 0 WHERE dni = ?";

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, dni);
            int filasAfectadas = preparedStatement.executeUpdate();
            return filasAfectadas > 0;
        } finally {
            cierraRecursosUtilizadosEnDB();
        }
    }

    /**
     * Establece el valor correspondiente en el PreparedStatement según el campo y valor proporcionados.
     *
     * @param preparedStatement El objeto PreparedStatement en el que se establecerá el valor.
     * @param campo             El campo para el que se establecerá el valor.
     * @param valor             El valor que se establecerá en el campo.
     * @throws SQLException Si ocurre un error al establecer el valor en el PreparedStatement.
     */
    private void setPreparedStatementValue(PreparedStatement preparedStatement, String campo, Object valor) throws SQLException {
        switch (campo) {
            case "nombre":
            case "dni":
                preparedStatement.setString(1, "%" + valor.toString().toLowerCase() + "%");
                break;
            case "sexo":
                if (valor instanceof String) {
                    String sexo = (String) valor;
                    if (sexo.length() == 1) {
                        char sexoMinuscula = Character.toLowerCase(sexo.charAt(0));
                        if (sexoMinuscula == 'm' || sexoMinuscula == 'f' || sexoMinuscula == 'i') {
                            preparedStatement.setString(1, String.valueOf(sexoMinuscula));
                        } else {
                            throw new IllegalArgumentException("Solo se puede escribir en el sexo los caracteres m, f o i. Pruebe con un caracter valido");
                        }
                    } else {
                        throw new IllegalArgumentException("El campo sexo debe tener sólo un caracter. Inténtelo de nuevo");
                    }
                } else {
                    throw new IllegalArgumentException("El valor de sexo no es una cadena de caracteres.");
                }
                break;

            case "categoria":
                try {
                    int valorInt = Integer.parseInt((String) valor);
                    preparedStatement.setInt(1, valorInt);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("El valor introducido no es un numero entero.");
                }
                break;

            case "salario":
            case "anyos":
                try {
                    String valorString = (String) valor;
                    double valorDouble = Double.parseDouble(valorString.replace(',', '.'));
                    preparedStatement.setDouble(1, valorDouble);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("El valor introducido no es un numero decimal.");
                }
                break;
        }
    }

    /**
     * Obtiene el operador correspondiente para el campo dado.
     *
     * @param campo El campo para el que se obtendrá el operador.
     * @return El operador correspondiente al campo especificado.
     */
    private String getOperador(String campo) {
        String operador = " = ?";
        switch (campo) {
            case "nombre":
            case "dni":
                operador = " like ?";
                break;
        }
        return operador;
    }

    /**
     * Obtiene la columna correspondiente para el campo dado.
     *
     * @param campo El campo para el que se obtendrá la columna.
     * @return La columna correspondiente al campo especificado.
     */
    private String getColumn(String campo) {
        String operator = "e."+campo;
        switch (campo) {
            case "nombre":
            case "dni":
                operator = " LOWER(e." + campo + ")";
                break;
        }
        return operator;
    }

    /**
     * Crea un objeto Empleado a partir del ResultSet proporcionado.
     *
     * @param rs El conjunto de resultados de la consulta.
     * @return Un objeto Empleado creado a partir de los datos del ResultSet.
     * @throws SQLException              Si ocurre un error al obtener datos del ResultSet.
     * @throws DatosNoCorrectosException Si los datos obtenidos no son correctos.
     */
    private Empleado obtenerEmpleadoDeResultSet(ResultSet rs) throws SQLException, DatosNoCorrectosException {
        String nombre = rs.getString("nombre");
        String dni = rs.getString("dni");
        Character sexo = rs.getString("sexo").charAt(0);
        int categoria = rs.getInt("categoria");
        double anyos = rs.getDouble("anyos");

        return new Empleado(nombre, dni, sexo, categoria, anyos);
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