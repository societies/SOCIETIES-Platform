<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="java.util.*"%><%@ taglib prefix="c"
	uri="http://www.springframework.org/tags/form"%><%@taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style>
* {
	margin: 0;
	padding: 0
}

html,body {
	height: 100%;
	width: 100%;
	overflow: hidden
}

table {
	height: 100%;
	width: 100%;
	table-layout: static;
	border-collapse: collapse
}

iframe {
	height: 100%;
	width: 100%
}

.header {
	border-bottom: 1px solid #000
}

.content {
	height: 100%
}
</style>
</head>
<body>
	<table>
		<tr>
			<td class="content"><iframe
				  src="${webappurl}" frameborder="0"  
			 
					scrolling="auto"></iframe></td>
		</tr>
	</table>
</body>
</html>
