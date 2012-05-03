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
import org.json.JSONException;
import org.json.JSONObject;

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

	public Person load(String data){

		person = new PersonImpl();

		try{
			response = new JSONObject(data);
//			System.out.println(response);
			
			if (response.has("response")) {
				JSONObject user = (JSONObject) response.get("response");
//				System.out.println(user);
				if (user.has("user")){
					db = (JSONObject) user.get("user");
//					System.out.println(db);
					person.setId(db.getString(ID));

					if (db.has(DESCRIPTION))	person.setAboutMe(db.getString(DESCRIPTION));
					if (db.has(GENDER))			person.setGender(gender(db.getString(GENDER)));
					if (db.has(HOME_LOCATION))	person.setAddresses(setAddress(db.getString(HOME_LOCATION)));
					if (db.has(CONTACT))		person.setEmails(getMails(db.getString(CONTACT)));
					person.setName(genName());
					setAccount();  
				}
			}
			
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		
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
			account.setDomain("foursquare.com");
			account.setUsername(db.getString(FIRSTNAME)+" "+db.getString(LASTNAME));
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

	private List<ListField> getMails(String mail) throws JSONException{

		
		JSONObject obj = new JSONObject(mail);
		
		List<ListField> emails = new ArrayList<ListField>();
		ListField email = new ListFieldImpl();
		email.setPrimary(true);
		email.setType("home");
		email.setValue(obj.get("email").toString());
		emails.add(email);
		return emails;
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
