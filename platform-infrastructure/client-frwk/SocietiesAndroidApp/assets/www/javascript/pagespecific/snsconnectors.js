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
 * Societies Android app Societies3PServices function(s) namespace
 * 
 * @namespace SocialNetworksConnectors
 */
var SocialNetworksConnectors = {
	
	/**
	 * @methodOf Societies3PServices#
	 * @description Refresh the 3P Service page with currently active services
	 * @returns null
	 */
	refreshConnectors: function() {
		console.log("Refreshing SN Connectors");

		function success(data) {
				
			//EMPTY TABLE - NEED TO LEAVE THE HEADER
			while( $('ul#listSNSconnectors').children().length >1 )
				$('ul#listSNSconnectors li:last').remove();

			//DISPLAY SERVICES
			for (i  = 0; i < data.length; i++) {
				var imageSource = SocialNetworksConnectors.getSNimage(data[i].name);
				var tableEntry = '<li><a href="#" onclick="SocialNetworksConnectors.removeConnector(\'' + data[i].name + '\', \'' + data[i].id + '\')"><img src="' + imageSource + '" class="profile_list" alt="logo" >' +
					'<h2>' + data[i].name + '</h2>' + 
					'<p>' + data[i].identity + '</p>' + 
					'</a><a href="#" onclick="SocialNetworksConnectors.removeConnector(\'' + data[i].name + '\', \'' + data[i].id + '\')">Remove</a>' +
					'</li>';
				jQuery('ul#listSNSconnectors').append(tableEntry);
			}
			$('ul#listSNSconnectors').listview('refresh');
		}
		
		function failure(data) {
			alert("listSNSconnectors - failure: " + data);
		}
		
		window.plugins.SocialConnectorsService.getSocialConnectors(success, failure);
	},
		
	addConnector: function (selectObj) {
		function success(data) {
			SocialNetworksConnectors.refreshConnectors();
		}
		
		function failure(data) {
			window.alert("Failed to add connector!");
		}
		
		var connectorType = $('select#connectorType').attr('value');
		if (connectorType != "0") { "Select a Connector"
			SocialConnectorsServiceHelper.connectToSNConnectorService(function() {
										window.plugins.SocialConnectorsService.getToken(connectorType, success, failure) }
										);
		}
	},
	
	removeConnector: function (connectorName, connectorId) {
		function success(data) {
			SocialNetworksConnectors.refreshConnectors();
		}
		
		function failure(data) {
			window.alert("Failed to add connector!");
		}
		
		if(window.confirm("Remove " + connectorName + " connector?")) {
			SocialConnectorsServiceHelper.connectToSNConnectorService(function() {
				window.plugins.SocialConnectorsService.removeSocialConnector(connectorId, success, failure) }
				);
		}
	},
	
	getSNimage: function (cnType){
		if (cnType == "facebook")
			return "../images/icons/facebook-col.png";
		else if (cnType == "twitter")
			return "../images/icons/twitter-col.png";
		else if (cnType == "foursquare")
			return "../images/icons/foursquare-col.png";
		else if (cnType == "linkedin")
			return "../images/icons/linkedin-col.png";
		else if (cnType == "googleplus")
			return "../images/icons/googleplus-col.png";
		else 
			return "../images/icons/worldweb.png";
	}
};

/*
$(document).bind('pageinit', function(){
	console.log("jQuery pageinit action(s) for SNS Connectors");
	
	$('select#connectorType').change(function() {
		window.alert('Value change to ' + $(this).attr('value'));
		//addConnector($(this).attr('value'));
	});
});
*/