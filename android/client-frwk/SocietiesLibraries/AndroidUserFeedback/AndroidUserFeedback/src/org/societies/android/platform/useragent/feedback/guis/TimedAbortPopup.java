/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp.,
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.platform.useragent.feedback.guis;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.events.IPlatformEventsCallback;
import org.societies.android.api.events.PlatformEventsHelperNotConnectedException;
import org.societies.android.platform.useragent.feedback.R;
import org.societies.android.platform.useragent.feedback.TimedAbortProcessor;
import org.societies.api.schema.useragent.feedback.FeedbackStage;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;

public class TimedAbortPopup extends UserFeedbackPopup {

    public static final String IGNORE_FLAG = "ignore";
    public static final String ABORT_FLAG = "abort";
    
    TextView counterTextView;

    public TimedAbortPopup() {
        super(R.layout.activity_timedabort_popup,
                R.id.timedAbortProposalText,
                UserFeedbackPopup.NOT_APPLICABLE,
                UserFeedbackPopup.NOT_APPLICABLE,
                IAndroidSocietiesEvents.UF_IMPLICIT_RESPONSE_INTENT);
        Log.d(LOG_TAG, "TimedAbortPopup()");
    }

    @Override
    protected void populateOptions() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.timedAbortInnerLinearLayout);
        Log.d(LOG_TAG, "populateOptions()");
        // clear design time sample components
        layout.removeAllViews();

        //counterTextView = (TextView) findViewById(R.id.countTextView);      
        
        Button ignoreButton = new Button(this);
        ignoreButton.setText(R.string.timed_abort_positive_string);
        ignoreButton.setTag(R.string.timed_abort_positive_string);
        layout.addView(ignoreButton);

        ignoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getResultPayload().clear();
                getResultPayload().add(IGNORE_FLAG);
                Log.d(LOG_TAG, "getResultPayload(): " + getResultPayload().add(IGNORE_FLAG));

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
                Log.d(LOG_TAG, "getResultPayload(): " + getResultPayload().add(ABORT_FLAG));

                submit();
            }
        });
    }

    @Override
    protected void populateSubmitButton() {
        // can't use the default behaviour - the two buttons need to set and return different values
//        super.populateSubmitButton();
    	Log.d(LOG_TAG, "populateSubmitButton");
    }

    @Override
    protected void publishEvent() {
        try {
        	Log.d(LOG_TAG, "publishEvent()");
            ImpFeedbackResultBean bean = new ImpFeedbackResultBean();
            bean.setAccepted(!getResultPayload().contains(ABORT_FLAG));
            bean.setRequestId(getUserFeedbackBean().getRequestId());
            getUserFeedbackBean().setStage(FeedbackStage.COMPLETED);

            // flag the bean as having response sent, so that the background thread doesn't send an abort response when it times out
            TimedAbortProcessor.getInstance().removeTimedAbort(getUserFeedbackBean().getRequestId());

            //TODO: THE PUBLISH EVENT IS OCCURRING MULTIPLE TIMES - DYNAMICALLY CREATED FORM?
            if (!isPublished()) {
                getEventsHelper().publishEvent(IAndroidSocietiesEvents.UF_IMPLICIT_RESPONSE_INTENT, bean, new IPlatformEventsCallback() {
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
