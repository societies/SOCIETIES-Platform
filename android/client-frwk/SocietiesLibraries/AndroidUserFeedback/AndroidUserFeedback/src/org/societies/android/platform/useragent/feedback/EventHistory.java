package org.societies.android.platform.useragent.feedback;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.android.platform.useragent.feedback.model.NotificationHistoryItem;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;

import java.util.*;
import java.util.concurrent.Future;

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
                    NotificationHistoryRepository.ELEMENT_NAMES,
                    NotificationHistoryRepository.NAMESPACES,
                    NotificationHistoryRepository.PACKAGES,
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

            reloadFromRepository(DEFAULT_FETCH_COUNT);
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

    public static final int REQUEST_TIMEOUT = 10000;

    // Binder given to clients
    private final IBinder serviceBinder = new LocalBinder();

    private final BindMethodCallback bindMethodCallback = new BindMethodCallback();
    private final RegisterMethodCallback registerMethodCallback = new RegisterMethodCallback();

    private static final String LOG_TAG = EventHistory.class.getCanonicalName();
    public static final int DEFAULT_FETCH_COUNT = 50;

    private INotificationHistoryRepository historyRepository;
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
        historyRepository = new NotificationHistoryRepository(clientCommunicationMgr);
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "onDestroy()");
        super.onDestroy();
        // NB: If we need to register more names for use in other components, we should either union the two lists or register twice
        clientCommunicationMgr.unregister(NotificationHistoryRepository.ELEMENT_NAMES, NotificationHistoryRepository.NAMESPACES, registerMethodCallback);
        clientCommunicationMgr.unbindCommsService();
    }

    public void reloadFromRepository(int howMany) {
        Log.i(LOG_TAG, "reloadFromRepository(int)");

        Future<List<NotificationHistoryItem>> storedItems = historyRepository.listPrevious(howMany);

        if (storedItems == null) {
            Log.e(LOG_TAG, "historyRepository.listPrevious returned null when trying to reload from repository");
            return;
        }

        Log.d(LOG_TAG, "Waiting for response to arrive");


        try {
            synchronized (storedItems) {
                storedItems.wait(REQUEST_TIMEOUT);
            }
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "Error sleeping while waiting for historyRepository.listPrevious(int) response", e);
        }

        if (!storedItems.isDone()) {
            Log.w(LOG_TAG, "No response received for historyRepository.listPrevious(int) after " + REQUEST_TIMEOUT + "ms");
            return;
        }

        Log.d(LOG_TAG, "Response has arrived");
        try {
            Log.d(LOG_TAG, "Received " + storedItems.get().size() + " items");
            replaceCacheWithList(storedItems.get());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error getting reloadFromRepository response", e);
        }
    }

    public void reloadFromRepository(Date sinceWhen) {
        Log.i(LOG_TAG, "reloadFromRepository(Date)");

        Future<List<NotificationHistoryItem>> storedItems = historyRepository.listSince(sinceWhen);

        while (!storedItems.isDone()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "Error sleeping while waiting for reloadFromRepository response", e);
            }
        }

        try {
            replaceCacheWithList(storedItems.get());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error getting reloadFromRepository response", e);
        }
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
