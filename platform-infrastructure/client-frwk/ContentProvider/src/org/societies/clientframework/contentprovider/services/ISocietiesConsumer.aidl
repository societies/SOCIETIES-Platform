package org.societies.clientframework.contentprovider.services;

/**
 * Example of a callback interface used by IRemoteService to send
 * synchronous notifications back to its clients.  Note that this is a
 * one-way interface so the server does not block waiting for the client.
 */
 
interface ISocietiesConsumer{
    
    void storeCredential(String username, String password, String serviceName);
    
   	String getCredentialUsename(String serviceName);
   	
   	String getCredentialPassword(String serviceName);
   	
   	String[] getServices();
   	
   	void storeCommFwk(String server, String port);
   	
   	String[] getCommFwkEndpoint();
    
}
