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
package org.societies.context.broker.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.context.api.user.db.IUserCtxDBMgr;
import org.societies.context.api.user.db.IUserCtxDBMgrCallback;
import org.societies.context.api.user.history.IUserCtxHistoryMgr;
import org.societies.context.broker.api.CtxBrokerException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * Internal Context Broker Implementation
 * This class implements the internal context broker interfaces and orchestrates the db 
 * management 
 */
public class InternalCtxBroker implements ICtxBroker {

	private IUserCtxDBMgr userCtxDBMgr;
	private IUserCtxHistoryMgr userCtxHistoryMgr;

	public InternalCtxBroker(IUserCtxDBMgr userDB,IUserCtxHistoryMgr userHocDB) throws CtxException {
		this.userCtxDBMgr=userDB;
		this.userCtxHistoryMgr = userHocDB;
		// TODO Use logging.debug
		//System.out.println(this.getClass().getName()+" full");
	}

	public InternalCtxBroker() throws CtxException {
		// TODO Use logging.debug
		//System.out.println(this.getClass().getName()+ " empty");
	}

	public void setUserCtxDBMgr(IUserCtxDBMgr userDB) throws CtxException {
		this.userCtxDBMgr = userDB;
	}

	public void setUserCtxHistoryMgr (IUserCtxHistoryMgr userHocDB) throws CtxException {
		this.userCtxHistoryMgr = userHocDB;
	}


	@Override
	@Async
	public Future<CtxAssociation> createAssociation(String type) throws CtxException {
		
		UserDBCallback callback = new UserDBCallback();
		
		userCtxDBMgr.createAssociation(type, callback);
		CtxAssociation association = (CtxAssociation) callback.getCreatedCtxAssociation();
		if (association!=null)
			return new AsyncResult<CtxAssociation>(association);
		else 
			return new AsyncResult<CtxAssociation>(null);
	}

	@Override
	@Async
	public Future<CtxAttribute> createAttribute(CtxEntityIdentifier scope,
			String type) throws CtxException {
		// TODO Auto-generated method stub		
		UserDBCallback callback = new UserDBCallback();
		
		userCtxDBMgr.createAttribute(scope, null, type, callback);
		CtxAttribute attribute = (CtxAttribute) callback.getCreatedCtxAttribute();
		if (attribute!=null)
			return new AsyncResult<CtxAttribute>(attribute);
		else 
			return new AsyncResult<CtxAttribute>(null);
	}

	@Override
	@Async
	public Future<CtxEntity> createEntity(String type) throws CtxException {
		
		UserDBCallback callback = new UserDBCallback();
		
		userCtxDBMgr.createEntity(type, callback);
		CtxEntity entity = (CtxEntity) callback.getCreatedCtxEntity();
		if (entity!=null)
			return new AsyncResult<CtxEntity>(entity);
		else 
			return new AsyncResult<CtxEntity>(null);
	}

	@Override
	public void disableCtxMonitoring(CtxAttributeValueType type) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disableCtxRecording() throws CtxException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableCtxMonitoring(CtxAttributeValueType type) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableCtxRecording() throws CtxException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Future<List<CtxEntityIdentifier>> lookupEntities(String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {
		
		UserDBCallback callback = new UserDBCallback();
		userCtxDBMgr.lookupEntities(entityType, attribType, minAttribValue, maxAttribValue, callback);
		List<CtxEntityIdentifier> results = callback.getLookedUpCtxEntities();
		// add fix
				
		return null;
	}

	@Override
	public void registerForUpdates(CtxEntityIdentifier scope,
			String attrType) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerForUpdates(CtxAttributeIdentifier attrId) throws CtxException {
		// TODO Auto-generated method stub
	
	}

	@Override
	public Future<CtxModelObject> remove(CtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	@Async
	public Future<CtxModelObject> retrieve(CtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback();
				
		userCtxDBMgr.retrieve(identifier, callback);
		CtxModelObject modelObj = (CtxModelObject) callback.getCtxModelObjectRetrieved();
		if (modelObj!=null)
			return new AsyncResult<CtxModelObject>(modelObj);
		else 
			return new AsyncResult<CtxModelObject>(null);
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(
			CtxAttributeIdentifier attrId, Date date) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(
			CtxAttributeIdentifier attrId, int modificationIndex) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void unregisterForUpdates(CtxAttributeIdentifier attrId) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterForUpdates(CtxEntityIdentifier scope,
			String attributeType) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Async
	public Future<CtxModelObject> update(CtxModelObject identifier) throws CtxException {
		
		UserDBCallback callback = new UserDBCallback();
		
		userCtxDBMgr.update(identifier, callback);
		CtxModelObject modelObject = (CtxModelObject) callback.getUpdatedCtxModelObject();
		

		// this part allows the storage of attribute updates to context history
		if(modelObject.getModelType().equals(CtxModelType.ATTRIBUTE)){
			CtxAttribute ctxAttr = (CtxAttribute) modelObject; 
			if (ctxAttr.isHistoryRecorded() && userCtxHistoryMgr != null){
				Date date = new Date();
				//	System.out.println("storing hoc attribute");
				userCtxHistoryMgr.storeHoCAttribute(ctxAttr, date);
			}
			return new AsyncResult<CtxModelObject>(modelObject);
		}
		else 
			return new AsyncResult<CtxModelObject>(null);
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.ICtxBroker#updateAttribute(org.societies.api.context.model.CtxAttributeIdentifier, java.io.Serializable)
	 */
	@Override
	@Async
	public Future<CtxAttribute> updateAttribute(
			CtxAttributeIdentifier attributeId, Serializable value) throws CtxException {
		
		// Implies <code>null</code> valueMetric param
		return this.updateAttribute(attributeId, value, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.ICtxBroker#updateAttribute(org.societies.api.context.model.CtxAttributeIdentifier, java.io.Serializable, java.lang.String)
	 */
	@Override
	@Async
	public Future<CtxAttribute> updateAttribute(
			CtxAttributeIdentifier attributeId, Serializable value,
			String valueMetric) throws CtxException {
	
		if (attributeId == null)
			throw new NullPointerException("attributeId can't be null");
		// Ugly but will throw IllegalArgumentException if value type is not supported
		this.findAttributeValueType(value);
		
		Future<CtxModelObject> futureModelObj = this.retrieve(attributeId);
		CtxAttribute attribute;
		try {
			attribute = (CtxAttribute) futureModelObj.get();
				
			if (attribute == null) {
				// Requested attribute not found
				return new AsyncResult<CtxAttribute>(null);
			} else {
				final CtxAttributeValueType valueType = this.findAttributeValueType(value);
				if (CtxAttributeValueType.EMPTY.equals(valueType))
					attribute.setStringValue(null);
				else if (CtxAttributeValueType.STRING.equals(valueType))
					attribute.setStringValue((String) value);
				else if (CtxAttributeValueType.INTEGER.equals(valueType))
					attribute.setIntegerValue((Integer) value);
				else if (CtxAttributeValueType.DOUBLE.equals(valueType))
					attribute.setDoubleValue((Double) value);
				else if (CtxAttributeValueType.BINARY.equals(valueType))
					attribute.setBinaryValue((byte[]) value);

				attribute.setValueType(valueType);
				futureModelObj = this.update(attribute);
				attribute = (CtxAttribute) futureModelObj.get();
				return new AsyncResult<CtxAttribute>(attribute);
			}
		} catch (InterruptedException ie) {
			throw new CtxBrokerException("Could not update attribute " 
					+ attributeId + ": " + ie.getMessage(), ie);
		} catch (ExecutionException ee) {
			throw new CtxBrokerException("Could not update attribute " 
					+ attributeId + ": " + ee.getMessage(), ee);
		}
	}


	

	@Override
	public Future<IndividualCtxEntity> retrieveAdministratingCSS(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Set<CtxBond>> retrieveBonds(CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public Future<List<Object>> evaluateSimilarity(
			Serializable objectUnderComparison,
			List<Serializable> referenceObjects) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxIdentifier>> lookup(CtxModelType modelType,
			String type) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}
		
	private class UserDBCallback implements IUserCtxDBMgrCallback { 
				
		private CtxModelObject updatedCtxModelObject = null;
		
		private CtxEntity createdCtxEntity = null;
		
		private CtxAttribute createdCtxAttribute = null;
		
		private List<CtxEntityIdentifier> lookedUpCtxEntities = null;
			
		private CtxModelObject ctxModelObjectRetrieved = null;

		private CtxAssociation ctxAssociationCreated = null;
		
		/*
		 * (non-Javadoc)
		 * @see org.societies.context.api.user.db.IUserCtxDBMgrCallback#ctxEntityCreated(org.societies.api.context.model.CtxEntity)
		 */
		@Override
		public void ctxEntityCreated(CtxEntity ctxEntity) {
			this.createdCtxEntity = ctxEntity;
		}

		/*
		 * (non-Javadoc)
		 * @see org.societies.context.api.user.db.IUserCtxDBMgrCallback#ctxIndividualCtxEntityCreated(org.societies.api.context.model.CtxEntity)
		 */
		@Override
		public void ctxIndividualCtxEntityCreated(CtxEntity ctxEntity) {
			this.createdCtxEntity = ctxEntity;
		}

		/*
		 * (non-Javadoc)
		 * @see org.societies.context.api.user.db.IUserCtxDBMgrCallback#ctxAttributeCreated(org.societies.api.context.model.CtxAttribute)
		 */
		@Override
		public void ctxAttributeCreated(CtxAttribute ctxAttribute) {
			this.createdCtxAttribute = ctxAttribute;
		}
		
		// @Override TODO it is not in DB callback ifc
		public void ctxAssociationCreated(CtxAssociation ctxAssociation) {
			this.ctxAssociationCreated = ctxAssociation;
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.societies.context.api.user.db.IUserCtxDBMgrCallback#ctxModelObjectUpdated(org.societies.api.context.model.CtxModelObject)
		 */
		@Override
		public void ctxModelObjectUpdated(CtxModelObject ctxModelObject) {
			this.updatedCtxModelObject = ctxModelObject;
		}

		/*
		 * (non-Javadoc)
		 * @see org.societies.context.api.user.db.IUserCtxDBMgrCallback#ctxEntitiesLookedup(java.util.List)
		 */
		@Override
		public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list) {
			this.lookedUpCtxEntities = list;
		}

		/*
		 * (non-Javadoc)
		 * @see org.societies.context.api.user.db.IUserCtxDBMgrCallback#ctxModelObjectRetrieved(org.societies.api.context.model.CtxModelObject)
		 */
		@Override
		public void ctxModelObjectRetrieved(CtxModelObject ctxModelObject) {
			this.ctxModelObjectRetrieved = ctxModelObject;
		}

	
	//********************************************
	// Getters and Setters for callback variables
	//********************************************
				
		public CtxModelObject getUpdatedCtxModelObject() {
			return this.updatedCtxModelObject;
		}

		public CtxEntity getCreatedCtxEntity() {
			return this.createdCtxEntity;
		}

		public CtxAttribute getCreatedCtxAttribute() {
			return this.createdCtxAttribute;
		}

		public CtxAssociation getCreatedCtxAssociation() {
			return this.ctxAssociationCreated;
		}
			
		public List<CtxEntityIdentifier> getLookedUpCtxEntities(){
			return this.lookedUpCtxEntities;
		}
	
		public CtxModelObject getCtxModelObjectRetrieved(){
			return this.ctxModelObjectRetrieved;
		}
	}
	
	private CtxAttributeValueType findAttributeValueType(Serializable value) {
		if (value == null)
			return CtxAttributeValueType.EMPTY;
		else if (value instanceof String)
			return CtxAttributeValueType.STRING;
		else if (value instanceof Integer)
			return CtxAttributeValueType.INTEGER;
		else if (value instanceof Double)
			return CtxAttributeValueType.DOUBLE;
		else if (value instanceof byte[])
			return CtxAttributeValueType.BINARY;
		else
			throw new IllegalArgumentException(value + ": Invalid value type");
	}

	
	
	
	//***********************************************
	//     Context History Management Methods  
	//***********************************************
	
	
	@Override
	@Async
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			CtxAttributeIdentifier attrId, Date startDate, Date endDate) throws CtxException {
		
		final List<CtxHistoryAttribute> result = new ArrayList<CtxHistoryAttribute>();
		
		result.addAll(this.userCtxHistoryMgr.retrieveHistory(attrId, startDate, endDate));
	
		return new AsyncResult<List<CtxHistoryAttribute>>(result);
	}
	
	@Override
	public Future<Map<CtxHistoryAttribute, List<CtxHistoryAttribute>>> retrieveHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1,
			Date arg2, Date arg3) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			CtxAttributeIdentifier arg0, int arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttributeIdentifier>> getHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Boolean> removeHistoryTuples(CtxAttributeIdentifier arg0,
			List<CtxAttributeIdentifier> arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Boolean> setHistoryTuples(CtxAttributeIdentifier arg0,
			List<CtxAttributeIdentifier> arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttributeIdentifier>> updateHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Future<Integer> removeHistory(String type, Date startDate, Date endDate) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

}