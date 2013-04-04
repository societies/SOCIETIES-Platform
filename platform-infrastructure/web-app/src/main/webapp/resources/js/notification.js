$(document).ready(function() {
	
	$('#notification-image').click(function() {
		// Getting the variable's value from a link 
		//var notificationBox = $(this).attr('#notification-box');

		//Fade in the Popup and add close button
		$('.notification-popup').fadeIn(300);
		
		//Set the center alignment padding + border
		var popMargTop = ($('.notification-popup').height() + 24) / 2; 
		var popMargLeft = ($('.notification-popup').width() + 24) / 2; 
		
		$('.notification-popup').css({ 
			'margin-top' : -popMargTop,
			'margin-left' : -popMargLeft
		});
		
		// Add the mask to body
		$('body').append('<div id="mask"></div>');
		$('#mask').fadeIn(300);
		resetNotificationCount();
		return false;
	});
		
	// When clicking on the button close or the mask layer the popup closed
	$('a.close, #mask').live('click', function() { 
	  $('#mask , .notification-popup').fadeOut(300 , function() {
		$('#mask').remove();  
	}); 
	return false;
	});
});

var count = 0;
function increaseNotificationCount(){
	count++;
	$('#notification-count').html(count);
	$('#notification-count').show();
}	

function resetNotificationCount(){
	count=0;
	$('#notification-count').hide();
}

function addPanel(panel){

	$('.notifications').append(panel);	
	
	increaseNotificationCount();
}

function addSlider(panel){
	
	$('.notifications').append(panel);		
	$.getScript("js/jquery.ui.slider.js");	
	
	increaseNotificationCount();
}

function removePanel(panelId){
	$(panelId).hide();
}
