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

public class RadioPopup extends Activity {

    private static final String CLIENT_NAME = "org.societies.android.platform.useragent.feedback.guis.RadioPopup";
    private static final String LOG_TAG = RadioPopup.class.getName();
    EventsHelper eventsHelper = null;
    private boolean isEventsConnected = false;
    private String resultPayload = "";
    private UserFeedbackBean eventInfo;
    private boolean published = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_radio_popup);

        //RETRIEVE USER FEEDBACK BEAN FROM INTENT
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        eventInfo = bundle.getParcelable(UserFeedbackActivityIntentExtra.EXTRA_PRIVACY_POLICY);

        //HEADER
        TextView txtView = (TextView) findViewById(R.id.textView1);
        txtView.setText(eventInfo.getProposalText());
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioAckRadioGroup);
        Button submitButton = (Button) findViewById(R.id.radioAckOkButton);

        // clear design time sample components
        radioGroup.removeAllViews();

        RadioButton radio = null;
        for (String option : eventInfo.getOptions()) {
            radio = new RadioButton(this);
            radio.setText(option);
            radio.setTag(option);
            radioGroup.addView(radio);

            // remember the option as it's clicked
            radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RadioPopup.this.resultPayload = (String) view.getTag();
                }
            });
        }
        // check the last item implicitly
        if (radio != null)
            radio.setChecked(true);

        // handle the submit button click
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEventsConnected) {
                    Log.d(LOG_TAG, "Connected to eventsManager - resultFlag true");
                    publishEvent();
                } else {
                    eventsHelper = new EventsHelper(RadioPopup.this);
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
        getMenuInflater().inflate(R.menu.activity_radio_popup, menu);
        return true;
    }

    private void publishEvent() {
        try {
            ExpFeedbackResultBean bean = new ExpFeedbackResultBean();
            List<String> feedback = new ArrayList<String>();
            feedback.add(this.resultPayload);
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
            eventsHelper.tearDownService(new IMethodCallback() {
                @Override
                public void returnException(String result) {
                }

                @Override
                public void returnAction(String result) {
                }

                @Override
                public void returnAction(boolean resultFlag) {
                }
            });
            finish();
        } catch (PlatformEventsHelperNotConnectedException e) {
            Log.e("RadioPopup.publishEvent()", "Error sending response", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
