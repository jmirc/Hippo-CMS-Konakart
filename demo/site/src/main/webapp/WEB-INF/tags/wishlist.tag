<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="hst" uri="http://www.hippoecm.org/jsp/hst/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--@elvariable id="defaultWishList" type="com.konakart.appif.WishListIf"--%>



<hst:link var="cartLink" siteMapItemRefId="whishListId"/>
<hst:link var="cartImage" path="/images/cart_16x16.png"/>

<div class="thumbnail">
    <a href="${cartLink}"><i class="icon-list-alt"></i></a> WISH LIST <br/><br/>
    <c:choose>
        <c:when test="${!empty defaultWishList}">
            <c:forEach var="item" items="${defaultWishList.wishListItems}">
                <a href="${item.custom1}"><c:out value="${item.product.name}"/></a>
                <br/>
            </c:forEach>
            <br/>
            Total: <c:out value="${basketTotal}"/>
        </c:when>

        <c:otherwise>0 items</c:otherwise>

    </c:choose>
</div>
