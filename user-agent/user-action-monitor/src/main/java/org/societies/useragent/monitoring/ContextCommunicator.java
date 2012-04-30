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

import java.io.IOException;
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
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
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
		String key = action.getServiceID()+action.getparameterName();
		if(mappings.containsKey(key)){  //already has service attribute
			//update attribute
			CtxAttributeIdentifier attrID = mappings.get(key);
			try {
				ctxBroker.updateAttribute(attrID, action);
			} catch (CtxException e) {
				e.printStackTrace();
			}
		}else{
			//check context second for ctx attribute to update
			try {
				//get cssOperator (Person)
				//Future<IndividualCtxEntity> cssOperator = ctxBroker.retrieveCssOperator();
				Future<List<CtxEntityIdentifier>> futureServiceEntities = ctxBroker.lookupEntities(
						CtxEntityTypes.SERVICE, 
						CtxAttributeTypes.ID, 
						action.getServiceID(), 
						action.getServiceID());
				List<CtxEntityIdentifier> serviceEntities = futureServiceEntities.get();
				if(serviceEntities.size() > 0){  //service entity with matching serviceID found
					CtxEntityIdentifier serviceEntityId = serviceEntities.get(0);
					CtxEntity serviceEntity = (CtxEntity)ctxBroker.retrieve(serviceEntityId);
					Set<CtxAttribute> serviceAttributes = serviceEntity.getAttributes();
					
					//search for param attribute under entity and update
					boolean found = false;
					for(CtxAttribute nextAttr: serviceAttributes){
						if(nextAttr.getType().equals(action.getparameterName())){
							found = true;
							ctxBroker.updateAttribute(nextAttr.getId(), action);
							
							//update mappings with new key and CtxAttrIdentifier
							mappings.put(key, nextAttr.getId());
						}
					}
					
					//create new attribute if no matching already exists
					if(!found){
						//create new attribute for action with type action.getParameterName()
						Future<CtxAttribute> futureParamAttr = ctxBroker.createAttribute(serviceEntityId, action.getparameterName());
						CtxAttribute newParamAttr = futureParamAttr.get();
						byte[] paramBlob = SerialisationHelper.serialise(action);
						newParamAttr.setBinaryValue(paramBlob);
						
						//set history tuples on param attribute
						ctxBroker.setHistoryTuples(newParamAttr.getId(), snpshtMgr.getSnapshot(newParamAttr.getId()));

						//update mappings with new key and CtxAttrIdentifier
						mappings.put(key, newParamAttr.getId());
					}

				}else{  //no entity yet exists for this serviceID
					//create new service entity with type CtxEntityTypes.SERVICE
					Future<CtxEntity> futureServiceEntity = ctxBroker.createEntity(CtxEntityTypes.SERVICE);
					CtxEntity newServiceEntity = futureServiceEntity.get();

					//create new ID attribute with type: CtxAttributeTypes.ID and value: action.getServiceID
					Future<CtxAttribute> futureIDAttr = ctxBroker.createAttribute(newServiceEntity.getId(), CtxAttributeTypes.ID);
					CtxAttribute newIDAttr = futureIDAttr.get();
					byte[] idBlob = SerialisationHelper.serialise(action.getServiceID());
					newIDAttr.setBinaryValue(idBlob);

					//create new attribute for action with type action.getParameterName()
					Future<CtxAttribute> futureParamAttr = ctxBroker.createAttribute(newServiceEntity.getId(), action.getparameterName());
					CtxAttribute newParamAttr = futureParamAttr.get();
					byte[] paramBlob = SerialisationHelper.serialise(action);
					newParamAttr.setBinaryValue(paramBlob);

					//set history tuples on param attribute
					ctxBroker.setHistoryTuples(newParamAttr.getId(), snpshtMgr.getSnapshot(newParamAttr.getId()));

					//update mappings with new key and CtxAttrIdentifier
					mappings.put(key, newParamAttr.getId());
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (CtxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateUID(IIdentity owner){

	}
}
