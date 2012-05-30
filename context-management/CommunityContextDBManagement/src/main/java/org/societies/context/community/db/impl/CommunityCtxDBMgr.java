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
package org.societies.context.community.db.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.context.api.community.db.ICommunityCtxDBMgr;
import org.societies.context.api.event.CtxChangeEventTopic;
import org.societies.context.api.event.CtxEventScope;
import org.societies.context.api.event.ICtxEventMgr;
import org.societies.context.user.db.impl.CtxModelObjectNumberGenerator;
import org.societies.context.user.db.impl.UserCtxDBMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This component is responsible for establishing the functionality of the 
 * Context DB Management at CIS level, i.e. for the community context data.
 * 
 * @author
 * 
 */
@Service("communityCtxDBMgr")
public class CommunityCtxDBMgr implements ICommunityCtxDBMgr {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(UserCtxDBMgr.class);
	
	/** The Context Event Mgmt service reference. */
	@Autowired(required=true)
	private ICtxEventMgr ctxEventMgr;

	private final ConcurrentMap<CtxIdentifier, CtxModelObject> modelObjects;
//	private final Map<CtxIdentifier, CtxModelObject> modelObjects;

	private final IIdentityManager idMgr;
	
	private final IIdentity privateId;
	
	// TODO Remove and instantiate privateId properly so that privateId.toString() can be used instead
	private final String privateIdtoString = "myFooIIdentity@societies.local";
	
	@Autowired(required=true)
	CommunityCtxDBMgr (ICommManager commMgr) {

		LOG.info(this.getClass() + " instantiated");
		this.modelObjects =  new ConcurrentHashMap<CtxIdentifier, CtxModelObject>();
		
		this.idMgr = commMgr.getIdManager();
		privateId = idMgr.getThisNetworkNode();
		
	}

	/*
	 * Used for JUnit testing only
	 */
	public CommunityCtxDBMgr() {
		
		LOG.info(this.getClass() + " instantiated - fooId");
		this.modelObjects =  new ConcurrentHashMap<CtxIdentifier, CtxModelObject>();
		
		// TODO !!!!!! Identity should be instantiated properly
		this.privateId = null;
		this.idMgr = null;
	}
	
	public Future<CommunityCtxEntity> createCommunityEntity(IIdentity cisId) throws CtxException {
		//to type de xreiazetai giati prepei na mpainei karfwta iso me CtxEntityTypes.COMMUNITY
				
		return null;
	}
	
	public Future<CommunityCtxEntity> retrieveCommunityEntity(CtxEntityIdentifier ctxId) throws CtxException {

		return null;
	}

	public Future<CommunityCtxEntity> updateCommunityEntity(CommunityCtxEntity entity) throws CtxException {
	
		return null;
	}

	public Future<CommunityCtxEntity> removeCommunityEntity(CtxEntityIdentifier ctxId) throws CtxException {
		
		return null;
	}
	
	/**
	  * This method retrieves the CSS that is assigned with the community administration role.
	  * @param communityId
	  * @throws CtxException 
	  * @since 0.2
	  */
	public Future<IndividualCtxEntity> retrieveAdministratingCss(CtxEntityIdentifier communityId) throws CtxException {
		
		return null;
	}

	/**
	  * Retrieves the context bond(s) of the specified community. The community is identified by the CtxEntityIdentifier.
	  * 
	  * @param communityId
	  * @throws CtxException 
	  * @since 0.2
	  */
//	public Future<Set<CtxBond>> retrieveBonds(CtxEntityIdentifier communityId) throws CtxException {
		
//		return null;
//	}
	
	public CtxBond retrieveBonds(CtxEntityIdentifier arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	  * Retrieves the sub-communities of the specified community entity.
	  *  
	  * @param communityId
	  * @throws CtxException 
	  * @since 0.2
	  */
	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(CtxEntityIdentifier communityId) throws CtxException {
		
		return null;
	}
	
	
	@Override
	public List<CtxEntityIdentifier> retrieveChildCommunities(
			CtxEntityIdentifier arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	  * Retrieves a list of the members (individuals or subcommunities) of the specified community Entity.
	  * 
	  * @param communityId
	  * @throws CtxException 
	  * @since 0.2
	  */
//	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(CtxEntityIdentifier communityId) throws CtxException {
		
//		return null;
//	}

	@Override
	public List<CtxEntityIdentifier> retrieveCommunityMembers(
			CtxEntityIdentifier arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	  * Retrieves the parent communities of the specified community. This applies for community hierarchies. 
	  * 
	  * @param communityId
	  * @throws CtxException 
	  * @since 0.2
	  */
//	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(CtxEntityIdentifier communityId) throws CtxException {
	
//		return null;
//	}
	
	@Override
	public List<CtxEntityIdentifier> retrieveParentCommunities(
			CtxEntityIdentifier arg0) {
		// TODO Auto-generated method stub
		return null;
	} 

}
