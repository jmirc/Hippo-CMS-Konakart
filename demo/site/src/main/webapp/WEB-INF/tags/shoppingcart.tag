<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="hst" uri="http://www.hippoecm.org/jsp/hst/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<hst:link var="cartLink" siteMapItemRefId="detailCartId"/>
<hst:link var="cartImage" path="/images/cart_16x16.png"/>

<br/><br/><a href="${cartLink}"><img src="${cartImage}"/></a> SHOPPING CART <br/>
<c:choose>
    <c:when test="${!empty currentCustomer.basketItems}">
        <c:forEach var="item" items="${currentCustomer.basketItems}">
            <c:out value="${item.quantity}"/>&nbsp;x&nbsp; <a href="${item.custom1}"><c:out
                value="${item.product.name}"/></a>
            <br/>
        </c:forEach>
        <br/>
        Total: <c:out value="${basketTotal}"/>
    </c:when>

    <c:otherwise>0 items</c:otherwise>

</c:choose>
