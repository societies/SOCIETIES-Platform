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

import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPrivacyPreferenceTreeModelBean;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;

public class PrivatePreferenceCache {


	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private Registry registry;
	private PreferenceRetriever retriever;
	private PreferenceStorer storer;
	//the key refers to the ctxID of the (ppn) preference attribute (not the affected context attribute)
	private Hashtable<CtxAttributeIdentifier, PPNPrivacyPreferenceTreeModel> ppnCtxIDtoModel;
	//the key refers to the ctxID of the (ids) preference attribute (not the affected context attribute)
	private Hashtable<CtxAttributeIdentifier, IDSPrivacyPreferenceTreeModel> idsCtxIDtoModel;
	//the key refers to the ctxID of the (ids) preference attribute (not the affected context attribute)
	private Hashtable<CtxAttributeIdentifier, DObfPreferenceTreeModel> dobfCtxIDtoModel;
	//the key refers to the ctxID of the (accCtrl) preference attribute (not the affected context attribute)
	private Hashtable<CtxAttributeIdentifier, AccessControlPreferenceTreeModel> accCtrlCtxIDtoModel;

	private IIdentityManager idMgr;



	public PrivatePreferenceCache(ICtxBroker broker, IIdentityManager idMgr){
		this.idMgr = idMgr;
		this.ppnCtxIDtoModel = new Hashtable<CtxAttributeIdentifier,PPNPrivacyPreferenceTreeModel>();
		this.idsCtxIDtoModel = new Hashtable<CtxAttributeIdentifier, IDSPrivacyPreferenceTreeModel>();
		this.dobfCtxIDtoModel = new Hashtable<CtxAttributeIdentifier, DObfPreferenceTreeModel>();
		this.accCtrlCtxIDtoModel = new Hashtable<CtxAttributeIdentifier, AccessControlPreferenceTreeModel>();
		this.retriever = new PreferenceRetriever(broker, this.idMgr);
		this.storer = new PreferenceStorer(broker, idMgr);
		this.registry = retriever.retrieveRegistry();

	}


	public boolean addPPNPreference(PPNPreferenceDetailsBean details, PPNPrivacyPreferenceTreeModel model) {
		printCacheContentsOnScreen("Before update");
		this.logging.debug("REquest to add preference :\n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getPPNPreference(details);
		PPNPrivacyPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toPPNPrivacyPreferenceTreeModelBean(model);
		if (preferenceCtxID==null){
			this.logging.debug("Registry did not contain preference details. Proceeding to add as new preference");
			String name = this.registry.getNameForNewPreference(PrivacyPreferenceTypeConstants.PRIVACY_POLICY_NEGOTIATION);
			try {
				preferenceCtxID = this.storer.storeNewPreference(bean, name);
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			this.registry.addPPNPreference(details, preferenceCtxID);
			this.ppnCtxIDtoModel.put(preferenceCtxID, model);
			try {
				this.storer.storeRegistry(registry);
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.logging.debug("Unable to update Registry after adding PPN preference");
				return false;
			}
			this.logging.debug("Preference didn't exist. Created new context attribute");

		}else{
			this.logging.debug("Registry contained preference details. Proceeding to update existing preference");
			if (this.storer.storeExisting(preferenceCtxID, bean)){
				this.ppnCtxIDtoModel.put(preferenceCtxID,  model);
				this.logging.debug("Preference existed and updated.");
			}else{
				this.logging.debug("Unable to store PPN preference");
				return false;
			}

		}
		printCacheContentsOnScreen("After update");
		return true;
	}

	public boolean addIDSPreference(IDSPreferenceDetailsBean details, IDSPrivacyPreferenceTreeModel model) {
		printCacheContentsOnScreen("Before Update");
		this.logging.debug("REquest to add preference :\n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getIDSPreference(details);
		IDSPrivacyPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toIDSPreferenceTreeModelBean(model);
		if (preferenceCtxID==null){
			this.logging.debug("Registry did not contain preference details. Proceeding to add as new preference");
			String name = this.registry.getNameForNewPreference(PrivacyPreferenceTypeConstants.IDENTITY_SELECTION);
			try {
				preferenceCtxID = this.storer.storeNewPreference(bean, name);
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.logging.debug("Could not save IDS preference. Broker returned null ctx ID");
				return false;
			}
			this.registry.addIDSPreference(details, preferenceCtxID);
			this.idsCtxIDtoModel.put(preferenceCtxID, model);
			try {
				this.storer.storeRegistry(registry);
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.logging.debug("Unable to update Registry after adding IDS preference");
				return false;
			}

			this.logging.debug("Preference didn't exist. Created new context attribute");

		}else{
			this.logging.debug("Registry contained preference details. Proceeding to update existing preference");
			if (this.storer.storeExisting(preferenceCtxID, bean)){
				this.idsCtxIDtoModel.put(preferenceCtxID, model);
				this.logging.debug("Preference existed and updated.");	
			}else{
				this.logging.debug("Unable to store IDS preference");
				return false;
			}


		}
		printCacheContentsOnScreen("After Update");	
		return true;
	}

	public boolean addDObfPreference(DObfPreferenceDetailsBean details, DObfPreferenceTreeModel model){
		printCacheContentsOnScreen("Before Update");
		this.logging.debug("REquest to add preference :\n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getDObfPreference(details);
		DObfPrivacyPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toDObfPrivacyPreferenceTreeModelBean(model);
		if (preferenceCtxID==null){
			this.logging.debug("Registry did not contain preference details. Proceeding to add as new preference");
			String name = this.registry.getNameForNewPreference(PrivacyPreferenceTypeConstants.DATA_OBFUSCATION);
			try {
				preferenceCtxID = this.storer.storeNewPreference(bean, name);
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.logging.debug("Could not save DObf preference. Broker returned null ctx ID");
				return false;
			}

			this.registry.addDObfPreference(details, preferenceCtxID);
			this.dobfCtxIDtoModel.put(preferenceCtxID, model);
			try {
				this.storer.storeRegistry(registry);
			} catch (PrivacyException e) {
				e.printStackTrace();
				this.logging.debug("Unable to update Registry after adding DObf preference");
				return false;
			}
			this.logging.debug("Preference didn't exist. Created new context attribute");

		}else{
			this.logging.debug("Registry contained preference details. Proceeding to update existing preference");
			if (this.storer.storeExisting(preferenceCtxID, bean)){
				this.dobfCtxIDtoModel.put(preferenceCtxID, model);
				this.logging.debug("Preference existed and updated.");
			}else{
				this.logging.debug("Unable to store DObf preference");
				return false;
			}
		}		
		printCacheContentsOnScreen("After Update");		
		return true;
	}

	public boolean addAccCtrlPreference(AccessControlPreferenceDetailsBean details, AccessControlPreferenceTreeModel model){
		printCacheContentsOnScreen("Before Update");
		this.logging.debug("REquest to add preference :\n"+details.toString());
		this.logging.debug("REquest to add preference :\n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getAccCtrlPreference(details);
		AccessControlPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toAccessControlPreferenceTreeModelBean(model);
		if (preferenceCtxID==null){
			this.logging.debug("Registry did not contain preference details. Proceeding to add as new preference");
			this.logging.debug("Registry did not contain preference details. Proceeding to add as new preference");
			String name = this.registry.getNameForNewPreference(PrivacyPreferenceTypeConstants.ACCESS_CONTROL);
			try {
				preferenceCtxID = this.storer.storeNewPreference(bean, name);
			} catch (PrivacyException e) {
				e.printStackTrace();
				this.logging.debug("Could not save AccCtrl preference. Broker returned null ctx ID");
				this.logging.debug("Could not save AccCtrl preference. Broker returned null ctx ID");
				return false;
			}

			this.registry.addAccessCtrlPreference(details, preferenceCtxID);
			this.accCtrlCtxIDtoModel.put(preferenceCtxID, model);
			try {
				this.storer.storeRegistry(registry);
			} catch (PrivacyException e) {
				e.printStackTrace();
				this.logging.debug("Unable to update Registry after adding AccCtrl preference");
				this.logging.debug("Unable to update Registry after adding AccCtrl preference");
				return false;
			}
			this.logging.debug("Preference didn't exist. Created new context attribute");
			this.logging.debug("Preference didn't exist. Created new context attribute");


		}else{
			this.logging.debug("Registry contained preference details. Proceeding to update existing preference");
			this.logging.debug("Registry contained preference details. Proceeding to update existing preference");
			if (this.storer.storeExisting(preferenceCtxID, bean)){
				this.accCtrlCtxIDtoModel.put(preferenceCtxID, model);
				this.logging.debug("Preference existed and updated.");
				this.logging.debug("Preference existed and updated.");
			}else{
				this.logging.debug("Unable to store AccCtrl preference");
				this.logging.debug("Unable to store AccCtrl preference");
				return false;
			}
		}
		printCacheContentsOnScreen("After Update");		

		return true;
	}

	public PPNPrivacyPreferenceTreeModel getPPNPreference(PPNPreferenceDetailsBean details){

		this.logging.debug("Request for preference: \n"+details.toString());
		printCacheContentsOnScreen("No update");
		CtxAttributeIdentifier preferenceCtxID = this.registry.getPPNPreference(details);
		if (preferenceCtxID!=null){
			if (this.ppnCtxIDtoModel.containsKey(preferenceCtxID)){
				this.logging.debug("Found preference. returning object");
				return this.ppnCtxIDtoModel.get(preferenceCtxID);
			}else{
				this.logging.debug("Cache doesn't have preference. Will return obj if found in DB");
				try {
					return this.retrievePPNPFromDB(preferenceCtxID);
				} catch (PrivacyException e) {
					this.logging.debug("PPN preference not found");
					return null;
				}
			}

		}else{
			this.logging.debug("NOt found preference, returning null");
			return null;
		}
	}

	public IDSPrivacyPreferenceTreeModel getIDSPreference(IDSPreferenceDetailsBean details){

		this.logging.debug("Request for preference: \n"+details.toString());
		printCacheContentsOnScreen("No update");

		CtxAttributeIdentifier preferenceCtxID = this.registry.getIDSPreference(details);
		if (preferenceCtxID!=null){
			if (this.idsCtxIDtoModel.containsKey(preferenceCtxID)){
				this.logging.debug("Found preference in registry and cache. returning object");
				return this.idsCtxIDtoModel.get(preferenceCtxID);
			}else{
				this.logging.debug("Found preference in registry. Cache doesn't have preference. Will return obj if found in DB");
				try {
					return this.retrieveIDSPFromDB(preferenceCtxID);
				} catch (PrivacyException e) {
					this.logging.debug("IDS preference not found");
					return null;
				}
			}

		}else{
			this.logging.debug("ERROR251 - Not found preference in registry, returning null");
			return null;
		}
	}

	public DObfPreferenceTreeModel getDObfPreference(DObfPreferenceDetailsBean details){

		this.logging.debug("Request for preference: \n"+details.toString());
		printCacheContentsOnScreen("No update");

		CtxAttributeIdentifier preferenceCtxID = this.registry.getDObfPreference(details);
		if (preferenceCtxID!=null){
			if (this.dobfCtxIDtoModel.containsKey(preferenceCtxID)){
				this.logging.debug("Found preference in registry and cache. returning object");
				return this.dobfCtxIDtoModel.get(preferenceCtxID);
			}else{
				this.logging.debug("Found preference in registry. Cache doesn't have preference. Will return obj if found in DB");
				try {
					return this.retrieveDObfPFromDB(preferenceCtxID);
				} catch (PrivacyException e) {
					this.logging.debug("DObf preference not found");
					return null;
				}
			}

		}else{
			this.logging.debug("ERROR251 - Not found preference in registry, returning null");
			return null;
		}
	}

	public AccessControlPreferenceTreeModel getAccCtrlPreference(AccessControlPreferenceDetailsBean details){

		this.logging.debug("Request for preference: \n"+details.toString());
		printCacheContentsOnScreen("No update");

		CtxAttributeIdentifier preferenceCtxID = this.registry.getAccCtrlPreference(details);
		
		if (preferenceCtxID!=null){
			//JOptionPane.showMessageDialog(null, "cache: Found preferenceCtxId: "+preferenceCtxID.getUri());
			if (this.accCtrlCtxIDtoModel.containsKey(preferenceCtxID)){
				//JOptionPane.showMessageDialog(null, "cache: accCtrlCtxIDtoModel contains "+preferenceCtxID.getUri());
				this.logging.debug("Found preference in registry and cache. returning object");
				return this.accCtrlCtxIDtoModel.get(preferenceCtxID);
			}else{
				//JOptionPane.showMessageDialog(null, "cache: accCtrlCtxIDtoModel DOES NOT contain "+preferenceCtxID.getUri());
				this.logging.debug("Found preference in registry. Cache doesn't have preference. Will return obj if found in DB");
				try {
					return this.retrieveAccCtrlPFromDB(preferenceCtxID);
				} catch (PrivacyException e) {
					this.logging.debug("AccessCtrl preference not found");
					return null;
				}
			}

		}else{
			//JOptionPane.showMessageDialog(null, "cache: NOT Found preferenceCtxId");
			this.logging.debug("ERROR251 - Not found preference in registry, returning null");
			return null;
		}
	}
	private PPNPrivacyPreferenceTreeModel retrievePPNPFromDB(CtxAttributeIdentifier preferenceCtxID) throws PrivacyException{
		this.logging.debug("Request to retrieve preference from DB");
		IPrivacyPreferenceTreeModel model = this.retriever.retrievePreference(preferenceCtxID);

		if (model!=null){
			if (model instanceof PPNPrivacyPreferenceTreeModel){
				this.logging.debug("Preference found. returning");
				this.ppnCtxIDtoModel.put(preferenceCtxID, (PPNPrivacyPreferenceTreeModel) model);
				return (PPNPrivacyPreferenceTreeModel) model;
			}else{
				this.logging.debug("FAILURE: Retrieved model from context DB not of type "+PPNPrivacyPreferenceTreeModel.class.getName());
				throw new PrivacyException("FAILURE: Retrieved model from context DB not of type "+PPNPrivacyPreferenceTreeModel.class.getName());
			}
		}else{
			this.logging.debug("PPNPreference not found. returning null");
			throw new PrivacyException("PPN preference not found");
		}

	}
	private IDSPrivacyPreferenceTreeModel retrieveIDSPFromDB(CtxAttributeIdentifier preferenceCtxID) throws PrivacyException{
		this.logging.debug("Request to retrieve preference from DB");
		IPrivacyPreferenceTreeModel model = this.retriever.retrievePreference(preferenceCtxID);

		if (model!=null){
			if (model instanceof IDSPrivacyPreferenceTreeModel){
				this.logging.debug("Preference found. returning");
				this.idsCtxIDtoModel.put(preferenceCtxID, (IDSPrivacyPreferenceTreeModel) model);
				return (IDSPrivacyPreferenceTreeModel) model;
			}else{
				this.logging.debug("FAILURE: Retrieved model from context DB not of type "+IDSPrivacyPreferenceTreeModel.class.getName());
				throw new PrivacyException("FAILURE: Retrieved model from context DB not of type "+IDSPrivacyPreferenceTreeModel.class.getName());
			}
		}else{
			this.logging.debug("IDS Preference not found.");
			throw new PrivacyException("IDS Preference not found.");
		}
	}

	private DObfPreferenceTreeModel retrieveDObfPFromDB(CtxAttributeIdentifier preferenceCtxID) throws PrivacyException{
		this.logging.debug("Request to retrieve preference from DB");
		IPrivacyPreferenceTreeModel model = this.retriever.retrievePreference(preferenceCtxID);
		if (model!=null){
			if (model instanceof DObfPreferenceTreeModel){
				this.logging.debug("Preference found. returning");
				this.dobfCtxIDtoModel.put(preferenceCtxID, (DObfPreferenceTreeModel) model);
				return (DObfPreferenceTreeModel) model;
			}else{
				this.logging.debug("FAILURE: Retrieved model from context DB not of type "+DObfPreferenceTreeModel.class.getName());
				throw new PrivacyException("FAILURE: Retrieved model from context DB not of type "+DObfPreferenceTreeModel.class.getName());
			}
		}else{
			this.logging.debug("DObf Preference not found.");
			throw new PrivacyException("DObf Preference not found.");
		}
	}

	private AccessControlPreferenceTreeModel retrieveAccCtrlPFromDB(CtxAttributeIdentifier preferenceCtxID) throws PrivacyException{
		this.logging.debug("Request to retrieve preference from DB");
		IPrivacyPreferenceTreeModel model = this.retriever.retrievePreference(preferenceCtxID);

		if (model!=null){
			if (model instanceof AccessControlPreferenceTreeModel){
				this.logging.debug("Preference found. returning");
				this.accCtrlCtxIDtoModel.put(preferenceCtxID, (AccessControlPreferenceTreeModel) model);
				return (AccessControlPreferenceTreeModel) model;
			}else{
				this.logging.debug("FAILURE: Retrieved model from context DB not of type "+AccessControlPreferenceTreeModel.class.getName());
				throw new PrivacyException("FAILURE: Retrieved model from context DB not of type "+AccessControlPreferenceTreeModel.class.getName());
			}
		}else{
			this.logging.debug("Access control Preference not found.");
			throw new PrivacyException("Access control Preference not found.");
		}
	}
	private PPNPrivacyPreferenceTreeModel findPPNPreference(CtxAttributeIdentifier preferenceCtxID) throws PrivacyException{
		if (this.ppnCtxIDtoModel.containsKey(preferenceCtxID)){
			return this.ppnCtxIDtoModel.get(preferenceCtxID);
		}else{
			return this.retrievePPNPFromDB(preferenceCtxID);
		}
	}
	private IDSPrivacyPreferenceTreeModel findIDSPreference(CtxAttributeIdentifier preferenceCtxID) throws PrivacyException{
		if (this.idsCtxIDtoModel.containsKey(preferenceCtxID)){
			return this.idsCtxIDtoModel.get(preferenceCtxID);
		}else{
			return this.retrieveIDSPFromDB(preferenceCtxID);
		}
	}

	private IPrivacyPreferenceTreeModel findDObfPreference(CtxAttributeIdentifier preferenceCtxID) throws PrivacyException{
		if (this.dobfCtxIDtoModel.containsKey(preferenceCtxID)){
			return this.dobfCtxIDtoModel.get(preferenceCtxID);
		}else{
			return this.retrieveDObfPFromDB(preferenceCtxID);
		}
	}

	private IPrivacyPreferenceTreeModel findAccCtrlPreference(CtxAttributeIdentifier preferenceCtxID) throws PrivacyException{
		if (this.accCtrlCtxIDtoModel.containsKey(preferenceCtxID)){
			return this.accCtrlCtxIDtoModel.get(preferenceCtxID);
		}else{
			return this.retrieveAccCtrlPFromDB(preferenceCtxID);
		}
	}

	public boolean removePPNPreference(PPNPreferenceDetailsBean details){
		this.logging.debug("Request to remove preference : \n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getPPNPreference(details);
		if (preferenceCtxID!=null){
			this.storer.deletePreference(preferenceCtxID);
			if (this.ppnCtxIDtoModel.containsKey(preferenceCtxID)){

				logging.debug("Deleting preference details:"+details.toString());
				this.ppnCtxIDtoModel.remove(preferenceCtxID);
				this.registry.removePPNPreference(details);
				try {
					this.storer.storeRegistry(registry);
				} catch (PrivacyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.logging.debug("Unable to store updated registry after deleting PPN preference");

					return false;
				}
				return true;
			}
		}else{
			this.logging.debug("Registry did not contain preference details: "+details.toString());
		}
		return false;
	}

	public boolean removeIDSPreference(IDSPreferenceDetailsBean details){
		this.logging.debug("Request to remove preference : \n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getIDSPreference(details);
		if (preferenceCtxID!=null){
			this.storer.deletePreference(preferenceCtxID);
			if (this.idsCtxIDtoModel.containsKey(preferenceCtxID)){
				this.idsCtxIDtoModel.remove(preferenceCtxID);
				this.registry.removeIDSPreference(details);
				try {
					this.storer.storeRegistry(registry);
				} catch (PrivacyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.logging.debug("Unable to store updated registry after deleting IDS preference");
					return false;
				}
				return true;
			}
		}else{
			this.logging.debug("Preference Details not found in registry: "+details.toString());
		}

		return false;
	}

	public boolean removeDObfPreference(DObfPreferenceDetailsBean details){
		this.logging.debug("Request to remove preference : \n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getDObfPreference(details);
		if (preferenceCtxID!=null){
			this.storer.deletePreference(preferenceCtxID);
			if (this.dobfCtxIDtoModel.containsKey(preferenceCtxID)){
				this.dobfCtxIDtoModel.remove(preferenceCtxID);
				this.registry.removeDObfPreference(details);
				try {
					this.storer.storeRegistry(registry);

				} catch (PrivacyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.logging.debug("Unable to store updated registry after deleting DObf preference");
					return false;
				}
				return true;
			}
		}else{
			this.logging.debug("Preference Details not found in registry: "+details.toString());
		}
		return false;
	}

	public boolean removeAccCtrlPreference(AccessControlPreferenceDetailsBean details){
		this.logging.debug("Request to remove preference : \n"+details.toString());
		CtxAttributeIdentifier preferenceCtxID = this.registry.getAccCtrlPreference(details);
		if (preferenceCtxID!=null){
			this.storer.deletePreference(preferenceCtxID);
			if (this.accCtrlCtxIDtoModel.containsKey(preferenceCtxID)){
				this.accCtrlCtxIDtoModel.remove(preferenceCtxID);
				this.registry.removeAccCtrlPreference(details);
				try {
					this.storer.storeRegistry(registry);
				} catch (PrivacyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.logging.debug("Unable to store updated registry after deleting AccCtrl preference");
					return false;
				}
				return true;
			}
		}else{
			this.logging.debug("Preference Details not found in registry: "+details.toString());
		}
		return false;
	}
	private void printCacheContentsOnScreen(String string){

		this.logging.debug("*********CACHE CONTENTS START "+string+"**************");
		this.logging.debug(this.registry.toString());
		this.logging.debug("*********CACHE CONTENTS END "+string+"**************");
	}


	public List<PPNPreferenceDetailsBean> getPPNPreferenceDetails(){
		return this.registry.getPPNPreferenceDetails();
	}

	public List<IDSPreferenceDetailsBean> getIDSPreferenceDetails(){
		return this.registry.getIDSPreferenceDetails();	
	}

	public List<DObfPreferenceDetailsBean> getDObfPreferenceDetails() {
		return this.registry.getDObfPreferenceDetails();
	}
	
	public List<AccessControlPreferenceDetailsBean> getAccCtrlPreferenceDetails(){
		return this.registry.getAccCtrlPreferenceDetails();
	}







}


