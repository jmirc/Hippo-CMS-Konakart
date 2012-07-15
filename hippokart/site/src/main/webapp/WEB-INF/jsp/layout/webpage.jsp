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

    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
    <script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.9/jquery.validate.min.js" type="text/javascript"></script>
    <script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.9/additional-methods.min.js" type="text/javascript"></script>
    <script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.9/localization/messages_${hstRequest.locale.language}.js" type="text/javascript"></script>

    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <hst:headContributions categoryExcludes="scripts" xhtml="true"/>


</head>
<body>
    <div class="container">
        <div class="row"><!-- start header -->
            <hst:include ref="header"/>
        </div>

        <hst:include ref="main"/>
        <hst:include ref="footer"/>
     </div>
<hst:headContributions categoryIncludes="scripts" xhtml="true"/>
</body>
</html>