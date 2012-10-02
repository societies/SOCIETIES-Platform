<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies - Home</title>
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
<script language="javascript">
function ValidateForm(frm) {
	if (frm.password.value != frm.passwordConfirm.value) {
		alert("Please confirm the password!");
		return false;
	}
	return true;
}
</script>
<body>
<table border="0" width="100%">
	<tr><td align="center"><img src="${pageContext.request.contextPath}/images/societiesheader.png" /></td></tr>
</table>

<!-- .................PLACE YOUR CONTENT HERE ................ -->
<table border="0" width="100%">
<tr><td>

<div align="center"> 

<form:form method="POST" action="pilot.html" commandName="loginForm">
	<input type="hidden" name="method" value="login" >
	<table bgcolor="#BDBDBD">
		<tr><td colspan="2" align="center"><font color="red">${loginError}</font></td></tr>
		<tr><td><b>Sign in</b></td><td align="right"><img src="${pageContext.request.contextPath}/images/societies_xsmall.png" border="0"/></td></tr>
		<tr><td colspan="2">Username</td></tr>
		<tr><td colspan="2"><form:input path="userName" value=""  /></td></tr>
		<tr><td colspan="2">Password</td></tr>
		<tr><td colspan="2"><form:password path="password" value="" /></td></tr>
		<tr><td colspan="2"><input type="submit" value="Sign in"/></td></tr>
	</table>
</form:form>
</div>
</td></tr>
</table>

<!-- .................END PLACE YOUR CONTENT HERE ................ --> 
</body>
</html>

