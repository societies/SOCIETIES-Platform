package org.societies.platform.socialdata.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.shindig.social.opensocial.model.Group;

public class GroupConverterFromTwitter implements GroupConverter {

	@Override
	public List<Group> load(String data) {
		
		
		// not implemente for twitter
		return new ArrayList<Group>();
	}

}
