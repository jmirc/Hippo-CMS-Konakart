<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%--@elvariable id="currentCustomer" type="com.konakart.appif.CustomerIf"--%>
<%--@elvariable id="item" type="org.onehippo.forge.konakart.hst.vo.CartItem"--%>

<hst:defineObjects/>
<kk:activityActionURL var="billingLink"/>

<c:if test="${not empty form.message['globalmessage']}">
<div class="alert alert-error">
    ${form.message['globalmessage']}
</div>
</c:if>


<form id="billingFormID" action="${billingLink}" method="post" class="form-horizontal">
<fieldset>


<c:if test="${fn:length(addresses) > 0}">
    <div class="alert alert-info">
        Select a billing address from your address book or enter a new address.
    </div>

    <select id="select-address" name="address" class="input-xxlarge">
        <c:forEach items="${addresses}" var="address">
            <option value="${address.id}" <c:if test="${address.id == form.value['address'].value}">selected="selected"</c:if>>
                    ${address.firstName} ${address.lastName}, ${address.streetAddress}, ${address.city}, ${address.postcode} ${address.postcode}, ${address.countryName}
            </option>
        </c:forEach>
        <option value="-1" <c:if test="${-1 == form.value['address'].value}">selected="selected"</c:if>>New address</option>
    </select>
</c:if>

<c:if test="${fn:length(addresses) == 0}">
<br/>
<br/>

<div class="well" id="new-address-form1">
    <tag:checkoutAddress/>
</div>

<c:if test="${kk:hasCheckoutAsRegister(hstRequest)}">
    <p><strong>Enter a password that you can use to login next time you make an order:</strong></p>
    <div class="well">
        <div class="control-group">
            <label class="control-label required" for="input14"><fmt:message key="checkout.password"/></label>

            <div class="controls">
                <input type="password" class="input-xlarge highlight required" id="input14" name="password">
            </div>
        </div>
        <div class="control-group">
            <label class="control-label required" for="input15"><fmt:message key="checkout.passwordConfirmation"/></label>

            <div class="controls">
                <input type="password" class="input-xlarge highlight required" equalTo="#input14" id="input15" name="passwordConfirmation">
            </div>
        </div>
    </div>
</c:if>

</c:if>


<div >
    <label class="radio">
        <input type="radio" name="shippingAddress" id="optionsRadios1" value="same" checked="true">
        Ship to this address
    </label>
    <label class="radio">
        <input type="radio" name="shippingAddress" id="optionsRadios2" value="other">
        Ship to different address
    </label>
</div>

<br/>
<input type="submit" class="btn btn-success" value="Continue"/>

</fieldset>

</form>
