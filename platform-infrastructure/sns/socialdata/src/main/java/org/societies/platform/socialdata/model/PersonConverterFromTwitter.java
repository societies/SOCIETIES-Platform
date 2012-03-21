package org.societies.platform.socialdata.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.shindig.social.core.model.AccountImpl;
import org.apache.shindig.social.core.model.AddressImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Person.Gender;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PersonConverterFromTwitter implements PersonConverter {

	public static String STATUS_COUNT 	= "statuses_count";
	public static String LANGUAGE		= "lang";
	public static String ID 			= "id";
	public static String NAME 			= "name";
	public static String SCREEN_NAME	= "screen_name";
	public static String CREATED_AT 	= "created_at";
	public static String LOCATION		= "location";
	public static String FRIENDS_COUNT	= "friends_count";
	public static String IMAGE			= "profile_image_url";
	public static String FOLLOWERS_COUNT= "followers_count";
	public static String STATUSES_COUNT	= "statuses_count";
	public static String DESCRIPTION	= "description";
	public static String TIMEZONE		= "time_zone";
	
	public static String STATUS 		= "status";
	public static String COORDINATES	= "coordinates";
	public static String TEXT			= "text";
	public static String CONTRIBUTIONS	= "contributors";
	public static String GEO			= "geo";
	public static String RETWEETED		= "retweeted";
	public static String TWEET_ID		= "id";
	
	
	
	private JSONObject db;
	private Person person;
	@Override
	public Person load(JSONObject db){
		
		person = new PersonImpl();
		this.db = db;
		
		try{
			
			person.setId(db.getString(ID));
			//if(db.has(UCT)) person.setUtcOffset(db.getLong(UCT));
			if (db.has(NAME))			person.setNickname(db.getString(NAME));
			if (db.has(DESCRIPTION))	person.setAboutMe(db.getString(DESCRIPTION));
			if (db.has(LOCATION))		person.setCurrentLocation(setLocation(db.getString(LOCATION)));
			if (db.has(SCREEN_NAME))	person.setDisplayName(db.getString(SCREEN_NAME));
			if (db.has(SCREEN_NAME))	person.setDisplayName(db.getString(SCREEN_NAME));
			
			
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		setAccount();  // Set Twitter Account
		person.setLanguagesSpoken(genLanguages());
		return person;
	}
	
	
	
	private List<String> genLanguages(){
		
		ArrayList<String> langs = new ArrayList<String>();
		try {
			if(!db.has(LANGUAGE)) 
				return null;
			langs.add(db.getString(LANGUAGE));
		} catch (JSONException e) {}
		return langs;
	}

	private void setAccount(){
		Account account = new AccountImpl();
		try{
			account.setDomain("twitter.com");
			account.setUsername(db.getString(NAME));
			account.setUserId(db.getString(ID));
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		//Add the twitter account
		List<Account> accounts = new ArrayList<Account>();
		accounts.add(account);
		person.setAccounts(accounts);
	}
	
	private Address setLocation(String data) {
		Address address = new AddressImpl();
		try{
			
			JSONObject loc = new JSONObject(data);
			address.setFormatted(loc.getString("name"));
		
		}
		catch(Exception ex){}
		
		return address;
	}
	
	private Object getData(String key){
		try {
			if (db.has(key)){
				return db.get(key);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	private String getString(String key){
		try {
			if (db.has(key)){
				return db.getString(key);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";

	}

}
