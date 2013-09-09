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

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.platform.useragent.feedback.*;
import org.societies.android.platform.useragent.feedback.constants.UserFeedbackActivityIntentExtra;
import org.societies.android.platform.useragent.feedback.model.NotificationHistoryItem;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.schema.useragent.feedback.FeedbackMethodType;
import org.societies.api.schema.useragent.feedback.FeedbackStage;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NotificationHistoryPopup extends ListActivity {

    private class PrivateBroadcastReceiver extends BroadcastReceiver {
        protected final String LOG_TAG = this.getClass().getCanonicalName();

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, String.format("onReceive(ctx, intent[action=%s])", intent.getAction()));

            // TO//DO: Also check for new items?

            populateHistoryList();
        }
    }

    protected final String LOG_TAG = this.getClass().getCanonicalName();

    private final EventHistory eventHistoryService = EventHistory.getInstance();

    private SimpleAdapter adapter;
    private final LinkedList<Map<String, String>> valueMapsList;
    private final LinkedList<NotificationHistoryItem> historyItemsList;
    private final PrivateBroadcastReceiver privateBroadcastReceiver;

    public NotificationHistoryPopup() {
        valueMapsList = new LinkedList<Map<String, String>>();
        historyItemsList = new LinkedList<NotificationHistoryItem>();
        privateBroadcastReceiver = new PrivateBroadcastReceiver();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Dialog);

//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notification_history);

        Button closeButton = (Button) findViewById(android.R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationHistoryPopup.this.finish();
            }
        });

        adapter = new SimpleAdapter(
                this,
                valueMapsList,
                R.layout.notification_history_row,
                new String[]{"propText", "propDate"},
                new int[]{R.id.nhPropText, R.id.nhPropDate});
        setListAdapter(adapter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(IAndroidSocietiesEvents.UF_PRIVACY_NEGOTIATION_REQUEST_INTENT);
        filter.addAction(IAndroidSocietiesEvents.UF_PRIVACY_NEGOTIATION_RESPONSE_INTENT);
        filter.addAction(IAndroidSocietiesEvents.UF_ACCESS_CONTROL_REQUEST_INTENT);
        filter.addAction(IAndroidSocietiesEvents.UF_ACCESS_CONTROL_RESPONSE_INTENT);
        filter.addAction(IAndroidSocietiesEvents.UF_REQUEST_INTENT);
        filter.addAction(IAndroidSocietiesEvents.UF_EXPLICIT_RESPONSE_INTENT);
        filter.addAction(IAndroidSocietiesEvents.UF_IMPLICIT_RESPONSE_INTENT);
        registerReceiver(privateBroadcastReceiver, filter);

        populateHistoryList();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(privateBroadcastReceiver);
    }

    private void checkCompletedItems() {
        Log.d(LOG_TAG, "checkCompletedItems()");
        // we've just come back to this window, may need to remove item from list
        boolean changes = false;

        for (int i = 0; i < historyItemsList.size(); i++) {
            NotificationHistoryItem item = historyItemsList.get(i);

            if (item.getUserFeedbackBean().getStage() == FeedbackStage.COMPLETED) {
                Log.d(LOG_TAG + ".checkCompletedItems()", "Removing completed item [" + item.getUserFeedbackBean().getProposalText() + "]");

                historyItemsList.remove(i);
                valueMapsList.remove(i);
                i--;

                changes = true;
            }
        }

        if (changes)
            adapter.notifyDataSetChanged();
    }

    private void populateHistoryList() {
        Log.d(LOG_TAG, "populateHistoryList()");
        List<NotificationHistoryItem> downloadedItems = eventHistoryService.getHistoryItemsList();

        valueMapsList.clear();
        historyItemsList.clear();

        for (NotificationHistoryItem item : downloadedItems) {
            Map<String, String> valuesMap = new HashMap<String, String>();

            if (item.getUserFeedbackBean() != null)
                valuesMap.put("propText", item.getUserFeedbackBean().getProposalText());
            else if (item.getPrivacyNegotiationEvent() != null)
                valuesMap.put("propText", "Privacy Policy Negotiation");
            else if (item.getAccessControlEvent() != null)
                valuesMap.put("propText", "Access Control Request");

            valuesMap.put("propDate", item.getEventDate().toString());

            valueMapsList.add(valuesMap);
            historyItemsList.add(item);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // get the item
        NotificationHistoryItem selectedItem = historyItemsList.get(position);

        if (selectedItem.getUserFeedbackBean() != null)
            processItemClick(selectedItem.getUserFeedbackBean());
        else if (selectedItem.getPrivacyNegotiationEvent() != null)
            processItemClick(selectedItem.getPrivacyNegotiationEvent());
        else if (selectedItem.getAccessControlEvent() != null)
            processItemClick(selectedItem.getAccessControlEvent());
    }

    private void processItemClick(UserFeedbackAccessControlEvent event) {
        Intent intent = new Intent(this.getApplicationContext(), AccessControlActivity.class);
        intent.putExtra(UserFeedbackActivityIntentExtra.EXTRA_PRIVACY_POLICY, (Parcelable) event);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void processItemClick(UserFeedbackPrivacyNegotiationEvent event) {
        Intent intent = new Intent(this.getApplicationContext(), NegotiationActivity.class);
        intent.putExtra(UserFeedbackActivityIntentExtra.EXTRA_PRIVACY_POLICY, (Parcelable) event);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void processItemClick(UserFeedbackBean ufBean) {

        //DETERMINE WHICH ACTIVITY TO LAUNCH
        Class activityClass;
        if (ufBean.getMethod() == FeedbackMethodType.GET_EXPLICIT_FB) {

            // select type of explicit feedback
            if (ufBean.getType() == 0)
                activityClass = RadioPopup.class;
            else if (ufBean.getType() == 1)
                activityClass = CheckboxPopup.class;
            else
                activityClass = AcknackPopup.class;

        } else if (ufBean.getMethod() == FeedbackMethodType.GET_IMPLICIT_FB) {
            // only one type of implicit feedback

            activityClass = TimedAbortPopup.class;

            // Add to the background watcher
            TimedAbortProcessor.getInstance().addTimedAbort(ufBean);

        } else {
            // only one left is "SHOW_NOTIFICATION"

            activityClass = SimpleNotificationPopup.class;
        }

        //CREATE INTENT FOR LAUNCHING ACTIVITY
        Intent intent = new Intent(this.getApplicationContext(), activityClass);
        intent.putExtra(UserFeedbackActivityIntentExtra.USERFEEDBACK_NODES, (Parcelable) ufBean);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        this.startActivity(intent);
    }

}
