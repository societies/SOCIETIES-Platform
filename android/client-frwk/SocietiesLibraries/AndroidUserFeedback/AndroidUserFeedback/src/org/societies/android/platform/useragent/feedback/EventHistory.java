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

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.*;
import org.societies.android.platform.androidutils.AndroidNotifier;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.android.platform.useragent.feedback.guis.NotificationHistoryPopup;
import org.societies.android.platform.useragent.feedback.model.NotificationHistoryItem;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackHistoryRequest;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;

import java.util.*;

public class EventHistory extends Service {

    private static EventHistory singleton;

    public static EventHistory getInstance() {
        return singleton;
    }

    public class LocalBinder extends Binder {
        public EventHistory getService() {
            // Return this instance of EventHistory so clients can call public methods
            return EventHistory.this;
        }
    }

    private class BindMethodCallback implements IMethodCallback {
        private final String LOG_TAG = BindMethodCallback.class.getCanonicalName();

        @Override
        public void returnAction(boolean resultFlag) {
            Log.i(LOG_TAG + ".methodCallback", "returnAction(boolean)");

            // NB: If we need to register more names for use in other components, we should either union the two lists or register twice
            clientCommunicationMgr.register(
                    ELEMENT_NAMES,
                    NAMESPACES,
                    PACKAGES,
                    registerMethodCallback);
        }

        @Override
        public void returnAction(String result) {
            Log.i(LOG_TAG + ".methodCallback", "returnAction(String)");
        }

        @Override
        public void returnException(String result) {
            Log.i(LOG_TAG + ".methodCallback", "returnException(String)");
        }
    }

    private class RegisterMethodCallback implements IMethodCallback {
        private final String LOG_TAG = RegisterMethodCallback.class.getCanonicalName();

        @Override
        public void returnAction(boolean resultFlag) {
            Log.i(LOG_TAG + ".methodCallback", "returnAction(boolean)");

            historyRepository.loadNotifications(DEFAULT_FETCH_COUNT);
        }

        @Override
        public void returnAction(String result) {
            Log.i(LOG_TAG + ".methodCallback", "returnAction(String)");
        }

        @Override
        public void returnException(String result) {
            Log.i(LOG_TAG + ".methodCallback", "returnException(String)");
        }
    }

    private class NotificationHistoryRepository implements ICommCallback {
        private final String LOG_TAG = NotificationHistoryRepository.class.getCanonicalName();

        public void loadNotifications(int howMany) {
            try {
                if (clientCommunicationMgr == null) {
                    Log.e(LOG_TAG, "commsManager was null when trying to list previous");
                    return;
                }
                if (clientCommunicationMgr.getIdManager() == null) {
                    Log.e(LOG_TAG, "commsManager.getIdManager() was null when trying to list previous");
                    return;
                }

                Log.d(LOG_TAG, "listPrevious(int)");

                INetworkNode cloudNode = clientCommunicationMgr.getIdManager().getCloudNode();

                UserFeedbackHistoryRequest bean = new UserFeedbackHistoryRequest();

                Stanza stanza = new Stanza(cloudNode);
                String id = UUID.randomUUID().toString();
                stanza.setId(id);


                Log.d(LOG_TAG, "Sending loadNotifications IQ...");
                clientCommunicationMgr.sendIQ(stanza, IQ.Type.GET, bean, this);
                Log.d(LOG_TAG, "loadNotifications IQ sent");

            } catch (InvalidFormatException e) {
                Log.e(LOG_TAG, "Error listing previous notification history items", e);
            } catch (CommunicationException e) {
                Log.e(LOG_TAG, "Error listing previous notification history items", e);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error listing previous notification history items", e);
            }
        }

        @Override
        public List<String> getXMLNamespaces() {
            return NAMESPACES;
        }

        @Override
        public List<String> getJavaPackages() {
            return PACKAGES;
        }

        @Override
        public void receiveResult(Stanza stanza, Object payload) {
            Log.d(LOG_TAG, String.format("receiveResult() \nStanza=%s\nPayload=%s",
                    stanza != null ? stanza.toString() : "null",
                    payload != null ? payload.toString() : "null"));

            if (payload == null) {
                Log.w(LOG_TAG, "Received null payload in receiveResult()");
                return;
            }

            UserFeedbackHistoryRequest request = (UserFeedbackHistoryRequest) payload;

            Log.i(LOG_TAG,
                    String.format("Received: %s UF beans, %s PPNs, %s ACs",
                            request.getUserFeedbackBean() == null ? "null" : request.getUserFeedbackBean().size(),
                            request.getUserFeedbackPrivacyNegotiationEvent() == null ? "null" : request.getUserFeedbackPrivacyNegotiationEvent().size(),
                            request.getUserFeedbackAccessControlEvent() == null ? "null" : request.getUserFeedbackAccessControlEvent().size()));

            List<NotificationHistoryItem> historyItems = new ArrayList<NotificationHistoryItem>();

            // wrap the beans in NotificationHistoryItem objects
            for (UserFeedbackBean bean : request.getUserFeedbackBean()) {
                NotificationHistoryItem item = new NotificationHistoryItem(bean.getRequestId(),
                        bean.getRequestDate(),
//                        new Date(),
                        bean);
                historyItems.add(item);
            }
            for (UserFeedbackPrivacyNegotiationEvent bean : request.getUserFeedbackPrivacyNegotiationEvent()) {
                NotificationHistoryItem item = new NotificationHistoryItem(bean.getRequestId(),
                        bean.getRequestDate(),
//                        new Date(),
                        bean);
                historyItems.add(item);
            }
            for (UserFeedbackAccessControlEvent bean : request.getUserFeedbackAccessControlEvent()) {
                NotificationHistoryItem item = new NotificationHistoryItem(bean.getRequestId(),
                        bean.getRequestDate(),
//                        new Date(),
                        bean);
                historyItems.add(item);
            }

            EventHistory.this.replaceCacheWithList(historyItems);

            EventHistory.this.showHistoryItemsPopup();
        }

        @Override
        public void receiveError(Stanza stanza, XMPPError error) {
            Log.d(LOG_TAG, String.format("receiveError() \nStanza=%s\nerror=%s",
                    stanza != null ? stanza.toString() : "null",
                    error != null ? error.getStanzaErrorString() : "null"));

        }

        @Override
        public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
            Log.d(LOG_TAG, String.format("receiveInfo() \nStanza=%s\nNode=%s\nInfo=%s",
                    stanza != null ? stanza.toString() : "null",
                    node != null ? node : "null",
                    info != null ? info.toString() : "null"));

        }

        @Override
        public void receiveItems(Stanza stanza, String node, List<String> items) {
            Log.d(LOG_TAG, String.format("receiveInfo() \nStanza=%s\nNode=%s\nitems=%s",
                    stanza != null ? stanza.toString() : "null",
                    node != null ? node : "null",
                    items != null ? Arrays.toString(items.toArray()) : "null"));

        }

        @Override
        public void receiveMessage(Stanza stanza, Object payload) {
            Log.d(LOG_TAG, String.format("receiveMessage() \nStanza=%s\nPayload=%s",
                    stanza != null ? stanza.toString() : "null",
                    payload != null ? payload.toString() : "null"));


        }
    }

    private static final String LOG_TAG = EventHistory.class.getCanonicalName();

    public static final int DEFAULT_FETCH_COUNT = 50;
//    public static final int REQUEST_TIMEOUT = 10000;

    public static final List<String> ELEMENT_NAMES = Collections.unmodifiableList(
            Arrays.asList("userFeedbackHistoryRequest"));

    public static final List<String> NAMESPACES = Collections.unmodifiableList(
            Arrays.asList("http://societies.org/api/schema/useragent/feedback",
                    "http://societies.org/api/internal/schema/useragent/feedback"));
    public static final List<String> PACKAGES = Collections.unmodifiableList(
            Arrays.asList("org.societies.api.schema.useragent.feedback",
                    "org.societies.api.internal.schema.useragent.feedback"));

    // Binder given to clients
    private final IBinder serviceBinder = new LocalBinder();

    private final BindMethodCallback bindMethodCallback = new BindMethodCallback();

    private final RegisterMethodCallback registerMethodCallback = new RegisterMethodCallback();

    private NotificationHistoryRepository historyRepository;
    private ClientCommunicationMgr clientCommunicationMgr;

    // NB: to avoid deadlocks, always synchronise on historyItems, not on itemIDs
    private final List<NotificationHistoryItem> historyItems = new ArrayList<NotificationHistoryItem>();
    private final Set<String> itemIDs = new HashSet<String>();

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    @Override
    public void onCreate() {
        Log.i(LOG_TAG, "onCreate()");
        super.onCreate();
        EventHistory.singleton = this;


        clientCommunicationMgr = new ClientCommunicationMgr(getApplicationContext(), true);
        clientCommunicationMgr.bindCommsService(bindMethodCallback);
        historyRepository = new NotificationHistoryRepository();
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "onDestroy()");
        super.onDestroy();
        // NB: If we need to register more names for use in other components, we should either union the two lists or register twice
        clientCommunicationMgr.unregister(ELEMENT_NAMES,
                NAMESPACES,
                registerMethodCallback);
        clientCommunicationMgr.unbindCommsService();
    }

    private void replaceCacheWithList(List<NotificationHistoryItem> storedItems) {
        Log.i(LOG_TAG, String.format("replaceCacheWithList() with %s items",
                storedItems != null ? storedItems.size() : "0"));

        synchronized (historyItems) {
            historyItems.clear();
            itemIDs.clear();

            if (storedItems != null) {
                for (NotificationHistoryItem item : storedItems) {
                    historyItems.add(item);
                    itemIDs.add(item.getUuid());
                }
            }
        }
    }

    private void showHistoryItemsPopup() {
        if (historyItems.isEmpty())
            return;

        // pop up a notification

        //CREATE INTENT FOR LAUNCHING ACTIVITY
        Intent intent = new Intent(this.getApplicationContext(), NotificationHistoryPopup.class);
//                intent.putExtra(UserFeedbackActivityIntentExtra.USERFEEDBACK_NODES, (Parcelable) ufBean);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //CREATE ANDROID NOTIFICATION
        int notifierFlags[] = new int[1];
        notifierFlags[0] = Notification.FLAG_AUTO_CANCEL;
        AndroidNotifier notifier = new AndroidNotifier(this.getApplicationContext(), Notification.DEFAULT_SOUND, notifierFlags);
        notifier.notifyMessage(
                "Click to answer outstanding requests",
                "Outstanding Requests",
                NotificationHistoryPopup.class,
                intent,
                historyItems.size() + " outstanding requests");
    }

    public void addIncomingEvent(UserFeedbackBean uf) {
        synchronized (historyItems) {
            if (itemIDs.contains(uf.getRequestId())) {
                Log.w(LOG_TAG, "UF event ID " + uf.getRequestId() + " already in cache - ignoring");
                return;
            }

            NotificationHistoryItem newItem = new NotificationHistoryItem(
                    uf.getRequestId(),
                    new Date(),
                    uf
            );

            historyItems.add(newItem);
            itemIDs.add(newItem.getUuid());
        }
    }

    public void addIncomingEvent(UserFeedbackPrivacyNegotiationEvent ppn) {
        synchronized (historyItems) {
            if (itemIDs.contains(ppn.getRequestId())) {
                Log.w(LOG_TAG, "PPN event ID " + ppn.getRequestId() + " already in cache - ignoring");
                return;
            }

            NotificationHistoryItem newItem = new NotificationHistoryItem(
                    ppn.getRequestId(),
                    new Date(),
                    ppn
            );

            historyItems.add(newItem);
            itemIDs.add(newItem.getUuid());
        }
    }

    public List<NotificationHistoryItem> getHistoryItemsList() {
        return Collections.unmodifiableList(historyItems);
    }

}
