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
		String access_token = "AAAFs43XOj3IBANAuFLLTycEWZCiHLvqN1BH9f4OGyhQbWJ2GZC7D57XbITHafLpisDjx0B9OtZCx3hhoxZANUNqOl8FK6tzchZAthmjTQVwZDZD";
		FacebookConnectorImpl connector = new FacebookConnectorImpl(access_token, "Societies User");
		
		
		/*
		System.out.println("User Profile:"+connector.getUserProfile());
		System.out.println("User Groups:"+connector.getUserGroups());
		System.out.println("User Friends:"+connector.getUserFriends());
		System.out.println("User Activies:"+connector.getUserActivities());	
		*/
		
		//System.out.println(" Profile:\n" + connector.getUserProfile());
		//System.out.println(" GROUP:\n" + connector.getUserGroups());
	//	System.out.println(" Friends:\n" + connector.getUserFriends());
		
//		String data = connector.getUserActivities();
//		writeToFile("activities.txt", data);
		
		//connector.post("Test for societies");
		
		String value="{ \"checkin\": {"+
		        "\"lat\": \"53.345149444145\","+
		        "\"lon\": \"-6.2539714878708\","+
		        "\"message\": \"Hello!!!!\","+
		        "\"place\": \"173684349321759\"}"+
		        "}";
				
				connector.post(value);
				

	}

}
