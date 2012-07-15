<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="category" type="com.konakart.appif.CategoryIf"--%>
<%--@elvariable id="product" type="org.onehippo.forge.konakart.hst.beans.KKProductDocument"--%>


<hst:headContribution category="scripts">
    <hst:link var="rateJs" path="/js/rate.js"/>
    <script src="${rateJs}" type="text/javascript"></script>
</hst:headContribution>

<hst:headContribution category="css">
    <hst:link path="/css/rate.css" var="rateCss"/>
    <link rel="stylesheet" href="${rateCss}" type="text/css"/>
</hst:headContribution>

<hst:headContribution category="scripts">
    <hst:link path="/libs/bootstrap/js/tabs.js" var="tabsJs"/>
    <script src="${tabsJs}" type="text/javascript"></script>
</hst:headContribution>

<hst:headContribution category="scripts">
    <hst:link path="/js/shop.js" var="shopJs"/>
    <script src="${shopJs}" type="text/javascript"></script>
</hst:headContribution>


<div class="span9">
<ul class="breadcrumb">
    <li>
        <a href="<hst:link path="/"/>">Home</a> <span class="divider">/</span>
    </li>

    <c:forEach var="category" items="${currentCategories}" varStatus="status">
        <hst:link path="/listing/${category.id}" var="selectCategoryLink"/>
        <li class="<c:if test="${category.selected}">active</c:if>">
            <a href="${selectCategoryLink}">${category.name}</a> <c:if test="${not status.last}"> <span class="divider">/</span></c:if>
        </li>
    </c:forEach>
</ul>

<div class="row">
    <div class="span9">
        <h1>${product.productIf.name}</h1>
    </div>
</div>
<hr>
<kk:addToBasketActionURL product="${product}" var="productUrl"/>
<form id="productForm" action="${productUrl}" class="form-inline" method="post">
    <input type="hidden" id="addToWishList" name="addToWishList" value=""/>
    <input type="hidden" id="addToCompare" name="addToCompare" value=""/>
    <input type="hidden" name="wishListId" value="1"/>

    <div class="row">
        <div class="span3">
            <hst:link var="prdImgLink" hippobean="${product.mainImage.original}"/>
            <img alt="${product.mainImage.original.name}" src="${prdImgLink}"
                 width="${product.mainImage.original.width}" height="${product.mainImage.original.height}"/>

            <ul class="thumbnails">
                <c:forEach var="image" begin="1" items="${product.images}">
                    <li class="span1">
                        <hst:link var="otherImgLink" hippobean="${image.thumbnail}"/>

                        <a href="#" class="thumbnail">
                            <img src="${otherImgLink}" alt="${image.name}" width="${image.thumbnail.width}"
                                 height="${image.thumbnail.height}">
                        </a>
                    </li>
                </c:forEach>
            </ul>

        </div>

        <div class="span6">

            <div class="span6">
                <address>
                    <strong>Brand:</strong> <span>${product.productIf.manufacturerName}</span><br/>
                    <strong>Product Code:</strong> <span>${product.productIf.model}</span><br/>
                    <strong>Reward Points:</strong> <span>0</span><br/>
                    <c:if test="${product.productIf.quantity eq 0}">
                        <strong>Availability:</strong>Out Of Stock<span></span><br/>
                    </c:if>
                </address>
            </div>

            <div class="span6">
                <h2>
                    <strong>Price:
                        <c:if test="${not empty product.specialPrice}"><s></c:if>
                            <kk:formatPrice price="${product.productIf.price0}"/>
                            <c:if test="${not empty product.specialPrice}"></s></c:if></strong>

                    <c:if test="${not empty product.specialPrice}">
                        <small><kk:formatPrice price="${product.specialPrice}"/></small>
                    </c:if>
                    <br/><br/>
                </h2>
            </div>

            <div class="span6">

                <div class="span3 no_margin_left">
                    <label>Qty:</label>
                    <input type="text" name="quantity" class="span1" placeholder="1">
                    <button class="btn btn-primary" type="submit">Add to cart</button>
                </div>
                <div class="span1">
                    - OR -
                </div>

                <div class="span2">
                    <%--<c:if test="${wishListEnabled}">--%>
                    <p><a href="#" id="wishlistLink">Add to wish list</a></p>
                    <%--</c:if>--%>

                    <p><a href="#" id="compareLink">Add to Compare</a></p>
                </div>
            </div>

            <div class="span6">

                <c:set var="rating" value="${product.productIf.rating}"/>
                <fmt:formatNumber value="${rating * 10}" var="ratingStyle" pattern="#0"/>
                <p class="rating stars-${ratingStyle}">
                    <a href="#">
                        <span style="margin-left: 100px;">&nbsp;</span>
                    </a>
                    <a href="#">
                        <c:choose>
                            <c:when test="${empty rating}">0 review</c:when>
                            <c:when test="${rating eq 1}">1 review</c:when>
                            <c:otherwise>${rating} reviews</c:otherwise>
                        </c:choose>
                    </a></p>

                </p>
            </div>


            <div class="span6">
                <c:if test="${not empty prodOptContainer}">
                    <br/>
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
                                                    <option value="${optValue.id}"><c:out
                                                            value="${optValue.formattedValueIncTax}"/></option>
                                                </c:if>

                                                <c:if test="${not displayPriceWithTax}">
                                                    <option value="${optValue.id}"><c:out
                                                            value="${optValue.formattedValueExTax}"/></option>
                                                </c:if>
                                            </c:when>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </c:forEach>
                </c:if>
            </div>
        </div>
    </div>
</form>

<hr>
<div class="row">
    <div class="span9">
        <div class="tabbable">
            <ul class="nav nav-tabs">
                <li class="active"><a href="#1" data-toggle="tab">Description</a></li>
                <li><a href="#2" data-toggle="tab">Reviews</a></li>
                <li><a href="#3" data-toggle="tab">Related products</a></li>
            </ul>
            <div class="tab-content">
                <div class="tab-pane active" id="1">
                    <p>${product.productIf.description}</p>
                </div>
                <div class="tab-pane" id="2">
                    <c:choose>
                        <c:when test="${fn:length(reviews) == 0}"><p>There are no reviews for this product.</p></c:when>
                        <c:otherwise>
                            <c:forEach items="${reviews}" var="review">
                                <div class=" well">
                                    <p>
                                    <h5>${review.customerName} | <fmt:formatDate value="${review.dateAdded.time}"
                                                                                 pattern="MMM dd, yyyy"/></h5>
                                    </p>
                                    <fmt:formatNumber value="${review.rating * 10}" var="ratingStyle" pattern="#0"/>
                                    <p class="rating stars-${ratingStyle}">&nbsp;</p>

                                    <p>${review.reviewText}</p>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${isLogged}">
                        <kk:activityActionURL action="REVIEW" var="reviewUrl"/>
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
                                            <input type="text" class="input-xlarge" value="${fn:escapeXml(name)}"
                                                   name="name"/>
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
                                            <input type="text" class="input-xlarge" value="${fn:escapeXml(email)}"
                                                   name="email"/>
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
                                            <textarea name="comment" id="comment" rows="8" cols="50"
                                                      class="input-xlarge"></textarea>
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
                                            <input class="btn btn-primary" type="submit"
                                                   value="<fmt:message key="products.detail.submit.label"/>"/>
                                        </div>
                                    </div>
                                </fieldset>

                            </form>
                        </div>
                    </c:if>
                </div>
                <div class="tab-pane" id="3">
                    <ul class="thumbnails related_products">

                        <li class="span2">
                            <div class="thumbnail">
                                <a href="product.html"><img alt="" src="http://placehold.it/220x180"/></a>

                                <div class="caption">
                                    <a href="product.html"><h5>iPod Touch</h5></a> Price: &#36;50.00<br/><br/>
                                </div>
                            </div>
                        </li>

                        <li class="span2">
                            <div class="thumbnail">
                                <a href="product.html"><img alt="" src="http://placehold.it/220x180"/></a>

                                <div class="caption">
                                    <a href="product.html"><h5>iPod Touch</h5></a> Price: &#36;50.00<br/><br/>
                                </div>
                            </div>
                        </li>

                        <li class="span2">
                            <div class="thumbnail">
                                <a href="product.html"><img alt="" src="http://placehold.it/220x180"/></a>

                                <div class="caption">
                                    <a href="product.html"><h5>iPod Touch</h5></a> Price: &#36;50.00<br/><br/>
                                </div>
                            </div>
                        </li>

                        <li class="span2">
                            <div class="thumbnail">
                                <a href="product.html"><img alt="" src="http://placehold.it/220x180"/></a>

                                <div class="caption">
                                    <a href="product.html"><h5>iPod Touch</h5></a> Price: &#36;50.00<br/><br/>
                                </div>
                            </div>
                        </li>


                    </ul>
                </div>
            </div>
        </div>

    </div>
</div>
</div>

