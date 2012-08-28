
/**
 * Societies Android app SocietiesLogout function(s) namespace
 * 
 * @namespace SocietiesLogout
 */


var SocietiesLogout = {
	/**
	 * @methodOf SocietiesLogout#
	 * @description Carry out the logout process from the Cloud CSS node
	 * @returns null
	 */
	successfulCSSCloudLogout: function() {
		console.log("Logout from CSS");

		function success(data) {
			jQuery("#username").val("");
			jQuery("#userpass").val("");
			SocietiesLogout.successfulXMPPServerLogout();

		}

		function failure(data) {
			alert("successfulCSSCloudLogout : " + "failure: " + data);
		}
		
	    window.plugins.SocietiesLocalCSSManager.logoutCSS(success, failure);

	},
	/**
	 * @methodOf SocietiesLogout#
	 * @description Carry out the logout process from the XMPP server domain
	 * @returns null
	 */
	successfulXMPPServerLogout: function() {
		console.log("Logout from XMPP server");

		function success(data) {
			SocietiesLocalCSSManagerHelper.disconnectFromLocalCSSManager();

		}

		function failure(data) {
			alert("successfulXMPPServerLogout : " + "failure: " + data);
		}
		
	    window.plugins.SocietiesLocalCSSManager.logoutXMPPServer(success, failure);

	}


}

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */

jQuery(function() {
	console.log("Logout jQuery calls");

	$("#logoutIcon").click(function() {
		SocietiesLocalCSSManagerHelper.connectToLocalCSSManager(SocietiesLogout.successfulCSSCloudLogout);
	});
});