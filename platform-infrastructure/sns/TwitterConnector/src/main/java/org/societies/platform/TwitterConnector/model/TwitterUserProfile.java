package org.societies.platform.TwitterConnector.model;

import java.util.ArrayList;

/*
 * Twitter user profile (light)
 */

class TwitterUserProfile {
	
	private String userID;
	private String name;
	private String screenName;
	private String language;
	private ArrayList<TwitterUserProfile> followers = new ArrayList<TwitterUserProfile>();
	private ArrayList<TwitterUserProfile> friends = new ArrayList<TwitterUserProfile>();
	
	public TwitterUserProfile(String userID){
		this.userID = userID;
	}


	public String getUserID() {
		return userID;
	}


	public void setUserID(String userID) {
		this.userID = userID;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getScreenName() {
		return screenName;
	}


	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}


	public ArrayList<TwitterUserProfile> getFollowers() {
		return followers;	
	}
	

	

	public void setFollowers(ArrayList<TwitterUserProfile> followers) {
		this.followers = followers;
	}
	
	


	public ArrayList<TwitterUserProfile> getFriends() {
		return friends;
	}


	public void setFriends(ArrayList<TwitterUserProfile> friends) {
		this.friends = friends;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}
	
	
}