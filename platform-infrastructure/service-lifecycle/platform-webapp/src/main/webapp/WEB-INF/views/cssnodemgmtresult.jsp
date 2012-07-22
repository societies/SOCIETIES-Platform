<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
	import="java.util.*" %>
<%@ taglib prefix="c" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies CSS Node Result</title>
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
<Table border="1">
<tr><td><B>CSS NodeID</B></td><td><B>CSS Node Status</B></td><td><B>CSS Node Type</B></td><td><B>CSS Node MAC</B></td><td><B>CSS Node Interactable</B></td></tr> 

	<xc:forEach var="cssnode" items="${cssNodes}">
        <tr>
        	<td>${cssnode.identity}</td>
         	<td>${cssnode.status}</td>
            <td>${cssnode.type}</td>
            <td>${cssnode.cssNodeMAC}</td>
            <td>${cssnode.interactable}</td>
        </tr>
    </xc:forEach>
    	
	</table>
	
<!-- .................END PLACE YOUR CONTENT ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>