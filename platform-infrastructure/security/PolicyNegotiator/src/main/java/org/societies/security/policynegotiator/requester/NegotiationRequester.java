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
package org.societies.security.policynegotiator.requester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyNegotiationManager;
import org.societies.api.internal.schema.security.policynegotiator.MethodType;
import org.societies.api.internal.security.policynegotiator.INegotiation;
import org.societies.api.internal.security.policynegotiator.INegotiationCallback;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
import org.societies.api.internal.security.storage.ISecureStorage;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.security.digsig.ISignatureMgr;

//@Component
public class NegotiationRequester implements INegotiation {

	private static Logger LOG = LoggerFactory.getLogger(NegotiationRequester.class);
	
	private ISignatureMgr signatureMgr;
	private ISecureStorage secureStorage;
	private INegotiationProviderRemote groupMgr;
	private IPersonalisationManager personalizationMgr;
	private IPrivacyPolicyNegotiationManager privacyPolicyNegotiationMgr;
	private boolean isPrivacyPolicyNegotiationMgrAvailable = false;
	private IEventMgr eventMgr;
	
//	@Autowired
//	public NegotiationRequester(ISignatureMgr signatureMgr) {
//		this.signatureMgr = signatureMgr;
//		LOG.info("NegotiationRequester({})", signatureMgr);
//	}
	
	public NegotiationRequester() {
		LOG.info("NegotiationRequester()");
	}
	
//	@PostConstruct
	public void init() {
		//LOG.debug("init(): signed = {}", signatureMgr.signXml("xml", "xmlNodeId", "id"));
		//LOG.debug("init(): signature valid = {}", signatureMgr.verify("xml"));

		LOG.debug("init()");
		
		// Test: initialization of negotiation. Integration test is available to replace this.
//		IIdentityManager idMgr = groupMgr.getIdMgr();
//		IIdentity provider = idMgr.getThisNetworkNode();
//		startNegotiation(provider, "service-123", new INegotiationCallback() {
//			@Override
//			public void onNegotiationComplete(String agreementKey) {
//				LOG.info("onNegotiationComplete({})", agreementKey);
//			}
//		});
	}

	// Getters and setters for other OSGi services
	public INegotiationProviderRemote getGroupMgr() {
		return groupMgr;
	}
	public void setGroupMgr(INegotiationProviderRemote groupMgr) {
		this.groupMgr = groupMgr;
	}
	public ISignatureMgr getSignatureMgr() {
		return signatureMgr;
	}
	public void setSignatureMgr(ISignatureMgr signatureMgr) {
		this.signatureMgr = signatureMgr;
	}
	public ISecureStorage getSecureStorage() {
		return secureStorage;
	}
	public void setSecureStorage(ISecureStorage secureStorage) {
		this.secureStorage = secureStorage;
	}
	public IPersonalisationManager getPersonalizationMgr() {
		return personalizationMgr;
	}
	public void setPersonalizationMgr(IPersonalisationManager personalizationMgr) {
		this.personalizationMgr = personalizationMgr;
	}
	public IPrivacyPolicyNegotiationManager getPrivacyPolicyNegotiationManager() {
		return privacyPolicyNegotiationMgr;
	}
	public void setPrivacyPolicyNegotiationManager(IPrivacyPolicyNegotiationManager privacyPolicyNegotiationMgr) {
		this.privacyPolicyNegotiationMgr = privacyPolicyNegotiationMgr;
		this.isPrivacyPolicyNegotiationMgrAvailable = true;
	}
	public IEventMgr getEventMgr() {
		return eventMgr;
	}
	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}
	
	public boolean isPrivacyPolicyNegotiationMgrAvailable() {
		return isPrivacyPolicyNegotiationMgrAvailable;
	}

	@Override
	public void startNegotiation(Requestor provider, boolean includePrivacyPolicyNegotiation,
			INegotiationCallback callback) {

		String serviceOrCisId;
		
		if (provider instanceof RequestorService) {
			RequestorService providerService = (RequestorService) provider;
			LOG.info("startNegotiation([{}; {}])", providerService.getRequestorId(),
					providerService.getRequestorServiceId());
			serviceOrCisId = providerService.getRequestorServiceId().getIdentifier().toString();
		}
		else if (provider instanceof RequestorCis) {
			RequestorCis providerCis = (RequestorCis) provider;
			LOG.info("startNegotiation([{}; {}])", providerCis.getRequestorId(),
					providerCis.getCisRequestorId());
			serviceOrCisId = providerCis.getCisRequestorId().getJid();
		}
		else {
			String msg = "Terminating: Inappropriate provider: " + provider.getClass().getName();
			LOG.warn(msg);
			callback.onNegotiationError(msg);
			return;
		}
		ProviderCallback providerCallback = new ProviderCallback(this, provider,
				MethodType.GET_POLICY_OPTIONS, includePrivacyPolicyNegotiation, callback);
		
		groupMgr.getPolicyOptions(serviceOrCisId, provider, providerCallback);
	}

	@Override
	public void startNegotiation(Requestor provider, INegotiationCallback callback) {
		startNegotiation(provider, true, callback);
	}
}
