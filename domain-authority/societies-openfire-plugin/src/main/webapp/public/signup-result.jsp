<%@ page import="java.util.*,
                 org.jivesoftware.openfire.XMPPServer,
                 org.jivesoftware.util.ParamUtils,
                 org.societies.da.openfire.plugin.SocietiesPlugin"
    errorPage="error.jsp"
%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<%  // Get parameters
    boolean success = request.getParameter("success") != null;
    String error = ParamUtils.getParameter(request, "error");

    SocietiesPlugin plugin = (SocietiesPlugin) XMPPServer.getInstance().getPluginManager().getPlugin("societies");
    Collection<String> cloudProviderUrls = plugin.getCloudProviderUrls();
%>

<html>
    <head>
        <title>SOCIETIES Signup</title>
        <meta name="pageID" content="signup-result"/>
        <!-- do not remove this meta declaration - it keeps this page from being shown with openfire "graphics" -->
		<meta content="none" name="decorator" />
		<link rel="stylesheet" type="text/css" href="style.css" />
    </head>
    <body>


<%  if (success) { %>
    <div class="">
    	<img src="images/accept.png">
		SOCIETIES signup performed successfully.
    </div>
    <div class="">
    	<p>You can now choose a SOCIETIES Cloud Container Provider:</p>
    	<% for (String url : cloudProviderUrls) {%>
    		<p><a href="<%= url %>" ><%= url %></a></p>
    	<% } %>
    </div>
<% }  else  { %>
	<div class="">
		<img src="images/cancel.png">
		SOCIETIES signup error: <%= error %>
    </div><br>
<% } %>

</body>
</html>