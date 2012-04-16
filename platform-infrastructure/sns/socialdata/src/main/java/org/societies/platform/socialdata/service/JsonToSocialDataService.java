package org.societies.platform.socialdata.service;

import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONObject;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.FacebookConn.impl.FacebookConnectorImpl;
import org.societies.platform.socialdata.converters.PersonConverterFromFacebook;



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
		  //System.out.println("Convert JSON to SocialDATA");
		  String access_token = "AAAFs43XOj3IBAGbtrA2I7cibWs8YD1ODGr7JiqXl0ZCJ4DBkeXKeSsth9r2EbRGj6jh1eBIhUAkIZBNs1nKOJU1Ys81xKxUqZAC13DwBAZDZD";
		  ISocialConnector c = new FacebookConnectorImpl(access_token, null);
		  
		  
		  try {
			
			String data = c.getUserProfile();
			
			int index=0;
			PersonConverterFromFacebook parser = new PersonConverterFromFacebook();
			
			Person p= parser.load(data);
			System.out.println("p:"+p.getTurnOns().toString());
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
