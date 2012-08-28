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

import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Person.Gender;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Describe your class here...
 *
 * @author Edgar Domingues (PTIN)
 *
 */
public class PersonConverterFromGooglePlus implements PersonConverter {

	public static String ID = "id";
	public static String DISPLAY_NAME = "displayName";
	public static String NAME = "name";
	public static String FORMATTED = "formatted";
	public static String FAMILY_NAME = "familyName";
	public static String GIVEN_NAME = "givenName";
	public static String MIDDLE_NAME = "middleName";
	public static String HONORIFIC_PREFIX = "honorificPrefix";
	public static String HONORIFIC_SUFFIX = "honorificSufix";
	public static String NICKNAME = "nickname";
	public static String GENDER = "gender";
	public static String MALE = "male";
	public static String FEMALE = "female";
	public static String OTHER = "other";
	public static String BIRTHDAY = "birthday";
	public static String CURRENT_LOCATION = "currentLocation";
	public static String URL = "url";
	public static String IMAGE = "image";
	public static String LANGUAGES_SPOKEN = "languagesSpoken";
	public static String HAS_APP = "hasApp";
	public static String ABOUT_ME = "aboutMe";
	public static String RELATIONSHIP_STATUS = "relationshipStatus";
	public static String SINGLE = "single";
	public static String IN_A_RELATIONSHIP = "in_a_relationship";
	public static String ENGAGED = "engaged";
	public static String MARRIED = "married";
	public static String ITS_COMPLICATED = "its_complicated";
	public static String OPEN_RELATIONSHIP = "open_relationship";
	public static String WINDOWED = "windowed";
	public static String IN_DOMESTIC_PARTNERSHIP = "in_domestic_partnership";
	public static String IN_CIVIL_UNION = "in_civil_union";
	public static String URLS = "urls";
	public static String VALUE = "value";
	public static String TYPE = "type";
	public static String HOME = "home";
	public static String WORK = "work";
	public static String BLOG = "blog";
	public static String PROFILE = "profile";
	public static String PRIMARY = "primary";
	public static String ORGANIZATIONS = "organizations";
	public static String DEPARTMENT = "department";
	public static String TITLE = "title";
	public static String SCHOOL = "school";
	public static String START_DATE = "startDate";
	public static String END_DATE = "endDate";
	public static String LOCATION = "location";
	public static String DESCRIPTION = "description";
	public static String PLACES_LIVED = "placesLived";
	public static String TAGLINE = "tagline";
	public static String EMAILS = "emails";
	public static String OBJECT_TYPE = "objectType";
	public static String ETAG = "etag";
	
	@Override
	public Person load(String data) {
		Person person = new PersonImpl();
		
		try{
			JSONObject db = new JSONObject(data);
			
			if (db.has("error"))
				return person;
			
			person.setId(db.getString(ID));
			person.setDisplayName(db.getString(DISPLAY_NAME));			
			parseName(db, person);
			if(db.has(NICKNAME)) person.setNickname(NICKNAME);
//			parseBirthday(db, person);
			parseGender(db, person);
//			parseCurrentLocation(db, person);
			if(db.has(URL)) person.setProfileUrl(db.getString(URL));
			if(db.has(IMAGE)) person.setThumbnailUrl(db.getJSONObject(IMAGE).getString(URL));			 	
//			languagesSpoken[] 	list 	The languages spoken by this person. 	check if complies with ISO 639-1
			if(db.has(HAS_APP)) person.setHasApp(db.getBoolean(HAS_APP));
			if(db.has(ABOUT_ME)) person.setAboutMe(db.getString(ABOUT_ME));
//			parseRelationshipStatus(db, person);			
//			relationshipStatus 	string 	The person's relationship status. Possible values are:
//
//			    "single" - Person is single.
//			    "in_a_relationship" - Person is in a relationship.
//			    "engaged" - Person is engaged.
//			    "married" - Person is married.
//			    "its_complicated" - The relationship is complicated.
//			    "open_relationship" - Person is in an open relationship.
//			    "widowed" - Person is widowed.
//			    "in_domestic_partnership" - Person is in a domestic partnership.
//			    "in_civil_union" - Person is in a civil union.
		
//			urls[] 	list 	A list of URLs for this person. 	
//			urls[].value 	string 	The URL value. 	
//			urls[].type 	string 	The type of URL. Possible values are:
//
//			    "home" - URL for home.
//			    "work" - URL for work.
//			    "blog" - URL for blog.
//			    "profile" - URL for profile.
//			    "other" - Other.
//
//				
//			urls[].primary 	boolean 	If "true", this URL is the person's primary URL.
//			parseOrganizations(db, person);			
//			organizations[] 	list 	A list of current or past organizations with which this person is associated. 	
//			organizations[].name 	string 	The name of the organization. 	
//			organizations[].department 	string 	The department within the organization. 	
//			organizations[].title 	string 	The person's job title or role within the organization. 	
//			organizations[].type 	string 	The type of organization. Possible values are:
//
//			    "work" - Work.
//			    "school" - School.
//			organizations[].startDate 	string 	The date the person joined this organization. 	
//			organizations[].endDate 	string 	The date the person left this organization. 	
//			organizations[].location 	string 	The location of this organization. 	
//			organizations[].description 	string 	A short description of the person's role in this organization. 	
//			organizations[].primary 	boolean 	If "true", indicates this organization is the person's primary one (typically interpreted as current one).
			
//			placesLived[] 	list 	A list of places where this person has lived. 	
//			placesLived[].value 	string 	A place where this person has lived. For example: "Seattle, WA", "Near Toronto". 	
//			placesLived[].primary 	boolean 	If "true", this place of residence is this person's primary residence.
			
//			tagline 	string 	The brief description (tagline) of this person. 	
//			emails[] 	list 	A list of email addresses for this person. 	
//			emails[].value 	string 	The email address. 	
//			emails[].type 	string 	The type of address. Possible values are:
//
//			    "home" - Home email address.
//			    "work" - Work email address.
//			    "other" - Other.
//
//				
//			emails[].primary 	boolean 	If "true", indicates this email address is the person's primary one. 	
//			objectType 	string 	Type of person within Google+. Possible values are:
//
//			    "person" - represents an actual person.
//			    "page" - represents a page.
//
//				
//			etag 	etag 	ETag of this response for caching purposes.
			
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		return person;
	}
	
	private void parseName(JSONObject json, Person person) throws JSONException {
		if(json.has(NAME)) {
			JSONObject jsonName = json.getJSONObject(NAME);	
			Name name = new NameImpl();
			if(jsonName.has(FORMATTED))
				name.setFormatted(jsonName.getString(FORMATTED));
			if(jsonName.has(FAMILY_NAME))
				name.setFamilyName(jsonName.getString(FAMILY_NAME));
			if(jsonName.has(GIVEN_NAME))
				name.setGivenName(jsonName.getString(GIVEN_NAME));
																			// TODO middleName not supported by shindig 	
			if(jsonName.has(HONORIFIC_PREFIX))
				name.setHonorificPrefix(jsonName.getString(HONORIFIC_PREFIX));
			if(jsonName.has(HONORIFIC_SUFFIX))
				name.setHonorificSuffix(jsonName.getString(HONORIFIC_SUFFIX));
			person.setName(name);
		}
	}
	
	private void parseGender(JSONObject json, Person person) throws JSONException { // TODO gender 'other' not supported by shindig 
		if(json.has(GENDER)) {
			String gender = json.getString(GENDER);
			if(gender.equals("male"))
				person.setGender(Gender.male);
			else if(gender.equals("female"))
				person.setGender(Gender.male);
		}
	}

}
