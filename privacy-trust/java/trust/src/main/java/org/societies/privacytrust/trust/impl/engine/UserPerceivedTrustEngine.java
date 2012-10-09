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

import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.engine.IUserPerceivedTrustEngine;
import org.societies.privacytrust.trust.api.engine.TrustEngineException;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.event.TrustEventMgrException;
import org.societies.privacytrust.trust.api.event.TrustEventTopic;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
@Service
public class UserPerceivedTrustEngine extends TrustEngine implements IUserPerceivedTrustEngine {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(UserPerceivedTrustEngine.class);
	
	@Autowired
	public UserPerceivedTrustEngine(ITrustEventMgr trustEventMgr) throws TrustEventMgrException {
		
		super(trustEventMgr);
		LOG.info(this.getClass() + " instantiated");
		
		LOG.info("Registering for direct and indirect trust updates...");
		super.trustEventMgr.registerListener(
				new TrustUpdateListener(), 
				new String[] { TrustEventTopic.DIRECT_TRUST_UPDATED,
					TrustEventTopic.INDIRECT_TRUST_UPDATED }, null);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.engine.IUserPerceivedTrustEngine#evaluateCssTrustValues(java.util.List)
	 */
	@Override
	public void evaluateCssTrustValues(final List<ITrustedCss> cssList)
			throws TrustEngineException {
		
		for (final ITrustedCss css : cssList) {
			final double directTrustValue = (css.getDirectTrust().getValue() != null) 
					? css.getDirectTrust().getValue() : 0.0d;
			final double indirectTrustValue = (css.getIndirectTrust().getValue() != null)
					? css.getIndirectTrust().getValue() : 0.0d;
			final double bias = (css.getIndirectTrust().getValue() != null 
					&& css.getIndirectTrust().getConfidence() != null)
					? css.getIndirectTrust().getConfidence() : 0.0d;
			final double userPerceivedTrustValue = (1-bias) * directTrustValue + bias * indirectTrustValue;
			css.getUserPerceivedTrust().setValue(userPerceivedTrustValue);
		}
		
		if (LOG.isDebugEnabled())
			LOG.debug("Evaluated user-perceived trust for entities: " + cssList);
	}

	/*
	 * @see org.societies.privacytrust.trust.api.engine.IUserPerceivedTrustEngine#evaluateCisTrustValues(java.util.List)
	 */
	@Override
	public void evaluateCisTrustValues(final List<ITrustedCis> cisList)
			throws TrustEngineException {
		
		for (final ITrustedCis cis : cisList) {
			final double directTrustValue = (cis.getDirectTrust().getValue() != null)
					? cis.getDirectTrust().getValue() : 0.0d;
			final double indirectTrustValue = (cis.getIndirectTrust().getValue() != null)
					? cis.getIndirectTrust().getValue() : 0.0d;
			final double bias = (cis.getIndirectTrust().getValue() != null
					&& cis.getIndirectTrust().getConfidence() != null)
					? cis.getIndirectTrust().getConfidence() : 0.0d;
			final double userPerceivedTrustValue = (1-bias) * directTrustValue + bias * indirectTrustValue;
			cis.getUserPerceivedTrust().setValue(userPerceivedTrustValue);
		}
		
		if (LOG.isDebugEnabled())
			LOG.debug("Evaluated user-perceived trust for entities: " + cisList);
	}

	/*
	 * @see org.societies.privacytrust.trust.api.engine.IUserPerceivedTrustEngine#evaluateServiceTrustValues(java.util.List)
	 */
	@Override
	public void evaluateServiceTrustValues(final List<ITrustedService> serviceList)
			throws TrustEngineException {
		
		for (final ITrustedService service : serviceList) {
			final double directTrustValue = (service.getDirectTrust().getValue() != null)
					? service.getDirectTrust().getValue() : 0.0d;
			final double indirectTrustValue = (service.getIndirectTrust().getValue() != null)
					? service.getIndirectTrust().getValue() : 0.0d;
			final double bias = (service.getIndirectTrust().getValue() != null
					&& service.getIndirectTrust().getConfidence() != null)
					? service.getIndirectTrust().getConfidence() : 0.0d;
			final double userPerceivedTrustValue = (1-bias) * directTrustValue + bias * indirectTrustValue;
			service.getUserPerceivedTrust().setValue(userPerceivedTrustValue);
		}
		
		if (LOG.isDebugEnabled())
			LOG.debug("Evaluated user-perceived trust for entities: " + serviceList);
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
				final ITrustedCss css = (ITrustedCss) trustRepo.retrieveEntity(teid);
				if (css == null) {
					LOG.error("Could not (re)evaluate user-perceived trust for entity "
						+ teid + ": Entity not found in the trust repository");
					return;
				}
				
				final List<ITrustedCss> cssList = new ArrayList<ITrustedCss>(1);
				cssList.add(css);
				evaluateCssTrustValues(cssList);
				
				for (final ITrustedCss evaluatedCss : cssList)
					trustRepo.updateEntity(evaluatedCss);
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
			
			if (LOG.isDebugEnabled())
				LOG.debug("Running CisDirectTrustEngine for entity " + teid);
			
			try {
				final ITrustedCis cis = (ITrustedCis) trustRepo.retrieveEntity(teid);
				if (cis == null) {
					LOG.error("Could not (re)evaluate user-perceived trust for entity "
						+ teid + ": Entity not found in the trust repository");
					return;
				}
				
				final List<ITrustedCis> cisList = new ArrayList<ITrustedCis>(1);
				cisList.add(cis);
				evaluateCisTrustValues(cisList);
				
				for (final ITrustedCis evaluatedCis : cisList)
					trustRepo.updateEntity(evaluatedCis);
			} catch (TrustException te) {
				
				LOG.error("Could not (re)evaluate user-perceived trust for entity "
						+ teid + ": " + te.getLocalizedMessage(), te);
			}
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
			
			if (LOG.isDebugEnabled())
				LOG.debug("Running ServiceDirectTrustEngine for entity " + teid);
			
			try {
				final ITrustedService service = (ITrustedService) trustRepo.retrieveEntity(teid);
				if (service == null) {
					LOG.error("Could not (re)evaluate user-perceived trust for entity "
						+ teid + ": Entity not found in the trust repository");
					return;
				}
				
				final List<ITrustedService> serviceList = new ArrayList<ITrustedService>(1);
				serviceList.add(service);
				evaluateServiceTrustValues(serviceList);
				
				for (final ITrustedService evaluatedService : serviceList)
					trustRepo.updateEntity(evaluatedService);
			} catch (TrustException te) {
				
				LOG.error("Could not (re)evaluate user-perceived trust for entity "
						+ teid + ": " + te.getLocalizedMessage(), te);
			}
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