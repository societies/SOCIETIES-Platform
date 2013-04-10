package org.societies.platform.socialdata.converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.shindig.social.core.model.AccountImpl;
import org.apache.shindig.social.core.model.ActivityObjectImpl;
import org.apache.shindig.social.core.model.AddressImpl;
import org.apache.shindig.social.core.model.ListFieldImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.OrganizationImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Organization;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Person.Gender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PersonConverterFromLinkedin implements PersonConverter {

	public static String STATUS_COUNT 	= "statuses_count";
	public static String LANGUAGE		= "languages";
	public static String ID 			= "id";
	
	public static String FIRST_NAME		= "firstName";
	public static String LAST_NAME		= "lastName";
	
	
	public static String SCREEN_NAME	= "screen_name";
	public static String CREATED_AT 	= "created_at";
	public static String LOCATION		= "location";
	public static String FRIENDS_COUNT	= "friends_count";
	public static String IMAGE			= "profile_image_url";
	public static String FOLLOWERS_COUNT= "followers_count";
	public static String STATUSES_COUNT	= "statuses_count";
	public static String DESCRIPTION	= "description";
	public static String TIMEZONE		= "time_zone";
	public static String BIRTHDAY		= "birthday";
	public static String EMAIL			= "email";
	public static String GENDER 		= "gender";

	public static String STATUS 		= "status";
	public static String COORDINATES	= "coordinates";
	public static String TEXT			= "text";
	public static String CONTRIBUTIONS	= "contributors";
	public static String GEO			= "geo";
	public static String RETWEETED		= "retweeted";
	public static String TWEET_ID		= "id";


	private String     rawData;
	private JSONObject db;
	private Person 	   person;
	private List<Account> accounts;
	
	
	public PersonConverterFromLinkedin() {
	    	Account linkedinAccounts = new AccountImpl();
	    	linkedinAccounts.setDomain("linkedin.com");
		accounts = new ArrayList<Account>();
		accounts.add(linkedinAccounts);
	}

	public Person load(String data){

		person = new PersonImpl();
		person.setAccounts(accounts);
		
		
		this.rawData = data;

		try{
		    
		    	db = new JSONObject(data);
			
			if (db.has("error"))
				return person;
			
			
			person.setId("linkedin:" + db.getString(ID));
	
			ActivityObject providerObj = new ActivityObjectImpl();
			providerObj.setUrl("www.linkedin.com");
			providerObj.setId("linkedin.com");
			providerObj.setDisplayName("Linkedin");
			
			/// SET NAME
			Name name = new NameImpl();
			String formattedName= "";
			if (db.has("firstName")){
				name.setGivenName(db.getString("firstName"));
				formattedName=name.getGivenName();
			}
			else 
				name.setGivenName(db.getString("--"));
			
			if (db.has("lastName")){
				name.setFamilyName(db.getString("lastName"));
				if (formattedName.length()>0) formattedName += " ";
				formattedName += name.getFamilyName();
			}
			else
				name.setFamilyName("--");
			
			if (db.has("formattedName"))
				name.setFormatted(db.getString("formattedName"));
			else
				name.setFormatted(formattedName);
		
			
			person.setName(name);
			person.setDisplayName(name.getFormatted());
			
			
			if (db.has("summary")){
				person.setAboutMe(db.getString("summary"));
			}
			
			
			if (db.has("skills")){
				JSONObject jSkills = db.getJSONObject("skills");
				JSONArray list = jSkills.getJSONArray("values");
				String skills="";
				for(int i=0; i<list.length();i++){
					if (skills.length()>0) skills+=",";
					skills+=list.getJSONObject(i).getJSONObject("skill").getString("name");
				}
				person.setJobInterests(skills);
			}
			
			if (db.has("dateOfBirth")){
				
			}
			
			if (db.has("headline")){
				
			}
			
			if (db.has("honors")){
				
			}
			
			if (db.has("specialties")){
				
			}
			
			if (db.has("pictureUrl")){
				
			}
			
			if (db.has("associations")){
				
			}
			
			if (db.has("educations")){
				
			}
			
			if(db.has("industry")){
				List<Organization> orgs = new ArrayList<Organization>();
				Organization org = new OrganizationImpl();
				org.setName(db.getString("industry"));
				orgs.add(org);
				person.setOrganizations(orgs);
			}

			
			if (db.has("interests"))  {
				List<String> interests = new ArrayList<String>();
				try{
					for (String i : db.getString("interests").split(",")) 
						interests.add(i);
				}catch(Exception e){};
				person.setInterests(interests);
			}
			
			if (db.has("emailAddress")){
				getMails(db.getString("emailAddress"));
			}
			
			if (db.has("publicProfileUrl")){
				person.setProfileUrl(db.getString("publicProfileUrl"));
			}
			
			if (db.has("languages")) genLanguages();
			
		      
			
			if (db.has("pictureUrl")){
			    person.setThumbnailUrl(db.getString("pictureUrl"));
			}
//			
//			if (db.has(SCREEN_NAME))	person.setDisplayName(db.getString(SCREEN_NAME));
//			if (db.has(DESCRIPTION))	person.setAboutMe(db.getString(DESCRIPTION));
//			if (db.has(LOCATION))		person.setCurrentLocation(setLocation(db.getString(LOCATION)));
//			if (db.has(EMAIL))			person.setEmails(getMails(db.getString(EMAIL)));
//			if (db.has(BIRTHDAY))		person.setBirthday(getBirthDay(db.getString(BIRTHDAY)));
//			if (db.has(GENDER))			person.setGender(gender(db.getString(GENDER)));

		}
		catch (JSONException e) {
			e.printStackTrace();
		}

//		setAccount();  // Set Twitter Account
//		person.setLanguagesSpoken(genLanguages());
//		//		System.out.println("profile:\n"+person.getTurnOns().toString());
		return person;
	}



	private List<String> genLanguages(){

		ArrayList<String> langs = new ArrayList<String>();
		try {
			JSONArray list = db.getJSONObject("languages").getJSONArray("values");
			for (int i=0; i<list.length(); i++){
				JSONObject lang = list.getJSONObject(i);
				langs.add(lang.getJSONObject("language").getString("name"));
			}
		
		}
		catch (JSONException e) {}
		return langs;
	}

	private void setAccount(){
		Account account = new AccountImpl();
		try{
			account.setDomain("linkedin.com");
			account.setUsername("me");
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

			address.setFormatted(data);

		}
		catch(Exception ex){}

		return address;
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

	public String getString(String key){
		try {
			if (db.has(key)){
				return db.getString(key);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";

	}

	public String getRawData(){
		return rawData;
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

	private Gender gender(String g){
		if (g.equals("male"))
			return Gender.male;
		else
			return Gender.female;
	}

	private Date getBirthDay(String date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return df.parse(date);
		} catch (ParseException e) {

		}
		return null;

	}
}
