package org.societies.platform.socialdata.service;

import java.util.HashMap;

import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.socialdata.SocialData;
import org.societies.platform.socialdata.converters.PersonConverterFromFacebook;




public class JsonToSocialDataService {

	
	 private static  Logger logger = LoggerFactory.getLogger(JsonToSocialDataService.class);
	 
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
	  
	  public static void main(String[]args){
		  
		  
		  
		  SocialData sd= new SocialData();
		  //System.out.println("Convert JSON to SocialDATA");
		  String access_token = "";
		  HashMap<String, String> pars = new HashMap<String, String>();
		  pars.put(ISocialConnector.AUTH_TOKEN, access_token);
		  
		  ISocialConnector c = sd.createConnector(ISocialConnector.SocialNetwork.Facebook, pars);
		  
		 
		  
//		  try {
//			
//			String data = c.getUserActivities();
//			
//			int index=0;
//			ActivityConverterFromFacebook parser = new ActivityConverterFromFacebook();
//			
//			List<ActivityEntry> p= parser.load(data);
////			System.out.println("Size of:"+p.size());
//			Iterator<ActivityEntry> it = p.iterator();
//			while (it.hasNext()){
//				ActivityEntry entry = it.next();
//				System.out.println(entry.getPublished() + "-- "+entry.getActor().getDisplayName() + " made a  "+entry.getVerb() + " ? " + entry.getContent());
//				index++;
//			}
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		  
		  
		  String data = c.getUserProfile();
		  logger.info("profile Data:"+data);
		  System.out.println("data:"+data);
		  PersonConverterFromFacebook parser = new PersonConverterFromFacebook();
		  Person p = parser.load(data);
		  
		  
	  }
	  
}
