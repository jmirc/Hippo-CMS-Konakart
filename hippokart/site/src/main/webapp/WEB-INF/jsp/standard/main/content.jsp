<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="document" type="org.onehippo.cms7.hst.hippokart.beans.TextDocument"--%>

<div class="row">

    <c:choose>
        <c:when test="${empty document}">
            <tag:pagenotfound/>
        </c:when>
        <c:otherwise>



            <c:if test="${not empty document.title}">
                <hst:element var="headTitle" name="title">
                    <c:out value="${document.title}"/>
                </hst:element>
                <hst:headContribution keyHint="headTitle" element="${headTitle}"/>
            </c:if>

            <hst:cmseditlink hippobean="${document}"/>
            <div class="span2">
                &nbsp;
            </div>
            <div class="span9">
                <div class="page-header">
                    <h1>${fn:escapeXml(document.title)}&nbsp;<small>${fn:escapeXml(document.summary)}</small></h1>
                </div>
                <div class="row">
                    <hst:html hippohtml="${document.html}"/>
                </div>
            </div>

        </c:otherwise>
    </c:choose>
</div>