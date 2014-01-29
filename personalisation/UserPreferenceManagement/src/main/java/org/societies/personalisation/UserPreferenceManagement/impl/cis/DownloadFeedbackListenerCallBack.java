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
package org.societies.personalisation.UserPreferenceManagement.impl.cis;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.internal.useragent.feedback.IUserFeedbackResponseEventListener;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.ServiceUtils;
import org.societies.personalisation.UserPreferenceManagement.impl.UserPreferenceManagement;
import org.societies.personalisation.UserPreferenceManagement.impl.merging.PreferenceMerger;
import org.societies.personalisation.UserPreferenceManagement.impl.monitoring.PersonalisationConstants;
import org.societies.personalisation.UserPreferenceManagement.impl.monitoring.UserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;
import org.societies.personalisation.preference.api.model.util.PreferenceUtils;

/**
 * @author Eliza
 *
 */
public class DownloadFeedbackListenerCallBack implements IUserFeedbackResponseEventListener	 {

	private final Hashtable<String, PreferenceDetails> detailsTable;
	private final UserPreferenceConditionMonitor pcm;
	private IIdentityManager idm;
	private String type;
	private IIdentity cisID;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	public enum FeedbackType {DOWNLOAD,UPLOAD};
	private IIdentity userID;

	private boolean responseReceived = false;

	public DownloadFeedbackListenerCallBack(IIdentity cisID, Hashtable<String, PreferenceDetails> detailsTable, UserPreferenceConditionMonitor pcm, FeedbackType type){
		this.cisID = cisID;
		this.detailsTable = detailsTable;
		this.pcm = pcm;
		this.idm = pcm.getCommManager().getIdManager();
		this.userID = idm.getThisNetworkNode();
		if (type.equals(FeedbackType.DOWNLOAD)){
			this.type = PersonalisationConstants.DOWNLOAD;
		}else{
			this.type = PersonalisationConstants.UPLOAD;
		}

	}

	private boolean changeToClientServiceResourceIdentifier(IPreferenceTreeModel model) {
		logging.debug("Changing PreferenceDetails SRI To Server");

		ServiceResourceIdentifier serverID = model.getPreferenceDetails().getServiceID();
		ServiceResourceIdentifier clientID = null;
		List<Service> services;
		try {
			services = pcm.getServiceDiscovery().getLocalServices().get();

			for(Service service : services) {
				ServiceResourceIdentifier potentialServerID = pcm.getServiceMgmt().getServerServiceIdentifier(service.getServiceIdentifier());
				if(ServiceUtils.compare(serverID, potentialServerID)) {
					logging.debug("Found local service: " + service.getServiceIdentifier());
					clientID = service.getServiceIdentifier();
					break;
				} else if (serverID.getServiceInstanceIdentifier().equalsIgnoreCase(potentialServerID.getServiceInstanceIdentifier())) {
					clientID = service.getServiceIdentifier();
					break;
				}

			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceDiscoveryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(clientID==null) {
			pcm.getUserFeedbackMgr().showNotification("Your preferences could not be downloaded as you do not have the service installed!");
			return false;
		}
			model.getPreferenceDetails().setServiceID(clientID);
		IPreference rootPreference = model.getRootPreference();

		Enumeration<IPreference> breadthFirstEnumeration = rootPreference.breadthFirstEnumeration();
		while (breadthFirstEnumeration.hasMoreElements()){
			IPreference node = breadthFirstEnumeration.nextElement();
			if (node.getUserObject() != null){
				if (node.getUserObject() instanceof PreferenceOutcome){
					PreferenceOutcome outcome = (PreferenceOutcome) node.getUserObject();
					outcome.setServiceID(clientID);

				}
			}
		}
		return true;

	}


	@Override
	public void responseReceived(Object obj) {
		List<String> responses = (List<String>) obj;

		List<PreferenceDetails> detailsToUploadOrDownload = new ArrayList<PreferenceDetails>();

		if (responses!=null){ //responses contain all the preferences for which the user indicated he wants to upload/download automatically

			for (String response : responses){
				if (this.detailsTable.containsKey(response)){ //detailsTable contain all the preferences we asked about in the UF notification
					UserPreferenceManagement preferenceManager = this.pcm.getPreferenceManager();
					PreferenceDetails preferenceDetails = detailsTable.get(response);

					detailsToUploadOrDownload.add(preferenceDetails);
					try {
						/*
						 * section: storing preference that indicates whether the preferences(preferenceDetails) should be uploaded/downloaded or not. 
						 */
						PreferenceDetails communityPreferenceManagerDetails = PreferenceUtils.getCommunityPreferenceManagerDetails(idm, preferenceDetails.getServiceID(), type);
						IPreferenceOutcome preferenceOutcome = new PreferenceOutcome(communityPreferenceManagerDetails.getServiceID(), 
								communityPreferenceManagerDetails.getServiceType(), communityPreferenceManagerDetails.getPreferenceName(), PersonalisationConstants.YES, false, false, false);
						PreferenceTreeNode treeNode = new PreferenceTreeNode(preferenceOutcome);
						this.pcm.getPreferenceManager().storePreference(idm.getThisNetworkNode(), communityPreferenceManagerDetails, treeNode);
						/*
						 * end section
						 */


						this.detailsTable.remove(response);

					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		if (type.equals(PersonalisationConstants.DOWNLOAD)){
			//if we are downloading preferences, first we download the models from the CommunityPreferenceManager
			List<IPreferenceTreeModel> communityPreferences = pcm.getCommunityPreferenceMgr().getCommunityPreferences(cisID, detailsToUploadOrDownload);


			for (IPreferenceTreeModel communityModel : communityPreferences)
			{//then we replace the server serviceID with the client serviceID
				//	ServiceResourceIdentifier serviceClientID = getClientServiceID(communityModel.getPreferenceDetails().getServiceID());
				//if (serviceClientID!= null){
				//	logging.debug("I am searching for: " + serviceClientID);



				//	this.changeServiceResourceIdentifier(serviceClientID, communityModel);
				PreferenceDetails preferenceDetails = communityModel.getPreferenceDetails();
				//preferenceDetails.setServiceID(serviceClientID);
				IPreferenceTreeModel model = pcm.getPreferenceManager().getModel(null, preferenceDetails);

				if(changeToClientServiceResourceIdentifier(communityModel)) {
					logging.debug("Model: " + communityModel.getPreferenceDetails());

					if (model==null){
						//then we store the preference
						pcm.getPreferenceManager().storePreference(userID, preferenceDetails, communityModel.getRootPreference());
						pcm.processPreferenceChanged(userID, preferenceDetails.getServiceID(), preferenceDetails.getServiceType(), preferenceDetails.getPreferenceName());
					}else{
						//then we merge with existing preferences
						PreferenceMerger merger = new PreferenceMerger(pcm.getUserFeedbackMgr());
						PreMerger preMerger = new PreMerger(pcm.getCtxBroker(), userID);
						IPreference replaceCtxIdentifiers = preMerger.replaceCtxIdentifiers(communityModel.getRootPreference());
						if (replaceCtxIdentifiers!=null){
							IPreference mergeTrees = merger.mergeTrees(model.getRootPreference(), replaceCtxIdentifiers, "");
							pcm.getPreferenceManager().storePreference(userID, preferenceDetails, mergeTrees);
							pcm.processPreferenceChanged(userID, preferenceDetails.getServiceID(), preferenceDetails.getServiceType(), preferenceDetails.getPreferenceName());
						}
					}
				}
				//	else {
				//		logging.debug("Could not find client service ID of service:" + ServiceModelUtils.serviceResourceIdentifierToString(communityModel.getPreferenceDetails().getServiceID()));
				//	}
				//}
			}



		}
		else if (type.equals(PersonalisationConstants.UPLOAD)){
			logging.debug("In the upload branch!");

			List<IPreferenceTreeModel> modelsToUpload = new ArrayList<IPreferenceTreeModel>();
			//if we are uploading preferences, first we retrieve the local preferences using the client serviceID, 
			for (PreferenceDetails d : detailsToUploadOrDownload){
				PreferenceDetails localDetail = new PreferenceDetails();
				localDetail.setPreferenceName(d.getPreferenceName());
				localDetail.setServiceID(d.getServiceID());
				localDetail.setServiceType(d.getServiceType());

				logging.debug("Getting the tree model");
				IPreferenceTreeModel model = this.pcm.getPreferenceManager().getModel(userID, localDetail);
				if(model.getRootPreference().getOutcome()==null) {
					logging.debug("out come is null after UF!");
				}
				//then we replace the client service ID with server service ID 
				if (model!=null){
					logging.debug("THe model has server: " + model.getPreferenceDetails().getServiceID().getServiceInstanceIdentifier());
					//		this.changeServiceResourceIdentifier(changeToServerID(d.getServiceID()), model);
					modelsToUpload.add(model);
				}
			}

			//then we upload
			if (modelsToUpload.size()>0){
				logging.debug("Calling ComPrefMgr to upload models to CIS with ID: " + cisID.getBareJid());
				this.pcm.getCommunityPreferenceMgr().uploadUserPreferences(cisID, modelsToUpload);
			}
		}

		/*
		 * section: create preference that indicates the preferences in detailsTable should NOT be downloaded/uploaded:
		 */
		Enumeration<String> keys = this.detailsTable.keys();

		while(keys.hasMoreElements()){
			try {
				logging.debug("in this loop");
				String nextElement = keys.nextElement();
				PreferenceDetails preferenceDetails = this.detailsTable.get(nextElement);
				PreferenceDetails communityPreferenceManagerDetails = PreferenceUtils.getCommunityPreferenceManagerDetails(idm, preferenceDetails.getServiceID(), type);
				IPreferenceOutcome preferenceOutcome = new PreferenceOutcome(communityPreferenceManagerDetails.getServiceID(), 
						communityPreferenceManagerDetails.getServiceType(), communityPreferenceManagerDetails.getPreferenceName(), PersonalisationConstants.NO, false, false, false);
				PreferenceTreeNode treeNode = new PreferenceTreeNode(preferenceOutcome);
				this.pcm.getPreferenceManager().storePreference(idm.getThisNetworkNode(), communityPreferenceManagerDetails, treeNode);

			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


		this.responseReceived = true;
	}

	/*	private ServiceResourceIdentifier changeToServerID(ServiceResourceIdentifier clientID) {
		ServiceResourceIdentifier serverServiceID = this.pcm.getServiceMgmt().getServerServiceIdentifier(clientID);
		if(serverServiceID!=null) {
			return serverServiceID;
		}
		return clientID;
	}

/*	private void changeServiceResourceIdentifier(ServiceResourceIdentifier serviceID, IPreferenceTreeModel model) {

		model.getPreferenceDetails().setServiceID(serviceID);
		IPreference rootPreference = model.getRootPreference();

		Enumeration<IPreference> breadthFirstEnumeration = rootPreference.breadthFirstEnumeration();
		while (breadthFirstEnumeration.hasMoreElements()){
			IPreference node = breadthFirstEnumeration.nextElement();
			if (node.getUserObject() != null){
				if (node.getUserObject() instanceof PreferenceOutcome){
					PreferenceOutcome outcome = (PreferenceOutcome) node.getUserObject();
					outcome.setServiceID(serviceID);

				}
			}
		} 

	}*/


	//**NEW**//
	/*public ServiceResourceIdentifier getClientServiceID(ServiceResourceIdentifier serviceServerID) {
		List<Service> services = new ArrayList<Service>();
		try {
			services = pcm.getServiceDiscovery().getLocalServices().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ServiceDiscoveryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		for(Service service : services) {
			if(ServiceUtils.compare(serviceServerID, pcm.getServiceMgmt().getServerServiceIdentifier(service.getServiceIdentifier()))){
				return service.getServiceIdentifier();
			}
		}
		return null;
	}*/
	public boolean isResponseReceived() {
		return responseReceived;
	}
	public void setResponseReceived(boolean responseReceived) {
		this.responseReceived = responseReceived;
	}
	public Hashtable<String, PreferenceDetails> getDetailsTable() {
		return detailsTable;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
