/**
 * Societies Android app CIS Manager function(s) namespace
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
			
			//SocietiesGUI.populateCISRecordpage(data);
			console.log("create CIS where data has name = " + data.cisName);

			
			//$.mobile.changePage( ($("#menu")), { transition: "slideup"} );
		}
		
		function failure(data) {
			alert("createCIS - failure: " + data);
		}
	    SocietiesLocalCISManager.createCIS(success, failure);

	}
			
}

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */

jQuery(function() {
	console.log("Create CIS jQuery calls");

	$('#createCISbutton').click(function() {
		SocietiesCISManagerService.CreateCIS();
	});
});