package org.societies.android.platform.useragent.feedback;

import android.util.Log;
import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.comms.xmpp.*;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.android.platform.useragent.feedback.model.NotificationHistoryItem;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NotificationHistoryRepository implements INotificationHistoryRepository {
    private class RequestCallback implements ICommCallback, Future<List<NotificationHistoryItem>> {

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
            Log.w(LOG_TAG, String.format("receiveResult() \nStanza=%s\nPayload=%s",
                    stanza != null ? stanza.toString() : "null",
                    payload != null ? payload.toString() : "null"));

        }

        @Override
        public void receiveError(Stanza stanza, XMPPError error) {
            Log.w(LOG_TAG, String.format("receiveError() \nStanza=%s\nerror=%s",
                    stanza != null ? stanza.toString() : "null",
                    error != null ? error.toString() : "null"));

        }

        @Override
        public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
            Log.w(LOG_TAG, String.format("receiveInfo() \nStanza=%s\nNode=%s\nInfo=%s",
                    stanza != null ? stanza.toString() : "null",
                    node != null ? node : "null",
                    info != null ? info.toString() : "null"));

        }

        @Override
        public void receiveItems(Stanza stanza, String node, List<String> items) {
            Log.w(LOG_TAG, String.format("receiveInfo() \nStanza=%s\nNode=%s\nitems=%s",
                    stanza != null ? stanza.toString() : "null",
                    node != null ? node : "null",
                    items != null ? Arrays.toString(items.toArray()) : "null"));

        }

        @Override
        public void receiveMessage(Stanza stanza, Object payload) {
            Log.w(LOG_TAG, String.format("receiveMessage() \nStanza=%s\nPayload=%s",
                    stanza != null ? stanza.toString() : "null",
                    payload != null ? payload.toString() : "null"));


        }

        @Override
        public boolean cancel(boolean b) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public List<NotificationHistoryItem> get() throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public List<NotificationHistoryItem> get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }
    }

    private static final String LOG_TAG = NotificationHistoryRepository.class.getCanonicalName();

    public static final List<String> NAMESPACES = Collections.unmodifiableList(
            Arrays.asList("http://societies.org/api/schema/useragent/monitoring",
                    "http://societies.org/api/schema/useragent/feedback"));
    public static final List<String> PACKAGES = Collections.unmodifiableList(
            Arrays.asList("org.societies.api.schema.useragent.monitoring",
                    "org.societies.api.schema.useragent.feedback"));


    private final ClientCommunicationMgr commsManager;

    public NotificationHistoryRepository(ClientCommunicationMgr commsManager) {
        this.commsManager = commsManager;
    }


    @Override
    public Future<List<NotificationHistoryItem>> listPrevious(int howMany) {

        try {
            if (commsManager == null) {
                Log.e(LOG_TAG, "commsManager was null when trying to list previous");
                return null;
            }
            if (commsManager.getIdManager() == null) {
                Log.e(LOG_TAG, "commsManager.getIdManager() was null when trying to list previous");
                return null;
            }

            INetworkNode cloudNode = commsManager.getIdManager().getCloudNode();

            RequestCallback requestCallback = new RequestCallback();

            Stanza stanza = new Stanza(cloudNode);
            commsManager.sendIQ(stanza, IQ.Type.GET, "REQUEST_PREVIOUS_NHQ", requestCallback);

            return requestCallback;

        } catch (InvalidFormatException e) {
            Log.e(LOG_TAG, "Error listing previous notification history items", e);
        } catch (CommunicationException e) {
            Log.e(LOG_TAG, "Error listing previous notification history items", e);
        }

        return null;
    }

    @Override
    public Future<List<NotificationHistoryItem>> listSince(Date sinceWhen) {
        return null;
    }
}
