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
package org.societies.personalisation.UserPreferenceManagement.impl.monitoring;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.ServiceMgmtEvent;
import org.societies.api.services.ServiceMgmtEventType;
import org.societies.personalisation.UserPreferenceManagement.impl.UserPreferenceManagement;
import org.societies.personalisation.UserPreferenceManagement.impl.merging.PreferenceMerger;
import org.societies.personalisation.preference.api.CommunityPreferenceManagement.ICommunityPreferenceManager;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;

/**
 * @author Eliza
 *
 */
public class CisEventListener extends EventListener{

	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private final IEventMgr evMgr;
	private final ICommManager commManager;
	private final IUserFeedback userFeedbackMgr;
	private final ICommunityPreferenceManager communityPreferenceMgr;
	private final UserPreferenceManagement userPrefMgr;
	
	private Hashtable<String, PreferenceDetails> tempTable;

	private final UserPreferenceConditionMonitor pcm;
	
	public CisEventListener(UserPreferenceConditionMonitor pcm){
		this.evMgr = pcm.getEventMgr();
		this.commManager = pcm.getCommManager();
		userFeedbackMgr = pcm.getUserFeedbackMgr();
		communityPreferenceMgr = pcm.getCommunityPreferenceMgr();
		this.userPrefMgr = pcm.getPreferenceManager();
		this.pcm = pcm;
		this.tempTable = new Hashtable<String, PreferenceDetails>();
		this.evMgr.subscribeInternalEvent(this, new String[]{EventTypes.CIS_SUBS, EventTypes.SERVICE_LIFECYCLE_EVENT, EventTypes.CIS_UNSUBS}, null);
		
	}

	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleInternalEvent(InternalEvent iEvent) {
		if (iEvent.geteventType().equalsIgnoreCase(EventTypes.CIS_SUBS)){
			if (iEvent.geteventInfo()!=null){
				Community community = (Community) iEvent.geteventInfo();
				try {
					IIdentity cisId = this.commManager.getIdManager().fromJid(community.getCommunityJid());
					
					List<PreferenceDetails> communityPreferenceDetails = this.communityPreferenceMgr.getCommunityPreferenceDetails(cisId);
					if (communityPreferenceDetails.size()>0){
						String[] options = this.getUserFriendlyListofDetails(communityPreferenceDetails);
						List<String> list = this.userFeedbackMgr.getExplicitFB(ExpProposalType.CHECKBOXLIST, new ExpProposalContent("Please select which community preferences you want to download", options)).get();
						
						
						List<PreferenceDetails> detailsToDownload = new ArrayList<PreferenceDetails>();
						for (String str: list){
							if (this.tempTable.containsKey(str)){
								this.logging.debug("Downloading community preference: "+str+" from: "+cisId);
								detailsToDownload.add(this.tempTable.get(str));
							}
						}
						
						List<IPreferenceTreeModel> communityPreferences = this.communityPreferenceMgr.getCommunityPreferences(cisId, detailsToDownload);
						
						
						for (IPreferenceTreeModel communityModel : communityPreferences){
							IPreferenceTreeModel model = this.userPrefMgr.getModel(null, communityModel.getPreferenceDetails());
							if (model==null){
								this.userPrefMgr.storePreference(null, communityModel.getPreferenceDetails(), model.getRootPreference());
							}else{
								PreferenceMerger merger = new PreferenceMerger();
								IPreference mergeTrees = merger.mergeTrees(model.getRootPreference(), communityModel.getRootPreference(), "");
								this.userPrefMgr.storePreference(null, communityModel.getPreferenceDetails(), mergeTrees);
							}
							this.pcm.processPreferenceChanged(null, communityModel.getPreferenceDetails().getServiceID(), communityModel.getPreferenceDetails().getServiceType(), communityModel.getPreferenceDetails().getPreferenceName());
						}
					}
					
					
					//TODO: add this CISId into list of CISs that I'm uploading preferences
					
				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}else if (iEvent.geteventType().equalsIgnoreCase(EventTypes.CIS_UNSUBS)){
			
		}else if (iEvent.geteventType().equalsIgnoreCase(ServiceMgmtEventType.SERVICE_SHARED.toString())){
			if (iEvent.geteventInfo()!=null){
				if (iEvent.geteventInfo() instanceof ServiceMgmtEvent){
					ServiceMgmtEvent sEvent = (ServiceMgmtEvent) iEvent.geteventInfo();
					ServiceResourceIdentifier serviceId = sEvent.getServiceId();
					List<PreferenceDetails> relevantDetails = this.getRelevantPreferenceDetails(userPrefMgr.getPreferenceDetailsForAllPreferences(), serviceId);
					List<IPreferenceTreeModel> models = new ArrayList<IPreferenceTreeModel>();
					
					for (PreferenceDetails detail : relevantDetails){
						models.add(this.userPrefMgr.getModel(null, detail));
					}
					
					if (models.size()!=0){
						this.communityPreferenceMgr.uploadUserPreferences(sEvent.getSharedNode(), models);
					}
					
				}
			}
		}else if (iEvent.geteventType().equalsIgnoreCase(ServiceMgmtEventType.SERVICE_UNSHARED.toString())){
			
		}
		
	}

	private String[] getUserFriendlyListofDetails(
			List<PreferenceDetails> communityPreferenceDetails) {
		List<String> options = new ArrayList<String>();
		this.tempTable.clear();
		for (PreferenceDetails d: communityPreferenceDetails){
			String key = "ServiceID: "+ServiceModelUtils.serviceResourceIdentifierToString(d.getServiceID())+" - PreferenceName: "+d.getPreferenceName();
			options.add(key);
			this.tempTable.put(key, d);
		}
		
		return (String[]) options.toArray();
	}
	
	private List<PreferenceDetails> getRelevantPreferenceDetails(List<PreferenceDetails> allDetails, ServiceResourceIdentifier serviceID){
		ArrayList<PreferenceDetails> relevantDetails = new ArrayList<PreferenceDetails>();
		for (PreferenceDetails detail : allDetails){
			if (ServiceModelUtils.compare(serviceID, detail.getServiceID())){
				relevantDetails.add(detail);
			}
		}
		return relevantDetails;
	}

}