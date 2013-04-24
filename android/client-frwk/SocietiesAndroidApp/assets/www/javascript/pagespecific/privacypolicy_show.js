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
	var actionList = {"READ": "access",
	                  "WRITE" : "update",
	                  "CREATE" : "create",
	                  "DELETE" : "delete"};
	var conditionList = {"SHARE_WITH_3RD_PARTIES": "Shared with the world",
	                     "SHARE_WITH_CIS_MEMBERS_ONLY": "Shared with community members",
	                     "SHARE_WITH_CIS_OWNER_ONLY" : "Not shared",
	                     "MAY_BE_INFERRED" : "Warning, this data may be inferred",
	                     "DATA_RETENTION_IN_SECONDS": "Data retention in seconds",
	                     "DATA_RETENTION_IN_MINUTES": "Data retention in minutes",
	                     "DATA_RETENTION_IN_HOURS": "Data retention in hours",
	                     "RIGHT_TO_OPTOUT": "Right to optout",
	                     "STORE_IN_SECURE_STORAGE": "Stored in a secure storage",
	                     "RIGHT_TO_ACCESS_HELD_DATA": "Right to access held data",
	                     "RIGHT_TO_CORRECT_INCORRECT_DATA": "Right to correct invalid data"};
	var resourceTypeList = {"CIS:///cis-member-list": "Member list",
	                        "CONTEXT:///favoriteQuotes": "Favorite quotes",
	                        "CONTEXT:///email": "Email",
	                        "CONTEXT:///interests": "Interests",
	                        "CONTEXT:///occupation": "Occupation",
	                        "CONTEXT:///workPosition": "Work position",
	                        "CONTEXT:///languages": "Languages",
	                        "CONTEXT:///locationCoordinates": "Location coordinates",
	                        "CONTEXT:///locationSymbolic": "Symbolic location",
	                        "CONTEXT:///movies": "Movies",
	                        "CONTEXT:///music": "Music",
	                        "cis:///cis-member-list": "Member list",
	                        "context:///favoriteQuotes": "Favorite quotes",
	                        "context:///email": "Email",
	                        "context:///interests": "Interests",
	                        "context:///occupation": "Occupation",
	                        "context:///workPosition": "Work position",
	                        "context:///languages": "Languages",
	                        "context:///locationCoordinates": "Location coordinates",
	                        "context:///locationSymbolic": "Symbolic location",
	                        "context:///movies": "Movies",
	                        "context:///music": "Music"};
	var resourceSchemeList = ["context", "device", "cis", "activity", "css"];

	/* ************************
	 * 		Private Functions
	 **************************/
	 
	function sortConditions(a, b) {
		return a.conditionConstant > b.conditionConstant;
	}

	/* ************************
	 * 		Public Elements
	 **************************/
	return{
		conditionList: conditionList,
		sortConditions: sortConditions,
	
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
		mapToResourceType : function(resourceType, defaultValue){
			if (undefined != resourceTypeList[resourceType]) {
				return resourceTypeList[resourceType];
			}
			return defaultValue;
		},
	};
}());

/**
 * PrivacyPolicy Utility Class
 * @namespace PrivacyPolicyUtils
 */
var PrivacyPolicyUtils=(function(){
	/* ************************
	 * 		Parameters
	 **************************/
	var TRUE = [1, 'Yes'];
	var FALSE = [0, 'No'];
	var globalBehaviourList = {'public':'Public',
	                           'private':'Private',
	                           'members-only':'Members only'};
	var whoCanList = {'public':'Anyone',
	                           'private':'Nobody',
	                           'members-only':'Any member'};

	/* ************************
	 * 		Private Functions
	 **************************/
	function in_array(needle, haystack) {
		var length = haystack.length;
		for(var i = 0; i < length; i++) {
			if(haystack[i] == needle)
				return true;
		}
		return false;
	}

	function getGlobalBehaviour(requestItem){
		if ("conditions" in requestItem && requestItem.conditions.length > 0) {
			for (var j=0; j<requestItem.conditions.length; j++){
				var condition = requestItem.conditions[j];
				if ("SHARE_WITH_3RD_PARTIES" == condition.conditionConstant
						&& PrivacyPolicyUtils.in_array(condition.value, TRUE)) {
					return "public";
				}
				if ("SHARE_WITH_CIS_MEMBERS_ONLY" == condition.conditionConstant
						&& PrivacyPolicyUtils.in_array(condition.value,TRUE)) {
					return "members-only";
				}
			}
		}
		return "private";
	}

	/* ************************
	 * 		Public Elements
	 **************************/
	return{
		TRUE: TRUE,
		FALSE: FALSE,
		
		in_array: in_array,
		getGlobalBehaviour: getGlobalBehaviour,
		mapToGlobalBehaviour : function(key){
			if (undefined != globalBehaviourList[key]) {
				return globalBehaviourList[key];
			}
			return key;
		},
		mapToWhoCanAccess : function(key){
			if (undefined != whoCanList[key]) {
				return whoCanList[key];
			}
			return key;
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
	function showPrivacyPolicy(data){
		console.log("getPrivacyPolicy - Succes");
		console.log(handler);
		console.log(data);
		console.log(JSON.parse(data));
		var privacyPolicy = JSON.parse(data);
		//EMPTY TABLE - NEED TO LEAVE THE HEADER
		while( $(handler).children().length >0 )
			$(handler+' li:last').remove();

		// Display request items
		if ("requestItems" in privacyPolicy) {
			console.log("Display privacy policy");
			for(var i=0; i<privacyPolicy.requestItems.length; i++) {
				var requestItem = privacyPolicy.requestItems[i];
				var dataTypeUri = '';
				var defaultDataType = '';
				if ("dataIdUri" in privacyPolicy) {
					dataTypeUri = requestItem.resource.dataIdUri;
					defaultDataType = requestItem.resource.dataIdUri;
				}
				else {
					dataTypeUri = requestItem.resource.scheme+":///"+requestItem.resource.dataType;
					defaultDataType = requestItem.resource.dataType;
				}
				console.log("Display: "+dataTypeUri);
				var li = $('<li>').addClass("requestItem")
				.attr('id', 'li'+i);
				var h2 = $('<h2>').html(SocietiesDataUtil.mapToResourceType(dataTypeUri, defaultDataType)+("optional" in requestItem && requestItem.optional ? " <small>(optional)</small>" : ""))
					.addClass(("optional" in requestItem && requestItem.optional ? "optional" : ""));
				var globalBehaviour = PrivacyPolicyUtils.getGlobalBehaviour(requestItem);
				$('<span>').html(PrivacyPolicyUtils.mapToGlobalBehaviour(globalBehaviour))
					.addClass('privacy_global-behaviour')
					.addClass('privacy_'+globalBehaviour)
					.prependTo(h2);
				h2.appendTo(li);
				// Actions
				var whoCanAccess = PrivacyPolicyUtils.mapToWhoCanAccess(globalBehaviour);
				if ("Nobody" != whoCanAccess) {
					var actions = whoCanAccess+' can ';
					requestItem.actions.sort();
					for (var j=0; j<requestItem.actions.length; j++){
						var action = requestItem.actions[j];
						actions += '<span class="'+action.actionConstant+(("optional" in action) && action.optional ? " optional" : "")+'">'+SocietiesDataUtil.mapToAction(action.actionConstant)+'</span>';
						if (j != (requestItem.actions.length-1)) {
							if (j == (requestItem.actions.length-2)) {
								actions += ' and ';
							}
							else {
								actions += ', ';
							}
						}
					}
					actions += ' it';
					$('<p>').addClass('actions')
					.html(actions)
					.appendTo(li);
				}
				// Conditions
				if ("conditions" in requestItem && requestItem.conditions.length > 0) {
					var conditions = '';
					requestItem.conditions.sort(SocietiesDataUtil.sortConditions);
					for (var j=0; j<requestItem.conditions.length; j++){
						var condition = requestItem.conditions[j];
						if (!PrivacyPolicyUtils.in_array(condition.value, PrivacyPolicyUtils.FALSE)
								&& "SHARE_WITH_3RD_PARTIES" != condition.conditionConstant
								&& "SHARE_WITH_CIS_MEMBERS_ONLY" != condition.conditionConstant
								&& "SHARE_WITH_CIS_OWNER_ONLY" != condition.conditionConstant) {
							conditions += '<span class="'+condition.conditionConstant+(("optional" in condition) && condition.optional ? " optional" : "")+'">'+SocietiesDataUtil.mapToCondition(condition.conditionConstant)+(!PrivacyPolicyUtils.in_array(condition.value, PrivacyPolicyUtils.TRUE) ? ': '+condition.value : '')+'</span>';
							if (j != (requestItem.conditions.length-1)) {
								conditions += '<br />';
							}
						}
					}
					$('<p>').addClass('conditions')
					.html(conditions)
					.appendTo(li);
				}
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
			window.plugins.PrivacyPolicyManager.getPrivacyPolicy({"requestorId":ownerId, "cisRequestorId": ownerCisId}, showPrivacyPolicy, failure);
		},
	};
}());
