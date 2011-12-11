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
package org.societies.context.userDatabase.impl;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.internal.context.user.db.IUserCtxDBMgr;
import org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback;
import org.societies.api.mock.EntityIdentifier;

public class UserContextDBManagement implements IUserCtxDBMgr{

	
	 private final Map<CtxIdentifier, CtxModelObject> modelObjects;
	
	 EntityIdentifier entID = new EntityIdentifier();
	 
	 /**
	     * Constructs and boots the standalone edition of the UserContextDBManagement
	     */
	    public UserContextDBManagement() {
	        this.modelObjects =  new HashMap<CtxIdentifier, CtxModelObject>();
	        
	    }
	    
	    
	    
	@Override
	public void createAssociation(String arg0, IUserCtxDBMgrCallback arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createAttribute(CtxEntityIdentifier arg0,
			CtxAttributeValueType arg1, String arg2, IUserCtxDBMgrCallback arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createEntity(String type, IUserCtxDBMgrCallback arg1) {
		
		CtxEntityIdentifier identifier = new CtxEntityIdentifier(entID, type, CtxModelObjectNumberGenerator.getNextValue());
		CtxEntity ctxEntity = new  CtxEntity(identifier);
		modelObjects.put(ctxEntity.getId(), ctxEntity);
 	}

	
	public CtxEntity createEntitySynch(String type, IUserCtxDBMgrCallback arg1) {

		CtxEntityIdentifier identifier = new CtxEntityIdentifier(entID, type, CtxModelObjectNumberGenerator.getNextValue() );
		CtxEntity ctxEntity = new  CtxEntity(identifier);
		modelObjects.put(ctxEntity.getId(), ctxEntity);
		
		return ctxEntity;
	}
	
	
	@Override
	public void lookup(CtxModelType arg0, String arg1,
			IUserCtxDBMgrCallback arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lookupEntities(String arg0, String arg1, Serializable arg2,
			Serializable arg3, IUserCtxDBMgrCallback arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerForUpdates(CtxAttributeIdentifier arg0,
			IUserCtxDBMgrCallback arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerForUpdates(CtxEntityIdentifier arg0, String arg1,
			IUserCtxDBMgrCallback arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(CtxIdentifier arg0, IUserCtxDBMgrCallback arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieve(CtxIdentifier arg0, IUserCtxDBMgrCallback arg1) {
		// TODO Auto-generated method stub
		
	}

	public CtxModelObject retrieveSynch(CtxIdentifier ctxIdentifier) {
				
		return modelObjects.get(ctxIdentifier);
	}
	
	@Override
	public void unregisterForUpdates(CtxAttributeIdentifier arg0,
			IUserCtxDBMgrCallback arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterForUpdates(CtxEntityIdentifier arg0, String arg1,
			IUserCtxDBMgrCallback arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(CtxModelObject arg0, IUserCtxDBMgrCallback arg1) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void createIndividualCtxEntity(String arg0,
			IUserCtxDBMgrCallback arg1) {
		// TODO Auto-generated method stub
		
	}

}
