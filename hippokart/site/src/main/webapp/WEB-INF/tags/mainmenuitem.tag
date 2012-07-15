<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="siteMenuItem" type="org.hippoecm.hst.core.sitemenu.HstSiteMenuItem" rtexprvalue="true"
              required="true" %>

<c:choose>
    <c:when test="${siteMenuItem.selected}">
        <strong><a href="#">${fn:escapeXml(siteMenuItem.name)}</a></strong>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${empty siteMenuItem.externalLink}">
                <hst:link var="link" link="${siteMenuItem.hstLink}"/>
            </c:when>
            <c:otherwise>
                <c:set var="link" value="${fn:escapeXml(siteMenuItem.externalLink)}"/>
            </c:otherwise>
        </c:choose>
        <a href="${link}">${fn:escapeXml(siteMenuItem.name)}</a>
    </c:otherwise>
</c:choose>