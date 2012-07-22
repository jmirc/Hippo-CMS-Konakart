<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="currency" type="com.konakart.appif.CurrencyIf"--%>

<hst:headContribution category="css">
    <hst:link var="bootstrapswitchcss" path="/libs/bootstrap/css/${cssTheme}/bootstrap.css"/>
    <link id="switch_style" rel="stylesheet" href="${bootstrapswitchcss}" type="text/css"/>
</hst:headContribution>



<div class="span4 logo">
    <hst:link var="homeLink" path="/"/>
    <a href="${homeLink}">
        <h1>${headerName}</h1>
    </a>
</div>
<div class="span8">

    <div class="row">
        <div class="span1">&nbsp;</div>
        <div class="span2">
            <h4>Currency</h4>

            <c:forEach items="${currencies}" var="currency" varStatus="status">
                <hst:actionURL var="currencyLink">
                    <hst:param name="action" value="SELECT_CURRENCY"/>
                    <hst:param name="currencyCode" value="${currency.code}"/>
                </hst:actionURL>

                <a href="${currencyLink}">
                    <c:if test="${status.first}"><strong></c:if>
                    ${currency.code}
                    <c:if test="${status.first}"></strong></c:if>
                </a> <c:if test="${not status.last}">|
            </c:if>
            </c:forEach>
        </div>
        <div class="span2">

            <hst:link var="cartLink" siteMapItemRefId="cartDetailId"/>
            <a href="${cartLink}"><h4>Shopping Cart</h4></a>
            <a href="${cartLink}">
                <c:if test="${nbItems eq 0}">No items</c:if>
                <c:if test="${nbItems eq 1}">1 item - ${basketTotal}</c:if>
                <c:if test="${nbItems > 1}">${nbItems} item - ${basketTotal}</c:if>
            </a>
        </div>
        <div class="span3 customer_service">
            <h4>FREE delivery on ALL orders</h4>
            <h4><small>Customer service: 0800 8475 548</small></h4>
        </div>
    </div>
    <br />
    <div class="row">
        <div class="links pull-right">
            <c:if test="${not empty menu.siteMenuItems}">
                <c:forEach var="item" items="${menu.siteMenuItems}" varStatus="status">
                    <tag:mainmenuitem siteMenuItem="${item}"/> <c:if test="${not status.last}">|</c:if>
                </c:forEach>
            </c:if>
            <c:if test="${isLogged}">
                <hst:link path="/j_spring_security_logout" var="logout"/>
                | <a href="${logout}">Logout</a>
            </c:if>
        </div>

    </div>
</div>