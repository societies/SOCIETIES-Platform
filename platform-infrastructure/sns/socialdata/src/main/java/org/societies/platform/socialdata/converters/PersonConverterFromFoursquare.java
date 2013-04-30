package org.societies.platform.socialdata.converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.shindig.social.core.model.AccountImpl;
import org.apache.shindig.social.core.model.AddressImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Person.Gender;
import org.json.JSONException;
import org.json.JSONObject;
import org.societies.platform.socialdata.model.FieldsUtils;

public class PersonConverterFromFoursquare implements PersonConverter {

	public static String STATUS_COUNT 	= "statuses_count";
	public static String LANGUAGE		= "lang";
	public static String ID 			= "id";
	public static String NAME 			= "name";
	public static String SCREEN_NAME	= "screen_name";
	public static String CREATED_AT 	= "created_at";
	public static String HOME_LOCATION	= "homeCity";
	public static String LOCATION		= "location";
	public static String FRIENDS_COUNT	= "friends_count";
	public static String IMAGE			= "profile_image_url";
	public static String FOLLOWERS_COUNT= "followers_count";
	public static String STATUSES_COUNT	= "statuses_count";
	public static String DESCRIPTION	= "bio";
	public static String TIMEZONE		= "time_zone";
	public static String BIRTHDAY		= "birthday";
	public static String EMAIL			= "email";
	public static String GENDER 		= "gender";
	public static String FIRSTNAME 		= "firstName";
	public static String LASTNAME 		= "lastName";
	public static String CONTACT		= "contact";

	public static String STATUS 		= "status";
	public static String COORDINATES	= "coordinates";
	public static String TEXT			= "text";
	public static String CONTRIBUTIONS	= "contributors";
	public static String GEO			= "geo";
	public static String RETWEETED		= "retweeted";
	public static String TWEET_ID		= "id";


	private JSONObject response;
	private Person 	   person;
	private JSONObject db = null;
	private List<Account> accounts;
	
	public PersonConverterFromFoursquare() {
	    	Account linkedinAccounts = new AccountImpl();
	    	linkedinAccounts.setDomain("foursquare.com");
		accounts = new ArrayList<Account>();
		accounts.add(linkedinAccounts);
	}
	
	public Person load(String data){

	    
	        
		person = new PersonImpl();
		person.setAccounts(accounts);
		
		try{
			response = new JSONObject(data);
			if (response.has("response")) {
				JSONObject jresponse = response.getJSONObject("response");
				if (jresponse.has("user")) {
				    db= jresponse.getJSONObject("user");
				    return genPerson();	
				}
			}
			
			
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		
		return person;
	}
	
	public Person load(JSONObject data){

		person = new PersonImpl();

		try{
		    
		    db = data;
		    return genPerson();
			
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		
		return person;
	}
	
	
	
	public Person genPerson() throws JSONException {
	        
//	        System.out.println("Person:"+db.getString(ID));
	        person.setId("foursquare:" +db.getString(ID));

		if (db.has(DESCRIPTION))	person.setAboutMe(db.getString(DESCRIPTION));
		if (db.has(GENDER))			person.setGender(gender(db.getString(GENDER)));
		if (db.has(HOME_LOCATION))	person.setAddresses(setAddress(db.getString(HOME_LOCATION)));
		
		setAccount();  
		
		if (db.has(CONTACT))		{
		    JSONObject contacts = db.getJSONObject(CONTACT);
		    if (contacts.has("email")){
			String mail= contacts.getString("email");
			person.setEmails(FieldsUtils.genList(mail, "home", true));
		    }
			
		    if (contacts.has("phone")){
			String phone = contacts.getString("phone");
			person.setPhoneNumbers(FieldsUtils.genList(phone, "phone", true));	
		    }
		    
		    List<ListField> sn_connected = new ArrayList<ListField>();
		    if (contacts.has("twitter")){
			String tw= contacts.getString("twitter");
			sn_connected.add(FieldsUtils.genListField(tw, "SocialNetwork", false));
		    }
		    if (contacts.has("facebook")){
			String fb= contacts.getString("facebook");
			sn_connected.add(FieldsUtils.genListField(fb, "SocialNetwork", false));
		    }
		    
		    person.setRelationshipStatus(db.getString("relationship"));
		    
		        				    	
		}
		person.setName(genName());
		
		setThumb();
		
		return person;
	}



	
	private void setThumb() {
	    if (db.has("photo")){
		
		try {
		
		    
		    
		    Object photo = db.get("photo");
		    if (photo instanceof JSONObject){
			JSONObject jphoto = db.getJSONObject("photo");
			person.setThumbnailUrl(jphoto.getString("prefix") + "original" + jphoto.getString("suffix"));
		    }
		    else person.setThumbnailUrl((db.getString("photo")));
		} catch (JSONException e) {
		    
		    e.printStackTrace();
		}
		
	    }
	    
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
			account.setDomain("foursquare.com");
			String name = "";
			if(db.has(FIRSTNAME))
				name = name + db.getString(FIRSTNAME);
			if(db.has(LASTNAME))
				name = name + " "+db.getString(LASTNAME);
			
			account.setUsername(name);
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

	private List<Address> setAddress(String data) {
		List<Address> addresses = new ArrayList<Address>();
		try{
			AddressImpl address = new AddressImpl();
			address.setFormatted(data);
			addresses.add(address);
		}
		catch(Exception ex){}

		return addresses;
	}
	
	

	public Object getData(String key){
		try {
			if (db.has(key)){
				return db.get(key);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	

	private Date getBirthDay(String date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return df.parse(date);
		} catch (ParseException e) {

		}
		return null;

	}
	
	private Name genName(){
		Name name = null;
		if (getString(NAME)!=null) {
			name = new NameImpl(getString(NAME));
			person.setDisplayName(getString(NAME));
			name.setFormatted(getString(NAME));
		}
		
		if (getString(FIRSTNAME)!=null) name.setGivenName(getString(FIRSTNAME));
		if (getString(LASTNAME) !=null) name.setFamilyName(getString(LASTNAME));
		if (name.getFormatted()=="")
			name.setFormatted(name.getGivenName()+" "+ name.getFamilyName());
			
		
		return name;
	}
	
	private Gender gender(String g){
			if (g.equals("male"))
				return Gender.male;
			else
				return Gender.female;
	}
	
	private String getString(String key){
		try {
			if (db.has(key)){
//				System.out.println(db.getString(key).toString());
				return db.getString(key);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";

	}
}
