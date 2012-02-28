<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>

<ul>
    <c:forEach var="product" items="${products.items}">
        <hst:link var="prdlink" hippobean="${product}"/>

        <li class="overview-item">
            <hst:cmseditlink hippobean="${product}"/>
            <a href="${fn:escapeXml(prdlink)}"><c:out value="${product.name}"/></a>
            <br/>
            <c:if test="${not empty product.specialPrice}"><s></c:if>
            <c:out value="${product.price}"/>
            <c:if test="${not empty product.specialPrice}"></s></c:if>
            | <c:if test="${not empty product.specialPrice}"><c:out value="${product.specialPrice}"/></c:if>
            <fmt:formatNumber value="${product.rating * 10}" var="ratingStyle" pattern="#0"/>
            <div class="rating stars-${ratingStyle}"><a href="${fn:escapeXml(prdlink)}"><c:out value="${product.rating}"/></a></div>
            <br/>
            <br/>
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


