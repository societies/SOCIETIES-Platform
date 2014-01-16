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
	@Override
	public void responseReceived(Object obj) {
		List<String> responses = (List<String>) obj;



		if (responses!=null){
			List<PreferenceDetails> detailsToDownload = new ArrayList<PreferenceDetails>();
			for (String response : responses){
				if (this.detailsTable.containsKey(response)){
					UserPreferenceManagement preferenceManager = this.pcm.getPreferenceManager();
					PreferenceDetails preferenceDetails = detailsTable.get(response);
					detailsToDownload.add(preferenceDetails);
					try {
						PreferenceDetails communityPreferenceManagerDetails = PreferenceUtils.getCommunityPreferenceManagerDetails(idm, preferenceDetails.getServiceID(), type);
						IPreferenceOutcome preferenceOutcome = new PreferenceOutcome(communityPreferenceManagerDetails.getServiceID(), 
								communityPreferenceManagerDetails.getServiceType(), communityPreferenceManagerDetails.getPreferenceName(), PersonalisationConstants.YES, false, false, false);
						PreferenceTreeNode treeNode = new PreferenceTreeNode(preferenceOutcome);

						this.pcm.getPreferenceManager().storePreference(idm.getThisNetworkNode(), communityPreferenceManagerDetails, treeNode);
						this.detailsTable.remove(response);


						//retrieve preference from community preference mgr using <preferenceDetails>
						//replace serviceID in retrieved preference with client serviceID
						//merge retrieved preference with existing preference (using mergingManager)


					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			List<IPreferenceTreeModel> communityPreferences = pcm.getCommunityPreferenceMgr().getCommunityPreferences(cisID, detailsToDownload);


			for (IPreferenceTreeModel communityModel : communityPreferences)
			{
				ServiceResourceIdentifier serviceClientID = getClientServiceID(communityModel.getPreferenceDetails().getServiceID());
				if (serviceClientID!= null){
					logging.debug("I am searching for: " + serviceClientID);

					if(null!=serviceClientID) {
						PreferenceDetails preferenceDetails = communityModel.getPreferenceDetails();
						preferenceDetails.setServiceID(serviceClientID);
						IPreferenceTreeModel model = pcm.getPreferenceManager().getModel(null, preferenceDetails);


						if (model==null){
							pcm.getPreferenceManager().storePreference(userID, preferenceDetails, communityModel.getRootPreference());
							pcm.processPreferenceChanged(userID, preferenceDetails.getServiceID(), preferenceDetails.getServiceType(), preferenceDetails.getPreferenceName());
						}else{
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
					else {
						logging.debug("Could not find client service ID of service:" + ServiceModelUtils.serviceResourceIdentifierToString(communityModel.getPreferenceDetails().getServiceID()));
					}
				}

			

		}
	}


	Enumeration<String> keys = this.detailsTable.keys();

	while(keys.hasMoreElements()){		
		try {
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

//**NEW**//
public ServiceResourceIdentifier getClientServiceID(ServiceResourceIdentifier serviceServerID) {
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
}
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
