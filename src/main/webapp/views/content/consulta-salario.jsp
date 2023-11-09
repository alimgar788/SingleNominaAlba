<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="consulta-salario">
    <c:choose>
        <c:when test="${not empty salario}">
            <div>
                <h3>El salario correspondiente al empleado:</h3>
            </div>
            <div>
                <p>Con dni <strong>${dni}</strong> es de: <strong>${salario} &euro;</strong> al a&ntilde;o </p>
            </div>
        </c:when>
        <c:when test="${not empty error}">
            <div>
                <p>${error}</p>
            </div>
        </c:when>
        <c:otherwise>
            <div>
                <p>No se encuentra salario asociado a este dni</p>
            </div>
        </c:otherwise>
    </c:choose>
</div>