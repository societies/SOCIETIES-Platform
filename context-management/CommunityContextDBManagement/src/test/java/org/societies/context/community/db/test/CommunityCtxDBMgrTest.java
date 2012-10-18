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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.identity.IIdentity;
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

	@Autowired
	private ICommunityCtxDBMgr communityDB;

	private static IIdentity mockCisIdentity0 = mock(IIdentity.class);
	private static IIdentity mockCisIdentity1 = mock(IIdentity.class);
	private static IIdentity mockCisIdentity2 = mock(IIdentity.class);
	private static IIdentity mockCisIdentity3 = mock(IIdentity.class);
	private static IIdentity mockCisIdentity4 = mock(IIdentity.class);
	private static IIdentity mockCisIdentity5 = mock(IIdentity.class);
	private static IIdentity mockCisIdentity6 = mock(IIdentity.class);
	private static IIdentity mockCisIdentity7 = mock(IIdentity.class);
	private static IIdentity mockCisIdentity8 = mock(IIdentity.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		when(mockCisIdentity0.toString()).thenReturn(CIS_IIDENTITY_STRING0);
		when(mockCisIdentity1.toString()).thenReturn(CIS_IIDENTITY_STRING1);
		when(mockCisIdentity2.toString()).thenReturn(CIS_IIDENTITY_STRING2);
		when(mockCisIdentity3.toString()).thenReturn(CIS_IIDENTITY_STRING3);
		when(mockCisIdentity4.toString()).thenReturn(CIS_IIDENTITY_STRING4);
		when(mockCisIdentity5.toString()).thenReturn(CIS_IIDENTITY_STRING5);
		when(mockCisIdentity6.toString()).thenReturn(CIS_IIDENTITY_STRING6);
		when(mockCisIdentity7.toString()).thenReturn(CIS_IIDENTITY_STRING7);
		when(mockCisIdentity8.toString()).thenReturn(CIS_IIDENTITY_STRING8);
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
				this.communityDB.createCommunityEntity(mockCisIdentity0);

		assertNotNull(entity);
		assertNotNull(entity.getId());
		assertEquals(mockCisIdentity0.toString(), entity.getOwnerId());
		assertEquals(CtxModelType.ENTITY, entity.getModelType());
		assertEquals(CtxEntityTypes.COMMUNITY, entity.getType());
		assertNotNull(entity.getObjectNumber());
		assertNotNull(entity.getLastModified());
		assertNotNull(entity.getAttributes());
		assertTrue(entity.getAttributes().isEmpty());
		assertNotNull(entity.getAssociations());
		assertEquals(2, entity.getAssociations().size());
	}

	@Test
	public void testCreateCommunityAttribute() throws CtxException {

		final CommunityCtxEntity entity = 
				this.communityDB.createCommunityEntity(mockCisIdentity1);
		final CtxAttribute attribute = 
				this.communityDB.createCommunityAttribute(entity.getId(), CtxAttributeTypes.NAME);

		assertNotNull(attribute);
		assertNotNull(attribute.getId());
		assertEquals(entity.getId(), attribute.getScope());
		assertEquals(mockCisIdentity1.toString(), attribute.getOwnerId());
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
		assertNull(attribute.getQuality().getOriginType());
		assertNull(attribute.getQuality().getPrecision());
		assertNull(attribute.getQuality().getUpdateFrequency());
	}

	@Test
	public void testUpdateCommunityAttribute() throws CtxException {

		final CtxEntityIdentifier commEntityId = 
				this.communityDB.createCommunityEntity(mockCisIdentity2).getId();
		CtxAttribute attribute = this.communityDB.createCommunityAttribute(
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
		assertNull(attribute.getQuality().getOriginType());
		assertNull(attribute.getQuality().getPrecision());
		assertNull(attribute.getQuality().getUpdateFrequency());
		
		CtxAttribute attribute2 = this.communityDB.createCommunityAttribute(
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
	public void testRetrieveCommunityEntity() throws CtxException {

		final CommunityCtxEntity entity = 
				this.communityDB.createCommunityEntity(mockCisIdentity3);
		final CommunityCtxEntity entityFromDb = this.communityDB.retrieveCommunityEntity(mockCisIdentity3);

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
   public void testCommunityHierarchies() throws CtxException{

	   CtxAssociation association;
	   
	   CommunityCtxEntity entity = 
			   this.communityDB.createCommunityEntity(mockCisIdentity4);
	   assertTrue(entity.getCommunities().isEmpty());
	   assertTrue(entity.getMembers().isEmpty());

	   // Setup (parent) Super-community
	   final CtxEntityIdentifier parentEntityId = 
			   this.communityDB.createCommunityEntity(mockCisIdentity5).getId();
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
			   this.communityDB.createCommunityEntity(mockCisIdentity6).getId();
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
			   this.communityDB.createCommunityEntity(mockCisIdentity7).getId();
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
			   this.communityDB.createCommunityEntity(mockCisIdentity8).getId();
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