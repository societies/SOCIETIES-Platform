package org.societies.platform.TwitterConnector;

import org.societies.api.internal.sns.ISocialConnector;


public interface TwitterConnector extends ISocialConnector {
	
	public static final String PROTECTED_RESOURCE_URL = "https://api.twitter.com/1/statuses/update.json";
	public static final String ACCOUNT_VERIFICATION = "https://api.twitter.com/1/account/verify_credentials.json";
	public static final String GET_USERINFO_URL = "http://api.twitter.com/1/users/lookup.json?user_id=";
	public static final String GET_FRIENDS_URL = "https://api.twitter.com/1/friends/ids.json?user_id=";
	public static final String GET_FOLLOWERS_URL = "https://api.twitter.com/1/followers/ids.json?user_id=";
	public static final String GET_OTHER_PROFILE_URL = "https://api.twitter.com/1/users/lookup.json?user_id=";
	
	/**
	 * @return
	 */
	String getUserFollowers();
	/**
	 * @param id
	 * @return
	 */
	String getOtherProfile(String id);
}