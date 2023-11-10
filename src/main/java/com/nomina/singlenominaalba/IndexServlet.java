package com.nomina.singlenominaalba;

import com.nomina.singlenominaalba.dao.AdministradorDao;
import com.nomina.singlenominaalba.dao.EmpleadoDao;
import com.nomina.singlenominaalba.dao.SalarioDao;
import com.nomina.singlenominaalba.exceptions.CierreRecursosException;
import com.nomina.singlenominaalba.exceptions.DatosNoCorrectosException;
import com.nomina.singlenominaalba.exceptions.EmpleadoNoEncontradoException;
import com.nomina.singlenominaalba.model.Administrador;
import com.nomina.singlenominaalba.model.Empleado;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Este es el único servlet del proyecto y maneja las solicitudes entrantes y dirige el flujo de control según la opción especificada.
 */

@WebServlet(name = "indexServlet", value = "/index")
public class IndexServlet extends HttpServlet {

    /**
     * Se ejecuta cuando se recibe una solicitud GET y dirige el flujo de control según el parámetro 'opcion'.
     *
     * @param request  La solicitud HTTP recibida.
     * @param response La respuesta HTTP que se enviará.
     * @throws ServletException Si ocurre un error de servlet.
     * @throws IOException      Si ocurre un error de E/S.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (this.necesitaLogin(request)) {
            this.loginRedirect(request, response);
            return;
        }

        this.estableceAtributosComunes(request);
        switch (request.getParameter("opcion")) {
            case "registro":
                this.doGetRegistro(request, response);
                break;
            case "listado":
                this.doGetListado(request, response);
                break;
            case "consulta":
                this.doGetConsulta(request, response);
                break;
            case "actualiza":
                this.doGetActualiza(request, response);
                break;
            case "login":
                this.doGetLogin(request, response);
                break;
            case "cierraSesion":
                this.doGetCierraSesion(request, response);
                break;
            case "bienvenida":
            default:
                this.doGetBienvenida(request, response);
        }
    }

    /**
     * Se ejecuta cuando se recibe una solicitud POST y dirige el flujo de control según el parámetro 'opcion'.
     *
     * @param request  La solicitud HTTP recibida.
     * @param response La respuesta HTTP que se enviará.
     * @throws ServletException Si ocurre un error de servlet.
     * @throws IOException      Si ocurre un error de E/S.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (this.necesitaLogin(request)) {
            this.loginRedirect(request, response);
            return;
        }

        this.estableceAtributosComunes(request);
        switch (request.getParameter("opcion")) {
            case "registro":
                this.doPostRegistro(request, response);
                break;
            case "actualiza":
                this.doPostActualiza(request, response);
                break;
            case "login":
                this.doPostLogin(request, response);
                break;
            default:
                this.doPostDefault(request, response);
        }

    }

    /**
     * Comprueba si se necesita iniciar sesión para acceder a una página.
     *
     * @param request La solicitud HTTP recibida.
     * @return true si se necesita iniciar sesión, de lo contrario false.
     */
    private Boolean necesitaLogin(HttpServletRequest request) {
        String opcion = request.getParameter("opcion");
        Boolean logueado = this.estaLogueado(request);
        return !logueado && (opcion == null || !opcion.equals("login"));
    }

    /**
     * Método que establece los atributos comunes utilizados en las solicitudes.
     *
     * @param request La solicitud HTTP.
     */
    private void estableceAtributosComunes(HttpServletRequest request) {
        String opcion = request.getParameter("opcion");
        Boolean logueado = this.estaLogueado(request);
        request.setAttribute("paginaActual", opcion);
        request.setAttribute("logueado", logueado);
    }

    /**
     * Método que redirige a la página de inicio de sesión si es necesario.
     *
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    private void loginRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/index?opcion=login");
    }

    /**
     * Método que maneja la solicitud GET para la página de bienvenida.
     *
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws IOException      Si ocurre un error de entrada/salida.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     */
    private void doGetBienvenida(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
        request.setAttribute("opcion", "views/content/bienvenida.jsp");
        requestDispatcher.forward(request, response);
    }
    private void doGetCierraSesion(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession sesion = request.getSession();
        sesion.invalidate();
        this.loginRedirect(request, response);
    }

    /**
     * Método que maneja la solicitud GET para la página de registro.
     *
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws IOException      Si ocurre un error de entrada/salida.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     */
    private void doGetRegistro(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
        request.setAttribute("contenido", "/views/content/registro.jsp");
        requestDispatcher.forward(request, response);
    }

    /**
     * Método que maneja la solicitud GET para la lista de empleados.
     *
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws IOException      Si ocurre un error de entrada/salida.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     */
    private void doGetListado(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
        EmpleadoDao empleadoDao = new EmpleadoDao();
        try {
            List<Empleado> listaEmpleados = empleadoDao.obtenerListaEmpleados(null, null);
            Boolean creacion = request.getParameter("confirmar-creacion") != null;
            request.setAttribute("usuarioCreado", creacion);
            request.setAttribute("listaEmpleados", listaEmpleados);
            request.setAttribute("contenido", "/views/content/listado-empleados.jsp");

        } catch (SQLException e) {
            this.manejaException(e, request, response);
        } catch (CierreRecursosException e) {
            this.manejaException(e, request, response);
        } finally {
            requestDispatcher.forward(request, response);
        }
    }

    /**
     * Método que maneja la solicitud GET para la consulta de empleados.
     *
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws IOException      Si ocurre un error de entrada/salida.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     */
    private void doGetConsulta(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String dni = request.getParameter("dni");
        SalarioDao salarioDao = new SalarioDao();
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
        try {
            if (dni == null || dni == "") {
                request.setAttribute("contenido", "views/buscador/buscador.jsp");
            } else {
                request.setAttribute("dni", dni);
                Double salario = salarioDao.muestraSalarioPorDni(dni);
                request.setAttribute("salario", salario);
                request.setAttribute("contenido", "views/content/consulta-salario.jsp");
            }
        } catch (CierreRecursosException e) {
            this.manejaException(e, request, response);
        } catch (SQLException e) {
            this.manejaException(e, request, response);
        } catch (EmpleadoNoEncontradoException e) {
            this.manejaException(e, request, response);
        } catch (RuntimeException e) {
            this.manejaException(e, request, response);
        } finally {
            requestDispatcher.forward(request, response);
        }
    }

    /**
     * Método que maneja la solicitud GET para la actualización de empleados.
     *
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws IOException      Si ocurre un error de entrada/salida.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     */
    private void doGetActualiza(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String editar = request.getParameter("editar");
        String eliminar = request.getParameter("eliminar");
        if (editar != null) {
            this.redireccionaActualiza(editar, request, response);
        } else if (eliminar != null) {
            this.redireccionaEliminar(eliminar, request, response);
        } else {
            this.redireccionaListado(request, response);
        }
    }

    /**
     * Método que maneja la solicitud GET para la página de inicio de sesión.
     *
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws IOException      Si ocurre un error de entrada/salida.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     */
    private void doGetLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
        request.setAttribute("contenido", "/views/content/login.jsp");
        requestDispatcher.forward(request, response);
    }

    /**
     * Método que maneja la solicitud POST para la página por defecto.
     *
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    private void doPostDefault(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "index?opcion=bienvenida");
    }

    /**
     * Método que maneja la solicitud POST para el registro de empleados.
     *
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    private void doPostRegistro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombreRegistro = request.getParameter("nombre");
        String dniRegistro = request.getParameter("dni");
        Character sexoRegistro = request.getParameter("sexo").charAt(0);
        int categoriaRegistro = Integer.parseInt(request.getParameter("categoria"));
        double anyosRegistro = Double.parseDouble(request.getParameter("anyos"));
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
        boolean empleadoRegistrado = false;
        boolean salarioRegistrado = false;
        try {
            Empleado empl = new Empleado(nombreRegistro, dniRegistro, sexoRegistro, categoriaRegistro, anyosRegistro);

            EmpleadoDao empleadoDao = new EmpleadoDao();
            empleadoRegistrado = empleadoDao.insertarEmpleado(empl);

            SalarioDao salarioDao = new SalarioDao();
            salarioRegistrado = salarioDao.insertarSalario(empl);

        } catch (DatosNoCorrectosException e) {
            this.manejaException(e, request, response);
        } catch (SQLException e) {
            this.manejaException(e, request, response);
        } catch (CierreRecursosException e) {
            this.manejaException(e, request, response);
        } catch (Exception e) {
            this.manejaException(e, request, response);
        } finally {
            if (empleadoRegistrado && salarioRegistrado) {
                response.sendRedirect(request.getContextPath() + "/index?opcion=listado&confirmar-creacion=1");
            } else {
                response.getWriter().println("Error al registrar el empleado en la base de datos");
                requestDispatcher.forward(request, response);
            }
        }
    }

    /**
     * Método que maneja la solicitud POST para la actualización de empleados.
     *
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    private void doPostActualiza(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dniOriginal = request.getParameter("editar");
        String nombre = request.getParameter("nombre");
        String dni = request.getParameter("dni");
        String sexo = request.getParameter("sexo");
        String categoria = request.getParameter("categoria");
        String anyos = request.getParameter("anyos");

        EmpleadoDao empleadoDao = new EmpleadoDao();
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
        boolean empleadoActualizado = false;
        try {
            Empleado empl = empleadoDao.obtenerEmpleadoPorDni(dniOriginal);

            empl.setNombre(nombre);
            empl.setSexo(sexo.charAt(0));
            empl.setCategoria(Integer.parseInt(categoria));
            empl.setAnyos(Double.parseDouble(anyos));
            empl.setDni(dni);

            empleadoActualizado = empleadoDao.actualizarEmpleado(empl);

        } catch (SQLException e) {
            this.manejaException(e, request, response);
        } catch (CierreRecursosException e) {
            this.manejaException(e, request, response);
        } catch (DatosNoCorrectosException e) {
            this.manejaException(e, request, response);
        } catch (NumberFormatException e) {
            this.manejaException(e, request, response);
        } catch (IllegalArgumentException e) {
            this.manejaException(e, request, response);
        } finally {
            if (empleadoActualizado) {
                response.sendRedirect(request.getContextPath() + "/index?opcion=actualiza&confirmar-actualizacion=1");
            } else {
                requestDispatcher.forward(request, response);
            }

        }
    }

    /**
     * Método que maneja la solicitud POST para la página de inicio de sesión.
     *
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    private void doPostLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession();
        Boolean logueado = false;
        try {
            String dniLogin = request.getParameter("dni");
            String email = request.getParameter("email");
            String contrasenya = request.getParameter("contrasenya");
            AdministradorDao administradorDao = new AdministradorDao();
            Administrador admin = new Administrador(dniLogin, email, contrasenya);
            logueado = administradorDao.accederSesion(admin);
        } catch (SQLException e) {
        } catch (CierreRecursosException e) {
        } finally {
            if (logueado) {
                sesion.setMaxInactiveInterval(900);
                Date expiracion = new Date(new Date().getTime() + sesion.getMaxInactiveInterval() * 1000);
                sesion.setAttribute("expiracion", expiracion.toGMTString());
            } else {
                sesion.invalidate();
            }
        }
        response.sendRedirect(request.getContextPath() + "/index?opcion=bienvenida");
    }

    /**
     * Método que maneja una excepción de tipo SQLException.
     *
     * @param e        La excepción de tipo SQLException a manejar.
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    protected void manejaException(SQLException e, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("mensajeError", "El DNI que intentas registrar ya se encuentra en la base de datos.");
        request.setAttribute("contenido", "/views/exception/error.jsp");
    }

    /**
     * Método que maneja una excepción de tipo CierreRecursosException.
     *
     * @param e        La excepción de tipo CierreRecursosException a manejar.
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    private void manejaException(CierreRecursosException e, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("mensajeError", "Error al cerrar los recursos utilizados en la base de datos. " + e.getMessage());
        request.setAttribute("contenido", "/views/exception/error.jsp");
    }

    /**
     * Método que maneja una excepción de tipo DatosNoCorrectosException.
     *
     * @param e        La excepción de tipo DatosNoCorrectosException a manejar.
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    private void manejaException(DatosNoCorrectosException e, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("mensajeError", "Los datos que estás queriendo introducir no son correcto. " + e.getMessage());
        request.setAttribute("contenido", "/views/exception/error.jsp");
    }

    /**
     * Método que maneja una excepción genérica de tipo Exception.
     *
     * @param e        La excepción genérica de tipo Exception a manejar.
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    private void manejaException(Exception e, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("mensajeError", "Ha ocurrido un error inesperado. " + e.getMessage());
        request.setAttribute("contenido", "/views/exception/error.jsp");
    }

    /**
     * Método que maneja una excepción de tipo EmpleadoNoEncontradoException.
     *
     * @param e        La excepción de tipo EmpleadoNoEncontradoException a manejar.
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    private void manejaException(EmpleadoNoEncontradoException e, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("mensajeError", "Error al encontrar al empleado. " + e.getMessage());
        request.setAttribute("contenido", "/views/exception/error.jsp");
    }

    /**
     * Redirige a la página de listado de empleados, realizando consultas según los parámetros proporcionados.
     *
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    private void redireccionaListado(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EmpleadoDao empleadoDao = new EmpleadoDao();
        String campo = request.getParameter("campo");
        String valor = request.getParameter("valor");
        String actualizado = request.getParameter("confirmar-actualizacion");
        String eliminado = request.getParameter("confirmar-eliminacion");
        List<Empleado> listaEmpleados = null;
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
        request.setAttribute("contenido", "views/content/listado-actualizacion-empleados.jsp");
        request.setAttribute("actualizado", actualizado != null);
        request.setAttribute("eliminado", eliminado != null);
        request.setAttribute("campo", campo != null ? campo : "");
        request.setAttribute("valor", valor != null ? valor : "");
        try {
            listaEmpleados = empleadoDao.obtenerListaEmpleados(campo, valor);
            request.setAttribute("listaEmpleados", listaEmpleados);

        } catch (SQLException e) {
            this.manejaException(e, request, response);
        } catch (CierreRecursosException e) {
            this.manejaException(e, request, response);
        } finally {
            requestDispatcher.forward(request, response);
        }
    }

    /**
     * Redirige a la eliminación de un empleado específico según el DNI proporcionado.
     *
     * @param dni      El DNI del empleado a eliminar.
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    private void redireccionaEliminar(String dni, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EmpleadoDao empleadoDao = new EmpleadoDao();
        boolean eliminado = false;
        try {
            eliminado = empleadoDao.eliminarEmpleado(dni);
        } catch (SQLException e) {
        } catch (CierreRecursosException e) {
        } finally {
            response.sendRedirect(request.getContextPath() + "/index?opcion=actualiza&confirmar-eliminacion=" + (eliminado ? "1" : "0"));
        }
    }

    /**
     * Redirige a la página de actualización de un empleado específico según el DNI proporcionado.
     *
     * @param dni      El DNI del empleado a actualizar.
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error durante la ejecución del servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    private void redireccionaActualiza(String dni, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EmpleadoDao empleadoDao = new EmpleadoDao();
        Empleado empleado = null;
        try {
            empleado = empleadoDao.obtenerEmpleadoPorDni(dni);
            request.setAttribute("editar", dni);
            request.setAttribute("empleado", empleado);
            request.setAttribute("contenido", "views/content/formulario-actualizar.jsp");
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
            requestDispatcher.forward(request, response);
        } catch (SQLException e) {
            this.manejaException(e, request, response);
        } catch (CierreRecursosException e) {
            this.manejaException(e, request, response);
        }
    }

    /**
     * Comprueba si el usuario ha iniciado sesión.
     *
     * @param request La solicitud HTTP recibida.
     * @return true si el usuario ha iniciado sesión, de lo contrario false.
     */
    private Boolean estaLogueado(HttpServletRequest request) {
        HttpSession sesion = request.getSession();
        String expiracion = (String) sesion.getAttribute("expiracion");
        Date actual = new Date();
        return expiracion != null && actual.before(new Date(Date.parse(expiracion)));
    }

}