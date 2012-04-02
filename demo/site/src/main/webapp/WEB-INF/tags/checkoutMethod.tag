<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="hst" uri="http://www.hippoecm.org/jsp/hst/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--@elvariable id="currentCustomer" type="com.konakart.appif.CustomerIf"--%>
<%--@elvariable id="item" type="org.onehippo.forge.konakart.hst.vo.CartItem"--%>

<p>

<h3>Register to Create an Account</h3></p>

<div class="alert alert-info">
    Register and save time!
    Register with us and save time. Fast and easy checkout, access to order history and quick order status.
</div>

<hst:actionURL var="register">
    <hst:param name="action" value="CHECKOUT_METHOD_REGISTER"/>
    <hst:param name="state" value="${state}"/>
</hst:actionURL>
<a href="${register}" class="btn btn-success">Continue</a>

<br/>
<br/>

<p>


<h3>Registered Users</h3></p>

<hst:actionURL var="loginLink"/>

<p>If you have an account with us, login below.</p>

<form action="${loginLink}" method="post">
    <input type="hidden" name="action" value="LOGIN"/>
    <input type="hidden" name="state" value="${state}"/>

    <div class="control-group">
        <div class="controls">
            <div class="input-prepend">
                <span class="add-on"><i class="icon-envelope"></i></span>
                <input class="span2" id="inputIcon" name="email" type="text"
                       placeholder="Email address" value="${form.value['email'].value}">
            </div>
        </div>
    </div>
    <div class="control-group  <c:if test="${not empty form.message['email']}">error</c:if>">
        <div class="controls">
            <div class="input-prepend">
                <span class="add-on"></span>
                <input class="span2" id="inputIcon2" name="password" type="password"
                       placeholder="Password">
                <span class="help-inline">${form.message['email']}</span>
            </div>
        </div>
    </div>



    <input type="submit" class="btn btn-success" value="Login"/>
</form>

