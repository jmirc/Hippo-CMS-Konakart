<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>

<div class="container">
    <div class="row">
        <div class="span2">
            <hst:include ref="leftmenu"/>
        </div>
        <div class="span8">
            <hst:include ref="content"/>
            <!-- the lists is a general 'slot' where items can be dropped in -->
            <hst:include ref="lists"/>
        </div>
        <div class="span2">
            <hst:include ref="right"/>
        </div>
    </div>
</div>
