<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies services - Privacy Assessment</title>
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

	<h3>Privacy Assessment</h3>
	
	<h4>View Results</h4>

	<form:form method="POST" action="privacy-assessment.html"
		commandName="assForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td>Show for</td>
				<td><form:select path="assessmentSubjectType">
						<!--form:option value="NONE" label="--- Select ---" /-->
						<form:options items="${assessmentSubjectTypes}" />
					</form:select></td>
				<td><form:errors path="assessmentSubjectType" cssClass="error" /></td>
			</tr>
			<tr>
				<td>Presentation format</td>
				<td><form:select path="presentationFormat">
						<!--form:option value="NONE" label="--- Select ---" /-->
						<form:options items="${presentationFormats}" />
					</form:select></td>
				<td><form:errors path="presentationFormat" cssClass="error" /></td>
			<tr/>
			<tr>
				<td>Identities / classes</td>
				<td><form:select path="assessmentSubject">
						<!--form:option value="NONE" label="--- Select ---" /-->
						<form:options items="${assessmentSubjects}" />
					</form:select></td>
				<td><form:errors path="assessmentSubject" cssClass="error" /></td>
			<tr/>
			<tr>
				<td colspan="3"><input type="submit" /></td>
			</tr>
		</table>
		
	</form:form>

	<h4>Settings</h4>
	<a href="privacy-assessment-settings.html">Assessment Settings and Control</a>

	<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

