/* **************************
 * 		Parameters
 ****************************/
var userfeedbackBoxId = '#ufeedbackNotifications';
var url = {getForm: 'get-form.html', sendAnswer: 'send-answer.html' };
var formType = {RADIOLIST: 0, CHECKBOXLIST: 1, ACKNACK: 2};


$(document).ready(function(){
	$('.ufeedbackRefreshHandler').click(function() {
		retrieveNotification();
	});
	
	$('.sendFeedback').live('click', function() {
		// -- Retrieve data
		var notificationDom = $(this).parent();
		var notification = {
				id: notificationDom.find('.notificationId').val(),
				type: notificationDom.find('.notificationType').val(),
				data: []
		}
		console.log(notification);
		sendAnswer(notification);
	});
});

function retrieveNotification() {
	$.ajax({
		url: url.getForm,
		beforeSend: function (xhr) {
			console.log("Before send");
		},
		success: function(formInfo, textStatus, xhr) {
			console.log("Success");
			console.log(formInfo);
			// Generate the relevant form
			var form = renderForm(formInfo);
			// Clean the previous notification
			$(userfeedbackBoxId).html('');
			// Display the notification
			form.appendTo(userfeedbackBoxId);
		},
		error: function(e) {
			console.log("Error");
			console.log(e);
		},
		complete: function(jqXHR, textStatus) {
			console.log("Complete");
		}
	});
}

function sendAnswer(data) {
	$.ajax({
		url: url.sendAnswer,
		data: data,
		beforeSend: function (xhr) {
			console.log("Before send");
		},
		success: function(formInfo) {
			console.log("Success");
			console.log(formInfo);
			// Clean the previous notification
			$(userfeedbackBoxId).html('');
		},
		error: function(e) {
			console.log("Error");
			console.log(e);
		},
		complete: function(jqXHR, textStatus) {
			console.log("Complete");
		}
	});
}

function renderForm(formInfo) {
	if (formType.ACKNACK == formInfo.type && null != formInfo) {
		return renderFormAcknack(formInfo);
	}
	if (formType.RADIOLIST == formInfo.type && null != formInfo) {
		return renderFormRadioList(formInfo);
	}
	if (formType.CHECKBOXLIST == formInfo.type && null != formInfo) {
		return renderFormCheckoxList(formInfo);
	}
	return $('<div>').addClass('nothing').html('nothing');
}

function renderFormAcknack(formInfo) {
	var res = $('<div>').addClass('acknack').html('Acknack form');
	return res;
}

function renderFormRadioList(formInfo) {
	var res = $('<div>').addClass('radiolist').html('Radiolist form');
	return res;
}
function renderFormCheckoxList(formInfo) {
	var res = $('<div>').addClass('checkboxlist').html('Checkboxlist form');
	return res;
}
