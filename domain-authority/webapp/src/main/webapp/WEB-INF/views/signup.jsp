<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Sign Up - SOCIETIES</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" title="SOCIETIES" />
</head>
<body>

<jsp:include page="common/header.jsp" />

<div id="content" class="signup">
	<h2>Sign Up for SOCIETIES</h2>
	<form:form method="POST" action="signup.html" commandName="loginForm" onsubmit="return ValidateForm(this)">
		<h3>
			<img src="${pageContext.request.contextPath}/images/societies_xsmall.png" alt="Logo SOCIETIES" />
			Create your free personal account
		</h3>
		<c:if test="${not empty errormsg}">
			<p>
				<span class="error">${errormsg}</span>
				<c:if test="${not empty infomsg}">
					<br />${infomsg}
				</c:if>
			</p>
		</c:if>
		<c:if test="${not empty result}">
			<p>
				<span class="ok">${result}</span>
				<br />
				Meanwhile, you can download the <strong>SOCIETIES <a href="download.html" class="greatButton">Android client</a></strong>
				<c:if test="${not empty infomsg}">
					<br />${infomsg}
				</c:if>
			</p>
		</c:if>
		<table>
			<tr>
				<td>Name</td>
				<td>
					<form:input path="name" value="${name}" required="required" />
					<form:errors path="name" cssClass="error" />
				</td>
				<td></td>
			</tr>	
			<tr>
				<td>Username <em style="font-size:0.8em;">(used for login)</em></td>
				<td>
					<form:input path="userName" value="${username}" required="required"  />
					<form:errors path="userName" cssClass="error" />
				</td>
				<td>@ <form:select path="subDomain" >
						   <form:options items="${domains}" />
					   </form:select>
				</td>
			</tr>
			<tr>
				<td>Password</td>
				<td>
					<form:password path="password" value="${password}" required="required" />
					<form:errors path="password" cssClass="error" />
				</td>
				<td></td>
			</tr>
			<tr>
				<td>Password Confirmation</td>
				<td>
					<form:password path="passwordConfirm" value="${passwordConfirm}" required="required" />
					<form:errors path="passwordConfirm" cssClass="error" />
				</td>
				<td></td>
			</tr>	
			<tr>
				<td colspan="3" class="submitBlock">
					<input type="submit" value="Register"/>
				</td>
			</tr>
		</table>
		<h3>Already have a SOCIETIES account? <strong><a href="index.html" class="greatButton">Sign In!</a></strong></h3>
	</form:form>
</div>

<jsp:include page="common/footer.jsp" />
</body>
</html>

