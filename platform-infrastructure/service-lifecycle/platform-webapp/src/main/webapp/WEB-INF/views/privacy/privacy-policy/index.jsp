<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies services - Privacy Policies</title>
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
	<jsp:include page="../../header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="../../leftbar.jsp" />
	<!-- END LEFTBAR -->
<!-- .................PLACE YOUR CONTENT HERE ................ -->

	<h3>Privacy Policy Management</h3>
	<p>This page is for testing purpose. You can use it to add manually a Privacy Policy (embedded in CIS creation GUI), and show an example of CIS or 3P service privacy policy (embedded in CIS / 3P service list GUIs).</p>
	<ul>
		<li><a href="privacy-policy.html">Add a new one</a></li>
		<li><a href="cis-privacy-policy-show.html?cisId=onecis.societies.local&cisOwnerId=olivier.societies.local&test=true">Show an existing CIS privacy policy</a></li>
		<li><a href="service-privacy-policy-show.html?serviceId=css://olivier.societies.local&serviceOwnerId=olivier.societies.local&test=true">Show an existing 3P service privacy policy</a></li>
	</ul>
	
	<h3>Search a CIS Privacy Policy</h3>
	<c:if test="${null != ResultMsg}">
	<p>
		<pre>${ResultMsg}</pre>
	</p>
	</c:if>
	<form:form method="POST" action="privacy-policies.html" commandName="privacyPolicyCriteria" class="searchPrivacyPolicy">
		<fieldset>
		<legend>Criteria</legend>
			<form:errors path="*" cssClass="errorblock" element="div" />
			
			<form:label path="cisLocation">CIS location</form:label>
			<form:select path="cisLocation" class="cisLocation">
				<option value="local">One of my CIS</option>
				<option value="remote">Other CIS, remotely</option>
			</form:select>
			<form:errors path="cisLocation" cssClass="error" />
			<span class="remoteCis">			
				<form:label path="ownerId">select the CIS owner JID</form:label>
				<form:input path="ownerId" placeholder="e.g. myfriend.societies.local" />
				<form:errors path="ownerId" cssClass="error" />
			</span>

			<form:label path="cisId">CIS JID</form:label>
			<form:input path="cisId" placeholder="e.g. cis-lion.societies.local" />
			<form:errors path="cisId" cssClass="error" />
			
			<input type="submit" value="Submit" />
			<span class="globalError error"></span>
		</fieldset>
	</form:form>

	<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="../../footer.jsp" />
	<!-- END FOOTER -->
	
	<script type="text/javascript">
	$(document).ready(function(){
		$('.remoteCis').hide();
		// CIS location changed
		$('.cisLocation').change(function(){
			if (0 == $(this).prop('selectedIndex')) {
				$('.remoteCis').hide();
			}
			else {
				$('.remoteCis').show('slow');
			}
		});
	});
	</script>
</body>
</html>

