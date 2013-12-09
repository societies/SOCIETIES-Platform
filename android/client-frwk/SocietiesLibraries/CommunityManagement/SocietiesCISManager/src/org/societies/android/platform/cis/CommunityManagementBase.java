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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.cis.management.ICisManager;
import org.societies.android.api.cis.management.ICisSubscribed;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.android.api.privacytrust.privacy.util.privacypolicy.PrivacyPolicyUtils;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.activityfeed.AddActivity;
import org.societies.api.schema.activityfeed.CleanUpActivityFeed;
import org.societies.api.schema.activityfeed.CleanUpActivityFeedResponse;
import org.societies.api.schema.activityfeed.DeleteActivity;
import org.societies.api.schema.activityfeed.GetActivities;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.DeleteMember;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.cis.manager.AskCisManagerForJoin;
import org.societies.api.schema.cis.manager.AskCisManagerForLeave;
import org.societies.api.schema.cis.manager.CommunityManager;
import org.societies.api.schema.cis.manager.Create;
import org.societies.api.schema.cis.manager.ListCrit;
import org.societies.api.schema.cis.manager.ListResponse;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyBehaviourConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

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
	private static final List<String> ELEMENT_NAMES = Arrays.asList("communityManager", "communityMethods", "marshaledActivityFeed", "listResponse");
    private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/schema/cis/manager",
														    	  "http://societies.org/api/schema/activityfeed",
																  "http://societies.org/api/schema/cis/community");
    private static final List<String> PACKAGES = Arrays.asList("org.societies.api.schema.cis.manager",
													    	   "org.societies.api.schema.activityfeed",
															   "org.societies.api.schema.cis.community");
    private ClientCommunicationMgr commMgr;
    private Context androidContext;
    private boolean connectedToComms = false;
    private boolean restrictBroadcast;
    
    /**DEFAULT CONSTRUCTOR*/
    public CommunityManagementBase(Context androidContext) {
    	this(androidContext, true);
    }
    
    /**Parameterised CONSTRUCTOR */
    public CommunityManagementBase(Context androidContext, boolean restrictBroadcast) {
    	Log.d(LOG_TAG, "CommunityManagementBase created");
    	
    	this.androidContext = androidContext;
    	this.restrictBroadcast = restrictBroadcast;
		try {
			//INSTANTIATE COMMS MANAGER
			this.commMgr = new ClientCommunicationMgr(androidContext, true);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
        }
    }

    public boolean startService() {
    	if (!connectedToComms) {
        	//NOT CONNECTED TO COMMS SERVICE YET
        	Log.d(LOG_TAG, "CommunityManagementBase startService binding to comms");
	        this.commMgr.bindCommsService(new IMethodCallback() {	
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Connected to comms: " + resultFlag);
					if (resultFlag) {
						connectedToComms = true;
						//REGISTER NAMESPACES
			        	commMgr.register(ELEMENT_NAMES, NAME_SPACES, PACKAGES, new IMethodCallback() {
							@Override
							public void returnAction(boolean resultFlag) {
								Log.d(LOG_TAG, "Namespaces registered: " + resultFlag);
								//SEND INTENT WITH SERVICE STARTED STATUS
				        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
				        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, resultFlag);
				        		CommunityManagementBase.this.androidContext.sendBroadcast(intent);
							}
							@Override
							public void returnAction(String result) { }
							@Override
							public void returnException(String result) {
								// TODO Auto-generated method stub
							}

						});
					} else {
						Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
			    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false);
			    		CommunityManagementBase.this.androidContext.sendBroadcast(intent);
					}
				}	
				@Override
				public void returnAction(String result) { }
				@Override
				public void returnException(String result) {
					// TODO Auto-generated method stub
				}

			});
        }
    	else {
    		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
    		androidContext.sendBroadcast(intent);
    	}
		return true;
    }
    
    public boolean stopService() {
    	if (connectedToComms) {
        	//UNREGISTER AND DISCONNECT FROM COMMS
        	Log.d(LOG_TAG, "CommunityManagementBase stopService unregistering namespaces");
        	commMgr.unregister(ELEMENT_NAMES, NAME_SPACES, new IMethodCallback() {
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Unregistered namespaces: " + resultFlag);
					connectedToComms = false;
					
					commMgr.unbindCommsService();
					//SEND INTENT WITH SERVICE STOPPED STATUS
	        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
	        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
	        		CommunityManagementBase.this.androidContext.sendBroadcast(intent);
				}	
				@Override
				public void returnAction(String result) { }
				@Override
				public void returnException(String result) {
					// TODO Auto-generated method stub
				}

			});
        }
    	else {
    		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
    		androidContext.sendBroadcast(intent);
    	}
    	return true;
    }
    
    /**
	 * @param client
	 */
	private void broadcastServiceNotStarted(String client, String method) {
		if (client != null) {
			Intent intent = new Intent(method);
			intent.putExtra(IServiceManager.INTENT_NOTSTARTED_EXCEPTION, true);
			intent.setPackage(client);
			androidContext.sendBroadcast(intent);
		}
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ICisManager >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/* @see org.societies.android.api.cis.management.ICisManager#createCis(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Hashtable, java.lang.String)*/
	public Community createCis(final String client, final String cisName, final String cisType, final String description, final MembershipCrit rules, final String privacyPolicy) {
		Log.d(LOG_TAG, "createCis called by client: " + client);
		/*
		String privacyPolicyXml = "<RequestPolicy />";
		PrivacyPolicyBehaviourConstants policyType = PrivacyPolicyBehaviourConstants.MEMBERS_ONLY; //DEFAULT
		try {
			policyType = PrivacyPolicyBehaviourConstants.fromValue(privacyPolicy);
		}
		catch (IllegalArgumentException ex) {}//IGNORE - DEFAULT TO MEMBERS_ONLY
		
		try {			
			RequestPolicy policyObj = PrivacyPolicyUtils.inferCisPrivacyPolicy(policyType, rules);
			privacyPolicyXml =  PrivacyPolicyUtils.toXmlString(policyObj);
		} catch (PrivacyException pEx) {
			pEx.printStackTrace();
		}
		*/
		if (connectedToComms) {
	        //COMMUNITY INFO
			Community cisinfo = new Community();
			cisinfo.setCommunityName(cisName);
			cisinfo.setDescription(description);
			cisinfo.setCommunityType(cisType);
			cisinfo.setMembershipCrit(rules);
			//ADD TO BEAN
			Create create = new Create();
			create.setCommunity(cisinfo);
			create.setPrivacyPolicy(privacyPolicy); //privacyPolicyXml);
			//CREATE MESSAGE BEAN
			CommunityManager messageBean = new CommunityManager();
			messageBean.setCreate(create);
	
			//COMMS STUFF
			try {
				ICommCallback cisCallback = new CommunityCallback(client, CREATE_CIS); 
				IIdentity toID = commMgr.getIdManager().getCloudNode();
				Log.e(LOG_TAG, ">>>>>>>>>>>>>>Cloud Node: " + toID.getJid());
				Stanza stanza = new Stanza(toID);
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (Exception e) {
				Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
	        }
		} else {
        	//NOT CONNECTED TO COMMS SERVICE
        	broadcastServiceNotStarted(client, CREATE_CIS);
        }
		return null;
	}
		
	/* @see org.societies.android.api.cis.management.ICisManager#deleteCis(java.lang.String, java.lang.String)*/
	public Boolean deleteCis(final String client, final String cisId) {
		Log.d(LOG_TAG, "deleteCis called by client: " + client);
		
		if (connectedToComms) {
			//COMMUNITY INFO
			org.societies.api.schema.cis.manager.Delete cisDel = new org.societies.api.schema.cis.manager.Delete();
			cisDel.setCommunityJid(cisId);
			//CREATE MESSAGE BEAN
			CommunityManager messageBean = new CommunityManager();
			messageBean.setDelete(cisDel);
			
			//COMMS STUFF
			try {
				ICommCallback cisCallback = new CommunityCallback(client, DELETE_CIS); 
				IIdentity toID = commMgr.getIdManager().getCloudNode();
				Log.e(LOG_TAG, ">>>>>>>>>>>>>>Cloud Node: " + toID.getJid());
				Stanza stanza = new Stanza(toID);
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
		} else {
        	//NOT CONNECTED TO COMMS SERVICE
        	broadcastServiceNotStarted(client, DELETE_CIS);
        }
		return null;
	}
	
	/* @see org.societies.android.api.cis.management.ICisManager#getCisList(java.lang.String, org.societies.api.schema.cis.manager.ListCrit)*/
	public Community[] getCisList(final String client, final String query) {
		Log.d(LOG_TAG, "createCis called by client: " + client);

		if (connectedToComms) {
        	//COMMUNITY INFO
			org.societies.api.schema.cis.manager.List list = new org.societies.api.schema.cis.manager.List();
			list.setListCriteria(ListCrit.fromValue(query));
			//CREATE MESSAGE BEAN
			CommunityManager messageBean = new CommunityManager();
			messageBean.setList(list);
	
			//COMMS STUFF
			try {
				ICommCallback cisCallback = new CommunityCallback(client, GET_CIS_LIST); 
				IIdentity toID = commMgr.getIdManager().getCloudNode();
				Log.e(LOG_TAG, ">>>>>>>>>>>>>>Cloud Node: " + toID.getJid());
				Stanza stanza = new Stanza(toID);
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
		} else {
        	//NOT CONNECTED TO COMMS SERVICE
        	broadcastServiceNotStarted(client, GET_CIS_LIST);
        }
		return null;
	}
	
	/* @see org.societies.android.api.cis.management.ICisManager#removeMember(java.lang.String, java.lang.String, java.lang.String)*/
	public void removeMember(final String client, final String cisId, final String memberJid) {
		Log.d(LOG_TAG, "removeMember called by client: " + client);
		
		if (connectedToComms) {
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
				Stanza stanza = new Stanza(toID);
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
		} else {
        	//NOT CONNECTED TO COMMS SERVICE
        	broadcastServiceNotStarted(client, REMOVE_MEMBER);
        }
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ICisSubscribed >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/* @see org.societies.android.api.cis.management.ICisManager#Join(java.lang.String, java.lang.String, java.util.List)*/
	public String Join(final String client, final CisAdvertisementRecord targetCis) {
		Log.d(LOG_TAG, "Join CIS called by client: " + client);

		if (connectedToComms) {
        	//CREATE JOIN INFO
			AskCisManagerForJoin join = new AskCisManagerForJoin();
			join.setCisAdv(targetCis);
			//CREATE MESSAGE BEAN
			CommunityManager messageBean = new CommunityManager();
			messageBean.setAskCisManagerForJoin(join);
			//COMMS STUFF
			try {
				ICommCallback cisCallback = new CommunityCallback(client, JOIN_CIS); 
				IIdentity toID = commMgr.getIdManager().getCloudNode();	
				Stanza stanza = new Stanza(toID);
				commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
	    } else {
	    	//NOT CONNECTED TO COMMS SERVICE
	    	broadcastServiceNotStarted(client, JOIN_CIS);
	    }
		return null;
	}
	
	/* @see org.societies.android.api.cis.management.ICisManager#Leave(java.lang.String, java.lang.String)*/
	public String Leave(final String client, final String cisId) {
		Log.d(LOG_TAG, "Leave CIS called by client: " + client);

		if (connectedToComms) {
			//CREATE Leave INFO
			AskCisManagerForLeave leave = new AskCisManagerForLeave(); 
			leave.setTargetCisJid(cisId);
			//CREATE MESSAGE BEAN
			CommunityManager messageBean = new CommunityManager();
			messageBean.setAskCisManagerForLeave(leave);
			//COMMS STUFF
			try {
				ICommCallback cisCallback = new CommunityCallback(client, LEAVE_CIS); 
				IIdentity toID = commMgr.getIdManager().getCloudNode();
				Stanza stanza = new Stanza(toID);
		        
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
	    } else {
	    	//NOT CONNECTED TO COMMS SERVICE
	    	broadcastServiceNotStarted(client, LEAVE_CIS);
	    }
		return null;
	}
	
	/* @see org.societies.android.api.cis.management.ICisSubscribed#addActivity(java.lang.String, org.societies.api.schema.activityfeed.AddActivity)*/
	public Boolean addActivity(final String client, final String cisId, final MarshaledActivity activity) {
		Log.d(LOG_TAG, "addActivity called by client: " + client);

		if (connectedToComms) {
			//GETFEED OBJECT
			String sActor = "unknown";
			try {
				sActor = commMgr.getIdManager().getCloudNode().getJid();
			} catch (InvalidFormatException e1) {
				Log.e(LOG_TAG, "ERROR querying cloud node");
			}
			activity.setActor(sActor);
			AddActivity addAct = new AddActivity();
			addAct.setMarshaledActivity(activity);
			//CREATE MESSAGE BEAN
			MarshaledActivityFeed messageBean = new MarshaledActivityFeed();
			messageBean.setAddActivity(addAct);
	
			//COMMS STUFF
			ICommCallback cisCallback = new CommunityCallback(client, ADD_ACTIVITY); 
			IIdentity toID = null;
			try { 
				toID = commMgr.getIdManager().fromJid(cisId);
				Stanza stanza = new Stanza(toID);
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
        } else {
        	//NOT CONNECTED TO COMMS SERVICE
        	broadcastServiceNotStarted(client, ADD_ACTIVITY);
        }
		return null;		
	}
	
	/* @see org.societies.android.api.cis.management.ICisSubscribed#cleanActivityFeed(java.lang.String)*/
	public CleanUpActivityFeedResponse cleanActivityFeed(final String client, final String cisId) {
		Log.d(LOG_TAG, "cleanActivityFeed called by client: " + client);

		if (connectedToComms) {
			//GETFEED OBJECT
			CleanUpActivityFeed feed = new CleanUpActivityFeed();
			//CREATE MESSAGE BEAN
			MarshaledActivityFeed messageBean = new MarshaledActivityFeed();
			messageBean.setCleanUpActivityFeed(feed);
	
			//COMMS STUFF
			ICommCallback cisCallback = new CommunityCallback(client, CLEAN_ACTIVITIES); 
			IIdentity toID = null;
			try { 
				toID = commMgr.getIdManager().fromJid(cisId);
				Stanza stanza = new Stanza(toID);
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
				Log.d(LOG_TAG, "Sending stanza");
			
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
	    } else {
	    	//NOT CONNECTED TO COMMS SERVICE
	    	broadcastServiceNotStarted(client, CLEAN_ACTIVITIES);
	    }
		return null;
	}
	
	/*@see org.societies.android.api.cis.management.ICisSubscribed#deleteActivity(java.lang.String, org.societies.api.schema.activityfeed.DeleteActivity)*/
	public Boolean deleteActivity(final String client, final String cisId, final MarshaledActivity activity) {
		Log.d(LOG_TAG, "deleteActivity called by client: " + client);

		if (connectedToComms) {
			//GETFEED OBJECT
			DeleteActivity getFeed = new DeleteActivity();
			getFeed.setMarshaledActivity(activity);
			//CREATE MESSAGE BEAN
			MarshaledActivityFeed messageBean = new MarshaledActivityFeed();
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
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
	    } else {
	    	//NOT CONNECTED TO COMMS SERVICE
	    	broadcastServiceNotStarted(client, DELETE_ACTIVITY);
	    }
		return null;
	}
	
	/* @see org.societies.android.api.cis.management.ICisSubscribed#getActivityFeed(java.lang.String, java.lang.String)*/
	public MarshaledActivity[] getActivityFeed(final String client, final String cisId) {
		Log.d(LOG_TAG, "getActivityFeed called by client: " + client);

		if (connectedToComms) {
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
			MarshaledActivityFeed messageBean = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
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
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
				Log.d(LOG_TAG, "Sending stanza");
	        } catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
        } else {
        	//NOT CONNECTED TO COMMS SERVICE
        	broadcastServiceNotStarted(client, GET_ACTIVITY_FEED);
        }
		return null;	
	}
	
	/* @see org.societies.android.api.cis.management.ICisSubscribed#getCisInformation(java.lang.String, java.lang.String)*/
	public Community getCisInformation(final String client, final String cisId) {
		Log.d(LOG_TAG, "getCisInformation called by client: " + client);

		if (connectedToComms) {
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
				Stanza stanza = new Stanza(toID);
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
				Log.d(LOG_TAG, "Sending stanza");
	        } catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
	    } else {
	    	//NOT CONNECTED TO COMMS SERVICE
	    	broadcastServiceNotStarted(client, GET_CIS_INFO);
	    }
		return null;		
	}
	
	/* @see org.societies.android.api.cis.management.ICisSubscribed#getMembers(java.lang.String, java.lang.String)*/
	public String[] getMembers(final String client, final String cisId) {
		Log.d(LOG_TAG, "getMembers called by client: " + client);

		if (connectedToComms) {
			//CREATE LIST INFO
			org.societies.api.schema.cis.community.WhoRequest listing = new org.societies.api.schema.cis.community.WhoRequest();
			RequestorBean requestor = new RequestorBean();
			try {
				requestor.setRequestorId(commMgr.getIdManager().getCloudNode().getBareJid());
			} catch (InvalidFormatException e2) {
				e2.printStackTrace();
			}
			listing.setRequestor(requestor);
			//CREATE MESSAGE BEAN
			CommunityMethods messageBean = new CommunityMethods();
			messageBean.setWhoRequest(listing);
	
			//COMMS STUFF
			ICommCallback cisCallback = new CommunityCallback(client, GET_MEMBERS); 
			IIdentity toID = null;
			try { 
				toID = commMgr.getIdManager().fromJid(cisId);
				Stanza stanza = new Stanza(toID);

	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback);
				Log.d(LOG_TAG, "Sending stanza");
	        } catch (InvalidFormatException e1) {
				e1.printStackTrace();
	        } catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
        } else {
        	//NOT CONNECTED TO COMMS SERVICE
        	broadcastServiceNotStarted(client, GET_MEMBERS);
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
					
					// --------- JOIN RESPONSE --------- moved to IQ
					//if (communityMessage.getJoinResponse() != null) {
					//	boolean bJoined = communityMessage.getJoinResponse().isResult();
					//	if (bJoined) {
					//		Parcelable joined = communityMessage.getJoinResponse();
					//		//NOTIFY CALLING CLIENT
					//		intent.putExtra(ICisSubscribed.INTENT_RETURN_VALUE, joined);
					//	}
					//	intent.putExtra(ICisSubscribed.INTENT_RETURN_BOOLEAN, bJoined);
					//}
				}
				intent.setPackage(client);
				CommunityManagementBase.this.androidContext.sendBroadcast(intent);
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
							//NOTIFY CALLING CLIENT
							intent.putExtra(ICisManager.INTENT_RETURN_VALUE, (Parcelable)cis);
							Toast.makeText(androidContext, "CIS created", Toast.LENGTH_SHORT).show();
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
					Community returnArray[] = listReturned.toArray(new Community[listReturned.size()]);
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
						Participant returnArray[] = listReturned.toArray(new Participant[listReturned.size()]);
						//NOTIFY CALLING CLIENT
						intent.putExtra(ICisSubscribed.INTENT_RETURN_VALUE, returnArray);
					}
					//REMOVE MEMBER RESPONSE
					if (communityResponse.getDeleteMemberResponse() !=null) {
						boolean bSuccess = communityResponse.getDeleteMemberResponse().isResult();
						//NOTIFY CALLING CLIENT
						intent.putExtra(ICisSubscribed.INTENT_RETURN_VALUE, bSuccess);
					}
					//JOIN RESPONSE 
					if (communityResponse.getJoinResponse() != null) {
						boolean bJoined = communityResponse.getJoinResponse().isResult();
						if (bJoined) {
							Parcelable joined = communityResponse.getJoinResponse();
							//NOTIFY CALLING CLIENT
							intent.putExtra(ICisSubscribed.INTENT_RETURN_VALUE, joined);
						}
						intent.putExtra(ICisSubscribed.INTENT_RETURN_BOOLEAN, bJoined);
					}
				}
				
				// --------- ACTIVITY FEED BEAN---------
				else if(msgBean instanceof MarshaledActivityFeed) {
					Log.d(LOG_TAG, "Activity Feed Result!");
                    MarshaledActivityFeed response = (MarshaledActivityFeed)msgBean;
					
					//GET ACTIVITIES RESULT
					if (response.getGetActivitiesResponse() != null) {
						List<MarshaledActivity> listReturned = response.getGetActivitiesResponse().getMarshaledActivity();
						MarshaledActivity returnArray[] = listReturned.toArray(new MarshaledActivity[listReturned.size()]);
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
				if(restrictBroadcast)
					intent.setPackage(client);
				CommunityManagementBase.this.androidContext.sendBroadcast(intent);
			}
		}
	}//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> END COMMS CALLBACK >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
