package org.societies.platform.socialdata.converters;

import java.util.List;

import org.apache.shindig.social.opensocial.model.ActivityEntry;

public interface ActivityConverter {

	/**
	 * Transform data from a JSON raw data to a ACTIVITY model in social-data
	 * @param connector
	 * @return
	 */
	List<ActivityEntry> load(String data);

}
