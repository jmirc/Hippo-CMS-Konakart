<%@ taglib prefix='hst' uri="http://www.hippoecm.org/jsp/hst/core" %>

<%-- Keep the current url as the "destination", so the login page redirects to it once done. --%>
<hst:link fullyQualified="true" var="destination" navigationStateful="true" />
<hst:link var="redirectUrl" path="/myaccount">
    <hst:param name="needauth" value="true"/>
    <hst:param name="destination" value="${destination}"/>
</hst:link>

<html>
<head>
    <meta http-equiv="refresh" content="0;URL=${redirectUrl}" />
</head>
<body>
</body>
</html>