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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.management;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IDSPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;

public class PrivatePreferenceCache {

	
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private Registry registry;
	private PreferenceRetriever retriever;
	private PreferenceStorer storer;
	//the key refers to the ctxID of the (ppn) preference attribute (not the affected context attribute)
	private Hashtable<CtxAttributeIdentifier, IPrivacyPreferenceTreeModel> ppnCtxIDtoModel;
	//the key refers to the ctxID of the (ids) preference attribute (not the affected context attribute)
	private Hashtable<CtxAttributeIdentifier, IPrivacyPreferenceTreeModel> idsCtxIDtoModel;
	
	
	

	public PrivatePreferenceCache(ICtxBroker broker){
		this.ppnCtxIDtoModel = new Hashtable<CtxAttributeIdentifier,IPrivacyPreferenceTreeModel>();
		this.idsCtxIDtoModel = new Hashtable<CtxAttributeIdentifier, IPrivacyPreferenceTreeModel>();
		this.retriever = new PreferenceRetriever(broker);
		this.storer = new PreferenceStorer(broker);
		this.registry = retriever.retrieveRegistry();
		
	}
	
	
	public void addPPNPreference(PPNPreferenceDetails details, IPrivacyPreferenceTreeModel model){
		printCacheContentsOnScreen();
		this.logging.debug("REquest to add preference :\n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getPPNPreference(details);
		if (preferenceCtxID==null){
			this.logging.debug("Registry did not contain preference details. Proceeding to add as new preference");
			String name = this.registry.getNameForNewPreference(PrivacyPreferenceTypeConstants.PPNP);
			preferenceCtxID = this.storer.storeNewPreference(model, name);
			this.registry.addPPNPreference(details, preferenceCtxID);
			this.ppnCtxIDtoModel.put(preferenceCtxID, model);
			this.storer.storeRegistry(registry);
			this.logging.debug("Preference didn't exist. Created new context attribute");

		}else{
			this.logging.debug("Registry contained preference details. Proceeding to update existing preference");
			this.storer.storeExisting(preferenceCtxID, model);
			this.ppnCtxIDtoModel.put(preferenceCtxID, model);
			this.logging.debug("Preference existed and updated.");

		}
		printCacheContentsOnScreen();
		
	}
	

	public void addIDSPreference(IDSPreferenceDetails details, IPrivacyPreferenceTreeModel model){
		printCacheContentsOnScreen();
		this.logging.debug("REquest to add preference :\n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getIDSPreference(details);
		if (preferenceCtxID==null){
			this.logging.debug("Registry did not contain preference details. Proceeding to add as new preference");
			String name = this.registry.getNameForNewPreference(PrivacyPreferenceTypeConstants.IDS);
			preferenceCtxID = this.storer.storeNewPreference(model, name);
			if (preferenceCtxID!=null){
				this.registry.addIDSPreference(details, preferenceCtxID);
				this.idsCtxIDtoModel.put(preferenceCtxID, model);
				this.storer.storeRegistry(registry);
				this.logging.debug("Preference didn't exist. Created new context attribute");
			}else{
				this.logging.debug("Could not save IDS preference. Broker returned null ctx ID");
			}

		}else{
			this.logging.debug("Registry contained preference details. Proceeding to update existing preference");
			this.idsCtxIDtoModel.put(preferenceCtxID, model);
			this.storer.storeExisting(preferenceCtxID, model);
			this.logging.debug("Preference existed and updated.");

		}
		printCacheContentsOnScreen();	
	}
	public IPrivacyPreferenceTreeModel getPPNPreference(PPNPreferenceDetails details){
		
		this.logging.debug("Request for preference: \n"+details.toString());
		printCacheContentsOnScreen();
		CtxAttributeIdentifier preferenceCtxID = this.registry.getPPNPreference(details);
		if (preferenceCtxID!=null){
			if (this.ppnCtxIDtoModel.containsKey(preferenceCtxID)){
				this.logging.debug("Found preference. returning object");
				return this.ppnCtxIDtoModel.get(preferenceCtxID);
			}else{
				this.logging.debug("Cache doesn't have preference. Will return obj if found in DB");
				return this.retrievePPNPFromDB(preferenceCtxID);
			}
			
		}else{
			this.logging.debug("NOt found preference, returning null");
			return null;
		}
	}
	
	public IPrivacyPreferenceTreeModel getIDSPreference(IDSPreferenceDetails details){
		
		this.logging.debug("Request for preference: \n"+details.toString());
		printCacheContentsOnScreen();
		
		CtxAttributeIdentifier preferenceCtxID = this.registry.getIDSPreference(details);
		if (preferenceCtxID!=null){
			if (this.idsCtxIDtoModel.containsKey(preferenceCtxID)){
				this.logging.debug("Found preference in registry and cache. returning object");
				return this.idsCtxIDtoModel.get(preferenceCtxID);
			}else{
				this.logging.debug("Found preference in registry. Cache doesn't have preference. Will return obj if found in DB");
				return this.retrieveIDSPFromDB(preferenceCtxID);
			}
			
		}else{
			this.logging.debug("ERROR251 - Not found preference in registry, returning null");
			return null;
		}
	}
	
	private IPrivacyPreferenceTreeModel retrievePPNPFromDB(CtxAttributeIdentifier preferenceCtxID){
		this.logging.debug("Request to retrieve preference from DB");
		IPrivacyPreferenceTreeModel model = this.retriever.retrievePreference(preferenceCtxID);
		if (model!=null){
			this.logging.debug("Preference found. returning");
			this.ppnCtxIDtoModel.put(preferenceCtxID, model);
			return model;
		}else{
			this.logging.debug("Preference not found. returning null");
			return null;
		}
	}
	private IPrivacyPreferenceTreeModel retrieveIDSPFromDB(CtxAttributeIdentifier preferenceCtxID){
		this.logging.debug("Request to retrieve preference from DB");
		IPrivacyPreferenceTreeModel model = this.retriever.retrievePreference(preferenceCtxID);
		if (model!=null){
			this.logging.debug("Preference found. returning");
			this.idsCtxIDtoModel.put(preferenceCtxID, model);
			return model;
		}else{
			this.logging.debug("Preference not found. returning null");
			return null;
		}
	}
	private IPrivacyPreferenceTreeModel findPPNPreference(CtxAttributeIdentifier preferenceCtxID){
		if (this.ppnCtxIDtoModel.containsKey(preferenceCtxID)){
			return this.ppnCtxIDtoModel.get(preferenceCtxID);
		}else{
			return this.retrievePPNPFromDB(preferenceCtxID);
		}
	}
	private IPrivacyPreferenceTreeModel findIDSPreference(CtxAttributeIdentifier preferenceCtxID){
		if (this.idsCtxIDtoModel.containsKey(preferenceCtxID)){
			return this.idsCtxIDtoModel.get(preferenceCtxID);
		}else{
			return this.retrieveIDSPFromDB(preferenceCtxID);
		}
	}
	
	public IPrivacyPreferenceTreeModel getPPNPreference(String contextType, CtxAttributeIdentifier affectedCtxID, Identity dpi, IServiceResourceIdentifier serviceID){
		PPNPreferenceDetails details = new PPNPreferenceDetails(contextType);
		details.setAffectedCtxID(affectedCtxID);
		details.setRequestorDPI(dpi);
		details.setServiceID(serviceID);
		return this.getPPNPreference(details);
	}
	
	public IPrivacyPreferenceTreeModel getIDSPreference(Identity affectedDPI, Identity providerDPI, IServiceResourceIdentifier serviceID){
		IDSPreferenceDetails details = new IDSPreferenceDetails(affectedDPI);
		details.setProviderDPI(providerDPI);
		details.setServiceID(serviceID);
		return this.getIDSPreference(details);
	}
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType){
		this.logging.debug("Request for preferences for context type: \n"+contextType);
		List<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getPPNPreferences(contextType);
		
		for (CtxAttributeIdentifier ctxID : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel model = this.findPPNPreference(ctxID);
			if (model!=null){
				modelList.add(model);
			}
		}
		
		this.logging.debug("Found "+modelList.size()+" preferences for contextType: "+contextType);
		return modelList;
	}
	
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences(Identity affectedDPI){
		this.logging.debug("Request for IDS preferences for DPI: \n"+affectedDPI.toString());
		List<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getIDSPreferences(affectedDPI);
		
		for (CtxAttributeIdentifier ctxID : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel model = this.findIDSPreference(ctxID);
			if (model!=null){
				modelList.add(model);
			}
		}
		
		this.logging.debug("Found "+modelList.size()+" IDS preferences for dpi: "+affectedDPI.toString());
		return modelList;
	}
	
	
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, CtxAttributeIdentifier affectedCtxID){
		this.logging.debug("Request for preferences for context type: \n"+contextType+" and affectedCtxID: "+affectedCtxID.toString());
		List<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getPPNPreferences(contextType, affectedCtxID);
		
		for (CtxAttributeIdentifier ctxID : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel model = this.findPPNPreference(ctxID);
			if (model!=null){
				modelList.add(model);
			}
		}
		
		this.logging.debug("Found "+modelList.size()+" preferences for contextType: "+contextType+" and affectedCtxID: "+affectedCtxID.toString());

		return modelList;
	}
	
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, CtxAttributeIdentifier affectedCtxID, Identity requestorDPI){
		this.logging.debug("Request for preferences for context type: \n"+contextType+" and affectedCtxID: "+affectedCtxID.toString());
		List<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getPPNPreferences(contextType, affectedCtxID, requestorDPI);
		
		for (CtxAttributeIdentifier ctxID : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel model = this.findPPNPreference(ctxID);
			if (model!=null){
				modelList.add(model);
			}
		}
		
		this.logging.debug("Found "+modelList.size()+" preferences for contextType: "+contextType+" and affectedCtxID: "+affectedCtxID.toString());

		return modelList;
	}
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences(Identity affectedDPI, Identity providerDPI){
		this.logging.debug("Request for IDS preferences for dpi: \n"+affectedDPI.toString()+", providerDPI: "+providerDPI.toString());
		List<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getIDSPreferences(affectedDPI, providerDPI);
		
		for (CtxAttributeIdentifier ctxID : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel model = this.findIDSPreference(ctxID);
			if (model!=null){
				modelList.add(model);
			}
		}
		
		this.logging.debug("Found "+modelList.size()+" IDS preferences for dpi: \n"+affectedDPI.toString()+", providerDPI: "+providerDPI.toString());

		return modelList;
	}
	
	
	
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, Identity dpi){
		this.logging.debug("Request for preferences for context type: \n"+contextType+" and requestorDPI: "+dpi.toString());

		List<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getPPNPreferences(contextType, dpi);
		
		for (CtxAttributeIdentifier ctxID : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel model = this.findPPNPreference(ctxID);
			if (model!=null){
				modelList.add(model);
			}
		}
		this.logging.debug("Found "+modelList.size()+" preferences for contextType: "+contextType+" and requestorDPI: "+dpi.toString());

		return modelList;
	}
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, Identity dpi, IServiceResourceIdentifier serviceID){
		this.logging.debug("Request for preferences for context type: \n"+contextType+" and requestorDPI: "+dpi.toString());

		List<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getPPNPreferences(contextType, dpi, serviceID);
		
		for (CtxAttributeIdentifier ctxID : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel model = this.findPPNPreference(ctxID);
			if (model!=null){
				modelList.add(model);
			}
		}
		this.logging.debug("Found "+modelList.size()+" preferences for contextType: "+contextType+" and requestorDPI: "+dpi.toString());

		return modelList;
	}
	public void removePPNPreference(PPNPreferenceDetails details){
		this.logging.debug("Request to remove preference : \n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getPPNPreference(details);
		if (preferenceCtxID!=null){
			this.storer.deletePreference(preferenceCtxID);
			if (this.ppnCtxIDtoModel.containsKey(preferenceCtxID)){
				
				logging.debug("Deleting preference details:"+details.toString());
				this.ppnCtxIDtoModel.remove(preferenceCtxID);
				this.registry.removePPNPreference(details);
				this.storer.storeRegistry(registry);
			}
		}else{
			this.logging.debug("Registry did not contain preference details: "+details.toString());
		}
	}
	
	public void removeIDSPreference(IDSPreferenceDetails details){
		this.logging.debug("Request to remove preference : \n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getIDSPreference(details);
		if (preferenceCtxID!=null){
			this.storer.deletePreference(preferenceCtxID);
			if (this.idsCtxIDtoModel.containsKey(preferenceCtxID)){
				this.idsCtxIDtoModel.remove(preferenceCtxID);
				this.registry.removeIDSPreference(details);
				this.storer.storeRegistry(registry);
			}
		}else{
			this.logging.debug("Preference Details not found in registry: "+details.toString());
		}
	}
	
	private void printCacheContentsOnScreen(){
	
		this.logging.debug("*********CACHE CONTENTS START **************");
		this.logging.debug(this.registry.toString());
		this.logging.debug("*********CACHE CONTENTS END **************");
	}

	
	public List<PPNPreferenceDetails> getPPNPreferenceDetails(){
		return this.registry.getPPNPreferenceDetails();
	}
	
	public List<IDSPreferenceDetails> getIDSPreferenceDetails(){
		return this.registry.getIDSPreferenceDetails();	
	}
	/*
	 * OLD METHODS
	 */



	/*	public ArrayList<IPrivacyPreferenceTreeModel> getPreferences(String contextType){
	ArrayList<IPrivacyPreferenceTreeModel> prefList = new ArrayList<IPrivacyPreferenceTreeModel>();
	List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getPreferences(contextType);
	for (CtxAttributeIdentifier id : preferenceCtxIDs){
		if (this.ppnCtxIDtoModel.contains(id)){
			prefList.add(this.ppnCtxIDtoModel.get(id));
		}else{
			IPrivacyPreferenceTreeModel model = this.retriever.retrievePreference(id);
			if (model!=null){
				this.ppnCtxIDtoModel.put(id, model);
				if (null!=model.getAffectedContextIdentifier()){
					this.affectedCtxIDtoPreferenceCtxIDMap.put(model.getAffectedContextIdentifier(), id);
				}
				prefList.add(model);
			}
		}
	}
		return prefList;
	
}*/

/*	public Hashtable<String, ArrayList<IPrivacyPreferenceTreeModel>> getPreferences(ArrayList<String> contextTypes){
	Hashtable<String, ArrayList<IPrivacyPreferenceTreeModel>>  table = new Hashtable<String, ArrayList<IPrivacyPreferenceTreeModel>>();
	
	for (String contextType : contextTypes){
		ArrayList<IPrivacyPreferenceTreeModel> modelList = this.getPreferences(contextType);
		table.put(contextType, modelList);
	}
	
	return table;
}*/





/*	public void storePreference(IPrivacyPreferenceTreeModel model){
	if (model.getPrivacyType().equals(PrivacyPreferenceTypeConstants.PPNP)){
		String contextType = model.getContextType();
		CtxAttributeIdentifier affectedCtxID = model.getAffectedContextIdentifier();
		boolean isSpecificToCtxID = (affectedCtxID != null);

		if this preference is specific to a context attribute,
		 * and a preference already exists that affects the
		 * access permissions of this ctx attribute, this preference
		 * has to overwrite the existing preference. if it doesnt exist,
		 * it is stored as a new preference (specific to a ctx id)
		 * if it is generic (i.e. it only affects a context type and not
		 * a specific context attribute), the preference has to overwrite
		 * the generic preference. if a generic preference for this context
		 * type does not exist, it will be stored as a new preference. 
		 
		List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getPreferences(contextType);
		if (isSpecificToCtxID){
			if (this.affectedCtxIDtoPreferenceCtxIDMap.contains(affectedCtxID)){
				CtxAttributeIdentifier preferenceCtxID = this.getPreferenceCtxIDFromAffectedCtxID(affectedCtxID);
				this.storer.storeExisting(preferenceCtxID, model);
				return;
			}
		}else{
			for (CtxAttributeIdentifier id : preferenceCtxIDs){
				IPrivacyPreferenceTreeModel tempModel = this.getPreference(id);
				if (tempModel!=null){
					if (tempModel.getAffectedContextIdentifier()==null){
						this.storer.storeExisting(id, model);
						return;
					}
				}
			}
		}
		
		//if not found, it needs to be stored as a new preference

		String newPrefName = this.registry.getNameForNewPreference(PrivacyPreferenceTypeConstants.PPNP);
		CtxAttributeIdentifier newPreferenceCtxID = storer.storeNewPreference(model, newPrefName);
		this.registry.addPreference(contextType, newPreferenceCtxID);
		this.storer.storeRegistry(registry);
		
		
	}else if (model.getPrivacyType().equals(PrivacyPreferenceTypeConstants.IDS)){

		//TODO: DECIDE WHAT TO DO!!!
	}
}*/




/*
 * 
 * @param contextType the affected contextType
 * @param ctxID	the affected ctxID. 
 * 	if the affected contextType is symloc then the ctxID will be the the ctxID of the symloc attribute as stored in the DB. 
 * In order to delete only the generic preference that affects any symloc attribute stored in the DB, insert null. 
 */
/*	public void deletePreference(String contextType, CtxAttributeIdentifier affectedCtxID){
	List<CtxAttributeIdentifier> preferenceCtxIDs = this.registry.getPreferences(contextType);
	if (affectedCtxID==null){
		for (CtxAttributeIdentifier id : preferenceCtxIDs){
			IPrivacyPreferenceTreeModel tempModel = this.getPreference(id);
			if (null==tempModel.getAffectedContextIdentifier()){
				this.ppnCtxIDtoModel.remove(id);
				this.registry.removePreference(contextType, id);
				this.storer.deletePreference(id);
			}
		}
	}else{
		for (CtxAttributeIdentifier id: preferenceCtxIDs){
			IPrivacyPreferenceTreeModel tempModel = this.getPreference(id);
			if (null!=tempModel.getAffectedContextIdentifier()){
				if (tempModel.getAffectedContextIdentifier().equals(affectedCtxID)){
					this.ppnCtxIDtoModel.remove(id);
					this.registry.removePreference(contextType, id);
					this.storer.deletePreference(id);
				}
			}
		}
	}
	
}*/
}


