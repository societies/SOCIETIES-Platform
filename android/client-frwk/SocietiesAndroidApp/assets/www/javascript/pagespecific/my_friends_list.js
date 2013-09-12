/**
 * Societies Android app Societies3PServices function(s) namespace
 * 
 * @namespace Societies3PServices
 */
var CSSFriendsServices = {
	
	isFacebookFlagged: function(flag) {
		var FACEBOOK_BIT=0x0000000001;
		return (flag & FACEBOOK_BIT) === FACEBOOK_BIT; 
	},
			
	isTwitterFlagged: function(flag) {
		var TWITTER_BIT=0x0000000010;
		return (flag & TWITTER_BIT) === TWITTER_BIT; 
	},
	
	isLinkedinFlagged: function(flag) {
		var LINKEDIN_BIT=0x0000000100;
		return (flag & LINKEDIN_BIT) === LINKEDIN_BIT; 
	},
	
	isFoursquareFlagged: function(flag) {
		var FOURSQUARE_BIT=0x0000001000;
		return (flag & FOURSQUARE_BIT) === FOURSQUARE_BIT; 
	},
	
	isGooglePlusFlagged: function(flag) {
		var GOOGLEPLUS_BIT=0x0000010000;
		return (flag & GOOGLEPLUS_BIT) === GOOGLEPLUS_BIT; 
	},
	
	/**
	 * @methodOf Societies3PServices#
	 * @description Refresh your friend requests
	 * @returns null
	 */
	refreshFriendRequests: function() {
		function showAvatar(VCard) {
			if (VCard.from != null) {
				var n=VCard.from.indexOf("@");
				var identityStr = VCard.from.substring(0, n);
				var imageStr = VCard.avatar;
				if (imageStr != null)
					$('img#' + identityStr).attr("src", "data:image/jpg;base64," + VCard.avatar);
			}
		}
		
		function success(data) {
			//UPDATE COUNT
			$("span#myFriendRequests").html(data.length);
			$("span#myFriendRequests").css({ visibility: "visible"});
			//EMPTY TABLE - NEED TO LEAVE THE HEADER
			while( $('ul#FriendRequestsListUL').children().length >1 )
				$('ul#FriendRequestsListUL li:last').remove();
			//DISPLAY REQUESTS
			for (i  = 0; i < data.length; i++) {
				var n=data[i].id.indexOf(".");
				var identityStr = data[i].id.substring(0, n);
				var tableEntry = '<li id="li' + i + '"><a href="#" onclick="CSSFriendsServices.acceptFriendRequest(\'' + data[i].name + '\', \'' + data[i].id + '\', ' + i + ')">' +
					'<img src="images/profile_pic.png" id="' + identityStr + '" style="max-width:100px;" />' +
					'<h2>' + data[i].name + '</h2>' + 
					'<p>' + data[i].id + '</p>' +
					'</a></li>';
				$('ul#FriendRequestsListUL').append(tableEntry);
				window.plugins.SocietiesLocalCSSManager.getVCardUser(data[i].id, showAvatar, failure);
			}
			$('ul#FriendRequestsListUL').listview('refresh');
		}
		
		function failure(data) {
			console.log("refreshFriendRequests failure: " + data);
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

		function showAvatar(VCard) {
			if (VCard.from != null) {
				var n=VCard.from.indexOf("@");
				var identityStr = VCard.from.substring(0, n);
				var imageStr = VCard.avatar;
				if (imageStr != null)
					$('img#' + identityStr).attr("src", "data:image/jpg;base64," + VCard.avatar);
			}
		}
		
		function success(data) {
			//UPDATE COUNT
			$("span#myFriendsCount").html(data.length);
			$("span#myFriendsCount").css({ visibility: "visible"});
			//EMPTY TABLE - NEED TO LEAVE THE HEADER
			while( $('ul#FriendsListDiv').children().length >1 )
				$('ul#FriendsListDiv li:last').remove();

			//DISPLAY FRIENDS
			for (i  = 0; i < data.length; i++) {
				var n=data[i].id.indexOf(".");
				var identityStr = data[i].id.substring(0, n);
				var tableEntry = '<li><a href="#" onclick="CSSFriendsServices.showFriendDetails(\'' + data[i].id + '\')">' +
					'<img src="images/profile_pic.png" id="' + identityStr + '" style="max-width:100px;" />' +
					'<h2>' + data[i].name + '</h2>' + 
					'<p>' + data[i].id + '</p>' +
					'</a></li>';
				$('ul#FriendsListDiv').append(tableEntry);
				window.plugins.SocietiesLocalCSSManager.getVCardUser(data[i].id, showAvatar, failure);
			}
			$('ul#FriendsListDiv').listview('refresh');
		}
		
		function failure(data) {
			console.log("refreshFriendList failure: " + data);
		}
		
		window.plugins.SocietiesLocalCSSManager.getMyFriendsList(success, failure);
	},
		
	showFriendDetails: function (css_id) {
		function success(data) {
			CSSFriendsServices.showFriendDetailPage(data);
			// Populate Trust Level
			jTrustLevel.initRating('#user-trust-level', 'CSS', css_id);
			jTrustLevel.showDetails('#user-trust-level', 'CSS', css_id);
			$.mobile.changePage($("#friend-profile"), { transition: "fade"} );
		}
		
		function failure(data) {
			alert("Error displaying friend details");
		}
		
		window.plugins.SocietiesLocalCSSManager.readRemoteCSSProfile(css_id, success, failure);
	},
	
	showFriendDetailPage: function(data) {
		function success(data) {
			//DISPLAY PROFILE IMAGE IF AVAILABLE
			
			//$("img").attr({
			//	  src: "/resources/hat.gif",
			//	  title: "jQuery",
			//	  alt: "jQuery Logo"
			//	});
			
			if (data.avatar==null)
				$("img#friendProfilePic").attr("src", "images/profile_pic_sample.jpg");
			else
				$("img#friendProfilePic").attr("src", "data:image/jpg;base64," + data.avatar);
		}
		
		function failure(data) {
			console.log("Error retrieving avatar: " + data);
		}
		
		//CSS Record OBJECT
		var name 	 = data.name, 
			location = data.homeLocation,
			email	 = data.emailID;
		if (name==null) name="";
		if (location==null) name="";
		if (email==null) email="";
		var markup = "<h1>" + name + " </h1>" + 
					 "<p>" + location + " </p>" +
					 "<p>" + data.cssIdentity + "</p><br />"; 
		//INJECT
		$('div#friend_profile_info').html( markup );
		//ADDITIONAL INFO
		var addInfo = "<br/>" + "<p>Email: " + email + " </p>";
		$('li#friend_additional_info').html( addInfo );
				
		try {//REFRESH FORMATTING
			//ERRORS THE FIRST TIME AS YOU CANNOT refresh() A LISTVIEW IF NOT INITIALISED
			$('ul#friendInfoUL').listview('refresh');
		}
		catch(err) {}
		//RETRIEVE PHOTO
		window.plugins.SocietiesLocalCSSManager.getVCardUser(data.cssIdentity, success, failure);
	},
	
	refreshSuggestedFriendsList: function() {
		console.log("Refreshing Suggested friends");

		function success(data) {
			//UPDATE COUNT
			$("span#suggestedFriendsCount").html(data.length);
			$("span#suggestedFriendsCount").css({ visibility: "visible"});			
			//DISPLAY RECORDS
			CSSFriendsServices.displayFriendEntryRecords(data);
		}
		
		function failure(data) {
			alert("Error occured retrieving suggested friends: " + data);
		}
		
		window.plugins.SocietiesLocalCSSManager.getSuggestedFriends(success, failure);
	},
	
	displayFriendEntryRecords: function(data) {
		function showAvatar(VCard) {
			if (VCard.from != null) {
				var n=VCard.from.indexOf("@");
				var identityStr = VCard.from.substring(0, n);
				var imageStr = VCard.avatar;
				if (imageStr != null)
					$('img#' + identityStr).attr("src", "data:image/jpg;base64," + VCard.avatar);
			}
		}
		
		function failure(data) {
			console.log("Error retrieving avatar: " + data);
		}
		
		//EMPTY TABLE - NEED TO LEAVE THE HEADER
		while( $('ul#SuggestedFriendsListUL').children().length >1 )
			$('ul#SuggestedFriendsListUL li:last').remove();

		var rankings = new Array();
		//DISPLAY SUGGESTIONS
		for (i = 0; i < data.length; i++) {
			var rank=0;
			//GENERATE SNS IMAGES
			var images="";
			if (CSSFriendsServices.isFacebookFlagged(data[i].value)) {
				images+='<img src="images/icons/facebook-col.png" width="20" height="20" /> &nbsp;';
				rank++
			}
			if (CSSFriendsServices.isTwitterFlagged(data[i].value)) {
				images+='<img src="images/icons/twitter-col.png" width="20" height="20" /> &nbsp;';
				rank++
			}
			if (CSSFriendsServices.isLinkedinFlagged(data[i].value)) {
				images+='<img src="images/icons/linkedin-col.png" width="20" height="20" /> &nbsp;';
				rank++
			}
			if (CSSFriendsServices.isFoursquareFlagged(data[i].value)) {
				images+='<img src="images/icons/foursquare-col.png" width="20" height="20" /> &nbsp;';
				rank++
			}
			if (CSSFriendsServices.isGooglePlusFlagged(data[i].value)) {
				images+='<img src="images/icons/googleplus-col.png" width="20" height="20" /> &nbsp;';
				rank++
			}
			
			//ADD TO LINE ITEM
			var n=data[i].key.id.indexOf(".");
			var identityStr = data[i].key.id.substring(0, n);
			//ADD TO LINE ITEM data-filtertext="NASDAQ:AAPL Apple Inc."
			var tableEntry = '<li id="li' + i + '"><a href="#" onclick="CSSFriendsServices.sendFriendRequest(\'' + data[i].key.name + '\', \'' + data[i].key.id + '\', ' + i + ')">' +
				'<img src="images/profile_pic.png" id="' + identityStr + '" style="max-width:100px;" />' +
				'<p class="ui-li-aside">' + images + '</p>' + 
				'<h2>' + data[i].key.name + '</h2>' + 
				'<p>' + data[i].key.id + '</p>' +
				'</a></li>';
			rankings[i] = {"rank": rank, "htmlStr": tableEntry, "id": data[i].key.id};
		}
		//SORT BASED ON RANK AND PRINT TABLE
		rankings.sort(function(a,b) { return parseInt(b.rank) - parseInt(a.rank) } );
		for (i=0; i <rankings.length; i++) {
			$('ul#SuggestedFriendsListUL').append(rankings[i].htmlStr);
			window.plugins.SocietiesLocalCSSManager.getVCardUser(rankings[i].id, showAvatar, failure);
		}
		
		$('ul#SuggestedFriendsListUL').listview('refresh');
	},
	
	displayCSSAdvertRecords: function(data) {
		function showAvatar(VCard) {
			if (VCard.from != null) {
				var n=VCard.from.indexOf("@");
				var identityStr = VCard.from.substring(0, n);
				var imageStr = VCard.avatar;
				if (imageStr != null)
					$('img#' + identityStr).attr("src", "data:image/jpg;base64," + VCard.avatar);
			}
		}
		
		function failure(data) {
			console.log("Error retrieving avatar: " + data);
		}
		
		//EMPTY TABLE - NEED TO LEAVE THE HEADER
		while( $('ul#SuggestedFriendsListUL').children().length >1 )
			$('ul#SuggestedFriendsListUL li:last').remove();

		//DISPLAY SUGGESTIONS
		for (i  = 0; i < data.length; i++) {
			if (myIdentity != data[i].id) {
				var n=data[i].id.indexOf(".");
				var identityStr = data[i].id.substring(0, n);
				var tableEntry = '<li id="li' + i + '"><a href="#" onclick="CSSFriendsServices.sendFriendRequest(\'' + data[i].name + '\', \'' + data[i].id + '\', ' + i + ')">' +
					'<img src="images/profile_pic.png" id="' + identityStr + '" style="max-width:100px;" />' +
					'<h2>' + data[i].name + '</h2>' + 
					'<p>' + data[i].id + '</p>' +
					'</a></li>';
				$('ul#SuggestedFriendsListUL').append(tableEntry);
				window.plugins.SocietiesLocalCSSManager.getVCardUser(data[i].id, showAvatar, failure);
			}
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
		if (myIdentity != css_id) {
			if (window.confirm("Send friend request to " + name + "?")) {
			    	 $('#li' + id).append("Sending Request...");
			    	 window.plugins.SocietiesLocalCSSManager.sendFriendRequest(css_id, success, failure);
			}
		}
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
