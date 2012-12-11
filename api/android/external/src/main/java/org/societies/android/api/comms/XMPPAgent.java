package org.societies.android.api.comms;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;

/**
 * Mirror interface of org.societies.api.comm.xmpp.interfaces.ICommManager, adapted for Android. Check javadoc there.
 * 
 * @author Edgar Domingues
 * @author Joao M. Goncalves
 */
public interface XMPPAgent {
	String methodsArray [] = {"register(String client, String[] elementNames, String[] namespaces, org.societies.android.api.internal.comms.Callback callback)",
							  "unregister(String client, String[] elementNames, String[] namespaces)",
							  "UnRegisterCommManager(String client)",
							  "sendMessage(String client, String messageXml)",
							  "sendIQ(String client, String xml, org.societies.android.api.internal.comms.Callback callback)",
							  "getIdentity(String client)",
							  "getDomainAuthorityNode(String client)",
							  "getItems(String client, String entity, String node, org.societies.android.api.internal.comms.Callback callback)",
							  "isConnected(String client)",
							  "newMainIdentity(String client, String identifier, String domain, String password)",
							  "login(String client, String identifier, String domain, String password)",
							  "logout(String client)",
							  "destroyMainIdentity(String client)",
							  "setDomainAuthorityNode(String client, String domainAuthorityNode)",
							  "setPortNumber(String client, int port)",
							  "setResource(String client, String resource)",
							  "setDebug(String client, boolean enabled)", 
							  "configureAgent(String client, String domainAuthorityNode, int xmppPort, String resource, boolean debug)"
	};
	
	/**
	 * Android Comms intents
	 * Used to create to create Intents to signal return values of a called method
	 * If the method is locally bound it is possible to directly return a value but is discouraged
	 * as called methods usually involve making asynchronous calls. 
	 */
	public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.platform.comms.ReturnValue";

	public static final String UN_REGISTER_COMM_MANAGER = "org.societies.android.platform.comms.UN_REGISTER_COMM_MANAGER";
	public static final String GET_IDENTITY = "org.societies.android.platform.comms.GET_IDENTITY";
	public static final String GET_DOMAIN_AUTHORITY_NODE = "org.societies.android.platform.comms.GET_DOMAIN_AUTHORITY_NODE";
	public static final String GET_ITEMS = "org.societies.android.platform.comms.GET_ITEMS";
	public static final String IS_CONNECTED = "org.societies.android.platform.comms.IS_CONNECTED";
	public static final String NEW_MAIN_IDENTITY = "org.societies.android.platform.comms.NEW_MAIN_IDENTITY";
	public static final String LOGIN = "org.societies.android.platform.comms.LOGIN";
	public static final String LOGOUT = "org.societies.android.platform.comms.LOGOUT";
	public static final String DESTROY_MAIN_IDENTITY = "org.societies.android.platform.comms.DESTROY_MAIN_IDENTITY";
	public static final String CONFIGURE_AGENT = "org.societies.android.platform.comms.CONFIGURE_AGENT";


	public void register(String client, String[] elementNames, String[] namespaces, Callback callback);
	
	public void unregister(String client, String[] elementNames, String[] namespaces);
	
	public boolean UnRegisterCommManager(String client);
	
	public void sendMessage(String client, String messageXml);

	public void sendIQ(String client, String xml, Callback callback);
	
	public String getIdentity(String client);
	
	public String getDomainAuthorityNode(String client);
	
	public String getItems(String client, String entity, String node, Callback callback) throws CommunicationException;
	
	public Boolean isConnected(String client);
	
	public String newMainIdentity(String client, String identifier, String domain, String password) throws CommunicationException; // TODO this takes no credentials in a private/public key case
	
	public String login(String client, String identifier, String domain, String password);
	
//	public String loginFromConfig(String client);
	
	public boolean logout(String client);
	
	public boolean destroyMainIdentity(String client);
	
	public void setDomainAuthorityNode(String client, String domainAuthorityNode);
	
	public void setPortNumber(String client, int port);
	
	public void setResource(String client, String resource);
	
	public void setDebug(String client, boolean enabled);
	
	public boolean configureAgent(String client, String domainAuthorityNode, int xmppPort, String resource, boolean debug);
}
