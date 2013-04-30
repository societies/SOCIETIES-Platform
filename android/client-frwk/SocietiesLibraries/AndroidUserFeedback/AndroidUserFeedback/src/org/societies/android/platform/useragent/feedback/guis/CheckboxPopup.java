package org.societies.android.platform.useragent.feedback.guis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.*;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.events.IPlatformEventsCallback;
import org.societies.android.api.events.PlatformEventsHelperNotConnectedException;
import org.societies.android.platform.useragent.feedback.R;
import org.societies.android.platform.useragent.feedback.constants.UserFeedbackActivityIntentExtra;
import org.societies.android.remote.helper.EventsHelper;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;

import java.util.ArrayList;
import java.util.List;

public class CheckboxPopup extends Activity {

    private static final String CLIENT_NAME = "org.societies.android.platform.useragent.feedback.guis.CheckboxPopup";
    private static final String LOG_TAG = CheckboxPopup.class.getName();
    EventsHelper eventsHelper = null;
    private boolean isEventsConnected = false;
    private final List<String> resultPayload = new ArrayList<String>();
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
        TextView txtView = (TextView) findViewById(R.id.checkAckProposalText);
        txtView.setText(eventInfo.getProposalText());
        LinearLayout checkboxGroup = (LinearLayout) findViewById(R.id.radioAckRadioGroup);
        Button submitButton = (Button) findViewById(R.id.radioAckOkButton);

        // clear design time sample components
        checkboxGroup.removeAllViews();

        CheckBox checkbox = null;
        for (String option : eventInfo.getOptions()) {
            checkbox = new CheckBox(this);
            checkbox.setText(option);
            checkbox.setTag(option);
            checkboxGroup.addView(checkbox);

            // remember the option as it's clicked
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;

                    if (cb.isChecked())
                        CheckboxPopup.this.resultPayload.add((String) view.getTag());
                    else
                        CheckboxPopup.this.resultPayload.remove((String) view.getTag());
                }
            });
        }

        // handle the submit button click
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEventsConnected) {
                    Log.d(LOG_TAG, "Connected to eventsManager - resultFlag true");
                    publishEvent();
                } else {
                    eventsHelper = new EventsHelper(CheckboxPopup.this);
                    eventsHelper.setUpService(new IMethodCallback() {
                        @Override
                        public void returnAction(String result) {
                            Log.d(LOG_TAG, "eventMgr callback: ReturnAction(String) called");
                        }

                        @Override
                        public void returnAction(boolean resultFlag) {
                            Log.d(LOG_TAG, "eventMgr callback: ReturnAction(boolean) called. Connected");
                            if (resultFlag) {
                                isEventsConnected = true;
                                Log.d(LOG_TAG, "Connected to eventsManager - resultFlag true");
                                publishEvent();
                            }
                        }

                        @Override
                        public void returnException(String result) {
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_checkbox_popup, menu);
        return true;
    }

    @Override
	public void onDestroy() {
		Log.d(LOG_TAG, "CheckboxPopup terminating");
		if (isEventsConnected) {
			eventsHelper.tearDownService(new IMethodCallback() {
				@Override
				public void returnException(String result) { }
				@Override
				public void returnAction(String result) { }
				@Override
				public void returnAction(boolean resultFlag) { }
			});
		}
		super.onDestroy();
	}
    
    private void publishEvent() {
        try {
            ExpFeedbackResultBean bean = new ExpFeedbackResultBean();
            List<String> feedback = new ArrayList<String>();
            feedback.addAll(this.resultPayload); // copy to the feedback array
            bean.setFeedback(feedback);
            bean.setRequestId(eventInfo.getRequestId());

            //TODO: THE PUBLISH EVENT IS OCCURRING MULTIPLE TIMES - DYNAMICALLY CREATED FORM?
            if (!published) {
                eventsHelper.publishEvent(IAndroidSocietiesEvents.UF_RESPONSE_INTENT, bean, new IPlatformEventsCallback() {
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
            }
            published = true;
            //FINISH
            finish();
        } catch (PlatformEventsHelperNotConnectedException e) {
            Log.e("CheckboxPopup.publishEvent()", "Error sending response", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
