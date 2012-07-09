<!doctype html>
<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<html lang="en">
<head>
    <hst:defineObjects/>

    <meta charset="utf-8"/>

    <hst:link var="link" path="/css/style.css"/>
    <link rel="stylesheet" href="${link}" type="text/css"/>
    <hst:link var="ecommerceLink" path="/css/ecommerce.css"/>
    <link rel="stylesheet" href="${ecommerceLink}" type="text/css"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
    <script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.9/jquery.validate.min.js" type="text/javascript"></script>
    <script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.9/additional-methods.min.js" type="text/javascript"></script>
    <script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.9/localization/messages_${hstRequest.locale.language}.js" type="text/javascript"></script>
    <hst:headContributions categoryExcludes="scripts" xhtml="true"/>
</head>
<body>
<hst:include ref="header"/>
<hst:include ref="main"/>
<hst:headContributions categoryIncludes="scripts" xhtml="true"/>
</body>
</html>