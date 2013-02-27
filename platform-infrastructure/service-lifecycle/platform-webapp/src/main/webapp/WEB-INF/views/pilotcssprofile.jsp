<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="java.util.*"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies - My Profile</title>

</head>
<body>


<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->
	<!-- .................PLACE YOUR CONTENT HERE ................ -->
	<h4>${message}</h4>

	<br />
	<form:form method="POST" action="pilotcssprofile.html"
						commandName="cmLoginForm">
						<form:errors path="*" cssClass="errorblock" element="div" />
						

						
						<b>My Private Details</b>
						
						<table>
							<tr>
								<td>Name</td>
								<td><form:input path="name"
										value="${cmLoginForm.name}"  size="50" />
								</td>
							</tr>
														
							<tr>
								<td>Email</td>
								<td><form:input path="emailID"
										value="${cmLoginForm.emailID}"  size="50" />
								</td>
							</tr>
						
							<tr>
								<td>Home Location</td>
								<td><form:input path="homeLocation"
										value="${cmLoginForm.homeLocation}"  size="50" />
								</td>
							</tr>
							
							<tr>
								<td>Sex</td>
								<td><form:input path="sex"
										value="${cmLoginForm.sex}" size="50" /> 
								</td>
								<td>0 = Male; 1 = Female; 2 = Undefined</td>
							</tr>
							
							<tr>
								<td>Workplace</td>
								<td><form:input path="workplace"
										value="${cmLoginForm.workplace}" size="50" /> 
								</td>
							</tr>
							
							<tr>
								<td>Position</td>
								<td><form:input path="position"
										value="${cmLoginForm.position}" size="50" /> 
								</td>
							</tr>
							<tr>
							<tr>
								<td>Entity</td>
								<td><form:input path="entity"
										value="${cmLoginForm.entity}" size="50" /> 
								</td>
								<td>0 = Person; 1 = Organisation</td>
							</tr>
							</tr>
						</table>
						<br/>
						
						<b>My CSS Nodes</b>
						<table>
						<tr><td><b>ID</b></td><td><b>Type</b></td></tr>
						<xc:forEach var="aNode" items="${allNodes}">
					        <tr>
					        	<td>${aNode.getJid()}</td>
					         	<td>${aNode.getType()}</td>
					        </tr>
					    </xc:forEach>
						</table>
						
						<br/>
						<b>Your Public Profile Details</b>
				
		<table >
			<!-- name="CssAdvertisementRecordDetails" -->
			<tr>
				<td>Name</td>
				<td><form:input path="cssAdName"  size="50" /></td>
				<td><form:errors path="cssAdName" cssClass="error" /></td>
				
			</tr>
		</table>

	
<form:input type="submit" path="buttonLabel" value="Save" />

</form:form>



	<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>


</html>


