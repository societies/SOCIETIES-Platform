<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="java.util.*"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies Css Manager Result</title>
</head>
<body>
<body>

	<h4>${methodcalled}</h4>
	<br />
	<h4>${res}</h4>
	<br />
	<h4>${error}</h4>
	<br />
	<Table>
		<xc:forEach var="cssServiceDetails" items="${cssServiceDetails}">
			<tr>
				<td>${cssServiceDetails.serviceName}</td>
				<td>${cssServiceDetails.serviceDescription}</td>
				<td>${cssServiceDetails.authorSignature}</td>
				<td>${cssServiceDetails.serviceEndpoint}</td>
				<td>${cssServiceDetails.serviceType}</td>
				<td>${cssServiceDetails.serviceStatus}</td>
			</tr>
		</xc:forEach>
	</Table>
	<br />
	<Table>
		<xc:forEach var="cssAdDetails" items="${cssAdDetails}">
			<tr>
				<td>${cssAdDetails.name}</td>
				<td>${cssAdDetails.id}</td>
				<td>${cssAdDetails.uri}</td>

			</tr>
		</xc:forEach>
	</Table>
	<br />
	<br />
	<br />
	<br />

	<h4>Please click the service to use .....</h4>

	<table>
		<tr>
			<td><a href="servicediscovery.html">Service Discovery
					Service</a></td>
		</tr>
		<tr>
			<td><a href="servicecontrol.html">Service Control Service</a></td>
		</tr>
		<tr>
			<td><a href="cssmanager.html">Css Manager Service</a></td>
		</tr>
	</table>
</body>
</html>