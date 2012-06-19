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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
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
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyPolicyTypeConstants;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager;
import org.societies.util.commonmock.MockIdentity;
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
 * - generate a privacy policy from XML string
 * - generate a privacy policy from empty XML string
 * - transform a privacy policy to a XML string
 * - transform an empty privacy policy to a XML string
 * - equality between two RequestPolicy
 * 
 * @author Olivier Maridat (Trialog)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration//(locations = { "PrivacyPolicyManagerTest-context.xml" })
public class PrivacyPolicyManagerTest extends AbstractJUnit4SpringContextTests {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyManagerTest.class.getSimpleName());

	@Autowired
	private IPrivacyPolicyManager privacyPolicyManager;
	
	private ICtxBroker ctxBroker;
	private RequestorCis requestorCis;
	private RequestorService requestorService;
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

		// CtxBorker
		ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
		Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.PRIVACY_POLICY_REGISTRY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
		Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, "policyOf"+getCtxType(requestorCis))).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
		Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, "policyOf"+getCtxType(requestorService))).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
		Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PRIVACY_POLICY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
		IndividualCtxEntity weirdPerson = new IndividualCtxEntity(personEntity.getId());
		Mockito.when(ctxBroker.retrieveCssOperator()).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
		Mockito.when(ctxBroker.createAssociation(CtxAssociationTypes.HAS_PRIVACY_POLICIES)).thenReturn(new AsyncResult<CtxAssociation>(hasPrivacyPolicies));
		Mockito.when(ctxBroker.createEntity(CtxEntityTypes.PRIVACY_POLICY)).thenReturn(new AsyncResult<CtxEntity>(policyEntity));
		Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) policyEntity.getId(), "policyOf"+getCtxType(requestorCis))).thenReturn(new AsyncResult<CtxAttribute>(cisPolicyAttribute));
		Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) policyEntity.getId(), "policyOf"+getCtxType(requestorService))).thenReturn(new AsyncResult<CtxAttribute>(servicePolicyAttribute));
		Mockito.when(ctxBroker.createAttribute(personEntity.getId(), CtxAttributeTypes.PRIVACY_POLICY_REGISTRY)).thenReturn(new AsyncResult<CtxAttribute>(registryAttribute));
		Mockito.when(ctxBroker.retrieve(cisPolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(cisPolicyAttribute));
		Mockito.when(ctxBroker.retrieve(servicePolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(servicePolicyAttribute));
		Mockito.when(ctxBroker.remove(cisPolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(cisPolicyAttribute));
		Mockito.when(ctxBroker.remove(servicePolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(servicePolicyAttribute));

		// Comm Manager
		ICommManager commManager = Mockito.mock(ICommManager.class);
		IIdentityManager idManager = Mockito.mock(IIdentityManager.class);
		IIdentity otherCssId = new MockIdentity(IdentityType.CSS, "othercss","societies.local");
		IIdentity cisId = new MockIdentity(IdentityType.CIS, "onecis", "societies.local");
		Mockito.when(idManager.fromJid(otherCssId.getJid())).thenReturn(otherCssId);
		Mockito.when(idManager.fromJid(cisId.getJid())).thenReturn(cisId);
		Mockito.when(idManager.fromJid("onecis.societies.local")).thenReturn(new MockIdentity("onecis.societies.local"));
		Mockito.when(idManager.fromJid("onecis@societies.local")).thenReturn(new MockIdentity("onecis@societies.local"));
		Mockito.when(idManager.fromJid("othercss@societies.local")).thenReturn(new MockIdentity("othercss@societies.local"));
		Mockito.when(idManager.fromJid("red@societies.local")).thenReturn(new MockIdentity("red@societies.local"));
		Mockito.when(idManager.fromJid("eliza@societies.local")).thenReturn(new MockIdentity("eliza@societies.local"));
		Mockito.when(commManager.getIdManager()).thenReturn(idManager);

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
		LOG.info("[Test] testGetCisPrivacyPolicyNonExisting: retrieve a non-existing privacy policy");
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
		assertEquals("Expected null privacy policy, but it is not.", privacyPolicy, expectedPrivacyPolicy);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetCisPrivacyPolicy() {
		LOG.info("[Test] testGetCisPrivacyPolicy: add and retrieve a privacy policy");
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy privacyPolicy = null;
		boolean deleteResult = false;
		try {
			addedPrivacyPolicy = privacyPolicyManager.updatePrivacyPolicy(cisPolicy);
			privacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestorCis);
			deleteResult = privacyPolicyManager.deletePrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] testGetCisPrivacyPolicy: add and retrieve a privacy policy", e);
			fail("[Error testGetCisPrivacyPolicy] Privacy error");
		} catch (Exception e) {
			LOG.error("[Test PrivacyException] testGetCisPrivacyPolicy: add and retrieve a privacy policy", e);
			fail("[Error testDeletePrivacyPolicy] error");
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", privacyPolicy);
		LOG.info(privacyPolicy.toString());
		LOG.info(addedPrivacyPolicy.toString());
		assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicy, addedPrivacyPolicy);
		assertTrue("Privacy policy not deleted.", deleteResult);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetServicePrivacyPolicyNonExisting() {
		LOG.info("[Test] testGetServicePrivacyPolicyNonExisting: retrieve a non-existing privacy policy");
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
		assertEquals("Expected null privacy policy, but it is not.", privacyPolicy, expectedPrivacyPolicy);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testGetServicePrivacyPolicy() {
		LOG.info("[Test] testGetServicePrivacyPolicy: add and retrieve a privacy policy");
		RequestPolicy addedPrivacyPolicy = null;
		RequestPolicy privacyPolicy = null;
		boolean deleteResult = false;
		try {
			addedPrivacyPolicy = privacyPolicyManager.updatePrivacyPolicy(servicePolicy);
			privacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestorService);
			deleteResult = privacyPolicyManager.deletePrivacyPolicy(requestorService);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] testGetServicePrivacyPolicy: add and retrieve a privacy policy", e);
			fail("[Error testGetServicePrivacyPolicy] Privacy error");
		} catch (Exception e) {
			fail("[Error testDeletePrivacyPolicy] error");
		}
		assertNotNull("Privacy policy not added.", addedPrivacyPolicy);
		assertNotNull("Privacy policy retrieved is null, but it should not.", privacyPolicy);
		assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicy, addedPrivacyPolicy);
		assertTrue("Privacy policy not deleted.", deleteResult);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)}.
	 */
	@Test
	public void testUpdatesCisPrivacyPolicy() {
		LOG.info("[Test] testUpdatePrivacyPolicy: update the same privacy policy");
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
		assertEquals("Privacy policy not created", cisPolicy, privacyPolicy1);
		assertEquals("Privacy policy not updated", cisPolicy, privacyPolicy2);
		assertEquals("Difference between same privacy policies", privacyPolicy1, privacyPolicy2);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)}.
	 */
	@Test
	public void testUpdatesCisPrivacyPolicies() {
		LOG.info("[Test] testUpdatePrivacyPolicy: update the same privacy policy");
		RequestPolicy privacyPolicy1 = null;
		RequestPolicy privacyPolicy2 = null;
		RequestPolicy servicePolicy2 = new RequestPolicy(cisPolicy.getRequests());
		servicePolicy2.setRequestor(requestorService);
		try {
			privacyPolicy1 = privacyPolicyManager.updatePrivacyPolicy(cisPolicy);
			privacyPolicy2 = privacyPolicyManager.updatePrivacyPolicy(servicePolicy2);
		} catch (PrivacyException e) {
			fail("[Error testUpdatePrivacyPolicy] Privacy error");
		} catch (Exception e) {
			fail("[Error testDeletePrivacyPolicy] error");
		}
		assertEquals("Privacy policy not created", cisPolicy, privacyPolicy1);
		assertEquals("Privacy policy not updated", servicePolicy2, privacyPolicy2);
		assertFalse("Same privacy policies but it should not", privacyPolicy1.equals(privacyPolicy2));
	}


	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#deletePrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testDeleteServicePrivacyPolicyNotExisting() {
		LOG.info("[Test] testDeleteServicePrivacyPolicyNotExisting: delete a non-existing privacy policy");
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
		LOG.info("[Test] testDeleteCisPrivacyPolicyNotExisting: delete a non-existing privacy policy");
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
		LOG.info("[Test] testDeletePrivacyPolicy: add and retrieve and delete a privacy policy");
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
		assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicyBefore, addedPrivacyPolicy);
		assertTrue("Privacy policy not deleted.", deleteResult);
		assertNull("Privacy policy not really deleted.", privacyPolicyAfter);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#deletePrivacyPolicy(java.lang.String)}.
	 */
	@Test
	public void testDeleteCisPrivacyPolicy() {
		LOG.info("[Test] testDeleteCisPrivacyPolicy: add and retrieve and delete a privacy policy");
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
		assertEquals("Expected a privacy policy, but it what not the good one.", privacyPolicyBefore, addedPrivacyPolicy);
		assertTrue("Privacy policy not deleted.", deleteResult);
		assertNull("Privacy policy not really deleted.", privacyPolicyAfter);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#fromXMLString(org.lang.String privacyPolicy)}.
	 */
	@Test
	public void testFromXmlNull() {
		LOG.info("[Test] testFromXml");
		RequestPolicy privacyPolicy = null;
		try {
			privacyPolicy = privacyPolicyManager.fromXMLString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] testFromXml", e);
			fail("[Error testUpdatePrivacyPolicy] Privacy error");
		} catch (Exception e) {
			LOG.info("[Test Exception] testFromXml", e);
			fail("[Error testFromXml] error");
		}
		assertNull("Privacy policy not null, but it should", privacyPolicy);
	}
	
	
	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#fromXMLString(org.lang.String privacyPolicy)}.
	 */
	@Test
	public void testFromXml() {
		LOG.info("[Test] testFromXml");
		RequestPolicy privacyPolicy = null;
		try {
			privacyPolicy = privacyPolicyManager.fromXMLString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+cisPolicy.toXMLString());
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] testFromXml", e);
			fail("[Error testUpdatePrivacyPolicy] Privacy error");
		} catch (Exception e) {
			LOG.info("[Test Exception] testFromXml", e);
			fail("[Error testFromXml] error");
		}
		assertEquals("Privacy policy generated not equal to the original policy", cisPolicy, privacyPolicy);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#fromXMLString(org.lang.String privacyPolicy)}.
	 */
	@Test
	public void testToXmlNull() {
		LOG.info("[Test] testToXmlNull");
		String privacyPolicy = null;
		try {
			privacyPolicy = privacyPolicyManager.toXMLString(null);
		} catch (Exception e) {
			LOG.info("[Test Exception] testToXmlNull", e);
			fail("[Error testFromXml] error");
		}
		assertEquals("Privacy policy generated not equal to the original policy", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", privacyPolicy);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#fromXMLString(org.lang.String privacyPolicy)}.
	 */
	@Test
	public void testToXml() {
		LOG.info("[Test] testToXml");
		String privacyPolicy = null;
		try {
			privacyPolicy = privacyPolicyManager.toXMLString(cisPolicy);
		} catch (Exception e) {
			LOG.info("[Test Exception] testToXml", e);
			fail("[Error testFromXml] error");
		}
		assertEquals("Privacy policy generated not equal to the original policy", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+cisPolicy.toXMLString(), privacyPolicy);
	}


	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#inferPrivacyPolicy()}.
	 */
	@Test
	public void testInferPrivacyPolicy() {
		LOG.info("[Test] testRequestPolicyEquals");
		RequestPolicy expected = new RequestPolicy(new ArrayList<RequestItem>());
		RequestPolicy actual = null;
		try {
			actual = privacyPolicyManager.inferPrivacyPolicy(PrivacyPolicyTypeConstants.CIS, null);
		} catch (PrivacyException e) {
			LOG.error("[Error testInferPrivacyPolicy] Privacy error", e);
			fail("[Error testInferPrivacyPolicy] Privacy error");
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testRequestPolicyEquals() {
		LOG.info("[Test] testRequestPolicyEquals");
		List<RequestItem> requestItems2 = new ArrayList<RequestItem>();
		Resource resource2 = new Resource(CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Action> actions2 = new ArrayList<Action>();
		actions2.add(new Action(ActionConstants.READ));
		RequestItem requestItem2 = new RequestItem(resource2, actions2, new ArrayList<Condition>());
		requestItems2.add(requestItem2);
		RequestPolicy actual = new RequestPolicy(requestItems2);
		actual.setRequestor(requestorCis);

		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		Resource resource = new Resource(CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		RequestItem requestItem = new RequestItem(resource, actions, new ArrayList<Condition>());
		requestItems.add(requestItem);
		RequestPolicy expected = new RequestPolicy(requestItems);
		expected.setRequestor(requestorCis);

		assertEquals("Expected equals", expected.toXMLString(), actual.toXMLString());
		assertEquals("Expected equals", expected, actual);
	}


	/* --- Tools --- */
	private void createPersonEntity() {
		mockId = new MockIdentity(IdentityType.CSS, "me", "societies.local");
		CtxEntityIdentifier ctxPersonId = new CtxEntityIdentifier(mockId.getJid(), "Person", new Long(1));
		personEntity = new CtxEntity(ctxPersonId);
		hasPrivacyPolicies = new CtxAssociation(new CtxAssociationIdentifier(mockId.getJid(), CtxAssociationTypes.HAS_PRIVACY_POLICIES, new Long(3)));
		CtxEntityIdentifier policyEntityId = new CtxEntityIdentifier(mockId.getJid(), CtxEntityTypes.PRIVACY_POLICY, new Long(1));
		policyEntity = new CtxEntity(policyEntityId);
		CtxAttributeIdentifier cisPolicyAttributeId = new CtxAttributeIdentifier(policyEntityId, "policyOf"+getCtxType(requestorCis), new Long(2));
		cisPolicyAttribute = new CtxAttribute(cisPolicyAttributeId);
		CtxAttributeIdentifier servicePolicyAttributeId = new CtxAttributeIdentifier(policyEntityId, "policyOf"+getCtxType(requestorService), new Long(2));
		servicePolicyAttribute = new CtxAttribute(servicePolicyAttributeId);
		CtxAttributeIdentifier registryAttrId = new CtxAttributeIdentifier(ctxPersonId, CtxAttributeTypes.PRIVACY_POLICY_REGISTRY, new Long(2));
		registryAttribute = new CtxAttribute(registryAttrId);
	}

	private RequestPolicy getRequestPolicy(Requestor requestor) {
		RequestPolicy requestPolicy;
		List<RequestItem> requestItems = getRequestItems();
		requestPolicy = new RequestPolicy(requestor, requestItems);
		return requestPolicy;
	}

	private List<RequestItem> getRequestItems() {
		List<RequestItem> items = new ArrayList<RequestItem>();
		Resource locationResource = new Resource(CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES,"NO"));
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		RequestItem rItem = new RequestItem(locationResource, actions, conditions, false);
		items.add(rItem);
		Resource someResource = new Resource("someResource");
		List<Condition> extendedConditions = new ArrayList<Condition>();
		extendedConditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES,"NO"));
		extendedConditions.add(new Condition(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA, "YES"));
		List<Action> extendedActions = new ArrayList<Action>();
		extendedActions.add(new Action(ActionConstants.READ));
		extendedActions.add(new Action(ActionConstants.CREATE));
		extendedActions.add(new Action(ActionConstants.WRITE));
		extendedActions.add(new Action(ActionConstants.DELETE));
		RequestItem someItem = new RequestItem(someResource, extendedActions, extendedConditions, false);
		items.add(someItem);
		return items;
	}

	private RequestorService getRequestorService(){
		IIdentity requestorId = new MockIdentity(IdentityType.CSS, "eliza","societies.local");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		try {
			serviceId.setIdentifier(new URI("css://eliza@societies.org/HelloEarth"));
		} catch (URISyntaxException e) {
			LOG.error("Can't create the service ID", e);
		}
		return new RequestorService(requestorId, serviceId);
	}

	private RequestorCis getRequestorCis(){
		IIdentity otherCssId = new MockIdentity(IdentityType.CSS, "othercss","societies.local");
		IIdentity cisId = new MockIdentity(IdentityType.CIS, "onecis", "societies.local");
		return new RequestorCis(otherCssId, cisId);
	}

	private String getCtxType(Requestor requestor){
		if (requestor instanceof RequestorService){
			return ((RequestorService) requestor).getRequestorServiceId().getIdentifier().toString();
		}
		else if (requestor instanceof RequestorCis){
			return ((RequestorCis) requestor).getCisRequestorId().getJid();
		}
		else {
			return requestor.getRequestorId().getJid();
		}
	}


	/* -- Dependency injection --- */
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
		LOG.info("[DependencyInjection] IPrivacyPolicyManager injected");
	}
}
