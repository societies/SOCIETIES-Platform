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

<div id="msgid">
</div>

	<h3>CIS Manager Service</h3>


	<form:form method="POST" action="createnewcis.html" commandName="cmForm" name="cisManagerForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td colspan="3"><input id="subBut" type="button" value="Create"/></td>
			</tr>
		</table>
		<br />
		Populate these fields with parameters
		<br />
		<table id="CisAdvertisementRecordDetails">
			<tr>
				<td><form:input path="cssId" style="display:none;"/></td>
				<td><form:errors path="cssId" cssClass="error" /></td>
			</tr>
			
			<tr>
				<td><form:input path="cisPassword" style="display:none;"/></td>
				<td><form:errors path="cisPassword" cssClass="error" /></td>
				<td>&nbsp;</td>
			</tr>
			
			<tr>
				<td>Enter A Name For Your New Community </td>
				<td><form:input path="cisName" /></td>
				<td><form:errors path="cisName" cssClass="error" /></td>
				<td>eg: Star Trek Fans </td>
			</tr>
			<tr>
				<td><form:input path="cisType" style="display:none;"/></td>
				<td><form:errors path="cisType" cssClass="error" /></td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td><form:input path="cisMode" style="display:none;"/></td>
				<td><form:errors path="cisMode" cssClass="error" /></td>
			</tr>
			<tr>

				<td><form:input path="cisJid" style="display:none;"/></td>
				<td><form:errors path="cisJid" cssClass="error" /></td>
			</tr>
			<tr>
				<td><form:input path="role" style="display:none;"/></td>
				<td><form:errors path="role" cssClass="error" /></td>
				
			</tr>
	
		</table>

<h4>${log}</h4>

	</form:form>

<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
	<script type="text/javascript">
 
$(document).ready(function(){
	//startup functionality
	
 
 var i = 0;

 document.getElementById('subBut').onclick = function() {
	 document.cisManagerForm.submit();
	 };
 

 


});// end of $(document).ready(function()
	 
</script>
</body>
</html>

