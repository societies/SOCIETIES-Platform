
/**
 * Societies Android app SocietiesAppPrefs function(s) namespace
 * 
 * @namespace SocietiesAppPrefs
 */


var SocietiesAppPrefs = {
	/**
	 * @methodOf SocietiesAppPrefs#
	 * @description Get an app preference value
	 * @returns null
	 */

	getAppPref: function() {
		console.log("Get app preference");
		
		jQuery("#prefValue").val("");
		prefName = jQuery("#prefName").val();
		type = jQuery("#prefType").val();

		function success(data) {
			console.log("getAppPref - successful: " + data.value);
			jQuery("#prefValue").val(data.value);
			
		};
		
		function failure(data) {
			console.log("getAppPref - failure: " + data.value);
		};

		console.log("Preference type: " + type);
		
		switch(type)
		{
		case "string":
			window.plugins.SocietiesAppPreferences.getStringPrefValue(success, failure, prefName);
			break;
		case "integer":
			window.plugins.SocietiesAppPreferences.getIntegerPrefValue(success, failure, prefName);
			break;
		case "long":
			window.plugins.SocietiesAppPreferences.getLongPrefValue(success, failure, prefName);
			break;
		case "float":
			window.plugins.SocietiesAppPreferences.getFloatPrefValue(success, failure, prefName);
			break;
		case "boolean":
			window.plugins.SocietiesAppPreferences.getBooleanPrefValue(success, failure, prefName);
			break;
		default:
		  console.log("Error - Preference type is not defined");
		}
	}
}

/**
 * JQuery boilerplate to attach JS functions to relevant HTML elements
 * 
 * @description Add Javascript functions to various HTML tags using JQuery
 * @returns null
 */

jQuery(function() {
	console.log("App Preferences jQuery calls");

	$('#getPref').click(function() {
		SocietiesAppPrefs.getAppPref();
	});
});