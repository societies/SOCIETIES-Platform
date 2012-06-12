<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
	import="java.util.*" %>
<%@ taglib prefix="c" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies Privacy Assessment Result</title>
</head>
<body>
	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->
<!-- .................PLACE YOUR CONTENT BELOW HERE ................ -->

	<h3>Privacy Assessment Results</h3>
	<br />
	<table>
		<tr>
			<td><b>Sender</b></td>
			<td><b>CorrWithDataAccessByAll</b></td>
			<td><b>CorrWithDataAccessBySender</b></td>
			<td><b>Sender</b></td>
			<td><b>Sender</b></td>
			<td><b>Sender</b></td>
		</tr>

		<xc:forEach var="assessmentResult" items="${assessmentResults}">
			<tr>
				<td>${assessmentResult.sender}</td>
				<td>${assessmentResult.corrWithDataAccessByAll}</td>
				<td>${assessmentResult.corrWithDataAccessBySender}</td>
				<td>${assessmentResult.sender}</td>
				<td>${assessmentResult.sender}</td>
				<td>${assessmentResult.sender}</td>
			</tr>
		</xc:forEach>

	</table>

<!-- .................END PLACE YOUR CONTENT ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>