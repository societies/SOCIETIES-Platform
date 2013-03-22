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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
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
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.NegotiationAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.AgreementEnvelopeUtils;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.AgreementUtils;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyAgreementManagerInternal;
import org.societies.privacytrust.privacyprotection.privacynegotiation.negotiation.client.AgreementFinaliser;
import org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyAgreementManager;
import org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyAgreementManagerInternal;
import org.societies.util.commonmock.MockIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test list:
 * - update a cis privacy policy agreement
 * - delete a not existing a cis privacy policy agreement
 * - delete a not existing a service privacy policy agreement
 * @author Olivier Maridat (Trialog)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "PrivacyPolicyManagerTest-context.xml" })
public class PrivacyAgreementManagerInternalTest extends AbstractJUnit4SpringContextTests {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyAgreementManagerInternalTest.class.getSimpleName());

	@Autowired
	private IPrivacyAgreementManager privacyAgreementManager;
	@Autowired
	private IPrivacyAgreementManagerInternal privacyAgreementManagerInternal;

	private ICtxBroker ctxBroker;
	private Requestor requestor;
	private RequestorCis requestorCis;
	private RequestorService requestorService;
	private AgreementEnvelope agreementCis;
	private AgreementEnvelope agreementService;
	private CtxEntity personEntity;
	private IIdentity mockId;
	private CtxAssociation hasPrivacyAgreements;
	private CtxEntity agreementEntity;
	private CtxAttribute cisAgreementAttribute;
	private CtxAttribute serviceAgreementAttribute;
	private CtxAttribute registryAttribute;
	private static IIdentityManager idManager;

	@BeforeClass
	public static void setUpClass() throws InvalidFormatException {
		idManager = Mockito.mock(IIdentityManager.class);
		Mockito.when(idManager.fromJid("onecis.societies.local")).thenReturn(new MockIdentity("onecis.societies.local"));
		Mockito.when(idManager.fromJid("othercss@societies.local")).thenReturn(new MockIdentity("othercss@societies.local"));
		Mockito.when(idManager.fromJid("eliza@societies.local")).thenReturn(new MockIdentity("eliza@societies.local"));
	}
	@Before
	public void setUp() throws IOException, PrivacyException {
		// Data
		requestorCis = getRequestorCis();
		requestorService = getRequestorService();
		requestor = new Requestor(requestorCis.getRequestorId());
		agreementCis = getAgreementEnveloppe(requestorCis);
		agreementService = getAgreementEnveloppe(requestorService);
		createPersonEntity();
	}

	public void setUpFilled() throws Exception {
		// CtxBorker
		ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
		List<CtxIdentifier> cisAgreementIdList = new ArrayList<CtxIdentifier>();
		cisAgreementIdList.add(cisAgreementAttribute.getId());
		List<CtxIdentifier> serviceAgreementIdList = new ArrayList<CtxIdentifier>();
		serviceAgreementIdList.add(serviceAgreementAttribute.getId());
		Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, PrivacyAgreementManagerInternal.getRequestorId(requestorCis))).thenReturn(new AsyncResult<List<CtxIdentifier>>(cisAgreementIdList));
		Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, PrivacyAgreementManagerInternal.getRequestorId(requestorService))).thenReturn(new AsyncResult<List<CtxIdentifier>>(serviceAgreementIdList));
		Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PRIVACY_POLICY_AGREEMENT)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
		IndividualCtxEntity weirdPerson = new IndividualCtxEntity(personEntity.getId());
		Mockito.when(ctxBroker.retrieveCssOperator()).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
		Mockito.when(ctxBroker.createAssociation(CtxAssociationTypes.HAS_PRIVACY_POLICY_AGREEMENTS)).thenReturn(new AsyncResult<CtxAssociation>(hasPrivacyAgreements));
		Mockito.when(ctxBroker.createEntity(CtxEntityTypes.PRIVACY_POLICY_AGREEMENT)).thenReturn(new AsyncResult<CtxEntity>(agreementEntity));
		Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) agreementEntity.getId(), PrivacyAgreementManagerInternal.getRequestorId(requestorCis))).thenReturn(new AsyncResult<CtxAttribute>(cisAgreementAttribute));
		Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) agreementEntity.getId(), PrivacyAgreementManagerInternal.getRequestorId(requestorService))).thenReturn(new AsyncResult<CtxAttribute>(serviceAgreementAttribute));
		//		Mockito.when(ctxBroker.createAttribute(personEntity.getId(), CtxAttributeTypes.PRIVACY_POLICY_AGREEMENT)).thenReturn(new AsyncResult<CtxAttribute>(registryAttribute));
		Mockito.when(ctxBroker.retrieve(cisAgreementAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(cisAgreementAttribute));
		Mockito.when(ctxBroker.retrieve(serviceAgreementAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(serviceAgreementAttribute));
		Mockito.when(ctxBroker.remove(cisAgreementAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(cisAgreementAttribute));
		Mockito.when(ctxBroker.remove(serviceAgreementAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(serviceAgreementAttribute));
		Mockito.when(ctxBroker.update(cisAgreementAttribute)).thenReturn(new AsyncResult<CtxModelObject>(cisAgreementAttribute));
		Mockito.when(ctxBroker.update(serviceAgreementAttribute)).thenReturn(new AsyncResult<CtxModelObject>(serviceAgreementAttribute));

		// Comm Manager
		ICommManager commManager = Mockito.mock(ICommManager.class);
		Mockito.when(commManager.getIdManager()).thenReturn(idManager);

		// Privacy Policy Manager
		((PrivacyAgreementManager) privacyAgreementManager).setCtxBroker(ctxBroker);
		((PrivacyAgreementManager) privacyAgreementManager).setCommManager(commManager);
		((PrivacyAgreementManagerInternal) privacyAgreementManagerInternal).setCtxBroker(ctxBroker);
		((PrivacyAgreementManagerInternal) privacyAgreementManagerInternal).setCommManager(commManager);
	}

	public void setUpEmpty() throws Exception {
		// CtxBorker
		ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
		Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, PrivacyAgreementManagerInternal.getRequestorId(requestorCis))).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
		Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE,PrivacyAgreementManagerInternal.getRequestorId(requestorService))).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
		Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PRIVACY_POLICY_AGREEMENT)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
		IndividualCtxEntity weirdPerson = new IndividualCtxEntity(personEntity.getId());
		Mockito.when(ctxBroker.retrieveCssOperator()).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
		Mockito.when(ctxBroker.createAssociation(CtxAssociationTypes.HAS_PRIVACY_POLICY_AGREEMENTS)).thenReturn(new AsyncResult<CtxAssociation>(hasPrivacyAgreements));
		Mockito.when(ctxBroker.createEntity(CtxEntityTypes.PRIVACY_POLICY_AGREEMENT)).thenReturn(new AsyncResult<CtxEntity>(agreementEntity));
		Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) agreementEntity.getId(), PrivacyAgreementManagerInternal.getRequestorId(requestorCis))).thenReturn(new AsyncResult<CtxAttribute>(cisAgreementAttribute));
		Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) agreementEntity.getId(), PrivacyAgreementManagerInternal.getRequestorId(requestorService))).thenReturn(new AsyncResult<CtxAttribute>(serviceAgreementAttribute));
		//		Mockito.when(ctxBroker.createAttribute(personEntity.getId(), CtxAttributeTypes.PRIVACY_POLICY_AGREEMENT)).thenReturn(new AsyncResult<CtxAttribute>(registryAttribute));
		Mockito.when(ctxBroker.retrieve(cisAgreementAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(cisAgreementAttribute));
		Mockito.when(ctxBroker.retrieve(serviceAgreementAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(serviceAgreementAttribute));
		Mockito.when(ctxBroker.remove(cisAgreementAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(cisAgreementAttribute));
		Mockito.when(ctxBroker.remove(serviceAgreementAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(serviceAgreementAttribute));
		Mockito.when(ctxBroker.update(cisAgreementAttribute)).thenReturn(new AsyncResult<CtxModelObject>(cisAgreementAttribute));
		Mockito.when(ctxBroker.update(serviceAgreementAttribute)).thenReturn(new AsyncResult<CtxModelObject>(serviceAgreementAttribute));

		// Comm Manager
		ICommManager commManager = Mockito.mock(ICommManager.class);
		IIdentityManager idManager = Mockito.mock(IIdentityManager.class);
		Mockito.when(idManager.fromJid("onecis.societies.local")).thenReturn(new MockIdentity("onecis.societies.local"));
		Mockito.when(idManager.fromJid("othercss@societies.local")).thenReturn(new MockIdentity("othercss@societies.local"));
		Mockito.when(idManager.fromJid("eliza@societies.local")).thenReturn(new MockIdentity("eliza@societies.local"));
		Mockito.when(commManager.getIdManager()).thenReturn(idManager);

		// Privacy Policy Manager
		((PrivacyAgreementManager) privacyAgreementManager).setCtxBroker(ctxBroker);
		((PrivacyAgreementManager) privacyAgreementManager).setCommManager(commManager);
		((PrivacyAgreementManagerInternal) privacyAgreementManagerInternal).setCtxBroker(ctxBroker);
		((PrivacyAgreementManagerInternal) privacyAgreementManagerInternal).setCommManager(commManager);
	}


	@Test
	public void testUpdateCisPrivacyAgreement() {
		String testTitle = new String("testUpdateCisPrivacyAgreement: update a privacy agreement");
		LOG.info("[Test] "+testTitle);
		AgreementEnvelope privacyAgreement = null;
		CtxIdentifier agreementId = null;
		try {
			setUpFilled();
			agreementId = privacyAgreementManagerInternal.updateAgreement(requestorCis, agreementCis);
			privacyAgreement = privacyAgreementManager.getAgreement(requestorCis);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error");
		} catch (Exception e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] error");
		}
		assertNotNull("Privacy agreement not updated.", agreementId);
		assertNotNull("This privacy agreement really not updated", privacyAgreement);
		assertEquals("This privacy agreement is not the same as the original", agreementCis, privacyAgreement);
	}

	@Test
	public void testDeleteCisPrivacyAgreementNotExisting() {
		LOG.info("[Test] testDeleteCisPrivacyAgreementNotExisting: delete a non-existing privacy agreement");
		AgreementEnvelope privacyAgreementBefore = null;
		boolean resultDelete = false;
		try {
			setUpEmpty();
			privacyAgreementBefore = privacyAgreementManager.getAgreement(requestorCis);
			resultDelete = privacyAgreementManagerInternal.deleteAgreement(requestorCis);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] testDeleteCisPrivacyAgreementNotExisting: delete a non-existing privacy agreement", e);
			fail("[Error testDeleteCisPrivacyAgreementNotExisting] Privacy error");
		} catch (Exception e) {
			LOG.info("[Test Exception] testDeleteCisPrivacyAgreementNotExisting: delete a non-existing privacy agreement", e);
			fail("[Error testDeleteCisPrivacyAgreementNotExisting] error");
		}
		assertNull("This privacy agreement already exists!", privacyAgreementBefore);
		assertTrue("Privacy agreement not deleted.", resultDelete);
	}

	@Test
	public void testDeleteServicePrivacyAgreementNotExisting() {
		LOG.info("[Test] testDeleteServicePrivacyAgreementNotExisting: delete a non-existing privacy agreement");
		AgreementEnvelope privacyAgreementBefore = null;
		boolean resultDelete = false;
		try {
			setUpEmpty();
			privacyAgreementBefore = privacyAgreementManager.getAgreement(requestorService);
			resultDelete = privacyAgreementManagerInternal.deleteAgreement(requestorService);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] testDeleteServicePrivacyAgreementNotExisting: delete a non-existing privacy agreement", e);
			fail("[Error testDeleteServicePrivacyAgreementNotExisting] Privacy error");
		} catch (Exception e) {
			LOG.info("[Test Exception] testDeleteServicePrivacyAgreementNotExisting: delete a non-existing privacy agreement", e);
			fail("[Error testDeleteServicePrivacyAgreementNotExisting] error");
		}
		assertNull("This privacy agreement already exists!", privacyAgreementBefore);
		assertTrue("Privacy agreement not deleted.", resultDelete);
	}

	@Test
	public void testCompareRequestorsHashCode() {
		String testTitle = new String("testUpdateCisPrivacyAgreement: update a privacy agreement");
		LOG.info("[Test] "+testTitle);
		try {
			assertEquals("RequestorCis Hashcode != RequestorCis Hashcode", requestorCis.hashCode(), requestorCis.hashCode());
			assertEquals("requestorService Hashcode != requestorService Hashcode", requestorService.hashCode(), requestorService.hashCode());
			assertEquals("requestor Hashcode != requestor Hashcode", requestor.hashCode(), requestor.hashCode());
			assertTrue("RequestorCis Hashcode == RequestorService Hashcode", requestorCis.hashCode() != requestorService.hashCode());
			assertTrue("RequestorCis Hashcode == Requestor Hashcode", requestorCis.hashCode() != requestor.hashCode());
			assertTrue("RequestorService Hashcode == Requestor Hashcode", requestorService.hashCode() != requestor.hashCode());
		} catch (Exception e) {
			LOG.info("[Test Exception] testDeleteServicePrivacyAgreementNotExisting: delete a non-existing privacy agreement", e);
			fail("[Error testDeleteServicePrivacyAgreementNotExisting] error");
		}
	}

	@Test
	public void testEqualsAgreement() {
		String testTitle = new String("testEqualsAgreement");
		LOG.info("[Test] "+testTitle);
		try {
			assertEquals("AgreementEnveloppe not equals to him", agreementCis, agreementCis);
			assertEquals("Agreement not equals to him", agreementCis.getAgreement(), agreementCis.getAgreement());
			assertEquals("Agreement publickey not equals to him after bean transformations", agreementCis.getPublicKey(), AgreementEnvelopeUtils.toAgreementEnvelope(AgreementEnvelopeUtils.toAgreementEnvelopeBean(agreementCis), idManager).getPublicKey());
			assertEquals("Agreement signature not equals to him after bean transformations", agreementCis.getSignature(), AgreementEnvelopeUtils.toAgreementEnvelope(AgreementEnvelopeUtils.toAgreementEnvelopeBean(agreementCis), idManager).getSignature());
			assertEquals("Agreement not equals to him after bean transformations", agreementCis.getAgreement(), AgreementEnvelopeUtils.toAgreementEnvelope(AgreementEnvelopeUtils.toAgreementEnvelopeBean(agreementCis), idManager).getAgreement());
			assertEquals("AgreementEnveloppe not equals to him after bean transformations", agreementCis, AgreementEnvelopeUtils.toAgreementEnvelope(AgreementEnvelopeUtils.toAgreementEnvelopeBean(agreementCis), idManager));
		} catch (Exception e) {
			LOG.info("[Test Exception] "+testTitle, e);
			fail("[Error "+testTitle+"] error");
		}
	}


	/* --- Tools --- */
	private void createPersonEntity() throws PrivacyException, IOException {
		mockId = new MockIdentity(IdentityType.CSS, "me", "societies.local");
		CtxEntityIdentifier ctxPersonId = new CtxEntityIdentifier(mockId.getJid(), "Person", new Long(1));
		personEntity = new CtxEntity(ctxPersonId);
		hasPrivacyAgreements = new CtxAssociation(new CtxAssociationIdentifier(mockId.getJid(), CtxAssociationTypes.HAS_PRIVACY_POLICY_AGREEMENTS, new Long(3)));
		CtxEntityIdentifier policyEntityId = new CtxEntityIdentifier(mockId.getJid(), CtxEntityTypes.PRIVACY_POLICY_AGREEMENT, new Long(1));
		agreementEntity = new CtxEntity(policyEntityId);
		CtxAttributeIdentifier cisAgreementAttributeId = new CtxAttributeIdentifier(policyEntityId, PrivacyAgreementManagerInternal.getRequestorId(requestorCis), new Long(2));
		cisAgreementAttribute = new CtxAttribute(cisAgreementAttributeId);
		cisAgreementAttribute.setBinaryValue(SerialisationHelper.serialise(agreementCis));
		CtxAttributeIdentifier serviceAgreementAttributeId = new CtxAttributeIdentifier(policyEntityId, PrivacyAgreementManagerInternal.getRequestorId(requestorService), new Long(2));
		serviceAgreementAttribute = new CtxAttribute(serviceAgreementAttributeId);
		serviceAgreementAttribute.setBinaryValue(SerialisationHelper.serialise(agreementService));
		//		CtxAttributeIdentifier registryAttrId = new CtxAttributeIdentifier(ctxPersonId, CtxAttributeTypes.PRIVACY_POLICY_AGREEMENT, new Long(2));
		//		registryAttribute = new CtxAttribute(registryAttrId);
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

	private AgreementEnvelope getAgreementEnveloppe(Requestor requestor) throws IOException {
		List<ResponseItem> responseItems = getResponseItems();
		NegotiationAgreement agreement = new NegotiationAgreement(ResponseItemUtils.toResponseItemBeans(responseItems));
		agreement.setRequestor(RequestorUtils.toRequestorBean(requestor));
		AgreementFinaliser finaliser = new AgreementFinaliser();
		byte[] signature = finaliser.signAgreement(AgreementUtils.toAgreementBean(agreement));
		Key publicKey = finaliser.getPublicKey();
		AgreementEnvelope agreementEnveloppe = new AgreementEnvelope(agreement, SerialisationHelper.serialise(publicKey), signature);
		return agreementEnveloppe;
	}

	private List<ResponseItem> getResponseItems() {
		List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
		Resource locationResource = new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES,"NO"));
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		RequestItem rItem = new RequestItem(locationResource, actions, conditions, false);
		responseItems.add(new ResponseItem(rItem, Decision.PERMIT));
		Resource someResource = new Resource(DataIdentifierScheme.CONTEXT, "someResource");
		List<Condition> extendedConditions = new ArrayList<Condition>();
		extendedConditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES,"NO"));
		extendedConditions.add(new Condition(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA, "YES"));
		List<Action> extendedActions = new ArrayList<Action>();
		extendedActions.add(new Action(ActionConstants.READ));
		extendedActions.add(new Action(ActionConstants.CREATE));
		extendedActions.add(new Action(ActionConstants.WRITE));
		extendedActions.add(new Action(ActionConstants.DELETE));
		RequestItem someItem = new RequestItem(someResource, extendedActions, extendedConditions, false);
		responseItems.add(new ResponseItem(someItem, Decision.DENY));
		return responseItems;
	}


	/* -- Dependency injection --- */
	public void setPrivacyAgreementManager(IPrivacyAgreementManager privacyAgreementManager) {
		this.privacyAgreementManager = privacyAgreementManager;
		LOG.info("[DependencyInjection] IPrivacyAgreementManager injected");
	}
	public void setPrivacyAgreementManagerInternal(IPrivacyAgreementManagerInternal privacyAgreementManagerInternal) {
		this.privacyAgreementManagerInternal = privacyAgreementManagerInternal;
		LOG.info("[DependencyInjection] IPrivacyAgreementManagerInternal injected");
	}
}
