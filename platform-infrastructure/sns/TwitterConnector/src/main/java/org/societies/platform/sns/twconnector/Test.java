package org.societies.platform.sns.twconnector;

import org.json.simple.*;
import org.json.simple.parser.ParseException;

class Test{

	
	public static void main(String[] args){
		TwitterConnector t = new TwitterConnector();
		
		testProfileExtraction(t);
		testFriendsExtraction(t);
		testFollowersExtraction(t);
	}
		
	public static void testProfileExtraction(TwitterConnector t){
		JSONObject r = null;
		try {
			r = t.twClient.getUserProfile("self");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(r==null)
			System.out.println("user profile = null");
		else
		System.out.println(r.toString());
	}
	
	public static void testFriendsExtraction(TwitterConnector t){
		JSONObject r = null;
		try {
			r = t.twClient.getUserFriends("self");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(r==null)
			System.out.println("user profile = null");
		else
		System.out.println(r.toString());
	}
	
	public static void testFollowersExtraction(TwitterConnector t){
		JSONObject r = null;
		try {
			r = t.twClient.getUserFollowers("self");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(r==null)
			System.out.println("user profile = null");
		else
		System.out.println(r.toString());
	}
	
	
}
