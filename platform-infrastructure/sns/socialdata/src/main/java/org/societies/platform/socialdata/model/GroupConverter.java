package org.societies.platform.socialdata.model;

import java.util.List;

import org.apache.shindig.social.opensocial.model.Group;
import org.json.JSONObject;

public interface GroupConverter {
	
	List<Group> load(JSONObject data);

}
