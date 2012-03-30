<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>

<hst:headContribution category="jsInternal">
    <hst:link var="jquery" path="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"/>
    <script src="${jquery}" type="text/javascript"></script>
</hst:headContribution>

<hst:headContribution category="jsInternal">
    <hst:link var="cartJs" path="/js/cart.js"/>
    <script src="${cartJs}" type="text/javascript"></script>
</hst:headContribution>


<hst:actionURL var="addToBasket">
    <hst:param name="action" value="addToBasket"/>
    <hst:param name="prodId" value="${document.productId}"/>
</hst:actionURL>


<hst:link var="prdImgLink" hippobean="${document.mainImage.original}"/>
<hst:link var="prdlink" hippobean="${document}"/>

<script type="text/javascript"><!--
function setAddToWishList() {
    document.addToCartForm.addToWishList.value="true";
    document.addToCartForm.wishListId.value="-1";
}

function resetAddToWishList() {
    document.addToCartForm.addToWishList.value="false";
}

function setWishListId(id) {
    document.addToCartForm.wishListId.value=id;
    document.addToCartForm.addToWishList.value="true";
}

//--></script>

<form name="addToCartForm" action="${addToBasket}" method="post">

    <input type="hidden" name="addToWishList" value=""/>
    <input type="hidden" name="wishListId" value=""/>

    <div class="thumbnail">

        <div class="caption">
            <img src="${prdImgLink}" alt=""/><br/>
            <h4><c:out value="${document.name}"/></h4>

            <p>
                <c:if test="${not empty document.specialPrice}"><s></c:if>
                <c:out value="${document.price}"/>
                <c:if test="${not empty document.specialPrice}"></s></c:if>
                <c:if test="${not empty document.specialPrice}">&nbsp;|&nbsp;
                    <c:out value="${document.specialPrice}"/></c:if>
            </p>
            <fmt:formatNumber value="${document.rating * 10}" var="ratingStyle" pattern="#0"/>

            <p class="rating stars-${ratingStyle}"><a href="${fn:escapeXml(prdlink)}">
                <span style="margin-left: 100px;"><c:out value="${document.rating}"/></span>
            </a>
            </p>

            <p>

                <br/>
                <c:if test="${not empty prodOptContainer}">
                    <h5>Available options :</h5><br/>


                    <c:forEach var="opt" items="${prodOptContainer}" varStatus="rowCounter">

                        <div class="control-group">
                            <label class="control-label"><c:out value="${opt.name}"/>&nbsp;:&nbsp;</label>
                            <div class="controls">
                                <select name="${opt.id}" id="${opt.name}">
                                    <c:forEach var="optValue" items="${opt.optValues}">
                                        <c:choose>
                                            <c:when test="${opt.type == 0}">
                                                <c:if test="${displayPriceWithTax}">
                                                    <option value="${optValue.id}"><c:out value="${optValue.formattedValueIncTax}"/></option>
                                                </c:if>

                                                <c:if test="${not displayPriceWithTax}">
                                                    <option value="${optValue.id}"><c:out value="${optValue.formattedValueExTax}"/></option>
                                                </c:if>
                                            </c:when>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </c:forEach>
                </c:if>

            </p>

            <p>
                <br/>
                <input type="submit" onmouseover="resetAddToWishList()" class="btn btn-primary" value="Add to basket"/>

                <c:if test="${wishListEnabled}">
                    <input type="submit" onmouseover="setAddToWishList()" class="btn btn-primary" value="Add to wish list"/>
                </c:if>


            </p>
        </div>
    </div>
</form>


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

    <br/>
    <br/>

    <div class="span8">
        <c:if test="${not empty success}">
            <fmt:message key="products.detail.thanksforreview"/>
            <br/>
            <br/>
        </c:if>

        <form id="frmRating" action="${reviewUrl}" method="post" class="form-horizontal">
            <input type="hidden" name="type" value="review">
            <fieldset>
                <legend><fmt:message key="products.detail.reviewarticle"/></legend>
                <div class="control-group">
                    <label class="control-label"><fmt:message key="products.detail.name"/></label>

                    <div class="controls">
                        <input type="text" class="input-xlarge" value="${fn:escapeXml(name)}" name="name"/>
                        <c:if test="${not empty errors}">
                            <c:forEach items="${errors}" var="error">
                                <c:if test="${error eq 'invalid.name-label'}">
                                        <span class="form-error"><fmt:message
                                                key="products.detail.name.error"/></span><br/>
                                </c:if>
                            </c:forEach>
                        </c:if>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label"><fmt:message key="products.detail.email"/></label>

                    <div class="controls">
                        <input type="text" class="input-xlarge" value="${fn:escapeXml(email)}" name="email"/>
                        <c:if test="${not empty errors}">
                            <c:forEach items="${errors}" var="error">
                                <c:if test="${error eq 'invalid.name-label'}">
                                        <span class="form-error"><fmt:message
                                                key="products.detail.email.error"/></span><br/>
                                </c:if>
                            </c:forEach>
                        </c:if>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label"><fmt:message key="products.detail.score"/></label>

                    <div class="controls">
                        <ol class="rate">
                            <li><span title="Rate: 1">1</span></li>
                            <li><span title="Rate: 2">2</span></li>
                            <li><span title="Rate: 3">3</span></li>
                            <li><span title="Rate: 4">4</span></li>
                            <li><span title="Rate: 5">5</span></li>
                        </ol>
                        <input type="hidden" value="0" name="rating" id="ratingField"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label"><fmt:message key="products.detail.review"/></label>

                    <div class="controls">
                        <textarea name="comment" id="comment" rows="8" cols="50" class="input-xlarge"></textarea>
                        <c:if test="${not empty errors}">
                            <c:forEach items="${errors}" var="error">
                                <c:if test="${error eq 'invalid.name-label'}">
                                        <span class="form-error"><fmt:message
                                                key="products.detail.review.error"/></span><br/>
                                </c:if>
                            </c:forEach>
                        </c:if>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <input class="btn btn-primary" type="submit" value="<fmt:message key="products.detail.submit.label"/>"/>
                    </div>
                </div>
            </fieldset>

        </form>
    </div>
</c:if>