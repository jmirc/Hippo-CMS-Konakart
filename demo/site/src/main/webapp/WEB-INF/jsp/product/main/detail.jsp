<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>

<hst:link var="prdlink" hippobean="${product}"/>

<hst:cmseditlink hippobean="${product}"/>
<a href="${fn:escapeXml(prdlink)}"><c:out value="${product.name}"/></a>
<br/>
<c:if test="${not empty product.specialPrice}"><s></c:if>
<c:out value="${product.price}"/>
<c:if test="${not empty product.specialPrice}"></s></c:if>
| <c:if test="${not empty product.specialPrice}"><c:out value="${product.specialPrice}"/></c:if><br/>
<fmt:formatNumber value="${product.rating * 10}" var="ratingStyle" pattern="#0"/>
<div class="rating stars-${ratingStyle}"><a href="${fn:escapeXml(prdlink)}"><c:out value="${product.rating}"/></a></div>
<br/>


<div id="comments">
    <c:forEach items="${reviews}" var="review">
        <ul class="comment-item">
            <li class="name"><a href="#"><c:out value="${review.name}"/></a></li>
            <li class="date"><span class="seperator">|</span> <fmt:formatDate value="${review.date.time}"
                                                                              pattern="MMM dd, yyyy"/></li>
            <li class="text">
                <ul>
                    <li class="score"><fmt:message key="products.detail.score"/>:</li>
                    <fmt:formatNumber value="${review.rating * 10}" var="reviewRatingStyle" pattern="#0"/>
                    <li class="rating stars-${reviewRatingStyle}"></li>
                    <li class="review"><c:out value="${review.comment}"/></li>
                </ul>
            </li>
            <li class="bg-bottom"></li>
        </ul>
    </c:forEach>
</div>

<c:if test="${allowComments}">
    <hst:actionURL var="reviewUrl">
        <hst:param name="action" value="review"/>
    </hst:actionURL>

    <div id="article-footer">
        <h3><fmt:message key="products.detail.reviewarticle"/></h3>
        <ul class="box-bottom box-form" id="review">
            <li class="content">
                <form id="frmRating" action="${reviewUrl}" method="post">
                    <input type="hidden" name="type" value="review">
                    <table>
                        <tr>
                            <td colspan="2">
                                <c:if test="${not empty success}">
                                    <fmt:message key="products.detail.thanksforreview"/>
                                    <br/>
                                    <br/>
                                </c:if>
                            </td>
                        </tr>
                        <tr>
                            <td class="label"><fmt:message key="products.detail.name"/></td>
                            <td class="input"><input type="text" value="${fn:escapeXml(name)}" name="name"/>
                                <c:if test="${not empty errors}">
                                    <c:forEach items="${errors}" var="error">
                                        <c:if test="${error eq 'invalid.name-label'}">
                                            <span class="form-error"><fmt:message
                                                    key="products.detail.name.error"/></span><br/>
                                        </c:if>
                                    </c:forEach>
                                </c:if>
                            </td>
                        </tr>
                        <tr>
                            <td class="label"><fmt:message key="products.detail.email"/></td>
                            <td class="input"><input type="text" value="${fn:escapeXml(email)}" name="email"/>
                                <c:if test="${not empty errors}">
                                    <c:forEach items="${errors}" var="error">
                                        <c:if test="${error eq 'invalid.email-label'}">
                                            <span class="form-error"><fmt:message key="products.detail.email.error"/></span><br/>
                                        </c:if>
                                    </c:forEach>
                                </c:if>
                            </td>
                        </tr>
                        <tr>
                            <td class="label vtop"><fmt:message key="products.detail.score"/></td>
                            <td class="input">
                                <ol class="rate">
                                    <li><span title="Rate: 1">1</span></li>
                                    <li><span title="Rate: 2">2</span></li>
                                    <li><span title="Rate: 3">3</span></li>
                                    <li><span title="Rate: 4">4</span></li>
                                    <li><span title="Rate: 5">5</span></li>
                                </ol>
                                <input type="hidden" value="0" name="rating" id="ratingField"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="label vtop"><fmt:message key="products.detail.review"/></td>
                            <%--Do not split next line in rows, else the textarea will show white spaces on initialization--%>
                            <td class="input"><textarea name="comment" id="comment" rows="8" cols="50"><c:if
                                    test="${not empty comment}"><c:out value="${comment}"/></c:if></textarea>
                                <c:if test="${not empty errors}">
                                    <c:forEach items="${errors}" var="error">
                                        <c:if test="${error eq 'invalid.comment-label'}">
                                            <span class="form-error"><fmt:message
                                                    key="products.detail.review.error"/></span><br/>
                                        </c:if>
                                    </c:forEach>
                                </c:if>
                            </td>
                        </tr>
                        <tr>
                            <td></td>
                            <td class="submit fright"><input type="submit"
                                                             value="<fmt:message key="products.detail.submit.label"/>"
                                                             id="comment-button"/></td>
                        </tr>
                    </table>
                </form>
            </li>
        </ul>
    </div>
</c:if>