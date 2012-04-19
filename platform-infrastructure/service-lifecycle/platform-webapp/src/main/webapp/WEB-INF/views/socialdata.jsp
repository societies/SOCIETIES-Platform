<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies services - Social Data</title>


<style>


#legend{
	border: dotted  2px;
	
}

.error {
	color: #ff0000;
}
 
 .connectors{
 	border: solid black;
	text-align: center;
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

	<h3>Social Network Data</h3>
	      <p> All your social data are stored in a common language and used by the platform to perform social operations</p>  
	      <div class="connectors" style="width: 289px; ">
	     	
	      <input onclick="window.open('http://dev.lucasimone.eu/fb.php', 'Facebook Connector', 'height=100', 'width=100');" type="image" src="images/facebook.png">
		  <input onclick="alert('Not implemented yet')";" type="image" src="images/Twitter.jpg">
		  <input onclick="alert('Not implemented yet')";" type="image" src="images/Foursquare.png">
		  <input onclick="alert('Not implemented yet')";" type="image" src="images/Linkedin.png">
		
		  </div>
		
<form:form method="POST" action="socialdata.html" commandName="sdForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td align="middle">Please</td>
					<td><form:select path="method" >
					   <form:option value="NONE" label="--- Select ---" />
					   <form:options items="${methods}" />
					</form:select></td>
				<td><form:errors path="method" cssClass="error" />
				</td>
			
				<td align="middle"> Connector </td>
					<td><form:select path="snName" >
					   <form:option value="NONE" label="--- Select ---" />
					   <form:options items="${snName}" />
					</form:select></td>
				<td><form:errors path="snName" cssClass="error" />
				</td>
			</tr>
			<tr>
		 	<td colspan="6" id="legend">
		 	     <p>How to work with SocialData Bundle </p>
		 	     <ul>
		 	     	<li>To <strong>ADD</strong>    a new Connnector please use one of the ICON above to get the token</li>
		 	     	<li>To <strong>REMOVE</strong> a new Connnector please set the ID in the field below</li>
		 	     	<li>To <strong>LIST</strong>   all Connnectors  available. Do not require any parameter</li>
		 	     	<li>To get <strong>FRIENDs</strong> you Do not require any parameter</li>
		 	     	<li>To get <strong>PROFILEs</strong> you Do not require any parameter</li>
		 	     	<li>To get <strong>GROUPs</strong> you Do not require any parameter</li>
		 	     	<li>To get <strong>ACTIVITIEs</strong> you Do not require any parameter</li>
		 	     </ul> 
		 	     
		 	     
		 	</td>
		 	</tr>
		 	<tr></tr>
			<tr><td align="middle" color="green" >Parameter</td><td colspan="5" align="middle"> Parameter Value</td></tr>
			<tr>
				<td>Access Token:</td>
				   <td colspan=4><form:input path="token"  value="${token}"/>
				</td>
				<td><form:errors path="token" cssClass="error" /></td>
			</tr>
			<tr>	
				<td>Connector ID</td>
				<td colspan="4"><form:input path="id" value="${id}"/>
				</td>
				<td><form:errors path="id" cssClass="error" />
				</td>
			</tr>
			<tr>	
				<td>Setting Options</td>
				<td colspan="4"><form:input path="params" value="${params}"/>
				</td>
				<td><form:errors path="params" cssClass="error" />
				</td>
			</tr>	
			<tr></tr>
			<tr>
				<td colspan="6"><input type="submit" value="Exectutes NOW! " style="width: 713px; "/></td>
			</tr>
		</table>	
</form:form>
	
<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

