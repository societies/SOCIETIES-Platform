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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.RegistryBean;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;



/**
 * @author Elizabeth
 *
 */
public class PreferenceStorer {

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private final ICtxBroker ctxBroker;
	private final IIdentityManager idMgr;


	public PreferenceStorer(ICtxBroker broker, IIdentityManager idMgr){
		this.ctxBroker = broker;
		this.idMgr = idMgr;	
	}


	public void deletePreference(CtxIdentifier id){
		CtxAttribute attrPreference;
		try {
			System.out.println("Deleting: "+id.toUriString());
			attrPreference = (CtxAttribute) ctxBroker.retrieve(id).get();
			if (attrPreference == null){
				this.logging.debug("Cannot delete preference. Doesn't exist: "+id.toUriString());
				
			}else{
				ctxBroker.remove(id);
			}
		} catch (CtxException e) {
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
	/*	public boolean storeExisting(CtxIdentifier id, IPrivacyPreferenceTreeModel p){
		this.logging.debug("Request to store preference to id:"+id.toUriString());
		try {
			Future<CtxAttribute> futureAttr;
			CtxAttribute attr = null;
			if (p instanceof PPNPrivacyPreferenceTreeModel){
				PPNPrivacyPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toPPNPrivacyPreferenceTreeModelBean((PPNPrivacyPreferenceTreeModel) p);
				futureAttr = ctxBroker.updateAttribute(((CtxAttributeIdentifier) id), SerialisationHelper.serialise(bean));
				attr = futureAttr.get();
			}
			else if (p instanceof IDSPrivacyPreferenceTreeModel){
				IDSPrivacyPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toIDSPreferenceTreeModelBean((IDSPrivacyPreferenceTreeModel) p);
				futureAttr = ctxBroker.updateAttribute(((CtxAttributeIdentifier) id), SerialisationHelper.serialise(bean));
				attr = futureAttr.get();
			}



			else if (p instanceof DObfPreferenceTreeModel){
				DObfPrivacyPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toDObfPrivacyPreferenceTreeModelBean((DObfPreferenceTreeModel) p);
				futureAttr = ctxBroker.updateAttribute((CtxAttributeIdentifier) id, SerialisationHelper.serialise(bean));
				attr = futureAttr.get();
			}

			else if (p instanceof AccessControlPreferenceTreeModel){
				AccessControlPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toAccessControlPreferenceTreeModelBean((AccessControlPreferenceTreeModel) p);
				futureAttr = ctxBroker.updateAttribute((CtxAttributeIdentifier) id, SerialisationHelper.serialise(bean));
				attr = futureAttr.get();
			}
			if (null==attr){
				this.logging.debug("Id doesn't exist in DB. Returning error");
				return false;	
			}
			this.logging.debug("Updated attribute in DB for id: "+id.toUriString());
			return true;

		} catch (CtxException e) {
			this.logging.debug("Error while updating preference in db for id"+id.toUriString());
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			this.logging.debug("Error while updating preference in db for id"+id.toUriString());
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			this.logging.debug("Error while updating preference in db for id"+id.toUriString());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}*/

	public boolean storeExisting(CtxIdentifier id, PrivacyPreferenceTreeModelBean p){
		this.logging.debug("Request to store preference to id:"+id.toUriString());
		try {
			Future<CtxAttribute> futureAttr;
			CtxAttribute attr = null;	
			futureAttr = ctxBroker.updateAttribute(((CtxAttributeIdentifier) id), SerialisationHelper.serialise(p));
			attr = futureAttr.get();

			if (null==attr){
				this.logging.debug("Id doesn't exist in DB. Returning error");
				return false;	
			}
			this.logging.debug("Updated attribute in DB for id: "+id.toUriString());
			return true;

		} catch (CtxException e) {
			this.logging.debug("Error while updating preference in db for id"+id.toUriString());
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			this.logging.debug("Error while updating preference in db for id"+id.toUriString());
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			this.logging.debug("Error while updating preference in db for id"+id.toUriString());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	private byte[] toByteArray(Object obj){
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush(); 
			oos.close(); 
			bos.close();
			this.logging.debug("Trying to store preference of size: "+bos.size());
			return bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return null;
	}

	public CtxAttributeIdentifier storeNewPreference(PrivacyPreferenceTreeModelBean bean, String key) throws PrivacyException{



		//iptm.setLastModifiedDate(new Date());


		try {
			List<CtxIdentifier> ctxIDs = ctxBroker.lookup(CtxModelType.ENTITY, CtxTypes.PRIVACY_PREFERENCE).get();
			this.logging.debug("Found : "+ctxIDs.size()+" PRIVACY_PREFERENCE entities");
			if (ctxIDs.size()==0){
				//Preference Entity doesn't exist for this identity so we're going to check if an association exists of type hasPreferences

				List<CtxIdentifier> assocCtxIDs = ctxBroker.lookup(CtxModelType.ASSOCIATION, CtxTypes.HAS_PRIVACY_PREFERENCES).get();
				this.logging.debug("Found : "+assocCtxIDs.size()+" Association entities");
				CtxAssociation assoc = null;
				if (assocCtxIDs.size()==0){
					//Has_Preferences association doesn't exist for this identity, so we're going to check if the Person Entity exists and create the association
					IIdentity userId = this.idMgr.getThisNetworkNode();
					CtxEntity person = ctxBroker.retrieveIndividualEntity(userId).get();


					if (person==null){
						this.logging.debug("Error retrieving Person Entity: aborting storing and exiting");
						this.logging.debug("Error retrieving Person Entity: aborting storing and exiting");
						throw new PrivacyException("Error retrieving Person entity while attempting to store a preference");
					}else{
						this.logging.debug("Found person entity");
					}

					//creating the association
					assoc = ctxBroker.createAssociation(CtxTypes.HAS_PRIVACY_PREFERENCES).get();
					this.logging.debug("Created Association: "+assoc.getId().toString());
							//adding the person as parent to the association
							assoc.setParentEntity(person.getId());

					//update model
					ctxBroker.update(assoc);

				}else{ //associations exist
					if (assocCtxIDs.size()>1){
						this.logging.debug("There's more than one association of type hasPreferences for private DPI\nStoring Preference under the first in the list");
					}

					//use first association in the list
					assoc = (CtxAssociation) ctxBroker.retrieve(assocCtxIDs.get(0));
					this.logging.debug("Retrieved association");
				}

				//create the PRIVACY_PREFERENCE entity
				CtxEntity preferenceEntity = ctxBroker.createEntity(CtxTypes.PRIVACY_PREFERENCE).get();
				this.logging.debug("Created preference entity");
				//add the entity as a child to the asosciation
				assoc.addChildEntity(preferenceEntity.getId());
				
				//update the model
				ctxBroker.update(assoc);
				//JOptionPane.showMessageDialog(null, "key is: "+key);
				//JOptionPane.showMessageDialog(null, "Created entity: id: "+preferenceEntity.getId()+" \nand going to create attribute with key:"+key);
				//create the context attribute to store the preference as a blob
				CtxAttribute attr = ctxBroker.createAttribute(preferenceEntity.getId(), key).get();
				
				//store the attribute
				attr = ctxBroker.updateAttribute(attr.getId(), SerialisationHelper.serialise(bean)).get();
				this.logging.debug("Created attribute : "+attr.getId());
				this.logging.debug("Created attribute: "+attr.getType());
				return attr.getId();
			}else{
				if (ctxIDs.size()>1){
					this.logging.debug("There's more than one entity of type Privacy_Preference\nStoring preference under the first entity in the list");
				}
				CtxIdentifier preferenceEntityID = ctxIDs.get(0);
				this.logging.debug("found preference entity: "+preferenceEntityID);
				CtxAttribute attr = ctxBroker.createAttribute((CtxEntityIdentifier) preferenceEntityID, key).get();
				this.logging.debug("Created attribute: "+attr.getId());

				attr = ctxBroker.updateAttribute(attr.getId(), SerialisationHelper.serialise(bean)).get();

				if(attr==null){
					throw new PrivacyException("Error updating context attribute while storing new preference");
				}

				/*				else if (iptm instanceof IDSPrivacyPreferenceTreeModel){
					IDSPrivacyPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toIDSPreferenceTreeModelBean((IDSPrivacyPreferenceTreeModel) iptm);
					attr = ctxBroker.updateAttribute(attr.getId(), SerialisationHelper.serialise(bean)).get();
				}
				else if (iptm instanceof DObfPreferenceTreeModel){
					//TODO:!!!
				}*/
				this.logging.debug("Created attribute: "+attr.getType());
				return attr.getId();
			}
		} catch (CtxException e) {
			this.logging.debug("Unable to store preference: "+key);
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public void storeRegistry(Registry registry) throws PrivacyException{
		try {
			List<CtxIdentifier> attrList = ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxTypes.PRIVACY_PREFERENCE_REGISTRY).get();

			if (attrList.size()>0){
				CtxIdentifier identifier = attrList.get(0);
				CtxAttribute attr = (CtxAttribute) ctxBroker.retrieve(identifier).get();

				RegistryBean bean = registry.toRegistryBean();
				attr = ctxBroker.updateAttribute(attr.getId(), SerialisationHelper.serialise(bean)).get();
				if (null==attr){
					this.logging.debug("Preference Registry not updated.");
					throw new PrivacyException("Unable to retrieve administrating CSS entity for storing the Privacy preference registry");
				}else{
					this.logging.debug("Successfully updated preference registry for private DPI");
				}
			}else{
				this.logging.debug("PreferenceRegistry not found in DB. Creating new registry");

				/*					Future<List<CtxIdentifier>> futurePersonCtxIDs = ctxBroker.lookup(CtxModelType.ENTITY, "PERSON");
					List<CtxIdentifier> personCtxIDs = futurePersonCtxIDs.get();

					if (personCtxIDs.size()==0){
						this.logging.debug("CtxEntity Person does not exist. aborting storing and exiting");
					}

					Future<CtxModelObject> futurePerson = ctxBroker.retrieve(personCtxIDs.get(0));*/
				IIdentity userId = this.idMgr.getThisNetworkNode();
				CtxEntity person = ctxBroker.retrieveIndividualEntity(userId).get();

				if (person==null){
					this.logging.debug("Error retrieving Person Entity: aborting storing and exiting");
					throw new PrivacyException("Unable to retrieve administrating CSS entity for storing the Privacy preference registry");
				}

				CtxAttribute attr = ctxBroker.createAttribute(person.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY).get();
				RegistryBean bean = registry.toRegistryBean();
				attr.setBinaryValue(SerialisationHelper.serialise(bean));
				ctxBroker.update(attr);


				if (null==attr){
					this.logging.debug("Preference Registry not updated.");
					throw new PrivacyException("Unable to update privacy registry");
				}else{
					this.logging.debug("Successfully updated preference registry for private DPI");
				}
			}

		} catch (CtxException e) {
			this.logging.debug("Exception while storing PreferenceRegistry to DB for private DPI");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*	private void calculateSizeOfObject(Object o){
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(o);
			oos.flush(); 
			oos.close(); 
			bos.close();
			this.logging.debug("Trying to store preference of size: "+bos.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}*/

}

