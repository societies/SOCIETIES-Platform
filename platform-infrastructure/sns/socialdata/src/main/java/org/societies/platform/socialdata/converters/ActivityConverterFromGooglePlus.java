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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.shindig.social.core.model.ActivityEntryImpl;
import org.apache.shindig.social.core.model.ActivityObjectImpl;
import org.apache.shindig.social.core.model.MediaLinkImpl;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.apache.shindig.social.opensocial.model.MediaLink;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Converter from Google+ in JSON to Activity List.
 *
 * @author Edgar Domingues (PTIN)
 *
 */
public class ActivityConverterFromGooglePlus implements ActivityConverter {

	public static final String ITEMS = "items";
	public static final String TITLE = "title";
	public static final String PUBLISHED = "published";
	public static final String UPDATED = "updated";
	public static final String ID = "id";
	public static final String URL = "url";
	public static final String ACTOR = "actor";
	public static final String DISPLAY_NAME = "displayName";
	public static final String IMAGE = "image";
	public static final String VERB = "verb";
	public static final String OBJECT = "object";
	public static final String OBJECT_TYPE = "objectType";
	public static final String CONTENT = "content";
	public static final String ATTACHMENTS = "attachments";
	public static final String HEIGHT = "height";
	public static final String WIDTH = "width";
	public static final String PROVIDER = "provider";
	
	@Override
	public List<ActivityEntry> load(String data) {
		List<ActivityEntry> activities = Collections.emptyList(); 
		
		
		ActivityObject providerObj = new ActivityObjectImpl();
		providerObj.setUrl("plus.google.com");
		providerObj.setId("plus.google.com");
		providerObj.setDisplayName("Google+");
		
		try{
			JSONObject db = new JSONObject(data);
			
			if (db.has("error"))
				return activities;
			
			JSONArray items = db.getJSONArray(ITEMS);
			activities = new ArrayList<ActivityEntry>(items.length());
			for(int i=0; i<items.length(); i++) {
				JSONObject item = items.getJSONObject(i);
				ActivityEntry activity = new ActivityEntryImpl();		
				activity.setProvider(providerObj);
				parseActivity(item, activity);
				activities.add(activity);
				
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		return activities;
	}

	private void parseActivity(JSONObject json, ActivityEntry activity) {
		try {					

			if(json.has(TITLE)) activity.setTitle(json.getString(TITLE));
			activity.setPublished(json.getString(PUBLISHED));
			if(json.has(UPDATED)) activity.setUpdated(json.getString(UPDATED));
			activity.setId("googleplus:"+json.getString(ID));
			if(json.has(URL)) activity.setUrl(json.getString(URL));
			parseActor(json.getJSONObject(ACTOR), activity);
			if(json.has(VERB)) activity.setVerb(json.getString(VERB));
			parseObject(json, activity);			
//			annotation 	string 	Additional content added by the person who shared this activity, applicable only when resharing an activity.			
//			crosspostSource 	string 	If this activity is a crosspost from another system, this property specifies the ID of the original activity.
			//parseProvider(json, activity);

//			access 	nested object 	Identifies who has access to see this activity. 	
//			access.kind 	string 	Identifies this resource as a collection of access controls. Value: "plus#acl". 	
//			access.description 	string 	Description of the access granted, suitable for display. 	
//			access.items[] 	list 	The list of access entries. 	
//			access.items[].type 	string 	The type of entry describing to whom access is granted. Possible values are:
//
//			    "person" - Access to an individual.
//			    "circle" - Access to members of a circle.
//			    "myCircles" - Access to members of all the person's circles.
//			    "extendedCircles" - Access to members of everyone in a person's circles, plus all of the people in their circles.
//			    "public" - Access to anyone on the web.
//
//				
//			access.items[].id 	string 	The ID of the entry. For entries of type "person" or "circle", this is the ID of the resource. For other types, this property is not set.			
//			geocode 	string 	Latitude and longitude where this activity occurred. Format is latitude followed by longitude, space separated. 	
//			address 	string 	Street address where this activity occurred. 	
//			radius 	string 	Radius, in meters, of the region where this activity occurred, centered at the latitude and longitude identified in geocode. 	
//			placeId 	string 	ID of the place where this activity occurred. 	
//			placeName 	string 	Name of the place where this activity occurred. 	
//			object.replies.selfLink 	string 	The URL for the collection of comments in reply to this activity. 	
//			object.plusoners.selfLink 	string 	The URL for the collection of people who +1'd this activity. 	
//			object.resharers.selfLink 	string 	The URL for the collection of resharers. 	
//			etag 	etag 	ETag of this response for caching purposes.
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseActor(JSONObject json, ActivityEntry activity) throws JSONException {	// name.familyName name.givenName not supported
		ActivityObject actor = new ActivityObjectImpl();
		activity.setActor(actor);
		actor.setId(json.getString(ID));
		if(json.has(DISPLAY_NAME)) actor.setDisplayName(json.getString(DISPLAY_NAME));
		if(json.has(URL)) actor.setUrl(json.getString(URL));
		if(json.has(IMAGE)) {
			MediaLink image = new MediaLinkImpl();
			actor.setImage(image);
			image.setUrl(json.getJSONObject(IMAGE).getString(URL));
		}
	}

	private void parseObject(JSONObject json, ActivityEntry activity) throws JSONException { // originalContent, replies, plusoner, reshares not supported
		if(json.has(OBJECT)) {
			ActivityObject object = new ActivityObjectImpl();
			activity.setObject(object);
			if(json.has(OBJECT_TYPE)) object.setObjectType(json.getString(OBJECT_TYPE));
			if(json.has(ID)) object.setId(json.getString(ID));
			if(json.has(ACTOR)) {
				ActivityObject author = new ActivityObjectImpl();
				object.setAuthor(author);
				JSONObject actor = json.getJSONObject(ACTOR);
				if(actor.has(ID)) author.setId(actor.getString(ID));
				if(actor.has(DISPLAY_NAME)) author.setDisplayName(actor.getString(DISPLAY_NAME));
				if(actor.has(URL)) author.setUrl(actor.getString(URL));
				if(actor.has(IMAGE)) {
					MediaLink image = new MediaLinkImpl();
					author.setImage(image);
					image.setUrl(actor.getJSONObject(IMAGE).getString(URL));
				}				
			}
			if(json.has(CONTENT)) object.setContent(json.getString(CONTENT));
			if(json.has(URL)) object.setUrl(json.getString(URL));
			parseAttachments(json, object);
		}		
	}
	
	private void parseAttachments(JSONObject json, ActivityObject object) throws JSONException {
		if(json.has(ATTACHMENTS)) {
			JSONArray jsonAttachments = json.getJSONArray(ATTACHMENTS);
			List<ActivityObject> attachments = new ArrayList<ActivityObject>(jsonAttachments.length());
			object.setAttachments(attachments);
			for(int i=0; i<jsonAttachments.length(); i++) {
				JSONObject jsonAttachment = jsonAttachments.getJSONObject(i);
				ActivityObject attachment = new ActivityObjectImpl();
				attachments.add(attachment);
				parseAttachment(jsonAttachment, attachment);
			}
		}
	}
	
	private void parseAttachment(JSONObject json, ActivityObject attachment) throws JSONException { // image.type, fullImage, embed not supported
		if(json.has(OBJECT_TYPE)) attachment.setObjectType(json.getString(OBJECT_TYPE));
		if(json.has(DISPLAY_NAME)) attachment.setDisplayName(json.getString(DISPLAY_NAME));
		if(json.has(ID)) attachment.setId(json.getString(ID));
		if(json.has(CONTENT)) attachment.setContent(json.getString(CONTENT));
		if(json.has(URL)) attachment.setUrl(json.getString(URL));
		if(json.has(IMAGE)) {
			MediaLink image = new MediaLinkImpl();
			attachment.setImage(image);
			JSONObject jsonImage = json.getJSONObject(IMAGE);
			parseImage(jsonImage, image);
		}
		
	}

	private void parseImage(JSONObject json, MediaLink image) throws JSONException {
		if(json.has(URL)) image.setUrl(json.getString(URL));
		if(json.has(HEIGHT)) image.setHeight(json.getInt(HEIGHT));
		if(json.has(WIDTH)) image.setWidth(json.getInt(WIDTH));
	}
	
	private void parseProvider(JSONObject json, ActivityEntry activity) throws JSONException {
		if(json.has(PROVIDER)) {
			ActivityObject provider = new ActivityObjectImpl();
			activity.setProvider(provider);
			if(json.getJSONObject(PROVIDER).has(TITLE)) provider.setDisplayName(json.getJSONObject(PROVIDER).getString(TITLE));
		}
	}
	
}
