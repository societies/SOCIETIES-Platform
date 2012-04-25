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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.CtxModelObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.societies.context.user.db.impl.CtxModelObjectNumberGenerator;
import org.societies.context.user.db.impl.UserCtxDBMgr;

/**
 * 
 * 
 * @author
 * 
 */



public class UserContextDBManagementTest {

	private UserCtxDBMgr userDB;
	CtxEntity entity;	
	IndividualCtxEntity indEntity;
	CtxAttribute attribute;
	CtxAssociation association;
	CtxModelObject modObj;
	List<CtxIdentifier> foundList = new ArrayList<CtxIdentifier>();

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
		userDB = new UserCtxDBMgr();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		userDB = null;
	}
	
   @Test
 	public void testCreateIndividualCtxEntity() throws CtxException{
		System.out.println("---- testCreateIndividualCtxEntity");
		indEntity = userDB.createIndividualCtxEntity("person");

		assertNotNull(indEntity);
		assertEquals("person", indEntity.getType());
	}

   @Test
 	public void testCreateCtxAssociation() throws CtxException{
		System.out.println("---- testCreateCtxAssociation");
		association = userDB.createAssociation("IsRelatedWith");
		assertNotNull(association);
		assertEquals("IsRelatedWith", association.getType());
	}

   @Test
 	public void testAssociations() throws CtxException{
		System.out.println("---- testAssociations");

		association = userDB.createAssociation("IsRelatedWith");
		
		//Set Parent Entity
		final CtxEntityIdentifier entId1 = userDB.createEntity("person").getId();
		association.setParentEntity(entId1);		
		assertEquals(entId1, association.getParentEntity());

		//Add Child Entities
		final CtxEntityIdentifier entId2 = userDB.createEntity("person").getId();
		final CtxEntityIdentifier entId3 = userDB.createEntity("person").getId();
		association.addChildEntity(entId2);
		association.addChildEntity(entId3);
		assertTrue(association.getChildEntities().contains(entId2));
		assertTrue(association.getChildEntities().contains(entId3));
		assertTrue(association.getChildEntities("person").contains(entId2));
		assertTrue(association.getChildEntities("person").contains(entId3));

		//Remove Parent Entity
		association.setParentEntity(null);
		assertEquals(null, association.getParentEntity());
		
		//Remove Child Entities
		association.removeChildEntity(entId2);
		association.removeChildEntity(entId3);
		assertTrue(association.getChildEntities().isEmpty());
	}

   @Test
   public void testRetrieveAssociation() throws CtxException{
	   System.out.println("---- testRetrieveAssociation");
	   ////////////////
	   association = userDB.createAssociation("IsRelatedWith");

	   
	   modObj = userDB.retrieve(association.getId());
	   association = (CtxAssociation) modObj;

	   assertNotNull(association);
   }

   @Test
	public void testCreateAttribute() throws CtxException{
	   System.out.println("---- testCreateAttribute");
	   indEntity = userDB.createIndividualCtxEntity("house");
	   modObj = userDB.createAttribute(indEntity.getId(), CtxAttributeValueType.EMPTY, "name");
	   attribute = (CtxAttribute) modObj;

	   assertNotNull(attribute);
	   assertEquals("name", attribute.getType());

   }
   
   @Test
   public void testRetrieveAttribute() throws CtxException{
	   System.out.println("---- testRetrieveAttribute");
	   ////////////////
	   indEntity = userDB.createIndividualCtxEntity("house");
	   attribute = userDB.createAttribute(indEntity.getId(), CtxAttributeValueType.EMPTY, "name");
	   
	   modObj = userDB.retrieve(attribute.getId());
	   attribute = (CtxAttribute) modObj;

	   assertNotNull(attribute);
   }

   @Test
   public void testUpdateAttribute() throws CtxException{
	   System.out.println("---- testUpdateAttribute");

	   indEntity = userDB.createIndividualCtxEntity("house");
	   attribute = userDB.createAttribute(indEntity.getId(), CtxAttributeValueType.EMPTY, "name");

	   modObj = userDB.retrieve(attribute.getId());
	   attribute = (CtxAttribute) modObj;
//	   attribute = (CtxAttribute) callback.getCtxModelObject();
	   attribute.setIntegerValue(5);
	   userDB.update(attribute);
	   //verify update
	   modObj = userDB.retrieve(attribute.getId());
	   attribute = (CtxAttribute) modObj;
//	   attribute = (CtxAttribute) callback.getCtxModelObject();
	   System.out.println("attribute value should be 5 and it is:"+attribute.getIntegerValue());

	   assertNotNull(attribute);
//	   assertEquals(5,attribute.getIntegerValue());
	}
   
   @Test
   public void testLookup() throws CtxException{
	   System.out.println("---- testLookup");
	   
       List<CtxIdentifier> ids;
       
       // Create test entities.
       final CtxEntityIdentifier entId1 = userDB.createEntity("FooBar").getId();
       final CtxEntityIdentifier entId2 = userDB.createEntity("Foo").getId();
       final CtxEntityIdentifier entId3 = userDB.createEntity("Bar").getId();
      
       // Create test attributes.
       final CtxAttributeIdentifier attrId1 = userDB.createAttribute(entId1, CtxAttributeValueType.EMPTY, "FooBar").getId();
       final CtxAttributeIdentifier attrId2 = userDB.createAttribute(entId1, CtxAttributeValueType.EMPTY, "Foo").getId();
       final CtxAttributeIdentifier attrId3 = userDB.createAttribute(entId1, CtxAttributeValueType.EMPTY, "Bar").getId();
       
       // Create test attributes.
       final CtxAssociationIdentifier assocId1 = userDB.createAssociation("FooBar").getId();
       final CtxAssociationIdentifier assocId2 = userDB.createAssociation("Foo").getId();
       final CtxAssociationIdentifier assocId3 = userDB.createAssociation("Bar").getId();

       //
       // Lookup entities
       //
       
       ids =userDB.lookup(CtxModelType.ENTITY, "FooBar");
       assertTrue(ids.contains(entId1));
       assertEquals(1, ids.size());
              
       ids = userDB.lookup(CtxModelType.ENTITY, "Foo");
       assertTrue(ids.contains(entId2));
       assertEquals(1, ids.size());
       
       ids = userDB.lookup(CtxModelType.ENTITY, "Bar");
       assertTrue(ids.contains(entId3));
       assertEquals(1, ids.size());
       
       //
       // Lookup attributes
       //
       
       ids = userDB.lookup(CtxModelType.ATTRIBUTE, "FooBar");
       assertTrue(ids.contains(attrId1));
       assertEquals(1, ids.size());
              
       ids = userDB.lookup(CtxModelType.ATTRIBUTE, "Foo");
       assertTrue(ids.contains(attrId2));
       assertEquals(1, ids.size());
       
       ids = userDB.lookup(CtxModelType.ATTRIBUTE, "Bar");
       assertTrue(ids.contains(attrId3));
       assertEquals(1, ids.size());
       
       //
       // Lookup associations.
       //
       
       ids = userDB.lookup(CtxModelType.ASSOCIATION, "FooBar");
       assertTrue(ids.contains(assocId1));
       assertEquals(1, ids.size());
             
       ids = userDB.lookup(CtxModelType.ASSOCIATION, "Foo");
       assertTrue(ids.contains(assocId2));
       assertEquals(1, ids.size());
       
       ids = userDB.lookup(CtxModelType.ASSOCIATION, "Bar");
       assertTrue(ids.contains(assocId3));
       assertEquals(1, ids.size());
       
	}
	   
}