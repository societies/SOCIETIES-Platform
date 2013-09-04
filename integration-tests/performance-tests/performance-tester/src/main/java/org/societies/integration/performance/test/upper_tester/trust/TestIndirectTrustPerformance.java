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
package org.societies.integration.performance.test.upper_tester.trust;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.internal.privacytrust.trust.model.ExtTrustRelationship;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustEvidence;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.integration.performance.test.lower_tester.PerformanceLowerTester;
import org.societies.integration.performance.test.lower_tester.PerformanceTestMgmtInfo;
import org.societies.integration.performance.test.lower_tester.PerformanceTestResult;
import org.societies.integration.performance.test.upper_tester.trust.direct.ITestDirectTrustPerformance;
import org.societies.integration.performance.test.upper_tester.trust.indirect.ITestIndirectTrustPerformance;

/**
 * Implementation of the {@link ITestDirectTrustPerformance} interface.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.2
 */
public class TestIndirectTrustPerformance implements ITestIndirectTrustPerformance {

	private static Logger LOG = LoggerFactory.getLogger(TestIndirectTrustPerformance.class);
	
	/** The time to wait for DIRECT trust update events. */
	private static final long DIRECT_EVAL_TIMEOUT = 2000l;
	
	/** The time to wait for INDIRECT trust update events. */
	private static final long INDIRECT_EVAL_TIMEOUT = 60000l;
	
	private PerformanceLowerTester performanceLowerTester;
	private PerformanceTestResult performanceTestResult;
	
	/** The Comms Manager service reference. */
	private ICommManager commManager;
	
	/** The internal Trust Evidence Collector service reference. */
	private ITrustEvidenceCollector internalTrustEvidenceCollector;
	
	/** The internal Trust Broker service reference. */
	private ITrustBroker internalTrustBroker;
	
	/** The count-down latch to use for verifying trust update events. */
	private CountDownLatch cdLatch;
	
	public TestIndirectTrustPerformance() {
		
		LOG.info("{} instantiated", this.getClass().getName());
	}
	
	/*
	 * @see org.societies.integration.performance.test.upper_tester.trust.indirect.ITestIndirectTrustPerformance#testEvaluateIndirectTrust(org.societies.integration.performance.test.lower_tester.PerformanceTestMgmtInfo, org.societies.integration.performance.test.upper_tester.trust.TrustEvidenceParams)
	 */
	@Override
	public void testEvaluateIndirectTrust(
			PerformanceTestMgmtInfo performanceTestMgmtInfo,
			TrustEvidenceParams trustEvidenceParams) {

		// The following 2 lines are mandatory for the beginning of each test 
		this.performanceLowerTester = new PerformanceLowerTester(performanceTestMgmtInfo);
		this.performanceLowerTester.testStart(this.getClass().getName(), this.getCommManager());
		
		final Date testStart = new Date();
		
		// Extract the Map<cssId, Set<TrustEvidence> contained in the test params.
		Map<String, Set<TrustEvidence>> trustEvidenceMap = null;
		try {
			trustEvidenceMap = TrustEvidenceParamParser.toTrustEvidence(trustEvidenceParams);
		} catch (Exception e) {
			
			this.fail("Could not parse TrustEvidenceParams JSON String: "
					+ e.getLocalizedMessage());
			return;
		}
		
		try {
			// Retrieve the IIdentity of my CSS.
			final String myCssId = this.commManager.getIdManager().getCloudNode().getBareJid();
			final TrustedEntityId myTeid = new TrustedEntityId(TrustedEntityType.CSS, myCssId);
			
			// Extract TrustSimilarity Map based on the test params.
			final Map<TrustedEntityId, TrustSimilarity> trustSimilarityMap =
					this.extractTrustSimilarity(myTeid, trustEvidenceMap);

			// The set of trust evidence to evaluate
			final Set<TrustEvidence> trustEvidenceSet = (trustEvidenceMap.get(myCssId) != null)
					? trustEvidenceMap.get(myCssId) : Collections.<TrustEvidence>emptySet();

			// Start test
			LOG.info("START testEvaluateDirectTrust: trustEvidenceSet={}, trustSimilarityMap={}",
					trustEvidenceSet, trustSimilarityMap);
			final Set<TrustRelationship> result = new LinkedHashSet<TrustRelationship>();
			for (final TrustEvidence trustEvidence : trustEvidenceSet) {

				LOG.info("Evaluating evidence '{}'", trustEvidence);
				// Create a DIRECT trust query for the entity specified in this piece of evidence
				final TrustQuery trustQuery = new TrustQuery(myTeid).setTrusteeId(
						trustEvidence.getObjectId()).setTrustValueType(TrustValueType.DIRECT);
				// Initialise count-down latch to use for verifying trust update event.
				this.cdLatch = new CountDownLatch(1);
				final TrustUpdateEventListener trustListener = new TrustUpdateEventListener();
				// Register for trust value updates of this entity
				this.internalTrustBroker.registerTrustUpdateListener(trustListener, trustQuery);
				// Add evidence
				this.internalTrustEvidenceCollector.addDirectEvidence(
						trustEvidence.getSubjectId(), trustEvidence.getObjectId(),
						trustEvidence.getType(), trustEvidence.getTimestamp(),
						trustEvidence.getInfo());
				final boolean isTrustUpdated = this.cdLatch.await(DIRECT_EVAL_TIMEOUT, TimeUnit.MILLISECONDS);
				// Unregister from trust value updates of this entity
				this.internalTrustBroker.unregisterTrustUpdateListener(trustListener, trustQuery);
				if (!isTrustUpdated) {	
					LOG.warn("Did not receive TrustUpdateEvent for evidence '"
							+ trustEvidence + "' in the specified timeout: " + DIRECT_EVAL_TIMEOUT + "ms");
				} else {
					final TrustRelationship updatedTrustRelationship =
							trustListener.getUpdatedTrustRelationship();
					LOG.info("Updated trust relationship '{}'", updatedTrustRelationship);
					// Verify Trust Relationships properties
					if (!myTeid.equals(updatedTrustRelationship.getTrustorId())) {
						this.fail("Expected trustorId '" + myTeid + "' but was '"
								+ updatedTrustRelationship.getTrustorId() + "'");
						return;
					}
					if (!trustEvidence.getObjectId().equals(updatedTrustRelationship.getTrusteeId())) {
						this.fail("Expected trusteeId '" + myTeid + "' but was '"
								+ updatedTrustRelationship.getTrusteeId() + "'");
						return;
					}
					if (TrustValueType.DIRECT != updatedTrustRelationship.getTrustValueType()) {
						this.fail("Expected trustValueType '" + TrustValueType.DIRECT + "' but was '"
								+ updatedTrustRelationship.getTrustValueType() + "'");
						return;
					}
				}
			}
			
			final Set<TrustedEntityId> connectionTeids = trustSimilarityMap.keySet();
			for (final TrustedEntityId connectionTeid : connectionTeids) {
				LOG.info("Establishing DIRECT trust relationship with connection '{}'",
						connectionTeid);
				// Create a DIRECT trust query for the new connection
				final TrustQuery trustQuery = new TrustQuery(myTeid).setTrusteeId(
						connectionTeid).setTrustValueType(TrustValueType.DIRECT);
				// Initialise count-down latch to use for verifying trust update event.
				this.cdLatch = new CountDownLatch(1);
				final TrustUpdateEventListener trustListener = new TrustUpdateEventListener();
				// Register for trust value updates of this entity
				this.internalTrustBroker.registerTrustUpdateListener(trustListener, trustQuery);
				// Add fake evidence to establish DIRECT trust connection with connection
				this.internalTrustEvidenceCollector.addDirectEvidence(
						myTeid, connectionTeid, TrustEvidenceType.FRIENDED_USER,
						new Date(),	null);
				final boolean isTrustUpdated = this.cdLatch.await(DIRECT_EVAL_TIMEOUT, TimeUnit.MILLISECONDS);
				// Unregister from trust value updates of this entity
				this.internalTrustBroker.unregisterTrustUpdateListener(trustListener, trustQuery);
				if (!isTrustUpdated) {	
					LOG.warn("Did not receive TrustUpdateEvent for connection '"
							+ connectionTeid + "' in the specified timeout: " + DIRECT_EVAL_TIMEOUT + "ms");
				} else {
					final TrustRelationship updatedTrustRelationship =
							trustListener.getUpdatedTrustRelationship();
					LOG.info("Updated trust relationship '{}'", updatedTrustRelationship);
					// Verify Trust Relationships properties
					if (!myTeid.equals(updatedTrustRelationship.getTrustorId())) {
						this.fail("Expected trustorId '" + myTeid + "' but was '"
								+ updatedTrustRelationship.getTrustorId() + "'");
						return;
					}
					if (!connectionTeid.equals(updatedTrustRelationship.getTrusteeId())) {
						this.fail("Expected trusteeId '" + connectionTeid + "' but was '"
								+ updatedTrustRelationship.getTrusteeId() + "'");
						return;
					}
					if (TrustValueType.DIRECT != updatedTrustRelationship.getTrustValueType()) {
						this.fail("Expected trustValueType '" + TrustValueType.DIRECT + "' but was '"
								+ updatedTrustRelationship.getTrustValueType() + "'");
						return;
					}
				}
			}
			
			LOG.info("Sleeping for {}ms to allow INDIRECT trust evaluation", INDIRECT_EVAL_TIMEOUT);
			Thread.sleep(INDIRECT_EVAL_TIMEOUT);
			
			for (final Map.Entry<TrustedEntityId, TrustSimilarity> trustSimilarity : trustSimilarityMap.entrySet()) {
				for (final TrustedEntityId trusteeId : trustSimilarity.getValue().getUnsharedTrustees()) {
					final ExtTrustRelationship extTrustRelationship =
							this.internalTrustBroker.retrieveExtTrustRelationship(new TrustQuery(myTeid)
							.setTrusteeId(trusteeId).setTrustValueType(TrustValueType.INDIRECT)).get();
					LOG.info("INDIRECT trust relationship '{}'", extTrustRelationship);
					if (extTrustRelationship == null) {
						this.fail("INDIRECT trust relationship with '" + trusteeId + "' not found");
						return;
					}
					if (extTrustRelationship.getTimestamp().compareTo(testStart) < 0) {
						this.fail("INDIRECT trust relationship with '" + trusteeId + "' is out-of-date");
						return;
					}
					if (extTrustRelationship.getTrustEvidence().isEmpty()) {
						this.fail("INDIRECT trust relationship with '" + trusteeId + "' contains no evidence");
						return;
					}
					boolean foundConnection = false;
					for (final TrustEvidence evidence : extTrustRelationship.getTrustEvidence()) {
						if (TrustEvidenceType.DIRECTLY_TRUSTED == evidence.getType()
								&& trustSimilarity.getKey().equals(evidence.getSubjectId())) {
							foundConnection = true;
							break;
						}
					}
					if (!foundConnection) {
						this.fail("INDIRECT trust relationship with '" 
								+ trusteeId + "' not associated with connection '"
								+ trustSimilarity.getKey() + "'");
						return;
					}
					result.add(extTrustRelationship);
				}
			}
			
			// End test
			LOG.info("END testEvaluateDirectTrust for params: {}", trustEvidenceSet);
			this.performanceTestResult = new PerformanceTestResult(this.getClass().getName(),
					"Updated trust relationships: " + result,
					PerformanceTestResult.SUCCESS_STATUS);
			this.performanceLowerTester.testFinish(this.performanceTestResult);

		} catch (TrustException te) {

			this.fail(te.getLocalizedMessage());

		} catch (InterruptedException ie) {
		
			this.fail("Interrupted while executing test: " + ie.getLocalizedMessage());
			
		} catch (Exception e) {

			this.fail(e.getLocalizedMessage());
		}
	}

	public ICommManager getCommManager() {
		
		return this.commManager;
	}
	
	public void setCommManager(ICommManager commManager) {
		
		this.commManager = commManager;
	}
	
	public ITrustEvidenceCollector getInternalTrustEvidenceCollector() {
		
		return this.internalTrustEvidenceCollector;
	}
	
	public void setInternalTrustEvidenceCollector(ITrustEvidenceCollector internalTrustEvidenceCollector) {
		
		this.internalTrustEvidenceCollector = internalTrustEvidenceCollector;
	}
	
	public ITrustBroker getInternalTrustBroker() {
		
		return this.internalTrustBroker;
	}
	
	public void setInternalTrustBroker(ITrustBroker internalTrustBroker) {
		
		this.internalTrustBroker = internalTrustBroker;
	}
	
	private Map<TrustedEntityId,TrustSimilarity> extractTrustSimilarity(
			final TrustedEntityId myTeid, final Map<String,Set<TrustEvidence>> trustEvidenceMap) 
					throws TrustException {
		
		final Map<TrustedEntityId,TrustSimilarity> trustSimilarityMap =
				new LinkedHashMap<TrustedEntityId, TrustSimilarity>();
		
		final Map<TrustedEntityId,Set<TrustedEntityId>> trustPrefsMap =
				this.extractTrustPrefs(trustEvidenceMap);
		LOG.info("extractTrustSimilarity: trustPrefsMap={}", trustPrefsMap);
		final Set<TrustedEntityId> myTrustees = (trustPrefsMap.get(myTeid) != null)
				? trustPrefsMap.get(myTeid) : Collections.<TrustedEntityId>emptySet(); 
		for (final Map.Entry<TrustedEntityId, Set<TrustedEntityId>> trustPrefs : trustPrefsMap.entrySet()) {
			if (trustPrefs.getKey().equals(myTeid)) {
				continue;
			}
			final Set<TrustedEntityId> sharedTrustees = new LinkedHashSet<TrustedEntityId>(trustPrefs.getValue());
			sharedTrustees.retainAll(myTrustees);
			final Set<TrustedEntityId> unsharedTrustees = new LinkedHashSet<TrustedEntityId>(trustPrefs.getValue());
			unsharedTrustees.removeAll(myTrustees);
			trustSimilarityMap.put(trustPrefs.getKey(), 
					new TrustSimilarity(sharedTrustees, unsharedTrustees));
		}
		
		return trustSimilarityMap;
	}
	
	/**
	 * Map<TEID,Set<TEID>> = {
	 *   userTeidX = { teid1, teid2 }
	 *   userTeidY = { teid2 }
	 *   userTeidZ = { teid1, teid2, teid3, teid4 }
	 * }
	 * @param trustEvidenceMap
	 * @return
	 */
	private Map<TrustedEntityId,Set<TrustedEntityId>> extractTrustPrefs(
			final Map<String,Set<TrustEvidence>> trustEvidenceMap) 
					throws TrustException {
		
		final Map<TrustedEntityId,Set<TrustedEntityId>> trustPrefsMap = 
				new LinkedHashMap<TrustedEntityId, Set<TrustedEntityId>>(trustEvidenceMap.size());
		
		for (final Map.Entry<String, Set<TrustEvidence>> trustorEntry : trustEvidenceMap.entrySet()) {
			final TrustedEntityId trustorId = new TrustedEntityId(TrustedEntityType.CSS, trustorEntry.getKey());
			final Set<TrustedEntityId> trustPrefs = new LinkedHashSet<TrustedEntityId>();
			for (final TrustEvidence evidence : trustEvidenceMap.get(trustorEntry.getKey())) {
				trustPrefs.add(evidence.getObjectId());
			}
			trustPrefsMap.put(trustorId, trustPrefs);
		}
		
		return trustPrefsMap;
	}
	
	private void fail(String errorMesg) {
		
		this.performanceTestResult = new PerformanceTestResult(
				this.getClass().getName(), errorMesg, 
				PerformanceTestResult.ERROR_STATUS);
		this.performanceLowerTester.testFinish(this.performanceTestResult);
	}
	
	private class TrustSimilarity {
		
		private final Set<TrustedEntityId> sharedTrustees;
		
		private final Set<TrustedEntityId> unsharedTrustees;
		
		private TrustSimilarity(final Set<TrustedEntityId> sharedTrustees,
				final Set<TrustedEntityId> unsharedTrustees) {
			
			this.sharedTrustees = sharedTrustees;
			this.unsharedTrustees = unsharedTrustees;
		}
		
		@SuppressWarnings("unused")
		private Set<TrustedEntityId> getSharedTrustees() {
			
			return this.sharedTrustees;
		}
		
		private Set<TrustedEntityId> getUnsharedTrustees() {
			
			return this.unsharedTrustees;
		}

		/*
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			
			final StringBuilder sb = new StringBuilder();
			
			sb.append("TrustSimilarity [sharedTrustees=");
			sb.append(this.sharedTrustees);
			sb.append(", unsharedTrustees=");
			sb.append(this.unsharedTrustees);
			sb.append("]");
			
			return sb.toString();
		}
	}
	
	private class TrustUpdateEventListener implements ITrustUpdateEventListener {
		
		private TrustRelationship updatedTrustRelationship;
		
		/*
		 * @see org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener#onUpdate(org.societies.api.privacytrust.trust.event.TrustUpdateEvent)
		 */
		@Override
		public void onUpdate(TrustUpdateEvent event) {
			
			this.updatedTrustRelationship = event.getTrustRelationship();
			// Signal event has been received
			TestIndirectTrustPerformance.this.cdLatch.countDown();
		}
		
		private TrustRelationship getUpdatedTrustRelationship() {
			
			return this.updatedTrustRelationship;
		}
	}
}
