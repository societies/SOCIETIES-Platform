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
package org.societies.android.platform.useragent.feedback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.VCardParcel;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.events.IPlatformEventsCallback;
import org.societies.android.api.events.PlatformEventsHelperNotConnectedException;
import org.societies.android.platform.useragent.feedback.constants.UserFeedbackActivityIntentExtra;
import org.societies.android.remote.helper.EventsHelper;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

import java.util.List;

public class NegotiationActivity extends Activity implements OnItemSelectedListener {

    private static final String LOG_TAG = NegotiationActivity.class.getCanonicalName();

    private EventsHelper eventsHelper;
    private UserFeedbackPrivacyNegotiationEvent privacyNegotiationEvent = null;
    private boolean isEventsConnected = false;
    private TableLayout[] tblConditions;
    private ScrollView svScroll;
    private boolean published = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_negotiation);
        eventsHelper = new EventsHelper(this);

        //GET EVENT OBJECT
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        try {
            privacyNegotiationEvent = bundle.getParcelable(UserFeedbackActivityIntentExtra.EXTRA_PRIVACY_POLICY);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error unparceling PPN", ex);
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        //SET HEADER INFO
        RequestorBean requestor = privacyNegotiationEvent.getNegotiationDetails().getRequestor();
        String sRequestor = requestor.getRequestorId();
        if (requestor instanceof RequestorServiceBean) {
            sRequestor = sRequestor.concat("\nfor Installed service:"+((RequestorServiceBean) requestor).getRequestorServiceId().getServiceInstanceIdentifier()+"\n");
        }else if (requestor instanceof RequestorCisBean){
        	sRequestor = sRequestor.concat("\nfor community:"+((RequestorCisBean) requestor).getCisRequestorId()+"\n");
        }
        TextView lblHeader = (TextView) findViewById(R.id.txtHeader);
        //lblHeader.setText(sHeader + "\r\n has requested access to the following data:");
        lblHeader.setText("The " + sRequestor + " is requesting access to your personal info for the following uses. Please select what you would like to allow:");

        //GENERATE RESOURCE SPINNER
        final List<ResponseItem> responses = privacyNegotiationEvent.getResponsePolicy().getResponseItems();
        String[] resourceItems = new String[responses.size()];
        for (int i = 0; i < responses.size(); i++) {
            resourceItems[i] = responses.get(i).getRequestItem().getResource().getDataType();
        }
        Spinner spinResources = (Spinner) findViewById(R.id.spinResource);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, resourceItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinResources.setAdapter(adapter);
        spinResources.setOnItemSelectedListener(this);

        //PROCESS EACH RESPONSE
        tblConditions = new TableLayout[responses.size()];

        populateResponseGuiTable(responses);

        //ADD FIRST TABLE OF CONDITIONS TO SCROLL VIEW - OTHERS ADDED ON CHANGE EVENT
        svScroll = (ScrollView) findViewById(R.id.svConditions);
        svScroll.addView(tblConditions[0]);

        //ACCEPT BUTTON EVENT HANDLER
        Button btnAccept = (Button) findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                publishEvent();
                finish();
            }
        });
        //CANCEL BUTTON EVENT HANDLER
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //BASICALLY, IGNORE REQUEST
            }
        });

        //ADD IMAGE - IF AVAILABLE
        populateAvatar();
    }

    private void populateAvatar() {
        try {
            VCardParcel vCard = getIntent().getParcelableExtra(UserFeedbackActivityIntentExtra.EXTRA_CSS_VCARD);
            byte[] avatarBytes = vCard.getAvatar();
            if (avatarBytes != null) {
                Bitmap bMap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);

                ImageView image = (ImageView) findViewById(R.id.imageProfile);
                image.setImageBitmap(bMap);
            }
        } catch (Exception ex) {
            Log.w(LOG_TAG, "Error loading avatar", ex);
        }
    }

    private void populateResponseGuiTable(List<ResponseItem> responses) {
        for (int response_idx = 0; response_idx < responses.size(); response_idx++) {
            ResponseItem response = responses.get(response_idx);
            RequestItem request = response.getRequestItem();
            List<Condition> conditions = request.getConditions();

            TableLayout tblCondition = new TableLayout(this);
            tblCondition.setBackgroundResource(R.color.Grey);
            View[] requestControls = new View[conditions.size()];

            //EACH CONDITION
            for (int condition_idx = 0; condition_idx < conditions.size(); condition_idx++) {
                Condition condition = conditions.get(condition_idx);
                String contentDescription = response_idx + "_" + condition_idx;

                View newControl = createGuiControlFromCondition(responses, condition, contentDescription);
                requestControls[condition_idx] = newControl;


                TableRow row = new TableRow(this);
                row.setId(condition_idx);
                //row.addView(requestLabels[i]);

                TextView label = new TextView(this);
                label.setText(condition.getConditionConstant().value());
                row.addView(label);
                row.addView(requestControls[condition_idx]);

                tblCondition.addView(row);
            }
            tblConditions[response_idx] = tblCondition;
        }
    }

    private View createGuiControlFromCondition(final List<ResponseItem> responses, Condition condition, String contentDescription) {
        //DATA TYPE - CHECKBOX/TEXTBOX
        if (condition.getConditionConstant().value().startsWith("data")) {
            final EditText textbox = new EditText(this);
            textbox.setText(condition.getValue());
            textbox.setContentDescription(contentDescription);
            textbox.addTextChangedListener(new TextWatcher() {
                EditText thisTextBox = textbox;

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String textValue = thisTextBox.getText().toString();
                    String posValues = (String) thisTextBox.getContentDescription();
                    String[] positions = posValues.split("_");
                    int responsePos = Integer.parseInt(positions[0]);
                    int conditionPos = Integer.parseInt(positions[1]);
                    responses.get(responsePos).getRequestItem().getConditions().get(conditionPos).setValue(textValue);
                }
            });
            return textbox;
        } else {
            CheckBox checkbox = new CheckBox(this);
            checkbox.setChecked(true);
            checkbox.setEnabled(condition.isOptional());
            checkbox.setContentDescription(contentDescription);
            checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String conditionValue = (isChecked ? "1" : "0");
                    String posValues = (String) buttonView.getContentDescription();
                    String[] positions = posValues.split("_");
                    int responsePos = Integer.parseInt(positions[0]);
                    int conditionPos = Integer.parseInt(positions[1]);
                    responses.get(responsePos).getRequestItem().getConditions().get(conditionPos).setValue(conditionValue);
                }
            });
            return checkbox;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_negotiation, menu);
        return true;
    }

    /* @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long) */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        svScroll.removeAllViews();
        svScroll.addView(tblConditions[position]);
    }

    /* @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView) */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "NegotiationActivity terminating");
        if (isEventsConnected) {
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
        }
        super.onDestroy();
    }

    private void publishEvent() {
        if (!published) { //EVENT IS BEING PUBLISHED MULTIPLE TIMES
            if (!isEventsConnected) {
                eventsHelper.setUpService(new IMethodCallback() {
                    @Override
                    public void returnException(String result) {
                    }

                    @Override
                    public void returnAction(String result) {
                    }

                    @Override
                    public void returnAction(boolean resultFlag) {
                        if (resultFlag) {
                            try {
                                isEventsConnected = true;
                                published = true;
                                eventsHelper.publishEvent(IAndroidSocietiesEvents.UF_PRIVACY_NEGOTIATION_RESPONSE_INTENT, NegotiationActivity.this.privacyNegotiationEvent, new IPlatformEventsCallback() {
                                    @Override
                                    public void returnException(int exception) {
                                    }

                                    @Override
                                    public void returnAction(int result) {
                                    }

                                    @Override
                                    public void returnAction(boolean resultFlag) {
                                    }
                                });
                            } catch (PlatformEventsHelperNotConnectedException e) {
                                Log.e(LOG_TAG, "Error publishing PPN response event", e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }

}
