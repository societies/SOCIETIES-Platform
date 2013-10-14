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

import static org.junit.Assert.*;

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
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
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

		final IndividualCtxEntity indEntity = this.userDB.createIndividualEntity(CSS_ID, CtxEntityTypes.PERSON);

		assertNotNull(indEntity);
		assertNotNull(indEntity.getId());
		assertEquals(CtxModelType.ENTITY, indEntity.getModelType());
		assertEquals(CtxEntityTypes.PERSON, indEntity.getType());
		assertNotNull(indEntity.getObjectNumber());
		assertNotNull(indEntity.getLastModified());
		assertNotNull(indEntity.getAttributes());
		assertTrue(indEntity.getAttributes().isEmpty());
		assertNotNull(indEntity.getAssociations());
		assertEquals(2, indEntity.getAssociations().size());
		assertEquals(1, indEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).size());
		assertEquals(1, indEntity.getAssociations(CtxAssociationTypes.IS_ADMIN_OF).size());
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
		assertEquals(attribute.getLastModified().getTime(), attribute.getQuality().getLastUpdated().getTime());
		assertEquals(CtxOriginType.MANUALLY_SET, attribute.getQuality().getOriginType());
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
		assertTrue(entity.getAssociations(CtxAssociationTypes.USES_DEVICES).contains(association.getId()));
		assertEquals(1, entity.getAssociations(CtxAssociationTypes.USES_DEVICES).size());

		// Set another Parent Entity (Individual)
		final CtxEntityIdentifier parentEntityId2 = this.userDB.createIndividualEntity(CSS_ID, CtxEntityTypes.PERSON).getId();
		association.setParentEntity(parentEntityId2);
		association = (CtxAssociation) this.userDB.update(association);
		assertNotNull(association.getParentEntity());
		assertEquals(parentEntityId2, association.getParentEntity());
		// check association from the entity's side
		entity = (CtxEntity) this.userDB.retrieve(parentEntityId);
		assertTrue(entity.getAssociations(CtxAssociationTypes.USES_DEVICES).isEmpty());
		entity = (CtxEntity) this.userDB.retrieve(parentEntityId2);
		assertTrue(entity.getAssociations(CtxAssociationTypes.USES_DEVICES).contains(association.getId()));
		assertEquals(1, entity.getAssociations(CtxAssociationTypes.USES_DEVICES).size());

		// Add Child Entities
		final CtxEntityIdentifier childEntityId = userDB.createEntity(CtxEntityTypes.DEVICE).getId();
		association.addChildEntity(childEntityId);
		association = (CtxAssociation) this.userDB.update(association);
		assertEquals(parentEntityId2, association.getParentEntity());
		assertEquals(1, association.getChildEntities().size());
		assertTrue(association.getChildEntities().contains(childEntityId));
		// check association from the entity's side
		entity = (CtxEntity) this.userDB.retrieve(childEntityId);
		assertTrue(entity.getAssociations(CtxAssociationTypes.USES_DEVICES).contains(association.getId()));
		assertEquals(1, entity.getAssociations(CtxAssociationTypes.USES_DEVICES).size());

		final CtxEntityIdentifier childEntityId2 = userDB.createEntity(CtxEntityTypes.DEVICE).getId();
		association.addChildEntity(childEntityId2);
		association = (CtxAssociation) this.userDB.update(association);
		assertEquals(parentEntityId2, association.getParentEntity());
		assertEquals(2, association.getChildEntities().size());
		assertTrue(association.getChildEntities().contains(childEntityId));
		assertTrue(association.getChildEntities().contains(childEntityId2));
		// check association from the entity's side
		entity = (CtxEntity) this.userDB.retrieve(childEntityId2);
		assertTrue(entity.getAssociations(CtxAssociationTypes.USES_DEVICES).contains(association.getId()));
		assertEquals(1, entity.getAssociations(CtxAssociationTypes.USES_DEVICES).size());

		// Remove Parent Entity
		association.setParentEntity(null);
		association = (CtxAssociation) this.userDB.update(association);
		assertEquals(null, association.getParentEntity());
		// check association from the entity's side
		entity = (CtxEntity) this.userDB.retrieve(parentEntityId2);
		assertTrue(entity.getAssociations(CtxAssociationTypes.USES_DEVICES).isEmpty());

		// Remove Child Entities
		association.removeChildEntity(childEntityId);
		association.removeChildEntity(childEntityId2);
		association = (CtxAssociation) this.userDB.update(association);
		assertTrue(association.getChildEntities().isEmpty());
		// check association from the entity's side
		entity = (CtxEntity) this.userDB.retrieve(childEntityId);
		assertTrue(entity.getAssociations(CtxAssociationTypes.USES_DEVICES).isEmpty());
		entity = (CtxEntity) this.userDB.retrieve(childEntityId2);
		assertTrue(entity.getAssociations(CtxAssociationTypes.USES_DEVICES).isEmpty());
	}

	@Test
	public void testUpdateIsMemberOfAssociation() throws CtxException{

		// Create IndividualCtxEntity
		IndividualCtxEntity indEntity = 
				this.userDB.createIndividualEntity(CSS_ID, CtxEntityTypes.PERSON);
		assertNotNull(indEntity.getAssociations());
		assertEquals(1, indEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).size());
		assertTrue(indEntity.getCommunities().isEmpty());
		
		CtxAssociationIdentifier isMemberOfAssociationId = 
				indEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).iterator().next();
		CtxAssociation isMemberOfAssociation = 
				(CtxAssociation) this.userDB.retrieve(isMemberOfAssociationId);
		assertNotNull(isMemberOfAssociation.getParentEntity());
		assertEquals(indEntity.getId(), isMemberOfAssociation.getParentEntity());

		// Add Child Entities
		final CtxEntityIdentifier childEntityId = userDB.createEntity(CtxEntityTypes.COMMUNITY).getId();
		isMemberOfAssociation.addChildEntity(childEntityId);
		isMemberOfAssociation = (CtxAssociation) this.userDB.update(isMemberOfAssociation);
		assertEquals(indEntity.getId(), isMemberOfAssociation.getParentEntity());
		assertEquals(1, isMemberOfAssociation.getChildEntities().size());
		assertTrue(isMemberOfAssociation.getChildEntities().contains(childEntityId));
		// check association from the entity's side
		indEntity = (IndividualCtxEntity) this.userDB.retrieve(indEntity.getId());
		assertEquals(1, indEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).size());
		assertEquals(1, indEntity.getCommunities().size());
		assertTrue(indEntity.getCommunities().contains(childEntityId));

		final CtxEntityIdentifier childEntityId2 = userDB.createEntity(CtxEntityTypes.COMMUNITY).getId();
		isMemberOfAssociation.addChildEntity(childEntityId2);
		isMemberOfAssociation = (CtxAssociation) this.userDB.update(isMemberOfAssociation);
		assertEquals(indEntity.getId(), isMemberOfAssociation.getParentEntity());
		assertEquals(2, isMemberOfAssociation.getChildEntities().size());
		assertTrue(isMemberOfAssociation.getChildEntities().contains(childEntityId));
		assertTrue(isMemberOfAssociation.getChildEntities().contains(childEntityId2));
		// check association from the entity's side
		indEntity = (IndividualCtxEntity) this.userDB.retrieve(indEntity.getId());
		assertEquals(1, indEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).size());
		assertEquals(2, indEntity.getCommunities().size());
		assertTrue(indEntity.getCommunities().contains(childEntityId));
		assertTrue(indEntity.getCommunities().contains(childEntityId2));

		// Remove Child Entities
		isMemberOfAssociation.removeChildEntity(childEntityId);
		isMemberOfAssociation = (CtxAssociation) this.userDB.update(isMemberOfAssociation);
		// check association from the entity's side
		indEntity = (IndividualCtxEntity) this.userDB.retrieve(indEntity.getId());
		assertEquals(1, indEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).size());
		assertEquals(1, indEntity.getCommunities().size());
		assertTrue(indEntity.getCommunities().contains(childEntityId2));
		
		isMemberOfAssociation.removeChildEntity(childEntityId2);
		isMemberOfAssociation = (CtxAssociation) this.userDB.update(isMemberOfAssociation);
		// check association from the entity's side
		indEntity = (IndividualCtxEntity) this.userDB.retrieve(indEntity.getId());
		assertEquals(1, indEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).size());
		assertEquals(0, indEntity.getCommunities().size());
	}

	@Test
	public void testUpdateAttribute() throws CtxException{

		final CtxEntityIdentifier indEntityId = this.userDB.createIndividualEntity(CSS_ID, CtxEntityTypes.PERSON).getId();
		CtxAttribute attribute = this.userDB.createAttribute(indEntityId, CtxAttributeTypes.NAME);

		attribute.setStringValue("Jane Do");
		attribute.setValueType(CtxAttributeValueType.STRING);
		// verify update
		attribute = (CtxAttribute) this.userDB.update(attribute);
		assertEquals("Jane Do", attribute.getStringValue());
		assertNull(attribute.getIntegerValue());
		assertNull(attribute.getDoubleValue());
		assertNull(attribute.getBinaryValue());
		assertEquals(CtxAttributeValueType.STRING, attribute.getValueType());
		assertNull(attribute.getValueMetric());
		assertNull(attribute.getSourceId());
		assertEquals(CtxOriginType.MANUALLY_SET, attribute.getQuality().getOriginType());
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
		assertTrue(attribute2.getLastModified().getTime() - attribute2.getQuality().getLastUpdated().getTime() < 1000);
	}
	
	@Test
	public void testUpdateAttributeWithoutModification() throws Exception {

		final CtxEntityIdentifier indEntityId = this.userDB.createIndividualEntity(CSS_ID, CtxEntityTypes.PERSON).getId();
		CtxAttribute attribute = this.userDB.createAttribute(indEntityId, CtxAttributeTypes.NAME);

		attribute.setStringValue("Jane Do");
		attribute.setValueType(CtxAttributeValueType.STRING);
		// verify update
		attribute = (CtxAttribute) this.userDB.update(attribute);
		final Date lastModified = attribute.getLastModified();
		assertNotNull(lastModified);
		final Date lastUpdated = attribute.getQuality().getLastUpdated();
		assertNotNull(lastUpdated);
		assertEquals("Jane Do", attribute.getStringValue());
		assertNull(attribute.getIntegerValue());
		assertNull(attribute.getDoubleValue());
		assertNull(attribute.getBinaryValue());
		assertEquals(CtxAttributeValueType.STRING, attribute.getValueType());
		assertNull(attribute.getValueMetric());
		assertNull(attribute.getSourceId());
		assertTrue(attribute.getLastModified().getTime() - attribute.getQuality().getLastUpdated().getTime() < 1000);
		assertEquals(CtxOriginType.MANUALLY_SET, attribute.getQuality().getOriginType());
		assertNull(attribute.getQuality().getPrecision());
		assertNull(attribute.getQuality().getUpdateFrequency());
		
		final long delay = 1000;
		Thread.sleep(delay);
		// update with the same value - only QoC should change
		attribute.setStringValue("Jane Do");
		attribute.setValueType(CtxAttributeValueType.STRING);
		// verify update
		attribute = (CtxAttribute) this.userDB.update(attribute);
		final Date lastModified2 = attribute.getLastModified();
		final Date lastUpdated2 = attribute.getQuality().getLastUpdated();
		assertEquals(lastModified, lastModified2);
		assertTrue(lastUpdated2.getTime() - lastUpdated.getTime() >= delay);
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
		
		final IndividualCtxEntity entity = this.userDB.createIndividualEntity(CSS_ID, CtxEntityTypes.PERSON);
		final IndividualCtxEntity removedEntity = (IndividualCtxEntity) this.userDB.remove(entity.getId());
		assertNotNull(removedEntity);
		assertEquals(entity, removedEntity);
		assertNull(this.userDB.retrieve(removedEntity.getId()));;
		assertNull(this.userDB.remove(entity.getId()));
	}
	
	@Test
	public void testRemoveAttribute() throws CtxException {
		
		final IndividualCtxEntity entity = this.userDB.createIndividualEntity(CSS_ID, CtxEntityTypes.PERSON);
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
	public void testLookupEntitiesByAttrType() throws CtxException {
		
		List<CtxEntityIdentifier> ids;

		final CtxEntityIdentifier entId1 = this.userDB.createIndividualEntity(CSS_ID, CtxEntityTypes.PERSON).getId();
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

	@Test
	public void testLookupSetOfTypes() throws CtxException{
		   
		Set<CtxIdentifier> ids;
	    
		// Create test entity.   
		final CtxEntityIdentifier entId = this.userDB.createEntity(CtxEntityTypes.ORGANISATION).getId();
		final CtxEntityIdentifier entId2 = this.userDB.createEntity(CtxEntityTypes.ORGANISATION).getId();
		final CtxEntityIdentifier entId3 = this.userDB.createIndividualEntity(CSS_ID, CtxEntityTypes.SOCIAL_NETWORK).getId();
		
	    // Create test attribute.
	    final CtxAttribute attribute = this.userDB.createAttribute(entId, CtxAttributeTypes.AFFILIATION);
	    final CtxAttribute attribute2 = this.userDB.createAttribute(entId2, CtxAttributeTypes.AFFILIATION);
	    final CtxAttribute attribute3 = this.userDB.createAttribute(entId3, CtxAttributeTypes.SOCIAL_NETWORK_CONNECTOR);
	    
	    final Set<String> types = new HashSet<String>();
	    types.add(entId.getType());
	    types.add(entId3.getType());

	    //
	    // Lookup entities
	    //
	    ids = userDB.lookup(CSS_ID, types);
	    assertTrue(ids.contains(entId));
	    assertEquals(3, ids.size());
	              
	    assertTrue(ids.contains(entId2));
	    assertTrue(ids.contains(entId3));

	    final Set<String> types2 = new HashSet<String>();
	    types2.add(attribute.getType());
	    types2.add(attribute3.getType());
	    //
	    // Lookup attributes
	    //
	    ids = userDB.lookup(attribute.getOwnerId(), types2);
	    assertTrue(ids.contains(attribute.getId()));
	    assertEquals(3, ids.size());
	              
	    assertTrue(ids.contains(attribute2.getId()));
	    assertTrue(ids.contains(attribute3.getId()));
	       
	    //
	    // Lookup entities using modelType and type
	    //
	    final Set<String> types3 = new HashSet<String>();
	    types3.add(entId3.getType());
	    ids = userDB.lookup(CSS_ID, CtxModelType.ENTITY, types3);
	    assertTrue(ids.contains(entId3));
	    assertEquals(1, ids.size());
	    
	    //
	    // Lookup attributes using modelType and type
	    //
	    ids = userDB.lookup(attribute.getOwnerId(), CtxModelType.ATTRIBUTE, types2);
	    assertTrue(ids.contains(attribute.getId()));
	    assertTrue(ids.contains(attribute2.getId()));
	    assertTrue(ids.contains(attribute3.getId()));
	    assertEquals(3, ids.size());	
	    
	    //
	    // Lookup attributes using entityId and type
	    //
	    ids = userDB.lookup(entId, CtxModelType.ATTRIBUTE, types2);
	    assertTrue(ids.contains(attribute.getId()));
	    assertEquals(1, ids.size());
	    
	    //ASSOCIATIONS
		// Create IndividualCtxEntity
		IndividualCtxEntity indEntity = 
				this.userDB.createIndividualEntity(CSS_ID, CtxEntityTypes.PERSON);
		assertNotNull(indEntity.getAssociations());
		assertEquals(1, indEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).size());
		assertTrue(indEntity.getCommunities().isEmpty());
		
		CtxAssociationIdentifier isMemberOfAssociationId = 
				indEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).iterator().next();
		CtxAssociation isMemberOfAssociation = 
				(CtxAssociation) this.userDB.retrieve(isMemberOfAssociationId);
		assertNotNull(isMemberOfAssociation.getParentEntity());
		assertEquals(indEntity.getId(), isMemberOfAssociation.getParentEntity());

		// Add Child Entities
		final CtxEntityIdentifier childEntityId = userDB.createEntity(CtxEntityTypes.COMMUNITY).getId();
		isMemberOfAssociation.addChildEntity(childEntityId);
		isMemberOfAssociation = (CtxAssociation) this.userDB.update(isMemberOfAssociation);
		assertEquals(indEntity.getId(), isMemberOfAssociation.getParentEntity());
		assertEquals(1, isMemberOfAssociation.getChildEntities().size());
		assertTrue(isMemberOfAssociation.getChildEntities().contains(childEntityId));
		// check association from the entity's side
		indEntity = (IndividualCtxEntity) this.userDB.retrieve(indEntity.getId());
		assertEquals(1, indEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).size());
		assertEquals(1, indEntity.getCommunities().size());
		assertTrue(indEntity.getCommunities().contains(childEntityId));

		final CtxEntityIdentifier childEntityId2 = userDB.createEntity(CtxEntityTypes.COMMUNITY).getId();
		isMemberOfAssociation.addChildEntity(childEntityId2);
		isMemberOfAssociation = (CtxAssociation) this.userDB.update(isMemberOfAssociation);
		assertEquals(indEntity.getId(), isMemberOfAssociation.getParentEntity());
		assertEquals(2, isMemberOfAssociation.getChildEntities().size());
		assertTrue(isMemberOfAssociation.getChildEntities().contains(childEntityId));
		assertTrue(isMemberOfAssociation.getChildEntities().contains(childEntityId2));
		// check association from the entity's side
		indEntity = (IndividualCtxEntity) this.userDB.retrieve(indEntity.getId());
		assertEquals(1, indEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).size());
		assertEquals(2, indEntity.getCommunities().size());
		assertTrue(indEntity.getCommunities().contains(childEntityId));
		assertTrue(indEntity.getCommunities().contains(childEntityId2));

	    // 
	    // Lookup associations using entityId and type
	    //
		// using the child entityId
	    final Set<String> types4 = new HashSet<String>();
	    types4.add(isMemberOfAssociation.getType());
	    ids = userDB.lookup(childEntityId, CtxModelType.ASSOCIATION, types4);
	    assertTrue(ids.contains(isMemberOfAssociation.getId()));
	    assertEquals(1, ids.size());
	    // using the parent entityId
	    ids = userDB.lookup(indEntity.getId(), CtxModelType.ASSOCIATION, types4);
	    assertTrue(ids.contains(isMemberOfAssociation.getId()));
	    assertEquals(1, ids.size());
	    
	    //
	    // Lookup associations using ownerId and type
	    //
	    ids = userDB.lookup(isMemberOfAssociation.getOwnerId(), types4);
	    assertTrue(ids.contains(isMemberOfAssociation.getId()));
	    
	    //
	    // Lookup associations using ownerId, modelType and type
	    //
	    ids = userDB.lookup(isMemberOfAssociation.getOwnerId(), CtxModelType.ASSOCIATION, types4);
	    assertTrue(ids.contains(isMemberOfAssociation.getId()));
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