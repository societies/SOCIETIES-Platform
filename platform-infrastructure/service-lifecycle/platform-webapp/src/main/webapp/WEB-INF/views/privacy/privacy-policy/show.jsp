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

label {
	display: block;
	padding: 0;
	margin: 2px 0;
	text-align: left;
	font-size: 1.2em;
}

label.inline {
	display: inline;
}

input,select {
	margin-bottom: 20px;
	padding: 5px;
}

.clear {display block;
	
}

th {
	text-align: center
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
				<h3><c:out value="${request.resource.scheme}" /> > <c:out value="${request.resource.type}" /></h3>
				<div class="description">
					This CIS requests to 
					<c:if test="${null != request.actions || fn:length(request.actions) > 0}">
						<c:forEach var="action" items="${request.actions}" var="statusAction">
							<c:out value="${action.actionConstant}" /><c:if test="${statusAction.count != fn:length(request.actions)}">, </c:if>
						</c:forEach>
					</c:if>
					your "<c:out value="${request.resource.type}" />".
					<br />
					<c:if test="${null != request.conditions || fn:length(request.conditions) > 0}">
						Following these conditions:
						<ul>
							<c:forEach var="condition" items="${request.conditions}" var="statusCondition">
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

