<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies - User Administration</title>
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

	<h3>Societies - User Administration</h3>
		
<form:form method="POST" action="useradmin.html" commandName="userForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
		
		
	
		<b>
		<tr>
		<td>Name</td>
		<td>Id</td>
		<td>Host</td>
		<td>Port</td>
		<td>Status</td>
		<td>User Type</td>
		</tr>
		</b>
		<xc:forEach var="user" items="${userForm.userDetails}" varStatus="userLoop">
		
		<tr>   
			 <!-- it works -->    
			 <td><form:input path="userDetails[${userLoop.index}].name" value="${user.name}"/>  </td>
			 <td><form:input path="userDetails[${userLoop.index}].id" value="${user.id}"/>  </td>
			 <td><form:input path="userDetails[${userLoop.index}].host" value="${user.host}"/>  </td>
			 <td><form:input path="userDetails[${userLoop.index}].port" value="${user.port}"/>  </td>
			 <td><form:input path="userDetails[${userLoop.index}].status" value="${user.status}"/>  </td>
			 <td><form:input path="userDetails[${userLoop.index}].userType" value="${user.userType}"/>  </td>
			 </tr>  
		
  		</xc:forEach>
 
			 <tr>
				<td colspan="3"><input type="submit" /></td>
			</tr>
			 
		
			
		</table>		
	</form:form>
	
<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

