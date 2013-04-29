package org.societies.platform.socialdata.converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.shindig.social.core.model.AccountImpl;
import org.apache.shindig.social.core.model.AddressImpl;
import org.apache.shindig.social.core.model.ListFieldImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Person.Gender;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PersonConverterFromFacebook implements PersonConverter{
	
	public static String WORKS 			= "works";
	public static String ACCOUNTS 		= "accounts";
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
	public static String BIO 			= "headline";
	public static String BIRTHDAY		= "birthday";
	public static String EMAIL			= "email";
	public static String PROFILELINK 	= "link";
	public static String PHOTOS 		= "photos";
	public static String TURNONS		= "turnOns";
	public static String INTERESTS		= "interests";
	public static String MUSIC		    = "music";
	public static String BOOKS		    = "books";
	public static String PICTURE		    = "picture";
	
	// portable contact ids
	public static String GIVENAME 		= "givenName";
	public static String FAMILYNAME		= "familyName";
	
	
	private JSONObject 	db;
	private String     	rawData;
	private Person 		person;
	
	
	
	public Person load(String  data){
	
	   	person = new PersonImpl();
		this.rawData = data;
		
		try{
			
			db = new JSONObject(this.rawData);
			person.setId("facebook:"+db.getString(ID));
			
			//if(db.has(UCT)) person.setUtcOffset(db.getLong(UCT));
			if (db.has(BIO))		person.setAboutMe(db.getString(BIO));
			if (db.has(SPORTS)) 	 	person.setSports(setSports(db.getString(SPORTS)));
			if (db.has(RELATIONSHIP)) 	person.setRelationshipStatus(db.getString(RELATIONSHIP));
			if (db.has(RELIGION))           person.setReligion(db.getString(RELIGION));
			if (db.has(LOCATION))		person.setCurrentLocation(setLocation(db.getString(LOCATION)));
			if (db.has(ACCOUNTS))		person.setAccounts(setAccounts(db.getString(ACCOUNTS)));
			if (db.has(WORKS))		person.setActivities(setActivities(WORKS));
			if (db.has(PROFILELINK))	person.setProfileUrl(db.getString(PROFILELINK));
			if (db.has(BIRTHDAY))		person.setBirthday(getBirthDay(db.getString(BIRTHDAY)));
			if (db.has(GENDER))		person.setGender(gender(db.getString(GENDER)));
			if (db.has(EMAIL))		person.setEmails(getMails(db.getString(EMAIL)));
			if (db.has(PHOTOS))		person.setPhotos(getPhotos(db.getString(PHOTOS)));
			if (db.has(TURNONS))		person.setTurnOns(jarrayToList(db.getString(TURNONS)));
			if (db.has(MUSIC))		person.setMusic(jarrayToList(db.getString(MUSIC)));
			if (db.has(INTERESTS))	        person.setInterests(jarrayToList(db.getString(INTERESTS)));
			if (db.has(BOOKS))	    	person.setBooks(jarrayToList(db.getString(BOOKS)));					
			
			if (db.has(PICTURE))		{
			    try{
			          person.setThumbnailUrl(db.getJSONObject(PICTURE).getJSONObject("data").getString("url"));
			    }catch(Exception ex){}
			    
			 }
			
			setName();
			setAccount();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		person.setActivities(genActivities());
		
		
			
		return person;
	}
	
	private void setName(){
	    Name name = new NameImpl();
	    try {
		
		String formattedName= "";
		
		if (db.has("first_name")){
		    name.setGivenName(db.getString("first_name"));
		    formattedName=db.getString("first_name");
		}
		
		if (db.has("last_name")){
		    name.setFamilyName(db.getString("last_name"));
		    if (formattedName.length()>0) formattedName+=" ";
		    formattedName+= db.getString("last_name");
		}
	      
		name.setFormatted(formattedName);
		person.setDisplayName(formattedName);
	      
	    }
	    catch (JSONException e) {
		e.printStackTrace(); 
	    }
	    person.setName(name);
	}
	
	private void setAccount(){
		Account account = new AccountImpl();
		try{
			account.setDomain("facebook.com");
			String name = "";
			if(db.has(FIRSTNAME))
				name = name + db.getString(FIRSTNAME);
			if(db.has(LASTNAME))
				name = name + " "+db.getString(LASTNAME);
			if (db.has(USERNAME))
			    account.setUsername(db.getString(USERNAME));
			else
			    account.setUsername(name);
			
			account.setUserId(db.getString(ID));
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		//Add the Facebook account
		List<Account> accounts = new ArrayList<Account>();
		accounts.add(account);
		person.setAccounts(accounts);
	}
	
	private List<String> jarrayToList(String data) {
		
		List<String> list = new ArrayList<String>();
		try {
			JSONArray jdata = new JSONArray(data);
			for(int i=0; i<jdata.length();i++){
				list.add(jdata.getString(i));
			}
			
		}
		catch (JSONException e) {
			
		}
		return list;
	}

	private List<ListField> getPhotos(String data) {
		List<ListField> photos = new ArrayList<ListField>();
		try {
			JSONArray json_photos = new JSONArray(data);
			for(int i=0; i<json_photos.length();i++){
				JSONObject p = (JSONObject) json_photos.get(i);
				
				ListField photo = new ListFieldImpl();
				photo.setPrimary(p.getBoolean("primary"));
				photo.setType(p.getString("type"));
				photo.setValue(p.getString("value"));
				photos.add(photo);
				
				
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		return photos;
	}

	private Date getBirthDay(String date) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		try {
			return df.parse(date);
			} catch (ParseException e) {
			
		}
		return null;

	}

	private List<String> setActivities(String data) {
		List<String> works = new ArrayList<String>();
		try {
			JSONArray json_works = new JSONArray(data);
			for(int i=0; i<json_works.length();i++){
				JSONObject work = (JSONObject) json_works.get(i);
				works.add(work.getString("description"));
			}
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return works;
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

	private List<Account> setAccounts(String data){
		
		List<Account> accounts = new ArrayList<Account>();
		
		// not usefull!
		return accounts;
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

	private List<ListField> getMails(String mail){
		
		List<ListField> emails = new ArrayList<ListField>();
		ListField email = new ListFieldImpl();
		email.setPrimary(true);
		email.setType("home");
		email.setValue(mail);
		emails.add(email);
		return emails;
	}
	
	private Name genName(){
		Name name = null;
		if (getString(NAME)!=null) {
			name = new NameImpl(getString(NAME));
			person.setDisplayName(getString(NAME));
			name.setFormatted(getString(NAME));
		}
		if (name==null) name = new NameImpl();
		
		if (getString(FIRSTNAME)!=null)  name.setGivenName(getString(FIRSTNAME));
		if (getString(LASTNAME) !=null) name.setFamilyName(getString(LASTNAME));
		
		
		return name;
	}
	
	private Gender gender(String g){
			if ("male".equals(g))
				return Gender.male;
			else
				return Gender.female;
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
