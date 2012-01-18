package org.societies.interfaces;

public interface XMPPAgent {
	public void sendMessage(String messageXml);

	public void sendIQ(String id, String xml, Callback callback);
}
