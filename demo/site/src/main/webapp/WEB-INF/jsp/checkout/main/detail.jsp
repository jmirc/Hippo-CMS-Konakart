<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>

<hst:headContribution category="jsInternal">
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
</hst:headContribution>


<div class="container">
    <div class="row">

        <div class="span10">
            <div class="row">
                <div class="span8">
                    <ul class="breadcrumb">
                        <li class="active"><h2>1. Checkout Method</h2></li>
                    </ul>
                    <c:if test="${STATE == 'INITIAL'}">
                        <tag:checkoutMethod/>
                    </c:if>

                    <ul class="breadcrumb">
                        <li class="active"><h2>2. Billing Address</h2></li>
                    </ul>
                    <c:if test="${STATE == 'BILLING_ADDRESS'}">
                        <tag:checkoutBillingAddress/>
                    </c:if>

                    <ul class="breadcrumb">
                        <li class="active"><h2>3. Shipping Address</h2></li>
                    </ul>
                    <ul class="breadcrumb">
                        <li class="active"><h2>4. Shipping Method</h2></li>
                    </ul>
                    <ul class="breadcrumb">
                        <li class="active"><h2>5. Payment Method</h2></li>
                    </ul>
                    <ul class="breadcrumb">
                        <li class="active"><h2>6. Order Review</h2></li>
                    </ul>

                </div>
            </div>

        </div>
        <div class="span2">
            checkout process
        </div>
    </div>


</div>


<%--<tag:onepagecheckout/>--%>