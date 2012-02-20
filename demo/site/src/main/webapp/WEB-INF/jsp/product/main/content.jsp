<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>

<c:out value="${products.items}"/>

<c:forEach items="${products.items}" var="item">
    <hst:link var="link" hippobean="${item}"/>
    <hst:cmseditlink hippobean="${item}"/>
    <a href="${link}">${item.product.name}</a>
</c:forEach>
