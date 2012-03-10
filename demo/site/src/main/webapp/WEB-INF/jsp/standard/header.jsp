<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="headerName" type="java.lang.String"--%>

<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container">
            <a class="brand" href="#"><c:out value="${headerName}"/></a>

            <div class="nav-collapse">
                <fmt:message var="submitText" key="search.submit.text"/>
                <hst:link var="link" path="/search"/>
                <form action="${link}" method="POST" class="navbar-search pull-left">
                    <input type="text" name="query" class="search-query" placeholder="Search"/>
                    <input type="submit" value="${submitText}"/>
                </form>
            </div>
        </div>

    </div>


</div>



