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
import org.societies.privacytrust.trust.api.event.TrustEventMgrException;
import org.societies.privacytrust.trust.api.event.TrustEventTopic;
import org.societies.privacytrust.trust.api.event.TrustEvidenceUpdateEvent;
import org.societies.privacytrust.trust.api.evidence.model.IDirectTrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.api.model.IDirectTrust;
import org.societies.privacytrust.trust.api.model.ITrust;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;
import org.societies.privacytrust.trust.api.model.ITrustedService;
import org.societies.privacytrust.trust.api.repo.TrustRepositoryException;
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
				new String[] { TrustEventTopic.DIRECT_TRUST_EVIDENCE_UPDATED });
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(org.societies.privacytrust.trust.api.evidence.model.IDirectTrustEvidence)
	 */
	@Override
	public Set<ITrustedEntity> evaluate(final IDirectTrustEvidence evidence)
			throws TrustEngineException {
		
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("Evaluating direct trust evidence " + evidence);
		
		if (evidence == null)
			throw new NullPointerException("evidence can't be null");
		
		final Set<ITrustedEntity> resultSet = new HashSet<ITrustedEntity>();

		try {
			// Create the trustee the evidence refers to if not already available
			this.createEntityIfAbsent(evidence.getTeid());
			
			final Class<? extends ITrustedEntity> entityClass;
			switch (evidence.getTeid().getEntityType()) {
			
			case CSS:
				entityClass = ITrustedCss.class;
				break;
				
			case CIS:
				entityClass = ITrustedCis.class;
				break;
				
			case SVC:
				entityClass = ITrustedService.class;
				break;
				
			// TODO case LGC:
				
			default:
				throw new TrustEngineException("Unsupported object type: " 
						+ evidence.getTeid().getEntityType());
			}
			
			// Retrieve all TrustedEntities trusted by the trustor
			// referenced in the specified TrustEvidence
			final List<? extends ITrustedEntity> entityList = this.trustRepo.retrieveEntities(
					evidence.getTeid().getTrustorId(), entityClass);
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("entityList=" + entityList);
			resultSet.addAll(entityList);
			
			final Map<TrustedEntityId,ITrustedEntity> entityMap = 
					new HashMap<TrustedEntityId, ITrustedEntity>(entityList.size());
			for (final ITrustedEntity entity : entityList)
				entityMap.put(entity.getTeid(), entity);
			
			final ITrustedEntity trustee = entityMap.get(evidence.getTeid());
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
				// Create TEID of the user that joined the community (trustee)
				final TrustedEntityId userTeid = new TrustedEntityId(
						evidence.getTeid().getTrustorId(), // TODO me
						TrustedEntityType.CSS, 
						evidence.getTeid().getTrustorId());
				final ITrustedCss user;
				if (super.trustedEntityIdMgr.getMyIds().contains(userTeid))
					user = this.createMyCssIfAbsent(userTeid);
				else
					user = (ITrustedCss) this.createEntityIfAbsent(userTeid);

				final ITrustedCis community = (ITrustedCis) trustee; // TODO class cast exception?
				if (TrustEvidenceType.JOINED_COMMUNITY.equals(evidence.getType()))
					user.addCommunity(community);
				else 
					user.removeCommunity(community);
				this.trustRepo.updateEntity(user); // TODO add to resultSet?
				break;
				
			default:
				throw new TrustEngineException("Unsupported type: " 
						+ evidence.getType());
			}
			
			final Set<ITrustedCss> userSet = new HashSet<ITrustedCss>(entityList.size());
			final Set<ITrustedCis> communitySet = new HashSet<ITrustedCis>(entityList.size());
			final Set<ITrustedService> serviceSet = new HashSet<ITrustedService>(entityList.size());
			
			for (final ITrustedEntity entity : entityList) {
				if (entity instanceof ITrustedCss)
					userSet.add((ITrustedCss) entity);
				else if (entity instanceof ITrustedCis)
					communitySet.add((ITrustedCis) entity);
				else if (entity instanceof ITrustedService)
					serviceSet.add((ITrustedService) entity);
			}
			
			if (!userSet.isEmpty()) {
				this.evaluateUsers(userSet);
				final List<ITrustedCis> communityList = this.trustRepo.retrieveEntities(
						evidence.getTeid().getTrustorId(), ITrustedCis.class);
				if (!communityList.isEmpty()) {
					this.evaluateCommunities(new HashSet<ITrustedCis>(communityList));
					resultSet.addAll(communityList);
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
	 * @see org.societies.privacytrust.trust.api.engine.IDirectTrustEngine#evaluate(java.util.Set)
	 */
	@Override
	public Set<ITrustedEntity> evaluate(final Set<IDirectTrustEvidence> evidenceSet)
			throws TrustEngineException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Evaluating direct trust evidence set " + evidenceSet);
		
		if (evidenceSet == null)
			throw new NullPointerException("evidenceSet can't be null");
		
		final Set<ITrustedEntity> resultSet = new HashSet<ITrustedEntity>();
		// create sorted evidence set based on the evidence timestamps
		final SortedSet<IDirectTrustEvidence> sortedEvidenceSet =
				new TreeSet<IDirectTrustEvidence>(evidenceSet);
		if (LOG.isDebugEnabled())
			LOG.debug("Sorted direct trust evidence set " + sortedEvidenceSet);
		for (final IDirectTrustEvidence evidence : sortedEvidenceSet) {
			final Set<ITrustedEntity> newResultSet = this.evaluate(evidence);  
			resultSet.removeAll(newResultSet);
			resultSet.addAll(newResultSet);
		}
		
		return resultSet;
	}
	
	private void evaluateUsers(final Set<ITrustedCss> cssSet) throws TrustEngineException {
		
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("cssSet=" + cssSet);
		
		final double[] rawTrustScores = new double[cssSet.size()];
		final ITrustedCss[] cssArray = cssSet.toArray(new ITrustedCss[0]);
		for (int i = 0; i < cssArray.length; ++i)
			rawTrustScores[i] = cssArray[i].getDirectTrust().getScore();
		
		final double[] stanineTrustScores = MathUtils.stanine(rawTrustScores);
		for (int i = 0; i < cssArray.length; ++i) {
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("CSS '" + cssArray[i].getTeid() + "' direct trust before normalisation: "
						+ cssArray[i].getDirectTrust());
			final Double rating = cssArray[i].getDirectTrust().getRating();
			final Double stanineScore = stanineTrustScores[i]; 
			cssArray[i].getDirectTrust().setValue(
					estimateValue(rating, stanineScore));
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("CSS '" + cssArray[i].getTeid() + "' direct trust after normalisation: "
						+ cssArray[i].getDirectTrust());
		}
	}

	private void evaluateCommunities(final Set<ITrustedCis> cisSet) throws TrustEngineException {
		
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("cisSet=" + cisSet);
		
		// 1. Re-evaluate trust ratings/scores
		for (final ITrustedCis cis : cisSet) {
			
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("CIS '" + cis.getTeid() + "' direct trust before rating/score re-evaluation: " 
						+ cis.getDirectTrust());
	
			// 1A. Reset CIS trust if empty  
			if (cis.getMembers().size() == 0) {
				cis.getDirectTrust().setScore(IDirectTrust.INIT_SCORE);
				// TODO re-consider cis.getDirectTrust().setRating(null);
				if (LOG.isInfoEnabled()) // TODO DEBUG
					LOG.info("CIS '" + cis.getTeid() + "' direct trust after rating/score re-evaluation: " 
							+ cis.getDirectTrust());
				continue;
			}
			
			// 1Bi. Choose weakest link for score
			final List<Double> memberScoreList = new ArrayList<Double>(cis.getMembers().size());
			for (final ITrustedCss member : cis.getMembers())
				if (member.getDirectTrust().getScore() != null)
					memberScoreList.add(member.getDirectTrust().getScore());
			final double[] memberScoreArray = new double[memberScoreList.size()];
			for (int i = 0; i < memberScoreArray.length; ++i)
				memberScoreArray[i] = memberScoreList.get(i);
			cis.getDirectTrust().setScore(MathUtils.min(memberScoreArray));
			
			// 2Bii. Choose weakest link for rating *unless* the user has already assigned one
			if (cis.getDirectTrust().getRating() == null) {
				final List<Double> memberRatingList = new ArrayList<Double>(cis.getMembers().size());
				for (final ITrustedCss member : cis.getMembers())
					if (member.getDirectTrust().getRating() != null)
						memberRatingList.add(member.getDirectTrust().getRating());
				final double[] memberRatingArray = new double[memberRatingList.size()];
				for (int i = 0; i < memberRatingArray.length; ++i)
					memberRatingArray[i] = memberRatingList.get(i);
				cis.getDirectTrust().setRating(MathUtils.min(memberRatingArray));
			}
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("CIS '" + cis.getTeid() + "' direct trust after re-evaluation: " 
						+ cis.getDirectTrust());
		}

		// 2. Re-evaluate trust values
		final ITrustedCis[] cisArray = cisSet.toArray(new ITrustedCis[0]);
		final double[] rawTrustScores = new double[cisArray.length];
		for (int i = 0; i < cisArray.length; ++i)
			rawTrustScores[i] = cisArray[i].getDirectTrust().getScore();

		final double[] stanineTrustScores = MathUtils.stanine(rawTrustScores);
		for (int i = 0; i < cisArray.length; ++i) {
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("CIS '" + cisArray[i].getTeid() + "' direct trust before normalisation: "
						+ cisArray[i].getDirectTrust());
			final Double rating = cisArray[i].getDirectTrust().getRating();
			final Double stanineScore = stanineTrustScores[i]; 
			cisArray[i].getDirectTrust().setValue(
					estimateValue(rating, stanineScore));
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("CIS '" + cisArray[i].getTeid() + "' direct trust after normalisation: "
						+ cisArray[i].getDirectTrust());
		}
	}

	private void evaluateServices(final Set<ITrustedService> svcSet) throws TrustEngineException {
		
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("svcSet=" + svcSet);
		
		final double[] rawTrustScores = new double[svcSet.size()];
		final ITrustedService[] svcArray = svcSet.toArray(new ITrustedService[0]);
		for (int i = 0; i < svcArray.length; ++i)
			rawTrustScores[i] = svcArray[i].getDirectTrust().getScore();
		
		final double[] stanineTrustScores = MathUtils.stanine(rawTrustScores);
		for (int i = 0; i < svcArray.length; ++i) {
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("SVC '" + svcArray[i].getTeid() + "' direct trust before normalisation: "
						+ svcArray[i].getDirectTrust());
			final Double rating = svcArray[i].getDirectTrust().getRating();
			final Double stanineScore = stanineTrustScores[i]; 
			svcArray[i].getDirectTrust().setValue(
					estimateValue(rating, stanineScore));
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("SVC '" + svcArray[i].getTeid() + "' direct trust after normalisation: "
						+ svcArray[i].getDirectTrust());
		}
	}

	private ITrustedEntity createEntityIfAbsent(final TrustedEntityId teid) 
			throws TrustRepositoryException {

		ITrustedEntity entity = (ITrustedEntity) this.trustRepo.retrieveEntity(teid);

		return (entity == null) ? this.trustRepo.createEntity(teid) : entity;
	}
	
	private ITrustedCss createMyCssIfAbsent(final TrustedEntityId myTeid) 
			throws TrustRepositoryException {

		ITrustedCss myCss = (ITrustedCss) this.trustRepo.retrieveEntity(myTeid);
		if (myCss == null) {
			myCss = (ITrustedCss) this.trustRepo.createEntity(myTeid);
			myCss.getDirectTrust().setRating(IDirectTrust.MAX_RATING);
			myCss.getDirectTrust().setScore(IDirectTrust.MAX_SCORE);
			myCss.getDirectTrust().setValue(ITrust.MAX_VALUE);
			myCss = (ITrustedCss) this.trustRepo.updateEntity(myCss);
		}

		return myCss;
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
				evaluate(evidence);
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