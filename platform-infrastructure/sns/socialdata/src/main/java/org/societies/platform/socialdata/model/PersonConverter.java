package org.societies.platform.socialdata.model;

import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONObject;

public interface PersonConverter {

	/**
	 * Transform data from a JSON raw data to a PERSON model in social-data
	 * @param connector
	 * @return
	 */
	Person load(JSONObject data);

}
