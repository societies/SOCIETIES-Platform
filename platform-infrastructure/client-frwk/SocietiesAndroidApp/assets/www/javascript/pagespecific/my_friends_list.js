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
				var tableEntry = '<li><a href="#" onclick="CSSFriendsServices.showFriendDetails(\'' + data[i].id + '\')">' +
					'<h2>' + data[i].name + '</h2>' + 
					'<p>' + data[i].id + '</p>' +
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
			CSSFriendsServices.showFriendDetailPage(data);
			$.mobile.changePage($("#friend-profile"), { transition: "fade"} );
		}
		
		function failure(data) {
			alert("refresh3PServices - failure: " + data);
		}
		
		window.plugins.SocietiesLocalCSSManager.readRemoteCSSProfile(css_id, success, failure);
	},
	
	showFriendDetailPage: function(data) {
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
	},
	
	refreshSuggestedFriendsList: function() {
		console.log("Refreshing Suggested friends");

		function success(data) {			
			CSSFriendsServices.displayCSSAdvertRecords(data);
		}
		
		function failure(data) {
			alert("refresh3PServices - failure: " + data);
		}
		
		window.plugins.SocietiesLocalCSSManager.getSuggestedFriends(success, failure);
	},
	
	displayCSSAdvertRecords: function(data) {
		//EMPTY TABLE - NEED TO LEAVE THE HEADER
		while( $('ul#SuggestedFriendsListUL').children().length >1 )
			$('ul#SuggestedFriendsListUL li:last').remove();

		//DISPLAY SUGGESTIONS
		for (i  = 0; i < data.length; i++) {
			var tableEntry = '<li><a href="#" onclick="CSSFriendsServices.sendFriendRequest(\'' + data[i].name + '\', \'' + data[i].id + '\')">' +
				'<h2>' + data[i].name + '</h2>' + 
				'<p>' + data[i].id + '</p>' +
				'</a></li>';
			jQuery('ul#SuggestedFriendsListUL').append(tableEntry);
		}
		$('ul#SuggestedFriendsListUL').listview('refresh');
	},
	
	sendFriendRequest: function(name, css_id) {
		
		function success(data) {
			CSSFriendsServices.showFriendDetailPage(data);
			$.mobile.changePage($("#friend-profile"), {transition: "fade"});
		}
		
		function failure(data) {
			alert("getJoinResponse - failure: " + data);
		}
		
		//SEND REQUEST
		if (window.confirm("Send friend request to " + name + "?")) {
			window.plugins.SocietiesLocalCSSManager.sendFriendRequest(css_id, success, failure);
		}
	},
	
	searchCssDirectory: function(searchTerm) {
		console.log("Search CIS Dir for " + searchTerm);
		
		function success(data) {
			CSSFriendsServices.displayCSSAdvertRecords(data);
			$.mobile.changePage($("#suggested-friends-list"), { transition: "fade"} );
		}
		
		function failure(data) {
			alert("searchCisDirectory - failure: " + data);
		}
		window.plugins.SocietiesLocalCSSManager.findForAllCss(searchTerm, success, failure);
	},
	
	returnAllCssDirAdverts: function() {
		console.log("returnAllCssDirAdverts");
		
		function success(data) {
			CSSFriendsServices.displayCSSAdvertRecords(data);
			$.mobile.changePage($("#suggested-friends-list"), { transition: "fade"} );
		}
		
		function failure(data) {
			alert("searchCisDirectory - failure: " + data);
		}
		window.plugins.SocietiesLocalCSSManager.findAllCssAdvertisementRecords(success, failure);
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
		SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(CSSFriendsServices.refreshSuggestedFriendsList);
		$.mobile.changePage($("#suggested-friends-list"), { transition: "fade"} );
	});
	
	$('input#btnSearchFriends').off('click').on('click', function() {
		var search = $("#search-friends").val();
		if (search != "Search Friends" && search != "") 
			CSSFriendsServices.searchCssDirectory(search);
		else {
			SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(CSSFriendsServices.returnAllCssDirAdverts);
			$.mobile.changePage($("#suggested-friends-list"), { transition: "fade"} );
		}
	});
	
});
