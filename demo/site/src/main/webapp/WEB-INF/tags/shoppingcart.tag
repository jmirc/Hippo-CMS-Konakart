<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="hst" uri="http://www.hippoecm.org/jsp/hst/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="kk" uri="http://www.onehippo.org/jsp/konakart" %>
<%--@elvariable id="currentCustomer" type="com.konakart.appif.CustomerIf"--%>


<hst:link var="cartLink" siteMapItemRefId="cartDetailId"/>
<hst:link var="cartImage" path="/images/cart_16x16.png"/>

<div class="thumbnail">
    <a href="${cartLink}"><i class="icon-shopping-cart"></i></a> SHOPPING CART <br/><br/>
    <c:choose>
        <c:when test="${not empty currentCustomer.basketItems}">
            <c:forEach var="item" items="${currentCustomer.basketItems}">
                <c:out value="${item.quantity}"/>&nbsp;x&nbsp;
                    <kk:productLink productId="${item.productId}" var="productLink"/>
                    <a href="${productLink}"><c:out value="${item.product.name}"/></a>
                <br/>
            </c:forEach>
            <br/>
            Total: <c:out value="${basketTotal}"/>
        </c:when>

        <c:otherwise>0 items</c:otherwise>

    </c:choose>
</div>
