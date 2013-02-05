phonegapdesktop.internal.parseConfigFile('pluginjs/feedback.json');

window.plugins.SocietiesFeedback = {
    beepFeedback : function(successCallback, errorCallback){
        if (phonegapdesktop.internal.randomException("Feedback")) {
            errorCallback('A random error was generated');
        }
        else {
            successCallback(phonegapdesktop.internal.getDebugValue('Feedback', 'beepFeedback'));
        }            
    }

    vibrateFeedback : function(successCallback, errorCallback){
        if (phonegapdesktop.internal.randomException("Feedback")) {
            errorCallback('A random error was generated');
        }
        else {
            successCallback(phonegapdesktop.internal.getDebugValue('Feedback', 'vibrateFeedback'));
        }            
    }
}

