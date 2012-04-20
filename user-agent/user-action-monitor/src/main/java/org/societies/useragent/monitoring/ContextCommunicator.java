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

package org.societies.useragent.monitoring;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.IAction;

public class ContextCommunicator {

	ICtxBroker ctxBroker;
	SnapshotManager snpshtMgr;
	Hashtable<String, CtxAttributeIdentifier> mappings;

	public ContextCommunicator(ICtxBroker ctxBroker){
		this.ctxBroker = ctxBroker;
		snpshtMgr = new SnapshotManager(ctxBroker);
		mappings = new Hashtable<String, CtxAttributeIdentifier>();  //quick lookup for serviceId.paramName -> ctxAttrIdentifier
	}

	public void updateHistory(IIdentity owner, IAction action){
		//check cache first for ctxAttrIdentifier to update
		String key = action.getServiceID()+"|"+action.getparameterName();
		if(mappings.containsKey(key)){  //already has service attribute
			//confirm snapshot -> check that it is complete, if not then update
			snpshtMgr.confirmSnapshot(key);
			//update attribute
			CtxAttributeIdentifier attrID = mappings.get(key);
			try {
				ctxBroker.updateAttribute(attrID, action.getvalue());
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			//check context second for ctx attribute to update
			try {
				//Look for Entity with serviceId
				Future<List<CtxIdentifier>> futureEntityIDs = ctxBroker.lookup(CtxModelType.ENTITY, action.getServiceID().getIdentifier().toString());
				List<CtxIdentifier> entityIds = futureEntityIDs.get();
				if(entityIds.size() > 0){
					//Get Attribute from Entity with parameter name
					CtxEntityIdentifier entityId = (CtxEntityIdentifier)entityIds.get(0);
					Future<CtxModelObject> futureEntity = ctxBroker.retrieve(entityId);
					CtxEntity serviceEntity = (CtxEntity)futureEntity.get();
					//search for attributes of type parameterName associated to service entity
					Set<CtxAttribute> attributes = serviceEntity.getAttributes(action.getparameterName());
					if(attributes.size() > 0){  //attribute found under this entityId
						CtxAttribute attribute = attributes.iterator().next();
						ctxBroker.updateAttribute(attribute.getId(), action.getvalue());
						mappings.put(key, attribute.getId());
						
					}else{  //no attribute found under this entityId
						//create new attribute for parameterName
						Future<CtxAttribute> futureAttribute = ctxBroker.createAttribute(entityId, action.getparameterName());
						CtxAttribute newAttribute = futureAttribute.get();
						//set history tuples
						ctxBroker.setHistoryTuples(newAttribute.getId(), null); //add list of context snapshot IDs
						//set as recorded
						//populate attribute with action value
						ctxBroker.updateAttribute(newAttribute.getId(), action.getvalue());
						//update mappings
						mappings.put(key, newAttribute.getId());
					}
					
				}else{ //no entities for this serviceId
					//create new entity for serviceId
					Future<CtxEntity> futureEntity = ctxBroker.createEntity(action.getServiceID().toString());
					CtxEntity newEntity = futureEntity.get();
					//create new attribute for parameterName
					Future<CtxAttribute> futureAttribute = ctxBroker.createAttribute(newEntity.getId(), action.getparameterName());
					CtxAttribute newAttribute = futureAttribute.get();
					//set as history
					ctxBroker.setHistoryTuples(newAttribute.getId(), null); //add list of context snapshot IDs
					//set as recorded
					//populate attribute with action value
					ctxBroker.updateAttribute(newAttribute.getId(), action.getvalue());
					//update mappings with new key and CtxAttrIdentifier
					mappings.put(key, newAttribute.getId());
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (CtxException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void updateUID(IIdentity owner){
		
	}
}
