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
import java.util.List;
import java.util.Map;

import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.sns.ISocialData;

/**
 * Describe your class here...
 *
 * @author Mirko
 *
 */
public class SociaDataResultModel {

	//2013-04-10T06:44:10+0000 
	private DateFormat facebookFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	private DateFormat societiesFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static final Logger logger = LoggerFactory.getLogger(SocialDataModel.class);
	private static final String VERB ="verb";
	private static final String NAME ="name";
	private static final String ICON ="icon";
	private static final String DATE ="date";
	private static final String CONTENT ="content";
	private static final String ID ="id";
	private static final String THUMB = "thumb";


	private boolean isActivity;
	private boolean isGroup;
	private boolean isPerson;

	private List<Map<String, String>> activities;
	private List<Map<String, String>> groups;
	private List<Map<String, String>> friends;

	public SociaDataResultModel(){
		isActivity=false;
		isGroup= false;
		isPerson=false;
	}

	@SuppressWarnings("unchecked")
	public void setActivityList(ISocialData socialdata){
		isActivity = true;
		activities = new ArrayList<Map<String,String>>();

		List<ActivityEntry> list = (List<ActivityEntry>)socialdata.getSocialActivity();
		for(ActivityEntry entry : list){
			if(entry.getId()!= null){
				String social = entry.getId().split(":")[0];

				String date = entry.getUpdated();
				try {
					Date parse = facebookFormat.parse(entry.getUpdated());
					date = societiesFormat.format(parse).toString();
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Map<String,String> activity = new HashMap<String, String>();
				activity.put(VERB, entry.getVerb());
				activity.put(ID, entry.getId());
				activity.put(NAME, entry.getActor().getDisplayName());
				activity.put(ICON, SocialDataModel.getIconFileName(social));
				activity.put(DATE, date);
				activity.put(CONTENT, entry.getContent());


				getActivities().add(activity);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setGroupList(ISocialData socialdata){
		isGroup = true;
		groups = new ArrayList<Map<String,String>>();
		
		List<Group> list = (List<Group>)socialdata.getSocialGroups();
		for(Group group : list){
			if(group.getId() != null){
				
				Map<String,String> gr = new HashMap<String, String>();
				gr.put(ICON, SocialDataModel.getIconFileName(group.getTitle()));
				gr.put(ID, group.getId().getGroupId());
				gr.put(NAME, group.getDescription());
				groups.add(gr);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setFiendsList(ISocialData socialdata){
		isPerson = true;
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
				friend.put(ICON,  SocialDataModel.getIconFileName(domain));
				friend.put(THUMB, person.getThumbnailUrl());
				friend.put(ID, person.getId());
				
				friends.add(friend);
			}
		}
	}

	public boolean getIsActivity() {return isActivity;}
	public void setIsActivity(boolean isActivity) {this.isActivity = isActivity;}

	public boolean getIsGroup() {return isGroup;}
	public void setIsGroup(boolean isGroup) {this.isGroup = isGroup;}

	public boolean getIsPerson() {return isPerson;}
	public void setIsPerson(boolean isPerson) {this.isPerson = isPerson;}

	public List<Map<String, String>> getActivities() {return activities;}
	public void setActivities(List<Map<String, String>> activities) {this.activities = activities;}

	public List<Map<String, String>> getGroups() {return groups;}
	public void setGroups(List<Map<String, String>> groups) {this.groups = groups;}

	public List<Map<String, String>> getFriends() {return friends;}
	public void setFriends(List<Map<String, String>> friends) {this.friends = friends;}
}
