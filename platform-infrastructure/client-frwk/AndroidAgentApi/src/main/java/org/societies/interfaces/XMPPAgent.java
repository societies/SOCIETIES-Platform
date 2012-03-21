package org.societies.interfaces;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;

public interface XMPPAgent {
	
	public void register(String[] elementNames, String[] namespaces, Callback callback);
	
	public void unregister(String[] elementNames, String[] namespaces);
	
	public void sendMessage(String messageXml);

	public void sendIQ(String xml, Callback callback);
	
	public String getIdentity();
	
	public String getItems(String entity, String node, Callback callback) throws CommunicationException;
}
