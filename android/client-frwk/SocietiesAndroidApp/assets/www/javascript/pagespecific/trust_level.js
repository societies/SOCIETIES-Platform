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
 * Trust level functionality.
 * 
 * @namespace jTrustLevel
 */
var jTrustLevel = {
		
		RATING_STARS: 5,

		/**
		 * @methodOf jTrustLevel#
	     * @description Shows the trust values assigned by my CSS to the
	     *              identified entity of the specified type, i.e. CSS, CIS
	     *              or service.
	     * @param divSelector
	     *            the CSS selector matching the trust level HTML DIV
		 * @param entityType 
		 *            one of "CSS", "CIS" or "SVC".
		 * @param entityId
		 *            the identity of the entity whose trust values to show.
		 * @returns
		 */
		showDetails: function(divSelector, entityType, entityId) {

			console.log("showDetails");

			// Check global variable myIdentity
			if (typeof myIdentity === "undefined" || myIdentity === null) {
				console.log("Could not show trust values: Undefined or null myIdentity");
				return;
			}
			
			if (typeof divSelector === "undefined" || divSelector === null) {
				console.log("Could not show trust values: Undefined or null DIV selector");
				return;
			}
			
			if (entityType !== "CSS" && entityType !== "CIS" && entityType !== "SVC") {
				console.log("Could not show trust values: Invalid entityType " + entityType);
				return;
			}
			
			if (typeof entityId === "undefined" || entityId === null) {
				console.log("Could not add trust rating: Undefined or null entityId");
				return;
			}

			function success(data) {

				console.log("showDetails: success");

				var trust = new Object();
				trust.directValue = "N/A";
				trust.directRating = null;
				trust.indirectValue = "N/A";
				trust.userPerceivedValue = "N/A";

				// Foreach trust relationship
				for (i = 0; i < data.length; i++) {
					// Display trust value as a percentage
					var trustValue = jTrustLevel.formatTrustValueAsPercentage(data[i].trustValue);
					if (data[i].trustValueType == "DIRECT") {
						trust.directValue = trustValue;
						var trustEvidenceArray = data[i].trustEvidence;
						for (j = 0; j < trustEvidenceArray.length; j++) {
							if (trustEvidenceArray[j].type === "RATED" 
								&& typeof trustEvidenceArray[j].info == "number") {
								// Adjust value based on number of rating stars
								trust.directRating = (jTrustLevel.RATING_STARS * trustEvidenceArray[j].info).toFixed();
							}
						}
					} else if (data[i].trustValueType == "INDIRECT") {
						trust.indirectValue = trustValue; 
					} else if (data[i].trustValueType == "USER_PERCEIVED") {
						trust.userPerceivedValue = trustValue; 
					} 
				}

				// Inject trust values
				$(divSelector + ' .user-perceived-trust-value').html(trust.userPerceivedValue);
				$(divSelector + ' .direct-trust-value').html(trust.directValue);
				$(divSelector + ' .indirect-trust-value').html(trust.indirectValue);

				// Inject trust rating
				if (trust.directRating !== null) {
					$(divSelector + ' .trust-rating').raty('score', trust.directRating);
				}
			}

			function failure(data) {

				console.log("showDetails: failure " + data);
			}

			jTrustPluginHelper.connect(function() {
				window.plugins.jTrustPlugin.retrieveExtTrustRelationships(success, failure,
						{
							"trustorId":{"entityType":"CSS","entityId":myIdentity},
							"trusteeId":{"entityType":entityType,"entityId":entityId}
						}); 
			});
		},
		
		/**
		 * @methodOf jTrustLevel#
	     * @description Initialises the trust star rating plugin under the 
	     *              specified DIV.
		 * @param divSelector
	     *            the CSS selector matching the trust level HTML DIV.
	     * @param entityType 
		 *            one of "CSS", "CIS" or "SVC".
		 * @param entityId
		 *            the identity of the entity whose trust values to show.
		 * @returns
		 */
		initRating: function(divSelector, entityType, entityId) {

			console.log("initRating");
			
			if (typeof divSelector === "undefined" || divSelector === null) {
				console.log("Could not show trust values: Undefined or null DIV selector");
				return;
			}
			
			if (entityType !== "CSS" && entityType !== "CIS" && entityType !== "SVC") {
				console.log("Could not show trust values: Invalid entityType " + entityType);
				return;
			}
			
			if (typeof entityId === "undefined" || entityId === null) {
				console.log("Could not add trust rating: Undefined or null entityId");
				return;
			}
			
			$(divSelector + ' .trust-rating').raty({
				path: 'images',
				number: jTrustLevel.RATING_STARS,
				size: 24,
				hints: ['Not trusted at all', 'Poorly trusted', 'Marginally trusted', 'Well trusted', 'Fully trusted'],
				target: divSelector + ' .trust-rating-hint',
				targetKeep: true,
				targetText: 'Not rated yet',
				targetFormat: 'Your rating:<br/>{score}',
				click: function(score) {
					var rating = score / jTrustLevel.RATING_STARS;
					jTrustLevel.addRating(entityType, entityId, rating);
				}
			});
		},
		
		/**
		 * @methodOf jTrustLevel#
	     * @description Assigns the specified rating to the identified entity
	     *              of the specified type, i.e. CSS, CIS or service.
		 * @param entityType 
		 *            one of "CSS", "CIS" or "SVC".
		 * @param entityId
		 *            the identity of the entity whose trust values to show.
		 * @param rating
		 *            the rating to assign to the specified entity, i.e. a 
		 *            numeric value between zero and one [0,1].
		 * @returns
		 */
		addRating: function(entityType, entityId, rating) {

			console.log("addRating");

			if (entityType !== "CSS" && entityType !== "CIS" && entityType !== "SVC") {
				console.log("Could not add trust rating: Invalid entityType " + entityType);
				return;
			}
			
			if (typeof myIdentity === "undefined" || myIdentity === null) {
				console.log("Could not add trust rating: Undefined or null myIdentity");
				return;
			}
			
			if (typeof entityId === "undefined" || entityId === null) {
				console.log("Could not add trust rating: Undefined or null entityId");
				return;
			}
			
			if (typeof rating === "undefined" || rating === null) {
				console.log("Could not add trust rating: Undefined or null rating");
				return;
			}
			
			if (typeof rating !== "number" || (typeof rating === 'number' && (rating < 0 || rating > 1))) {
				console.log("Could not add trust rating: Invalid rating value: " + rating);
				return;
			}
			
			function success(data) {

				console.log("addRating: success " + data);
			}

			function failure(data) {

				console.log("addRating: failure " + data);
			}

			jTrustPluginHelper.connect(function() {
				window.plugins.jTrustPlugin.addDirectTrustEvidence(success, failure, 
						{"entityType":"CSS","entityId":myIdentity}, 
						{"entityType":entityType,"entityId":entityId},
						"rated",
						new Date().getTime(),
						rating); 
			});
		},

		/**
		 * 
		 */
		formatTrustValueAsPercentage: function(trustValue) {

			var result = "N/A";

			if (typeof trustValue == 'number' && trustValue >= 0 && trustValue <= 1) {
				result = (trustValue * 100).toFixed(0) + "%";
			}

			return result;
		},
		
		encodeServiceResourceIdentifier: function(sriObject) {

			if (sriObject === null 
					|| sriObject.identifier === undefined 
					|| sriObject.identifier === null 
					|| sriObject.serviceInstanceIdentifier === undefined 
					|| sriObject.serviceInstanceIdentifier === null) {
				return null;
			}
			
			return sriObject.serviceInstanceIdentifier + ' ' + sriObject.identifier; 
		}
};