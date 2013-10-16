$(function() {
	$(".button_connector").draggable({
		containment: "#connection_container",
		revert: true
	});
	$( "#connection_area" ).droppable({
		drop: function( event, ui ){
			var socialName="";
			if(ui.draggable.attr('id') == 'fq_button'){
				socialName="connect_fq";
			}else if(ui.draggable.attr('id') == 'fb_button'){
				socialName="connect_fb";
			}else if(ui.draggable.attr('id') == 'ln_button'){
				socialName="connect_lk";
			}else if(ui.draggable.attr('id') == 'tw_button'){
				socialName="connect_tw";
			}
			//alert(socialName+" connected!");
			connectSN(socialName);
		}
	});

	$('.hover_img').hover(
			function(){
				lastImg = $(this).attr("src");
				$(this).attr("src","images/exit.png");
				$(this).css("cursor","pointer");
				$(this).click(function(){
					disconnectSN($(this).attr("id"));
					//alert($(this).attr("id"));
				});
			},
			function(){
				$(this).attr("src",lastImg);
				$(this).css("cursor","");
			}

	);
});

function manageSelection(obj){
	if($(obj).val() == 'ck_all' && $(obj).is(':checked')){
		$('.messenger_ck').attr('checked', 'checked');
	} else {
		if(!$(obj).is(':checked')){
			$('#ck_all').attr('checked', false);
		}
	}
}

function connectSN(sn){
	document.getElementById("method").value  = sn;
	document.sd.submit();
}

function disconnectSN(id){
	document.getElementById("method").value  = "remove";
	document.getElementById("id").value  = id;
	document.sd.submit();
}

function goToSocial(method){
	document.getElementById("method").value  = method;
	document.sd.submit();
}

function sendPost(){
	if($('#messageToPost').val()==''){
		alert("Unable to send a post, Message is void");
		return
	}

	var ckList = $('.messenger_ck');
	var par="";
	for(var i = 0; i< ckList.length ; i++){
		if($(ckList[i]).is(':checked')){
			par += "|"+$(ckList[i]).attr('name');
		}
	}
	if(par == ''){
		alert('Unable to send a post, select a social network!');
		return;
	}
	
	document.getElementById("method").value  = 'postMessage';
	document.getElementById("snName").value = par.substring(1,par.length); //remove first pipe char
	document.getElementById("params").value  = $('#messageToPost').val();
	document.sd.submit();
}
