<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="hst" uri="http://www.hippoecm.org/jsp/hst/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<c:choose>
    <c:when test="${!empty currentCustomer.basketItems}">
        <hst:actionURL var="formAction">
            <hst:param name="action" value="update"/>
        </hst:actionURL>

        <form action="${formAction}" method="post">
            <table border="0" width="90%" cellspacing="0" cellpadding="2" class="productListing">
                <tr>
                    <td align="center" class="productListing-heading">Remove</td>
                    <td align="center" class="productListing-heading">Product(s)</td>
                    <td align="center" class="productListing-heading">Qty.</td>
                    <td align="center" class="productListing-heading">Total</td>
                </tr>
                <c:forEach var="item" items="${cartitems}" varStatus="rowCounter">
                    <tr class="productListing-even">
                        <td align="center" class="productListing-data" valign="top">
                            <input type="checkbox" name="remove_${item.basketItemId}">
                        </td>
                        <td class="productListing-data" align="center">
                            <table border="0" cellspacing="2" cellpadding="2">
                                <tr>
                                    <td class="productListing-data" align="center">
                                        <a href="${item.custom1}"><img src="#" border="0" alt="${item.prodName}"
                                                                       title="${item.prodName}" width="100"
                                                                       height="80"></a>
                                    </td>
                                    <td class="productListing-data" valign="top">
                                        <a href="${item.custom1}"><b><c:out value="${item.prodName}"/></b></a>
                                        <c:if test="${!item.inStock}"><span
                                                class="markProductOutOfStock">***</span></c:if>

                                        <!-- display the konakart attributes if exists -->
                                        <c:forEach var="attribute" items="${item.optNameArray}">
                                            <br>
                                            <small><i> - <c:out value="${attribute}"/></i></small>
                                        </c:forEach>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td align="center" class="productListing-data" valign="top">
                            <input type="text" name="quantity_${item.basketItemId}" size="4" value="${item.quantity}"/>
                        </td>
                        <td align="center" class="productListing-data" valign="top">
                            <c:out value="${item.totalPrice}"/>
                        </td>
                    </tr>
                </c:forEach>
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

            <!-- Update buton -->
            <input type="submit" value="Update"/>

        </form>


    </c:when>
    <c:otherwise>
        Your Shopping Cart is empty!
    </c:otherwise>
</c:choose>
