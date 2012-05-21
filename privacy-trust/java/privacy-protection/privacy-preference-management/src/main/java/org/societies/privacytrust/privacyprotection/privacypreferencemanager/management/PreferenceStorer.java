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

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;



/**
 * @author Elizabeth
 *
 */
public class PreferenceStorer {

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private final ICtxBroker ctxBroker;


	public PreferenceStorer(ICtxBroker broker){
		this.ctxBroker = broker;	
	}


	public void deletePreference(CtxIdentifier id){
		CtxAttribute attrPreference;
		try {
			attrPreference = (CtxAttribute) ctxBroker.retrieve(id);
			if (attrPreference == null){
				this.logging.debug("Cannot delete preference. Doesn't exist");
			}else{
				ctxBroker.remove(id);
			}
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	public boolean storeExisting(CtxIdentifier id, IPrivacyPreferenceTreeModel p){
		this.logging.debug("Request to store preference to id:"+id.toUriString());
		try {
			
			Future<CtxAttribute> futureAttr = ctxBroker.updateAttribute(((CtxAttributeIdentifier) id), SerialisationHelper.serialise(p));
			
			CtxAttribute attr = futureAttr.get();
			
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

	public CtxAttributeIdentifier storeNewPreference(IPrivacyPreferenceTreeModel iptm, String key){
		//iptm.setLastModifiedDate(new Date());


		try {
			List<CtxIdentifier> ctxIDs = ctxBroker.lookup(CtxModelType.ENTITY, CtxTypes.PRIVACY_PREFERENCE).get();
			if (ctxIDs.size()==0){
				//Preference Entity doesn't exist for this dpi so we're going to check if an association exists of type hasPreferences

				List<CtxIdentifier> assocCtxIDs = ctxBroker.lookup(CtxModelType.ASSOCIATION, CtxTypes.HAS_PRIVACY_PREFERENCES).get();

				CtxAssociation assoc = null;
				if (assocCtxIDs.size()==0){
					//Has_Preferences association doesn't exist for this dpi, so we're going to check if the Person Entity exists and create the association
					CtxEntity person = (CtxEntity) ctxBroker.retrieveCssOperator().get();
					
					/*Future<List<CtxIdentifier>> futurePersonCtxIDs = ctxBroker.lookup(CtxModelType.ENTITY, "PERSON");
					List<CtxIdentifier> personCtxIDs = futurePersonCtxIDs.get();
					
					if (personCtxIDs.size()==0){
						this.logging.debug("CtxEntity Person does not exist. aborting storing and exiting");
						return null;
					}
					
					Future<CtxModelObject> futurePerson = ctxBroker.retrieve(personCtxIDs.get(0));
					CtxEntity person = (CtxEntity) futurePerson.get();*/
					
					if (person==null){
						this.logging.debug("Error retrieving Person Entity: aborting storing and exiting");
						return null;
					}

					assoc = ctxBroker.createAssociation(CtxTypes.HAS_PRIVACY_PREFERENCES).get();
					assoc.setParentEntity(person.getId());
		
					ctxBroker.update(assoc);

				}else{
					if (assocCtxIDs.size()>1){
						this.logging.debug("There's more than one association of type hasPreferences for private DPI\nStoring Preference under the first in the list");
					}
					assoc = (CtxAssociation) ctxBroker.retrieve(assocCtxIDs.get(0));
				}

				CtxEntity preferenceEntity = ctxBroker.createEntity(CtxTypes.PRIVACY_PREFERENCE).get();
				assoc.addChildEntity(preferenceEntity.getId());
				ctxBroker.update(assoc);
				//JOptionPane.showMessageDialog(null, "key is: "+key);
				CtxAttribute attr = ctxBroker.createAttribute(preferenceEntity.getId(), key).get();
				ctxBroker.updateAttribute(attr.getId(), SerialisationHelper.serialise(iptm));
				this.logging.debug("Created attribute: "+attr.getType());
				return attr.getId();
			}else{
				if (ctxIDs.size()>1){
					this.logging.debug("There's more than one entity of type Privacy_Preference\nStoring preference under the first entity in the list");
				}
				CtxIdentifier preferenceEntityID = ctxIDs.get(0);
				CtxAttribute attr = ctxBroker.createAttribute((CtxEntityIdentifier) preferenceEntityID, key).get();
				ctxBroker.updateAttribute(attr.getId(), SerialisationHelper.serialise(iptm));
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
	
	public void storeRegistry(Registry registry){
		try {
			List<CtxIdentifier> attrList = ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxTypes.PRIVACY_PREFERENCE_REGISTRY).get();
			
				if (attrList.size()>0){
					CtxIdentifier identifier = attrList.get(0);
					CtxAttribute attr = (CtxAttribute) ctxBroker.retrieve(identifier);
					attr = ctxBroker.updateAttribute(attr.getId(), SerialisationHelper.serialise(registry)).get();
					if (null==attr){
						this.logging.debug("Preference Registry not updated.");
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
					Future<IndividualCtxEntity> futurePerson = ctxBroker.retrieveCssOperator();
					CtxEntity person = (CtxEntity) futurePerson.get();
					
					if (person==null){
						this.logging.debug("Error retrieving Person Entity: aborting storing and exiting");
					}
					
					CtxAttribute attr = ctxBroker.createAttribute(person.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY).get();
					attr.setBinaryValue(SerialisationHelper.serialise(registry));
					ctxBroker.update(attr);
					
					
					if (null==attr){
						this.logging.debug("Preference Registry not updated.");
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

