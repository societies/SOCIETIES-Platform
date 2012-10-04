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
package org.societies.context.user.db.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.context.api.user.db.IUserCtxDBMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/test-context.xml"})
public class UserCtxDBMgrTest {

	public static final String CSS_ID = "jane.societies.local";
    
    @Autowired
	private IUserCtxDBMgr userDB;

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
	public void testCreateEntity() throws CtxException {

		final CtxEntity entity = this.userDB.createEntity(CtxEntityTypes.DEVICE);

		assertNotNull(entity);
		assertNotNull(entity.getId());
		assertEquals(CtxModelType.ENTITY, entity.getModelType());
		assertEquals(CtxEntityTypes.DEVICE, entity.getType());
		assertNotNull(entity.getObjectNumber());
		assertNotNull(entity.getLastModified());
		assertNotNull(entity.getAttributes());
		assertTrue(entity.getAttributes().isEmpty());
		assertNotNull(entity.getAssociations());
		assertTrue(entity.getAssociations().isEmpty());
	}

	@Test
	public void testCreateIndividualEntity() throws CtxException {

		final IndividualCtxEntity indEntity = this.userDB.createIndividualCtxEntity(CtxEntityTypes.PERSON);

		assertNotNull(indEntity);
		assertNotNull(indEntity.getId());
		assertEquals(CtxModelType.ENTITY, indEntity.getModelType());
		assertEquals(CtxEntityTypes.PERSON, indEntity.getType());
		assertNotNull(indEntity.getObjectNumber());
		assertNotNull(indEntity.getLastModified());
		assertNotNull(indEntity.getAttributes());
		assertTrue(indEntity.getAttributes().isEmpty());
		assertNotNull(indEntity.getAssociations());
		assertTrue(indEntity.getAssociations().isEmpty());
		assertNotNull(indEntity.getCommunities());
		assertTrue(indEntity.getCommunities().isEmpty());
	}

	@Test
	public void testCreateAssociation() throws CtxException{

		final CtxAssociation association = this.userDB.createAssociation(CtxAssociationTypes.USES_DEVICES);

		assertNotNull(association);
		assertNotNull(association.getId());
		assertEquals(CtxModelType.ASSOCIATION, association.getModelType());
		assertEquals(CtxAssociationTypes.USES_DEVICES, association.getType());
		assertNotNull(association.getLastModified());
		assertNull(association.getParentEntity());
		assertNotNull(association.getChildEntities());
		assertTrue(association.getChildEntities().isEmpty());
	}

	@Test
	public void testCreateAttribute() throws CtxException{

		final CtxEntity entity = this.userDB.createEntity(CtxEntityTypes.DEVICE);
		final CtxAttribute attribute = this.userDB.createAttribute(
				entity.getId(), CtxAttributeTypes.NAME);

		assertNotNull(attribute);
		assertNotNull(attribute.getId());
		assertEquals(entity.getId(), attribute.getScope());
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
	public void testRetrieveEntity() throws CtxException {

		final CtxEntity entity = this.userDB.createEntity(CtxEntityTypes.DEVICE);
		final CtxEntity entityFromDb = (CtxEntity) this.userDB.retrieve(entity.getId());

		assertNotNull(entityFromDb);
		assertEquals(entity.getId(), entityFromDb.getId());
		assertEquals(entity.getModelType(), entityFromDb.getModelType());
		assertEquals(entity.getType(), entityFromDb.getType());
		assertEquals(entity.getObjectNumber(), entityFromDb.getObjectNumber());
		assertEquals(entity.getLastModified(), entityFromDb.getLastModified());
		assertNotNull(entityFromDb.getAttributes());
		assertEquals(entity.getAttributes().size(), entityFromDb.getAttributes().size());
		assertNotNull(entityFromDb.getAssociations());
		assertEquals(entity.getAssociations().size(), entityFromDb.getAssociations().size());
	}

	@Test
	public void testUpdateAssociation() throws CtxException{

		CtxAssociation association = this.userDB.createAssociation(CtxAssociationTypes.USES_DEVICES);
		CtxEntity entity;

		// Set Parent Entity
		final CtxEntityIdentifier parentEntityId = this.userDB.createEntity(CtxEntityTypes.PERSON).getId();
		association.setParentEntity(parentEntityId);
		association = (CtxAssociation) this.userDB.update(association);
		assertNotNull(association.getParentEntity());
		assertEquals(parentEntityId, association.getParentEntity());
		// check association from the entity's side
		entity = (CtxEntity) this.userDB.retrieve(parentEntityId);
		assertTrue(entity.getAssociations().contains(association.getId()));
		assertEquals(1, entity.getAssociations().size());

		// Set another Parent Entity (Individual)
		final CtxEntityIdentifier parentEntityId2 = this.userDB.createIndividualCtxEntity(CtxEntityTypes.PERSON).getId();
		association.setParentEntity(parentEntityId2);
		association = (CtxAssociation) this.userDB.update(association);
		assertNotNull(association.getParentEntity());
		assertEquals(parentEntityId2, association.getParentEntity());
		// check association from the entity's side
		entity = (CtxEntity) this.userDB.retrieve(parentEntityId);
		assertTrue(entity.getAssociations().isEmpty());
		entity = (CtxEntity) this.userDB.retrieve(parentEntityId2);
		assertTrue(entity.getAssociations().contains(association.getId()));
		assertEquals(1, entity.getAssociations().size());

		// Add Child Entities
		final CtxEntityIdentifier childEntityId = userDB.createEntity(CtxEntityTypes.DEVICE).getId();
		association.addChildEntity(childEntityId);
		association = (CtxAssociation) this.userDB.update(association);
		assertEquals(parentEntityId2, association.getParentEntity());
		assertEquals(1, association.getChildEntities().size());
		assertTrue(association.getChildEntities().contains(childEntityId));
		// check association from the entity's side
		entity = (CtxEntity) this.userDB.retrieve(childEntityId);
		assertTrue(entity.getAssociations().contains(association.getId()));
		assertEquals(1, entity.getAssociations().size());

		final CtxEntityIdentifier childEntityId2 = userDB.createEntity(CtxEntityTypes.DEVICE).getId();
		association.addChildEntity(childEntityId2);
		association = (CtxAssociation) this.userDB.update(association);
		assertEquals(parentEntityId2, association.getParentEntity());
		assertEquals(2, association.getChildEntities().size());
		assertTrue(association.getChildEntities().contains(childEntityId));
		assertTrue(association.getChildEntities().contains(childEntityId2));
		// check association from the entity's side
		entity = (CtxEntity) this.userDB.retrieve(childEntityId2);
		assertTrue(entity.getAssociations().contains(association.getId()));
		assertEquals(1, entity.getAssociations().size());

		// Remove Parent Entity
		association.setParentEntity(null);
		association = (CtxAssociation) this.userDB.update(association);
		assertEquals(null, association.getParentEntity());
		// check association from the entity's side
		entity = (CtxEntity) this.userDB.retrieve(parentEntityId2);
		assertTrue(entity.getAssociations().isEmpty());

		// Remove Child Entities
		association.removeChildEntity(childEntityId);
		association.removeChildEntity(childEntityId2);
		association = (CtxAssociation) this.userDB.update(association);
		assertTrue(association.getChildEntities().isEmpty());
		// check association from the entity's side
		entity = (CtxEntity) this.userDB.retrieve(childEntityId);
		assertTrue(entity.getAssociations().isEmpty());
		entity = (CtxEntity) this.userDB.retrieve(childEntityId2);
		assertTrue(entity.getAssociations().isEmpty());
	}

	@Ignore
	@Test
	public void testRetrieveAssociation() throws CtxException{
		System.out.println("---- testRetrieveAssociation");
		////////////////
		CtxAssociation association = userDB.createAssociation("IsRelatedWith");


		association = (CtxAssociation) userDB.retrieve(association.getId());

		assertNotNull(association);
	}

	@Test
	public void testUpdateAttribute() throws CtxException{

		final CtxEntityIdentifier indEntityId = this.userDB.createIndividualCtxEntity(CtxEntityTypes.PERSON).getId();
		CtxAttribute attribute = this.userDB.createAttribute(indEntityId, CtxAttributeTypes.NAME);

		attribute.setStringValue("Jane Do");
		attribute.setValueType(CtxAttributeValueType.STRING);
		Date lastUpdated = attribute.getQuality().getLastUpdated();
		// verify update
		attribute = (CtxAttribute) this.userDB.update(attribute);
		assertEquals("Jane Do", attribute.getStringValue());
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
		
		CtxAttribute attribute2 = this.userDB.createAttribute(indEntityId, CtxAttributeTypes.TEMPERATURE);
		attribute2.setHistoryRecorded(true);
		attribute2 = (CtxAttribute) this.userDB.update(attribute2);
		assertTrue(attribute2.isHistoryRecorded());
		
		attribute2.setDoubleValue(25.5);
		attribute2.setValueType(CtxAttributeValueType.DOUBLE);
		attribute2.getQuality().setOriginType(CtxOriginType.SENSED);
		attribute2 = (CtxAttribute) this.userDB.update(attribute2);
		assertEquals(CtxOriginType.SENSED, attribute2.getQuality().getOriginType());
		final Date lastModified1 = attribute2.getLastModified();
		final Date lastUpdated1 = attribute2.getQuality().getLastUpdated();
		
		attribute2.setDoubleValue(25.5);
		attribute2.setValueType(CtxAttributeValueType.DOUBLE);
		attribute2 = (CtxAttribute) this.userDB.update(attribute2);
		final Date lastModified2 = attribute2.getLastModified();
		final Date lastUpdated2 = attribute2.getQuality().getLastUpdated();
		assertEquals(lastModified1, lastModified2);
		//problem with Mysql when testing
		//assertTrue(lastUpdated2.compareTo(lastUpdated1) > 0);
	}
	
	@Test
	public void testRemoveEntity() throws CtxException {
		
		final CtxEntity entity = this.userDB.createEntity(CtxEntityTypes.DEVICE);
		final CtxEntity removedEntity = (CtxEntity) this.userDB.remove(entity.getId());
		assertNotNull(removedEntity);
		assertEquals(entity, removedEntity);
		assertNull(this.userDB.retrieve(removedEntity.getId()));;
		assertNull(this.userDB.remove(entity.getId()));
	}
	
	@Test
	public void testRemoveIndividualEntity() throws CtxException {
		
		final IndividualCtxEntity entity = this.userDB.createIndividualCtxEntity(CtxEntityTypes.PERSON);
		final IndividualCtxEntity removedEntity = (IndividualCtxEntity) this.userDB.remove(entity.getId());
		assertNotNull(removedEntity);
		assertEquals(entity, removedEntity);
		assertNull(this.userDB.retrieve(removedEntity.getId()));;
		assertNull(this.userDB.remove(entity.getId()));
	}
	
	@Test
	public void testRemoveAttribute() throws CtxException {
		
		final IndividualCtxEntity entity = this.userDB.createIndividualCtxEntity(CtxEntityTypes.PERSON);
		final CtxAttribute attribute1 = this.userDB.createAttribute(entity.getId(), CtxAttributeTypes.NAME);
		final CtxAttribute attribute2 = this.userDB.createAttribute(entity.getId(), CtxAttributeTypes.BIRTHDAY);
		final CtxAttribute attribute3 = this.userDB.createAttribute(entity.getId(), CtxAttributeTypes.ABOUT);
		
		final CtxAttribute removedAttribute2 = (CtxAttribute) this.userDB.remove(attribute2.getId());
		assertNotNull(removedAttribute2);
		assertEquals(attribute2, removedAttribute2);
		assertNull(this.userDB.remove(attribute2.getId()));
		
		final IndividualCtxEntity retrievedEntity = (IndividualCtxEntity) this.userDB.retrieve(entity.getId());
		assertTrue(!retrievedEntity.getAttributes().contains(attribute2));
		
		this.userDB.remove(entity.getId());
		assertNull(this.userDB.retrieve(attribute1.getId()));
		assertNull(this.userDB.retrieve(attribute3.getId()));
	}

	@Test
	public void testLookupByEntityType() throws CtxException{
	
		List<CtxIdentifier> ids;
       
		// Create test entities.
		final CtxEntityIdentifier entId1 = this.userDB.createIndividualCtxEntity(CtxEntityTypes.PERSON).getId();
		final CtxEntityIdentifier entId2 = this.userDB.createEntity(CtxEntityTypes.DEVICE).getId();
		final CtxEntityIdentifier entId3 = this.userDB.createEntity(CtxEntityTypes.DEVICE).getId();
   
		// Lookup entities
		ids = this.userDB.lookup(CtxModelType.ENTITY, "foo");
		assertNotNull(ids);
		assertTrue(ids.isEmpty());
		
		ids = this.userDB.lookup(CtxModelType.ENTITY, CtxEntityTypes.PERSON);
		assertNotNull(ids);
		assertTrue(ids.contains(entId1));
              
		ids = this.userDB.lookup(CtxModelType.ENTITY, CtxEntityTypes.DEVICE);
		assertTrue(ids.contains(entId2));
		assertTrue(ids.contains(entId3));
	}
	
	@Test
	public void testLookupByAttributeType() throws CtxException{
	
		List<CtxIdentifier> ids;
       
		// Create test entities.
		final CtxEntityIdentifier entId1 = this.userDB.createIndividualCtxEntity(CtxEntityTypes.PERSON).getId();
		final CtxEntityIdentifier entId2 = this.userDB.createEntity(CtxEntityTypes.DEVICE).getId();
		final CtxEntityIdentifier entId3 = this.userDB.createEntity(CtxEntityTypes.DEVICE).getId();
      
		// Create test attributes.
		final CtxAttributeIdentifier attrId1 = userDB.createAttribute(entId1, CtxAttributeTypes.NAME).getId();
		final CtxAttributeIdentifier attrId2 = userDB.createAttribute(entId1, CtxAttributeTypes.TEMPERATURE).getId();
		final CtxAttributeIdentifier attrId3 = userDB.createAttribute(entId2, CtxAttributeTypes.ID).getId();
		final CtxAttributeIdentifier attrId4 = userDB.createAttribute(entId2, CtxAttributeTypes.TEMPERATURE).getId();
		final CtxAttributeIdentifier attrId5 = userDB.createAttribute(entId3, CtxAttributeTypes.ID).getId();
		final CtxAttributeIdentifier attrId6 = userDB.createAttribute(entId3, CtxAttributeTypes.TEMPERATURE).getId();

		// Lookup attributes
		ids = this.userDB.lookup(CtxModelType.ATTRIBUTE, "foo");
		assertNotNull(ids);
		assertTrue(ids.isEmpty());
		
		ids = this.userDB.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.NAME);
		assertNotNull(ids);
		assertTrue(ids.contains(attrId1));
              
		ids = this.userDB.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.ID);
		assertTrue(ids.contains(attrId3));
		assertTrue(ids.contains(attrId5));
		
		ids = this.userDB.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.TEMPERATURE);
		assertTrue(ids.contains(attrId2));
		assertTrue(ids.contains(attrId4));
		assertTrue(ids.contains(attrId6));
	}

	@Test
	public void testLookupByAssociationType() throws CtxException{
	
		List<CtxIdentifier> ids;
       
		// Create test associations
		final CtxAssociationIdentifier assocId1 = 
				this.userDB.createAssociation(CtxAssociationTypes.USES_DEVICES).getId();
		final CtxAssociationIdentifier assocId2 = 
				this.userDB.createAssociation(CtxAssociationTypes.USES_SERVICES).getId();
		final CtxAssociationIdentifier assocId3 = 
				this.userDB.createAssociation(CtxAssociationTypes.USES_SERVICES).getId();

		// Lookup associations
		ids = this.userDB.lookup(CtxModelType.ASSOCIATION, "foo");
		assertNotNull(ids);
		assertTrue(ids.isEmpty());
		
		ids = this.userDB.lookup(CtxModelType.ASSOCIATION, CtxAssociationTypes.USES_DEVICES);
		assertNotNull(ids);
		assertTrue(ids.contains(assocId1));
              
		ids = this.userDB.lookup(CtxModelType.ASSOCIATION, CtxAssociationTypes.USES_SERVICES);
		assertTrue(ids.contains(assocId2));
		assertTrue(ids.contains(assocId3));
	}
	
	@Test
	public void testLookupEntitiesByAttrType() throws CtxException {
		
		List<CtxEntityIdentifier> ids;

		final CtxEntityIdentifier entId1 = this.userDB.createIndividualCtxEntity(CtxEntityTypes.PERSON).getId();
		CtxAttribute attr1 = this.userDB.createAttribute(entId1, CtxAttributeTypes.NAME);
		final CtxEntityIdentifier entId2 = this.userDB.createEntity(CtxEntityTypes.PERSON).getId();
		CtxAttribute attr2 = this.userDB.createAttribute(entId2, CtxAttributeTypes.NAME);
		final CtxEntityIdentifier entId3 = this.userDB.createEntity(CtxEntityTypes.DEVICE).getId();
		CtxAttribute attr3 = this.userDB.createAttribute(entId3, CtxAttributeTypes.NAME);
       
		ids = this.userDB.lookupEntities(CtxEntityTypes.PERSON, CtxAttributeTypes.NAME, "bar", "foo");
		assertNotNull(ids);
		assertEquals(0, ids.size());
		
		attr1.setStringValue("do");
		attr1.setValueType(CtxAttributeValueType.STRING);
		this.userDB.update(attr1);
		
		attr2.setStringValue("la");
		attr2.setValueType(CtxAttributeValueType.STRING);
		attr2 = (CtxAttribute) this.userDB.update(attr2);
		
		attr3.setStringValue("do");
		attr3.setValueType(CtxAttributeValueType.STRING);
		this.userDB.update(attr3);
		
		ids = this.userDB.lookupEntities(CtxEntityTypes.PERSON, CtxAttributeTypes.NAME, "do", "do");
		assertEquals(1, ids.size());
		assertTrue(ids.contains(entId1));
       
		ids = this.userDB.lookupEntities(CtxEntityTypes.DEVICE, CtxAttributeTypes.NAME, "do", "do");
		assertEquals(1, ids.size());
		assertTrue(ids.contains(entId3));
		
		attr2.setStringValue("do");
		attr2.setValueType(CtxAttributeValueType.STRING);
		this.userDB.update(attr2);
		
		ids = this.userDB.lookupEntities(CtxEntityTypes.PERSON, CtxAttributeTypes.NAME, "do", "do");
		assertEquals(2, ids.size());
		assertTrue(ids.contains(entId1));
		assertTrue(ids.contains(entId2));
	}
	
   /*
	@Ignore
	@Test
	public void testLookupEntitiesIntegers() throws CtxException {
		List<CtxEntityIdentifier> identifiers;
		CtxEntity entity, entity2;
		CtxAttribute attribute2;
		CtxEntityIdentifier entityId;

		entity = userDB.createEntity("NUMBER");
		attribute = userDB.createAttribute((CtxEntityIdentifier)entity.getId(), "BOOKS");
		entity2 = userDB.createEntity("NUMBER");
		attribute2 = userDB.createAttribute((CtxEntityIdentifier)entity2.getId(), "BOOKS");
       
		// lookup by name attribute
		identifiers = userDB.lookupEntities("NUMBER", "BOOKS", 1, 10);
		assertEquals(0, identifiers.size());
		attribute.setIntegerValue(5);
		attribute.setValueType(CtxAttributeValueType.INTEGER);
		userDB.update(attribute);
		attribute2.setIntegerValue(12);
		attribute2.setValueType(CtxAttributeValueType.INTEGER);
		userDB.update(attribute2);
		//update with DB
		identifiers = userDB.lookupEntities("NUMBER", "BOOKS", 1, 10);
		System.out.println(identifiers);
		//       System.out.println(identifiers.get(0));
		//       System.out.println(identifiers.get(1));
       
		assertEquals(1, identifiers.size());
       
		assertTrue(identifiers.get(0)instanceof CtxEntityIdentifier);
		entityId = (CtxEntityIdentifier) identifiers.get(0);
		assertEquals(CtxModelType.ENTITY, entityId.getModelType());
		assertEquals("NUMBER", entityId.getType());

	}

	@Ignore
	@Test
	public void testLookupEntitiesBLOBS() throws CtxException {
		List<CtxEntityIdentifier> identifiers;
		CtxEntity entity, entity2;
		CtxAttribute attribute2;
		CtxEntityIdentifier entityId;

		entity = userDB.createEntity("NUMBER");
		attribute = userDB.createAttribute((CtxEntityIdentifier)entity.getId(), "BOOKS");
		entity2 = userDB.createEntity("NUMBER");
		attribute2 = userDB.createAttribute((CtxEntityIdentifier)entity2.getId(), "BOOKS");
       
		byte[] byteArray = new byte[2];
		byteArray[0] = 0x11;
		byteArray[1] = 0x00;
		byte[] byteArray2 = new byte[2];
		byteArray2[0] = 0x11;
		byteArray2[1] = 0x00;
       
		identifiers = userDB.lookupEntities("NUMBER", "BOOKS", byteArray, byteArray2);
		assertEquals(0, identifiers.size());
		attribute.setBinaryValue(byteArray);
		attribute.setValueType(CtxAttributeValueType.BINARY);
		userDB.update(attribute);
		//update with DB
		identifiers = userDB.lookupEntities("NUMBER", "BOOKS", byteArray, byteArray2);
		System.out.println(identifiers);
		//       System.out.println(identifiers.get(0));
		//       System.out.println(identifiers.get(1));
       
		assertEquals(1, identifiers.size());
       
		assertTrue(identifiers.get(0)instanceof CtxEntityIdentifier);
		entityId = (CtxEntityIdentifier) identifiers.get(0);
		assertEquals(CtxModelType.ENTITY, entityId.getModelType());
		assertEquals("NUMBER", entityId.getType());
	}
*/	
}