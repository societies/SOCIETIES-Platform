package org.societies.platform.FacebookConn.impl;






public class ConnectorTester {

	/**
	 * to get the token there is right now a cloud API in 
	 * http://wd.teamlife.it/fbconnector.php that allows to get the access token
	 */
	public static void main(String[] args) {
		String access_token = "";
		FacebookConnectorImpl connector = new FacebookConnectorImpl(access_token,null);
		
		
		/*
		System.out.println("User Profile:"+connector.getUserProfile());
		System.out.println("User Groups:"+connector.getUserGroups());
		System.out.println("User Friends:"+connector.getUserFriends());
		System.out.println("User Activies:"+connector.getUserActivities());	
		*/
		
		//System.out.println(" Profile:\n" + connector.getUserProfile());
		//System.out.println(" GROUP:\n" + connector.getUserGroups());
	//	System.out.println(" Friends:\n" + connector.getUserFriends());
		System.out.println(" Activities:\n" + connector.getUserActivities());
		System.out.println("=== END ===");

	}

}
