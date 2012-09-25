<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
	import="java.util.*" %>
<%@ taglib prefix="c" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies CIS Directory Result</title>
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
<script language="javascript">
	function updateForm(cisId) {    
		document.forms["cisDirectory"]["cisJid"].value = cisId;
		document.forms["cisDirectory"].submit();
	} 
</script>

<form id="cisDirectory" name="cisDirectory" method="post" action="cismanager.html">
<input type="hidden" name="cisJid" id="cisJid">
<input type="hidden" name="method" id="method" value="JoinRemoteCIS">

<Table border="1">
<tr><td><B>CIS Name</B></td><td><B>CSS Owner ID</B></td><td><B>CIS Type</B></td><td><B>CIS ID</B></td><td><B>Action</B></td>
</tr> 

	<xc:forEach var="advert" items="${adverts}">
        <tr>
        	<td>${advert.name}</td>
        	<td>${advert.cssownerid}</td>
            <td>${advert.type}</td>
            <td>${advert.id}</td> 
            <td><input type="button" value="join" onclick="updateForm('${advert.id}')" ></td> 
           
        </tr>
    </xc:forEach>
    	
	</table>
</form>	
<!-- .................END PLACE YOUR CONTENT ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>