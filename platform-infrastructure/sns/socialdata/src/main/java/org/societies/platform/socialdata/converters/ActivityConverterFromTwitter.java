package org.societies.platform.socialdata.converters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.shindig.social.core.model.ActivityEntryImpl;
import org.apache.shindig.social.core.model.ActivityObjectImpl;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityConverterFromTwitter implements ActivityConverter {

	@Override
	public List<ActivityEntry> load(String data) {
		ArrayList<ActivityEntry> activities = new ArrayList<ActivityEntry>();
		JSONArray elements;

		ActivityObject providerObj = new ActivityObjectImpl();
		providerObj.setContent("twitter");
		providerObj.setUrl("www.twitter.com");
		providerObj.setId("twitter");
		providerObj.setDisplayName("Twitter");

		System.out.println();
		try {
			elements  = new JSONArray(data);
		} catch (JSONException e1) {
			return activities;
		}
		try{
			

			for (int i=0; i<elements.length(); i++){
				JSONObject elm = elements.getJSONObject(i);
				ActivityEntry entry = new ActivityEntryImpl();
				entry.setId("twitter:"+elm.getString("id"));
				if (elm.has("text")) 
					entry.setContent(elm.getString("text"));
				if (elm.has("user")) {
					JSONObject user = new JSONObject(elm.getString("user"));
//					System.out.println(user.toString());
					if (user.has("name")) {
						ActivityObject aobj = new ActivityObjectImpl();
						aobj.setDisplayName(user.getString("name"));
						entry.setActor(aobj);
					}
				}
				if (elm.has("created_at")) {
//					System.out.println(elm.getString("created_at"));
					SimpleDateFormat date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy",Locale.ENGLISH);
					SimpleDateFormat publishedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
					Date datetemp = null;
					try {
						datetemp = date.parse(elm.getString("created_at"));
						entry.setPublished(publishedDate.format(datetemp));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				entry.setVerb("post");
				entry.setProvider(providerObj);
				ActivityObject type = new ActivityObjectImpl();
				type.setObjectType("note");
				entry.setObject(type);
				
				activities.add(entry);
				
			}
		} 
		catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		return activities;
	}

}
