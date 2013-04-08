package org.societies.platform.socialdata.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.shindig.social.core.model.ActivityEntryImpl;
import org.apache.shindig.social.core.model.ActivityObjectImpl;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityConverterFromLinkedin implements ActivityConverter {

	
	
	@Override
	public List<ActivityEntry> load(String data) {
		
	    	System.out.println("activity\n:"+data);
	    	ArrayList<ActivityEntry> activities = new ArrayList<ActivityEntry>();
		try{
		    JSONObject response = new JSONObject(data);
		    if(response.has("values")){
			JSONArray values = response.getJSONArray("values");
			for(int i=0; i< values.length();i++){
			    JSONObject item = values.getJSONObject(i);
			    activities.add(genActivity(item));
			}
		    }
		    
		    
		}
		catch(JSONException ex){}

		return activities;
	}
	
	
	
	public ActivityEntry genActivity(JSONObject item){
	    ActivityEntry activity = new ActivityEntryImpl();
	    activity.setContent(item.toString());
	    return activity;
	}
	
	
}