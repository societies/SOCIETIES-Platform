<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
	import="java.util.*" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies - My Communities</title>
<style>
.privacy-policy-handler{
display: inline-block;
padding: 1px 4px;
border: 1px solid gray;
border-radius: 2px;
background-color: #D3D3D3;
color: black;
text-decoration: none;
font-family: arial;
font-size: 0.9em;
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
<!-- .................PLACE YOUR CONTENT BELOW HERE ................ -->



<script language="javascript">
function updateForm(cisId, toDo, where) {    
	document.forms["cmForm"]["cisJid"].value = cisId;
	document.forms["cmForm"]["node"].value = cisId;
	document.forms["cmForm"]["method"].value = toDo;
	document.forms["cmForm"].action = where;
	document.forms["cmForm"].submit();
} 
</script>

<h4>${res}</h4>

<p><b>CIS's I own or am a member of </b></p>

<form id="cmForm" name="cmForm" id="cmForm" method="post" action="cismanager.html">
<input type="hidden" name="cisJid" id="cisJid">
<input type="hidden" name="method" id="method">
<input type="hidden" name="node" id="node">

	<table>
		<tr><td><B>Name</B></td></tr>
		<xc:forEach var="record" items="${cisrecords}">
	        <tr>
	        	<td>${record.getName()}&nbsp;</td>
	        	<td><a href="cis-privacy-policy-show.html?cisId=${record.getCisId()}&cisOwnerId=${currentNodeId.jid}" class="privacy-policy-handler">Privacy Policy</a></td>
	        	<td><input type="button" value="Members" onclick="updateForm('${record.getCisId()}', 'GetMemberList', 'cismanager.html')" ></td>
	        	<td><input type="button" value="Services" onclick="updateForm('${record.getCisId()}', 'GetServicesCis', 'servicediscovery.html')" ></td>
	        </tr>
	    </xc:forEach>
	</table>	

<p>&nbsp;</p>

<xc:if test="${methodCalled=='GetMemberList'}">
    <p><b>Member list:</b> ${cisid}</p>
	<table>
		<tr><td><B>Participant</B></td><td><B>Role</B></td></tr>
		<xc:forEach var="record" items="${memberRecords}">
	        <tr>
	        	<td>${record.getMembersJid()}&nbsp;</td>
	        	<td>${record.getMembershipType()}&nbsp;</td>
	        </tr>
	    </xc:forEach>
	</table>
</xc:if>	
<xc:if test="${methodCalled=='GetMemberListRemote'}">

	<p>Checking with hosting CIS: ${cisid} ...</p>
	<form id="myform" name="myform" action="cismanager.html" method="post">
	<input type="hidden" name="method" id="method" value="RefreshRemoteMembers">
	</form>
	<script language="javascript">
	setTimeout(continueExecution, 5 * 1000); 
	//wait n seconds before continuing  
	
	function continueExecution() {    
		document.forms["myform"].submit(); 
	} 
	</script>
</xc:if>	

<xc:if test="${methodCalled=='RefreshRemoteMembers'}">

    <p><b>Membership List from remote CIS:</b> ${cisid}</p>
	<table>
		<tr><td><B>Participant</B></td><td><B>Role</B></td></tr>
		<xc:forEach var="record" items="${remoteMemberRecords}">
	        <tr>
	        	<td>${record.getJid()}&nbsp;</td>
	        	<td>${record.getRole().toString()}&nbsp;</td>
	        </tr>
	    </xc:forEach>
	</table>
</xc:if>
</form>	
<!-- .................END PLACE YOUR CONTENT ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>
