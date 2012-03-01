<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="hst" uri="http://www.hippoecm.org/jsp/hst/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<c:if test="${isLogged}">

    <br/><br/>SHOPPING CART <br/>
    <c:if test="${!empty currentCustomer.basketItems}">
        <c:forEach var="item" items="${currentCustomer.basketItems}">
            <c:out value="${item.quantity}"/>&nbsp;x&nbsp; <a href="${item.custom1}"><c:out value="${item.product.name}"/></a>
            <br/>
        </c:forEach>
        <br/>
        Total: <c:out value="${basketTotal}"/>
    </c:if>


</c:if>
