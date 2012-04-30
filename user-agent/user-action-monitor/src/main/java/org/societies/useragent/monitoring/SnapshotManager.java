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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;

public class SnapshotManager {

	/*
	 * DEFAULT SNAPSHOT DEFINITION
	 */
	String[] defaultDef = {
			CtxAttributeTypes.LOCATION_SYMBOLIC, 
			CtxAttributeTypes.STATUS, 
			CtxAttributeTypes.TEMPERATURE
			};
	/*
	 * End of definition
	 */

	private ICtxBroker ctxBroker;
	private SnapshotsRegistry snpshtRegistry;
	private String registryName;
	private List<CtxAttributeIdentifier> defaultSnpsht;

	public SnapshotManager(ICtxBroker ctxBroker){
		this.ctxBroker = ctxBroker;
		registryName = "snpshtRegistry";
		initialiseDefaultSnpsht();
		retrieveSnpshtRegistry();
	}

	public List<CtxAttributeIdentifier> getSnapshot(CtxAttributeIdentifier primary){
		List<CtxAttributeIdentifier> snapshot = snpshtRegistry.getSnapshot(primary);
		if(snapshot == null){//no existing snapshot mapping to this primary -> create with default snapshot
			snapshot = defaultSnpsht;
			snpshtRegistry.addMapping(primary, snapshot);
			try{
				//update registry in context
				Future<List<CtxIdentifier>> futureAttrIds = ctxBroker.lookup(CtxModelType.ATTRIBUTE, registryName);
				List<CtxIdentifier> attrIds = futureAttrIds.get();
				CtxAttributeIdentifier attrId = (CtxAttributeIdentifier)attrIds.get(0);
				ctxBroker.updateAttribute(attrId, snpshtRegistry);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (CtxException e) {
				e.printStackTrace();
			}
		}
		return snapshot;
	}
	
	public void updateSnapshot(CtxAttributeIdentifier primary, List<CtxAttributeIdentifier> newSnapshot){
		if(snpshtRegistry.getSnapshot(primary) != null){
			snpshtRegistry.updateMapping(primary, newSnapshot);
			try{
				//update registry in context
				Future<List<CtxIdentifier>> futureAttrIds = ctxBroker.lookup(CtxModelType.ATTRIBUTE, registryName);
				List<CtxIdentifier> attrIds = futureAttrIds.get();
				CtxAttributeIdentifier attrId = (CtxAttributeIdentifier)attrIds.get(0);
				ctxBroker.updateAttribute(attrId, snpshtRegistry);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (CtxException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("ERROR - cannot update snapshot as no snapshot exists for this primary attribute identifier");
		}
	}

	private void initialiseDefaultSnpsht(){
		defaultSnpsht = new ArrayList<CtxAttributeIdentifier>();
		for(int i = 0; i<defaultDef.length; i++){
			String attrType = defaultDef[i];
			//retrieve attribute ID from context
			try {
				Future<List<CtxIdentifier>> futureAttributes = ctxBroker.lookup(CtxModelType.ATTRIBUTE, attrType);
				List<CtxIdentifier> attributes = futureAttributes.get();
				if(attributes.size() > 0){
					defaultSnpsht.add((CtxAttributeIdentifier) attributes.get(0));
				}else{
					System.out.println("ERROR - Could not find context attribute for default snapshot!!");
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

	private void retrieveSnpshtRegistry(){
		try {
			Future<List<CtxIdentifier>> futureAttributes = ctxBroker.lookup(CtxModelType.ATTRIBUTE, registryName);
			List<CtxIdentifier> attributes = futureAttributes.get();
			if(attributes.size() > 0){  //existing registry attribute found
				CtxIdentifier attrID = attributes.get(0);
				Future<CtxModelObject> futureAttribute = ctxBroker.retrieve(attrID);
				CtxAttribute attr = (CtxAttribute)futureAttribute.get();
				snpshtRegistry = (SnapshotsRegistry)SerialisationHelper.deserialise(attr.getBinaryValue(), this.getClass().getClassLoader());
			}else{  //create new mappings attribute and populate
				snpshtRegistry = new SnapshotsRegistry();
				Future<IndividualCtxEntity> futurePersonEntity = ctxBroker.retrieveCssOperator();  //get PERSON entity to store mappings for
				IndividualCtxEntity personEntity = futurePersonEntity.get();
				Future<CtxAttribute> futureAttribute = ctxBroker.createAttribute((CtxEntityIdentifier)personEntity.getId(), registryName);
				CtxAttribute newAttribute = futureAttribute.get();
				byte[] blobRegistry = SerialisationHelper.serialise(snpshtRegistry);
				newAttribute.setBinaryValue(blobRegistry);
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
	}
}
