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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.engine.IIndirectTrustEngine;
import org.societies.privacytrust.trust.api.engine.TrustEngineException;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener;
import org.societies.privacytrust.trust.api.event.TrustEventTopic;
import org.societies.privacytrust.trust.api.event.TrustEvidenceUpdateEvent;
import org.societies.privacytrust.trust.api.evidence.model.IIndirectTrustEvidence;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;
import org.societies.privacytrust.trust.api.similarity.ITrustSimilarityEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
@Service
public class IndirectTrustEngine extends TrustEngine implements IIndirectTrustEngine {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(IndirectTrustEngine.class);
	
	@Autowired(required=true)
	private ITrustSimilarityEvaluator trustSimilarityEvaluator;
	
	@Autowired(required=true)
	IndirectTrustEngine(ITrustEventMgr trustEventMgr) throws Exception {
		
		super(trustEventMgr);
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		
		try {
			if (LOG.isInfoEnabled())
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
	
	/*
	 * @see org.societies.privacytrust.trust.api.engine.IIndirectTrustEngine#evaluate(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.privacytrust.trust.api.evidence.model.IIndirectTrustEvidence)
	 */
	@Override
	public Set<ITrustedEntity> evaluate(final TrustedEntityId trustorId, 
			final IIndirectTrustEvidence evidence) throws TrustEngineException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Evaluating indirect trust evidence " + evidence
					+ " on behalf of '" + trustorId + "'");
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (evidence == null)
			throw new NullPointerException("evidence can't be null");
		
		final Set<ITrustedEntity> resultSet = new HashSet<ITrustedEntity>();
		
		if (!this.areRelevant(trustorId, evidence))
			return resultSet;

		try {
			// Does similarity between trustor and subject needs re-evaluation?
			boolean doSimilarityEval = false;
			// Create the trusted entity the evidence object refers to if not already available
			ITrustedEntity trustee = (ITrustedEntity) this.trustRepo.retrieveEntity(
					trustorId, evidence.getObjectId());
			if (trustee == null)
				trustee = super.trustRepo.createEntity(trustorId, evidence.getObjectId());
			else
				doSimilarityEval = true;
			if (LOG.isDebugEnabled())
				LOG.debug("doSimilarity=" + doSimilarityEval);
			resultSet.add(trustee);
			
			switch (evidence.getType()) {

			// Update value
			case DIRECTLY_TRUSTED:
				// Check if similarity between trustor and subject needs re-evaluation
				if (doSimilarityEval && TrustedEntityType.CSS == evidence.getSubjectId().getEntityType()) {
					final ITrustedCss subject = (ITrustedCss)
							super.createEntityIfAbsent(trustorId, evidence.getSubjectId());
					final Double similarity = this.trustSimilarityEvaluator
							.evaluateCosineSimilarity(trustorId, evidence.getSubjectId());
					if (LOG.isDebugEnabled())
						LOG.debug("similarity=" + similarity);
					if (similarity != null && !Double.isNaN(similarity)) {
						subject.setSimilarity(similarity);
						super.trustRepo.updateEntity(subject);
					}
				}
				// Fetch top N users
				if (LOG.isDebugEnabled())
					LOG.debug("Retrieving trusted users based on similarity");
				final Map<TrustedEntityId, ITrustedCss> topNCssMap =
						this.retrieveTopNCss(trustorId);
				if (LOG.isDebugEnabled())
					LOG.debug("topNCssMap=" + topNCssMap);
				double weightedOpinionSum = 0d;
				double weightSum = 0d;
				// Retrieve all Indirect Trust Evidence related to the object
				// referenced in the specified TrustEvidence
				final Set<IIndirectTrustEvidence> evidenceSet = super.trustEvidenceRepo
						.retrieveLatestIndirectEvidence(null, evidence.getObjectId(),
								evidence.getType(), null);
				if (LOG.isDebugEnabled())
					LOG.debug("evidenceSet=" + evidenceSet);
				for (final IIndirectTrustEvidence relatedEvidence : evidenceSet) {
					if (!(relatedEvidence.getInfo() instanceof Double)) {
						LOG.warn("Related evidence " + relatedEvidence 
								+ " has no trust value!");
						continue;
					}
					final ITrustedCss opinionSource = topNCssMap.get(relatedEvidence.getSubjectId());
					if (opinionSource == null) {
						LOG.warn("Could not find CSS trust relationship with related evidence subject '"
								+ relatedEvidence.getSubjectId() + "'");
						continue;
					}
					final Double weight = evaluateWeight(opinionSource);
					Double weightedOpinion = null;
					if (weight != null)	{
						if (LOG.isDebugEnabled())
							LOG.debug("Retrieving mean trust opinion of '"
									+ relatedEvidence.getSubjectId() + "'");
						final double meanOpinion = this.retrieveMeanTrustOpinion(
								relatedEvidence.getSubjectId());
						weightedOpinion = weight * ((Double) relatedEvidence.getInfo() - meanOpinion); 
					}
					if (weightedOpinion == null) {
						LOG.warn("Ignoring related evidence " + relatedEvidence 
								+ ": Weighted opinion is null");
						continue;
					}
					weightedOpinionSum += weightedOpinion; 
					weightSum += Math.abs(weight);
				}
				if (LOG.isDebugEnabled())
					LOG.debug("weightedOpinionSum=" + weightedOpinionSum 
							+ ", weightSum=" + weightSum);
				// t_x,i = avg(t_x) + weighted opinions
				double value = super.trustRepo.retrieveMeanTrustValue(
						trustorId, TrustValueType.DIRECT, null);
				final double confidence;
				if (weightSum > 0) {
					value += weightedOpinionSum / weightSum;
					confidence = 0.5d; // TODO constant or what?
				} else {
					confidence = 0.25d; // TODO constant or what?
				}
				if (LOG.isDebugEnabled())
					LOG.debug("Evaluated indirect trust value " + value);
				if (value > 1)
					value = 1.0d; // TODO use constant
				else if (value < 0)
					value = 0.0d; // TODO use constant
				trustee.getIndirectTrust().setValue(value);
				trustee.getIndirectTrust().setConfidence(confidence);
				break;
				
			default:
				throw new TrustEngineException("Unsupported type: " 
						+ evidence.getType());
			}
			
			// persist updated TrustedEntities in the Trust Repository
			for (final ITrustedEntity entity : resultSet) {
				if (LOG.isDebugEnabled())
					LOG.debug("Persisting " + entity);
				super.trustRepo.updateEntity(entity);
			}

		} catch (Exception e) {
			throw new TrustEngineException("Could not evaluate indirect trust evidence " 
					+ evidence + ": " + e.getLocalizedMessage(), e);
		}
		
		return resultSet;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.engine.IIndirectTrustEngine#evaluate(org.societies.api.privacytrust.trust.model.TrustedEntityId, java.util.Set)
	 */
	@Override
	public Set<ITrustedEntity> evaluate(final TrustedEntityId trustorId,
			final Set<IIndirectTrustEvidence> evidenceSet) 
					throws TrustEngineException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Evaluating indirect trust evidence set " + evidenceSet
					+ " on behalf of '" + trustorId + "'");
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (evidenceSet == null)
			throw new NullPointerException("evidenceSet can't be null");
		
		final Set<ITrustedEntity> resultSet = new HashSet<ITrustedEntity>();
		// create sorted evidence set based on the evidence timestamps
		final SortedSet<IIndirectTrustEvidence> sortedEvidenceSet =
				new TreeSet<IIndirectTrustEvidence>(evidenceSet);
		if (LOG.isDebugEnabled())
			LOG.debug("Sorted indirect trust evidence set " + sortedEvidenceSet);
		for (final IIndirectTrustEvidence evidence : sortedEvidenceSet) {
			final Set<ITrustedEntity> newResultSet = this.evaluate(trustorId, evidence);  
			resultSet.removeAll(newResultSet);
			resultSet.addAll(newResultSet);
		}
		
		return resultSet;
	}
	
	private boolean areRelevant(final TrustedEntityId trustorId,
			final IIndirectTrustEvidence evidence) throws TrustEngineException {
		
		boolean result = false;
		
		switch (evidence.getType()) {

		case DIRECTLY_TRUSTED:
			if (!trustorId.equals(evidence.getSubjectId()))
				result = true;
			break;
			
		default:
			throw new TrustEngineException("Unsupported type: " 
					+ evidence.getType());
		}
		
		return result;
	}
	
	/*
	 * Map<K,V> = Map<TRUSTEE_TEID,TRUSTEE>
	 */
	private Map<TrustedEntityId, ITrustedCss> retrieveTopNCss(
			final TrustedEntityId trustorId) throws TrustException {
		
		final Map<TrustedEntityId, ITrustedCss> result = 
				new HashMap<TrustedEntityId, ITrustedCss>();
		final Set<ITrustedCss> cssSet =
				super.trustRepo.retrieveCssBySimilarity(trustorId, null, null);
		for (final ITrustedCss css : cssSet)
			result.put(css.getTrusteeId(), css);
		
		return result;
	}
	
	/*
	 * Returns the mean of the trust opinions of the specified trustee.
	 */
	private double retrieveMeanTrustOpinion(
			final TrustedEntityId trusteeId) throws TrustException {

		final Set<IIndirectTrustEvidence> evidenceSet = 
				super.trustEvidenceRepo.retrieveLatestIndirectEvidence(
						trusteeId, null, TrustEvidenceType.DIRECTLY_TRUSTED, null);
		double sum = 0.0d;
		int count = 0;
		for (final IIndirectTrustEvidence evidence : evidenceSet) {
			if (evidence.getInfo() instanceof Double) {
				sum += (Double) evidence.getInfo(); 
				count++;
			}
		}
		
		return (count > 0) ? sum/count : 0.0d;
	}
	
	private static Double evaluateWeight(final ITrustedCss opinionSource) {

		if (opinionSource.getDirectTrust().getValue() == null
				|| opinionSource.getSimilarity() == null)
			return null; // TODO or 0.0d???

		return (opinionSource.getDirectTrust().getValue() * opinionSource.getSimilarity());
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
			
			try {
				for (final TrustedEntityId myId : IndirectTrustEngine.super.trustNodeMgr.getMyIds())
					evaluate(myId, this.evidence);
			} catch (TrustException te) {
				
				LOG.error("Could not handle evidence "
						+ evidence + ": " + te.getLocalizedMessage(), te);
			}
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