package org.societies.android.platform.comms.helper;

import android.content.*;
import android.os.*;
import android.util.Log;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.XMPPAgent;
import org.societies.android.api.comms.xmpp.*;
import org.societies.android.api.services.ICoreSocietiesServices;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.platform.androidutils.PacketMarshaller;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.identity.IdentityManagerImpl;
import org.societies.utilities.DBC.Dbc;

import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings({"JavaDoc", "ConstantConditions", "UnusedDeclaration"})
public class ClientCommunicationMgr {

    private static final String LOG_TAG = ClientCommunicationMgr.class.getCanonicalName();
    private static final boolean DEBUG_LOGGING = true;
    private boolean boundToService;
    private Messenger targetService = null;
    private String clientPackageName;
    private Random randomGenerator;

    private Context androidContext;
    private PacketMarshaller marshaller = new PacketMarshaller();
    private final Map<Long, ICommCallback> xmppCallbackMap;
    private final Map<Long, IMethodCallback> methodCallbackMap;

    private String identityJID;
    private String domainAuthority;
    private IIdentityManager idManager;

    private boolean loginCompleted;
    private BroadcastReceiver receiver;
    private IMethodCallback bindCallback;

    /**
     * Default constructor
     *
     * @param androidContext
     * @param loginCompleted true if XMPP login has taken place
     */
    public ClientCommunicationMgr(Context androidContext, boolean loginCompleted) {
        Dbc.require("Android context must be supplied", null != androidContext);

        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Instantiate ClientCommunicationMgr");
        }
        this.androidContext = androidContext;

        this.clientPackageName = this.androidContext.getApplicationContext().getPackageName();

        this.randomGenerator = new Random(System.currentTimeMillis());

        this.xmppCallbackMap = Collections.synchronizedMap(new HashMap<Long, ICommCallback>());
        this.methodCallbackMap = Collections.synchronizedMap(new HashMap<Long, IMethodCallback>());

        this.identityJID = null;
        this.domainAuthority = null;
        this.idManager = null;
        this.loginCompleted = loginCompleted;
        this.receiver = null;
    }

    public boolean isLoginCompleted() {
        return loginCompleted;
    }

    public void setLoginCompleted(boolean loginCompleted) {
        this.loginCompleted = loginCompleted;
    }

    /**
     * Binds to Android Comms Service
     *
     * @param bindCallback callback
     */
    public void bindCommsService(IMethodCallback bindCallback) {
        Dbc.require("Service Bind Callback cannot be null", null != bindCallback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Bind to Android Comms Service");
        }

        this.setupBroadcastReceiver();

        this.bindCallback = bindCallback;

        if (this.loginCompleted) {
            if (!this.bindToServiceAfterLogin()) {
                this.bindCallback.returnException("Societies Android Comms Service not on device");
            }
        } else {
            if (!this.bindToServiceBeforeLogin()) {
                this.bindCallback.returnException("Societies Android Comms Service not on device");
            }
        }

    }

    /**
     * Unbinds from the Android Comms service
     *
     * @return true if no more requests queued
     */
    public boolean unbindCommsService() {
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Unbind from Android Comms Service");
        }
        boolean retValue = false;
        synchronized (this.methodCallbackMap) {
            synchronized (this.xmppCallbackMap) {
                if (this.methodCallbackMap.isEmpty() && this.xmppCallbackMap.isEmpty()) {
                    this.teardownBroadcastReceiver();
                    unBindService();
                    retValue = true;
                } else {
                    if (DEBUG_LOGGING) {
                        Log.d(LOG_TAG, "Methodcallback entries: " + this.methodCallbackMap.size());
                        Log.d(LOG_TAG, "XmppCallbackMap entries: " + this.xmppCallbackMap.size());
                    }
                }
            }
        }

        return retValue;
    }

    public void register(List<String> elementNames, List<String> namespaces, List<String> packages, IMethodCallback callback) {
        Dbc.require("Message Beans must be specified", null != elementNames && elementNames.size() > 0);
        Dbc.require("Message Beans must be specified", null != namespaces && namespaces.size() > 0);
        Dbc.require("Message Beans must be specified", null != packages && packages.size() > 0);
        Dbc.require("Callback object must be supplied", null != callback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Register XMPP data namespace attributes");
        }

        long callbackID = this.randomGenerator.nextLong();

        synchronized (this.methodCallbackMap) {
            //store callback in order to activate required methods
            this.methodCallbackMap.put(callbackID, callback);
        }


//		for (String element : elementNames) {
//			Log.d(LOG_TAG, "register element: " + element);
//		}

        marshaller.register(elementNames, namespaces, packages);

        InvokeRegister invoke = new InvokeRegister(this.clientPackageName, elementNames, namespaces, callbackID);
        invoke.execute();

    }

    public void unregister(final List<String> elementNames, final List<String> namespaces, final IMethodCallback callback) {
        Dbc.require("Message Beans must be specified", null != elementNames && elementNames.size() > 0);
        Dbc.require("Callback object must be supplied", null != callback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "unregister");
        }

        long callbackID = this.randomGenerator.nextLong();

        synchronized (this.methodCallbackMap) {
            //store callback in order to activate required methods
            this.methodCallbackMap.put(callbackID, callback);
        }
//		if (DEBUG_LOGGING) {
//			for (String element : elementNames) {
//			Log.d(LOG_TAG, "unregister element: " + element);
//		}
//		}

        InvokeUnRegister invoke = new InvokeUnRegister(this.clientPackageName, elementNames, namespaces, callbackID);
        invoke.execute();

    }

    public boolean UnRegisterCommManager(IMethodCallback callback) {
        Dbc.require("Method callback object must be specified", null != callback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "UnRegisterCommManager");
        }

        long callbackID = this.randomGenerator.nextLong();

        synchronized (this.methodCallbackMap) {
            //store callback in order to activate required methods
            this.methodCallbackMap.put(callbackID, callback);
        }

        InvokeUnRegisterCommManager invoke = new InvokeUnRegisterCommManager(this.clientPackageName, callbackID);
        invoke.execute();

        return false;
    }

    /**
     * Send an XMPP message
     *
     * @param stanza
     * @param type
     * @param payload
     * @throws CommunicationException
     */
    public void sendMessage(Stanza stanza, Message.Type type, Object payload) throws CommunicationException {
        Dbc.require("Stanza must be specified", null != stanza);
        Dbc.require("Type must be specified", null != type);
        Dbc.require("Payload must be specified", null != payload);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Send Message");
        }

        try {
            stanza.setFrom(getIdManager().getThisNetworkNode());
            String xml = marshaller.marshallMessage(stanza, type, payload);
            sendMessage(xml);
        } catch (Exception e) {
            throw new CommunicationException("Error sending message", e);
        }
    }

    public void sendMessage(Stanza stanza, Object payload) throws CommunicationException {
        Dbc.require("Stanza must be specified", null != stanza);
        Dbc.require("Payload must be specified", null != payload);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Send Message");
        }

        try {
            stanza.setFrom(getIdManager().getThisNetworkNode());

            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "sendMessage stanza from : " + stanza.getFrom() + " to: " + stanza.getTo());
            }

            sendMessage(stanza, Message.Type.normal, payload);
        } catch (Exception e) {
            throw new CommunicationException(e.getMessage(), e);
        }
    }

    public void sendIQ(Stanza stanza, IQ.Type type, Object payload, ICommCallback callback) throws CommunicationException {
        Dbc.require("Stanza must be specified", null != stanza);
        Dbc.require("IQ Type must be specified", null != type);
        Dbc.require("Payload must be specified", null != payload);
        Dbc.require("Callback object must be supplied", null != callback);

        long callbackID = this.randomGenerator.nextLong();

        synchronized (this.xmppCallbackMap) {
            //store callback in order to activate required methods
            this.xmppCallbackMap.put(callbackID, callback);
        }

        try {
            stanza.setFrom(getIdManager().getThisNetworkNode());

            String xml = marshaller.marshallIQ(stanza, type, payload);

            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, String.format("sendIQ: type=[%s], from=[%s], to=[%s]\n    stanza=[%s]\n    payload=[%s]",
                        type.toString(),
                        stanza.getFrom(),
                        stanza.getTo(),
                        stanza.toString(),
                        payload.toString()));
                Log.d(LOG_TAG, "XML=" + xml);
            }

            sendIQ(xml, callbackID);

        } catch (Exception e) {
            throw new CommunicationException("Error sending IQ message", e);
        }
    }

    /**
     * Get a user's XMPP identity. Can be called synchronously.
     *
     * @return identity or null if identity not available
     * @throws InvalidFormatException
     */
    public IIdentity getIdentity() throws InvalidFormatException {
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "getIdentity");
        }

        IIdentity returnIdentity = null;

        if (null != this.identityJID) {
            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "getIdentity identity: " + this.identityJID);
            }
            returnIdentity = IdentityManagerImpl.staticfromJid(this.identityJID);
        }
        return returnIdentity;
    }

    /**
     * Get the Identity Manager. Can be called synchronously.
     *
     * @return IIdentityManager or null if identity not available
     * @throws InvalidFormatException
     */
    public IIdentityManager getIdManager() throws InvalidFormatException {
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "getIdManager");
        }

        if (null == this.idManager && null != this.domainAuthority && null != this.identityJID) {
            this.idManager = createIdentityManager(this.identityJID, this.domainAuthority);
        }
        return this.idManager;
    }

    /**
     * Get XMPP items
     *
     * @param entity
     * @param node
     * @param callback
     * @return
     * @throws CommunicationException
     */
    public String getItems(final IIdentity entity, final String node, final ICommCallback callback) throws CommunicationException {
        Dbc.require("Entity cannot be null", null != entity);
//		Dbc.require("Node must be specified", null != node && node.length() > 0);
        Dbc.require("Callback object must be supplied", null != callback);

        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "getItems for entity: " + entity + " and node " + node);
        }

        long callbackID = this.randomGenerator.nextLong();

        synchronized (this.xmppCallbackMap) {
            //store callback in order to activate required methods
            this.xmppCallbackMap.put(callbackID, callback);
        }

        InvokeGetItems invoke = new InvokeGetItems(this.clientPackageName, entity.getJid(), node, callbackID);
        invoke.execute();

        return null;
    }


    private void sendMessage(final String xml) {

        InvokeSendMessage invoke = new InvokeSendMessage(this.clientPackageName, xml);
        invoke.execute();

    }

    private void sendIQ(final String xml, long remoteCallID) {

        InvokeSendIQ invoke = new InvokeSendIQ(this.clientPackageName, xml, remoteCallID);
        invoke.execute();

    }


    private String getIdentityJid(long callbackID) {
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "getIdentityJid");
        }

        InvokeGetIdentityJid invoke = new InvokeGetIdentityJid(this.clientPackageName, callbackID);
        invoke.execute();

        return null;
    }

    private String getDomainAuthorityNode(long callbackID) {
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "getDomainAuthorityNode");
        }

        InvokeGetDomainAuthorityNode invoke = new InvokeGetDomainAuthorityNode(this.clientPackageName, callbackID);
        invoke.execute();

        return null;
    }

    public boolean isConnected(IMethodCallback callback) {
        Dbc.require("Method callback object must be specified", null != callback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "isConnected");
        }

        long callbackID = this.randomGenerator.nextLong();

        synchronized (this.methodCallbackMap) {
            //store callback in order to activate required methods
            this.methodCallbackMap.put(callbackID, callback);
            Dbc.ensure("Callback has to be added to map", this.methodCallbackMap.containsKey(callbackID));
        }

        InvokeIsConnected invoke = new InvokeIsConnected(this.clientPackageName, callbackID);
        invoke.execute();

        return false;
    }

    public INetworkNode newMainIdentity(final String identifier, final String domain, final String password, IMethodCallback callback, String host) throws XMPPError {
        Dbc.require("User identifier must be specified", null != identifier && identifier.length() > 0);
        Dbc.require("Domain must be specified", null != domain && domain.length() > 0);
        Dbc.require("User password must be specified", null != password && password.length() > 0);
        Dbc.require("Method callback object must be specified", null != callback);

        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "newMainIdentity domain: " + domain + " identifier: " + identifier + " password: " + password + " host: " + host);
        }

        long callbackID = this.randomGenerator.nextLong();

        synchronized (this.methodCallbackMap) {
            //store callback in order to activate required methods
            this.methodCallbackMap.put(callbackID, callback);
        }

        InvokeNewMainIdentity invoke = new InvokeNewMainIdentity(this.clientPackageName, identifier, domain, password, callbackID, host);
        invoke.execute();

        return null;
    }

    /**
     * Log a user into a designated XMPP server. Provides for case where login only knows the XMPP server DNS name
     *
     * @param identifier User name
     * @param domain     XMPP server (DNS name)
     * @param password   User password
     * @param callback   Callback to allow asynchronous update
     * @return null Return value delivered by callback
     */
    public INetworkNode login(final String identifier, final String domain, final String password, IMethodCallback callback) {
        this.login(identifier, domain, password, null, callback);
        return null;
    }

    /**
     * Log a user into a designated XMPP server. Provides for case where login knows the IP address of the XMPP server. Useful for testing purposes.
     *
     * @param identifier User name
     * @param domain     XMPP server (DNS name)
     * @param password   User password
     * @param host       XMPP server IP address (optional). If supplied overrides DNS lookup
     * @param callback   Callback to allow asynchronous update
     * @return null Return value delivered by callback
     */
    public INetworkNode login(final String identifier, final String domain, final String password, String host, IMethodCallback callback) {
        Dbc.require("User identifier must be specified", null != identifier && identifier.length() > 0);
        Dbc.require("Domain must be specified", null != domain && domain.length() > 0);
        Dbc.require("User password must be specified", null != password && password.length() > 0);
        Dbc.require("Method callback object must be specified", null != callback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "login domain: " + domain + " identifier: " + identifier + " password: " + password);
        }

        long callbackID = this.randomGenerator.nextLong();

        synchronized (this.methodCallbackMap) {
            //store callback in order to activate required methods
            this.methodCallbackMap.put(callbackID, callback);
        }

        InvokeLogin invoke = new InvokeLogin(this.clientPackageName, identifier, domain, password, host, callbackID);
        invoke.execute();

        return null;
    }


    public boolean logout(IMethodCallback callback) {
        Dbc.require("Method callback object must be specified", null != callback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "logout");
        }

        long callbackID = this.randomGenerator.nextLong();

        synchronized (this.methodCallbackMap) {
            //store callback in order to activate required methods
            this.methodCallbackMap.put(callbackID, callback);
        }

        InvokeLogout invoke = new InvokeLogout(this.clientPackageName, callbackID);
        invoke.execute();

        return false;
    }

    public boolean destroyMainIdentity(IMethodCallback callback) {
        Dbc.require("Method callback object must be specified", null != callback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "destroyMainIdentity");
        }

        long callbackID = this.randomGenerator.nextLong();

        synchronized (this.methodCallbackMap) {
            //store callback in order to activate required methods
            this.methodCallbackMap.put(callbackID, callback);
        }

        InvokeDestroyMainIdentity invoke = new InvokeDestroyMainIdentity(this.clientPackageName, callbackID);
        invoke.execute();

        return false;
    }

    public void configureAgent(String domainAuthorityNode, int xmppPort, String resource, boolean debug, IMethodCallback callback) {
        Dbc.require("Domain Authority Node must be specified", null != domainAuthorityNode && domainAuthorityNode.length() > 0);
        Dbc.require("XMPP Port must be greater than zero", xmppPort > 0);
        Dbc.require("JID resource must be specified", null != resource && resource.length() > 0);
        Dbc.require("Method callback object must be specified", null != callback);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "configureAgent");
        }

        long callbackID = this.randomGenerator.nextLong();

        synchronized (this.methodCallbackMap) {
            //store callback in order to activate required methods
            this.methodCallbackMap.put(callbackID, callback);
        }

        InvokeConfigureAgent invoke = new InvokeConfigureAgent(this.clientPackageName, domainAuthorityNode, xmppPort, resource, debug, callbackID);
        invoke.execute();
    }

    public void setVCard(VCardParcel vCard) {
        int targetMethodInt = 15;
        String targetMethodSignature = XMPPAgent.methodsArray[targetMethodInt];

        android.os.Message outMessage = android.os.Message.obtain(null, targetMethodInt, 0, 0);
        Bundle outBundle = new Bundle();

        //CLIENT PARAM
        outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethodSignature, 0), this.clientPackageName);
        if (DEBUG_LOGGING) Log.d(LOG_TAG, "Client Package Name: " + this.clientPackageName);
        //VCARD PARAM
        outBundle.putParcelable(ServiceMethodTranslator.getMethodParameterName(targetMethodSignature, 1), vCard);
        if (DEBUG_LOGGING) Log.d(LOG_TAG, "VCard added: " + vCard.getJabberId());

        outMessage.setData(outBundle);
        if (DEBUG_LOGGING) Log.d(LOG_TAG, "Call Societies Android Comms Service: " + targetMethodSignature);
        try {
            targetService.send(outMessage);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public VCardParcel getVCard(ICommCallback callback) {
        try {
            //MANAGE CALLBACK
            long callbackID = this.randomGenerator.nextLong();
            synchronized (this.xmppCallbackMap) {
                //store callback in order to activate required methods
                this.xmppCallbackMap.put(callbackID, callback);
            }
            InvokeVCard invoke = new InvokeVCard(this.clientPackageName, callbackID);
            String[] params = new String[]{XMPPAgent.GET_VCARD};
            invoke.execute(params);

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public VCardParcel getVCard(String userId, ICommCallback callback) {
        Dbc.require("userID Param must be specified", null != userId);
        try {
            //CONVERT TO USER ACCOUNT IF PARAM WAS EXTERNAL COMPONENT
            IIdentity userIdentity = this.getIdManager().fromJid(userId);
            userId = userIdentity.getIdentifier() + "@" + userIdentity.getDomain();

            //MANAGE CALLBACK
            long callbackID = this.randomGenerator.nextLong();
            synchronized (this.xmppCallbackMap) {
                //store callback in order to activate required methods
                this.xmppCallbackMap.put(callbackID, callback);
            }

            InvokeVCard invoke = new InvokeVCard(this.clientPackageName, callbackID);
            String[] params = new String[]{XMPPAgent.GET_USER_VCARD, userId};
            invoke.execute(params);

        } catch (InvalidFormatException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private static IIdentityManager createIdentityManager(String thisNode, String daNode) throws InvalidFormatException {
        IIdentityManager idManager;
        if (daNode == null)
            idManager = new IdentityManagerImpl(thisNode);
        else
            idManager = new IdentityManagerImpl(thisNode, daNode);
        return idManager;
    }

    /**
     * Create a broadcast receiver
     *
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Set up broadcast receiver");
        }

        this.receiver = new CCSMgrMainReceiver();
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
     * Essentially this receiver invokes callbacks for relevant intents received from Android Communications.
     * Since more than one instance of this class can exist for an app, i.e. more than one component could be communicating,
     * callback IDs cannot be assumed to exist for a particular Broadcast receiver.
     */
    private class CCSMgrMainReceiver extends BroadcastReceiver {
        private final String LOG_TAG = CCSMgrMainReceiver.class.getCanonicalName();

        @Override
        public void onReceive(Context context, Intent intent) {
//            if (DEBUG_LOGGING) {
//                Log.d(LOG_TAG, "Received action: " + intent.getAction());
//                Log.d(LOG_TAG, "Received action CALL_ID_KEY: " + intent.getLongExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY, 0));
//            }
            long callbackId = intent.getLongExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY, 0);

            if (intent.getAction().equals(XMPPAgent.IS_CONNECTED)) {
                synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                    IMethodCallback callback = ClientCommunicationMgr.this.methodCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.methodCallbackMap.remove(callbackId);
                        callback.returnAction(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
                    }
                }

            } else if (intent.getAction().equals(XMPPAgent.GET_IDENTITY)) {
                if (ClientCommunicationMgr.this.methodCallbackMap.containsKey(callbackId)) {
                    ClientCommunicationMgr.this.identityJID = intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY);
                    //Having logged in and obtained the DomainAuthority and Identity JID through chained calls
                    //invoke the appropriate callback
                    if (ClientCommunicationMgr.this.loginCompleted) {
                        synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                            ClientCommunicationMgr.this.methodCallbackMap.remove(callbackId);
                            ClientCommunicationMgr.this.bindCallback.returnAction(true);
                        }
                    } else {
                        synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                            IMethodCallback callback = ClientCommunicationMgr.this.methodCallbackMap.get(callbackId);
                            ClientCommunicationMgr.this.methodCallbackMap.remove(callbackId);
                            callback.returnAction(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
                        }
                    }
                }
            } else if (intent.getAction().equals(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE)) {
                if (ClientCommunicationMgr.this.methodCallbackMap.containsKey(callbackId)) {
                    ClientCommunicationMgr.this.domainAuthority = intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY);
                    ClientCommunicationMgr.this.getIdentityJid(callbackId);
                }

            } else if (intent.getAction().equals(XMPPAgent.LOGIN_EXCEPTION)) {
                if (ClientCommunicationMgr.this.methodCallbackMap.containsKey(callbackId)) {
                    synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                        IMethodCallback callback = ClientCommunicationMgr.this.methodCallbackMap.get(callbackId);
                        ClientCommunicationMgr.this.methodCallbackMap.remove(callbackId);
                        callback.returnException(intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
                    }
                }
            } else if (intent.getAction().equals(XMPPAgent.LOGIN)) {
                if (ClientCommunicationMgr.this.methodCallbackMap.containsKey(callbackId)) {
                    //Get the values for DomainAuthority and Identity JID after the XMPP login has been performed
                    ClientCommunicationMgr.this.getDomainAuthorityNode(callbackId);
                }
            } else if (intent.getAction().equals(XMPPAgent.LOGOUT)) {
                synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                    IMethodCallback callback = ClientCommunicationMgr.this.methodCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.methodCallbackMap.remove(callbackId);
                        callback.returnAction(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
                    }
                }
            } else if (intent.getAction().equals(XMPPAgent.UN_REGISTER_COMM_MANAGER_RESULT)) {
                synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                    IMethodCallback callback = ClientCommunicationMgr.this.methodCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.methodCallbackMap.remove(callbackId);
                        callback.returnAction(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
                    }
                }
            } else if (intent.getAction().equals(XMPPAgent.CONFIGURE_AGENT)) {
                synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                    IMethodCallback callback = ClientCommunicationMgr.this.methodCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.methodCallbackMap.remove(callbackId);
                        callback.returnAction(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
                    }
                }
            } else if (intent.getAction().equals(XMPPAgent.UN_REGISTER_COMM_MANAGER_EXCEPTION)) {
            } else if (intent.getAction().equals(XMPPAgent.GET_ITEMS_RESULT)) {
                synchronized (ClientCommunicationMgr.this.xmppCallbackMap) {
                    ICommCallback callback = ClientCommunicationMgr.this.xmppCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.xmppCallbackMap.remove(callbackId);
                        try {
                            Packet packet = marshaller.unmarshallIq(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
                            Entry<String, List<String>> nodeMap = marshaller.parseItemsResult(packet);
                            callback.receiveItems(stanzaFromPacket(packet), nodeMap.getKey(), nodeMap.getValue());
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                        }
                    }
                }

            } else if (intent.getAction().equals(XMPPAgent.GET_ITEMS_ERROR)) {
                synchronized (ClientCommunicationMgr.this.xmppCallbackMap) {
                    ICommCallback callback = ClientCommunicationMgr.this.xmppCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.xmppCallbackMap.remove(callbackId);
                        try {
                            Packet packet = marshaller.unmarshallIq(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
                            Entry<String, List<String>> nodeMap = marshaller.parseItemsResult(packet);
                            callback.receiveItems(stanzaFromPacket(packet), nodeMap.getKey(), nodeMap.getValue());
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                        }
                    }
                }

            } else if (intent.getAction().equals(XMPPAgent.GET_ITEMS_EXCEPTION)) {
            } else if (intent.getAction().equals(XMPPAgent.DESTROY_MAIN_IDENTITY)) {
                synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                    IMethodCallback callback = ClientCommunicationMgr.this.methodCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.methodCallbackMap.remove(callbackId);
                        callback.returnAction(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
                    }
                }
            } else if (intent.getAction().equals(XMPPAgent.REGISTER_RESULT)) {
                synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                    IMethodCallback callback = ClientCommunicationMgr.this.methodCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.methodCallbackMap.remove(callbackId);
                        callback.returnAction(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
                    }
                }

            } else if (intent.getAction().equals(XMPPAgent.REGISTER_EXCEPTION)) {
                synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                    IMethodCallback callback = ClientCommunicationMgr.this.methodCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.methodCallbackMap.remove(callbackId);
                        callback.returnException(intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
                    }
                }

            } else if (intent.getAction().equals(XMPPAgent.UNREGISTER_RESULT)) {
                synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                    IMethodCallback callback = ClientCommunicationMgr.this.methodCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.methodCallbackMap.remove(callbackId);
                        callback.returnAction(intent.getBooleanExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false));
                    }
                }

            } else if (intent.getAction().equals(XMPPAgent.UNREGISTER_EXCEPTION)) {
                synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                    IMethodCallback callback = ClientCommunicationMgr.this.methodCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.methodCallbackMap.remove(callbackId);
                        callback.returnException(intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
                    }
                }

            } else if (intent.getAction().equals(XMPPAgent.SEND_IQ_RESULT)) {
                synchronized (ClientCommunicationMgr.this.xmppCallbackMap) {
                    ICommCallback callback = ClientCommunicationMgr.this.xmppCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.xmppCallbackMap.remove(callbackId);
                        if (DEBUG_LOGGING) {
                            Log.d(LOG_TAG, "Received result: " + intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
                        }
                        Packet packet;
                        try {
                            packet = marshaller.unmarshallIq(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
                            Object payload = marshaller.unmarshallPayload(packet);
                            callback.receiveResult(stanzaFromPacket(packet), payload);
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                            e.printStackTrace();
                        }
                    }
                }
            } else if (intent.getAction().equals(XMPPAgent.SEND_IQ_ERROR)) {
                synchronized (ClientCommunicationMgr.this.xmppCallbackMap) {
                    ICommCallback callback = ClientCommunicationMgr.this.xmppCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.xmppCallbackMap.remove(callbackId);

                        Packet packet;
                        try {
                            packet = marshaller.unmarshallIq(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
                            XMPPError payload = marshaller.unmarshallError(packet);
                            callback.receiveError(stanzaFromPacket(packet), payload);
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                            e.printStackTrace();
                        }
                    }
                }
            } else if (intent.getAction().equals(XMPPAgent.SEND_IQ_EXCEPTION)) {
                synchronized (ClientCommunicationMgr.this.xmppCallbackMap) {
                    ICommCallback callback = ClientCommunicationMgr.this.xmppCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.xmppCallbackMap.remove(callbackId);

                        Packet packet;
                        try {
                            packet = marshaller.unmarshallIq(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
                            Object payload = marshaller.unmarshallPayload(packet);
                            callback.receiveMessage(stanzaFromPacket(packet), payload);
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                            e.printStackTrace();
                        }
                    }
                }
            } else if (intent.getAction().equals(XMPPAgent.SEND_MESSAGE_RESULT)) {
            } else if (intent.getAction().equals(XMPPAgent.SEND_MESSAGE_EXCEPTION)) {
            } else if (intent.getAction().equals(XMPPAgent.NEW_MAIN_IDENTITY)) {
                synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                    IMethodCallback callback = ClientCommunicationMgr.this.methodCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.methodCallbackMap.remove(callbackId);
                        callback.returnAction(intent.getStringExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY));
                    }
                }
            } else if (intent.getAction().equals(XMPPAgent.NEW_MAIN_IDENTITY_EXCEPTION)) {
                synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                    IMethodCallback callback = ClientCommunicationMgr.this.methodCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.methodCallbackMap.remove(callbackId);
                        callback.returnException(intent.getStringExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY));
                    }
                }
            } //////////////////////////// RETURN VCARD ///////////////////////////
            else if (intent.getAction().equals(XMPPAgent.GET_VCARD)) {
                synchronized (ClientCommunicationMgr.this.xmppCallbackMap) {
                    ICommCallback callback = ClientCommunicationMgr.this.xmppCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.xmppCallbackMap.remove(callbackId);
                        if (DEBUG_LOGGING) Log.d(LOG_TAG, "Received my VCard result");
                        try {
                            VCardParcel vcard = intent.getParcelableExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY);
                            callback.receiveResult(new Stanza(ClientCommunicationMgr.this.getIdManager().getThisNetworkNode()), vcard);
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                            e.printStackTrace();
                        }
                    }
                }
            } //////////////////////////RETURN VCARD ///////////////////////////
            else if (intent.getAction().equals(XMPPAgent.GET_USER_VCARD)) {
                synchronized (ClientCommunicationMgr.this.xmppCallbackMap) {
                    ICommCallback callback = ClientCommunicationMgr.this.xmppCallbackMap.get(callbackId);
                    if (null != callback) {
                        ClientCommunicationMgr.this.xmppCallbackMap.remove(callbackId);
                        if (DEBUG_LOGGING)
                            Log.d(LOG_TAG, "Received user's Vcard result");
                        try {
                            VCardParcel vcard = intent.getParcelableExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY);
                            callback.receiveResult(new Stanza(ClientCommunicationMgr.this.getIdManager().getThisNetworkNode()), vcard);
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    /**
     * Create a suitable intent filter
     *
     * @return IntentFilter
     */
    private IntentFilter createTestIntentFilter() {
        //register broadcast receiver to receive SocietiesEvents return values
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(XMPPAgent.UN_REGISTER_COMM_MANAGER_RESULT);
        intentFilter.addAction(XMPPAgent.UN_REGISTER_COMM_MANAGER_EXCEPTION);
        intentFilter.addAction(XMPPAgent.DESTROY_MAIN_IDENTITY);
        intentFilter.addAction(XMPPAgent.GET_DOMAIN_AUTHORITY_NODE);
        intentFilter.addAction(XMPPAgent.GET_IDENTITY);
        intentFilter.addAction(XMPPAgent.GET_ITEMS_RESULT);
        intentFilter.addAction(XMPPAgent.GET_ITEMS_ERROR);
        intentFilter.addAction(XMPPAgent.GET_ITEMS_EXCEPTION);
        intentFilter.addAction(XMPPAgent.SEND_IQ_RESULT);
        intentFilter.addAction(XMPPAgent.SEND_IQ_ERROR);
        intentFilter.addAction(XMPPAgent.SEND_IQ_EXCEPTION);
        intentFilter.addAction(XMPPAgent.SEND_MESSAGE_RESULT);
        intentFilter.addAction(XMPPAgent.SEND_MESSAGE_EXCEPTION);
        intentFilter.addAction(XMPPAgent.IS_CONNECTED);
        intentFilter.addAction(XMPPAgent.LOGIN);
        intentFilter.addAction(XMPPAgent.LOGIN_EXCEPTION);
        intentFilter.addAction(XMPPAgent.LOGOUT);
        intentFilter.addAction(XMPPAgent.CONFIGURE_AGENT);
        intentFilter.addAction(XMPPAgent.REGISTER_RESULT);
        intentFilter.addAction(XMPPAgent.REGISTER_EXCEPTION);
        intentFilter.addAction(XMPPAgent.UNREGISTER_RESULT);
        intentFilter.addAction(XMPPAgent.UNREGISTER_EXCEPTION);
        intentFilter.addAction(XMPPAgent.NEW_MAIN_IDENTITY);
        intentFilter.addAction(XMPPAgent.NEW_MAIN_IDENTITY_EXCEPTION);
        intentFilter.addAction(XMPPAgent.GET_VCARD);
        intentFilter.addAction(XMPPAgent.GET_USER_VCARD);
        return intentFilter;
    }

    private boolean bindToServiceAfterLogin() {
        Intent serviceIntent = new Intent(ICoreSocietiesServices.COMMS_SERVICE_INTENT);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Bind to Societies Android Comms Service after Login");
        }
        return this.androidContext.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private boolean bindToServiceBeforeLogin() {
        Intent serviceIntent = new Intent(ICoreSocietiesServices.COMMS_SERVICE_INTENT);
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Bind to Societies Android Comms Service before Login");
        }
        return this.androidContext.bindService(serviceIntent, serviceConnectionLogin, Context.BIND_AUTO_CREATE);
    }

    /**
     * Unbind from Android Comms service
     */
    private void unBindService() {
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "Unbind from Societies Android Comms Service");
        }
        if (this.loginCompleted) {
            if (this.boundToService) {
                this.androidContext.unbindService(serviceConnection);
            }
        } else {
            if (this.boundToService) {
                this.androidContext.unbindService(serviceConnectionLogin);
            }
        }
    }

    /**
     * Create Service Connection to remote service. Assumes that XMPP login and configuration
     * has taken place
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            ClientCommunicationMgr.this.boundToService = false;
            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "Societies Android Comms Service disconnected");
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ClientCommunicationMgr.this.boundToService = true;
            targetService = new Messenger(service);
            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "Societies Android Comms Service connected");
            }
            //The Domain Authority and Identity must now be retrieved before any other calls
            long callbackID = ClientCommunicationMgr.this.randomGenerator.nextLong();

            synchronized (ClientCommunicationMgr.this.methodCallbackMap) {
                //store callback in order to activate required methods
                ClientCommunicationMgr.this.methodCallbackMap.put(callbackID, null);
            }

            ClientCommunicationMgr.this.getDomainAuthorityNode(callbackID);
        }
    };

    /**
     * Create Service Connection to remote service with XMPP login
     */
    private ServiceConnection serviceConnectionLogin = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            ClientCommunicationMgr.this.boundToService = false;
            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "Societies Android Comms Service (Login) disconnected");
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ClientCommunicationMgr.this.boundToService = true;
            targetService = new Messenger(service);
            if (DEBUG_LOGGING) {
                Log.d(LOG_TAG, "Societies Android Comms Service (Login) connected");
            }
            ClientCommunicationMgr.this.bindCallback.returnAction(true);
        }
    };

    /**
     * Async task to invoke remote service method register
     */
    private class InvokeRegister extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeRegister.class.getCanonicalName();
        private String client;
        private List<String> elementNames;
        private List<String> nameSpaces;
        private long remoteID;

        /**
         * Default Constructor
         *
         * @param client
         */
        public InvokeRegister(String client, List<String> elementNames, List<String> nameSpaces, long remoteID) {
            this.client = client;
            this.elementNames = elementNames;
            this.nameSpaces = nameSpaces;
            this.remoteID = remoteID;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = XMPPAgent.methodsArray[0];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
            /*
             * By passing the client package name to the service, the service can modify its broadcast intent so that
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putStringArray(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.elementNames.toArray(new String[this.elementNames.size()]));

            outBundle.putStringArray(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), this.nameSpaces.toArray(new String[this.nameSpaces.size()]));

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), this.remoteID);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteID);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);
            }

            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke remote service method unregister
     */
    private class InvokeUnRegister extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeUnRegister.class.getCanonicalName();
        private String client;
        private List<String> elementNames;
        private List<String> nameSpaces;
        private long remoteID;

        /**
         * Default Constructor
         *
         * @param client
         */
        public InvokeUnRegister(String client, List<String> elementNames, List<String> nameSpaces, long remoteID) {
            this.client = client;
            this.elementNames = elementNames;
            this.nameSpaces = nameSpaces;
            this.remoteID = remoteID;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = XMPPAgent.methodsArray[1];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
            /*
             * By passing the client package name to the service, the service can modify its broadcast intent so that
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putStringArray(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.elementNames.toArray(new String[this.elementNames.size()]));

            outBundle.putStringArray(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), this.nameSpaces.toArray(new String[this.nameSpaces.size()]));

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), this.remoteID);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteID);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);
            }

            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke remote service method unregister
     */
    private class InvokeUnRegisterCommManager extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeUnRegisterCommManager.class.getCanonicalName();
        private String client;
        private long remoteCallId;

        /**
         * Default Constructor
         *
         * @param client
         */
        public InvokeUnRegisterCommManager(String client, long remoteCallId) {
            this.client = client;
            this.remoteCallId = remoteCallId;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = XMPPAgent.methodsArray[2];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
            /*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.remoteCallId);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote Caller identity: " + this.remoteCallId);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);
            }

            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke remote service method unregister
     */
    private class InvokeSendMessage extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeSendMessage.class.getCanonicalName();
        private String client;
        private String xml;

        /**
         * Default Constructor
         *
         * @param client
         */
        public InvokeSendMessage(String client, String xml) {
            this.client = client;
            this.xml = xml;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = XMPPAgent.methodsArray[3];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.xml);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Message: " + this.xml);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);
            }


            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke remote service method unregister
     */
    private class InvokeSendIQ extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeSendIQ.class.getCanonicalName();
        private String client;
        private String xml;
        private long remoteCallID;

        /**
         * Default Constructor
         *
         * @param client
         */
        public InvokeSendIQ(String client, String xml, long remoteCallID) {
            this.client = client;
            this.xml = xml;
            this.remoteCallID = remoteCallID;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = XMPPAgent.methodsArray[4];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.xml);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Message: " + this.xml);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), this.remoteCallID);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteCallID);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);
            }

            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke remote service method unregister
     */
    private class InvokeGetItems extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeGetItems.class.getCanonicalName();
        private String client;
        private String entity;
        private String node;
        private long remoteCallId;

        /**
         * Default Constructor
         *
         * @param client
         */
        public InvokeGetItems(String client, String entity, String node, long remoteCallId) {
            this.client = client;
            this.entity = entity;
            this.node = node;
            this.remoteCallId = remoteCallId;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = XMPPAgent.methodsArray[7];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.entity);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Entity: " + this.entity);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), this.node);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Node: " + this.node);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), this.remoteCallId);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteCallId);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);
            }

            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke remote service method unregister
     */
    private class InvokeGetIdentityJid extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeGetIdentityJid.class.getCanonicalName();
        private String client;
        private long remoteCallId;

        /**
         * Default Constructor
         *
         * @param client
         */
        public InvokeGetIdentityJid(String client, long remoteCallId) {
            this.client = client;
            this.remoteCallId = remoteCallId;

        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = XMPPAgent.methodsArray[5];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.remoteCallId);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteCallId);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);
            }

            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke remote service method unregister
     */
    private class InvokeGetDomainAuthorityNode extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeGetDomainAuthorityNode.class.getCanonicalName();
        private String client;
        private long remoteCallId;

        /**
         * Default Constructor
         *
         * @param client
         */
        public InvokeGetDomainAuthorityNode(String client, long remoteCallId) {
            this.client = client;
            this.remoteCallId = remoteCallId;

        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = XMPPAgent.methodsArray[6];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.remoteCallId);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteCallId);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);
            }

            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke remote service method unregister
     */
    private class InvokeIsConnected extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeIsConnected.class.getCanonicalName();
        private String client;
        private long remoteCallId;

        /**
         * Default Constructor
         *
         * @param client
         */
        public InvokeIsConnected(String client, long remoteCallId) {
            this.client = client;
            this.remoteCallId = remoteCallId;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = XMPPAgent.methodsArray[8];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.remoteCallId);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteCallId);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);
            }

            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke remote service method unregister
     */
    private class InvokeNewMainIdentity extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeNewMainIdentity.class.getCanonicalName();
        private String client;
        private String identifier;
        private String domain;
        private String password;
        private long remoteCallId;
        private String host;

        /**
         * Default Constructor
         *
         * @param client
         */
        public InvokeNewMainIdentity(String client, String identifier, String domain, String password, long remoteCallId, String host) {
            this.client = client;
            this.identifier = identifier;
            this.domain = domain;
            this.password = password;
            this.remoteCallId = remoteCallId;
            this.host = host;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = XMPPAgent.methodsArray[9];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.identifier);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Identifer: " + this.identifier);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), this.domain);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Domain: " + this.domain);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), this.password);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Password: " + this.password);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 4), this.remoteCallId);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteCallId);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 5), this.host);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Host: " + this.host);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);
            }

            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke remote service method unregister
     */
    private class InvokeLogin extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeLogin.class.getCanonicalName();
        private String client;
        private String identifier;
        private String domain;
        private String password;
        private long remoteCallId;
        private String host;

        /**
         * Default Constructor
         *
         * @param client
         * @param identifier
         * @param domain
         * @param password
         * @param host
         * @param remoteCallId
         */
        public InvokeLogin(String client, String identifier, String domain, String password, String host, long remoteCallId) {
            this.client = client;
            this.identifier = identifier;
            this.domain = domain;
            this.password = password;
            this.host = host;
            this.remoteCallId = remoteCallId;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = XMPPAgent.methodsArray[14];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.identifier);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Identifer: " + this.identifier);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), this.domain);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Domain: " + this.domain);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), this.password);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Password: " + this.password);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 4), this.host);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Host: " + this.host);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 5), this.remoteCallId);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteCallId);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);
            }


            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke remote service method unregister
     */
    private class InvokeLogout extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeLogout.class.getCanonicalName();
        private String client;
        private long remoteCallId;

        /**
         * Default Constructor
         *
         * @param client
         */
        public InvokeLogout(String client, long remoteCallId) {
            this.client = client;
            this.remoteCallId = remoteCallId;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = XMPPAgent.methodsArray[11];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.remoteCallId);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteCallId);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);
            }

            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke remote service method unregister
     */
    private class InvokeDestroyMainIdentity extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeDestroyMainIdentity.class.getCanonicalName();
        private String client;
        private long remoteCallId;

        /**
         * Default Constructor
         *
         * @param client
         */
        public InvokeDestroyMainIdentity(String client, long remoteCallId) {
            this.client = client;
            this.remoteCallId = remoteCallId;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = XMPPAgent.methodsArray[12];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.remoteCallId);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteCallId);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);
            }


            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke remote service method unregister
     */
    private class InvokeConfigureAgent extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeConfigureAgent.class.getCanonicalName();
        private String client;
        private String domainAuthorityNode;
        private int xmppPort;
        private String resource;
        private boolean debug;
        private long callbackID;

        /**
         * Default Constructor
         *
         * @param client
         */
        public InvokeConfigureAgent(String client, String domainAuthorityNode, int xmppPort, String resource, boolean debug, long callbackID) {
            this.client = client;
            this.domainAuthorityNode = domainAuthorityNode;
            this.xmppPort = xmppPort;
            this.resource = resource;
            this.debug = debug;
            this.callbackID = callbackID;
        }

        @Override
        protected Void doInBackground(Void... args) {

            String targetMethod = XMPPAgent.methodsArray[13];
            android.os.Message outMessage = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(XMPPAgent.methodsArray, targetMethod), 0, 0);
            Bundle outBundle = new Bundle();
    		/*
    		 * By passing the client package name to the service, the service can modify its broadcast intent so that 
    		 * only the client can receive it.
    		 */
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.client);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 1), this.domainAuthorityNode);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Domain Authority: " + this.domainAuthorityNode);
            }

            outBundle.putInt(ServiceMethodTranslator.getMethodParameterName(targetMethod, 2), this.xmppPort);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "XMPP Port: " + this.xmppPort);
            }

            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 3), this.resource);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "JID Resource: " + this.resource);
            }

            outBundle.putBoolean(ServiceMethodTranslator.getMethodParameterName(targetMethod, 4), this.debug);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Debug Flag: " + this.debug);
            }

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethod, 5), this.callbackID);
            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.callbackID);
            }

            outMessage.setData(outBundle);

            if (DEBUG_LOGGING) {
                Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethod);
            }

            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Async task to invoke VCard functions
     */
    private class InvokeVCard extends AsyncTask<String, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeVCard.class.getCanonicalName();
        private String client;
        private long remoteCallId;

        /**
         * Default Constructor
         *
         * @param client
         */
        public InvokeVCard(String client, long remoteCallId) {
            this.client = client;
            this.remoteCallId = remoteCallId;
        }

        @Override
        protected Void doInBackground(String... params) {

            String targetIntent = params[0];
            int targetMethodInt;
            if (targetIntent.equals(XMPPAgent.GET_VCARD))
                targetMethodInt = 16;
            else
                targetMethodInt = 17; //XMPPAgent.GET_USER_VCARD

            String targetMethodSignature = XMPPAgent.methodsArray[targetMethodInt];
            android.os.Message outMessage = android.os.Message.obtain(null, targetMethodInt, 0, 0);
            Bundle outBundle = new Bundle();

            //CLIENT PARAM
            outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethodSignature, 0), this.client);
            if (DEBUG_LOGGING) Log.d(LOCAL_LOG_TAG, "Client Package Name: " + this.client);

            outBundle.putLong(ServiceMethodTranslator.getMethodParameterName(targetMethodSignature, 1), this.remoteCallId);
            if (DEBUG_LOGGING) Log.d(LOCAL_LOG_TAG, "Remote call ID: " + this.remoteCallId);

            if (targetMethodInt == 17) { //ONLY REQUIRED WHEN GETTING ANOTHER USER'S VCARD
                String userID = params[1];
                outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethodSignature, 2), userID);
                if (DEBUG_LOGGING) Log.d(LOCAL_LOG_TAG, "Target user ID: " + userID);
            }
            outMessage.setData(outBundle);
            if (DEBUG_LOGGING) Log.d(LOCAL_LOG_TAG, "Call Societies Android Comms Service: " + targetMethodSignature);
            try {
                targetService.send(outMessage);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Create a stanza from a received packet
     *
     * @param packet
     * @return
     */
    private Stanza stanzaFromPacket(Packet packet) {
        if (DEBUG_LOGGING) {
            Log.d(LOG_TAG, "stanzaFromPacket packet: " + packet.getPacketID());
        }
        try {
            IIdentity to = IdentityManagerImpl.staticfromJid(packet.getTo());
            IIdentity from = IdentityManagerImpl.staticfromJid(packet.getFrom());
            Stanza returnStanza = new Stanza(packet.getPacketID(), from, to);
            return returnStanza;
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
