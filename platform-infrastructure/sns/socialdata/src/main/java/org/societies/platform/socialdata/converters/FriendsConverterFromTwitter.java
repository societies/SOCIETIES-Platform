package org.societies.platform.socialdata.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.shindig.social.core.model.AccountImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendsConverterFromTwitter implements FriendsConverter{
	
	
	
	public List<Person> load(String  data){
		List <Person> friends = new ArrayList<Person>();
		Account tw = new AccountImpl();
		tw.setDomain("twitter.com");
		List<Account> accounts = new ArrayList<Account>();
		accounts.add(tw);
		
	    
	    try{
	    	JSONObject jdata  = new JSONObject(data);
	    	if (jdata.has("error")){
				return new ArrayList<Person>();
			}
	    	
	    	JSONArray  jfriends  =  null;
	    	if (jdata.has("friends")){
	    	                PersonConverterFromTwitter converter = new PersonConverterFromTwitter();
				jfriends = jdata.getJSONArray("friends");
				for (int i=0; i<jfriends.length();i++){
					JSONObject jfriend = jfriends.getJSONObject(i);
					Person p = converter.load(jfriend.toString());
					
//					
//					Person p = new PersonImpl();
//					p.setId("twitter:"+jfriend.getString("id"));
//					p.setRelationshipStatus("friend");
//					p.setName(new NameImpl(jfriend.getString("name")));
//					p.setAccounts(accounts);
					//System.out.println(">>> new Friends"+p.getName().getFormatted());
					friends.add(p);
				}	
			}
			else if (jdata.has("ids")){
				jfriends = jdata.getJSONArray("ids");
				for (int i=0; i<jfriends.length();i++){
					Person p = new PersonImpl();
					p.setId("twitter:"+jfriends.getString(i));
					p.setRelationshipStatus("friend");
					p.setAccounts(accounts);
					//System.out.println(">>> new Friends"+p.getName().getFormatted());
					friends.add(p);
				}	
			}else {
				jfriends = new JSONArray(data);
			}
	    	
	    	
	    }
	    catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return friends;
	}
	
}
