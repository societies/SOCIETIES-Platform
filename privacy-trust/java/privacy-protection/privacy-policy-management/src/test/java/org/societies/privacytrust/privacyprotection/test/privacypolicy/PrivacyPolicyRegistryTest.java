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

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.services.ServiceUtils;
import org.societies.privacytrust.privacyprotection.privacypolicy.registry.PrivacyPolicyRegistry;

/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyPolicyRegistryTest {
	private static final Logger LOG = LoggerFactory.getLogger(PrivacyPolicyRegistryTest.class.getName());

	private PrivacyPolicyRegistry privacyPolicyRegistry;
	private RequestorBean requestorCis1;
	private RequestorBean requestorCis2;
	private RequestorBean requestorService;
	private DataIdentifier mockCtxId1;
	private DataIdentifier mockCtxId2;
	private DataIdentifier mockCtxId3;

	@Before
	public void setUp() {
		String testTitle = "setUp";
		privacyPolicyRegistry = new PrivacyPolicyRegistry();
		try {
			mockCtxId1 = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://me.ict-societies.eu/ENTITY/person/1/ATTRIBUTE/privac-policy/13");
			mockCtxId2 = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://me.ict-societies.eu/ENTITY/person/1/ATTRIBUTE/privac-policy/14");
			mockCtxId3 = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://me.ict-societies.eu/ENTITY/person/1/ATTRIBUTE/privac-policy/15");
		} catch (MalformedCtxIdentifierException e) {
			LOG.info("[MalformedCtxIdentifierException "+testTitle+"] can't generate mock ctx id", e);
			fail("[MalformedCtxIdentifierException "+testTitle+"] can't generate mock ctx id: "+e.getMessage());
		}
		requestorCis1 = RequestorUtils.create("me.ict-societies.eu", "cis-daddy.ict-societies.eu");
		requestorCis2 = RequestorUtils.create("me.ict-societies.eu", "cis-mummy.ict-societies.eu");
		requestorService = RequestorUtils.create("me.ict-societies.eu", ServiceUtils.generateServiceResourceIdentifierFromString("myService me.ict-societies.eu"));
	}


	@Test
	public void testEmptyRegistry() {
		String testTitle = "Empty registry";
		LOG.info("[TEST] "+testTitle);
		CtxIdentifier ctxId1 = null;
		CtxIdentifier ctxId2 = null;
		CtxIdentifier ctxId3 = null;
		try {
			ctxId1 = privacyPolicyRegistry.getPolicyStorageID(requestorCis1);
			ctxId2 = privacyPolicyRegistry.getPolicyStorageID(requestorCis2);
			ctxId3 = privacyPolicyRegistry.getPolicyStorageID(requestorService);
		}
		catch (Exception e) {
			LOG.info("[Exception "+testTitle+"] can't retrieve the ctx id", e);
			fail("[Exception "+testTitle+"] can't retrieve the ctx id: "+e.getMessage());
		}
		assertNull("Ctx id 1 in an mepty registry should be null", ctxId1);
		assertNull("Ctx id 2 in an mepty registry should be null", ctxId2);
		assertNull("Ctx id 3 in an mepty registry should be null", ctxId3);
		assertTrue("Registry should be empty", privacyPolicyRegistry.isEmpty());
	}

	@Test
	public void testExistingRegistry() {
		String testTitle = "Existing registry";
		LOG.info("[TEST] "+testTitle);
		CtxIdentifier ctxId1 = null;
		CtxIdentifier ctxId2 = null;
		CtxIdentifier ctxId3 = null;
		// Only requestor 1
		try {
			privacyPolicyRegistry.addPolicy(requestorCis1, (CtxIdentifier) mockCtxId1);
			ctxId1 = privacyPolicyRegistry.getPolicyStorageID(requestorCis1);
			ctxId2 = privacyPolicyRegistry.getPolicyStorageID(requestorCis2);
			ctxId3 = privacyPolicyRegistry.getPolicyStorageID(requestorService);
		}
		catch (Exception e) {
			LOG.info("[Exception "+testTitle+"] can't retrieve the ctx id", e);
			fail("[Exception "+testTitle+"] can't retrieve the ctx id: "+e.getMessage());
		}
		assertNotNull("Ctx id 1 should not be null", ctxId1);
		assertEquals("Ctx id 1 should be equal to the added ctx id", mockCtxId1, ctxId1);
		assertNull("Ctx id 2 should be null", ctxId2);
		assertNull("Ctx id 3 should be null", ctxId3);
		assertFalse("Registry should not be empty", privacyPolicyRegistry.isEmpty());

		// Now also requestor 2
		ctxId1 = null;
		ctxId2 = null;
		ctxId3 = null;
		try {
			privacyPolicyRegistry.addPolicy(requestorCis2, (CtxIdentifier) mockCtxId2);
			privacyPolicyRegistry.addPolicy(requestorService, (CtxIdentifier) mockCtxId3);
			ctxId1 = privacyPolicyRegistry.getPolicyStorageID(requestorCis1);
			ctxId2 = privacyPolicyRegistry.getPolicyStorageID(requestorCis2);
			ctxId3= privacyPolicyRegistry.getPolicyStorageID(requestorService);
		}
		catch (Exception e) {
			LOG.info("[Exception "+testTitle+"] can't retrieve the ctx id", e);
			fail("[Exception "+testTitle+"] can't retrieve the ctx id: "+e.getMessage());
		}
		assertNotNull("Ctx id 1 should not be null", ctxId1);
		assertEquals("Ctx id 1 should be equal to the added ctx id", mockCtxId1, ctxId1);
		assertNotNull("Ctx id 2 should not be null", ctxId2);
		assertEquals("Ctx id 2 should be equal to the added ctx id", mockCtxId2, ctxId2);
		assertNotNull("Ctx id 3 should not be null", ctxId3);
		assertEquals("Ctx id 3 should be equal to the added ctx id", mockCtxId3, ctxId3);
		assertFalse("Registry should not be empty", privacyPolicyRegistry.isEmpty());

		// Let's remove requestor 1
		ctxId1 = null;
		ctxId2 = null;
		ctxId3 = null;
		try {
			privacyPolicyRegistry.removePolicy(requestorCis1);
			ctxId1 = privacyPolicyRegistry.getPolicyStorageID(requestorCis1);
			ctxId2 = privacyPolicyRegistry.getPolicyStorageID(requestorCis2);
			ctxId3 = privacyPolicyRegistry.getPolicyStorageID(requestorService);
		}
		catch (Exception e) {
			LOG.info("[Exception "+testTitle+"] can't retrieve the ctx id", e);
			fail("[Exception "+testTitle+"] can't retrieve the ctx id: "+e.getMessage());
		}
		assertNull("Ctx id 1 should be null", ctxId1);
		assertNotNull("Ctx id 2 should not be null", ctxId2);
		assertEquals("Ctx id 2 should be equal to the added ctx id", mockCtxId2, ctxId2);
		assertNotNull("Ctx id 3 should not be null", ctxId3);
		assertEquals("Ctx id 3 should be equal to the added ctx id", mockCtxId3, ctxId3);
		assertFalse("Registry should not be empty", privacyPolicyRegistry.isEmpty());

		// And now, let's remove requestor 2
		ctxId1 = null;
		ctxId2 = null;
		ctxId3 = null;
		try {
			privacyPolicyRegistry.removePolicy(requestorCis2);
			privacyPolicyRegistry.removePolicy(requestorService);
			ctxId1 = privacyPolicyRegistry.getPolicyStorageID(requestorCis1);
			ctxId2 = privacyPolicyRegistry.getPolicyStorageID(requestorCis2);
			ctxId3 = privacyPolicyRegistry.getPolicyStorageID(requestorService);
		}
		catch (Exception e) {
			LOG.info("[Exception "+testTitle+"] can't retrieve the ctx id", e);
			fail("[Exception "+testTitle+"] can't retrieve the ctx id: "+e.getMessage());
		}
		assertNull("Ctx id 1 should be null", ctxId1);
		assertNull("Ctx id 2 should be null", ctxId2);
		assertNull("Ctx id 3 should be null", ctxId3);
		assertTrue("Registry should be empty", privacyPolicyRegistry.isEmpty());
	}
}
