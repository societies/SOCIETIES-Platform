package org.societies.platform.socialdata.converters;

import java.util.List;

import org.apache.shindig.social.opensocial.model.Group;

public interface GroupConverter {
	
	List<Group> load(String data);

}
