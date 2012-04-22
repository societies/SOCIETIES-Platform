<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies services - Css Manager</title>
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

	<h3>Css Manager Service</h3>


	<H4>${message}</h4> 

<form:form method="POST" action="cssmanager.html" commandName="cmLoginForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td>Css Identity :</td>
				<td><form:input path="cssIdentity" value="${cmLoginForm.cssIdentity}" readonly="true" size="50"/>
				</td>
			</tr>	
			
			<tr>
				<td>Password :</td>
				<td><form:password path="password" value="" />
				</td>
				<td><form:errors path="password" cssClass="error" />
				</td>
			</tr>						
			<tr>
				<td colspan="3"><form:input type="submit" path="buttonLabel" value="Logon to Css Manager" /></td>
			</tr>
		</table>
	</form:form>
	

<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

