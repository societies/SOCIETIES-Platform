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
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.societies.api.internal.context.broker.ICommunityCtxBroker;
import org.societies.api.internal.context.broker.ICommunityCtxBrokerCallback;
import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;
import org.societies.api.internal.context.broker.IUserCtxBroker;
import org.societies.context.api.user.db.IUserCtxDBMgr;
import org.societies.context.api.user.db.IUserCtxDBMgrCallback;
import org.societies.context.api.user.history.IUserCtxHistoryCallback;
import org.societies.context.api.user.history.IUserCtxHistoryMgr;

/**
 * Platform Context Broker Implementation
 * This class implements the internal context broker interfaces and the callback interface of the community context db 
 * management in order to facilitate within platform db interaction 
 */
public class InternalCtxBroker extends CtxBroker implements IUserCtxBroker, ICommunityCtxBroker {

	private IUserCtxDBMgr userDB;

	private IUserCtxHistoryMgr userHocDB;

	public InternalCtxBroker(IUserCtxDBMgr userDB,IUserCtxHistoryMgr userHocDB) {
		this.userDB=userDB;
		this.userHocDB = userHocDB;
		// TODO Use logging.debug
		//System.out.println(this.getClass().getName()+" full");
	}

	public InternalCtxBroker() {
		// TODO Use logging.debug
		//System.out.println(this.getClass().getName()+ " empty");
	}

	public void setUserCtxDBMgr(IUserCtxDBMgr userDB) {
		this.userDB = userDB;
	}

	public void setUserCtxHistoryMgr (IUserCtxHistoryMgr userHocDB) {
		this.userHocDB = userHocDB;
	}

	/**
	 * As of release 0.0.1, deprecated by {@link #createAttribute(CtxEntityIdentifier, String, IUserCtxBrokerCallback)}
	 * TODO Remove method signature from API + implementation in future release 
	 */
	@Override
	public void createAttribute(CtxEntityIdentifier scope,CtxAttributeValueType enumerate, String type,
			IUserCtxBrokerCallback brokerCallback) {
		UserDBCallback callback = new UserDBCallback(brokerCallback);
		userDB.createAttribute(scope, enumerate, type, callback);
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.IUserCtxBroker#createAttribute(org.societies.api.context.model.CtxEntityIdentifier, java.lang.String, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)
	 */
	@Override
	public void createAttribute(CtxEntityIdentifier scope, String type,
			IUserCtxBrokerCallback brokerCallback) {
		UserDBCallback callback = new UserDBCallback(brokerCallback);
		// TODO IUserCtxDBMgr interface should also provide createAttribute(CtxEntityIdentifier scope, String type, IUserCtxBrokerCallback brokerCallback)
		userDB.createAttribute(scope, null, type, callback);
	}

	@Override
	public void createEntity(String type, IUserCtxBrokerCallback brokerCallback) {
		UserDBCallback callback = new UserDBCallback(brokerCallback);
		userDB.createEntity(type, callback);		
	}

	@Override
	public void createAssociation(String type, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrieveAdministratingCSS(CtxEntityIdentifier community,
			ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrieveBonds(CtxEntityIdentifier community,
			ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrieveChildCommunities(CtxEntityIdentifier community,
			ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrieveCommunityMembers(CtxEntityIdentifier community,
			ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrieveParentCommunities(CtxEntityIdentifier community,
			ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}


	@Override
	public void disableCtxMonitoring(CtxAttributeValueType type) {
		// TODO Auto-generated method stub
	}

	@Override
	public void disableCtxRecording() {
		// TODO Auto-generated method stub
	}

	@Override
	public void enableCtxMonitoring(CtxAttributeValueType type) {
		// TODO Auto-generated method stub
	}

	@Override
	public void enableCtxRecording() {
		// TODO Auto-generated method stub
	}

	@Override
	public void evaluateSimilarity(Serializable objectUnderComparison,
			List<Serializable> referenceObjects, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void lookup(CtxModelType modelType, String type,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void lookupEntities(String entityType, String attribType,
			Serializable minAttribValue, Serializable maxAttribValue,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void registerForUpdates(CtxEntityIdentifier scope, String attrType,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void registerForUpdates(CtxAttributeIdentifier attrId,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(CtxIdentifier identifier, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public int removeHistory(String type, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void retrieve(CtxIdentifier identifier, IUserCtxBrokerCallback brokerCallback) {
		UserDBCallback callback = new UserDBCallback(brokerCallback);
		userDB.retrieve(identifier, callback);
	}

	@Override
	public void unregisterForUpdates(CtxAttributeIdentifier attrId,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unregisterForUpdates(CtxEntityIdentifier scope,
			String attributeType, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(CtxModelObject modelObject,
			IUserCtxBrokerCallback brokerCallback) {
		UserDBCallback callback = new UserDBCallback(brokerCallback);

		userDB.update(modelObject, callback);

		// this part allows the storage of attribute updates to context history
		if(modelObject.getModelType().equals(CtxModelType.ATTRIBUTE)){
			CtxAttribute ctxAttr = (CtxAttribute)modelObject; 
			if (ctxAttr.isHistoryRecorded() && userHocDB != null){
				Date date = new Date();
				//	System.out.println("storing hoc attribute");
				userHocDB.storeHoCAttribute(ctxAttr, date);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.IUserCtxBroker#updateAttribute(org.societies.api.context.model.CtxAttributeIdentifier, java.io.Serializable, IUserCtxBrokerCallback)
	 */
	@Override
	public void updateAttribute(CtxAttributeIdentifier attributeId, final Serializable value, final IUserCtxBrokerCallback callback) {
		this.updateAttribute(attributeId, value, null, callback);
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.IUserCtxBroker#updateAttribute(org.societies.api.context.model.CtxAttributeIdentifier, java.io.Serializable, String, IUserCtxBrokerCallback)
	 */
	@Override
	public void updateAttribute(CtxAttributeIdentifier attributeId, final Serializable value, final String valueMetric, final IUserCtxBrokerCallback callback) {
		if (attributeId == null)
			throw new NullPointerException("attributeId can't be null");
		// Will throw IllegalArgumentException if value type is not supported
		this.findAttributeValueType(value);
		this.retrieve(attributeId, new IUserCtxBrokerCallback() {

			@Override
			public void cancel(CtxIdentifier arg0, String arg1) {}

			@Override
			public void ctxAssociationCreated(CtxAssociation arg0) {}

			@Override
			public void ctxAttributeCreated(CtxAttribute arg0) {}

			@Override
			public void ctxEntitiesLookedup(List<CtxEntityIdentifier> arg0) {}

			@Override
			public void ctxEntityCreated(CtxEntity arg0) {}

			@Override
			public void ctxHistoryTuplesRemoved(Boolean arg0) {}

			@Override
			public void ctxHistoryTuplesRetrieved(
					List<CtxAttributeIdentifier> arg0) {}

			@Override
			public void ctxHistoryTuplesSet(Boolean arg0) {}

			@Override
			public void ctxHistoryTuplesUpdated(
					List<CtxAttributeIdentifier> arg0) {}

			@Override
			public void ctxIndividualCtxEntityCreated(CtxEntity arg0) {}

			@Override
			public void ctxModelObjectRemoved(CtxModelObject arg0) {}

			@Override
			public void ctxModelObjectRetrieved(CtxModelObject modelObject) {
				if (modelObject == null) { // Requested attribute not found
					callback.ctxModelObjectUpdated(null);
				} else {
					final CtxAttribute attribute = (CtxAttribute) modelObject;
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
					update(attribute, callback);
				}
			}

			@Override
			public void ctxModelObjectUpdated(CtxModelObject arg0) {}

			@Override
			public void ctxModelObjectsLookedup(List<CtxIdentifier> arg0) {}

			@Override
			public void futureCtxRetrieved(List<CtxAttribute> arg0) {}

			@Override
			public void futureCtxRetrieved(CtxAttribute arg0) {}

			@Override
			public void historyCtxRetrieved(CtxHistoryAttribute arg0) {}

			@Override
			public void historyCtxRetrieved(List<CtxHistoryAttribute> arg0) {}

			@Override
			public void ok(CtxIdentifier arg0) {}

			@Override
			public void ok_list(List<CtxIdentifier> arg0) {}

			@Override
			public void ok_values(List<Object> arg0) {}

			@Override
			public void similartyResults(List<Object> arg0) {}

			@Override
			public void updateReceived(CtxModelObject arg0) {}
		});
	}

	
	//***********************************************************************
	//  
	// Context Prediction Methods
	//
	//***********************************************************************

	@Override
	public void retrieveFuture(CtxAttributeIdentifier attrId, Date date,
			IUserCtxBrokerCallback callback) {

	}

	@Override
	public void retrieveFuture(CtxAttributeIdentifier attrId,
			int modificationIndex, IUserCtxBrokerCallback callback) {
	}


	//***********************************************************************
	//  
	// Context History Methods
	//
	//***********************************************************************

	@Override
	public void retrievePast(CtxAttributeIdentifier attrId,
			int modificationIndex, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrievePast(CtxAttributeIdentifier attrId, Date startDate,
			Date endDate, IUserCtxBrokerCallback brokerCallback) {
		UserHoCDBCallback callback = new UserHoCDBCallback(brokerCallback);
		userHocDB.retrieveHistory(attrId, startDate, endDate, callback);
	}
	
	@Override
	public void setCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds,
			IUserCtxBrokerCallback callback) {
	}

	@Override
	public void getCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}

	private class UserHoCDBCallback implements IUserCtxHistoryCallback {

		private IUserCtxBrokerCallback brokerCallback;

		UserHoCDBCallback(IUserCtxBrokerCallback brokerCallback) {
			this.brokerCallback = brokerCallback;
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
		public void historyTupleIdsRetrieved(
				List<List<CtxAttributeIdentifier>> tupleIds) {
			// TODO Auto-generated method stub
		}

		@Override
		public void historyTuplesRegistered() {
			// TODO Auto-generated method stub
		}

		@Override
		public void historyRemovedByDate(int i) {
			// TODO Auto-generated method stub
		}

		@Override
		public void historyRemovedByType(int i) {
			// TODO Auto-generated method stub
		}

		@Override
		public void historyRetrievedIndex(List<CtxHistoryAttribute> history) {
			// TODO Auto-generated method stub
		}

		@Override
		public void historyRetrievedDate(List<CtxHistoryAttribute> history) {

			this.brokerCallback.historyCtxRetrieved(history);
			//System.out.println("history retrieved "+history);
		}

		@Override
		public void historyTuplesRetrieved(
				Map<CtxAttribute, List<CtxAttribute>> tuples) {
			// TODO Auto-generated method stub
		}
	}

	private class UserDBCallback implements IUserCtxDBMgrCallback {

		private IUserCtxBrokerCallback brokerCallback;

		UserDBCallback(IUserCtxBrokerCallback brokerCallback) {
			this.brokerCallback = brokerCallback;
		} 

		public void ctxEntityCreated(CtxEntity ctxEntity) {
			this.brokerCallback.ctxEntityCreated(ctxEntity);
		}

		public void ctxIndividualCtxEntityCreated(CtxEntity ctxEntity) {
			this.brokerCallback.ctxIndividualCtxEntityCreated(ctxEntity);
		}

		public void ctxAttributeCreated(CtxAttribute ctxAttribute) {
			this.brokerCallback.ctxAttributeCreated(ctxAttribute);
			//System.out.println("Broker callback : Ctx Attribute Created: " + ctxAttribute.getId());
		}

		public void ctxModelObjectUpdated(CtxModelObject ctxModelObject) {
			this.brokerCallback.ctxModelObjectUpdated(ctxModelObject);
		}

		public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list) {
			this.brokerCallback.ctxEntitiesLookedup(list);
		}

		public void ctxModelObjectRetrieved(CtxModelObject ctxModelObject) {
			this.brokerCallback.ctxModelObjectRetrieved(ctxModelObject);
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
}