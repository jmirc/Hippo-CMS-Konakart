<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <hst:headContributions categoryExcludes="scripts"/>
    <hst:link var="styleLink" path="/css/style.css"/>
    <link rel="stylesheet" href="${styleLink}" type="text/css"/>
    <hst:link var="bootstrapLink" path="/css/bootstrap.css"/>
    <link rel="stylesheet" href="${bootstrapLink}" type="text/css"/>

    <style type="text/css">
        body {
            padding-top: 60px;
            padding-bottom: 40px;
        }
        .sidebar-nav {
            padding: 9px 0;
        }
    </style>

</head>
<body>
<hst:include ref="header"/>
<hst:include ref="main"/>
<hst:headContributions categoryIncludes="scripts"/>
</body>
</html>
