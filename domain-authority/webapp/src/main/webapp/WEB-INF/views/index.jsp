<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Sign In - SOCIETIES</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" title="SOCIETIES" />
</head>
<body>

<jsp:include page="common/header.jsp" />

<div id="content" class="signin">
	<h2>Sign In SOCIETIES</h2>
	<form:form method="POST" action="index.html" commandName="loginForm" onsubmit="return ValidateForm(this)">
		<h3><img src="${pageContext.request.contextPath}/images/societies_xsmall.png" alt="Logo SOCIETIES" /> Sign In</h3>
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
				<c:if test="${not empty infomsg}">
					<br />${infomsg}
				</c:if>
			</p>
		</c:if>
		<c:if test="${not empty debugmsg}">
			<p>
				<span>${debugmsg}</span>
				<c:if test="${not empty infomsg}">
					<br />${infomsg}
				</c:if>
			</p>
		</c:if>
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td>Username</td>
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
			<tr><td colspan="3" class="submitBlock"><input type="submit" value="Ok"/></td></tr>
		</table>
		
		<h3>No SOCIETIES account yet? <strong><a href="signup.html" class="greatButton">Sign Up!</a></strong></h3>
		<br />
		<h3>Download the <strong><a href="download.html" class="greatButton">Android client</a></strong></h3>
	</form:form>
</div>

<jsp:include page="common/footer.jsp" />
</body>
</html>

