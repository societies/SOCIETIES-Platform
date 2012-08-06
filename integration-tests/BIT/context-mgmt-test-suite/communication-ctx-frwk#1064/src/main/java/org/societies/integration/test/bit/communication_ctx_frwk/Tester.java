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

package org.societies.integration.test.bit.communication_ctx_frwk;

import java.util.concurrent.ExecutionException;


import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;


/**
 * Utility class that creates mock actions
 *
 * @author Nikos
 *
 */
public class Tester {
	
	private static Logger LOG = LoggerFactory.getLogger(Test1064.class);
	Requestor requestor = null;
	
	public Tester(){
		LOG.info("*** " + this.getClass() + " starting");
	}
	
	@Before
	public void setUp(){
			
	}
	
	
	@Test
	public void Test(){
		
		LOG.info("*** REMOTE CM TEST STARTING ***");
		LOG.info("*** " + this.getClass() + " instantiated");
		LOG.info("*** ctxBroker service :"+Test1064.getCtxBroker());
		
		try {								
			INetworkNode cssNodeId = Test1064.getCommManager().getIdManager().getThisNetworkNode();
			final String cssOwnerStr = cssNodeId.getBareJid();
			IIdentity cssOwnerId = Test1064.getCommManager().getIdManager().fromJid(cssOwnerStr);
			this.requestor = new Requestor(cssOwnerId);
			LOG.info("*** requestor = " + this.requestor);
						
			// this should be set with the use of identManager
			// in current test a null targetCss creates the entity in local cm 
			IIdentity targetCss = null;
			
			CtxEntity entity = Test1064.getCtxBroker().createEntity(requestor, targetCss, CtxEntityTypes.PERSON).get();
			LOG.info("entity person created based on 3p broker "+entity);
			LOG.info("entity person created based on 3p broker entity id :"+entity.getId());	
	    	
	    	
		
	    	CtxEntityIdentifier entityID = entity.getId();
	       	
	    	LOG.info(" scope entityID "+entityID.toString());
	    	// this null is set in order to trigger remote call
	    	entityID.setOwnerId("null");
	    	LOG.info(" scope entityID "+entityID.toString());
	    
	    	LOG.info("create attribute BIRTHDAY ");
	    	CtxAttribute attribute = Test1064.getCtxBroker().createAttribute(requestor, entityID, CtxAttributeTypes.BIRTHDAY).get();
	    	LOG.info("attribute BIRTHDAY created based on 3p broker "+attribute.getId());
		    	
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//private createRemoteEntity(){	}
	
	
}
