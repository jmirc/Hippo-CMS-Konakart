<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>

<h1>Your Order Has Been Processed!</h1>

<p>
    <h4>Your order has been successfully processed! Your products will arrive at their destination within 2-5 working days.</h4>
    <br/>
</p>

<c:if test="${globalProductNotifierEnabled}">
    <p>
    <c:if test="${not empty notifiedProducts}">
        <h5 class="verticalSpace">Please notify me of updates to the products I have selected below:</h5>

        <kk:activityActionURL var="checkoutSuccessLink" state="CHECKOUT_FINISHED"/>

        <form action="${checkoutSuccessLink}" method="POST">
            <div class="well">
            <c:forEach items="${notifiedProducts}" var="notifiedProduct">
                <div class="control-group">
                    <div class="controls">
                        <label class="checkbox">
                            <input type="checkbox" name="remove_${notifiedProduct.prodId}"/> ${notifiedProduct.prodName}
                        </label>
                    </div>
                </div>
            </c:forEach>

            </div>

            <!-- Update buton -->
            <div>
                <button class="btn btn-success" type="submit">Continue</button>
            </div>


        </form>
    </c:if>
    </p>
</c:if>


