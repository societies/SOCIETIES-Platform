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
package org.societies.context.userHistory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxModelObjectFactory;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.context.api.user.db.IUserCtxDBMgr;
import org.societies.context.api.user.history.IUserCtxHistoryMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/test-context.xml"})
public class UserContextHistoryManagementTest {

public static final String CSS_ID = "jane.societies.local";
    	
	
	CtxAttribute attribute1;
	CtxAttribute attribute2;
	
	
    @Autowired
   	private  IUserCtxHistoryMgr userCtxHistoryDb;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testCreateHistoryDataSet() throws CtxException {
		
	
		CtxAttributeIdentifier ctxAttrID1 = new CtxAttributeIdentifier("context://john.societies.local/ENTITY/person/31/ATTRIBUTE/temperature/48");
		CtxAttributeIdentifier ctxAttrID2 = new CtxAttributeIdentifier("context://john.societies.local/ENTITY/person/31/ATTRIBUTE/locationSymbolic/40");
		
		
		attribute1 = CtxModelObjectFactory.getInstance().createAttribute(ctxAttrID1, new Date(), new Date(), "hot");
		attribute1.setHistoryRecorded(true);
		this.userCtxHistoryDb.storeHoCAttribute(attribute1);
		
		attribute1.setStringValue("warm");
		this.userCtxHistoryDb.storeHoCAttribute(attribute1);
		
		attribute1.setStringValue("mild");
		this.userCtxHistoryDb.storeHoCAttribute(attribute1);
		
		attribute1.setStringValue("cold");
		this.userCtxHistoryDb.storeHoCAttribute(attribute1);
		
		attribute1.setStringValue("freezing");
		this.userCtxHistoryDb.storeHoCAttribute(attribute1);
		
		List<CtxHistoryAttribute> hocResultsTemperature = this.userCtxHistoryDb.retrieveHistory(ctxAttrID1);
		
		System.out.println("temperature results "+hocResultsTemperature.size() );
		assertEquals(5, hocResultsTemperature.size());
		
		
		//for(CtxHistoryAttribute hocAttr: hocResultsTemperature){
		//	System.out.println("hoc attribute "+hocAttr.getStringValue() + " timestamp "+hocAttr.getLastUpdated());}
		
		
		// location history test
		Date startDate = new Date();
		Date endDate = null;
		attribute2 = CtxModelObjectFactory.getInstance().createAttribute(ctxAttrID2, new Date(), new Date(), "home");
		attribute2.setHistoryRecorded(true);
	
		for (int i=0; i<100; i++){
		this.userCtxHistoryDb.storeHoCAttribute(attribute2);
		attribute2.setStringValue("office");
		
		
		this.userCtxHistoryDb.storeHoCAttribute(attribute2);
		attribute2.setStringValue("pub");
		 
		
		
		this.userCtxHistoryDb.storeHoCAttribute(attribute2);
		attribute2.setStringValue("park");

		this.userCtxHistoryDb.storeHoCAttribute(attribute2);
		attribute2.setStringValue("restaurant");
		
		if(i==50) endDate= new Date();
		
		}
		
		List<CtxHistoryAttribute> hocResultsLocation = this.userCtxHistoryDb.retrieveHistory(ctxAttrID2);
		assertEquals(400, hocResultsLocation.size());
		System.out.println("all location results "+hocResultsLocation.size() );
		
		Date lastTime = null;
		for(CtxHistoryAttribute hocAttr: hocResultsLocation){
		//	System.out.println("hoc attribute Location: "+hocAttr.getStringValue() + " timestamp "+hocAttr.getLastUpdated());
			lastTime = hocAttr.getLastUpdated();
		}
		
		
		System.out.println("start: "+startDate.getTime()+" end "+ endDate.getTime() +" total end: "+ lastTime.getTime());
		List<CtxHistoryAttribute> hocResultsLocationTimeBased = this.userCtxHistoryDb.retrieveHistory(ctxAttrID2, startDate, endDate);
		
		System.out.println("location results time based "+hocResultsLocationTimeBased.size() );
		boolean correctNum = false;
		
		if(hocResultsLocationTimeBased.size() <= 280){
			correctNum = true;
		}
		assertTrue(correctNum);
		
				
		//for(CtxHistoryAttribute hocAttr: hocResultsLocationTimeBased){
		//System.out.println("time based hoc attribute Location dataset: "+hocAttr.getStringValue() + " timestamp "+hocAttr.getLastUpdated().getTime());}
	}

}