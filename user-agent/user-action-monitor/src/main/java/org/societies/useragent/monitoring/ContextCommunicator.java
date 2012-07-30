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
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.personalisation.model.IAction;

public class ContextCommunicator {

	private static Logger LOG = LoggerFactory.getLogger(ContextCommunicator.class);
	ICtxBroker ctxBroker;
	SnapshotManager snpshtMgr;
	Hashtable<String, CtxAttributeIdentifier> mappings;

	public ContextCommunicator(ICtxBroker ctxBroker, IIdentity myCssID){
		this.ctxBroker = ctxBroker;
		snpshtMgr = new SnapshotManager(ctxBroker, myCssID);
		mappings = new Hashtable<String, CtxAttributeIdentifier>();  //quick lookup for serviceId.paramName -> ctxAttrIdentifier
	}

	public void updateHistory(IIdentity owner, IAction action){
		//check cache first for ctxAttrIdentifier to update
		URI serviceID = action.getServiceID().getIdentifier();
		String parameterName = action.getparameterName();
		String key = serviceID+"|"+parameterName;
		if(mappings.containsKey(key)){  //already has service attribute
			LOG.debug("Mapping exists for key: "+key);
			//update attribute
			CtxAttributeIdentifier attrID = mappings.get(key);
			try {
				ctxBroker.updateAttribute(attrID, SerialisationHelper.serialise(action));
			} catch (CtxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			LOG.debug("Mapping doesn't yet exist for key: "+key);
			//check context second for ctx attribute to update
			try {
				//get cssOperator (Person)
				IndividualCtxEntity cssOperator = ctxBroker.retrieveIndividualEntity(owner).get();
				if(cssOperator != null){
					LOG.debug("Retrieved PERSON entity with ID: "+cssOperator.getId());

					//get USES_SERVICE associations for this person entity
					Set<CtxAssociationIdentifier> usesServiceAssocIDs = cssOperator.getAssociations(CtxAssociationTypes.USES_SERVICES);
					if(usesServiceAssocIDs.size() > 0){  //USES_SERVICE associations found!
						LOG.debug("Found USES_SERVICES association under PERSON entity");
						CtxAssociation usesServiceAssoc = (CtxAssociation)ctxBroker.retrieve(usesServiceAssocIDs.iterator().next()).get();
						
						//Get SERVICE entities under USES_SERVICE association
						Set<CtxEntityIdentifier> serviceEntityIDs = usesServiceAssoc.getChildEntities();
											
						//find SERVICE entity with correct ID
						List<CtxEntityIdentifier> serviceEntityIDsList = new ArrayList<CtxEntityIdentifier>(serviceEntityIDs);
						List<CtxEntityIdentifier> returnedServiceIDs = ctxBroker.lookupEntities(serviceEntityIDsList, CtxAttributeTypes.ID, serviceID).get();
						if(returnedServiceIDs.size() > 0){  //SERVICE entity with this serviceID found!
							LOG.debug("Found SERVICE entity with serviceID: "+serviceID);
							
							CtxEntity serviceEntity = (CtxEntity)ctxBroker.retrieve(returnedServiceIDs.get(0)).get();
							
							//Get HAS_PARAMETER associations for this service entity
							Set<CtxAssociationIdentifier> hasParamAssocIDs = serviceEntity.getAssociations(CtxAssociationTypes.HAS_PARAMETERS);
							if(hasParamAssocIDs.size() > 0){  //HAS_PARAMETER associations found!
								LOG.debug("Found HAS_PARAMETER association under SERVICE entity");
								CtxAssociation hasParamAssoc = (CtxAssociation)ctxBroker.retrieve(hasParamAssocIDs.iterator().next()).get();
								
								//Get SERVICE_PARAMETER entities under HAS_PARAMETER association
								Set<CtxEntityIdentifier> paramEntityIDs = hasParamAssoc.getChildEntities();
								
								//find SERVICE_PARAMETER entity with correct name
								List<CtxEntityIdentifier> paramEntityIDsList = new ArrayList<CtxEntityIdentifier>(paramEntityIDs);
								List<CtxEntityIdentifier> returnedParamIDs = ctxBroker.lookupEntities(paramEntityIDsList, CtxAttributeTypes.PARAMETER_NAME, parameterName).get();
								if(returnedParamIDs.size() > 0){  //SERVICE_PARAMETER entity with this name found!
									LOG.debug("Found SERVICE_PARAMETER entity with parameterName: "+parameterName);
									CtxEntity parameterEntity = (CtxEntity)ctxBroker.retrieve(returnedParamIDs.get(0)).get();
									
									//Get LAST_ACTION attribute
									Set<CtxAttribute> returnedAttributes = parameterEntity.getAttributes(CtxAttributeTypes.LAST_ACTION);
									CtxAttribute lastActionAttr = returnedAttributes.iterator().next();
									
									//update LAST_ACTION value
									LOG.debug("Updating LAST_ACTION attribute with action");
									ctxBroker.updateAttribute(lastActionAttr.getId(), SerialisationHelper.serialise(action));
									
									//update mappings
									LOG.debug("Updating mappings table with key: "+key+" and attributeID: "+lastActionAttr.getId());
									mappings.put(key, lastActionAttr.getId());
									
								}else{  //no SERVICE_PARAMETER entity found :(
									
									//CREATING NEW SERVICE_PARAMETER
									//setting as child of HAS_PARAMETER association
									//adding PARAMETER_NAME attribute
									//adding LAST_ACTION attribute
									//adding to mappings with key
									createServiceParameter(key, hasParamAssoc, parameterName, action);
								}
								
							}else{  //no HAS_PARAMETER associations found :(
								
								//creating new HAS_PARAMETER association and setting SERVICE entity as parent
								CtxAssociation newHasParamAssoc = createHasParameterAssociation(serviceEntity);
								//creating new SERVICE_PARAMETER entity, setting as child of HAS_PARAMETER association
								//adding ID and PARAMETER_NAME attributes
								createServiceParameter(key, newHasParamAssoc, parameterName, action);
								
							}
							
						}else{  //no SERVICE entity with this serviceID found :(
							
							//creating new SERVICE entity
							CtxEntity newServiceEntity = createServiceEntity(usesServiceAssoc, serviceID);
							//creating new HAS_PARAMETER association and setting service entity as parent
							CtxAssociation newHasParamAssoc = createHasParameterAssociation(newServiceEntity);
							//creating new SERVICE_PARAMETER entity, setting as child of HAS_PARAMETER association
							//adding ID and PARAMETER_NAME attributes
							createServiceParameter(key, newHasParamAssoc, parameterName, action);
						}
						
					}else{  //no USES_SERVICE associations found :(
						
						//creating new USE_SERVICE ASSOCATION
						CtxAssociation newUsesServiceAssoc = createUsesServiceAssociation(cssOperator);
						//creating new SERVICE entity
						CtxEntity newServiceEntity = createServiceEntity(newUsesServiceAssoc, serviceID);
						//creating new HAS_PARAMETER association and setting service entity as parent
						CtxAssociation newHasParamAssoc = createHasParameterAssociation(newServiceEntity);
						//creating new SERVICE_PARAMETER entity, setting as child of HAS_PARAMETER association
						//adding ID and PARAMETER_NAME attributes
						createServiceParameter(key, newHasParamAssoc, parameterName, action);
					}
				}else{
					LOG.error("Could not retrieve Person EntityIdentifier from JID");
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
	
	private CtxAssociation createUsesServiceAssociation(CtxEntity parent){
		CtxAssociation usesServiceAssoc = null;
		try {
			//create USES_SERVICE association
			LOG.debug("Creating USE_SERVICES association with parent: "+parent.getType());
			usesServiceAssoc = ctxBroker.createAssociation(CtxAssociationTypes.USES_SERVICES).get();
			
			//set parent entity
			usesServiceAssoc.setParentEntity(parent.getId());
			ctxBroker.update(usesServiceAssoc);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return usesServiceAssoc;
	}
	
	private CtxEntity createServiceEntity(CtxAssociation usesServiceAssoc, URI serviceID){
		CtxEntity serviceEntity = null;
		try {
			//create new SERVICE entity
			LOG.debug("Creating SERVICE entity and adding as child to association: "+usesServiceAssoc.getType());
			serviceEntity = ctxBroker.createEntity(CtxEntityTypes.SERVICE).get();
			
			//add as child to USES_SERVICE association
			usesServiceAssoc.addChildEntity(serviceEntity.getId());
			ctxBroker.update(usesServiceAssoc);
			
			//create new ID attribute, update and add to entity
			LOG.debug("Creating ID attribute under SERVICE entity");
			CtxAttribute newIDAttr = ctxBroker.createAttribute(serviceEntity.getId(), CtxAttributeTypes.ID).get();
			LOG.debug("Setting value of ID attribute to: "+serviceID);
			ctxBroker.updateAttribute(newIDAttr.getId(), SerialisationHelper.serialise(serviceID));
			LOG.debug("Testing serialisation and deserialisation");
			byte[] serialised = SerialisationHelper.serialise(serviceID);
			LOG.debug("SERIALISED: "+serialised);
			try {
				URI deserialised = (URI)SerialisationHelper.deserialise(serialised, this.getClass().getClassLoader());
				LOG.debug("DESERIALISED: "+deserialised);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
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
		
		return serviceEntity;
	}
	
	private CtxAssociation createHasParameterAssociation(CtxEntity parentEntity){
		CtxAssociation hasParameterAssoc = null;
		try {
			//create new HAS_PARAMETER association
			LOG.debug("Creating HAS_PARAMETER association with parent: "+parentEntity.getType());
			hasParameterAssoc = ctxBroker.createAssociation(CtxAssociationTypes.HAS_PARAMETERS).get();
			
			//set parent entity
			hasParameterAssoc.setParentEntity(parentEntity.getId());
			ctxBroker.update(hasParameterAssoc);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return hasParameterAssoc;
	}
	
	private void createServiceParameter(String key, CtxAssociation hasParameterAssoc, String parameterName, IAction lastAction){
		try {
			//create new SERVICE_PARAMETER entity
			LOG.debug("Creating SERVICE_PARAMETER entity and adding as child to association: "+hasParameterAssoc.getType());
			CtxEntity serviceParamEntity = ctxBroker.createEntity(CtxEntityTypes.SERVICE_PARAMETER).get();
			
			//add as child to HAS_PARAMETER association
			hasParameterAssoc.addChildEntity(serviceParamEntity.getId());	
			ctxBroker.update(hasParameterAssoc);
			
			//create new PARAMETER_NAME attribute, update and add to entity
			LOG.debug("Creating PARAMETER_NAME attribute under SERVICE_PARAMETER entity with value: "+parameterName);
			CtxAttribute newParamAttr = ctxBroker.createAttribute(serviceParamEntity.getId(), CtxAttributeTypes.PARAMETER_NAME).get();
			ctxBroker.updateAttribute(newParamAttr.getId(), parameterName);
			
			//create new LAST_ACTION attribute, update and add to entity
			LOG.debug("Creating LAST_ACTION attribute under SERVICE_PARAMETER entity with value: "+lastAction);
			CtxAttribute newLastActionAttr = ctxBroker.createAttribute(serviceParamEntity.getId(), CtxAttributeTypes.LAST_ACTION).get();
			ctxBroker.setHistoryTuples(newLastActionAttr.getId(), snpshtMgr.getSnapshot(newLastActionAttr.getId()).getIDList());
			ctxBroker.updateAttribute(newLastActionAttr.getId(), SerialisationHelper.serialise(lastAction));
			
			//update mappings
			LOG.debug("Updating mappings table with key: "+key+" and attributeID: "+newLastActionAttr.getId());
			mappings.put(key, newLastActionAttr.getId());
			
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateUID(IIdentity owner, String myDeviceID){
		try {
			List<CtxIdentifier> attrIds = ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.UID).get();
			if(attrIds.size() > 0){ //found existing UID - update
				CtxAttributeIdentifier uidAttrID = (CtxAttributeIdentifier)attrIds.get(0);
				ctxBroker.updateAttribute(uidAttrID, myDeviceID);
			}else{  //no existing UID - create and populate
				CtxEntity personEntity = ctxBroker.retrieveIndividualEntity(owner).get();
				if(personEntity != null){
					ctxBroker.createAttribute(personEntity.getId(), CtxAttributeTypes.UID);
				}else{
					LOG.error("Could not retrieve Person EntityIdentifier from JID");
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
