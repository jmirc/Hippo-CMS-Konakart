<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="category" type="com.konakart.appif.CategoryIf"--%>
<%--@elvariable id="childCategory" type="com.konakart.appif.CategoryIf"--%>

<ul class="breadcrumb">
    <li>Categories</li>
</ul>
<div class="span3 product_list">

    <ul class="nav">
        <c:forEach var="category" items="${categoriesFacet}">
            <c:if test="${category.level eq 0}">
                <li>
                    <hst:link siteMapItemRefId="categoryRefId" var="selectCategory"/>
                    <a class="<c:if test="${category.selected}">active</c:if>" href="${selectCategory}/${category.id}">${category.name} (${category.numberOfProducts})</a>
                    <c:if test="${category.selected && fn:length(category.children) > 0}">
                        <ul>
                            <c:forEach var="childCategory" items="${category.children}">
                                <li>
                                    <hst:link siteMapItemRefId="categoryRefId" var="selectChildCategory"/>
                                    <a class="<c:if test="${childCategory.selected}">active</c:if>" href="${selectChildCategory}/${childCategory.id}"> - ${childCategory.name} (${childCategory.numberOfProducts})</a>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </li>
            </c:if>
        </c:forEach>
    </ul>
</div>

