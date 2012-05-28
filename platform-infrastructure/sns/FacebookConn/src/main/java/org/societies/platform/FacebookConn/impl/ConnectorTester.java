package org.societies.platform.FacebookConn.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;






public class ConnectorTester {

	
	private static void writeToFile(String filename, String data){
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(filename);
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.write(data);
			  //Close the output stream
			  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	
	/**
	 * to get the token there is right now a cloud API in 
	 * http://wd.teamlife.it/fbconnector.php that allows to get the access token
	 */
	public static void main(String[] args) {
		String access_token = "AAAFs43XOj3IBAGbtrA2I7cibWs8YD1ODGr7JiqXl0ZCJ4DBkeXKeSsth9r2EbRGj6jh1eBIhUAkIZBNs1nKOJU1Ys81xKxUqZAC13DwBAZDZD";
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
		
		String data = connector.getUserActivities();
		writeToFile("activities.txt", data);
		

	}

}
