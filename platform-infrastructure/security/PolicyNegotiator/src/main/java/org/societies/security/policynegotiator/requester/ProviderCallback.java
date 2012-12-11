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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyNegotiationManager;
import org.societies.api.internal.schema.security.policynegotiator.MethodType;
import org.societies.api.internal.schema.security.policynegotiator.SlaBean;
import org.societies.api.internal.security.policynegotiator.INegotiationCallback;
import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.security.digsig.DigsigException;
import org.societies.security.policynegotiator.sla.SLA;
import org.societies.security.policynegotiator.xml.Xml;
import org.societies.security.policynegotiator.xml.XmlException;

/**
 * This class receives results from async invocations of {@link INegotiationProvider}
 *
 * @author Mitja Vardjan
 *
 */
public class ProviderCallback implements INegotiationProviderCallback {

	private static Logger LOG = LoggerFactory.getLogger(ProviderCallback.class);

	private NegotiationRequester requester;
	private MethodType method;
	private Requestor provider;
	private INegotiationCallback finalCallback;
	private boolean includePrivacyPolicyNegotiation;
	
	public ProviderCallback(NegotiationRequester requester, Requestor provider,
			MethodType method, boolean includePrivacyPolicyNegotiation,
			INegotiationCallback callback) {
		
		LOG.debug("ProviderCallback({})", method);

		this.requester = requester;
		this.method = method;
		this.provider = provider;
		this.finalCallback = callback;
		this.includePrivacyPolicyNegotiation = includePrivacyPolicyNegotiation;
//		if (method != MethodType.GET_POLICY_OPTIONS) {
//			LOG.warn("Wrong constructor is used");
//		}
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback
	 * #receiveExamplesResult(java.lang.Object)
	 */
	@Override
	public void receiveResult(SlaBean result) {
		
		LOG.debug("receiveResult(): method = ", method);
		
		int sessionId = result.getSessionId();
		
		switch(method) {
		case GET_POLICY_OPTIONS:
			if (result.isSuccess()) {
				String sop;
				sop = result.getSla();
				try {
					String selectedSop = selectSopOption(sop);
					// TODO: use real identity when it can be gathered from other components
					sop = requester.getSignatureMgr().signXml(sop, selectedSop, null);
					ProviderCallback callback = new ProviderCallback(requester, provider,
							MethodType.ACCEPT_POLICY_AND_GET_SLA, includePrivacyPolicyNegotiation,
							finalCallback); 
					requester.getGroupMgr().acceptPolicyAndGetSla(
							sessionId,
							sop,
							false,
							provider.getRequestorId(),
							callback);
				} catch (XmlException e) {
					LOG.warn("receiveResult(): session {}: ", sessionId, e);
				} catch (DigsigException e) {
					LOG.warn("receiveResult(): session {}: ", sessionId, e);
				}
			}
			break;
		case ACCEPT_POLICY_AND_GET_SLA:
			if (result.isSuccess()) {
				String sla;
				sla = result.getSla();
				if (requester.getSignatureMgr().verifyXml(sla)) {
					LOG.info("receiveResult(): session = {}, final SLA reached.", sessionId);
					LOG.debug("receiveResult(): final SLA size: {}", sessionId, sla == null ? null : sla.length());
					
					// Store the SLA into secure storage
					String agreementKey = generateKey();
					requester.getSecureStorage().putDocument(agreementKey, sla.getBytes());
					
					// Get service URL if applicable
					List<URI> fileUris = result.getFileUris();
					LOG.debug("URLs = {}", fileUris);
					if (fileUris != null) {
						for (URI u : fileUris) {
							LOG.debug("--> URI = {}", u);
						}
					}
					
					if (includePrivacyPolicyNegotiation) {
						if (requester.isPrivacyPolicyNegotiationMgrAvailable()) {
							startPrivacyPolicyNegotiation(provider, agreementKey, fileUris);
						}
						else {
							LOG.warn("Privacy Policy Negotiation Manager not available");
							finalCallback.onNegotiationError("Privacy Policy Negotiation Manager not available");
						}
					}
					else {
						// Notify successful end of negotiation
						LOG.debug("invoking final callback.");
						finalCallback.onNegotiationComplete(agreementKey, fileUris);
						LOG.info("negotiation finished, final callback invoked");
					}
				}
				else {
					LOG.warn("receiveResult(): session = {}, final SLA invalid!", sessionId);
				}
			}
			break;
		case REJECT:
			// No need for action.
			// After more tests, the method could be changed back to void to save some bandwidth.
			LOG.debug("receiveResult(): session = {}, reject success = ", sessionId, result.isSuccess());
			break;
		}
	}
	
	private void startPrivacyPolicyNegotiation(Requestor provider, String agreementKey, List <URI> fileUris) {

		IPrivacyPolicyNegotiationManager ppn = requester.getPrivacyPolicyNegotiationManager();
		PrivacyPolicyNegotiationListener listener;
		
		String[] eventTypes = new String[] {
				EventTypes.FAILED_NEGOTIATION_EVENT,
				EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT}; 

		listener = new PrivacyPolicyNegotiationListener(finalCallback, agreementKey, fileUris,
				requester.getEventMgr(), eventTypes);
		
		requester.getEventMgr().subscribeInternalEvent(listener, eventTypes, null);
		
		if (provider instanceof RequestorService) {
			RequestorService providerService = (RequestorService) provider;
			LOG.debug("startPrivacyPolicyNegotiation([{}; {}])", providerService.getRequestorId(),
					providerService.getRequestorServiceId());
			ppn.negotiateServicePolicy(providerService);
		}
		else if (provider instanceof RequestorCis) {
			RequestorCis providerCis = (RequestorCis) provider;
			LOG.debug("startPrivacyPolicyNegotiation([{}; {}])", providerCis.getRequestorId(),
					providerCis.getCisRequestorId());
			ppn.negotiateCISPolicy(providerCis);
		}
		else {
			LOG.warn("startPrivacyPolicyNegotiation(): unrecognized provider type: {}", provider.getClass().getName());
		}
	}
	
	private String selectSopOption(String sopString) throws XmlException {
		
		Xml xml = new Xml(sopString);
		SLA sop = new SLA(xml);
		String[] sopName = sop.getSopNames();
		String[] providerName = new String[sopName.length];
		String[] sopContent = new String[sopName.length];
		
		for (int k = 0; k < sopName.length; k++) {
			providerName[k] = sop.getProviderName(sopName[k]);
			sopContent[k] = sop.getSopContent(sopName[k]);
			LOG.debug("selectSopOption(): SOP = {}, provider = {}", sopName[k], providerName[k]);
		}
		
		//SopSuitability suitability = new SopSuitability(personalizationMgr);
		//suitability.calculateSuitability(preferenceNames, valuesInSop, weights);
		
		return sopName[0];  // FIXME: display all options in a pop-up GUI and return what user has chosen
	}
	
	private String generateKey() {
		
		String key;
		
		if (provider instanceof RequestorService) {
			RequestorService providerService = (RequestorService) provider;
			key = "policy-sla-" + providerService.getRequestorServiceId().getIdentifier().toString();
		}
		else if (provider instanceof RequestorCis) {
			RequestorCis providerCis = (RequestorCis) provider;
			key = "policy-cis_membership-" + providerCis.getCisRequestorId().getJid();
		}
		else {
			LOG.warn("generateKey(): unrecognized provider type: {}", provider.getClass().getName());
			Random r = new Random();
			key = "policy-unknown-" + Long.toString(r.nextLong());
		}
		return key;
	}
}
