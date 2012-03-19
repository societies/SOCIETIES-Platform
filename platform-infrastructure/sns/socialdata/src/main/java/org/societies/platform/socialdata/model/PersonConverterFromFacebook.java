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


public class PersonConverterFromFacebook implements PersonConverter{
	
	public static String USERNAME 		= "username";
	public static String LOCATION		= "location";
	public static String RELIGION		= "religion";
	public static String RELATIONSHIP 	= "relationship_status";
	public static String SPORTS  		= "sports";
	public static String SPORT_NAME 	= "name";
	public static String ID 			= "id";
	public static String NAME 			= "name";
	public static String FIRSTNAME 		= "first_name";
	public static String LASTNAME 		= "last_name";
	public static String GENDER 		= "gender";
	public static String UCT 			= "updated_time";
	public static String BIO 			= "bio";
	public static String BIRTHDAY		= "birthday";
	public static String EMAIL			= "email";
	
	
	// portable contact ids
	public static String GIVENAME 		= "givenName";
	public static String FAMILYNAME		= "familyName";
	
	
	
	private JSONObject db;
	private Person person;
	
	public Person load(JSONObject db){
	
		person = new PersonImpl();
		this.db = db;
		
		try{
			
			person.setId(db.getString(ID));
			//if(db.has(UCT)) person.setUtcOffset(db.getLong(UCT));
			if (db.has(BIO))		 	person.setAboutMe(db.getString(BIO));
			if (db.has(SPORTS)) 	 	person.setSports(setSports(db.getString(SPORTS)));
			if (db.has(RELATIONSHIP)) 	person.setRelationshipStatus(db.getString(RELATIONSHIP));
			if (db.has(RELIGION))       person.setReligion(db.getString(RELIGION));
			if (db.has(LOCATION))		person.setCurrentLocation(setLocation(db.getString(LOCATION)));
			
			
			
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		person.setName(genName());
		person.setGender(genGender());
		person.setEmails(genEmails());
		person.setActivities(genActivities());
		
		setAccount();  // Set Facebook Account
			
		return person;
	}
	
	private List<String> genActivities() {
		List<String> activities = new ArrayList<String>();
//		try{
//			
//			
//		}
//		catch (JSONException e) {
//			e.printStackTrace();
//		}
		
		return activities;
	}

	private void setAccount(){
		Account account = new AccountImpl();
		try{
			account.setDomain("facebook.com");
			account.setUsername(db.getString(USERNAME));
			account.setUserId(db.getString(ID));
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		//Add the account facebook
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

	private List<String> setSports(String data) {
		JSONArray sports = null;
		List<String> sportList = null;
		try {
			
			 sports = new JSONArray(data);
			 sportList= new ArrayList<String>();
			 
			 for(int i=0; i< sports.length(); i++){
				 JSONObject sport = sports.getJSONObject(i);
				 sportList.add(sport.getString(SPORT_NAME));
			 }
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		return sportList;
	}

	private ArrayList genEmails(){
		
		ArrayList emails = new ArrayList();
		try{
			if (!db.has(EMAIL)) return null;
			
			
			HashMap<String, String> email = new HashMap<String, String>();
			email.put("value", db.getString(EMAIL));
			email.put("type", "home");
			email.put("primary", "true");
			emails.add(email);
		}
		catch(JSONException ex){}
		
		return emails;
	}
	
	private Name genName(){
		Name name = new NameImpl();
		if (getString(NAME)!=null) {
			person.setDisplayName(getString(NAME));
			name.setFormatted(getString(NAME));
		}
		
		if (getString(FIRSTNAME)!=null) name.setGivenName(getString(FIRSTNAME));
		if (getString(FAMILYNAME)!=null) name.setGivenName(getString(FAMILYNAME));
		
		
		return name;
	}
	
	private Gender genGender(){
		
		if (db.has(GENDER)){
			if (getString(GENDER)=="male")
				return Gender.male;
			else
				return Gender.female;
		}
		
		//?????
		return Gender.male;	
	}
	
	
	private ArrayList emails(){
		ArrayList mails = new ArrayList();
		
		try{
			HashMap<String, String> mail = new HashMap<String, String>();
			mail.put("value", db.getString(EMAIL));
			mail.put("type", "home");
			mail.put("primary","true");
			mails.add(mail);
		}
		catch(Exception ex){}
		
		return mails;
		
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
