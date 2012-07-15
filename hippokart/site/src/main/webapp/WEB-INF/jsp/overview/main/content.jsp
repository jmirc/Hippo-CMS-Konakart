<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="crPage" type="java.lang.Integer"--%>
<%--@elvariable id="info" type="org.example.componentsinfo.GeneralListInfo"--%>
<%--@elvariable id="page" type="java.util.Collection<java.lang.Integer>"--%>
<%--@elvariable id="pages" type="java.util.Collection<java.lang.Integer>"--%>
<%--@elvariable id="result" type="org.hippoecm.hst.content.beans.query.HstQueryResult"--%>

<c:choose>
    <c:when test="${empty info}">
        <tag:pagenotfound/>
    </c:when>
    <c:otherwise>
        <c:if test="${not empty info.title}">
            <hst:element var="headTitle" name="title">
                <c:out value="${info.title}"/>
            </hst:element>
            <hst:headContribution keyHint="headTitle" element="${headTitle}"/>
        </c:if>

        <div class="page-header">
            <h1> ${fn:escapeXml(info.title)}
                <small><c:if test="${not empty result.totalSize}"> Total results ${result.totalSize}</c:if></small>
            </h1>
        </div>

        <c:forEach var="item" items="${result.hippoBeans}">
            <hst:link var="link" hippobean="${item}"/>
            <article>
                <hst:cmseditlink hippobean="${item}"/>
                <h3><a href="${link}">${fn:escapeXml(item.title)}</a></h3>
                <c:if test="${hst:isReadable(item, 'date.time')}">
                    <p class="badge badge-info">
                        <fmt:formatDate value="${item.date.time}" type="both" dateStyle="medium"
                                        timeStyle="short"/>
                    </p>
                </c:if>
                <p>${fn:escapeXml(item.summary)}</p>
            </article>
        </c:forEach>

        <!--if there are pages on the request, they will be printed by the tag:pages -->
        <tag:pages pages="${pages}" page="${page}"/>

    </c:otherwise>
</c:choose>