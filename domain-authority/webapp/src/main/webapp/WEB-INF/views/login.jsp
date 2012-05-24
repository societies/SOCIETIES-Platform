<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies - User login</title>
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
<h1>${message}</h1>
	<h3>please user below user name and password</h3>
	 
	<FONT color="blue">

		<h6>User Name="userid" and password="password"</h6>

	</FONT>

<h2>${error}</h2>

<form:form method="POST" action="login.html" commandName="loginForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td>User :</td>
				<td><form:input path="userName" value="userid"  />
				</td>
				<td><form:errors path="userName" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>Sub Domain :</td>
				<td><form:input path="subDomain" value="societies.local"  />
				</td>
				<td><form:errors path="subDomain" cssClass="error" />
				</td>
			</tr>			
					
			<tr>
				<td>Password :</td>
				<td><form:password path="password" value="password.societies.local" />
				</td>
				<td><form:errors path="password" cssClass="error" />
				</td>
			</tr>						
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

