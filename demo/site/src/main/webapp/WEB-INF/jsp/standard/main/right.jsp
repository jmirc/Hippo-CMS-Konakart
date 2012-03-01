<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>


<c:choose>
    <c:when test="${not isLogged}">
        <hst:actionURL var="loginLink"/>

        <p>Login</p>
        <form action="${loginLink}" method="post">
            <input type="text" name="username" value=""/><br/>
            <input type="password" name="password" value=""/> <br/>
            <input type="submit" value="Log in"/>
        </form>
    </c:when>
    <c:otherwise>
        Welcome - <c:out value="${currentCustomer.firstName}"/>
    </c:otherwise>
</c:choose>

<!-- Add the shopping Cart -->
<tag:shoppingcart/>



