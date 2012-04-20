package org.societies.platform.socialdata.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.shindig.protocol.model.ExtendableBean;
import org.apache.shindig.protocol.model.ExtendableBeanImpl;
import org.apache.shindig.social.core.model.ActivityEntryImpl;
import org.apache.shindig.social.core.model.ActivityObjectImpl;
import org.apache.shindig.social.core.model.MediaLinkImpl;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.apache.shindig.social.opensocial.model.MediaLink;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.societies.platform.socialdata.model.ActionLink;

public class ActivityConverterFromFacebook implements ActivityConverter {

	
	/* COSTANTS */
	
	public static final String DATA					=	"data";
	public static final String ID					=	"id";
	public static final String POST					=	"post";
	private static final String STORY 				=   "story";
	private static final String CAPTION 			=   "caption";
	
	
	private static final String AUTHOR_NAME 		= "name";
	private static final String MESSAGE 			= "message";
	private static final String FROM 				= "from";
	private static final String ICON 				= "icon";
	private static final String TYPE 				= "type";
	private static final String UPDATED_TIME 		= "updated_time";
	private static final String CREATED_TIME 		= "created_time";
	private static final String LINK 				= "link";
	private static final String APPLICATION 		= "application";
	private static final String ACTIONLINKS 		= "actionLinks";
	
	
	
	String		imageUrl   	 = "https://graph.facebook.com/FBID/picture?type=normal&access_token=";      
	String		previewUrl	 = "https://graph.facebook.com/OBJECTID/picture?type=normal&access_token=";
	
	@Override
	public List<ActivityEntry> load(String data) {
			
		ArrayList<ActivityEntry> activities = new ArrayList<ActivityEntry>();
		JSONArray elements;
		
		ActivityObject providerObj = new ActivityObjectImpl();
		providerObj.setContent("facebook");
		providerObj.setUrl("www.facebook.com");
		providerObj.setId("facebook");
		providerObj.setDisplayName("Facebook");
		
		try {
			
			
			JSONObject jsonData = new JSONObject(data);
			elements = new JSONArray(jsonData.getString(DATA));
			
			for (int i=0; i<elements.length(); i++){
				JSONObject elm = elements.getJSONObject(i);
				
				//System.out.println("ANALIZZO:"+elm.toString());
				ActivityEntry entry = new ActivityEntryImpl();
				entry.setId("facebook:"+elm.getString(ID));
				entry.setActor(setActor(elm.getString(FROM)));
				
				if (elm.has(ICON)) 	  entry.setIcon(getIcon(elm));
				
				if (elm.has(MESSAGE)) 
					entry.setContent(elm.getString(MESSAGE));
				else if (elm.has(STORY))
					entry.setContent(elm.getString(STORY));
				else if (elm.has(CAPTION)){
					entry.setContent(elm.getString(CAPTION));
				}
				//else entry.setContent("???");
				
				
				entry.setVerb(POST);
				entry.setUpdated(elm.getString(UPDATED_TIME));
				entry.setPublished(elm.getString(CREATED_TIME));
			
				ActivityObject aObj = new ActivityObjectImpl();
				aObj.setObjectType(genType(elm.getString(TYPE)));
				if (elm.has(LINK)) aObj.setUrl(elm.getString(LINK));
				entry.setObject(aObj);
			
				ArrayList<ActionLink> actionLinks = new ArrayList<ActionLink>();
				ExtendableBean actionlinksEB= new ExtendableBeanImpl();
				actionlinksEB.put(ACTIONLINKS, actionLinks);
				entry.setOpenSocial(actionlinksEB);
				
				if (elm.has(APPLICATION)) entry.setProvider(setProvider(elm.getString(APPLICATION)));
				
				
				
				entry.setProvider(providerObj);
				
				//System.out.println("ADD id:"+entry.getId() + " from:"+entry.getActor().getDisplayName() + " verb:"+entry.getVerb() + " content:"+entry.getContent());
				activities.add(entry);
				
				
			}
			
			
		} catch (JSONException e) {
		
			e.printStackTrace();
		}
		
		return activities;
		
	}




	private String genType(String string) {
		if ("photo".equals(string)) return "image";
		else if ("link".equals(string)) return "bookmark";
		else if ("status".equals(string)) return "note";
		else return string.toLowerCase();
 		
	}




	private ActivityObject setProvider(String string) {
		ActivityObject provider = new ActivityObjectImpl();
		JSONObject elm = null;
		
		try {
			elm = new JSONObject(string);
			provider.setId(elm.getString(ID));
			provider.setDisplayName(elm.getString(AUTHOR_NAME));
		
		
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
			actor.setDisplayName(elm.getString(AUTHOR_NAME));
			actor.setUrl(imageUrl.replaceAll("FBID", actor.getId()));
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return actor;
	}

}
