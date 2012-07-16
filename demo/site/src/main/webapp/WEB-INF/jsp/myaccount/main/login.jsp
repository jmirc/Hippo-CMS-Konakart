<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>


<h1><fmt:message key="login.sectionTitle"/></h1>

<div class="row-fluid verticalSpace">
    <div class="span6">
        <br/><br/>
        <h5 class="verticalSpace">New Customer</h5>

        <div class="well register-new-customer" >
            <p>I am a new customer.</p>
            <p>
                By creating an account, you will be able to shop faster, be up to date on an orders status,
                and keep track of the orders you have previously made.
            </p>
            <div class="pull-right-register-button" >
                <hst:link siteMapItemRefId="register" var="registerUrl"/>

                <a href="${registerUrl}" class="btn btn-success" ><fmt:message key="new.customer.continue"/></a>
            </div>
            <br/>

        </div>
    </div>
    <div class="span6">
        <br/><br/>
        <h5 class="verticalSpace">Returning Customer</h5>

        <div class="well">

            <div class="verticalSpace">
            I am a returning registered customer. If you have forgotten your password,
            please click on the Password Forgotten link below and we'll send you a new one.
            </div>
            <p class="verticalSpace">
            <c:choose>
                <c:when test="${loginError}">
                    <div class="alert alert-error">
                        <fmt:message key="login.errors.invalidUserPass"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:if test="${needauth}">
                        <div class="alert alert-info">
                            <fmt:message key="login.warning.needToAuthenticate"/>
                        </div>
                    </c:if>
                </c:otherwise>
            </c:choose>
            </p>

            <hst:link var="loginLink" path="/login/proxy" />
            <form id="loginFormID" action="${loginLink}" method="post">
                <div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"><i class="icon-envelope"></i></span><input
                                class="required email highlight span6" id="username" name="username" value="${username}"
                                type="text" placeholder="Email address">
                        </div>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"><i class="icon-lock"></i></span><input
                                class="span6 highlight" id="inputIcon2" name="password" type="password"
                                placeholder="Password">
                            <span class="help-inline">${login.failed}</span>
                        </div>
                        <p class="help-block">* These fields are required.</p>
                    </div>
                </div>
                <div class="pull-right-sign-button">
                    <%-- destination url when the login is successful --%>
                    <c:if test="${not empty destination}">
                        <input type="hidden" name="destination" value="${destination}"/>
                    </c:if>
                    <button class="btn btn-success" type="submit"><fmt:message key="login.form.submit"/></button>
                </div>
                <br/>
            </form>
        </div>
    </div>
</div>

