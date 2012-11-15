<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
	import="java.util.*,org.societies.api.internal.servicelifecycle.*,org.societies.api.schema.servicelifecycle.model.*" %>
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

<a href="javascript:history.back()">&lt;--back</a>
<h4>${result}</h4>
<script language="javascript">
function updateForm(serviceID, toDo) {
	document.forms["scForm"]["service"].value = serviceID;
	document.forms["scForm"]["method"].value = toDo;
	document.forms["scForm"].submit();
} 
</script>

<form method="POST" action="servicecontrol.html" id="scForm" name="scForm" enctype="multipart/form-data">
<input type="hidden" name="service" id="service">
<input type="hidden" name="method" id="method">
<input type="hidden" name="endpoint" id="endpoint"/>
<input type="hidden" name="url" id="url" />

<table border="1">
<tr><td><B>Name</B></td>
	<td><B>Description</B></td>
	<td><B>Author</B></td>
	<td><B>Status</B></td>
	<td><B>Action</B></td>
</tr> 

<%

List<Service> myServices = (List<Service>) request.getAttribute("services");

for(Service myService : myServices ){
	%>
	<tr>
	<td><%= myService.getServiceName() %></td>
 	<td><%= myService.getServiceDescription() %></td>
    <td><%= myService.getAuthorSignature() %></td>
    <td><%= myService.getServiceStatus() %></td>      
    <td>
	 <%
	
	if(!myService.getServiceType().equals(ServiceType.DEVICE)){
		if(myService.getServiceType().equals(ServiceType.THIRD_PARTY_SERVER)){
			%>
			<a href="service-privacy-policy-show.html?serviceId=<%=ServiceModelUtils.getServiceId64Encode(myService.getServiceIdentifier())%>&serviceOwnerId=${node}" class="privacy-policy-handler">Privacy Policy</a>	
			<%
		} else{
    		%>
			<a href="service-privacy-policy-show.html?serviceId=<%=ServiceModelUtils.getServiceId64Encode(myService.getServiceInstance().getParentIdentifier())%>&serviceOwnerId=${node}" class="privacy-policy-handler">Privacy Policy</a>	
			<%
		}
		if(myService.getServiceStatus().equals(ServiceStatus.STARTED)){
			%>
			<input type="button" value="stop" onclick="updateForm('<%=ServiceModelUtils.getServiceId64Encode(myService.getServiceIdentifier())%>', 'StopService')" >
			<%
		} else{
			%>
			<input type="button" value="start" onclick="updateForm('<%=ServiceModelUtils.getServiceId64Encode(myService.getServiceIdentifier())%>', 'StartService')" >
			<!-- 		<input type="button" value="uninstall" onclick="updateForm('${service.getServiceIdentifier().getServiceInstanceIdentifier()}' + '_' + '${service.getServiceIdentifier().getIdentifier().toString()}', 'UninstallService')" > -->
			<%
		}
		%>
		<input type="button" value="uninstall" onclick="updateForm('<%=ServiceModelUtils.getServiceId64Encode(myService.getServiceIdentifier())%>', 'UninstallService')" >
		<%
	} else{
		%>
		Device Management
		<%
	}
    %>
	</td>	
	</tr>
	<%
}
%>
</table>

<h3>Install new service</h3>
<input type="file" name="fileData">
<input type="button" value="Install" onclick="updateForm('NONE', 'InstallService')" >

	
</form>	
<!-- .................END PLACE YOUR CONTENT ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>