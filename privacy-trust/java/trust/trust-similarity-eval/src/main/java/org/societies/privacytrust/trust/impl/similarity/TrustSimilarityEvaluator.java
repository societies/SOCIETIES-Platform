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
package org.societies.privacytrust.trust.impl.similarity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.societies.privacytrust.trust.api.similarity.ITrustSimilarityEvaluator;
import org.societies.privacytrust.trust.api.similarity.TrustSimilarityEvalException;
import org.societies.privacytrust.trust.api.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ITrustSimilarityEvaluator} interface.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.1
 */
@Service
@Lazy(false)
public class TrustSimilarityEvaluator implements ITrustSimilarityEvaluator {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(TrustSimilarityEvaluator.class);
	
	private static final double COS_SIM_VALUE_SHIFT = -0.5d;
	
	@Autowired(required=true)
	private ITrustRepository trustRepository;
	
	@Autowired(required=true)
	private ITrustEvidenceRepository trustEvidenceRepository;

	TrustSimilarityEvaluator() {

		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
	}

	/*
	 * @see org.societies.privacytrust.trust.api.similarity.ITrustSimilarityEvaluator#evaluateCosineSimilarity(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public Double evaluateCosineSimilarity(final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId) throws TrustSimilarityEvalException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (!TrustedEntityType.CSS.equals(trustorId.getEntityType()))
			throw new IllegalArgumentException("trustorId is not of type CSS");
		if (!TrustedEntityType.CSS.equals(trusteeId.getEntityType()))
			throw new IllegalArgumentException("trusteeId is not of type CSS");
		
		if (LOG.isDebugEnabled())
			LOG.debug("Evaluating cosine similarity between trustor '"
					+ trustorId + "' and trustee '" + trusteeId + "'");
		
		// Self-comparison
		if (trustorId.equals(trusteeId))
			return 1.0d;
		
		try {
			// The trust relationships of the trustor
			final Map<TrustedEntityId, Double> trustorRelationships =
					this.retrieveTrustorRelationships(trustorId);
			if (LOG.isDebugEnabled())
				LOG.debug("trustorRelationships: " + trustorRelationships);
			if (trustorRelationships.isEmpty())
				return 0.0d;
			
			// The trust relationships of the trustee in common with the trustor
			final Map<TrustedEntityId, Double> commonRelationships =
					this.retrieveCommonRelationships(trusteeId, 
							trustorRelationships.keySet());
			if (LOG.isDebugEnabled())
				LOG.debug("commonRelationships: " + commonRelationships);
			if (commonRelationships.isEmpty())
				return 0.0d;
			
			final double[] trustorValueVector = new double[commonRelationships.size()]; 
			final double[] trusteeValueVector = new double[commonRelationships.size()];
			int i = 0;
			for (final TrustedEntityId teid : commonRelationships.keySet()) {
				trustorValueVector[i] = trustorRelationships.get(teid) + COS_SIM_VALUE_SHIFT;
				trusteeValueVector[i] = commonRelationships.get(teid) + COS_SIM_VALUE_SHIFT;
				i++;
			}
			return MathUtils.cos(trustorValueVector, trusteeValueVector);
			
		} catch (Exception e) {
		
			throw new TrustSimilarityEvalException(e.getLocalizedMessage(), e);
		}
	}
	
	private Map<TrustedEntityId, Double> retrieveTrustorRelationships(
			TrustedEntityId trustorId) throws TrustException {
		
		// The trust relationships of the trustor
		final Map<TrustedEntityId, Double> result =
				new HashMap<TrustedEntityId, Double>();
		
		// Fetch entities trusted by trustor
		final Set<ITrustedEntity> trustedEntities = 
				this.trustRepository.retrieveEntities(trustorId, null, TrustValueType.DIRECT);
		for (final ITrustedEntity entity : trustedEntities) {
			
			// Ignore entity if
			// 1. trustorId == trusteeId
			if (!entity.getTrustorId().equals(entity.getTrusteeId()))
				result.put(entity.getTrusteeId(),
						entity.getDirectTrust().getValue());
		}
		
		return result;
	}
	
	private Map<TrustedEntityId, Double> retrieveCommonRelationships(
			TrustedEntityId trusteeId, Set<TrustedEntityId> trustorRelationships)
					throws TrustException {
		
		// The trust relationships of the trustee in common with the trustor
		final Map<TrustedEntityId, Double> result =
				new HashMap<TrustedEntityId, Double>();
		
		// Fetch evidence regarding entities trusted by trustee
		final Set<ITrustEvidence> trusteeEvidenceSet =
				this.trustEvidenceRepository.retrieveLatestEvidence(
						trusteeId, null, TrustEvidenceType.DIRECTLY_TRUSTED, null);
		for (final ITrustEvidence evidence : trusteeEvidenceSet) {
			
			// Ignore evidence if
			// 1. objectId not contained in trustorRelationships
			// 2. info (directTrustValue) == null
			// 3. subjectId == objectId
			if (trustorRelationships.contains(evidence.getObjectId()) &&
					evidence.getInfo() instanceof Double &&
					(!evidence.getSubjectId().equals(evidence.getObjectId())))
				result.put(evidence.getObjectId(),
						(Double) evidence.getInfo());
		}
		
		return result;
	}
}