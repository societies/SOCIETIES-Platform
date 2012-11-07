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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.cis.directory.ACisAdvertisementRecord;
import org.societies.android.api.cis.management.AActivity;
import org.societies.android.api.cis.management.ACommunity;
import org.societies.android.api.cis.management.ACriteria;
import org.societies.android.api.cis.management.AJoinResponse;
import org.societies.android.api.cis.management.AParticipant;
import org.societies.android.api.cis.management.ICisManager;
import org.societies.android.api.cis.management.ICisSubscribed;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.activity.Activity;
import org.societies.api.schema.activityfeed.Activityfeed;
import org.societies.api.schema.activityfeed.AddActivity;
import org.societies.api.schema.activityfeed.CleanUpActivityFeed;
import org.societies.api.schema.activityfeed.CleanUpActivityFeedResponse;
import org.societies.api.schema.activityfeed.DeleteActivity;
import org.societies.api.schema.activityfeed.GetActivities;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.DeleteMember;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.manager.AskCisManagerForJoin;
import org.societies.api.schema.cis.manager.AskCisManagerForLeave;
import org.societies.api.schema.cis.manager.CommunityManager;
import org.societies.api.schema.cis.manager.Create;
import org.societies.api.schema.cis.manager.ListCrit;
import org.societies.api.schema.cis.manager.ListResponse;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.app.Service;
import android.content.Context;
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
public class CommunityManagementBase implements ICisManager, ICisSubscribed {
	//LOGGING TAG
	private static final String LOG_TAG = CommunityManagementBase.class.getName();
	
	//COMMS REQUIRED VARIABLES
	private static final List<String> ELEMENT_NAMES = Arrays.asList("communityManager", "communityMethods", "activityfeed", "listResponse");
    private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/schema/cis/manager",
														    	  "http://societies.org/api/schema/activityfeed",	  		
																  "http://societies.org/api/schema/cis/community");
    private static final List<String> PACKAGES = Arrays.asList("org.societies.api.schema.cis.manager",
													    	   "org.societies.api.schema.activityfeed",
															   "org.societies.api.schema.cis.community");
    private ClientCommunicationMgr commMgr;
    private Context androidContext;
    
    /**
     * CONSTRUCTOR
     */
    public CommunityManagementBase(Context androidContext) {
    	Log.d(LOG_TAG, "CommunityManagementBase created");
    	
    	this.androidContext = androidContext;
    	
		try {
			//INSTANTIATE COMMS MANAGER
			this.commMgr = new ClientCommunicationMgr(androidContext);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
        }    

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
		cisinfo.setPrivacyPolicy(privacyPolicy);
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

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ICisSubscribed >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/* @see org.societies.android.api.cis.management.ICisSubscribed#Join(java.lang.String, java.lang.String, java.util.List)*/
	public String Join(String client, ACisAdvertisementRecord targetCis) {
		Log.d(LOG_TAG, "Join CIS called by client: " + client);

		//CREATE JOIN INFO
		AskCisManagerForJoin join = new AskCisManagerForJoin();
		join.setCisAdv( ACisAdvertisementRecord.convertACisAdvertRecord(targetCis));
		//CREATE MESSAGE BEAN
		CommunityManager messageBean = new CommunityManager();
		messageBean.setAskCisManagerForJoin(join);
		//COMMS STUFF
		ICommCallback cisCallback = new CommunityCallback(client, JOIN_CIS); 
		IIdentity toID = commMgr.getIdManager().getCloudNode();	
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
	public String Leave(String client, String cisId) {
		Log.d(LOG_TAG, "Leave CIS called by client: " + client);

		//CREATE Leave INFO
		AskCisManagerForLeave leave = new AskCisManagerForLeave(); 
		leave.setTargetCisJid(cisId);
		//CREATE MESSAGE BEAN
		CommunityManager messageBean = new CommunityManager();
		messageBean.setAskCisManagerForLeave(leave);
		//COMMS STUFF
		ICommCallback cisCallback = new CommunityCallback(client, LEAVE_CIS); 
		IIdentity toID = commMgr.getIdManager().getCloudNode();
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
		activity.setActor(commMgr.getIdManager().getCloudNode().getJid());
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
		
		//TODO: HARDCODED RANGE DATE
		String str_date="20100101";
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = null;
		try {
			date = (Date)formatter.parse(str_date);
		} catch (ParseException e2) {
			e2.printStackTrace();
		} 
		long longFrom=date.getTime();

		Date now = new Date();
		long longNow = now.getTime();
		getFeed.setTimePeriod(longFrom + " " + longNow);
		
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
		org.societies.api.schema.cis.community.WhoRequest listing = new org.societies.api.schema.cis.community.WhoRequest();
		//CREATE MESSAGE BEAN
		CommunityMethods messageBean = new CommunityMethods();
		messageBean.setWhoRequest(listing);

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

		public void receiveMessage(Stanza stanza, Object msgBean) {
			Log.d(LOG_TAG, "Callback receiveMessage");	
			
			if (client != null) {
				Intent intent = new Intent(returnIntent);
				
				if (msgBean==null) Log.d(LOG_TAG, ">>>>msgBean is null");
				// --------- COMMUNITY MANAGER Bean ---------
				if (msgBean instanceof CommunityMethods) {
					Log.d(LOG_TAG, "CommunityManager Result!");
					CommunityMethods communityMessage = (CommunityMethods) msgBean;
					
					// --------- JOIN RESPONSE ---------
					if (communityMessage.getJoinResponse() != null) {
						//CONVERT TO PARCEL BEAN
						Parcelable joined = AJoinResponse.convertJoinResponse(communityMessage.getJoinResponse());
						//NOTIFY CALLING CLIENT
						intent.putExtra(ICisSubscribed.INTENT_RETURN_VALUE, joined);
						intent.putExtra(ICisSubscribed.INTENT_RETURN_BOOLEAN, communityMessage.getJoinResponse().isResult());
					}
				}
				intent.setPackage(client);
				CommunityManagementBase.this.androidContext.sendBroadcast(intent);
				CommunityManagementBase.this.commMgr.unregister(ELEMENT_NAMES, this);
			}
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
							intent.putExtra(ICisManager.INTENT_RETURN_VALUE, pCis); 
						}
						intent.putExtra(ICisManager.INTENT_RETURN_BOOLEAN,communityResult.getCreate().isResult());
					}
					else if (communityResult.getListResponse() != null) {
						//TODO: MOVE THE LIST RESPONSE TO HERE FROM BELOW BEAN CHANGED
					}
					//ASK FOR JOIN COMMUNITIES RESULT
					else if (communityResult.getAskCisManagerForJoinResponse() != null) {
						Log.d(LOG_TAG, "Ask CIS JOIN Response = " + communityResult.getAskCisManagerForJoinResponse().getStatus());
						return; //WE DON'T WANT TO BROADCAST THIS EVENT
					}
				} 
				//LIST COMMUNITIES RESULT
				else if(msgBean instanceof ListResponse) {
					Log.d(LOG_TAG, "List CIS Result!");
					ListResponse response = (ListResponse) msgBean;
					List<Community> listReturned = response.getCommunity();
					//CONVERT TO PARCEL BEANS
					Parcelable returnArray[] = new Parcelable[listReturned.size()];
					for (int i=0; i<listReturned.size(); i++) {
						ACommunity cis = ACommunity.convertCommunity(listReturned.get(i)); 
						returnArray[i] = cis;
						Log.d(LOG_TAG, "Added cis: " + cis.getCommunityJid().toString());
					}
					//NOTIFY CALLING CLIENT
					intent.putExtra(ICisManager.INTENT_RETURN_VALUE, returnArray);
				}
				// --------- CIS SUBSCRIBED BEAN---------
				else if(msgBean instanceof CommunityMethods) {
					Log.d(LOG_TAG, "CommunityMethods Result!");
					CommunityMethods communityResponse = (CommunityMethods)msgBean;
					
					//GET MEMBERS RESULT
					if (communityResponse.getWhoResponse() != null) {
						List<Participant> listReturned = communityResponse.getWhoResponse().getParticipant();
						//CONVERT TO PARCEL BEANS
						Parcelable returnArray[] = new Parcelable[listReturned.size()];
						for (int i=0; i<listReturned.size(); i++) {
							AParticipant member = AParticipant.convertParticipant(listReturned.get(i)); 
							returnArray[i] = member;
							Log.d(LOG_TAG, "member: " + member.getJid());
						}
						//NOTIFY CALLING CLIENT
						intent.putExtra(ICisSubscribed.INTENT_RETURN_VALUE, returnArray);
					}
					//REMOVE MEMBER RESPONSE
					if (communityResponse.getDeleteMemberResponse() !=null) {
						boolean bSuccess = communityResponse.getDeleteMemberResponse().isResult();
						//NOTIFY CALLING CLIENT
						intent.putExtra(ICisSubscribed.INTENT_RETURN_VALUE, bSuccess);
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
						intent.putExtra(ICisSubscribed.INTENT_RETURN_VALUE, returnArray);
					}
					
					//ADD ACTIVITY RESULT
					else if (response.getAddActivityResponse() != null) {
						Boolean published = response.getAddActivityResponse().isResult();
						//NOTIFY CALLING CLIENT
						intent.putExtra(ICisSubscribed.INTENT_RETURN_VALUE, published);
					}
					//REMOVE ACTIVITY RESULT
					else if (response.getDeleteActivityResponse() != null) {
						Boolean bDeleted = response.getDeleteActivityResponse().isResult();
						//NOTIFY CALLING CLIENT
						intent.putExtra(ICisSubscribed.INTENT_RETURN_VALUE, bDeleted);
					}
				}
				
				intent.setPackage(client);
				CommunityManagementBase.this.androidContext.sendBroadcast(intent);
				CommunityManagementBase.this.commMgr.unregister(ELEMENT_NAMES, this);
			}
		}
	}//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> END COMMS CALLBACK >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
