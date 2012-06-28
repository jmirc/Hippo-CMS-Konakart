<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="hst" uri="http://www.hippoecm.org/jsp/hst/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="kk" uri="http://www.onehippo.org/jsp/konakart" %>
<%--@elvariable id="paymentGateway" type="com.konakart.appif.PaymentDetailsIf"--%>


<hst:actionURL var="orderReviewLink">
    <hst:param name="action" value="SELECT"/>
    <hst:param name="state" value="ORDER_REVIEW"/>
</hst:actionURL>

<c:if test="${not empty form.message['globalmessage']}">
    <div class="alert alert-error">
            ${form.message['globalmessage']}
    </div>
</c:if>

<form action="${orderReviewLink}" method="post">
    <div class="alert alert-info">
        Please review your order before processing.
    </div>

    <table class="table table-striped table-condensed">
        <thead>
        <tr>
            <th align="center" class="productListing-heading">Product(s)</th>
            <th align="center" class="productListing-heading">Price</th>
            <th align="center" class="productListing-heading">Qty.</th>
            <th align="center" class="productListing-heading">Total</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${checkoutOrder.orderProducts}" varStatus="rowCounter">
            <tr class="productListing-even">
                <td class="productListing-data" align="center">
                    <b><c:out value="${item.name}"/></b>

                    <!-- display the konakart attributes if exists -->
                    <c:forEach var="option" items="${item.opts}">
                        <c:choose>
                            <c:when test="${option.type == 1}">
                                <br><small>&nbsp;<i> - ${option.name} : ${option.quantity} ${option.value} (<kk:formatPrice price="${option.priceExTax}"/>)</i></small>
                            </c:when>
                            <c:otherwise>
                                <br><small>&nbsp;<i> - ${option.name} : ${option.value} (<kk:formatPrice price="${option.priceExTax}"/>)</i></small>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </td>
                <td align="center" class="productListing-data" valign="top">
                    <kk:formatPrice price="${item.price}"/>
                </td>
                <td align="center" class="productListing-data" valign="top">
                    ${item.quantity}
                </td>
                <td align="center" class="productListing-data" valign="top">
                    <c:choose>
                        <c:when test="${displayPriceWithTax}">
                            <kk:formatPrice price="${item.finalPriceIncTax}"/>
                        </c:when>
                        <c:otherwise>
                            <kk:formatPrice price="${item.finalPriceExTax}"/>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot>
            <c:forEach var="ot" items="${checkoutOrder.orderTotals}">
                <tr>
                    <td width="60%" colspan="2"></td>
                    <td class="productListing-data">
                        <b><c:out value="${ot.title}"/></b>
                    </td>
                    <td align="right" class="productListing-data">
                        <b><kk:formatPrice price="${ot.value}"/></b>
                    </td>
                </tr>
            </c:forEach>
        </tfoot>
    </table>




    <!-- Display the order information -->
    <c:if test="${!empty orderTotals}">
        <table width="90%">
            <c:forEach var="ot" items="${orderTotals}">
                <tr>
                    <td width="60%"></td>
                    <td class="productListing-data">
                        <b><c:out value="${ot.title}"/></b>
                    </td>
                    <td align="right" class="productListing-data">
                        <b><c:out value="${ot.value}"/></b>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

    <!-- display coupon entry -->

    <c:if test="${displayCouponEntry}">
        <br/><b>Coupon Code</b>

        <div class="msg-box-no-pad">
            <table border="0" width="100%" cellspacing="0" cellpadding="2" class="body-content-tab">
                <tr>
                    <td><input type="text" size="40" name="couponCode" value="${couponCode}"/></td>
                    <td>Enter the coupon code and then click <b>Update</b></td>
                </tr>
            </table>
        </div>
    </c:if>

    <c:if test="${displayGiftCertEntry}">
        <br/><b>Gift Certificate</b>

        <div class="msg-box-no-pad">
            <table border="0" width="100%" cellspacing="0" cellpadding="2" class="body-content-tab">
                <tr>
                    <td><input type="text" size="40" name="giftCertCode" value="${giftCertCode}"/></td>
                    <td>Enter the gift certificate and then click <b>Update</b></td>
                </tr>
            </table>
        </div>
    </c:if>

    <c:if test="${stockCheck}">
        <c:if test="${itemOutOfStock}">
            <c:if test="${stockAllowCheckout}">Products marked with *** don't exist in desired quantity in our stock.<br>You can buy them anyway and check the quantity we have in stock for immediate delivery in the checkout process.</c:if>
            <c:if test="${! stockAllowCheckout}">Products marked with *** dont exist in desired quantity in our stock.<br>Please alter the quantity of products marked with (***), Thank you</c:if>
        </c:if>
    </c:if>

    <br/><br/>
    <hst:link var="editCartLink" siteMapItemRefId="cartDetailId"/>
    <a class="btn btn-success" href="${editCartLink}">Edit your Cart</a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="btn btn-danger" value="Place Order"/>
</form>



