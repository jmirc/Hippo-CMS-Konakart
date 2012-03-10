<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>


<div class="row">
    <div class="span4">

        <c:choose>
            <c:when test="${not isLogged}">
                <hst:actionURL var="loginLink"/>

                <p>Login</p>

                <form action="${loginLink}" method="post">
                    <div class="control-group">
                        <div class="controls">
                            <div class="input-prepend">
                                <span class="add-on"><i class="icon-envelope"></i></span>
                                <input class="span2" id="inputIcon" name="username" type="text"
                                       placeholder="Email address">
                            </div>
                        </div>
                    </div>
                    <div class="control-group">
                        <div class="controls">
                            <div class="input-prepend">
                                <span class="add-on"></span>
                                <input class="span2" id="inputIcon2" name="password" type="password"
                                       placeholder="Password">
                            </div>
                        </div>
                    </div>


                    <input type="submit" value="Log in"/>
                </form>
            </c:when>
            <c:otherwise>
                <hst:link path="/login/logout" var="logout"/>
                Welcome - <c:out value="${currentCustomer.firstName}"/> | <a href="${logout}">Logout</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<br/>
<br/>
<!-- Add the shopping Cart -->
<tag:shoppingcart/>



