// Web Nav dropdown menu

function nt_nav() {
	/*$('#webmenu_nav ul').css({display:'none'});*/ // Opera 
	$('#webmenu_nav li').hover(
		function () {
			$(this).find('ul:first').css({display:'none'}).slideDown(200);
		},
		
		function () {
		
			$(this).find('ul:first').slideUp(150);
		
		});
}
 
$(document).ready(function() {
	nt_nav();
	});


startList = function() {
	if (document.all&&document.getElementById) {
	
		navRoot = document.getElementById("nav");

		for (i=0; i<navRoot.childNodes.length; i++) {
			node = navRoot.childNodes[i];
			if (node.nodeName == "LI") {
				node.onmouseover = function() {
				this.className+= "over";
  				}
				node.onmouseout = function() {
					this.className = this.className.replace("over", "");
				}
			}
		}
	}
}

window.onload=startList;