<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="error">
    <div>
        <h3>Oh vaya! Ha ocurrido un error</h3>
    </div>
    <c:if test="${not empty mensajeError}">
        <div>
            <p>${mensajeError}</p>
        </div>
    </c:if>

    <c:set var="mensajeError">
        <c:choose>
            <c:when test="${pageContext.response.status == 404}">
                No se encontró el recurso solicitado. Vuelve a la página anterior e inténtalo de nuevo.
            </c:when>
            <c:when test="${pageContext.response.status == 500}">
                Se produjo un error en el servidor. Inténtalo de nuevo más tarde.
            </c:when>
            <c:otherwise>
                Ha ocurrido un error inesperado. Vuelve a la página anterior e inténtalo de nuevo.
            </c:otherwise>
        </c:choose>
    </c:set>
</div>