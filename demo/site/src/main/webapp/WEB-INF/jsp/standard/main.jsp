<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<div class="container-fluid">
    <div class="row-fluid">
        <nav class="span2">
            <hst:include ref="leftmenu"/>
        </nav>
        <div class="span8">
            <hst:include ref="content"/>
            <hst:include ref="lists"/>
        </div>
        <aside class="span2">
            <hst:include ref="right"/>
        </aside>
    </div>
    <div class="row-fluid">
        <hr class="soften">
        <div class="span12 footer">
            <strong>Powered By Hippo CMS & KonaKart</strong>
        </div>
    </div>
</div>
