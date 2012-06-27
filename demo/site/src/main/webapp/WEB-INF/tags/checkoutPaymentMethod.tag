<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="hst" uri="http://www.hippoecm.org/jsp/hst/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="kk" uri="http://www.onehippo.org/jsp/konakart" %>
<%--@elvariable id="paymentGateway" type="com.konakart.appif.PaymentDetailsIf"--%>


<hst:actionURL var="paymentMethodLink">
    <hst:param name="action" value="SELECT"/>
    <hst:param name="state" value="PAYMENT_METHOD"/>
</hst:actionURL>

<c:if test="${not empty form.message['globalmessage']}">
    <div class="alert alert-error">
            ${form.message['globalmessage']}
    </div>
</c:if>

<form action="${paymentMethodLink}" method="post">
    <c:forEach items="${paymentDetails}" var="paymentGateway">
        <fieldset>
            <legend>Please select the preferred payment method to use on this order.</legend>
            <div class="control-group">
                <div class="controls">
                    <label class="radio inline" for="input01">
                        <input type="radio" name="paymentMethod" class="input-mini" id="input01"
                               value="${paymentGateway.code}"
                               <c:if test="${paymentMethod == paymentGateway.code}">checked="checked"</c:if>
                               <c:if test="${fn:length(paymentDetails) == 1}">checked="checked"</c:if>
                         > ${paymentGateway.title}
                    </label>
                </div>
        </fieldset>
        <br/>
    </c:forEach>

    <br/>
    <input type="submit" class="btn btn-success" value="Continue"/>
</form>



