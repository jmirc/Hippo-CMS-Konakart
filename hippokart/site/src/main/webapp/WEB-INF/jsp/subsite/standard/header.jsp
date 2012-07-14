<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="headerName" type="java.lang.String"--%>
<div class="container-fluid">
  <div class="row-fluid">
    <div class="span2"></div>
    <div class="span8">
      <div class="navbar">
        <div class="navbar-inner">
          <h1 class="container">
            <a class="brand" href="<hst:link path="/" />"><c:out value="${headerName}"/></a>
          </h1>
        </div>
      </div>
    </div>
    <div class="span2"></div>
  </div>
</div>