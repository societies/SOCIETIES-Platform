/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.personalisation.CommunityPreferenceManagement.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.personalisation.CommunityPreferenceManagement.impl.comms.CommunityPreferenceManagementClient;
import org.societies.personalisation.CommunityPreferenceManagement.impl.management.PrivatePreferenceCache;
import org.societies.personalisation.CommunityPreferenceManagement.impl.merging.PreferenceMerger;
import org.societies.personalisation.preference.api.CommunityPreferenceManagement.ICommunityPreferenceManager;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceTreeModel;
import org.societies.personalisation.preference.api.model.util.PreferenceUtils;

public class CommunityPreferenceManagement implements ICommunityPreferenceManager{

	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private ICtxBroker ctxBroker;
	private ICommManager commsMgr;
	private ICisManager cisManager;
	private PrivatePreferenceCache prefCache;
	private CommunityPreferenceManagementClient communityPreferenceManagementClient;

	public CommunityPreferenceManagement(){

	}

	public void initialiseCommunityPreferenceManagement(){
		this.prefCache = new PrivatePreferenceCache(ctxBroker, commsMgr, cisManager);
	}
	@Override
	public List<IPreferenceTreeModel> getAllCommunityPreferences(IIdentity cisID) {

		if (logging.isDebugEnabled()){
			this.logging.debug("Request to get all community preferences for cis: "+cisID.getBareJid());
		}
		boolean ownCIS = false;
		List<ICisOwned> listOfOwnedCis = cisManager.getListOfOwnedCis();
		for (ICisOwned ownedCis : listOfOwnedCis){
			if (ownedCis.getCisId().equalsIgnoreCase(cisID.getBareJid())){
				ownCIS = true;
			}
		}

		if (ownCIS){
			ArrayList<IPreferenceTreeModel> models = new ArrayList<IPreferenceTreeModel>();

			List<PreferenceDetails> preferenceDetailsForAllPreferences = this.prefCache.getPreferenceDetailsForAllPreferences(cisID);
			for (PreferenceDetails detail : preferenceDetailsForAllPreferences){
				IPreferenceTreeModel preference = prefCache.getPreference(cisID, detail);
				if (preference!=null){
					models.add(preference);
				}
			}

			if(logging.isDebugEnabled()){
				this.logging.debug("Returning : "+models.size()+" preferences from my community: "+cisID.getBareJid());
			}
			return models;
		}else{
			List<IPreferenceTreeModel> allCommunityPreferences = this.communityPreferenceManagementClient.getAllCommunityPreferences(cisID);
			if (this.logging.isDebugEnabled()){
				this.logging.debug("Returning : "+allCommunityPreferences.size()+" preferences from (not my) community: "+cisID.getBareJid());
			}
			return allCommunityPreferences;
		}

	}

	@Override
	public void uploadUserPreferences(IIdentity cisId, List<IPreferenceTreeModel> models) {
		if (this.logging.isDebugEnabled()){
			this.logging.debug("Uploading : "+models.size()+" preferences to community: "+cisId.getBareJid());
		}
		boolean ownCIS = false;

		List<ICisOwned> listOfOwnedCis = cisManager.getListOfOwnedCis();
		//JOptionPane.showMessageDialog(null, listOfOwnedCis.size());
		for (ICisOwned ownedCis : listOfOwnedCis){
			if (ownedCis.getCisId().equalsIgnoreCase(cisId.getBareJid())){
				ownCIS = true;
				
			}
		}

		if (ownCIS){
			//JOptionPane.showMessageDialog(null, "I own this CIS");
			if (logging.isDebugEnabled()){
				this.logging.debug("Uploading "+models.size()+" community preferences to my community: "+cisId.getBareJid());
			}
			for (IPreferenceTreeModel newModel : models){
				PreferenceDetails newDetail = newModel.getPreferenceDetails();
				IPreferenceTreeModel existingModel = this.prefCache.getPreference(cisId, newDetail);
				if (existingModel==null){
					if (logging.isDebugEnabled()){
						this.logging.debug("There's no previous preference for : "+newDetail.toString()+". Saving as is ");
					}
					this.prefCache.storePreference(cisId, newDetail, newModel);
				}else{
					if (logging.isDebugEnabled()){
						this.logging.debug("Merging individual with community preference");
					}
					PreferenceMerger merger = new PreferenceMerger();
					IPreference mergeTrees = merger.mergeTrees(existingModel.getRootPreference(), newModel.getRootPreference(), "");
					IPreferenceTreeModel mergedModel = new PreferenceTreeModel(newDetail, mergeTrees);
					this.prefCache.storePreference(cisId, newDetail, mergedModel);
				}
			}

		}else{
			//JOptionPane.showMessageDialog(null, "I do not own this CIS");
			if (logging.isDebugEnabled()){
				this.logging.debug("Uploading "+models.size()+" community preferences to someone else's community: "+cisId.getBareJid());
			}
			this.communityPreferenceManagementClient.uploadUserPreferences(cisId, models);
		}
	}

	@Override
	public List<PreferenceDetails> getCommunityPreferenceDetails(IIdentity cisId) {
		if (logging.isDebugEnabled()){
			this.logging.debug("Request to get all community preference details for cis: "+cisId.getBareJid());
		}
		boolean ownCIS = false;
		List<ICisOwned> listOfOwnedCis = cisManager.getListOfOwnedCis();
		for (ICisOwned ownedCis : listOfOwnedCis){
			if (ownedCis.getCisId().equalsIgnoreCase(cisId.getBareJid())){
				ownCIS = true;
			}
		}

		if (ownCIS){
			ArrayList<PreferenceDetails> preferenceDetailsForAllPreferences = this.prefCache.getPreferenceDetailsForAllPreferences(cisId);
			if (this.logging.isDebugEnabled()){
				this.logging.debug("Returning : "+preferenceDetailsForAllPreferences.size()+" preference details from (my) community: "+cisId.getBareJid());
			}
			
			return preferenceDetailsForAllPreferences;
		}else{
			List<PreferenceDetails> communityPreferenceDetails = this.communityPreferenceManagementClient.getCommunityPreferenceDetails(cisId);
			if (this.logging.isDebugEnabled()){
				this.logging.debug("Returning : "+communityPreferenceDetails.size()+" preference details from (not my) community: "+cisId.getBareJid());
			}
			return communityPreferenceDetails;
		}
	}


	@Override
	public List<IPreferenceTreeModel> getCommunityPreferences(IIdentity cisId,
			List<PreferenceDetails> details) {
		if (logging.isDebugEnabled()){
			this.logging.debug("Request to get "+details.size()+" community preferences from cis: "+cisId.getBareJid());
		}
		boolean ownCIS = false;
		List<ICisOwned> listOfOwnedCis = cisManager.getListOfOwnedCis();
		for (ICisOwned ownedCis : listOfOwnedCis){
			if (ownedCis.getCisId().equalsIgnoreCase(cisId.getBareJid())){
				ownCIS = true;
			}
		}

		List<IPreferenceTreeModel> models = new ArrayList<IPreferenceTreeModel>();

		if (ownCIS){
			for (PreferenceDetails detail : details){
				models.add(this.prefCache.getPreference(cisId, detail));
				
			}
			if (this.logging.isDebugEnabled()){
				this.logging.debug("Returning : "+models.size()+" preferences  from (my) community: "+cisId.getBareJid());
			}
			return models;
		}else{
			
			List<IPreferenceTreeModel> communityPreferences = this.communityPreferenceManagementClient.getCommunityPreferences(cisId, details);
			if (this.logging.isDebugEnabled()){
				this.logging.debug("Returning : "+communityPreferences.size()+" preferences  from (my) community: "+cisId.getBareJid());
			}
			return communityPreferences;
		}
	}

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	public ICommManager getCommsMgr() {
		return commsMgr;
	}

	public void setCommsMgr(ICommManager commsMgr) {
		this.commsMgr = commsMgr;
	}

	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	public CommunityPreferenceManagementClient getCommunityPreferenceManagementClient() {
		return communityPreferenceManagementClient;
	}

	public void setCommunityPreferenceManagementClient(
			CommunityPreferenceManagementClient communityPreferenceManagementClient) {
		this.communityPreferenceManagementClient = communityPreferenceManagementClient;
	}



}
