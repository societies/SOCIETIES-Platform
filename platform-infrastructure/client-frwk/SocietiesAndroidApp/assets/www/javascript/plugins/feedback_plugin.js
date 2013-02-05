/**
 * Provides an API for notifications
 * 
 * @namespace SocietiesAppPreferences
 */
var SocietiesFeedback = { 
 
        /**
         * @methodOf SocietiesFeedback#
         * @description Beep when the user click/press a button
         * @param {Object} successCallback The callback which will be called when result is successful
         * @param {Object} failureCallback The callback which will be called when result is unsuccessful
         * @param {Integer} count The number of beeps
         * @returns null
         */
        beepFeedback: function (success, fail, count)
        {
            console.log("Beeping");
            return cordova.exec (success, fail, "PluginFeedback", "beepFeedback", [count]);
        }
        
        /**
         * @methodOf SocietiesFeedback#
         * @description Vibrate when the user click/press a button
         * @param {Object} successCallback The callback which will be called when result is successful
         * @param {Object} failureCallback The callback which will be called when result is unsuccessful
         * @param {Integer} time The length of vibration
         * @returns null
         */
        vibrateFeedback: function (success, fail, time)
        {
            console.log("Vibrating");
            return cordova.exec (success, fail, "PluginFeedback", "vibrateFeedback", [time]);
        }
};