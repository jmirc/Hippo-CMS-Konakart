<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="category" type="com.konakart.appif.CategoryIf"--%>
<%--@elvariable id="products" type="org.onehippo.forge.utilities.hst.paging.IterablePagination"--%>
<%--@elvariable id="product" type="org.onehippo.forge.konakart.hst.beans.KKProductDocument"--%>
<%--@elvariable id="productIf" type="com.konakart.appif.ProductIf"--%>


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

    <hr>
    <c:if test="${empty products}"><p>No products have been found.</p></c:if>
    <c:if test="${not empty products}">
        <c:forEach var="productIf" items="${products.items}">
            <kk:retrieveKKProductDocument productId="${productIf.id}" var="product"/>

            <c:if test="${not empty product}">
                <kk:addToBasketActionURL product="${product}" var="productUrl"/>
            </c:if>
            <form id="productForm" action="${productUrl}" class="form-inline" method="post">
                <input type="hidden" id="addToWishList" name="addToWishList" value=""/>
                <input type="hidden" id="addToCompare" name="addToCompare" value=""/>
                <input type="hidden" name="wishListId" value="1"/>

                <div class="row">
                    <div class="span1">

                        <hst:link hippobean="${product}" var="prdLink"/>
                        <hst:link hippobean="${product.mainImage.thumbnail}" var="prdImgLink"/>
                        <a href="${prdLink}"><img alt="${product.mainImage.thumbnail.name}" id="tmp" src="${prdImgLink}"></a>
                    </div>

                    <div class="span6">
                        <a href="${prdLink}"><h5>${productIf.name}</h5></a>
                        <kk:rating productId="${product.productId}" ratingVar="rating" nbReviewsVar="nbReviews"/>
                        <fmt:formatNumber value="${rating * 10}" var="ratingStyle" pattern="#0"/>
                        <p class="rating stars-${ratingStyle}">
                            <a href="#">
                                <span style="margin-left: 100px;">&nbsp;</span>
                            </a>
                        </p>
                        <p>
                            <c:set var="summary" value="${fn:substringBefore(productIf.description, '-------')}"/>

                            ${summary}
                        </p>
                    </div>

                    <div class="span2">
                        <p>
                            <c:if test="${not empty product.specialPrice}"><s></c:if>
                            <kk:formatPrice price="${productIf.price0}"/>
                            <c:if test="${not empty product.specialPrice}"></s></c:if>
                            <c:if test="${not empty product.specialPrice}">&nbsp;|&nbsp;
                                <kk:formatPrice price="${product.specialPrice}"/>
                            </c:if>
                        </p>
                    </div>

                    <div class="span2">
                        <p><button class="btn btn-primary" type="submit">Add to cart</button></p>

                        <c:if test="${wishListEnabled}">
                            <p><a href="#" id="wishlistLink">Add to wish list</a></p>
                        </c:if>

                        <p><a href="#" id="compareLink">Add to Compare</a></p>
                    </div>
                </div>
                <hr/>
            </form>
        </c:forEach>



        <div class="pagination">
            <tag:pagination pageableResult="${products}" queryName="query" queryValue="${query}"/>
        </div>
    </c:if>


</div>


