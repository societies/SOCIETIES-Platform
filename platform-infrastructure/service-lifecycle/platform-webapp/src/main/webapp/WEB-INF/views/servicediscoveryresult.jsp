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

<%


//GET THE METHOD CALLED FROM THE FORM
Map model = request.getParameterMap();
String[] methodCalledArr = (String[]) model.get("method");
String methodCalled;
if(methodCalledArr != null)
	methodCalled = methodCalledArr[0];
else
	methodCalled = "GetLocalServices";

String node = "";
if (methodCalled.equals("GetServicesCis") || methodCalled.equals("ShareService") || methodCalled.equals("UnshareService")) {
	String[] nodeArr = (String[]) model.get("node");
	node = nodeArr[0];
}

List<Service> myServices = (List<Service>) request.getAttribute("services");
List<Service> cisServices = (List<Service>) request.getAttribute("cisservices");
String myNode = (String) request.getAttribute("myNode");

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

<form method="POST" action="servicecontrol.html" id="scForm" name="scForm" enctype="multipart/form-data">
<input type="hidden" name="service" id="service">
<input type="hidden" name="method" id="method">
<input type="hidden" name="endpoint" id="endpoint"/>
<input type="hidden" name="url" id="url" />
<input type="hidden" name="node" id="node" value="<%= node %>" />

<table border="1">
	<tr><td><B>Name</B></td>
	<td><B>Description</B></td>
	<td><B>Author</B></td>
	<td><B>Status</B></td>
	<td><B>Action</B></td>
</tr> 

<%
	for(Service myService : myServices ){

		if (methodCalled.equals("GetServicesCis") || methodCalled.equals("ShareService") || methodCalled.equals("UnshareService")) {
				
			if(!myService.getServiceType().equals(ServiceType.THIRD_PARTY_CLIENT) && (myService.getServiceInstance().getServiceImpl().getServiceClient() != null || myService.getServiceType().equals(ServiceType.DEVICE))){

				%>
				<tr>
			    <td><%= myService.getServiceName() %></td>
			    <td><%= myService.getServiceDescription() %></td>
				<td><%= myService.getAuthorSignature() %></td>
				<td><%= myService.getServiceStatus() %></td>      
				<td>
				<%
				if(isShared(myService,cisServices)){
					%>
					<input type="button" value="unshare" onclick="updateForm('<%=ServiceModelUtils.getServiceId64Encode(myService.getServiceIdentifier()) %>', 'UnshareService')" >		
					<%
				}else{					
					%>
					<input type="button" value="share" onclick="updateForm('<%=ServiceModelUtils.getServiceId64Encode(myService.getServiceIdentifier())%>', 'ShareService')" >
					<%
				}
				%>
				</td></tr>
				<% 
			}
				
		} else {
				
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
					
				}
				else{
					%>
					Device Management
					<%
				}
		            %>
					</td>	
		        </tr>
		    <%
			}

	}
	%>
	</table>
<%
//DISPLAY LIST OF SERVICES FROM CIS
if (methodCalled.equals("GetServicesCis") || methodCalled.equals("ShareService") || methodCalled.equals("UnshareService")) {
%>
	<p>&nbsp;</p>
    <p><b>Community Services: ${cis.name}</b></p>	
	<table border="1">
		<tr><td><B>Name</B></td>
		<td><B>Description</B></td>
		<td><B>Author</B></td>
		<td><B>Action</B></td>
		</tr> 
<% 

	for(Service cisService : cisServices ){		
		%>
		<tr>
    	<td><%= cisService.getServiceName() %></td>
     	<td><%= cisService.getServiceDescription() %></td>
        <td><%= cisService.getAuthorSignature() %></td>
        <td>
        <%        
        if(!haveClient(cisService,myServices)){
    		if(!cisService.getServiceType().equals(ServiceType.DEVICE)){
    			%>
    			<a href="service-privacy-policy-show.html?serviceId=<%=ServiceModelUtils.getServiceId64Encode(cisService.getServiceIdentifier())%>&serviceOwnerId=${node}" class="privacy-policy-handler">Privacy Policy</a>	
    			<input type="button" value="install" onclick="updateForm('<%=ServiceModelUtils.getServiceId64Encode(cisService.getServiceIdentifier())%>', 'Install3PService')" >  			
    			<%		
    		} else{
    			%>
    			Device 
    			<%
    			if(isMine(cisService,myNode)){
        			%>
        			is mine!
        			<%
    			} else {
    				%>
        			<input type="button" value="install" onclick="updateForm('<%=ServiceModelUtils.getServiceId64Encode(cisService.getServiceIdentifier())%>', 'Install3PService')" >  			
					<%
    			}		
    		}
        	
        } else{
        	%>
        	Client installed!
        	<%
        }
        %>
    	</td>
    	</tr>
    	<%
	}
	%>
	</table>
	<% 
} else{

	%>
	<h3>Install new service</h3>
	<input type="file" name="fileData">
	<input type="button" value="Install" onclick="updateForm('NONE', 'InstallService')" >
	<%
}
%>

<%! 
boolean isShared(Service myService, List<Service> sharedServices){
	
	boolean shared = false;
	for(Service sharedService: sharedServices){
		if(ServiceModelUtils.compare(myService.getServiceIdentifier(), sharedService.getServiceIdentifier()))
			return true;
	}
	
	return shared;
}
%>
<%!
boolean haveClient(Service sharedService, List<Service> installedServices){
	
	boolean clientInstalled = false;
	
	for(Service installedService: installedServices ){
		if(installedService.getServiceType().equals(ServiceType.THIRD_PARTY_CLIENT) && ServiceModelUtils.compare(sharedService.getServiceIdentifier(), installedService.getServiceInstance().getParentIdentifier()))
			return true;				
	}
	
	return clientInstalled;
}
%>

<%!
boolean isMine(Service sharedService, String myNode){
		
	return sharedService.getServiceInstance().getFullJid().equals(myNode);
}
%>
</form>	
<!-- .................END PLACE YOUR CONTENT ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>