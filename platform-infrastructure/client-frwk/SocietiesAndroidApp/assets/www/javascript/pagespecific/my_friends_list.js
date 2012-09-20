/**
 * Societies Android app Societies3PServices function(s) namespace
 * 
 * @namespace Societies3PServices
 */
var CSSFriendsServices = {
	
	/**
	 * @methodOf Societies3PServices#
	 * @description Refresh the 3P Service page with currently active services
	 * @returns null
	 */
	refreshFriendList: function() {
		console.log("Refreshing 3P Services");

		function success(data) {			
			//EMPTY TABLE - NEED TO LEAVE THE HEADER
			while( $('ul#FriendsListDiv').children().length >1 )
				$('ul#FriendsListDiv li:last').remove();

			//DISPLAY SERVICES
			for (i  = 0; i < data.length; i++) {
				var tableEntry = '<li><a href="#" onclick="CSSFriendsServices.showFriendDetails(\'' + data[i] + '\')">' +
					'<h2>' + data[i] + '</h2>' + 
					'<p>' + data[i] + '</p>' +
					'</a></li>';
				/*
				<li><a href="friend_profile.html">
				<img src="../images/profile_pic_sample.jpg" class="profile_list" alt="profile picture" />
				<h2>Sara Weber</h2>
				<p>Communities: 10</p>
				<p>Location: 10</p>
				</a></li>   
				*/
				jQuery('ul#FriendsListDiv').append(tableEntry);
			}
			$('ul#FriendsListDiv').listview('refresh');
		}
		
		function failure(data) {
			alert("refresh3PServices - failure: " + data);
		}
		
		window.plugins.SocietiesLocalCSSManager.getMyFriendsList(success, failure);
	},
		
	showFriendDetails: function (css_id) {
		
		function success(data) {
			//CSS Record OBJECT
			var markup = "<h1>" + data.foreName + " " + data.name + "</h1>" + 
						 "<p>" + data.homeLocation + "</p>" +
						 "<p>" + data.cssIdentity + "</p><br />"; 
			//INJECT
			$('div#friend_profile_info').html( markup );
			//ADDITIONAL INFO
			var addInfo = "<br/>" + "<p>Email: " + data.emailID + "</p>";
			$('li#friend_additional_info').html( addInfo );
					
			try {//REFRESH FORMATTING
				//ERRORS THE FIRST TIME AS YOU CANNOT refresh() A LISTVIEW IF NOT INITIALISED
				$('ul#friendInfoUL').listview('refresh');
			}
			catch(err) {}
			$.mobile.changePage($("#friend-profile"), { transition: "fade"} );
		}
		
		function failure(data) {
			alert("refresh3PServices - failure: " + data);
		}
		
		window.plugins.SocietiesLocalCSSManager.readRemoteCSSProfile(css_id, success, failure);
	}
};

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */
$(document).bind('pageinit',function(){

	console.log("pageinit: MyFriends jQuery calls");
	
	$("a#MyFriendsListLink").off('click').on('click', function(e){
		SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(CSSFriendsServices.refreshFriendList);
		$.mobile.changePage($("#my-friends-list"), { transition: "fade"} );
	});
	
	$("a#SuggestFriendsLink").off('click').on('click', function(e){
		SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(CSSFriendsServices.refresh3PServices);
	});
	
	
});
