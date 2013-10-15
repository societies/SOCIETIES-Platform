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
package org.societies.personalisation.CommunityPreferenceManagement.impl.management;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CommunityPreferenceManagement.impl.Tools;
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
	private Hashtable<IIdentity,Registry> registries;
	private Tools tools; 
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private final ICtxBroker broker;
	private PreferenceRetriever retriever;
	private ICommManager commsMgr;
	private ICisManager cisManager;

	public PrivatePreferenceCache(ICtxBroker broker, ICommManager commsMgr, ICisManager cisManager ){

		this.broker = broker;
		this.commsMgr = commsMgr;
		this.cisManager = cisManager;

		this.idToIPreferenceTreeModel = new Hashtable<CtxIdentifier, IPreferenceTreeModel>();
		this.retriever = new PreferenceRetriever(this.broker, this.cisManager, this.commsMgr);
		this.registries = retriever.retrieveRegistries();

	}

	private IPreferenceTreeModel getPreference(CtxIdentifier id){
		//if the preference exists in the cache return it
		if (this.idToIPreferenceTreeModel==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("Hashtable is null. Cache not initalised properly");
			}
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
	public IPreferenceTreeModel getPreference(IIdentity cisId, PreferenceDetails details){
		if (cisId!=null){
			Enumeration<IIdentity> keys = this.registries.keys();
			while (keys.hasMoreElements()){
				IIdentity nextElement = keys.nextElement();
				if (cisId.getBareJid().equalsIgnoreCase(nextElement.getBareJid())){
					Registry registry = this.registries.get(nextElement);
					CtxIdentifier id = registry.getCtxID(details);
					if (id==null){
						if (logging.isDebugEnabled()){
							this.logging.debug("Could not find preference for :\n"+details.toString());
						}
						return null;
					}else{
						if (logging.isDebugEnabled()){
							this.logging.debug("Found preference in DB. CtxID: "+id.toUriString()+" for: "+details.toString());
						}

					}
					return this.getPreference(id);		
				}
			}
			if (logging.isDebugEnabled()){
				this.logging.debug("Could not find registry for cisId :"+cisId.getBareJid());
			}
			return null;
		}
		if (logging.isDebugEnabled()){
			this.logging.debug("Request for community preference with a null cisId ");
		}
		return null;


	}
	public IPreferenceTreeModel getPreference(IIdentity cisId, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName){
		if (serviceType==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("request to get preference with null serviceType, returning empty model");
			}
			return null;
		}
		if (serviceID == null){
			if (logging.isDebugEnabled()){
				this.logging.debug("request to get preference with null serviceID, returning empty model");
			}
			return null;
		}
		if (preferenceName==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("request to get preference with null preferenceName, returning empty model");
			}
			return null;
		}
		return this.getPreference(cisId, new PreferenceDetails(serviceType, serviceID, preferenceName));
	}


	public boolean storePreference(IIdentity cisId, PreferenceDetails details, IPreferenceTreeModel model){
		if (cisId!=null){
			Registry registry = null;
			Enumeration<IIdentity> keys = this.registries.keys();
			while (keys.hasMoreElements()){
				IIdentity nextElement = keys.nextElement();
				if (cisId.getBareJid().equals(nextElement.getBareJid())){
					registry = registries.get(nextElement);
				}
			}
			if (registry == null){
				registry = new Registry(cisId);
				this.registries.put(cisId, registry);
			}
			if (logging.isDebugEnabled()){
				this.logging.debug("Request to store community preference for:"+details.toString());
			}


			CtxIdentifier id = registry.getCtxID(details);
			if (id==null){
				if (logging.isDebugEnabled()){
					this.logging.debug("Community Preference doesn't exist in DB. Attempt  to store new preference");
				}
				//preference doesn't exist. we're going to store new preference in the db
				PreferenceStorer storer = new PreferenceStorer(this.broker);
				CtxIdentifier newCtxIdentifier = storer.storeNewPreference(cisId, model, registry.getNameForNewPreference());
				if (newCtxIdentifier==null){
					if (logging.isDebugEnabled()){
						this.logging.debug("Could not store NEW community preference in DB. aborting");
					}
					return false;
				}
				if (logging.isDebugEnabled()){
					this.logging.debug("Successfully stored NEW community preference in DB. CtxID: "+newCtxIdentifier.toUriString());
				}
				registry.addPreference(details, newCtxIdentifier);
				if (logging.isDebugEnabled()){
					this.logging.debug("Successfully added community preference details to registry: ");
				}
				if (logging.isDebugEnabled()){
					this.logging.debug("Stored community preference for: "+details.toString());
				}
				storer.storeRegistry(cisId, registry);
				if (logging.isDebugEnabled()){
					this.logging.debug("Successfully community stored registry in DB");
				}
				this.idToIPreferenceTreeModel.put(newCtxIdentifier, model);
				if (logging.isDebugEnabled()){
					this.logging.debug("Successfully added community preference to cache");
				}

			}else{
				if (logging.isDebugEnabled()){
					this.logging.debug("community Preference exists in DB. Attempt  to update existing community preference");
				}
				PreferenceStorer storer = new PreferenceStorer(this.broker);
				if (!storer.storeExisting(cisId, id, model)){
					return false;
				}
				if (logging.isDebugEnabled()){
					this.logging.debug("Successfully updated community preference in DB. CtxID: "+id.toUriString());
				}
				this.idToIPreferenceTreeModel.put(id, model);
				if (logging.isDebugEnabled()){
					this.logging.debug("Successfully updated community preference cache with new preference");
				}
			}
			return true;
		}
		return false;
	}

	public void deletePreference(IIdentity cisId, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName){
		if (cisId==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("Request to delete a community preference of a null cisId");
			}
			return;
		}
		Registry registry = null;
		Enumeration<IIdentity> keys = this.registries.keys();
		while (keys.hasMoreElements()){
			IIdentity nextElement = keys.nextElement();
			if (cisId.getBareJid().equals(nextElement.getBareJid())){
				registry = registries.get(nextElement);
			}
		}
		if (registry==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("Request to delete a preference that doesn't exist");
			}
			return;
		}
		PreferenceDetails details = new PreferenceDetails(serviceType, serviceID, preferenceName);
		CtxIdentifier id = registry.getCtxID(details);
		if (id==null){
			//preference doesn't exist. can't delete it
			if (logging.isDebugEnabled()){
				logging.debug("Preference "+preferenceName+" of "+serviceType+":"+ServiceModelUtils.serviceResourceIdentifierToString(serviceID)+"doesn't exist. Aborting deletion");
			}
		}else{
			PreferenceStorer storer = new PreferenceStorer(this.broker);
			storer.deletePreference(cisId, id);
			registry.deletePreference(details);
			storer.storeRegistry(cisId, registry);
		}
	}

	public boolean deletePreference(IIdentity cisId, PreferenceDetails details){
		if (cisId==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("Request to delete a community preference of a null cisId");
			}
			return false;
		}
		Registry registry = null;
		Enumeration<IIdentity> keys = this.registries.keys();
		while (keys.hasMoreElements()){
			IIdentity nextElement = keys.nextElement();
			if (cisId.getBareJid().equals(nextElement.getBareJid())){
				registry = registries.get(nextElement);
			}
		}
		if (registry==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("Request to delete a preference that doesn't exist");
			}
			return false;
		}
		CtxIdentifier id = registry.getCtxID(details);
		if (id==null){
			//preference doesn't exist. can't delete it
			if (logging.isDebugEnabled()){
				logging.debug("Preference :"+details.toString()+"\ndoesn't exist. Aborting deletion");
			}
			return false;
		}
		PreferenceStorer storer = new PreferenceStorer(this.broker);
		if (storer.deletePreference(cisId, id)){
			registry.deletePreference(details);
			storer.storeRegistry(cisId, registry);
			return true;
		}else{
			return false;
		}

	}
/*	public List<String> getPreferenceNamesofService(String serviceType, ServiceResourceIdentifier serviceID){
		return this.registry.getPreferenceNamesofService(serviceType, serviceID);
	}*/

	public ArrayList<PreferenceDetails> getPreferenceDetailsForAllPreferences(IIdentity cisId){
		if (cisId==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("Request to retrieve community preference details with a null cisId");
			}
			return new ArrayList<PreferenceDetails>();
		}
		Enumeration<IIdentity> keys = this.registries.keys();
		while (keys.hasMoreElements()){
			IIdentity nextElement = keys.nextElement();
			if (cisId.getBareJid().equals(nextElement.getBareJid())){
				return registries.get(nextElement).getPreferenceDetailsOfAllPreferences();
			}
		}
		if (logging.isDebugEnabled()){
			this.logging.debug("Request to retrieve all community preference details for cisId: "+cisId.getBareJid()+" but not found a registry for that cisId");
		}
		return new ArrayList<PreferenceDetails>();
	}
}

