<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="menu" type="org.hippoecm.hst.core.sitemenu.HstSiteMenu"--%>

<div class="span12">
    <div class="navbar">
        <div class="navbar-inner">
            <div class="container" style="width: auto;">
                <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </a>

                <div class="nav-collapse">
                    <c:if test="${not empty menu.siteMenuItems}">
                        <ul class="nav">
                            <c:forEach var="item" items="${menu.siteMenuItems}">
                                <tag:menuitem siteMenuItem="${item}"/>
                            </c:forEach>
                        </ul>
                    </c:if>
                    <ul class="nav pull-right">
                        <li class="divider-vertical"></li>
                        <hst:link var="link" path="/search"/>
                        <form class="navbar-search" action="${link}" method="post">
                            <input type="text" class="search-query span2" placeholder="Search">
                            <button class="btn btn-primary btn-small search_btn" type="submit">Go</button>
                        </form>

                    </ul>
                </div>
                <!-- /.nav-collapse -->
            </div>
        </div>
        <!-- /navbar-inner -->
    </div>
    <!-- /navbar -->
</div>