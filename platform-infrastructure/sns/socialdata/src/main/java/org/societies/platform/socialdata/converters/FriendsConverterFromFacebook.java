package org.societies.platform.socialdata.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendsConverterFromFacebook implements FriendsConverter {

  

    
    public List<Person> load(String data) {

	List<Person> friends = new ArrayList<Person>();
	PersonConverterFromFacebook converter = new PersonConverterFromFacebook();
	try {
	    JSONObject jdata = new JSONObject(data);
	    if (jdata.has("error")) {
		return new ArrayList<Person>();
	    }

	    JSONArray jfriends = null;
	    if (jdata.has("body")) {
		JSONObject body = new JSONObject("body");
		jfriends = body.getJSONArray("data");
	    } else if (jdata.has("data"))
		jfriends = jdata.getJSONArray("data");
	    else
		jfriends = new JSONArray(data);

	    for (int i = 0; i < jfriends.length(); i++) {

		JSONObject jfriend = jfriends.getJSONObject(i);

		// Person p = new PersonImpl();
		// p.setId("facebook:"+jfriend.getString("id"));
		// p.setRelationshipStatus("friend");
		// p.setName(new NameImpl(jfriend.getString("name")));
		// p.setAccounts(accounts);
		// System.out.println(">>> new Friends"+p.getName().getFormatted());
		
		System.out.println("JSON:"+jfriend.toString(1));
		friends.add(converter.load(jfriend.toString()));
	    }
	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return friends;
    }

}
