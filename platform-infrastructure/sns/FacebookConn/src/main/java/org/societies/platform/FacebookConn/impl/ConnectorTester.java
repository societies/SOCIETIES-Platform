package org.societies.platform.FacebookConn.impl;


import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.FacebookConn.FacebookConnector;



public class ConnectorTester {

	/**
	 * to get the token there is right now a cloud API in 
	 * http://wd.teamlife.it/fbconnector.php that allows to get the access token
	 */
	public static void main(String[] args) {
		String access_token = "AAAFPIhZAkC90BAO1fmJZAxs754pEGhaGQesb9haktJ8JgDOnxsSUo2A9PJOnQa34b2mRUuRXRUS1mhUIJG8RMetAUwZBZBkOBFfo7G0h6wZDZD";
		ISocialConnector connector = new FacebookConnectorImpl(access_token,null);
		
		
		String str = connector.getSocialData(FacebookConnector.ME);
		System.out.println("FACEBOOK DATA:");
			
		System.out.println(str);
		
		System.out.println("=== END ===");

	}

}
