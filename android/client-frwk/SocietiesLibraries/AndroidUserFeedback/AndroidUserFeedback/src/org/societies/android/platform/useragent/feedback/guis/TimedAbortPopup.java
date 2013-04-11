package org.societies.android.platform.useragent.feedback.guis;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.events.IPlatformEventsCallback;
import org.societies.android.api.events.PlatformEventsHelperNotConnectedException;
import org.societies.android.platform.useragent.feedback.R;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;

public class TimedAbortPopup extends UserFeedbackPopup {

    public static final String IGNORE_FLAG = "ignore";
    public static final String ABORT_FLAG = "abort";

    public TimedAbortPopup() {
        super(R.layout.activity_timedabort_popup,
                R.id.timedAbortProposalText,
                UserFeedbackPopup.NOT_APPLICABLE,
                UserFeedbackPopup.NOT_APPLICABLE);
    }

    @Override
    protected void populateOptions() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.timedAbortInnerLinearLayout);

        // clear design time sample components
        layout.removeAllViews();

        Button ignoreButton = new Button(this);
        ignoreButton.setText(R.string.timed_abort_positive_string);
        ignoreButton.setTag(R.string.timed_abort_positive_string);
        layout.addView(ignoreButton);

        ignoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getResultPayload().clear();
                getResultPayload().add(IGNORE_FLAG);

                submit();
            }
        });
        Button abortButton = new Button(this);
        abortButton.setText(R.string.timed_abort_negative_string);
        abortButton.setTag(R.string.timed_abort_negative_string);
        layout.addView(abortButton);

        abortButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getResultPayload().clear();
                getResultPayload().add(ABORT_FLAG);

                submit();
            }
        });
    }

    @Override
    protected void populateSubmitButton() {
        // can't use the default behaviour - the two buttons need to set and return different values
//        super.populateSubmitButton();

    }

    @Override
    protected void publishEvent() {
        try {
            ImpFeedbackResultBean bean = new ImpFeedbackResultBean();
            bean.setAccepted(!getResultPayload().contains(ABORT_FLAG));
            bean.setRequestId(getUserFeedbackBean().getRequestId());

            //TODO: THE PUBLISH EVENT IS OCCURRING MULTIPLE TIMES - DYNAMICALLY CREATED FORM?
            if (!isPublished()) {
                getEventsHelper().publishEvent(IAndroidSocietiesEvents.UF_EXPLICIT_RESPONSE_INTENT, bean, new IPlatformEventsCallback() {
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
            setPublished(true);

            //FINISH
            getEventsHelper().tearDownService(new IMethodCallback() {
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
            Log.e(LOG_TAG, "Error sending response", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
