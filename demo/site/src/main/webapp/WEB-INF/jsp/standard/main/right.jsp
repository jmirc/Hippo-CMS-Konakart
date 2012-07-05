<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>


<div class="verticalSpace">
    <c:if test="${isLogged}">
        <hst:link path="/login/logout" var="logout"/>
        Welcome - <c:out value="${currentCustomer.firstName}"/> | <a href="${logout}">Logout</a>
    </c:if>
</div>


<!-- Add the shopping Cart -->
<c:if test="${empty checkoutOrder}">
    <tag:shoppingcart/>
</c:if>

<c:if test="${not empty checkoutOrder}">
    <tag:checkoutprocess/>
</c:if>

<!-- Add the wish list -->
<c:if test="${wishListEnabled}">
    <br/>
    <tag:wishlist/>
</c:if>