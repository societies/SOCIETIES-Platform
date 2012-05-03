package org.societies.platform.socialdata.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.shindig.social.core.model.AccountImpl;
import org.apache.shindig.social.core.model.ActivityEntryImpl;
import org.apache.shindig.social.core.model.ActivityObjectImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.apache.shindig.social.opensocial.model.Person;
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
				entry.setId(elm.getString("id"));
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
				entry.setVerb("publish");
				entry.setProvider(providerObj);
				activities.add(entry);
			}
		} 
		catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		return activities;
	}

}
