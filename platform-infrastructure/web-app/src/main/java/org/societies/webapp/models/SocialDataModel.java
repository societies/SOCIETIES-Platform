/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.webapp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.sns.ISocialData;

/**
 * SocialDataModel provides all parameters to build jsp page
 *
 * @author Mirko
 *
 */
public class SocialDataModel {

	private static final String SOCIAL_NAME="name";
	private static final String USER_ID ="id";
	private static final String SOCIAL_ICON="icon";
	private static final String USER_PHOTO="thumbnail";
	private static final String CONNECTION_ID = "connection_id";

	private static final Logger logger = LoggerFactory.getLogger(SocialDataModel.class);

	private static final String FACEBOOK_ICON="images/Facebook.png";
	private static final String TWITTER_ICON="images/Twitter.png";
	private static final String FOURSQUARE_ICON="images/Foursquare.png";
	private static final String LINKEDIN_ICON="images/Linkedin.png";
	private static final String SN_GENERIC_ICON="images/social_network.png";

	private List<Map<String, String>> profileList;
	private Map<String,Integer> counters;
	private Map<String,String> connection;

	private int totFriends;
	private int totActivities;
	private int totGroups;


	@SuppressWarnings("unchecked")
	public SocialDataModel(ISocialData socialdata){

		profileList = new ArrayList<Map<String,String>>();
		counters = new HashMap<String, Integer>();
		connection = new HashMap<String, String>();

		connection.put("facebook", "disconnected");
		connection.put("twitter", "disconnected");
		connection.put("linkedin", "disconnected");
		connection.put("foursquare", "disconnected");
		
		List<Person> profiles = (List<Person>)socialdata.getSocialProfiles();
		//user information
		for(Person person :  profiles) {
			Map<String, String> profileMap = new HashMap<String, String>();
			logger.debug("adding person with id: "+person.getId());
			if(person.getId() != null){
				profileMap.put(SOCIAL_NAME, person.getId().split(":")[0]);
				profileMap.put(SOCIAL_ICON, getIconFileName(profileMap.get(SOCIAL_NAME)));
				profileMap.put(USER_ID, person.getId());
				profileMap.put(USER_PHOTO, person.getThumbnailUrl());
				setConnection(profileMap.get(SOCIAL_NAME));
				
				if(socialdata.getConnectionMapper().containsValue(person.getId())){
					for(String connID :socialdata.getConnectionMapper().keySet()){
						if(socialdata.getConnectionMapper().get(connID).equals(person.getId())){
							profileMap.put(CONNECTION_ID,connID);
						}
					}
				}
				profileList.add(profileMap);
				logger.debug("connector added: "+profileMap.get(SOCIAL_NAME));
			}
		}
		logger.debug("number of connector found: "+socialdata.getConnectionMapper());

		setTotFriends(socialdata.getSocialPeople().size());
		setTotGroups(socialdata.getSocialGroups().size());
		setTotActivities(socialdata.getSocialActivity().size());

		logger.debug("number of groups found: "+socialdata.getSocialGroups().size());
		List<Group> groups = (List<Group>)socialdata.getSocialGroups();
		for(Group group : groups){
			addCount("group", group.getTitle());
		}

		logger.debug("number of activities found: "+socialdata.getSocialActivity().size());
		List<ActivityEntry> activities = (List<ActivityEntry>)socialdata.getSocialActivity();
		for(ActivityEntry activity : activities){
			if(activity.getId() != null){
				String social = activity.getId().split(":")[0];
				addCount("activity",social);
			}
		}
		
		logger.debug("number of friends found: "+socialdata.getSocialPeople().size());
		List<Person> friends = (List<Person>)socialdata.getSocialPeople();
		for(Person friend : friends){
			String social = friend.getId().split(":")[0];
			addCount("friend",social);
		}

		logger.debug("counters: "+counters);
	}

	private void addCount(String prefix,String socialN){
		if(!counters.containsKey(prefix+"_"+socialN)){
			counters.put(prefix+"_"+socialN,0);
		}

		counters.put(prefix+"_"+socialN, counters.get(prefix+"_"+socialN)+1);
	}

	public static String getIconFileName(String name) {

		if(name.equalsIgnoreCase("facebook")){
			return FACEBOOK_ICON;
		} else if(name.equalsIgnoreCase("twitter")){
			return TWITTER_ICON;
		} else if(name.equalsIgnoreCase("linkedin")){
			return LINKEDIN_ICON;
		} else if(name.equalsIgnoreCase("foursquare")){
			return FOURSQUARE_ICON;
		} else {
			return SN_GENERIC_ICON;
		}
	}

	private void setConnection(String name){
		if(name.equalsIgnoreCase("facebook")){
			connection.put("facebook", "connected");
		} else if(name.equalsIgnoreCase("twitter")){
			connection.put("twitter", "connected");
		} else if(name.equalsIgnoreCase("linkedin")){
			connection.put("linkedin", "connected");
		} else if(name.equalsIgnoreCase("foursquare")){
			connection.put("foursquare", "connected");
		}
	}
	
	public List<Map<String, String>> getProfileList() {return profileList;}
	public void setProfileList(List<Map<String, String>> profileList) {	this.profileList = profileList;}

	public int getTotActivities() {return totActivities;}
	public void setTotActivities(int totActivities) {this.totActivities = totActivities;}

	public int getTotFriends() {return totFriends;}
	public void setTotFriends(int totFriends) {this.totFriends = totFriends;}

	public int getTotGroups() {return totGroups;}
	public void setTotGroups(int totGroups) {this.totGroups = totGroups;}

	public Map<String, Integer> getCounters(){return counters;}
	public void setCounters(Map<String,Integer> counters){this.counters = counters;}

	public Map<String,String> getConnection(){return connection;}
	public void setConnection(Map<String,String> connection){this.connection = connection;}
}
