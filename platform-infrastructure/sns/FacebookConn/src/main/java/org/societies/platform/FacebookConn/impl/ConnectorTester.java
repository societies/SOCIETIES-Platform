package org.societies.platform.FacebookConn.impl;


import org.societies.api.internal.sns.ISocialConnector;



public class ConnectorTester {

	/**
	 * to get the token there is right now a cloud API in 
	 * http://wd.teamlife.it/fbconnector.php that allows to get the access token
	 */
	public static void main(String[] args) {
		String access_token = "AAAFPIhZAkC90BACxKegPyIwc7rxot9Yn7zblMnv79S5sKveE08LENLiWXO97ZCL8NS7JFc3CU09k1ODJxlnZAWap1YUPykLmXrGZCt9gzAZDZD";
		ISocialConnector connector = new FacebookConnectorImpl(access_token,null);
		/*
		System.out.println("User Profile:"+connector.getUserProfile());
		System.out.println("User Groups:"+connector.getUserGroups());
		System.out.println("User Friends:"+connector.getUserFriends());
		System.out.println("User Activies:"+connector.getUserActivities());	
		*/
		
		System.out.println(" PROFILO" + connector.getUserGroups());
		System.out.println("=== END ===");

	}

}
