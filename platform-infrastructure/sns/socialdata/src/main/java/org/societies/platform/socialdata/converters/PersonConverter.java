package org.societies.platform.socialdata.converters;

import org.apache.shindig.social.opensocial.model.Person;

public interface PersonConverter {

	/**
	 * Transform data from a JSON raw data to a PERSON model in social-data
	 * @param connector
	 * @return
	 */
	Person load(String data);

}
