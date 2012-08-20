package org.societies.interfaces;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;

/**
 * Mirror interface of org.societies.api.comm.xmpp.interfaces.ICommManager, adapted for Android. Check javadoc there.
 * 
 * @author Edgar Domingues
 * @author Joao M. Goncalves
 */
public interface XMPPAgent {
	
	public void register(String[] elementNames, String[] namespaces, Callback callback);
	
	public void unregister(String[] elementNames, String[] namespaces);
	
	public boolean UnRegisterCommManager();
	
	public void sendMessage(String messageXml);

	public void sendIQ(String xml, Callback callback);
	
	public String getIdentity();
	
	public String getDomainAuthorityNode();
	
	public String getItems(String entity, String node, Callback callback) throws CommunicationException;
	
	public Boolean isConnected();
	
	public String newMainIdentity(String identifier, String domain, String password) throws CommunicationException; // TODO this takes no credentials in a private/public key case
	
	public String login(String identifier, String domain, String password);
	
	public String loginFromConfig();
	
	public boolean logout();
	
	public boolean destroyMainIdentity();
}
