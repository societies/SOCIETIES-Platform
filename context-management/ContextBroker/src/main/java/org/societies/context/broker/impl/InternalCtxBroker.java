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

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICommunityCtxBroker;
import org.societies.api.internal.context.broker.ICommunityCtxBrokerCallback;
import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;
import org.societies.api.internal.context.broker.IUserCtxBroker;
import org.societies.api.internal.context.user.db.IUserCtxDBMgr;
import org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback;
import org.societies.api.internal.context.user.prediction.PredictionMethod;

/**
 * Platform Context Broker Implementation
 * This class implements the internal context broker interfaces and the callback interface of the community context db 
 * management in order to facilitate within platform db interaction 
 */
public class InternalCtxBroker extends CtxBroker implements IUserCtxBroker, ICommunityCtxBroker {

	private IUserCtxDBMgr userDB;

	public InternalCtxBroker(IUserCtxDBMgr userDB) {
		this.userDB=userDB;
		System.out.println(this.getClass().getName()+" full");
	}

	public InternalCtxBroker() {
		System.out.println(this.getClass().getName()+ " empty");
	}

	public void setUserCtxDBMgr(IUserCtxDBMgr userDB) {
		this.userDB = userDB;
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
	public PredictionMethod getDefaultPredictionMethod(
			PredictionMethod predMethod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PredictionMethod getPredictionMethod(PredictionMethod predMethod) {
		// TODO Auto-generated method stub
		return null;
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
	public void removePredictionMethod(PredictionMethod predMethod) {
		// TODO Auto-generated method stub

	}

	@Override
	public void retrieve(CtxIdentifier identifier, IUserCtxBrokerCallback brokerCallback) {
		UserDBCallback callback = new UserDBCallback(brokerCallback);
		userDB.retrieve(identifier, callback);

	}

	@Override
	public void retrieveFuture(CtxAttributeIdentifier attrId, Date date,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void retrieveFuture(CtxAttributeIdentifier attrId,
			int modificationIndex, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void retrievePast(CtxAttributeIdentifier attrId,
			int modificationIndex, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void retrievePast(CtxAttributeIdentifier attrId, Date startDate,
			Date endDate, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefaultPredictionMethod(PredictionMethod predMethod) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPredictionMethod(PredictionMethod predMethod,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub

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
	public void update(CtxModelObject identifier,
			IUserCtxBrokerCallback brokerCallback) {
		UserDBCallback callback = new UserDBCallback(brokerCallback);
		userDB.update(identifier, callback);

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


}
