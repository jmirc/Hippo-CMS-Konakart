<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="checkoutOrder" type="com.konakart.appif.OrderIf"--%>

<hst:headContribution category="jsInternal">
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
</hst:headContribution>
<hst:headContribution category="jsInternal">
    <hst:link path="/js/checkout.js" var="checkoutJs"/>
    <script src="${checkoutJs}" type="text/javascript"></script>
</hst:headContribution>

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
                                    <h3>${count}. Checkout Method</h3>
                                </div>
                            </li>
                        </ul>
                        <c:if test="${state == 'INITIAL'}">
                            <tag:checkoutMethod/>
                        </c:if>
                    </c:if>


                    <ul class="breadcrumb">
                        <c:set var="count" value="${count + 1}" scope="page"/>
                        <li class="active">
                            <h3>${count}. Billing Address</h3>

                            <c:if test="${BILLING_ADDRESS_EDIT}">
                                <hst:actionURL var="link">
                                    <hst:param name="action" value="EDIT"/>
                                    <hst:param name="state" value="INITIAL"/>
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
                            <h3>${count}. Shipping Address</h3>

                            <c:if test="${SHIPPING_ADDRESS_EDIT}">
                                <hst:actionURL var="link">
                                    <hst:param name="action" value="INITIAL"/>
                                    <hst:param name="state" value="${state}"/>
                                </hst:actionURL>
                                <a href="${link}" class="pull-right">edit</a>
                            </c:if>

                        </li>
                    </ul>
                    <c:if test="${state == 'SHIPPING_ADDRESS'}">
                        <tag:checkoutBillingAddress/>
                    </c:if>

                    <ul class="breadcrumb">
                        <c:set var="count" value="${count + 1}" scope="page"/>
                        <li class="active"><h3>${count}. Shipping Method</h3></li>
                    </ul>
                    <ul class="breadcrumb">
                        <c:set var="count" value="${count + 1}" scope="page"/>
                        <li class="active"><h3>${count}. Payment Method</h3></li>
                    </ul>
                    <ul class="breadcrumb">
                        <c:set var="count" value="${count + 1}" scope="page"/>
                        <li class="active"><h3>${count}. Order Review</h3></li>
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
                <li class="active"><a href="#"><b>Shipping Method</b></a></li>
                <li class="active"><a href="#"><b>Payment Method</b></a></li>
                <li class="active"><a href="#"><b>Order Review</b></a></li>
            </ul>
        </div>
    </div>


</div>

