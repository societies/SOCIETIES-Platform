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
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.useragent.monitoring.model.Snapshot;
import org.societies.useragent.monitoring.model.SnapshotsRegistry;

public class SnapshotManager implements CtxChangeEventListener{

	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	/*
	 * DEFAULT SNAPSHOT DEFINITION
	 */
	String[] defaultDef = {
			CtxAttributeTypes.LOCATION_SYMBOLIC, 
			//CtxAttributeTypes.STATUS, 
			//CtxAttributeTypes.TEMPERATURE
		    CtxAttributeTypes.TIME_OF_DAY,
		    CtxAttributeTypes.DAY_OF_WEEK,
		    CtxAttributeTypes.HOUR_OF_DAY
	};
	/*
	 * End of definition
	 */

	private ICtxBroker ctxBroker;
	private SnapshotsRegistry snpshtRegistry;
	private Snapshot defaultSnpsht;
	private IIdentity myCssID;

	private boolean doFix;

	public SnapshotManager(ICtxBroker ctxBroker, IIdentity myCssID){
		this.ctxBroker = ctxBroker;
		this.myCssID = myCssID;

		snpshtRegistry = retrieveSnpshtsRegistry();
		defaultSnpsht = new Snapshot();
		initialiseDefaultSnpsht();
	}

	public Snapshot getSnapshot(CtxAttributeIdentifier primary){
		if (LOG.isDebugEnabled()){
			LOG.debug("Getting context snapshot for primary attribute ID: "+primary);
		}
		Snapshot snapshot = snpshtRegistry.getSnapshot(primary);
		if(snapshot == null){//no existing snapshot mapping to this primary -> create with default snapshot
			if (LOG.isDebugEnabled()){
				LOG.debug("No snapshot is mapped to this primary attribute ID: "+primary+"...returning default snapshot");
			}
			snapshot = defaultSnpsht;
			if (LOG.isDebugEnabled()){
				LOG.debug("Adding new mapping for primary attribute ID with default snapshot to SnapshotRegistry");
			}
			snpshtRegistry.addMapping(primary, snapshot);
			storeReg();
		}
		return snapshot;
	}

	public void updateSnapshot(CtxAttributeIdentifier primary, Snapshot newSnapshot){
		if(snpshtRegistry.getSnapshot(primary) != null){
			snpshtRegistry.updateMapping(primary, newSnapshot);
			storeReg();
		}else{
			LOG.error("Cannot update snapshot as no snapshot exists for this primary attribute identifier: "+primary);
		}
	}

	private void initialiseDefaultSnpsht(){
		if (LOG.isDebugEnabled()){
			LOG.debug("Initialising default snapshot");
		}
		for(int i = 0; i<defaultDef.length; i++){
			String attrType = defaultDef[i];
			//retrieve attribute ID from context
			try {
				//16/11/13: replacing following line 
				//Future<List<CtxIdentifier>> futureAttributes = ctxBroker.lookup(CtxModelType.ATTRIBUTE, attrType);
				//with the following two lines to get the attribute under Person entity. 
				IndividualCtxEntity individualCtxEntity = ctxBroker.retrieveIndividualEntity(myCssID).get();
				List<CtxIdentifier> attributes = ctxBroker.lookup(individualCtxEntity.getId(), CtxModelType.ATTRIBUTE, attrType).get();
				
				if(attributes.size() > 0){
					if (LOG.isDebugEnabled()){
						LOG.debug("Found "+attrType+" attribute in context - adding to snapshot");
					}
					defaultSnpsht.setTypeID(attrType, (CtxAttributeIdentifier) attributes.get(0));
				}else{
					//register for attribute creation event
					defaultSnpsht.addType(attrType);
					if (LOG.isDebugEnabled()){
						LOG.debug("Couldn't find "+attrType+" attribute in context - registering for creation event");
					}
					CtxEntityIdentifier personID = ctxBroker.retrieveIndividualEntity(myCssID).get().getId();
					ctxBroker.registerForChanges(this, personID, attrType);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				e.printStackTrace();
			}
		}
	}

	private SnapshotsRegistry retrieveSnpshtsRegistry(){
		if (LOG.isDebugEnabled()){
			LOG.debug("Retrieving SnapshotRegistry from context");
		}
		SnapshotsRegistry retrievedReg = null;
		try {
			IndividualCtxEntity personEntity = ctxBroker.retrieveIndividualEntity(myCssID).get();
			Set<CtxAttribute> attributes = personEntity.getAttributes(CtxAttributeTypes.SNAPSHOT_REG);
			//Future<List<CtxIdentifier>> futureAttributes = ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.SNAPSHOT_REG);
			//List<CtxIdentifier> attributes = futureAttributes.get();
			if(attributes.size() > 0){  //existing registry attribute found
				if (LOG.isDebugEnabled()){
					LOG.debug("Found SnapshotRegistry in context");
				}
				 
				//Future<CtxModelObject> futureAttribute = ctxBroker.retrieve(attrID);
				CtxAttribute attr = attributes.iterator().next();
				retrievedReg = (SnapshotsRegistry)SerialisationHelper.deserialise(attr.getBinaryValue(), this.getClass().getClassLoader());
				
				
			}else{  //create new mappings attribute and populate
				if (LOG.isDebugEnabled()){
					LOG.debug("SnapshotRegistry does not yet exist in context - creating");
				}
				retrievedReg = new SnapshotsRegistry();
				
				Future<CtxAttribute> futureAttribute = ctxBroker.createAttribute((CtxEntityIdentifier)personEntity.getId(), CtxAttributeTypes.SNAPSHOT_REG);
				CtxAttribute newAttribute = futureAttribute.get();
				byte[] blobRegistry = SerialisationHelper.serialise(retrievedReg);
				newAttribute.setBinaryValue(blobRegistry);
				ctxBroker.update(newAttribute);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return retrievedReg;
	}

	@Override
	public void onCreation(CtxChangeEvent event) {
		if (LOG.isDebugEnabled()){
			LOG.debug("Recieved Ctx Attribute Creation event for attribute: "+event.getId().getType());
		}
		CtxAttributeIdentifier attrID = (CtxAttributeIdentifier)event.getId();
		String type = attrID.getType();
		//check default snapshot
		if(defaultSnpsht.containsType(type)){
			defaultSnpsht.setTypeID(type, attrID);
		}
		//update snapshot registry
		snpshtRegistry.updateSnapshots(type, attrID);

		//update registry in context
		storeReg();
	}
	
	private void storeReg(){
		try {
			Future<List<CtxIdentifier>> futureAttrIds = ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.SNAPSHOT_REG);
			List<CtxIdentifier> attrIds = futureAttrIds.get();
			CtxAttributeIdentifier attrId = (CtxAttributeIdentifier)attrIds.get(0);
			ctxBroker.updateAttribute(attrId, SerialisationHelper.serialise(snpshtRegistry));
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onModification(CtxChangeEvent arg0) {
	}

	@Override
	public void onRemoval(CtxChangeEvent arg0) {
	}

	@Override
	public void onUpdate(CtxChangeEvent arg0) {
	}
}
