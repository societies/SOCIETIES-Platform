/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.platform.socialdata.converters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.shindig.social.core.model.AccountImpl;
import org.apache.shindig.social.core.model.AddressImpl;
import org.apache.shindig.social.core.model.ListFieldImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.OrganizationImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.core.model.UrlImpl;
import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Organization;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Person.Gender;
import org.apache.shindig.social.opensocial.model.Url;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convert data from Google+ in JSON to Person.
 *
 * @author Edgar Domingues (PTIN)
 *
 */
public class PersonConverterFromGooglePlus implements PersonConverter {
	
	private static final Logger LOG = LoggerFactory.getLogger(PersonConverterFromGooglePlus.class);

	public static final String ID = "id";
	public static final String DISPLAY_NAME = "displayName";
	public static final String NAME = "name";
	public static final String FORMATTED = "formatted";
	public static final String FAMILY_NAME = "familyName";
	public static final String GIVEN_NAME = "givenName";
	public static final String MIDDLE_NAME = "middleName";
	public static final String HONORIFIC_PREFIX = "honorificPrefix";
	public static final String HONORIFIC_SUFFIX = "honorificSufix";
	public static final String NICKNAME = "nickname";
	public static final String GENDER = "gender";
	public static final String MALE = "male";
	public static final String FEMALE = "female";
	public static final String OTHER = "other";
	public static final String BIRTHDAY = "birthday";
	public static final String BIRTHDAY_DATE_FORMAT = "yyyy-MM-dd";
	public static final String CURRENT_LOCATION = "currentLocation";
	public static final String URL = "url";
	public static final String IMAGE = "image";
	public static final String LANGUAGES_SPOKEN = "languagesSpoken";
	public static final String HAS_APP = "hasApp";
	public static final String ABOUT_ME = "aboutMe";
	public static final String RELATIONSHIP_STATUS = "relationshipStatus";
	public static final String SINGLE = "single";
	public static final String IN_A_RELATIONSHIP = "in_a_relationship";
	public static final String ENGAGED = "engaged";
	public static final String MARRIED = "married";
	public static final String ITS_COMPLICATED = "its_complicated";
	public static final String OPEN_RELATIONSHIP = "open_relationship";
	public static final String WINDOWED = "windowed";
	public static final String IN_DOMESTIC_PARTNERSHIP = "in_domestic_partnership";
	public static final String IN_CIVIL_UNION = "in_civil_union";
	public static final String URLS = "urls";
	public static final String VALUE = "value";
	public static final String TYPE = "type";
	public static final String HOME = "home";
	public static final String WORK = "work";
	public static final String BLOG = "blog";
	public static final String PROFILE = "profile";
	public static final String PRIMARY = "primary";
	public static final String ORGANIZATIONS = "organizations";	
	public static final String DEPARTMENT = "department";
	public static final String TITLE = "title";
	public static final String SCHOOL = "school";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String ORGANIZATION_START_DATE_FORMAT = "yyyy-MM-dd";
	public static final String ORGANIZATION_END_DATE_FORMAT = "yyyy-MM-dd";
	public static final String LOCATION = "location";
	public static final String DESCRIPTION = "description";
	public static final String PLACES_LIVED = "placesLived";
	public static final String TAGLINE = "tagline";
	public static final String EMAILS = "emails";
	public static final String OBJECT_TYPE = "objectType";
	public static final String ETAG = "etag";
	public static final String ERROR = "error";
	
	@Override
	public Person load(String data) {
		Person person = new PersonImpl();
		
		try{
			JSONObject db = new JSONObject(data);
			
			if (db.has(ERROR))
				return person;
			
			person.setId("googleplus:"+db.getString(ID));
			person.setDisplayName(db.getString(DISPLAY_NAME));			
			parseName(db, person);
			if(db.has(NICKNAME)) person.setNickname(NICKNAME);
			parseBirthday(db, person);
			parseGender(db, person);
			parseCurrentLocation(db, person);
			if(db.has(URL)) person.setProfileUrl(db.getString(URL));
			if(db.has(IMAGE)) person.setThumbnailUrl(db.getJSONObject(IMAGE).getString(URL));	
			parseLanguagesSpoken(db, person);
			if(db.has(HAS_APP)) person.setHasApp(db.getBoolean(HAS_APP));
			if(db.has(ABOUT_ME)) person.setAboutMe(db.getString(ABOUT_ME));
			if(db.has(RELATIONSHIP_STATUS)) person.setRelationshipStatus(db.getString(RELATIONSHIP_STATUS));
			parseUrls(db, person);
			parseOrganizations(db, person);			
			parsePlacesLived(db, person);
			if(db.has(TAGLINE)) person.setStatus(db.getString(TAGLINE));
			parseEmails(db, person);	
			person.setAccounts(setAccounts());
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		return person;
	}
	
	private List<Account> setAccounts(){
		Account account = new AccountImpl();
		account.setDomain("foursquare.com");
		
		//Add the twitter account
		List<Account> accounts = new ArrayList<Account>();
		accounts.add(account);
		return accounts;
	}
	
	private void parseName(JSONObject json, Person person) throws JSONException { // middleName not supported by shindig
		if(json.has(NAME)) {
			JSONObject jsonName = json.getJSONObject(NAME);	
			Name name = new NameImpl();
			if(jsonName.has(FORMATTED))
				name.setFormatted(jsonName.getString(FORMATTED));
			if(jsonName.has(FAMILY_NAME))
				name.setFamilyName(jsonName.getString(FAMILY_NAME));
			if(jsonName.has(GIVEN_NAME))
				name.setGivenName(jsonName.getString(GIVEN_NAME));																			 	
			if(jsonName.has(HONORIFIC_PREFIX))
				name.setHonorificPrefix(jsonName.getString(HONORIFIC_PREFIX));
			if(jsonName.has(HONORIFIC_SUFFIX))
				name.setHonorificSuffix(jsonName.getString(HONORIFIC_SUFFIX));
			person.setName(name);
		}
	}
	
	private void parseBirthday(JSONObject json, Person person) throws JSONException {
		if(json.has(BIRTHDAY)) {
			String birthday = json.getString(BIRTHDAY);
			Date birthdayDate = parseDate(birthday, BIRTHDAY_DATE_FORMAT);
			if(birthdayDate != null)
				person.setBirthday(birthdayDate);
		}
	}
	
	private void parseGender(JSONObject json, Person person) throws JSONException { // gender 'other' not supported by shindig 
		if(json.has(GENDER)) {
			String gender = json.getString(GENDER);
			if(gender.equals("male"))
				person.setGender(Gender.male);
			else if(gender.equals("female"))
				person.setGender(Gender.male);
		}
	}
	
	private void parseCurrentLocation(JSONObject json, Person person) throws JSONException {
		if(json.has(CURRENT_LOCATION)) {
			String currentLocation = json.getString(CURRENT_LOCATION);
			Address currentLocationAddress = parseAddress(currentLocation);
			person.setCurrentLocation(currentLocationAddress);
		}
	}
	
	private void parseLanguagesSpoken(JSONObject json, Person person) throws JSONException {
		if(json.has(LANGUAGES_SPOKEN)) {
			JSONArray jsonLanguages = json.getJSONArray(LANGUAGES_SPOKEN);
			List<String> languagesSpoken = new ArrayList<String>(jsonLanguages.length());
			for(int i=0; i<jsonLanguages.length(); i++)
				languagesSpoken.add(jsonLanguages.getString(i));
			person.setLanguagesSpoken(languagesSpoken);
		}
	}
	
	private void parseUrls(JSONObject json, Person person) throws JSONException {
		if(json.has(URLS)) {
			JSONArray jsonUrls = json.getJSONArray(URLS);
			List<Url> urls = new ArrayList<Url>(jsonUrls.length());
			for(int i=0; i<jsonUrls.length(); i++) {
				Url url = new UrlImpl();
				if(jsonUrls.getJSONObject(i).has(VALUE))
					url.setValue(jsonUrls.getJSONObject(i).getString(VALUE));	
				if(jsonUrls.getJSONObject(i).has(TYPE))
					url.setType(jsonUrls.getJSONObject(i).getString(TYPE));
				if(jsonUrls.getJSONObject(i).has(PRIMARY))
					url.setPrimary(jsonUrls.getJSONObject(i).getBoolean(PRIMARY));
				urls.add(url);
			}
			person.setUrls(urls);
		}
	}
	
	private void parseOrganizations(JSONObject json, Person person) throws JSONException { // organizations[].department 	not supported by shindig
		if(json.has(ORGANIZATIONS)) {
			JSONArray jsonOrganizations = json.getJSONArray(ORGANIZATIONS);			
			List<Organization> organizations = new ArrayList<Organization>(jsonOrganizations.length());
			for(int i=0; i<jsonOrganizations.length(); i++) {
				Organization organization = new OrganizationImpl();
				if(jsonOrganizations.getJSONObject(i).has(NAME))
					organization.setName(jsonOrganizations.getJSONObject(i).getString(NAME));
				if(jsonOrganizations.getJSONObject(i).has(TITLE))
					organization.setTitle(jsonOrganizations.getJSONObject(i).getString(TITLE));
				if(jsonOrganizations.getJSONObject(i).has(TYPE))
					organization.setType(jsonOrganizations.getJSONObject(i).getString(TYPE));
				if(jsonOrganizations.getJSONObject(i).has(START_DATE)) {
					Date startDate = parseDate(jsonOrganizations.getJSONObject(i).getString(START_DATE), ORGANIZATION_START_DATE_FORMAT);
					if(startDate != null)
						organization.setStartDate(startDate);
				}
				if(jsonOrganizations.getJSONObject(i).has(END_DATE)) {
					Date endDate = parseDate(jsonOrganizations.getJSONObject(i).getString(END_DATE), ORGANIZATION_END_DATE_FORMAT);
					if(endDate != null)
						organization.setEndDate(endDate);
				}
				if(jsonOrganizations.getJSONObject(i).has(LOCATION))
					organization.setAddress(parseAddress(jsonOrganizations.getJSONObject(i).getString(LOCATION)));
				if(jsonOrganizations.getJSONObject(i).has(DESCRIPTION))
					organization.setDescription(jsonOrganizations.getJSONObject(i).getString(DESCRIPTION));
				if(jsonOrganizations.getJSONObject(i).has(PRIMARY))
					organization.setPrimary(jsonOrganizations.getJSONObject(i).getBoolean(PRIMARY));
				
				organizations.add(organization);
			}
			
			person.setOrganizations(organizations);	
		}
	}
	
	private void parsePlacesLived(JSONObject json, Person person) throws JSONException {
		if(json.has(PLACES_LIVED)) {
			JSONArray jsonPlaces = json.getJSONArray(PLACES_LIVED);
			List<Address> addresses = new ArrayList<Address>(jsonPlaces.length());
			for(int i=0; i<jsonPlaces.length(); i++) {
				Address address;
				if(jsonPlaces.getJSONObject(i).has(VALUE))
					address = parseAddress(jsonPlaces.getJSONObject(i).getString(VALUE));
				else
					address = new AddressImpl();
				if(jsonPlaces.getJSONObject(i).has(PRIMARY))
					address.setPrimary(jsonPlaces.getJSONObject(i).getBoolean(PRIMARY));
				addresses.add(address);
			}				
			person.setAddresses(addresses);
			
		}
	}
	
	private void parseEmails(JSONObject json, Person person) throws JSONException {
		if(json.has(EMAILS)) {
			JSONArray jsonEmails = json.getJSONArray(EMAILS);
			List<ListField> emails = new ArrayList<ListField>(jsonEmails.length());
			for(int i=0; i<jsonEmails.length(); i++) {
				ListField email = new ListFieldImpl();
				if(jsonEmails.getJSONObject(i).has(VALUE))
					email.setValue(jsonEmails.getJSONObject(i).getString(VALUE));
				if(jsonEmails.getJSONObject(i).has(TYPE))
					email.setType(jsonEmails.getJSONObject(i).getString(TYPE));
				if(jsonEmails.getJSONObject(i).has(PRIMARY))
					email.setPrimary(jsonEmails.getJSONObject(i).getBoolean(PRIMARY));
				emails.add(email);
			}
			person.setEmails(emails);
		}
	}
	
	private Date parseDate(String dateString, String format) {
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			date = sdf.parse(dateString);
			
		} catch (ParseException e) {
			LOG.error("Invalid date '"+dateString+"' for format '"+format+"'.", e);
		}
		return date;
	}
	
	private Address parseAddress(String addressString) {
		Address address = new AddressImpl(addressString);
		return address;
	}

}
