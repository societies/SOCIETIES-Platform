package org.societies.android.platform.useragent.feedback.guis;

import org.societies.android.platform.useragent.feedback.R;
import org.societies.android.platform.useragent.feedback.R.layout;
import org.societies.android.platform.useragent.feedback.R.menu;
import org.societies.android.platform.useragent.feedback.constants.UserFeedbackActivityIntentExtra;
import org.societies.android.remote.helper.EventsHelper;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheckboxPopup extends Activity {

	private static final String CLIENT_NAME      = "org.societies.android.platform.useragent.feedback.guis.CheckboxPopup";
	private static final String LOG_TAG = CheckboxPopup.class.getName();
	EventsHelper eventsHelper = null;
	private boolean isEventsConnected = false;
	private String resultPayload = "";
	private UserFeedbackBean eventInfo;
	private boolean published = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_checkbox_popup);
        
        //RETRIEVE USERFEEDBACK BEAN FROM INTENT
  		Intent intent = getIntent();
  		Bundle bundle = intent.getExtras();
  		eventInfo = bundle.getParcelable(UserFeedbackActivityIntentExtra.EXTRA_PRIVACY_POLICY);
  		
  		//HEADER
  		TextView txtView = (TextView) findViewById(R.id.textView1);
		txtView.setText(eventInfo.getProposalText());
		LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout1);
		
		
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_checkbox_popup, menu);
        return true;
    }
}
