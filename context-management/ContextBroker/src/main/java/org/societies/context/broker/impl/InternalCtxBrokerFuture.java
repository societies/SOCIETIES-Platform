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
import java.util.concurrent.Future;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
//import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;
import org.societies.context.api.user.db.IUserCtxDBMgr;
import org.societies.context.api.user.db.IUserCtxDBMgrCallback;
import org.societies.context.api.user.history.IUserCtxHistoryCallback;
import org.societies.context.api.user.history.IUserCtxHistoryMgr;
//import org.societies.context.broker.impl.InternalCtxBroker.UserHoCDBCallback;
//import org.societies.context.broker.impl.InternalCtxBroker.UserDBCallback;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * Internal Context Broker Implementation
 * This class implements the internal context broker interfaces and orchestrates the db 
 * management 
 */
public class InternalCtxBrokerFuture implements ICtxBroker {

	private IUserCtxDBMgr userDB;
	private ICtxBroker broker;
	private IUserCtxHistoryMgr userHocDB;

	public InternalCtxBrokerFuture(IUserCtxDBMgr userDB,IUserCtxHistoryMgr userHocDB,ICtxBroker broker) throws CtxException {
		this.userDB=userDB;
		this.userHocDB = userHocDB;
		this.broker = broker;
		// TODO Use logging.debug
		//System.out.println(this.getClass().getName()+" full");
	}

	public InternalCtxBrokerFuture() throws CtxException {
		// TODO Use logging.debug
		//System.out.println(this.getClass().getName()+ " empty");
	}

	public void setUserCtxDBMgr(IUserCtxDBMgr userDB) throws CtxException {
		this.userDB = userDB;
	}

	public void setUserCtxHistoryMgr (IUserCtxHistoryMgr userHocDB) throws CtxException {
		this.userHocDB = userHocDB;
	}


	@Override
	@Async
	public Future<CtxAssociation> createAssociation(String type) throws CtxException {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(broker);
		
		userDB.createAssociation(type, callback);
		CtxAssociation association = (CtxAssociation) callback.getCtxModelObject();
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
		UserDBCallback callback = new UserDBCallback(broker);
		
		userDB.createAttribute(scope, null, type, callback);
		CtxAttribute attribute = (CtxAttribute) callback.getCtxModelObject();
		if (attribute!=null)
			return new AsyncResult<CtxAttribute>(attribute);
		else 
			return new AsyncResult<CtxAttribute>(null);
	}

	@Override
	@Async
	public Future<CtxEntity> createEntity(String type) throws CtxException {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(broker);
		
		userDB.createEntity(type, callback);
		CtxEntity entity = (CtxEntity) callback.getCtxModelObject();
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
	public Future<List<CtxModelObject>> lookup(CtxModelType modelType,
			String type) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntity>> lookupEntities(String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<Object>> registerForUpdates(CtxEntityIdentifier scope,
			String attrType) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<Object>> registerForUpdates(CtxAttributeIdentifier attrId) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxModelObject> remove(CtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int removeHistory(String type, Date startDate, Date endDate) throws CtxException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Async
	public Future<CtxModelObject> retrieve(CtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(broker);
				
		userDB.retrieve(identifier, callback);
		CtxModelObject modelObj = (CtxModelObject) callback.getCtxModelObject();
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
	public Future<List<CtxHistoryAttribute>> retrievePast(
			CtxAttributeIdentifier attrId, int modificationIndex) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Async
	public Future<List<CtxHistoryAttribute>> retrievePast(
			CtxAttributeIdentifier attrId, Date startDate, Date endDate) throws CtxException {
		// TODO Auto-generated method stub
		UserHoCDBCallback callback = new UserHoCDBCallback(broker);
		userHocDB.retrieveHistory(attrId, startDate, endDate, callback);
		CtxHistoryAttribute modelObj = (CtxHistoryAttribute) callback.getCtxModelObject();
		List<CtxHistoryAttribute> listAttrs = new ArrayList<CtxHistoryAttribute>();
		listAttrs.add(modelObj);
		if (modelObj!=null)
			return new AsyncResult<List<CtxHistoryAttribute>>(listAttrs);
		else 
			return new AsyncResult<List<CtxHistoryAttribute>>(null);
	}

	@Override
	public void unregisterForUpdates(CtxAttributeIdentifier attrId) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Future<List<Object>> unregisterForUpdates(CtxEntityIdentifier scope,
			String attributeType) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Async
	public Future<CtxModelObject> update(CtxModelObject identifier) throws CtxException {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(broker);
		
		userDB.update(identifier, callback);
		CtxModelObject modelObject = (CtxModelObject) callback.getCtxModelObject();
		

		// this part allows the storage of attribute updates to context history
		if(modelObject.getModelType().equals(CtxModelType.ATTRIBUTE)){
			CtxAttribute ctxAttr = (CtxAttribute) modelObject; 
			if (ctxAttr.isHistoryRecorded() && userHocDB != null){
				Date date = new Date();
				//	System.out.println("storing hoc attribute");
				userHocDB.storeHoCAttribute(ctxAttr, date);
			}
			return new AsyncResult<CtxModelObject>(modelObject);
		}
		else 
			return new AsyncResult<CtxModelObject>(null);
		
	}

	@Override
	@Async
	public Future<CtxAttribute> updateAttribute(
			CtxAttributeIdentifier attributeId, Serializable value) throws CtxException {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(broker);
		if (attributeId == null)
			throw new NullPointerException("attributeId can't be null");
		// Will throw IllegalArgumentException if value type is not supported
		this.findAttributeValueType(value);
		CtxModelObject modelObj = (CtxModelObject) this.retrieve(attributeId);
		
		if (modelObj == null) { // Requested attribute not found
			callback.ctxModelObjectUpdated(null);
			return null;
		} else {
			final CtxAttribute attribute = (CtxAttribute) modelObj;
			final CtxAttributeValueType valueType = findAttributeValueType(value);
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
			update(attribute);
			return new AsyncResult<CtxAttribute>(attribute);
		}
	}

	@Override
	public Future<CtxAttribute> updateAttribute(
			CtxAttributeIdentifier attributeId, Serializable value,
			String valueMetric) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Boolean> setCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttributeIdentifier>> getCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttributeIdentifier>> updateCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Boolean> removeCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxEntity> retrieveAdministratingCSS(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxAttribute> retrieveBonds(CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveChildCommunities(
			CtxEntityIdentifier community) throws CtxException {
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
	
	private class UserDBCallback implements IUserCtxDBMgrCallback {

		private ICtxBroker brokerCallback;
		private CtxModelObject ctxModelObject = null;
		
		UserDBCallback(ICtxBroker brokerCallback) {
			this.brokerCallback = brokerCallback;
		} 
				
		public CtxModelObject getCtxModelObject() {
			return ctxModelObject;
		}

		private void setCtxModelObject(CtxModelObject ctxModelObject) {
			this.ctxModelObject = ctxModelObject;
		}
		

		public void ctxEntityCreated(CtxEntity ctxEntity) {}

		public void ctxIndividualCtxEntityCreated(CtxEntity ctxEntity) {}

		public void ctxAttributeCreated(CtxAttribute ctxAttribute) {}

		public void ctxModelObjectUpdated(CtxModelObject ctxModelObject) {}

		public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list) {}

		public void ctxModelObjectRetrieved(CtxModelObject ctxModelObject) {}
	}
	
	private class UserHoCDBCallback implements IUserCtxHistoryCallback {

		private ICtxBroker brokerCallback;
		private CtxModelObject ctxModelObject = null;
		
		UserHoCDBCallback(ICtxBroker brokerCallback) {
			this.brokerCallback = brokerCallback;
		} 
				
		public CtxModelObject getCtxModelObject() {
			return ctxModelObject;
		}

		private void setCtxModelObject(CtxModelObject ctxModelObject) {
			this.ctxModelObject = ctxModelObject;
		}
		
		@Override
		public void ctxRecordingDisable() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void ctxRecordingEnabled() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void historyRemovedByDate(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void historyRemovedByType(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void historyRetrievedDate(List<CtxHistoryAttribute> arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void historyRetrievedIndex(List<CtxHistoryAttribute> arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void historyTupleIdsRetrieved(
				List<List<CtxAttributeIdentifier>> arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void historyTuplesRegistered() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void historyTuplesRetrieved(
				Map<CtxAttribute, List<CtxAttribute>> arg0) {
			// TODO Auto-generated method stub
			
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

	@Override
	public Future<List<Object>> evaluateSimilarity(
			Serializable objectUnderComparison,
			List<Serializable> referenceObjects) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}
}
