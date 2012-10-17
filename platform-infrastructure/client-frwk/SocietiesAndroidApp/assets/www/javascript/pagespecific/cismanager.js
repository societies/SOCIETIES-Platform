/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Societies Android app Create CIS function(s) namespace
 * 
 * @namespace SocietiesCISManagerService
 */

var	SocietiesCISManagerService = {
			
	/**
	 * @methodOf SocietiesCISManagerService#
	 * @description create a CIS
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns CIS record
	 */
	CreateCIS: function() {
		console.log("create CIS");

		function success(data) {
			SocietiesCISManagerService.populateCISProfilepage(data);
		}
		
		function failure(data) {
			alert("createCIS - failure: " + data);
		}
		
		var cisName = jQuery("#cisNameOnCisCreate").val(),
            cisType = jQuery("#cisCategoryOnCisCreate").val(),
            cisCriteria = [{
                    "attrib": "age",
                    "operator": "greater than",
                    "value1": "18",
                    "value2": "18",
                    "rank": "1"}],
             cisCriteriaEmpty = [],
             cisDescription = jQuery("#cisDescOnCisCreate").val(),
             privacyPolicy = "<RequestPolicy />";
		window.plugins.SocietiesLocalCISManager.createCIS(success, failure, cisName, cisDescription, cisType, cisCriteriaEmpty, privacyPolicy);
	},
	
	/**
	 * @methodOf SocietiesCISManagerService#
	 * @description update the CIS data on community_profile.html 
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns CIS record
	 */
	populateCISProfilepage: function(data) {
		if ( data ) {
			//VALID COMMUNITY OBJECT
			var markup = "<h1>" + data.communityName + "</h1>" + 
						 "<p>Type: " + data.communityType + "</p>" + 
						 "<p>" + data.description + "</p>" + 
						 "<p>Owner: " + data.ownerJid + "</p>";
			$('input#cis_id').val(data.communityJid);
			
			//PREPARE THE ACTIVITY FEED
			var currentDateTime = new Date(), 
				headerText = currentDateTime.getFullYear() + "-" + (currentDateTime.getMonth()+1) + "-" + currentDateTime.getDate();
			$('input#last_date').val(headerText);
			
			var hours = currentDateTime.getHours(),
		    minutes = currentDateTime.getMinutes();
			if (minutes < 10)
				minutes = "0" + minutes
			var suffix = "AM";
			if (hours >= 12) {
				suffix = "PM";
				hours = hours - 12;
			}
			if (hours == 0)
				hours = 12;
			
			//REMOVE ALL PREVIOUS ENTRIES
			while( $('ul#cis_activity_feed').children().length >0 )
				$('ul#cis_activity_feed li:last').remove();
			
			//ADD INITIAL ENTRY "CREATED COMMUNITY"
			var tableEntry = "<li><p>" + hours + ":" + minutes + " " + suffix + "</p>" +
			 	"<p>I created this community</p></li>";
			$("ul#cis_activity_feed").append(tableEntry).slideDown('slow');
			$('ul#cis_activity_feed').listview('refresh');
			
			//INJECT
			$("#community_profile_info").html( markup );
			
			try {//REFRESH FORMATTING
				//ERRORS THE FIRST TIME AS YOU CANNOT refresh() A LISTVIEW IF NOT INITIALISED
				$('ul#community_details').listview('refresh');
			}
			catch(err) {}
			//POPULATE THE MyServices SELECT BOX
			ServiceManagementServiceHelper.connectToServiceManagement(SocietiesCISListService.createSelectServices);
			
			$.mobile.changePage($("#community-details-page"), {transition: "fade"});
		}
	},
	
	/**
	 * @methodOf SocietiesCISManagerService#
	 * @description list CISs
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns CIS records
	 */
	ListCIS: function() {
		console.log("list CISs");
		
		function success(data) {
			console.log("List CISs where  = TODO");
			SocietiesCISListService.populateCISListpage(data);
			$.mobile.changePage( $("#community-list"), { transition: "fade"} );
		}
		
		function failure(data) {
			alert("ListCIS - failure: " + data);
		}
		SocietiesCISManagerHelper.connectToLocalCISManager(function() {
				window.plugins.SocietiesLocalCISManager.listCIS(success, failure); } );
	}, 
	
	searchCisDirectory: function(searchTerm) {
		console.log("Search CIS Dir for " + searchTerm);
		
		function success(data) {
			SocietiesCisDirService.populateCISListpage(data);
			$.mobile.changePage( $("#community-results"), { transition: "fade"} );
		}
		
		function failure(data) {
			alert("searchCisDirectory - failure: " + data);
		}
		SocietiesCISManagerHelper.connectToLocalCISManager(function() {
				window.plugins.SocietiesLocalCISManager.findForAllCis(searchTerm, success, failure); } );
	},
	
	getAllCisDirAds: function() {
		console.log("getAllCisDirAds");
		
		function success(data) {
			SocietiesCisDirService.populateCISListpage(data);
			$.mobile.changePage( $("#community-results"), { transition: "fade"} );
		}
		
		function failure(data) {
			alert("searchCisDirectory - failure: " + data);
		}
		SocietiesCISManagerHelper.connectToLocalCISManager(function() {
				window.plugins.SocietiesLocalCISManager.findAllCisAdvertisementRecords(success, failure); } );
	},
	
	getJoinResponse: function(cisAdvert) {
		console.log("getAllCisDirAds");
		
		function success(data) {
			SocietiesCisDirService.showJoinResponse(data);
			$.mobile.changePage($("#community-details-page"), {transition: "fade"});
		}
		
		function failure(data) {
			alert("getJoinResponse - failure: " + data);
		}
		SocietiesCISManagerHelper.connectToLocalCISManager(function() {
					window.plugins.SocietiesLocalCISManager.joinCis(cisAdvert, success, failure); } );
	}
}
