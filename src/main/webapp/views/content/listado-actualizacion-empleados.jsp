<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<h2>Actualizaci&oacute;n de empleados</h2>
<c:if test="${actualizado}">
    <div class="info">El empleado ha sido actualizado correctamente</div>
</c:if>
<c:if test="${eliminado}">
    <div class="info">El empleado ha sido eliminado correctamente</div>
</c:if>
<div class="formulario horizontal">
    <form id="formulario-filtro-busqueda-empleado" action="index" method="get">
        <input type="hidden" name="opcion" value="actualiza">
        <div class="form_group">
            <select class="form_field" name="campo">
                <option value="nombre" ${campo == "nombre" ? "selected" : ""}>Nombre</option>
                <option value="dni" ${campo == "dni" ? "selected" : ""}>DNI</option>
                <option value="sexo" ${campo == "sexo" ? "selected" : ""}>Sexo</option>
                <option value="categoria" ${campo == "categoria" ? "selected" : ""}>Categor&iacutea</option>
                <option value="anyos" ${campo == "anyos" ? "selected" : ""}>Antig&uumledad</option>
                <option value="salario" ${campo == "salario" ? "selected" : ""}>Salario</option>
            </select>
            <label class="form_label">Buscar por:</label>
        </div>
        <div class="form_group">
            <input class="form_field" type="text" name="valor" value="${valor}" placeholder="Filtrar por el dato...">
            <label class="form_label">Valor de b&uacutesqueda:</label>
        </div>
        <div class="button_row">
            <button id="reset" onclick="resetearFormulario(event)">Resetear</button>
            <input type="submit" value="Buscar"/>
        </div>
    </form>
</div>

<div class="listado">
    <table class="tabla">
        <tr>
            <th>Nombre</th>
            <th>DNI</th>
            <th class="center">Sexo</th>
            <th class="center">Categor&iacutea</th>
            <th class="center">Antig&uumledad</th>
            <th class="actions center">Acciones</th>
        </tr>
        <c:forEach items="${listaEmpleados}" var="empleado">
            <tr>
                <td>${empleado.nombre}</td>
                <td>${empleado.dni}</td>
                <td class="center">${empleado.getSexoFormato()}</td>
                <td class="center">${empleado.categoria}</td>
                <td class="center">${empleado.anyos}</td>
                <td class="actions center">
                    <a href="index?opcion=actualiza&editar=${empleado.dni}">
                        <img src="/singleNominaAlba_war_exploded/views/img/editar.png" class="icono-modificar">
                    </a>
                    <a onclick="confirmarEliminacion(event)" href="index?opcion=actualiza&eliminar=${empleado.dni}">
                        <img src="/singleNominaAlba_war_exploded/views/img/papelera.png" class="icono-eliminar">
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>