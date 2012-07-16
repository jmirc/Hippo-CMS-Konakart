<!doctype html>
<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<html lang="en">
<head>
    <hst:defineObjects/>

    <meta charset="utf-8"/>
    <hst:link var="maincss" path="/css/main.css"/>
    <link rel="stylesheet" href="${maincss}" type="text/css"/>

    <hst:link var="bootstrapcss" path="/libs/bootstrap/css/bootstrap.css"/>
    <link rel="stylesheet" href="${bootstrapcss}" type="text/css"/>

    <hst:link var="bootstrapswitchcss" path="/libs/bootstrap/css/united/bootstrap.css"/>
    <link id="switch_style" rel="stylesheet" href="${bootstrapswitchcss}" type="text/css"/>

    <hst:headContributions categoryIncludes="css" xhtml="true"/>


    <hst:link path="/libs/jquery/jquery.js" var="jqueryJs"/>
    <script src="${jqueryJs}" type="text/javascript"></script>

    <hst:headContribution category="homeScripts">
        <hst:link path="/libs/jquery-validation/jquery.validate.min.js" var="jqueryValidateJs"/>
        <script src="${jqueryValidateJs}" type="text/javascript"></script>
    </hst:headContribution>

    <hst:headContribution category="homeScripts">
        <hst:link path="/libs/jquery-validation/additional-methods.min.js" var="additionalMethodsJs"/>
        <script src="${additionalMethodsJs}" type="text/javascript"></script>
    </hst:headContribution>

    <c:if test="${hstRequest.locale.language != 'en'}">
        <hst:headContribution category="homeScripts">
            <hst:link path="/libs/jquery-validation/localization/messages_${hstRequest.locale.language}.js" var="jqueryValidateLocaleJs"/>
            <script src="${jqueryValidateLocaleJs}" type="text/javascript"></script>
        </hst:headContribution>
    </c:if>

    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>


</head>
<body>
    <div class="container">
        <div class="row"><!-- start header -->
            <hst:include ref="header"/>
        </div>

        <hst:include ref="main"/>
        <hst:include ref="footer"/>
     </div>
<hst:headContributions categoryIncludes="homeScripts" xhtml="true"/>
<hst:headContributions categoryIncludes="scripts" xhtml="true"/>
</body>
</html>