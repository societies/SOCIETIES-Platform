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
package org.societies.android.platform.contextservice;


//import java.net.URL;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.societies.android.api.context.model.ACtxAssociation;
import org.societies.android.api.context.model.ACtxAssociationIdentifier;
import org.societies.android.api.context.model.ACtxAttribute;
import org.societies.android.api.context.model.ACtxAttributeIdentifier;
import org.societies.android.api.context.model.ACtxEntity;
import org.societies.android.api.context.model.ACtxEntityIdentifier;
import org.societies.android.api.context.model.ACtxIdentifier;
import org.societies.android.api.context.model.ACtxModelObject;
import org.societies.android.api.internal.context.IInternalCtxClient;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author pkosmides
 *
 */
public class ContextManagement extends Service implements IInternalCtxClient {

	private static ExpiringCache<ACtxIdentifier, ACtxModelObject> cache = new ExpiringCache();

	// TODO Remove and instantiate privateId properly so that privateId.toString() can be used instead
	private final String privateIdtoString = "myFooIIdentity@societies.local";

    private ClientCommunicationMgr commMgr;
    
    private static final String LOG_TAG = ContextManagement.class.getName();
    private IBinder binder = null;
    
    @Override
	public void onCreate () {
		this.binder = new LocalBinder();
		Log.d(LOG_TAG, "ContextManagement service starting");
		try {
			//INSTANTIATE COMMS MANAGER
			commMgr = new ClientCommunicationMgr(this);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
        }    
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "ContextManagement service terminating");
	}

	/**Create Binder object for local service invocation */
	public class LocalBinder extends Binder {
		public ContextManagement getService() {
			return ContextManagement.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return this.binder;
	}

	public ACtxAssociation createAssociation(String client, String type) throws CtxException {

		if (type == null)
			throw new NullPointerException("type can't be null");

		final ACtxAssociationIdentifier identifier;

		identifier = new ACtxAssociationIdentifier(this.privateIdtoString, 
				type, CtxModelObjectNumberGenerator.getNextValue());

		final ACtxAssociation association = new  ACtxAssociation(identifier);
		cache.put(association.getId(), association);		

		return association;
	}

	public ACtxAttribute createAttribute(String client, ACtxEntityIdentifier scope, String type)
			throws CtxException {

		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");

//		final CtxEntity entity = (CtxEntity) modelObjects.get(scope);
		final ACtxEntity entity = (ACtxEntity) cache.get(scope);

		if (entity == null)	
			throw new NullPointerException("Scope not found: " + scope);

		ACtxAttributeIdentifier attrIdentifier = new ACtxAttributeIdentifier(scope, type, CtxModelObjectNumberGenerator.getNextValue());
		final ACtxAttribute attribute = new ACtxAttribute(attrIdentifier);

//		this.modelObjects.put(attribute.getId(), attribute);
		cache.put(attribute.getId(), attribute);
		entity.addAttribute(attribute);
		Log.d(LOG_TAG, "Attribute cached - " + attribute);

		return attribute;
	}

	public ACtxEntity createEntity(String client, String type) throws CtxException {

		final ACtxEntityIdentifier identifier;

		identifier = new ACtxEntityIdentifier(this.privateIdtoString, 
					type, CtxModelObjectNumberGenerator.getNextValue());

		final ACtxEntity entity = new  ACtxEntity(identifier);
		if (entity.getId()!=null)
			Log.d(LOG_TAG, "Maps key is OK!!");
		else
			Log.d(LOG_TAG, "Problem with maps key!!");
	//	modelObjects.put(entity.getId(), entity);
		cache.put(entity.getId(), entity);
		Log.d(LOG_TAG, "Entity cached - " + entity);

		return entity;
	}

	public List<ACtxEntityIdentifier> lookupEntities(String client, String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {

        final List<ACtxEntityIdentifier> foundList = new ArrayList<ACtxEntityIdentifier>();
        for (ACtxIdentifier identifier : cache.keySet()) {
            if (identifier.getModelType().equals(CtxModelType.ATTRIBUTE)
                    && identifier.getType().equals(attribType)) {
                final ACtxAttribute attribute = (ACtxAttribute) cache.get(identifier);
//                if (attribute.getScope().getType().equals(entityType) && attribute.getValue().equals(minAttribValue)) {
                if (attribute.getScope().getType().equals(entityType)) {
                	if (minAttribValue instanceof String && maxAttribValue instanceof String) {
                		if (attribute.getStringValue()!=null) {
		                	String valueStr = attribute.getStringValue();
		                		if(valueStr.compareTo(minAttribValue.toString()) >=0 && valueStr.compareTo(maxAttribValue.toString()) <=0)
		               				foundList.add(attribute.getScope());                			
        				}
                	} else if (minAttribValue instanceof Integer && maxAttribValue instanceof Integer) {
                		if(attribute.getIntegerValue()!=null) {
		               		Integer valueInt = attribute.getIntegerValue();
		          			if(valueInt.compareTo((Integer) minAttribValue) >=0 && valueInt.compareTo((Integer) maxAttribValue) <=0)
		               			foundList.add(attribute.getScope());
                		}
                	} else if (minAttribValue instanceof Double && maxAttribValue instanceof Double) {
                		if(attribute.getDoubleValue()!=null) {
		               		Double valueDouble = attribute.getDoubleValue();
		           			if(valueDouble.compareTo((Double) minAttribValue) >= 0 && valueDouble.compareTo((Double) maxAttribValue) <= 0)
		               			foundList.add(attribute.getScope());                			
                		}
                	} else {
                		byte[] valueBytes;
                		byte[] minValueBytes;
                		byte[] maxValueBytes;
						try {
							minValueBytes = SerialisationHelper.serialise(minAttribValue);
							maxValueBytes = SerialisationHelper.serialise(maxAttribValue);
							valueBytes = SerialisationHelper.serialise(attribute.getBinaryValue());
							if (Arrays.equals(minValueBytes, maxValueBytes))
								if (Arrays.equals(valueBytes, minValueBytes))
									foundList.add(attribute.getScope());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}                		
                	}
                	
                }
            }
        }
        return foundList;
	}

	public List<ACtxIdentifier> lookup(String client, CtxModelType modelType, String type)
			throws CtxException {

		final List<ACtxIdentifier> foundList = new ArrayList<ACtxIdentifier>();

		for (ACtxIdentifier identifier : cache.keySet()) {
			if (identifier.getModelType().equals(modelType) && identifier.getType().equals(type)) {
				foundList.add(identifier);
			}		
		}
		return foundList;
	}

	public ACtxModelObject remove(String client, ACtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxModelObject retrieve(String client, ACtxIdentifier id)
			throws CtxException {

		return this.cache.get(id);
	}
	//modelObject - identity in api
	public ACtxModelObject update(String client, ACtxModelObject modelObject) throws CtxException {

		if (cache.keySet().contains(modelObject.getId())) {
			cache.put(modelObject.getId(), modelObject);
		}

		 if (modelObject instanceof ACtxAssociation) {

			   ACtxEntity ent = null;
			   ACtxEntityIdentifier entId;

			   // Add association to parent entity
			   entId = ((ACtxAssociation) modelObject).getParentEntity();
			   if (entId != null)
			     ent = (ACtxEntity) this.retrieve(client, entId);
			     if (ent != null)
			       ent.addAssociation(((ACtxAssociation) modelObject).getId());

			    // Add association to child entities
			    Set<ACtxEntityIdentifier> entIds = ((ACtxAssociation) modelObject).getChildEntities();
			    for (ACtxEntityIdentifier entIdent : entIds) {
			    	//entIdent = ((CtxAssociation) modelObject).getParentEntity();
			    	ent = (ACtxEntity) this.retrieve(client, entIdent);
			    	if (ent != null)
			    		ent.addAssociation(((ACtxAssociation) modelObject).getId());
			    }
		}

		return modelObject;
	}

	public ACtxAttribute updateAttribute(String client, ACtxAttributeIdentifier attributeId,
			Serializable value) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxAttribute updateAttribute(String client, ACtxAttributeIdentifier attributeId,
			Serializable value, String valueMetric) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

}
