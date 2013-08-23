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
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.privacytrust.trust.model.TrustEvidence;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.integration.performance.test.lower_tester.PerformanceLowerTester;
import org.societies.integration.performance.test.lower_tester.PerformanceTestMgmtInfo;
import org.societies.integration.performance.test.lower_tester.PerformanceTestResult;
import org.societies.integration.performance.test.upper_tester.trust.direct.ITestDirectTrustPerformance;
import org.springframework.osgi.service.ServiceUnavailableException;

/**
 * Implementation of the {@link ITestDirectTrustPerformance} interface.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.2
 */
public class TestDirectTrustPerformance implements ITestDirectTrustPerformance {

	private static Logger LOG = LoggerFactory.getLogger(TestDirectTrustPerformance.class);
	
	/** The time to wait for trust update events. */
	private static final long EVENT_TIMEOUT = 2000l;
	
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
	
	public TestDirectTrustPerformance() {
		
		LOG.info("{} instantiated", this.getClass().getName());
	}
	
	/*
	 * @see org.societies.integration.performance.test.upper_tester.trust.direct.ITestDirectTrustPerformance#testEvaluateDirectTrust(org.societies.integration.performance.test.lower_tester.PerformanceTestMgmtInfo, org.societies.integration.performance.test.upper_tester.trust.TrustEvidenceParams)
	 */
	@Override
	public void testEvaluateDirectTrust(
			PerformanceTestMgmtInfo performanceTestMgmtInfo,
			TrustEvidenceParams trustEvidenceParams) {

		// The following 2 lines are mandatory for the beginning of each test 
		this.performanceLowerTester = new PerformanceLowerTester(performanceTestMgmtInfo);
		this.performanceLowerTester.testStart(this.getClass().getName(), this.getCommManager());
		
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
			// Retrieve the IIdentity of my CSS to match associated trust evidence.
			final String myCssId = this.commManager.getIdManager().getCloudNode().getBareJid();

			// The set of trust evidence to evaluate
			final Set<TrustEvidence> trustEvidenceSet = trustEvidenceMap.get(myCssId);
			if (trustEvidenceSet == null || trustEvidenceSet.isEmpty()) {

				this.fail("Could not find trust evidence for this CSS in the specified TrustEvidenceParams JSON String: "
						+ trustEvidenceParams.getTrustEvidenceJsonString());
				return;
			}

			// Start test
			LOG.info("START testEvaluateDirectTrust for params: {}", trustEvidenceSet);
			final Set<TrustRelationship> result = new LinkedHashSet<TrustRelationship>();
			final TrustedEntityId myTeid = new TrustedEntityId(TrustedEntityType.CSS, myCssId);
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
				final boolean isTrustUpdated = this.cdLatch.await(EVENT_TIMEOUT, TimeUnit.MILLISECONDS);
				// Unregister from trust value updates of this entity
				this.internalTrustBroker.unregisterTrustUpdateListener(trustListener, trustQuery);
				if (!isTrustUpdated) {
					
					this.fail("Did not receive TrustUpdateEvent for evidence '"
							+ trustEvidence + "' in the specified timeout: " + EVENT_TIMEOUT + "ms");
					return;
				}
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
					this.fail("Expected trusteeId '" + trustEvidence.getObjectId() + "' but was '"
							+ updatedTrustRelationship.getTrusteeId() + "'");
					return;
				}
				if (TrustValueType.DIRECT != updatedTrustRelationship.getTrustValueType()) {
					this.fail("Expected trustValueType '" + TrustValueType.DIRECT + "' but was '"
							+ updatedTrustRelationship.getTrustValueType() + "'");
					return;
				}
				result.add(updatedTrustRelationship);
			}
			
			// End test
			LOG.info("END testEvaluateDirectTrust for params: {}", trustEvidenceSet);
			this.performanceTestResult = new PerformanceTestResult(this.getClass().getName(),
					"Updated trust relationships: " + result,
					PerformanceTestResult.SUCCESS_STATUS);
			this.performanceLowerTester.testFinish(this.performanceTestResult);

		} catch (TrustException te) {

			this.fail(te.getLocalizedMessage());

		}catch (InterruptedException ie) {
		
			this.fail("Interrupted while executing test: " + ie.getLocalizedMessage());
			
		} catch (ServiceUnavailableException sue) {

			this.fail(sue.getLocalizedMessage());
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
	
	private void fail(String errorMesg) {
		
		this.performanceTestResult = new PerformanceTestResult(
				this.getClass().getName(), errorMesg, 
				PerformanceTestResult.ERROR_STATUS);
		this.performanceLowerTester.testFinish(this.performanceTestResult);
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
			TestDirectTrustPerformance.this.cdLatch.countDown();
		}
		
		private TrustRelationship getUpdatedTrustRelationship() {
			
			return this.updatedTrustRelationship;
		}
	}
}