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
 * Societies Android Data Utility Class
 * @namespace SocietiesDataUtil
 */
var SocietiesDataUtil=(function(){
	/* ************************
	 * 		Parameters
	 **************************/
	var actionList = {"READ": "Read",
	                  "WRITE" : "Write",
	                  "CREATE" : "Create",
	                  "DELETE" : "Delete"};
	var conditionList = {"SHARE_WITH_3RD_PARTIES": "Share with the world",
	                     "SHARE_WITH_CIS_MEMBERS_ONLY": "Share with this community",
	                     "SHARE_WITH_CIS_OWNER_ONLY" : "Not shared",
	                     "MAY_BE_INFERRED" : "Warning, this information may be inferred by people",
	                     "DATA_RETENTION_IN_SECONDS": "data retention in seconds",
	                     "DATA_RETENTION_IN_MINUTES": "data retention in minutes",
	                     "DATA_RETENTION_IN_HOURS": "data retention in hours",
	                     "RIGHT_TO_OPTOUT": "right to optout",
	                     "STORE_IN_SECURE_STORAGE": "store in a secure storage",
	                     "RIGHT_TO_ACCESS_HELD_DATA": "right to access held data",
	                     "RIGHT_TO_CORRECT_INCORRECT_DATA": "right to correct invalid data"};
	var resourceTypeList = {"cis:///cis-member-list": "This CIS' member list",
	                        "context:///favoriteQuotes": "Favorite quotes",
	                        "context:///interests": "Interests",
	                        "context:///languages": "Languages",
	                        "context:///locationCoordinates": "Location coordinates",
	                        "context:///locationSymbolic": "Location",
	                        "context:///movies": "Movies",
	                        "context:///music": "Music"};
	var resourceSchemeList = ["context", "device", "cis", "activity", "css"];

	/* ************************
	 * 		Private Functions
	 **************************/

	/* ************************
	 * 		Public Elements
	 **************************/
	return{
		mapToAction : function(actionId){
			if (undefined != actionList[actionId]) {
				return actionList[actionId];
			}
			return actionId;
		},
		mapToCondition : function(conditionId){
			if (undefined != conditionList[conditionId]) {
				return conditionList[conditionId];
			}
			return conditionId;
		},
		mapToResourceType : function(resourceType){
			if (undefined != resourceTypeList[resourceType]) {
				return resourceTypeList[resourceType];
			}
			return resourceType;
		},
	};
}());

/**
 * Societies Android app manage privacy policy namespace
 * @namespace SocietiesPrivacyPolicyManagerService
 */
var	SocietiesPrivacyPolicyManagerService=(function(){
	/* ************************
	 * 		Parameters
	 **************************/
	var handler = '#getPrivacyPolicy';

	/* ************************
	 * 		Private Functions
	 **************************/
	function showPrivacyPolicy(privacyPolicy){
		console.log("getPrivacyPolicy - Succes: ", handler, privacyPolicy);
		//EMPTY TABLE - NEED TO LEAVE THE HEADER
		while( $(handler).children().length >0 )
			$(handler+' li:last').remove();

		// Display request items
		if ("requestItems" in privacyPolicy) {
			for(var i=0; i<privacyPolicy.requestItems.length; i++) {
				var requestItem = privacyPolicy.requestItems[i];
				var li = $('<li>').addClass("requestItem")
				.attr('id', 'li'+i);
				$('<h2>').html(SocietiesDataUtil.mapToResourceType(requestItem.resource.dataIdUri))
				.addClass(("optional" in requestItem && requestItem.optional ? " optional" : ""))
				.appendTo(li);
				// Actions
				var actions = '';
				for (var j=0; j<requestItem.actions.length; j++){
					var action = requestItem.actions[j];
					actions += '<span class="'+action.actionConstant+(("optional" in action) && action.optional ? " optional" : "")+'">'+SocietiesDataUtil.mapToAction(action.actionConstant)+'</span>';
					if (j != (requestItem.actions.length-1)) {
						actions += ', ';
					}
				}
				$('<p>').addClass('actions')
				.html(actions)
				.appendTo(li);
				// Conditions
				var conditions = '';
				for (var j=0; j<requestItem.conditions.length; j++){
					var condition = requestItem.conditions[j];
					conditions += '<span class="'+condition.conditionConstant+(("optional" in condition) && condition.optional ? " optional" : "")+'">'+SocietiesDataUtil.mapToCondition(condition.conditionConstant)+': '+condition.value+'</span>';
					if (j != (requestItem.conditions.length-1)) {
						conditions += ', ';
					}
				}
				$('<p>').addClass('conditions')
				.html(conditions)
				.appendTo(li);
				li.appendTo(handler);
			}
		}
		else {
			$('<li>').addClass("empty")
			.html('<p>Empty</p>')
			.appendTo(handler);
		}
		$(handler).listview('refresh');
		$(handler).trigger( "collapse" );
	}

	/* ************************
	 * 		Public Elements
	 **************************/
	return{
		/**
		 * @methodOf SocietiesPrivacyPolicyManagerService#
		 * @description Get privacy policy
		 * @param {Object} successCallback The callback which will be called when result is successful
		 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
		 */
		getPrivacyPolicy: function(handler, ownerId, ownerCisId, bAdmin) {
			console.log("getPrivacyPolicy", handler, ownerId, ownerCisId, bAdmin);

			this.handler = handler;

			function failure(data) {
				console.log("getPrivacyPolicy - failure: " + data);
				$(this.handler).html("Faillure: "+data);
			}

			// Call
			window.plugins.PrivacyPolicyManager.getPrivacyPolicy("test", showPrivacyPolicy, failure);
		},
	};
}());
