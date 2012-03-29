package org.societies.platform.socialdata.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.shindig.social.opensocial.model.Group;

public class GroupConverterFromFacebook implements GroupConverter {

	@Override
	public List<Group> load(String data) {
		
		System.out.println(data);
		
		return new ArrayList<Group>();
	}

}
