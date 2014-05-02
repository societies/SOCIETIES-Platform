/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
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

package org.societies.webapp.controller.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.activityfeed.GetActivities;
import org.societies.api.schema.activityfeed.GetActivitiesResponse;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Eliza
 *
 */
@ViewScoped
@ManagedBean(name="ActivityFeedController")
public class ActivityFeedController extends BasePageController{
	private static final String ALL = "all";
	static enum Method {POSTACTIVITIES, GET_ACTIVITIES};
	private final Logger logging = LoggerFactory.getLogger(getClass());

	@ManagedProperty(value = "#{activityFeedManager}")
	private IActivityFeedManager activityFeedManager;

	@ManagedProperty(value = "#{cisManager}")
	private ICisManager cisManager;


	@ManagedProperty(value = "#{commMngrRef}")
	private ICommManager commMngrRef;

	
	@ManagedProperty(value = "#{userService}")
	private UserService userService;
	
	private List<ICis> cisList;
	private String selectedCIS = "";
	
	
	private List<MarshaledActivity> activities = new ArrayList<MarshaledActivity>();

	private String postActivityText = "";

	private IActivityFeed activityFeed;


	
	private Hashtable<String, List<MarshaledActivity>> hash = new Hashtable<String, List<MarshaledActivity>>();
	
	@PostConstruct
	public void initController(){
		this.logging.info("#CODE2#: Initialising ActivityFeed controller");
		cisList = cisManager.getCisList();
		this.logging.debug("Got "+cisList.size()+" CISs that I own or participate in");

		if (this.selectedCIS.trim().length()==0){
			if (this.cisList.size()==0){
				selectedCIS = ALL;
			}else{
				selectedCIS = this.cisList.get(0).getCisId();
			}
		}
		handleSelectedCISChange();

	}

	public void postActivity(){
		logging.debug("Attemping at posting an activity!");
		if (this.postActivityText.trim().equalsIgnoreCase("") || postActivityText.trim().length()==0){
			addGlobalMessage("Empty Post", "Post cannot be empty. Please type your message in the textbox", FacesMessage.SEVERITY_WARN);
			return;
		}
		if (this.activityFeed!=null){
			IActivity activity = this.activityFeed.getEmptyIActivity();
			activity.setActor(this.userService.getUserID());
			activity.setVerb(postActivityText);
			String uuid = UUID.randomUUID().toString();
			activityFeed.addActivity(activity, new ActivityFeedCallback(uuid, Method.POSTACTIVITIES));
			addGlobalMessage("Posted", "Posted message to: "+selectedCIS+" CIS feed", FacesMessage.SEVERITY_INFO);

			
			synchronized (hash) {
				while (!hash.containsKey(uuid)){
					try {
						hash.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			this.logging.debug("Done posting activity. removing from hashtable");
			this.hash.remove(uuid);
			this.handleSelectedCISChange();
			this.postActivityText="";
		}else{
			addGlobalMessage("Opps, something when wrong!", "Something is wrong. ActivityFeed is null", FacesMessage.SEVERITY_ERROR);
			
			return;
		}
		
		
		
		
	}

	
	public void orderByDate(){
		 Collections.sort(activities, new Comparator<MarshaledActivity>(){
	           public int compare (MarshaledActivity m1, MarshaledActivity m2){
	        	   
	        	   int compareTo = getDate(m1.getPublished()).compareTo(getDate(m2.getPublished()));

	        	   if (compareTo>0){
	        		   return -1;
	        	   }
	        	   if (compareTo<0){
	        		   return 1;
	        	   }
	        	   
	        	   return compareTo;
	           }
	       });
	}
	
	public void handleSelectedCISChange(){
		if (selectedCIS.equalsIgnoreCase(ALL)){
			FacesMessage message = new FacesMessage("Not Implemented yet");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}else{
			if (!selectedCIS.equalsIgnoreCase("")){
				for (ICis cis: cisList){
					if (cis.getCisId().equalsIgnoreCase(selectedCIS)){
						setActivityFeed(cis.getActivityFeed());

						 String uuid = UUID.randomUUID().toString();
						IActivityFeedCallback callback = new ActivityFeedCallback(uuid, Method.GET_ACTIVITIES);
						getActivityFeed().getActivities("0 "+Long.toString(System.currentTimeMillis()),callback );
						synchronized (hash) {
							while (!this.hash.containsKey(uuid)){
								try {
									hash.wait();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						
						this.activities = this.hash.remove(uuid);
						this.logging.debug("Done loading activities: "+activities.size());
					}

				}
			}
		}
	}
	
	public Date getDate(String currentTimeMillis){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.parseLong(currentTimeMillis));
		return cal.getTime();
		
	}
	
	private class ActivityFeedCallback implements IActivityFeedCallback{

		private String uuid;
		private Method method;

		
		
		public ActivityFeedCallback(String uuid, Method method) {
			this.uuid = uuid;
			this.method = method;
			
		}

		@Override
		public void receiveResult(MarshaledActivityFeed marshalledActivity) {
			
			if (method.equals(Method.POSTACTIVITIES)){
				logging.debug("ReceivedResult: addedActivity: "+marshalledActivity.getAddActivityResponse().toString());
				
				synchronized (hash) {
					hash.put(uuid, new ArrayList<MarshaledActivity>());
					hash.notifyAll();
				}
				return;
			}
			
			GetActivitiesResponse getActivitiesResponse = marshalledActivity.getGetActivitiesResponse();
			
			List<MarshaledActivity> marshaledActivity = new ArrayList<MarshaledActivity>();
			if (getActivitiesResponse!=null){
				marshaledActivity = getActivitiesResponse.getMarshaledActivity();
				if (marshaledActivity==null){
					marshaledActivity = new ArrayList<MarshaledActivity>();
				}
			}
			logging.debug("Found: "+marshaledActivity.size()+" for selected cis: "+selectedCIS);

			synchronized (hash) {
				hash.put(uuid, marshaledActivity);
				hash.notifyAll();
			}
			
			logging.debug("Notified activities");
		}
		
	}
	public IActivityFeedManager getActivityFeedManager() {
		return activityFeedManager;
	}

	public void setActivityFeedManager(IActivityFeedManager activityFeedManager) {
		this.activityFeedManager = activityFeedManager;
	}

	public List<MarshaledActivity> getActivities() {
		this.logging.debug("GetActivities: "+activities.size());
		orderByDate();
		return activities;
	}

	public void setActivities(List<MarshaledActivity> activities) {
		this.activities = activities;
	}

	public String getPostActivityText() {
		return postActivityText;
	}

	public void setPostActivityText(String postActivityText) {
		this.postActivityText = postActivityText;
	}

	public IActivityFeed getActivityFeed() {
		return activityFeed;
	}

	public void setActivityFeed(IActivityFeed activityFeed) {
		this.activityFeed = activityFeed;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public List<ICis> getCisList() {
		return cisList;
	}

	public void setCisList(List<ICis> cisList) {
		this.cisList = cisList;
	}

	public String getSelectedCIS() {
		return selectedCIS;
	}

	public void setSelectedCIS(String selectedCIS) {
		this.selectedCIS = selectedCIS;
	}

	public ICommManager getCommMngrRef() {
		return commMngrRef;
	}

	public void setCommMngrRef(ICommManager commMngrRef) {
		this.commMngrRef = commMngrRef;
	}

	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}
}
