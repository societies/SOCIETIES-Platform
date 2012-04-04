<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies services - Service Control</title>
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
<!-- .................PLACE YOUR CONTENT BELOW HERE ................ -->

	<h3>Service Control Service</h3>
		
<form:form method="POST" action="servicecontrol.html" commandName="scForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td>Service Control Methods: </td>
					<td><form:select path="method" >
					   <form:option value="NONE" label="--- Select ---" />
					   <form:options items="${methods}" />
					</form:select></td>
				<td><form:errors path="method" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>Services: </td>
					<td><form:select path="service" >
					   <form:option value="NONE" label="--- Select ---" />
					   <form:option value="REMOTE" label="-- Remote Service --" />
					   <form:options items="${services}" />
					</form:select></td>
				<td><form:errors path="service" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>Remote Service Endpoint: </td>
				<td><form:input path="endpoint" />
				</td>
				<td><form:errors path="endpoint" cssClass="error" />
				</td>
			</tr>			
			<tr>
				<td>Bundle Url: </td>
				<td><form:input path="url" />
				</td>
				<td><form:errors path="url" cssClass="error" />
				</td>
			</tr>						
			<tr>
				<td>Node to Install: </td>
				<td><form:input path="node" />
				</td>
				<td><form:errors path="node" cssClass="error" />
				</td>
			</tr>					
			<tr>
				<td colspan="3"><input type="submit" /></td>
			</tr>
		</table>		
	</form:form>	

<!-- .................END PLACE YOUR CONTENT ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->	
</body>
</html>

