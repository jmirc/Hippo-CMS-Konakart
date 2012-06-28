<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="hst" uri="http://www.hippoecm.org/jsp/hst/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="kk" uri="http://www.onehippo.org/jsp/konakart" %>
<%--@elvariable id="quote" type="com.konakart.appif.ShippingQuoteIf"--%>


<hst:actionURL var="shippingMethod">
    <hst:param name="action" value="SELECT"/>
    <hst:param name="state" value="SHIPPING_METHOD"/>
</hst:actionURL>

<c:if test="${not empty form.message['globalmessage']}">
    <div class="alert alert-error">
            ${form.message['globalmessage']}
    </div>
</c:if>

<form action="${shippingMethod}" method="post">
    <div class="alert alert-info">
        Select a shipping method.
    </div>


    <c:forEach items="${shippingQuotes}" var="quote">
        <fieldset>
            <legend>${quote.title}</legend>
            <c:choose>
                <c:when test="${quote.free}">
                    Free shipping
                    <input type="hidden" name="shipping" value="dummy"/>
                </c:when>
                <c:otherwise>
                    <div class="control-group">
                        <div class="controls">
                            <label class="radio inline" for="input01">
                                <input type="radio" name="shipMethod" class="input-mini" id="input01"
                                       value="${quote.code}"
                                       <c:if test="${shipMethod == quote.code}">checked="checked"</c:if>
                                       <c:if test="${fn:length(shippingQuotes) == 1}">checked="checked"</c:if>
                                 >
                                 <b>${quote.responseText} -
                                    <c:choose>
                                        <c:when test="${displayPriceWithTax}"><kk:formatPrice price="${quote.totalIncTax}"/> </c:when>
                                        <c:when test="${!displayPriceWithTax}"><kk:formatPrice price="${quote.totalExTax}"/></c:when>
                                    </c:choose>
                                    </b>
                            </label>
                        </div>
                    </div>

                </c:otherwise>
            </c:choose>
        </fieldset>
        <br/>
    </c:forEach>

    <br/>
    <input type="submit" class="btn btn-success" value="Continue"/>
</form>



