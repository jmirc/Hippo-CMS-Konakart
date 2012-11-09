<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="item" type="org.onehippo.forge.konakart.hst.vo.CartItem"--%>


<hst:headContribution category="scripts">
    <hst:link path="/libs/bootstrap/js/collapse.js" var="collapseJs"/>
    <script src="${collapseJs}" type="text/javascript"></script>
</hst:headContribution>

<hst:headContribution category="scripts">
    <hst:link path="/js/cart.js" var="cartJs"/>
    <script src="${cartJs}" type="text/javascript"></script>
</hst:headContribution>


<h1> Shopping Cart</h1><br/>

<c:choose>
    <c:when test="${!empty currentCustomer.basketItems}">

        <hst:actionURL var="formAction">
            <hst:param name="action" value="UPDATE"/>
        </hst:actionURL>

        <form action="${formAction}" method="post">

            <table class="table table-bordered table-striped">
                <thead>
                <tr>
                    <th>Remove</th>
                    <th>Image</th>
                    <th>Product Name</th>
                    <th>Quantity</th>
                    <th>Unit Price</th>
                    <th>Total</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${cartitems}" varStatus="rowCounter">
                <tr>
                    <td class=""><input type="checkbox" name="remove_${item.basketItemId}"></td>
                    <td class="muted center_text">


                        <hst:link var="prdImgLink" hippobean="${item.productDocument.mainImage.original}"/>
                        <img src="${prdImgLink}" border="0" alt="${item.prodName}"
                                                  title="${item.prodName}" width="100"
                                                  height="80">
                    </td>
                    <td>
                        <hst:link var="prdLink" hippobean="${item.productDocument}"/>
                        <a href="${prdLink}"><b><c:out value="${item.prodName}"/></b></a>

                        <c:if test="${!item.inStock}"><span
                                class="markProductOutOfStock">***</span></c:if>

                            <!-- display the konakart attributes if exists -->
                        <c:forEach var="attribute" items="${item.optNameArray}">
                            <br>
                            <small><i> - <c:out value="${attribute}"/></i></small>
                        </c:forEach>
                    </td>
                    <td>
                        <input type="text" name="quantity_${item.basketItemId}" class="input-mini" size="4"
                               value="${item.quantity}"/>
                    </td>
                    <td><kk:formatPrice price="${item.productDocument.productIf.priceExTax}"/></td>
                    <td>${item.totalPrice}</td>
                </tr>
                </c:forEach>

                <!-- Display the order information -->
                <c:if test="${!empty orderTotals}">
                    <c:forEach var="ot" items="${orderTotals}">
                        <tr>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td class="productListing-data">
                                <b><c:out value="${ot.title}"/></b>
                            </td>
                            <td align="right" class="productListing-data">
                                <b><c:out value="${ot.value}"/></b>
                            </td>
                        </tr>
                    </c:forEach>
                </c:if>

                </tbody>
            </table>

            <c:if test="${stockCheck}">
                <c:if test="${itemOutOfStock}">
                    <c:if test="${stockAllowCheckout}">Products marked with *** don't exist in desired quantity in our stock.<br>You can buy them anyway and check the quantity we have in stock for immediate delivery in the checkout process.</c:if>
                    <c:if test="${! stockAllowCheckout}">Products marked with *** dont exist in desired quantity in our stock.<br>Please alter the quantity of products marked with (***), Thank you</c:if>
                    <hr/>
                </c:if>
            </c:if>


            <fieldset>
                <div class="accordion" id="accordion2">
                    <c:if test="${displayCouponEntry}">
                        <div class="accordion-group">
                            <div class="accordion-heading">

                                <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2"
                                   href="#collapseOne">
                                    <h3>Apply discount code</h3>
                                </a>
                            </div>
                            <div id="collapseOne" class="accordion-body collapse in">
                                <div class="accordion-inner">
                                    <div class="control-group">
                                        <label for="input01" class="control-label">Discount code: </label>

                                        <div class="controls">
                                            <input type="text" size="40" name="couponCode" value="${couponCode}"
                                                   placeholder="Enter your coupon here"/>
                                            <p class="help-block">You can only use one discount code at a time</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${displayGiftCertEntry}">
                        <div class="accordion-group">
                            <div class="accordion-heading">
                                <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2"
                                   href="#collapseTwo">
                                    <h3>Use gift voucher</h3>
                                </a>
                            </div>
                            <div id="collapseTwo" class="accordion-body collapse">
                                <div class="accordion-inner">
                                    <div class="control-group">
                                        <label for="input01" class="control-label">Gift voucher: </label>

                                        <div class="controls">
                                            <input type="text" id="input01" class="input-xlarge" name="giftCertCode"
                                                   value="${giftCertCode}"
                                                   placeholder="Enter your gift voucher here">

                                            <p class="help-block">You can use multiple gift vouchers at a time</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </div>

                <div class="row">
                    <div class="span5">
                        <button class="btn btn-primary" type="submit">Update</button>
                    </div>
                    <div class="span2">
                        <hst:link var="continueShopping" path="/products"/>
                        <a class="btn btn-primary" href="${continueShopping}">Continue Shopping</a>
                    </div>
                    <div class="span5">
                        <hst:link var="checkout" path="/checkout">
                            <hst:param name="new_checkout" value="true"/>
                        </hst:link>
                        <a href="${checkout}" class="btn btn-primary pull-right">Checkout</a>
                    </div>
                </div>
            </fieldset>
        </form>
    </c:when>
    <c:otherwise>
        <p>Your Shopping Cart is empty!</p>
    </c:otherwise>
</c:choose>
