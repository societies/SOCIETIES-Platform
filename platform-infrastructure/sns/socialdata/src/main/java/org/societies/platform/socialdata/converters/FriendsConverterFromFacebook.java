package org.societies.platform.socialdata.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.shindig.social.opensocial.model.Person;


public class FriendsConverterFromFacebook implements FriendsConverter{
	
	
	
	public List<Person> load(String  data){
		List <Person> friends = new ArrayList<Person>();
		System.out.println(data);
		
		return friends;
	}
	
	

	
	
}
