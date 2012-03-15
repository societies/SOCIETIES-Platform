package org.societies.platform.socialdata.model;

import java.util.List;

import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.json.JSONObject;

public interface ActivityConverter {

	/**
	 * Transform data from a JSON raw data to a ACTIVITY model in social-data
	 * @param connector
	 * @return
	 */
	List<ActivityEntry> load(JSONObject data);

}
