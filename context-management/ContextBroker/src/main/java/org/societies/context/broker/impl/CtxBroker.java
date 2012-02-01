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

import org.societies.api.context.broker.ICommunityCtxBroker;
import org.societies.api.context.broker.ICommunityCtxBrokerCallback;
import org.societies.api.context.broker.IUserCtxBroker;
import org.societies.api.context.broker.IUserCtxBrokerCallback;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.mock.EntityIdentifier;

import org.societies.context.api.user.db.IUserCtxDBMgr;

/**
 * 3p Context Broker Implementation
 * This class implements the community and user broker api methods along with the callback methods 
 * of the internal context broker api
 */
public class CtxBroker implements IUserCtxBroker, ICommunityCtxBroker {

	private IUserCtxDBMgr userDB;
	
	public CtxBroker() {
	}
	
	public CtxBroker(IUserCtxDBMgr userDB) {
		this.userDB = userDB;
	}
		
	//ICommunityCtxBroker methods
	@Override
	public void retrieveAdministratingCSS(EntityIdentifier arg0,
			CtxEntityIdentifier arg1, ICommunityCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrieveBonds(EntityIdentifier arg0, CtxEntityIdentifier arg1,
			ICommunityCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrieveChildCommunities(EntityIdentifier arg0,
			CtxEntityIdentifier arg1, ICommunityCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrieveCommunityMembers(EntityIdentifier arg0,
			CtxEntityIdentifier arg1, ICommunityCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrieveParentCommunities(EntityIdentifier arg0,
			CtxEntityIdentifier arg1, ICommunityCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
	}
	// end of ICommunityCtxBroker methods

	//IUserCtxBroker methods
	@Override
	public void createAssociation(EntityIdentifier arg0, String arg1,
			IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void createAttribute(EntityIdentifier arg0,
			CtxEntityIdentifier arg1, CtxAttributeValueType arg2, String arg3,
			IUserCtxBrokerCallback arg4) {
		// TODO Auto-generated method stub
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.IUserCtxBroker#createAttribute(org.societies.api.mock.EntityIdentifier, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String, org.societies.api.context.broker.IUserCtxBrokerCallback)
	 */
	@Override
	public void createAttribute(EntityIdentifier requester,
			CtxEntityIdentifier scope, String type, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
	}	

	@Override
	public void createEntity(EntityIdentifier arg0, String arg1,
			IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void evaluateSimilarity(Serializable arg0, List<Serializable> arg1,
			IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void lookup(EntityIdentifier arg0, CtxModelType arg1, String arg2,
			IUserCtxBrokerCallback arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void lookupEntities(EntityIdentifier arg0, String arg1, String arg2,
			Serializable arg3, Serializable arg4, IUserCtxBrokerCallback arg5) {
		// TODO Auto-generated method stub
	}

	@Override
	public void registerForUpdates(EntityIdentifier arg0,
			CtxAttributeIdentifier arg1, IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void registerForUpdates(EntityIdentifier arg0,
			CtxEntityIdentifier arg1, String arg2, IUserCtxBrokerCallback arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(EntityIdentifier arg0, CtxIdentifier arg1,
			IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrieve(EntityIdentifier arg0, CtxIdentifier arg1,
			IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrieveFuture(EntityIdentifier arg0,
			CtxAttributeIdentifier arg1, Date arg2, IUserCtxBrokerCallback arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrieveFuture(EntityIdentifier arg0,
			CtxAttributeIdentifier arg1, int arg2, IUserCtxBrokerCallback arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrievePast(EntityIdentifier arg0,
			CtxAttributeIdentifier arg1, int arg2, IUserCtxBrokerCallback arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void retrievePast(EntityIdentifier arg0,
			CtxAttributeIdentifier arg1, Date arg2, Date arg3,
			IUserCtxBrokerCallback arg4) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unregisterForUpdates(EntityIdentifier arg0,
			CtxAttributeIdentifier arg1, IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void unregisterForUpdates(EntityIdentifier arg0,
			CtxEntityIdentifier arg1, String arg2, IUserCtxBrokerCallback arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(EntityIdentifier arg0, CtxModelObject arg1,
			IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
	}
}