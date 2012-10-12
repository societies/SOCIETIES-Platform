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

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CommunityMemberCtxEntity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.model.CtxAttributeTypes;
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

	private static final String CIS_IIDENTITY_STRING = "myCIS.societies.local";
	private static final String CIS_IIDENTITY_STRING2 = "myCIS2.societies.local";
	private static final String CIS_IIDENTITY_STRING3 = "myCIS3.societies.local";

	@Autowired
	private ICommunityCtxDBMgr communityDB;

	private static IIdentity mockCisIdentity = mock(IIdentity.class);
	private static IIdentity mockCisIdentity2 = mock(IIdentity.class);
	private static IIdentity mockCisIdentity3 = mock(IIdentity.class);
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		when(mockCisIdentity.toString()).thenReturn(CIS_IIDENTITY_STRING);
		when(mockCisIdentity2.toString()).thenReturn(CIS_IIDENTITY_STRING2);
		when(mockCisIdentity3.toString()).thenReturn(CIS_IIDENTITY_STRING3);
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
				this.communityDB.createCommunityEntity(mockCisIdentity);
		
		assertNotNull(entity);
		assertNotNull(entity.getId());
		assertEquals(mockCisIdentity.toString(), entity.getOwnerId());
		assertEquals(CtxModelType.ENTITY, entity.getModelType());
		assertEquals(CtxEntityTypes.COMMUNITY, entity.getType());
		assertNotNull(entity.getObjectNumber());
		assertNotNull(entity.getLastModified());
		assertNotNull(entity.getAttributes());
		assertTrue(entity.getAttributes().isEmpty());
		assertNotNull(entity.getAssociations());
		assertTrue(entity.getAssociations().isEmpty());
	}
	
	@Test
 	public void testCreateCommunityAttribute() throws CtxException {

		final CommunityCtxEntity entity = 
				this.communityDB.createCommunityEntity(mockCisIdentity2);
		final CtxAttribute attribute = 
				this.communityDB.createCommunityAttribute(entity.getId(), CtxAttributeTypes.NAME);
		
		assertNotNull(attribute);
		assertNotNull(attribute.getId());
		assertEquals(entity.getId(), attribute.getScope());
		assertEquals(mockCisIdentity2.toString(), attribute.getOwnerId());
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
/*	
   @Test
   public void testUpdateEntity() throws CtxException{
	   System.out.println("---- testUpdateEntity");

	   entity = communityDB.createCommunityEntity(mockCisIdentity);
	   System.out.println("entities attributes " + entity.getAttributes());
	   System.out.println("entities members " + entity.getMembers());

	   // Add CommunityMemberCtxEntity
	   member = communityDB.createCommunityEntity(mockCisIdentity);
	   entity.addMember(member.getId());
	   
	   communityDB.updateCommunityEntity(entity);
	   System.out.println("updated entities members " + entity.getMembers());
	   
	   // Add Attribute
	   attribute = communityDB.createCommunityAttribute(entity.getId(), "name");
	   entity.addAttribute(attribute);
	   communityDB.updateCommunityEntity(entity);
	   System.out.println("updated entities attributes " + entity.getAttributes());

	   // Retrieve CommunityCtxEntity
	   modObj = communityDB.retrieve(entity.getId());
	   CommunityCtxEntity retrEntity = (CommunityCtxEntity) modObj;
	   System.out.println("retrieved entity - " + retrEntity);
	   System.out.println("retrieved attributes - " + retrEntity.getAttributes());
	   System.out.println("retrieves members - " + retrEntity.getMembers());
	   System.out.println("retrieve entity another way - " + communityDB.retrieveCommunityEntity(mockCisIdentity));
	   
	   assertNotNull(entity);
	   assertNotNull(retrEntity);
	   assertEquals(entity.getAttributes(), retrEntity.getAttributes());
	   assertEquals(entity.getMembers(), retrEntity.getMembers());
	}
   
   @Test
   public void testUpdateAttribute() throws CtxException{
	   System.out.println("---- testUpdateAttribute");

	   entity = communityDB.createCommunityEntity(mockCisIdentity);
	   System.out.println("entities attributes " + entity.getAttributes());
	   System.out.println("entities members " + entity.getMembers());

	   // Add CommunityMemberCtxEntity
	   member = communityDB.createCommunityEntity(mockCisIdentity);
	   entity.addMember(member.getId());
	   
	   communityDB.updateCommunityEntity(entity);
	   System.out.println("updated entities members " + entity.getMembers());
	   
	   // Add Attribute
	   attribute = communityDB.createCommunityAttribute(entity.getId(), "name");
	   entity.addAttribute(attribute);
	   communityDB.updateCommunityEntity(entity);
	   System.out.println("updated entities attributes " + entity.getAttributes());
	   
	   // Retrieve CommunityCtxEntity
	   modObj = communityDB.retrieve(entity.getId());
	   CommunityCtxEntity retrEntity = (CommunityCtxEntity) modObj;
	   System.out.println("retrieved entity - " + retrEntity);
	   System.out.println("retrieved attributes - " + retrEntity.getAttributes());
	   System.out.println("retrieves members - " + retrEntity.getMembers());
	   System.out.println("retrieve entity another way - " + communityDB.retrieveCommunityEntity(mockCisIdentity));
	   
	   // Update attribute
	   attribute.setIntegerValue(5);
	   communityDB.updateCommunityAttribute(attribute);
	   System.out.println("updated attribute");
	   
	   modObj = communityDB.retrieve(attribute.getId());
	   attribute = (CtxAttribute) modObj;

	   System.out.println("attribute value should be 5 and it is:"+attribute.getIntegerValue());

	   assertNotNull(attribute);
	   
	   assertNotNull(entity);
	   assertNotNull(retrEntity);
	   assertEquals(entity.getAttributes(), retrEntity.getAttributes());
	   assertEquals(entity.getMembers(), retrEntity.getMembers());
	}
*/
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
}