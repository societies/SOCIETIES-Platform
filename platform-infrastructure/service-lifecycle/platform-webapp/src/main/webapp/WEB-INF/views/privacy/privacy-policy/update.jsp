<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

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
label{
display: block;
padding: 0;
margin:  2px 0;
text-align: left;
font-size: 1.2em;
}
label.inline{
display: inline;
}
input, select{
margin-bottom: 20px;
padding: 5px;
}
.clear{
display block;
}
th{
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

	<h3>Privacy Policies</h3>
	<h4>Add a new privacy policy</h4>
	<p><c:out value="${ResultMsg}" /></p>
	<form:form method="POST" action="privacy-policy.html" commandName="privacyPolicy" class="updatePrivacyPolicy">
		<fieldset>
			<legend>CIS identity</legend>
			<form:errors path="*" cssClass="errorblock" element="div" />

			<form:label path="cssOwnerId">CSS Owner JID</form:label>
			<form:input path="cssOwnerId" placeholder="e.g. me@societies.local" />
			<form:errors path="cssOwnerId" cssClass="error" />

			<form:label path="cisId">CIS JID</form:label>
			<form:input path="cisId" placeholder="e.g. lioncis@societies.local" />
			<form:errors path="cisId" cssClass="error" />
		</fieldset>
		
		<fieldset>
			<legend>Requested data</legend>
			<c:forEach var="resource" items="${privacyPolicy.resources}" varStatus="status">
			<fieldset class="requestedData lastResource">
				<legend>Resource #${status.count}</legend>
				
				<form:label path="resources[${status.index}].resourceType">Resource Type</form:label>
				<form:select path="resources[${status.index}].resourceType" class="resources${status.index}resourceType">
					<option value="NONE">--- Select ---</option>
					<c:forEach var="resourceType" items="${ResourceTypeList}">
						<form:option value="${resourceType.name}">${resourceType.name}</form:option>
					</c:forEach>
				</form:select>
				<form:errors path="resources[${status.index}].resourceType" cssClass="error" />
				<form:label path="resources[${status.index}].resourceTypeCustom" class="inline">or tip a custom one</form:label>
				<form:input path="resources[${status.index}].resourceTypeCustom"  class="resources${status.index}resourceTypeCustom" placeholder="e.g. mood"/>
				<span class="resources${status.index}resourceTypeError error"></span>
				<form:errors path="resources[${status.index}].resourceTypeCustom" cssClass="error" />
				
				<div class="clear"></div>

				<form:label path="resources[${status.index}].optional" class="inline">Optional?</form:label>
				<form:checkbox path="resources[${status.index}].optional" value="1" />
				<form:errors path="resources[${status.index}].optional" cssClass="error" />

				<label for="resources[${status.index}].actions1">Actions to apply over this resource</label>
				<c:forEach var="action" items="${ActionList}" varStatus="statusAction">
					<form:checkbox path="resources[${status.index}].actions[${statusAction.index}].action" value="${action}" /> <label for="resources${status.index}.actions${statusAction.index}.action" class="inline">${action}</label>
					(<form:checkbox path="resources[${status.index}].actions[${statusAction.index}].optional" value="1" /> <label for="resources${status.index}.actions${statusAction.index}.optional" class="inline">Optional?</label>)
					<div class="clear"></div>
				</c:forEach>
				<form:errors path="resources[${status.index}].actions" cssClass="error" />
				
				<label for="resources${status.index}conditionTypeAdd">Add a condition</label>
				<table>
					<thead>
						<tr>
							<th>Condition type</th>
							<th>Condition value</th>
							<th>Optional?</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody>
						<tr class="resources${status.index}conditionAddAction conditionFromResource${status.index}">
							<td>
								<select name="resources${status.index}.conditionTypeAdd" id="resources${status.index}conditionTypeAdd">
									<option value="NONE">--- Select ---</option>
									<c:forEach var="condition" items="${ConditionList}">
										<option value="${condition}" /> ${condition}
									</c:forEach>
								</select>
							</td>
							<td>
								<input type="text" name="resources${status.index}.conditionValueAdd" id="resources${status.index}conditionValueAdd" />
							</td>
							<td>
								<input type="checkbox" name="resources${status.index}.conditionOptionalAdd" id="resources${status.index}conditionOptionalAdd" value="1" />
							</td>
							<td>
								<input type="button" name="resources${status.index}" class="addCondition" value="Add" />
							</td>
						</tr>
						<c:forEach var="condition" items="${privacyPolicy.resources[status.index].conditions}" varStatus="statusCondition">
							<tr class="conditionFromResource${status.index} lastCondition${status.index}">
								<td>
									<form:select path="resources[${status.index}].conditions[${statusCondition.index}].theCondition">
										<form:option value="NONE">--- Select ---</form:option>
										<c:forEach var="conditionType" items="${ConditionList}" varStatus="statusConditionList">
											<form:option value="${conditionType}">${conditionType}</form:option>
										</c:forEach>
									</form:select>
									<form:errors path="resources[${status.index}].conditions[${statusCondition.index}].theCondition" cssClass="error" />
								</td>
								<td>
									<form:input path="resources[${status.index}].conditions[${statusCondition.index}].value" />
									<form:errors path="resources[${status.index}].conditions[${statusCondition.index}].value" cssClass="error" />
								</td>
								<td>
									<form:checkbox path="resources[${status.index}].conditions[${statusCondition.index}].optional" value="1" />
									<form:errors path="resources[${status.index}].conditions[${statusCondition.index}].optional" cssClass="error" />
								</td>
								<td class="action">
									<c:if test="${statusCondition.count == fn:length(privacyPolicy.resources[status.index].conditions)}">
										<input type="button" name="resources${status.index}" class="removeCondition removeCondition${status.index}" value="Remove" />
									</c:if>
								</td>
							</tr>
							<c:set var="ConditionNumber" value="${statusCondition.count}" />
						</c:forEach>
						<input type="hidden" name="resources${status.index}.resourceId" id="resources${status.index}resourceId" value="${status.index}" />
						<input type="hidden" name="resources${status.index}.conditionId" id="resources${status.index}conditionId" value="${ConditionNumber}" />
					</tbody>
				</table>
			</fieldset>
			</c:forEach>

			<input type="button" value="Add a requested data" class="addRequestedData" />
		</fieldset>
		<input type="submit" value="Submit" /><span class="globalError error"></span>
	</form:form>


	<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="../../footer.jsp" />
	<!-- END FOOTER -->
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
	<script type="text/javascript">
	$(document).ready(function(){
		var actionList = [<c:forEach var="action" items="${ActionList}">"${action}", </c:forEach>];
		var conditionList = [<c:forEach var="condition" items="${ConditionList}">"${condition}", </c:forEach>];
		var resourceTypeList = [<c:forEach var="resourceType" items="${ResourceTypeList}">"${resourceType.name}", </c:forEach>];
		var lastResourceId = ${fn:length(privacyPolicy.resources)};
		
		// -- Update a privacy policy
		$('.updatePrivacyPolicy').submit(function(){
			// - Verification (validation)
			var valid = true;
			for(var i=0; i<lastResourceId; i++) {
				// Reset old validation error
				$('.resources'+i+'resourceType').css({'border': '1px inset gray'});
				$('.resources'+i+'resourceTypeCustom').css({'border': '1px inset gray'});
				$('.resources'+i+'resourceTypeError').hide();
				$('.globalError').hide();
				// Verify
				if (0 == $('.resources'+i+'resourceType').attr('selectedIndex')
						&& (null == $('.resources'+i+'resourceTypeCustom').val()
								|| '' == $('.resources'+i+'resourceTypeCustom').val()
								|| undefined == $('.resources'+i+'resourceTypeCustom').val())) {
					$('.resources'+i+'resourceType').css({'border': '1px inset red'});
					$('.resources'+i+'resourceTypeCustom').css({'border': '1px inset red'});
					$('.resources'+i+'resourceTypeError').show();
					$('.resources'+i+'resourceTypeError').html('Select a type or tip a custom type');
					$('.globalError').show();
					$('.globalError').html('Please, correct above error(s).');
					valid = false;
				}
			}
			return valid;
		});
			


		// -- Add a new requested data
		$('.addRequestedData').click(function(){
			var newRequestedData = $('<fieldset>').addClass('requestedData');
			$('<legend>').html('Resource #'+(lastResourceId+1))
						.appendTo(newRequestedData);
			
			// - Resource Type
			$('<label>').attr('for', 'resources'+lastResourceId+'.resourceType')
						.html('Resource Type')
						.appendTo(newRequestedData);
			var selectResourceType = $('<select>').attr('name', 'resources['+lastResourceId+'].resourceType')
						.addClass('resources'+lastResourceId+'resourceType')
						.attr('id', 'resources'+lastResourceId+'.resourceType');
			$('<option>').attr('value', 'NONE')
					.html('--- Select ---')
					.appendTo(selectResourceType);
			for(var i=0; i<resourceTypeList.length; i++) {
				$('<option>').attr('value', resourceTypeList[i])
							.html(resourceTypeList[i])
							.appendTo(selectResourceType);
			}
			selectResourceType.appendTo(newRequestedData);
			// Resource Custom Type
			$('<label>').attr('for', 'resources'+lastResourceId+'.resourceTypeCustom')
						.addClass('inline')
						.html('or tip a custom one')
						.appendTo(newRequestedData);
			$('<input>').attr('type', 'text')
						.attr('name', 'resources['+lastResourceId+'].resourceTypeCustom')
						.attr('id', 'resources'+lastResourceId+'.resourceTypeCustom')
						.addClass('resources'+lastResourceId+'resourceTypeCustom')
						.attr('placeholder', 'e.g. mood')
						.appendTo(newRequestedData);
			// Resource type error
			$('<span>').addClass('resources'+lastResourceId+'resourceTypeError')
						.addClass('error')
						.appendTo(newRequestedData);
			
			// - Resource Optional?
			$('<div>').addClass('clear')
					.appendTo(newRequestedData);
			$('<label>').attr('for', 'resources'+lastResourceId+'.optional')
						.addClass('inline')
						.html('Optional?')
						.appendTo(newRequestedData);
			$('<input>').attr('type', 'checkbox')
						.attr('value', 1)
						.attr('name', 'resources['+lastResourceId+'].optional')
						.attr('id', 'resources'+lastResourceId+'.optional')
						.appendTo(newRequestedData);
			$('<input>').attr('type', 'hidden')
							.attr('value', 'on')
							.attr('name', '_resources['+lastResourceId+'].optional')
							.appendTo(newRequestedData);
			
			// - Actions List
			$('<label>').attr('for', 'resources'+lastResourceId+'.actions1')
						.html('Actions to apply over this resource')
						.appendTo(newRequestedData);
			for(var i=0; i<actionList.length; i++) {
				// Action
				$('<input>').attr('type', 'checkbox')
							.attr('value', actionList[i])
							.attr('name', 'resources['+lastResourceId+'].actions['+i+'].action')
							.attr('id', 'resources'+lastResourceId+'.actions'+i+'.action1')
							.appendTo(newRequestedData);
				$('<input>').attr('type', 'hidden')
							.attr('value', 'on')
							.attr('name', '_resources['+lastResourceId+'].actions['+i+'].action')
							.appendTo(newRequestedData);
				var labelAction = $('<label>').attr('for', 'resources'+lastResourceId+'.actions'+i+'.action1')
							.addClass('inline')
							.html(actionList[i]);
				labelAction.appendTo(newRequestedData);
				labelAction.after(document.createTextNode(" ("));
				
				// Optional
				$('<input>').attr('type', 'checkbox')
							.attr('value', 1)
							.attr('name', 'resources['+lastResourceId+'].actions['+i+'].optional')
							.attr('id', 'resources'+lastResourceId+'.actions'+i+'.optional1')
							.appendTo(newRequestedData);
				$('<input>').attr('type', 'hidden')
							.attr('value', 'on')
							.attr('name', '_resources['+lastResourceId+'].actions['+i+'].optional')
							.appendTo(newRequestedData);
				var labelOptional = $('<label>').attr('for', 'resources'+lastResourceId+'.actions'+i+'.optional1')
							.addClass('inline')
							.html('Optional?');
				labelOptional.appendTo(newRequestedData);
				labelOptional.after(document.createTextNode(")"));
				
				// Clear
				$('<div>').addClass('clear')
					.appendTo(newRequestedData);
			}
			
			// - Condition List
			$('<label>').attr('for', 'resources'+lastResourceId+'conditionTypeAdd')
						.html('Add a condition')
						.appendTo(newRequestedData);
			var tableCondition = $('<table>');
			$('<thead>').html('<tr><th>Condition type</th><th>Condition value</th><th>Optional?</th><th>Action</th></tr>')
					.appendTo(tableCondition);
			var tbody = $('<tbody>');
			var trHeader = buildConditionLineHeader(lastResourceId, conditionList);
			trHeader.appendTo(tbody);
			tbody.appendTo(tableCondition);
			tableCondition.appendTo(newRequestedData);
			
			// -- Add the new requested data
			newRequestedData.insertBefore('.addRequestedData');
			lastResourceId++;
		});
		
		
		// -- Add a new condition
		$('.addCondition').live('click', function(){
			var btnAddCondition = $(this);
			var addConditionClass = btnAddCondition.attr('name');
			// -- Verification
			// - reset last verification error
			$('#'+addConditionClass+'conditionTypeAdd').css({'border': '1px inset gray'});
			if (0 == $('#'+addConditionClass+'conditionTypeAdd').attr('selectedIndex')) {
				$('#'+addConditionClass+'conditionTypeAdd').css('border-color', 'red');
				return;
			}
			// Retrieve data
			var resourceId = $('#'+addConditionClass+'resourceId').val();
			var conditionId = Math.round($('#'+addConditionClass+'conditionId').attr('value'));
			var conditionValue = $('#'+addConditionClass+'conditionValueAdd').val();
			var conditionOptional = $('#'+addConditionClass+'conditionOptionalAdd').val();
			var selectedConditionType = $('#'+addConditionClass+'conditionTypeAdd').attr('selectedIndex');
			
			// -- Remove some information of the last condition added
			$('.conditionFromResource'+resourceId).removeClass('lastCondition'+resourceId);
			$('.removeCondition'+resourceId).remove();
			
			// -- Build the table line
			var tr = buildConditionLine(addConditionClass, resourceId, conditionId, conditionList, conditionValue, conditionOptional, selectedConditionType);
			tr.insertAfter('.conditionFromResource'+resourceId+':last');
			
			// Incr Condition ID
			$('#'+addConditionClass+'conditionId').attr('value', (conditionId+1));
			// Reinit
			$('#'+addConditionClass+'conditionTypeAdd').attr('selectedIndex', 0);
			$('#'+addConditionClass+'conditionValueAdd').val('');
			$('#'+addConditionClass+'conditionOptionalAdd').attr('checked', '');
		});
	
	
	
		// -- Remove a condition
		$('.removeCondition').live('click', function(){
			var btnAddCondition = $(this);
			var addConditionClass = btnAddCondition.attr('name');
			var resourceId = $('#'+addConditionClass+'resourceId').val();
			var conditionId = Math.round($('#'+addConditionClass+'conditionId').attr('value'));
			
			// Remove this condition
			var prevOne = $('.lastCondition'+resourceId).prev();
			$('.lastCondition'+resourceId).remove();
			$('#'+addConditionClass+'conditionId').attr('value', (conditionId-1));
			
			// Mark the previous one as "last"
			prevOne.addClass('lastCondition'+resourceId);
			prevOne.children('.action').html('<input type="button" name="resources'+resourceId+'" class="removeCondition removeCondition'+resourceId+'" value="Remove" />');
		});
	});
	
	
	
	
	function buildConditionLineHeader(resourceId, conditionList) {
		var tr = $('<tr>').addClass('resources'+resourceId+'conditionAddAction')
						.addClass('conditionFromResource'+resourceId);
		// - Condition type
		var tdSelectionCondition = $('<td>');
		var selectCondition = $('<select>').attr('name', 'resources'+resourceId+'conditionTypeAdd')
										.attr('id', 'resources'+resourceId+'conditionTypeAdd');
		$('<option>').attr('value', 'NONE')
					.html('--- Select ---')
					.appendTo(selectCondition);
		for(var i=0; i<conditionList.length; i++) {
			$('<option>').attr('value', conditionList[i])
						.html(conditionList[i])
						.appendTo(selectCondition);
		}
		selectCondition.attr('selectedIndex', 0);
		selectCondition.appendTo(tdSelectionCondition);
		tdSelectionCondition.appendTo(tr);
		// - Condition value
		$('<td>').html('<input type="text" name="resources'+resourceId+'conditionValueAdd" id="resources'+resourceId+'conditionValueAdd" />')
				.appendTo(tr);
		// - Condition optional?
		$('<td>').html('<input type="checkbox" name="resources'+resourceId+'conditionOptionalAdd" id="resources'+resourceId+'conditionOptionalAdd" value="1" />')
				.appendTo(tr);
		// - Action
		$('<td>').html('<input type="button" name="resources'+resourceId+'" class="addCondition" value="Add" />')
				.appendTo(tr);
		$('<td>').html('<input type="hidden" name="resources'+resourceId+'.resourceId" id="resources'+resourceId+'resourceId" value="'+resourceId+'" /><input type="hidden" name="resources'+resourceId+'.conditionId" id="resources'+resourceId+'conditionId" value="0" />')
				.appendTo(tr);
		return tr;
	}
	
	function buildConditionLine(addConditionClass, resourceId, conditionId, conditionList, conditionValue, conditionOptional, selectedConditionType) {
		var tr = $('<tr>').addClass('lastCondition'+resourceId)
							.addClass('conditionFromResource'+resourceId);
		// - Condition type
		var tdSelectionCondition = buildConditionTypeSelection(resourceId, conditionId, conditionList, selectedConditionType);
		tdSelectionCondition.appendTo(tr);
		// - Condition value
		$('<td>').html('<input type="text" name="resources['+resourceId+'].conditions['+conditionId+'].value" id="resources'+resourceId+'.conditions'+conditionId+'.value" value="'+conditionValue+'" />')
				.appendTo(tr);
		// - Condition optional?
		$('<td>').html('<input type="checkbox" name="resources['+resourceId+'].conditions['+conditionId+'].optional" id="resources'+resourceId+'.conditions'+conditionId+'.optional1" value="true" '+($('#'+addConditionClass+'conditionOptionalAdd').is(':checked') ? 'checked="checked" ' : '')+'/><input type="hidden" name="_resource['+resourceId+'].conditions['+conditionId+'].optional" value="on" />')
				.appendTo(tr);
		// - Action
		$('<td>').addClass('action')
				.html('<input type="button" name="resources'+resourceId+'" class="removeCondition removeCondition'+resourceId+'" value="Remove" />')
				.appendTo(tr);
		return tr;
	}
	function buildConditionTypeSelection(resourceId, conditionId, conditionList, selectedConditionType) {
		var tdSelectionCondition = $('<td>');
		var selectCondition = $('<select>').attr('name', 'resources['+resourceId+'].conditions['+conditionId+'].theCondition')
										.attr('id', 'resources'+resourceId+'.conditions'+conditionId+'.theCondition');
		$('<option>').attr('value', 'NONE')
					.html('--- Select ---')
					.appendTo(selectCondition);
		for(var i=0; i<conditionList.length; i++) {
			$('<option>').attr('value', conditionList[i])
						.html(conditionList[i])
						.appendTo(selectCondition);
		}
		selectCondition.attr('selectedIndex', selectedConditionType);
		selectCondition.appendTo(tdSelectionCondition);
		return tdSelectionCondition;
	}
	</script>
</body>
</html>

