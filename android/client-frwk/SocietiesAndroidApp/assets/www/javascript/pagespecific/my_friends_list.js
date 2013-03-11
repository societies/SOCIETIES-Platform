/**
 * Societies Android app Societies3PServices function(s) namespace
 * 
 * @namespace Societies3PServices
 */
var CSSFriendsServices = {
	
	/**
	 * @methodOf Societies3PServices#
	 * @description Refresh your friend requests
	 * @returns null
	 */
	refreshFriendRequests: function() {
		function success(data) {
			//UPDATE COUNT
			$("span#myFriendRequests").html(data.length);
			$("span#myFriendRequests").css({ visibility: "visible"});
			//EMPTY TABLE - NEED TO LEAVE THE HEADER
			while( $('ul#FriendRequestsListUL').children().length >1 )
				$('ul#FriendRequestsListUL li:last').remove();
			//DISPLAY SERVICES
			for (i  = 0; i < data.length; i++) {
				var tableEntry = '<li id="li' + i + '"><a href="#" onclick="CSSFriendsServices.acceptFriendRequest(\'' + data[i].name + '\', \'' + data[i].id + '\', ' + i + ')">' +
					'<img src="images/profile_pic.png" />' +
					'<h2>' + data[i].name + '</h2>' + 
					'<p>' + data[i].id + '</p>' +
					'</a></li>';
				jQuery('ul#FriendRequestsListUL').append(tableEntry);
			}
			$('ul#FriendRequestsListUL').listview('refresh');
		}
		
		function failure(data) {
			alert("refreshFriendRequests - failure: " + data);
		}
		
		window.plugins.SocietiesLocalCSSManager.getFriendRequests(success, failure);
	},
	
	/**
	 * @methodOf Societies3PServices#
	 * @description Refresh the 3P Service page with currently active services
	 * @returns null
	 */
	refreshFriendList: function() {
		console.log("refreshFriendList");

		function success(data) {
			//UPDATE COUNT
			$("span#myFriendsCount").html(data.length);
			$("span#myFriendsCount").css({ visibility: "visible"});
			//EMPTY TABLE - NEED TO LEAVE THE HEADER
			while( $('ul#FriendsListDiv').children().length >1 )
				$('ul#FriendsListDiv li:last').remove();

			//DISPLAY SERVICES
			for (i  = 0; i < data.length; i++) {
				var tableEntry = '<li><a href="#" onclick="CSSFriendsServices.showFriendDetails(\'' + data[i].id + '\')">' +
					'<img src="images/profile_pic.png" />' +	
					'<h2>' + data[i].name + '</h2>' + 
					'<p>' + data[i].id + '</p>' +
					'</a></li>';
				$('ul#FriendsListDiv').append(tableEntry);
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
			alert("Error displaying friend details");
		}
		
		window.plugins.SocietiesLocalCSSManager.readRemoteCSSProfile(css_id, success, failure);
	},
	
	showFriendDetailPage: function(data) {
		//CSS Record OBJECT
		var forename = data.foreName;
		if (forename=="undefined")
			forename="";
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
			//UPDATE COUNT
			$("span#suggestedFriendsCount").html(data.length);
			$("span#suggestedFriendsCount").css({ visibility: "visible"});			
			//DISPLAY RECORDS
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
			var tableEntry = '<li id="li' + i + '"><a href="#" onclick="CSSFriendsServices.sendFriendRequest(\'' + data[i].name + '\', \'' + data[i].id + '\', ' + i + ')">' +
				'<img src="images/profile_pic.png" />' +	
				'<h2>' + data[i].name + '</h2>' + 
				'<p>' + data[i].id + '</p>' +
				'</a></li>';
			$('ul#SuggestedFriendsListUL').append(tableEntry);
		}
		$('ul#SuggestedFriendsListUL').listview('refresh');
	},
	
	sendFriendRequest: function(name, css_id, id) {
		function success(data) {
			$('#li' + id).remove().slideUp('slow');
		}
		
		function failure(data) {
			alert("sendFriendRequest - failure: " + data);
		}
		
		//SEND REQUEST
		if (window.confirm("Send friend request to " + name + "?")) {
		//jConfirm("Send friend request to " + name + "?", 'Friend Request', function(answer) {
			//if (answer) {
		    	 $('#li' + id).append("Sending Request...");
		    	 window.plugins.SocietiesLocalCSSManager.sendFriendRequest(css_id, success, failure);
			//}
		}
		//);
	},
	
	acceptFriendRequest: function(name, css_id, id) {
		function success(data) {
			$('#li' + id).remove().slideUp('slow');
			//CSSFriendsServices.showFriendDetailPage(data);
			//$.mobile.changePage($("#friend-profile"), {transition: "fade"});
			//UPDATE COUNTER
			var count = parseInt($("span#myFriendRequests").html());
			$("span#myFriendRequests").html(--count);
		}
		
		function failure(data) {
			alert("sendFriendRequest - failure: " + data);
		}

		//ACCEPT REQUEST
		if (window.confirm("Accept friend request from " + name + "?")) {
		//jConfirm("Accept friend request from " + name + "?", 'Accept Friend', function(answer) {
		     //if (answer){
		    	 $('#li' + id).append("Accepting Request...");
		 		window.plugins.SocietiesLocalCSSManager.acceptFriendRequest(css_id, success, failure);
		     //}
		}
		//);
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
$(document).on('pageinit', '#friends-landing', function(event) {

	console.log("pageinit: MyFriends jQuery calls");
	
	$("a#MyFriendsListLink").off('click').on('click', function(e){
		SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(CSSFriendsServices.refreshFriendList);
		$.mobile.changePage($("#my-friends-list"), { transition: "fade"} );
	});
	
	$("a#SuggestFriendsLink").off('click').on('click', function(e){
		SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(CSSFriendsServices.refreshSuggestedFriendsList);
		$.mobile.changePage($("#suggested-friends-list"), { transition: "fade"} );
	});
	
	$("a#FriendRequestsLink").off('click').on('click', function(e){
		SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(CSSFriendsServices.refreshFriendRequests);
		$.mobile.changePage($("#friend-requests-list"), { transition: "fade"} );
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

	$('#search-friends').off('focus').on('focus', function(){
		SocietiesLogin.clearElementValue('#search-friends');
	});
	
	$("form#formCSSDirSearch").submit(function(e) {
		var search = $("#search-friends").val();
		if (search != "Search Friends" && search != "") 
			CSSFriendsServices.searchCssDirectory(search);
		else
			SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(CSSFriendsServices.returnAllCssDirAdverts);
		e.preventDefault();
		return false;
	});
});
