package org.societies.platform.socialdata.service;

import java.util.HashMap;
import java.util.List;

import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;
import org.societies.api.sns.SocialDataState;
import org.societies.platform.socialdata.SocialData;




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
		  

		  ISocialConnector c = sd.createConnector(SocialNetwork.FACEBOOK, pars);
		  System.out.println("connector id:" + c.getID());
		  System.out.println("connector token:" + c.getToken());
		  System.out.println("connector name:" + c.getConnectorName());
		  
		  
		  
		  try {
		    sd.addSocialConnector(c);
		} catch (Exception e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
		  sd.updateSocialData();
		  
		  while(sd.getStatus() != SocialDataState.WITH_SOME_SOCIAL_DATA){
		     try {
			Thread.sleep(500);
			System.out.println(sd.getStatus());
		    } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		  }
		  
//		  for(Object p : sd.getSocialProfiles()){
//		      Person profile =(Person)p;
//		      System.out.println("Profile "+profile.getName().getFormatted());
//			  
//		  }
		  
		  List<Object> friends 	        = sd.getSocialPeople();
		  List<Object> feed 		= sd.getSocialActivity();
		  
		  
		  for(Object o: friends){
		      Person elm = (Person)o;
		      System.out.println("Friends-> "+elm.getAccounts().get(0).getDomain() + " =NAME==> " + elm.getName().getFormatted() + " "+elm.getDisplayName());
		  }
		  
		  
		  
		 
//		  Message msg = new Message();
//		  msg.setData("test 1");
//		  sd.postMessage(SocialNetwork.LINKEDIN, msg);
		  
		  
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
		  
		  
//		  String data = c.getUserProfile();
//		  logger.info("profile Data:"+data);
//		  System.out.println("data:"+data);
//		  PersonConverterFromFacebook parser = new PersonConverterFromFacebook();
//		  Person p = parser.load(data);
		  
		  
	  }
	  
}
