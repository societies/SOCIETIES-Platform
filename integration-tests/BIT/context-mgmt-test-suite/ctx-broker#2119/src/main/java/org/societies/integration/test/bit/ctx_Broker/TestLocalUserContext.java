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
package org.societies.integration.test.bit.ctx_Broker;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
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
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.ServiceUtils;
import org.societies.api.context.broker.CtxAccessControlException;
import org.societies.integration.test.userfeedback.UserFeedbackMockResult;
import org.societies.integration.test.userfeedback.UserFeedbackType;

/**
 * 
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 2.0
 */
public class TestLocalUserContext {

	private static Logger LOG = LoggerFactory.getLogger(TestLocalUserContext.class);

	private static final String SERVICE_ID_SUFFIX = ".societies.org";
	private static final String SERVICE_SRI = "css://requestor.societies.org/HelloWorld";

	private static final String USER_NAME_FIRST = "John";
	private static final String USER_NAME_LAST = "Do";
	private static final String USER_BIRTHDAY = "today";

	private org.societies.api.internal.context.broker.ICtxBroker internalCtxBroker;
	//private ICtxBroker ctxBroker;
	private ICommManager commMgr;

	/** The identifiers of the context model objects created in this test. */
	private Set<CtxIdentifier> testCtxIds = new HashSet<CtxIdentifier>();

	/** The CSS owner ID. */
	private IIdentity userId;

	/** The "good" 3P service ID. */
	private IIdentity allowedServiceId;

	/** The "bad" 3P service ID. */
	private IIdentity deniedServiceId;

	/** The 3P service SRI. */
	private ServiceResourceIdentifier serviceSri;

	/** The "good" 3P service requestor. */
	private RequestorService allowedRequestorService;

	/** The "bad" 3P service requestor. */
	private RequestorService deniedRequestorService;

	private CtxAttributeIdentifier userNameFirstCtxAttrId;
	private String userNameFirstCtxAttrValue;
	private CtxAttributeIdentifier userNameLastCtxAttrId;
	private String userNameLastCtxAttrValue;
	private CtxAttributeIdentifier userBirthdayCtxAttrId;
	private String userBirthdayCtxAttrValue;

	@Before
	public void setUp() throws Exception {

		this.internalCtxBroker = Test2119.getInternalCtxBroker();
		//this.ctxBroker = Test2119.getCtxBroker();
		this.commMgr = Test2119.getCommManager();

		this.userId = this.commMgr.getIdManager().fromJid(
				this.commMgr.getIdManager().getThisNetworkNode().getBareJid());
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: userId=" + this.userId);

		this.allowedServiceId = this.commMgr.getIdManager().fromJid(
				"GoodGuy-" + UUID.randomUUID().toString() + SERVICE_ID_SUFFIX);
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: allowedServiceId=" + this.allowedServiceId);

		this.deniedServiceId = this.commMgr.getIdManager().fromJid(
				"BadGuy-" + UUID.randomUUID().toString() + SERVICE_ID_SUFFIX);
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: deniedServiceId=" + this.deniedServiceId);

		this.serviceSri = new ServiceResourceIdentifier();
		this.serviceSri.setServiceInstanceIdentifier(SERVICE_SRI);
		this.serviceSri.setIdentifier(new URI(SERVICE_SRI));
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: serviceSri=" + ServiceUtils.serviceResourceIdentifierToString(this.serviceSri));

		this.allowedRequestorService = new RequestorService(allowedServiceId, serviceSri);
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: allowedRequestorService=" + this.allowedRequestorService);

		this.deniedRequestorService = new RequestorService(deniedServiceId, serviceSri);
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: deniedRequestorService=" + this.deniedRequestorService);

		final IndividualCtxEntity userCtxEnt = 
				this.internalCtxBroker.retrieveIndividualEntity(this.userId).get();

		// Create NAME_FIRST user attribute if not exists
		CtxAttribute userNameFirstCtxAttr = this.createCtxAttributeIfNotExists(
				userCtxEnt, CtxAttributeTypes.NAME_FIRST);
		if (userNameFirstCtxAttr.getStringValue() == null) {
			userNameFirstCtxAttr.setStringValue(USER_NAME_FIRST);
			this.internalCtxBroker.update(userNameFirstCtxAttr);
		}
		this.userNameFirstCtxAttrId = userNameFirstCtxAttr.getId();
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: userNameFirstCtxAttrId=" + this.userNameFirstCtxAttrId);
		this.userNameFirstCtxAttrValue = userNameFirstCtxAttr.getStringValue();
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: userNameFirstCtxAttrValue=" + this.userNameFirstCtxAttrValue);

		// Create NAME_LAST user attribute if not exists
		CtxAttribute userNameLastCtxAttr = this.createCtxAttributeIfNotExists(
				userCtxEnt, CtxAttributeTypes.NAME_LAST);
		if (userNameLastCtxAttr.getStringValue() == null) {
			userNameLastCtxAttr.setStringValue(USER_NAME_LAST);	
			this.internalCtxBroker.update(userNameLastCtxAttr);
		}
		this.userNameLastCtxAttrId = userNameLastCtxAttr.getId();
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: userNameLastCtxAttrId=" + this.userNameLastCtxAttrId);
		this.userNameLastCtxAttrValue = userNameLastCtxAttr.getStringValue();
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: userNameLastCtxAttrValue=" + this.userNameLastCtxAttrValue);

		// Create BIRTHDAY user attribute if not exists
		CtxAttribute userBirthdayCtxAttr = this.createCtxAttributeIfNotExists(
				userCtxEnt, CtxAttributeTypes.BIRTHDAY);
		if (userBirthdayCtxAttr.getStringValue() == null) {
			userBirthdayCtxAttr.setStringValue(USER_BIRTHDAY);
			this.internalCtxBroker.update(userBirthdayCtxAttr);
		}
		this.userBirthdayCtxAttrId = userBirthdayCtxAttr.getId();
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: userBirthdayCtxAttrId=" + this.userBirthdayCtxAttrId);
		this.userBirthdayCtxAttrValue = userBirthdayCtxAttr.getStringValue();
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: userBirthdayCtxAttrValue=" + this.userBirthdayCtxAttrValue);

		// Enable mock User Feedback

		if (!Test2119.getUserFeedbackMocker().isEnabled()) {
			Test2119.getUserFeedbackMocker().setEnabled(true);
		}

		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: UserFeedbackMocker.isEnabled="
					+ Test2119.getUserFeedbackMocker().isEnabled());

	}

	@After
	public void tearDown() throws Exception {

		this.userId = null;
		this.allowedServiceId = null;
		this.serviceSri = null;
		this.allowedRequestorService = null;
		this.userNameFirstCtxAttrId = null;
		this.userNameFirstCtxAttrValue = null;
		this.userNameLastCtxAttrId = null;
		this.userNameLastCtxAttrValue = null;
		this.userBirthdayCtxAttrId = null;
		this.userBirthdayCtxAttrValue = null;

		if (LOG.isInfoEnabled())
			LOG.info("tearDown: Removing '" + this.testCtxIds + "' from Context DB");
		for (final CtxIdentifier ctxId : this.testCtxIds)
			this.internalCtxBroker.remove(ctxId);

				// Disable mock User Feedback
				if (Test2119.getUserFeedbackMocker().isEnabled()) {
					Test2119.getUserFeedbackMocker().setEnabled(false);
					Test2119.getUserFeedbackMocker().removeAllReplies();
				}
				if (LOG.isInfoEnabled())
					LOG.info("*** tearDown: UserFeedbackMocker.isEnabled="
							+ Test2119.getUserFeedbackMocker().isEnabled());
	}

	@Test
	public void testRetrieveFutureAttr() throws Exception {
		
		LOG.info("*** testRetrieveFutureAttr: START");
		
		
	}
	
	@Test
	public void testRetrieveUserEntity() throws Exception {

		LOG.info("*** testRetrieveUserEntity: START");

		final CtxEntityIdentifier userCtxEntId = 
				this.internalCtxBroker.retrieveIndividualEntityId(this.allowedRequestorService, this.userId).get();
		LOG.info("*** testRetrieveUserEntity: userCtxEntId=" + userCtxEntId);
		assertNotNull(userCtxEntId);
		final IndividualCtxEntity userCtxEnt = (IndividualCtxEntity) this.internalCtxBroker.retrieve(
				this.allowedRequestorService, userCtxEntId).get();
		LOG.info("*** testRetrieveUserEntity: userCtxEntId " + userCtxEnt);
		assertNotNull(userCtxEnt);
		// Retrieve CSS owner context entity through internal Context Broker
		final IndividualCtxEntity internalUserCtxEnt = this.internalCtxBroker.retrieveIndividualEntity(this.userId).get();
		// Compare CSS owner context entity ID
		assertEquals(internalUserCtxEnt.getId(), userCtxEntId);
		assertEquals(userCtxEntId, userCtxEnt.getId());
		// Compare CSS owner context attributes
		for (final CtxAttribute internalUserCtxAttr : internalUserCtxEnt.getAttributes())
			assertTrue(userCtxEnt.getAttributes().contains(internalUserCtxAttr));
				// Compare CSS owner context associations
				for (final CtxAssociationIdentifier internalUserCtxAssoc : internalUserCtxEnt.getAssociations())
					assertTrue(userCtxEnt.getAssociations().contains(internalUserCtxAssoc));

						LOG.info("*** testRetrieveUserEntity: END");
	}

	/*
	 * lookup based on iidentity
	 */
	@Test
	public void testLookupUserAttribute() throws Exception {

		LOG.info("*** LookupUserAttribute   based on entity identifier ");
		// device 1 entity
		CtxEntity device1 = this.internalCtxBroker.createEntity(CtxEntityTypes.DEVICE).get();

		CtxAttribute address1A = this.internalCtxBroker.createAttribute(device1.getId(), CtxAttributeTypes.ADDRESS_HOME_CITY).get();
		address1A.setStringValue("address1A");
		this.internalCtxBroker.update(address1A);
		CtxAttribute address1B = this.internalCtxBroker.createAttribute(device1.getId(), CtxAttributeTypes.ADDRESS_HOME_CITY).get();
		address1B.setStringValue("address1B");
		this.internalCtxBroker.update(address1B);


		CtxAssociation assoc1 = this.internalCtxBroker.createAssociation(CtxAssociationTypes.OWNS_CSS_NODES).get();
		assoc1.setParentEntity(device1.getId());
		this.internalCtxBroker.update(assoc1);

		// device 2 entity		
		CtxEntity device2 = this.internalCtxBroker.createEntity(CtxEntityTypes.DEVICE).get();

		CtxAttribute address2A = this.internalCtxBroker.createAttribute(device2.getId(), CtxAttributeTypes.ADDRESS_HOME_CITY).get();
		address2A.setStringValue("address2A");
		this.internalCtxBroker.update(address2A);

		CtxAssociation assoc2 = this.internalCtxBroker.createAssociation(CtxAssociationTypes.OWNS_CSS_NODES).get();
		assoc2.setParentEntity(device2.getId());
		this.internalCtxBroker.update(assoc2);

		// Attribute lookups
		List<CtxIdentifier> listAttr1 = this.internalCtxBroker.lookup(device1.getId(), CtxModelType.ATTRIBUTE, CtxAttributeTypes.ADDRESS_HOME_CITY).get();
		assertEquals(2, listAttr1.size());
		if( !listAttr1.isEmpty() ){
			boolean attr1exist = false;
			boolean attr2exist = false;
			for(CtxIdentifier id : listAttr1){
				CtxAttribute attr = (CtxAttribute) this.internalCtxBroker.retrieve(id).get();
				if( attr.getStringValue().equals("address1A")) attr1exist = true;
				if( attr.getStringValue().equals("address1B")) attr2exist = true;
			}
			assertTrue(attr1exist );
			assertTrue(attr2exist );
		}

		List<CtxIdentifier> listAttr2 = this.internalCtxBroker.lookup(device2.getId(), CtxModelType.ATTRIBUTE, CtxAttributeTypes.ADDRESS_HOME_CITY).get();
		assertEquals(1, listAttr2.size());
		if( !listAttr2.isEmpty() ){
			boolean attr1exist = false;
			for(CtxIdentifier id : listAttr2){
				CtxAttribute attr = (CtxAttribute) this.internalCtxBroker.retrieve(id).get();
				if( attr.getStringValue().equals("address2A")) attr1exist = true;
			}
			assertTrue(attr1exist );
		}

		// Association lookups
		List<CtxIdentifier> listAssoc1 = this.internalCtxBroker.lookup(device2.getId(), CtxModelType.ASSOCIATION, CtxAssociationTypes.OWNS_CSS_NODES).get();
		assertEquals(1, listAssoc1.size());
		if( !listAssoc1.isEmpty() ){

			boolean assoc1exist = false;
			for(CtxIdentifier id : listAssoc1){
				CtxAssociation assoc = (CtxAssociation) this.internalCtxBroker.retrieve(id).get();
				LOG.info("*** assoc id :::: "+assoc.getId()  );
				LOG.info("*** assoc :::: "+assoc.getType()  );

				if(assoc.getType().equals(CtxAssociationTypes.OWNS_CSS_NODES)) assoc1exist = true;
				assertEquals(assoc.getParentEntity(),device2.getId() );
			}
			assertTrue(assoc1exist);
		}

		
		this.internalCtxBroker.remove(device1.getId());
		this.internalCtxBroker.remove(device2.getId());
		//this.internalCtxBroker.remove(device1);
		LOG.info("*** LookupUserAttribute   based on entity identifier  END");

		/*	
	final CtxEntityIdentifier userCtxEntId = 
				this.internalCtxBroker.retrieveIndividualEntityId(this.allowedRequestorService, this.userId).get();
		assertNotNull(userCtxEntId);
		 */



	}


	@Test
	public void testLookupUserAssociation() throws Exception {

	}

	@Test
	public void testRetrieveUserAttribute() throws Exception {

		LOG.info("*** testRetrieveUserAttribute: START");

		final CtxEntityIdentifier userCtxEntId = 
				this.internalCtxBroker.retrieveIndividualEntityId(this.allowedRequestorService, this.userId).get();
		assertNotNull(userCtxEntId);
		final List<CtxIdentifier> userBirthdayCtxAttrIds =  this.internalCtxBroker.lookup(
				this.allowedRequestorService, userCtxEntId, CtxModelType.ATTRIBUTE,
				CtxAttributeTypes.BIRTHDAY).get();
		LOG.info("*** testRetrieveUserAttribute: userBirthdayCtxAttrIds=" + userBirthdayCtxAttrIds);
		assertNotNull(userBirthdayCtxAttrIds);
		assertFalse(userBirthdayCtxAttrIds.isEmpty());
		assertEquals(1, userBirthdayCtxAttrIds.size());
		assertTrue(userBirthdayCtxAttrIds.contains(this.userBirthdayCtxAttrId));

		// Setup mock User Feedback to allow READ access *once*
		Test2119.getUserFeedbackMocker().addReply(
				UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(1, "READ"));
		final CtxAttribute userBirthdayCtxAttr = (CtxAttribute) this.internalCtxBroker.retrieve(
				this.allowedRequestorService, userBirthdayCtxAttrIds.get(0)).get();
		LOG.info("*** testRetrieveUserAttribute: userBirthdayCtxAttr=" + userBirthdayCtxAttr);
		assertNotNull(userBirthdayCtxAttr);
		assertEquals(this.userBirthdayCtxAttrId, userBirthdayCtxAttr.getId());
		assertEquals(this.userBirthdayCtxAttrValue, userBirthdayCtxAttr.getStringValue());

		// Retrieve one more time to verify granted READ permission has been stored, i.e. no User Feedback involved.
		Test2119.getUserFeedbackMocker().removeAllReplies();
		this.internalCtxBroker.retrieve(
				this.allowedRequestorService, userBirthdayCtxAttrIds.get(0)).get();

		// Setup mock User Feedback to deny READ access *once*
		Test2119.getUserFeedbackMocker().addReply(
				UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(1, "DENY"));
		boolean caughtCtxAccessControlException = false;
		try {
			this.internalCtxBroker.retrieve(this.deniedRequestorService, 
					userBirthdayCtxAttrIds.get(0)).get();
		} catch (CtxAccessControlException cace) {
			caughtCtxAccessControlException = true;
		}
		assertTrue(caughtCtxAccessControlException);

		// Retrieve one more time to verify denied READ permission has been stored, i.e. no User Feedback involved.
		Test2119.getUserFeedbackMocker().removeAllReplies();
		caughtCtxAccessControlException = false;
		try {
			this.internalCtxBroker.retrieve(this.deniedRequestorService,
					userBirthdayCtxAttrIds.get(0)).get();
		} catch (CtxAccessControlException cace) {
			caughtCtxAccessControlException = true;
		}
		assertTrue(caughtCtxAccessControlException);

		LOG.info("*** testRetrieveUserAttribute: END");
	}

	@Test
	public void testRetrieveUserAttributes() throws Exception {

		LOG.info("*** testRetrieveUserAttributes: START");

		final CtxEntityIdentifier userCtxEntId = 
				this.internalCtxBroker.retrieveIndividualEntityId(this.allowedRequestorService, this.userId).get();
		assertNotNull(userCtxEntId);
		final List<CtxIdentifier> userCtxAttrIds =  this.internalCtxBroker.lookup(
				this.allowedRequestorService, userCtxEntId, CtxModelType.ATTRIBUTE,
				CtxAttributeTypes.NAME_FIRST).get();
		LOG.info("*** testRetrieveUserAttributes: userCtxAttrIds=" + userCtxAttrIds);
		assertNotNull(userCtxAttrIds);
		assertFalse(userCtxAttrIds.isEmpty());
		assertEquals(1, userCtxAttrIds.size());
		assertTrue(userCtxAttrIds.contains(this.userNameFirstCtxAttrId));

		userCtxAttrIds.addAll(this.internalCtxBroker.lookup(
				this.allowedRequestorService, userCtxEntId, CtxModelType.ATTRIBUTE,
				CtxAttributeTypes.NAME_LAST).get());
		LOG.info("*** testRetrieveUserAttributes: userCtxAttrIds=" + userCtxAttrIds);
		assertNotNull(userCtxAttrIds);
		assertFalse(userCtxAttrIds.isEmpty());
		assertEquals(2, userCtxAttrIds.size());
		assertTrue(userCtxAttrIds.contains(this.userNameLastCtxAttrId));

		userCtxAttrIds.addAll(this.internalCtxBroker.lookup(
				this.allowedRequestorService, userCtxEntId, CtxModelType.ATTRIBUTE,
				CtxAttributeTypes.BIRTHDAY).get());
		LOG.info("*** testRetrieveUserAttributes: userCtxAttrIds=" + userCtxAttrIds);
		assertNotNull(userCtxAttrIds);
		assertFalse(userCtxAttrIds.isEmpty());
		assertEquals(3, userCtxAttrIds.size());
		assertTrue(userCtxAttrIds.contains(this.userBirthdayCtxAttrId));

		// Setup mock User Feedback to allow READ access *three* times
		// TODO This WILL break once the User Feedback supports multiple options per pop-up 
		Test2119.getUserFeedbackMocker().addReply(
				UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(3, "READ"));
		List<CtxModelObject> userCtxModelObjects = this.internalCtxBroker.retrieve(
				this.allowedRequestorService, userCtxAttrIds).get();
		LOG.info("*** testRetrieveUserAttributes: userCtxModelObjects=" + userCtxModelObjects);
		assertNotNull(userCtxModelObjects);
		assertFalse(userCtxModelObjects.isEmpty());
		assertEquals(3, userCtxModelObjects.size());
		boolean foundUserNameFirst = false;
		boolean foundUserNameLast = false;
		boolean foundUserBirthday = false;
		for (CtxModelObject userCtxModelObject : userCtxModelObjects) {
			if (this.userNameFirstCtxAttrId.equals(userCtxModelObject.getId()))
				foundUserNameFirst = true;
			else if (this.userNameLastCtxAttrId.equals(userCtxModelObject.getId()))
				foundUserNameLast = true;
			else if (this.userBirthdayCtxAttrId.equals(userCtxModelObject.getId()))
				foundUserBirthday = true;
		}
		assertTrue(foundUserNameFirst);
		assertTrue(foundUserNameLast);
		assertTrue(foundUserBirthday);

		// Retrieve one more time to verify granted READ permission has been stored, i.e. no User Feedback involved.
		Test2119.getUserFeedbackMocker().removeAllReplies();
		userCtxModelObjects = this.internalCtxBroker.retrieve(this.allowedRequestorService,
				userCtxAttrIds).get();
		assertNotNull(userCtxModelObjects);
		assertFalse(userCtxModelObjects.isEmpty());
		assertEquals(3, userCtxModelObjects.size());

		// Setup mock User Feedback to allow READ access *once* and then deny for the next *two* questions
		// This will grant READ access to BIRTHDAY but not NAME_FIRST or NAME_LAST
		Test2119.getUserFeedbackMocker().addReply(
				UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(1, "READ"));
		Test2119.getUserFeedbackMocker().addReply(
				UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(2, "DENY"));
		userCtxModelObjects = this.internalCtxBroker.retrieve(
				this.deniedRequestorService, userCtxAttrIds).get();
		LOG.info("*** testRetrieveUserAttributes: userCtxModelObjects=" + userCtxModelObjects);
		assertNotNull(userCtxModelObjects);
		assertFalse(userCtxModelObjects.isEmpty());
		assertEquals(1, userCtxModelObjects.size());
		assertEquals(this.userBirthdayCtxAttrId, userCtxModelObjects.get(0).getId());

		Test2119.getUserFeedbackMocker().removeAllReplies();

		// Test CtxAccessControlException is thrown when retrieving denied attributes only
		final List<CtxIdentifier> deniedCtxIdList = new ArrayList<CtxIdentifier>();
		deniedCtxIdList.add(this.userNameFirstCtxAttrId);
		deniedCtxIdList.add(this.userNameLastCtxAttrId);
		boolean caughtCtxAccessControlException = false;
		try {
			this.internalCtxBroker.retrieve(this.deniedRequestorService, 
					deniedCtxIdList).get();
		} catch (CtxAccessControlException cace) {
			LOG.info("*** testRetrieveUserAttributes: expectedCtxAccessControlException=" + cace.getLocalizedMessage());
			caughtCtxAccessControlException = true;
		}
		assertTrue(caughtCtxAccessControlException);

		// Test retrieving allowed attribute only
		final List<CtxIdentifier> allowedCtxIdList = new ArrayList<CtxIdentifier>();
		allowedCtxIdList.add(this.userBirthdayCtxAttrId);
		userCtxModelObjects = this.internalCtxBroker.retrieve(this.deniedRequestorService, 
				allowedCtxIdList).get();
		LOG.info("*** testRetrieveUserAttributes: userCtxModelObjects=" + userCtxModelObjects);
		assertNotNull(userCtxModelObjects);
		assertFalse(userCtxModelObjects.isEmpty());
		assertEquals(1, userCtxModelObjects.size());
		assertEquals(this.userBirthdayCtxAttrId, userCtxModelObjects.get(0).getId());

		LOG.info("*** testRetrieveUserAttributes: END");
	}

	@Test
	public void testCRUD() throws Exception {

		LOG.info("*** testCRUD: START");

		// Test creation of DEVICE entity
		final CtxEntity deviceCtxEnt = this.internalCtxBroker.createEntity(
				this.allowedRequestorService, this.userId, CtxEntityTypes.DEVICE).get();
		LOG.info("*** testCRUD: deviceCtxEnt=" + deviceCtxEnt);
		// Verify creation of DEVICE entity through create
		assertNotNull(deviceCtxEnt);
		assertNotNull(deviceCtxEnt.getId());
		assertEquals(this.userId.getBareJid(), deviceCtxEnt.getId().getOwnerId());
		assertEquals(CtxEntityTypes.DEVICE, deviceCtxEnt.getId().getType());
		assertEquals(0, deviceCtxEnt.getAttributes().size());
		assertEquals(0, deviceCtxEnt.getAssociations().size());
		// Add new Context Entity ID to list for removal
		this.testCtxIds.add(deviceCtxEnt.getId());

		// Test creation of TEMPERATURE attribute under DEVICE entity
		final CtxAttribute deviceTempCtxAttr = this.internalCtxBroker.createAttribute(
				this.allowedRequestorService, deviceCtxEnt.getId(), 
				CtxAttributeTypes.TEMPERATURE).get();
		LOG.info("*** testCRUD: deviceTempCtxAttr=" + deviceTempCtxAttr);
		// Verify creation of TEMPERATURE attribute through create result
		assertNotNull(deviceTempCtxAttr);
		assertNotNull(deviceTempCtxAttr.getId());
		assertEquals(deviceCtxEnt.getId(), deviceTempCtxAttr.getId().getScope());
		assertEquals(deviceCtxEnt.getId(), deviceTempCtxAttr.getScope());
		assertEquals(CtxAttributeTypes.TEMPERATURE, deviceTempCtxAttr.getId().getType());

		// Test search for TEMPERATURE attribute through lookup(IIdentity target, String attrType)
		List<CtxIdentifier> ctxAttrIds = this.internalCtxBroker.lookup(
				this.allowedRequestorService, this.userId,
				CtxAttributeTypes.TEMPERATURE).get();
		LOG.info("*** testCRUD: lookup(IIdentity target, String attrType)=" + ctxAttrIds);
		assertNotNull(ctxAttrIds);
		assertFalse(ctxAttrIds.isEmpty());
		assertTrue(ctxAttrIds.size() >= 1);
		assertTrue(ctxAttrIds.contains(deviceTempCtxAttr.getId()));

		// Test search for TEMPERATURE attribute through lookup(IIdentity target, CtxModelType modelType, String attrType)
		ctxAttrIds = this.internalCtxBroker.lookup(this.allowedRequestorService, 
				this.userId, CtxModelType.ATTRIBUTE, 
				CtxAttributeTypes.TEMPERATURE).get();
		LOG.info("*** testCRUD: lookup(IIdentity target, CtxModelType modelType, String attrType)=" + ctxAttrIds);
		assertNotNull(ctxAttrIds);
		assertFalse(ctxAttrIds.isEmpty());
		assertTrue(ctxAttrIds.size() >= 1);
		assertTrue(ctxAttrIds.contains(deviceTempCtxAttr.getId()));

		// Test search for TEMPERATURE attribute through lookup(CtxEntityIdentifier scope, CtxModelType modelType, String attrType)
		ctxAttrIds = this.internalCtxBroker.lookup(this.allowedRequestorService, 
				deviceCtxEnt.getId(), CtxModelType.ATTRIBUTE, 
				CtxAttributeTypes.TEMPERATURE).get();
		LOG.info("*** testCRUD: lookup(CtxEntityIdentifier scope, CtxModelType modelType, String attrType)=" + ctxAttrIds);
		assertNotNull(ctxAttrIds);
		assertFalse(ctxAttrIds.isEmpty());
		assertTrue(ctxAttrIds.size() >= 1); // TODO should be assertEquals(1, ctxAttrIds.size()); 
		assertTrue(ctxAttrIds.contains(deviceTempCtxAttr.getId()));

		// Test retrieval of TEMPERATURE attribute through retrieve(CtxIdentifier ctxId)
		// Setup mock User Feedback to allow READ access *once*
		Test2119.getUserFeedbackMocker().addReply(
				UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(1, "READ"));
		CtxAttribute deviceTempCtxAttrCopy = (CtxAttribute) this.internalCtxBroker.retrieve(
				this.allowedRequestorService, deviceTempCtxAttr.getId()).get();
		LOG.info("*** testCRUD: retrieve(CtxIdentifier ctxId)=" + deviceTempCtxAttrCopy);
		assertNotNull(deviceTempCtxAttrCopy);
		assertEquals(deviceTempCtxAttr, deviceTempCtxAttrCopy);

		// Test update of TEMPERATURE attribute through retrieve(CtxModelObject ctxModelObject)
		// Setup mock User Feedback to allow WRITE access *once*
		Test2119.getUserFeedbackMocker().addReply(
				UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(1, "WRITE"));
		deviceTempCtxAttrCopy.setStringValue("foo");
		deviceTempCtxAttrCopy = (CtxAttribute) this.internalCtxBroker.update(
				this.allowedRequestorService, deviceTempCtxAttrCopy).get();
		LOG.info("*** testCRUD: update(CtxModelObject ctxModelObject)=" + deviceTempCtxAttrCopy);
		assertNotNull(deviceTempCtxAttrCopy);
		assertEquals(deviceTempCtxAttr, deviceTempCtxAttrCopy);
		assertEquals("foo", deviceTempCtxAttrCopy.getStringValue());
		assertEquals(CtxAttributeValueType.STRING, deviceTempCtxAttrCopy.getValueType());

		// Test retrieval of TEMPERATURE attribute through retrieve(List<CtxIdentifier> ctxIdList)
		// Mock User Feedback should not be involved
		Test2119.getUserFeedbackMocker().removeAllReplies();
		final List<CtxIdentifier> ctxIdList = new ArrayList<CtxIdentifier>();
		ctxIdList.add(deviceTempCtxAttr.getId());
		final List<CtxModelObject> ctxMoList = this.internalCtxBroker.retrieve(
				this.allowedRequestorService, ctxIdList).get();
		LOG.info("*** testCRUD: retrieve(List<CtxIdentifier> ctxIdList)=" + ctxMoList);
		assertNotNull(ctxMoList);
		assertFalse(ctxMoList.isEmpty());
		assertEquals(1, ctxMoList.size());
		deviceTempCtxAttrCopy = (CtxAttribute) ctxMoList.get(0);
		assertNotNull(deviceTempCtxAttrCopy);
		assertEquals(deviceTempCtxAttr, deviceTempCtxAttrCopy);

		LOG.info("*** testCRUD: END");
	}

	private CtxAttribute createCtxAttributeIfNotExists(
			final CtxEntity scope, String attrType) throws Exception {

		final CtxAttribute result;
		if (scope.getAttributes(attrType).size() == 0) {

			// Create new attribute
			result = this.internalCtxBroker.createAttribute(scope.getId(), attrType).get();
			// Add to test context data IDs
			this.testCtxIds.add(result.getId());

		} else if (scope.getAttributes(attrType).size() == 1) {

			// Return existing attribute
			result = scope.getAttributes(attrType).iterator().next();

		} else {

			// There can only be one!
			throw new IllegalStateException("Found multiple context attributes of type '"
					+ attrType + "' under entity '" + scope + "'");
		}

		return result;
	}
}