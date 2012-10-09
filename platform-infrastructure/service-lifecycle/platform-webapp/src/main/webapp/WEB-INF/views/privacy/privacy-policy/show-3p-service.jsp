<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies services - Privacy Policy - ${Service.serviceName}</title>
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

.resource{
	float: left;
	width: 30%;
	padding: 5px;
	margin: 10px;
	border: 1px solid black;
	border-radius: 5px;
}
.resource:nth-child(3n+1){
	clear: left;
}

.clear {
	display block;
}
.resources{
margin: 0 auto;
padding: 0;
clear: left;
}
.resource h5{
	margin: 0 auto 5px auto;
	cursor: pointer;
	text-align: center;
	font-size: 1.1em;
	font-weight: bold;
}
.resource .short-description{
margin: 0 auto;
padding: 0;
text-align: center;
}
.resource .short-description li{
display: inline-block;
margin: 0 5px;
padding: 1px;
border: 1px solid black;
border-radius: 3px;
}
.resource .short-description .Public{
background-color: orange;
}
.resource .short-description .Private{
background-color: green;
}
.resource .short-description .inference{
background-color: red;
}
.description{
	display: none;
	margin: 5px auto 0 auto;
}
.conditions{
	margin-top: 0;
	margin-bottom: 0;
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
	<p><a href="javascript:history.back()">Go back</a></p>
	<h3>Privacy Policy for the Service: ${Service.serviceName}</h3>
	<p class="error">
		<c:out value="${error}" />
	</p>
	<p class="info">
		<c:out value="${info}" />
	</p>
	
	<jsp:include page="show.jsp" />

	<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="../../footer.jsp" />
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/privacypolicy/show.js"></script>
</body>
</html>

