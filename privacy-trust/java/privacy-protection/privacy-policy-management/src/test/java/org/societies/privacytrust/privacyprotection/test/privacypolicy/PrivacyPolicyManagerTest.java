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
package org.societies.privacytrust.privacyprotection.test.privacypolicy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.PrivacyPolicyUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.ServiceUtils;
import org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager;
import org.societies.util.commonmock.MockIdentity;
import org.societies.util.commonmock.MockNetworkNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test list:
 * - get a not existing cis privacy policy
 * - add, get and delete a cis privacy policy
 * - get a not existing service privacy policy
 * - add, get and delete a service privacy policy
 * - update two times the same cis privacy policy
 * - update two times a cis privacy policy (but the second time it is transform into a service privacy policy)
 * - delete a not existing cis privacy policy
 * - delete a not existing service privacy policy
 * - delete a cis privacy policy
 * - delete a service privacy policy
 * - equality between two RequestPolicy
 * 
 * @author Olivier Maridat (Trialog)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration//(locations = { "PrivacyPolicyManagerTest-context.xml" })
public class PrivacyPolicyManagerTest extends AbstractJUnit4SpringContextTests {
	private static final Logger LOG = LoggerFactory.getLogger(PrivacyPolicyManagerTest.class.getName());

	@Autowired
	private IPrivacyPolicyManager privacyPolicyManager;

	private ICtxBroker ctxBroker;
	private RequestorCisBean requestorCis;
	private RequestorServiceBean requestorService;
	private RequestPolicy cisPolicy;
	private RequestPolicy servicePolicy;
	private CtxEntity personEntity;
	private IIdentity mockId;
	private CtxAssociation hasPrivacyPolicies;
	private CtxEntity policyEntity;
	private CtxAttribute cisPolicyAttribute;
	private CtxAttribute servicePolicyAttribute;
	private CtxAttribute registryAttribute;

	@Before
	public void setUp() throws Exception {
		// Data
		requestorCis = getRequestorCis();
		requestorService = getRequestorService();
		cisPolicy = getRequestPolicy(requestorCis);
		servicePolicy = getRequestPolicy(requestorService);
		createPersonEntity();

		// Comm Manager
		ICommManager commManager = Mockito.mock(ICommManager.class);
		IIdentityManager idManager = Mockito.mock(IIdentityManager.class);
		INetworkNode myCssId = new MockNetworkNode(IdentityType.CSS, "me","ict-societies.eu", "RICH");
		IIdentity otherCssId = new MockIdentity(IdentityType.CSS, "othercss","ict-societies.eu");
		IIdentity cisId = new MockIdentity(IdentityType.CIS, "cis-one", "ict-societies.eu");
		Mockito.when(idManager.getThisNetworkNode()).thenReturn(myCssId);
		Mockito.when(idManager.fromJid(otherCssId.getJid())).thenReturn(otherCssId);
		Mockito.when(idManager.fromJid(cisId.getJid())).thenReturn(cisId);
		Mockito.when(idManager.fromJid("cis-one.ict-societies.eu")).thenReturn(new MockIdentity("cis-one.ict-societies.eu"));
		Mockito.when(idManager.fromJid("cis-one.ict-societies.eu")).thenReturn(new MockIdentity("cis-one.ict-societies.eu"));
		Mockito.when(idManager.fromJid("othercss.ict-societies.eu")).thenReturn(new MockIdentity("othercss.ict-societies.eu"));
		Mockito.when(idManager.fromJid("red.ict-societies.eu")).thenReturn(new MockIdentity("red.ict-societies.eu"));
		Mockito.when(idManager.fromJid("eliza.ict-societies.eu")).thenReturn(new MockIdentity("eliza.ict-societies.eu"));
		Mockito.when(commManager.getIdManager()).thenReturn(idManager);

		// CtxBorker
		ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
		Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, org.societies.api.internal.context.model.CtxAttributeTypes.PRIVACY_POLICY_REGISTRY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
		Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, "policyOf"+RequestorUtils.toUriString(requestorCis))).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
		Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, "policyOf"+RequestorUtils.toUriString(requestorService))).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
		Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PRIVACY_POLICY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
		IndividualCtxEntity weirdPerson = new IndividualCtxEntity(personEntity.getId());
		Mockito.when(ctxBroker.retrieveIndividualEntity(myCssId)).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
		Mockito.when(ctxBroker.createAssociation(CtxAssociationTypes.HAS_PRIVACY_POLICIES)).thenReturn(new AsyncResult<CtxAssociation>(hasPrivacyPolicies));
		Mockito.when(ctxBroker.createEntity(CtxEntityTypes.PRIVACY_POLICY)).thenReturn(new AsyncResult<CtxEntity>(policyEntity));
		Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) policyEntity.getId(), "policyOf"+RequestorUtils.toUriString(requestorCis))).thenReturn(new AsyncResult<CtxAttribute>(cisPolicyAttribute));
		Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) policyEntity.getId(), "policyOf"+RequestorUtils.toUriString(requestorService))).thenReturn(new AsyncResult<CtxAttribute>(servicePolicyAttribute));
		Mockito.when(ctxBroker.createAttribute(personEntity.getId(), org.societies.api.internal.context.model.CtxAttributeTypes.PRIVACY_POLICY_REGISTRY)).thenReturn(new AsyncResult<CtxAttribute>(registryAttribute));
		Mockito.when(ctxBroker.retrieve(cisPolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(cisPolicyAttribute));
		Mockito.when(ctxBroker.retrieve(servicePolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(servicePolicyAttribute));
		Mockito.when(ctxBroker.remove(cisPolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(cisPolicyAttribute));
		Mockito.when(ctxBroker.remove(servicePolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(servicePolicyAttribute));

		// Privacy Policy Manager
		((PrivacyPolicyManager) privacyPolicyManager).setCtxBroker(ctxBroker);
		((PrivacyPolicyManager) privacyPolicyManager).setCommManager(commManager);
		((PrivacyPolicyManager) privacyPolicyManager).init();
	}


	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetCisPrivacyPolicyNonExisting() {
		LOG.info("[[TEST]] testGetCisPrivacyPolicyNonExisting: retrieve a non-existing privacy policy");
		RequestPolicy expectedPrivacyPolicy = null;
		RequestPolicy privacyPolicy = null;
		try {
			privacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] testGetCisPrivacyPolicyNonExisting: retrieve a non-existing privacy policy", e);
			fail("[Error testGetCisPrivacyPolicyNonExisting] Privacy error");
		} catch (Exception e) {
			fail("[Error testGetCisPrivacyPolicyNonExisting] error");
		}
		assertTrue("Expected null privacy policy, but it is not.", RequestPolicyUtils.equal(privacyPolicy, expectedPrivacyPolicy));
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetCisPrivacyPolicy() {
		LOG.info("[[TEST]] testGetCisPrivacyPolicy: add and retrieve a privacy policy");
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy retrievedPrivacyPolicy = null;
		boolean deleteResult = false;
		try {
			addedPrivacyPolicy = privacyPolicyManager.updatePrivacyPolicy(cisPolicy);
			retrievedPrivacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestorCis);
			deleteResult = privacyPolicyManager.deletePrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] testGetCisPrivacyPolicy: add and retrieve a privacy policy", e);
			fail("[Error testGetCisPrivacyPolicy] Privacy error");
		} catch (Exception e) {
			LOG.error("[Test PrivacyException] testGetCisPrivacyPolicy: add and retrieve a privacy policy", e);
			e.printStackTrace();
			fail("[Error testDeletePrivacyPolicy] error");
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", retrievedPrivacyPolicy);
		LOG.info("Added privacy policy: "+addedPrivacyPolicy.toString());
		LOG.info("Retrieved privacy policy: "+retrievedPrivacyPolicy.toString());
		assertTrue("Expected a privacy policy, but it was not the good one.", RequestPolicyUtils.equal(retrievedPrivacyPolicy, addedPrivacyPolicy));
		assertTrue("Privacy policy not deleted.", deleteResult);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetServicePrivacyPolicyNonExisting() {
		LOG.info("[[TEST]] testGetServicePrivacyPolicyNonExisting: retrieve a non-existing privacy policy");
		RequestPolicy expectedPrivacyPolicy = null;
		RequestPolicy privacyPolicy = null;
		try {
			privacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestorService);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] testGetServicePrivacyPolicyNonExisting: retrieve a non-existing privacy policy", e);
			fail("[Error testGetServicePrivacyPolicyNonExisting] Privacy error");
		} catch (Exception e) {
			fail("[Error testDeletePrivacyPolicy] error");
		}
		assertTrue("Expected null privacy policy, but it is not.", RequestPolicyUtils.equal(privacyPolicy, expectedPrivacyPolicy));
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetServicePrivacyPolicy() {
		LOG.info("[[TEST]] testGetServicePrivacyPolicy: add and retrieve a privacy policy");
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy retrievedPrivacyPolicy = null;
		boolean deleteResult = false;
		try {
			addedPrivacyPolicy = privacyPolicyManager.updatePrivacyPolicy(servicePolicy);
			retrievedPrivacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestorService);
			deleteResult = privacyPolicyManager.deletePrivacyPolicy(requestorService);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] testGetServicePrivacyPolicy: add and retrieve a privacy policy", e);
			fail("[Error testGetServicePrivacyPolicy] Privacy error");
		} catch (Exception e) {
			e.printStackTrace();
			fail("[Error testDeletePrivacyPolicy] error");
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", retrievedPrivacyPolicy);
		assertTrue("Expected a privacy policy, but it what not the good one.", RequestPolicyUtils.equal(retrievedPrivacyPolicy, addedPrivacyPolicy));
		assertTrue("Privacy policy not deleted.", deleteResult);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy)}.
	 */
	@Test
	public void testUpdatesCisPrivacyPolicy() {
		LOG.info("[[TEST]] testUpdatesCisPrivacyPolicy: update the same privacy policy");
		RequestPolicy privacyPolicy1 = null;
		RequestPolicy privacyPolicy2 = null;
		try {
			privacyPolicy1 = privacyPolicyManager.updatePrivacyPolicy(cisPolicy);
			privacyPolicy2 = privacyPolicyManager.updatePrivacyPolicy(cisPolicy);
		} catch (PrivacyException e) {
			fail("[Error testUpdatePrivacyPolicy] Privacy error");
		} catch (Exception e) {
			fail("[Error testDeletePrivacyPolicy] error");
		}
		assertTrue("Privacy policy not created", RequestPolicyUtils.equal(cisPolicy, privacyPolicy1));
		assertTrue("Privacy policy not updated", RequestPolicyUtils.equal(cisPolicy, privacyPolicy2));
		assertTrue("Difference between same privacy policies", RequestPolicyUtils.equal(privacyPolicy1, privacyPolicy2));
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy)}.
	 */
	@Test
	public void testUpdatesCisPrivacyPolicies() {
		LOG.info("[[TEST]] testUpdatesCisPrivacyPolicies: update the same privacy policy");
		RequestPolicy privacyPolicy1 = null;
		RequestPolicy privacyPolicy2 = null;
		RequestPolicy servicePolicy2 = RequestPolicyUtils.create(requestorService, cisPolicy.getRequestItems());
		try {
			privacyPolicy1 = privacyPolicyManager.updatePrivacyPolicy(cisPolicy);
			privacyPolicy2 = privacyPolicyManager.updatePrivacyPolicy(servicePolicy2);
		} catch (PrivacyException e) {
			fail("[Error testUpdatePrivacyPolicy] Privacy error");
		} catch (Exception e) {
			fail("[Error testDeletePrivacyPolicy] error");
		}
		assertTrue("Privacy policy not created", RequestPolicyUtils.equal(cisPolicy, privacyPolicy1));
		assertTrue("Privacy policy not updated", RequestPolicyUtils.equal(servicePolicy2, privacyPolicy2));
		assertFalse("Same privacy policies but it should not 1", RequestPolicyUtils.equal(privacyPolicy1, privacyPolicy2));
		assertFalse("Same privacy policies but it should not 2", privacyPolicy1.equals(privacyPolicy2));
	}


	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#deletePrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testDeleteServicePrivacyPolicyNotExisting() {
		LOG.info("[[TEST]] testDeleteServicePrivacyPolicyNotExisting: delete a non-existing privacy policy");
		RequestPolicy privacyPolicy = null;
		boolean deleteResult = false;
		try {
			privacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestorService);
			deleteResult = privacyPolicyManager.deletePrivacyPolicy(requestorService);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] testDeleteServicePrivacyPolicyNotExisting: delete a non-existing privacy policy", e);
			fail("[Error testDeletePrivacyPolicyNotExisting] Privacy error");
		} catch (Exception e) {
			fail("[Error testDeletePrivacyPolicy] error");
		}
		assertNull("This privacy policy exists!", privacyPolicy);
		assertTrue("Privacy policy not deleted.", deleteResult);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#deletePrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testDeleteCisPrivacyPolicyNotExisting() {
		LOG.info("[[TEST]] testDeleteCisPrivacyPolicyNotExisting: delete a non-existing privacy policy");
		RequestPolicy privacyPolicy = null;
		boolean deleteResult = false;
		try {
			privacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestorCis);
			deleteResult = privacyPolicyManager.deletePrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] testDeleteCisPrivacyPolicyNotExisting: delete a non-existing privacy policy", e);
			fail("[Error testDeletePrivacyPolicyNotExisting] Privacy error");
		} catch (Exception e) {
			fail("[Error testDeletePrivacyPolicy] error");
		}
		assertNull("This privacy policy exists!", privacyPolicy);
		assertTrue("Privacy policy not deleted.", deleteResult);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#deletePrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testDeleteServicePrivacyPolicy() {
		LOG.info("[[TEST]] testDeletePrivacyPolicy: add and retrieve and delete a privacy policy");
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy privacyPolicyBefore = null;
		RequestPolicy privacyPolicyAfter = null;
		boolean deleteResult = false;
		try {
			addedPrivacyPolicy = privacyPolicyManager.updatePrivacyPolicy(servicePolicy);
			privacyPolicyBefore = privacyPolicyManager.getPrivacyPolicy(requestorService);
			deleteResult = privacyPolicyManager.deletePrivacyPolicy(requestorService);
			privacyPolicyAfter = privacyPolicyManager.getPrivacyPolicy(requestorService);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] testDeleteServicePrivacyPolicy: add and retrieve and delete a privacy policy", e);
			fail("[Error testDeletePrivacyPolicy] Privacy error");
		} catch (Exception e) {
			fail("[Error testDeletePrivacyPolicy] error");
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", privacyPolicyBefore);
		assertTrue("Expected a privacy policy, but it what not the good one.", RequestPolicyUtils.equal(privacyPolicyBefore, addedPrivacyPolicy));
		assertTrue("Privacy policy not deleted.", deleteResult);
		assertNull("Privacy policy not really deleted.", privacyPolicyAfter);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#deletePrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testDeleteCisPrivacyPolicy() {
		LOG.info("[[TEST]] testDeleteCisPrivacyPolicy: add and retrieve and delete a privacy policy");
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy privacyPolicyBefore = null;
		RequestPolicy privacyPolicyAfter = null;
		boolean deleteResult = false;
		try {
			addedPrivacyPolicy = privacyPolicyManager.updatePrivacyPolicy(cisPolicy);
			privacyPolicyBefore = privacyPolicyManager.getPrivacyPolicy(requestorCis);
			deleteResult = privacyPolicyManager.deletePrivacyPolicy(requestorCis);
			privacyPolicyAfter = privacyPolicyManager.getPrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] testDeleteCisPrivacyPolicy: add and retrieve and delete a privacy policy", e);
			fail("[Error testDeletePrivacyPolicy] Privacy error");
		} catch (Exception e) {
			fail("[Error testDeletePrivacyPolicy] error");
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", privacyPolicyBefore);
		assertTrue("Expected a privacy policy, but it what not the good one.", RequestPolicyUtils.equal(privacyPolicyBefore, addedPrivacyPolicy));
		assertTrue("Privacy policy not deleted.", deleteResult);
		assertNull("Privacy policy not really deleted.", privacyPolicyAfter);
	}

	@Test
	public void testDataIdentifierSchemeEquals() {
		LOG.info("[[TEST]] testDataIdentifierSchemeEquals");
		assertEquals("Expected equals", DataIdentifierScheme.CONTEXT, DataIdentifierScheme.CONTEXT);
		assertTrue("Expected equals", DataIdentifierScheme.CONTEXT.equals(DataIdentifierScheme.CONTEXT));
	}

	@Test
	public void testRequestPolicyEquals() {
		LOG.info("[[TEST]] testRequestPolicyEquals");
		List<RequestItem> requestItems2 = new ArrayList<RequestItem>();
		Resource resource2 = ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Action> actions2 = new ArrayList<Action>();
		actions2.add(ActionUtils.create(ActionConstants.READ));
		RequestItem requestItem2 = RequestItemUtils.create(resource2, actions2, new ArrayList<Condition>());
		requestItems2.add(requestItem2);
		RequestPolicy actual = RequestPolicyUtils.create(requestorCis, requestItems2);

		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		Resource resource = ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Action> actions = new ArrayList<Action>();
		actions.add(ActionUtils.create(ActionConstants.READ));
		RequestItem requestItem = RequestItemUtils.create(resource, actions, new ArrayList<Condition>());
		requestItems.add(requestItem);
		RequestPolicy expected = RequestPolicyUtils.create(requestorCis, requestItems);

		assertEquals("Expected equals XML string", RequestPolicyUtils.toXmlString(expected), RequestPolicyUtils.toXmlString(actual));
		assertTrue("Expected equals", RequestPolicyUtils.equal(expected, actual));
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testUpdatePrivacyPolicyFromXml() {
		String testTitle = "testUpdatePrivacyPolicyFromXml: add and retrieve a privacy policy created from a XML string";
		LOG.info("[[TEST]] "+testTitle);
		String privacyPolicy = "<RequestPolicy>" +
				"<Target>" +
				"<Resource>" +
				"<Attribute AttributeId=\""+DataIdentifierScheme.CONTEXT+"\" DataType=\"http://www.w3.org/2001/XMLSchema#string\">" +
				"<AttributeValue>fdsfsf</AttributeValue>" +
				"</Attribute>" +
				"</Resource>" +
				"<Action>" +
				"<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants\">" +
				"<AttributeValue>WRITE</AttributeValue>" +
				"</Attribute>" +
				"<optional>false</optional>" +
				"</Action>" +
				"<Condition>" +
				"<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\" DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">" +
				"<AttributeValue DataType=\"SHARE_WITH_3RD_PARTIES\">dfsdf</AttributeValue>" +
				"</Attribute>" +
				"<optional>true</optional>" +
				"</Condition>" +
				"<Condition>" +
				"<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\" DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">" +
				"<AttributeValue DataType=\"DATA_RETENTION_IN_MINUTES\">412</AttributeValue>" +
				"</Attribute>" +
				"<optional>true</optional>" +
				"</Condition>" +
				"<optional>false</optional>" +
				"</Target>" +
				"</RequestPolicy>";
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy readPrivacyPolicy = null;
		boolean deleteResult = false;
		try {
			addedPrivacyPolicy = privacyPolicyManager.updatePrivacyPolicy(privacyPolicy, requestorCis);
			readPrivacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestorCis);
			deleteResult = privacyPolicyManager.deletePrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("Privacy error "+e.getLocalizedMessage()+": "+testTitle);
		} catch (Exception e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("Error "+e.getLocalizedMessage()+": "+testTitle);
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", readPrivacyPolicy);
		LOG.info(" *** Original Privacy Policy: \n"+privacyPolicy);
		LOG.info(" *** Added Privacy Policy: \n"+addedPrivacyPolicy.toString());
		LOG.info(" *** Retrieved Privacy Policy: \n"+readPrivacyPolicy.toString());
		assertTrue("Expected a privacy policy, but it what not the good one.", RequestPolicyUtils.equal(readPrivacyPolicy, addedPrivacyPolicy));
		assertTrue("Privacy policy not deleted.", deleteResult);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testUpdatePrivacyPolicyFromEmptyXml() {
		String testTitle = "testUpdatePrivacyPolicyFromEmptyXml: add and retrieve a privacy policy created from a XML string representing an empty privacy policy";
		LOG.info("[[TEST]] "+testTitle);
		String privacyPolicy = "<RequestPolicy></RequestPolicy>";
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy readPrivacyPolicy = null;
		boolean deleteResult = false;
		try {
			addedPrivacyPolicy = privacyPolicyManager.updatePrivacyPolicy(privacyPolicy, requestorCis);
			readPrivacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestorCis);
			deleteResult = privacyPolicyManager.deletePrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("Privacy error "+e.getLocalizedMessage()+": "+testTitle);
		} catch (Exception e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("Error "+e.getLocalizedMessage()+": "+testTitle);
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", readPrivacyPolicy);
		LOG.info(" *** Original Privacy Policy: \n"+privacyPolicy);
		LOG.info(" *** Added Privacy Policy: \n"+addedPrivacyPolicy.toString());
		LOG.info(" *** Retrieved Privacy Policy: \n"+readPrivacyPolicy.toString());
		assertTrue("Expected a privacy policy, but it what not the good one.", RequestPolicyUtils.equal(readPrivacyPolicy, addedPrivacyPolicy));
		assertTrue("Privacy policy not deleted.", deleteResult);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testGetPrivacyPolicyFromJar() {
		String testTitle = "testGetPrivacyPolicyFromJar: retrieve a privacy policy contained in a JAR";
		LOG.info("[[TEST]] "+testTitle);
		String expectedPrivacyPolicy = "<RequestPolicy>" +
				"<Subject>" +
				"<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\" DataType=\""+IIdentity.class.getCanonicalName()+"\">" +
				"<AttributeValue>othercss@societies.local</AttributeValue>" +
				"</Attribute>" +
				"<Attribute AttributeId=\"CisId\" DataType=\"org.societies.api.identity.IIdentity\">" +
				"<AttributeValue>onecis.societies.local</AttributeValue>" +
				"</Attribute>" +
				"</Subject>"+
				"<Target>" +
				"<Resource>" +
				"<Attribute AttributeId=\""+DataIdentifierScheme.CONTEXT+"\" DataType=\"http://www.w3.org/2001/XMLSchema#string\">" +
				"<AttributeValue>fdsfsf</AttributeValue>" +
				"</Attribute>" +
				"</Resource>" +
				"<Action>" +
				"<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants\">" +
				"<AttributeValue>WRITE</AttributeValue>" +
				"</Attribute>" +
				"<optional>false</optional>" +
				"</Action>" +
				"<Condition>" +
				"<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\" DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">" +
				"<AttributeValue DataType=\"SHARE_WITH_3RD_PARTIES\">dfsdf</AttributeValue>" +
				"</Attribute>" +
				"<optional>true</optional>" +
				"</Condition>" +
				"<Condition>" +
				"<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\" DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">" +
				"<AttributeValue DataType=\"DATA_RETENTION_IN_MINUTES\">412</AttributeValue>" +
				"</Attribute>" +
				"<optional>true</optional>" +
				"</Condition>" +
				"<optional>false</optional>" +
				"</Target>" +
				"</RequestPolicy>";
		String retrievedPrivacyPolicy = null;
		String jarLocation = "testjar-1.0.jar";
		try {
			retrievedPrivacyPolicy = privacyPolicyManager.getPrivacyPolicyFromLocation(jarLocation);
			assertTrue("Expected equals privacy policies", RequestPolicyUtils.equal(PrivacyPolicyUtils.fromXacmlString(expectedPrivacyPolicy), PrivacyPolicyUtils.fromXacmlString(retrievedPrivacyPolicy)));
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("Privacy error "+e.getLocalizedMessage()+": "+testTitle);
		} catch (Exception e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("Error "+e.getLocalizedMessage()+": "+testTitle);
		}
		LOG.info("*** Expected privacy policy:"+expectedPrivacyPolicy);
		LOG.info("*** Retrieved privacy policy:"+retrievedPrivacyPolicy);
	}


	/* --- Tools --- */
	private void createPersonEntity() {
		mockId = new MockIdentity(IdentityType.CSS, "me", "ict-societies.eu");
		CtxEntityIdentifier ctxPersonId = new CtxEntityIdentifier(mockId.getJid(), "Person", new Long(1));
		personEntity = new CtxEntity(ctxPersonId);
		hasPrivacyPolicies = new CtxAssociation(new CtxAssociationIdentifier(mockId.getJid(), CtxAssociationTypes.HAS_PRIVACY_POLICIES, new Long(3)));
		CtxEntityIdentifier policyEntityId = new CtxEntityIdentifier(mockId.getJid(), CtxEntityTypes.PRIVACY_POLICY, new Long(1));
		policyEntity = new CtxEntity(policyEntityId);
		CtxAttributeIdentifier cisPolicyAttributeId = new CtxAttributeIdentifier(policyEntityId, "policyOf"+RequestorUtils.toUriString(requestorCis), new Long(2));
		cisPolicyAttribute = new CtxAttribute(cisPolicyAttributeId);
		CtxAttributeIdentifier servicePolicyAttributeId = new CtxAttributeIdentifier(policyEntityId, "policyOf"+RequestorUtils.toUriString(requestorService), new Long(2));
		servicePolicyAttribute = new CtxAttribute(servicePolicyAttributeId);
		CtxAttributeIdentifier registryAttrId = new CtxAttributeIdentifier(ctxPersonId, org.societies.api.internal.context.model.CtxAttributeTypes.PRIVACY_POLICY_REGISTRY, new Long(2));
		registryAttribute = new CtxAttribute(registryAttrId);
	}

	private RequestPolicy getRequestPolicy(RequestorBean requestor) {
		List<RequestItem> requestItems = this.getRequestItems();
		RequestPolicy requestPolicy = RequestPolicyUtils.create(requestor, requestItems);
		return requestPolicy;
	}

	private List<RequestItem> getRequestItems() {
		List<RequestItem> items = new ArrayList<RequestItem>();

		Resource locationResource = ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "0"));
		List<Action> actions = new ArrayList<Action>();
		actions.add(ActionUtils.create(ActionConstants.READ));
		RequestItem rItem = RequestItemUtils.create(locationResource, actions, conditions, false);

		items.add(rItem);


		Resource someResource = ResourceUtils.create(DataIdentifierScheme.CONTEXT, "someResource");
		List<Condition> extendedConditions = new ArrayList<Condition>();
		extendedConditions.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES,"0"));
		extendedConditions.add(ConditionUtils.create(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA, "1"));
		List<Action> extendedActions = new ArrayList<Action>();
		extendedActions.add(ActionUtils.create(ActionConstants.READ));
		extendedActions.add(ActionUtils.create(ActionConstants.CREATE));
		extendedActions.add(ActionUtils.create(ActionConstants.WRITE));
		extendedActions.add(ActionUtils.create(ActionConstants.DELETE));
		RequestItem someItem = RequestItemUtils.create(someResource, extendedActions, extendedConditions, false);

		items.add(someItem);
		return items;
	}

	private RequestorServiceBean getRequestorService(){
		IIdentity requestorId = new MockIdentity(IdentityType.CSS, "eliza","ict-societies.eu");
		ServiceResourceIdentifier serviceId = ServiceUtils.generateServiceResourceIdentifierFromString("myGreatService eliza.societies.org");
		return RequestorUtils.create(requestorId.getJid(), serviceId);
	}

	private RequestorCisBean getRequestorCis(){
		IIdentity requestorId = new MockIdentity(IdentityType.CSS, "me","ict-societies.eu");
		IIdentity cisId = new MockIdentity(IdentityType.CIS, "cis-holidays", "ict-societies.eu");
		return (RequestorCisBean) RequestorUtils.create(requestorId.getJid(), cisId.getJid());
	}


	/* -- Dependency injection --- */
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
		LOG.info("[DependencyInjection] IPrivacyPolicyManager injected");
	}
}
