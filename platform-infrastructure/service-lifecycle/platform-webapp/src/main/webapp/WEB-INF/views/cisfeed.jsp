<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies services - Service Discovery</title>
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

	<h3>CIS Activity Stream</h3>
		
<form:form method="POST" action="cisfeed.html" commandName="activityForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td>CIS Activity Methods: </td>
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
		Populate the fields below for Adding a feed
		<br />
		<table id="AddFeed">
			<tr>
				<td>Verb:</td>
				<td><form:input path="verb" /></td>
				<td><form:errors path="verb" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Actor:</td>
				<td><form:input path="actor" /></td>
				<td><form:errors path="actor" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Object:</td>
				<td><form:input path="object" /></td>
				<td><form:errors path="object" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Target:</td>
				<td><form:input path="target" /></td>
				<td><form:errors path="target" cssClass="error" /></td>
			</tr>
		</table>
		<br />
		Populate the fields below for querying a feed
		<br />
		<table id="queryFeed">
			<tr>
				<td>CSS ID:</td>
				<td><form:input path="cssId" /></td>
				<td><form:errors path="cssId" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Time Period:</td>
				<td><form:input path="timePeriod" /></td>
				<td><form:errors path="timePeriod" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Query:</td>
				<td><form:input path="query" /></td>
				<td><form:errors path="query" cssClass="error" /></td>
			</tr>	
			<tr>
				<td>Query:</td>
				<td><form:input path="query" /></td>
				<td><form:errors path="query" cssClass="error" /></td>
			</tr>			
	</form:form>
	
<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

