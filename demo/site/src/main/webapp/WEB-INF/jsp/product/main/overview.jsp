<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>


<ul class="thumbnails">

    <c:forEach var="product" items="${products.items}">
        <hst:link var="prdImgLink" hippobean="${product.mainImage.original}"/>
        <hst:link var="prdlink" hippobean="${product}"/>
        <hst:cmseditlink hippobean="${product}"/>

        <li class="span4">
            <div class="thumbnail well">
                <img src="${prdImgLink}" alt=""/><br/>

                <div class="caption">
                    <h4><c:out value="${product.name}"/></h4>

                    <p>
                        <c:if test="${not empty product.specialPrice}"><s></c:if>
                        <c:out value="${product.price0}"/>
                        <c:if test="${not empty product.specialPrice}"></s></c:if>
                        <c:if test="${not empty product.specialPrice}">&nbsp;|&nbsp;
                            <c:out value="${product.specialPrice}"/></c:if>
                    </p>
                    <kk:rating product="${product}" var="rating"/>
                    <fmt:formatNumber value="${rating * 10}" var="ratingStyle" pattern="#0"/>

                    <p class="rating stars-${ratingStyle}">
                        <a href="${fn:escapeXml(prdlink)}">
                            <span style="margin-left: 100px;"><c:out value="${rating}"/></span>
                        </a>
                    </p>
                    <p>
                        <br/>
                        <a class="btn btn-primary" href="${fn:escapeXml(prdlink)}">Learn more</a>
                    </p>
                </div>
            </div>
        </li>
    </c:forEach>

</ul>


<c:choose>
    <c:when test="${products.total eq 0}">
        <p id="results"><fmt:message key="search.results.noresults"/> '${query}'</p>
    </c:when>
    <c:otherwise>
        <tag:pagination pageableResult="${products}" queryName="query" queryValue="${query}"/>
    </c:otherwise>
</c:choose>


