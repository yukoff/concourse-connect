<%@ page isErrorPage="true" contentType="text/html" %>
<%@page import="org.apache.commons.logging.Log"%>
<%@page import="org.apache.commons.logging.LogFactory"%>
<%!
  private static Log LOG = LogFactory.getLog(com.concursive.connect.web.modules.welcome.servlets.WelcomeServlet.class);
%>
<%
  LOG.error("Request failed (" + pageContext.getErrorData().getStatusCode() + ") - " + pageContext.getErrorData().getRequestURI());
  LOG.error("Application Server Error", pageContext.getErrorData().getThrowable());
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Error Page</title>
    </head>
    <body>
      <h1>An Error Has Occurred</h1>
      <p>
        Sorry, but things didn't work out as planned. As much information about
        the problem has been logged for the administrator.
      </p>
      <p>
        If the problem continues please contact the administrator.
      </p>
    </body>
</html>
