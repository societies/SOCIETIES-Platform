$(document).ready(function(){
	// - At loading: hide advanced if necessary
	if ("CUSTOM" != $('input[type=radio][name=mode]:checked').val()) {
		$('.advanced-box').hide();
	}
	
	// - Select custom privacy policy
	$('.advanced-handler').click(function(){
		$('.advanced-box').toggle('slow', function() {
			if ($('.advanced-box').is(':visible')) {
				$('input[type=radio][name=mode]').attr('checked', '');
				$('.custom-radio').attr('checked', 'checked');
			}
		});
	});
	$('.mode-custom').click(function(){
		if ($('.advanced-box').is(':hidden')) {
			$('.advanced-box').show('slow');
		}
	});
	
	
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
			if (0 == $('.resources'+i+'resourceType').prop('selectedIndex')
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
		$('.requestedData').removeClass('lastResource');
		var newRequestedData = $('<fieldset>').addClass('requestedData').addClass('resource'+lastResourceId).addClass('lastResource');
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
						.html(resourceTypeHumanList[i])
						.appendTo(selectResourceType);
		}
		selectResourceType.appendTo(newRequestedData);
		// Resource Custom Type and Scheme
		$('<label>').attr('for', 'resources'+lastResourceId+'.resourceTypeCustom')
					.addClass('inline')
					.html('or tip a custom one')
					.appendTo(newRequestedData);
		// Scheme
		var selectResourceSchemeCustom = $('<select>').attr('name', 'resources['+lastResourceId+'].resourceSchemeCustom')
					.addClass('resources'+lastResourceId+'resourceSchemeCustom')
					.attr('id', 'resources'+lastResourceId+'.resourceSchemeCustom');
		for(var i=0; i<resourceSchemeList.length; i++) {
			$('<option>').attr('value', resourceSchemeList[i])
						.html(resourceSchemeList[i])
						.appendTo(selectResourceSchemeCustom);
		}
		selectResourceSchemeCustom.appendTo(newRequestedData);
		// Resource scheme error
		$('<span>').addClass('resources'+lastResourceId+'resourceSchemeError')
					.addClass('error')
					.appendTo(newRequestedData);
		// Type
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
		
		/* // - Resource Optional?
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
						.appendTo(newRequestedData); */
		
		// - Actions List
		$('<label>').attr('for', 'resources'+lastResourceId+'.actions1')
					.html('Actions to apply over this resource')
					.appendTo(newRequestedData);
		for(var i=0; i<actionList.length; i++) {
			// Action
			var inputAction = $('<input>').attr('type', 'checkbox')
						.attr('value', actionList[i])
						.attr('name', 'resources['+lastResourceId+'].actions['+i+'].action')
						.attr('id', 'resources'+lastResourceId+'.actions'+i+'.action1');
			if ('READ' == actionList[i]) {
				inputAction.attr('checked', 'checked');
			}
			inputAction.appendTo(newRequestedData);
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
		
		// - Button Remove
		$('<input>').addClass('removeResource')
					.addClass(' removeResource'+lastResourceId)
					.attr('name', 'resource'+lastResourceId)
					.attr('type', 'button')
					.attr('value', 'Remove this resource')
					.appendTo(newRequestedData);
		$('<input>').attr('id', 'resource'+lastResourceId)
					.attr('type', 'hidden')
					.attr('value', lastResourceId)
					.appendTo(newRequestedData);
		
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
		if (0 == $('#'+addConditionClass+'conditionTypeAdd').prop('selectedIndex')) {
			$('#'+addConditionClass+'conditionTypeAdd').css('border-color', 'red');
			return;
		}
		// Retrieve data
		var resourceId = $('#'+addConditionClass+'resourceId').val();
		var conditionId = Math.round($('#'+addConditionClass+'conditionId').attr('value'));
		var conditionValue = $('#'+addConditionClass+'conditionValueAdd').val();
		var conditionOptional = $('#'+addConditionClass+'conditionOptionalAdd').val();//resources${status.index}.conditionOptionalAdd
		var selectedConditionType = $('#'+addConditionClass+'conditionTypeAdd').prop('selectedIndex');
		
		// -- Remove some information of the last condition added
		$('.conditionFromResource'+resourceId).removeClass('lastCondition'+resourceId);
		$('.removeCondition'+resourceId).remove();
		
		// -- Build the table line
		var tr = buildConditionLine(addConditionClass, resourceId, conditionId, conditionList, conditionValue, conditionOptional, selectedConditionType);
		tr.insertAfter('.conditionFromResource'+resourceId+':last');
		
		// Incr Condition ID
		$('#'+addConditionClass+'conditionId').attr('value', (conditionId+1));
		// Reinit
		$('#'+addConditionClass+'conditionTypeAdd').prop('selectedIndex', 0);
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
	
	// -- Remove a resource
	$('.removeResource').live('click', function(){
		var btnRemoveResource = $(this);
		var removeResourceMarker = btnRemoveResource.attr('name');
		var resourceId = $('#'+removeResourceMarker).val();
		
		
		// If it was the last resource
		if ($('.'+removeResourceMarker).hasClass('lastResource')) {
			var prevOne = $('.'+removeResourceMarker).prev();
			lastResourceId--;
			// Mark the previous one as "last"
			prevOne.addClass('lastResource');
		}
		
		
		// Remove this resource
		$('.'+removeResourceMarker).remove();
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
		selectCondition.prop('selectedIndex', 0);
		selectCondition.appendTo(tdSelectionCondition);
		tdSelectionCondition.appendTo(tr);
		// - Condition value
		$('<td>').html('<input type="text" name="resources'+resourceId+'conditionValueAdd" id="resources'+resourceId+'conditionValueAdd" />')
				.appendTo(tr);
		// - Condition optional?conditionOptionalAdd
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
		selectCondition.prop('selectedIndex', selectedConditionType);
		selectCondition.appendTo(tdSelectionCondition);
		return tdSelectionCondition;
	}