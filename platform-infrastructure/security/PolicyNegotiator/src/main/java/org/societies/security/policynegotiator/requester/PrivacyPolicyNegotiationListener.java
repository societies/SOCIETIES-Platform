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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.NegotiationStatus;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.security.policynegotiator.INegotiationCallback;
import org.societies.api.internal.security.storage.ISecureStorage;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;

/**
 * 
 * @author Mitja Vardjan
 *
 */
public class PrivacyPolicyNegotiationListener extends EventListener {

	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyNegotiationListener.class);
	
	INegotiationCallback finalCallback;
	String slaKey;
	URI jar;
	
	/**
	 * 
	 * @param finalCallback The callback to be invoked when both SLA and privacy
	 * policy negotiations complete.
	 * 
	 * @param slaKey The key to gather SLA from secure storage using
	 * {@link ISecureStorage#getDocument(String)}
	 */
	public PrivacyPolicyNegotiationListener(INegotiationCallback finalCallback, String slaKey, URI jar) {
		this.finalCallback = finalCallback;
		this.slaKey = slaKey;
		this.jar = jar;
	}
	
	@Override
	public void handleInternalEvent(InternalEvent event) {

		String type = event.geteventType();
		
		LOG.info("Internal event received: {}", type);    
		LOG.debug("*** event name : " + event.geteventName());
		LOG.debug("*** event source : " + event.geteventSource());
		PPNegotiationEvent payload = (PPNegotiationEvent) event.geteventInfo();
		NegotiationStatus status = payload.getNegotiationStatus();
		LOG.debug("negotiation status : " + status);

		if (type.equals(EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT)) {
			if (status == NegotiationStatus.SUCCESSFUL) {
				notifySuccess();
			}
			else if (status == NegotiationStatus.FAILED) {
				notifyFailure();
			}
		}
		else if (type.equals(EventTypes.FAILED_NEGOTIATION_EVENT)) {
			notifyFailure();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.societies.api.osgi.event.EventListener#handleExternalEvent(org.societies.api.osgi.event.CSSEvent)
	 */
	@Override
	public void handleExternalEvent(CSSEvent event) {
		LOG.warn("External event received unexpectedly: {}", event.geteventType());    
	}
	
	private void notifySuccess() {
		if (finalCallback != null) {
			LOG.debug("invoking final callback");
			finalCallback.onNegotiationComplete(slaKey, jar);
			LOG.info("negotiation finished, final callback invoked");
		}
		else {
			LOG.info("negotiation finished, but final callback is null");
		}
	}
	
	private void notifyFailure() {
		LOG.warn("Privacy policy negotiation failed");
		finalCallback.onNegotiationError("");
	}
}
