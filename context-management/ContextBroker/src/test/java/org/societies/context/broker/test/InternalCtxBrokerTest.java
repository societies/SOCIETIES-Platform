/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER //SystemS (ICCS), LAKE
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
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.junit.runner.RunWith;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.context.api.user.inference.IUserCtxInferenceMgr;
import org.societies.context.broker.api.security.ICtxAccessController;
import org.societies.context.broker.impl.InternalCtxBroker;
import org.societies.context.broker.test.util.MockBlobClass;
import org.societies.context.community.db.impl.CommunityCtxDBMgr;
import org.societies.context.user.db.impl.UserCtxDBMgr;
import org.societies.context.userHistory.impl.UserContextHistoryManagement;


/**
 * Describe your class here...
 *
 * @author 
 *
 *
 */

public class InternalCtxBrokerTest {

	private static final String OWNER_IDENTITY_STRING = "myFooIIdentity@societies.local";
	private static final String NETWORK_NODE_STRING = "myFooIIdentity@societies.local/node";
	private static final String CIS_IDENTITY_STRING = "FooCISIIdentity@societies.local";
	//myFooIIdentity@societies.local
	private static final List<String> INF_TYPES_LIST = new ArrayList<String>(); 
	
	
	private InternalCtxBroker internalCtxBroker;

	private static IIdentityManager mockIdentityMgr = mock(IIdentityManager.class);
	private static IIdentity cssMockIdentity = mock(IIdentity.class);
	private static IIdentity cisMockIdentity = mock(IIdentity.class);
	private static INetworkNode mockNetworkNode = mock(INetworkNode.class);

	private static ICtxAccessController mockCtxAccessController = mock(ICtxAccessController.class);

	private static IUserCtxInferenceMgr mockUserCtxInferenceMgr = mock(IUserCtxInferenceMgr.class);
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		INF_TYPES_LIST.add(CtxAttributeTypes.LOCATION_SYMBOLIC);
		INF_TYPES_LIST.add(CtxAttributeTypes.LOCATION_COORDINATES);
		
		when(mockIdentityMgr.getThisNetworkNode()).thenReturn(mockNetworkNode);
		when(mockNetworkNode.getBareJid()).thenReturn(OWNER_IDENTITY_STRING);
		when(mockIdentityMgr.fromJid(OWNER_IDENTITY_STRING)).thenReturn(cssMockIdentity);
		when(mockNetworkNode.toString()).thenReturn(NETWORK_NODE_STRING);
		when(mockIdentityMgr.isMine(cssMockIdentity)).thenReturn(true);
		
		when(cssMockIdentity.toString()).thenReturn(OWNER_IDENTITY_STRING);
		when(cssMockIdentity.getType()).thenReturn(IdentityType.CSS);

		when(cisMockIdentity.getType()).thenReturn(IdentityType.CIS);
		when(cisMockIdentity.toString()).thenReturn(CIS_IDENTITY_STRING);

		//IIdentity scopeID = this.idMgr.fromJid(communityCtxEnt.getOwnerId());
		when(mockIdentityMgr.fromJid(CIS_IDENTITY_STRING)).thenReturn(cisMockIdentity);
		
		//this.commMgr.getIdManager().fromJid(ctxModelObj.getOwnerId());

	//	when(mockUserCtxInferenceMgr.getInferrableTypes()).thenReturn(INF_TYPES_LIST);
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
		internalCtxBroker.setCommunityCtxDBMgr(new CommunityCtxDBMgr());
		internalCtxBroker.setUserCtxHistoryMgr(new UserContextHistoryManagement());
		//internalCtxBroker.setUserCtxInferenceMgr(new UserCtxInferenceMgr());
		//internalCtxBroker.setIdentityMgr(mockIdentityMgr);
		internalCtxBroker.createIndividualEntity(cssMockIdentity, CtxEntityTypes.PERSON); // TODO remove?
		//internalCtxBroker.createCssNode(mockNetworkNode); // TODO remove?
		
		internalCtxBroker.setCtxAccessController(mockCtxAccessController);

		
		//internalCtxBroker.setUserCtxInferenceMgr(mockUserCtxInferenceMgr);
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

		internalCtxBroker = null;
	}


	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveIndividualEntity(IIdentity)}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 */
	@Ignore
	@Test
	public void testRetrieveIndividualEntity() throws Exception {

		final IndividualCtxEntity ownerEnt = 
				internalCtxBroker.retrieveIndividualEntity(cssMockIdentity).get();
		assertNotNull(ownerEnt);
		assertEquals(OWNER_IDENTITY_STRING, ownerEnt.getId().getOwnerId());
		assertEquals(CtxEntityTypes.PERSON, ownerEnt.getType());
		assertFalse(ownerEnt.getAttributes(CtxAttributeTypes.ID).isEmpty());
		assertEquals(1, ownerEnt.getAttributes(CtxAttributeTypes.ID).size());
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveCssNode(org.societies.api.identity.INetworkNode)}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 */
	@Ignore
	@Test
	public void testRetrieveCssNode() throws Exception {

		final CtxEntity cssNodeEnt = internalCtxBroker.retrieveCssNode(mockNetworkNode).get();
		assertNotNull(cssNodeEnt);
		assertEquals(CtxEntityTypes.CSS_NODE, cssNodeEnt.getType());
		assertFalse(cssNodeEnt.getAttributes(CtxAttributeTypes.ID).isEmpty());
		assertEquals(1, cssNodeEnt.getAttributes(CtxAttributeTypes.ID).size());
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
	@Ignore
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
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createAttribute(org.societies.api.context.model.CtxCommunityEntityIdentifier, java.lang.String)}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Ignore
	@Test
	public void testCreateAttributeByCtxCommunityEntityIdentifierString() throws CtxException, InterruptedException, ExecutionException {

		// Create the attribute's scope		
		final CommunityCtxEntity communityCtxEnt = internalCtxBroker.createCommunityEntity(cisMockIdentity).get();
		// Create the attribute to be tested
		final CtxAttribute ctxAttribute = internalCtxBroker.createAttribute(communityCtxEnt.getId(), CtxAttributeTypes.POLITICAL_VIEWS).get();

		assertNotNull(ctxAttribute);
		assertNotNull(ctxAttribute.getId());
		assertEquals(ctxAttribute.getId().getScope(), communityCtxEnt.getId());
		assertEquals(ctxAttribute.getOwnerId(), CIS_IDENTITY_STRING);
		assertTrue(ctxAttribute.getType().equalsIgnoreCase(CtxAttributeTypes.POLITICAL_VIEWS));
	}
	@Ignore
	@Test
	public void testLookupCommunityCtxAttr() throws CtxException, InterruptedException, ExecutionException {
		
		// Create the attribute's scope		
		final CommunityCtxEntity communityCtxEnt = internalCtxBroker.createCommunityEntity(cisMockIdentity).get();
		//System.out.println("communityCtxEnt type :" + communityCtxEnt.getType());
		// Create the attribute to be tested
		CtxAttribute commCtxAttributeComm = internalCtxBroker.createAttribute(communityCtxEnt.getId(), CtxAttributeTypes.POLITICAL_VIEWS).get();
		commCtxAttributeComm.setStringValue("foo");
		commCtxAttributeComm = (CtxAttribute) internalCtxBroker.update(commCtxAttributeComm).get();
		//System.out.println("commCtxAttributeComm:" + commCtxAttributeComm);
		
		// test lookup and retrieve
		List<CtxEntityIdentifier> commListResults = internalCtxBroker.lookupEntities("community", CtxAttributeTypes.POLITICAL_VIEWS, "foo", "foo").get();
		//System.out.println(" commListResults size :"+commListResults.size());
	}

	@Ignore
	@Test
	public void testRetrieveCommunityCtxAttr() throws CtxException, InterruptedException, ExecutionException {
		
	
		// Create the attribute's scope		
		final CommunityCtxEntity communityCtxEnt = internalCtxBroker.createCommunityEntity(cisMockIdentity).get();
	//	System.out.println("communityCtxEnt type :" + communityCtxEnt.getType());
	//	System.out.println(" commEntResults  :"+communityCtxEnt.getId());
	//	System.out.println(" commEntResults  :"+communityCtxEnt.getId().getOwnerId());
		// den kseroume an einai css i cis ... opote den kseroume pia vasi na kalesei
		if(communityCtxEnt.getId().getOwnerId().compareToIgnoreCase("cis")>1) System.out.println(communityCtxEnt.getId().getOwnerId().compareToIgnoreCase("cis"));
		// Create the attribute to be tested
		
			CtxAttribute commCtxAttributeComm = internalCtxBroker.createAttribute(communityCtxEnt.getId(), CtxAttributeTypes.POLITICAL_VIEWS).get();
		commCtxAttributeComm.setStringValue("foo");
		commCtxAttributeComm = (CtxAttribute) internalCtxBroker.update(commCtxAttributeComm).get();
		//System.out.println("commCtxAttributeComm:" + commCtxAttributeComm);
		
		// test lookup and retrieve
		CommunityCtxEntity commEntRetrieved = (CommunityCtxEntity) internalCtxBroker.retrieve(communityCtxEnt.getId()).get();
		//System.out.println(" community Entity Results  :"+commEntRetrieved);
		assertEquals(communityCtxEnt, commEntRetrieved);
		
		CtxAttribute commCtxAttributeCommRetrieved =  (CtxAttribute) internalCtxBroker.retrieve(commCtxAttributeComm.getId()).get();
		//System.out.println(" community Attribute Results  :"+commCtxAttributeCommRetrieved);
		assertEquals(commCtxAttributeComm, commCtxAttributeCommRetrieved);
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

		final CtxEntity ctxEntity;

		final Future<CtxEntity> futureCtxEntity = internalCtxBroker.createEntity("entType");
		ctxEntity = futureCtxEntity.get();
		assertNotNull(ctxEntity);
		assertTrue(ctxEntity.getType().equalsIgnoreCase("entType"));
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createCommunityEntity(java.lang.String)}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Ignore
	@Test
	public void testCreateCommunityEntityByString() throws CtxException, InterruptedException, ExecutionException {

		final CommunityCtxEntity communityCtxEnt = internalCtxBroker.createCommunityEntity(cisMockIdentity).get();
		assertNotNull(communityCtxEnt);
		assertTrue(communityCtxEnt.getType().equalsIgnoreCase("community"));
		final CommunityCtxEntity communityCtxEntRetrieved = (CommunityCtxEntity) internalCtxBroker.retrieve(communityCtxEnt.getId()).get();
		assertEquals(communityCtxEnt, communityCtxEntRetrieved);
	}

	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createIndividualEntity(IIdentity, String)}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Ignore
	@Test
	public void testCreateIndividualEntity() throws CtxException, InterruptedException, ExecutionException {

		final IndividualCtxEntity ownerEnt = 
				internalCtxBroker.createIndividualEntity(cssMockIdentity, CtxEntityTypes.PERSON).get();
		assertNotNull(ownerEnt);
		assertEquals(OWNER_IDENTITY_STRING, ownerEnt.getId().getOwnerId());
		assertEquals(CtxEntityTypes.PERSON, ownerEnt.getType());
		assertFalse(ownerEnt.getAttributes(CtxAttributeTypes.ID).isEmpty());
		assertEquals(1, ownerEnt.getAttributes(CtxAttributeTypes.ID).size());
	}


	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createAssociation(java.lang.String)}.
	 */
	@Ignore
	@Test
	public void testCreateAssociationString() {
		try {
			CtxAssociation ctxAssocHasServ = internalCtxBroker.createAssociation("hasService").get();

			List<CtxIdentifier> assocIdentifierList = internalCtxBroker.lookup(CtxModelType.ASSOCIATION, "hasService").get();
			assertEquals(assocIdentifierList.size(),1);
			CtxIdentifier retrievedAssocHasServID = assocIdentifierList.get(0);
			assertEquals(retrievedAssocHasServID.toString(),ctxAssocHasServ.getId().toString());
			//System.out.println("assocID "+ retrievedAssocHasServID.toString());

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
	@Ignore
	public void testStoreRetrieveServiceParameters2() {


		//	ServiceResourceIdentifier serviceId2 = new ServiceResourceIdentifier();
		//		serviceId2.setIdentifier(new URI("http://testService2"));

		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		try {
			serviceId1.setIdentifier(new URI("http://testService1"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		//System.out.println("testStoreRetrieveServiceParameters service created :"+ serviceId1);

		try {
		//IndividualCtxEntity operator = (IndividualCtxEntity) this.internalCtxBroker.createIndividualEntity(cssId, ownerType).createIndividualEntity().get();
		IndividualCtxEntity operator = this.internalCtxBroker.retrieveIndividualEntity(cssMockIdentity).get();
			//System.out.println("operator "+operator);
			// create service attribute
			CtxAttribute service1Attr = this.internalCtxBroker.createAttribute(operator.getId(), "service").get();
			final byte[] service1Blob = SerialisationHelper.serialise(serviceId1);
			service1Attr = this.internalCtxBroker.updateAttribute(service1Attr.getId(), service1Blob).get();

			// retrieve service attribute
			List<CtxIdentifier> listAttrs = this.internalCtxBroker.lookup(CtxModelType.ATTRIBUTE, "service").get();
			CtxAttributeIdentifier serviceAttrID = (CtxAttributeIdentifier) listAttrs.get(0);
			CtxAttribute ctxAttrRetrieved = (CtxAttribute) this.internalCtxBroker.retrieveAttribute(serviceAttrID, false).get();

			ServiceResourceIdentifier ctxAttrRetrievedValue = (ServiceResourceIdentifier) SerialisationHelper.deserialise(ctxAttrRetrieved.getBinaryValue(), this.getClass().getClassLoader());
			assertEquals(ctxAttrRetrievedValue.getServiceInstanceIdentifier(),serviceId1.getServiceInstanceIdentifier());
			//System.out.println("testStoreRetrieveServiceParameters service retrieved :"+ ctxAttrRetrievedValue);


		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
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
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#createAssociation(java.lang.String)}.
	 */
	@Ignore
	@Test
	public void testStoreRetrieveServiceParameters() {

		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		ServiceResourceIdentifier serviceId2 = new ServiceResourceIdentifier();
		try {
			serviceId1.setIdentifier(new URI("http://testService1"));
			serviceId2.setIdentifier(new URI("http://testService2"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		try {

			final IndividualCtxEntity operator = 
					internalCtxBroker.retrieveIndividualEntity(cssMockIdentity).get();
			
			CtxEntity serviceEnt = this.internalCtxBroker.createEntity(CtxEntityTypes.SERVICE).get();

			CtxAssociation usesServiceAssoc = this.internalCtxBroker.createAssociation(CtxAssociationTypes.USES_SERVICES).get();
			usesServiceAssoc.addChildEntity(operator.getId());
			usesServiceAssoc.addChildEntity(serviceEnt.getId());
			this.internalCtxBroker.update(operator);
			this.internalCtxBroker.update(usesServiceAssoc);
			
			CtxEntity parServiceEnt = this.internalCtxBroker.createEntity(CtxEntityTypes.SERVICE_PARAMETER).get();
			CtxAttribute parNameAttr = this.internalCtxBroker.createAttribute(parServiceEnt.getId(), CtxAttributeTypes.PARAMETER_NAME).get();
			parNameAttr.setStringValue("Volume");
			CtxAttribute parValueAttr = this.internalCtxBroker.createAttribute(parServiceEnt.getId(), CtxAttributeTypes.LAST_ACTION).get();

			CtxAssociation hasParametersServiceAssoc = this.internalCtxBroker.createAssociation(CtxAssociationTypes.HAS_PARAMETERS).get();
			hasParametersServiceAssoc.addChildEntity(serviceEnt.getId());
			hasParametersServiceAssoc.addChildEntity(parServiceEnt.getId());
			this.internalCtxBroker.update(parServiceEnt);
			this.internalCtxBroker.update(hasParametersServiceAssoc);

			//find a SERVICE_PARAMETER entity with a specific PARAMETER_NAME attribute under a SERVICE entity
			// e.g. PARAMETER_NAME attribute has value "Volume"

			CtxEntity serviceParamEntityResult = null;
			//returns all services assigned to user
			Set<CtxAssociationIdentifier> operatorServicesAssocs = operator.getAssociations(CtxAssociationTypes.USES_SERVICES);
			//System.out.println("************ ");
			//System.out.println("************ operatorServicesAssocs "+operatorServicesAssocs.size());
			
			CtxAssociation assocUseServices = null;

			for(CtxAssociationIdentifier assocId: operatorServicesAssocs){
				assocUseServices = (CtxAssociation) this.internalCtxBroker.retrieve(assocId).get();
			}

			// the set contains all entities of type service that the operator is using	
			Set<CtxEntityIdentifier> servicesSet = assocUseServices.getChildEntities(CtxEntityTypes.SERVICE);

			for(CtxEntityIdentifier serviceEntID : servicesSet){
				CtxEntity serviceEntity = (CtxEntity) this.internalCtxBroker.retrieve(serviceEntID).get();

				Set<CtxAssociationIdentifier> hasParamAssocSet = serviceEntity.getAssociations(CtxAssociationTypes.HAS_PARAMETERS);
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
	@Ignore
	@Test
	public void testRetrieveEntitiesAssociationString() {
		try {
			//System.out.println("testRetrieveEntitiesAssociationString");

			CtxEntity person = this.internalCtxBroker.createEntity("Person").get();
			CtxEntity serviceEnt = this.internalCtxBroker.createEntity("ServiceID").get();
			CtxAttribute serviceAttr = this.internalCtxBroker.createAttribute(serviceEnt.getId(), "parameterName1").get();
			serviceAttr.setStringValue("paramValue");

			CtxAssociation hasServiceAssoc = this.internalCtxBroker.createAssociation("hasService").get();
			hasServiceAssoc.addChildEntity(serviceEnt.getId());
			hasServiceAssoc.addChildEntity(person.getId());
			hasServiceAssoc.setParentEntity(person.getId());

			hasServiceAssoc = (CtxAssociation) this.internalCtxBroker.update(hasServiceAssoc).get();
			//System.out.println("hasServiceAssoc "+hasServiceAssoc);

			serviceEnt = (CtxEntity) this.internalCtxBroker.update(serviceEnt).get();
			person = (CtxEntity) this.internalCtxBroker.update(person).get();

			//retrieve assoc data
			CtxAssociationIdentifier retrievedAssocID = null;
			CtxEntity serviceRetrieved = null;
			CtxAttribute ctxServiceAttrRetrieved = null;

			List<CtxIdentifier> list = this.internalCtxBroker.lookup(CtxModelType.ENTITY ,"Person").get();
			if(list.size()>0) {
				CtxIdentifier persID = list.get(0);				
				CtxEntity retrievedEnt = (CtxEntity) this.internalCtxBroker.retrieve(persID).get();

				assertEquals(retrievedEnt,person);			
				Set<CtxAssociationIdentifier> assocIDSet = retrievedEnt.getAssociations();

				for(CtxAssociationIdentifier assocID :assocIDSet){
					assertEquals(assocID,hasServiceAssoc.getId());
				}

				//	//System.out.println("Association1 set " + assocIDSet);
				//	//System.out.println("Association1 set " + assocIDSet.size());

				Set<CtxAssociationIdentifier> assocIDSet2 = retrievedEnt.getAssociations("hasService");
				//		//System.out.println("assocIDSet2 size "+assocIDSet2.size());
				//		//System.out.println("assocIDSet2 "+assocIDSet2);

				for(CtxAssociationIdentifier assocID : assocIDSet2 ){
					//System.out.println("Association2 set " + assocID);
					retrievedAssocID = assocID;
				}
				CtxAssociation hasServiceRetrieved = (CtxAssociation) this.internalCtxBroker.retrieve(retrievedAssocID).get();

				if(hasServiceRetrieved != null && hasServiceAssoc != null){
					assertEquals(hasServiceRetrieved,hasServiceAssoc);
					//if(hasServiceRetrieved.equals(hasServiceAssoc))//System.out.println("CtxAssociation Retrieved matches created CtxAssociation");	
				}

				//System.out.println("hasServiceRetrieved "+ hasServiceRetrieved);
				Set<CtxEntityIdentifier> assocEntitiesSet = hasServiceRetrieved.getChildEntities("ServiceID");
				for(CtxEntityIdentifier ctxAssocEntityId : assocEntitiesSet ){
					serviceRetrieved = (CtxEntity) this.internalCtxBroker.retrieve(ctxAssocEntityId).get();		
					//System.out.println("ctxAssocEntityId "+ ctxAssocEntityId);
				}
				//System.out.println("^^^^^^^^^^^^^^^^ serviceRetrieved "+ serviceRetrieved.getId());
				//System.out.println("^^^^^^^^^^^^^^^^ serviceEnt "+ serviceEnt.getId());
				assertEquals(serviceRetrieved,serviceEnt);
				//if(serviceRetrieved.equals(serviceEnt)) //System.out.println("CtxAssociation Retrieved matches created CtxAssociation");

				for(CtxAttribute ctxAttributeRetrived : serviceRetrieved.getAttributes("parameterName1") ){
					//	//System.out.println("ctxAttributeRetrived "+ ctxAttributeRetrived);
					ctxServiceAttrRetrieved = ctxAttributeRetrived;
				}
				//if(ctxServiceAttrRetrieved.equals(serviceAttr)) //System.out.println("ctxServiceAttrRetrieved Retrieved matches created serviceAttr");
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
	@Ignore
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
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#lookup(org.societies.api.context.model.CtxEntityIdentfier,org.societies.api.context.model.CtxModelType,java.lang.String)}.
	 */
	@Ignore
	@Test
	public void testLookupByCtxEntityIdentfierCtxModelTypeString() throws Exception {

		final CtxEntity ent1 = this.internalCtxBroker.createEntity("entity1").get();
		final CtxEntity ent2 = this.internalCtxBroker.createEntity("entity2").get();
		final CtxEntity ent3 = this.internalCtxBroker.createEntity("entity3").get();

		// Create test attributes:
		// - entity1
		final CtxAttribute ent1attr1 = this.internalCtxBroker.createAttribute(ent1.getId(),"attr1").get();
		final CtxAttribute ent1attr2 = this.internalCtxBroker.createAttribute(ent1.getId(),"attr2").get();
		final CtxAttribute ent1attr3 = this.internalCtxBroker.createAttribute(ent1.getId(),"attr3").get();
		final CtxAttribute ent1attr3b = this.internalCtxBroker.createAttribute(ent1.getId(),"attr3").get();
		// - entity2
		final CtxAttribute ent2attr1 = this.internalCtxBroker.createAttribute(ent2.getId(),"attr1").get();
		final CtxAttribute ent2attr2 = this.internalCtxBroker.createAttribute(ent2.getId(),"attr2").get();
		// - entity3
		final CtxAttribute ent3attr1 = this.internalCtxBroker.createAttribute(ent3.getId(),"attr1").get();

		// verify lookup under ent1
		List<CtxIdentifier> ids = this.internalCtxBroker.lookup(
				ent1.getId(), CtxModelType.ATTRIBUTE, "notexists").get();
		assertNotNull(ids);
		assertTrue(ids.isEmpty());

		ids = this.internalCtxBroker.lookup(ent1.getId(), CtxModelType.ATTRIBUTE, "attr1").get();
		assertEquals(1, ids.size());
		assertTrue(ids.contains(ent1attr1.getId()));
		
		ids = this.internalCtxBroker.lookup(ent1.getId(), CtxModelType.ATTRIBUTE, "attr2").get();
		assertEquals(1, ids.size());
		assertTrue(ids.contains(ent1attr2.getId()));
		
		ids = this.internalCtxBroker.lookup(ent1.getId(), CtxModelType.ATTRIBUTE, "attr3").get();
		assertEquals(2, ids.size());
		assertTrue(ids.contains(ent1attr3.getId()));
		assertTrue(ids.contains(ent1attr3b.getId()));
		
		// verify lookup under ent2
		ids = this.internalCtxBroker.lookup(ent2.getId(), CtxModelType.ATTRIBUTE, "attr1").get();
		assertEquals(1, ids.size());
		assertTrue(ids.contains(ent2attr1.getId()));

		ids = this.internalCtxBroker.lookup(ent2.getId(), CtxModelType.ATTRIBUTE, "attr2").get();
		assertEquals(1, ids.size());
		assertTrue(ids.contains(ent2attr2.getId()));

		ids = this.internalCtxBroker.lookup(ent2.getId(), CtxModelType.ATTRIBUTE, "attr3").get();
		assertTrue(ids.isEmpty());

		// verify lookup under ent3
		ids = this.internalCtxBroker.lookup(ent3.getId(), CtxModelType.ATTRIBUTE, "attr1").get();
		assertEquals(1, ids.size());
		assertTrue(ids.contains(ent3attr1.getId()));

		ids = this.internalCtxBroker.lookup(ent3.getId(), CtxModelType.ATTRIBUTE, "attr2").get();
		assertTrue(ids.isEmpty());

		ids = this.internalCtxBroker.lookup(ent3.getId(), CtxModelType.ATTRIBUTE, "attr3").get();
		assertTrue(ids.isEmpty());
		
		// test expected IllegalArgumentException
		try {
			this.internalCtxBroker.lookup(ent1.getId(), CtxModelType.ENTITY, "foo");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException iae) {}
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
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Ignore
	@Test
	public void testRemoveCtxIdentifier() throws CtxException, InterruptedException, ExecutionException {
		
		final CtxEntity ctxEntity;

		final Future<CtxEntity> futureCtxEntity = internalCtxBroker.createEntity("entType");
		ctxEntity = futureCtxEntity.get();
		final Future<CtxModelObject> removed = internalCtxBroker.remove(ctxEntity.getId());
		assertNotNull(removed);
		//assertTrue(ctxEntity.getType().equalsIgnoreCase("entType"));
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
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Ignore
	@Test
	public void testRetrieveCtxIdentifier() throws CtxException, InterruptedException, ExecutionException {
		
		final CtxEntity ctxEntity;

		final Future<CtxEntity> futureCtxEntity = internalCtxBroker.createEntity("entType");
		ctxEntity = futureCtxEntity.get();
		final Future<CtxModelObject> retrieved = internalCtxBroker.retrieve(ctxEntity.getId());
		assertNotNull(retrieved);
		//assertTrue(ctxEntity.getType().equalsIgnoreCase("entType"));
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

	@Ignore
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
	@Ignore
	@Test
	public void testRetrieveHistoryCtxAttributeIdentifierDateDate() {


		final CtxAttribute emptyAttribute;
		CtxAttribute initialisedAttribute;
		final CtxEntity scope;
		//System.out.println("testRetrieveHistoryCtxAttributeIdentifierDateDate");

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

			//	Future<List<CtxHistoryAttribute>> historyFuture = internalCtxBroker.retrieveHistory(initialisedAttribute.getId(), null, null);

			List<CtxHistoryAttribute> history = internalCtxBroker.retrieveHistory(initialisedAttribute.getId(), null, null).get();

			for(CtxHistoryAttribute hocAttr: history){
				//System.out.println(history.size());
				//System.out.println("history List id:"+hocAttr.getId()+" getLastMod:"+hocAttr.getLastModified() +" hocAttr value:"+hocAttr.getIntegerValue());		
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
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Ignore
	@Test
	public void testUpdateByCtxEntity() throws CtxException, InterruptedException, ExecutionException {
		
		final CtxEntity entity;
		final CtxAttribute attribute;
		
		// Create the entity to be tested
		Future<CtxEntity> futureEntity = internalCtxBroker.createEntity("entType");
		entity = futureEntity.get();
		
		// Create the attribute to be tested
		Future<CtxAttribute> futureCtxAttribute = internalCtxBroker.createAttribute(entity.getId(), "attrType");
		attribute = futureCtxAttribute.get();

		// Set the attribute's initial value
		attribute.setIntegerValue(100);
		attribute.setHistoryRecorded(true);

		// Verify the initial attribute value
		assertEquals(new Integer(100), attribute.getIntegerValue());
		
		Future<CtxModelObject> updatedEntity = internalCtxBroker.update(entity);
		assertNotNull(updatedEntity);
		
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
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws CtxException 
	 */
	@Ignore
	@Test
	public void testUpdateByCtxAssociation() throws InterruptedException, ExecutionException, CtxException {
		
		final CtxAssociation emptyAssociation;
		final CtxEntity scope;
		final CtxEntity parent;

		// Create the Association's scope
		Future<CtxEntity> futureEntity = internalCtxBroker.createEntity("entType");
		scope = futureEntity.get();
		Future<CtxEntity> futureEntity2 = internalCtxBroker.createEntity("entType_II");
		parent = futureEntity2.get();
		
		// Create the Association to be tested
		Future<CtxAssociation> futureCtxAssociation = internalCtxBroker.createAssociation("assocType");
		emptyAssociation = futureCtxAssociation.get();

		// Set the Association's initial value
		emptyAssociation.setParentEntity(scope.getId());
		assertNotNull(emptyAssociation);
		assertEquals(scope.getId(), emptyAssociation.getParentEntity());
		// Update the attribute value
		emptyAssociation.setParentEntity(parent.getId());
		assertNotNull(emptyAssociation);
		assertEquals(parent.getId(), emptyAssociation.getParentEntity());
		
		
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
	@Ignore
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
	@Ignore
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
		//System.out.println("primary: "+ primaryAttribute.getId());
		//System.out.println("escorting tuple list: "+ listOfEscortingAttributeIds);

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
	@Ignore
	@Test
	public void testUpdateHistoryTuples() {

		final CtxEntity scope;
		CtxAttribute primaryAttribute;
		CtxAttribute escortingAttribute1;
		CtxAttribute escortingAttribute2;
		CtxAttribute escortingAttribute3;
		//System.out.println("********* testUpdateHistoryTuples");
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
			//System.out.println("********* tuplesBeforeUpdate "+tuplesBeforeUpdate +" size "+tuplesBeforeUpdate.size());
			assertEquals(3,tuplesBeforeUpdate.size());

			escortingAttribute3 = (CtxAttribute)internalCtxBroker.createAttribute(scope.getId(), "escortingAttribute3").get();
			listOfEscortingAttributeIds.add(escortingAttribute3.getId());
			//System.out.println("listOfEscortingAttributeIds "+listOfEscortingAttributeIds.size());

			internalCtxBroker.updateHistoryTuples(primaryAttribute.getId(),listOfEscortingAttributeIds);
			List<CtxAttributeIdentifier> tuplesAfterUpdate = internalCtxBroker.getHistoryTuples(primaryAttribute.getId(), null).get();
			assertEquals(4,tuplesAfterUpdate.size());
			//System.out.println("********* tuplesAfterUpdate "+tuplesAfterUpdate +" size "+tuplesAfterUpdate.size());

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


	@Ignore
	@Test
	public void testLookupAttributeValues(){

		try {
			CtxEntity entity1 = internalCtxBroker.createEntity(CtxEntityTypes.SERVICE).get();
			CtxAttribute ctxAttribute10 = internalCtxBroker.createAttribute(entity1.getId(), "blobValue").get();

			MockBlobClass binaryValue1 = new MockBlobClass(125);
			ctxAttribute10.setBinaryValue(SerialisationHelper.serialise(binaryValue1));
			internalCtxBroker.update(ctxAttribute10);
			CtxAttribute ctxAttribute11 = internalCtxBroker.createAttribute(entity1.getId(), "stringValue").get();
			ctxAttribute11.setStringValue("StrinA");
			internalCtxBroker.update(ctxAttribute11);

			CtxEntity entity2 = internalCtxBroker.createEntity(CtxEntityTypes.SERVICE).get();
			CtxAttribute ctxAttribute2 = internalCtxBroker.createAttribute(entity2.getId(), "blobValue").get();

			MockBlobClass binaryValue2 = new MockBlobClass(135);
			ctxAttribute2.setBinaryValue(SerialisationHelper.serialise(binaryValue2));
			internalCtxBroker.update(ctxAttribute2);
			CtxAttribute ctxAttribute21 = internalCtxBroker.createAttribute(entity2.getId(), "stringValue").get();
			ctxAttribute21.setStringValue("StringB");
			internalCtxBroker.update(ctxAttribute21);


			List<CtxEntityIdentifier> allServiceEntIds = new ArrayList<CtxEntityIdentifier>();

			List<CtxIdentifier> listServiceCtxIds = internalCtxBroker.lookup(CtxModelType.ENTITY,CtxEntityTypes.SERVICE).get();
			for(CtxIdentifier ctxId: listServiceCtxIds){
				CtxEntityIdentifier cxtEnt = (CtxEntityIdentifier) ctxId;
				allServiceEntIds.add(cxtEnt);
			}

			List<CtxEntityIdentifier> serviceEntIdBlobs = internalCtxBroker.lookupEntities(allServiceEntIds, "blobValue", binaryValue1).get();

			//System.out.println("results serviceEntIds:"+serviceEntIdBlobs);
			CtxEntityIdentifier entIdBlob = serviceEntIdBlobs.get(0);

			CtxEntity ent1 = (CtxEntity) internalCtxBroker.retrieve(entIdBlob).get();
			Set<CtxAttribute> atrrSet1 = ent1.getAttributes("blobValue");
			for(CtxAttribute attr: atrrSet1){
				final MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.deserialise(attr.getBinaryValue(), this.getClass().getClassLoader());
				//	//System.out.println("retrievedBlob.getSeed() "+retrievedBlob.getSeed());
				assertEquals(retrievedBlob.getSeed(),125);
			}


			List<CtxEntityIdentifier> serviceEntIdStrings = internalCtxBroker.lookupEntities(allServiceEntIds, "stringValue", "StringB").get();
			//System.out.println("results serviceEntIds:"+serviceEntIdStrings);

			CtxEntityIdentifier entIdString = serviceEntIdStrings.get(0);
			CtxEntity ent2 = (CtxEntity) internalCtxBroker.retrieve(entIdString).get();
			Set<CtxAttribute> atrrSet2 = ent2.getAttributes("stringValue");
			for(CtxAttribute attr: atrrSet2){
				//System.out.println("retrieved string "+attr.getStringValue());
				assertEquals(attr.getStringValue(),"StringB");
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void testHistoryTupleDataRetrievalByType() throws CtxException, InterruptedException, ExecutionException {


		//System.out.println("testHistoryTupleDataRetrievalByType");
		final CtxEntity scope1;
		final CtxEntity scope2;
		CtxAttribute primaryAttribute1;
		CtxAttribute primaryAttribute2;
		CtxAttribute escortingAttribute1;
		CtxAttribute escortingAttribute2;

		scope1 = (CtxEntity)internalCtxBroker.createEntity("entType").get();
		// Create the attribute to be tested
		primaryAttribute1 = (CtxAttribute) internalCtxBroker.createAttribute(scope1.getId(), "primaryAttribute").get();
		primaryAttribute1.setStringValue("fistValue");
		primaryAttribute1.setHistoryRecorded(true);

		scope2 = (CtxEntity)internalCtxBroker.createEntity("entType").get();
		// Create the attribute to be tested
		primaryAttribute2 = (CtxAttribute) internalCtxBroker.createAttribute(scope2.getId(), "primaryAttribute").get();
		primaryAttribute2.setStringValue("fistValue2");
		primaryAttribute2.setHistoryRecorded(true);

		//1.
		internalCtxBroker.update(primaryAttribute1);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		internalCtxBroker.update(primaryAttribute2);


		escortingAttribute1 = (CtxAttribute)internalCtxBroker.createAttribute(scope1.getId(), "escortingAttribute1").get();
		escortingAttribute1.setHistoryRecorded(true);
		escortingAttribute2 = (CtxAttribute)internalCtxBroker.createAttribute(scope1.getId(), "escortingAttribute2").get();
		escortingAttribute2.setHistoryRecorded(true);

		escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingValue1_xx").get();
		escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingValue2_xx").get();


		List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
		listOfEscortingAttributeIds.add(escortingAttribute1.getId());
		listOfEscortingAttributeIds.add(escortingAttribute2.getId());

		assertTrue(internalCtxBroker.setHistoryTuples(primaryAttribute1.getId(), listOfEscortingAttributeIds).get());	
		assertTrue(internalCtxBroker.setHistoryTuples(primaryAttribute2.getId(), listOfEscortingAttributeIds).get());
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//2.
		internalCtxBroker.update(primaryAttribute1);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		internalCtxBroker.update(primaryAttribute2);

		primaryAttribute1 =  internalCtxBroker.updateAttribute(primaryAttribute1.getId(),(Serializable)"secondValue1").get();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		primaryAttribute2 =  internalCtxBroker.updateAttribute(primaryAttribute2.getId(),(Serializable)"secondValue2").get();


		escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingValue1_zz").get();
		escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingValue2_yy").get();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//3.
		primaryAttribute1 =  internalCtxBroker.updateAttribute(primaryAttribute1.getId(),(Serializable)"thirdValue").get();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		primaryAttribute2 =  internalCtxBroker.updateAttribute(primaryAttribute2.getId(),(Serializable)"thirdValue").get();

		escortingAttribute1 =  internalCtxBroker.updateAttribute(escortingAttribute1.getId(),(Serializable)"escortingValue1_oo").get();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		escortingAttribute2 =  internalCtxBroker.updateAttribute(escortingAttribute2.getId(),(Serializable)"escortingValue2_tt").get();

		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//4.
		primaryAttribute1 =  internalCtxBroker.updateAttribute(primaryAttribute1.getId(),(Serializable)"forthValue").get();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		primaryAttribute2 =  internalCtxBroker.updateAttribute(primaryAttribute2.getId(),(Serializable)"forthValue").get();

		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults = internalCtxBroker.retrieveHistoryTuples("primaryAttribute", listOfEscortingAttributeIds, null, null).get();
		//System.out.println("testHistoryTupleDataRetrievalByType tupleResults "+ tupleResults);

	}

	@Ignore
	@Test
	public void testHistoryMultipleSizeTupleDataRetrieval() throws CtxException, InterruptedException, ExecutionException {


		//System.out.println("testHistoryMultipleSizeTupleDataRetrieval");
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
		String tupletype = "tuple_"+primaryAttribute.getId().toString();
		assertNotNull(lookupAttrHelp(tupletype));

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

		//System.out.println("**** " +tupleResults);
		
		assertEquals(4,tupleResults.size());

		printHocTuplesDB(tupleResults);
		//System.out.println("add new attribute in an existing tuple");

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
		//System.out.println("updatedTupleResults "+updatedTupleResults);
		assertEquals(4,tupleResults.size());
		//printHocTuplesDB(updatedTupleResults);
		//TODO : add more test for attribute values of type binary
	}



	@Ignore
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
		String tupletype = "tuple_"+primaryAttribute.getId().toString();
		assertNotNull(lookupAttrHelp(tupletype));

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
		//System.out.println("tupleResults: "+tupleResults);
		printHocTuplesDB(tupleResults);

		//TODO : add more test for attribute values of type binary
	}

	protected CtxAttribute lookupAttrHelp(String type){
		CtxAttribute ctxAttr = null;
		try {
			List<CtxIdentifier> tupleAttrList = internalCtxBroker.lookup(CtxModelType.ATTRIBUTE,type).get();
			CtxAttributeIdentifier ctxId = (CtxAttributeIdentifier) tupleAttrList.get(0);
			ctxAttr =  (CtxAttribute) this.internalCtxBroker.retrieveAttribute(ctxId,false).get();

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
			//System.out.println(i+ " primaryValue: "+primaryValue+ " escValues: "+escValueTotal);
			i++;
		}
	}
}