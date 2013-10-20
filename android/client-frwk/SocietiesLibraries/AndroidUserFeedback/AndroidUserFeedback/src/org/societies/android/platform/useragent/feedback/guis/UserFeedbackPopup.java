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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.events.IPlatformEventsCallback;
import org.societies.android.api.events.PlatformEventsHelperNotConnectedException;
import org.societies.android.platform.useragent.feedback.constants.UserFeedbackActivityIntentExtra;
import org.societies.android.remote.helper.EventsHelper;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.FeedbackStage;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;

import java.util.ArrayList;
import java.util.List;

public abstract class UserFeedbackPopup extends Activity {

    public static final int NOT_APPLICABLE = -999;

    protected final String LOG_TAG = this.getClass().getName();
    private UserFeedbackBean userFeedbackBean;
    private final List<String> resultPayload = new ArrayList<String>();

    private EventsHelper eventsHelper = null;
    private boolean isEventsConnected = false;
    private boolean published = false;

    protected final int contentViewID;
    protected final int headerID;
    protected final int submitButtonID;
    protected final int optionsMenuID;
    protected final String responseIntent;

    protected UserFeedbackPopup(int contentViewID, int headerID, int submitButtonID, int optionsMenuID, String responseIntent) {
    	Log.d(LOG_TAG, "UserFeedbackPopup()");
        this.contentViewID = contentViewID;
        this.headerID = headerID;
        this.submitButtonID = submitButtonID;
        this.optionsMenuID = optionsMenuID;
        this.responseIntent = responseIntent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Dialog);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(contentViewID);
        
        Log.d(LOG_TAG, "onCreate UserFeedbackPopup()");

        //RETRIEVE USER FEEDBACK BEAN FROM INTENT
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userFeedbackBean = bundle.getParcelable(UserFeedbackActivityIntentExtra.USERFEEDBACK_NODES);

        populateHeader();

        populateOptions();

        populateSubmitButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (optionsMenuID == NOT_APPLICABLE) return true;

        getMenuInflater().inflate(optionsMenuID, menu);
        return true;
    }

    protected void populateHeader() {
        if (headerID == NOT_APPLICABLE) return;

        TextView txtView = (TextView) findViewById(headerID);
        txtView.setText(userFeedbackBean.getProposalText());
        Log.d(LOG_TAG, "UserFeedBackPopUp- populateHeader()");
    }

    protected abstract void populateOptions();

    protected void populateSubmitButton() {
        if (submitButtonID == NOT_APPLICABLE) return;

        // handle the submit button click
        Button submitButton = (Button) findViewById(submitButtonID);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    protected void submit() {
    	Log.d(LOG_TAG, "submit()");
        if (isEventsConnected) {
            Log.d(LOG_TAG, "Connected to eventsManager - resultFlag true");
            publishEvent();
        } else {
            eventsHelper = new EventsHelper(this);
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

    protected void publishEvent() {
        try {
        	Log.d(LOG_TAG, "publishEvent()");
            ExpFeedbackResultBean bean = new ExpFeedbackResultBean();
            List<String> feedback = new ArrayList<String>();
            feedback.addAll(this.resultPayload); // copy to the feedback array
            bean.setFeedback(feedback);
            bean.setRequestId(userFeedbackBean.getRequestId());
            userFeedbackBean.setStage(FeedbackStage.COMPLETED);

            //TODO: THE PUBLISH EVENT IS OCCURRING MULTIPLE TIMES - DYNAMICALLY CREATED FORM?
            if (!published) {
                eventsHelper.publishEvent(responseIntent, bean, new IPlatformEventsCallback() {
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
            Log.e(LOG_TAG, "Error sending response", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    protected final UserFeedbackBean getUserFeedbackBean() {
        return userFeedbackBean;
    }

    protected final List<String> getResultPayload() {
        return resultPayload;
    }

    protected EventsHelper getEventsHelper() {
        return eventsHelper;
    }

    protected boolean isPublished() {
        return published;
    }

    protected void setPublished(boolean published) {
        this.published = published;
    }
}
