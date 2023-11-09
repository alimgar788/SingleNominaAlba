<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:import url="views/header/header.jsp"/>

<c:choose>
    <c:when test="${empty requestScope.contenido or requestScope.contenido == null}">
        <c:import url="views/content/bienvenida.jsp"/>
    </c:when>
    <c:otherwise>
        <c:import url="${requestScope.contenido}"/>
    </c:otherwise>
</c:choose>

<c:import url="views/footer/footer.jsp"/>