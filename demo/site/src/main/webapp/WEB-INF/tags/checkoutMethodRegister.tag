<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%--@elvariable id="currentCustomer" type="com.konakart.appif.CustomerIf"--%>
<%--@elvariable id="item" type="org.onehippo.forge.konakart.hst.vo.CartItem"--%>

<div class="row-fluid">

    <div class="span6 well">

        <kk:activityActionURL action="LOGIN" var="loginLink"/>

        <h3>I Have an Account</h3></p>

        <p class="verticalSpace">Good news: You can sign in with your e-mail address.</p>

        <hst:link var="loginLink" path="/login/proxy" />
        <form id="loginFormID" action="${loginLink}" method="post">
            <div class="control-group">
                <div class="controls">
                    <div class="input-prepend">
                        <span class="add-on"><i class="icon-envelope"></i></span><input
                            class="required email highlight span5" id="username" name="username" value="${username}"
                            type="text" placeholder="Email address">
                    </div>
                </div>
            </div>
            <div class="control-group">
                <div class="controls">
                    <div class="input-prepend">
                        <span class="add-on"><i class="icon-lock"></i></span><input
                            class="required highlight span5" id="inputIcon2"
                            name="password" type="password"
                            placeholder="Password">
                        <span class="help-inline">${login.failed}</span>
                    </div>
                    <p class="help-block">* These fields are required.</p>
                </div>
            </div>
            <div>
                <%-- destination url when the login is successful --%>
                <hst:link var="checkoutLink"/>
                <input type="hidden" name="destination" value="${checkoutLink}"/>
                <button class="btn btn-success" type="submit"><fmt:message key="login.form.submit"/></button>
            </div>
        </form>

    </div>


    <div class="span6 well">
        <p>
            <c:choose>
            <c:when test="${allowCheckoutWithoutRegistration}">

        <h3>Checkout as a guest or register</h3>
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

            <div class="alert alert-info">
                <p>
                    <strong>Register and save time!</strong><br/>
                    Register with us for future convenience.
                </p>

                <p>
                <ul>
                    <li>Fast and easy check out</li>
                    <li>Easy access to your order history and status</li>
                </ul>
                </p>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-success">Continue</button>
            </div>
        </form>


    </div>
</div>





