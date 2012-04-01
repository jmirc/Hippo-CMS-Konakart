<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>


<div class="row">
    <div class="span4">

        <c:choose>
            <c:when test="${isLogged}">
                <hst:link path="/login/logout" var="logout"/>
                Welcome - <c:out value="${currentCustomer.firstName}"/> | <a href="${logout}">Logout</a>
            </c:when>
            <%--<c:otherwise>--%>
                <%--<hst:actionURL var="loginLink"/>--%>

                <%--<p>Login</p>--%>

                <%--<form action="${loginLink}" method="post">--%>
                    <%--<div class="control-group">--%>
                        <%--<div class="controls">--%>
                            <%--<div class="input-prepend">--%>
                                <%--<span class="add-on"><i class="icon-envelope"></i></span>--%>
                                <%--<input class="span2" id="inputIcon" name="username" type="text"--%>
                                       <%--placeholder="Email address">--%>
                            <%--</div>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <%--<div class="control-group">--%>
                        <%--<div class="controls">--%>
                            <%--<div class="input-prepend">--%>
                                <%--<span class="add-on"></span>--%>
                                <%--<input class="span2" id="inputIcon2" name="password" type="password"--%>
                                       <%--placeholder="Password">--%>
                            <%--</div>--%>
                        <%--</div>--%>
                    <%--</div>--%>


                    <%--<input type="submit" value="Log in"/>--%>
                <%--</form>--%>
            <%--</c:otherwise>--%>
        </c:choose>
    </div>
</div>
<br/>
<br/>
<!-- Add the shopping Cart -->
<tag:shoppingcart/>

<!-- Add the wish list -->
<c:if test="${wishListEnabled}">
   <br/>
    <tag:wishlist/>
</c:if>



