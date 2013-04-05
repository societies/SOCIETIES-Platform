package org.societies.platform.socialdata.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.shindig.social.core.model.GroupImpl;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.GroupId.Type;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GroupConverterFromFoursquare implements GroupConverter {

	
    
	@Override
	public List<Group> load(String data) {
		
		List<Group> groups = new ArrayList<Group>();
		
		try{
		    JSONObject response = new JSONObject(data);
		    if (response.has("following")){
			JSONObject following = response.getJSONObject("following");
			if (following.has("groups")){
			    JSONObject fgroups    = following.getJSONObject("groups");
			    JSONArray items = fgroups.getJSONArray("items");
			    for(int i=0; i<items.length();i++){
				groups.add(parseGroup(items.getJSONObject(i)));
			    }
			}
		    }
		    if (response.has("lists")){
			JSONObject lists = response.getJSONObject("following");
			if (lists.has("groups")){
			    JSONObject fgroups    = lists.getJSONObject("groups");
			    JSONArray items = fgroups.getJSONArray("items");
			    for(int i=0; i<items.length();i++){
				groups.add(parseGroup(items.getJSONObject(i)));
			    }
			}
			
		    }
		    
		    
		}catch(JSONException ex){
		    
		}
		catch(Exception e){}
		
		return groups;
	}
	
	
	
          public Group parseGroup(JSONObject group){
              
              
              Group g = new GroupImpl();
              try{
        	  g.setId(new GroupId(Type.groupId, group.getString("id")));
        	  g.setTitle(group.getString("firstName"));
        	  g.setDescription(group.getString("photo"));
        	  
              
              }catch(JSONException ex){}
              return g;
          }
	
	

}
