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

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.trust.TrustException;
import org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.engine.TrustEngineException;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener;
import org.societies.privacytrust.trust.api.event.TrustEventMgrException;
import org.societies.privacytrust.trust.api.event.TrustEventTopic;
import org.societies.privacytrust.trust.api.event.TrustEvidenceUpdateEvent;
import org.societies.privacytrust.trust.api.evidence.model.IDirectTrustEvidence;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.api.model.ITrust;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedService;
import org.societies.privacytrust.trust.impl.repo.model.TrustedCss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
@Service
public class DirectTrustEngine extends TrustEngine {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(DirectTrustEngine.class);
	
	/** The Trust Evidence Repository service reference. */
	@Autowired
	private ITrustEvidenceRepository trustEvidenceRepo;
	
	@Autowired
	DirectTrustEngine(ITrustEventMgr trustEventMgr) throws TrustEventMgrException {
		
		super(trustEventMgr);
		LOG.info(this.getClass() + " instantiated");
		
		LOG.info("Registering for direct trust evidence updates...");
		super.trustEventMgr.registerListener(
				new DirectTrustEvidenceUpdateListener(), 
				new String[] { TrustEventTopic.DIRECT_TRUST_EVIDENCE_UPDATED }, null);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.engine.ITrustEngine#evaluate(org.societies.privacytrust.trust.api.model.ITrustedCss, java.util.Set)
	 */
	@Override
	public ITrust evaluate(ITrustedCss css, Set<ITrustEvidence> evidenceSet)
			throws TrustEngineException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.engine.ITrustEngine#evaluate(org.societies.privacytrust.trust.api.model.ITrustedCis, java.util.Set)
	 */
	@Override
	public ITrust evaluate(ITrustedCis cis, Set<ITrustEvidence> evidenceSet)
			throws TrustEngineException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.engine.ITrustEngine#evaluate(org.societies.privacytrust.trust.api.model.ITrustedService, java.util.Set)
	 */
	@Override
	public ITrust evaluate(ITrustedService service,
			Set<ITrustEvidence> evidenceSet) throws TrustEngineException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class CssDirectTrustEngine implements Runnable {

		private final IDirectTrustEvidence evidence;
		
		private CssDirectTrustEngine(final IDirectTrustEvidence evidence) {
			
			this.evidence = evidence;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (LOG.isDebugEnabled())
				LOG.debug("Running CssDirectTrustEngine with evidence "
						+ this.evidence);
			
			try {
				Double newTrust = null;
				ITrustedCss css = (ITrustedCss) trustRepo.retrieveEntity(this.evidence.getTeid());
				if (css == null)
					css = new TrustedCss(this.evidence.getTeid());
				if (TrustEvidenceType.RATED.equals(evidence.getType()))
					newTrust = (Double) evidence.getInfo();
				css.getDirectTrust().setValue(newTrust);
				trustRepo.updateEntity(css);
			} catch (TrustException te) {
				
				LOG.error("Could not (re)evaluate direct trust for entity "
						+ evidence.getTeid() + ": " + te.getLocalizedMessage(), te);
			}
		} 
	}
	
	private class CisDirectTrustEngine implements Runnable {

		private final IDirectTrustEvidence evidence;
		
		private CisDirectTrustEngine(final IDirectTrustEvidence evidence) {
			
			this.evidence = evidence;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (LOG.isDebugEnabled())
				LOG.debug("Running CisDirectTrustEngine with evidence " 
						+ this.evidence);
		} 
	}
	
	private class ServiceDirectTrustEngine implements Runnable {

		private final IDirectTrustEvidence evidence;
		
		private ServiceDirectTrustEngine(final IDirectTrustEvidence evidence) {
			
			this.evidence = evidence;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (LOG.isDebugEnabled())
				LOG.debug("Running ServiceDirectTrustEngine with evidence "	
						+ this.evidence);
		} 
	}
	
	private class DirectTrustEvidenceUpdateListener implements ITrustEvidenceUpdateEventListener {

		/*
		 * @see org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener#onNew(org.societies.privacytrust.trust.api.event.TrustEvidenceUpdateEvent)
		 */
		@Override
		public void onNew(TrustEvidenceUpdateEvent evt) {
			
			if (LOG.isDebugEnabled())
				LOG.debug("Received direct TrustEvidenceUpdateEvent " + evt);
			
			if (!(evt.getSource() instanceof IDirectTrustEvidence)) {
				LOG.error("TrustEvidenceUpdateEvent source is not instance of IDirectTrustEvidence");
				return;
			}
			final IDirectTrustEvidence evidence = (IDirectTrustEvidence) evt.getSource();
			final TrustedEntityType entityType = evidence.getTeid().getEntityType();
			if (TrustedEntityType.CSS.equals(entityType))
				executorService.execute(new CssDirectTrustEngine(evidence));
			else if (TrustedEntityType.CIS.equals(entityType))
				executorService.execute(new CisDirectTrustEngine(evidence));
			else if (TrustedEntityType.SVC.equals(entityType))
				executorService.execute(new ServiceDirectTrustEngine(evidence));
			else
				LOG.error("Unsupported trusted entity type: " + entityType);
		}
	}
}