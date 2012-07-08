<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%--@elvariable id="currentCustomer" type="com.konakart.appif.CustomerIf"--%>
<%--@elvariable id="item" type="org.onehippo.forge.konakart.hst.vo.CartItem"--%>


<hst:actionURL var="billing">
    <hst:param name="action" value="SELECT"/>
    <hst:param name="state" value="SHIPPING_ADDRESS"/>
</hst:actionURL>


<c:if test="${not empty form.message['globalmessage']}">
    <div class="alert alert-error">
            ${form.message['globalmessage']}
    </div>
</c:if>


<form id="shippingFormID" action="${billing}" method="post" class="form-horizontal">

    <div class="alert alert-info">
        Select a shipping address from your address book or enter a new address.
    </div>

    <c:if test="${fn:length(addresses) > 0}">

    <select id="select-address" name="address" class="input-xxlarge">
        <c:forEach items="${addresses}" var="address">
            <option value="${address.id}" <c:if test="${address.id == form.value['address'].value}">selected="selected"</c:if>>
                    ${address.firstName} ${address.lastName}, ${address.streetAddress}, ${address.city}, ${address.postcode} ${address.postcode}, ${address.countryName}
            </option>
        </c:forEach>
        <option value="-1" <c:if test="${-1 == form.value['address'].value}">selected="selected"</c:if>>New address</option>
    </select>
    </c:if>

    <br/>
    <br/>

    <div class="well" id="new-address-form">
        <tag:checkoutAddress/>
    </div>

    <br/>
    <input type="submit" class="btn btn-success" value="Continue"/>

</form>
