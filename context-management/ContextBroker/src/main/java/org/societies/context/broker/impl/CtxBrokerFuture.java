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
import java.util.concurrent.Future;

import org.societies.api.context.broker.ICommunityCtxBroker;
import org.societies.api.context.broker.ICommunityCtxBrokerCallback;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.broker.IUserCtxBroker;
import org.societies.api.context.broker.IUserCtxBrokerCallback;
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
import org.societies.api.mock.EntityIdentifier;

import org.societies.context.api.user.db.IUserCtxDBMgr;

/**
 * 3p Context Broker Implementation
 */
public class CtxBrokerFuture implements ICtxBroker {

	private IUserCtxDBMgr userDB;
	
	public CtxBrokerFuture() {
	}
	
	public CtxBrokerFuture(IUserCtxDBMgr userDB) {
		this.userDB = userDB;
	}

	@Override
	public Future<CtxAssociation> createAssociation(EntityIdentifier requester,
			String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxAttribute> createAttribute(EntityIdentifier requester,
			CtxEntityIdentifier scope, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxEntity> createEntity(EntityIdentifier requester,
			String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<Object>> evaluateSimilarity(
			Serializable objectUnderComparison,
			List<Serializable> referenceObjects) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxModelObject>> lookup(EntityIdentifier requester,
			CtxModelType modelType, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntity>> lookupEntities(EntityIdentifier requester,
			String entityType, String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<Object>> registerForUpdates(EntityIdentifier requester,
			CtxEntityIdentifier scope, String attrType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<Object>> registerForUpdates(EntityIdentifier requester,
			CtxAttributeIdentifier attrId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxModelObject> remove(EntityIdentifier requester,
			CtxIdentifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxModelObject> retrieve(EntityIdentifier requester,
			CtxIdentifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(
			EntityIdentifier requester, CtxAttributeIdentifier attrId, Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(
			EntityIdentifier requester, CtxAttributeIdentifier attrId,
			int modificationIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrievePast(
			EntityIdentifier requester, CtxAttributeIdentifier attrId,
			int modificationIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrievePast(
			EntityIdentifier requester, CtxAttributeIdentifier attrId,
			Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<Object>> unregisterForUpdates(
			EntityIdentifier requester, CtxAttributeIdentifier attrId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<Object>> unregisterForUpdates(
			EntityIdentifier requester, CtxEntityIdentifier scope,
			String attributeType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxModelObject> update(EntityIdentifier requester,
			CtxModelObject object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxEntity> retrieveAdministratingCSS(
			EntityIdentifier requester, CtxEntityIdentifier communityEntId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveBonds(EntityIdentifier requester,
			CtxEntityIdentifier community) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveChildCommunities(
			EntityIdentifier requester, CtxEntityIdentifier community) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(
			EntityIdentifier requester, CtxEntityIdentifier community) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(
			EntityIdentifier requester, CtxEntityIdentifier community) {
		// TODO Auto-generated method stub
		return null;
	}
		
	
}
