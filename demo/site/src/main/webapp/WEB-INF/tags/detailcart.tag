<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="hst" uri="http://www.hippoecm.org/jsp/hst/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:choose>
    <c:when test="${!empty currentCustomer.basketItems}">
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
                        <input type="checkbox" ${empty form.value['remove'].values[rowCounter.count] ? '':'checked="checked"'}
                               name="remove">
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
                                </td>
                            </tr>
                        </table>
                    </td>
                    <td align="center" class="productListing-data" valign="top">
                        <input type="text" name="quantity" size="4" value="${item.quantity}"/>
                    </td>
                    <td align="center" class="productListing-data" valign="top">
                        <c:out value="${item.totalPrice}"/>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:when>
    <c:otherwise>
        Your Shopping Cart is empty!
    </c:otherwise>
</c:choose>
