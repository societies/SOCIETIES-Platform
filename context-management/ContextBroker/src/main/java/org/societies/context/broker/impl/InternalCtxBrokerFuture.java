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
import java.util.concurrent.Future;

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
import org.societies.context.api.user.db.IUserCtxDBMgr;
import org.societies.context.api.user.db.IUserCtxDBMgrCallback;
import org.societies.context.api.user.history.IUserCtxHistoryCallback;
import org.societies.context.api.user.history.IUserCtxHistoryMgr;

/**
 * Internal Context Broker Implementation
 * This class implements the internal context broker interfaces and orchestrates the db 
 * management 
 */
public class InternalCtxBrokerFuture extends CtxBrokerFuture implements ICtxBroker {

	private IUserCtxDBMgr userDB;

	private IUserCtxHistoryMgr userHocDB;

	public InternalCtxBrokerFuture(IUserCtxDBMgr userDB,IUserCtxHistoryMgr userHocDB) {
		this.userDB=userDB;
		this.userHocDB = userHocDB;
		// TODO Use logging.debug
		//System.out.println(this.getClass().getName()+" full");
	}

	public InternalCtxBrokerFuture() {
		// TODO Use logging.debug
		//System.out.println(this.getClass().getName()+ " empty");
	}

	public void setUserCtxDBMgr(IUserCtxDBMgr userDB) {
		this.userDB = userDB;
	}

	public void setUserCtxHistoryMgr (IUserCtxHistoryMgr userHocDB) {
		this.userHocDB = userHocDB;
	}


	@Override
	public Future<CtxAssociation> createAssociation(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxAttribute> createAttribute(CtxEntityIdentifier scope,
			String type) {
		// TODO Auto-generated method stub		
		return null;
	}

	@Override
	public Future<CtxEntity> createEntity(String type) {
		// TODO Auto-generated method stub
		return null;
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
	public Future<List<CtxModelObject>> lookup(CtxModelType modelType,
			String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntity>> lookupEntities(String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<Object>> registerForUpdates(CtxEntityIdentifier scope,
			String attrType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<Object>> registerForUpdates(CtxAttributeIdentifier attrId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxModelObject> remove(CtxIdentifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int removeHistory(String type, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Future<CtxModelObject> retrieve(CtxIdentifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(
			CtxAttributeIdentifier attrId, Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(
			CtxAttributeIdentifier attrId, int modificationIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrievePast(
			CtxAttributeIdentifier attrId, int modificationIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrievePast(
			CtxAttributeIdentifier attrId, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unregisterForUpdates(CtxAttributeIdentifier attrId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Future<List<Object>> unregisterForUpdates(CtxEntityIdentifier scope,
			String attributeType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxModelObject> update(CtxModelObject identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxAttribute> updateAttribute(
			CtxAttributeIdentifier attributeId, Serializable value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxAttribute> updateAttribute(
			CtxAttributeIdentifier attributeId, Serializable value,
			String valueMetric) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Boolean> setCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttributeIdentifier>> getCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttributeIdentifier>> updateCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Boolean> removeCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxEntity> retrieveAdministratingCSS(
			CtxEntityIdentifier community) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxAttribute> retrieveBonds(CtxEntityIdentifier community) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveChildCommunities(
			CtxEntityIdentifier community) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(
			CtxEntityIdentifier community) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(
			CtxEntityIdentifier community) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class UserDBCallback implements IUserCtxDBMgrCallback {

		private ICtxBroker brokerCallback;

		UserDBCallback(ICtxBroker brokerCallback) {
			this.brokerCallback = brokerCallback;
		} 

		public void ctxEntityCreated(CtxEntity ctxEntity) {}

		public void ctxIndividualCtxEntityCreated(CtxEntity ctxEntity) {}

		public void ctxAttributeCreated(CtxAttribute ctxAttribute) {}

		public void ctxModelObjectUpdated(CtxModelObject ctxModelObject) {}

		public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list) {}

		public void ctxModelObjectRetrieved(CtxModelObject ctxModelObject) {}
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
