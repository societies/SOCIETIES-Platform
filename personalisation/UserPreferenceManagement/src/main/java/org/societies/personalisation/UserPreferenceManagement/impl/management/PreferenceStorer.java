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
package org.societies.personalisation.UserPreferenceManagement.impl.management;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;



/**
 * @author Elizabeth
 *
 */
public class PreferenceStorer {

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private final ICtxBroker broker;


	public PreferenceStorer(ICtxBroker broker){
		this.broker = broker;	
	}


	public void deletePreference(Identity userId, CtxIdentifier id){
		CtxAttribute attrPreference;
		try {
			attrPreference = (CtxAttribute) broker.retrieve(id);
			if (attrPreference == null){
				this.logging.debug("Cannot delete preference. Doesn't exist");
			}else{
				broker.remove(id);
			}
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	public boolean storeExisting(Identity userId, CtxIdentifier id, IPreferenceTreeModel p){
		try {
			p.setLastModifiedDate(new Date());
			CtxAttribute attrPreference = (CtxAttribute) broker.retrieve(id);
			if (attrPreference==null){
				return false;
			}

			byte[] bytearray = this.toByteArray(p);
			if (null!=bytearray){
				attrPreference.setBinaryValue(bytearray);
				broker.update(attrPreference);
				return true;
			}
			return false;
		} catch (CtxException e) {
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


	public CtxIdentifier storeNewPreference(Identity userId, IPreferenceTreeModel iptm, String key){
		iptm.setLastModifiedDate(new Date());


		try {
			//the original code has the lookup based on an identity. this has to be updated accordingly if the contextAPI is to change and 
			//methods will include the extra identity parameter for the user (data owner)
			Future<List<CtxIdentifier>> futureCtxIDs = broker.lookup(/*userId,*/ CtxModelType.ENTITY, "PREFERENCE"); 
			List<CtxIdentifier> ctxIDs = futureCtxIDs.get();
			if (ctxIDs.size()==0){
				//Preference Entity doesn't exist for this dpi so we're going to check if an association exists of type hasPreferences

				Future<List<CtxIdentifier>> futureAssocCtxIDs =broker.lookup(/*userId,*/ CtxModelType.ASSOCIATION, CtxModelTypes.HAS_PREFERENCES); 
				List<CtxIdentifier> assocCtxIDs = futureAssocCtxIDs.get();

				CtxAssociation assoc = null;
				if (assocCtxIDs.size()==0){
					//Has_Preferences association doesn't exist for this dpi, so we're going to check if the Person Entity exists and create the association
					/*CtxEntity person = broker.retrieveOperator(userId);*/
					Future<List<CtxIdentifier>> futurePersonCtxIDs = broker.lookup(CtxModelType.ENTITY, "PERSON");
					List<CtxIdentifier> personCtxIDs = futurePersonCtxIDs.get();
					
					if (personCtxIDs.size()==0){
						this.logging.debug("CtxEntity for operator with userId: "+userId.toString()+" does not exist. aborting storing and exiting");
						return null;
					}
					
					Future<CtxModelObject> futurePerson = broker.retrieve(personCtxIDs.get(0));
					CtxEntity person = (CtxEntity) futurePerson.get();
					if (person==null){
						this.logging.debug("CtxEntity for operator with userId: "+userId.toString()+" does not exist. aborting storing and exiting");
						return null;
					}

					Future<CtxAssociation> futureAssoc = broker.createAssociation(/*userId, */CtxModelTypes.HAS_PREFERENCES);
					assoc = futureAssoc.get();
					assoc.setParentEntity(person.getId());
					broker.update(assoc);

				}else{
					if (assocCtxIDs.size()>1){
						this.logging.debug("There's more than one association of type hasPreferences for userId:"+userId.toString()+"\nStoring Preference under the first in the list");
					}
					assoc = (CtxAssociation) broker.retrieve(assocCtxIDs.get(0));
				}

				CtxEntity preferenceEntity = (broker.createEntity(/*userId, */CtxModelTypes.PREFERENCE)).get();
				assoc.addEntity(preferenceEntity.getId());
				broker.update(assoc);
				CtxAttribute attr = (broker.createAttribute(preferenceEntity.getId(), key)).get();
				attr.setBinaryValue(this.toByteArray(iptm));
				this.logging.debug("Created attribute: "+attr.getType());
				return attr.getId();
			}else{
				if (ctxIDs.size()>1){
					this.logging.debug("There's more than one entity of type Preference for userId: "+userId.toString()+"\nStoring preference under the first in the list");
				}
				CtxIdentifier preferenceEntityID = ctxIDs.get(0);
				CtxAttribute attr = (broker.createAttribute((CtxEntityIdentifier) preferenceEntityID, key)).get();
				attr.setBinaryValue(this.toByteArray(iptm));
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
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	public void storeRegistry(Identity userId, Registry registry){
		try {
			List<CtxIdentifier> attrList = (broker.lookup(/*userId, */CtxModelType.ATTRIBUTE, CtxModelTypes.PREFERENCE_REGISTRY)).get();
			
				if (attrList.size()>0){
					CtxIdentifier identifier = attrList.get(0);
					CtxAttribute attr =  (CtxAttribute) (broker.retrieve(identifier)).get();
					attr.setBinaryValue(this.toByteArray(registry));
					
					broker.update(attr);					
					this.logging.debug("Successfully updated preference registry for userId: "+userId.toString());
				}else{
					this.logging.debug("PreferenceRegistry not found in DB for userId:"+userId.toString()+". Creating new registry");
					
					Future<List<CtxIdentifier>> futurePersonCtxIDs = broker.lookup(CtxModelType.ENTITY, "PERSON");
					List<CtxIdentifier> personCtxIDs = futurePersonCtxIDs.get();
					
					if (personCtxIDs.size()==0){
						this.logging.debug("CtxEntity for operator with userId: "+userId.toString()+" does not exist. aborting storing and exiting");
						return ;
					}
					
					//CtxEntity operatorEntity = broker.retrieveOperator(userId);
					
					CtxEntity operatorEntity = (CtxEntity) (broker.retrieve(personCtxIDs.get(0))).get();
					CtxAttribute attr = (broker.createAttribute(operatorEntity.getId(), CtxModelTypes.PREFERENCE_REGISTRY)).get();
					
					
					attr.setBinaryValue(this.toByteArray(registry));
					broker.update(attr);
					this.logging.debug("Successfully stored new preference registry");
				}
			
		} catch (CtxException e) {
			this.logging.debug("Exception while storing PreferenceRegistry to DB for userId:"+userId.toString());
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}

