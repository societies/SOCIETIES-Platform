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

import org.societies.android.api.internal.context.broker.ICtxClientBroker;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
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
public class ContextManagement extends Service implements ICtxClientBroker {

	private static ExpiringCache<CtxIdentifier, CtxModelObject> cache = new ExpiringCache();

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

	public CtxAssociation createAssociation(String type) throws CtxException {

		if (type == null)
			throw new NullPointerException("type can't be null");

		final CtxAssociationIdentifier identifier;

		identifier = new CtxAssociationIdentifier(this.privateIdtoString, 
				type, CtxModelObjectNumberGenerator.getNextValue());

		final CtxAssociation association = new  CtxAssociation(identifier);
		cache.put(association.getId(), association);		

		return association;
	}

	public CtxAttribute createAttribute(CtxEntityIdentifier scope, String type)
			throws CtxException {

		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");

//		final CtxEntity entity = (CtxEntity) modelObjects.get(scope);
		final CtxEntity entity = (CtxEntity) cache.get(scope);

		if (entity == null)	
			throw new NullPointerException("Scope not found: " + scope);

		CtxAttributeIdentifier attrIdentifier = new CtxAttributeIdentifier(scope, type, CtxModelObjectNumberGenerator.getNextValue());
		final CtxAttribute attribute = new CtxAttribute(attrIdentifier);

//		this.modelObjects.put(attribute.getId(), attribute);
		cache.put(attribute.getId(), attribute);
		entity.addAttribute(attribute);
		Log.d(LOG_TAG, "Attribute cached - " + attribute);

		return attribute;
	}

	public CtxEntity createEntity(String type) throws CtxException {

		final CtxEntityIdentifier identifier;

		identifier = new CtxEntityIdentifier(this.privateIdtoString, 
					type, CtxModelObjectNumberGenerator.getNextValue());

		final CtxEntity entity = new  CtxEntity(identifier);
		if (entity.getId()!=null)
			Log.d(LOG_TAG, "Maps key is OK!!");
		else
			Log.d(LOG_TAG, "Problem with maps key!!");
	//	modelObjects.put(entity.getId(), entity);
		cache.put(entity.getId(), entity);
		Log.d(LOG_TAG, "Entity cached - " + entity);

		return entity;
	}

	public void disableCtxMonitoring(CtxAttributeValueType type)
			throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void enableCtxMonitoring(CtxAttributeValueType type)
			throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public List<CtxEntityIdentifier> lookupEntities(String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {

        final List<CtxEntityIdentifier> foundList = new ArrayList<CtxEntityIdentifier>();
        for (CtxIdentifier identifier : cache.keySet()) {
            if (identifier.getModelType().equals(CtxModelType.ATTRIBUTE)
                    && identifier.getType().equals(attribType)) {
                final CtxAttribute attribute = (CtxAttribute) cache.get(identifier);
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

	public List<CtxIdentifier> lookup(CtxModelType modelType, String type)
			throws CtxException {

		final List<CtxIdentifier> foundList = new ArrayList<CtxIdentifier>();

		for (CtxIdentifier identifier : cache.keySet()) {
			if (identifier.getModelType().equals(modelType) && identifier.getType().equals(type)) {
				foundList.add(identifier);
			}		
		}
		return foundList;
	}

	public void registerForChanges(CtxChangeEventListener listener,
			CtxIdentifier ctxId) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void unregisterFromChanges(CtxChangeEventListener listener,
			CtxIdentifier ctxId) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void registerForChanges(CtxChangeEventListener listener,
			CtxEntityIdentifier scope, String attrType) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void unregisterFromChanges(CtxChangeEventListener listener,
			CtxEntityIdentifier scope, String attrType) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public CtxModelObject remove(CtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public CtxModelObject retrieve(CtxIdentifier id)
			throws CtxException {

		return this.cache.get(id);
	}
	//modelObject - identity in api
	public CtxModelObject update(CtxModelObject modelObject) throws CtxException {

		if (cache.keySet().contains(modelObject.getId())) {
			cache.put(modelObject.getId(), modelObject);
		}

		 if (modelObject instanceof CtxAssociation) {

			   CtxEntity ent = null;
			   CtxEntityIdentifier entId;

			   // Add association to parent entity
			   entId = ((CtxAssociation) modelObject).getParentEntity();
			   if (entId != null)
			     ent = (CtxEntity) this.retrieve(entId);
			     if (ent != null)
			       ent.addAssociation(((CtxAssociation) modelObject).getId());

			    // Add association to child entities
			    Set<CtxEntityIdentifier> entIds = ((CtxAssociation) modelObject).getChildEntities();
			    for (CtxEntityIdentifier entIdent : entIds) {
			    	//entIdent = ((CtxAssociation) modelObject).getParentEntity();
			    	ent = (CtxEntity) this.retrieve(entIdent);
			    	if (ent != null)
			    		ent.addAssociation(((CtxAssociation) modelObject).getId());
			    }
		}

		return modelObject;
	}

	public CtxAttribute updateAttribute(CtxAttributeIdentifier attributeId,
			Serializable value) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public CtxAttribute updateAttribute(CtxAttributeIdentifier attributeId,
			Serializable value, String valueMetric) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public IndividualCtxEntity retrieveAdministratingCSS(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<CtxBond> retrieveBonds(CtxEntityIdentifier community)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CtxEntityIdentifier> retrieveSubCommunities(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CtxEntityIdentifier> retrieveCommunityMembers(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean setHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CtxAttributeIdentifier> getHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CtxAttributeIdentifier> updateHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean removeHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public void enableCtxRecording() throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void disableCtxRecording() throws CtxException {
		// TODO Auto-generated method stub
		
	}

}
