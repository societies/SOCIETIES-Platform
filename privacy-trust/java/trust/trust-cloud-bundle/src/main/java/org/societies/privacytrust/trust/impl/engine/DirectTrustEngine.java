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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
import org.societies.privacytrust.trust.api.event.TrustEventTopic;
import org.societies.privacytrust.trust.api.event.TrustEvidenceUpdateEvent;
import org.societies.privacytrust.trust.api.evidence.model.IDirectTrustEvidence;
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
        aMap.put(TrustEvidenceType.FRIENDED_USER, +5.0d);
        aMap.put(TrustEvidenceType.UNFRIENDED_USER, -50.0d);
        aMap.put(TrustEvidenceType.USED_SERVICE, +1.0d);
        EVIDENCE_SCORE_MAP = Collections.unmodifiableMap(aMap);
    }
	
	@Autowired
	public DirectTrustEngine(ITrustEventMgr trustEventMgr) throws Exception {
		
		super(trustEventMgr);
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		
		try {
			if (LOG.isInfoEnabled())
				LOG.info("Registering for direct trust evidence updates...");
			super.trustEventMgr.registerEvidenceUpdateListener(
					new DirectTrustEvidenceUpdateListener(), 
					new String[] { TrustEventTopic.DIRECT_TRUST_EVIDENCE_UPDATED });
		} catch (Exception e) {
			LOG.error(this.getClass() + " could not be initialised: "
					+ e.getLocalizedMessage(), e);
			throw e;
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.privacytrust.trust.api.evidence.model.IDirectTrustEvidence)
	 */
	@Override
	public Set<ITrustedEntity> evaluate(final TrustedEntityId trustorId, 
			final IDirectTrustEvidence evidence) throws TrustEngineException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Evaluating direct trust evidence " + evidence
					+ " on behalf of '" + trustorId + "'");
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (evidence == null)
			throw new NullPointerException("evidence can't be null");
		
		final Set<ITrustedEntity> resultSet = new HashSet<ITrustedEntity>();
		
		if (!this.areRelevant(trustorId, evidence))
			return resultSet;

		try {
			// Create the trusted entity the evidence object refers to if not already available
			super.createEntityIfAbsent(trustorId, evidence.getObjectId());
			
			// Retrieve all TrustedEntities trusted by the trustor
			// having the same type as the object referenced in the specified TrustEvidence
			final Set<ITrustedEntity> entitySet = this.trustRepo.retrieveEntities(
					trustorId, evidence.getObjectId().getEntityType(), null);
			if (LOG.isDebugEnabled())
				LOG.debug("entitySet=" + entitySet);
			resultSet.addAll(entitySet);
			
			final Map<TrustedEntityId,ITrustedEntity> entityMap = 
					new HashMap<TrustedEntityId, ITrustedEntity>(entitySet.size());
			for (final ITrustedEntity entity : entitySet)
				entityMap.put(entity.getTrusteeId(), entity);
			
			final ITrustedEntity trustee = entityMap.get(evidence.getObjectId());
			// TODO if trustee == null die
			
			switch (evidence.getType()) {

			// Update rating
			case RATED:
				// TODO check null info
				trustee.getDirectTrust().setRating((Double) evidence.getInfo());
				break;
				
			// Update score
			case FRIENDED_USER:
			case UNFRIENDED_USER:
			case USED_SERVICE:
				final Double oldScore = trustee.getDirectTrust().getScore();
				if (!(evidence.getInfo() instanceof Double))
					trustee.getDirectTrust().setScore(oldScore + EVIDENCE_SCORE_MAP.get(evidence.getType()));
				else
					trustee.getDirectTrust().setScore(oldScore + (Double) evidence.getInfo());
				break;
				
			// Update association
			case JOINED_COMMUNITY:
			case LEFT_COMMUNITY:
				// Create TrustedCss representing the user that joined the community
				final ITrustedCss user;
				if (super.trustNodeMgr.getMyIds().contains(evidence.getSubjectId()))
					user = this.createMyCssIfAbsent(evidence.getSubjectId());
				else
					user = (ITrustedCss) super.createEntityIfAbsent(trustorId, evidence.getSubjectId());
				final ITrustedCis community = (ITrustedCis) trustee; // TODO class cast exception?
				if (TrustEvidenceType.JOINED_COMMUNITY.equals(evidence.getType()))
					user.addCommunity(community);
				else 
					user.removeCommunity(community);
				if (LOG.isDebugEnabled()) {
					LOG.debug("user '" + user + "' " + evidence.getType() + " community '" + community + "'");
					LOG.debug("user is member of " + user.getCommunities().size() + " communities");
					LOG.debug("community has " + community.getMembers().size() + " members");
				}
				this.trustRepo.updateEntity(user); // TODO add to resultSet?
				break;
				
			default:
				throw new TrustEngineException("Unsupported type: " 
						+ evidence.getType());
			}
			
			final Set<ITrustedCss> userSet = new HashSet<ITrustedCss>(entitySet.size());
			final Set<ITrustedCis> communitySet = new HashSet<ITrustedCis>(entitySet.size());
			final Set<ITrustedService> serviceSet = new HashSet<ITrustedService>(entitySet.size());
			
			for (final ITrustedEntity entity : entitySet) {
				if (entity instanceof ITrustedCss)
					userSet.add((ITrustedCss) entity);
				else if (entity instanceof ITrustedCis)
					communitySet.add((ITrustedCis) entity);
				else if (entity instanceof ITrustedService)
					serviceSet.add((ITrustedService) entity);
			}
			
			if (!userSet.isEmpty()) {
				this.evaluateUsers(userSet);
				final Set<ITrustedEntity> allCommunitySet = this.trustRepo.retrieveEntities(
						trustorId, TrustedEntityType.CIS, null);
				if (!allCommunitySet.isEmpty()) {
					final Set<ITrustedCis> theAllCommunitySet = 
							new HashSet<ITrustedCis>(allCommunitySet.size());
					for (final ITrustedEntity entity : allCommunitySet)
						if (entity instanceof ITrustedCis)
							theAllCommunitySet.add((ITrustedCis) entity);
					this.evaluateCommunities(theAllCommunitySet);
					resultSet.addAll(theAllCommunitySet);
				}
			} else if (!communitySet.isEmpty()) {
				this.evaluateCommunities(communitySet);
			} else if (!serviceSet.isEmpty()) {
				this.evaluateServices(serviceSet);
			}
			
			// persist updated TrustedEntities in the Trust Repository
			for (final ITrustedEntity entity : resultSet)
				trustRepo.updateEntity(entity);

		} catch (Exception e) {
			throw new TrustEngineException("Could not evaluate direct trust evidence " 
					+ evidence + ": " + e.getLocalizedMessage(), e);
		}
		
		return resultSet;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(org.societies.api.privacytrust.trust.model.TrustedEntityId, java.util.Set)
	 */
	@Override
	public Set<ITrustedEntity> evaluate(final TrustedEntityId trustorId,
			final Set<IDirectTrustEvidence> evidenceSet) 
					throws TrustEngineException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Evaluating direct trust evidence set " + evidenceSet
					+ " on behalf of '" + trustorId + "'");
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (evidenceSet == null)
			throw new NullPointerException("evidenceSet can't be null");
		
		final Set<ITrustedEntity> resultSet = new HashSet<ITrustedEntity>();
		// create sorted evidence set based on the evidence timestamps
		final SortedSet<IDirectTrustEvidence> sortedEvidenceSet =
				new TreeSet<IDirectTrustEvidence>(evidenceSet);
		if (LOG.isDebugEnabled())
			LOG.debug("Sorted direct trust evidence set " + sortedEvidenceSet);
		for (final IDirectTrustEvidence evidence : sortedEvidenceSet) {
			final Set<ITrustedEntity> newResultSet = this.evaluate(trustorId, evidence);  
			resultSet.removeAll(newResultSet);
			resultSet.addAll(newResultSet);
		}
		
		return resultSet;
	}
	
	private void evaluateUsers(final Set<ITrustedCss> cssSet) throws TrustEngineException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Evaluating cssSet=" + cssSet);
		
		final double[] rawTrustScores = new double[cssSet.size()];
		final ITrustedCss[] cssArray = cssSet.toArray(new ITrustedCss[0]);
		for (int i = 0; i < cssArray.length; ++i)
			rawTrustScores[i] = cssArray[i].getDirectTrust().getScore();
		
		final double[] stanineTrustScores = MathUtils.stanine(rawTrustScores);
		for (int i = 0; i < cssArray.length; ++i) {
			if (LOG.isDebugEnabled())
				LOG.debug("(" + cssArray[i].getTrustorId() + ", " + cssArray[i].getTrusteeId() + ") direct trust before normalisation: "
						+ cssArray[i].getDirectTrust());
			final Double rating = cssArray[i].getDirectTrust().getRating();
			final Double stanineScore = stanineTrustScores[i]; 
			cssArray[i].getDirectTrust().setValue(
					estimateValue(rating, stanineScore));
			if (LOG.isDebugEnabled())
				LOG.debug("(" + cssArray[i].getTrustorId() + ", " + cssArray[i].getTrusteeId() + ") direct trust after normalisation: "
						+ cssArray[i].getDirectTrust());
		}
	}

	private void evaluateCommunities(final Set<ITrustedCis> cisSet) throws TrustEngineException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Evaluating cisSet=" + cisSet);
		
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
		final ITrustedCis[] cisArray = cisSet.toArray(new ITrustedCis[0]);
		final double[] rawTrustScores = new double[cisArray.length];
		for (int i = 0; i < cisArray.length; ++i)
			rawTrustScores[i] = cisArray[i].getDirectTrust().getScore();

		final double[] stanineTrustScores = MathUtils.stanine(rawTrustScores);
		for (int i = 0; i < cisArray.length; ++i) {
			if (LOG.isDebugEnabled())
				LOG.debug("(" + cisArray[i].getTrustorId() + ", " 
						+ cisArray[i].getTrusteeId() + ") direct trust before normalisation: "
						+ cisArray[i].getDirectTrust());
			final Double rating = cisArray[i].getDirectTrust().getRating();
			final Double stanineScore = stanineTrustScores[i]; 
			cisArray[i].getDirectTrust().setValue(
					estimateValue(rating, stanineScore));
			if (LOG.isDebugEnabled())
				LOG.debug("(" + cisArray[i].getTrustorId() + ", " 
						+ cisArray[i].getTrusteeId() + ") direct trust after normalisation: "
						+ cisArray[i].getDirectTrust());
		}
	}

	private void evaluateServices(final Set<ITrustedService> svcSet) throws TrustEngineException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Evaluating svcSet=" + svcSet);
		
		final double[] rawTrustScores = new double[svcSet.size()];
		final ITrustedService[] svcArray = svcSet.toArray(new ITrustedService[0]);
		for (int i = 0; i < svcArray.length; ++i)
			rawTrustScores[i] = svcArray[i].getDirectTrust().getScore();
		
		final double[] stanineTrustScores = MathUtils.stanine(rawTrustScores);
		for (int i = 0; i < svcArray.length; ++i) {
			if (LOG.isDebugEnabled())
				LOG.debug("(" + svcArray[i].getTrustorId() + ", " 
						+ svcArray[i].getTrusteeId() + ") direct trust before normalisation: "
						+ svcArray[i].getDirectTrust());
			final Double rating = svcArray[i].getDirectTrust().getRating();
			final Double stanineScore = stanineTrustScores[i]; 
			svcArray[i].getDirectTrust().setValue(
					estimateValue(rating, stanineScore));
			if (LOG.isDebugEnabled())
				LOG.debug("(" + svcArray[i].getTrustorId() + ", " 
						+ svcArray[i].getTrusteeId() + ") direct trust after normalisation: "
						+ svcArray[i].getDirectTrust());
		}
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
	
	private boolean areRelevant(final TrustedEntityId trustorId,
			final IDirectTrustEvidence evidence) throws TrustEngineException {
		
		boolean result = false;
		
		switch (evidence.getType()) {

		case RATED:
		case FRIENDED_USER:
		case UNFRIENDED_USER:
		case USED_SERVICE:
			if (trustorId.equals(evidence.getSubjectId()))
				result = true;
			break;
			
		case JOINED_COMMUNITY:
		case LEFT_COMMUNITY:
			result = true;
			break;
			
		default:
			throw new TrustEngineException("Unsupported type: " 
					+ evidence.getType());
		}
		
		return result;
	}

	private static Double estimateValue(final Double rating, final Double stanineScore) {

		if (rating == null && stanineScore == null)
			return null;

		if (stanineScore == null)
			return 0.5d * rating;

		final Double normalisedScore = 0.1d * stanineScore;

		if (rating == null)
			return 0.5d * normalisedScore; // TODO use constant

		return (0.5d * rating + 0.5d * normalisedScore); // TODO use constant
	}

	private class DirectTrustEvidenceHandler implements Runnable {

		private final IDirectTrustEvidence evidence;
		
		private DirectTrustEvidenceHandler(final IDirectTrustEvidence evidence) {
			
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
			
			if (LOG.isDebugEnabled())
				LOG.debug("Received direct TrustEvidenceUpdateEvent " + evt);
			
			if (!(evt.getSource() instanceof IDirectTrustEvidence)) {
				LOG.error("TrustEvidenceUpdateEvent source is not instance of IDirectTrustEvidence");
				return;
			}
			final IDirectTrustEvidence evidence = (IDirectTrustEvidence) evt.getSource();
			executorService.execute(new DirectTrustEvidenceHandler(evidence));
		}
	}
}