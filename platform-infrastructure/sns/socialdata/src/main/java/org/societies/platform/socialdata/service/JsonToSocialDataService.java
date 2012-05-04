package org.societies.platform.socialdata.service;

import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONObject;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.socialdata.SocialData;
import org.societies.platform.socialdata.converters.PersonConverterFromTwitter;




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
	  
	  public static void main(String[]args){
		  
		  
		  
		  SocialData sd= new SocialData();
		  //System.out.println("Convert JSON to SocialDATA");
		  String access_token = "";
		  ISocialConnector c = sd.createConnector(ISocialConnector.SocialNetwork.twitter, null);
		  
		  
		  try {
			
			String data = c.getUserProfile();
			
			int index=0;
			PersonConverterFromTwitter parser = new PersonConverterFromTwitter();
			
			Person p= parser.load(data);
			System.out.println("p:"+p.getAboutMe());
//			Iterator<ActivityEntry> it = list.iterator();
//			while (it.hasNext()){
//				ActivityEntry entry = it.next();
//				System.out.println("-- "+entry.getActor().getDisplayName() + " made a  "+entry.getVerb() + " ? " + entry.getContent());
//				index++;
//			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
	  }
	  
}
