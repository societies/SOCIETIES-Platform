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

package org.societies.orchestration.EgocentricCommunityAnalyser.test;

import java.net.URI;
import java.util.concurrent.Future;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import org.societies.orchestration.EgocentricCommunityAnalyser.impl.EgocentricCommunityCreationManager;

import org.societies.api.identity.IIdentity;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;

import org.societies.api.internal.context.broker.ICtxBroker;

import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.internal.cis.management.ICisManager;
import org.societies.api.internal.cis.management.CisRecord;

/**
 * This is the test class for the Egocentric Community Creation Manager component
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class EgocentricCommunityCreationManagerTest {
	
	private EgocentricCommunityCreationManager egocentricCommunityCreationManager;
	private ICtxBroker userCtxBroker;
	//private IUserCtxDBMgr userCtxDBMgr;
	private CtxEntityIdentifier entityId;
	//private IUserCtxBrokerCallback userCtxBrokerCallback;
	private ICisManager cisManager;
	
	//@Test
	public void testNonExtensiveCreationCheck() {
		
		IIdentity ownerId = null; //James Jents CSS
		entityId = new CtxEntityIdentifier(ownerId, "James Jents", new Long(1));
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		
		
    	egocentricCommunityCreationManager = new EgocentricCommunityCreationManager(ownerId, "CSS");
		
    	
    	
    	//create some test context data for the CSS?
    	//ServiceResourceIdentifier userInterfacedService = new ServiceResourceIdentifier(/**new URI("Chat system")*/);
    	
    	Future <CtxEntity> theEntity = null;
    	try {
			if (userCtxBroker != null)
    		    theEntity = userCtxBroker.createEntity("CSS User");
		} catch (CtxException e) {
			e.printStackTrace();
		}
    	//userCtxBroker.addAttribute(ownerIdContextEntity, CtxAttributeValueType.INDIVIDUAL, "friends", IUserCtxBrokerCallback);
    	
    	try {
			if (userCtxBroker != null)
    		    userCtxBroker.createAttribute(entityId, "proximity");
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//check user joined CISs before
		egocentricCommunityCreationManager.identifyCissToCreate("not extensive");
		//check and compare user joined CISs after
		
		String[] members = new String[1];
		members[0] = "James";
		
		//Assert.assertNull(cisManager.getCisList(new CisRecord(null, null, null, null, null, members, null, null)));
		
		Assert.assertNull(cisManager.getCisList(new CisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	//@Test
    public void testExtensiveCreationCheck() {
    	
    	IIdentity ownerId = null; //James Jents CSS
		entityId = new CtxEntityIdentifier(ownerId, "James Jents", new Long(1));
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		
    	egocentricCommunityCreationManager = new EgocentricCommunityCreationManager(ownerId, "CSS");
		
    	
    	
    	//create some test context data for the CSS?
    	//ServiceResourceIdentifier userInterfacedService = new ServiceResourceIdentifier(/**new URI("Chat system")*/);
    
    	Future <CtxEntity> theEntity = null;
    	try {
			theEntity = userCtxBroker.createEntity("CSS User");
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	//userCtxBroker.addAttribute(ownerIdContextEntity, CtxAttributeValueType.INDIVIDUAL, "friends", IUserCtxBrokerCallback);
    	try {
    		userCtxBroker.createAttribute(entityId, "friends");
		} catch (CtxException e) {
			e.printStackTrace();
		}
    	
    	//create some test context data for the CSS?	
    	//ServiceResourceIdentifier userInterfacedService = new ServiceResourceIdentifier(/**new URI("Chat system")*/);
    	
    	//userCtxBroker = new userCtxBroker();
    	//userCtxBroker.createEntity("CSS User", IUserCtxBrokerCallback);
    	//userCtxBroker.addAttribute(ownerIdContextEntity, CtxAttributeValueType.INDIVIDUAL, "CSS proximity", IUserCtxBrokerCallback);
    	
    	//check user joined CISs before
		egocentricCommunityCreationManager.identifyCissToCreate("extensive");
		//check and compare user joined CISs after
		
		//Assert.assertNotNull(/**User's joined CISs*/);
		
		
		String[] members = new String[1];
		members[0] = "James";
		Assert.assertNull(cisManager.getCisList(new CisRecord(null, null, null, null, null, members, null, null, null)));
	}
    
    public void setCtxBroker(ICtxBroker userCtxBroker){
		this.userCtxBroker = userCtxBroker;
	}
    
    /**public void setUserCtxBrokerCallback(IUserCtxBrokerCallback userCtxBrokerCallback) {
    	this.userCtxBrokerCallback = userCtxBrokerCallback;
    }*/
    
    public void callbackForUserCtxEntity(CtxEntityIdentifier contextEntityIdentifier) {
    	entityId = contextEntityIdentifier;
    }
    
    public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}
    
    public void contextCallback(CtxEntityIdentifier context) {
    	
    }
    
}