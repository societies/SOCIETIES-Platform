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
package org.societies.privacytrust.trust.impl.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener;
import org.societies.privacytrust.trust.api.event.TrustEventTopic;
import org.societies.privacytrust.trust.api.event.TrustEvidenceUpdateEvent;
import org.societies.privacytrust.trust.api.evidence.model.IIndirectTrustEvidence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
@Service
public class IndirectTrustEngine extends TrustEngine {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(IndirectTrustEngine.class);
	
	@Autowired
	IndirectTrustEngine(ITrustEventMgr trustEventMgr) throws Exception {
		
		super(trustEventMgr);
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		
		try {
			LOG.info("Registering for indirect trust evidence updates...");
			super.trustEventMgr.registerEvidenceUpdateListener(
					new IndirectTrustEvidenceUpdateListener(), 
					new String[] { TrustEventTopic.INDIRECT_TRUST_EVIDENCE_UPDATED });
		} catch (Exception e) {
			LOG.error(this.getClass() + " could not be initialised: "
					+ e.getLocalizedMessage(), e);
			throw e;
		}
	}
	
	private class IndirectTrustEvidenceHandler implements Runnable {

		private final IIndirectTrustEvidence evidence;
		
		private IndirectTrustEvidenceHandler(final IIndirectTrustEvidence evidence) {
			
			this.evidence = evidence;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
		
			if (LOG.isDebugEnabled())
				LOG.debug("Handling evidence " + this.evidence);
			
			//try {
				for (final TrustedEntityId myId : IndirectTrustEngine.super.trustNodeMgr.getMyIds())
					;//TODO evaluate(myId, evidence);
			//} catch (TrustException te) {
				
			//	LOG.error("Could not handle evidence "
			//			+ evidence + ": " + te.getLocalizedMessage(), te);
			//}
		} 
	}
	
	private class IndirectTrustEvidenceUpdateListener implements ITrustEvidenceUpdateEventListener {

		/*
		 * @see org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener#onNew(org.societies.privacytrust.trust.api.event.TrustEvidenceUpdateEvent)
		 */
		@Override
		public void onNew(TrustEvidenceUpdateEvent evt) {
			
			if (LOG.isDebugEnabled())
				LOG.debug("Received indirect TrustEvidenceUpdateEvent " + evt);
			
			if (!(evt.getSource() instanceof IIndirectTrustEvidence)) {
				LOG.error("TrustEvidenceUpdateEvent source is not instance of IIndirectTrustEvidence");
				return;
			}
			final IIndirectTrustEvidence evidence = (IIndirectTrustEvidence) evt.getSource();
			executorService.execute(new IndirectTrustEvidenceHandler(evidence));
		}
	}
}