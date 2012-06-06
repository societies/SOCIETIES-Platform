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
package org.societies.privacytrust.example.trust;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.privacytrust.trust.TrustException;
import org.societies.api.internal.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.internal.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.event.TrustEventTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides examples for using the internal Context Broker in OSGi. 
 */
@Service
public class TrustClient {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(TrustClient.class);
	
	private static final String TRUSTED_CSS_ID = "fooIIdentity.societies.local";
	
	/** The String representation of my IIdentity. */
	private final String trustorId;

	/** The Internal Context Broker service reference. */
	private ITrustBroker trustBroker;
	
	/** The Trust Event Mgr service reference. TODO TEMP */
	private ITrustEventMgr trustEventMgr;

	@Autowired(required=true)
	public TrustClient(ITrustBroker trustBroker, ICommManager commMgr, ITrustEventMgr trustEventMgr) {

		LOG.info("*** " + this.getClass() + " instantiated");
		this.trustBroker = trustBroker;
		this.trustEventMgr = trustEventMgr;
		this.trustorId = commMgr.getIdManager().getThisNetworkNode().getBareJid();
		
		LOG.info("*** Starting examples...");
		this.retrieveTrust();
		this.registerForTrustChanges();
	}

	/**
	 * This method demonstrates how to retrieve trust values from the trust repository.
	 */
	private void retrieveTrust() {

		LOG.info("*** retrieveTrust");

		try {
			Double trustValue = this.trustBroker.retrieveTrust(
					new TrustedEntityId(this.trustorId, TrustedEntityType.CSS, TRUSTED_CSS_ID)).get();
			LOG.info("*** retrieved trust value = " + trustValue);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TrustException te) {
			
			LOG.error("Trust Broker sucks: " + te.getLocalizedMessage(), te);
		}
	}

	/**
	 * This method demonstrates how to register for trust update events in the trust repository.
	 */
	private void registerForTrustChanges() {

		LOG.info("*** registerForTrustChanges");

		try {
			final TrustedEntityId teid = new TrustedEntityId(this.trustorId, TrustedEntityType.CSS, TRUSTED_CSS_ID); 
			// 1. Register listener by specifying the context attribute identifier
			this.trustBroker.registerTrustUpdateEventListener(new MyTrustUpdateEventListener(),
					teid);

			// 2. Fire fake TrustUpdateEvent
			this.trustEventMgr.postEvent(
					new TrustUpdateEvent(teid, 0.05d, 0.8d), 
					new String[] { TrustEventTopic.USER_PERCEIVED_TRUST_UPDATED },
					this.getClass().toString());

		} catch (TrustException te) {

			LOG.error("Trust Broker sucks: " + te.getLocalizedMessage(), te);
		}
	}

	private class MyTrustUpdateEventListener implements ITrustUpdateEventListener {

		/*
		 * (non-Javadoc)
		 * @see org.societies.api.internal.privacytrust.trust.event.ITrustUpdateEventListener#onUpdate(org.societies.api.internal.privacytrust.trust.event.TrustUpdateEvent)
		 */
		@Override
		public void onUpdate(TrustUpdateEvent event) {

			LOG.info("*** TrustUpdateEvent " + event);
		}
	}
}
