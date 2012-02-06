package org.societies.interfaces;

public interface XMPPAgent {
	
	public void register(String[] elementNames, String[] namespaces, Callback callback);
	
	public void unregister(String[] elementNames, String[] namespaces);
	
	public void sendMessage(String messageXml);

	public void sendIQ(String xml, Callback callback);
}
