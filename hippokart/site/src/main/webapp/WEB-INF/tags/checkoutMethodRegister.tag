<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%--@elvariable id="currentCustomer" type="com.konakart.appif.CustomerIf"--%>
<%--@elvariable id="item" type="org.onehippo.forge.konakart.hst.vo.CartItem"--%>

<div class="row-fluid">
    <div class="span5">

        <kk:activityActionURL action="LOGIN" var="loginLink"/>

        <h4>Registered Customers</h4></p>
        <p>If you have an account with us, please log in.</p>

        <hst:link var="loginLink" path="/login/proxy" />
        <form id="loginFormID" action="${loginLink}" method="post">
            <div class="control-group">
                <label for="username" class="control-label">Username</label>
                <div class="controls">
                    <input class="required email highlight input-large" id="username" name="username" value="${username}"
                           type="text" placeholder="Email address">
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">Password</label>
                <div class="controls">
                    <input class="required highlight input-large" id="inputIcon2"
                           name="password" type="password"
                           placeholder="Password">

                    <span class="help-inline">${login.failed}</span>
                </div>
            </div>

            <%-- destination url when the login is successful --%>
            <hst:link var="checkoutLink"/>
            <input type="hidden" name="destination" value="${checkoutLink}"/>
            <button class="btn btn-primary pull-right-signin-button" type="submit"><fmt:message key="login.form.submit"/></button>
        </form>
    </div>

    <div class="span6 ">
        <p>
            <c:choose>
                <c:when test="${allowCheckoutWithoutRegistration}">
                    <h4>New Customer</h4>
                    <p>By creating an account you will be able to shop faster, be up to date on an order's status, and keep track of the orders you have previously made.</p>
                </c:when>
                <c:otherwise>
                    <h3>Please Register</h3>
                </c:otherwise>
            </c:choose>
        </p>

        <kk:activityActionURL action="REGISTER" var="checkoutMethodRegisterLink"/>

        <form action="${checkoutMethodRegisterLink}" method="post">
            <div class="control-group verticalSpace">
                <c:if test="${allowCheckoutWithoutRegistration}">
                    <div class="controls">
                        <div class="input-prepend">
                            <label class="radio">
                                <input type="radio" name="dontHaveAccount" class="input-mini" value="checkoutAsGuest"
                                       checked="checked">
                                Checkout as Guest
                            </label>
                        </div>
                    </div>
                </c:if>
                <div class="controls">
                    <div class="input-prepend">
                        <label class="radio">
                            <input type="radio" name="dontHaveAccount" class="input-mini" value="checkoutAskRegister"
                                <c:if test="${not allowCheckoutWithoutRegistration}">checked="checked" </c:if>
                                    >
                            Register
                        </label>
                    </div>
                </div>
            </div>



            <button type="submit" class="btn btn-primary pull-right">Continue</button>
        </form>
    </div>

</div>







