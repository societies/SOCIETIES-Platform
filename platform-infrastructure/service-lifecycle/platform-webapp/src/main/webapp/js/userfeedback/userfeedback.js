if ( ! window.console ) {
    (function() {
      var names = ["log", "debug", "info", "warn", "error",
          "assert", "dir", "dirxml", "group", "groupEnd", "time",
          "timeEnd", "count", "trace", "profile", "profileEnd"],
          i, l = names.length;
      window.console = {};
      for ( i = 0; i < l; i++ ) {
        window.console[ names[i] ] = function() {};
      }
    }());
}


var userFeedback = (function () {
	/* ************************
	 * 		Parameters
	 **************************/
	var lastNotificationId = 0;
	var boxId = '#ufeedbackNotifications';
	var handlerId = '.ufeedbackRefreshHandler';
	var url = {getForm: 'get_form.html', sendAnswer: 'get_form.html' };
	var formType = {RADIOLIST: "radio", CHECKBOXLIST: "check", ACKNACK: "ack", ABORT: "abort", NOTIFICATION: "notification", EMPTY: "NO_REQUESTS"};
	var toastTime = 5000; // 5s

	var oneSecond = 1000; // 1s
	var timeoutTimer = $.timer(function(){}, oneSecond, false);
	var poolingNormalTime = 5000; // 5s
	var poolingBusyTime = 15000; // 15s
	var poolingTimer = $.timer(retrieveNotification, poolingNormalTime, false);


	var loader = $('<img>').addClass('ufeedbackLoader')
	.attr('src', '/societies-test/images/loading.gif')
	.attr('alt', 'Loading')
	.hide();
	var result = $('<span>').addClass('ufeedbackResult')
	.hide();


	/* ************************
	 * 		Functions
	 **************************/
	/* Retrieve/Display form  */
	function retrieveNotification() {
		$.ajax({
			url: url.getForm,
			beforeSend: function (xhr, settings) {
				console.log("Before retrieve notification");
				// Hide old toast result
				result.hide();
				// Display loader
				loader.show();
				// Reduce pooling time interval
				poolingTimerBusy();
			},
			dataFilter: function(data, dataType) {
				//var data = jQuery.parseJSON(data);
				console.log("Response received ("+dataType+"): ", data);
				return data;
			},
			success: function(data, textStatus, xhr) {
				console.log("Success: notification retrieved", textStatus);
				// -- No form to display
				if (formType.EMPTY == data.type) {
					// Pooling timer normal internal
					poolingTimerNormal();
					return;
				}
				// -- Form already displayed: don't do anything
				if (lastNotificationId == data.id) {
					return;
				}
				// -- Manage data form
				lastNotificationId = data.id;
				// - Timeout and timer
				// Stop timeout count
				timeoutTimer.stop();
				// Pooling timer normal interval
				if (formType.NOTIFICATION == data.type) {
					poolingTimerNormal();
				}
				// - Generate and display form
				// Generate the relevant form
				var form = renderForm(data);
				// Display the notification
				displayForm(form);
			},
			error: function(xhr, textStatus, e) {
				console.log("Error: ", textStatus, e);
				// Pooling timer normal internal
				poolingTimerNormal();
			},
			complete: function(xhr, textStatus) {
				console.log("Complete", textStatus);
				// Remove loader
				loader.hide('slow');
			}
		});
	}

	function poolingTimerBusy() {
		poolingTimer.set({time: poolingBusyTime});
		if (poolingTimer.isActive) {
			poolingTimer.reset();
		}
	}
	function poolingTimerNormal() {
		poolingTimer.set({time: poolingNormalTime});
		if (poolingTimer.isActive) {
			poolingTimer.reset();
		}
	}

	function displayForm(form) {
		// Clean the previous notification
		$(boxId).html('');
		// Display
		$(boxId).append(form).slideDown('slow');
	}

	function renderForm(formInfo) {
		// - Generate generic form
		var res = $('<div>').addClass('ufeedbackNotification');
		$('<span>').addClass('notificationText')
		.addClass(formInfo.type)
		.html(formInfo.text)
		.appendTo(res);
		$('<input>').addClass('notificationId')
		.attr('type', 'hidden')
		.attr('value', formInfo.id)
		.appendTo(res);
		$('<input>').addClass('notificationType')
		.attr('type', 'hidden')
		.attr('value', formInfo.type)
		.appendTo(res);

		// - Add specific information
		if (null != formInfo && formType.ACKNACK == formInfo.type) {
			res = renderFormAcknack(res, formInfo);
		}
		else if (null != formInfo && formType.RADIOLIST == formInfo.type) {
			res = renderFormRadioList(res, formInfo);
		}
		else if (null != formInfo && formType.CHECKBOXLIST == formInfo.type) {
			res = renderFormCheckoxList(res, formInfo);
		}
		else if (null != formInfo && formType.ABORT == formInfo.type) {
			res = renderFormAbort(res, formInfo);
		}
		else if (null != formInfo && formType.NOTIFICATION == formInfo.type) {
			res = renderFormNotification(res, formInfo);
		}
		else {
			return "";
		}
		return res;
	}

	function renderFormAcknack(res, formInfo) {
		for(var i=0; i< formInfo.data.length; i++) {
			$('<input>').addClass('sendFeedback')
			.attr('type', 'button')
			.attr('name', 'data'+i)
			.attr('value', formInfo.data[i])
			.appendTo(res);
		}
		return res;
	}

	function renderFormRadioList(res, formInfo) {
		var ul = $('<ul class="list radiolist">').appendTo(res);
		for(var i=0; i< formInfo.data.length; i++) {
			var li = $('<li>').appendTo(ul);
			$('<input>').attr('type', 'radio')
			.attr('name', 'data')
			.attr('id', 'data'+i)
			.attr('value', formInfo.data[i])
			.appendTo(li);
			$('<label>').attr('for', 'data'+i)
			.html(formInfo.data[i])
			.appendTo(li);
		}
		$('<input>').addClass('sendFeedback')
		.attr('type', 'button')
		.attr('value', 'Send')
		.appendTo(res);
		return res;
	}

	function renderFormCheckoxList(res, formInfo) {
		var ul = $('<ul class="list checkboxlist">').appendTo(res);
		for(var i=0; i< formInfo.data.length; i++) {
			var li = $('<li>').appendTo(ul);
			$('<input>').attr('type', 'checkbox')
			.attr('name', 'data'+i)
			.attr('id', 'data'+i)
			.attr('checked', 'checked')
			.attr('value', formInfo.data[i])
			.appendTo(li);
			$('<label>').attr('for', 'data'+i)
			.html(formInfo.data[i])
			.appendTo(li);
		}
		$('<input>').addClass('sendFeedback')
		.attr('type', 'button')
		.attr('value', 'Send')
		.appendTo(res);
		return res;
	}

	function renderFormAbort(res, formInfo) {
		// Add form
		var timeout = displayTimeout(formInfo.data[0]);
		var continueButton = $('<input>').addClass('sendFeedback')
		.addClass('timeout')
		.attr('type', 'button')
		.attr('name', 'continue')
		.attr('value', "Continue ("+timeout+"s)");
		continueButton.appendTo(res);
		var abortButton = $('<input>').addClass('sendFeedback')
		.attr('type', 'button')
		.attr('name', 'abort')
		.attr('value', "Abort");
		abortButton.appendTo(res);

		// Manage timeout
		timeoutTimer.set(function() {
			$('.timeout').attr('value', "Continue ("+(--timeout)+"s)");
			if (timeout <= 0) {
				this.stop();
				var answer = retrieveAnswer(continueButton, true);
				console.log("Send timeout answer: ", answer);
				sendAnswer(answer);
				return;
			}
		});
		timeoutTimer.play();
		return res;
	}

	function renderFormNotification(res, formInfo) {
		// Add form
		var timeout = displayTimeout(formInfo.data[0]);
		var button = $('<input>').addClass('sendFeedback')
		.addClass('timeout')
		.attr('type', 'button')
		.attr('value', "Close ("+timeout+"s)");
		button.appendTo(res);

		// Manage timeout
		timeoutTimer.set(function() {
			$('.timeout').attr('value', "Close ("+(--timeout)+"s)");
			if (timeout <= 0) {
				this.stop();
				var answer = retrieveAnswer(button, true);
				console.log("Send timeout answer: ", answer);
				sendAnswer(answer);
				return;
			}
		});
		timeoutTimer.play();
		return res;
	}

	function displayTimeout(seconds) {
		return Math.ceil(parseInt(seconds)/1000);
	}

	/*       Send answer     */
	function sendAnswer(data) {
		$.ajax({
			url: url.sendAnswer,
			type: "POST",
			data: {data: JSON.stringify(data)},
			beforeSend: function (xhr) {
				console.log("Before send answer");
				// Hide old toast result
				result.hide();
				// Display loader
				loader.show();
				// Stop timeout count
				timeoutTimer.stop();
				// Pooling timer normal internal
				poolingTimerNormal();
			},
			success: function(formInfo) {
				console.log("Success");
				console.log(formInfo);
				// Clean the previous notification
				closeNotification();
				// Display result
				result.addClass(formInfo.ack ? 'ok' : 'error')
				.html(formInfo.ack ? 'Ok!' : 'Oups, error!')
				.fadeIn('slow')
				.delay(toastTime).fadeOut('slow');
			},
			error: function(e) {
				console.log("Error", e);
			},
			complete: function(jqXHR, textStatus) {
				console.log("Complete", textStatus);
				// Remove loader
				loader.hide('slow');
				// Relaunch
				retrieveNotification();
			}
		});
	}

	function retrieveAnswer(clickedElement, timeout) {
		var notificationDom = clickedElement.parent();
		var notification = {
				id: notificationDom.find('.notificationId').val(),
				type: notificationDom.find('.notificationType').val(),
				data: []
		};
		// Ack nack
		if (userFeedback.formType.ACKNACK == notification.type) {
			notification.data[0] = clickedElement.val();
		}
		else if (userFeedback.formType.RADIOLIST == notification.type) {
			notification.data[0] = notificationDom.find(':checked').val();
		}
		else if (userFeedback.formType.CHECKBOXLIST == notification.type) {
			notificationDom.find(':checked').each(function(i, element) {
				notification.data[i] = $(element).val();
			});
		}
		else if (userFeedback.formType.ABORT == notification.type) {
			notification.data[0] = ('continue' == clickedElement.attr('name') ? true : false);
		}
		else if (userFeedback.formType.NOTIFICATION == notification.type) {
			notification.data[0] = (true);
		}
		return notification;
	}

	function closeNotification() {
		lastNotificationId = 0;
		$(boxId).slideUp('slow');
		result.removeClass(['ok', 'error']);

	}

	// Public
	return {
		boxId: boxId,
		handlerId: handlerId,
		url: url,
		formType: formType,
		toastTime: toastTime,
		loader: loader,
		result: result,
		timeoutTimer: timeoutTimer,
		poolingNormalTime: poolingNormalTime,
		poolingBusyTime: poolingBusyTime,
		poolingTimer: poolingTimer,

		retrieveNotification: retrieveNotification,
		sendAnswer: sendAnswer,
		retrieveAnswer: retrieveAnswer,
		closeNotification: closeNotification,
	};

}());


/* ************************
 * 		Controller
 **************************/
$(document).ready(function(){
	// -- Prepare UI
	userFeedback.loader.insertBefore(userFeedback.boxId);
	userFeedback.result.insertBefore(userFeedback.boxId);

	// -- Init pooling
	userFeedback.poolingTimer.play();

	// -- Handler: retrieve notification
	$(userFeedback.handlerId).click(function() {
		// -- Send request to check if there is notifications to display
		userFeedback.retrieveNotification();
	});

	// -- Handler: send an answer to a notification
	$('.sendFeedback').live('click', function() {
		// -- Retrieve data
		var answer = userFeedback.retrieveAnswer($(this));
		// -- Send answer
		console.log("Send answer: ", answer);
		userFeedback.sendAnswer(answer);
	});

	// -- Handler: close notification
	$('.closeFeedback').live('click', function() {
		// -- Stop timeout count
		userFeedback.timeoutTimer.stop();
		// -- Hide notification
		userFeedback.closeNotification();
	});

	// -- Handlers: manage pooling
	$('.ufeedbackPoolingRefresh').click(function() {
		// -- Send request to check if there is notifications to display
		userFeedback.retrieveNotification();
		return false;
	});
	$('.ufeedbackPoolingStartStop').click(function() {
		userFeedback.poolingTimer.toggle();
		if ('Start' == $(this).html()) {
			$(this).html('Stop').removeClass('start').addClass('stop');
		}
		else {
			$(this).html('Start').removeClass('stop').addClass('start');
		}
		return false;
	});
});
