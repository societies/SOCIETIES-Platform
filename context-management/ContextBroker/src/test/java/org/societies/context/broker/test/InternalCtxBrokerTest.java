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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;

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
		internalCtxBroker.setUserCtxHistoryMgr(new UserContextHistoryManagement());
		//	internalCtxBroker.createCSSOperator();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		internalCtxBroker = null;
	}


	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createCSSOperator}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testCreateCSSOperator() {

		//internalCtxBroker.createCSSOperator();
		try {
			IndividualCtxEntity ctxEntity = internalCtxBroker.retrieveCssOperator().get();

			System.out.println("operator entity " +ctxEntity);
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
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#evaluateSimilarity(java.io.Serializable, java.util.List, org.societies.api.internal.context.broker.IUserCtxBrokerCallback)}.
	 */
	@Ignore
	@Test
	public void testEvaluateSimilarity() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createAttribute(org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testCreateAttributeByCtxEntityIdentifierString() throws CtxException, InterruptedException, ExecutionException {

		final CtxAttribute ctxAttribute;
		final CtxEntity ctxEntity;

		// Create the attribute's scope		
		final Future<CtxEntity> futureCtxEntity = internalCtxBroker.createEntity("entType");
		ctxEntity = futureCtxEntity.get();

		// Create the attribute to be tested
		Future<CtxAttribute> futureCtxAttribute = internalCtxBroker.createAttribute(ctxEntity.getId(), "attrType");
		ctxAttribute = futureCtxAttribute.get();

		assertNotNull(ctxAttribute.getId());
		assertEquals(ctxAttribute.getId().getScope(), ctxEntity.getId());
		assertTrue(ctxAttribute.getType().equalsIgnoreCase("attrType"));
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createEntity(java.lang.String)}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testCreateEntityByString() throws CtxException, InterruptedException, ExecutionException {

		final CtxEntity ctxEntity;

		final Future<CtxEntity> futureCtxEntity = internalCtxBroker.createEntity("entType");
		ctxEntity = futureCtxEntity.get();
		assertNotNull(ctxEntity);
		assertTrue(ctxEntity.getType().equalsIgnoreCase("entType"));
	}


	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createEntity(java.lang.String)}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testcreateIndividualCtxEntity() throws CtxException, InterruptedException, ExecutionException {

		final IndividualCtxEntity individualCtxEnt ;

		final Future<IndividualCtxEntity> futureIndividualCtxEntity = internalCtxBroker.createIndividualEntity("Person");
		individualCtxEnt = futureIndividualCtxEntity.get();
		assertNotNull(individualCtxEnt);
		assertTrue(individualCtxEnt.getType().equalsIgnoreCase("Person"));
	}


	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createAssociation(java.lang.String)}.
	 */
	@Test
	public void testCreateAssociationString() {
		try {
			CtxAssociation ctxAssocHasServ = internalCtxBroker.createAssociation("hasService").get();

			List<CtxIdentifier> assocIdentifierList = internalCtxBroker.lookup(CtxModelType.ASSOCIATION, "hasService").get();
			assertEquals(assocIdentifierList.size(),1);
			CtxIdentifier retrievedAssocHasServID = assocIdentifierList.get(0);
			assertEquals(retrievedAssocHasServID.toString(),ctxAssocHasServ.getId().toString());
			System.out.println("assocID "+ retrievedAssocHasServID.toString());

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
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createAssociation(java.lang.String)}.
	 */
	@Ignore
	@Test
	public void testStoreRetrieveServiceParameters() {

		try {
			IndividualCtxEntity operator = this.internalCtxBroker.retrieveCssOperator().get();

			CtxEntity serviceEnt = this.internalCtxBroker.createEntity(CtxEntityTypes.SERVICE).get();

			CtxAssociation usesServiceAssoc = this.internalCtxBroker.createAssociation(CtxAssociationTypes.USES_SERVICE).get();
			usesServiceAssoc.addChildEntity(operator.getId());
			usesServiceAssoc.addChildEntity(serviceEnt.getId());

			CtxEntity parServiceEnt = this.internalCtxBroker.createEntity(CtxEntityTypes.SERVICE_PARAMETER).get();
			CtxAttribute parNameAttr = this.internalCtxBroker.createAttribute(parServiceEnt.getId(), CtxAttributeTypes.PARAMETER_NAME).get();
			CtxAttribute parValueAttr = this.internalCtxBroker.createAttribute(parServiceEnt.getId(), CtxAttributeTypes.LAST_ACTION).get();

			CtxAssociation hasParametersServiceAssoc = this.internalCtxBroker.createAssociation(CtxAssociationTypes.HAS_PARAMETER).get();
			hasParametersServiceAssoc.addChildEntity(serviceEnt.getId());
			hasParametersServiceAssoc.addChildEntity(parServiceEnt.getId());


			//find a SERVICE_PARAMETER entity with a specific PARAMETER_NAME attribute under a SERVICE entity
			// e.g. PARAMETER_NAME attribute has value "Volume"

			CtxEntity serviceParamEntityResult = null;
			//returns all services assigned to user
			Set<CtxAssociationIdentifier> operatorServicesAssocs = operator.getAssociations(CtxAssociationTypes.USES_SERVICE);

			CtxAssociation assocUseServices = null;

			for(CtxAssociationIdentifier assocId: operatorServicesAssocs){
				assocUseServices = (CtxAssociation) this.internalCtxBroker.retrieve(assocId).get();
			}

			// the set contains all entities of type service that the operator is using	
			Set<CtxEntityIdentifier> servicesSet = assocUseServices.getChildEntities(CtxEntityTypes.SERVICE);

			for(CtxEntityIdentifier serviceEntID : servicesSet){
				CtxEntity serviceEntity = (CtxEntity) this.internalCtxBroker.retrieve(serviceEntID).get();

				Set<CtxAssociationIdentifier> hasParamAssocSet = serviceEntity.getAssociations(CtxAssociationTypes.HAS_PARAMETER);
				CtxAssociation assocHasParam = null;
				for(CtxAssociationIdentifier hasParamAssocID : hasParamAssocSet){
					assocHasParam = (CtxAssociation) this.internalCtxBroker.retrieve(hasParamAssocID).get();

				}
				Set<CtxEntityIdentifier> serviceParamEntIdSet = assocHasParam.getChildEntities(CtxEntityTypes.SERVICE_PARAMETER);
				for(CtxEntityIdentifier serviceParamEntId : serviceParamEntIdSet){
					CtxEntity serviceParamEntity =  (CtxEntity) this.internalCtxBroker.retrieve(serviceParamEntId).get();
					Set<CtxAttribute> paramNameAttrsAttributeSet = serviceParamEntity.getAttributes(CtxAttributeTypes.PARAMETER_NAME);
					for(CtxAttribute paramNameAttr : paramNameAttrsAttributeSet){
						if(paramNameAttr.getStringValue().equals("Volume")) serviceParamEntity = serviceParamEntityResult;
					}
				}
			}

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
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createAssociation(java.lang.String)}.
	 */
	@Test
	public void testRetrieveEntitiesAssociationString() {
		try {
			System.out.println("testRetrieveEntitiesAssociationString");

			CtxEntity person = this.internalCtxBroker.createEntity("Person").get();
			CtxEntity serviceEnt = this.internalCtxBroker.createEntity("ServiceID").get();
			CtxAttribute serviceAttr = this.internalCtxBroker.createAttribute(serviceEnt.getId(), "parameterName1").get();
			serviceAttr.setStringValue("paramValue");

			CtxAssociation hasServiceAssoc = this.internalCtxBroker.createAssociation("hasService").get();
			hasServiceAssoc.addChildEntity(serviceEnt.getId());
			hasServiceAssoc.addChildEntity(person.getId());
			hasServiceAssoc.setParentEntity(person.getId());

			hasServiceAssoc = (CtxAssociation) this.internalCtxBroker.update(hasServiceAssoc).get();
			System.out.println("hasServiceAssoc "+hasServiceAssoc);

			serviceEnt = (CtxEntity) this.internalCtxBroker.update(serviceEnt).get();
			person = (CtxEntity) this.internalCtxBroker.update(person).get();


			//retrieve assoc data
			CtxAssociationIdentifier retrievedAssocID = null;
			CtxEntity serviceRetrieved = null;
			CtxAttribute ctxServiceAttrRetrieved = null;

			List<CtxIdentifier> list = this.internalCtxBroker.lookup(CtxModelType.ENTITY ,"Person").get();
			if(list.size()>0) {
				System.out.println("list "+list);
				CtxIdentifier persID = list.get(0);				
				CtxEntity retrievedEnt = (CtxEntity) this.internalCtxBroker.retrieve(persID).get();
				System.out.println("retrievedEnt "+retrievedEnt);

				Set<CtxAssociationIdentifier> assocIDSet = retrievedEnt.getAssociations();
				System.out.println("Association1 set " + assocIDSet);
				System.out.println("Association1 set " + assocIDSet.size());

				Set<CtxAssociationIdentifier> assocIDSet2 = retrievedEnt.getAssociations("hasService");
				System.out.println("6");
				System.out.println("assocIDSet2 size "+assocIDSet2.size());
				System.out.println("assocIDSet2 "+assocIDSet2);


				/*
				for(CtxAssociationIdentifier assocID : assocIDSet2 ){
					System.out.println("Association2 set " + assocID);
					retrievedAssocID = assocID;
				}
				CtxAssociation hasServiceRetrieved = (CtxAssociation) this.internalCtxBroker.retrieve(retrievedAssocID).get();
				System.out.println("7");

				if(hasServiceRetrieved != null && hasService != null){
					if(hasServiceRetrieved.equals(hasService))System.out.println("CtxAssociation Retrieved matches created CtxAssociation");	
				}

				System.out.println("hasServiceRetrieved "+ hasServiceRetrieved);

				Set<CtxEntityIdentifier> assocEntitiesSet = hasServiceRetrieved.getChildEntities();
				for(CtxEntityIdentifier ctxAssocEntityId : assocEntitiesSet ){
					serviceRetrieved = (CtxEntity) this.internalCtxBroker.retrieve(ctxAssocEntityId).get();		
					System.out.println("ctxAssocEntityId "+ ctxAssocEntityId);
				}

				if(serviceRetrieved.equals(serviceEnt)) System.out.println("CtxAssociation Retrieved matches created CtxAssociation");
				System.out.println("8");
				for(CtxAttribute ctxAttributeRetrived : serviceRetrieved.getAttributes("parameterName1") ){
					System.out.println("ctxAttributeRetrived "+ ctxAttributeRetrived);
					ctxServiceAttrRetrieved = ctxAttributeRetrived;
				}
				if(ctxServiceAttrRetrieved.equals(serviceAttr)) System.out.println("ctxServiceAttrRetrieved Retrieved matches created serviceAttr");
				System.out.println("9");

				 */
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
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveAdministratingCSS(org.societies.api.context.model.CtxEntityIdentifier)}.
	 */
	@Ignore
	@Test
	public void testRetrieveAdministratingCSSCtxEntityIdentifier() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveBonds(org.societies.api.context.model.CtxEntityIdentifier)}.
	 */
	@Ignore
	@Test
	public void testRetrieveBondsCtxEntityIdentifier() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveChildCommunities(org.societies.api.context.model.CtxEntityIdentifier)}.
	 */
	@Ignore
	@Test
	public void testRetrieveChildCommunitiesCtxEntityIdentifier() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveCommunityMembers(org.societies.api.context.model.CtxEntityIdentifier)}.
	 */
	@Ignore
	@Test
	public void testRetrieveCommunityMembersCtxEntityIdentifier() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveParentCommunities(org.societies.api.context.model.CtxEntityIdentifier)}.
	 */
	@Ignore
	@Test
	public void testRetrieveParentCommunitiesCtxEntityIdentifier() {
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
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#lookup(org.societies.api.context.model.CtxModelType)}.
	 */

	@Test
	public void testLookupCtxModelTypeString() {

		try {
			final CtxEntity ent1 = internalCtxBroker.createEntity("FooBar").get();
			final CtxEntity ent2 = internalCtxBroker.createEntity("Foo").get();
			final CtxEntity ent3 = internalCtxBroker.createEntity("Bar").get();

			// Create test attributes.
			final CtxAttribute attr1 = internalCtxBroker.createAttribute(ent1.getId(),"attrFooBar").get();
			final CtxAttribute attr2 = internalCtxBroker.createAttribute(ent2.getId(),"attrFoo").get();
			final CtxAttribute attr3 = internalCtxBroker.createAttribute(ent3.getId(),"attrBar").get();

			assertNotNull(ent1);
			assertNotNull(ent2);
			assertNotNull(ent3);
			assertNotNull(attr1);
			assertNotNull(attr2);
			assertNotNull(attr3);


			List<CtxIdentifier> ids =internalCtxBroker.lookup(CtxModelType.ENTITY, "FooBar").get();
			assertTrue(ids.contains(ent1.getId()));
			assertEquals(1, ids.size());

			ids = internalCtxBroker.lookup(CtxModelType.ATTRIBUTE, "attrFooBar").get();
			assertTrue(ids.contains(attr1.getId()));
			assertEquals(1, ids.size());

			ids = internalCtxBroker.lookup(CtxModelType.ATTRIBUTE, "xxxx").get();
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
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#lookupEntities(java.lang.String, java.lang.String, java.io.Serializable, java.io.Serializable)}.
	 */
	@Ignore
	@Test
	public void testLookupEntitiesStringStringSerializableSerializable() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#registerForUpdates(org.societies.api.context.model.CtxEntityIdentifier)}.
	 */
	@Ignore
	@Test
	public void testRegisterForUpdatesCtxEntityIdentifierString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#registerForUpdates(org.societies.api.context.model.CtxAttributeIdentifier)}.
	 */
	@Ignore
	@Test
	public void testRegisterForUpdatesCtxAttributeIdentifier() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#remove(org.societies.api.context.model.CtxIdentifier)}.
	 */
	@Ignore
	@Test
	public void testRemoveCtxIdentifier() {
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
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieve(org.societies.api.context.model.CtxIdentifier)}.
	 */
	@Ignore
	@Test
	public void testRetrieveCtxIdentifier() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveFuture(org.societies.api.context.model.CtxAttributeIdentifier, java.util.Date)}.
	 */
	@Ignore
	@Test
	public void testRetrieveFutureCtxAttributeIdentifierDate() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveFuture(org.societies.api.context.model.CtxAttributeIdentifier, int)}.
	 */
	@Ignore
	@Test
	public void testRetrieveFutureCtxAttributeIdentifierInt() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrievePast(org.societies.api.context.model.CtxAttributeIdentifier, int)}.
	 */
	@Ignore
	@Test
	public void testRetrieveHistoryCtxAttributeIdentifierInt() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createHistoryAttribute(CtxAttributeIdentifier attID, Date date, Serializable value, CtxAttributeValueType valueType)}.
	 */

	@Test
	public void testCreateHistoryAttribute() {

		CtxEntity ctxEntity;
		try {
			ctxEntity = internalCtxBroker.createEntity("PersonEntity").get();
			CtxAttribute ctxAttribute =  internalCtxBroker.createAttribute(ctxEntity.getId(), "PersonAttribute").get();
			Date date1 = new Date();
			String value = "valueString1";
			CtxHistoryAttribute hocAttr1 = internalCtxBroker.createHistoryAttribute(ctxAttribute.getId(), date1, value, CtxAttributeValueType.STRING).get();

			Date date2 = new Date();
			final MockBlobClass mock = new MockBlobClass(666);
			byte[] blobValue = SerialisationHelper.serialise(mock);

			CtxHistoryAttribute hocAttr2 = internalCtxBroker.createHistoryAttribute(ctxAttribute.getId(), date2, blobValue, CtxAttributeValueType.BINARY).get();
			assertEquals(hocAttr2.getLastModified(),date2);
			assertEquals(hocAttr2.getType(),"PersonAttribute");
			assertEquals(hocAttr2.getModelType(),CtxModelType.ATTRIBUTE);
			final MockBlobClass retrievedMock = (MockBlobClass) SerialisationHelper.deserialise(hocAttr2.getBinaryValue(), this.getClass().getClassLoader());
			assertEquals(mock,retrievedMock);

			Date date3 = new Date();
			value = "valueString3";
			CtxHistoryAttribute hocAttr3 = internalCtxBroker.createHistoryAttribute(ctxAttribute.getId(), date3, value, CtxAttributeValueType.STRING).get();

			List<CtxHistoryAttribute> history = internalCtxBroker.retrieveHistory(ctxAttribute.getId(), null, null).get();
			assertEquals(3, history.size());

			CtxHistoryAttribute ctxHocAttr = history.get(0);
			assertEquals(ctxHocAttr.getLastModified(),date1);
			assertEquals(ctxHocAttr.getStringValue(),"valueString1");
			assertEquals(ctxHocAttr.getType(),"PersonAttribute");
			assertEquals(ctxHocAttr.getModelType(),CtxModelType.ATTRIBUTE);

			CtxHistoryAttribute ctxHocAttrBlob = history.get(1);
			assertEquals(ctxHocAttrBlob.getLastModified(),date2);
			assertEquals(ctxHocAttrBlob.getType(),"PersonAttribute");
			assertEquals(ctxHocAttrBlob.getModelType(),CtxModelType.ATTRIBUTE);
			final MockBlobClass retrievedMockFromHocDB = (MockBlobClass) SerialisationHelper.deserialise(hocAttr2.getBinaryValue(), this.getClass().getClassLoader());
			assertEquals(mock,retrievedMockFromHocDB);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrievePast(org.societies.api.context.model.CtxAttributeIdentifier, java.util.Date, java.util.Date)}.
	 */
	@Test
	public void testRetrieveHistoryCtxAttributeIdentifierDateDate() {

		final CtxAttribute emptyAttribute;
		CtxAttribute initialisedAttribute;
		final CtxEntity scope;

		// Create the attribute's scope
		Future<CtxEntity> futureEntity;
		try {
			futureEntity = internalCtxBroker.createEntity("entType");
			scope = futureEntity.get();

			// Create the attribute to be tested
			Future<CtxAttribute> futureCtxAttribute = internalCtxBroker.createAttribute(scope.getId(), "attrType");
			emptyAttribute = futureCtxAttribute.get();

			// Set the attribute's initial value
			emptyAttribute.setIntegerValue(100);
			emptyAttribute.setHistoryRecorded(true);

			Future<CtxModelObject> futureCtxModelObject = internalCtxBroker.update(emptyAttribute);
			initialisedAttribute = (CtxAttribute) futureCtxModelObject.get();
			// Verify the initial attribute value
			assertEquals(new Integer(100), initialisedAttribute.getIntegerValue());

			emptyAttribute.setIntegerValue(200);
			futureCtxModelObject = internalCtxBroker.update(emptyAttribute);
			initialisedAttribute = (CtxAttribute) futureCtxModelObject.get();
			// Verify the initial attribute value
			assertEquals(new Integer(200), initialisedAttribute.getIntegerValue());

			emptyAttribute.setIntegerValue(300);
			futureCtxModelObject = internalCtxBroker.update(emptyAttribute);
			initialisedAttribute = (CtxAttribute) futureCtxModelObject.get();
			// Verify the initial attribute value
			assertEquals(new Integer(300), initialisedAttribute.getIntegerValue());

			//			Future<List<CtxHistoryAttribute>> historyFuture = internalCtxBroker.retrieveHistory(initialisedAttribute.getId(), null, null);

			List<CtxHistoryAttribute> history = internalCtxBroker.retrieveHistory(initialisedAttribute.getId(), null, null).get();

			for(CtxHistoryAttribute hocAttr: history){
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
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#unregisterForUpdates(org.societies.api.context.model.CtxAttributeIdentifier)}.
	 */
	@Ignore
	@Test
	public void testUnregisterForUpdatesCtxAttributeIdentifier() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#unregisterForUpdates(org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)}.
	 */
	@Ignore
	@Test
	public void testUnregisterForUpdatesCtxEntityIdentifierString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#update(org.societies.api.context.model.CtxModelObject)}.
	 */
	@Ignore
	@Test
	public void testUpdateByCtxEntity() {
		fail("Not yet implemented");
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
	@Test
	public void testUpdateByCtxAttribute() throws IOException, ClassNotFoundException, CtxException, InterruptedException, ExecutionException {

		final CtxAttribute emptyAttribute;
		final CtxAttribute initialisedAttribute;
		final CtxAttribute updatedAttribute;
		final CtxEntity scope;

		// Create the attribute's scope
		Future<CtxEntity> futureEntity = internalCtxBroker.createEntity("entType");
		scope = futureEntity.get();

		// Create the attribute to be tested
		Future<CtxAttribute> futureCtxAttribute = internalCtxBroker.createAttribute(scope.getId(), "attrType");
		emptyAttribute = futureCtxAttribute.get();

		// Set the attribute's initial value
		emptyAttribute.setIntegerValue(100);
		Future<CtxModelObject> futureCtxModelObject = internalCtxBroker.update(emptyAttribute);
		initialisedAttribute = (CtxAttribute) futureCtxModelObject.get();

		// Verify the initial attribute value
		assertEquals(new Integer(100), initialisedAttribute.getIntegerValue());

		// Update the attribute value
		initialisedAttribute.setIntegerValue(200);
		futureCtxModelObject = internalCtxBroker.update(initialisedAttribute);

		// Verify updated attribute value
		updatedAttribute = (CtxAttribute) futureCtxModelObject.get();
		assertEquals(new Integer(200), updatedAttribute.getIntegerValue());

		// Test update with a binary value
		final CtxAttribute binaryAttribute;
		final MockBlobClass blob = new MockBlobClass(666);
		final byte[] blobBytes = SerialisationHelper.serialise(blob);
		updatedAttribute.setBinaryValue(blobBytes);
		futureCtxModelObject = internalCtxBroker.update(updatedAttribute);

		// Verify binary attribute value
		binaryAttribute = (CtxAttribute) futureCtxModelObject.get();
		assertNull(binaryAttribute.getIntegerValue());
		assertNotNull(binaryAttribute.getBinaryValue());
		final MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.
				deserialise(binaryAttribute.getBinaryValue(), this.getClass().getClassLoader());
		assertEquals(blob, retrievedBlob);
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#update(org.societies.api.context.model.CtxModelObject)}.
	 */
	@Ignore
	@Test
	public void testUpdateByCtxAssociation() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#updateAttribute(CtxAttributeIdentifier, java.io.Serializable, java.lang.String)}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void testUpdateAttributeByCtxAttributeIdSerializableString() throws CtxException, InterruptedException, ExecutionException, IOException, ClassNotFoundException {

		final CtxAttribute emptyAttribute;
		final CtxAttribute initialisedAttribute;
		final CtxAttribute updatedAttribute;
		final CtxEntity scope;

		// Create the attribute's scope
		final Future<CtxEntity> futureEntity = internalCtxBroker.createEntity("entType");
		scope = (CtxEntity) futureEntity.get();

		// Create the attribute to be tested
		Future<CtxAttribute> futureAttribute = internalCtxBroker.createAttribute(scope.getId(), "attrType");
		emptyAttribute = (CtxAttribute) futureAttribute.get();

		// Set the attribute's initial value
		futureAttribute = internalCtxBroker.updateAttribute(emptyAttribute.getId(), new Integer(100), "valueMetric");

		// Verify the initial attribute value
		initialisedAttribute = futureAttribute.get();
		assertEquals(new Integer(100), initialisedAttribute.getIntegerValue());

		// Update the attribute value
		futureAttribute = internalCtxBroker.updateAttribute(initialisedAttribute.getId(), new Integer(200), "valueMetric");

		// Verify updated attribute value
		updatedAttribute = futureAttribute.get();
		assertEquals(new Integer(200), updatedAttribute.getIntegerValue());

		// Test update with a binary value
		final CtxAttribute binaryAttribute;
		final MockBlobClass blob = new MockBlobClass(666);
		final byte[] blobBytes = SerialisationHelper.serialise(blob);

		futureAttribute = internalCtxBroker.updateAttribute(updatedAttribute.getId(), blobBytes);
		// Verify binary attribute value
		binaryAttribute = (CtxAttribute) futureAttribute.get();
		assertNotNull(binaryAttribute.getBinaryValue());
		final MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.deserialise(
				binaryAttribute.getBinaryValue(), this.getClass().getClassLoader());
		assertEquals(blob, retrievedBlob);
	}


	/**
	 * Test method for {@link org.societies.context.broker.impl.setHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
	 *		List<CtxAttributeIdentifier> listOfEscortingAttributeIds)}.
	 * Test method for {@link org.societies.context.broker.impl.getHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
	 *		List<CtxAttributeIdentifier> listOfEscortingAttributeIds)}.
	 *
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */

	@Test
	public void testSetGetHistoryTuples() throws CtxException, InterruptedException, ExecutionException {

		final CtxAttribute primaryAttribute;
		final CtxAttribute escortingAttribute1;
		final CtxAttribute escortingAttribute2;
		final CtxEntity scope;

		scope = (CtxEntity)internalCtxBroker.createEntity("entType").get();
		// Create the attribute to be tested
		primaryAttribute = (CtxAttribute) internalCtxBroker.createAttribute(scope.getId(), "primaryAttribute").get();
		//internalCtxBroker.update(primaryAttribute);

		escortingAttribute1 = (CtxAttribute)internalCtxBroker.createAttribute(scope.getId(), "escortingAttribute1").get();
		escortingAttribute2 = (CtxAttribute)internalCtxBroker.createAttribute(scope.getId(), "escortingAttribute2").get();

		assertNotNull(primaryAttribute);
		assertNotNull(escortingAttribute1);
		assertNotNull(escortingAttribute2);

		List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
		listOfEscortingAttributeIds.add(escortingAttribute1.getId());
		listOfEscortingAttributeIds.add(escortingAttribute2.getId());
		System.out.println("primary: "+ primaryAttribute.getId());
		System.out.println("escorting tuple list: "+ listOfEscortingAttributeIds);

		//System.out.println("primary attr last update: "+primaryAttribute.getQuality().getLastUpdated());

		assertTrue(internalCtxBroker.setHistoryTuples(primaryAttribute.getId(), listOfEscortingAttributeIds).get());	

		//tuples created --- end of setTuples test

		// start getTuples test
		List<CtxIdentifier> primaryAttrList = internalCtxBroker.lookup(CtxModelType.ATTRIBUTE, "primaryAttribute").get();
		assertEquals(primaryAttrList.size(),1);
		//System.out.println("primaryAttrList" + primaryAttrList);
		CtxAttributeIdentifier primaryAttrId = (CtxAttributeIdentifier) primaryAttrList.get(0);
		//System.out.println("primaryAttrId" + primaryAttrId);
		assertTrue(primaryAttrId.toString().contains("primaryAttribute"));
		List<CtxAttributeIdentifier> results = internalCtxBroker.getHistoryTuples(primaryAttrId, null).get();
		assertEquals(results.size(),3);
	}


	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#updateHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier, List<CtxAttributeIdentifier> arg1)}.
	 */

	@Test
	public void testUpdateHistoryTuples() {

		final CtxEntity scope;
		CtxAttribute primaryAttribute;
		CtxAttribute escortingAttribute1;
		CtxAttribute escortingAttribute2;
		CtxAttribute escortingAttribute3;
		System.out.println("********* testUpdateHistoryTuples");
		try {
			scope = (CtxEntity)internalCtxBroker.createEntity("entType").get();
			// Create the attribute to be tested
			primaryAttribute = (CtxAttribute) internalCtxBroker.createAttribute(scope.getId(), "primaryAttribute").get();
			primaryAttribute.setStringValue("fistValue");
			primaryAttribute.setHistoryRecorded(true);
			internalCtxBroker.update(primaryAttribute);

			escortingAttribute1 = (CtxAttribute)internalCtxBroker.createAttribute(scope.getId(), "escortingAttribute1").get();
			escortingAttribute1.setHistoryRecorded(true);
			escortingAttribute2 = (CtxAttribute)internalCtxBroker.createAttribute(scope.getId(), "escortingAttribute2").get();
			escortingAttribute2.setHistoryRecorded(true);

			escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingValue1").get();
			escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingValue2").get();


			List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
			listOfEscortingAttributeIds.add(escortingAttribute1.getId());
			listOfEscortingAttributeIds.add(escortingAttribute2.getId());

			assertTrue(internalCtxBroker.setHistoryTuples(primaryAttribute.getId(), listOfEscortingAttributeIds).get());	
			internalCtxBroker.update(primaryAttribute);

			List<CtxAttributeIdentifier> tuplesBeforeUpdate = internalCtxBroker.getHistoryTuples(primaryAttribute.getId(), null).get();
			System.out.println("********* tuplesBeforeUpdate "+tuplesBeforeUpdate +" size "+tuplesBeforeUpdate.size());
			assertEquals(3,tuplesBeforeUpdate.size());

			escortingAttribute3 = (CtxAttribute)internalCtxBroker.createAttribute(scope.getId(), "escortingAttribute3").get();
			listOfEscortingAttributeIds.add(escortingAttribute3.getId());
			System.out.println("listOfEscortingAttributeIds "+listOfEscortingAttributeIds.size());

			internalCtxBroker.updateHistoryTuples(primaryAttribute.getId(),listOfEscortingAttributeIds);
			List<CtxAttributeIdentifier> tuplesAfterUpdate = internalCtxBroker.getHistoryTuples(primaryAttribute.getId(), null).get();
			assertEquals(4,tuplesAfterUpdate.size());
			System.out.println("********* tuplesAfterUpdate "+tuplesAfterUpdate +" size "+tuplesAfterUpdate.size());

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

	@Test
	public void testHistoryMultipleSizeTupleDataRetrieval() throws CtxException, InterruptedException, ExecutionException {


		System.out.println("testHistoryMultipleSizeTupleDataRetrieval");
		final CtxEntity scope;
		CtxAttribute primaryAttribute;
		CtxAttribute escortingAttribute1;
		CtxAttribute escortingAttribute2;

		scope = (CtxEntity)internalCtxBroker.createEntity("entType").get();
		// Create the attribute to be tested
		primaryAttribute = (CtxAttribute) internalCtxBroker.createAttribute(scope.getId(), "primaryAttribute").get();
		primaryAttribute.setStringValue("fistValue");
		primaryAttribute.setHistoryRecorded(true);
		internalCtxBroker.update(primaryAttribute);

		escortingAttribute1 = (CtxAttribute)internalCtxBroker.createAttribute(scope.getId(), "escortingAttribute1").get();
		escortingAttribute1.setHistoryRecorded(true);
		escortingAttribute2 = (CtxAttribute)internalCtxBroker.createAttribute(scope.getId(), "escortingAttribute2").get();
		escortingAttribute2.setHistoryRecorded(true);

		escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingValue1_xx").get();
		escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingValue2_xx").get();


		List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
		listOfEscortingAttributeIds.add(escortingAttribute1.getId());
		listOfEscortingAttributeIds.add(escortingAttribute2.getId());

		assertTrue(internalCtxBroker.setHistoryTuples(primaryAttribute.getId(), listOfEscortingAttributeIds).get());	
		internalCtxBroker.update(primaryAttribute);

		primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"secondValue").get();
		//CtxAttribute tupleAttr = lookupAttrHelp("tuple_primaryAttribute");
		assertNotNull(lookupAttrHelp("tuple_primaryAttribute"));

		//System.out.println("1 tupleAttrType: "+tupleAttr.getType());
		//List historyList = internalCtxBroker.retrieveHistory(primaryAttribute.getId(), null,null).get();
		//System.out.println("2. "+historyList);

		escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingValue1_zz").get();
		//escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingValue2_yy").get();

		primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"thirdValue").get();

		escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingValue1_oo").get();
		escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingValue2_tt").get();

		primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"forthValue").get();

		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults = internalCtxBroker.retrieveHistoryTuples(primaryAttribute.getId(), listOfEscortingAttributeIds, null, null).get();

		assertEquals(4,tupleResults.size());

		printHocTuplesDB(tupleResults);
		System.out.println("add new attribute in an existing tuple");

		CtxAttribute escortingAttribute3 = (CtxAttribute) internalCtxBroker.createAttribute(scope.getId(),"escortingAttribute3").get();
		//escortingAttribute3.setHistoryRecorded(true);
		List<CtxAttributeIdentifier> newlistOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
		newlistOfEscortingAttributeIds.add(escortingAttribute1.getId());
		newlistOfEscortingAttributeIds.add(escortingAttribute2.getId());
		newlistOfEscortingAttributeIds.add(escortingAttribute3.getId());
		internalCtxBroker.updateHistoryTuples(primaryAttribute.getId(), newlistOfEscortingAttributeIds);

		escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingValue1_XX").get();
		escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingValue2_YY").get();
		escortingAttribute3 =  internalCtxBroker.updateAttribute(escortingAttribute3.getId(),(Serializable)"escortingValue3_ZZ").get();
		primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"fifthValue").get();

		escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingValue1_XXX").get();
		escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingValue2_YYY").get();
		escortingAttribute3 =  internalCtxBroker.updateAttribute(escortingAttribute3.getId(),(Serializable)"escortingValue3_ZZZ").get();
		primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"sixthValue").get();

		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> updatedTupleResults = internalCtxBroker.retrieveHistoryTuples(primaryAttribute.getId(), listOfEscortingAttributeIds, null, null).get();
		System.out.println("updatedTupleResults "+updatedTupleResults);
		assertEquals(4,tupleResults.size());
		//printHocTuplesDB(updatedTupleResults);
		//TODO : add more test for attribute values of type binary
	}




	@Test
	public void testHistoryTupleDataRetrieval() throws CtxException, InterruptedException, ExecutionException {

		final CtxEntity scope;
		CtxAttribute primaryAttribute;
		CtxAttribute escortingAttribute1;
		CtxAttribute escortingAttribute2;

		scope = (CtxEntity)internalCtxBroker.createEntity("entType").get();
		// Create the attribute to be tested
		primaryAttribute = (CtxAttribute) internalCtxBroker.createAttribute(scope.getId(), "primaryAttribute").get();
		primaryAttribute.setStringValue("fistValue");
		primaryAttribute.setHistoryRecorded(true);
		internalCtxBroker.update(primaryAttribute);

		escortingAttribute1 = (CtxAttribute)internalCtxBroker.createAttribute(scope.getId(), "escortingAttribute1").get();
		escortingAttribute1.setHistoryRecorded(true);
		escortingAttribute2 = (CtxAttribute)internalCtxBroker.createAttribute(scope.getId(), "escortingAttribute2").get();
		escortingAttribute2.setHistoryRecorded(true);

		escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingValue1").get();
		escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingValue2").get();


		List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
		listOfEscortingAttributeIds.add(escortingAttribute1.getId());
		listOfEscortingAttributeIds.add(escortingAttribute2.getId());

		assertTrue(internalCtxBroker.setHistoryTuples(primaryAttribute.getId(), listOfEscortingAttributeIds).get());	
		internalCtxBroker.update(primaryAttribute);

		primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"secondValue").get();
		//CtxAttribute tupleAttr = lookupAttrHelp("tuple_primaryAttribute");
		assertNotNull(lookupAttrHelp("tuple_primaryAttribute"));

		//System.out.println("1 tupleAttrType: "+tupleAttr.getType());
		//List historyList = internalCtxBroker.retrieveHistory(primaryAttribute.getId(), null,null).get();
		//System.out.println("2. "+historyList);

		escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingValue3").get();
		escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingValue4").get();

		primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"thirdValue").get();

		escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingValue5").get();
		escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),null).get();

		primaryAttribute =  internalCtxBroker.updateAttribute(primaryAttribute.getId(),(Serializable)"forthValue").get();

		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults = internalCtxBroker.retrieveHistoryTuples(primaryAttribute.getId(), listOfEscortingAttributeIds, null, null).get();

		assertEquals(4,tupleResults.size());
		System.out.println("tupleResults: "+tupleResults);
		printHocTuplesDB(tupleResults);

		//TODO : add more test for attribute values of type binary
	}

	protected CtxAttribute lookupAttrHelp(String type){
		CtxAttribute ctxAttr = null;
		try {
			List<CtxIdentifier> tupleAttrList = internalCtxBroker.lookup(CtxModelType.ATTRIBUTE,type).get();
			CtxIdentifier ctxId = tupleAttrList.get(0);
			ctxAttr =  (CtxAttribute) this.internalCtxBroker.retrieve(ctxId).get();

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
		return ctxAttr;
	}

	protected void printDB(){
		CtxEntity fake;
		try {
			fake = (CtxEntity)internalCtxBroker.createEntity("fake").get();
			CtxAttribute fakeAttribute = (CtxAttribute) internalCtxBroker.createAttribute(fake.getId(), "fakeAttribute").get();
			internalCtxBroker.retrieveHistory(fakeAttribute.getId(), 2);
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

	protected void printHocTuplesDB(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults){

		int i = 0;
		for (CtxHistoryAttribute primary : tupleResults.keySet()){
			String primaryValue = null;
			if (primary.getStringValue() != null) primaryValue =primary.getStringValue();


			String escValueTotal = null;
			for(CtxHistoryAttribute escortingAttr: tupleResults.get(primary)){
				String escValue = null;
				if (escortingAttr.getStringValue() != null )  escValue =escortingAttr.getStringValue();	
				escValueTotal = escValueTotal+" "+escValue; 
				//System.out.println("escValue: "+escValue);
			}
			System.out.println(i+ " primaryValue: "+primaryValue+ " escValues: "+escValueTotal);
			i++;
		}
	}
}