package org.societies.android.platform.pubsub.helper;

import android.content.*;
import android.os.*;
import android.util.Log;
import org.jivesoftware.smack.packet.Packet;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.XMPPAgent;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.pubsub.*;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.platform.androidutils.MarshallUtils;
import org.societies.android.platform.androidutils.PacketMarshaller;
import org.societies.api.identity.IIdentity;
import org.societies.utilities.DBC.Dbc;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;


public class PubsubHelper implements IPubsubClient {
    private static final String LOG_TAG = PubsubHelper.class.getName();
    private static final String SERVICE_ACTION = "org.societies.android.platform.comms.app.ServicePlatformPubsubRemote";
    private static final boolean DEBUG_LOGGING = true;

    private final static List<String> NAMESPACES = Collections
            .unmodifiableList(Arrays.asList("http://jabber.org/protocol/pubsub",
                    "http://jabber.org/protocol/pubsub#errors",
                    "http://jabber.org/protocol/pubsub#event",
                    "http://jabber.org/protocol/pubsub#owner",
                    "http://jabber.org/protocol/disco#items"));
    private static final List<String> PACKAGES = Collections
            .unmodifiableList(Arrays.asList("org.jabber.protocol.pubsub",
                    "org.jabber.protocol.pubsub.errors",
                    "org.jabber.protocol.pubsub.event",
                    "org.jabber.protocol.pubsub.owner"));

    private static final List<String> ELEMENTS = Collections.unmodifiableList(
            Arrays.asList("pubsub",
                    "event",
                    "query"));

    private boolean boundToService;
    private Messenger targetService = null;
    private String clientPackageName;
    private Random randomGenerator;
    private Serializer serializer;

    private Context androidContext;
    private final Map<Long, ICommCallback> xmppCallbackMap;
    private final Map<Long, IMethodCallback> methodCallbackMap;
    private final Map<String, Class<?>> elementToClass;

    private BroadcastReceiver receiver;
    private IMethodCallback bindCallback;
    private PacketMarshaller marshaller;
    private ISubscriber subscriberCallback;

    public PubsubHelper(Context androidContext) {
        Dbc.require("Android context must be supplied", null != androidContext);

        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Instantiate PubsubHelper");
        }
        this.androidContext = androidContext;

        this.clientPackageName = this.androidContext.getApplicationContext().getPackageName();
        this.randomGenerator = new Random(System.currentTimeMillis());

        this.xmppCallbackMap = Collections.synchronizedMap(new HashMap<Long, ICommCallback>());
        this.methodCallbackMap = Collections.synchronizedMap(new HashMap<Long, IMethodCallback>());
        this.elementToClass = Collections.synchronizedMap(new HashMap<String, Class<?>>());

        Strategy strategy = new AnnotationStrategy();
        this.serializer = new Persister(strategy);

        this.marshaller = new PacketMarshaller();
        marshaller.register(ELEMENTS, NAMESPACES, PACKAGES);
    }

    /**
     * set the event subscriber for Pubsub node events
     *
     * @param subscriberCallback
     */
    public void setSubscriberCallback(ISubscriber subscriberCallback) {
        this.subscriberCallback = subscriberCallback;
    }

    /**
     * Binds to Android Pubsub Service
     *
     * @param bindCallback callback
     */
    public boolean bindPubsubService(IMethodCallback bindCallback) {
        Dbc.require("Service Bind Callback cannot be null", null != bindCallback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Bind to Android Pubsub Service");
        }
        this.setupBroadcastReceiver();

        this.bindCallback = bindCallback;
        this.bindToPubsubService();

        return false;
    }

    /**
     * Unbinds from the Android Pubsub service
     *
     * @return true if no more requests queued
     */
    public boolean unbindCommsService(IMethodCallback bindCallback) {
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Unbind from Android Pubsub Service");
        }
        this.bindCallback = bindCallback;

        synchronized (PubsubHelper.this.methodCallbackMap) {
            long remoteCallID = PubsubHelper.this.randomGenerator.nextLong();
            PubsubHelper.this.methodCallbackMap.put(remoteCallID, null);
            InvokeUnBindToAndroidComms invoker = new InvokeUnBindToAndroidComms(PubsubHelper.this.clientPackageName, remoteCallID);
            invoker.execute();
        }

        return false;
    }

    @Override
    public void addSimpleClasses(List<String> classList) throws ClassNotFoundException {
        Dbc.require("Class list must have at least one class", null != classList && classList.size() > 0);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "addSimpleClasses called");
        }

        for (String c : classList) {
            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "Adding Simple Class: " + c);
            }

            Class<?> clazz = Class.forName(c);
            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "clazz: " + clazz.getName());
            }

            Root rootAnnotation = clazz.getAnnotation(Root.class);
            Namespace namespaceAnnotation = clazz.getAnnotation(Namespace.class);
            if (rootAnnotation != null && namespaceAnnotation != null) {
                if (DEBUG_LOGGING) {
                    Log.d(LOG_TAG, "Adding Simple Class: " + c + " key: " + "{" + namespaceAnnotation.reference() + "}" + rootAnnotation.name());
                }
                elementToClass.put("{" + namespaceAnnotation.reference() + "}" + rootAnnotation.name(), clazz);
            }
        }
    }

    @Override
    public List<String> discoItems(IIdentity arg0, String arg1, IMethodCallback callback) throws XMPPError, CommunicationException {
        Dbc.require("Method callback cannot be null", null != callback);
        Dbc.invariant("Method currently unsupported", false);
        return null;
    }

    @Override
    public void ownerCreate(IIdentity pubsubServiceID, String node, IMethodCallback callback) throws XMPPError, CommunicationException {
        Dbc.require("Pubsub identity cannot be null", null != pubsubServiceID);
        Dbc.require("Pubsub node must be specified", null != node && node.length() > 0);
        Dbc.require("Method callback cannot be null", null != callback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "ownerCreate called for node: " + node);
        }

        final String pubsubServiceJid = pubsubServiceID.getJid();

        synchronized (this.methodCallbackMap) {
            long remoteCallID = this.randomGenerator.nextLong();
            this.methodCallbackMap.put(remoteCallID, callback);
            InvokeOwnerCreate invoker = new InvokeOwnerCreate(this.clientPackageName, pubsubServiceJid, node, remoteCallID);
            invoker.execute();
        }
    }

    @Override
    public void ownerDelete(IIdentity pubsubServiceID, String node, IMethodCallback callback) throws XMPPError, CommunicationException {
        Dbc.require("Pubsub identity cannot be null", null != pubsubServiceID);
        Dbc.require("Pubsub node must be specified", null != node && node.length() > 0);
        Dbc.require("Method callback cannot be null", null != callback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "ownerDelete called for node: " + node);
        }

        final String pubsubServiceJid = pubsubServiceID.getJid();
        synchronized (this.methodCallbackMap) {
            long remoteCallID = this.randomGenerator.nextLong();
            this.methodCallbackMap.put(remoteCallID, callback);
            InvokeOwnerDelete invoker = new InvokeOwnerDelete(this.clientPackageName, pubsubServiceJid, node, remoteCallID);
            invoker.execute();
        }
    }

    @Override
    public Map<IIdentity, Affiliation> ownerGetAffiliations(IIdentity arg0, String arg1, IMethodCallback callback) throws XMPPError, CommunicationException {
        Dbc.require("Method callback cannot be null", null != callback);
        Dbc.invariant("Method currently unsupported", false);
        return null;
    }

    @Override
    public Map<IIdentity, SubscriptionState> ownerGetSubscriptions(IIdentity arg0, String arg1, IMethodCallback callback) throws XMPPError, CommunicationException {
        Dbc.require("Method callback cannot be null", null != callback);
        Dbc.invariant("Method currently unsupported", false);
        return null;
    }

    @Override
    public void ownerPurgeItems(IIdentity arg0, String arg1, IMethodCallback callback) throws XMPPError, CommunicationException {
        Dbc.require("Method callback cannot be null", null != callback);
        Dbc.invariant("Method currently unsupported", false);
    }

    /* (non-Javadoc)
     * @see org.societies.android.api.pubsub.IPubsubClient#ownerSetAffiliations(org.societies.api.identity.IIdentity, java.lang.String, java.util.Map)
     */
    @Override
    public void ownerSetAffiliations(IIdentity pubsubService, String node, Map<IIdentity, Affiliation> affiliations, IMethodCallback callback) throws XMPPError, CommunicationException {
        Dbc.require("Method callback cannot be null", null != callback);
        Dbc.invariant("Method currently unsupported", false);
    }

    @Override
    public void ownerSetSubscriptions(IIdentity arg0, String arg1, Map<IIdentity, SubscriptionState> arg2, IMethodCallback callback) throws XMPPError, CommunicationException {
        Dbc.require("Method callback cannot be null", null != callback);
        Dbc.invariant("Method currently unsupported", false);
    }

    @Override
    public void publisherDelete(IIdentity arg0, String arg1, String arg2, IMethodCallback callback) throws XMPPError, CommunicationException {
        Dbc.require("Method callback cannot be null", null != callback);
        Dbc.invariant("Method currently unsupported", false);
    }

    @Override
    public String publisherPublish(IIdentity pubsubServiceID, String node, String itemID, Object payload, IMethodCallback callback) throws XMPPError, CommunicationException {
        Dbc.require("Pubsub identity cannot be null", null != pubsubServiceID);
        Dbc.require("Pubsub node must be specified", null != node && node.length() > 0);
        Dbc.require("Pubsub event identity must be specified", null != itemID && itemID.length() > 0);
        Dbc.require("Pubsub event payload must be specified", null != payload);
        Dbc.require("Method callback cannot be null", null != callback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "publisherPublish called for node: " + node);
        }

        final String pubsubServiceJid = pubsubServiceID.getJid();
        synchronized (this.methodCallbackMap) {
            long remoteCallID = this.randomGenerator.nextLong();
            this.methodCallbackMap.put(remoteCallID, callback);

            final String itemXml;

            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                this.serializer.write(payload, os);
                itemXml = os.toString();
                if (DEBUG_LOGGING) {
                    Log.d(LOG_TAG, "Serialised event payload: " + itemXml);
                }
            } catch (TransformerException e) {
                throw new CommunicationException(e.getMessage(), e);
            } catch (ParserConfigurationException e) {
                throw new CommunicationException("ParserConfigurationException while marshalling item to publish", e);
            } catch (Exception e) {
                throw new CommunicationException("Exception while marshalling item to publish", e);
            }

            InvokePublisherPublish invoker = new InvokePublisherPublish(this.clientPackageName, pubsubServiceJid, node, itemID, itemXml, remoteCallID);
            invoker.execute();
        }
        return null;
    }

    @Override
    public List<Element> subscriberRetrieveLast(IIdentity arg0, String arg1, String arg2, IMethodCallback callback) throws XMPPError, CommunicationException {
        Dbc.require("Method callback cannot be null", null != callback);
        Dbc.invariant("Method currently unsupported", false);
        return null;
    }

    @Override
    public List<Element> subscriberRetrieveSpecific(IIdentity arg0, String arg1, String arg2, List<String> arg3, IMethodCallback callback) throws XMPPError,
            CommunicationException {
        Dbc.require("Method callback cannot be null", null != callback);
        Dbc.invariant("Method currently unsupported", false);
        return null;
    }

    @Override
    public boolean subscriberSubscribe(IIdentity pubsubServiceID, String node, IMethodCallback methodCallback) throws XMPPError, CommunicationException {
        Dbc.require("Pubsub identity cannot be null", null != pubsubServiceID);
        Dbc.require("Pubsub node must be specified", null != node && node.length() > 0);
        Dbc.require("Method callback cannot be null", null != methodCallback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "subscriberSubscribe called for node: " + node);
        }

        final String pubsubServiceJid = pubsubServiceID.getJid();
        synchronized (this.methodCallbackMap) {
            long remoteCallID = this.randomGenerator.nextLong();
            this.methodCallbackMap.put(remoteCallID, methodCallback);
            InvokeSubscriberSubscribe invoker = new InvokeSubscriberSubscribe(this.clientPackageName, pubsubServiceJid, node, remoteCallID);
            invoker.execute();
        }
        return false;
    }

    @Override
    public boolean subscriberUnsubscribe(IIdentity pubsubServiceID, String node, IMethodCallback methodCallback) throws XMPPError, CommunicationException {
        Dbc.require("Pubsub identity cannot be null", null != pubsubServiceID);
        Dbc.require("Pubsub node must be specified", null != node && node.length() > 0);
        Dbc.require("Method callback cannot be null", null != methodCallback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "subscriberUnsubscribe called for node: " + node);
        }

        final String pubsubServiceJid = pubsubServiceID.getJid();
        synchronized (this.methodCallbackMap) {
            long remoteCallID = this.randomGenerator.nextLong();
            this.methodCallbackMap.put(remoteCallID, methodCallback);
            InvokeSubscriberUnSubscribe invoker = new InvokeSubscriberUnSubscribe(this.clientPackageName, pubsubServiceJid, node, remoteCallID);
            invoker.execute();
        }

        return false;
    }

    /**
     * Transform the raw XMPP Pubsub node event message into an event payload object
     * usable by the client
     *
     * @param intent
     * @return
     * @throws Exception
     */
    private PubsubNodePayload getEventPayload(Intent intent) throws Exception {
        PubsubNodePayload returnValue = null;
        Packet packet = PubsubHelper.this.marshaller.unmarshallMessage(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
        Object xmppObject = PubsubHelper.this.marshaller.unmarshallPayload(packet);

        if (xmppObject instanceof org.jabber.protocol.pubsub.event.Event) {
            org.jabber.protocol.pubsub.event.Items items = ((org.jabber.protocol.pubsub.event.Event) xmppObject).getItems();

            String node = items.getNode();
            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "Node of event is: " + node);
                Log.d(LOG_TAG, "Number of items: " + items.getItem().size());
            }

            org.jabber.protocol.pubsub.event.Item i = items.getItem().get(0); // TODO assume only one item per notification

            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "ID: " + i.getId());
            }

            String xmlPayload = MarshallUtils.nodeToString((Element) i.getAny());
            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "Event XML: " + xmlPayload);
            }

            Class<?> c = elementToClass.get(getElementIdentifier(xmlPayload));
            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "Class: " + c.getName());
            }
            Object bean = serializer.read(c, new ByteArrayInputStream(xmlPayload.getBytes()));
            returnValue = new PubsubNodePayload(bean, i.getId(), node);

        }
        return returnValue;
    }

    private static String getElementIdentifier(String item) {
        String trimmedItem = item.trim();
        String elementName = trimmedItem.substring(1, trimmedItem.indexOf(" "));
        String nsStr = trimmedItem.substring(trimmedItem.indexOf("xmlns=") + 7);
        int endIndex = nsStr.indexOf("\"");
        if (endIndex < 0)
            endIndex = nsStr.indexOf("'");
        nsStr = nsStr.substring(0, endIndex);

        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Element identifier: " + "{" + nsStr + "}" + elementName);
        }
        return "{" + nsStr + "}" + elementName;
    }


    /**
     * Bind to remote Android Pubsub Service
     */
    private void bindToPubsubService() {
        Intent serviceIntent = new Intent(SERVICE_ACTION);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Bind to Societies Android Pubsub Service");
        }
        this.androidContext.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Unbind from remote Android Pubsub service
     */
    private void unBindService() {
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Unbind from Societies Android Pubsub Service");
        }
        if (this.boundToService) {
            this.androidContext.unbindService(serviceConnection);
        }
    }

    /**
     * Create Service Connection to remote service. Assumes that XMPP login and configuration
     * has taken place
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            PubsubHelper.this.boundToService = false;
            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "Societies Android Pubsub Service disconnected");
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PubsubHelper.this.boundToService = true;
            targetService = new Messenger(service);
            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "Societies Android Pubsub Service connected");
            }
            synchronized (PubsubHelper.this.methodCallbackMap) {
                long remoteCallID = PubsubHelper.this.randomGenerator.nextLong();
                PubsubHelper.this.methodCallbackMap.put(remoteCallID, null);
                InvokeBindToAndroidComms invoker = new InvokeBindToAndroidComms(PubsubHelper.this.clientPackageName, remoteCallID);
                invoker.execute();
            }
        }
    };


    /**
     * Create a broadcast receiver
     *
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Set up broadcast receiver");
        }

        this.receiver = new PubsubHelperReceiver();
        this.androidContext.registerReceiver(this.receiver, createTestIntentFilter());
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Register broadcast receiver");
        }

        return receiver;
    }

    /**
     * Unregister the broadcast receiver
     */
    private void teardownBroadcastReceiver() {
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Tear down broadcast receiver");
        }
        this.androidContext.unregisterReceiver(this.receiver);
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     * Essentially this receiver invokes callbacks for relevant intents received from Android Pubsub.
     * Since more than one instance of this class can exist for an app, i.e. more than one component could be communicating,
     * callback IDs cannot be assumed to exist for a particular Broadcast receiver.
     */
    private class PubsubHelperReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "Received action: " + intent.getAction());
                Log.d(LOG_TAG, "Received action CALL_ID_KEY: " + intent.getLongExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY, 0));
            }
            long callbackId = intent.getLongExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY, 0);

            if (intent.getAction().equals(IPubsubService.BIND_TO_ANDROID_COMMS)) {
                synchronized (PubsubHelper.this.methodCallbackMap) {
                    PubsubHelper.this.methodCallbackMap.remove(callbackId);
                    PubsubHelper.this.bindCallback.returnAction(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
                }
            } else if (intent.getAction().equals(IPubsubService.UNBIND_FROM_ANDROID_COMMS)) {
                synchronized (PubsubHelper.this.methodCallbackMap) {
                    PubsubHelper.this.methodCallbackMap.remove(callbackId);
                    synchronized (PubsubHelper.this.xmppCallbackMap) {
                        if (PubsubHelper.this.methodCallbackMap.isEmpty() && PubsubHelper.this.xmppCallbackMap.isEmpty()) {
                            PubsubHelper.this.teardownBroadcastReceiver();
                            unBindService();
                        } else {
                            if (DEBUG_LOGGING) {
                                Log.d(LOG_TAG, "Methodcallback entries: " + PubsubHelper.this.methodCallbackMap.size());
                                Log.d(LOG_TAG, "XmppCallbackMap entries: " + PubsubHelper.this.xmppCallbackMap.size());
                            }
                        }
                    }

                    PubsubHelper.this.bindCallback.returnAction(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
                }
            } else if (intent.getAction().equals(IPubsubService.SUBSCRIBER_SUBSCRIBE)) {
                synchronized (PubsubHelper.this.methodCallbackMap) {
                    IMethodCallback callback = PubsubHelper.this.methodCallbackMap.get(callbackId);
                    PubsubHelper.this.methodCallbackMap.remove(callbackId);
                    callback.returnAction(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
                }
            } else if (intent.getAction().equals(IPubsubService.SUBSCRIBER_UNSUBSCRIBE)) {
                synchronized (PubsubHelper.this.methodCallbackMap) {
                    IMethodCallback callback = PubsubHelper.this.methodCallbackMap.get(callbackId);
                    PubsubHelper.this.methodCallbackMap.remove(callbackId);
                    callback.returnAction(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
                }
            } else if (intent.getAction().equals(XMPPAgent.PUBSUB_EVENT)) {
                PubsubNodePayload payload;
                try {
                    payload = PubsubHelper.this.getEventPayload(intent);
                    PubsubHelper.this.subscriberCallback.pubsubEvent(null, payload.getPubsubNode(), payload.getEventId(), payload.getPayload());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (intent.getAction().equals(IPubsubService.OWNER_CREATE)) {
                synchronized (PubsubHelper.this.methodCallbackMap) {
                    IMethodCallback callback = PubsubHelper.this.methodCallbackMap.get(callbackId);
                    PubsubHelper.this.methodCallbackMap.remove(callbackId);
                    callback.returnAction(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
                }
            } else if (intent.getAction().equals(IPubsubService.OWNER_DELETE)) {
                synchronized (PubsubHelper.this.methodCallbackMap) {
                    IMethodCallback callback = PubsubHelper.this.methodCallbackMap.get(callbackId);
                    PubsubHelper.this.methodCallbackMap.remove(callbackId);
                    callback.returnAction(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
                }
            } else if (intent.getAction().equals(IPubsubService.PUBLISHER_PUBLISH)) {
                synchronized (PubsubHelper.this.methodCallbackMap) {
                    IMethodCallback callback = PubsubHelper.this.methodCallbackMap.get(callbackId);
                    PubsubHelper.this.methodCallbackMap.remove(callbackId);
                    callback.returnAction(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
                }
            }
        }
    }

    /**
     * Create a suitable intent filter
     *
     * @return IntentFilter
     */
    private static IntentFilter createTestIntentFilter() {
        //register broadcast receiver to receive SocietiesEvents return values
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(IPubsubService.BIND_TO_ANDROID_COMMS);
        intentFilter.addAction(IPubsubService.UNBIND_FROM_ANDROID_COMMS);
        intentFilter.addAction(IPubsubService.DISCO_ITEMS);
        intentFilter.addAction(IPubsubService.OWNER_CREATE);
        intentFilter.addAction(IPubsubService.OWNER_DELETE);
        intentFilter.addAction(IPubsubService.OWNER_PURGE_ITEMS);
        intentFilter.addAction(IPubsubService.PUBLISHER_DELETE);
        intentFilter.addAction(IPubsubService.PUBLISHER_PUBLISH);
        intentFilter.addAction(IPubsubService.SUBSCRIBER_SUBSCRIBE);
        intentFilter.addAction(IPubsubService.SUBSCRIBER_UNSUBSCRIBE);
        intentFilter.addAction(XMPPAgent.PUBSUB_EVENT);

        return intentFilter;
    }

    /**
     * Async task to invoke Pubsub Bind to Android Comms
     */
    private class InvokeBindToAndroidComms extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeBindToAndroidComms.class.getName();
        private String client;
        private long remoteID;

        /**
         * Default Constructor
         *
         * @param client
         * @param remoteID
         */
        public InvokeBindToAndroidComms(String client, long remoteID) {
            this.client = client;
            this.remoteID = remoteID;
        }

        @Override
        protected Void doInBackground(Void... args) {

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Executing InvokeBindToAndroidComms");
            }
            String targetMethod = IPubsubService.methodsArray[8];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IPubsubService.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
            /*
             * By passing the client package name to the service, the service can modify its broadcast intent so that
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.remoteID);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteID);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Pubsub Service: " + targetMethod);
            }


            try {
                PubsubHelper.this.targetService.send(outMessage);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke Pubsub UnBind to Android Comms
     */
    private class InvokeUnBindToAndroidComms extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeUnBindToAndroidComms.class.getName();
        private String client;
        private long remoteID;

        /**
         * Default Constructor
         *
         * @param client
         * @param remoteID
         */
        public InvokeUnBindToAndroidComms(String client, long remoteID) {
            this.client = client;
            this.remoteID = remoteID;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = IPubsubService.methodsArray[9];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IPubsubService.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
            /*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.remoteID);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteID);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Pubsub Service: " + targetMethod);
            }

            try {
                PubsubHelper.this.targetService.send(outMessage);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }


    /**
     * Async task to invoke Pubsub node subscription
     */
    private class InvokeSubscriberSubscribe extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeSubscriberSubscribe.class.getName();
        private String client;
        private long remoteID;
        private String pubsubServiceJid;
        private String node;

        /**
         * Default Constructor
         *
         * @param client
         * @param pubsubServiceJid
         * @param node
         * @param remoteID
         */
        public InvokeSubscriberSubscribe(String client, String pubsubServiceJid, String node, long remoteID) {
            this.client = client;
            this.pubsubServiceJid = pubsubServiceJid;
            this.node = node;
            this.remoteID = remoteID;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = IPubsubService.methodsArray[6];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IPubsubService.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.pubsubServiceJid);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Pubsub service JID: " + this.pubsubServiceJid);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), this.node);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Pubsub node: " + this.node);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), this.remoteID);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteID);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Pubsub Service: " + targetMethod);
            }

            try {
                PubsubHelper.this.targetService.send(outMessage);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke Pubsub node unsubscription
     */
    private class InvokeSubscriberUnSubscribe extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeSubscriberUnSubscribe.class.getName();
        private String client;
        private long remoteID;
        private String pubsubServiceJid;
        private String node;

        /**
         * Default Constructor
         *
         * @param client
         * @param pubsubServiceJid
         * @param node
         * @param remoteID
         */
        public InvokeSubscriberUnSubscribe(String client, String pubsubServiceJid, String node, long remoteID) {
            this.client = client;
            this.pubsubServiceJid = pubsubServiceJid;
            this.node = node;
            this.remoteID = remoteID;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = IPubsubService.methodsArray[7];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IPubsubService.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.pubsubServiceJid);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Pubsub service JID: " + this.pubsubServiceJid);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), this.node);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Pubsub node: " + this.node);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), this.remoteID);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteID);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Pubsub Service: " + targetMethod);
            }


            try {
                PubsubHelper.this.targetService.send(outMessage);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke Pubsub node create
     */
    private class InvokeOwnerCreate extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeOwnerCreate.class.getName();
        private String client;
        private long remoteID;
        private String pubsubServiceJid;
        private String node;

        /**
         * Default Constructor
         *
         * @param client
         * @param pubsubServiceJid
         * @param node
         * @param remoteID
         */
        public InvokeOwnerCreate(String client, String pubsubServiceJid, String node, long remoteID) {
            this.client = client;
            this.pubsubServiceJid = pubsubServiceJid;
            this.node = node;
            this.remoteID = remoteID;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = IPubsubService.methodsArray[1];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IPubsubService.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.pubsubServiceJid);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Pubsub service JID: " + this.pubsubServiceJid);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), this.node);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Pubsub node: " + this.node);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), this.remoteID);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteID);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Pubsub Service: " + targetMethod);
            }

            try {
                PubsubHelper.this.targetService.send(outMessage);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke Pubsub node delete
     */
    private class InvokeOwnerDelete extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeOwnerDelete.class.getName();
        private String client;
        private long remoteID;
        private String pubsubServiceJid;
        private String node;

        /**
         * Default Constructor
         *
         * @param client
         * @param pubsubServiceJid
         * @param node
         * @param remoteID
         */
        public InvokeOwnerDelete(String client, String pubsubServiceJid, String node, long remoteID) {
            this.client = client;
            this.pubsubServiceJid = pubsubServiceJid;
            this.node = node;
            this.remoteID = remoteID;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = IPubsubService.methodsArray[2];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IPubsubService.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.pubsubServiceJid);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Pubsub service JID: " + this.pubsubServiceJid);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), this.node);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Pubsub node: " + this.node);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), this.remoteID);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteID);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Pubsub Service: " + targetMethod);
            }

            try {
                PubsubHelper.this.targetService.send(outMessage);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke Pubsub node publish
     */
    private class InvokePublisherPublish extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokePublisherPublish.class.getName();
        private String client;
        private long remoteID;
        private String pubsubServiceJid;
        private String node;
        private String payload;
        private String eventID;

        /**
         * Default Constructor
         *
         * @param client
         * @param pubsubServiceJid
         * @param node
         * @param remoteID
         */
        public InvokePublisherPublish(String client, String pubsubServiceJid, String node, String eventID, String payload, long remoteID) {
            this.client = client;
            this.pubsubServiceJid = pubsubServiceJid;
            this.node = node;
            this.remoteID = remoteID;
            this.payload = payload;
            this.eventID = eventID;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = IPubsubService.methodsArray[4];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IPubsubService.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.pubsubServiceJid);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Pubsub service JID: " + this.pubsubServiceJid);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), this.node);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Pubsub node: " + this.node);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), this.eventID);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Pubsub event ID: " + this.eventID);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 4), this.payload);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Pubsub event payload: " + this.payload);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 5), this.remoteID);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteID);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Pubsub Service: " + targetMethod);
            }

            try {
                PubsubHelper.this.targetService.send(outMessage);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }

}
