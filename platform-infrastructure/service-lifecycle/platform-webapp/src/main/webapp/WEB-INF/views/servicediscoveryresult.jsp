<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
	import="java.util.*" %>
<%@ taglib prefix="c" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies Service Discovery Result</title>
<style>
.privacy-policy-handler{
	display: inline-block;
	padding: 1px 4px;
	border: 1px solid gray;
	border-radius: 2px;
	background-color: #D3D3D3;
	color: black;
	text-decoration: none;
	font-family: arial;
	font-size: 0.9em;
}
</style>
</head>
<body>
	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->
<!-- .................PLACE YOUR CONTENT BELOW HERE ................ -->

<%
//GET THE METHOD CALLED FROM THE FORM
Map model = request.getParameterMap();
String[] methodCalledArr = (String[]) model.get("method");
String methodCalled = methodCalledArr[0];

String node = "";
if (methodCalled.equals("GetServicesCis") || methodCalled.equals("ShareService")) {
	String[] nodeArr = (String[]) model.get("node");
	node = nodeArr[0];
}
%>
<a href="javascript:history.back()">&lt;--back</a>
<h4>${result}</h4>
<script language="javascript">
function updateForm(serviceID, toDo) {
	document.forms["scForm"]["service"].value = serviceID;
	document.forms["scForm"]["method"].value = toDo;
	document.forms["scForm"].submit();
} 
</script>

<form method="POST" action="servicecontrol.html" id="scForm" name="scForm">
<input type="hidden" name="service" id="service">
<input type="hidden" name="method" id="method">
<input type="hidden" name="endpoint" id="endpoint"/>
<input type="hidden" name="url" id="url" />
<input type="hidden" name="node" id="node" value="<%= node %>" />

<table border="1">
<tr><td><B>Name</B></td><td><B>Description</B></td>
	<td><B>Author</B></td>
	<td><B>Status</B></td>
	<td><B>Action</B></td>
</tr> 

	<xc:forEach var="service" items="${services}">
        <tr>
        	<td>${service.serviceName}</td>
         	<td>${service.serviceDescription}</td>
            <td>${service.authorSignature}</td>
            <td>${service.serviceStatus}</td>      
            <td>
            <xc:if test="${service.serviceType != 'DEVICE'}">
			<a href="service-privacy-policy-show.html?serviceId=${service.getServiceIdentifier().getServiceInstanceIdentifier()}&serviceOwnerId=${node}" class="privacy-policy-handler">Privacy Policy</a>
            </xc:if>
            <%
			if (methodCalled.equals("GetServicesCis")) {
			%>
				<input type="button" value="share" onclick="updateForm('${service.getServiceIdentifier().getServiceInstanceIdentifier()}' + '_' + '${service.getServiceIdentifier().getIdentifier().toString()}', 'ShareService')" >
			<%
			} else {
			%>
			 <xc:if test="${service.serviceType == 'DEVICE'}">
				<a href="${pageContext.request.contextPath}/devicemgmt.html">Device Management</a>
			</xc:if>
			<xc:if test="${service.serviceType != 'DEVICE'}">
				<input type="button" value="start" onclick="updateForm('${service.getServiceIdentifier().getServiceInstanceIdentifier()}' + '_' + '${service.getServiceIdentifier().getIdentifier().toString()}', 'StartService')" >
				<input type="button" value="stop" onclick="updateForm('${service.getServiceIdentifier().getServiceInstanceIdentifier()}' + '_' + '${service.getServiceIdentifier().getIdentifier().toString()}', 'StopService')" >
				<input type="button" value="uninstall" onclick="updateForm('${service.getServiceIdentifier().getServiceInstanceIdentifier()}' + '_' + '${service.getServiceIdentifier().getIdentifier().toString()}', 'UninstallService')" >
			</xc:if>
			<%
			}
            %>
			</td>
        </tr>
    </xc:forEach>

	</table>
<%
//DISPLAY LIST OF SERVICES FROM CIS
if (methodCalled.equals("GetServicesCis") || methodCalled.equals("ShareService")) {
%>
	<p>&nbsp;</p>
    <p><b>Community Services: <%= node %></b></p>	
	<table border="1">
		<tr><td><B>Name</B></td><td><B>Description</B></td>
			<td><B>Author</B></td>
			<td><B>Status</B></td>
			<td><B>Action</B></td>
		</tr> 

	<xc:forEach var="service" items="${cisservices}">
        <tr>
        	<td>${service.serviceName}</td>
         	<td>${service.serviceDescription}</td>
            <td>${service.authorSignature}</td>
            <td>${service.serviceStatus}</td>
            <td><input type="button" value="install" onclick="updateForm('${service.getServiceIdentifier().getServiceInstanceIdentifier()}' + '_' + '${service.getServiceIdentifier().getIdentifier().toString()}', 'Install3PService')" >
			</td>
        </tr>
    </xc:forEach>
	</table>
<% 
} 
%>
	
</form>	
<!-- .................END PLACE YOUR CONTENT ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>