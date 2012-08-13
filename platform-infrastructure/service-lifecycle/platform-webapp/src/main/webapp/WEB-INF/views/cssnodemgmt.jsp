<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies Nodes - CSS Nodes</title>
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

	<h3>CSS Node Management</h3>
		
<form:form method="POST" action="cssnodemgmt.html" commandName="cnForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td>CSS Node Methods :</td>
					<td><form:select path="method" >
					   <form:option value="NONE" label="--- Select ---" />
					   <form:options items="${methods}" />
					</form:select></td>
				<td><form:errors path="method" cssClass="error" />
				</td>
			</tr>	
			Populate the fields below for Adding a Node
		<br />
		<table id="CSS Nodes">
			<tr>
				<td>CSS Node ID:</td>
				<td><form:input path="cssNodeId" /></td>
				<td><form:errors path="cssNodeId" cssClass="error" /></td>
			</tr>
			<tr>
				<td>CSS Node Status:</td>
				<td><form:select path="nodestatus" >
					   <form:option value="NONE" label="--- Select ---" />
					   <form:options items="${nodestatus}" />
					</form:select></td>
				<td><form:errors path="nodestatus" cssClass="error" />
			</tr>
			<tr>
				<td>CSS Node Type</td>
				<td><form:select path="nodetypes" >
					   <form:option value="NONE" label="--- Select ---" />
					   <form:options items="${nodetypes}" />
					</form:select></td>
				<td><form:errors path="nodetypes" cssClass="error" />
			</tr>
			<tr>
				<td>CSS Node MAC Address</td>
				<td><form:input path="cssNodeMAC" /></td>
				<td><form:errors path="cssNodeType" cssClass="error" /></td>
			</tr>
			<tr>
				<td>CSS Node Interactable</td>
				<td><form:select path="interactable" >
						<form:option value="NONE" label="--- Select ---" />
						<form:options items="${nodeinteractable}" />
					</form:select></td>
				<td><form:errors path="interactable" cssClass="error" /></td>
			</tr>
		</table>
			<tr>
				<td colspan="3"><input type="submit" /></td>
			</tr>
		</table>		
	</form:form>
	
<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

