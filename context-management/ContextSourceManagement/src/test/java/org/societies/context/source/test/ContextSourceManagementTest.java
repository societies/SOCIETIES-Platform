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
package org.societies.context.source.test;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxQuality;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.context.source.impl.ContextSourceManagement;

public class ContextSourceManagementTest {
    private static ContextSourceManagement csm;
	private static Logger LOG = LoggerFactory
			.getLogger(ContextSourceManagementTest.class);
	private static ICtxBroker mockBroker = mock(ICtxBroker.class);
	private static ICommManager mockCommMgr = mock(ICommManager.class);
	

	private static Future<List<CtxEntityIdentifier>> mockFutureEntityIDs = mock(Future.class);
	private static Future<List<CtxEntityIdentifier>> mockFutureEntityIDsEmpty = mock(Future.class);
	private static Future<List<CtxIdentifier>> mockFutureIDs = mock(Future.class);
	private static List<CtxEntityIdentifier> mockEntityIDs = mock(List.class);
	private static List<CtxEntityIdentifier> mockEntityIDsEmpty = mock(List.class);
	private static List<CtxIdentifier> mockAssocIdentifierList = mock(List.class);
	private static CtxEntityIdentifier mockEntityID = mock(CtxEntityIdentifier.class);
	private static CtxIdentifier mockCtxID = mock(CtxIdentifier.class);
	private static Future<CtxModelObject> mockFutureModelObject = mock(Future.class);
	private static Future<CtxEntity> mockFutureEntity = mock(Future.class);
	private static Future<CtxModelObject> mockFutureModelObjectAttribute = mock(Future.class);
	private static CtxModelObject mockModelObject = mock(CtxModelObject.class);
	private static CtxEntity mockCtxEntity = null;
	private static Future<CtxAttribute> mockFutureAttribute = mock(Future.class);
	private static CtxAttribute mockAttribute = mock(CtxAttribute.class);
	private static CtxQuality mockQuality = mock(CtxQuality.class);
	private static Future<CtxAssociation> mockFutureAssociation = mock(Future.class);
	private static CtxAssociation mockAssociation = mock(CtxAssociation.class);
	private static INetworkNode mocknetworkNode = mock(INetworkNode.class);
	
	private static IIdentityManager mockIdManager = mock(IIdentityManager.class);
	private static INetworkNode mockNetworkNode = mock(INetworkNode.class);
	private static IEventMgr mockEventMgr = mock(IEventMgr.class);
	private static IIdentity mockIIdentity = mock(IIdentity.class);
	private static Future<IndividualCtxEntity> mockFutureIndividualEntity = mock(Future.class);
	private static IndividualCtxEntity mockIndividualEntity = mock(IndividualCtxEntity.class);
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mockCtxEntity = mock(CtxEntity.class);

		when(mockBroker.lookupEntities("CONTEXT_SOURCE", "CtxSourceId", null, null)).thenReturn(mockFutureEntityIDs);
		when(mockBroker.lookupEntities("CONTEXT_SOURCE", "CtxSourceId", "TemperatureSensor0", "TemperatureSensor0")).thenReturn(mockFutureEntityIDs);
		when(mockBroker.lookupEntities("CONTEXT_SOURCE", "CtxSourceId", "IamAnewID", "IamAnewID")).thenReturn(mockFutureEntityIDsEmpty);
		when(mockBroker.lookup(CtxModelType.ASSOCIATION, "providesUpdatesFor")).thenReturn(mockFutureIDs);
		when(mockFutureEntityIDs.get()).thenReturn(mockEntityIDs);
		when(mockEntityIDs.get(0)).thenReturn(mockEntityID);
		
		when(mockFutureEntityIDsEmpty.get()).thenReturn(mockEntityIDsEmpty);
		when(mockEntityIDsEmpty.isEmpty()).thenReturn(true);


		when(mockBroker.retrieve(mockEntityID)).thenReturn(mockFutureModelObject);
		when(mockFutureModelObject.get()).thenReturn(mockCtxEntity);

		when(mockBroker.createAttribute(mockEntityID, "data")).thenReturn(mockFutureAttribute);
		when(mockBroker.createAttribute(mockEntityID, "CtxSourceId")).thenReturn(mockFutureAttribute);
		when(mockBroker.createAttribute(mockEntityID, "CtxType")).thenReturn(mockFutureAttribute);
		when(mockFutureAttribute.get()).thenReturn(mockAttribute);

		when(mockBroker.createEntity("CONTEXT_SOURCE")).thenReturn(mockFutureEntity);
		when(mockFutureEntity.get()).thenReturn(mockCtxEntity);
		when(mockCtxEntity.getId()).thenReturn(mockEntityID);

		when(mockBroker.createAssociation("providesUpdatesFor")).thenReturn(mockFutureAssociation);
		when(mockFutureAssociation.get()).thenReturn(mockAssociation);

		when(mockBroker.update(mockAttribute)).thenReturn(mockFutureModelObjectAttribute);
		when(mockFutureModelObjectAttribute.get()).thenReturn(mockAttribute);
		when(mockAttribute.getQuality()).thenReturn(mockQuality);
		

		when(mockFutureIDs.get()).thenReturn(mockAssocIdentifierList);
		when(mockAssocIdentifierList.size()).thenReturn(0);
		
		

		when(mockCommMgr.getIdManager()).thenReturn(mockIdManager);
		when(mockIdManager.getThisNetworkNode()).thenReturn(mockNetworkNode);
		when(mockIdManager.fromJid(null)).thenReturn(mockIIdentity);
		when(mockBroker.retrieveCssNode(mockNetworkNode)).thenReturn(mockFutureEntity);
		when(mockBroker.retrieveIndividualEntity(mockIIdentity)).thenReturn(mockFutureIndividualEntity);
		when(mockFutureIndividualEntity.get()).thenReturn(mockIndividualEntity);		
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


    	csm = new ContextSourceManagement();
    	csm.setCtxBroker(mockBroker);
    	csm.setCommManager(mockCommMgr);
    	csm.setEventManager(mockEventMgr);
    	
    	csm.activate();
	}
 
    @Test
    public void testWithoutRegistration() {
    	csm.sendUpdate("IamAnewID", "SensorMeasurement");
    	
    	LOG.info("There must have been an error message");
    	assert(true);
    }
 
    @Test
    public void testWithRegistrationPure() {
    	boolean result =false;
    	try {
			String id = csm.register("TemperatureSensor", "Temperature").get();

	    	result = csm.sendUpdate(id, "Temperature").get();
	    	
	    	
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
    	
    	assert(result);
    }
 
    @Test
    public void testWithRegistrationWithEntity() {
    	boolean result =false;
    	try {
			String id = csm.register(mocknetworkNode, "TemperatureSensor", "Temperature").get();

	    	result = csm.sendUpdate(id, "Temperature",mockCtxEntity).get();
	    	
	    	
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
    	
    	assert(result);
    }
 
    @Test
    public void testWithRegistrationAndFullQoC() {
    	boolean result =false;
    	try {
			String id = csm.register("TemperatureSensor", "Temperature").get();
			LOG.info("ID is: "+id);
			
	    	result = csm.sendUpdate(id, "Temperature",mockCtxEntity, true, 1.0, 2.0).get();
	    	
	    	
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
    	
    	assert(result);
    }
    
    @After
    public void tearDown() throws Exception {
        // Code executed after each test   
    }
 
    @AfterClass
    public static void tearDownClass() throws Exception {
        csm = null; 
    }
}