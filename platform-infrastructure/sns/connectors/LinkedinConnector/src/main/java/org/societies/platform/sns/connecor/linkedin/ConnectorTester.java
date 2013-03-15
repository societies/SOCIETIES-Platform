package org.societies.platform.sns.connecor.linkedin;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;






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
		String access_token = "98d8df36-b9fc-41bf-abd6-0ab97f07247e,d73af323-2ff8-4625-b919-748576221396";
		
		LinkedinConnector connector = new LinkedinConnector(access_token, "Societies User");
		try {
	
			  
			
			JSONObject json = new JSONObject(connector.getUserProfile());
			System.out.println("RESULT\n:"+ json.toString(1));
			
			
			
			
			// make the json payload using json-simple
//			JSONObject jsonMap = new JSONObject();
//			jsonMap.put("comment", "Posting from the API using JSON");
//			 
//			JSONObject contentObject = new JSONObject();
//			contentObject.put("title", "A title for your share");
//			contentObject.put("submitted-url","http://www.linkedin.com");
//			contentObject.put("submitted-image-url", "http://lnkd.in/Vjc5ec");
//			 
//			jsonMap.put("content", contentObject);
//			 
//			JSONObject visibilityObject = new JSONObject();
//			visibilityObject.put("code", "anyone");
//			 
//			jsonMap.put("visibility", visibilityObject);
//			
//			
//			System.out.println(jsonMap.toString(2));
//			connector.post(jsonMap.toString(2));
//			System.out.println("Friends:\n"+connector.getUserFriends());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
