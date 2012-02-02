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
package org.societies.context.broker.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.util.SerialisationHelper;

import org.societies.context.broker.impl.InternalCtxBrokerFuture;

import org.societies.context.broker.test.util.MockBlobClass;
import org.societies.context.user.db.impl.UserCtxDBMgr;
import org.societies.context.userHistory.impl.UserContextHistoryManagement;

/**
 * Describe your class here...
 *
 * @author 
 *
 */
public class InternalCtxBrokerFutureTest {

	private InternalCtxBrokerFuture internalCtxBroker;

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
		internalCtxBroker = new InternalCtxBrokerFuture();
		internalCtxBroker.setUserCtxDBMgr(new UserCtxDBMgr());
		internalCtxBroker.setUserCtxHistoryMgr(new UserContextHistoryManagement());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		internalCtxBroker = null;
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#evaluateSimilarity(java.io.Serializable, java.util.List, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testEvaluateSimilarity() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createAttribute(org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.context.model.CtxAttributeValueType, java.lang.String)}.
	 */
	@Test
	public void testCreateAttributeByCtxEntityIdentifierCtxAttributeValueTypeString() {

		final CtxAttribute ctxAttribute;
		final CtxEntity ctxEntity;


		// Create the attribute's scope
		
		try {
			Future<CtxEntity> futureCtxEntity = internalCtxBroker.createEntity("entType");
			ctxEntity = futureCtxEntity.get();

			// Create the attribute to be tested
			Future<CtxAttribute> futureCtxAttribute =  internalCtxBroker.createAttribute(ctxEntity.getId(), "attrType");
			ctxAttribute =  futureCtxAttribute.get();

			assertNotNull(ctxAttribute.getId());
			assertEquals(ctxAttribute.getId().getScope(), ctxEntity.getId());
			assertTrue(ctxAttribute.getType().equalsIgnoreCase("attrType"));
			//System.out.println("ctxAttribute: "+ctxAttribute.getId());
			//System.out.println("ctxEntity: "+ctxEntity.getId());

		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createEntity(java.lang.String, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	
	@Test
	public void testCreateEntityByString() {
		
		final CtxEntity ctxEntity;
		
		final Future<CtxEntity> futureCtxEntity;
		
		try {
			futureCtxEntity = internalCtxBroker.createEntity("entType");
			ctxEntity = futureCtxEntity.get();
			assertNotNull(ctxEntity.getId());
			//System.out.println("ctxEntity: "+ctxEntity.getId());
			assertTrue(ctxEntity.getType().equalsIgnoreCase("entType"));
		
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		



		//assertNotNull(entity.getId());
		//assertTrue(entity.getType().equalsIgnoreCase("entType"));
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createAssociation(java.lang.String, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testCreateAssociationStringIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveAdministratingCSS(org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.internal.context.broker.ICommunityCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testRetrieveAdministratingCSSCtxEntityIdentifierICommunityCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveBonds(org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.internal.context.broker.ICommunityCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testRetrieveBondsCtxEntityIdentifierICommunityCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveChildCommunities(org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.internal.context.broker.ICommunityCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testRetrieveChildCommunitiesCtxEntityIdentifierICommunityCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveCommunityMembers(org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.internal.context.broker.ICommunityCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testRetrieveCommunityMembersCtxEntityIdentifierICommunityCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveParentCommunities(org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.internal.context.broker.ICommunityCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testRetrieveParentCommunitiesCtxEntityIdentifierICommunityCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#disableCtxMonitoring(org.societies.api.context.model.CtxAttributeValueType)}.
	 */
	@Ignore
	@Test
	public void testDisableCtxMonitoring() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#disableCtxRecording()}.
	 */
	@Ignore
	@Test
	public void testDisableCtxRecording() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#enableCtxMonitoring(org.societies.api.context.model.CtxAttributeValueType)}.
	 */
	@Ignore
	@Test
	public void testEnableCtxMonitoring() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#enableCtxRecording()}.
	 */
	@Ignore
	@Test
	public void testEnableCtxRecording() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#getDefaultPredictionMethod(org.societies.api.internal.context.user.prediction.PredictionMethod)}.
	 */
	@Ignore
	@Test
	public void testGetDefaultPredictionMethod() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#getPredictionMethod(org.societies.api.internal.context.user.prediction.PredictionMethod)}.
	 */
	@Ignore
	@Test
	public void testGetPredictionMethod() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#lookup(org.societies.api.context.model.CtxModelType, java.lang.String, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testLookupCtxModelTypeStringIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#lookupEntities(java.lang.String, java.lang.String, java.io.Serializable, java.io.Serializable, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testLookupEntitiesStringStringSerializableSerializableIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#registerForUpdates(org.societies.api.context.model.CtxEntityIdentifier, java.lang.String, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testRegisterForUpdatesCtxEntityIdentifierStringIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#registerForUpdates(org.societies.api.context.model.CtxAttributeIdentifier, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testRegisterForUpdatesCtxAttributeIdentifierIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#remove(org.societies.api.context.model.CtxIdentifier, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testRemoveCtxIdentifierIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#removeHistory(java.lang.String, java.util.Date, java.util.Date)}.
	 */
	@Ignore
	@Test
	public void testRemoveHistory() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#removePredictionMethod(org.societies.api.internal.context.user.prediction.PredictionMethod)}.
	 */
	@Ignore
	@Test
	public void testRemovePredictionMethod() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieve(org.societies.api.context.model.CtxIdentifier, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testRetrieveCtxIdentifierIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveFuture(org.societies.api.context.model.CtxAttributeIdentifier, java.util.Date, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testRetrieveFutureCtxAttributeIdentifierDateIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveFuture(org.societies.api.context.model.CtxAttributeIdentifier, int, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testRetrieveFutureCtxAttributeIdentifierIntIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrievePast(org.societies.api.context.model.CtxAttributeIdentifier, int, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testRetrievePastCtxAttributeIdentifierIntIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrievePast(org.societies.api.context.model.CtxAttributeIdentifier, java.util.Date, java.util.Date, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testRetrievePastCtxAttributeIdentifierDateDateIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#setDefaultPredictionMethod(org.societies.api.internal.context.user.prediction.PredictionMethod)}.
	 */
	@Ignore
	@Test
	public void testSetDefaultPredictionMethod() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#setPredictionMethod(org.societies.api.internal.context.user.prediction.PredictionMethod, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testSetPredictionMethod() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#unregisterForUpdates(org.societies.api.context.model.CtxAttributeIdentifier, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testUnregisterForUpdatesCtxAttributeIdentifierIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#unregisterForUpdates(org.societies.api.context.model.CtxEntityIdentifier, java.lang.String, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testUnregisterForUpdatesCtxEntityIdentifierStringIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#update(org.societies.api.context.model.CtxModelObject, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testUpdateByCtxEntityIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#update(org.societies.api.context.model.CtxModelObject)}.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	
	@Test
	public void testUpdateByCtxAttribute() throws IOException, ClassNotFoundException {

		final CtxAttribute emptyCtxAttribute;
		final CtxAttribute initialisedCtxAttribute;
		 
		CtxAttribute updatedCtxAttribute;
		final CtxEntity ctxEntity;

		
		// Create the attribute's scope
		try {
			internalCtxBroker.createEntity("entType");
			Future<CtxEntity> futureCtxEntity = internalCtxBroker.createEntity("entType");
			ctxEntity = futureCtxEntity.get();

			// Create the attribute to be tested
			Future<CtxAttribute> futureCtxAttribute = internalCtxBroker.createAttribute(ctxEntity.getId(), "attrType");
			emptyCtxAttribute = futureCtxAttribute.get();

			// Set the attribute's initial value
			emptyCtxAttribute.setIntegerValue(100);
			Future<CtxModelObject> futureCtxModelObject = internalCtxBroker.update(emptyCtxAttribute);
			updatedCtxAttribute = (CtxAttribute) futureCtxModelObject.get();
			//System.out.println("ctxAttribute id: "+updatedCtxAttribute.getId());
			//System.out.println("ctxAttribute getValueMetric(): "+updatedCtxAttribute.getValueMetric());
			//System.out.println("ctxAttribute getIntegerValue(): "+updatedCtxAttribute.getIntegerValue());
			
			// Verify the initial attribute value
			assertEquals(new Integer(100), updatedCtxAttribute.getIntegerValue());

			// Update the attribute value
			updatedCtxAttribute.setIntegerValue(200);
			futureCtxModelObject = internalCtxBroker.update(updatedCtxAttribute);
			updatedCtxAttribute = (CtxAttribute) futureCtxModelObject.get();
			
			// Verify updated attribute value
			updatedCtxAttribute = (CtxAttribute) futureCtxModelObject.get();
			assertEquals(new Integer(200), updatedCtxAttribute.getIntegerValue());
			
			// Test update with a binary value
			final CtxAttribute binaryAttribute;
			final MockBlobClass blob = new MockBlobClass(666);
			final byte[] blobBytes;

			blobBytes = SerialisationHelper.serialise(blob);
			updatedCtxAttribute.setBinaryValue(blobBytes);
			futureCtxModelObject = internalCtxBroker.update(updatedCtxAttribute);
			
			// Verify binary attribute value
			binaryAttribute = (CtxAttribute) futureCtxModelObject.get();
			
			assertNull(binaryAttribute.getIntegerValue());
			assertNotNull(binaryAttribute.getBinaryValue());
			final MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.
					deserialise(binaryAttribute.getBinaryValue(), this.getClass().getClassLoader());
			assertEquals(blob, retrievedBlob);
			//System.out.println("ctxAttribute getBlobValue(): "+retrievedBlob);	
		
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		// Verify binary attribute value
		binaryAttribute = (CtxAttribute) callback.getModelObject();
		assertNull(binaryAttribute.getIntegerValue());
		assertNotNull(binaryAttribute.getBinaryValue());
		final MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.
				deserialise(binaryAttribute.getBinaryValue(), this.getClass().getClassLoader());
		assertEquals(blob, retrievedBlob);
		*/
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#update(org.societies.api.context.model.CtxModelObject, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testUpdateByCtxAssociationIUserCtxBrokerCallback() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#updateAttribute(CtxAttributeIdentifier, java.io.Serializable, String, IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testUpdateAttributeByCtxAttributeIdSerializableStringUserCtxBrokerCallback() {

		final CtxAttribute emptyAttribute;
		final CtxAttribute initialisedAttribute;
		final CtxAttribute updatedAttribute;
		final CtxEntity entity;
		/*
		final BrokerCallback callback = new BrokerCallback();

		// Create the attribute's scope
		internalCtxBroker.createEntity("entType", callback);
		entity = (CtxEntity) callback.getModelObject();

		// Create the attribute to be tested
		internalCtxBroker.createAttribute(entity.getId(), "attrType", callback);
		emptyAttribute = (CtxAttribute) callback.getModelObject();

		// Set the attribute's initial value
		internalCtxBroker.updateAttribute(emptyAttribute.getId(), new Integer(100), "valueMetric", callback);

		// Verify the initial attribute value
		initialisedAttribute = (CtxAttribute) callback.getModelObject();
		assertEquals(new Integer(100), initialisedAttribute.getIntegerValue());

		// Update the attribute value
		internalCtxBroker.updateAttribute(initialisedAttribute.getId(), new Integer(200), "valueMetric", callback);

		// Verify updated attribute value
		updatedAttribute = (CtxAttribute) callback.getModelObject();
		assertEquals(new Integer(200), updatedAttribute.getIntegerValue());
		/* TODO
		// Test update with a binary value
		final CtxAttribute binaryAttribute;
		final MockBlobClass blob = new MockBlobClass(666);
		final byte[] blobBytes;

		blobBytes = SerialisationHelper.serialise(blob);
		updatedAttribute.setBinaryValue(blobBytes);
		internalCtxBroker.update(updatedAttribute, callback);

		// Verify binary attribute value
		binaryAttribute = (CtxAttribute) callback.getModelObject();
		assertNull(binaryAttribute.getIntegerValue());
		assertNotNull(binaryAttribute.getBinaryValue());
		final MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.
				deserialise(binaryAttribute.getBinaryValue(), this.getClass().getClassLoader());
		assertEquals(blob, retrievedBlob);
		 */
	}


}