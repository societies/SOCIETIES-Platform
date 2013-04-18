<%@page import="com.google.inject.spi.Element"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>My Context</title>


<link href="css/context/context.css" rel="stylesheet" type="text/css"
	media="screen" />
<link href="css/context/ctx-table-style.css" rel="stylesheet"
	type="text/css" media="screen" />

<script type="text/javascript" src="js/context/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="js/context/context.js"></script>


</head>

<body>

	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->

	<!-- .................PLACE YOUR CONTENT HERE ................ -->


	<div id="container">
		<h1>Ooops! An error has occurred when processing your request!</h1>
		<ul>
			<li>Error code:${error.errorCode}</li>
			<li>Error message:${error.errorMessage}</li>
			<li>Error description:${error.errorDescription}</li>
		</ul>

	</div>

	<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

