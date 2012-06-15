<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
	import="java.util.*" %>
<%@ taglib prefix="c" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies CIS Results</title>
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
<tr><td><B>Name</B></td><td><B>ID</B></td></tr>

	<xc:forEach var="record" items="${cisrecords}">
        <tr>
        	<td>${record.getName()}&nbsp;</td>
        	<td>${record.getCisId()}&nbsp;</td>
        </tr>
    </xc:forEach>
    	
	</table>
	
	
<table>
<tr><td><B>Participant</B></td><td><B>Role</B></td></tr>

	<xc:forEach var="record" items="${memberRecords}">
        <tr>
        	<td>${record.getMembersJid()}&nbsp;</td>
        	<td>${record.getMembershipType()}&nbsp;</td>
        </tr>
    </xc:forEach>
    	
	</table>
	
	
	<h4>${res}</h4>
	
<!-- .................END PLACE YOUR CONTENT ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>