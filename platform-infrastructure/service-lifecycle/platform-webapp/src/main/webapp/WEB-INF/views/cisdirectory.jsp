<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies Adverts - CIS Directory</title>
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

	<h3>CIS Directory Service</h3>
		
<form:form method="POST" action="cisdirectory.html" commandName="cdForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td>CIS Directory Methods :</td>
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
		Populate the fields below for Adding a CIS Advertisement
		<br />
		<table id="CISAdverts">
			<tr>
				<td>CIS Name:</td>
				<td><form:input path="name" /></td>
				<td><form:errors path="name" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Owner CSS Id:</td>
				<td><form:input path="cssownerid" /></td>
				<td><form:errors path="cssownerid" cssClass="error" /></td>
			</tr>
			<tr>
				<td>CIS Type:</td>
				<td><form:input path="type" /></td>
				<td><form:errors path="type" cssClass="error" /></td>
			</tr>
			<tr>
				<td>CIS ID:</td>
				<td><form:input path="id" /></td>
				<td><form:errors path="id" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Attribute:</td>
				<td><form:input path="attrib" /></td>
				<td><form:errors path="attrib" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Operator:</td>
				<td><form:input path="operator" /></td>
				<td><form:errors path="operator" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Value 1:</td>
				<td><form:input path="value1" /></td>
				<td><form:errors path="value1" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Value 2:</td>
				<td><form:input path="value2" /></td>
				<td><form:errors path="value2" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Rank:</td>
				<td><form:input path="rank" /></td>
				<td><form:errors path="rank" cssClass="error" /></td>
			</tr>
			
			</table>
	</form:form>
	
<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

