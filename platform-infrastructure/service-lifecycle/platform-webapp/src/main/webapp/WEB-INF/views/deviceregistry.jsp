<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies Devices - Device Registry</title>
<style>
.error {
	color: #ff0000;
}
 
.errorblock {
	color: #000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
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
<!-- .................PLACE YOUR CONTENT HERE ................ -->

	<h3>Device Registry</h3>
		
<form:form method="POST" action="deviceregistry.html" commandName="drForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td>Device Registry Methods: </td>
					<td><form:select path="method" >
					   <form:option value="NONE" label="--- Select ---" />
					   <form:options items="${methods}" />
					</form:select></td>
				<td><form:errors path="method" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td colspan="3"><input type="submit" /></td>
			</tr>
		</table>
		<br />
		Populate the fields below for Adding a Device
		<br />
		<table id="CSS Devices">
			<tr>
				<td>CSS Node ID:</td>
				<td><form:input path="cssNodeId" /></td>
				<td><form:errors path="cssNodeId" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Device Family ID:</td>
				<td><form:input path="deviceFamilyIdentity" /></td>
				<td><form:errors path="deviceFamilyIdentity" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Device Name:</td>
				<td><form:input path="deviceName" /></td>
				<td><form:errors path="deviceName" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Device Type:</td>
				<td><form:input path="deviceType" /></td>
				<td><form:errors path="deviceType" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Device Description:</td>
				<td><form:input path="deviceDescription" /></td>
				<td><form:errors path="deviceDescription" cssClass="error" /></td>
			</tr>
			<tr>
				<td>DeviceConnectionType:</td>
				<td><form:input path="deviceConnectionType" /></td>
				<td><form:errors path="deviceConnectionType" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Device Location:</td>
				<td><form:input path="deviceLocation" /></td>
				<td><form:errors path="deviceLocation" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Device Provider:</td>
				<td><form:input path="deviceProvider" /></td>
				<td><form:errors path="deviceProvider" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Device ID:</td>
				<td><form:input path="deviceID" /></td>
				<td><form:errors path="deviceID" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Context Source:</td>
				<td><form:checkbox path="contextSource" /></td>
				<td><form:errors path="contextSource" cssClass="error" /></td>
			</tr>
		</table>
		<br />
	</form:form>
	
<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

