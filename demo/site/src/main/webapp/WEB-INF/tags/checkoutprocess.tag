<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="hst" uri="http://www.hippoecm.org/jsp/hst/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="kk" uri="http://www.onehippo.org/jsp/konakart" %>
<%--@elvariable id="currentCustomer" type="com.konakart.appif.CustomerIf"--%>


<hst:link var="cartLink" siteMapItemRefId="cartDetailId"/>
<hst:link var="cartImage" path="/images/cart_16x16.png"/>


<h4>YOUR CHECKOUT PROCESS</h4>
<br/>
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
                            <c:when test="${displayPriceWithTax}">
                                <kk:formatPrice price="${checkoutOrder.shippingQuote.totalIncTax}"/>
                            </c:when>
                            <c:otherwise>
                                <kk:formatPrice price="${checkoutOrder.shippingQuote.totalExTax}"/>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </p>
            </c:if>
        </a>
    </li>
    <li class="active">
        <a href="#">
            <b>Payment Method</b>
            <c:if test="${PAYMENT_METHOD_EDIT}">
                <br/><br/>
                <p>
                    <c:if test="${not empty checkoutOrder}">
                        ${checkoutOrder.paymentMethod}
                    </c:if>
                </p>
            </c:if>
        </a>
    </li>
</ul>

