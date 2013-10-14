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
package org.societies.context.community.db.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.context.api.community.db.ICommunityCtxDBMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * 
 * @author Pavlos
 *  
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/test-context.xml"})
public class CommunityCtxDBMgrTest {

	private static final String CIS_IIDENTITY_STRING0 = "myCIS0.societies.local";
	private static final String CIS_IIDENTITY_STRING1 = "myCIS1.societies.local";
	private static final String CIS_IIDENTITY_STRING2 = "myCIS2.societies.local";
	private static final String CIS_IIDENTITY_STRING3 = "myCIS3.societies.local";
	private static final String CIS_IIDENTITY_STRING4 = "myCIS4.societies.local";
	private static final String CIS_IIDENTITY_STRING5 = "myCIS5.societies.local";
	private static final String CIS_IIDENTITY_STRING6 = "myCIS6.societies.local";
	private static final String CIS_IIDENTITY_STRING7 = "myCIS7.societies.local";
	private static final String CIS_IIDENTITY_STRING8 = "myCIS8.societies.local";
	private static final String CIS_IIDENTITY_STRING9 = "myCIS9.societies.local";
	private static final String CIS_IIDENTITY_STRING10 = "myCIS10.societies.local";
	private static final String CIS_IIDENTITY_STRING11 = "myCIS11.societies.local";
	private static final String CIS_IIDENTITY_STRING12 = "myCIS12.societies.local";
	private static final String CIS_IIDENTITY_STRING13 = "myCIS13.societies.local";
	private static final String CIS_IIDENTITY_STRING14 = "myCIS14.societies.local";
	private static final String CIS_IIDENTITY_COMMUNITY_PARENT = "myCISCommunityParent.societies.local";
	private static final String CIS_IIDENTITY_ENTITY_CHILD = "myCISEntityChild.societies.local";
	private static final String CIS_IIDENTITY_COMMUNITY_PARENT2 = "myCISCommunityParent2.societies.local";
	private static final String CIS_IIDENTITY_ENTITY_CHILD2 = "myCISEntityChild2.societies.local";

	@Autowired
	private ICommunityCtxDBMgr communityDB;

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
	public void testCreateCommunityEntity() throws CtxException {

		final CommunityCtxEntity entity = 
				this.communityDB.createCommunityEntity(CIS_IIDENTITY_STRING0);

		assertNotNull(entity);
		assertNotNull(entity.getId());
		assertEquals(CIS_IIDENTITY_STRING0, entity.getOwnerId());
		assertEquals(CtxModelType.ENTITY, entity.getModelType());
		assertEquals(CtxEntityTypes.COMMUNITY, entity.getType());
		assertNotNull(entity.getObjectNumber());
		assertNotNull(entity.getLastModified());
		assertNotNull(entity.getAttributes());
		assertTrue(entity.getAttributes().isEmpty());
//		assertNotNull(entity.getAssociations());
//		assertEquals(2, entity.getAssociations().size());
	}

	@Test
	public void testCreateCommunityAttribute() throws CtxException {

		final CommunityCtxEntity entity = 
				this.communityDB.createCommunityEntity(CIS_IIDENTITY_STRING1);
		final CtxAttribute attribute = 
				this.communityDB.createAttribute(entity.getId(), CtxAttributeTypes.NAME);

		assertNotNull(attribute);
		assertNotNull(attribute.getId());
		assertEquals(entity.getId(), attribute.getScope());
		assertEquals(CIS_IIDENTITY_STRING1, attribute.getOwnerId());
		assertEquals(CtxModelType.ATTRIBUTE, attribute.getModelType());
		assertEquals(CtxAttributeTypes.NAME, attribute.getType());
		assertNotNull(attribute.getLastModified());
		assertTrue(!attribute.isHistoryRecorded());
		assertEquals(CtxAttributeValueType.EMPTY, attribute.getValueType());
		assertNull(attribute.getStringValue());
		assertNull(attribute.getIntegerValue());
		assertNull(attribute.getDoubleValue());
		assertNull(attribute.getBinaryValue());
		assertNull(attribute.getValueMetric());
		assertNull(attribute.getSourceId());
		assertNotNull(attribute.getQuality());
		assertEquals(attribute, attribute.getQuality().getAttribute());
		// TODO assertEquals(attribute.getLastModified(), attribute.getQuality().getLastUpdated());
		assertEquals(CtxOriginType.MANUALLY_SET, attribute.getQuality().getOriginType());
		assertNull(attribute.getQuality().getPrecision());
		assertNull(attribute.getQuality().getUpdateFrequency());
	}

	@Test
	public void testCreateCtxEntity() throws CtxException {

		final CtxEntity entity = 
				this.communityDB.createEntity(CIS_IIDENTITY_STRING9, CtxEntityTypes.SERVICE);

		assertNotNull(entity);
		assertNotNull(entity.getId());
		assertEquals(CIS_IIDENTITY_STRING9, entity.getOwnerId());
		assertEquals(CtxModelType.ENTITY, entity.getModelType());
		assertEquals(CtxEntityTypes.SERVICE, entity.getType());
		assertNotNull(entity.getObjectNumber());
		assertNotNull(entity.getLastModified());
		assertNotNull(entity.getAttributes());
		assertTrue(entity.getAttributes().isEmpty());
		assertNotNull(entity.getAssociations());
		assertTrue(entity.getAssociations().isEmpty());
	}
	
	@Test
	public void testUpdateCommunityAttribute() throws CtxException {

		final CtxEntityIdentifier commEntityId = 
				this.communityDB.createCommunityEntity(CIS_IIDENTITY_STRING2).getId();
		CtxAttribute attribute = this.communityDB.createAttribute(
				commEntityId, CtxAttributeTypes.NAME);

		attribute.setStringValue("Jane Do Fans");
		attribute.setValueType(CtxAttributeValueType.STRING);
		Date lastUpdated = attribute.getQuality().getLastUpdated();
		// verify update
		attribute = (CtxAttribute) this.communityDB.update(attribute);
		assertEquals("Jane Do Fans", attribute.getStringValue());
		assertNull(attribute.getIntegerValue());
		assertNull(attribute.getDoubleValue());
		assertNull(attribute.getBinaryValue());
		assertEquals(CtxAttributeValueType.STRING, attribute.getValueType());
		assertNull(attribute.getValueMetric());
		assertNull(attribute.getSourceId());
		//problem with Mysql when testing
		//assertEquals(lastUpdated, attribute.getQuality().getLastUpdated());
		assertEquals(CtxOriginType.MANUALLY_SET, attribute.getQuality().getOriginType());
		assertNull(attribute.getQuality().getPrecision());
		assertNull(attribute.getQuality().getUpdateFrequency());
		
		CtxAttribute attribute2 = this.communityDB.createAttribute(
				commEntityId, CtxAttributeTypes.TEMPERATURE);
		attribute2.setHistoryRecorded(true);
		attribute2 = (CtxAttribute) this.communityDB.update(attribute2);
		assertTrue(attribute2.isHistoryRecorded());
		
		attribute2.setDoubleValue(25.5);
		attribute2.setValueType(CtxAttributeValueType.DOUBLE);
		attribute2.getQuality().setOriginType(CtxOriginType.SENSED);
		attribute2 = (CtxAttribute) this.communityDB.update(attribute2);
		assertEquals(CtxOriginType.SENSED, attribute2.getQuality().getOriginType());
		final Date lastModified1 = attribute2.getLastModified();
		final Date lastUpdated1 = attribute2.getQuality().getLastUpdated();
		
		attribute2.setDoubleValue(25.5);
		attribute2.setValueType(CtxAttributeValueType.DOUBLE);
		attribute2 = (CtxAttribute) this.communityDB.update(attribute2);
		final Date lastModified2 = attribute2.getLastModified();
		final Date lastUpdated2 = attribute2.getQuality().getLastUpdated();
		assertEquals(lastModified1, lastModified2);
		//problem with Mysql when testing
		//assertTrue(lastUpdated2.compareTo(lastUpdated1) > 0);
	}
	
	@Test
	public void testAssociations() throws CtxException {
		
		CtxAssociation association = this.communityDB.createAssociation(CIS_IIDENTITY_COMMUNITY_PARENT, CtxAssociationTypes.HAS_PARAMETERS);
		
		CommunityCtxEntity communityEntity = this.communityDB.createCommunityEntity(CIS_IIDENTITY_COMMUNITY_PARENT);
		CtxEntity entity = this.communityDB.createEntity(CIS_IIDENTITY_ENTITY_CHILD, CtxEntityTypes.PERSON);
		
		//set parent entity
		final CtxEntityIdentifier parentEntityId = communityEntity.getId();
		association.setParentEntity(parentEntityId);
		association = (CtxAssociation) this.communityDB.update(association);
		assertNotNull(association.getParentEntity());
		assertEquals(parentEntityId, association.getParentEntity());
		
		//check association from the entity's side
		communityEntity = (CommunityCtxEntity) this.communityDB.retrieve(parentEntityId);
		assertTrue(communityEntity.getAssociations(CtxAssociationTypes.HAS_PARAMETERS).contains(association.getId()));
		assertEquals(1, communityEntity.getAssociations(CtxAssociationTypes.HAS_PARAMETERS).size());
		
		//add child entity
		final CtxEntityIdentifier childEntityId = entity.getId();
		association.addChildEntity(childEntityId);
		association = (CtxAssociation) this.communityDB.update(association);
		assertEquals(communityEntity.getId(), association.getParentEntity());
		assertEquals(1, association.getChildEntities().size());
		assertTrue(association.getChildEntities().contains(childEntityId));
		
		//check association from the entity's side
		entity = (CtxEntity) this.communityDB.retrieve(childEntityId);
		assertTrue(entity.getAssociations(CtxAssociationTypes.HAS_PARAMETERS).contains(association.getId()));
		assertEquals(1, entity.getAssociations(CtxAssociationTypes.HAS_PARAMETERS).size());

		//add second child entity
		final CtxEntityIdentifier childEntityId2 = this.communityDB.createEntity(CIS_IIDENTITY_COMMUNITY_PARENT, CtxEntityTypes.PERSON).getId();
		association.addChildEntity(childEntityId2);
		association = (CtxAssociation) this.communityDB.update(association);
		assertEquals(communityEntity.getId(), association.getParentEntity());
		assertEquals(2, association.getChildEntities().size());
		assertTrue(association.getChildEntities().contains(childEntityId));
		assertTrue(association.getChildEntities().contains(childEntityId2));
		
		//check association from the entity's side
		entity = (CtxEntity) this.communityDB.retrieve(childEntityId2);
		assertTrue(entity.getAssociations(CtxAssociationTypes.HAS_PARAMETERS).contains(association.getId()));
		assertEquals(1, entity.getAssociations(CtxAssociationTypes.HAS_PARAMETERS).size());		
	}
	
	@Test
	public void testRetrieveCommunityEntity() throws CtxException {

		final CommunityCtxEntity entity = 
				this.communityDB.createCommunityEntity(CIS_IIDENTITY_STRING3);
		final CommunityCtxEntity entityFromDb = this.communityDB.retrieveCommunityEntity(CIS_IIDENTITY_STRING3);

		assertNotNull(entityFromDb);
		assertEquals(entity.getId(), entityFromDb.getId());
		assertEquals(entity.getOwnerId(), entityFromDb.getOwnerId());
		assertEquals(entity.getModelType(), entityFromDb.getModelType());
		assertEquals(entity.getType(), entityFromDb.getType());
		assertEquals(entity.getObjectNumber(), entityFromDb.getObjectNumber());
		assertEquals(entity.getLastModified(), entityFromDb.getLastModified());
		assertNotNull(entityFromDb.getAttributes());
		assertEquals(entity.getAttributes().size(), entityFromDb.getAttributes().size());
		assertNotNull(entityFromDb.getAssociations());
		assertEquals(entity.getAssociations().size(), entityFromDb.getAssociations().size());
	}
/*   
   @Test
   public void testRetrieve() throws CtxException {

	   entity = communityDB.createCommunityEntity(mockCisIdentity);
	   attribute = communityDB.createCommunityAttribute(entity.getId(), CtxAttributeTypes.NAME);

	   CommunityCtxEntity retrEntity;
	   retrEntity = (CommunityCtxEntity) communityDB.retrieve(entity.getId());
	   assertNotNull(retrEntity);
	   assertEquals(entity, retrEntity);
	   
	   CtxAttribute retrAttribute;
	   retrAttribute = (CtxAttribute) communityDB.retrieve(attribute.getId());
	   assertNotNull(retrAttribute);
	   assertEquals(attribute, retrAttribute);
	}

   @Test
   public void testLookup() throws CtxException{
	   System.out.println("---- testLookup");
	   
       List<CtxIdentifier> ids;
       
       // Create test entity.
       final CtxEntityIdentifier entId = communityDB.createCommunityEntity(mockCisIdentity).getId();
       final CtxEntityIdentifier entId2 = communityDB.createCommunityEntity(mockCisIdentity2).getId();
       
       // Create test attribute.
       final CtxAttributeIdentifier attrId = communityDB.createCommunityAttribute(entId, CtxAttributeTypes.NAME).getId();
       final CtxAttributeIdentifier attrId2 = communityDB.createCommunityAttribute(entId2, CtxAttributeTypes.NAME_LAST).getId();
       

       //
       // Lookup entities
       //
       ids = communityDB.lookup(CtxModelType.ENTITY, entId.getType());
       assertTrue(ids.contains(entId));
       assertEquals(entId.getOwnerId(),CIS_IIDENTITY_STRING);
       assertEquals(2, ids.size());
              
       ids = communityDB.lookup(CtxModelType.ENTITY, entId2.getType());
       assertTrue(ids.contains(entId2));
       assertEquals(entId2.getOwnerId(),CIS_IIDENTITY_STRING2);
       assertEquals(2, ids.size());

       //
       // Lookup attributes
       //
       
       ids = communityDB.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.NAME);
       assertTrue(ids.contains(attrId));
       assertEquals(1, ids.size());
       
       ids = communityDB.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.NAME_LAST);
       assertTrue(ids.contains(attrId2));
       assertEquals(1, ids.size());
	}*/
   
	
	@Test
	public void testLookupSetOfTypes() throws CtxException {
		   
		Set<CtxIdentifier> ids;
	    
		// Create test entity.   
		final CtxEntityIdentifier entId = communityDB.createEntity(CIS_IIDENTITY_STRING12, CtxEntityTypes.DEVICE).getId();
	    final CtxEntityIdentifier entId2 = communityDB.createEntity(CIS_IIDENTITY_STRING13, CtxEntityTypes.DEVICE).getId();
	    final CtxEntityIdentifier entId3 = communityDB.createEntity(CIS_IIDENTITY_STRING14, CtxEntityTypes.ORGANISATION).getId();
	       
	    // Create test attribute.
	    final CtxAttribute attribute = 
				this.communityDB.createAttribute(entId, CtxAttributeTypes.PHONES);
	    final CtxAttribute attribute2 = 
				this.communityDB.createAttribute(entId2, CtxAttributeTypes.PHONES);
	    final CtxAttribute attribute3 = 
				this.communityDB.createAttribute(entId3, CtxAttributeTypes.AFFILIATION);
	    
	    final Set<String> types = new HashSet<String>();
	    types.add(entId.getType());
	    types.add(entId3.getType());
	    //
	    // Lookup entities
	    //
	    ids = communityDB.lookup(CIS_IIDENTITY_STRING12, types);
	    assertTrue(ids.contains(entId));
	    assertEquals(entId.getOwnerId(),CIS_IIDENTITY_STRING12);
	    assertEquals(1, ids.size());
	              
	    assertTrue(!ids.contains(entId2));
	    assertTrue(!ids.contains(entId3));
	    assertEquals(entId2.getOwnerId(),CIS_IIDENTITY_STRING13);

	    final Set<String> types2 = new HashSet<String>();
	    types2.add(attribute.getType());
	    types2.add(attribute3.getType());
	    //
	    // Lookup attributes
	    //
	    ids = communityDB.lookup(attribute.getOwnerId(), types2);
	    assertTrue(ids.contains(attribute.getId()));
	    assertEquals(1, ids.size());
	              
	    assertTrue(!ids.contains(attribute2.getId()));
	    assertTrue(!ids.contains(attribute3.getId()));

	    //
	    // Lookup entities using modelType and type
	    //
	    final Set<String> types3 = new HashSet<String>();
	    types3.add(entId3.getType());
	    ids = communityDB.lookup(CIS_IIDENTITY_STRING14, CtxModelType.ENTITY, types3);
	    assertTrue(ids.contains(entId3));
	    assertEquals(1, ids.size());
	    
	    //
	    // Lookup attributes using modelType and type
	    //
	    ids = communityDB.lookup(attribute.getOwnerId(), CtxModelType.ATTRIBUTE, types2);
	    assertTrue(ids.contains(attribute.getId()));
	    assertTrue(!ids.contains(attribute2.getId()));
	    assertTrue(!ids.contains(attribute3.getId()));
	    assertEquals(1, ids.size());
	    
	    //
	    // Lookup attributes using entityId and type
	    //
	    ids = communityDB.lookup(entId, CtxModelType.ATTRIBUTE, types2);
	    assertTrue(ids.contains(attribute.getId()));
	    assertEquals(1, ids.size());
	    
	    //ASSOCIATIONS
		CtxAssociation association = this.communityDB.createAssociation(CIS_IIDENTITY_COMMUNITY_PARENT2, CtxAssociationTypes.HAS_PARAMETERS);
		
		CommunityCtxEntity communityEntity = this.communityDB.createCommunityEntity(CIS_IIDENTITY_COMMUNITY_PARENT2);
		CtxEntity entity = this.communityDB.createEntity(CIS_IIDENTITY_ENTITY_CHILD2, CtxEntityTypes.PERSON);
		
		//set parent entity
		final CtxEntityIdentifier parentEntityId = communityEntity.getId();
		association.setParentEntity(parentEntityId);
		association = (CtxAssociation) this.communityDB.update(association);
		assertNotNull(association.getParentEntity());
		assertEquals(parentEntityId, association.getParentEntity());
		
		//add child entity
		final CtxEntityIdentifier childEntityId = entity.getId();
		association.addChildEntity(childEntityId);
		association = (CtxAssociation) this.communityDB.update(association);
		assertEquals(communityEntity.getId(), association.getParentEntity());
		assertEquals(1, association.getChildEntities().size());
		assertTrue(association.getChildEntities().contains(childEntityId));
		
	    // 
	    // Lookup associations using entityId and type
	    //
		// using the child entityId
	    final Set<String> types4 = new HashSet<String>();
	    types4.add(association.getType());
	    ids = communityDB.lookup(childEntityId, CtxModelType.ASSOCIATION, types4);
	    assertTrue(ids.contains(association.getId()));
	    assertEquals(1, ids.size());
	    // using the parent entityId
	    ids = communityDB.lookup(parentEntityId, CtxModelType.ASSOCIATION, types4);
	    assertTrue(ids.contains(association.getId()));
	    assertEquals(1, ids.size());
	    
	    //
	    // Lookup associations using ownerId and type
	    //
	    ids = communityDB.lookup(association.getOwnerId(), types4);
	    assertTrue(ids.contains(association.getId()));
	    //
	    // Lookup associations using ownerId, modelType and type
	    //
	    ids = communityDB.lookup(association.getOwnerId(), CtxModelType.ASSOCIATION, types4);
	    assertTrue(ids.contains(association.getId()));
	    
   }
	
   @Test
   public void testLookupCommunityCtxEntity() throws CtxException {

	   List<CtxIdentifier> ids;
	   CommunityCtxEntity entity = this.communityDB.createCommunityEntity(CIS_IIDENTITY_STRING10);
	   final CtxAttribute attribute = this.communityDB.createAttribute(entity.getId(), CtxAttributeTypes.ADDRESS_HOME_STREET_NAME);
	   final CtxAttribute attribute2 = this.communityDB.createAttribute(entity.getId(), CtxAttributeTypes.ADDRESS_HOME_STREET_NUMBER);
	   
	   CommunityCtxEntity entity2 = this.communityDB.createCommunityEntity(CIS_IIDENTITY_STRING11);
	   final CtxAttribute attribute3 = this.communityDB.createAttribute(entity2.getId(), CtxAttributeTypes.ADDRESS_HOME_STREET_NAME);
	   
	   ids = communityDB.lookupCommunityCtxEntity(CtxAttributeTypes.ADDRESS_HOME_STREET_NAME);
	   assertTrue(ids.contains(entity.getId()));
	   assertTrue(ids.contains(entity2.getId()));
	   assertEquals(2, ids.size());
	   
	   ids = communityDB.lookupCommunityCtxEntity(CtxAttributeTypes.ADDRESS_HOME_STREET_NUMBER);
	   assertTrue(ids.contains(entity.getId()));
	   assertEquals(1, ids.size());

   }
   
   @Test
   public void testCommunityHierarchies() throws CtxException{

	   CtxAssociation association;
	   
	   CommunityCtxEntity entity = 
			   this.communityDB.createCommunityEntity(CIS_IIDENTITY_STRING4);
	   assertTrue(entity.getCommunities().isEmpty());
	   assertTrue(entity.getMembers().isEmpty());

	   // Setup (parent) Super-community
	   final CtxEntityIdentifier parentEntityId = 
			   this.communityDB.createCommunityEntity(CIS_IIDENTITY_STRING5).getId();
	   association = (CtxAssociation) this.communityDB.retrieve(
			   entity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).iterator().next());
	   association.addChildEntity(parentEntityId);
	   association = (CtxAssociation) this.communityDB.update(association);
	   assertEquals(1, association.getChildEntities().size());
	   assertTrue(association.getChildEntities().contains(parentEntityId));
	   // check association from the entity's side
	   entity = (CommunityCtxEntity) this.communityDB.retrieve(entity.getId());
	   assertTrue(entity.getCommunities().contains(parentEntityId));

	   // Setup another Super-community
	   final CtxEntityIdentifier parentEntityId2 = 
			   this.communityDB.createCommunityEntity(CIS_IIDENTITY_STRING6).getId();
	   association.addChildEntity(parentEntityId2);
	   association = (CtxAssociation) this.communityDB.update(association);
	   assertEquals(2, association.getChildEntities().size());
	   assertTrue(association.getChildEntities().contains(parentEntityId2));
	   // check association from the entity's side
	   entity = (CommunityCtxEntity) this.communityDB.retrieve(entity.getId());
	   assertEquals(2, entity.getCommunities().size());
	   assertTrue(entity.getCommunities().contains(parentEntityId));
	   assertTrue(entity.getCommunities().contains(parentEntityId2));

	   // Add (child) sub-communities
	   final CtxEntityIdentifier childEntityId = 
			   this.communityDB.createCommunityEntity(CIS_IIDENTITY_STRING7).getId();
	   association = (CtxAssociation) this.communityDB.retrieve(
			   entity.getAssociations(CtxAssociationTypes.HAS_MEMBERS).iterator().next());
	   association.addChildEntity(childEntityId);
	   association = (CtxAssociation) this.communityDB.update(association);
	   assertEquals(1, association.getChildEntities().size());
	   assertTrue(association.getChildEntities().contains(childEntityId));
	   // check association from the entity's side
	   entity = (CommunityCtxEntity) this.communityDB.retrieve(entity.getId());
	   assertEquals(1, entity.getMembers().size());
	   assertTrue(entity.getMembers().contains(childEntityId));
	   
	   final CtxEntityIdentifier childEntityId2 = 
			   this.communityDB.createCommunityEntity(CIS_IIDENTITY_STRING8).getId();
	   association.addChildEntity(childEntityId2);
	   association = (CtxAssociation) this.communityDB.update(association);
	   assertEquals(2, association.getChildEntities().size());
	   assertTrue(association.getChildEntities().contains(childEntityId));
	   assertTrue(association.getChildEntities().contains(childEntityId2));
	   // check association from the entity's side
	   entity = (CommunityCtxEntity) this.communityDB.retrieve(entity.getId());
	   assertEquals(2, entity.getMembers().size());
	   assertTrue(entity.getMembers().contains(childEntityId));
	   assertTrue(entity.getMembers().contains(childEntityId2));
   }
}