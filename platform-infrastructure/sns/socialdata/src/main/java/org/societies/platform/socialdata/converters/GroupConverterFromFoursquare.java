package org.societies.platform.socialdata.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.shindig.social.core.model.GroupImpl;
import org.apache.shindig.social.opensocial.spi.GroupId.Type;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GroupConverterFromFoursquare implements GroupConverter {

	
	@Override
	public List<Group> load(String data) {
		
		List<Group> groups = new ArrayList<Group>();
		
		return groups;
	}

}
