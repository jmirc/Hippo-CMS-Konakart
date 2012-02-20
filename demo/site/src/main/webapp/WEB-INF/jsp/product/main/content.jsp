<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>

<ul>
    <c:forEach var="product" items="${products.items}">
        <hst:link var="prdlink" hippobean="${product}"/>

        <li class="overview-item">
            <hst:cmseditlink hippobean="${product}"/>
            <a href="${fn:escapeXml(prdlink)}"><c:out value="${product.name}"/></a>
            <br/>
            <c:out value="${product.price}"/> |
            <div class="rating stars-${product.rating}"><a href="${fn:escapeXml(prdlink)}"><c:out value="${product.rating}"/></a></div>
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


