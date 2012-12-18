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
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.CtxAccessControlException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.context.broker.api.security.CtxPermission;
import org.societies.context.broker.api.security.ICtxAccessController;
import org.societies.context.broker.impl.CtxBroker;
import org.societies.context.broker.impl.InternalCtxBroker;
import org.societies.context.broker.test.util.MockBlobClass;
import org.societies.context.user.db.impl.UserCtxDBMgr;
import org.societies.context.userHistory.impl.UserContextHistoryManagement;

/**
 * Describe your class here...
 *
 * @author 
 *
 */
public class ExternalCtxBrokerTest {

	private static final String OWNER_IDENTITY_STRING = "myFooIIdentity@societies.local";
	private static final String NETWORK_NODE_STRING = "myFooIIdentity@societies.local/node";
	@SuppressWarnings("unused")
	private static final String CIS_IDENTITY_STRING = "FooCISIIdentity@societies.local";

	private CtxBroker ctxBroker;

	private static IIdentityManager mockIdentityMgr = mock(IIdentityManager.class);
	private static IIdentity mockIdentityLocal = mock(IIdentity.class);
	private static Requestor mockRequestor = mock(Requestor.class);
	private static INetworkNode mockNetworkNode = mock(INetworkNode.class);

	@SuppressWarnings("unused")
	private static IIdentity cisMockIdentity = mock(IIdentity.class);

	private static ICtxAccessController mockCtxAccessController = mock(ICtxAccessController.class);
	
	//Requestor mockRequestor = mock(Requestor.class);
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		when(mockIdentityMgr.getThisNetworkNode()).thenReturn(mockNetworkNode);
		when(mockIdentityMgr.isMine(mockIdentityLocal)).thenReturn(true);

		when(mockNetworkNode.getBareJid()).thenReturn(OWNER_IDENTITY_STRING);
		when(mockIdentityMgr.fromJid(OWNER_IDENTITY_STRING)).thenReturn(mockIdentityLocal);
		when(mockIdentityLocal.toString()).thenReturn(OWNER_IDENTITY_STRING);
		when(mockIdentityLocal.getJid()).thenReturn(OWNER_IDENTITY_STRING);
		when(mockRequestor.toString()).thenReturn(OWNER_IDENTITY_STRING);
		when(mockNetworkNode.toString()).thenReturn(NETWORK_NODE_STRING);

		when(mockIdentityMgr.isMine(mockIdentityLocal)).thenReturn(true);
	
		
		//when(mockIdentityLocal.toString()).thenReturn(OWNER_IDENTITY_STRING);
		when(mockIdentityLocal.getType()).thenReturn(IdentityType.CSS);
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

		InternalCtxBroker internalCtxBroker = new InternalCtxBroker();

		internalCtxBroker.setUserCtxDBMgr(new UserCtxDBMgr());
		internalCtxBroker.setUserCtxHistoryMgr(new UserContextHistoryManagement());
		//internalCtxBroker.setIdentityMgr(mockIdentityMgr);
		internalCtxBroker.createIndividualEntity(mockIdentityLocal, CtxEntityTypes.PERSON); // TODO remove?
		//internalCtxBroker.createCssNode(mockNetworkNode); // TODO remove?
		internalCtxBroker.setCtxAccessController(mockCtxAccessController);
		
		ctxBroker = new CtxBroker(internalCtxBroker);
		ctxBroker.setIdentityMgr(mockIdentityMgr);
		//ctxBroker.setCtxAccessController(mockCtxAccessController);
		
	//	when(internalCtxBroker.getLocalRequestor()).thenReturn(mockRequestor);
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

		ctxBroker = null;
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createEntity(java.lang.String)}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Ignore
	@Test
	public void testCreateEntityByString() throws CtxException, InterruptedException, ExecutionException {

		Requestor requestor = new Requestor(mockIdentityLocal);
		final CtxEntity ctxEntity = ctxBroker.createEntity(
				requestor, mockIdentityLocal, CtxEntityTypes.DEVICE).get();
		assertNotNull(ctxEntity);
		assertNotNull(ctxEntity.getId());
		assertEquals(mockIdentityLocal.toString(), ctxEntity.getOwnerId());
		assertEquals(CtxEntityTypes.DEVICE, ctxEntity.getType());

	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.CtxBroker#createAttribute(org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Ignore
	@Test
	public void testCreateAttributeByCtxEntityIdentifierString() throws CtxException, InterruptedException, ExecutionException {

		final CtxAttribute ctxAttribute;
		final CtxEntity ctxEntity;

		Requestor requestor = new Requestor(mockIdentityLocal);
		// Create the attribute's scope		
		ctxEntity = ctxBroker.createEntity(requestor, mockIdentityLocal, CtxEntityTypes.DEVICE).get();

		// Create the attribute to be tested
		ctxAttribute = ctxBroker.createAttribute(requestor, ctxEntity.getId(), CtxAttributeTypes.ID).get();
		assertNotNull(ctxAttribute.getId());
		assertEquals(ctxEntity.getId(), ctxAttribute.getId().getScope());
		assertEquals(CtxAttributeTypes.ID, ctxAttribute.getType());
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.CtxBroker#retrieveIndividualEntityId()}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 */
	@Ignore
	@Test
	public void testRetrieveCssOwnerEntityId() throws Exception {

		Requestor requestor = new Requestor(mockIdentityLocal);

		final CtxEntityIdentifier ownerEntityId = 
				ctxBroker.retrieveIndividualEntityId(requestor, mockIdentityLocal).get();
		assertNotNull(ownerEntityId);
		assertEquals(mockIdentityLocal.toString(), ownerEntityId.getOwnerId());
		assertEquals(CtxEntityTypes.PERSON, ownerEntityId.getType());
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createAssociation(java.lang.String)}.
	 */
	@Ignore
	@Test	
	public void testCreateAssociationByString() throws Exception {

		Requestor requestor = new Requestor(mockIdentityLocal);
		//System.out.println("mockIdentityLocal ******************************");
		CtxAssociation ctxAssocHasParam = 
				this.ctxBroker.createAssociation(requestor, mockIdentityLocal, CtxAssociationTypes.HAS_PARAMETERS).get();
		assertNotNull(ctxAssocHasParam);
		assertNotNull(ctxAssocHasParam.getId());
		assertEquals(mockIdentityLocal.toString(), ctxAssocHasParam.getOwnerId());
		assertEquals(CtxAssociationTypes.HAS_PARAMETERS, ctxAssocHasParam.getType());

		System.out.println("1 mockIdentityLocal "+mockIdentityLocal);

		// mock checkPermission
		//doNothing().when()
		//when(getLocalRequestor()).thenReturn(new Requestor(mockIdentityLocal));
		//doNothing().when()
		
		doNothing().when(mockCtxAccessController).checkPermission(requestor, mockIdentityLocal, 
				new CtxPermission(ctxAssocHasParam.getId(), CtxPermission.READ));
		
		//doNothing().when(internalCtxBroker).getLocalRequestor()
		
		
		final List<CtxIdentifier> assocIdentifierList =	this.ctxBroker.lookup(
				requestor, mockIdentityLocal, CtxModelType.ASSOCIATION, CtxAssociationTypes.HAS_PARAMETERS).get();

		
		assertEquals(1, assocIdentifierList.size());
		CtxIdentifier retrievedCtxAssocHasParamID = assocIdentifierList.get(0);
		assertEquals(ctxAssocHasParam.getId().toString(), retrievedCtxAssocHasParamID.toString());
	}


	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createAssociation(java.lang.String)}.
	 */
	@Ignore
	@Test
	public void testRetrieveEntitiesAssociationString() {

		Requestor requestor = new Requestor(mockIdentityLocal);
		try {
			System.out.println("testRetrieveEntitiesAssociationString");

			CtxEntityIdentifier personId = 
					this.ctxBroker.retrieveIndividualEntityId(requestor, mockIdentityLocal).get();

			CtxEntity serviceEnt = this.ctxBroker.createEntity(requestor,mockIdentityLocal,CtxEntityTypes.SERVICE).get();
			CtxAttribute serviceAttr = this.ctxBroker.createAttribute(requestor, serviceEnt.getId(), "parameterName1").get();
			serviceAttr.setStringValue("paramValue");

			CtxAssociation hasServiceAssoc = this.ctxBroker.createAssociation(requestor,mockIdentityLocal,CtxAssociationTypes.HAS_PARAMETERS).get();
			hasServiceAssoc.addChildEntity(serviceEnt.getId());
			hasServiceAssoc.addChildEntity(personId);
			hasServiceAssoc.setParentEntity(personId);

			hasServiceAssoc = (CtxAssociation) this.ctxBroker.update(requestor,hasServiceAssoc).get();
			//System.out.println("hasServiceAssoc "+hasServiceAssoc);

			serviceEnt = (CtxEntity) this.ctxBroker.update(requestor, serviceEnt).get();
			
			CtxEntity person = (CtxEntity) this.ctxBroker.retrieve(requestor,personId).get();

			//retrieve assoc data
			CtxAssociationIdentifier retrievedAssocID = null;
			CtxEntity serviceRetrieved = null;
			CtxAttribute ctxServiceAttrRetrieved = null;

			List<CtxIdentifier> list = this.ctxBroker.lookup(requestor,mockIdentityLocal, CtxModelType.ENTITY ,"Person").get();
			if(list.size()>0) {
				CtxIdentifier persID = list.get(0);				
				CtxEntity retrievedEnt = (CtxEntity) this.ctxBroker.retrieve(requestor, persID).get();

				assertEquals(retrievedEnt,person);			
				Set<CtxAssociationIdentifier> assocIDSet = retrievedEnt.getAssociations();

				for(CtxAssociationIdentifier assocID :assocIDSet){
					assertEquals(assocID,hasServiceAssoc.getId());
				}

				//	System.out.println("Association1 set " + assocIDSet);
				//	System.out.println("Association1 set " + assocIDSet.size());

				Set<CtxAssociationIdentifier> assocIDSet2 = retrievedEnt.getAssociations("hasService");
				//		System.out.println("assocIDSet2 size "+assocIDSet2.size());
				//		System.out.println("assocIDSet2 "+assocIDSet2);

				for(CtxAssociationIdentifier assocID : assocIDSet2 ){
					//System.out.println("Association2 set " + assocID);
					retrievedAssocID = assocID;
				}
				CtxAssociation hasServiceRetrieved = (CtxAssociation) this.ctxBroker.retrieve(requestor, retrievedAssocID).get();

				if(hasServiceRetrieved != null && hasServiceAssoc != null){
					assertEquals(hasServiceRetrieved,hasServiceAssoc);
					//if(hasServiceRetrieved.equals(hasServiceAssoc))System.out.println("CtxAssociation Retrieved matches created CtxAssociation");	
				}

				//System.out.println("hasServiceRetrieved "+ hasServiceRetrieved);
				Set<CtxEntityIdentifier> assocEntitiesSet = hasServiceRetrieved.getChildEntities("ServiceID");
				for(CtxEntityIdentifier ctxAssocEntityId : assocEntitiesSet ){
					serviceRetrieved = (CtxEntity) this.ctxBroker.retrieve(requestor,ctxAssocEntityId).get();		
					//System.out.println("ctxAssocEntityId "+ ctxAssocEntityId);
				}
				System.out.println("^ serviceRetrieved "+ serviceRetrieved.getId());
				System.out.println("^ serviceEnt "+ serviceEnt.getId());
				assertEquals(serviceRetrieved,serviceEnt);
				//if(serviceRetrieved.equals(serviceEnt)) System.out.println("CtxAssociation Retrieved matches created CtxAssociation");

				for(CtxAttribute ctxAttributeRetrived : serviceRetrieved.getAttributes("parameterName1") ){
					//	System.out.println("ctxAttributeRetrived "+ ctxAttributeRetrived);
					ctxServiceAttrRetrieved = ctxAttributeRetrived;
				}
				//if(ctxServiceAttrRetrieved.equals(serviceAttr)) System.out.println("ctxServiceAttrRetrieved Retrieved matches created serviceAttr");
				assertEquals(ctxServiceAttrRetrieved,serviceAttr);
			}
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

	/**
	 * Test method for {@link org.societies.context.broker.impl.CtxBroker#lookup(org.societies.api.context.model.CtxModelType)}.
	 */
	@Ignore
	@Test
	public void testLookupCtxModelTypeString() {

		Requestor requestor = new Requestor(mockIdentityLocal);
		try {
			final CtxEntity ent1 = this.ctxBroker.createEntity(requestor,mockIdentityLocal,"FooBar").get();
			final CtxEntity ent2 = this.ctxBroker.createEntity(requestor,mockIdentityLocal,"Foo").get();
			final CtxEntity ent3 = this.ctxBroker.createEntity(requestor,mockIdentityLocal,"Bar").get();

			// Create test attributes.
			final CtxAttribute attr1 = this.ctxBroker.createAttribute(requestor, ent1.getId(),"attrFooBar").get();
			final CtxAttribute attr2 = this.ctxBroker.createAttribute(requestor, ent2.getId(),"attrFoo").get();
			final CtxAttribute attr3 = this.ctxBroker.createAttribute(requestor, ent3.getId(),"attrBar").get();

			assertNotNull(ent1);
			assertNotNull(ent2);
			assertNotNull(ent3);
			assertNotNull(attr1);
			assertNotNull(attr2);
			assertNotNull(attr3);


			List<CtxIdentifier> ids = this.ctxBroker.lookup(requestor,mockIdentityLocal, CtxModelType.ENTITY, "FooBar").get();
			assertTrue(ids.contains(ent1.getId()));
			assertEquals(1, ids.size());

			ids = this.ctxBroker.lookup(requestor,mockIdentityLocal, CtxModelType.ATTRIBUTE, "attrFooBar").get();
			assertTrue(ids.contains(attr1.getId()));
			assertEquals(1, ids.size());

			ids = this.ctxBroker.lookup(requestor,mockIdentityLocal,CtxModelType.ATTRIBUTE, "xxxx").get();
			assertFalse(ids.contains(attr1.getId()));
			assertEquals(0, ids.size());

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


	/**
	 * Test method for {@link org.societies.context.broker.impl.CtxBroker#retrievePast(Requestor, org.societies.api.context.model.CtxAttributeIdentifier, java.util.Date, java.util.Date)}.
	 */
	@Ignore
	@Test
	public void testRetrieveHistoryCtxAttributeIdentifierDateDate() {

		Requestor requestor = new Requestor(mockIdentityLocal);

		final CtxAttribute emptyAttribute;
		CtxAttribute initialisedAttribute;
		final CtxEntity scope;
		System.out.println("testRetrieveHistoryCtxAttributeIdentifierDateDate");

		try {
			scope = this.ctxBroker.createEntity(requestor,mockIdentityLocal,"entType").get();

			// Create the attribute to be tested
			Future<CtxAttribute> futureCtxAttribute = this.ctxBroker.createAttribute(requestor, scope.getId(), "attrType");
			emptyAttribute = futureCtxAttribute.get();

			// Set the attribute's initial value
			emptyAttribute.setIntegerValue(100);
			emptyAttribute.setHistoryRecorded(true);

			initialisedAttribute = (CtxAttribute) this.ctxBroker.update(requestor, emptyAttribute).get();

			// Verify the initial attribute value
			assertEquals(new Integer(100), initialisedAttribute.getIntegerValue());

			emptyAttribute.setIntegerValue(200);
			initialisedAttribute = (CtxAttribute) this.ctxBroker.update(requestor, emptyAttribute).get();

			// Verify the initial attribute value
			assertEquals(new Integer(200), initialisedAttribute.getIntegerValue());

			emptyAttribute.setIntegerValue(300);
			initialisedAttribute = (CtxAttribute) this.ctxBroker.update(requestor, emptyAttribute).get();

			// Verify the initial attribute value
			assertEquals(new Integer(300), initialisedAttribute.getIntegerValue());

			//	Future<List<CtxHistoryAttribute>> historyFuture = internalCtxBroker.retrieveHistory(initialisedAttribute.getId(), null, null);

			List<CtxHistoryAttribute> history = this.ctxBroker.retrieveHistory(requestor, initialisedAttribute.getId(), null, null).get();

			for(CtxHistoryAttribute hocAttr: history){
				//	System.out.println(history.size());
				System.out.println("history List id:"+hocAttr.getId()+" getLastMod:"+hocAttr.getLastModified() +" hocAttr value:"+hocAttr.getIntegerValue());		
			}


			CtxHistoryAttribute hocAttr1 = history.get(0);
			CtxHistoryAttribute hocAttr2 = history.get(1);
			CtxHistoryAttribute hocAttr3 = history.get(2);

			assertEquals(new Integer(100), hocAttr1.getIntegerValue());
			assertEquals(new Integer(200), hocAttr2.getIntegerValue());
			assertEquals(new Integer(300), hocAttr3.getIntegerValue());
			assertEquals(history.size(),3);

			assertNotNull(hocAttr1.getLastModified());
			assertNotNull(hocAttr2.getLastModified());
			assertNotNull(hocAttr3.getLastModified());

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
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#update(org.societies.api.context.model.CtxModelObject)}.
	 * 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Ignore
	@Test
	public void testUpdateByCtxAttribute() throws Exception {

		final CtxAttribute emptyAttribute;
		final CtxAttribute initialisedAttribute;
		final CtxAttribute updatedAttribute;
		final CtxEntity deviceEntity;

		final Requestor requestor = new Requestor(mockIdentityLocal);
		// Create the attribute's scope
		deviceEntity = this.ctxBroker.createEntity(requestor,mockIdentityLocal, CtxEntityTypes.DEVICE).get();

		// Create the attribute to be tested
		emptyAttribute = this.ctxBroker.createAttribute(requestor, deviceEntity.getId(), "attrType").get();

		// mock checkPermission
		doNothing().when(mockCtxAccessController).checkPermission(
				requestor, mockIdentityLocal, new CtxPermission(emptyAttribute.getId(), CtxPermission.WRITE));

		// Set the attribute's initial value
		emptyAttribute.setIntegerValue(100);
		initialisedAttribute = (CtxAttribute) this.ctxBroker.update(requestor, emptyAttribute).get();

		// Verify the initial attribute value
		assertEquals(new Integer(100), initialisedAttribute.getIntegerValue());

		// Update the attribute value
		initialisedAttribute.setIntegerValue(200);
		updatedAttribute = (CtxAttribute) this.ctxBroker.update(requestor, initialisedAttribute).get();

		// Verify updated attribute value
		assertEquals(new Integer(200), updatedAttribute.getIntegerValue());

		// Test update with a binary value
		final CtxAttribute binaryAttribute;
		final MockBlobClass blob = new MockBlobClass(666);
		final byte[] blobBytes = SerialisationHelper.serialise(blob);
		updatedAttribute.setBinaryValue(blobBytes);
		binaryAttribute = (CtxAttribute) this.ctxBroker.update(requestor, updatedAttribute).get();

		// Verify binary attribute value
		assertNull(binaryAttribute.getIntegerValue());
		assertNotNull(binaryAttribute.getBinaryValue());
		final MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.
				deserialise(binaryAttribute.getBinaryValue(), this.getClass().getClassLoader());
		assertEquals(blob, retrievedBlob);
	}	

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#update(org.societies.api.context.model.CtxModelObject)}.
	 * 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Ignore
	@Test(expected=CtxAccessControlException.class)
	public void testUpdateByCtxAttributeAccessControlException() throws Exception {

		final CtxAttribute emptyAttribute;
		final CtxEntity deviceEntity;

		final Requestor requestor = new Requestor(mockIdentityLocal);
		// Create the attribute's scope
		deviceEntity = this.ctxBroker.createEntity(requestor, mockIdentityLocal, CtxEntityTypes.DEVICE).get();

		// Create the attribute to be tested
		emptyAttribute = this.ctxBroker.createAttribute(requestor, deviceEntity.getId(), "attrType").get();

		doThrow(new CtxAccessControlException()).when(mockCtxAccessController).checkPermission(
				requestor, mockIdentityLocal, 
				new CtxPermission(emptyAttribute.getId(), CtxPermission.WRITE));

		// Set the attribute's initial value
		emptyAttribute.setIntegerValue(100);
		this.ctxBroker.update(requestor, emptyAttribute).get();
	}

	@Ignore
	@Test
	public void testLookupAttributeValues() throws Exception {

		final Requestor requestor = new Requestor(mockIdentityLocal);

		// create entity1
		CtxEntity entity1 = this.ctxBroker.createEntity(requestor, mockIdentityLocal, CtxEntityTypes.SERVICE).get();

		// create ctxAttributeLocationCoords1
		CtxAttribute ctxAttributeLocationCoords1 = this.ctxBroker.createAttribute(requestor, entity1.getId(), CtxAttributeTypes.LOCATION_COORDINATES).get();

		// mock checkPermission
		doNothing().when(mockCtxAccessController).checkPermission(
				requestor, mockIdentityLocal, 
				new CtxPermission(ctxAttributeLocationCoords1.getId(), CtxPermission.WRITE));

		MockBlobClass coordinatesValue = new MockBlobClass(125125);
		ctxAttributeLocationCoords1.setBinaryValue(SerialisationHelper.serialise(coordinatesValue));
		this.ctxBroker.update(requestor, ctxAttributeLocationCoords1);

		// create ctxAttributeLocationSymb
		CtxAttribute ctxAttributeLocationSymb = this.ctxBroker.createAttribute(requestor, entity1.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();

		// mock checkPermission
		doNothing().when(mockCtxAccessController).checkPermission(
				requestor, mockIdentityLocal, 
				new CtxPermission(ctxAttributeLocationSymb.getId(), CtxPermission.WRITE));

		ctxAttributeLocationSymb.setStringValue("Athens");
		this.ctxBroker.update(requestor,ctxAttributeLocationSymb);

		// create entity2
		CtxEntity entity2 = this.ctxBroker.createEntity(requestor, mockIdentityLocal, CtxEntityTypes.SERVICE).get();

		// create ctxAttributeLocationCoords2
		CtxAttribute ctxAttributeLocationCoords2 = this.ctxBroker.createAttribute(requestor, entity2.getId(), CtxAttributeTypes.LOCATION_COORDINATES).get();

		// mock checkPermission
		doNothing().when(mockCtxAccessController).checkPermission(
				requestor, mockIdentityLocal, 
				new CtxPermission(ctxAttributeLocationCoords2.getId(), CtxPermission.WRITE));

		MockBlobClass coordinatesValue2 = new MockBlobClass(135135);
		ctxAttributeLocationCoords2.setBinaryValue(SerialisationHelper.serialise(coordinatesValue2));
		this.ctxBroker.update(requestor, ctxAttributeLocationCoords2);

		// create ctxAttributeLocationSymb
		CtxAttribute ctxAttributeLocationSymb2 = this.ctxBroker.createAttribute(requestor, entity2.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();

		// mock checkPermission
		doNothing().when(mockCtxAccessController).checkPermission(
				requestor, mockIdentityLocal, 
				new CtxPermission(ctxAttributeLocationSymb2.getId(), CtxPermission.WRITE));

		ctxAttributeLocationSymb2.setStringValue("Caracas");
		this.ctxBroker.update(requestor,ctxAttributeLocationSymb2);
		//start lookups
		/*
			List<CtxEntityIdentifier> allServiceEntIds = new ArrayList<CtxEntityIdentifier>();

			List<CtxIdentifier> listServiceCtxIds = this.ctxBroker.lookup(requestor,mockIdentityLocal,CtxModelType.ENTITY,CtxEntityTypes.SERVICE).get();
			for(CtxIdentifier ctxId: listServiceCtxIds){
				CtxEntityIdentifier cxtEnt = (CtxEntityIdentifier) ctxId;
				allServiceEntIds.add(cxtEnt);
			}
		 */
		List<CtxEntityIdentifier> serviceEntStringValues = this.ctxBroker.lookupEntities(
				requestor, mockIdentityLocal, CtxEntityTypes.SERVICE, CtxAttributeTypes.LOCATION_SYMBOLIC , "Caracas", "Caracas").get();
		CtxEntityIdentifier entId = serviceEntStringValues.get(0);

		// mock checkPermission
		doNothing().when(mockCtxAccessController).checkPermission(
				requestor, mockIdentityLocal, 
				new CtxPermission(entId, CtxPermission.READ));

		CtxEntity ent1 = (CtxEntity) this.ctxBroker.retrieve(requestor, entId).get();
		Set<CtxAttribute> atrrSet1 = ent1.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);

		for(CtxAttribute attr: atrrSet1){
			//final MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.deserialise(attr.getBinaryValue(), this.getClass().getClassLoader());
			System.out.println("retrievedBlob.getSeed() "+attr.getStringValue());
			assertEquals(attr.getStringValue(),"Caracas");
			//assertEquals(retrievedBlob.getSeed(),125);
		}			
		//TODO
		//verify that also works for blob values
		/*
			MockBlobClass coordinatesValueX = new MockBlobClass(135135);
			List<CtxEntityIdentifier> serviceEntBlobValues = this.ctxBroker.lookupEntities(requestor,mockIdentityLocal,CtxEntityTypes.SERVICE, CtxAttributeTypes.LOCATION_COORDINATES , coordinatesValueX, coordinatesValueX).get();
			CtxEntityIdentifier entId2 = serviceEntBlobValues.get(0);

			CtxEntity entity = (CtxEntity) this.ctxBroker.retrieve(requestor, entId2).get();
			Set<CtxAttribute> atrrSet2 = entity.getAttributes(CtxAttributeTypes.LOCATION_COORDINATES);

			for(CtxAttribute attr: atrrSet2){
				final MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.deserialise(attr.getBinaryValue(), this.getClass().getClassLoader());
				System.out.println("retrievedBlob.getSeed() "+retrievedBlob.getSeed());
				assertEquals(retrievedBlob.getSeed(),135135);
			}		
		 */
	}

	

}