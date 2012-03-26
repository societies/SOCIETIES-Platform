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
package org.societies.security.commsmgr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderCallback;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
import org.societies.api.schema.security.policynegotiator.MethodType;
import org.societies.api.schema.security.policynegotiator.ProviderBean;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.springframework.scheduling.annotation.Async;

/**
 * Comms Client that initiates the remote communication
 * 
 * @author Mitja Vardjan
 * 
 */
//@Component
public class CommsClient implements INegotiationProviderRemote {
	
	private ICommManager commMgr;
	private static Logger LOG = LoggerFactory.getLogger(CommsClient.class);
	private IIdentityManager idMgr;
	private CommsClientCallback clientCallback;

//	@Autowired
//	public CommsClient(ICommManager commManager) {
//		
//		this.commManager = commManager;
//		
//		LOG.info("CommsClient({})", commManager);
//	}
	
	public CommsClient() {
		LOG.info("CommsClient()");
	}

//	@PostConstruct
	public void init() {

		LOG.debug("init()");
		
		clientCallback = new CommsClientCallback();
		
		// REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			commMgr.register(clientCallback);
			LOG.debug("init(): commMgr registered");
		} catch (CommunicationException e) {
			LOG.error("init(): ", e);
		}
		idMgr = commMgr.getIdManager();
	}

	// Getters and setters for beans
	public ICommManager getCommMgr() {
		return commMgr;
	}
	public void setCommMgr(ICommManager commMgr) {
		this.commMgr = commMgr;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.security.policynegotiator.
	 * INegotiationProviderRemote# acceptPolicyAndGetSla(int, java.lang.String,
	 * boolean, org.societies.api.internal.security.policynegotiator.
	 * INegotiationProviderCallback)
	 */
	@Override
	@Async
	public void acceptPolicyAndGetSla(int sessionId, String signedPolicyOption,
			boolean modified, INegotiationProviderCallback callback) {
		
		LOG.debug("acceptPolicyAndGetSla({}, ...)", sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.security.policynegotiator.
	 * INegotiationProviderRemote#
	 * getPolicyOptions(org.societies.api.internal.security
	 * .policynegotiator.INegotiationProviderCallback)
	 */
	@Override
	@Async
	public void getPolicyOptions(String serviceId, INegotiationProviderCallback callback) {
		
		LOG.debug("getPolicyOptions({})", serviceId);
		
		IIdentity toIdentity;
		try {
			toIdentity = idMgr.fromJid("xcmanager.societies.local");
		} catch (InvalidFormatException e) {
			LOG.error("getPolicyOptions({}): ", serviceId, e);
			return;
		}
		
		Stanza stanza = new Stanza(toIdentity);

		// Create message bean
		ProviderBean provider = new ProviderBean();
		provider.setServiceId(serviceId);
		provider.setMethod(MethodType.GET_POLICY_OPTIONS);
		try {
			// Send information query
			commMgr.sendIQGet(stanza, provider, clientCallback);
			LOG.debug("getPolicyOptions({}): message sent to {}", serviceId, toIdentity.getJid());
		} catch (CommunicationException e) {
			LOG.warn("getPolicyOptions({}): could not send message to " + toIdentity.getJid(), serviceId, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.security.policynegotiator.
	 * INegotiationProviderRemote#reject(int)
	 */
	@Override
	@Async
	public void reject(int sessionId, INegotiationProviderCallback callback) {
		
		LOG.debug("reject({})", sessionId);
		
		IIdentity toIdentity;
		try {
			toIdentity = idMgr.fromJid("xcmanager.societies.local");
		} catch (InvalidFormatException e) {
			LOG.error("reject({}): ", sessionId, e);
			return;
		}
		
		Stanza stanza = new Stanza(toIdentity);

		// Create message bean
		ProviderBean provider = new ProviderBean();
		provider.setSessionId(sessionId);
		provider.setMethod(MethodType.REJECT);
		
		try {
			// Send information query
			commMgr.sendIQGet(stanza, provider, clientCallback);
			LOG.debug("reject({}): message sent to {}", sessionId, toIdentity.getJid());
		} catch (CommunicationException e) {
			LOG.warn("reject({}): could not send message to " + toIdentity.getJid(), sessionId, e);
		}
	}
}
