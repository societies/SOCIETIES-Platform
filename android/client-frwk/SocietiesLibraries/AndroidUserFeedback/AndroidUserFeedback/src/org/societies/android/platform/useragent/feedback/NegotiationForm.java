package org.societies.android.platform.useragent.feedback;

import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.events.IPlatformEventsCallback;
import org.societies.android.api.events.PlatformEventsHelperNotConnectedException;
import org.societies.android.remote.helper.EventsHelper;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class NegotiationForm extends Activity {
	private static final String EXTRA_PRIVACY_POLICY = "org.societies.userfeedback.eventInfo";
	EventsHelper eventsHelper = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negotiation_form);
        
        UserFeedbackPrivacyNegotiationEvent eventInfo = (UserFeedbackPrivacyNegotiationEvent)getIntent().getParcelableExtra(EXTRA_PRIVACY_POLICY);
        
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_negotiation_form, menu);
        return true;
    }
    
    private void publishEvent(UserFeedbackPrivacyNegotiationEvent eventInfo) {
		try {    		
			eventsHelper.publishEvent(IAndroidSocietiesEvents.UF_PRIVACY_NEGOTIATION_RESPONSE_INTENT, eventInfo, new IPlatformEventsCallback() {
				@Override				
				public void returnAction(int result) {
					}				
				@Override				
				public void returnAction(boolean resultFlag) {									
					}
				@Override
				public void returnException(int exception) {
				}			
			});
		} catch (PlatformEventsHelperNotConnectedException e) {
			e.printStackTrace();		
		}	
	}
}
