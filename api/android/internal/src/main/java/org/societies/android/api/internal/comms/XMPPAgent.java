package org.societies.android.api.internal.comms;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;

/**
 * Mirror interface of org.societies.api.comm.xmpp.interfaces.ICommManager, adapted for Android. Check javadoc there.
 * 
 * @author Edgar Domingues
 * @author Joao M. Goncalves
 */
public interface XMPPAgent {
	String methodsArray [] = {"register(String client, String[] elementNames, String[] namespaces, Callback callback)",
							  "unregister(String client, String[] elementNames, String[] namespaces)",
							  "UnRegisterCommManager(String client)",
							  "sendMessage(String client, String messageXml)",
							  "sendIQ(String client, String xml, Callback callback)",
							  "getIdentity(String client)",
							  "getDomainAuthorityNode(String client)",
							  "getItems(String client, String entity, String node, Callback callback)",
							  "isConnected(String client)",
							  "newMainIdentity(String client, String identifier, String domain, String password)",
							  "login(String client, String identifier, String domain, String password)",
							  "loginFromConfig(String client)",
							  "logout(String client)",
							  "destroyMainIdentity(String client)",
							  "setDomainAuthorityNode(String client, String domainAuthorityNode)",
							  "setPortNumber(String client, Integer port)",
							  "setResource(String client, String resource)",
							  "setDebug(String client, Boolean enabled)"
	};

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
	
	public String loginFromConfig(String client);
	
	public boolean logout(String client);
	
	public boolean destroyMainIdentity(String client);
	
	public void setDomainAuthorityNode(String client, String domainAuthorityNode);
	
	public void setPortNumber(String client, Integer port);
	
	public void setResource(String client, String resource);
	
	public void setDebug(String client, Boolean enabled);
}
