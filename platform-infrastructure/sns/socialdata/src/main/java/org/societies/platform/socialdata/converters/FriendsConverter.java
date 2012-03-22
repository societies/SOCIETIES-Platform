package org.societies.platform.socialdata.converters;

import java.util.List;

import org.apache.shindig.social.opensocial.model.Person;

public interface FriendsConverter {

	/**
	 * Transform data from a JSON raw data to a PERSON model in social-data
	 * @param connector
	 * @return
	 */
	List<Person> load(String data);

}
