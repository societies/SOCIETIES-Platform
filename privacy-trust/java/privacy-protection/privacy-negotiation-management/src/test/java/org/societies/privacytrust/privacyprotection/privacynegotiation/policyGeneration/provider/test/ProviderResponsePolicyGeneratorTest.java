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
package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.provider.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.ClientResponseChecker;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.provider.ProviderResponsePolicyGenerator;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class ProviderResponsePolicyGeneratorTest {
	private static Logger LOG = LoggerFactory.getLogger(ProviderResponsePolicyGeneratorTest.class);

	private ProviderResponsePolicyGenerator checkResponseChecked;


	@Before
	public void setUp() {
		checkResponseChecked = new ProviderResponsePolicyGenerator();
	}
	
//	
//	@Test
//	public void testGenerateResponse() {
//		String testTitle = "Generate Response";
//		LOG.info("[Test Case] "+testTitle);
//
//		// Common data
//		List<Condition> conditionsPublic = new ArrayList<Condition>();
//		conditionsPublic.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
//		List<Condition> conditionsPublicAndMaybBeInferred = new ArrayList<Condition>();
//		conditionsPublicAndMaybBeInferred.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
//		conditionsPublicAndMaybBeInferred.add(ConditionUtils.create(ConditionConstants.MAY_BE_INFERRED, "1"));
//		RequestItem requestItemLocation = RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_COORDINATES), ActionUtils.createList(ActionConstants.READ), conditionsPublic);
//		RequestItem requestItemAction = RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION), ActionUtils.createList(ActionConstants.READ, ActionConstants.WRITE), conditionsPublicAndMaybBeInferred);
//		RequestItem requestItemActionOptional = RequestItemUtils.create(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION), ActionUtils.createList(ActionConstants.READ, ActionConstants.WRITE), conditionsPublicAndMaybBeInferred, true);
//
//		// - NULL
//		LOG.info("[Test] NULL");
//		RequestPolicy requestedPolicy = null;
//		ResponsePolicy providedPolicy = null;
//		assertTrue("Null response policies should match", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//
//		// - Empty
//		LOG.info("[Test] Empty");
//		requestedPolicy = new ResponsePolicy();
//		providedPolicy = new ResponsePolicy();
//		assertTrue("Empty response policies should match", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//
//		// - Filled but empty
//		LOG.info("[Test] Filled but empty");
//		requestedPolicy.setRequestor(RequestorUtils.create("emma.ict-societies.eu", "cis-test.ict-societies.eu"));
//		requestedPolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
//		List<ResponseItem> requestedResponseItems = new ArrayList<ResponseItem>();
//		List<ResponseItem> providedResponseItems = new ArrayList<ResponseItem>();
//		requestedPolicy.setResponseItems(requestedResponseItems);
//		providedPolicy = new ResponsePolicy();
//		assertTrue("Empty pre-filled Response policies should match", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//
//		// - Filled with one mandatory
//		LOG.info("[Test] Filled with one mandatory");
//		requestedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemLocation));
//		// Empty
//		providedPolicy = new ResponsePolicy();
//		assertFalse("Only mandatory Response policies should not match (1/3)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		// Filled with DENY
//		providedPolicy.setRequestor(RequestorUtils.create("emma.ict-societies.eu", "cis-test.ict-societies.eu"));
//		providedPolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
//		providedResponseItems.add(ResponseItemUtils.create(Decision.DENY, requestItemLocation));
//		providedPolicy.setResponseItems(providedResponseItems);
//		assertFalse("Only mandatory Response policies should not match (2/3)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		// Filled with PERMIT
//		providedPolicy = new ResponsePolicy();
//		providedPolicy.setRequestor(RequestorUtils.create("emma.ict-societies.eu", "cis-test.ict-societies.eu"));
//		providedPolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
//		providedResponseItems = new ArrayList<ResponseItem>();
//		providedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemLocation));
//		providedPolicy.setResponseItems(providedResponseItems);
//		assertTrue("Only mandatory Response policies should match (3/3)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//
//		// - Filled with several mandatory
//		LOG.info("[Test] Filled with several mandatory");
//		requestedPolicy = new ResponsePolicy();
//		requestedPolicy.setRequestor(RequestorUtils.create("emma.ict-societies.eu", "cis-test.ict-societies.eu"));
//		requestedPolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
//		providedPolicy = new ResponsePolicy();
//		providedPolicy.setRequestor(RequestorUtils.create("emma.ict-societies.eu", "cis-test.ict-societies.eu"));
//		providedPolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
//		// Empty
//		LOG.info("[Test] Filled with several mandatory: with two missing");
//		requestedResponseItems = new ArrayList<ResponseItem>();
//		requestedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemLocation));
//		requestedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemAction));
//		requestedPolicy.setResponseItems(requestedResponseItems);
//		assertFalse("Only mandatories Response policies should not match (1/7)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		// Filled with DENY
//		LOG.info("[Test] Filled with several mandatory: with two DENY");
//		providedResponseItems = new ArrayList<ResponseItem>();
//		providedResponseItems.add(ResponseItemUtils.create(Decision.DENY, requestItemLocation));
//		providedResponseItems.add(ResponseItemUtils.create(Decision.DENY, requestItemAction));
//		providedPolicy.setResponseItems(providedResponseItems);
//		assertFalse("Only mandatories Response policies should not match (2/7)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		// Filled with PERMIT
//		LOG.info("[Test] Filled with several mandatory: with two PERMIT");
//		providedResponseItems = new ArrayList<ResponseItem>();
//		providedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemLocation));
//		providedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemAction));
//		providedPolicy.setResponseItems(providedResponseItems);
//		assertTrue("Only mandatories Response policies should match (7/7)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		// Filled with one DENY, one PERMIT
//		LOG.info("[Test] Filled with several mandatory: with one DENY, one PERMIT");
//		providedResponseItems = new ArrayList<ResponseItem>();
//		providedResponseItems.add(ResponseItemUtils.create(Decision.DENY, requestItemLocation));
//		providedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemAction));
//		providedPolicy.setResponseItems(providedResponseItems);
//		assertFalse("Only mandatories Response policies should not match (3/7)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		// Filled with one PERMIT, one DENY
//		LOG.info("[Test] Filled with several mandatory: with one PERMIT, one DENY");
//		providedResponseItems = new ArrayList<ResponseItem>();
//		providedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemLocation));
//		providedResponseItems.add(ResponseItemUtils.create(Decision.DENY, requestItemAction));
//		providedPolicy.setResponseItems(providedResponseItems);
//		assertFalse("Only mandatories Response policies should not match (4/7)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		// Filled with one DENY, one missing
//		LOG.info("[Test] Filled with several mandatory: with one DENY, one missing");
//		providedResponseItems = new ArrayList<ResponseItem>();
//		providedResponseItems.add(ResponseItemUtils.create(Decision.DENY, requestItemLocation));
//		providedPolicy.setResponseItems(providedResponseItems);
//		assertFalse("Only mandatories Response policies should not match (5/7)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		// Filled with one PERMIT, one missing
//		LOG.info("[Test] Filled with several mandatory: with one PERMIT, one missing");
//		providedResponseItems = new ArrayList<ResponseItem>();
//		providedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemLocation));
//		providedPolicy.setResponseItems(providedResponseItems);
//		assertFalse("Only mandatories Response policies should not match (6/7)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		
//
//		// - Filled with one mandatory, one optional
//		LOG.info("[Test] Filled with  one mandatory, one optional");
//		requestedPolicy = new ResponsePolicy();
//		requestedPolicy.setRequestor(RequestorUtils.create("emma.ict-societies.eu", "cis-test.ict-societies.eu"));
//		requestedPolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
//		providedPolicy = new ResponsePolicy();
//		providedPolicy.setRequestor(RequestorUtils.create("emma.ict-societies.eu", "cis-test.ict-societies.eu"));
//		providedPolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
//		// Empty
//		LOG.info("[Test] Filled with  one mandatory, one optional: with two missing");
//		requestedResponseItems = new ArrayList<ResponseItem>();
//		requestedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemLocation));
//		requestedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemActionOptional));
//		requestedPolicy.setResponseItems(requestedResponseItems);
//		assertFalse(" one mandatory, one optional Response policies should not match (1/7)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		// Filled with DENY
//		LOG.info("[Test] Filled with  one mandatory, one optional: with two DENY");
//		providedResponseItems = new ArrayList<ResponseItem>();
//		providedResponseItems.add(ResponseItemUtils.create(Decision.DENY, requestItemLocation));
//		providedResponseItems.add(ResponseItemUtils.create(Decision.DENY, requestItemAction));
//		providedPolicy.setResponseItems(providedResponseItems);
//		assertFalse(" one mandatory, one optional Response policies should not match (2/7)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		// Filled with PERMIT
//		LOG.info("[Test] Filled with  one mandatory, one optional: with two PERMIT");
//		providedResponseItems = new ArrayList<ResponseItem>();
//		providedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemLocation));
//		providedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemAction));
//		providedPolicy.setResponseItems(providedResponseItems);
//		assertTrue(" one mandatory, one optional Response policies should match (7/7)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		// Filled with one DENY, one PERMIT
//		LOG.info("[Test] Filled with  one mandatory, one optional: with one DENY, one PERMIT");
//		providedResponseItems = new ArrayList<ResponseItem>();
//		providedResponseItems.add(ResponseItemUtils.create(Decision.DENY, requestItemLocation));
//		providedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemAction));
//		providedPolicy.setResponseItems(providedResponseItems);
//		assertFalse(" one mandatory, one optional Response policies should not match (3/7)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		// Filled with one PERMIT, one DENY
//		LOG.info("[Test] Filled with  one mandatory, one optional: with one PERMIT, one DENY");
//		providedResponseItems = new ArrayList<ResponseItem>();
//		providedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemLocation));
//		providedResponseItems.add(ResponseItemUtils.create(Decision.DENY, requestItemAction));
//		providedPolicy.setResponseItems(providedResponseItems);
//		assertTrue(" one mandatory, one optional Response policies should match (4/7)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		// Filled with one DENY, one missing
//		LOG.info("[Test] Filled with  one mandatory, one optional: with one DENY, one missing");
//		providedResponseItems = new ArrayList<ResponseItem>();
//		providedResponseItems.add(ResponseItemUtils.create(Decision.DENY, requestItemLocation));
//		providedPolicy.setResponseItems(providedResponseItems);
//		assertFalse(" one mandatory, one optional Response policies should not match (5/7)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//		// Filled with one PERMIT, one missing
//		LOG.info("[Test] Filled with  one mandatory, one optional: with one PERMIT, one missing");
//		providedResponseItems = new ArrayList<ResponseItem>();
//		providedResponseItems.add(ResponseItemUtils.create(Decision.PERMIT, requestItemLocation));
//		providedPolicy.setResponseItems(providedResponseItems);
//		assertTrue(" one mandatory, one optional Response policies should match (6/7)", checkResponseChecked.generateResponse(providedPolicy, requestedPolicy));
//	}
}
