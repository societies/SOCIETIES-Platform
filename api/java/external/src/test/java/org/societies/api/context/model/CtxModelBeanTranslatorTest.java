/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru≈æbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA√á√ÉO, SA (PTIN), IBM Corp., 
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
package org.societies.api.context.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.test.util.MockBlobClass;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.schema.context.model.CommunityCtxEntityBean;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxAttributeValueTypeBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxOriginTypeBean;
import org.societies.api.schema.context.model.IndividualCtxEntityBean;

/**
 * Tests the {@link CtxModelBeanTranslator}.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 2.0
 */
public class CtxModelBeanTranslatorTest {

	private static final String CSS_IDENTITY_STRING = "foo-css.societies.local";
	private static final String CIS_IDENTITY_STRING = "foo-cis.societies.local";
	
	/* CSS Entity */
	private static IndividualCtxEntity userEnt;
	private static CtxAssociation ownsCssNodeAssoc;
	private static CtxAssociation isMemberOfAssoc;
	private static CtxAttribute userLocationSymbAttr;
	private static CtxAttribute userLocationCoordsAttr;
	
	/* CSS Node Entity */
	private static CtxEntity cssNodeEnt;
	private static CtxAttribute cssNodeIdAttr;
	
	/* CIS Entity */
	private static CommunityCtxEntity communityEnt;
	private static CtxAssociation hasMembersAssoc;
	private static CtxAttribute communityNameAttr;
	
	private static long objectNumber = 1l;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		/* CSS Entity */
		final CtxEntityIdentifier userEntId = 
				new CtxEntityIdentifier(CSS_IDENTITY_STRING, CtxEntityTypes.PERSON, objectNumber++);
		final Date userEntLastModified = new Date();
		
		final CtxAssociationIdentifier ownsCssNodeAssocId = 
				new CtxAssociationIdentifier(CSS_IDENTITY_STRING, CtxAssociationTypes.OWNS_CSS_NODES, objectNumber++);
		final Date ownsCssNodeAssocLastModified = new Date();
		
		final CtxAssociationIdentifier isMemberOfAssocId = 
				new CtxAssociationIdentifier(CSS_IDENTITY_STRING, CtxAssociationTypes.IS_MEMBER_OF, objectNumber++);
		final Date isMemberOfAssocLastModified = new Date();
		
		final CtxAssociationIdentifier hasMembersAssocId = 
				new CtxAssociationIdentifier(CIS_IDENTITY_STRING, CtxAssociationTypes.HAS_MEMBERS, objectNumber++);
		final Date hasMembersAssocLastModified = new Date();
		
		final CtxAttributeIdentifier userLocationSymbAttrId = 
				new CtxAttributeIdentifier(userEntId, CtxAttributeTypes.LOCATION_SYMBOLIC, objectNumber++);
		final Date userLocationSymbAttrLastModified = new Date();
		final Date userLocationSymbAttrLastUpdated = new Date(userLocationSymbAttrLastModified.getTime()); 
		userLocationSymbAttr = CtxModelObjectFactory.getInstance().createAttribute(
				userLocationSymbAttrId, userLocationSymbAttrLastModified, 
				userLocationSymbAttrLastUpdated, "home");
		userLocationSymbAttr.setValueType(CtxAttributeValueType.STRING);
		userLocationSymbAttr.setHistoryRecorded(true);
		userLocationSymbAttr.setSourceId("RFID-666");
		userLocationSymbAttr.getQuality().setOriginType(CtxOriginType.INFERRED);
		userLocationSymbAttr.getQuality().setPrecision(1.0d);
		userLocationSymbAttr.getQuality().setUpdateFrequency(1.0d);
		
		final CtxAttributeIdentifier userLocationCoordsAttrId = 
				new CtxAttributeIdentifier(userEntId, CtxAttributeTypes.LOCATION_COORDINATES, objectNumber++);
		final Date userLocationCoordsAttrLastModified = new Date();
		final Date userLocationCoordsAttrLastUpdated = new Date(userLocationCoordsAttrLastModified.getTime());
		userLocationCoordsAttr = CtxModelObjectFactory.getInstance().createAttribute(
				userLocationCoordsAttrId, userLocationCoordsAttrLastModified, 
				userLocationCoordsAttrLastUpdated, null);
		userLocationCoordsAttr.setHistoryRecorded(true);
		
		final CtxEntityIdentifier cssNodeEntId = 
				new CtxEntityIdentifier(CSS_IDENTITY_STRING, CtxEntityTypes.CSS_NODE, objectNumber++);
		final Date cssNodeEntLastModified = new Date();
		final Set<CtxEntityIdentifier> ownsCssNodeAssocChildEntities = new HashSet<CtxEntityIdentifier>();
		ownsCssNodeAssocChildEntities.add(cssNodeEntId);
		ownsCssNodeAssoc = CtxModelObjectFactory.getInstance().createAssociation(
				ownsCssNodeAssocId, ownsCssNodeAssocLastModified, userEntId, ownsCssNodeAssocChildEntities);
		
		final CtxEntityIdentifier communityEntId = 
				new CtxEntityIdentifier(CIS_IDENTITY_STRING, CtxEntityTypes.COMMUNITY, objectNumber++);
		final Set<CtxEntityIdentifier> isMemberOfAssocChildEntities = new HashSet<CtxEntityIdentifier>();
		isMemberOfAssocChildEntities.add(communityEntId);
		isMemberOfAssoc = CtxModelObjectFactory.getInstance().createAssociation(
				isMemberOfAssocId, isMemberOfAssocLastModified, userEntId, isMemberOfAssocChildEntities);
		
		final Set<CtxAttribute> userEntAttrs = new HashSet<CtxAttribute>();
		userEntAttrs.add(userLocationSymbAttr);
		userEntAttrs.add(userLocationCoordsAttr);
		final Set<CtxAssociationIdentifier> userEntAssocs = new HashSet<CtxAssociationIdentifier>();
		userEntAssocs.add(ownsCssNodeAssocId);
		userEntAssocs.add(isMemberOfAssocId);
		userEntAssocs.add(hasMembersAssocId);
		final Set<CtxEntityIdentifier> userEntCommunities = new HashSet<CtxEntityIdentifier>();
		userEntCommunities.add(communityEntId);
		userEnt = CtxModelObjectFactory.getInstance().createIndividualEntity(
				userEntId, userEntLastModified, userEntAttrs, userEntAssocs, userEntCommunities);
		
		/* CSS Node Entity */
		final CtxAttributeIdentifier cssNodeIdAttrId = 
				new CtxAttributeIdentifier(cssNodeEntId, CtxAttributeTypes.ID, objectNumber++);
		final Date cssNodeIdAttrLastModified = new Date();
		final Date cssNodeIdAttrLastUpdated = new Date(cssNodeIdAttrLastModified.getTime());
		cssNodeIdAttr = CtxModelObjectFactory.getInstance().createAttribute(
				cssNodeIdAttrId, cssNodeIdAttrLastModified, cssNodeIdAttrLastUpdated,
				CSS_IDENTITY_STRING);
		final Set<CtxAttribute> cssNodeEntAttrs = new HashSet<CtxAttribute>();
		cssNodeEntAttrs.add(cssNodeIdAttr);
		final Set<CtxAssociationIdentifier> cssNodeEntAssocs = new HashSet<CtxAssociationIdentifier>();
		cssNodeEntAssocs.add(ownsCssNodeAssocId);
		cssNodeEnt = CtxModelObjectFactory.getInstance().createEntity(
				cssNodeEntId, cssNodeEntLastModified, cssNodeEntAttrs, cssNodeEntAssocs);
		
		/* CIS Entity */
		final Set<CtxEntityIdentifier> hasMembersAssocChildEntities = new HashSet<CtxEntityIdentifier>();
		hasMembersAssocChildEntities.add(userEntId);
		hasMembersAssoc = CtxModelObjectFactory.getInstance().createAssociation(
				hasMembersAssocId, hasMembersAssocLastModified, communityEntId, hasMembersAssocChildEntities);
		final Date communityEntLastModified = new Date();
		
		final CtxAttributeIdentifier communityNameAttrId = 
				new CtxAttributeIdentifier(communityEntId, CtxAttributeTypes.NAME, objectNumber++);
		final Date communityNameAttrLastModified = new Date();
		final Date communityNameAttrLastUpdated = new Date(cssNodeIdAttrLastModified.getTime());
		communityNameAttr = CtxModelObjectFactory.getInstance().createAttribute(
				communityNameAttrId, communityNameAttrLastModified, communityNameAttrLastUpdated,
				"A Kewl Community");
		final Set<CtxAttribute> communityEntAttrs = new HashSet<CtxAttribute>();
		communityEntAttrs.add(communityNameAttr);
		final Set<CtxAssociationIdentifier> communityEntAssocs = new HashSet<CtxAssociationIdentifier>();
		communityEntAssocs.add(hasMembersAssocId);
		communityEntAssocs.add(isMemberOfAssocId);
		final Set<CtxEntityIdentifier> communityEntCommunities = new HashSet<CtxEntityIdentifier>();
		final Set<CtxEntityIdentifier> communityEntMembers = new HashSet<CtxEntityIdentifier>();
		communityEntMembers.add(userEntId);
		communityEnt = CtxModelObjectFactory.getInstance().createCommunityEntity(
				communityEntId, communityEntLastModified, communityEntAttrs, 
				communityEntAssocs, communityEntCommunities, communityEntMembers);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		/* CSS Entity */
		userEnt = null;
		ownsCssNodeAssoc = null;
		isMemberOfAssoc = null;
		userLocationSymbAttr = null;
		userLocationCoordsAttr = null;
		
		/* CSS Node Entity */
		cssNodeEnt = null;
		cssNodeIdAttr = null;
		
		/* CIS Entity */
		communityEnt = null;
		hasMembersAssoc = null;
		communityNameAttr = null;
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
		
	/**
	 * Test method for {@link CtxModelBeanTranslator#fromIndiCtxEntity}
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testIndividualEntityToBean() throws Exception {

		final IndividualCtxEntityBean userEntBean = 
				CtxModelBeanTranslator.getInstance().fromIndiCtxEntity(userEnt);				
		assertNotNull(userEntBean);
		assertEquals(userEnt.getLastModified(), userEntBean.getLastModified());
		assertEquals(CSS_IDENTITY_STRING, userEntBean.getId().getOwnerId());
		assertEquals(CtxEntityTypes.PERSON, userEntBean.getId().getType());
		// TODO assertEquals(CtxModelTypeBean.ENTITY, userEntBean.getModelType());
		assertEquals(userEnt.getId().getObjectNumber(), new Long(userEntBean.getId().getObjectNumber()));
		// attributes
		assertNotNull(userEntBean.getAttributes());
		assertEquals(userEnt.getAttributes().size(), userEntBean.getAttributes().size());
		// associations
		assertNotNull(userEntBean.getAssociations());
		assertEquals(userEnt.getAssociations().size(), userEntBean.getAssociations().size());
	}
	
	/**
	 * Test method for {@link CtxModelBeanTranslator#fromIndiCtxEntityBean}
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testIndividualEntityFromBean() throws Exception {

		final IndividualCtxEntityBean userEntBean = 
				CtxModelBeanTranslator.getInstance().fromIndiCtxEntity(userEnt);
		final IndividualCtxEntity userEntCopy =
				CtxModelBeanTranslator.getInstance().fromIndiCtxEntityBean(userEntBean);
		assertNotNull(userEntCopy);
		assertEquals(userEnt, userEntCopy);
		assertEquals(userEnt.getId(), userEntCopy.getId());
		assertEquals(userEnt.getLastModified(), userEntCopy.getLastModified());
		assertEquals(CSS_IDENTITY_STRING, userEntCopy.getId().getOwnerId());
		assertEquals(CSS_IDENTITY_STRING, userEntCopy.getOwnerId());
		assertEquals(CtxEntityTypes.PERSON, userEntCopy.getId().getType());
		assertEquals(CtxEntityTypes.PERSON, userEntCopy.getType());
		assertEquals(CtxModelType.ENTITY, userEntCopy.getModelType());
		assertEquals(CtxModelType.ENTITY, userEntCopy.getId().getModelType());
		assertEquals(userEnt.getObjectNumber(), userEntCopy.getObjectNumber());
		assertEquals(userEnt.getId().getObjectNumber(), userEntCopy.getId().getObjectNumber());
		// attributes
		assertNotNull(userEntCopy.getAttributes());
		assertEquals(userEnt.getAttributes(), userEntCopy.getAttributes());
		// associations
		assertNotNull(userEntCopy.getAssociations());
		assertEquals(userEnt.getAssociations(), userEntCopy.getAssociations());
		// communities
		assertNotNull(userEntCopy.getCommunities());
		assertEquals(userEnt.getCommunities(), userEntCopy.getCommunities());
	}
	
	/**
	 * Test method for {@link CtxModelBeanTranslator#fromCommunityCtxEntity}
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testCommunityEntityToBean() throws Exception {

		final CommunityCtxEntityBean communityEntBean = 
				CtxModelBeanTranslator.getInstance().fromCommunityCtxEntity(communityEnt);				
		assertNotNull(communityEntBean);
		assertEquals(communityEnt.getLastModified(), communityEntBean.getLastModified());
		assertEquals(CIS_IDENTITY_STRING, communityEntBean.getId().getOwnerId());
		assertEquals(CtxEntityTypes.COMMUNITY, communityEntBean.getId().getType());
		// TODO assertEquals(CtxModelTypeBean.ENTITY, communityEntBean.getModelType());
		assertEquals(communityEnt.getId().getObjectNumber(), new Long(communityEntBean.getId().getObjectNumber()));
		// attributes
		assertNotNull(communityEntBean.getAttributes());
		assertEquals(communityEnt.getAttributes().size(), communityEntBean.getAttributes().size());
		// associations
		assertNotNull(communityEntBean.getAssociations());
		assertEquals(communityEnt.getAssociations().size(), communityEntBean.getAssociations().size());
	}
	
	/**
	 * Test method for {@link CtxModelBeanTranslator#fromCommunityCtxEntityBean}
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testCommunityEntityFromBean() throws Exception {

		final CommunityCtxEntityBean communityEntBean = 
				CtxModelBeanTranslator.getInstance().fromCommunityCtxEntity(communityEnt);
		final CommunityCtxEntity communityEntCopy =
				CtxModelBeanTranslator.getInstance().fromCommunityCtxEntityBean(communityEntBean);
		assertNotNull(communityEntCopy);
		assertEquals(communityEnt, communityEntCopy);
		assertEquals(communityEnt.getId(), communityEntCopy.getId());
		assertEquals(communityEnt.getLastModified(), communityEntCopy.getLastModified());
		assertEquals(CIS_IDENTITY_STRING, communityEntCopy.getId().getOwnerId());
		assertEquals(CIS_IDENTITY_STRING, communityEntCopy.getOwnerId());
		assertEquals(CtxEntityTypes.COMMUNITY, communityEntCopy.getId().getType());
		assertEquals(CtxEntityTypes.COMMUNITY, communityEntCopy.getType());
		assertEquals(CtxModelType.ENTITY, communityEntCopy.getModelType());
		assertEquals(CtxModelType.ENTITY, communityEntCopy.getId().getModelType());
		assertEquals(communityEnt.getObjectNumber(), communityEntCopy.getObjectNumber());
		assertEquals(communityEnt.getId().getObjectNumber(), communityEntCopy.getId().getObjectNumber());
		// attributes
		assertNotNull(communityEntCopy.getAttributes());
		assertEquals(communityEnt.getAttributes(), communityEntCopy.getAttributes());
		// associations
		assertNotNull(communityEntCopy.getAssociations());
		assertEquals(communityEnt.getAssociations(), communityEntCopy.getAssociations());
		// communities
		assertNotNull(communityEntCopy.getCommunities());
		assertEquals(communityEnt.getCommunities(), communityEntCopy.getCommunities());
		// members
		assertNotNull(communityEntCopy.getMembers());
		assertEquals(communityEnt.getMembers(), communityEntCopy.getMembers());
	}
	
	/**
	 * Test method for {@link CtxModelBeanTranslator#fromCtxEntity}
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testEntityToBean() throws Exception {

		final CtxEntityBean cssNodeEntBean = 
				CtxModelBeanTranslator.getInstance().fromCtxEntity(cssNodeEnt);				
		assertNotNull(cssNodeEntBean);
		assertEquals(cssNodeEnt.getLastModified(), cssNodeEntBean.getLastModified());
		assertEquals(CSS_IDENTITY_STRING, cssNodeEntBean.getId().getOwnerId());
		assertEquals(CtxEntityTypes.CSS_NODE, cssNodeEntBean.getId().getType());
		// TODO assertEquals(CtxModelTypeBean.ENTITY, cssNodeEntBean.getModelType());
		assertEquals(cssNodeEnt.getId().getObjectNumber(), new Long(cssNodeEntBean.getId().getObjectNumber()));
		// attributes
		assertNotNull(cssNodeEntBean.getAttributes());
		assertEquals(cssNodeEnt.getAttributes().size(), cssNodeEntBean.getAttributes().size());
		// associations
		assertNotNull(cssNodeEntBean.getAssociations());
		assertEquals(cssNodeEnt.getAssociations().size(), cssNodeEntBean.getAssociations().size());
	}
	
	/**
	 * Test method for {@link CtxModelBeanTranslator#fromCtxEntityBean}
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testEntityFromBean() throws Exception {

		final CtxEntityBean cssNodeEntBean = 
				CtxModelBeanTranslator.getInstance().fromCtxEntity(cssNodeEnt);
		final CtxEntity cssNodeEntCopy =
				CtxModelBeanTranslator.getInstance().fromCtxEntityBean(cssNodeEntBean);
		assertNotNull(cssNodeEntCopy);
		assertEquals(cssNodeEnt, cssNodeEntCopy);
		assertEquals(cssNodeEnt.getId(), cssNodeEntCopy.getId());
		assertEquals(cssNodeEnt.getLastModified(), cssNodeEntCopy.getLastModified());
		assertEquals(CSS_IDENTITY_STRING, cssNodeEntCopy.getId().getOwnerId());
		assertEquals(CSS_IDENTITY_STRING, cssNodeEntCopy.getOwnerId());
		assertEquals(CtxEntityTypes.CSS_NODE, cssNodeEntCopy.getId().getType());
		assertEquals(CtxEntityTypes.CSS_NODE, cssNodeEntCopy.getType());
		assertEquals(CtxModelType.ENTITY, cssNodeEntCopy.getModelType());
		assertEquals(CtxModelType.ENTITY, cssNodeEntCopy.getId().getModelType());
		assertEquals(cssNodeEnt.getObjectNumber(), cssNodeEntCopy.getObjectNumber());
		assertEquals(cssNodeEnt.getId().getObjectNumber(), cssNodeEntCopy.getId().getObjectNumber());
		// attributes
		assertNotNull(cssNodeEntCopy.getAttributes());
		assertEquals(cssNodeEnt.getAttributes(), cssNodeEntCopy.getAttributes());
		// associations
		assertNotNull(cssNodeEntCopy.getAssociations());
		assertEquals(cssNodeEnt.getAssociations(), cssNodeEntCopy.getAssociations());
	}
	
	/**
	 * Test method for {@link CtxModelBeanTranslator#fromCtxAssociation}
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testAssociationToBean() throws Exception {

		final CtxAssociationBean ownsCssNodeAssocBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAssociation(ownsCssNodeAssoc);				
		assertNotNull(ownsCssNodeAssocBean);
		assertEquals(ownsCssNodeAssoc.getLastModified(), ownsCssNodeAssocBean.getLastModified());
		assertEquals(CSS_IDENTITY_STRING, ownsCssNodeAssocBean.getId().getOwnerId());
		assertEquals(CtxAssociationTypes.OWNS_CSS_NODES, ownsCssNodeAssocBean.getId().getType());
		// TODO assertEquals(CtxModelTypeBean.ASSOCIATION, ownsCssNodeAssocBean.getModelType());
		assertEquals(ownsCssNodeAssoc.getId().getObjectNumber(), new Long(ownsCssNodeAssocBean.getId().getObjectNumber()));
		
		final CtxAssociationBean isMemberOfAssocBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAssociation(isMemberOfAssoc);				
		assertNotNull(isMemberOfAssocBean);
		assertEquals(isMemberOfAssoc.getLastModified(), isMemberOfAssocBean.getLastModified());
		assertEquals(CSS_IDENTITY_STRING, isMemberOfAssocBean.getId().getOwnerId());
		assertEquals(CtxAssociationTypes.IS_MEMBER_OF, isMemberOfAssocBean.getId().getType());
		// TODO assertEquals(CtxModelTypeBean.ASSOCIATION, isMemberOfAssocBean.getModelType());
		assertEquals(isMemberOfAssoc.getId().getObjectNumber(), new Long(isMemberOfAssocBean.getId().getObjectNumber()));
		
		final CtxAssociationBean hasMembersAssocBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAssociation(hasMembersAssoc);				
		assertNotNull(hasMembersAssocBean);
		assertEquals(hasMembersAssoc.getLastModified(), hasMembersAssocBean.getLastModified());
		assertEquals(CIS_IDENTITY_STRING, hasMembersAssocBean.getId().getOwnerId());
		assertEquals(CtxAssociationTypes.HAS_MEMBERS, hasMembersAssocBean.getId().getType());
		// TODO assertEquals(CtxModelTypeBean.ASSOCIATION, hasMembersAssocBean.getModelType());
		assertEquals(hasMembersAssoc.getId().getObjectNumber(), new Long(hasMembersAssocBean.getId().getObjectNumber()));
	}
	
	/**
	 * Test method for {@link CtxModelBeanTranslator#fromCtxAssociationBean}
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testAssociationFromBean() throws Exception {

		final CtxAssociationBean ownsCssNodeAssocBean =
				CtxModelBeanTranslator.getInstance().fromCtxAssociation(ownsCssNodeAssoc);
		final CtxAssociation ownsCssNodeAssocCopy =
				CtxModelBeanTranslator.getInstance().fromCtxAssociationBean(ownsCssNodeAssocBean);
		assertNotNull(ownsCssNodeAssocCopy);
		assertEquals(ownsCssNodeAssoc, ownsCssNodeAssocCopy);
		assertEquals(ownsCssNodeAssoc.getId(), ownsCssNodeAssocCopy.getId());
		assertEquals(ownsCssNodeAssoc.getLastModified(), ownsCssNodeAssocCopy.getLastModified());
		assertEquals(CSS_IDENTITY_STRING, ownsCssNodeAssocCopy.getId().getOwnerId());
		assertEquals(CSS_IDENTITY_STRING, ownsCssNodeAssocCopy.getOwnerId());
		assertEquals(CtxAssociationTypes.OWNS_CSS_NODES, ownsCssNodeAssocCopy.getId().getType());
		assertEquals(CtxAssociationTypes.OWNS_CSS_NODES, ownsCssNodeAssocCopy.getType());
		assertEquals(CtxModelType.ASSOCIATION, ownsCssNodeAssocCopy.getModelType());
		assertEquals(CtxModelType.ASSOCIATION, ownsCssNodeAssocCopy.getId().getModelType());
		assertEquals(ownsCssNodeAssoc.getObjectNumber(), ownsCssNodeAssocCopy.getObjectNumber());
		assertEquals(ownsCssNodeAssoc.getId().getObjectNumber(), ownsCssNodeAssocCopy.getId().getObjectNumber());
		
		final CtxAssociationBean isMemberOfAssocBean =
				CtxModelBeanTranslator.getInstance().fromCtxAssociation(isMemberOfAssoc);
		final CtxAssociation isMemberOfAssocCopy =
				CtxModelBeanTranslator.getInstance().fromCtxAssociationBean(isMemberOfAssocBean);
		assertNotNull(isMemberOfAssocCopy);
		assertEquals(isMemberOfAssoc, isMemberOfAssocCopy);
		assertEquals(isMemberOfAssoc.getId(), isMemberOfAssocCopy.getId());
		assertEquals(isMemberOfAssoc.getLastModified(), isMemberOfAssocCopy.getLastModified());
		assertEquals(CSS_IDENTITY_STRING, isMemberOfAssocCopy.getId().getOwnerId());
		assertEquals(CSS_IDENTITY_STRING, isMemberOfAssocCopy.getOwnerId());
		assertEquals(CtxAssociationTypes.IS_MEMBER_OF, isMemberOfAssocCopy.getId().getType());
		assertEquals(CtxAssociationTypes.IS_MEMBER_OF, isMemberOfAssocCopy.getType());
		assertEquals(CtxModelType.ASSOCIATION, isMemberOfAssocCopy.getModelType());
		assertEquals(CtxModelType.ASSOCIATION, isMemberOfAssocCopy.getId().getModelType());
		assertEquals(isMemberOfAssoc.getObjectNumber(), isMemberOfAssocCopy.getObjectNumber());
		assertEquals(isMemberOfAssoc.getId().getObjectNumber(), isMemberOfAssocCopy.getId().getObjectNumber());
		
		final CtxAssociationBean hasMembersAssocBean =
				CtxModelBeanTranslator.getInstance().fromCtxAssociation(hasMembersAssoc);
		final CtxAssociation hasMembersAssocCopy =
				CtxModelBeanTranslator.getInstance().fromCtxAssociationBean(hasMembersAssocBean);
		assertNotNull(hasMembersAssocCopy);
		assertEquals(hasMembersAssoc, hasMembersAssocCopy);
		assertEquals(hasMembersAssoc.getId(), hasMembersAssocCopy.getId());
		assertEquals(hasMembersAssoc.getLastModified(), hasMembersAssocCopy.getLastModified());
		assertEquals(CIS_IDENTITY_STRING, hasMembersAssocCopy.getId().getOwnerId());
		assertEquals(CIS_IDENTITY_STRING, hasMembersAssocCopy.getOwnerId());
		assertEquals(CtxAssociationTypes.HAS_MEMBERS, hasMembersAssocCopy.getId().getType());
		assertEquals(CtxAssociationTypes.HAS_MEMBERS, hasMembersAssocCopy.getType());
		assertEquals(CtxModelType.ASSOCIATION, hasMembersAssocCopy.getModelType());
		assertEquals(CtxModelType.ASSOCIATION, hasMembersAssocCopy.getId().getModelType());
		assertEquals(hasMembersAssoc.getObjectNumber(), hasMembersAssocCopy.getObjectNumber());
		assertEquals(hasMembersAssoc.getId().getObjectNumber(), hasMembersAssocCopy.getId().getObjectNumber());
	}
	
	/**
	 * Test method for {@link CtxModelBeanTranslator#fromCtxAttribute}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testAttributeToBean() throws Exception {

		final CtxAttributeBean userLocationSymbAttrBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAttribute(userLocationSymbAttr);				
		assertNotNull(userLocationSymbAttrBean);
		assertEquals(userLocationSymbAttrBean.getLastModified(), userLocationSymbAttrBean.getLastModified());
		assertEquals(CSS_IDENTITY_STRING, userLocationSymbAttrBean.getId().getOwnerId());
		assertEquals(CtxAttributeTypes.LOCATION_SYMBOLIC, userLocationSymbAttrBean.getId().getType());
		// TODO assertEquals(CtxModelTypeBean.ATTRIBUTE, userLocationSymbAttrBean.getModelType());
		assertEquals(userLocationSymbAttr.getId().getObjectNumber(), new Long(userLocationSymbAttrBean.getId().getObjectNumber()));
		// value
		assertEquals(CtxModelBeanTranslator.NaI, userLocationSymbAttrBean.getIntegerValue());
		assertEquals(CtxModelBeanTranslator.NaD, userLocationSymbAttrBean.getDoubleValue());
		assertEquals(userLocationSymbAttr.getStringValue(), userLocationSymbAttrBean.getStringValue());
		assertEquals(CtxModelBeanTranslator.NaB, userLocationSymbAttrBean.getBinaryValue());
		// valueType
		assertEquals(CtxAttributeValueTypeBean.STRING, userLocationSymbAttrBean.getValueType());
		// valueMetric
		assertEquals(userLocationSymbAttr.getValueMetric(), userLocationSymbAttrBean.getValueMetric());
		// historyRecorded
		assertEquals(userLocationSymbAttr.isHistoryRecorded(), userLocationSymbAttrBean.isHistoryRecorded());
		// sourceId
		assertEquals(userLocationSymbAttr.getSourceId(), userLocationSymbAttrBean.getSourceId());
		// quality
		assertEquals(userLocationSymbAttr.getQuality().getLastUpdated(), userLocationSymbAttrBean.getQuality().getLastUpdated());
		assertEquals(CtxOriginTypeBean.INFERRED, userLocationSymbAttrBean.getQuality().getOriginType());
		assertEquals(userLocationSymbAttr.getQuality().getPrecision(), userLocationSymbAttrBean.getQuality().getPrecision());
		assertEquals(userLocationSymbAttr.getQuality().getUpdateFrequency(), userLocationSymbAttrBean.getQuality().getUpdateFrequency());
		
		final CtxAttributeBean userLocationCoordsAttrBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAttribute(userLocationCoordsAttr);				
		assertNotNull(userLocationCoordsAttrBean);
		assertEquals(userLocationCoordsAttrBean.getLastModified(), userLocationCoordsAttrBean.getLastModified());
		assertEquals(CSS_IDENTITY_STRING, userLocationCoordsAttrBean.getId().getOwnerId());
		assertEquals(CtxAttributeTypes.LOCATION_COORDINATES, userLocationCoordsAttrBean.getId().getType());
		// TODO assertEquals(CtxModelTypeBean.ATTRIBUTE, userLocationCoordsAttrBean.getModelType());
		assertEquals(userLocationCoordsAttr.getId().getObjectNumber(), new Long(userLocationCoordsAttrBean.getId().getObjectNumber()));
		// value
		assertEquals(CtxModelBeanTranslator.NaI, userLocationCoordsAttrBean.getIntegerValue());
		assertEquals(CtxModelBeanTranslator.NaD, userLocationCoordsAttrBean.getDoubleValue());
		assertEquals(userLocationCoordsAttr.getStringValue(), userLocationCoordsAttrBean.getStringValue());
		assertEquals(CtxModelBeanTranslator.NaB, userLocationCoordsAttrBean.getBinaryValue());
		// valueType
		assertEquals(CtxAttributeValueTypeBean.EMPTY, userLocationCoordsAttrBean.getValueType());
		// valueMetric
		assertEquals(userLocationCoordsAttr.getValueMetric(), userLocationCoordsAttrBean.getValueMetric());
		// historyRecorded
		assertEquals(userLocationCoordsAttr.isHistoryRecorded(), userLocationCoordsAttrBean.isHistoryRecorded());
		// sourceId
		assertEquals(userLocationCoordsAttr.getSourceId(), userLocationCoordsAttrBean.getSourceId());
		// quality
		assertEquals(userLocationCoordsAttr.getQuality().getLastUpdated(), userLocationCoordsAttrBean.getQuality().getLastUpdated());
		assertNotNull(userLocationCoordsAttrBean.getQuality().getOriginType());
		assertEquals(userLocationCoordsAttr.getQuality().getOriginType().name(), userLocationCoordsAttrBean.getQuality().getOriginType().name());
		assertEquals(CtxModelBeanTranslator.NaD, userLocationCoordsAttrBean.getQuality().getPrecision());
		assertEquals(CtxModelBeanTranslator.NaD, userLocationCoordsAttrBean.getQuality().getUpdateFrequency());
	
		//
		// Test valueTypes
		//
		// 1. Test integerValue
		communityNameAttr.setIntegerValue(1);
		CtxAttributeBean communityNameAttrBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAttribute(communityNameAttr);				
		assertNotNull(communityNameAttrBean);
		assertEquals(communityNameAttrBean.getLastModified(), communityNameAttrBean.getLastModified());
		assertEquals(CIS_IDENTITY_STRING, communityNameAttrBean.getId().getOwnerId());
		assertEquals(CtxAttributeTypes.NAME, communityNameAttrBean.getId().getType());
		// TODO assertEquals(CtxModelTypeBean.ATTRIBUTE, communityNameAttrBean.getModelType());
		assertEquals(communityNameAttr.getId().getObjectNumber(), new Long(communityNameAttrBean.getId().getObjectNumber()));
		// value
		assertEquals(communityNameAttr.getIntegerValue(), communityNameAttrBean.getIntegerValue());
		assertEquals(CtxModelBeanTranslator.NaD, communityNameAttrBean.getDoubleValue());
		assertEquals(communityNameAttr.getStringValue(), communityNameAttrBean.getStringValue());
		assertEquals(CtxModelBeanTranslator.NaB, communityNameAttrBean.getBinaryValue());
		// valueType
		assertEquals(CtxAttributeValueTypeBean.INTEGER, communityNameAttrBean.getValueType());
		// valueMetric
		assertEquals(communityNameAttr.getValueMetric(), communityNameAttrBean.getValueMetric());
		// historyRecorded
		assertEquals(communityNameAttr.isHistoryRecorded(), communityNameAttrBean.isHistoryRecorded());
		// sourceId
		assertEquals(communityNameAttr.getSourceId(), communityNameAttrBean.getSourceId());
		// quality
		assertEquals(communityNameAttr.getQuality().getLastUpdated(), communityNameAttrBean.getQuality().getLastUpdated());
		assertNotNull(communityNameAttrBean.getQuality().getOriginType());
		assertEquals(communityNameAttr.getQuality().getOriginType().name(), communityNameAttrBean.getQuality().getOriginType().name());
		assertEquals(CtxModelBeanTranslator.NaD, communityNameAttrBean.getQuality().getPrecision());
		assertEquals(CtxModelBeanTranslator.NaD, communityNameAttrBean.getQuality().getUpdateFrequency());
		
		// 2. Test doubleValue
		communityNameAttr.setDoubleValue(1.0d);
		communityNameAttrBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAttribute(communityNameAttr);				
		assertNotNull(communityNameAttrBean);
		assertEquals(communityNameAttrBean.getLastModified(), communityNameAttrBean.getLastModified());
		assertEquals(CIS_IDENTITY_STRING, communityNameAttrBean.getId().getOwnerId());
		assertEquals(CtxAttributeTypes.NAME, communityNameAttrBean.getId().getType());
		// TODO assertEquals(CtxModelTypeBean.ATTRIBUTE, communityNameAttrBean.getModelType());
		assertEquals(communityNameAttr.getId().getObjectNumber(), new Long(communityNameAttrBean.getId().getObjectNumber()));
		// value
		assertEquals(CtxModelBeanTranslator.NaI, communityNameAttrBean.getIntegerValue());
		assertEquals(communityNameAttr.getDoubleValue(), communityNameAttrBean.getDoubleValue());
		assertEquals(communityNameAttr.getStringValue(), communityNameAttrBean.getStringValue());
		assertEquals(CtxModelBeanTranslator.NaB, communityNameAttrBean.getBinaryValue());
		// valueType
		assertEquals(CtxAttributeValueTypeBean.DOUBLE, communityNameAttrBean.getValueType());
		// valueMetric
		assertEquals(communityNameAttr.getValueMetric(), communityNameAttrBean.getValueMetric());
		// historyRecorded
		assertEquals(communityNameAttr.isHistoryRecorded(), communityNameAttrBean.isHistoryRecorded());
		// sourceId
		assertEquals(communityNameAttr.getSourceId(), communityNameAttrBean.getSourceId());
		// quality
		assertEquals(communityNameAttr.getQuality().getLastUpdated(), communityNameAttrBean.getQuality().getLastUpdated());
		assertNotNull(communityNameAttrBean.getQuality().getOriginType());
		assertEquals(communityNameAttr.getQuality().getOriginType().name(), communityNameAttrBean.getQuality().getOriginType().name());
		assertEquals(CtxModelBeanTranslator.NaD, communityNameAttrBean.getQuality().getPrecision());
		assertEquals(CtxModelBeanTranslator.NaD, communityNameAttrBean.getQuality().getUpdateFrequency());
		
		// 3. Test binaryValue
		final MockBlobClass blob = new MockBlobClass(0);
		communityNameAttr.setBinaryValue(SerialisationHelper.serialise(blob));
		communityNameAttrBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAttribute(communityNameAttr);				
		assertNotNull(communityNameAttrBean);
		assertEquals(communityNameAttrBean.getLastModified(), communityNameAttrBean.getLastModified());
		assertEquals(CIS_IDENTITY_STRING, communityNameAttrBean.getId().getOwnerId());
		assertEquals(CtxAttributeTypes.NAME, communityNameAttrBean.getId().getType());
		// TODO assertEquals(CtxModelTypeBean.ATTRIBUTE, communityNameAttrBean.getModelType());
		assertEquals(communityNameAttr.getId().getObjectNumber(), new Long(communityNameAttrBean.getId().getObjectNumber()));
		// value
		assertEquals(CtxModelBeanTranslator.NaI, communityNameAttrBean.getIntegerValue());
		assertEquals(CtxModelBeanTranslator.NaD, communityNameAttrBean.getDoubleValue());
		assertEquals(communityNameAttr.getStringValue(), communityNameAttrBean.getStringValue());
		final byte[] blobBytes = communityNameAttr.getBinaryValue();
		assertEquals(blobBytes, communityNameAttrBean.getBinaryValue());
		final MockBlobClass blobCopy = (MockBlobClass) SerialisationHelper.deserialise(blobBytes, this.getClass().getClassLoader());
		assertEquals(blob, blobCopy);
		// valueType
		assertEquals(CtxAttributeValueTypeBean.BINARY, communityNameAttrBean.getValueType());
		// valueMetric
		assertEquals(communityNameAttr.getValueMetric(), communityNameAttrBean.getValueMetric());
		// historyRecorded
		assertEquals(communityNameAttr.isHistoryRecorded(), communityNameAttrBean.isHistoryRecorded());
		// sourceId
		assertEquals(communityNameAttr.getSourceId(), communityNameAttrBean.getSourceId());
		// quality
		assertEquals(communityNameAttr.getQuality().getLastUpdated(), communityNameAttrBean.getQuality().getLastUpdated());
		assertNotNull(communityNameAttrBean.getQuality().getOriginType());
		assertEquals(communityNameAttr.getQuality().getOriginType().name(), communityNameAttrBean.getQuality().getOriginType().name());
		assertEquals(CtxModelBeanTranslator.NaD, communityNameAttrBean.getQuality().getPrecision());
		assertEquals(CtxModelBeanTranslator.NaD, communityNameAttrBean.getQuality().getUpdateFrequency());
		
		// 4. Test complexValue
		final CtxAttributeComplexValue complexValue = new CtxAttributeComplexValue();
		complexValue.setAverage(1.0d);
		communityNameAttr.setComplexValue(complexValue);
		communityNameAttrBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAttribute(communityNameAttr);				
		assertNotNull(communityNameAttrBean);
		assertEquals(communityNameAttrBean.getLastModified(), communityNameAttrBean.getLastModified());
		assertEquals(CIS_IDENTITY_STRING, communityNameAttrBean.getId().getOwnerId());
		assertEquals(CtxAttributeTypes.NAME, communityNameAttrBean.getId().getType());
		// TODO assertEquals(CtxModelTypeBean.ATTRIBUTE, communityNameAttrBean.getModelType());
		assertEquals(communityNameAttr.getId().getObjectNumber(), new Long(communityNameAttrBean.getId().getObjectNumber()));
		// value
		assertEquals(CtxModelBeanTranslator.NaI, communityNameAttrBean.getIntegerValue());
		assertEquals(CtxModelBeanTranslator.NaD, communityNameAttrBean.getDoubleValue());
		assertEquals(communityNameAttr.getStringValue(), communityNameAttrBean.getStringValue());
		final CtxAttributeComplexValue complexValueCopy = communityNameAttr.getComplexValue();
		assertEquals(complexValue.getAverage(), complexValueCopy.getAverage());
		// valueType
		assertEquals(CtxAttributeValueTypeBean.COMPLEX, communityNameAttrBean.getValueType());
		// valueMetric
		assertEquals(communityNameAttr.getValueMetric(), communityNameAttrBean.getValueMetric());
		// historyRecorded
		assertEquals(communityNameAttr.isHistoryRecorded(), communityNameAttrBean.isHistoryRecorded());
		// sourceId
		assertEquals(communityNameAttr.getSourceId(), communityNameAttrBean.getSourceId());
		// quality
		assertEquals(communityNameAttr.getQuality().getLastUpdated(), communityNameAttrBean.getQuality().getLastUpdated());
		assertNotNull(communityNameAttrBean.getQuality().getOriginType());
		assertEquals(communityNameAttr.getQuality().getOriginType().name(), communityNameAttrBean.getQuality().getOriginType().name());
		assertEquals(CtxModelBeanTranslator.NaD, communityNameAttrBean.getQuality().getPrecision());
		assertEquals(CtxModelBeanTranslator.NaD, communityNameAttrBean.getQuality().getUpdateFrequency());
	}
	
	/**
	 * Test method for {@link CtxModelBeanTranslator#fromCtxAttributeBean}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testAttributeFromBean() throws Exception {

		final CtxAttributeBean userLocationSymbAttrBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAttribute(userLocationSymbAttr);				
		final CtxAttribute userLocationSymbAttrCopy = 
				CtxModelBeanTranslator.getInstance().fromCtxAttributeBean(userLocationSymbAttrBean);
		assertNotNull(userLocationSymbAttrCopy);
		assertEquals(userLocationSymbAttr.getLastModified(), userLocationSymbAttrCopy.getLastModified());
		assertEquals(CSS_IDENTITY_STRING, userLocationSymbAttrCopy.getOwnerId());
		assertEquals(CSS_IDENTITY_STRING, userLocationSymbAttrCopy.getId().getOwnerId());
		assertEquals(CtxAttributeTypes.LOCATION_SYMBOLIC, userLocationSymbAttrCopy.getType());
		assertEquals(CtxAttributeTypes.LOCATION_SYMBOLIC, userLocationSymbAttrCopy.getId().getType());
		assertEquals(CtxModelType.ATTRIBUTE, userLocationSymbAttrCopy.getModelType());
		assertEquals(CtxModelType.ATTRIBUTE, userLocationSymbAttrCopy.getId().getModelType());
		assertEquals(userLocationSymbAttr.getId().getObjectNumber(), new Long(userLocationSymbAttrCopy.getObjectNumber()));
		assertEquals(userLocationSymbAttr.getId().getObjectNumber(), new Long(userLocationSymbAttrCopy.getId().getObjectNumber()));
		// value
		assertNull(userLocationSymbAttrCopy.getIntegerValue());
		assertNull(userLocationSymbAttrCopy.getDoubleValue());
		assertEquals(userLocationSymbAttr.getStringValue(), userLocationSymbAttrCopy.getStringValue());
		assertNull(userLocationSymbAttrCopy.getBinaryValue());
		// valueType
		assertEquals(CtxAttributeValueType.STRING, userLocationSymbAttrCopy.getValueType());
		// valueMetric
		assertEquals(userLocationSymbAttr.getValueMetric(), userLocationSymbAttrCopy.getValueMetric());
		// historyRecorded
		assertEquals(userLocationSymbAttr.isHistoryRecorded(), userLocationSymbAttrCopy.isHistoryRecorded());
		// sourceId
		assertEquals(userLocationSymbAttr.getSourceId(), userLocationSymbAttrCopy.getSourceId());
		// quality
		assertEquals(userLocationSymbAttr.getQuality().getLastUpdated(), userLocationSymbAttrCopy.getQuality().getLastUpdated());
		assertEquals(CtxOriginType.INFERRED, userLocationSymbAttrCopy.getQuality().getOriginType());
		assertEquals(userLocationSymbAttr.getQuality().getPrecision(), userLocationSymbAttrCopy.getQuality().getPrecision());
		assertEquals(userLocationSymbAttr.getQuality().getUpdateFrequency(), userLocationSymbAttrCopy.getQuality().getUpdateFrequency());
		
		final CtxAttributeBean userLocationCoordsAttrBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAttribute(userLocationCoordsAttr);				
		final CtxAttribute userLocationCoordsAttrCopy = 
				CtxModelBeanTranslator.getInstance().fromCtxAttributeBean(userLocationCoordsAttrBean);
		assertNotNull(userLocationCoordsAttrCopy);
		assertEquals(userLocationCoordsAttr.getLastModified(), userLocationCoordsAttrCopy.getLastModified());
		assertEquals(CSS_IDENTITY_STRING, userLocationCoordsAttrCopy.getOwnerId());
		assertEquals(CSS_IDENTITY_STRING, userLocationCoordsAttrCopy.getId().getOwnerId());
		assertEquals(CtxAttributeTypes.LOCATION_COORDINATES, userLocationCoordsAttrCopy.getType());
		assertEquals(CtxAttributeTypes.LOCATION_COORDINATES, userLocationCoordsAttrCopy.getId().getType());
		assertEquals(CtxModelType.ATTRIBUTE, userLocationCoordsAttrCopy.getModelType());
		assertEquals(CtxModelType.ATTRIBUTE, userLocationCoordsAttrCopy.getId().getModelType());
		assertEquals(userLocationCoordsAttr.getId().getObjectNumber(), new Long(userLocationCoordsAttrCopy.getObjectNumber()));
		assertEquals(userLocationCoordsAttr.getId().getObjectNumber(), new Long(userLocationCoordsAttrCopy.getId().getObjectNumber()));
		// value
		assertNull(userLocationCoordsAttrCopy.getIntegerValue());
		assertNull(userLocationCoordsAttrCopy.getDoubleValue());
		assertEquals(userLocationCoordsAttr.getStringValue(), userLocationCoordsAttrCopy.getStringValue());
		assertNull(userLocationCoordsAttrCopy.getBinaryValue());
		// valueType
		assertEquals(CtxAttributeValueType.EMPTY, userLocationCoordsAttrCopy.getValueType());
		// valueMetric
		assertEquals(userLocationCoordsAttr.getValueMetric(), userLocationCoordsAttrCopy.getValueMetric());
		// historyRecorded
		assertEquals(userLocationCoordsAttr.isHistoryRecorded(), userLocationCoordsAttrCopy.isHistoryRecorded());
		// sourceId
		assertEquals(userLocationCoordsAttr.getSourceId(), userLocationCoordsAttrCopy.getSourceId());
		// quality
		assertEquals(userLocationCoordsAttr.getQuality().getLastUpdated(), userLocationCoordsAttrCopy.getQuality().getLastUpdated());
		assertEquals(userLocationCoordsAttr.getQuality().getOriginType(), userLocationCoordsAttrCopy.getQuality().getOriginType());
		assertEquals(userLocationCoordsAttr.getQuality().getPrecision(), userLocationCoordsAttrCopy.getQuality().getPrecision());
		assertEquals(userLocationCoordsAttr.getQuality().getUpdateFrequency(), userLocationCoordsAttrCopy.getQuality().getUpdateFrequency());
		
		//
		// Test valueTypes
		//
		// 1. Test integerValue
		communityNameAttr.setIntegerValue(1);
		CtxAttributeBean communityNameAttrBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAttribute(communityNameAttr);
		CtxAttribute communityNameAttrCopy = 
				CtxModelBeanTranslator.getInstance().fromCtxAttributeBean(communityNameAttrBean);
		assertNotNull(communityNameAttrCopy);
		assertEquals(communityNameAttr.getLastModified(), communityNameAttrCopy.getLastModified());
		assertEquals(CIS_IDENTITY_STRING, communityNameAttrCopy.getOwnerId());
		assertEquals(CIS_IDENTITY_STRING, communityNameAttrCopy.getId().getOwnerId());
		assertEquals(CtxAttributeTypes.NAME, communityNameAttrCopy.getType());
		assertEquals(CtxAttributeTypes.NAME, communityNameAttrCopy.getId().getType());
		assertEquals(CtxModelType.ATTRIBUTE, communityNameAttrCopy.getModelType());
		assertEquals(CtxModelType.ATTRIBUTE, communityNameAttrCopy.getId().getModelType());
		assertEquals(communityNameAttr.getId().getObjectNumber(), new Long(communityNameAttrCopy.getObjectNumber()));
		assertEquals(communityNameAttr.getId().getObjectNumber(), new Long(communityNameAttrCopy.getId().getObjectNumber()));
		// value
		assertEquals(communityNameAttr.getIntegerValue(), communityNameAttrCopy.getIntegerValue());
		assertNull(communityNameAttrCopy.getDoubleValue());
		assertNull(communityNameAttrCopy.getStringValue());
		assertNull(communityNameAttrCopy.getBinaryValue());
		assertNull(communityNameAttrCopy.getComplexValue());
		// valueType
		assertEquals(CtxAttributeValueType.INTEGER, communityNameAttrCopy.getValueType());
		// valueMetric
		assertEquals(communityNameAttr.getValueMetric(), communityNameAttrCopy.getValueMetric());
		// historyRecorded
		assertEquals(communityNameAttr.isHistoryRecorded(), communityNameAttrCopy.isHistoryRecorded());
		// sourceId
		assertEquals(communityNameAttr.getSourceId(), communityNameAttrCopy.getSourceId());
		// quality
		assertEquals(communityNameAttr.getQuality().getLastUpdated(), communityNameAttrCopy.getQuality().getLastUpdated());
		assertEquals(communityNameAttr.getQuality().getOriginType(), communityNameAttrCopy.getQuality().getOriginType());
		assertNull(communityNameAttrCopy.getQuality().getPrecision());
		assertNull(communityNameAttrCopy.getQuality().getUpdateFrequency());

		// 2. Test doubleValue
		communityNameAttr.setDoubleValue(1.0d);
		communityNameAttrBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAttribute(communityNameAttr);
		communityNameAttrCopy = 
				CtxModelBeanTranslator.getInstance().fromCtxAttributeBean(communityNameAttrBean);
		assertNotNull(communityNameAttrCopy);
		assertEquals(communityNameAttr.getLastModified(), communityNameAttrCopy.getLastModified());
		assertEquals(CIS_IDENTITY_STRING, communityNameAttrCopy.getOwnerId());
		assertEquals(CIS_IDENTITY_STRING, communityNameAttrCopy.getId().getOwnerId());
		assertEquals(CtxAttributeTypes.NAME, communityNameAttrCopy.getType());
		assertEquals(CtxAttributeTypes.NAME, communityNameAttrCopy.getId().getType());
		assertEquals(CtxModelType.ATTRIBUTE, communityNameAttrCopy.getModelType());
		assertEquals(CtxModelType.ATTRIBUTE, communityNameAttrCopy.getId().getModelType());
		assertEquals(communityNameAttr.getId().getObjectNumber(), new Long(communityNameAttrCopy.getObjectNumber()));
		assertEquals(communityNameAttr.getId().getObjectNumber(), new Long(communityNameAttrCopy.getId().getObjectNumber()));
		// value
		assertNull(communityNameAttrCopy.getIntegerValue());
		assertEquals(communityNameAttr.getDoubleValue(), communityNameAttrCopy.getDoubleValue());
		assertNull(communityNameAttrCopy.getStringValue());
		assertNull(communityNameAttrCopy.getBinaryValue());
		assertNull(communityNameAttrCopy.getComplexValue());
		// valueType
		assertEquals(CtxAttributeValueType.DOUBLE, communityNameAttrCopy.getValueType());
		// valueMetric
		assertEquals(communityNameAttr.getValueMetric(), communityNameAttrCopy.getValueMetric());
		// historyRecorded
		assertEquals(communityNameAttr.isHistoryRecorded(), communityNameAttrCopy.isHistoryRecorded());
		// sourceId
		assertEquals(communityNameAttr.getSourceId(), communityNameAttrCopy.getSourceId());
		// quality
		assertEquals(communityNameAttr.getQuality().getLastUpdated(), communityNameAttrCopy.getQuality().getLastUpdated());
		assertEquals(communityNameAttr.getQuality().getOriginType(), communityNameAttrCopy.getQuality().getOriginType());
		assertNull(communityNameAttrCopy.getQuality().getPrecision());
		assertNull(communityNameAttrCopy.getQuality().getUpdateFrequency());

		// 3. Test binaryValue
		final MockBlobClass blob = new MockBlobClass(0);
		communityNameAttr.setBinaryValue(SerialisationHelper.serialise(blob));
		communityNameAttrBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAttribute(communityNameAttr);
		communityNameAttrCopy = 
				CtxModelBeanTranslator.getInstance().fromCtxAttributeBean(communityNameAttrBean);
		assertNotNull(communityNameAttrCopy);
		assertEquals(communityNameAttr.getLastModified(), communityNameAttrCopy.getLastModified());
		assertEquals(CIS_IDENTITY_STRING, communityNameAttrCopy.getOwnerId());
		assertEquals(CIS_IDENTITY_STRING, communityNameAttrCopy.getId().getOwnerId());
		assertEquals(CtxAttributeTypes.NAME, communityNameAttrCopy.getType());
		assertEquals(CtxAttributeTypes.NAME, communityNameAttrCopy.getId().getType());
		assertEquals(CtxModelType.ATTRIBUTE, communityNameAttrCopy.getModelType());
		assertEquals(CtxModelType.ATTRIBUTE, communityNameAttrCopy.getId().getModelType());
		assertEquals(communityNameAttr.getId().getObjectNumber(), new Long(communityNameAttrCopy.getObjectNumber()));
		assertEquals(communityNameAttr.getId().getObjectNumber(), new Long(communityNameAttrCopy.getId().getObjectNumber()));
		// value
		assertNull(communityNameAttrCopy.getIntegerValue());
		assertNull(communityNameAttrCopy.getDoubleValue());
		assertNull(communityNameAttrCopy.getStringValue());
		final byte[] blobBytes = communityNameAttrCopy.getBinaryValue();
		assertEquals(communityNameAttr.getBinaryValue(), blobBytes);
		final MockBlobClass blobCopy = (MockBlobClass) SerialisationHelper.deserialise(blobBytes, this.getClass().getClassLoader());
		assertEquals(blob, blobCopy);
		assertNull(communityNameAttrCopy.getComplexValue());
		// valueType
		assertEquals(CtxAttributeValueType.BINARY, communityNameAttrCopy.getValueType());
		// valueMetric
		assertEquals(communityNameAttr.getValueMetric(), communityNameAttrCopy.getValueMetric());
		// historyRecorded
		assertEquals(communityNameAttr.isHistoryRecorded(), communityNameAttrCopy.isHistoryRecorded());
		// sourceId
		assertEquals(communityNameAttr.getSourceId(), communityNameAttrCopy.getSourceId());
		// quality
		assertEquals(communityNameAttr.getQuality().getLastUpdated(), communityNameAttrCopy.getQuality().getLastUpdated());
		assertEquals(communityNameAttr.getQuality().getOriginType(), communityNameAttrCopy.getQuality().getOriginType());
		assertNull(communityNameAttrCopy.getQuality().getPrecision());
		assertNull(communityNameAttrCopy.getQuality().getUpdateFrequency());

		// 4. Test complexValue
		final CtxAttributeComplexValue complexValue = new CtxAttributeComplexValue();
		final List<Integer> complexValueMode = new ArrayList<Integer>(); 
		complexValueMode.add(1);
		complexValue.setMode(complexValueMode);
		final Map<String,Integer> complexValuePairs = new HashMap<String,Integer>(); 
		complexValuePairs.put("foo", 1);
		complexValue.setPairs(complexValuePairs);
		communityNameAttr.setComplexValue(complexValue);
		communityNameAttrBean = 
				CtxModelBeanTranslator.getInstance().fromCtxAttribute(communityNameAttr);				
		communityNameAttrCopy = 
				CtxModelBeanTranslator.getInstance().fromCtxAttributeBean(communityNameAttrBean);
		assertNotNull(communityNameAttrCopy);
		assertEquals(communityNameAttr.getLastModified(), communityNameAttrCopy.getLastModified());
		assertEquals(CIS_IDENTITY_STRING, communityNameAttrCopy.getOwnerId());
		assertEquals(CIS_IDENTITY_STRING, communityNameAttrCopy.getId().getOwnerId());
		assertEquals(CtxAttributeTypes.NAME, communityNameAttrCopy.getType());
		assertEquals(CtxAttributeTypes.NAME, communityNameAttrCopy.getId().getType());
		assertEquals(CtxModelType.ATTRIBUTE, communityNameAttrCopy.getModelType());
		assertEquals(CtxModelType.ATTRIBUTE, communityNameAttrCopy.getId().getModelType());
		assertEquals(communityNameAttr.getId().getObjectNumber(), new Long(communityNameAttrCopy.getObjectNumber()));
		assertEquals(communityNameAttr.getId().getObjectNumber(), new Long(communityNameAttrCopy.getId().getObjectNumber()));
		// value
		assertNull(communityNameAttrCopy.getIntegerValue());
		assertNull(communityNameAttrCopy.getDoubleValue());
		assertNull(communityNameAttrCopy.getStringValue());
		final byte[] complexValueBytes = communityNameAttrCopy.getBinaryValue();
		assertEquals(communityNameAttr.getBinaryValue(), complexValueBytes);
		final CtxAttributeComplexValue complexValueCopy = (CtxAttributeComplexValue) 
				SerialisationHelper.deserialise(complexValueBytes, this.getClass().getClassLoader());
		assertEquals(complexValueMode, complexValueCopy.getMode());
		assertEquals(complexValuePairs, complexValueCopy.getPairs());
		assertNotNull(communityNameAttrCopy.getComplexValue());
		final CtxAttributeComplexValue complexValueCopy2 = communityNameAttrCopy.getComplexValue(); 
		assertEquals(complexValueMode, complexValueCopy2.getMode());
		assertEquals(complexValuePairs, complexValueCopy2.getPairs());
		// valueType
		assertEquals(CtxAttributeValueType.COMPLEX, communityNameAttrCopy.getValueType());
		// valueMetric
		assertEquals(communityNameAttr.getValueMetric(), communityNameAttrCopy.getValueMetric());
		// historyRecorded
		assertEquals(communityNameAttr.isHistoryRecorded(), communityNameAttrCopy.isHistoryRecorded());
		// sourceId
		assertEquals(communityNameAttr.getSourceId(), communityNameAttrCopy.getSourceId());
		// quality
		assertEquals(communityNameAttr.getQuality().getLastUpdated(), communityNameAttrCopy.getQuality().getLastUpdated());
		assertEquals(communityNameAttr.getQuality().getOriginType(), communityNameAttrCopy.getQuality().getOriginType());
		assertNull(communityNameAttrCopy.getQuality().getPrecision());
		assertNull(communityNameAttrCopy.getQuality().getUpdateFrequency());
	}
}