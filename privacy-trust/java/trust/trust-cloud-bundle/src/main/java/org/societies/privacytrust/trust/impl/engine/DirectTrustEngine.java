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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import org.societies.privacytrust.trust.api.engine.IDirectTrustEngine;
import org.societies.privacytrust.trust.api.engine.TrustEngineException;
import org.societies.privacytrust.trust.api.event.ITrustEventMgr;
import org.societies.privacytrust.trust.api.event.ITrustEvidenceUpdateEventListener;
import org.societies.privacytrust.trust.api.event.TrustEventTopic;
import org.societies.privacytrust.trust.api.event.TrustEvidenceUpdateEvent;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.model.IDirectTrust;
import org.societies.privacytrust.trust.api.model.ITrust;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;
import org.societies.privacytrust.trust.api.model.ITrustedService;
import org.societies.privacytrust.trust.api.repo.TrustRepositoryException;
import org.societies.privacytrust.trust.api.util.MathUtils;
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
        aMap.put(TrustEvidenceType.SHARED_CONTEXT, +1.0d);
        aMap.put(TrustEvidenceType.WITHHELD_CONTEXT, -10.0d);
        aMap.put(TrustEvidenceType.FRIENDED_USER, +10.0d);
        aMap.put(TrustEvidenceType.UNFRIENDED_USER, -50.0d);
        aMap.put(TrustEvidenceType.USED_SERVICE, +1.0d);
        EVIDENCE_SCORE_MAP = Collections.unmodifiableMap(aMap);
    }
	
	@Autowired
	public DirectTrustEngine(ITrustEventMgr trustEventMgr) throws Exception {
		
		super(trustEventMgr);
		LOG.info("{} instantiated", this.getClass());
		
		try {
			LOG.info("Registering for trust evidence updates...");
			super.trustEventMgr.registerEvidenceUpdateListener(
					new DirectTrustEvidenceUpdateListener(), 
					new String[] { TrustEventTopic.TRUST_EVIDENCE_UPDATED });
		} catch (Exception e) {
			LOG.error(this.getClass() + " could not be initialised: "
					+ e.getLocalizedMessage(), e);
			throw e;
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence)
	 */
	@Override
	public Set<ITrustedEntity> evaluate(final TrustedEntityId trustorId, 
			final ITrustEvidence evidence) throws TrustEngineException {
		
		if (trustorId == null) {
			throw new NullPointerException("trustorId can't be null");
		}
		if (evidence == null) {
			throw new NullPointerException("evidence can't be null");
		}
		
		LOG.debug("evaluate: trustorId={}, evidence={}", trustorId, evidence);
		
		Set<ITrustedEntity> resultSet = new HashSet<ITrustedEntity>();
		
		if (!this.areRelevant(trustorId, evidence)) {
			return resultSet;
		}

		try {
			// Retrieve all TrustedEntities DIRECTLY trusted by the trustor
			// having the same type as the object referenced in the specified TrustEvidence
			resultSet = this.trustRepo.retrieveEntities(
					trustorId, evidence.getObjectId().getEntityType(), TrustValueType.DIRECT);
			ITrustedEntity trustee = null;
			// 1. Obtain reference to trustee (if already contained in the result set)
			// 2. Remove myCss from result set
			final Iterator<ITrustedEntity> resultIter = resultSet.iterator();
			while (resultIter.hasNext()) {
			
				final ITrustedEntity entity = resultIter.next();
				// 1. Obtain reference to trustee
				if (entity.getTrusteeId().equals(evidence.getObjectId())) {
					trustee = entity;
					continue;
				}
				// 2. Remove myCss from result set
				if (entity.getTrusteeId().equals(trustorId)) {
					resultIter.remove();
				}
			}
			// Create trustee if not available
			if (trustee == null) {
				trustee = super.createEntityIfAbsent(trustorId, evidence.getObjectId());
				resultSet.add(trustee);
			}
			LOG.debug("evaluate: trustee={}, resultSet={}", trustee, resultSet);
			
			switch (evidence.getType()) {

			// Update rating
			case RATED:
				// Ignore rating if there is no prior direct trust relationship
				if (trustee.getDirectTrust().getValue() == null) {
					LOG.debug("evaluate: Ignoring rating for trustee '{}' by trustor '{}'"
							+ " - No prior direct trust relationship", trustee.getTrusteeId(), trustorId);
					return new HashSet<ITrustedEntity>();
				}
				trustee.getDirectTrust().setRating((Double) evidence.getInfo());
				break;
				
			// Update score
			case SHARED_CONTEXT:
			case WITHHELD_CONTEXT:
			case FRIENDED_USER:
			case UNFRIENDED_USER:
			case USED_SERVICE:
				final Double oldScore = trustee.getDirectTrust().getScore();
				if (!(evidence.getInfo() instanceof Double)) {
					trustee.getDirectTrust().setScore(oldScore + EVIDENCE_SCORE_MAP.get(evidence.getType()));
				} else {
					trustee.getDirectTrust().setScore(oldScore + (Double) evidence.getInfo());
				}
				break;
				
			// Update association
			case JOINED_COMMUNITY:
			case LEFT_COMMUNITY:
				// Create TrustedCss representing the user that joined the community
				final ITrustedCss user;
				if (trustorId.equals(evidence.getSubjectId())) {
					user = this.createMyCssIfAbsent(evidence.getSubjectId());
				} else {
					user = (ITrustedCss) super.createEntityIfAbsent(trustorId, evidence.getSubjectId());
				}
				final ITrustedCis community = (ITrustedCis) trustee; // TODO class cast exception?
				if (TrustEvidenceType.JOINED_COMMUNITY == evidence.getType()) {
					user.addCommunity(community);
				} else { 
					user.removeCommunity(community);
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("user '" + user + "' " + evidence.getType() + " community '" + community + "'");
					LOG.debug("user is member of " + user.getCommunities().size() + " communities");
					LOG.debug("community has " + community.getMembers().size() + " members");
				}
				this.trustRepo.updateEntity(user);
				break;
				
			default:
				throw new TrustEngineException("Unsupported type: " 
						+ evidence.getType());
			}
			
			final Set<ITrustedCss> userSet = new HashSet<ITrustedCss>(resultSet.size());
			final Set<ITrustedCis> communitySet = new HashSet<ITrustedCis>(resultSet.size());
			final Set<ITrustedService> serviceSet = new HashSet<ITrustedService>(resultSet.size());
			
			for (final ITrustedEntity entity : resultSet) {
				if (entity instanceof ITrustedCss) {
					userSet.add((ITrustedCss) entity);
				} else if (entity instanceof ITrustedCis) {
					communitySet.add((ITrustedCis) entity);
				} else if (entity instanceof ITrustedService) { 
					serviceSet.add((ITrustedService) entity);
				}
			}
			
			if (!userSet.isEmpty()) {
				this.evaluateUsers(userSet);
				final Set<ITrustedEntity> allCommunitySet = this.trustRepo.retrieveEntities(
						trustorId, TrustedEntityType.CIS, null); // TODO why????
				if (!allCommunitySet.isEmpty()) {
					final Set<ITrustedCis> theAllCommunitySet = 
							new HashSet<ITrustedCis>(allCommunitySet.size());
					for (final ITrustedEntity entity : allCommunitySet) {
						if (entity instanceof ITrustedCis) {
							theAllCommunitySet.add((ITrustedCis) entity);
						}
					}
					this.evaluateCommunities(theAllCommunitySet);
					resultSet.addAll(theAllCommunitySet);
				}
			} else if (!communitySet.isEmpty()) {
				this.evaluateCommunities(communitySet);
			} else if (!serviceSet.isEmpty()) {
				this.evaluateServices(serviceSet);
			}
			
			// Add related evidence to trustee
			trustee.addEvidence(evidence);
			
			// persist updated TrustedEntities in the Trust Repository
			for (final ITrustedEntity entity : resultSet) {
				this.trustRepo.updateEntity(entity);
			}

		} catch (Exception e) {
			throw new TrustEngineException(e.getLocalizedMessage(), e);
		}
		
		return resultSet;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(org.societies.api.privacytrust.trust.model.TrustedEntityId, java.util.Set)
	 */
	@Override
	public Set<ITrustedEntity> evaluate(final TrustedEntityId trustorId,
			final Set<ITrustEvidence> evidenceSet) 
					throws TrustEngineException {
		
		if (trustorId == null) {
			throw new NullPointerException("trustorId can't be null");
		}
		if (evidenceSet == null) {
			throw new NullPointerException("evidenceSet can't be null");
		}
		
		LOG.debug("evaluate: trustorId={}, evidenceSet={}", trustorId, evidenceSet);
		
		final Set<ITrustedEntity> resultSet = new HashSet<ITrustedEntity>();
		// create sorted evidence set based on the evidence timestamps
		final SortedSet<ITrustEvidence> sortedEvidenceSet =
				new TreeSet<ITrustEvidence>(evidenceSet);
		LOG.debug("evaluate: sortedEvidenceSet={}", sortedEvidenceSet);
		for (final ITrustEvidence evidence : sortedEvidenceSet) {
			final Set<ITrustedEntity> newResultSet = this.evaluate(trustorId, evidence);  
			resultSet.removeAll(newResultSet);
			resultSet.addAll(newResultSet);
		}
		
		return resultSet;
	}
	
	private void evaluateUsers(final Set<ITrustedCss> cssSet) throws TrustEngineException {
		
		LOG.debug("evaluateUsers: cssSet={}", cssSet);
		
		estimateValues(cssSet);
	}

	private void evaluateCommunities(final Set<ITrustedCis> cisSet) throws TrustEngineException {
		
		LOG.debug("evaluateCommunities: cisSet={}", cisSet);
		
		// 1. Re-evaluate trust ratings/scores
		for (final ITrustedCis cis : cisSet) {
			
			if (LOG.isDebugEnabled())
				LOG.debug("(" + cis.getTrustorId() + ", " + cis.getTrusteeId() 
						+ ") direct trust before rating/score re-evaluation: " 
						+ cis.getDirectTrust());
	
			// 1A. Reset CIS trust if empty  
			if (cis.getMembers().size() == 0) {
				cis.getDirectTrust().setScore(IDirectTrust.INIT_SCORE);
				if (LOG.isDebugEnabled())
					LOG.debug("(" + cis.getTrustorId() + ", " + cis.getTrusteeId() 
							+ ") direct trust after rating/score re-evaluation: " 
							+ cis.getDirectTrust());
				continue;
			}
			
			// 1B. Choose weakest link for score
			final List<Double> memberScoreList = new ArrayList<Double>(cis.getMembers().size());
			for (final ITrustedCss member : cis.getMembers())
				if (member.getDirectTrust().getScore() != null)
					memberScoreList.add(member.getDirectTrust().getScore());
			final double[] memberScoreArray = new double[memberScoreList.size()];
			for (int i = 0; i < memberScoreArray.length; ++i)
				memberScoreArray[i] = memberScoreList.get(i);
			cis.getDirectTrust().setScore(MathUtils.min(memberScoreArray));
			if (LOG.isDebugEnabled())
				LOG.debug("(" + cis.getTrustorId() + ", " + cis.getTrusteeId() 
						+ ") direct trust after re-evaluation: " + cis.getDirectTrust());
		}

		// 2. Re-evaluate trust values
		estimateValues(cisSet);
	}

	private void evaluateServices(final Set<ITrustedService> svcSet) throws TrustEngineException {
		
		LOG.debug("evaluateServices: svcSet={}", svcSet);
		
		estimateValues(svcSet);
	}
	
	private ITrustedCss createMyCssIfAbsent(final TrustedEntityId myTrustorId) 
			throws TrustRepositoryException {

		ITrustedCss myCss = (ITrustedCss) this.trustRepo.retrieveEntity(
				myTrustorId, myTrustorId);
		if (myCss == null) {
			myCss = (ITrustedCss) this.trustRepo.createEntity(myTrustorId, myTrustorId);
			myCss.getDirectTrust().setRating(IDirectTrust.MAX_RATING);
			myCss.getDirectTrust().setScore(IDirectTrust.MAX_SCORE);
			myCss.getDirectTrust().setValue(ITrust.MAX_VALUE);
			myCss = (ITrustedCss) this.trustRepo.updateEntity(myCss);
		}

		return myCss;
	}
	
	/**
	 * Checks if the specified piece of evidence is relevant for the supplied
	 * trustor. More specifically, a piece of evidence is relevant for direct
	 * trust evaluation if:
	 * <ol>
	 *   <li>trustorId != evidence.objectId, i.e. ignore evidence about trustor</li>
	 *   <li>trustorId == evidence.subjectId</li>
	 *     <ol>
	 *       <li>type == {@link TrustEvidenceType#SHARED_CONTEXT SHARED_CONTEXT}</li>
	 *       <li>type == {@link TrustEvidenceType#WITHHELD_CONTEXT WITHHELD_CONTEXT}</li>
	 *       <li>type == {@link TrustEvidenceType#RATED RATED}</li>
	 *       <li>type == {@link TrustEvidenceType#FRIENDED_USER FRIENDED_USER}</li>
	 *       <li>type == {@link TrustEvidenceType#UNFRIENDED_USER UNFRIENDED_USER}</li>
	 *       <li>type == {@link TrustEvidenceType#USED_SERVICE USED_SERVICE}</li>
	 *     </ol>
	 *   <li>type == {@link TrustEvidenceType#JOINED_COMMUNITY JOINED_COMMUNITY}</li>
	 *   <li>type == {@link TrustEvidenceType#LEFT_COMMUNITY LEFT_COMMUNITY}</li>
	 * </ol>
	 * 
	 * @param trustorId
	 * @param evidence
	 * @return <code>true</code> if the specified piece of evidence is relevant
	 *         for the supplied trustor; <code>false</code> otherwise.
	 */
	private boolean areRelevant(final TrustedEntityId trustorId,
			final ITrustEvidence evidence) {
		
		boolean result = false;
		
		if (!trustorId.equals(evidence.getObjectId())) {
		
			switch (evidence.getType()) {

			case SHARED_CONTEXT:
			case WITHHELD_CONTEXT:
			case RATED:
			case FRIENDED_USER:
			case UNFRIENDED_USER:
			case USED_SERVICE:
				if (trustorId.equals(evidence.getSubjectId())) {
					result = true;
				}
				break;

			case JOINED_COMMUNITY:
			case LEFT_COMMUNITY:
				result = true;
				break;
				
			default:
				// NOP
			}
		}
		
		LOG.debug("areRelevant: trustorId={}, evidence={}, result={}", 
				new Object[] { trustorId, evidence, result });
		return result;
	}
	
	private static void estimateValues(final Set<? extends ITrustedEntity> entitySet) 
			throws TrustEngineException {
		
		final double[] rawTrustScores = new double[entitySet.size()+1];
		final ITrustedEntity[] entityArray = entitySet.toArray(new ITrustedEntity[0]);
		for (int i = 0; i < entityArray.length; ++i) {
			rawTrustScores[i] = entityArray[i].getDirectTrust().getScore();
		}
		rawTrustScores[entitySet.size()] = IDirectTrust.INIT_SCORE;
		
		final double[] stanineTrustScores = MathUtils.stanine(rawTrustScores);
		for (int i = 0; i < entityArray.length; ++i) {
			final Double rating = entityArray[i].getDirectTrust().getRating();
			final Double stanineScore = stanineTrustScores[i]; 
			entityArray[i].getDirectTrust().setValue(
					estimateValue(rating, stanineScore));
		}
	}

	/**
	 *         { RATING_WEIGHT * rating + (1 - RATING_WEIGHT) * 0.1 * stanineScore, if rating && stanineScore != null 
	 * value = { rating, if staninceScore == null
	 *         { 0.1 * stanineScore, if rating == null
	 *         
	 * @param rating
	 * @param stanineScore
	 * @return
	 */
	private static Double estimateValue(final Double rating, final Double stanineScore) {
		
		if (rating == null && stanineScore == null) {
			return null;
		}

		if (stanineScore == null) {
			return rating;
		}

		final Double normalisedScore = 0.1d * stanineScore;

		if (rating == null) {
			return normalisedScore;
		}

		return (RATING_WEIGHT * rating + (1.0d - RATING_WEIGHT) * normalisedScore);
	}

	private class DirectTrustEvidenceHandler implements Runnable {

		private final ITrustEvidence evidence;
		
		private DirectTrustEvidenceHandler(final ITrustEvidence evidence) {
			
			this.evidence = evidence;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
		
			LOG.debug("Handling evidence {}", this.evidence);
			
			try {
				for (final TrustedEntityId myId : DirectTrustEngine.super.trustNodeMgr.getMyIds())
					evaluate(myId, evidence);
			} catch (TrustException te) {
				
				LOG.error("Could not handle evidence "
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
			
			LOG.debug("Received TrustEvidenceUpdateEvent {}", evt);
			
			if (!(evt.getSource() instanceof ITrustEvidence)) {
				LOG.error("TrustEvidenceUpdateEvent source is not instance of ITrustEvidence");
				return;
			}
			final ITrustEvidence evidence = (ITrustEvidence) evt.getSource();
			executorService.execute(new DirectTrustEvidenceHandler(evidence));
		}
	}
}