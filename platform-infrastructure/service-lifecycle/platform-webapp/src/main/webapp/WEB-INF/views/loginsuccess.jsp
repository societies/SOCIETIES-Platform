<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
	import="java.util.*" %>
<%@ taglib prefix="c" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies user login</title>
</head>
<body>
	<h3>Hello: ${name}, ${result}, Today:  <%= new java.util.Date() %> </h3>
	
	<h4>Please click the service to use</h4>
	
	<table>
		<tr>
			<td><a href="servicediscovery.html">Service Discovery Service</a></td>
		</tr>
	</table>
	
</body>
</html>