package org.societies.platform.socialdata.service;

import java.util.HashMap;

import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.sns.ISocialConnectorInternal;
import org.societies.platform.socialdata.SocialData;
import org.societies.platform.socialdata.converters.PersonConverterFactory;
import org.societies.platform.socialdata.converters.PersonConverterFromFacebook;
import org.societies.platform.socialdata.converters.PersonConverterFromLinkedin;




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
		  String access_token = "98d8df36-b9fc-41bf-abd6-0ab97f07247e,d73af323-2ff8-4625-b919-748576221396";
		  
		  
		  
		  HashMap<String, String> pars = new HashMap<String, String>();
		  pars.put(ISocialConnectorInternal.AUTH_TOKEN, access_token);
		  ISocialConnectorInternal c = sd.createConnector(ISocialConnectorInternal.SocialNetwork.linkedin, pars);
		 
		  
		  
		 
		  
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
		  
		  PersonConverterFromLinkedin aaa = new PersonConverterFromLinkedin();
		  Person ppp = aaa.load(c.getUserProfile());
		  
		  System.out.println("::::"+c.getUserProfile());
		  System.out.println("Person URL:"+ppp.getThumbnailUrl());
		  
		 // System.out.println("Person:"+p.toString());
		  
		  
		  
	  }
	  
}
