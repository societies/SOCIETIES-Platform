<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Download - SOCIETIES</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" title="SOCIETIES" />
</head>
<body>

<jsp:include page="common/header.jsp" />

<div id="content">
	<h2>Download SOCIETIES Android Client</h2>
	<p>SOCIETIES is also available on your Android smartphone. Just download and install the two SOCIETIES Android app.</p>
	${debugmsg}
	${errormsg}
</div>

<jsp:include page="common/footer.jsp" />
</body>
</html>

