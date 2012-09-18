<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies services - Privacy Policy - <c:out value="${PrivacyPolicy.requestor.cisRequestorId}" /></title>
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
	min-height: 250px;
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

	<h3>Privacy Policy for the CIS: <c:out value="${PrivacyPolicy.requestor.cisRequestorId}" /></h3>
	<p class="error">
		<c:out value="${error}" />
	</p>
	<p class="info">
		<c:out value="${info}" />
	</p>
	
	<h4>Requested Data</h4>
	<c:if test="${null != PrivacyPolicy || fn:length(PrivacyPolicy.requests) > 0}">
		<c:forEach var="request" items="${PrivacyPolicy.requests}" varStatus="status">
			<div class="resource" id="resource${status.index}">
				<h5><c:out value="${request.resource.scheme}" /> > <c:out value="${request.resource.dataType}" /></h5>
				<div class="description">
					This CIS requests to 
					<c:if test="${null != request.actions || fn:length(request.actions) > 0}">
						<c:forEach var="action" items="${request.actions}" varStatus="statusAction">
							<span class="${action.actionType}">${action.actionType}</span><c:if test="${statusAction.count != fn:length(request.actions)}">, </c:if>
						</c:forEach>
					</c:if>
					on "${request.resource.dataType}"
					<br />
					<c:if test="${null != request.conditions || fn:length(request.conditions) > 0}">
						Following these conditions:
						<ul>
							<c:forEach var="condition" items="${request.conditions}" varStatus="statusCondition">
								<li><c:out value="${condition.conditionName}" />: <c:out value="${condition.value}" /></li>
							</c:forEach>
						</ul>
					</c:if>
				</div>
			</div>
		</c:forEach>
	</c:if>

	<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="../../footer.jsp" />
</body>
</html>

