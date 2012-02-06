package org.societies.android.platform.interfaces;

import java.util.List;
import java.util.Map;

public interface IContentProvider {
		
		String methodsArray [] = {"setCredential(String username, String password, String serviceName)",
								  "getCredential(String serviceName)",
								  "setCommFwkEndpoint(String hostname, int port)",
								  "getCommFwkEndoint()",
								  "storeData(String key, String data, String serviceName)",
								  "getData(String key, String serviceName)",
								  "getServices()",
								  "resetDB()"
								  
		};
	
		public static final String CREDENTIAL_USERNAME = "credential.username";
		public static final String CREDENTIAL_PASSWORD = "credential.password";
		public static final String SERVICE_NAME		   = "societies.service";
		public static final String SERVICE_DEFAULT	   = "societeis.services.default";
		public static final String SERVICE_COMM_FWK    = "societies.services.communication.framework";
		public static final String COMM_FWK_HOSTNAME   = "societies.communication.framwork.hostname";
		public static final String COMM_FWK_PORT 	   = "societies.communication.framwork.port";
		
		
		public static final String CONTENT_PROVIDER_INTENT 	     = "org.societies.clientframework.contentprovider.service";
		
		public static final String CONTENT_PROVIDER_STORE_INTENT = "org.societies.clientframework.contentprovider.service.STORE";
		public static final String CONTENT_PROVIDER_STORE_LOOKUP = "org.societies.clientframework.contentprovider.service.LOOKUP";
		
		
		/**
		 * Set Credential (username, password) for a specific Service
		 * @param username
		 * @param password
		 * @param serviceName
		 */
		void setCredential(String username, String password, String serviceName);
		
		
		/**
		 * This method get the credential for a specific service name
		 * @param serviceName 
		 * @return an array of String that contains in order username and password.
		 */ 
		List<?> getCredential(String serviceName);
		
		/**
		 * Store the IP address or hostname and the port of the Communicaiton Framework Server
		 * @param hostname   (IP Address or Hostname e.g. societies.comm.fwk.eu)
		 * @param port		 (this is a string tha should be converted into integer)
		 */
		void setCommFwkEndpoint(String hostname, int port);
		
		/**
		 * Get CommunicationFramework Endpojnt Port
		 * @return Port value
		 */
		List<?> getCommFwkEndpoint();
		
		/**
		 * Generic method that allows to store data into content provider
		 * @param key   a key to retreive the element
		 * @param data  an object data to be stored
		 * @param serviceName specify the service associtated to this element
		 */
		void storeData(Map<String, ?> data, String serviceName);
		
		
		/**
		 * Generic method to retreive stored data
		 * @param key to retrieve elemented stored
		 * @param serviceName associated with the key
		 * @return returned the value
		 */
		Map<String, ?> getData(String serviceName);
		
		
		/**
		 * Get a list of the methods 
		 * @return
		 */
		String[] getServices();
		
		
		void resetDB();
		
}
