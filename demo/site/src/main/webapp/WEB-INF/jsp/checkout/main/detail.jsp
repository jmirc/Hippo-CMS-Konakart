<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="checkoutOrder" type="com.konakart.appif.OrderIf"--%>

<hst:headContribution category="jsInternal">
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
</hst:headContribution>

<hst:headContribution category="jsInternal">
    <hst:link path="/js/checkout.js" var="checkoutJs"/>
    <script src="${checkoutJs}" type="text/javascript"></script>
</hst:headContribution>


<hst:link var="countryDropDown" path="/restservices/checkout/states"/>


<script type="text/javascript">
    $(document).ready(function() {
        $('#CountryDropDown').change(function() {
            $.getJSON("${countryDropDown}/" + $(this).val(), "", function(data) {
                var list = $('#StateDropDown');
                list.empty('option');
                list.append($('<option />').attr('selected', 'true').text('---').val('-1'));
                $.each(data, function(index, itemData) {
                    list.append($('<option />').text(itemData.name).val(itemData.name));
                });
            });
        });
    });
</script>

-${state}- <br/>
-${isLogged}- <br/>


        <c:set var="count" value="0" scope="page" />
<c:if test="${not isLogged}">
    <ul class="breadcrumb">
        <li class="active">
            <div >
                <c:set var="count" value="${count + 1}" scope="page"/>
                <h3>${count}. <fmt:message key="checkout.step.checkoutmethod"/> </h3>
            </div>
        </li>
    </ul>
    <c:if test="${state == 'INITIAL'}">
        <tag:checkoutMethodRegister/>
    </c:if>
</c:if>


<ul class="breadcrumb">
    <c:set var="count" value="${count + 1}" scope="page"/>
    <li class="active">
        <h3>${count}. <fmt:message key="checkout.step.billingaddress"/></h3>

        <c:if test="${BILLING_ADDRESS_EDIT}">
            <hst:actionURL var="link">
                <hst:param name="action" value="EDIT"/>
                <hst:param name="state" value="BILLING_ADDRESS"/>
            </hst:actionURL>
            <a href="${link}" class="pull-right">edit</a>
        </c:if>
    </li>
</ul>
<c:if test="${state == 'BILLING_ADDRESS'}">
    <tag:checkoutBillingAddress/>
</c:if>

<ul class="breadcrumb">
    <c:set var="count" value="${count + 1}" scope="page"/>
    <li class="active">
        <h3>${count}. <fmt:message key="checkout.step.shippingaddress"/></h3>

        <c:if test="${SHIPPING_ADDRESS_EDIT}">
            <hst:actionURL var="link">
                <hst:param name="action" value="EDIT"/>
                <hst:param name="state" value="SHIPPING_ADDRESS"/>
            </hst:actionURL>
            <a href="${link}" class="pull-right">edit</a>
        </c:if>

    </li>
</ul>
<c:if test="${state == 'SHIPPING_ADDRESS'}">
    <tag:checkoutShippingAddress/>
</c:if>

<ul class="breadcrumb">
    <c:set var="count" value="${count + 1}" scope="page"/>
    <li class="active">
        <h3>${count}. <fmt:message key="checkout.step.shippingmethod"/></h3>

        <c:if test="${SHIPPING_METHOD_EDIT}">
            <hst:actionURL var="link">
                <hst:param name="action" value="EDIT"/>
                <hst:param name="state" value="SHIPPING_METHOD"/>
            </hst:actionURL>
            <a href="${link}" class="pull-right">edit</a>
        </c:if>
    </li>
</ul>
<c:if test="${state == 'SHIPPING_METHOD'}">
    <tag:checkoutShippingMethod/>
</c:if>

<ul class="breadcrumb">
    <c:set var="count" value="${count + 1}" scope="page"/>
    <li class="active">
        <h3>${count}. <fmt:message key="checkout.step.paymentmethod"/></h3>

        <c:if test="${PAYMENT_METHOD_EDIT}">
            <hst:actionURL var="link">
                <hst:param name="action" value="EDIT"/>
                <hst:param name="state" value="PAYMENT_METHOD"/>
            </hst:actionURL>
            <a href="${link}" class="pull-right">edit</a>
        </c:if>
    </li>
</ul>
<c:if test="${state == 'PAYMENT_METHOD'}">
    <tag:checkoutPaymentMethod/>
</c:if>


<ul class="breadcrumb">
    <c:set var="count" value="${count + 1}" scope="page"/>
    <li class="active"><h3>${count}. <fmt:message key="checkout.step.orderreview"/></h3></li>

    <c:if test="${state == 'ORDER_REVIEW'}">
        <tag:checkoutOrderReview/>
    </c:if>
</ul>

