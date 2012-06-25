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


<div class="container">
    <div class="row">

        <c:set var="count" value="0" scope="page" />
        <div class="span9">
            <div class="row">
                <div class="span8">
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
                        <li class="active"><h3>${count}. <fmt:message key="checkout.step.paymentmethod"/></h3></li>
                    </ul>
                    <ul class="breadcrumb">
                        <c:set var="count" value="${count + 1}" scope="page"/>
                        <li class="active"><h3>${count}. <fmt:message key="checkout.step.orderreview"/></h3></li>
                    </ul>

                </div>
            </div>

        </div>
        <div class="span3">
            <c:if test="${isLogged}">
                <hst:link path="/login/logout" var="logout"/>
                Welcome - <c:out value="${currentCustomer.firstName}"/> | <a href="${logout}">Logout</a>
                <br/>
                <br/>
            </c:if>

            <tag:shoppingcart/>
            <br/>

            <h3>Your Checkout Progress</h3>
            <ul class="nav nav-tabs nav-stacked">
                <li class="active">
                    <a href="#">
                        <b>Billing Address</b>
                        <c:if test="${BILLING_ADDRESS_EDIT}">
                            <br/><br/>
                            <p>
                                <c:if test="${not empty checkoutOrder}">
                                    ${checkoutOrder.customerName} <br/>
                                    ${checkoutOrder.billingStreetAddress} <br/>
                                    ${checkoutOrder.billingCity}, ${checkoutOrder.billingState}, ${checkoutOrder.billingPostcode}<br/>
                                    ${checkoutOrder.billingCountry} <br/>
                                    <c:if test="${not empty checkoutOrder.billingTelephone}">T: ${checkoutOrder.billingTelephone}<br/></c:if>
                                </c:if>
                            </p>
                        </c:if>
                    </a>
                </li>
                <li class="active">
                    <a href="#">
                        <b>Shipping Address</b>
                        <c:if test="${SHIPPING_ADDRESS_EDIT}">
                            <br/><br/>
                            <p>
                                <c:if test="${not empty checkoutOrder}">
                                    ${checkoutOrder.customerName} <br/>
                                    ${checkoutOrder.deliveryStreetAddress} <br/>
                                    ${checkoutOrder.deliveryCity}, ${checkoutOrder.deliveryState}, ${checkoutOrder.deliveryPostcode}<br/>
                                    ${checkoutOrder.deliveryCountry} <br/>
                                    <c:if test="${not empty checkoutOrder.deliveryTelephone}">T: ${checkoutOrder.deliveryTelephone}<br/></c:if>
                                </c:if>
                            </p>
                        </c:if>
                    </a>
                </li>
                <li class="active">
                    <a href="#">
                        <b>Shipping Method</b>
                        <c:if test="${SHIPPING_METHOD_EDIT}">
                            <br/><br/>
                            <p>
                                <c:if test="${not empty checkoutOrder}">
                                    ${checkoutOrder.shippingQuote.title} - ${checkoutOrder.shippingQuote.responseText} -
                                    <c:choose>
                                        <c:when test="${displayPriceWithTax}"><kk:formatPrice price="${checkoutOrder.shippingQuote.totalIncTax}"/> </c:when>
                                        <c:when test="${!displayPriceWithTax}"><kk:formatPrice price="${checkoutOrder.shippingQuote.totalExTax}"/></c:when>
                                    </c:choose>
                                </c:if>
                            </p>
                        </c:if>
                    </a>
                </li>
                <li class="active"><a href="#"><b>Payment Method</b></a></li>
            </ul>
        </div>
    </div>


</div>

