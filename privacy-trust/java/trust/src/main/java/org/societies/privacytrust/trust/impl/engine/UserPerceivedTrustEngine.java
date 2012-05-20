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
import org.societies.api.internal.privacytrust.trust.TrustException;
import org.societies.api.internal.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.internal.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.event.TrustEventMgrException;
import org.societies.privacytrust.trust.api.event.TrustEventTopic;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
@Service
public class UserPerceivedTrustEngine extends TrustEngine {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(UserPerceivedTrustEngine.class);
	
	private static final double a = 0.5d;
	
	@Autowired
	UserPerceivedTrustEngine(ITrustEventMgr trustEventMgr) throws TrustEventMgrException {
		
		super(trustEventMgr);
		LOG.info(this.getClass() + " instantiated");
		
		LOG.info("Registering for direct and indirect trust updates...");
		super.trustEventMgr.registerListener(
				new TrustUpdateListener(), 
				new String[] { TrustEventTopic.DIRECT_TRUST_UPDATED,
					TrustEventTopic.INDIRECT_TRUST_UPDATED }, null);
	}
	
	private class CssUserPerceivedTrustEngine implements Runnable {

		private final TrustedEntityId teid;
		
		private CssUserPerceivedTrustEngine(final TrustedEntityId teid) {
			
			this.teid = teid;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			if (LOG.isDebugEnabled())
				LOG.debug("Running CssUserPerceivedTrustEngine for entity " + teid);
			
			try {
				ITrustedCss css = (ITrustedCss) trustRepo.retrieveEntity(teid);
				if (css == null) {
					LOG.error("Could not (re)evaluate user-perceived trust for entity "
						+ teid + ": Entity not found in the trust repository");
					return;
				}
				final double directTrustValue = (css.getDirectTrust().getValue() != null)
						? css.getDirectTrust().getValue() : 0.0d;
				final double indirectTrustValue = (css.getIndirectTrust().getValue() != null)
						? css.getIndirectTrust().getValue() : 0.0d;
				final double userPerceivedTrustValue = a * directTrustValue + (1-a) * indirectTrustValue;
				css.getDirectTrust().setValue(userPerceivedTrustValue);
				trustRepo.updateEntity(css);
			} catch (TrustException te) {
				
				LOG.error("Could not (re)evaluate user-perceived trust for entity "
						+ teid + ": " + te.getLocalizedMessage(), te);
			}
		} 
	}
	
	private class CisUserPerceivedTrustEngine implements Runnable {

		private final TrustedEntityId teid;
		
		private CisUserPerceivedTrustEngine(final TrustedEntityId teid) {
			
			this.teid = teid;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (LOG.isDebugEnabled())
				LOG.debug("Running CisDirectTrustEngine for entity " + teid);
		} 
	}
	
	private class ServiceUserPerceivedTrustEngine implements Runnable {

		private final TrustedEntityId teid;
		
		private ServiceUserPerceivedTrustEngine(final TrustedEntityId teid) {
			
			this.teid = teid;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (LOG.isDebugEnabled())
				LOG.debug("Running ServiceDirectTrustEngine for entity " + teid);
		} 
	}
	
	private class TrustUpdateListener implements ITrustUpdateEventListener {

		/*
		 * @see org.societies.api.internal.privacytrust.trust.event.ITrustUpdateEventListener#onUpdate(org.societies.api.internal.privacytrust.trust.event.TrustUpdateEvent)
		 */
		@Override
		public void onUpdate(TrustUpdateEvent evt) {
			
			if (LOG.isDebugEnabled())
				LOG.debug("Received TrustUpdateEvent " + evt);
			
			final TrustedEntityId teid = evt.getId();
			final TrustedEntityType entityType = teid.getEntityType();
			if (TrustedEntityType.CSS.equals(entityType))
				executorService.execute(new CssUserPerceivedTrustEngine(teid));
			else if (TrustedEntityType.CIS.equals(entityType))
				executorService.execute(new CisUserPerceivedTrustEngine(teid));
			else if (TrustedEntityType.SVC.equals(entityType))
				executorService.execute(new ServiceUserPerceivedTrustEngine(teid));
			else
				LOG.warn("Unsupported trusted entity type: " + entityType);
		}		
	}
}