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
package org.societies.privacytrust.privacyprotection.privacynegotiation.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.preference.IUserPreferenceManagement;
import org.societies.api.internal.privacytrust.privacyprotection.INegotiationAgent;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyAgreementManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentitySelection;
import org.societies.privacytrust.privacyprotection.privacynegotiation.PrivacyPolicyNegotiationManager;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class PrivacyNegotiationTest {

	private IUserPreferenceManagement prefMgr = Mockito.mock(IUserPreferenceManagement.class);
	private ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
	private IEventMgr eventMgr = Mockito.mock(IEventMgr.class);
	private IPrivacyPreferenceManager privacyPreferenceManager = Mockito.mock(IPrivacyPreferenceManager.class);
	private PrivacyPolicyNegotiationManager negotiationMgr;
	private IPrivacyDataManagerInternal privacyDataManager = Mockito.mock(IPrivacyDataManagerInternal.class);
	private IPrivacyAgreementManagerInternal policyAgreementMgr = Mockito.mock(IPrivacyAgreementManagerInternal.class);
	private INegotiationAgent negotiationAgent;

	@Before
	public void setUp(){
		this.negotiationMgr = new PrivacyPolicyNegotiationManager();
		this.negotiationMgr.setCtxBroker(ctxBroker);
		this.negotiationMgr.setEventMgr(eventMgr);
		this.negotiationMgr.setIdentitySelection(Mockito.mock(IIdentitySelection.class));
		this.negotiationMgr.setIdm(Mockito.mock(IIdentityManager.class));
		this.negotiationMgr.setPrefMgr(prefMgr);
		this.negotiationMgr.setPrivacyDataManager(privacyDataManager);
		this.negotiationMgr.setPolicyAgreementMgr(policyAgreementMgr );
		this.negotiationMgr.setNegotiationAgent(negotiationAgent);
		this.negotiationMgr.initialisePrivacyPolicyNegotiationManager();
	}
	
	//@Test
	public void TestStartNegotiation(){
		RequestorService requestorService = this.getRequestorService();
		RequestorCis requestorCis = this.getRequestorCis();
		this.negotiationMgr.negotiateServicePolicy(requestorService);
		this.negotiationMgr.negotiateCISPolicy(requestorCis);
	}
	
	
	@Test
	public void TestGetProviderPolicy(){
		
	}
	
	@Test
	public void TestNegotiate(){
		
	}
	@Test
	public void TestAcknowledgeAgreement(){
		
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
	
	
}
