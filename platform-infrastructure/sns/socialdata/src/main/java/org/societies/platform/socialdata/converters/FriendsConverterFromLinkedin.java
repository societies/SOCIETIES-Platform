package org.societies.platform.socialdata.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.shindig.social.core.model.AccountImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendsConverterFromLinkedin implements FriendsConverter{
	
	
	
	public List<Person> load(String  data){
		List <Person> friends = new ArrayList<Person>();
		Account tw = new AccountImpl();
		tw.setDomain("linkedin.com");
	    List<Account> accounts = new ArrayList<Account>();
	    accounts.add(tw);
	    
	    try{
	    	JSONObject jdata  = new JSONObject(data);
	    	if (!jdata.has("values")){
				return new ArrayList<Person>();
			}
	    	
	    	JSONArray  jfriends  =  null;
	    	PersonConverterFromLinkedin converter = new PersonConverterFromLinkedin();
	    	if (jdata.has("values")){
				jfriends = jdata.getJSONArray("values");
				for (int i=0; i<jfriends.length();i++){
					JSONObject jfriend = jfriends.getJSONObject(i);
//					Person p = new PersonImpl();
//					Name name = new NameImpl();
//					String formattedName= "";
//					if (jfriend.has("firstName")){
//						name.setGivenName(jfriend.getString("firstName"));
//						formattedName=name.getGivenName();
//					}
//					else 
//						name.setGivenName(jfriend.getString("--"));
//					
//					if (jfriend.has("lastName")){
//						name.setFamilyName(jfriend.getString("lastName"));
//						if (formattedName.length()>0) formattedName += " ";
//						formattedName += name.getFamilyName();
//					}
//					else
//						name.setFamilyName("--");
//					
//					name.setFormatted(formattedName);
//					
//					p.setName(name);
//					p.setId("linkedin:"+jfriend.getString("id"));
//					p.setRelationshipStatus("friend");
//					
//					p.setAccounts(accounts);
//					if (jfriend.has("headline"))
//						p.setAboutMe(jfriend.getString("headline"));
//					
//					if (jfriend.has("pictureUrl")){
//						// add ICON
//					}
//					
//					if (jfriend.has("location")){
//						JSONObject location = jfriend.getJSONObject("location");
//						if (location.has("name"))
//							p.setLivingArrangement(location.getString("name"));
//					}
//					
//					if( jfriend.has("siteStandardProfileRequest")){
//						JSONObject url =  jfriend.getJSONObject("siteStandardProfileRequest");
//						if (url.has("url"))
//							p.setProfileUrl(url.getString("url"));
//					}
//					if (jfriend.has("industry"))
//						p.setJobInterests(jfriend.getString("industry"));
//					//System.out.println(">>> new Friends"+p.getName().getFormatted());
				    
				    
				    
					friends.add(converter.load(jfriend.toString()));
				}	
			}
			
	    }
	    catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return friends;
	}
	
}
