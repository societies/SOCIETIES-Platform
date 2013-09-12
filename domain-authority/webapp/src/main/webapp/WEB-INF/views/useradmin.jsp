<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>User Administration - SOCIETIES</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" title="SOCIETIES" />
</head>
<body>

<jsp:include page="common/header.jsp" />

<div id="content">
	<h2>User Administration <span style="font-size:0.7em;">- <a href="admin-logout.html">Logout</a></span></h2>
	${debugmsg}
	<form:form method="POST" action="useradmin.html" commandName="userForm">
		<xc:if test="${not empty errormsg}">
			<p>
				<span class="error">${errormsg}</span>
				<xc:if test="${not empty infomsg}">
					<br />${infomsg}
				</xc:if>
			</p>
		</xc:if>
		<xc:if test="${not empty result}">
			<p>
				<span class="ok">${result}</span>
				<xc:if test="${not empty infomsg}">
					<br />${infomsg}
				</xc:if>
			</p>
		</xc:if>
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<th>Name</th>
				<th>Id</th>
				<th>Host</th>
				<th>Port</th>
				<th>Status</th>
				<th>User Type</th>
			</tr>
			<xc:forEach var="user" items="${userForm.userDetails}" varStatus="userLoop">
			<tr>   
				 <td><form:input path="userDetails[${userLoop.index}].name" value="${user.name}"/>  </td>
				 <td><form:input path="userDetails[${userLoop.index}].id" value="${user.id}"/>  </td>
				 <td><form:input path="userDetails[${userLoop.index}].host" value="${user.host}"/>  </td>
				 <td><form:input path="userDetails[${userLoop.index}].port" value="${user.port}"/>  </td>
				 <td>
				 	<form:select path="userDetails[${userLoop.index}].status"  value="${user.status}" >
						<form:options items="${userStatusTypes}" />
					</form:select>
				</td>
				<td>
					<form:select path="userDetails[${userLoop.index}].userType"  value="${user.userType}" >
						<form:options items="${userTypes}" />
					</form:select>
				</td>
			</tr>  
	  		</xc:forEach>
			<tr>
				<td colspan="6" class="submitBlock"><input type="submit" /></td>
			</tr>
		</table>		
	</form:form>
</div>
	
<jsp:include page="common/footer.jsp" />
<script language="javascript">
function ValidateForm(frm){
	if (frm.password.value != frm.passwordConfirm.value){
		alert("Please confirm the password!");
		return false;
	}
	return true;
}
</script>
</body>
</html>

