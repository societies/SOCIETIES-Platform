<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
	import="java.util.*" %>
<%@ taglib prefix="c" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href="css/socialStyle.css" />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>SocialData Entry</title>
</head>
<body>
	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->
<!-- .................PLACE YOUR CONTENT BELOW HERE ................ -->

<br>
<h4>${result_title}</h4>
<br>
<br>
	<span class="button" onclick='javascript:history.back();'> Back </span><br/>
	<xc:if test="${model.isActivity}">
		<xc:forEach var="entity" items="${model.activities}">
			<div class="entry_container">
				<div class="title">
					<img src="${entity.icon}" class="icon"/>
					${entity.name} at ${entity.date}
				</div>
				<b>id:</b> ${entity.id}<br/>
				<b>${entity.verb}:</b> ${entity.content}
			</div>
		</xc:forEach>
	</xc:if>
	
	<xc:if test="${model.isGroup}">
		<xc:forEach var="entity" items="${model.groups}">
			<div class="entry_container">
				<div class="title">
					<img src="${entity.icon}" class="icon"/>
					${entity.name}
				</div>
				<b>id:</b> ${entity.id}<br/>
				${entity.content}
			</div>
		</xc:forEach>
	</xc:if>
	
	<xc:if test="${model.isPerson}">
		<xc:forEach var="entity" items="${model.friends}">
			<div class="entry_container">
				<div class="title">
					<img src="${entity.icon}" class="icon"/>
					${entity.name}
				</div>
				<img src="${entity.thumb}" style="width:50px;"/>
				<b>id:</b> ${entity.id}<br/>
				${entity.content}
				
			</div>
		</xc:forEach>
	</xc:if>
	
<!-- .................END PLACE YOUR CONTENT ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>