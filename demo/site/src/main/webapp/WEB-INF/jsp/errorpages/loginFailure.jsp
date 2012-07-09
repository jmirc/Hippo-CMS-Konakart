<%@page import="org.hippoecm.hst.security.servlet.LoginServlet"%>
<%@ taglib prefix='hst' uri="http://www.hippoecm.org/jsp/hst/core" %>

<hst:link var="redirectUrl" path="/myaccount">
    <hst:param name="loginError" value="true"/>
</hst:link>
<html>
    <head>
       <meta http-equiv="refresh" content="0;URL=${redirectUrl}" />
    </head>
<body>
</body>
</html>
