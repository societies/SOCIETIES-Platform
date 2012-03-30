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

	<h3>Css Manager Service</h3>


	<form:form method="POST" action="cssmanager.html" commandName="cmForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td>Css Manager Methods :</td>
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
		<!-- 
		Populate the 3 Fields Below if you select Register Css 
		<br/> 
			<table id="CssRecordDetails"> 
			<tr>
				<td>Css : Identity</td>
				<td><form:input path="cssRecordCssIdentity" />
				</td>
				<td><form:errors path="cssRecordCssIdentity" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>Css : name</td>
				<td><form:input path="cssRecordName" />
				</td>
				<td><form:errors path="cssRecordName" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td>Css : password</td>
				<td><form:input path="cssRecordPassword" />
				</td>
				<td><form:errors path="cssRecordPassword" cssClass="error" />
				</td>
			</tr>		
		</table>
		-->
		<br />
		Populate the 3 Fields Below if you select Register CssAdvertisement Record 
		<br />
		<table id="CssAdvertisementRecordDetails">
			<!-- name="CssAdvertisementRecordDetails" -->
			<tr>
				<td>Css Advert : Name</td>
				<td><form:input path="cssAdName" /></td>
				<td><form:errors path="cssAdName" cssClass="error" /></td>
				<td>Hint: Something Like XCManager Advert</td>
			</tr>
			<tr>
				<td>Css Advert : Id</td>
				<td><form:input path="cssAdId" /></td>
				<td><form:errors path="cssAdId" cssClass="error" /></td>
				<td>Hint: Something Like XCManager.societies.local</td>
			</tr>
			<tr>
				<td>Css Advert : Uri</td>
				<td><form:input path="cssAdUri" /></td>
				<td><form:errors path="cssAdUri" cssClass="error" /></td>
				<td>Hint : Something like http://XCManager.societies.local</td>
			</tr>
		</table>





	</form:form>

	<br />
	<h4>Please click the service to use .....</h4>

	<table>
		<tr>
			<td><a href="servicediscovery.html">Service Discovery
					Service</a></td>
		</tr>
		<tr>
			<td><a href="servicecontrol.html">Service Control Service</a></td>
		</tr>
		<tr>
			<td><a href="cssmanager.html">Css Manager Service</a></td>
		</tr>
	</table>

</body>
</html>

