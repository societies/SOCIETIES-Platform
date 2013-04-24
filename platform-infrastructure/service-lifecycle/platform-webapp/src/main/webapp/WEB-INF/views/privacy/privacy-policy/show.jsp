<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:if test="${null != PrivacyPolicy || fn:length(PrivacyPolicy.requests) > 0}">
<h4>Requested Data</h4>
<div class="resources">
	<c:forEach var="request" items="${PrivacyPolicy.requests}" varStatus="status">
		<div class="resource" id="resource${status.index}">
			<h5>${request.resource.scheme} > ${request.resource.dataType}<c:if test="${request.optional} == 1"> <span>optional</span></c:if></h5>
			<ul class="short-description">
				<c:forEach var="action" items="${request.actions}"><li class="${action.actionType}<c:if test="${action.optional} == 1"> optional</c:if>">${action.actionType}</li></c:forEach>
				<li class="status ${request.status}">${request.status}</li>
				<c:if test="${null != request.inferenceStatus}"><li class="status inference">${request.inferenceStatus}</li></c:if>
			</ul>
			<div class="description">
				This ${element} requests to 
				<c:if test="${null != request.actions || fn:length(request.actions) > 0}">
					<c:forEach var="action" items="${request.actions}" varStatus="statusAction">
						<span class="${action.actionType}<c:if test="${action.optional} == 1"> optional</c:if>">${action.actionType}</span><c:if test="${statusAction.count != fn:length(request.actions)}">, </c:if>
					</c:forEach>
				</c:if>
				on "${request.resource.dataType}"
				<br />
				<c:if test="${null != request.conditions || fn:length(request.conditions) > 0}">
					Following these conditions:
					<ul class="conditions">
						<c:forEach var="condition" items="${request.conditions}" varStatus="statusCondition">
							<li<c:if test="${condition.optional} == 1"> class="optional"</c:if>><c:out value="${condition.conditionName}" />: <c:out value="${condition.value}" /></li>
						</c:forEach>
					</ul>
				</c:if>
			</div>
		</div>
	</c:forEach>
</div>
</c:if>

