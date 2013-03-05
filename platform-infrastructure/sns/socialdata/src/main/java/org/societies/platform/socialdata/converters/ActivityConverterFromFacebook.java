package org.societies.platform.socialdata.converters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.sns.ISocialData;
import org.societies.platform.socialdata.model.ActionLink;

public class ActivityConverterFromFacebook implements ActivityConverter {

	private Logger logger = LoggerFactory.getLogger(ActivityConverterFromFacebook.class);
	 
	 
	class fb_user{
		public String name;
		public String type; 
		
		public fb_user(String name, String type){
			this.name= name;
			this.type= type;
		}
	}
	
	/* COSTANTS */
	
	public static final String DATA					=	"data";
	public static final String ID					=	"id";
	
//	// verb
//	public static final String POST					=	"post";  
//	public  static final String TAG 				=   "tag";   
//	public  static final String UPDATE 				=   "update";  
//	public  static final String LIKE 				=   "like";
//	public  static final String SHARE 				=   "share";  
//	public  static final String MAKE_FRIEND 		=   "make-friend";
//	public  static final String ATTEND 				=   "attend";
//	
//	//Object type???
//	
//	public  static final String NOTE 				=   "note";
//	public  static final String IMAGE 				=   "image";
//	public  static final String PERSON 				=   "person";
//	public  static final String BOOKMARK 			=   "bookmark";
//	public  static final String COMMENT 			=   "comment";
//	public  static final String EVENT 				=   "event";
//	public  static final String QUESTION 			=   "question";
//	public  static final String COLLECTION 			=   "collection";
//	public  static final String PLACE 				=   "places";
	
	
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
	ArrayList<ActivityEntry> activities = new ArrayList<ActivityEntry>();
	
	@Override
	public List<ActivityEntry> load(String data) {
		
		
		
		activities = new ArrayList<ActivityEntry>();
		JSONArray elements;
		
		ActivityObject providerObj = new ActivityObjectImpl();
		providerObj.setUrl("www.facebook.com");
		providerObj.setId("facebook.com");
		providerObj.setDisplayName("Facebook");
		
		try {
			
			
			JSONObject jsonData = new JSONObject(data);
			elements = new JSONArray(jsonData.getString(DATA));
			//System.out.println("element to be analyzed:"+elements.length());
			for (int i=0; i<elements.length(); i++){
				
				
				JSONObject elm = elements.getJSONObject(i);
			    
				ActivityEntry entry = new ActivityEntryImpl();
				entry.setId("facebook:"+elm.getString(ID));
				entry.setActor(setActor(elm.getString(FROM)));
				
				entry.setProvider(providerObj);  // FIX with facebook
				entry.setVerb(ISocialData.POST);
				entry.setUpdated(elm.getString(UPDATED_TIME));
				entry.setPublished(elm.getString(CREATED_TIME));
			
				ArrayList<ActionLink> actionLinks = new ArrayList<ActionLink>();
				ExtendableBean actionlinksEB= new ExtendableBeanImpl();
				actionlinksEB.put(ACTIONLINKS, actionLinks);
				entry.setOpenSocial(actionlinksEB);
				
				if (elm.has(APPLICATION)) entry.setProvider(setProvider(elm.getString(APPLICATION)));

				
				
				
				
				if (elm.has(ICON)) 	  entry.setIcon(getIcon(elm));
				
				if (elm.has(MESSAGE)){
					entry.setContent(elm.getString(MESSAGE));
					ActivityObject aObj = new ActivityObjectImpl();
					aObj.setObjectType(genType(elm.getString(TYPE)));
					if (elm.has(LINK)) aObj.setUrl(elm.getString(LINK));
					entry.setObject(aObj);
				}
				else if (elm.has(STORY)){
					entry.setContent(elm.getString(STORY));
					fixEntry(entry, elm);
					
					//System.out.println("---- Actor:"+entry.getActor().getDisplayName()+ " - Type:"+elm.getString(TYPE)+ " Story:  " + entry.getContent());
				}
				else if (elm.has(CAPTION)){
					entry.setContent(elm.getString(CAPTION));
					ActivityObject aObj = new ActivityObjectImpl();
					aObj.setObjectType(genType(elm.getString(TYPE)));
					if (elm.has(LINK)) aObj.setUrl(elm.getString(LINK));
					entry.setObject(aObj);
				}
				//else entry.setContent("???");
				
				
								
				
				
				
				//System.out.println("ADD id:"+entry.getId() + " from:"+entry.getActor().getDisplayName() + " verb:"+entry.getVerb() + " content:"+entry.getContent());
				activities.add(entry);
				
				
			}
			
			
		} catch (JSONException e) {
		
			e.printStackTrace();
		}
		
		return activities;
		
	}




	private void fixEntry(ActivityEntry entry, JSONObject elm) throws JSONException{
		
		 String story = entry.getContent();
		
		 
		 // VERB Tagged - Object - Actor - Author Myself
		 Pattern p1 = Pattern.compile(".* was tagged in .* (photo|album)(?:\\s(.*))?.");
	     Matcher m1 = p1.matcher(story);
	     if (m1.find()){
	    	 
	    	 entry.setVerb(ISocialData.TAG);
	    	 entry.setObject(entry.getActor());
	    	 
	    	 ActivityObject aObj = new ActivityObjectImpl();
	    	 entry.setTarget(aObj);	
	    	 if (elm.has(LINK)) aObj.setUrl(elm.getString(LINK));
				
	    	 
	    	 if (m1.group(2)!=null){
	    		aObj.setObjectType(ISocialData.COLLECTION);
				aObj.setDisplayName(m1.group(2));
	    	 }
	    	 else{
	    			aObj.setObjectType(genType(elm.getString(TYPE)));
	    	 }
	    	 return;
	     }
	     
	     
	     
	     // VERB 
	     Pattern p2 = Pattern.compile("\"(.*)\" on .* (\\S+).");
	     Matcher m2 = p2.matcher(story);
	     if (m2.find()){
	    	 
	    	 entry.setVerb(ISocialData.POST);
	    	 entry.setContent(m2.group(1));
	    	
	    	 ActivityObject object = new ActivityObjectImpl();
	    	 object.setObjectType(ISocialData.COMMENT);
	    	 entry.setObject(object);
	    	 
	    	 ActivityObject target = new ActivityObjectImpl();
	    	 target.setObjectType(genType(m2.group(2)));
	    	 entry.setTarget(target);
	    	 
	    	 return;
	     }
	     
	     
	    
	     
	    // VERB FRIENDS
	    Pattern p3 = Pattern.compile(".* (is|are) now friends.*.");
	    Matcher m3 = p3.matcher(story);
		if (m3.find()){
			String story_tags = elm.getString("story_tags");
			JSONObject jstags = new JSONObject(story_tags);
			List<fb_user> users = new ArrayList<fb_user>();
			Iterator<String> keys = jstags.keys();
			while(keys.hasNext()){
				JSONArray jUserArray = jstags.getJSONArray(keys.next());
				for (int i=0; i < jUserArray.length();i++){
					JSONObject jUser = jUserArray.getJSONObject(i);
					if (!entry.getActor().getDisplayName().equalsIgnoreCase(jUser.getString("name")))
						users.add(new fb_user(jUser.getString("name"), jUser.getString("type")));
				}			
			}
			
			
			entry.setVerb(ISocialData.MAKE_FRIEND);
			fb_user user = users.get(0);
			ActivityObject object = new ActivityObjectImpl();
			object.setDisplayName(user.name);
			object.setObjectType(genType(user.type));
			entry.setObject(object);
			
			if (users.size()>1){
				for(int i=1; i<users.size(); i++){
					fb_user user1 = users.get(i);
					
					ActivityEntry activity = new ActivityEntryImpl();
					activity.setActor(entry.getActor());
					activity.setVerb(ISocialData.MAKE_FRIEND);
					activity.setContent(entry.getContent());
					
					ActivityObject object1 = new ActivityObjectImpl();
					object.setDisplayName(user1.name);
					object.setObjectType(genType(user1.type));
					activity.setObject(object1);
					activity.setPublished(entry.getPublished());
					activities.add(activity);					
				}
			}
			
	    	return;
		}
	
		
		Pattern p4 = Pattern.compile(".* likes .*.");
		Matcher m4 = p4.matcher(story);
		if (m4.find()){
			

			List<fb_user> users = new ArrayList<fb_user>();
			String story_tags = elm.getString("story_tags");
			logger.debug("Add story tag:"+story_tags);

			JSONObject jstags 	= new JSONObject(story_tags);

			Iterator<String> keys = jstags.keys();
			while(keys.hasNext()){

				//this is the new line you have to find
				

				JSONArray jUserArray = jstags.getJSONArray(keys.next());
				for (int i=0; i < jUserArray.length();i++){
					JSONObject jUser = jUserArray.getJSONObject(i);
					if (!entry.getActor().getDisplayName().equalsIgnoreCase(jUser.getString("name"))){
						String type = "";
						if (jUser.has("type")) type= jUser.getString("type");
						users.add(new fb_user(jUser.getString("name"), type));
					}
				}			
			}


			
			
			entry.setVerb(ISocialData.LIKE);
			
			if (users.size()>0){
				fb_user user = users.get(0);
				
				ActivityObject object = new ActivityObjectImpl();
				object.setDisplayName(user.name);
				object.setObjectType(genType(user.type));
				entry.setObject(object);
				logger.info("Add User:"+ user.name + " type:"+user.type);
				if (users.size()>1){
					
					for(int i=1; i<users.size(); i++){
						fb_user user1 = users.get(i);

						ActivityEntry activity = new ActivityEntryImpl();
						activity.setActor(entry.getActor());
						activity.setVerb(ISocialData.LIKE);
						activity.setContent(entry.getContent());

						ActivityObject object1 = new ActivityObjectImpl();
						object1.setDisplayName(user1.name);
						object1.setObjectType(genType(user1.type));
						activity.setObject(object1);
						activity.setPublished(entry.getPublished());
						activities.add(activity);					
					}
				}
			}else logger.info("NO  User:");
	    	return;
		}
		
		
		 Pattern p5 = Pattern.compile(".* (?:changed|updated) .* (?:picture|photo).");
		 Matcher m5 = p5.matcher(story);
		 if (m5.find()){
			 entry.setVerb(ISocialData.UPDATE);
			 ActivityObject obj = new ActivityObjectImpl();
			 obj.setObjectType(ISocialData.PERSON);
			 obj.setDisplayName(entry.getActor().getDisplayName());
			 entry.setObject(obj);
			 return;
			 
		     
		 }
		
		
			 Pattern p6 = Pattern.compile(".* added (\\d+) new photo.* album (.*).");
		     Matcher m6 = p6.matcher(story);
		     if (m6.find()){
		    	 
		    	 	entry.setVerb(ISocialData.POST);
		    	 	ActivityObject obj = new ActivityObjectImpl();
		    	 	obj.setObjectType(genType(elm.getString(TYPE)));
		    	 	entry.setObject(obj);
		    	 	ActivityObject target = new ActivityObjectImpl();
		    	 	target.setObjectType(ISocialData.COLLECTION);
		    	 	target.setDisplayName(m6.group(2));
		    	 	entry.setTarget(target);
		    	 	
		    	 	
		    	 	for (int i=1; i< Integer.parseInt(m6.group(1)); i++){
		    	 		ActivityEntry activity = new ActivityEntryImpl();
						activity.setActor(entry.getActor());
						activity.setVerb(entry.getVerb());
						activity.setTarget(entry.getTarget());
						activity.setObject(entry.getObject());
						activity.setPublished(entry.getPublished());
						activity.setContent(entry.getContent());
						
						activities.add(activity);		
		    	 		
		    	 	}
		    	 
		    	    
		    		return;
		 		    					
				}
		     
		
		
		
			 Pattern p7 = Pattern.compile(".* went to (.*) at (.*).");
		     Matcher m7 = p7.matcher(story);
		     if (m7.find()){		    	 
		    	 entry.setVerb(ISocialData.ATTEND);
		    	 ActivityObject obj = new ActivityObjectImpl();
		    	 obj.setObjectType(ISocialData.EVENT);
		    	 obj.setDisplayName(m7.group(1));
		    	 entry.setObject(obj);
		    	 
		    	 ActivityObject target = new ActivityObjectImpl();
		    	 target.setObjectType(ISocialData.PLACE);
		    	 target.setDisplayName(m7.group(2));
		    	 entry.setTarget(target);
		    	 
		    	 return;
		     }
		
		     Pattern p8 = Pattern.compile(".* shared a link.");
		     Matcher m8 = p8.matcher(story);
		     if (m8.find()){
		    	 entry.setVerb(ISocialData.SHARE);
		    	 ActivityObject obj = new ActivityObjectImpl();
		    	 obj.setObjectType(ISocialData.BOOKMARK);
		    	 obj.setDisplayName(elm.getString("name"));
		    	 
		    	 if (elm.has(LINK)) obj.setUrl(elm.getString(LINK));
		    	 entry.setObject(obj);
		    	 
		    	 return; 
		     }
		
		
			 Pattern p10 = Pattern.compile(".* asked: (.*).");
		     Matcher m10 = p10.matcher(story);
		     if (m10.find()){
		    	 entry.setVerb(ISocialData.POST);
		    	 entry.setContent(m10.group(1));
		    	 
		    	 ActivityObject obj = new ActivityObjectImpl();
		    	 obj.setObjectType(ISocialData.QUESTION);
		    	 entry.setObject(obj);
		    	 return;
		     }
		
		
			 Pattern p11 = Pattern.compile(".* answered (.*) with (.*).");
		     Matcher m11 = p11.matcher(story);
		     if (m11.find()){
		    	 entry.setVerb(ISocialData.POST);
		    	 entry.setContent(m11.group(2));
		    	 
		    	 ActivityObject obj = new ActivityObjectImpl();
		    	 obj.setObjectType(ISocialData.NOTE);
		    	 entry.setObject(obj);
		    	 
		    	 ActivityObject target = new ActivityObjectImpl();
		    	 target.setObjectType(ISocialData.QUESTION);
		    	 target.setDisplayName(m11.group(1));
		    	 entry.setTarget(target);
		    	 
		    	 return;
		    	 
		     }
		
		
		
	}




	private String genType(String string) {
		if ("photo".equalsIgnoreCase(string)) 			return ISocialData.IMAGE;
		else if ("link".equalsIgnoreCase(string)) 		return ISocialData.BOOKMARK;
		else if ("status".equalsIgnoreCase(string)) 	return ISocialData.NOTE;
		else if ("wall".equalsIgnoreCase(string)) 		return ISocialData.COLLECTION;
		else if ("timeline".equalsIgnoreCase(string)) 	return ISocialData.COLLECTION;
		else if ("user".equalsIgnoreCase(string)) 		return ISocialData.PERSON;
		else if ("page".equalsIgnoreCase(string)) 		return ISocialData.BOOKMARK;
		
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
			actor.setObjectType(ISocialData.PERSON);
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return actor;
	}

}
