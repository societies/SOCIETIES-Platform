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


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.feedback.IUserFeedbackResponseEventListener;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceManagement.impl.UserPreferenceManagement;
import org.societies.personalisation.UserPreferenceManagement.impl.merging.PreferenceMerger;
import org.societies.personalisation.UserPreferenceManagement.impl.monitoring.DownloadFeedbackListenerCallBack.FeedbackType;
import org.societies.personalisation.preference.api.CommunityPreferenceManagement.ICommunityPreferenceManager;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceTreeModel;
import org.societies.personalisation.preference.api.model.util.PreferenceUtils;

/**
 * @author Eliza
 *
 */
public class CommunitiesHandler {

	private final ICommunityPreferenceManager communityPrefMgr;
	private final ICisManager cisManager;
	private final ICommManager commsMgr;
	private IIdentityManager idManager;
	private final IServiceDiscovery serviceDiscovery;
	private final UserPreferenceManagement userPrefMgr;
	private Hashtable<String, PreferenceDetails> downloadTempTable;
	private Hashtable<String, PreferenceDetails> uploadTempTable;
	private final IUserFeedback userFeedback;
	private final UserPreferenceConditionMonitor pcm;

	//TODO: add CisManager, serviceDiscovery dependencies in bundle 
	public CommunitiesHandler(UserPreferenceConditionMonitor pcm) {
		//TODO: need to check the timers
		this.pcm = pcm;

		this.communityPrefMgr = pcm.getCommunityPreferenceMgr(); 
		this.cisManager = pcm.getCisManager();
		this.commsMgr = pcm.getCommManager();
		this.serviceDiscovery = pcm.getServiceDiscovery();
		this.userPrefMgr = pcm.getPreferenceManager();
		this.userFeedback = pcm.getUserFeedbackMgr();

		idManager = commsMgr.getIdManager();

		this.downloadTempTable = new Hashtable<String, PreferenceDetails>();
		this.uploadTempTable = new Hashtable<String, PreferenceDetails>();


	}

	public void scheduleTasks(){
		Timer downloadTimer = new Timer();
		Calendar downloaderCalendar = Calendar.getInstance();
		//System.out.println(calendar.getTime().toString());
		downloaderCalendar.set(Calendar.HOUR, 21);
		downloaderCalendar.set(Calendar.MINUTE, 59);
		downloaderCalendar.set(Calendar.SECOND, 59);

		//System.out.println(calendar.getTime().toString());

		TimerTask downloadTimerTask = new DownloaderTask();
		downloadTimer.schedule(downloadTimerTask, downloaderCalendar.getTime());


		Timer uploadTimer = new Timer();

		Calendar uploadCalendar = Calendar.getInstance();

		uploadCalendar.set(Calendar.HOUR, 23);
		uploadCalendar.set(Calendar.MINUTE, 59);
		uploadCalendar.set(Calendar.SECOND, 59);


		TimerTask uploadTimerTask = new UploaderTask();
		uploadTimer.schedule(uploadTimerTask, uploadCalendar.getTime());
	}
	private class UploaderTask extends TimerTask{

		@Override
		public void run() {
			IIdentity userId = idManager.getThisNetworkNode();
			List<ICis> cisList = cisManager.getCisList();
			for (ICis cis: cisList){
				try {

					IIdentity cisId = idManager.fromJid(cis.getCisId());
					List<Service> services = serviceDiscovery.getServices(cisId).get();

					List<PreferenceDetails> matchingDetails = findRelevantPreferences(services, userPrefMgr.getPreferenceDetailsForAllPreferences());
					List<PreferenceDetails> allowedToUpload = new ArrayList<PreferenceDetails>();
					List<PreferenceDetails> toBeChecked = new ArrayList<PreferenceDetails>();
					for (PreferenceDetails prefDetail : matchingDetails){

						PreferenceDetails communityPreferenceManagerDetails = PreferenceUtils.getCommunityPreferenceManagerDetails(idManager, prefDetail.getServiceID(), PersonalisationConstants.UPLOAD);


						IPreferenceOutcome preference = pcm.getPreferenceManager().getPreference(userId, communityPreferenceManagerDetails.getServiceType(), communityPreferenceManagerDetails.getServiceID(), communityPreferenceManagerDetails.getPreferenceName());
						if (preference!=null){
							if (preference.getvalue().equalsIgnoreCase(PersonalisationConstants.YES)){
								allowedToUpload.add(prefDetail);
							}
						}else{
							toBeChecked.add(prefDetail);
						}
					}


					List<IPreferenceTreeModel> models = new ArrayList<IPreferenceTreeModel>();
					for (PreferenceDetails detail : allowedToUpload){
						IPreferenceTreeModel model = pcm.getPreferenceManager().getModel(userId, detail);
						if (model!=null){
							models.add(model);
						}
					}

					communityPrefMgr.uploadUserPreferences(cisId, models);


					String[] userFriendlyListofDetails = getUserFriendlyListofDetails(toBeChecked, FeedbackType.UPLOAD);

					userFeedback.getExplicitFBAsync(ExpProposalType.CHECKBOXLIST, new ExpProposalContent("Please select which of these preferences you want to upload to the CIS anonymously", userFriendlyListofDetails), new DownloadFeedbackListenerCallBack(uploadTempTable, pcm, FeedbackType.UPLOAD)).get();

				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceDiscoveryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	private class DownloaderTask extends TimerTask{

		@Override
		public void run() {
			List<ICis> cisList = cisManager.getCisList();
			for (ICis cis: cisList){
				try {
					IIdentity cisId = idManager.fromJid(cis.getCisId());
					IIdentity userId = idManager.getThisNetworkNode();
					List<PreferenceDetails> communityPreferenceDetails = communityPrefMgr.getCommunityPreferenceDetails(cisId);
					List<PreferenceDetails> listofPreferencesToDownload = new ArrayList<PreferenceDetails>();

					List<PreferenceDetails> listtoBeChecked = new ArrayList<PreferenceDetails>();
					//first check if there is a preference for downloading these 

					for (PreferenceDetails details : communityPreferenceDetails){

						PreferenceDetails managerDetails = PreferenceUtils.getCommunityPreferenceManagerDetails(idManager, details.getServiceID(), PersonalisationConstants.DOWNLOAD);
						IPreferenceOutcome preference = CommunitiesHandler.this.userPrefMgr.getPreference(idManager.getThisNetworkNode(), managerDetails.getServiceType(), managerDetails.getServiceID(), managerDetails.getPreferenceName());
						if (preference!=null){
							if (preference.getvalue().matches(PersonalisationConstants.YES)){
								listofPreferencesToDownload.add(details);
							}
						}else{
							listtoBeChecked.add(details);
						}
					}

					//download the ones that we know the user wants to download

					List<IPreferenceTreeModel> downloadedCommunityPreferences = communityPrefMgr.getCommunityPreferences(cisId, listofPreferencesToDownload);


					for (IPreferenceTreeModel communityModel : downloadedCommunityPreferences){
						IPreferenceTreeModel model = userPrefMgr.getModel(null, communityModel.getPreferenceDetails());


						if (model==null){
							userPrefMgr.storePreference(userId, communityModel.getPreferenceDetails(), communityModel.getRootPreference());
						}else{
							PreferenceMerger merger = new PreferenceMerger();
							IPreference mergeTrees = merger.mergeTrees(model.getRootPreference(), communityModel.getRootPreference(), "");
							userPrefMgr.storePreference(userId, communityModel.getPreferenceDetails(), mergeTrees);
						}
						pcm.processPreferenceChanged(userId, communityModel.getPreferenceDetails().getServiceID(), communityModel.getPreferenceDetails().getServiceType(), communityModel.getPreferenceDetails().getPreferenceName());
					}


					String[] options = getUserFriendlyListofDetails(listtoBeChecked, FeedbackType.DOWNLOAD);

					IUserFeedbackResponseEventListener<List<String>> feedbackListener = new DownloadFeedbackListenerCallBack(downloadTempTable, pcm, FeedbackType.DOWNLOAD);
					userFeedback.getExplicitFBAsync(ExpProposalType.CHECKBOXLIST, new ExpProposalContent("Please select which community preferences you want to download", options), feedbackListener).get();

				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}

		}

	}

	private String[] getUserFriendlyListofDetails(List<PreferenceDetails> communityPreferenceDetails, FeedbackType type) {

		List<String> options = new ArrayList<String>();
		if (type.equals(FeedbackType.DOWNLOAD)){
			this.downloadTempTable.clear();
		}else{
			this.uploadTempTable.clear();	
		}
		for (PreferenceDetails d: communityPreferenceDetails){
			String key = "ServiceID: "+ServiceModelUtils.serviceResourceIdentifierToString(d.getServiceID())+" - PreferenceName: "+d.getPreferenceName();
			options.add(key);
			if (type.equals(FeedbackType.DOWNLOAD)){
				this.downloadTempTable.put(key, d);
			}else{
				this.uploadTempTable.put(key, d);
			}
		}


		return options.toArray(new String[options.size()]);
	}

	private List<PreferenceDetails> findRelevantPreferences(List<Service> services, List<PreferenceDetails> details){

		IIdentity userId = this.idManager.getThisNetworkNode();
		List<PreferenceDetails> preferences = new ArrayList<PreferenceDetails>();
		for (Service service: services){
			ServiceResourceIdentifier serviceIdentifier = service.getServiceIdentifier();
			for (PreferenceDetails detail : details){
				if (ServiceModelUtils.compare(serviceIdentifier, detail.getServiceID())){
					preferences.add(detail);
				}
			}
		}

		return preferences;
	}

	private List<PreferenceDetails> findRelevantPreferenceDetails(List<Service> services, List<PreferenceDetails> details){
		ArrayList<PreferenceDetails> preferences = new ArrayList<PreferenceDetails>(); 
		IIdentity userId = this.idManager.getThisNetworkNode();
		for (Service service: services){
			ServiceResourceIdentifier serviceIdentifier = service.getServiceIdentifier();
			for (PreferenceDetails detail : details){
				if (ServiceModelUtils.compare(serviceIdentifier, detail.getServiceID())){
					preferences.add(detail);
				}
			}
		}

		return preferences;
	}
}
