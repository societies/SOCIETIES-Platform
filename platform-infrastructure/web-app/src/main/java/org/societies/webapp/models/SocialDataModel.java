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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

	private static final Logger logger = LoggerFactory.getLogger(SocialDataModel.class);

	private static final String FACEBOOK_ICON="images/Facebook.png";
	private static final String TWITTER_ICON="images/Twitter.png";
	private static final String FOURSQUARE_ICON="images/Foursquare.png";
	private static final String LINKEDIN_ICON="images/Linkedin.png";
	private static final String SN_GENERIC_ICON="images/social_network.png";
	
	private static final String FACEBOOK = "facebook";
	private static final String TWITTER = "facebook";
	private static final String FOURSQUARE = "facebook";
	private static final String LINKEDIN = "facebook";
	
	private static final String FACEBOOK_SHORT = "fb";
	private static final String TWITTER_SHORT = "tw";
	private static final String FOURSQUARE_SHORT = "fq";
	private static final String LINKEDIN_SHORT = "lk";
	
	private static final String VERB ="verb";
	private static final String NAME ="name";
	private static final String ICON ="icon";
	private static final String DATE ="date";
	private static final String CONTENT ="content";
	private static final String ID ="id";
	private static final String THUMB = "thumb";
	
	private DateFormat facebookFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	private DateFormat societiesFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	private List<SocialProfile> profileList;
	private Map<String,Integer> counters;
	private Map<String,String> connection;

	private int totFriends;
	private int totActivities;
	private int totGroups;
	
	private List<String> connectedSocial;
	private List<String> disconnectedSocial;
	private List<String> iconForDisconnected;
	
	private List<Map<String, String>> activities;
	private List<Map<String, String>> groups;
	private List<Map<String, String>> friends;


	@SuppressWarnings("unchecked")
	public SocialDataModel(ISocialData socialdata){

		profileList = new ArrayList<SocialProfile>();
		counters = new HashMap<String, Integer>();
		connection = new HashMap<String, String>();

		connection.put("facebook", "disconnected");
		connection.put("twitter", "disconnected");
		connection.put("linkedin", "disconnected");
		connection.put("foursquare", "disconnected");
		
		List<Person> profiles = (List<Person>)socialdata.getSocialProfiles();
		//user information
		for(Person person :  profiles) {
			SocialProfile profile = new SocialProfile();
			logger.debug("adding person with id: "+person.getId());
			if(person.getId() != null){
				profile.setName(person.getId().split(":")[0]);
				profile.setIcon(getIconFileName(profile.getName()));
				profile.setId(person.getId());
				profile.setThumbnail(person.getThumbnailUrl());
				setConnection(profile.getName());
				
				if(socialdata.getConnectionMapper().containsValue(person.getId())){
					for(String connID :socialdata.getConnectionMapper().keySet()){
						if(socialdata.getConnectionMapper().get(connID).equals(person.getId())){
							profile.setConnection_id(connID);
						}
					}
				}
				profileList.add(profile);
				logger.debug("connector added: "+profile.getName());
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
		
		setFiendsList(socialdata);
		setGroupList(socialdata);
		setActivityList(socialdata);
	}

	private void addCount(String prefix,String socialN){
		if(!counters.containsKey(prefix+"_"+socialN)){
			counters.put(prefix+"_"+socialN,0);
		}

		counters.put(prefix+"_"+socialN, counters.get(prefix+"_"+socialN)+1);
	}

	public String getIconFileName(String name) {

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
	
	public String getSocialNameFromIcon(String icon)
	{
		if(FACEBOOK_ICON.equalsIgnoreCase(icon)){
			return FACEBOOK;
		} else if(TWITTER_ICON.equalsIgnoreCase(icon)){
			return TWITTER;
		} else if(LINKEDIN_ICON.equalsIgnoreCase(icon)){
			return LINKEDIN;
		} else if(FOURSQUARE_ICON.equalsIgnoreCase(icon)){
			return FOURSQUARE;
		} else {
			return "social";
		}
	}
	
	public String getSocialNameShortFromIcon(String icon)
	{
		if(FACEBOOK_ICON.equalsIgnoreCase(icon)){
			return FACEBOOK_SHORT;
		} else if(TWITTER_ICON.equalsIgnoreCase(icon)){
			return TWITTER_SHORT;
		} else if(LINKEDIN_ICON.equalsIgnoreCase(icon)){
			return LINKEDIN_SHORT;
		} else if(FOURSQUARE_ICON.equalsIgnoreCase(icon)){
			return FOURSQUARE_SHORT;
		} else {
			return "social_short";
		}
	}
	
	public String getSocialNameShortFromLong(String longName)
	{
		if(FACEBOOK.equalsIgnoreCase(longName)){
			return FACEBOOK_SHORT;
		} else if(TWITTER.equalsIgnoreCase(longName)){
			return TWITTER_SHORT;
		} else if(LINKEDIN.equalsIgnoreCase(longName)){
			return LINKEDIN_SHORT;
		} else if(FOURSQUARE.equalsIgnoreCase(longName)){
			return FOURSQUARE_SHORT;
		} else {
			return "social_short";
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
	
	@SuppressWarnings("unchecked")
	public void setActivityList(ISocialData socialdata){
		activities = new ArrayList<Map<String,String>>();

		List<ActivityEntry> list = (List<ActivityEntry>)socialdata.getSocialActivity();
		for(ActivityEntry entry : list){
			if(entry.getId()!= null){
				String social = entry.getId().split(":")[0];

				String date = entry.getUpdated();
				try {
					if(entry.getUpdated() != null)
					{
						Date parse = facebookFormat.parse(entry.getUpdated());
						date = societiesFormat.format(parse).toString();
					}
					
				} catch (ParseException e) {
					logger.error(e.getMessage(),e);
				}
				
				Map<String,String> activity = new HashMap<String, String>();
				activity.put(VERB, entry.getVerb());
				activity.put(ID, entry.getId());
				activity.put(NAME, entry.getActor().getDisplayName());
				activity.put(ICON, getIconFileName(social));
				activity.put(DATE, date);
				activity.put(CONTENT, entry.getContent());


				getActivities().add(activity);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setGroupList(ISocialData socialdata){
		groups = new ArrayList<Map<String,String>>();
		
		List<Group> list = (List<Group>)socialdata.getSocialGroups();
		for(Group group : list){
			if(group.getId() != null){
				
				Map<String,String> gr = new HashMap<String, String>();
				gr.put(ICON, getIconFileName(group.getTitle()));
				gr.put(ID, group.getId().getGroupId());
				gr.put(NAME, group.getDescription());
				groups.add(gr);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setFiendsList(ISocialData socialdata){
		friends = new ArrayList<Map<String,String>>();
		
		List<Person> list = (List<Person>)socialdata.getSocialPeople();
		for(Person person : list){
			if(person.getId()!= null){
				Map<String, String> friend = new HashMap<String, String>();
				
				// get Name???
				String name="";
				if (person.getName() != null) {
					if (person.getName().getFormatted() != null)
						name = person.getName().getFormatted();
					else {
						if (person.getName().getFamilyName() != null)
							name = person.getName().getFamilyName();
						if (person.getName().getGivenName() != null) {
							if (name.length() > 0)
								name += " ";
							name += person.getName().getGivenName();
						}
					}
				}

				String domain = person.getId().split(":")[0];
					
				friend.put(NAME, name);
				friend.put(ICON,  getIconFileName(domain));
				friend.put(THUMB, person.getThumbnailUrl());
				friend.put(ID, person.getId());
				
				friends.add(friend);
			}
		}
	}

	public List<Map<String, String>> getActivities() {return activities;}
	public void setActivities(List<Map<String, String>> activities) {this.activities = activities;}

	public List<Map<String, String>> getGroups() {return groups;}
	public void setGroups(List<Map<String, String>> groups) {this.groups = groups;}

	public List<Map<String, String>> getFriends() {return friends;}
	public void setFriends(List<Map<String, String>> friends) {this.friends = friends;}
	
	/**
	 * @return the profileList
	 */
	public List<SocialProfile> getProfileList() {
		return profileList;
	}

	/**
	 * @param profileList the profileList to set
	 */
	public void setProfileList(List<SocialProfile> profileList) {
		this.profileList = profileList;
	}

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
	
	public List<String> getAllSocial()
	{
		if(connection != null)
			return new ArrayList<String>(connection.keySet());
		else return new ArrayList<String>();
			
	}
	
	public List<String> getConnectedSocial()
	{
		if(connection != null)
		{
			connectedSocial = new ArrayList<String>();
			for(Iterator<String> keyIterator = connection.keySet().iterator(); keyIterator.hasNext();)
			{
				String key = keyIterator.next();
				if("connected".equals(connection.get(key)))
					connectedSocial.add(key);
			}
		}
		logger.debug("CONNECTED SOCIAL: "+connectedSocial);
		return connectedSocial;
	}
	
	public List<String> getDisconnectedSocial()
	{
		disconnectedSocial = new ArrayList<String>(getAllSocial());
		disconnectedSocial.removeAll(getConnectedSocial());
		logger.debug("DISCONNECTED SOCIAL: "+disconnectedSocial);
		return disconnectedSocial;
		
	}
	
	public List<String> getIconForDisconnected()
	{
		iconForDisconnected = new ArrayList<String>();
		List<String> connected = getDisconnectedSocial();
		for(String s : connected)
		{
			String icon = getIconFileName(s);
			iconForDisconnected.add(icon);
		}
		return iconForDisconnected;
	}
}
