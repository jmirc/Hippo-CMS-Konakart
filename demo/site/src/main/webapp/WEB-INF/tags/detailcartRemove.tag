<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="hst" uri="http://www.hippoecm.org/jsp/hst/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--@elvariable id="currentCustomer" type="com.konakart.appif.CustomerIf"--%>
<%--@elvariable id="item" type="org.onehippo.forge.konakart.hst.vo.CartItem"--%>

<c:choose>
    <c:when test="${!empty currentCustomer.basketItems}">
        <hst:actionURL var="formAction">
            <hst:param name="action" value="UPDATE"/>
        </hst:actionURL>

        <form action="${formAction}" method="post">
            <fieldset>
                <legend><h1>Shopping Cart</h1></legend>
            </fieldset>
            <table class="table table-condensed">
                <thead>
                    <tr>
                        <th></th>
                        <th>Product(s)</th>
                        <th>Qty.</th>
                        <th>Total</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${cartitems}" varStatus="rowCounter">
                    <tr class="productListing-even">
                        <td>
                            <hst:link var="prdLink" hippobean="${item.productDocument}"/>
                            <hst:link var="prdImgLink"
                                      hippobean="${item.productDocument.mainImage.original}"/>


                            <a href="${prdLink}"><img src="${prdImgLink}" border="0" alt="${item.prodName}"
                                                      title="${item.prodName}" width="100"
                                                      height="80"></a>
                        </td>
                        <td>
                            <a href="${prdLink}"><b><c:out value="${item.prodName}"/></b></a>
                            <c:if test="${!item.inStock}"><span
                                    class="markProductOutOfStock">***</span></c:if>

                            <!-- display the konakart attributes if exists -->
                            <c:forEach var="attribute" items="${item.optNameArray}">
                                <br>
                                <small><i> - <c:out value="${attribute}"/></i></small>
                            </c:forEach>

                            <br/>
                            <hst:actionURL var="removeFromBasket">
                                <hst:param name="action" value="REMOVE_FROM_BASKET"/>
                                <hst:param name="basketId" value="${item.basketItemId}"/>
                            </hst:actionURL>
                            <a href="${removeFromBasket}">Remove</a>
                        </td>
                        <td>
                            <input type="text" name="quantity_${item.basketItemId}" class="input-mini" size="4"
                                   value="${item.quantity}"/>
                        </td>
                        <td>
                            <c:out value="${item.totalPrice}"/>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>

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

            <!-- Update buton -->
            <div class="inline">
                <div class="row">
                    <div class="span5">
                        <input class="btn" type="submit" value="Update Cart"/>

                        <hst:link var="continueShopping" path="/products"/>
                        <a class="btn btn-primary" href="${continueShopping}"><i
                                class="icon-home icon-large icon-white"></i> Continue Shopping</a>
                    </div>
                    <div class="span3">
                        <hst:link var="checkout" path="checkout"/>
                        <a class="btn btn-danger" href="${checkout}"><i
                                class="icon-shopping-cart icon-large icon-white"></i> Proceed to Checkout</a>
                    </div>
                </div>
            </div>


        </form>


        <c:if test="${itemOutOfStock}">
            <c:if test="${stockAllowCheckout}">
                <div class="alert">
                    <fmt:message key="outofstock"/>
                </div>
            </c:if>
            <c:if test="${!stockAllowCheckout}">
                <div class="alert alert-error">
                    <fmt:message key="outofstock.error"/>
                </div>
            </c:if>
        </c:if>


    </c:when>
    <c:otherwise>
        Your Shopping Cart is empty!
    </c:otherwise>
</c:choose>
