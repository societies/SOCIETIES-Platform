package org.societies.android.platform.useragent.feedback;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.XMPPAgent;
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

    // Binder given to clients
    private final IBinder serviceBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public EventHistory getService() {
            // Return this instance of EventHistory so clients can call public methods
            return EventHistory.this;
        }
    }

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

        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(LOG_TAG +".BroadcastReceiver", "onReceive() = " + intent.getAction());
                reloadFromRepository(DEFAULT_FETCH_COUNT);
            }
        }, createIntentFilter());

        clientCommunicationMgr = new ClientCommunicationMgr(getApplicationContext(), false);
        clientCommunicationMgr.bindCommsService(new IMethodCallback() {
            @Override
            public void returnAction(boolean resultFlag) {
                Log.i(LOG_TAG + ".methodCallback", "returnAction(boolean)");
            }

            @Override
            public void returnAction(String result) {
                Log.i(LOG_TAG + ".methodCallback", "returnAction(String)");
            }

            @Override
            public void returnException(String result) {
                Log.i(LOG_TAG + ".methodCallback", "returnException(String)");
            }
        });

        historyRepository = new NotificationHistoryRepository(clientCommunicationMgr);

    }

    private IntentFilter createIntentFilter() {
        //register broadcast receiver to receive SocietiesEvents return values
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE);
        return intentFilter;
    }

    public void reloadFromRepository(int howMany) {
        Log.i(LOG_TAG, "reloadFromRepository(int)");

        Future<List<NotificationHistoryItem>> storedItems = historyRepository.listPrevious(howMany);

        if (storedItems == null) {
            Log.e(LOG_TAG, "historyRepository.listPrevious returned null when trying to reload from repository");
            return;
        }

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
