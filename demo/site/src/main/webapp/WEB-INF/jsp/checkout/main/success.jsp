<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>

<h1>Your Order Has Been Processed!</h1>

<p>
Your order has been successfully processed! Your products will arrive at their destination within 2-5 working days. <br/>
</p>

<c:if test="${globalProductNotifierEnabled}">
    <c:if test="${not empty notifiedProducts}">
        Please notify me of updates to the products I have selected below:

        <hst:actionURL var="checkoutSuccessLink">
            <hst:param name="action" value="SELECT"/>
            <hst:param name="state" value="CHECKOUT_FINISHED"/>
        </hst:actionURL>

        <form action="${checkoutSuccessLink}" class="well">
            <c:forEach items="${notifiedProducts}" var="notifiedProduct">
                <input type="checkbox" name="remove_${notifiedProduct.prodId}"> ${notifiedProduct.prodName}
            </c:forEach>

            <p>
                <h3>Thanks for shopping with us online!</h3>
            </p>

            <!-- Update buton -->
            <div class="inline">
                <div class="row">
                    <div class="span5">
                        <input class="btn" type="submit" value="Continue"/>
                    </div>
                </div>
            </div>


        </form>
    </c:if>
</c:if>


