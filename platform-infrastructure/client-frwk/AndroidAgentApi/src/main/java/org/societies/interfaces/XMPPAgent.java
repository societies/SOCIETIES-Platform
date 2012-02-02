package org.societies.interfaces;

public interface XMPPAgent {
	
	/**
	 * Register the element names and corresponding namespaces of the IQ responses.
	 * Each element name in the first array is registered with namespace 
	 * in the same position of the second array. 
	 * Therefore the array must have the same size. 
	 */
	public void register(String[] elementNames, String[] namespaces);
	
	public void unregister(String[] elementNames, String[] namespaces);
	
	public void sendMessage(String messageXml);

	public void sendIQ(String xml, Callback callback);
}
