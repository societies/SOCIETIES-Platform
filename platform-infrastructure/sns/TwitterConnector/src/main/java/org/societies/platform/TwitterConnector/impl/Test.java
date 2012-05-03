package org.societies.platform.TwitterConnector.impl;

import org.json.simple.*;
import org.json.simple.parser.ParseException;
import org.societies.platform.TwitterConnector.TwitterConnector;

class Test {

	public static void main(String[] args) {
		String Token = "468234144-7jBtrulMAriO1yjg2J9POY6aeW2TwnwrEXWeDWYn,1lY5pClLbeJ2MGC8A9995Dlx7gxNqdnLPQarsplwLpU";

		TwitterConnectorImpl t = new TwitterConnectorImpl(Token,"dingqi");

		testProfileExtraction(t);
		testFriendsExtraction(t);
		testFollowersExtraction(t);
		testTweetsExtraction(t);
	}

	public static void testProfileExtraction(TwitterConnector t) {
		String r = null;
		r = t.getUserProfile();
		if (r == null)
			System.out.println("user profile = null");
		else
			System.out.println(r);
	}

	public static void testFriendsExtraction(TwitterConnector t) {
		String r = null;
		r = t.getUserFriends();
		if (r == null)
			System.out.println("connection error");
		else
			System.out.println(r);
	}

	public static void testFollowersExtraction(TwitterConnector t) {
		String r = null;
		r = t.getUserFollowers();
		if (r == null)
			System.out.println("connection error");
		else
			System.out.println(r);
	}

	public static void testTweetsExtraction(TwitterConnector t) {
		String r = null;
		r = t.getUserActivities();
		if (r == null)
			System.out.println("connection error");
		else
			System.out.println(r);
	}

}
