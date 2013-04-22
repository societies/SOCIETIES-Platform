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


package org.societies.integration.test.bit.ctx_similarity_eval;
import static org.junit.Assert.*;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;


import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.internal.context.broker.ICtxBroker;


/**
 *  @author nikosk
 *
 */
public class Tester {
	
	private ICtxBroker ctxBroker;
	
	private static Logger LOG = LoggerFactory.getLogger(Tester.class);
	CtxEntity serviceA = null;
	CtxAttribute userInterestsA = null;
	CtxAttribute moviesA = null;
	
	CtxEntity serviceB = null;
	CtxAttribute userInterestsB = null;
	CtxAttribute moviesB = null;
	
	
	
	public Tester(){

	}
	
	@Before
	public void setUp(){
			
	}
	
	@After
	public void tearDown(){

		try {
			LOG.info("*** tear down **** " );
			if(serviceA != null ) this.ctxBroker.remove(serviceA.getId());
			if(serviceB != null ) this.ctxBroker.remove(serviceB.getId());
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void Test(){
		
		this.ctxBroker = Test2018.getCtxBroker();
		
		CtxEntity serviceA = null;
		CtxAttribute userInterestsA = null;
		CtxAttribute moviesA = null;
		
		CtxEntity serviceB = null;
		CtxAttribute userInterestsB = null;
		CtxAttribute moviesB = null;
		
		try {
			serviceA = this.ctxBroker.createEntity(CtxEntityTypes.SERVICE).get();
			userInterestsA = this.ctxBroker.createAttribute(serviceA.getId(), CtxAttributeTypes.INTERESTS).get();
			userInterestsA.setStringValue("Movies,Books,Sports");
			userInterestsA = (CtxAttribute) this.ctxBroker.update(userInterestsA).get();
			LOG.info("service A  " +serviceA.getId());
			
			serviceB = ctxBroker.createEntity(CtxEntityTypes.SERVICE).get();
			userInterestsB = this.ctxBroker.createAttribute(serviceB.getId(), CtxAttributeTypes.INTERESTS).get();
			userInterestsB.setStringValue("Movies,Sports");
			userInterestsB = (CtxAttribute) this.ctxBroker.update(userInterestsB).get();
			LOG.info("service  B " +serviceB.getId());
			
			LOG.info("service A interests " +userInterestsA.getStringValue() );
			
			LOG.info("service B interests " +userInterestsB.getStringValue() );
			List<Serializable> objects = new ArrayList<Serializable>();
			
			objects.add(userInterestsB.getStringValue());
			
			// this should be changed
			this.ctxBroker.evaluateSimilarity(userInterestsA.getStringValue(),objects );
			
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
	}

	
	
	
	
	
	
	
	
	
}