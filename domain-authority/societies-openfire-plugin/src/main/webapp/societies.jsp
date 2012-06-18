<%@ page import="java.util.*,
                 org.jivesoftware.openfire.XMPPServer,
                 org.jivesoftware.util.*,
                 org.societies.da.openfire.plugin.SocietiesPlugin"
    errorPage="error.jsp"
%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<%-- Define Administration Bean --%>
<jsp:useBean id="admin" class="org.jivesoftware.util.WebManager"  />
<c:set var="admin" value="${admin.manager}" />
<% admin.init(request, response, session, application, out ); %>

<%  // Get parameters
    boolean save = request.getParameter("save") != null;
    boolean success = request.getParameter("success") != null;
    String secret = ParamUtils.getParameter(request, "secret");
    String allowedIPs = ParamUtils.getParameter(request, "allowedIPs");
    String cloudProviderUrls = ParamUtils.getParameter(request, "cloudProviderUrls");

    SocietiesPlugin plugin = (SocietiesPlugin) XMPPServer.getInstance().getPluginManager().getPlugin("societies");

    // Handle a save
    Map errors = new HashMap();
    if (save) {
        if (errors.size() == 0) {
        	plugin.setSecret(secret);
            plugin.setAllowedIPs(StringUtils.stringToCollection(allowedIPs));
            plugin.setCloudProviderUrls(StringUtils.stringToCollection(cloudProviderUrls));
            response.sendRedirect("societies.jsp?success=true");
            return;
        }
    }

    secret = plugin.getSecret();
    allowedIPs = StringUtils.collectionToString(plugin.getAllowedIPs());
    cloudProviderUrls = StringUtils.collectionToString(plugin.getCloudProviderUrls());
%>

<html>
    <head>
        <title>SOCIETIES Properties</title>
        <meta name="pageID" content="societies"/>
    </head>
    <body>


<p>
Use the form below to configure the secret key, the external IP addresses allowed to use the plugin HTTP API, and the URLs for the Cloud Node Providers recommended by this Domain Authority.
</p>
<p>
<strong>For the IPs and URLs use commas to separate between entries.</strong>
</p>

<%  if (success) { %>

    <div class="jive-success">
    <table cellpadding="0" cellspacing="0" border="0">
    <tbody>
        <tr><td class="jive-icon"><img src="images/success-16x16.gif" width="16" height="16" border="0"></td>
        <td class="jive-icon-label">
            SOCIETIES properties edited successfully.
        </td></tr>
    </tbody>
    </table>
    </div><br>
<% } %>

<form action="societies.jsp?save" method="post">

<fieldset>
    <legend>SOCIETIES</legend>
    <div>
        Secret key:
        <input type="text" name="secret" value="<%= secret %>" id="text_secret">
        <br><br>

        Allowed IP Addresses:
        <input type="text" name="allowedIPs" value="<%= ((allowedIPs != null) ? allowedIPs : "") %>" size="100">
        <br><br>
        
        SOCIETIES Cloud Node Provider URLs:
        <input type="text" name="cloudProviderUrls" value="<%= ((cloudProviderUrls != null) ? cloudProviderUrls : "") %>" size="100">
    </div>
</fieldset>

<br><br>

<input type="submit" value="Save Settings">
</form>


</body>
</html>