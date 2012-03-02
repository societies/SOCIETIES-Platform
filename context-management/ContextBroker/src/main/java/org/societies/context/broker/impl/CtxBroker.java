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
import java.util.Set;
import java.util.concurrent.Future;

import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;

import org.societies.context.api.user.db.IUserCtxDBMgr;

/**
 * 3p Context Broker Implementation
 */
public class CtxBroker implements ICtxBroker {

	private IUserCtxDBMgr userDB;
	
	public CtxBroker() throws CtxException { 
	}
	
	public CtxBroker(IUserCtxDBMgr userDB) throws CtxException {
		this.userDB = userDB;
	}

	@Override
	public Future<CtxAssociation> createAssociation(IIdentity requester,
			String type) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxAttribute> createAttribute(IIdentity requester,
			CtxEntityIdentifier scope, String type) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxEntity> createEntity(IIdentity requester,
			String type) throws CtxException {
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
	public Future<CtxModelObject> remove(IIdentity requester,
			CtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxModelObject> retrieve(IIdentity requester,
			CtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(
			IIdentity requester, CtxAttributeIdentifier attrId, Date date) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(
			IIdentity requester, CtxAttributeIdentifier attrId,
			int modificationIndex) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			IIdentity requester, CtxAttributeIdentifier attrId,
			int modificationIndex) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			IIdentity requester, CtxAttributeIdentifier attrId,
			Date startDate, Date endDate) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Future<CtxModelObject> update(IIdentity requester,
			CtxModelObject object) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CtxEntity> retrieveAdministratingCSS(
			IIdentity requester, CtxEntityIdentifier communityEntId) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(
			IIdentity requester, CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(
			IIdentity requester, CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxIdentifier>> lookup(IIdentity requester,
			CtxModelType modelType, String type) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> lookupEntities(
			IIdentity requester, String entityType, String attribType,
			Serializable minAttribValue, Serializable maxAttribValue)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerForUpdates(IIdentity requester,
			CtxAttributeIdentifier attrId) throws CtxException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void unregisterForUpdates(IIdentity requester,
			CtxAttributeIdentifier attrId) throws CtxException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void registerForUpdates(IIdentity requester,
			CtxEntityIdentifier scope, String attrType) throws CtxException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void unregisterForUpdates(IIdentity requester,
			CtxEntityIdentifier scope, String attributeType)
			throws CtxException {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#registerForChanges(org.societies.api.identity.IIdentity, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void registerForChanges(final IIdentity requester,
			final CtxChangeEventListener listener, final CtxIdentifier ctxId)
			throws CtxException {
		
		if (requester == null)
			throw new NullPointerException("requester can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");
		
		// TODO Auto-generated method stub
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.identity.IIdentity, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void unregisterFromChanges(final IIdentity requester,
			final CtxChangeEventListener listener, final CtxIdentifier ctxId)
			throws CtxException {
		
		if (requester == null)
			throw new NullPointerException("requester can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");
		
		// TODO Auto-generated method stub
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#registerForChanges(org.societies.api.identity.IIdentity, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void registerForChanges(final IIdentity requester,
			final CtxChangeEventListener listener, final CtxEntityIdentifier scope,
			final String attrType) throws CtxException {
		
		if (requester == null)
			throw new NullPointerException("requester can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (attrType == null)
			throw new NullPointerException("attrType can't be null");
		
		// TODO Auto-generated method stub
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.identity.datatypes.IIdentity, org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void unregisterFromChanges(final IIdentity requester,
			final CtxChangeEventListener listener, final CtxEntityIdentifier scope,
			final String attrType) throws CtxException {
		
		if (requester == null)
			throw new NullPointerException("requester can't be null");
		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (attrType == null)
			throw new NullPointerException("attrType can't be null");
		
		// TODO Auto-generated method stub
	}

	@Override
	public Future<Set<CtxBond>> retrieveBonds(IIdentity requester,
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(
			IIdentity requester, CtxEntityIdentifier community)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}
}