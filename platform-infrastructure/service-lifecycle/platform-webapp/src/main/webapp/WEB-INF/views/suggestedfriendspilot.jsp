<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
	import="java.util.*" %>
<%@ taglib prefix="c" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies Suggested Friends Result</title>
</head>
<body>
	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->
<!-- .................PLACE YOUR CONTENT BELOW HERE ................ -->

<h4>${result}</h4>
<br/>
<br/>

<script language="javascript">
	function updateForm(friendId, method) {    
		document.forms["friendReqForm"]["friendId"].value = friendId;
		document.forms["friendReqForm"]["method"].value = method;
		document.forms["friendReqForm"].submit();
	} 
</script>
<form id="friendReqForm" name="friendReqForm" method="post" action="suggestedfriendspilot.html">
		<input type="hidden" name="friendId" id="friendId">
		<input type="hidden" name="method" id="method">
		
		
		<h4>Social Network Friends</h4>
		<Table border="1">
		<tr>
			<td><B>Friend Name</B></td>
			<xc:forEach var="snsFriend" items="${snsFriends}">
        	<tr>
        		<td>${snsFriend.getResultCssAdvertisementRecord().getName()}</td>
        		<xc:if test="${snsFriend.status=='NEEDSRESP'}">
        			<td><input type="button" value="Accept Friend Request" onclick="updateForm('${snsFriend.resultCssAdvertisementRecord.id}','accept')" ></td> 
        			<td><input type="button" value="Decline Friend Request" onclick="updateForm('${snsFriend.resultCssAdvertisementRecord.id}','denied')" ></td>
        			</xc:if>
        			<xc:if test="${snsFriend.status=='PENDING'}">
        			<td><input type="button" value="Cancel Pending Friend Request" onclick="updateForm('${snsFriend.resultCssAdvertisementRecord.id}','cancel')" ></td> 
        			</xc:if>
        			<xc:if test="${snsFriend.status=='NOTREQUESTED'}">
        				<td><input type="button" value="Send Friend Request" onclick="updateForm('${snsFriend.resultCssAdvertisementRecord.id}','sendfr')" ></td> 	       	       
        			</xc:if>
        			<xc:if test="${otherFriend.status=='DENIED'}">
        				<td><input type="button" value="Send Friend Request" onclick="updateForm('${snsFriend.resultCssAdvertisementRecord.id}','sendfr')" ></td> 	       	       
        			</xc:if>        		
        	</tr>
    		</xc:forEach>
    	
		</Table>
	
		<h4>Other Suggested Friends</h4>
		<Table border="1">
			<tr>
				<td><B>Friend Name</B></td></tr>

				<xc:forEach var="otherFriend" items="${otherFriends}">
        		<tr>
        			<td>${otherFriend.getResultCssAdvertisementRecord().getName()}</td>
        			<xc:if test="${otherFriend.status=='NEEDSRESP'}">
        			<td><input type="button" value="Accept Friend Request" onclick="updateForm('${otherFriend.resultCssAdvertisementRecord.id}','accept')" ></td> 
        			<td><input type="button" value="Decline Friend Request" onclick="updateForm('${otherFriend.resultCssAdvertisementRecord.id}','denied')" ></td>
        			</xc:if>
        			<xc:if test="${otherFriend.status=='PENDING'}">
        			<td><input type="button" value="Cancel Pending Friend Request" onclick="updateForm('${otherFriend.resultCssAdvertisementRecord.id}','cancel')" ></td> 
        			</xc:if>
        			<xc:if test="${otherFriend.status=='NOTREQUESTED'}">
        				<td><input type="button" value="Send Friend Request" onclick="updateForm('${otherFriend.resultCssAdvertisementRecord.id}','sendfr')" ></td> 	       	       
        			</xc:if>
        			<xc:if test="${otherFriend.status=='DENIED'}">
        				<td><input type="button" value="Request Declined Send Again?" onclick="updateForm('${otherFriend.resultCssAdvertisementRecord.id}','sendfr')" ></td> 	       	       
        			</xc:if>
        		</tr>
    			</xc:forEach>
    		</tr>
		</Table>	
	
</form>	
<!-- .................END PLACE YOUR CONTENT ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>