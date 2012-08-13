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

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>

<script type="text/javascript">
 
$(document).ready(function(){
	//startup functionality
	
 $("#msgid").html("This is Hello World by JQuery");
 $("#removeCriteria").hide();
 
 var i = 0;

 document.getElementById('subBut').onclick = function() {
	 document.cisManagerForm.submit();
	 };
 
// function subButton(){
//	                 document.cisManagerForm.submit();
//	             }
 
 document.getElementById("logC").onclick = function logArray(){
	 
	 var data = Array();
	 $("#existingCriteria tr").each(function(i, v){
		     $(this).children('td').each(function(ii, vv){
		         data[i] = $(this).text();
		     }); 
		 });
	 alert(data);
	 
	};
 

 
 document.getElementById("addCriteria").onclick = function() {
	 
	 // test log string
	 $("#msgid").html( "" + $('#attributeValue').val() + $('#operatorValue').val() + 
			 $('#criteriaValue').val() );// end of  $("#msgid").html( "" +
	if(i>9){
		alert("max of 10 criteria")	
	}
	else{
		i++;
		
		// add the row
		var row = $('<tr/>', {class: "critRow"}).appendTo("#existingCriteria");
		// column 
		var column = $('<td/>', {text: $('#attributeValue').val() + " " + $('#operatorValue').val() + " " + $('#criteriaValue').val() }).appendTo(row);
		// button
		var f = function () {row.remove();};
		var b = $('<button/>',{		  text: "Delete", type: "button", 
			  click: f
		}).appendTo(column);
		
	}

	
 
};// end of document.getElementById("addCriteria").onclick = function() {

});// end of $(document).ready(function()
	 
</script>


<div id="msgid">
</div>

	<h3>CIS Manager Service</h3>


	<form:form method="POST" action="cismanager.html" commandName="cmForm" name="cisManagerForm">
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
				<td colspan="3"><input id="subBut" type="button" value="Enter"/></td>
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
			<tr>
				<td>Criteria:</td>
				
				<table id="existingCriteria">
				</table>
				
				<table id="critTable">
					<tbody>
					<tr id="critFistRow"><td>Attribute</td><td>Operator</td><td>Value</td></tr>
					
					<tr>
					<td><form:select id="attributeValue" path="attribute" cssClass="textArea"><form:options items="${attributeList}"/></form:select></td>
					<td><form:select id="operatorValue" path="operator" cssClass="textArea"><form:options items="${operatorList}"/></form:select></td>
					<td><form:input id="criteriaValue" path="value" /></td><td><form:errors path="value" cssClass="error" /></td>
					<td><button type="button" id="addCriteria"  disabled="disabled">Do not click me</button></td>
					<td><button type="button" id="removeCriteria">Remove Criteria</button></td>					
					
					</tr>
					
					<tr>
					<td colspan="4"><button id="logC" type="button"  disabled="disabled">Do not click neither</button></td>
					</tr>
	
				  </tbody>
				</table>
				
				
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

