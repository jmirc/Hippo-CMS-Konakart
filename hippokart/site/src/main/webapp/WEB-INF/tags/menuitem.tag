<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="siteMenuItem" type="org.hippoecm.hst.core.sitemenu.HstSiteMenuItem" rtexprvalue="true"
              required="true" %>

<c:choose>
    <c:when test="${siteMenuItem.selected || siteMenuItem.expanded}">
        <c:choose>
            <c:when test="${siteMenuItem.expanded and not empty siteMenuItem.childMenuItems}">
                <li class="active dropdown">
                    <hst:link var="link" link="${siteMenuItem.hstLink}"/>
                    <a href="${link}" class="dropdown-toggle" data-toggle="dropdown">${fn:escapeXml(siteMenuItem.name)}
                        <b class="caret"></b>
                        <ul class="dropdown-menu">
                            <c:forEach var="child" items="${siteMenuItem.childMenuItems}">
                                <tag:menuitem siteMenuItem="${child}"/>
                            </c:forEach>
                        </ul>
                    </a>
                </li>
            </c:when>
            <c:otherwise>
                <li class="active">
                    <hst:link var="link" link="${siteMenuItem.hstLink}"/>
                    <a href="${link}">${fn:escapeXml(siteMenuItem.name)}</a>
                </li>
            </c:otherwise>
        </c:choose>


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
        <li class="<c:if test="${siteMenuItem.expanded and not empty siteMenuItem.childMenuItems}">dropdown</c:if>">
            <a href="${link}" class="dropdown-toggle" data-toggle="dropdown">${fn:escapeXml(siteMenuItem.name)}
                <c:if test="${siteMenuItem.expanded and not empty siteMenuItem.childMenuItems}">
                    <b class="caret"></b>
                    <ul>
                        <c:forEach var="child" items="${siteMenuItem.childMenuItems}">
                            <tag:menuitem siteMenuItem="${child}"/>
                        </c:forEach>
                    </ul>
                </c:if>
            </a>
        </li>
    </c:otherwise>
</c:choose>