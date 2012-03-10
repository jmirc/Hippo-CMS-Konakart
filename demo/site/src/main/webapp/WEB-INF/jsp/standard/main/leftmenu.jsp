<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="menu" type="org.hippoecm.hst.core.sitemenu.HstSiteMenu"--%>

<div class="well sidebar-nav">
    <ul class="nav nav-list">
        <c:forEach var="item" items="${menu.siteMenuItems}">
            <li>
                <tag:menuitem siteMenuItem="${item}"/>
            </li>
        </c:forEach>
    </ul>
</div>
