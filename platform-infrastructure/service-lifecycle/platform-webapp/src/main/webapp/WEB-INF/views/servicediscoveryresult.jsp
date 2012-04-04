<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
	import="java.util.*" %>
<%@ taglib prefix="c" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies Service Discovery Result</title>
</head>
<body>
	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->
<!-- .................PLACE YOUR CONTENT BELOW HERE ................ -->

<h4>${result}</h4>
<br/>
<br/>
<Table>
<tr><td><B>Name</B></td><td><B>Description</B></td><td><B>Author</B></td><td><B>Endpoint</B></td>
<td><B>Type</B></td><td><B>Status</B></td></tr> 

	<xc:forEach var="service" items="${services}">
        <tr>
        	<td>${service.serviceName}</td>
         	<td>${service.serviceDescription}</td>
            <td>${service.authorSignature}</td>
            <td>${service.serviceEndpoint}</td>            
            <td>${service.serviceType}</td>
            <td>${service.serviceStatus}</td>
        </tr>
    </xc:forEach>
    	
	</table>
	
<!-- .................END PLACE YOUR CONTENT ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>