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


import org.societies.api.context.broker.ICommunityCtxBrokerCallback;
import org.societies.api.context.broker.IUserCtxBrokerCallback;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICommunityCtxBroker;
import org.societies.api.internal.context.broker.IUserCtxBroker;
import org.societies.api.internal.context.user.db.IUserCtxDBMgr;
import org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback;
import org.societies.api.internal.context.user.prediction.PredictionMethod;
import org.societies.api.mock.EntityIdentifier;

/*
 * Platform Context Broker Implementation
 * This class implements the internal context broker interfaces and the callback interface of the community context db 
 * management in order to facilitate within platform db interaction 
 */

public class PlatformContextBroker implements IUserCtxBroker, ICommunityCtxBroker {

	private IUserCtxDBMgr userDB = null;

	//getters and setters for DB handlers
	public IUserCtxDBMgr getUserDB() {
		return userDB;
	}

	public void setUserDB(IUserCtxDBMgr userDB) {
		this.userDB = userDB;
	}

	//ICommunityCtxBroker methods (internal)
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

	@Override
	public void retrieveAdministratingCSS(
			CtxEntityIdentifier arg0,
			org.societies.api.internal.context.broker.ICommunityCtxBrokerCallback arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveBonds(
			CtxEntityIdentifier arg0,
			org.societies.api.internal.context.broker.ICommunityCtxBrokerCallback arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveChildCommunities(
			CtxEntityIdentifier arg0,
			org.societies.api.internal.context.broker.ICommunityCtxBrokerCallback arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveCommunityMembers(
			CtxEntityIdentifier arg0,
			org.societies.api.internal.context.broker.ICommunityCtxBrokerCallback arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveParentCommunities(
			CtxEntityIdentifier arg0,
			org.societies.api.internal.context.broker.ICommunityCtxBrokerCallback arg1) {
		// TODO Auto-generated method stub
		
	}
	//end of ICommunityCtxBroker methods (internal)

	
	//IUserCtxBroker methods (internal)
	@Override
	public void createAssociation(EntityIdentifier arg0, String arg1,
			IUserCtxBrokerCallback arg2) {
		
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg2);
		userDB.createAssociation(arg1, callback);
		
	}

	@Override
	public void createAttribute(EntityIdentifier arg0,
			CtxEntityIdentifier arg1, CtxAttributeValueType arg2, String arg3,
			IUserCtxBrokerCallback arg4) {
		
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg4);
		userDB.createAttribute(arg1, arg2, arg3, callback);
	}

	@Override
	public void createEntity(EntityIdentifier arg0, String arg1,
			IUserCtxBrokerCallback arg2) {
		
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg2);
		userDB.createEntity(arg1, callback);
		
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
		UserDBCallback callback = new UserDBCallback(arg3);
		userDB.lookup(arg1, arg2, callback);
		
	}

	@Override
	public void lookupEntities(EntityIdentifier arg0, String arg1, String arg2,
			Serializable arg3, Serializable arg4, IUserCtxBrokerCallback arg5) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg5);
		userDB.lookupEntities(arg1, arg2, arg3, arg4, callback);
		
	}

	@Override
	public void registerForUpdates(EntityIdentifier arg0,
			CtxAttributeIdentifier arg1, IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg2);
		userDB.registerForUpdates(arg1, callback);
	}

	@Override
	public void registerForUpdates(EntityIdentifier arg0,
			CtxEntityIdentifier arg1, String arg2, IUserCtxBrokerCallback arg3) {
		
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg3);
		userDB.registerForUpdates(arg1, arg2, callback);
		
	}

	@Override
	public void remove(EntityIdentifier arg0, CtxIdentifier arg1,
			IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg2);
		userDB.remove(arg1, callback);
		
	}

	@Override
	public void retrieve(EntityIdentifier arg0, CtxIdentifier arg1,
			IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg2);
		userDB.retrieve(arg1, callback);
		
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
		UserDBCallback callback = new UserDBCallback(arg2);
		userDB.unregisterForUpdates(arg1, callback);
		
	}

	@Override
	public void unregisterForUpdates(EntityIdentifier arg0,
			CtxEntityIdentifier arg1, String arg2, IUserCtxBrokerCallback arg3) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg3);
		userDB.unregisterForUpdates(arg1, arg2, callback);
		
	}

	@Override
	public void update(EntityIdentifier arg0, CtxModelObject arg1,
			IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg2);
		userDB.update(arg1, callback);
		
	}

	@Override
	public void createAssociation(
			String arg0,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg1) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg1);
		userDB.createAssociation(arg0, callback);
		
	}

	@Override
	public void createAttribute(
			CtxEntityIdentifier arg0,
			CtxAttributeValueType arg1,
			String arg2,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg3) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg3);
		userDB.createAttribute(arg0, arg1, arg2, callback);
		
	}

	@Override
	public void createEntity(
			String arg0,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg1) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg1);
		userDB.createEntity(arg0, callback);
	}

	@Override
	public void disableCtxMonitoring(CtxAttributeValueType arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disableCtxRecording() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableCtxMonitoring(CtxAttributeValueType arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableCtxRecording() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evaluateSimilarity(
			Serializable arg0,
			List<Serializable> arg1,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public PredictionMethod getDefaultPredictionMethod(PredictionMethod arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PredictionMethod getPredictionMethod(PredictionMethod arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void lookup(
			CtxModelType arg0,
			String arg1,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg2);
		userDB.lookup(arg0, arg1, callback);
		
	}

	@Override
	public void lookupEntities(
			String arg0,
			String arg1,
			Serializable arg2,
			Serializable arg3,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg4) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg4);
		userDB.lookupEntities(arg0, arg1, arg2, arg3, callback);
		
	}

	@Override
	public void registerForUpdates(
			CtxAttributeIdentifier arg0,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg1) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg1);
		userDB.registerForUpdates(arg0, callback);
		
	}

	@Override
	public void registerForUpdates(
			CtxEntityIdentifier arg0,
			String arg1,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg2);
		userDB.registerForUpdates(arg0, arg1, callback);
		
	}

	@Override
	public void remove(
			CtxIdentifier arg0,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg1) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg1);
		userDB.remove(arg0, callback);
		
	}

	@Override
	public int removeHistory(String arg0, Date arg1, Date arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removePredictionMethod(PredictionMethod arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieve(
			CtxIdentifier arg0,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg1) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg1);
		userDB.retrieve(arg0, callback);
		
	}

	@Override
	public void retrieveFuture(
			CtxAttributeIdentifier arg0,
			Date arg1,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveFuture(
			CtxAttributeIdentifier arg0,
			int arg1,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrievePast(
			CtxAttributeIdentifier arg0,
			int arg1,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrievePast(
			CtxAttributeIdentifier arg0,
			Date arg1,
			Date arg2,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDefaultPredictionMethod(PredictionMethod arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPredictionMethod(
			PredictionMethod arg0,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterForUpdates(
			CtxAttributeIdentifier arg0,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg1) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg1);
		userDB.unregisterForUpdates(arg0, callback);
	}

	@Override
	public void unregisterForUpdates(
			CtxEntityIdentifier arg0,
			String arg1,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg2) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg2);
		userDB.unregisterForUpdates(arg0, arg1, callback);
		
	}

	@Override
	public void update(
			CtxModelObject arg0,
			org.societies.api.internal.context.broker.IUserCtxBrokerCallback arg1) {
		// TODO Auto-generated method stub
		UserDBCallback callback = new UserDBCallback(arg1);
		userDB.update(arg0, callback);

	}
	//end of IUserCtxBroker methods (internal)
	
	
	
	private class UserDBCallback implements IUserCtxDBMgrCallback {

	    private IUserCtxBrokerCallback brokerCallback;
	    private org.societies.api.internal.context.broker.IUserCtxBrokerCallback inBrokerCallback;

	    UserDBCallback(IUserCtxBrokerCallback brokerCallback) {
	       this.brokerCallback = brokerCallback;
	    } 

	    public UserDBCallback(
				org.societies.api.internal.context.broker.IUserCtxBrokerCallback brokerCallback) {
			// TODO Auto-generated constructor stub
	    	this.inBrokerCallback = brokerCallback;
		}

		void ctxEntityCreated(CtxEntity entity) {
	        this.brokerCallback.ctxEntityCreated(entity);
	    }
	}
}
