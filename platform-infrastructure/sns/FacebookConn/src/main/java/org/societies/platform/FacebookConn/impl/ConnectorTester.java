package org.societies.platform.FacebookConn.impl;


import org.societies.platform.FacebookConn.SocialConnector;
import org.societies.platform.FacebookConn.FacebookConnector;
import org.societies.platform.FacebookConn.exeptions.MissingTokenExeptions;



public class ConnectorTester {

	/**
	 * to get the token there is right now a cloud API in 
	 * http://wd.teamlife.it/fbconnector.php that allows to get the access token
	 */
	public static void main(String[] args) {
		String access_token = "AAAFPIhZAkC90BAI6gXREctk3mtdzoSHGAV1ElowxmyEbDWMAnq0zfzsfaLXFGtEt7bUezAZCKaXJFTIpsdblZCG2DUTXY31VAZAxXIprMAZDZD";
		SocialConnector connector = new FacebookConnectorImpl(access_token,null);
		
		try {
			
			
			String str = connector.getSocialData(FacebookConnector.FEED);
			System.out.println("FACEBOOK DATA:");
			
			System.out.println(str);
		} catch (MissingTokenExeptions e) {
			
		}
		
		System.out.println("=== END ===");

	}

}
