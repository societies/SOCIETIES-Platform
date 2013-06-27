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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
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
import org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyRegistryManager;
import org.societies.util.commonmock.MockIdentity;
import org.societies.util.commonmock.MockNetworkNode;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Eliza
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyPolicyRegistryManagerTest {
	private static final Logger LOG = LoggerFactory.getLogger(PrivacyPolicyRegistryManagerTest.class.getName());

	ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
	PrivacyPolicyRegistryManager registryMgr;
	private ICommManager commManager;
	private RequestorBean requestorService;
	private RequestorBean requestorCis;
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
	public void setUp(){
		requestorCis = this.getRequestorCis();
		cisPolicy = this.getRequestPolicy(requestorCis);
		requestorService = this.getRequestorService();
		servicePolicy = this.getRequestPolicy(requestorService);
		this.createPersonEntity();
		this.setupMockito();
		registryMgr = new PrivacyPolicyRegistryManager(ctxBroker, commManager);
	}


	private void setupMockito() {
		try {
			// Comm Manager
			commManager = Mockito.mock(ICommManager.class);
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
			ctxBroker = Mockito.mock(ICtxBroker.class);
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

		} catch (CtxException e) {
			LOG.error("CtxException error in setup", e);
			fail("CtxException Error in setup: "+e.getMessage());
		} catch (InvalidFormatException e) {
			LOG.error("InvalidFormatException error in setup", e);
			fail("InvalidFormatException Error in setup: "+e.getMessage());
		}

	}
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

	@Test
	public void testRegistryStoreRetrieve(){
		String testTitle = "testRegistryStoreRetrieve";
		RequestPolicy policy = null;
		try {
			assertNotNull("Requestor should not be null", requestorCis);
			assertNotNull("Privacy Policy should not be null", cisPolicy);
			registryMgr.updatePrivacyPolicy(requestorCis, cisPolicy);
			policy = registryMgr.getPrivacyPolicy(requestorCis);
			assertNotNull("Null policy for requestorCis: "+requestorCis.getRequestorId()+" / "+((RequestorCisBean)requestorCis).getCisRequestorId(), policy);
			registryMgr.deletePrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.error("PrivacyException in "+testTitle, e);
			fail("PrivacyException in "+testTitle+": "+e.getMessage());
		} catch (Exception e) {
			LOG.error("Exception in "+testTitle, e);
			fail("Exception in "+testTitle+": "+e.getMessage());
		}


		try {
			policy = registryMgr.getPrivacyPolicy(requestorCis);
			assertNull(policy);




			registryMgr.updatePrivacyPolicy(requestorService, servicePolicy);

			RequestPolicy policy2 = registryMgr.getPrivacyPolicy(requestorService);
			assertNotNull("Null policy for requestorService: "+requestorService.getRequestorId()+" / "+((RequestorServiceBean)requestorService).getRequestorServiceId().getServiceInstanceIdentifier().toString(), policy2);


			registryMgr.deletePrivacyPolicy(requestorService);
			policy2 = registryMgr.getPrivacyPolicy(requestorService);
			assertNull(policy2);

		} catch (PrivacyException e) {
			LOG.error("Error2 in "+testTitle, e);
			fail("Error2 in "+testTitle+": "+e.getMessage());
		}
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
		ServiceResourceIdentifier serviceId = ServiceUtils.generateServiceResourceIdentifierFromString("myGreatService eliza@societies.org");
		return RequestorUtils.create(requestorId.getJid(), serviceId);
	}

	private RequestorCisBean getRequestorCis(){
		IIdentity requestorId = new MockIdentity(IdentityType.CSS, "me","ict-societies.eu");
		IIdentity cisId = new MockIdentity(IdentityType.CIS, "cis-holidays", "ict-societies.eu");
		return (RequestorCisBean) RequestorUtils.create(requestorId.getJid(), cisId.getJid());
	}
}
