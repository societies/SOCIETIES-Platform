package org.societies.platform.FacebookConn.impl;


import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.FacebookConn.FacebookConnector;



public class ConnectorTester {

	/**
	 * to get the token there is right now a cloud API in 
	 * http://wd.teamlife.it/fbconnector.php that allows to get the access token
	 */
	public static void main(String[] args) {
		String access_token = "AAAFPIhZAkC90BAGbr1zzIiGRZAJ6khAsnCLhMPPxJdOuixheZCbooPwNGLgFX4pbZBacBH1JP9SOVF4vbfau3HSIgS8VKbSJ3gNbUVCZAbQZDZD";
		ISocialConnector connector = new FacebookConnectorImpl(access_token,null);
		
		System.out.println("FACEBOOK DATA:"+connector.getUserProfile());
			
		
		System.out.println("=== END ===");

	}

}
