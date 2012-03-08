package org.societies.platform.FacebookConn.impl;


import org.societies.platform.FacebookConn.Connector;
import org.societies.platform.FacebookConn.FacebookConnector;
import org.societies.platform.FacebookConn.exeptions.MissingTokenExeptions;



public class ConnectorTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String access_token = "AAAFPIhZAkC90BACBKLvSePRQ58AKIRSicS3EpGOmhfUMeQSpDcjlo5kTpHs2Xl2oG1igLIMpZAYpNNOhdGSDQ4tpkNn1VZCoRG8NJdR0AZDZD";
		Connector connector = new FacebookConnectorImpl(access_token,null);
		
		try {
			
			
			String str = connector.getSocialData(FacebookConnector.CHECKINS);
			System.out.println("FACEBOOK DATA:");
			
			System.out.println(str);
		} catch (MissingTokenExeptions e) {
			
		}
		
		System.out.println("=== END ===");

	}

}
