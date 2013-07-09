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
package org.societies.integration.test.bit.ctx_3pBroker;

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
import org.societies.api.context.model.CtxAssociationIdentifier;
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
import org.societies.api.context.broker.ICtxBroker;
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
	
	private static final String USER_BIRTHDAY = "today";
	
	private org.societies.api.internal.context.broker.ICtxBroker internalCtxBroker;
	private ICtxBroker ctxBroker;
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
	
	private CtxAttributeIdentifier userBirthdayCtxAttrId;
	private String userBirthdayCtxAttrValue;

	@Before
	public void setUp() throws Exception {

		this.internalCtxBroker = Test1858.getInternalCtxBroker();
		this.ctxBroker = Test1858.getCtxBroker();
		this.commMgr = Test1858.getCommManager();
		
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
		if (!Test1858.getUserFeedbackMocker().isEnabled()) {
			Test1858.getUserFeedbackMocker().setEnabled(true);
		}
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: UserFeedbackMocker.isEnabled="
					+ Test1858.getUserFeedbackMocker().isEnabled());
	}

	@After
	public void tearDown() throws Exception {

		this.userId = null;
		this.allowedServiceId = null;
		this.serviceSri = null;
		this.allowedRequestorService = null;
		this.userBirthdayCtxAttrId = null;
		this.userBirthdayCtxAttrValue = null;
		/*this.userNameFirstCtxAttrId = null;
		this.userNameFirstCtxAttrValue = null;
		this.userNameLastCtxAttrId = null;
		this.userNameLastCtxAttrValue = null;*/
		
		if (LOG.isInfoEnabled())
			LOG.info("tearDown: Removing '" + this.testCtxIds + "' from Context DB");
		for (final CtxIdentifier ctxId : this.testCtxIds)
			this.internalCtxBroker.remove(ctxId);
		
		// Disable mock User Feedback
		if (Test1858.getUserFeedbackMocker().isEnabled()) {
			Test1858.getUserFeedbackMocker().setEnabled(false);
			Test1858.getUserFeedbackMocker().removeAllReplies();
		}
		if (LOG.isInfoEnabled())
			LOG.info("*** tearDown: UserFeedbackMocker.isEnabled="
					+ Test1858.getUserFeedbackMocker().isEnabled());
	}
	
	@Test
	public void testRetrieveUserEntity() throws Exception {

		LOG.info("*** testRetrieveUserEntity: START");
		
		final CtxEntityIdentifier userCtxEntId = 
					this.ctxBroker.retrieveIndividualEntityId(this.allowedRequestorService, this.userId).get();
		LOG.info("*** testRetrieveUserEntity: userCtxEntId=" + userCtxEntId);
		assertNotNull(userCtxEntId);
		final IndividualCtxEntity userCtxEnt = (IndividualCtxEntity) this.ctxBroker.retrieve(
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
	
	@Test
	public void testRetrieveUserAttribute() throws Exception {

		LOG.info("*** testRetrieveUserAttribute: START");
		
		final CtxEntityIdentifier userCtxEntId = 
				this.ctxBroker.retrieveIndividualEntityId(this.allowedRequestorService, this.userId).get();
		assertNotNull(userCtxEntId);
		final List<CtxIdentifier> userBirthdayCtxAttrIds =  this.ctxBroker.lookup(
				this.allowedRequestorService, userCtxEntId, CtxModelType.ATTRIBUTE,
				CtxAttributeTypes.BIRTHDAY).get();
		LOG.info("*** testRetrieveUserAttribute: userBirthdayCtxAttrIds=" + userBirthdayCtxAttrIds);
		assertNotNull(userBirthdayCtxAttrIds);
		assertFalse(userBirthdayCtxAttrIds.isEmpty());
		assertEquals(1, userBirthdayCtxAttrIds.size());
		assertTrue(userBirthdayCtxAttrIds.contains(this.userBirthdayCtxAttrId));

		// Setup mock User Feedback to allow READ access *once*
		Test1858.getUserFeedbackMocker().addReply(
				UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(1, "READ"));
		final CtxAttribute userBirthdayCtxAttr = (CtxAttribute) this.ctxBroker.retrieve(
				this.allowedRequestorService, userBirthdayCtxAttrIds.get(0)).get();
		LOG.info("*** testRetrieveUserAttribute: userBirthdayCtxAttr=" + userBirthdayCtxAttr);
		assertNotNull(userBirthdayCtxAttr);
		assertEquals(this.userBirthdayCtxAttrId, userBirthdayCtxAttr.getId());
		assertEquals(this.userBirthdayCtxAttrValue, userBirthdayCtxAttr.getStringValue());
		
		// Retrieve one more time to verify granted READ permission has been stored, i.e. no User Feedback involved.
		Test1858.getUserFeedbackMocker().removeAllReplies();
		this.ctxBroker.retrieve(
				this.allowedRequestorService, userBirthdayCtxAttrIds.get(0)).get();
		
		// Setup mock User Feedback to deny READ access *once*
		Test1858.getUserFeedbackMocker().addReply(
				UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(1, "DENY"));
		boolean caughtCtxAccessControlException = false;
		try {
			this.ctxBroker.retrieve(this.deniedRequestorService, 
					userBirthdayCtxAttrIds.get(0)).get();
		} catch (CtxAccessControlException cace) {
			caughtCtxAccessControlException = true;
		}
		assertTrue(caughtCtxAccessControlException);
		
		// Retrieve one more time to verify denied READ permission has been stored, i.e. no User Feedback involved.
		Test1858.getUserFeedbackMocker().removeAllReplies();
		caughtCtxAccessControlException = false;
		try {
			this.ctxBroker.retrieve(this.deniedRequestorService,
					userBirthdayCtxAttrIds.get(0)).get();
		} catch (CtxAccessControlException cace) {
			caughtCtxAccessControlException = true;
		}
		assertTrue(caughtCtxAccessControlException);
		
		LOG.info("*** testRetrieveUserAttribute: END");
	}

	@Test
	public void testCRUD() throws Exception {
		
		LOG.info("*** testCRUD: START");

		// Test creation of DEVICE entity
		final CtxEntity deviceCtxEnt = this.ctxBroker.createEntity(
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
		
		// Test creation of LOCATION_COORDINATES attribute under DEVICE entity
		final CtxAttribute deviceCoordsCtxAttr = this.ctxBroker.createAttribute(
				this.allowedRequestorService, deviceCtxEnt.getId(), 
				CtxAttributeTypes.LOCATION_COORDINATES).get();
		LOG.info("*** testCRUD: deviceCoordsCtxAttr=" + deviceCoordsCtxAttr);
		// Verify creation of LOCATION_COORDINATES attribute through create result
		assertNotNull(deviceCoordsCtxAttr);
		assertNotNull(deviceCoordsCtxAttr.getId());
		assertEquals(deviceCtxEnt.getId(), deviceCoordsCtxAttr.getId().getScope());
		assertEquals(deviceCtxEnt.getId(), deviceCoordsCtxAttr.getScope());
		assertEquals(CtxAttributeTypes.LOCATION_COORDINATES, deviceCoordsCtxAttr.getId().getType());
		
		// Test search for LOCATION_COORDINATES attribute through lookup(IIdentity target, String attrType)
		List<CtxIdentifier> ctxAttrIds = this.ctxBroker.lookup(
				this.allowedRequestorService, this.userId,
				CtxAttributeTypes.LOCATION_COORDINATES).get();
		LOG.info("*** testCRUD: lookup(IIdentity target, String attrType)=" + ctxAttrIds);
		assertNotNull(ctxAttrIds);
		assertFalse(ctxAttrIds.isEmpty());
		assertTrue(ctxAttrIds.size() >= 1);
		assertTrue(ctxAttrIds.contains(deviceCoordsCtxAttr.getId()));
		
		// Test search for LOCATION_COORDINATES attribute through lookup(IIdentity target, CtxModelType modelType, String attrType)
		ctxAttrIds = this.ctxBroker.lookup(this.allowedRequestorService, 
				this.userId, CtxModelType.ATTRIBUTE, 
				CtxAttributeTypes.LOCATION_COORDINATES).get();
		LOG.info("*** testCRUD: lookup(IIdentity target, CtxModelType modelType, String attrType)=" + ctxAttrIds);
		assertNotNull(ctxAttrIds);
		assertFalse(ctxAttrIds.isEmpty());
		assertTrue(ctxAttrIds.size() >= 1);
		assertTrue(ctxAttrIds.contains(deviceCoordsCtxAttr.getId()));
		
		// Test search for LOCATION_COORDINATES attribute through lookup(CtxEntityIdentifier scope, CtxModelType modelType, String attrType)
		ctxAttrIds = this.ctxBroker.lookup(this.allowedRequestorService, 
				deviceCtxEnt.getId(), CtxModelType.ATTRIBUTE, 
				CtxAttributeTypes.LOCATION_COORDINATES).get();
		LOG.info("*** testCRUD: lookup(CtxEntityIdentifier scope, CtxModelType modelType, String attrType)=" + ctxAttrIds);
		assertNotNull(ctxAttrIds);
		assertFalse(ctxAttrIds.isEmpty());
		assertTrue(ctxAttrIds.size() >= 1); // TODO should be assertEquals(1, ctxAttrIds.size()); 
		assertTrue(ctxAttrIds.contains(deviceCoordsCtxAttr.getId()));
		
		// Test retrieval of LOCATION_COORDINATES attribute through retrieve(CtxIdentifier ctxId)
		// Setup mock User Feedback to allow READ access *once*
		Test1858.getUserFeedbackMocker().addReply(
				UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(1, "READ"));
		CtxAttribute deviceCoordsCtxAttrCopy = (CtxAttribute) this.ctxBroker.retrieve(
				this.allowedRequestorService, deviceCoordsCtxAttr.getId()).get();
		LOG.info("*** testCRUD: retrieve(CtxIdentifier ctxId)=" + deviceCoordsCtxAttrCopy);
		assertNotNull(deviceCoordsCtxAttrCopy);
		assertEquals(deviceCoordsCtxAttr, deviceCoordsCtxAttrCopy);
		
		// Test update of LOCATION_COORDINATES attribute through retrieve(CtxModelObject ctxModelObject)
		// Setup mock User Feedback to allow WRITE access *once*
		Test1858.getUserFeedbackMocker().addReply(
				UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(1, "WRITE"));
		deviceCoordsCtxAttrCopy.setStringValue("foo");
		deviceCoordsCtxAttrCopy = (CtxAttribute) this.ctxBroker.update(
				this.allowedRequestorService, deviceCoordsCtxAttrCopy).get();
		LOG.info("*** testCRUD: update(CtxModelObject ctxModelObject)=" + deviceCoordsCtxAttrCopy);
		assertNotNull(deviceCoordsCtxAttrCopy);
		assertEquals(deviceCoordsCtxAttr, deviceCoordsCtxAttrCopy);
		assertEquals("foo", deviceCoordsCtxAttrCopy.getStringValue());
		assertEquals(CtxAttributeValueType.STRING, deviceCoordsCtxAttrCopy.getValueType());
		
		// Test retrieval of LOCATION_COORDINATES attribute through retrieve(List<CtxIdentifier> ctxIdList)
		// Mock User Feedback should not be involved
		Test1858.getUserFeedbackMocker().removeAllReplies();
		final List<CtxIdentifier> ctxIdList = new ArrayList<CtxIdentifier>();
		ctxIdList.add(deviceCoordsCtxAttr.getId());
		final List<CtxModelObject> ctxMoList = this.ctxBroker.retrieve(
				this.allowedRequestorService, ctxIdList).get();
		LOG.info("*** testCRUD: retrieve(List<CtxIdentifier> ctxIdList)=" + ctxMoList);
		assertNotNull(ctxMoList);
		assertFalse(ctxMoList.isEmpty());
		assertEquals(1, ctxMoList.size());
		deviceCoordsCtxAttrCopy = (CtxAttribute) ctxMoList.get(0);
		assertNotNull(deviceCoordsCtxAttrCopy);
		assertEquals(deviceCoordsCtxAttr, deviceCoordsCtxAttrCopy);
		
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