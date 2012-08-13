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
package org.societies.privacytrust.example.trust.evidence;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides examples for using the internal Context Broker in OSGi. 
 */
@Service
public class TrustEvidenceCollectorClient {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(TrustEvidenceCollectorClient.class);
	
	private static final String TRUSTED_CSS_ID1 = "trustedCss1@societies.local";
	
	private final IIdentity trustedCssId1;
	
	/** The trustor's IIdentity. */
	private final IIdentity trustorId;

	/** The Internal Context Broker service reference. */
	private ITrustEvidenceCollector trustEvidenceCollector;

	@Autowired(required=true)
	public TrustEvidenceCollectorClient(ITrustEvidenceCollector trustEvidenceCollector, ICommManager commMgr) throws InvalidFormatException {

		LOG.info("*** " + this.getClass() + " instantiated");
		this.trustEvidenceCollector = trustEvidenceCollector;
		
		this.trustorId = commMgr.getIdManager().fromJid(
				//"trustorCss@societies.local");
				commMgr.getIdManager().getThisNetworkNode().getBareJid());
		
		this.trustedCssId1 = commMgr.getIdManager().fromJid(TRUSTED_CSS_ID1);
		
		LOG.info("*** Starting examples...");
		this.addDirectTrustEvidence();
	}

	/**
	 * This method demonstrates how to add direct trust evidence through the
	 * trust evidence collector.
	 */
	private void addDirectTrustEvidence() {

		LOG.info("*** addDirectTrustEvidence");

		try {
			final Double trustRating1 = 0.5d;
			final Date timestamp1 = new Date();
			LOG.info("*** adding trust rating: '" + this.trustorId + "," 
					+ this.trustedCssId1 + "," + trustRating1 + "," 
					+ timestamp1);
			this.trustEvidenceCollector.addDirectEvidence(
					new TrustedEntityId(this.trustorId.toString(), TrustedEntityType.CSS, this.trustedCssId1.toString()),
					TrustEvidenceType.RATED, timestamp1, trustRating1);
			/*
			final Double trustRating2 = 0.8d;
			final Date timestamp2 = new Date();
			LOG.info("*** adding trust rating: '" + this.trustorId + "," 
					+ this.trustedCssId1 + "," + trustRating2 + "," 
					+ timestamp1);
			this.trustEvidenceCollector.addTrustRating(this.trustorId, this.trustedCssId1,
					trustRating2, timestamp1);*/
		} catch (Exception e) {
			
			LOG.error("Trust Evidence Collector sucks: " + e.getLocalizedMessage(), e);
		}
	}
}