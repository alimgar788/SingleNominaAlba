<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<h2>Registro de nuevo empleado</h2>
<div class="formulario">
    <form id="formulario-registro" action="index?opcion=registro" method="post">
        <div class="form_group">
            <input class="form_field" type="text" id="nombre" name="nombre" placeholder="Ej: Alba Lima Garcia" required>
            <label class="form_label">Nombre:</label>
        </div>
        <div class="form_group">
            <input class="form_field" type="text" id="dni" name="dni" placeholder="Ej: 12345678A" required>
            <label class="form_label">DNI:</label>
        </div>
        <label>Sexo:</label>
        <div id="formulario-sexo">
            <label class="radio-button">
                <input type="radio" name="sexo" value="m" required>
                <span class="radio"></span>
                Masculino
            </label>
            <label class="radio-button">
                <input type="radio" name="sexo" value="f" required>
                <span class="radio"></span>
                Femenino
            </label>
            <label class="radio-button">
                <input type="radio" name="sexo" value="i" required>
                <span class="radio"></span>
                N/C
            </label>
        </div>
        <div class="form_group">
            <select class="form_field select" name="categoria" id="categoria">
                <c:forEach begin="1" end="9" var="i">
                    <option value="${i}">${i}</option>
                </c:forEach>
            </select>
            <label class="form_label">Categor&iacutea:</label>
        </div>
        <div class="form_group">
            <input class="form_field" type="number" step="0.1" id="anyos" name="anyos" min="0" required>
            <label class="form_label">Antig&uumledad</label>
        </div>
        <input type="submit" value="Registrar">
    </form>
</div>