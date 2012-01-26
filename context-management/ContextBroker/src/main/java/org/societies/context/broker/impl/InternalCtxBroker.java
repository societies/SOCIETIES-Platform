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
import org.societies.api.internal.context.user.prediction.PredictionMethod;


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
		System.out.println(this.getClass().getName()+" full");
	}

	public InternalCtxBroker() {
		System.out.println(this.getClass().getName()+ " empty");
	}

	public void setUserCtxDBMgr(IUserCtxDBMgr userDB) {
		this.userDB = userDB;
	}


	public void setUserCtxHistoryMgr (IUserCtxHistoryMgr userHocDB) {
		this.userHocDB = userHocDB;
	}


	@Override
	public void createAttribute(CtxEntityIdentifier scope,CtxAttributeValueType enumerate, String type,
			IUserCtxBrokerCallback brokerCallback) {
		UserDBCallback callback = new UserDBCallback(brokerCallback);
		userDB.createAttribute(scope, enumerate, type, callback);
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


	
	private class InternalCtxBrokerCallback implements IUserCtxBrokerCallback{

		@Override
		public void cancel(CtxIdentifier c_id, String reason) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxAssociationCreated(CtxAssociation ctxEntity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxAttributeCreated(CtxAttribute ctxAttribute) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxEntityCreated(CtxEntity ctxEntity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxIndividualCtxEntityCreated(CtxEntity ctxEntity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxModelObjectRemoved(CtxModelObject ctxModelObject) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxModelObjectRetrieved(CtxModelObject ctxModelObject) {
	
		}

		@Override
		public void ctxModelObjectsLookedup(List<CtxIdentifier> list) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxModelObjectUpdated(CtxModelObject ctxModelObject) {
			// TODO Auto-generated method stub

		}

		@Override
		public void futureCtxRetrieved(List<CtxAttribute> futCtx) {
			// TODO Auto-generated method stub

		}

		@Override
		public void futureCtxRetrieved(CtxAttribute futCtx) {
			// TODO Auto-generated method stub

		}

		@Override
		public void historyCtxRetrieved(CtxHistoryAttribute hoc) {
			// TODO Auto-generated method stub

		}

		@Override
		public void historyCtxRetrieved(List<CtxHistoryAttribute> hoc) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ok(CtxIdentifier c_id) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ok_list(List<CtxIdentifier> list) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ok_values(List<Object> list) {
			// TODO Auto-generated method stub

		}

		@Override
		public void similartyResults(List<Object> results) {
			// TODO Auto-generated method stub

		}

		@Override
		public void updateReceived(CtxModelObject ctxModelObj) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxHistoryTuplesSet(Boolean flag) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void ctxHistoryTuplesRetrieved(
				List<CtxAttributeIdentifier> listOfEscortingAttributeIds) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void ctxHistoryTuplesUpdated(
				List<CtxAttributeIdentifier> listOfEscortingAttributeIds) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void ctxHistoryTuplesRemoved(Boolean flag) {
			// TODO Auto-generated method stub
			
		}

	}
}