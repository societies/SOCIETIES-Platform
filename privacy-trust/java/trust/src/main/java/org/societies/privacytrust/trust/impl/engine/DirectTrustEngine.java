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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.engine.IDirectTrustEngine;
import org.societies.privacytrust.trust.api.engine.TrustEngineException;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener;
import org.societies.privacytrust.trust.api.event.TrustEventMgrException;
import org.societies.privacytrust.trust.api.event.TrustEventTopic;
import org.societies.privacytrust.trust.api.event.TrustEvidenceUpdateEvent;
import org.societies.privacytrust.trust.api.evidence.model.IDirectTrustEvidence;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedService;
import org.societies.privacytrust.trust.impl.engine.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
@Service
public class DirectTrustEngine extends TrustEngine implements IDirectTrustEngine {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(DirectTrustEngine.class);
	
	public static final Map<TrustEvidenceType, Double> EVIDENCE_SCORE_MAP;
    
	static {
		
        final Map<TrustEvidenceType, Double> aMap = new HashMap<TrustEvidenceType, Double>();
        aMap.put(TrustEvidenceType.FRIENDED_USER, +5.0d);
        aMap.put(TrustEvidenceType.UNFRIENDED_USER, -50.0d);
        aMap.put(TrustEvidenceType.USED_SERVICE, +1.0d);
        EVIDENCE_SCORE_MAP = Collections.unmodifiableMap(aMap);
    }
	
	/** The Trust Evidence Repository service reference. */
	@Autowired
	private ITrustEvidenceRepository trustEvidenceRepo;
	
	@Autowired
	public DirectTrustEngine(ITrustEventMgr trustEventMgr) throws TrustEventMgrException {
		
		super(trustEventMgr);
		LOG.info(this.getClass() + " instantiated");
		
		LOG.info("Registering for direct trust evidence updates...");
		super.trustEventMgr.registerListener(
				new DirectTrustEvidenceUpdateListener(), 
				new String[] { TrustEventTopic.DIRECT_TRUST_EVIDENCE_UPDATED }, null);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluateCss(java.util.List, java.util.List)
	 */
	@Override
	public void evaluateCss(final List<ITrustedCss> cssList, 
			final List<ITrustEvidence> evidenceList) throws TrustEngineException {
		
		// create <TEID,CSS> map
		final Map<TrustedEntityId, ITrustedCss> cssMap = 
				new HashMap<TrustedEntityId, ITrustedCss>(cssList.size());
		for (final ITrustedCss css : cssList)
			cssMap.put(css.getTeid(), css);
		
		// create sorted evidence list based on the evidence timestamps
		final List<ITrustEvidence> sortedEvidenceList = new ArrayList<ITrustEvidence>(evidenceList);
		Collections.sort(sortedEvidenceList);
		
		// re-evaluate trust ratings/scores
		for (final ITrustEvidence evidence : sortedEvidenceList) {
			final ITrustedCss css = cssMap.get(evidence.getTeid());
			if (css != null) {
				switch (evidence.getType()) {
				case RATED:
					// replace previous rating with new one
					css.getDirectTrust().setRating((Double) evidence.getInfo());
					break;
				case FRIENDED_USER:
					// add FRIENDED_USER score  to previous score
					css.getDirectTrust().setScore(new Double(
							css.getDirectTrust().getScore() 
							+ EVIDENCE_SCORE_MAP.get(evidence.getType())));
					break;
				case UNFRIENDED_USER:
					// add UNFRIENDED_USER score to previous score
					css.getDirectTrust().setScore(new Double(
							css.getDirectTrust().getScore() 
							+ EVIDENCE_SCORE_MAP.get(evidence.getType())));
					break;
				case JOINED_COMMUNITY:
					css.addCommunity(community); // TODO
					break;
				case LEFT_COMMUNITY:
					//css.removeCommunity(community); // TODO
					break;
				default:
					LOG.warn("Ignoring evidence '" + evidence 
							+ "': Unsupported type: " + evidence.getType());
					break;
				}
			} else {
				LOG.warn("Ignoring evidence '" + evidence
						+ "': Unrelated TEID: " + evidence.getTeid());
			}
		}
		
		// re-evaluate trust values
		double[] rawTrustScores = new double[cssList.size()];
		for (int i = 0; i < cssList.size(); ++i)
			rawTrustScores[i] = cssList.get(i).getDirectTrust().getScore();
		
		double[] normTrustScores = MathUtils.stanine(rawTrustScores);
		for (int i = 0; i < cssList.size(); ++i) {
			final Double rating = cssList.get(i).getDirectTrust().getRating();
			final Double score = 0.1d * normTrustScores[i]; 
			cssList.get(i).getDirectTrust().setValue(
					(rating != null) 
						? (0.5d * score + 0.5d * rating)
								: score);
		}
		
		if (LOG.isDebugEnabled())
			LOG.debug("Evaluated direct trust for entities: " + cssList);
	}

	/*
	 * @see org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluateCis(java.util.List, java.util.List)
	 */
	@Override
	public void evaluateCis(final List<ITrustedCis> cisList,
			final List<ITrustEvidence> evidenceList) throws TrustEngineException {
		
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("cisList=" + cisList + ", evidenceList=" + evidenceList);
		// create <TEID,CIS> map
		final Map<TrustedEntityId, ITrustedCis> cisMap = 
				new HashMap<TrustedEntityId, ITrustedCis>(cisList.size());
		for (final ITrustedCis cis : cisList)
			cisMap.put(cis.getTeid(), cis);
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("cisMap=" + cisMap);

		// create sorted evidence list based on the evidence timestamps
		final List<ITrustEvidence> sortedEvidenceList = new ArrayList<ITrustEvidence>(evidenceList);
		Collections.sort(sortedEvidenceList);
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("sortedEvidenceList=" + sortedEvidenceList);

		// re-evaluate trust ratings/scores
		for (final ITrustEvidence evidence : sortedEvidenceList) {
			final ITrustedCis cis = cisMap.get(evidence.getTeid());
			if (cis != null) {
				if (LOG.isInfoEnabled()) // TODO DEBUG
					LOG.info("CIS '" + cis.getTeid() + "' direct trust before evidence evaluation: " 
							+ cis.getDirectTrust());
				switch (evidence.getType()) {
				case RATED:
					// replace previous rating with new one
					cis.getDirectTrust().setRating((Double) evidence.getInfo());
					break;
				case JOINED_COMMUNITY:
				case LEFT_COMMUNITY:
					// weakest link
					if (cis.getMembers().size() == 0) {
						cis.getDirectTrust().setScore(0d);
						cis.getDirectTrust().setRating(null);
						break;
					}
					final List<Double> memberScoreList = new ArrayList<Double>(cis.getMembers().size());
					for (final ITrustedCss member : cis.getMembers())
						if (member.getDirectTrust().getScore() != null)
							memberScoreList.add(member.getDirectTrust().getScore());
					final double[] memberScoreArray = new double[memberScoreList.size()];
					for (int i = 0; i < memberScoreArray.length; ++i)
						memberScoreArray[i] = memberScoreList.get(i);
					cis.getDirectTrust().setScore(MathUtils.min(memberScoreArray));
					if (cis.getDirectTrust().getRating() == null) {
						final List<Double> memberRatingList = new ArrayList<Double>(cis.getMembers().size());
						for (final ITrustedCss member : cis.getMembers())
							if (member.getDirectTrust().getRating() != null)
								memberRatingList.add(member.getDirectTrust().getRating());
						final double[] memberRatingArray = new double[memberRatingList.size()];
						for (int i = 0; i < memberRatingArray.length; ++i)
							memberRatingArray[i] = memberRatingList.get(i);
						cis.getDirectTrust().setRating(MathUtils.min(memberScoreArray));
					}
					break;
				default:
					LOG.warn("Ignoring evidence '" + evidence // TODO DEBUG 
							+ "': Unsupported type: " + evidence.getType());
					break;
				}
				if (LOG.isInfoEnabled()) // TODO DEBUG
					LOG.info("CIS '" + cis.getTeid() + "' direct trust after evidence evaluation: " 
							+ cis.getDirectTrust());
			} else {
				LOG.warn("Ignoring evidence '" + evidence
						+ "': Unrelated TEID: " + evidence.getTeid());
			}
		}

		// re-evaluate trust values
		double[] rawTrustScores = new double[cisList.size()];
		for (int i = 0; i < cisList.size(); ++i)
			rawTrustScores[i] = cisList.get(i).getDirectTrust().getScore();

		double[] normTrustScores = MathUtils.stanine(rawTrustScores);
		for (int i = 0; i < cisList.size(); ++i) {
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("CIS '" + cisList.get(i).getTeid() + "' direct trust before normalisation: "
						+ cisList.get(i).getDirectTrust());
			final Double rating = cisList.get(i).getDirectTrust().getRating();
			final Double score = 0.1d * normTrustScores[i]; 
			cisList.get(i).getDirectTrust().setValue(
					(rating != null) 
					? (0.5d * score + 0.5d * rating)
							: score);
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("CIS '" + cisList.get(i).getTeid() + "' direct trust after normalisation: "
						+ cisList.get(i).getDirectTrust());
		}

		if (LOG.isDebugEnabled())
			LOG.debug("Evaluated direct trust for entities: " + cisList);
	}

	/*
	 * @see org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluateService(java.util.List, java.util.List)
	 */
	@Override
	public void evaluateService(List<ITrustedService> serviceList,
			final List<ITrustEvidence> evidenceList) throws TrustEngineException {
		
		// create <TEID,Service> map
		final Map<TrustedEntityId, ITrustedService> serviceMap = 
				new HashMap<TrustedEntityId, ITrustedService>(serviceList.size());
		for (final ITrustedService service : serviceList)
			serviceMap.put(service.getTeid(), service);

		// create sorted evidence list based on the evidence timestamps
		final List<ITrustEvidence> sortedEvidenceList = new ArrayList<ITrustEvidence>(evidenceList);
		Collections.sort(sortedEvidenceList);

		// re-evaluate trust ratings/scores
		for (final ITrustEvidence evidence : sortedEvidenceList) {
			final ITrustedService service = serviceMap.get(evidence.getTeid());
			if (service != null) {
				switch (evidence.getType()) {
				case RATED:
					// replace previous rating with new one
					service.getDirectTrust().setRating((Double) evidence.getInfo());
					break;
				case USED_SERVICE:
					// add USED_SERVICE score  to previous score
					service.getDirectTrust().setScore(new Double(
							service.getDirectTrust().getScore() 
							+ EVIDENCE_SCORE_MAP.get(evidence.getType())));
					break;
				default:
					LOG.warn("Ignoring evidence '" + evidence 
							+ "': Unsupported type: " + evidence.getType());
					break;
				}
			} else {
				LOG.warn("Ignoring evidence '" + evidence
						+ "': Unrelated TEID: " + evidence.getTeid());
			}
		}

		// re-evaluate trust values
		double[] rawTrustScores = new double[serviceList.size()];
		for (int i = 0; i < serviceList.size(); ++i)
			rawTrustScores[i] = serviceList.get(i).getDirectTrust().getScore();

		double[] normTrustScores = MathUtils.stanine(rawTrustScores);
		for (int i = 0; i < serviceList.size(); ++i) {
			final Double rating = serviceList.get(i).getDirectTrust().getRating();
			final Double score = 0.1d * normTrustScores[i]; 
			serviceList.get(i).getDirectTrust().setValue(
					(rating != null) 
					? (0.5d * score + 0.5d * rating)
							: score);
		}

		if (LOG.isDebugEnabled())
			LOG.debug("Evaluated direct trust for entities: " + serviceList);
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
		
			if (LOG.isDebugEnabled())
				LOG.debug("Running CssDirectTrustEngine with evidence "
						+ this.evidence);
			
			try {
				// if there is no TrustedEntity associated with the specified
				// TrustEvidence, then create it
				if (trustRepo.retrieveEntity(this.evidence.getTeid()) == null)
					trustRepo.createEntity(this.evidence.getTeid());
				
				// retrieve all TrustedEntities trusted by the trustor
				// referenced in the specified TrustEvidence
				final List<ITrustedCss> cssList = trustRepo.retrieveEntities(
						this.evidence.getTeid().getTrustorId(), ITrustedCss.class);
				if (LOG.isInfoEnabled()) // TODO DEBUG
					LOG.info("cssList=" + cssList);
					
				// prepare list of TrustEvidence
				final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>();
				evidenceList.add(this.evidence);
				
				// evaluate TrustedEntities based on the TrustEvidence list
				evaluateCss(cssList, evidenceList);
				
				// persist updated TrustValues in the Trust Repository
				for (final ITrustedCss css : cssList)
					trustRepo.updateEntity(css);
			} catch (TrustException te) {
				
				LOG.error("Could not (re)evaluate direct trust values using evidence "
						+ evidence + ": " + te.getLocalizedMessage(), te);
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
			
			if (LOG.isDebugEnabled())
				LOG.debug("Running CisDirectTrustEngine with evidence " 
						+ this.evidence);
			
			try {
				// if there is no TrustedEntity associated with the specified
				// TrustEvidence, then create it
				if (trustRepo.retrieveEntity(this.evidence.getTeid()) == null)
					trustRepo.createEntity(this.evidence.getTeid());
				
				// retrieve all TrustedEntities trusted by the trustor
				// referenced in the specified TrustEvidence
				final List<ITrustedCis> cisList = trustRepo.retrieveEntities(
						this.evidence.getTeid().getTrustorId(), ITrustedCis.class);
					
				// prepare list of TrustEvidence
				final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>();
				evidenceList.add(this.evidence);
				
				// evaluate TrustedEntities based on the TrustEvidence list
				evaluateCis(cisList, evidenceList);
				
				// persist updated TrustValues in the Trust Repository
				for (final ITrustedCis cis : cisList)
					trustRepo.updateEntity(cis);
			} catch (TrustException te) {
				
				LOG.error("Could not (re)evaluate direct trust values using evidence "
						+ evidence + ": " + te.getLocalizedMessage(), te);
			}
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
			
			if (LOG.isDebugEnabled())
				LOG.debug("Running ServiceDirectTrustEngine with evidence "	
						+ this.evidence);
			
			try {
				// if there is no TrustedEntity associated with the specified
				// TrustEvidence, then create it
				if (trustRepo.retrieveEntity(this.evidence.getTeid()) == null)
					trustRepo.createEntity(this.evidence.getTeid());
				
				// retrieve all TrustedEntities trusted by the trustor
				// referenced in the specified TrustEvidence
				final List<ITrustedService> serviceList = trustRepo.retrieveEntities(
						this.evidence.getTeid().getTrustorId(), ITrustedService.class);
					
				// prepare list of TrustEvidence
				final List<ITrustEvidence> evidenceList = new ArrayList<ITrustEvidence>();
				evidenceList.add(this.evidence);
				
				// evaluate TrustedEntities based on the TrustEvidence list
				evaluateService(serviceList, evidenceList);
				
				// persist updated TrustValues in the Trust Repository
				for (final ITrustedService service : serviceList)
					trustRepo.updateEntity(service);
			} catch (TrustException te) {
				
				LOG.error("Could not (re)evaluate direct trust values using evidence "
						+ evidence + ": " + te.getLocalizedMessage(), te);
			}
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
			if (TrustedEntityType.CSS.equals(entityType) || TrustedEntityType.CIS.equals(entityType)) {
				executorService.execute(new CssDirectTrustEngine(evidence));
				executorService.execute(new CisDirectTrustEngine(evidence));
			} else if (TrustedEntityType.SVC.equals(entityType))
				executorService.execute(new ServiceDirectTrustEngine(evidence));
			else
				LOG.error("Unsupported trusted entity type: " + entityType);
		}
	}
}