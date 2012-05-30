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
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyPolicyTypeConstants;
import org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Olivier Maridat (Trialog)
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "PrivacyPolicyManagerTest-context.xml" })
public class PrivacyPolicyManagerTest {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyManagerTest.class.getSimpleName());
	
	@Autowired
	IPrivacyPolicyManager privacyPolicyManager;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
//		ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
//		((PrivacyPolicyManager) privacyPolicyManager).setCtxBroker(ctxBroker);
//		Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.PRIVACY_POLICY_REGISTRY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
//		Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, "policyOf"+this.getCtxType(requestorCis))).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
//		Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, "policyOf"+this.getCtxType(requestorService))).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
//		Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PRIVACY_POLICY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
//		IndividualCtxEntity weirdPerson = new IndividualCtxEntity(personEntity.getId());
//		Mockito.when(ctxBroker.retrieveCssOperator()).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
//		Mockito.when(ctxBroker.createAssociation(CtxAssociationTypes.HAS_PRIVACY_POLICIES)).thenReturn(new AsyncResult<CtxAssociation>(hasPrivacyPolicies));
//		Mockito.when(ctxBroker.createEntity(CtxEntityTypes.PRIVACY_POLICY)).thenReturn(new AsyncResult<CtxEntity>(policyEntity));
//		Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) policyEntity.getId(), "policyOf"+this.getCtxType(requestorCis))).thenReturn(new AsyncResult<CtxAttribute>(this.cisPolicyAttribute));
//		Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) policyEntity.getId(), "policyOf"+this.getCtxType(requestorService))).thenReturn(new AsyncResult<CtxAttribute>(this.servicePolicyAttribute));
//		Mockito.when(ctxBroker.createAttribute(personEntity.getId(), CtxAttributeTypes.PRIVACY_POLICY_REGISTRY)).thenReturn(new AsyncResult<CtxAttribute>(this.registryAttribute));
//		Mockito.when(ctxBroker.retrieve(this.cisPolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.cisPolicyAttribute));
//		Mockito.when(ctxBroker.retrieve(this.servicePolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.servicePolicyAttribute));
//		Mockito.when(ctxBroker.remove(this.cisPolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.cisPolicyAttribute));
//		Mockito.when(ctxBroker.remove(this.servicePolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.servicePolicyAttribute));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#getPrivacyPolicy(java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testGetPrivacyPolicy() {
//		IIdentity requestorId;
		Requestor requestor = null; //new Requestor(requestorId);
		RequestPolicy expected = null;
		RequestPolicy actual = null;
		try {
			actual = privacyPolicyManager.getPrivacyPolicy(requestor);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(expected, actual);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)}.
	 */
	@Test
	@Ignore
	public void testUpdatePrivacyPolicy() {
		RequestPolicy expected = null;
		RequestPolicy actual = null;
		try {
			actual = privacyPolicyManager.updatePrivacyPolicy(null);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(expected, actual);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#deletePrivacyPolicy(java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testDeletePrivacyPolicy() {
		Requestor requestor = null;
		boolean expected = true;
		boolean actual = false;
		try {
			actual = privacyPolicyManager.deletePrivacyPolicy(requestor);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(expected, actual);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyManager#inferPrivacyPolicy()}.
	 */
	@Test
	@Ignore
	public void testInferPrivacyPolicy() {
		RequestPolicy expected = new RequestPolicy(null);
		RequestPolicy actual = null;
		try {
			actual = privacyPolicyManager.inferPrivacyPolicy(PrivacyPolicyTypeConstants.CIS, null);
		} catch (PrivacyException e) {
			LOG.error("[Error testInferPrivacyPolicy] Privacy error", e);
			fail("[Error testInferPrivacyPolicy] Privacy error");
		}
		assertEquals(expected, actual);
	}
	
	
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
	}
}
