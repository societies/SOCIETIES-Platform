<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies services - CIS Manager</title>
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

	<h3>CIS Manager Service</h3>


	<form:form method="POST" action="cismanager.html" commandName="cmForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td>CIS Manager Methods :</td>
				<td><form:select path="method">
						<form:option value="NONE" label="--- Select ---" />
						<form:options items="${methods}" />
					</form:select></td>

				<td><form:errors path="method" cssClass="error" /></td>
			</tr>
			<tr>
				<td colspan="3"><input type="submit" /></td>
			</tr>
		</table>
		<br />
		Populate these fields with parameters
		<br />
		<table id="CisAdvertisementRecordDetails">
			<tr>
				<td>CsS ID:</td>
				<td><form:input path="cssId" /></td>
				<td><form:errors path="cssId" cssClass="error" /></td>
				<td>Users CSS jid eg: paul@societies.com  -- Used for Add member, Remove Member</td>
			</tr>
			<tr>
				<td>password:</td>
				<td><form:input path="cisPassword" /></td>
				<td><form:errors path="cisPassword" cssClass="error" /></td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>CIS Name:</td>
				<td><form:input path="cisName" /></td>
				<td><form:errors path="cisName" cssClass="error" /></td>
				<td>eg: Star Trek Fans Community</td>
			</tr>
			<tr>
				<td>CIS Type:</td>
				<td><form:input path="cisType" /></td>
				<td><form:errors path="cisType" cssClass="error" /></td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>Mode:</td>
				<td><form:input path="cisMode" /></td>
				<td><form:errors path="cisMode" cssClass="error" /></td>
				<td>Integer</td>
			</tr>
			<tr>
				<td>Cis Jid:</td>
				<td><form:input path="cisJid" /></td>
				<td><form:errors path="cisJid" cssClass="error" /></td>
				<td>Used for Join, List member, Add member, Remove Member</td>
			</tr>
			<tr>
				<td>Role:</td>
				<td><form:input path="role" /></td>
				<td><form:errors path="role" cssClass="error" /></td>
				<td>admin, owner or participant. Used Add member, Remove Member</td>
			</tr>
		</table>

<h4>${log}</h4>

	</form:form>

<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

