$(document).ready(function(){
	$('.resource h5').click(function(){
		$(this).parent().find('.description').toggle('slow');
	});
});