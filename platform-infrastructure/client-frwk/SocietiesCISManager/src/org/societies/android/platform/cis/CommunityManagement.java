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
package org.societies.android.platform.cis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.cis.directory.ACisAdvertisementRecord;
import org.societies.android.api.cis.management.AActivity;
import org.societies.android.api.cis.management.ACommunity;
import org.societies.android.api.cis.management.ACriteria;
import org.societies.android.api.cis.management.AJoinResponse;
import org.societies.android.api.cis.management.AMembershipCrit;
import org.societies.android.api.cis.management.AParticipant;
import org.societies.android.api.cis.management.ICisManager;
import org.societies.android.api.cis.management.ICisSubscribed;
import org.societies.android.api.internal.servicemonitor.InstalledAppInfo;
import org.societies.android.api.servicelifecycle.AService;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.activity.Activity;
import org.societies.api.schema.activityfeed.Activityfeed;
import org.societies.api.schema.activityfeed.AddActivity;
import org.societies.api.schema.activityfeed.AddActivityResponse;
import org.societies.api.schema.activityfeed.CleanUpActivityFeed;
import org.societies.api.schema.activityfeed.CleanUpActivityFeedResponse;
import org.societies.api.schema.activityfeed.DeleteActivity;
import org.societies.api.schema.activityfeed.DeleteActivityResponse;
import org.societies.api.schema.activityfeed.GetActivities;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.DeleteMember;
import org.societies.api.schema.cis.community.JoinResponse;
import org.societies.api.schema.cis.community.LeaveResponse;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.community.Qualification;
import org.societies.api.schema.cis.manager.CommunityManager;
import org.societies.api.schema.cis.manager.Create;
import org.societies.api.schema.cis.manager.DeleteNotification;
import org.societies.api.schema.cis.manager.ListCrit;
import org.societies.api.schema.cis.manager.Notification;
import org.societies.api.schema.cis.manager.SubscribedTo;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class CommunityManagement extends Service implements ICisManager, ICisSubscribed {

	//COMMS REQUIRED VARIABLES
	private static final List<String> ELEMENT_NAMES = Arrays.asList("communityManager", "communityMethods", "activityfeed");
    private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/schema/cis/manager",
														    	  "http://societies.org/api/schema/activityfeed",	  		
																  "http://societies.org/api/schema/cis/community");
    private static final List<String> PACKAGES = Arrays.asList("org.societies.api.schema.cis.manager",
													    	   "org.societies.api.schema.activityfeed",
															   "org.societies.api.schema.cis.community");
    private ClientCommunicationMgr commMgr;
    
    //SERVICE LIFECYCLE INTENTS
	public static final String INTENT_RETURN_VALUE = "org.societies.android.platform.community.ReturnValue";
	public static final String INTENT_RETURN_BOOLEAN = "org.societies.android.platform.community.ReturnBoolean"; // extra from True/False methods

	//CIS MANAGER INTENTS
	public static final String CREATE_CIS     = "org.societies.android.platform.community.CREATE_CIS";
	public static final String DELETE_CIS    = "org.societies.android.platform.community.DELETE_CIS";
	public static final String GET_CIS_LIST     = "org.societies.android.platform.community.GET_CIS_LIST";
	public static final String SUBSCRIBE_TO_CIS = "org.societies.android.platform.community.SUBSCRIBE_TO_CIS";
	public static final String UNSUBSCRIBE_FROM_CIS = "org.societies.android.platform.community.UNSUBSCRIBE_FROM_CIS";
	public static final String REMOVE_MEMBER = "org.societies.android.platform.community.REMOVE_MEMBER";
	//CIS SUBSCRIBER INTENTS
	public static final String JOIN_CIS     = "org.societies.android.platform.community.JOIN_CIS";
	public static final String LEAVE_CIS    = "org.societies.android.platform.community.LEAVE_CIS";
	public static final String GET_MEMBERS     = "org.societies.android.platform.community.GET_MEMBERS";
	public static final String GET_ACTIVITY_FEED = "org.societies.android.platform.community.GET_ACTIVITY_FEED";
	public static final String ADD_ACTIVITY = "org.societies.android.platform.community.ADD_ACTIVITY";
	public static final String DELETE_ACTIVITY = "org.societies.android.platform.community.DELETE_ACTIVITY";
	public static final String CLEAN_ACTIVITIES = "org.societies.android.platform.community.CLEAN_ACTIVITIES";
	public static final String GET_CIS_INFO = "org.societies.android.platform.community.GET_CIS_INFO";
	
	
    private static final String LOG_TAG = CommunityManagement.class.getName();
    private IBinder binder = null;
    
    @Override
	public void onCreate () {
		this.binder = new LocalBinder();
		Log.d(LOG_TAG, "CommunityManagement service starting");
		try {
			//INSTANTIATE COMMS MANAGER
			commMgr = new ClientCommunicationMgr(this);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
        }    
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "CommunityManagement service terminating");
	}

	/**Create Binder object for local service invocation */
	public class LocalBinder extends Binder {
		public CommunityManagement getService() {
			return CommunityManagement.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return this.binder;
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ICisManager >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/* @see org.societies.android.api.cis.management.ICisManager#createCis(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Hashtable, java.lang.String)*/
	public ACommunity createCis(String client, String cisName, String cisType, String description, List<ACriteria> criteria, String privacyPolicy) {
		Log.d(LOG_TAG, "createCis called by client: " + client);
		
		//COMMUNITY INFO
		Community cisinfo = new Community();
		cisinfo.setCommunityName(cisName);
		cisinfo.setDescription(description);
		cisinfo.setCommunityType(cisType);
		//MEMBERSHIP CRITERIA - CONVERT FROM PARCELABLE VERSION
		MembershipCrit rules = new MembershipCrit();
		List<Criteria> listCriteria = new ArrayList<Criteria>();
		for(ACriteria acrit: criteria) {
			listCriteria.add( ACriteria.convertACriteria(acrit));
		}	
		rules.setCriteria(listCriteria);
		cisinfo.setMembershipCrit(rules); //TODO: NOT ADDING RULES, THEN THEY WON'T BE CHECKED ON JOINING - NEEDS FIX 
		//ADD TO BEAN
		Create create = new Create();
		create.setCommunity(cisinfo);
		//CREATE MESSAGE BEAN
		CommunityManager messageBean = new CommunityManager();
		messageBean.setCreate(create);

		//COMMS STUFF
		ICommCallback cisCallback = new CommunityCallback(client, CREATE_CIS); 
		IIdentity toID = commMgr.getIdManager().getCloudNode();
		Log.e(LOG_TAG, ">>>>>>>>>>>>>>Cloud Node: " + toID.getJid());
		Stanza stanza = new Stanza(toID);
        try {
        	commMgr.register(ELEMENT_NAMES, cisCallback);
        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
        }
        return null;
	}

	/* @see org.societies.android.api.cis.management.ICisManager#deleteCis(java.lang.String, java.lang.String)*/
	public Boolean deleteCis(String client, String cisId) {
		Log.d(LOG_TAG, "deleteCis called by client: " + client);
		
		//COMMUNITY INFO
		org.societies.api.schema.cis.manager.Delete cisDel = new org.societies.api.schema.cis.manager.Delete();
		cisDel.setCommunityJid(cisId);
		//CREATE MESSAGE BEAN
		CommunityManager messageBean = new CommunityManager();
		messageBean.setDelete(cisDel);

		//COMMS STUFF
		ICommCallback cisCallback = new CommunityCallback(client, DELETE_CIS); 
		IIdentity toID = commMgr.getIdManager().getCloudNode();
		Log.e(LOG_TAG, ">>>>>>>>>>>>>>Cloud Node: " + toID.getJid());
		Stanza stanza = new Stanza(toID);
        try {
        	commMgr.register(ELEMENT_NAMES, cisCallback);
        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
        }
        return null;
	}

	/* @see org.societies.android.api.cis.management.ICisManager#getCisList(java.lang.String, org.societies.api.schema.cis.manager.ListCrit)*/
	public ACommunity[] getCisList(String client, String query) {
		Log.d(LOG_TAG, "createCis called by client: " + client);
		
		//COMMUNITY INFO
		org.societies.api.schema.cis.manager.List list = new org.societies.api.schema.cis.manager.List();
		list.setListCriteria(ListCrit.fromValue(query));
		//CREATE MESSAGE BEAN
		CommunityManager messageBean = new CommunityManager();
		messageBean.setList(list);

		//COMMS STUFF
		ICommCallback cisCallback = new CommunityCallback(client, GET_CIS_LIST); 
		IIdentity toID = commMgr.getIdManager().getCloudNode();
		Log.e(LOG_TAG, ">>>>>>>>>>>>>>Cloud Node: " + toID.getJid());
		Stanza stanza = new Stanza(toID);
        try {
        	commMgr.register(ELEMENT_NAMES, cisCallback);
        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
        }
        return null;
	}

	/* @see org.societies.android.api.cis.management.ICisManager#removeMember(java.lang.String, java.lang.String, java.lang.String)*/
	public void removeMember(String client, String cisId, String memberJid) {
		Log.d(LOG_TAG, "removeMember called by client: " + client);
		
		//MEMBER INFO
		Participant member = new Participant();
		member.setJid(memberJid);
		//DELETE INFO
		DeleteMember delMember = new DeleteMember();
		delMember.setParticipant(member);
		//CREATE MESSAGE BEAN
		CommunityMethods messageBean = new CommunityMethods();
		messageBean.setDeleteMember(delMember);

		//COMMS STUFF
		ICommCallback cisCallback = new CommunityCallback(client, REMOVE_MEMBER); 
		IIdentity toID = null;
		try { //DELETE MEMBER IS PROVIDED BY THE CIS (NOT CIS MANAGER)
			toID = commMgr.getIdManager().fromJid(cisId);
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}		
		Stanza stanza = new Stanza(toID);
        try {
        	commMgr.register(ELEMENT_NAMES, cisCallback);
        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
        }		
	}

	/* @see org.societies.android.api.cis.management.ICisManager#subscribeToCommunity(java.lang.String, java.lang.String, java.lang.String)*/
/*	public void subscribeToCommunity(String client, String name, String cisId) {
		Log.d(LOG_TAG, "subscribeToCommunity called by client: " + client);
		
		//COMMUNIY INFO
		Community cis = new Community();
		cis.setCommunityJid(cisId);
		cis.setCommunityName(name);
		//SUBSCRIBED TO INFO
		SubscribedTo subTo = new SubscribedTo();
		subTo.setCommunity(cis);
		//NOTIFICATION INFO
		Notification note = new Notification();
		note.setSubscribedTo(subTo);
		//CREATE MESSAGE BEAN
		CommunityManager messageBean = new CommunityManager();
		messageBean.setNotification(note);
		
		//COMMS STUFF
		IIdentity toID = commMgr.getIdManager().getCloudNode();
		Log.e(LOG_TAG, ">>>>>>>>>>>>>>Cloud Node: " + toID.getJid());
		Stanza stanza = new Stanza(toID);
        try {
        	commMgr.register(ELEMENT_NAMES, new CommunityCallback());
        	commMgr.sendMessage(stanza, messageBean);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
        }
	}*/

	/* @see org.societies.android.api.cis.management.ICisManager#unsubscribeFromCommunity(java.lang.String, java.lang.String)*/
	/*public void unsubscribeFromCommunity(String client, String cisId) {
		Log.d(LOG_TAG, "unsubscribeFromCommunity called by client: " + client);
		
		//DELETE NOTIFICATION
		DeleteNotification delNote = new DeleteNotification();
		delNote.setCommunityJid(cisId);
		//NOTIFICATION INFO
		Notification note = new Notification();
		note.setDeleteNotification(delNote);
		//CREATE MESSAGE BEAN
		CommunityManager messageBean = new CommunityManager();
		messageBean.setNotification(note);
		
		//COMMS STUFF
		IIdentity toID = commMgr.getIdManager().getCloudNode();
		Log.e(LOG_TAG, ">>>>>>>>>>>>>>Cloud Node: " + toID.getJid());
		Stanza stanza = new Stanza(toID);
        try {
        	commMgr.register(ELEMENT_NAMES, new CommunityCallback());
        	commMgr.sendMessage(stanza, messageBean);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
        }
	}*/
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ICisSubscribed >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/* @see org.societies.android.api.cis.management.ICisSubscribed#Join(java.lang.String, java.lang.String, java.util.List)*/
	public AJoinResponse Join(String client, ACisAdvertisementRecord targetCis) {
		Log.d(LOG_TAG, "Join CIS called by client: " + client);

		
		//CREATE JOIN INFO
		org.societies.api.schema.cis.manager.AskCisManagerForJoin join = new org.societies.api.schema.cis.manager.AskCisManagerForJoin();
		join.setCisAdv(  ACisAdvertisementRecord.convertACisAdvertRecord(targetCis));
		//org.societies.api.schema.cis.community.Join join = new org.societies.api.schema.cis.community.Join();
		//List<Qualification> qualifications = new ArrayList<Qualification>();
		//join.setQualification(qualifications); TODO: GET MEMBERSHIP CRITERIA AND QUERY CONTEXT FOR QUALIFICATIONS FOR JOINING
		//CREATE MESSAGE BEAN
		CommunityManager messageBean = new CommunityManager();
		messageBean.setAskCisManagerForJoin(join);

		//COMMS STUFF
		ICommCallback cisCallback = new CommunityCallback(client, JOIN_CIS); 
		IIdentity toID = null; 
		toID = commMgr.getIdManager().getCloudNode();	
		Stanza stanza = new Stanza(toID);
        try {
        	commMgr.register(ELEMENT_NAMES, cisCallback);
        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
        }
        return null;
	}

	/* @see org.societies.android.api.cis.management.ICisSubscribed#Leave(java.lang.String, java.lang.String)*/
	public LeaveResponse Leave(String client, String cisId) {
		Log.d(LOG_TAG, "Leave CIS called by client: " + client);

		//CREATE JOIN INFO
		org.societies.api.schema.cis.community.Leave leave = new org.societies.api.schema.cis.community.Leave();
		//CREATE MESSAGE BEAN
		CommunityMethods messageBean = new CommunityMethods();
		messageBean.setLeave(leave);

		//COMMS STUFF
		ICommCallback cisCallback = new CommunityCallback(client, LEAVE_CIS); 
		IIdentity toID = null;
		try { 
			toID = commMgr.getIdManager().fromJid(cisId);
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}		
		Stanza stanza = new Stanza(toID);
        try {
        	commMgr.register(ELEMENT_NAMES, cisCallback);
        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
        }
        return null;
	}

	/* @see org.societies.android.api.cis.management.ICisSubscribed#addActivity(java.lang.String, org.societies.api.schema.activityfeed.AddActivity)*/
	public Boolean addActivity(String client, String cisId, AActivity activity) {
		Log.d(LOG_TAG, "addActivity called by client: " + client);

		//GETFEED OBJECT
		AddActivity addAct = new AddActivity();
		addAct.setActivity(AActivity.convertAActivity(activity));
		//CREATE MESSAGE BEAN
		org.societies.api.schema.activityfeed.Activityfeed messageBean = new org.societies.api.schema.activityfeed.Activityfeed();
		messageBean.setAddActivity(addAct);

		//COMMS STUFF
		ICommCallback cisCallback = new CommunityCallback(client, ADD_ACTIVITY); 
		IIdentity toID = null;
		try { 
			toID = commMgr.getIdManager().fromJid(cisId);
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}		
		Stanza stanza = new Stanza(toID);
        try {
        	commMgr.register(ELEMENT_NAMES, cisCallback);
        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
        }
        return null;
	}

	/* @see org.societies.android.api.cis.management.ICisSubscribed#cleanActivityFeed(java.lang.String)*/
	public CleanUpActivityFeedResponse cleanActivityFeed(String client, String cisId) {
		Log.d(LOG_TAG, "addActivity called by client: " + client);

		//GETFEED OBJECT
		CleanUpActivityFeed feed = new CleanUpActivityFeed();
		//CREATE MESSAGE BEAN
		org.societies.api.schema.activityfeed.Activityfeed messageBean = new org.societies.api.schema.activityfeed.Activityfeed();
		messageBean.setCleanUpActivityFeed(feed);

		//COMMS STUFF
		ICommCallback cisCallback = new CommunityCallback(client, CLEAN_ACTIVITIES); 
		IIdentity toID = null;
		try { 
			toID = commMgr.getIdManager().fromJid(cisId);
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}		
		Stanza stanza = new Stanza(toID);
        try {
        	commMgr.register(ELEMENT_NAMES, cisCallback);
        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
        }
        return null;
	}

	/*@see org.societies.android.api.cis.management.ICisSubscribed#deleteActivity(java.lang.String, org.societies.api.schema.activityfeed.DeleteActivity)*/
	public Boolean deleteActivity(String client, String cisId, AActivity activity) {
		Log.d(LOG_TAG, "deleteActivity called by client: " + client);

		//GETFEED OBJECT
		DeleteActivity getFeed = new DeleteActivity();
		getFeed.setActivity(activity);
		//CREATE MESSAGE BEAN
		org.societies.api.schema.activityfeed.Activityfeed messageBean = new org.societies.api.schema.activityfeed.Activityfeed();
		messageBean.setDeleteActivity(getFeed);

		//COMMS STUFF
		ICommCallback cisCallback = new CommunityCallback(client, DELETE_ACTIVITY); 
		IIdentity toID = null;
		try { 
			toID = commMgr.getIdManager().fromJid(cisId);
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}		
		Stanza stanza = new Stanza(toID);
        try {
        	commMgr.register(ELEMENT_NAMES, cisCallback);
        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
        }
        return null;
	}

	/* @see org.societies.android.api.cis.management.ICisSubscribed#getActivityFeed(java.lang.String, java.lang.String)*/
	public Activity[] getActivityFeed(String client, String cisId) {
		Log.d(LOG_TAG, "getActivityFeed called by client: " + client);

		//GETFEED OBJECT
		GetActivities getFeed = new GetActivities();
		//CREATE MESSAGE BEAN
		org.societies.api.schema.activityfeed.Activityfeed messageBean = new org.societies.api.schema.activityfeed.Activityfeed();
		messageBean.setGetActivities(getFeed);

		//COMMS STUFF
		ICommCallback cisCallback = new CommunityCallback(client, GET_ACTIVITY_FEED); 
		IIdentity toID = null;
		try { 
			toID = commMgr.getIdManager().fromJid(cisId);
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}		
		Stanza stanza = new Stanza(toID);
        try {
        	commMgr.register(ELEMENT_NAMES, cisCallback);
        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
        }
        return null;
	}

	/* @see org.societies.android.api.cis.management.ICisSubscribed#getCisInformation(java.lang.String, java.lang.String)*/
	public ACommunity getCisInformation(String client, String cisId) {
		Log.d(LOG_TAG, "getCisInformation called by client: " + client);

		//GETINFO OBJECT
		org.societies.api.schema.cis.community.GetInfo info = new org.societies.api.schema.cis.community.GetInfo();
		//CREATE MESSAGE BEAN
		CommunityMethods messageBean = new CommunityMethods();
		messageBean.setGetInfo(info);

		//COMMS STUFF
		ICommCallback cisCallback = new CommunityCallback(client, GET_CIS_INFO); 
		IIdentity toID = null;
		try { 
			toID = commMgr.getIdManager().fromJid(cisId);
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}		
		Stanza stanza = new Stanza(toID);
        try {
        	commMgr.register(ELEMENT_NAMES, cisCallback);
        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
        }
        return null;
	}

	/* @see org.societies.android.api.cis.management.ICisSubscribed#getMembers(java.lang.String, java.lang.String)*/
	public String[] getMembers(String client, String cisId) {
		Log.d(LOG_TAG, "getMembers called by client: " + client);

		//CREATE LIST INFO
		org.societies.api.schema.cis.community.Who listing = new org.societies.api.schema.cis.community.Who();
		//CREATE MESSAGE BEAN
		CommunityMethods messageBean = new CommunityMethods();
		messageBean.setWho(listing);

		//COMMS STUFF
		ICommCallback cisCallback = new CommunityCallback(client, GET_MEMBERS); 
		IIdentity toID = null;
		try { 
			toID = commMgr.getIdManager().fromJid(cisId);
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}		
		Stanza stanza = new Stanza(toID);
        try {
        	commMgr.register(ELEMENT_NAMES, cisCallback);
        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
        }
        return null;
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> COMMS CALLBACK >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/**
	 * Callback required for Android Comms Manager
	 */
	private class CommunityCallback implements ICommCallback {
		private String returnIntent;
		private String client;

		public CommunityCallback() { }
		
		/**Constructor sets the calling client and Intent to be returned
		 * @param client
		 * @param returnIntent
		 */
		public CommunityCallback(String client, String returnIntent) {
			this.client = client;
			this.returnIntent = returnIntent;
		}

		public List<String> getXMLNamespaces() {
			return NAME_SPACES;
		}

		public List<String> getJavaPackages() {
			return PACKAGES;
		}

		public void receiveError(Stanza arg0, XMPPError err) {
			Log.d(LOG_TAG, "Callback receiveError:" + err.getMessage());			
		}

		public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
			Log.d(LOG_TAG, "Callback receiveInfo");
		}

		public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
			Log.d(LOG_TAG, "Callback receiveItems");
		}

		public void receiveMessage(Stanza arg0, Object arg1) {
			Log.d(LOG_TAG, "Callback receiveMessage");	
		}

		public void receiveResult(Stanza returnStanza, Object msgBean) {
			Log.d(LOG_TAG, "CIS Callback receiveResult");
			
			if (client != null) {
				Intent intent = new Intent(returnIntent);
				
				Log.d(LOG_TAG, ">>>>>Return Stanza: " + returnStanza.toString());
				if (msgBean==null) Log.d(LOG_TAG, ">>>>msgBean is null");
				// --------- COMMUNITY MANAGER Bean ---------
				if (msgBean instanceof CommunityManager) {
					Log.d(LOG_TAG, "CommunityManager Result!");
					CommunityManager communityResult = (CommunityManager) msgBean;
					
					//CREATE COMMUNITY RESULT
					if (communityResult.getCreate() != null) {
						Log.d(LOG_TAG, "Create CIS Result!");
						if(communityResult.getCreate().isResult() == true){
							Community cis = communityResult.getCreate().getCommunity();
							//CONVERT TO PARCEL BEAN
							Parcelable pCis  = ACommunity.convertCommunity(cis);
							//NOTIFY CALLING CLIENT
							intent.putExtra(INTENT_RETURN_VALUE, pCis); 
						}
						intent.putExtra(INTENT_RETURN_BOOLEAN,communityResult.getCreate().isResult());
						
					}
					
					//LIST COMMUNITIES RESULT
					else if (communityResult.getListResponse() != null) {
						Log.d(LOG_TAG, "List CIS Result!");
						List<Community> listReturned = communityResult.getListResponse().getCommunity();
						//CONVERT TO PARCEL BEANS
						Parcelable returnArray[] = new Parcelable[listReturned.size()];
						for (int i=0; i<listReturned.size(); i++) {
							ACommunity cis = ACommunity.convertCommunity(listReturned.get(i)); 
							returnArray[i] = cis;
							Log.d(LOG_TAG, "Added cis: " + cis.getCommunityJid().toString());
						}
						//NOTIFY CALLING CLIENT
						intent.putExtra(INTENT_RETURN_VALUE, returnArray);
					}
					
					//ASK FOR JOIN COMMUNITIES RESULT
					else if (communityResult.getJoinResponse() != null) {
						Log.d(LOG_TAG, "JOIN CIS Response!");
						if(communityResult.getJoinResponse().isResult()){
							Log.d(LOG_TAG, "JOIN response is true");
							Community joinedCIS = communityResult.getJoinResponse().getCommunity(); 
							Parcelable pCis  = ACommunity.convertCommunity(joinedCIS);
							//NOTIFY CALLING CLIENT
							intent.putExtra(INTENT_RETURN_VALUE, pCis); 
						}else{
							Log.d(LOG_TAG, "JOIN response is false");
							intent.putExtra(INTENT_RETURN_BOOLEAN, false);
						}
					
						
					}

					
				} 
				// --------- CIS SUBSCRIBED BEAN---------
				else if(msgBean instanceof CommunityMethods) {
					Log.d(LOG_TAG, "CommunityMethods Result!");
					CommunityMethods communityResponse = (CommunityMethods)msgBean;
					
					//JOIN COMMUNITY RESULT
					if (communityResponse.getJoinResponse() != null) {
						//CONVERT TO PARCEL BEAN
						Parcelable joined = AJoinResponse.convertJoinResponse(communityResponse.getJoinResponse());
						//NOTIFY CALLING CLIENT
						intent.putExtra(INTENT_RETURN_VALUE, joined);
					}
					
					//GET MEMBERS RESULT
					else if (communityResponse.getWho() != null) {
						List<Participant> listReturned = communityResponse.getWho().getParticipant();
						//CONVERT TO PARCEL BEANS
						Parcelable returnArray[] = new Parcelable[listReturned.size()];
						for (int i=0; i<listReturned.size(); i++) {
							AParticipant member = AParticipant.convertParticipant(listReturned.get(i)); 
							returnArray[i] = member;
							Log.d(LOG_TAG, "member: " + member.getJid());
						}
						//NOTIFY CALLING CLIENT
						intent.putExtra(INTENT_RETURN_VALUE, returnArray);
					}
				}
				
				// --------- ACTIVITY FEED BEAN---------
				else if(msgBean instanceof Activityfeed) {
					Log.d(LOG_TAG, "Activity Feed Result!");
					Activityfeed response = (Activityfeed)msgBean;
					
					//GET ACTIVITIES RESULT
					if (response.getGetActivitiesResponse() != null) {
						List<Activity> listReturned = response.getGetActivitiesResponse().getActivity();
						//CONVERT TO PARCEL BEANS
						Parcelable returnArray[] = new Parcelable[listReturned.size()];
						for (int i=0; i<listReturned.size(); i++) {
							AActivity activity = AActivity.convertActivity(listReturned.get(i)); 
							returnArray[i] = activity;
							Log.d(LOG_TAG, "publish: " + activity.getPublished());
						}
						//NOTIFY CALLING CLIENT
						intent.putExtra(INTENT_RETURN_VALUE, returnArray);
					}
					
					//ADD ACTIVITY RESULT
					else if (response.getAddActivityResponse() != null) {
						Boolean published = response.getAddActivityResponse().isResult();
						//NOTIFY CALLING CLIENT
						intent.putExtra(INTENT_RETURN_VALUE, published);
					}
				}
				
				intent.setPackage(client);
				CommunityManagement.this.sendBroadcast(intent);
				CommunityManagement.this.commMgr.unregister(ELEMENT_NAMES, this);
			}
		}
	}//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> END COMMS CALLBACK >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
