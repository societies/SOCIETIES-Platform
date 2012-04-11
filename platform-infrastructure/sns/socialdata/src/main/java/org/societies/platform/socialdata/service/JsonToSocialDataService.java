package org.societies.platform.socialdata.service;

import org.json.JSONObject;



public class JsonToSocialDataService {

	 /**
	   * The DB
	   */
	  private JSONObject db;
	  
	  /**
	   * Allows access to the underlying json db.
	   *
	   * @return a reference to the json db
	   */
	  public JSONObject getDb() {
	    return db;
	  }
	  
	  /**
	   * override the json database
	   * @param db a {@link org.json.JSONObject}.
	   */
	  public void setDb(JSONObject db) {
	    this.db = db;
	  }
	  
//	  public static void main(String[]args){
//		  System.out.println("Convert JSON to SocialDATA");
//		  String access_token = "AAAFs43XOj3IBAGbtrA2I7cibWs8YD1ODGr7JiqXl0ZCJ4DBkeXKeSsth9r2EbRGj6jh1eBIhUAkIZBNs1nKOJU1Ys81xKxUqZAC13DwBAZDZD";
//		  ISocialConnector c = new FacebookConnectorImpl(access_token, null);
//		  
//		  
//		  try {
//			
//			String data = c.getUserFriends();
//			
//			int index=0;
//			FriendsConverterFromFacebook parser = new FriendsConverterFromFacebook();
//			
//			List<Person> list = parser.load(data);
//			Iterator<Person> it = list.iterator();
//			while (it.hasNext()){
//				Person entry = it.next();
//				System.out.println("-- "+entry.getName().getFormatted() + " is a "+entry.getRelationshipStatus());
//				index++;
//			}
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		  
//	  }
	  
}
