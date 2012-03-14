package org.societies.platform.socialdata.model;

import java.util.ArrayList;
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

public class ActivityConverterFromFacebook implements ActivityConverter {

	
	/* COSTANTS */
	
	public static final String DATA					=	"data";
	public static final String ID					=	"id";
	
	
	private static final String AUTOR_NAME 			= "name";
	private static final String MESSAGE 			= "message";
	private static final String FROM 				= "from";
	private static final String ICON 				= "icon";
	private static final String TYPE 				= "type";
	private static final String UPDATED_TIME 		= "updated_time";
	private static final String CREATED_TIME 		= "created_time";
	private static final String LINK 				= "link";
	private static final String APPLICATION 		= "application";
	
	
	String		imageUrl   	 = "https://graph.facebook.com/FBID/picture?type=normal&access_token=";      
	String		previewUrl	 = "https://graph.facebook.com/OBJECTID/picture?type=normal&access_token=";
	
	@Override
	public List<ActivityEntry> load(JSONObject data) {
			
		ArrayList<ActivityEntry> activities = new ArrayList<ActivityEntry>();
		JSONArray elements;
		try {
			elements = new JSONArray(data.getString(DATA));
			
			for (int i=0; i<elements.length(); i++){
				JSONObject elm = elements.getJSONObject(i);
				ActivityEntry entry = new ActivityEntryImpl();
				entry.setId(elm.getString(ID));
				entry.setActor(setActor(elm.getString(FROM)));
				entry.setContent(elm.getString(MESSAGE));
				entry.setIcon(getIcon(elm));
				entry.setVerb(elm.getString(TYPE));
				entry.setUpdated(elm.getString(UPDATED_TIME));
				entry.setPublished(elm.getString(CREATED_TIME));
				
				
				
				if (elm.has(LINK)) 	entry.setUrl(elm.getString(LINK));
				if (elm.has(APPLICATION)) entry.setProvider(setProvider(elm.getString(APPLICATION)));
			}
			
			
		} catch (JSONException e) {
		
			e.printStackTrace();
		}
		
		return activities;
		
	}




	private ActivityObject setProvider(String string) {
		ActivityObject provider = new ActivityObjectImpl();
		JSONObject elm = null;
		
		try {
			elm = new JSONObject(string);
			provider.setId(elm.getString(ID));
			provider.setDisplayName(elm.getString(AUTOR_NAME));
		
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return provider;
	}




	private MediaLink getIcon(JSONObject elm) {
		MediaLink icon = new MediaLinkImpl();
		try {
			icon.setUrl(elm.getString(ICON));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return icon;
	}




	private ActivityObject setActor(String data) {
		ActivityObject actor = new ActivityObjectImpl();
		JSONObject elm = null;
		
		try {
			elm = new JSONObject(data);
			actor.setId(elm.getString(ID));
			actor.setDisplayName(elm.getString(AUTOR_NAME));
			actor.setUrl(imageUrl.replaceAll("FBID", actor.getId()));
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return actor;
	}

}
