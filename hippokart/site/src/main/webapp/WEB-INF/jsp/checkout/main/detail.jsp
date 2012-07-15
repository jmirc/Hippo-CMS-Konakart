<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="checkoutOrder" type="com.konakart.appif.OrderIf"--%>

<hst:defineObjects/>

<hst:headContribution category="css">
    <hst:link path="/libs/datepicker/css/datepicker.css" var="datepickerCss"/>
    <link rel="stylesheet" href="${datepickerCss}" type="text/css"/>
</hst:headContribution>

<hst:headContribution category="jsInternal">
    <hst:link path="/libs/datepicker/js/bootstrap-datepicker.js" var="datepickerJs"/>
    <script src="${datepickerJs}" type="text/javascript"></script>
</hst:headContribution>

<hst:headContribution category="jsInternal">
    <hst:link path="/js/checkout.js" var="checkoutJs"/>
    <script src="${checkoutJs}" type="text/javascript"></script>
</hst:headContribution>

<hst:link var="countryDropDown" path="/restservices/checkout/states"/>

<script type="text/javascript">

    $(document).ready(function () {
        $('#CountryDropDown').change(function () {
            $.getJSON("${countryDropDown}/" + $(this).val(), "", function (data) {
                var list = $('#StateDropDown');
                list.empty('option')
                        .append($('<option />').attr('selected', 'true').text('---').val('-1'));

                if (data.length > 0) {
                    $('#StateDropDown').addClass('highlight');
                    $('#StateDropDownLabel').addClass('highlight');
                    $("#StateDropDown").rules("add", "required");
                } else {
                    $('#StateDropDown').removeClass('highlight');
                    $('#StateDropDownLabel').removeClass('highlight');
                    $("#StateDropDown").rules("remove", "required");
                }

                $.each(data, function (index, itemData) {
                    list.append($('<option />').text(itemData.name).val(itemData.name));
                });
            });
        });
    });
</script>

<c:set var="count" value="0" scope="page"/>
<c:if test="${not isLogged}">
    <ul class="breadcrumb">
        <li class="active">
            <c:set var="count" value="${count + 1}" scope="page"/>
            <c:choose>
                <c:when test="${kk:hasCheckoutAsGuest(hstRequest) || kk:hasCheckoutAsRegister(hstRequest)}">
                    <hst:actionURL var="link">
                        <hst:param name="action" value="EDIT"/>
                        <hst:param name="state" value="INITIAL"/>
                    </hst:actionURL>
                    <a href="${link}"><h3>Step ${count}. <fmt:message key="checkout.step.checkoutmethod"/></h3></a>
                </c:when>
                <c:otherwise>
                    <h3>Step ${count}. <fmt:message key="checkout.step.checkoutmethod"/></h3>
                </c:otherwise>
            </c:choose>
        </li>
    </ul>
    <c:if test="${state == 'INITIAL'}">
        <tag:checkoutMethodRegister/>
    </c:if>
</c:if>


<ul class="breadcrumb">
    <c:set var="count" value="${count + 1}" scope="page"/>
    <li class="active">
        <c:choose>
            <c:when test="${BILLING_ADDRESS_EDIT}">
                <hst:actionURL var="link">
                    <hst:param name="action" value="EDIT"/>
                    <hst:param name="state" value="BILLING_ADDRESS"/>
                </hst:actionURL>
                <a href="${link}"><h3>Step ${count}. <fmt:message key="checkout.step.billingaddress"/></h3></a>
            </c:when>
            <c:otherwise>
                <h3>Step ${count}. <fmt:message key="checkout.step.billingaddress"/></h3>
            </c:otherwise>
        </c:choose>
    </li>
</ul>
<c:if test="${state == 'BILLING_ADDRESS'}">
    <tag:checkoutBillingAddress/>
</c:if>

<ul class="breadcrumb">
    <c:set var="count" value="${count + 1}" scope="page"/>
    <li class="active">
        <c:choose>
            <c:when test="${SHIPPING_ADDRESS_EDIT}">
                <hst:actionURL var="link">
                    <hst:param name="action" value="EDIT"/>
                    <hst:param name="state" value="SHIPPING_ADDRESS"/>
                </hst:actionURL>
                <a href="${link}"><h3>Step ${count}. <fmt:message key="checkout.step.shippingaddress"/></h3></a>
            </c:when>
            <c:otherwise>
                <h3>Step ${count}. <fmt:message key="checkout.step.shippingaddress"/></h3>
            </c:otherwise>
        </c:choose>
    </li>
</ul>
<c:if test="${state == 'SHIPPING_ADDRESS'}">
    <tag:checkoutShippingAddress/>
</c:if>

<ul class="breadcrumb">
    <c:set var="count" value="${count + 1}" scope="page"/>
    <li class="active">
        <c:choose>
            <c:when test="${SHIPPING_METHOD_EDIT}">
                <hst:actionURL var="link">
                    <hst:param name="action" value="EDIT"/>
                    <hst:param name="state" value="SHIPPING_METHOD"/>
                </hst:actionURL>
                <a href="${link}"><h3>Step ${count}. <fmt:message key="checkout.step.shippingmethod"/></h3></a>
            </c:when>
            <c:otherwise>
                <h3>Step ${count}. <fmt:message key="checkout.step.shippingmethod"/></h3>
            </c:otherwise>
        </c:choose>
    </li>
</ul>
<c:if test="${state == 'SHIPPING_METHOD'}">
    <tag:checkoutShippingMethod/>
</c:if>

<ul class="breadcrumb">
    <c:set var="count" value="${count + 1}" scope="page"/>
    <li class="active">
        <c:choose>
            <c:when test="${PAYMENT_METHOD_EDIT}">
                <hst:actionURL var="link">
                    <hst:param name="action" value="EDIT"/>
                    <hst:param name="state" value="PAYMENT_METHOD"/>
                </hst:actionURL>
                <a href="${link}"><h3>Step ${count}. <fmt:message key="checkout.step.paymentmethod"/></h3></a>
            </c:when>
            <c:otherwise>
                <h3>Step ${count}. <fmt:message key="checkout.step.paymentmethod"/></h3>
            </c:otherwise>
        </c:choose>
    </li>
</ul>
<c:if test="${state == 'PAYMENT_METHOD'}">
    <tag:checkoutPaymentMethod/>
</c:if>


<ul class="breadcrumb">
    <c:set var="count" value="${count + 1}" scope="page"/>
    <li class="active"><h3>Step ${count}. <fmt:message key="checkout.step.orderreview"/></h3></li>
</ul>
<c:if test="${state == 'ORDER_REVIEW'}">
    <tag:checkoutOrderReview/>
</c:if>


