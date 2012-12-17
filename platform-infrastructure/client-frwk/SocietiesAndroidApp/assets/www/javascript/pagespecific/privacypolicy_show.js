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
 * Societies Android app manage privacy policy namespace
 * 
 * @namespace SocietiesPrivacyPolicyManagerService
 */

var	SocietiesPrivacyPolicyManagerService = {
		/**
		 * @methodOf SocietiesPrivacyPolicyManagerService#
		 * @description Get privacy policy
		 * @param {Object} successCallback The callback which will be called when result is successful
		 * @param {Object} failureCallback The callback which will be called when result is unsuccessful
		 */
		getPrivacyPolicy: function(ownerId, ownerCisId, bAdmin) {
			console.log("getPrivacyPolicy", ownerId, ownerCisId, bAdmin);

			function success(data) {
				console.log("getPrivacyPolicy - Succes: ", data);
				//EMPTY TABLE - NEED TO LEAVE THE HEADER
				while( $('ul#getPrivacyPolicy').children().length >0 )
					$('ul#getPrivacyPolicy li:last').remove();
				// Display
				if ("requestItems" in data) {
					var i;
					for (i=0; i<data.requestItems.length; i++) {
						$('<li>').addClass("requestItem")
						.attr('id', 'li'+i)
						.html('<h2>'+data.requestItems[i].resource.dataIdUri+'</h2>')
						.appendTo('#getPrivacyPolicy');
					}
				}
				else {
					$('<li>').addClass("empty")
					.html('<p>Empty</p>')
					.appendTo('#getPrivacyPolicy');
				}
				$('ul#getPrivacyPolicy').listview('refresh');
				$('ul#getPrivacyPolicy').trigger( "collapse" );
			}

			function failure(data) {
				console.log("getPrivacyPolicy - failure: " + data);
				$("#getPrivacyPolicy").html("Faillure: "+data);
			}

			// Call
			window.plugins.PrivacyPolicyManager.getPrivacyPolicy("test", success, failure);
		}
}
