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
 * Societies Android app CIS list function(s) namespace
 * 
 * @namespace SocietiesCISListService
 */

var	SocietiesCISListService = {
		
	mCommunitities: {}, //USED TO STORE ALL COMMUNITIES TO SAVE ROUND TRIPS
	mCisServices: {}, 		//USED TO STORE ALL SERVICES TO SAVE ROUND TRIPS
	mMyServices: {}, 		//USED TO STORE ALL SERVICES TO SAVE ROUND TRIPS
	mCis_id: "",
	mLastDate: "",
	
	/**
	 * @methodOf SocietiesCISListService#
	 * @description update the CIS data on communities_list.html 
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns none
	 */
	populateCISListpage: function(data) {
		
		mCommunitities = data;
		//EMPTY TABLE - NEED TO LEAVE THE HEADER
		while( $('ul#CommunitiesListDiv').children().length >1 )
			$('ul#CommunitiesListDiv li:last').remove();
		
		//DISPLAY COMMUNTIES
		for (i  = 0; i < data.length; i++) {
			var tableEntry = '<li><a href="#" onclick="SocietiesCISListService.showCISDetails(' + i + ')"><img src="images/community_profile_icon.png" class="profile_list" alt="logo" >' +
							 '<h2>' + data[i].communityName + '</h2>' + 
							 '<p>' + data[i].communityType + '</p>' + 
							 '</a></li>';
			$('ul#CommunitiesListDiv').append(tableEntry);
		}
		$('#CommunitiesListDiv').listview('refresh');
	},
	
	showCISDetails: function (cisPos) {
		// GET SERVICE FROM ARRAY AT POSITION
		var communityObj = mCommunitities[ cisPos ];
		if ( communityObj ) {
			//VALID SERVICE OBJECT
			mCis_id = communityObj.communityJid;
			var markup = "<h1>" + communityObj.communityName + "</h1>" + 
						 "<p>Type: " + communityObj.communityType + "</p>" + 
						 "<p>" + communityObj.description + "</p>" + 
						 "<p>Owner: " + communityObj.ownerJid + "</p>";
			//INJECT
			$("#community_profile_info").html( markup );
			
			try {//REFRESH FORMATTING
				//ERRORS THE FIRST TIME AS YOU CANNOT refresh() A LISTVIEW IF NOT INITIALISED
				$('ul#community_details').listview('refresh');
			}
			catch(err) {}
			$.mobile.changePage($("#community-details-page"), {transition: "fade"});
			
			SocietiesCISListService.showCISActivities(communityObj.communityJid);
			SocietiesCISListService.showCISMembers(communityObj.communityJid);
			ServiceManagementServiceHelper.connectToServiceManagement(function() {
								SocietiesCISListService.showCISServices(communityObj.communityJid); }
								)
			SocietiesCISListService.createSelectServices();
		}
	},
	
	showCISActivities: function (cisId) {
		function success(data) {
			mLastDate="";
			//EMPTY TABLE - NEED TO LEAVE THE HEADER
			while( $('ul#cis_activity_feed').children().length >0 )
				$('ul#cis_activity_feed li:last').remove();
			
			for (i=data.length-1; i >= 0 ; i--) {
				//HEADER
				var d = new Date();
				d.setTime(data[i].published); 
				var dateStr = d.getFullYear() + "-" + (d.getMonth()+1) + "-" + d.getDate();
				if (mLastDate != dateStr) {
					mLastDate = dateStr;
					$('ul#cis_activity_feed').append("<li data-role=\"list-divider\">" + dateStr + "</li>" );
				}
				//DATA
				var hours = d.getHours(),
				    minutes = d.getMinutes();
				if (minutes < 10)
					minutes = "0" + minutes	
				var suffix = "AM";
				if (hours >= 12) {
					suffix = "PM";
					hours = hours - 12;
				}
				if (hours == 0)
					hours = 12;
				//BODY FORMATTING
				var n=data[i].actor.indexOf(".");
				var actorStr = data[i].actor.substring(0, n);
				var tableEntry = "<li><p>" + hours + ":" + minutes + " " + suffix + "</p>" +
								 "<p>"+ actorStr + " " +
					 	 		 data[i].verb  + " " + 
					 	 		 data[i].object + "</p></li>";
				$('ul#cis_activity_feed').append(tableEntry);
			}
			$('ul#cis_activity_feed').listview('refresh');
			if (data.length <3)
				$('ul#cis_activity_feed').trigger( "expand" );
			//STORE MOST RECENT DATE - HELPS ADDING
			var recent = new Date();
			recent.setTime(data[data.length-1].published);
			mLastDate = recent.getFullYear() + "-" + (recent.getMonth()+1) + "-" + recent.getDate();
		}
		
		function failure(data) {
			var tableEntry = "<li><p>Error occurred retrieving activities: "+ data + "</p></li>";
			$('ul#cis_activity_feed').append(tableEntry);
			$('ul#cis_activity_feed').listview('refresh');
		}
		
		window.plugins.SocietiesLocalCISManager.getActivityFeed(cisId, success, failure);
	},
	/*
	  var lastdata = null;     
	  var liCount = 0;     
	  $(document).ready(function () { 
	      $.get('live.ashx?' + Math.random(), function (data) {
	      		if (lastdata != data) {                
	      			$("#LiveTraffic").prepend(data).slideDown('slow');
	               lastdata = data;        
	               liCount += 1;           
	           }        
	      })
	      if (liCount > 10) {
	      		$('#LiveTraffic li:not(:first)').remove();
	           liCount = 0;
           }         
           setTimeout(arguments.callee, 10000);     
      }); 
	 */
	
	addCISActivity: function() {
		function success(data) {
			//HEADER
			var currentDateTime = new Date(), 
				headerText = currentDateTime.getFullYear() + "-" + (currentDateTime.getMonth()+1) + "-" + currentDateTime.getDate();
			if (headerText != mLastDate) {
				mLastDate = headerText;
				var headerEntry = "<li data-role=\"list-divider\">" + headerText + "</li>";
				$('ul#cis_activity_feed').prepend(headerEntry).slideDown('slow');
			}
			//DATA 
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
			//BODY FORMATTING - INSERT AFTER HEADER
			var tableEntry = "<li><p>" + hours + ":" + minutes + " " + suffix + "</p>" +
							 "<p>I " + activity.verb  + " '" + activity.object + "'</p></li>";
			$("ul#cis_activity_feed li:eq(0)").after(tableEntry).slideDown('slow');
			//$('ul#cis_activity_feed').prepend(tableEntry).slideDown('slow');
			$('ul#cis_activity_feed').listview('refresh');
			$('textarea#activity_message').val('');
		}
		
		function failure(data) {
			alert("Error occuring posting: " + data);
		}
 
		var activity = {
 				"actor": "",
 				"verb": "posted",
                "object": $('textarea#activity_message').val(),
                "target": mCis_id,
                "published": $.now()
				};
		window.plugins.SocietiesLocalCISManager.addActivity(mCis_id, activity, success, failure);
	},
	
	showCISMembers: function (cisId) {
		
		function success(data) {
			//EMPTY TABLE - NEED TO LEAVE THE HEADER
			while( $('ul#cis_members').children().length >0 )
				$('ul#cis_members li:last').remove();
			
			for (i  = 0; i < data.length; i++) {
				var tableEntry = "<li><h2>"+ data[i].jid + "</h2>" + 
									 "<p>" + data[i].role  + "</p></li>";
				$('ul#cis_members').append(tableEntry);
			}
			$('ul#cis_members').listview('refresh');
			
			//AUTO EXPAND IF ROW COUNT IS SMALL 
			if (data.length <3)
				$('ul#cis_members').trigger( "expand" );
		}
		
		function failure(data) {
			var tableEntry = "<li><p>Error occurred retrieving members: "+ data + "</p></li>";
			$('ul#cis_members').append(tableEntry);
			$('ul#cis_members').listview('refresh');
		}
		
		window.plugins.SocietiesLocalCISManager.getMembers(cisId, success, failure);
	},
	
	/**
	 * @methodOf SocietiesCISListService#
	 * @description retrieves list of services from a CIS 
	 * @param {Object} successCallback The callback which will be called when result is successful
	 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
	 * @returns none
	 */
	showCISServices: function(cisId) {

		function success(data) {
			mCisServices = data;
			
			//EMPTY TABLE - NEED TO LEAVE THE HEADER
			while( $('ul#cis_shared_apps').children().length >0 )
				$('ul#cis_shared_apps li:last').remove();

			//DISPLAY SERVICES
			for (i  = 0; i < data.length; i++) {
				//var tableEntry = '<li><a href="#" onclick="Societies3PServices.installService(' + i + ')"><img src="images/printer_icon.png" class="profile_list" alt="logo" >' +
				var tableEntry = '<li><img src="images/printer_icon.png" class="profile_list" alt="logo" >' +
					'<h2>' + data[i].serviceName + '</h2>' + 
					'<p>' + data[i].serviceDescription + '</p>' + 
					'</li>';
				$('ul#cis_shared_apps').append(tableEntry);
			}
			$('ul#cis_shared_apps').listview('refresh');
		}
		
		function failure(data) {
			var tableEntry = "<li><p>Error occurred retrieving services: "+ data + "</p></li>";
			$('ul#cis_shared_apps').append(tableEntry);
			$('ul#cis_shared_apps').listview('refresh');
		}
		
		window.plugins.ServiceManagementService.getServices(cisId, success, failure);
	},
	
	/**
	 * Shares a service from the select list to a community
	 */
	shareService: function() {
		
		function success(data) {
			//ADD SERVICE TO LIST OF SHARED SERVICES
			mCisServices.push(serviceObj);

			//var tableEntry = '<li><a href="#" onclick="Societies3PServices.installService(' + (mCisServices.length - 1) + ')"><img src="images/printer_icon.png" class="profile_list" alt="logo" >' +
			var tableEntry = '<li><img src="images/printer_icon.png" class="profile_list" alt="logo" >' +
				'<h2>' + serviceObj.serviceName + '</h2>' + 
				'<p>' + serviceObj.serviceDescription + '</p>' + 
				'</li>';
			$('ul#cis_shared_apps').append(tableEntry);
			$('ul#cis_shared_apps').listview('refresh');
		}
		
		function failure(data) {
			alert("Error occurred sharing the service: " + data);
		}
		
		var servicePos = $('select#selShareService').attr('value'),
			serviceName = $("select#selShareService option:selected").text();

		if (servicePos != "0000") { //"Select a Service"
			var serviceObj = mMyServices[servicePos];
			if (confirm("Share service: " + serviceName + " to this community?"))
				window.plugins.ServiceManagementService.shareMyService(mCis_id, serviceObj, success, failure);
			else
				$('select#selShareService').attr('selectedIndex', 0);

			$('select#selShareService').selectmenu('refresh');
		}
	},

	/**
	 * Populates the select control with users services	 * 
	 */
	createSelectServices: function() {
		
		function success(data) {
			mMyServices = data;

			//REMOVE ALL SERVICE EXCEPT "SELECT SERVICE"
			var count = $('select#selShareService option').length;
			for (j=1; j<count; j++) {
				$("select#selShareService").children().slice(j).detach(); 
			}
			//POPULATE SERVICES
			for (i = 0; i < data.length; i++) {
				$('select#selShareService')
					.append($("<option></option>")
					.attr("value", i) //data[i].serviceIdentifier.identifier)
					.text(data[i].serviceName));
			}
			$('select#selShareService').attr('selectedIndex', 0); 
			$('select#selShareService').selectmenu();
		}
		
		function failure(data) {
			
		}
		
		window.plugins.ServiceManagementService.getMyServices(success, failure);
	},
	
	/**
	 * Shares a service from the select list to a community
	 */
	installService: function() {
		
		function success(data) {
			
		}
		
		function failure(data) {
			
		}
		
		var service = $('select#selShareService').attr('value');
		if (connectorType != "0") { //"Select a Connector"
			if (confirm("Install service: " + service + "?")) {
				window.plugins.ServiceManagementService.installService(success, failure);
			}
		}
	}
	
}
