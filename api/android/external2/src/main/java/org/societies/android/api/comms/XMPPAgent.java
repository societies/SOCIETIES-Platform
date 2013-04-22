package org.societies.android.api.comms;

import org.societies.android.api.comms.xmpp.VCardParcel;

/**
 * Mirror interface of org.societies.api.comm.xmpp.interfaces.ICommManager, adapted for Android. Check javadoc there.
 * 
 * @author Edgar Domingues
 * @author Joao M. Goncalves
 */
public interface XMPPAgent {
	String methodsArray [] = {"register(String client, String[] elementNames, String[] namespaces, long remoteCallId)",
							  "unregister(String client, String[] elementNames, String[] namespaces, long remoteCallId)",
							  "UnRegisterCommManager(String client, long remoteCallId)",
							  "sendMessage(String client, String messageXml, long remoteCallId)",
							  "sendIQ(String client, String xml, long remoteCallId)",
							  "getIdentity(String client, long remoteCallId)",
							  "getDomainAuthorityNode(String client, long remoteCallId)",
							  "getItems(String client, String entity, String node, long remoteCallId)",
							  "isConnected(String client, long remoteCallId)",
							  "newMainIdentity(String client, String identifier, String domain, String password, long remoteCallId, String host)",
							  "login(String client, String identifier, String domain, String password, long remoteCallId)",
							  "logout(String client, long remoteCallId)",
							  "destroyMainIdentity(String client, long remoteCallId)",
							  "configureAgent(String client, String domainAuthorityNode, int xmppPort, String resource, boolean debug, long remoteCallId)",
							  "login(String client, String identifier, String domain, String password, String host, long remoteCallId)", 
							  "setVCard(String client, VCardParcel vCard)",					//15
							  "getVCard(String client, long remoteCallId)", 				//16
							  "getVCard(String client, long remoteCallId, String userId)"	//17
							};
	
	/**
	 * Android Comms intents
	 * Used to create to create Intents to signal return values of a called method
	 * If the method is locally bound it is possible to directly return a value but is discouraged
	 * as called methods usually involve making asynchronous calls. 
	 */
	public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.platform.comms.ReturnValue";
	public static final String INTENT_RETURN_EXCEPTION_KEY = "org.societies.android.platform.comms.ReturnException";
	public static final String INTENT_RETURN_EXCEPTION_TRACE_KEY = "org.societies.android.platform.comms.ReturnExceptionTrace";
	public static final String INTENT_RETURN_CALL_ID_KEY = "org.societies.android.platform.comms.ReturnCallId";

	public static final String UN_REGISTER_COMM_MANAGER_RESULT = "org.societies.android.platform.comms.UN_REGISTER_COMM_MANAGER_RESULT";
	public static final String UN_REGISTER_COMM_MANAGER_EXCEPTION = "org.societies.android.platform.comms.UN_REGISTER_COMM_MANAGER_EXCEPTION";
	public static final String GET_IDENTITY = "org.societies.android.platform.comms.GET_IDENTITY";
	public static final String GET_DOMAIN_AUTHORITY_NODE = "org.societies.android.platform.comms.GET_DOMAIN_AUTHORITY_NODE";
	public static final String GET_ITEMS_RESULT = "org.societies.android.platform.comms.GET_ITEMS_RESULT";
	public static final String GET_ITEMS_ERROR = "org.societies.android.platform.comms.GET_ITEMS_ERROR";
	public static final String GET_ITEMS_EXCEPTION = "org.societies.android.platform.comms.GET_ITEMS_EXCEPTION";
	public static final String IS_CONNECTED = "org.societies.android.platform.comms.IS_CONNECTED";
	public static final String NEW_MAIN_IDENTITY = "org.societies.android.platform.comms.NEW_MAIN_IDENTITY";
	public static final String NEW_MAIN_IDENTITY_EXCEPTION = "org.societies.android.platform.comms.NEW_MAIN_IDENTITY_EXCEPTION";
	public static final String LOGIN_EXCEPTION = "org.societies.android.platform.comms.LOGIN_EXCEPTION";
	public static final String LOGIN = "org.societies.android.platform.comms.LOGIN";
	public static final String LOGOUT = "org.societies.android.platform.comms.LOGOUT";
	public static final String DESTROY_MAIN_IDENTITY = "org.societies.android.platform.comms.DESTROY_MAIN_IDENTITY";
	public static final String CONFIGURE_AGENT = "org.societies.android.platform.comms.CONFIGURE_AGENT";
	public static final String UNREGISTER_RESULT = "org.societies.android.platform.comms.UNREGISTER_RESULT";
	public static final String UNREGISTER_EXCEPTION = "org.societies.android.platform.comms.UNREGISTER_EXCEPTION";
	public static final String REGISTER_RESULT = "org.societies.android.platform.comms.REGISTER_RESULT";
	public static final String REGISTER_EXCEPTION = "org.societies.android.platform.comms.REGISTER_EXCEPTION";
	public static final String SEND_IQ_RESULT = "org.societies.android.platform.comms.SEND_IQ_RESULT";
	public static final String SEND_IQ_EXCEPTION = "org.societies.android.platform.comms.SEND_IQ_EXCEPTION";
	public static final String SEND_IQ_ERROR = "org.societies.android.platform.comms.SEND_IQ_ERROR";
	public static final String SEND_MESSAGE_RESULT = "org.societies.android.platform.comms.SEND_MESSAGE_RESULT";
	public static final String SEND_MESSAGE_EXCEPTION = "org.societies.android.platform.comms.SEND_MESSAGE_EXCEPTION";
	public static final String PUBSUB_EVENT = "org.societies.android.platform.comms.PUBSUB_EVENT";
	public static final String SET_VCARD 	 	= "org.societies.android.platform.comms.SET_VCARD";
	public static final String GET_VCARD 	 	= "org.societies.android.platform.comms.GET_VCARD";
	public static final String GET_USER_VCARD 	= "org.societies.android.platform.comms.GET_USER_VCARD";


	public boolean register(String client, String[] elementNames, String[] namespaces, long remoteCallId);
	
	public boolean unregister(String client, String[] elementNames, String[] namespaces, long remoteCallId);
	
	public boolean UnRegisterCommManager(String client, long remoteCallId);
	
	public boolean sendMessage(String client, String messageXml, long remoteCallId);

	public boolean sendIQ(String client, String xml, long remoteCallId);
	
	public String getIdentity(String client, long remoteCallId);
	
	public String getDomainAuthorityNode(String client, long remoteCallId);
	
	public String getItems(String client, String entity, String node, long remoteCallId);
	
	public boolean isConnected(String client, long remoteCallId);
	/**
	 * 
	 * @param client Calling component's app package. Used to restrict broadcast of return value via intent.
	 * @param identifier User's user name
	 * @param domain XMPP Domain server (service name)
	 * @param password User's password
	 * @param remoteCallId Callback identifier to allow ClientCommunicationMgr to invoke correct callback action
	 * @param host IP address of XMPP server. Prevents DNS lookup to resolve XMPP server address
	 * @return boolean via Intent
	 */
	public String newMainIdentity(String client, String identifier, String domain, String password, long remoteCallId, String host);
	/**
	 * Allows a user to login to the designated XMPP server
	 * 
	 * @param client Calling component's app package. Used to restrict broadcast of return value via intent.
	 * @param identifier User's user name
	 * @param domain XMPP Domain server (service name)
	 * @param password User's password
	 * @param remoteCallId Callback identifier to allow ClientCommunicationMgr to invoke correct callback action
	 * @return identity but via Intent
	 */
	public String login(String client, String identifier, String domain, String password, long remoteCallId);
	/**
	 * Allows a user to login to the designated XMPP server
	 * 
	 * @param client Calling component's app package. Used to restrict broadcast of return value via intent.
	 * @param identifier User's user name
	 * @param domain XMPP Domain server (service name)
	 * @param password User's password
	 * @param host IP address of XMPP server. Prevents DNS lookup to resolve XMPP server address
	 * @param remoteCallId Callback identifier to allow ClientCommunicationMgr to invoke correct callback action
	 * @return
	 */
	public String login(String client, String identifier, String domain, String password, String host, long remoteCallId);
	
	public boolean logout(String client, long remoteCallId);
	
	public boolean destroyMainIdentity(String client, long remoteCallId);	
	
	public boolean configureAgent(String client, String domainAuthorityNode, int xmppPort, String resource, boolean debug, long remoteCallId);
	
	/**
	 * Sets the VCard for the current user
	 * @param vCard
	 */
	public void setVCard(String client, VCardParcel vCard);
    
	/**
	 * Returns the VCard for the current user
	 * @return
	 */
	public VCardParcel getVCard(String client, long remoteCallId);
	
	/**
	 * Returns the VCard for a given user
	 * @param userId
	 * @return
	 */
	public VCardParcel getVCard(String client, long remoteCallId, String userId);
    
}
