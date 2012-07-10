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
package org.societies.personalisation.UserPreferenceManagement.impl.management;

import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceManagement.impl.Tools;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;

/**
 * @author Elizabeth
 *
 */
public class PrivatePreferenceCache {

	/*idToIPreferenceTreeModel: 
	 * CtxIdentifier: the CtxIdentifier of the preference when stored in the Context Mgmt System
	 * IPreferenceTreeModel: the object
	*/
	Hashtable<CtxIdentifier, IPreferenceTreeModel> idToIPreferenceTreeModel;
	/*
	 * mapper:
	 * String: the context type of the preference i.e. <serviceType>:<serviceID>:<preferenceName>
	 * CtxIdentifier: the context identifier of the preference when stored in the Context Mgmt System
	 */
	private Registry registry;
	private Tools tools; 
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private final ICtxBroker broker;
	private PreferenceRetriever retriever;
	
	public PrivatePreferenceCache(ICtxBroker broker){
		
		this.broker = broker;
		this.idToIPreferenceTreeModel = new Hashtable<CtxIdentifier, IPreferenceTreeModel>();
		this.retriever = new PreferenceRetriever(this.broker);
		this.registry = retriever.retrieveRegistry();
		
	}

	private IPreferenceTreeModel getPreference(CtxIdentifier id){
		//if the preference exists in the cache return it
		if (this.idToIPreferenceTreeModel==null){
			this.logging.debug("Hashtable is null. Cache not initalised properly");
			this.idToIPreferenceTreeModel = new Hashtable<CtxIdentifier,IPreferenceTreeModel>();
		}
		IPreferenceTreeModel p = this.idToIPreferenceTreeModel.get(id);
		if (null !=p){
			return p;
		}else{
			//retrieve the preference from context
			IPreferenceTreeModel pref = this.retriever.retrievePreference(id);
			if (null!=pref){
				//if the preference exists in context, put it in the cache and return it 
				this.idToIPreferenceTreeModel.put(id, pref);
				return pref;
			}
		}
		//if the preference doesn't exist at all, return null
		return null;
	}
	public IPreferenceTreeModel getPreference(PreferenceDetails details){
		CtxIdentifier id = this.registry.getCtxID(details);
		if (id==null){
			this.logging.debug("Could not find preference for :\n"+details.toString());
			return null;
		}else{
			this.logging.debug("Found preference in DB. CtxID: "+id.toUriString()+" for: "+details.toString());
			
		}
		return this.getPreference(id);
	}
	public IPreferenceTreeModel getPreference(String serviceType, ServiceResourceIdentifier serviceID, String preferenceName){
		if (serviceType==null){
			this.logging.debug("request to get preference with null serviceType, returning empty model");
			return null;
		}
		if (serviceID == null){
			this.logging.debug("request to get preference with null serviceID, returning empty model");
			return null;
		}
		if (preferenceName==null){
			this.logging.debug("request to get preference with null preferenceName, returning empty model");
			return null;
		}
		return this.getPreference(new PreferenceDetails(serviceType, serviceID, preferenceName));
	}

	
	public boolean storePreference(IIdentity userId, PreferenceDetails details, IPreferenceTreeModel model){
		this.logging.debug("Request to store preference for:"+details.toString());

		
		CtxIdentifier id = this.registry.getCtxID(details);
		if (id==null){
			this.logging.debug("Preference doesn't exist in DB. Attempt  to store new preference");
			//preference doesn't exist. we're going to store new preference in the db
			PreferenceStorer storer = new PreferenceStorer(this.broker);
			CtxIdentifier newCtxIdentifier = storer.storeNewPreference(userId, model, this.registry.getNameForNewPreference());
			if (newCtxIdentifier==null){
				this.logging.debug("Could not store NEW preference in DB. aborting");
				return false;
			}
			this.logging.debug("Successfully stored NEW preference in DB. CtxID: "+newCtxIdentifier.toUriString());
			this.registry.addPreference(details, newCtxIdentifier);
			this.logging.debug("Successfully added preference details to registry: ");
			this.logging.debug("Stored preference for: "+details.toString());
			storer.storeRegistry(userId, registry);
			this.logging.debug("Successfully stored registry in DB");
			this.idToIPreferenceTreeModel.put(newCtxIdentifier, model);
			this.logging.debug("Successfully added preference to cache");
			
		}else{
			this.logging.debug("Preference exists in DB. Attempt  to update existing preference");
			PreferenceStorer storer = new PreferenceStorer(this.broker);
			if (!storer.storeExisting(userId, id, model)){
				return false;
			}
			this.logging.debug("Successfully updated preference in DB. CtxID: "+id.toUriString());
			this.idToIPreferenceTreeModel.put(id, model);
			this.logging.debug("Successfully updated preference cache with new preference");
		}
		return true;
	}

	public void deletePreference(IIdentity dpi, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName){
		PreferenceDetails details = new PreferenceDetails(serviceType, serviceID, preferenceName);
		CtxIdentifier id = this.registry.getCtxID(details);
		if (id==null){
			//preference doesn't exist. can't delete it
			logging.debug("Preference "+preferenceName+" of "+serviceType+":"+serviceID.toString()+"doesn't exist. Aborting deletion");
		}else{
			PreferenceStorer storer = new PreferenceStorer(this.broker);
			storer.deletePreference(dpi, id);
			this.registry.deletePreference(details);
			storer.storeRegistry(dpi, registry);
		}
	}
	
	public boolean deletePreference(IIdentity dpi, PreferenceDetails details){
		CtxIdentifier id = this.registry.getCtxID(details);
		if (id==null){
			//preference doesn't exist. can't delete it
			logging.debug("Preference :"+details.toString()+"\ndoesn't exist. Aborting deletion");
			return false;
		}
			PreferenceStorer storer = new PreferenceStorer(this.broker);
			if (storer.deletePreference(dpi, id)){
				this.registry.deletePreference(details);
				storer.storeRegistry(dpi, registry);
				return true;
			}else{
				return false;
			}
				
	}
	public List<String> getPreferenceNamesofService(String serviceType, ServiceResourceIdentifier serviceID){
		return this.registry.getPreferenceNamesofService(serviceType, serviceID);
	}
	
	public List<PreferenceDetails> getPreferenceDetailsForAllPreferences(){
		return this.registry.getPreferenceDetailsOfAllPreferences();
	}
}

