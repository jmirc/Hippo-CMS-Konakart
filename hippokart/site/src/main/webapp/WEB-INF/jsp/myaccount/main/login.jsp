<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>


<hst:headContribution category="css">
    <hst:link path="/libs/datepicker/css/datepicker.css" var="datepickerCss"/>
    <link rel="stylesheet" href="${datepickerCss}" type="text/css"/>
</hst:headContribution>

<hst:headContribution category="scripts">
    <hst:link path="/libs/datepicker/js/bootstrap-datepicker.js" var="datepickerJs"/>
    <script src="${datepickerJs}" type="text/javascript"></script>
</hst:headContribution>

<hst:headContribution category="scripts">
    <hst:link path="/js/myaccount.js" var="myaccountJs"/>
    <script src="${myaccountJs}" type="text/javascript"></script>
</hst:headContribution>


<ul class="breadcrumb">
    <li><a href="<hst:link path="/"/>">Home</a> <span class="divider">/</span></li>
    <li><a href="<hst:link siteMapItemRefId="myaccount"/>">My Account</a> <span class="divider">/</span></li>
    <li class="active">Login</li>
</ul>

<div class="row">
    <div class="span9">
        <h1>Account login</h1>
    </div>
</div>

<hr/>

<div class="row">

    <div class="span5 well">
        <h2>New Customers</h2>

        <p>By creating an account with our store, you will be able to move through the checkout process faster, store
            multiple shipping addresses, view and track your orders in your account and more.</p><br/>

        <hst:actionURL var="register">
            <hst:param name="action" value="REGISTER"/>
        </hst:actionURL>
        <a href="${register}" class="btn btn-primary pull-right">Create an account</a>
    </div>

    <div class="span5 well pull-right">
        <h2>Registered Customers</h2>

        <p>If you have an account with us, please log in.</p>

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


        <hst:link var="myaccountLink" fullyQualified="true"/>
        <hst:link var="loginLink" path="/j_spring_security_check"/>
        <form id="loginFormID" action="${loginLink}?spring-security-redirect=${myaccountLink}" method="post">
            <div class="control-group">
                <label for="username" class="control-label">Username</label>

                <div class="controls">
                    <input class="required email highlight input-large" id="username" name="j_username"
                           value="${SPRING_SECURITY_LAST_USERNAME}"
                           type="text" placeholder="Email address">
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">Password</label>

                <div class="controls">
                    <input class="required highlight input-large" id="inputIcon2"
                           name="j_password" type="password"
                           placeholder="Password">

                    <span class="help-inline">${login.failed}</span>
                </div>
            </div>
            <div class="control-group">
                <div class="controls">
                    <label class="checkbox">
                        <input type="checkbox" id="optionsCheckbox" name="_spring_security_remember_me" value="true">
                        Remember me
                    </label>
                </div>
            </div>
            <button class="btn btn-primary pull-right" type="submit"><fmt:message key="login.form.submit"/></button>
        </form>
    </div>

</div>
