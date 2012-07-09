<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>


<h1><fmt:message key="login.sectionTitle"/></h1>

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
                    class="required email highlight span4" id="username" name="username" value="${username}"
                    type="text" placeholder="Email address">
            </div>
        </div>
    </div>
    <div class="control-group">
        <div class="controls">
            <div class="input-prepend">
                <span class="add-on"><i class="icon-lock"></i></span><input
                    class="span4 highlight" id="inputIcon2" name="password" type="password"
                    placeholder="Password">
                <span class="help-inline">${login.failed}</span>
            </div>
            <p class="help-block">* These fields are required.</p>
        </div>
    </div>
    <div>
        <%-- destination url when the login is successful --%>
        <c:if test="${not empty destination}">
            <input type="hidden" name="destination" value="${destination}"/>
        </c:if>
        <button class="btn btn-success" type="submit"><fmt:message key="login.form.submit"/></button>
    </div>
</form>


