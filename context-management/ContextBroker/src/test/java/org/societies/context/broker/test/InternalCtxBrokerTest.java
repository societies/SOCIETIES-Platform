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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
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
import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;
import org.societies.context.broker.impl.InternalCtxBroker;
import org.societies.context.broker.test.util.MockBlobClass;
import org.societies.context.user.db.impl.UserCtxDBMgr;

/**
 * Describe your class here...
 *
 * @author 
 *
 */
public class InternalCtxBrokerTest {

	private InternalCtxBroker internalCtxBroker;
	
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
		internalCtxBroker = new InternalCtxBroker();
		internalCtxBroker.setUserCtxDBMgr(new UserCtxDBMgr());
		//internalCtxBroker.setUserCtxHistoryMgr(new UserContextHistoryManagement());
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
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createAttribute(org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.context.model.CtxAttributeValueType, java.lang.String, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Test
	public void testCreateAttributeByCtxEntityIdentifierCtxAttributeValueTypeStringIUserCtxBrokerCallback() {
		
		final CtxAttribute attribute;
		final CtxEntity entity;
		final BrokerCallback callback = new BrokerCallback();
		
		// Create the attribute's scope
		internalCtxBroker.createEntity("entType", callback);
		entity = (CtxEntity) callback.getModelObject();
		
		// Create the attribute to be tested
		internalCtxBroker.createAttribute(entity.getId(), CtxAttributeValueType.INDIVIDUAL, "attrType", callback);
		assertNotNull(callback.getModelObject());
		assertTrue(callback.getModelObject() instanceof CtxAttribute);
		attribute = (CtxAttribute) callback.getModelObject();
		assertNotNull(attribute.getId());
		assertEquals(attribute.getId().getScope(), entity.getId());
		assertTrue(attribute.getType().equalsIgnoreCase("attrType"));
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createEntity(java.lang.String, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Test
	public void testCreateEntityByStringIUserCtxBrokerCallback() {
		
		final CtxEntity entity;
		final BrokerCallback callback = new BrokerCallback();
		
		// Create the entity to be tested
		internalCtxBroker.createEntity("entType", callback);
		assertNotNull(callback.getModelObject());
		assertTrue(callback.getModelObject() instanceof CtxEntity);
		entity = (CtxEntity) callback.getModelObject();
		assertNotNull(entity.getId());
		assertTrue(entity.getType().equalsIgnoreCase("entType"));
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
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#update(org.societies.api.context.model.CtxModelObject, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void testUpdateByCtxAttributeIUserCtxBrokerCallback() throws IOException, ClassNotFoundException {
		
		final CtxAttribute emptyAttribute;
		final CtxAttribute initialisedAttribute;
		final CtxAttribute updatedAttribute;
		final CtxEntity entity;
		final BrokerCallback callback = new BrokerCallback();
		
		// Create the attribute's scope
		internalCtxBroker.createEntity("entType", callback);
		entity = (CtxEntity) callback.getModelObject();
		
		// Create the attribute to be tested
		internalCtxBroker.createAttribute(entity.getId(), CtxAttributeValueType.INDIVIDUAL, "attrType", callback);
		emptyAttribute = (CtxAttribute) callback.getModelObject();
		
		// Set the attribute's initial value
		emptyAttribute.setIntegerValue(100);
		internalCtxBroker.update(emptyAttribute, callback);
		
		// Verify the initial attribute value
		initialisedAttribute = (CtxAttribute) callback.getModelObject();
		assertEquals(new Integer(100), initialisedAttribute.getIntegerValue());

		// Update the attribute value
		initialisedAttribute.setIntegerValue(200);
		internalCtxBroker.update(initialisedAttribute, callback);
		
		// Verify updated attribute value
		updatedAttribute = (CtxAttribute) callback.getModelObject();
		assertEquals(new Integer(200), updatedAttribute.getIntegerValue());
		
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
	@Test
	public void testUpdateAttributeByCtxAttributeIdSerializableStringUserCtxBrokerCallback() {
		
		final CtxAttribute emptyAttribute;
		final CtxAttribute initialisedAttribute;
		final CtxAttribute updatedAttribute;
		final CtxEntity entity;
		final BrokerCallback callback = new BrokerCallback();
		
		// Create the attribute's scope
		internalCtxBroker.createEntity("entType", callback);
		entity = (CtxEntity) callback.getModelObject();
		
		// Create the attribute to be tested
		internalCtxBroker.createAttribute(entity.getId(), CtxAttributeValueType.INDIVIDUAL, "attrType", callback);
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
	
	private class BrokerCallback implements IUserCtxBrokerCallback{

		private CtxModelObject modelObject = null;

		CtxModelObject getModelObject(){
			return this.modelObject;
		}

		@Override
		public void cancel(CtxIdentifier c_id, String reason) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxAssociationCreated(CtxAssociation ctxEntity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxAttributeCreated(CtxAttribute ctxAttribute) {
			this.modelObject = ctxAttribute;
		}

		@Override
		public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list) {
			// TODO Auto-generated method stub
		}

		@Override
		public void ctxEntityCreated(CtxEntity ctxEntity) {
			this.modelObject = ctxEntity;
		}

		@Override
		public void ctxIndividualCtxEntityCreated(CtxEntity ctxEntity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxModelObjectRemoved(CtxModelObject ctxModelObject) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxModelObjectRetrieved(CtxModelObject ctxModelObject) {
			this.modelObject = ctxModelObject;
		}

		@Override
		public void ctxModelObjectsLookedup(List<CtxIdentifier> list) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxModelObjectUpdated(CtxModelObject ctxModelObject) {
			this.modelObject = ctxModelObject;
		}

		@Override
		public void futureCtxRetrieved(List<CtxAttribute> futCtx) {
			// TODO Auto-generated method stub
		}

		@Override
		public void futureCtxRetrieved(CtxAttribute futCtx) {
			// TODO Auto-generated method stub
		}

		@Override
		public void historyCtxRetrieved(CtxHistoryAttribute hoc) {
			// TODO Auto-generated method stub
		}

		@Override
		public void historyCtxRetrieved(List<CtxHistoryAttribute> hoc) {
			System.out.println("history size retrieved  "+ hoc.size());
			for(int i=0 ; i<hoc.size(); i++){
				CtxHistoryAttribute hocAttr = hoc.get(i);
				System.out.println("HoC AttrID:"+hocAttr.getId() +" time recorded:"+hocAttr.getLastModified()+" value: ");
				if(hocAttr.getStringValue() != null) System.out.println(hocAttr.getStringValue());
				if(hocAttr.getIntegerValue() != null) System.out.println(hocAttr.getIntegerValue());
				if(hocAttr.getDoubleValue() != null) System.out.println(hocAttr.getDoubleValue());
				if(hocAttr.getBinaryValue() != null) {
					try {
						HashMap<String,String> blobValue = (HashMap<String,String>) SerialisationHelper.deserialise(hocAttr.getBinaryValue(), this.getClass().getClassLoader());
						System.out.println(blobValue);
					
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					
					System.out.println(hocAttr.getBinaryValue());
				}
			}

			//	ctxAttribute.getQuality().getLastUpdated();
		}

		@Override
		public void ok(CtxIdentifier c_id) {
			// TODO Auto-generated method stub
		}

		@Override
		public void ok_list(List<CtxIdentifier> list) {
			// TODO Auto-generated method stub
		}

		@Override
		public void ok_values(List<Object> list) {
			// TODO Auto-generated method stub
		}

		@Override
		public void similartyResults(List<Object> results) {
			// TODO Auto-generated method stub
		}

		@Override
		public void updateReceived(CtxModelObject ctxModelObj) {
			// TODO Auto-generated method stub
		}

		@Override
		public void ctxHistoryTuplesSet(Boolean flag) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void ctxHistoryTuplesRetrieved(
				List<CtxAttributeIdentifier> listOfEscortingAttributeIds) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void ctxHistoryTuplesUpdated(
				List<CtxAttributeIdentifier> listOfEscortingAttributeIds) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void ctxHistoryTuplesRemoved(Boolean flag) {
			// TODO Auto-generated method stub
			
		}
	}
}