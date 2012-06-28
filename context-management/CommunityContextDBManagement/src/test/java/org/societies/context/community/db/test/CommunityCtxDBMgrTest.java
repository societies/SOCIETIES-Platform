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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CommunityMemberCtxEntity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.context.community.db.impl.CommunityCtxDBMgr;

/**
 * 
 * 
 * @author
 * 
 */
public class CommunityCtxDBMgrTest {

	private static final String CIS_IIDENTITY_STRING = "myCIS.societies.local";

	private CommunityCtxDBMgr communityDB;
	
	CommunityCtxEntity entity;
	CtxAttribute attribute;
	CtxModelObject modObj;	
	CommunityMemberCtxEntity member;

	private static IIdentity mockCisIdentity = mock(IIdentity.class);
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		when(mockCisIdentity.toString()).thenReturn(CIS_IIDENTITY_STRING);

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
		communityDB = new CommunityCtxDBMgr();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		communityDB = null;
	}

	@Test
 	public void testCreateCommunityCtxEntity() throws CtxException{
		System.out.println("---- testCreateCommunityCtxEntity");

		entity = communityDB.createCommunityEntity(mockCisIdentity);

		assertNotNull(entity);
		assertEquals(mockCisIdentity.toString(), entity.getOwnerId());
	}
	
	@Test
 	public void testCreateCommunityCtxAttribute() throws CtxException{
		System.out.println("---- testCreateCommunityCtxAttribute");

		entity = communityDB.createCommunityEntity(mockCisIdentity);
		attribute = communityDB.createCommunityAttribute(entity.getId(), "name");
		
		assertNotNull(attribute);
		assertEquals("name", attribute.getType());
	}
	
   @Test
   public void testUpdateEntity() throws CtxException{
	   System.out.println("---- testUpdateEntity");

	   entity = communityDB.createCommunityEntity(mockCisIdentity);
	   System.out.println("entities attributes " + entity.getAttributes());
	   System.out.println("entities members " + entity.getMembers());

	   // Add CommunityMemberCtxEntity
	   member = communityDB.createCommunityEntity(mockCisIdentity);
	   entity.addMember(member);
	   
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
	   entity.addMember(member);
	   
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

   @Test
   public void testRetrieveCommunityEntity() throws CtxException {

	   entity = communityDB.createCommunityEntity(mockCisIdentity);

	   CommunityCtxEntity retrEntity;
	   retrEntity = communityDB.retrieveCommunityEntity(mockCisIdentity);
	   assertNotNull(retrEntity);
	   assertEquals(entity, retrEntity);
   }
   
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

}