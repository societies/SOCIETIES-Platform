package org.societies.platform.socialdata.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.json.JSONObject;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.socialdata.SocialData;
import org.societies.platform.socialdata.converters.ActivityConverterFromFacebook;




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
		  String access_token = "AAAFs43XOj3IBAGbtrA2I7cibWs8YD1ODGr7JiqXl0ZCJ4DBkeXKeSsth9r2EbRGj6jh1eBIhUAkIZBNs1nKOJU1Ys81xKxUqZAC13DwBAZDZD";
		  HashMap<String, String> pars = new HashMap<String, String>();
		  pars.put(ISocialConnector.AUTH_TOKEN, access_token);
		  
		  ISocialConnector c = sd.createConnector(ISocialConnector.SocialNetwork.Facebook, pars);
		  
		 
		  
		  try {
			
			String data = c.getUserActivities();
			
			int index=0;
			ActivityConverterFromFacebook parser = new ActivityConverterFromFacebook();
			
			List<ActivityEntry> p= parser.load(data);
//			System.out.println("Size of:"+p.size());
			Iterator<ActivityEntry> it = p.iterator();
			while (it.hasNext()){
				ActivityEntry entry = it.next();
				System.out.println(entry.getPublished() + "-- "+entry.getActor().getDisplayName() + " made a  "+entry.getVerb() + " ? " + entry.getContent());
				index++;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
	  }
	  
}
