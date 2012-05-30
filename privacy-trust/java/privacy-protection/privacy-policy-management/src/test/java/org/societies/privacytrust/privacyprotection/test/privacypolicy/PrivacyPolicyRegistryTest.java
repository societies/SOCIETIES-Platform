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

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.internal.context.broker.ICtxBroker;
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
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.privacypolicy.PrivacyPolicyRegistryManager;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class PrivacyPolicyRegistryTest {

	ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
	PrivacyPolicyRegistryManager registryMgr;
	private Requestor requestorService;
	private Requestor requestorCis;
	private RequestPolicy cisPolicy;
	private RequestPolicy servicePolicy;
	private CtxEntity personEntity;
	private org.societies.privacytrust.privacyprotection.test.privacypolicy.MyIdentity mockId;
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
		registryMgr = new PrivacyPolicyRegistryManager(ctxBroker);
	}

	
	private void setupMockito() {
		try {
			Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.PRIVACY_POLICY_REGISTRY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, "policyOf"+this.getCtxType(requestorCis))).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, "policyOf"+this.getCtxType(requestorService))).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PRIVACY_POLICY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			IndividualCtxEntity weirdPerson = new IndividualCtxEntity(personEntity.getId());
			Mockito.when(ctxBroker.retrieveCssOperator()).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
			Mockito.when(ctxBroker.createAssociation(CtxAssociationTypes.HAS_PRIVACY_POLICIES)).thenReturn(new AsyncResult<CtxAssociation>(hasPrivacyPolicies));
			Mockito.when(ctxBroker.createEntity(CtxEntityTypes.PRIVACY_POLICY)).thenReturn(new AsyncResult<CtxEntity>(policyEntity));
			Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) policyEntity.getId(), "policyOf"+this.getCtxType(requestorCis))).thenReturn(new AsyncResult<CtxAttribute>(this.cisPolicyAttribute));
			Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) policyEntity.getId(), "policyOf"+this.getCtxType(requestorService))).thenReturn(new AsyncResult<CtxAttribute>(this.servicePolicyAttribute));
			Mockito.when(ctxBroker.createAttribute(personEntity.getId(), CtxAttributeTypes.PRIVACY_POLICY_REGISTRY)).thenReturn(new AsyncResult<CtxAttribute>(this.registryAttribute));
			Mockito.when(ctxBroker.retrieve(this.cisPolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.cisPolicyAttribute));
			Mockito.when(ctxBroker.retrieve(this.servicePolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.servicePolicyAttribute));
			Mockito.when(ctxBroker.remove(this.cisPolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.cisPolicyAttribute));
			Mockito.when(ctxBroker.remove(this.servicePolicyAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.servicePolicyAttribute));
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void createPersonEntity() {
		mockId = new MyIdentity(IdentityType.CSS, "myId", "domain");
		CtxEntityIdentifier ctxPersonId = new CtxEntityIdentifier(mockId.getJid(), "Person", new Long(1));
		personEntity = new CtxEntity(ctxPersonId);
		hasPrivacyPolicies = new CtxAssociation(new CtxAssociationIdentifier(mockId.getJid(), CtxAssociationTypes.HAS_PRIVACY_POLICIES, new Long(3)));
		CtxEntityIdentifier policyEntityId = new CtxEntityIdentifier(mockId.getJid(), CtxEntityTypes.PRIVACY_POLICY, new Long(1));
		policyEntity = new CtxEntity(policyEntityId);
		CtxAttributeIdentifier cisPolicyAttributeId = new CtxAttributeIdentifier(policyEntityId, "policyOf"+this.getCtxType(requestorCis), new Long(2));
		cisPolicyAttribute = new CtxAttribute(cisPolicyAttributeId);
		CtxAttributeIdentifier servicePolicyAttributeId = new CtxAttributeIdentifier(policyEntityId, "policyOf"+this.getCtxType(requestorService), new Long(2));
		servicePolicyAttribute = new CtxAttribute(servicePolicyAttributeId);
		CtxAttributeIdentifier registryAttrId = new CtxAttributeIdentifier(ctxPersonId, CtxAttributeTypes.PRIVACY_POLICY_REGISTRY, new Long(2));
		registryAttribute = new CtxAttribute(registryAttrId);
		
	}

	@Test
	public void testRegistryStoreRetrieve(){
		registryMgr.addPolicy(requestorCis, cisPolicy);
		
		RequestPolicy policy = registryMgr.getPolicy(requestorCis);
		
		TestCase.assertNotNull("Null policy for requestorCis: "+requestorCis.getRequestorId().getJid()+" / "+((RequestorCis)requestorCis).getCisRequestorId().getJid(), policy);
		
		
		try {
			registryMgr.deletePolicy(requestorCis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		policy = registryMgr.getPolicy(requestorCis);
		
		TestCase.assertNull(policy);
		
		
		
		
		registryMgr.addPolicy(requestorService, servicePolicy);
		
		RequestPolicy policy2 = registryMgr.getPolicy(requestorService);
		TestCase.assertNotNull("Null policy for requestorService: "+requestorService.getRequestorId().getJid()+" / "+((RequestorService)requestorService).getRequestorServiceId().getServiceInstanceIdentifier().toString(), policy2);

		
		try {
			registryMgr.deletePolicy(requestorService);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		policy2 = registryMgr.getPolicy(requestorService);
		TestCase.assertNull(policy2);

		
	}
	
	
	private RequestPolicy getRequestPolicy(Requestor requestor) {
		RequestPolicy requestPolicy;
		
		
		List<RequestItem> requestItems = this.getRequestItems();
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
		IIdentity requestorId = new MyIdentity(IdentityType.CSS, "eliza","societies.org");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		try {
			serviceId.setIdentifier(new URI("css://eliza@societies.org/HelloEarth"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new RequestorService(requestorId, serviceId);
	}
	
	private RequestorCis getRequestorCis(){
		IIdentity requestorId = new MyIdentity(IdentityType.CSS, "me","domain.com");
		IIdentity cisId = new MyIdentity(IdentityType.CIS, "Holidays", "domain.com");
		return new RequestorCis(requestorId, cisId);
	}
	
	private String getCtxType(Requestor requestor){
		if (requestor instanceof RequestorService){
			return ((RequestorService) requestor).getRequestorServiceId().getIdentifier().toString();
		}else if (requestor instanceof RequestorCis){
			return ((RequestorCis) requestor).getCisRequestorId().getJid();
		}else {
			return requestor.getRequestorId().getJid();
		}
	}
}
