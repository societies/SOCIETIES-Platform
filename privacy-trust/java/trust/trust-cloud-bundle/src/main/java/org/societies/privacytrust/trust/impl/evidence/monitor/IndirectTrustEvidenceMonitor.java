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
package org.societies.privacytrust.trust.impl.evidence.monitor;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.ITrustNodeMgr;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.api.evidence.repo.TrustEvidenceRepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class is used to acquire trust evidence based on the trust opinions of
 * the CSSs this CSS is directly connected to. More specifically, it adds 
 * {@link IndirectTrustEvidence} to the Trust Evidence Repository by monitoring
 * {@link TrustValueType#DIRECT direct} trust value updates of other CSSs.
 * <p>
 * The generated pieces of Indirect Trust Evidence are then processed by the
 * Direct Trust Engine in order to (re)evaluate the indirect trust in the
 * referenced entities, i.e. CSS, CISs or services, on behalf of the CSS owner.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS) 
 * @since 1.0
 */
@Service
public class IndirectTrustEvidenceMonitor implements ITrustUpdateEventListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(IndirectTrustEvidenceMonitor.class);
	
	/** The time to wait between registration attempts for DIRECT trust updates (in seconds) */
	private static final long WAIT = 60l;
	
	@Autowired(required=true)
	private ITrustEvidenceCollector trustEvidenceCollector;
	
	@Autowired(required=true)
	private ITrustEvidenceRepository trustEvidenceRepository;
	
	private ITrustBroker trustBroker;
	
	private ITrustNodeMgr trustNodeMgr;
	
	/** The connections registered for DIRECT trust updates. */
	private final Set<TrustedEntityId> monitoredConnections = new CopyOnWriteArraySet<TrustedEntityId>();
	
	/** The connections to register for DIRECT trust updates. */
	private final Set<TrustedEntityId> unmonitoredConnections = new CopyOnWriteArraySet<TrustedEntityId>();
	
	/** The executor service. */
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	/** The scheduled executor service. */
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@Autowired
	IndirectTrustEvidenceMonitor(ITrustNodeMgr trustNodeMgr, 
			ITrustBroker trustBroker) throws Exception {
		
		LOG.info("{} instantiated", this.getClass());
		
		this.trustNodeMgr = trustNodeMgr;
		this.trustBroker = trustBroker;
		try {
			for (final TrustedEntityId myTeid : trustNodeMgr.getMyIds()) {
				this.retrieveConnections(myTeid);
				trustBroker.registerTrustUpdateListener(this, 
						new TrustQuery(myTeid).setTrustValueType(TrustValueType.DIRECT));
			}
			this.scheduler.scheduleWithFixedDelay(new MaintenanceDaemon(),
					WAIT, WAIT, TimeUnit.SECONDS);
		} catch (Exception e) {
			LOG.error("Failed to initialise " + this.getClass()
					+ ": " + e.getLocalizedMessage(), e);
			throw e;
		}
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener#onUpdate(org.societies.api.privacytrust.trust.event.TrustUpdateEvent)
	 */
	@Override
	public void onUpdate(TrustUpdateEvent event) {
		
		LOG.debug("Received event {}", event);
		
		if (event.getTrustRelationship() == null) {
			LOG.error("Could not handle DIRECT trust update event: "
					+ " TrustRelationship can't be null");
			return;
		}
		if (TrustValueType.DIRECT != event.getTrustRelationship().getTrustValueType()) {
			LOG.error("Could not handle trust update event: "
					+ " Unexpected TrustRelationship trust value type: " 
					+ event.getTrustRelationship().getTrustValueType());
			return;
		}
			
		this.executorService.submit(new DirectTrustUpdateHandler(event.getTrustRelationship()));
	}
	
	private class DirectTrustUpdateHandler implements Runnable {
		
		private final TrustRelationship updatedRelationship;
		
		private DirectTrustUpdateHandler(final TrustRelationship updatedRelationship) {
			
			this.updatedRelationship = updatedRelationship;
		}

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			final TrustedEntityId trustorId = this.updatedRelationship.getTrustorId();
			final TrustedEntityId trusteeId = this.updatedRelationship.getTrusteeId();
			// IF the trustor is mine THEN
			//   If the trustee is:
			//     1. of type CSS, AND
			//     2. not me, AND
			//     3. not already monitored connection THEN
			//       Add trustee to connections to be monitored
			// ELSE
			//   Add indirect trust evidence
			if (IndirectTrustEvidenceMonitor.this.trustNodeMgr.getMyIds().contains(trustorId)) {
				if (TrustedEntityType.CSS != trusteeId.getEntityType()
						|| trustorId.equals(trusteeId)) {
					LOG.debug("Nothing to do - '{}' cannot be monitored", trusteeId);
					return;
				}
				if (IndirectTrustEvidenceMonitor.this.monitoredConnections.contains(trusteeId)) {
					LOG.debug("Nothing to do - '{}' already monitored", trusteeId);
					return;
				} else {
					LOG.debug("Adding '{}' to connections to be monitored", trusteeId);
					IndirectTrustEvidenceMonitor.this.unmonitoredConnections.add(trusteeId);
				}
			} else {
				try {
					IndirectTrustEvidenceMonitor.this.addIndirectEvidence(
							this.updatedRelationship, trustorId);
				} catch (TrustException te) {
					LOG.error("Could not add indirect trust evidence from relationship "
							+ this.updatedRelationship + ": " 
							+ te.getLocalizedMessage(), te);
				}
			}
		}
	}
	
	/**
	 * Runs periodically in the background to perform various maintenance
	 * tasks.
	 */
	private class MaintenanceDaemon implements Runnable {

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			LOG.debug("Connections to monitor for direct trust updates: {}", 
					IndirectTrustEvidenceMonitor.this.unmonitoredConnections);
			for (final TrustedEntityId connectionId : new HashSet<TrustedEntityId>(
					IndirectTrustEvidenceMonitor.this.unmonitoredConnections)) {

				try {
					IndirectTrustEvidenceMonitor.this.retrieveOpinions(connectionId);
					IndirectTrustEvidenceMonitor.this.trustBroker.registerTrustUpdateListener(
							IndirectTrustEvidenceMonitor.this, 
							new TrustQuery(connectionId).setTrustValueType(TrustValueType.DIRECT));
					if (IndirectTrustEvidenceMonitor.this.unmonitoredConnections.remove(connectionId)) {
						LOG.info("Started monitoring connection '{}' for direct trust updates", connectionId);
						IndirectTrustEvidenceMonitor.this.monitoredConnections.add(connectionId);
					}
				} catch (Exception e) {
					LOG.warn("Failed to register for trust updates of CSS '" + connectionId 
							+ "': " + e.getLocalizedMessage() + ". Will re-attempt to register in "
							+ WAIT + " seconds...");
				}
			} // for each connectionId ends
		}
	}
	
	private void addIndirectEvidence(final TrustRelationship trustRelationship,
			final TrustedEntityId sourceId)	throws TrustException {
		
		final TrustedEntityId subjectId = trustRelationship.getTrustorId();
		final TrustedEntityId objectId = trustRelationship.getTrusteeId();
		final TrustEvidenceType type = TrustEvidenceType.DIRECTLY_TRUSTED;
		final Double trustValue = trustRelationship.getTrustValue();
		final Date ts = trustRelationship.getTimestamp();
		
		// Ignore evidence where subjectId == objectId
		if (!subjectId.equals(objectId)) {
			LOG.debug("Adding indirect trust evidence: subjectId={}, objectId={}"
					+ ", type={}, ts={}, trustValue={}, sourceId={}", 
					new Object[] { subjectId, objectId, type, ts, trustValue, sourceId });
			this.trustEvidenceCollector.addIndirectEvidence(subjectId, 
					objectId, type, ts, trustValue, sourceId);
		} else {
			LOG.debug("Ignoring indirect trust evidence: subjectId={}, objectId={}"
				+ ", type={}, ts={}, trustValue={}, sourceId={}", 
				new Object[] { subjectId, objectId, type, ts, trustValue, sourceId });
		}
	}
	
	private void retrieveOpinions(final TrustedEntityId connectionId)
			throws TrustException {
		
		LOG.debug("retrieveOpinions: connectionId={}", connectionId);
		
		final Set<TrustRelationship> retrievedRelationships;
		final Set<ITrustEvidence> existingEvidenceSet;
		try {
			existingEvidenceSet = this.trustEvidenceRepository.retrieveLatestEvidence(
					connectionId, null, TrustEvidenceType.DIRECTLY_TRUSTED, connectionId);
			// Get the last evidence timestamp
			Date lastEvidenceTimestamp = null;
			final Iterator<ITrustEvidence> evidenceIter = existingEvidenceSet.iterator();
			while (evidenceIter.hasNext()) {
				lastEvidenceTimestamp = evidenceIter.next().getTimestamp();
			}
			// Fetch trust relationships after lastEvidenceTimestamp 
			// TODO Add fromDate param to TrustQuery 
			retrievedRelationships = this.trustBroker.retrieveTrustRelationships(
					new TrustQuery(connectionId).setTrustValueType(TrustValueType.DIRECT)).get();			
			for (final TrustRelationship retrievedRelationship : retrievedRelationships) {
				if (lastEvidenceTimestamp == null || retrievedRelationship.getTimestamp().compareTo(lastEvidenceTimestamp) > 0) {
						this.addIndirectEvidence(retrievedRelationship, connectionId);
				}
			}
		} catch (TrustEvidenceRepositoryException tere) {
			throw new TrustEvidenceMonitorException(
					"Could not retrieve latest indirect trust evidence originating from '"
							+ connectionId + "': " + tere.getLocalizedMessage(), tere);
		} catch (Exception e) {
			throw new TrustEvidenceMonitorException(
					"Could not retrieve direct trust relationships of trustor '"
							+ connectionId + "': " + e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * Retrieves the connections to be monitored on behalf of the specified
	 * trustor. More specifically, the method finds all CSSs directly trusted
	 * by the specified trustor; any CSSs not already monitored are added to
	 * the {@link #unmonitoredConnections} set.
	 * 
	 * @param myTeid
	 * @throws TrustException
	 */
	private void retrieveConnections(final TrustedEntityId myTeid) 
			throws TrustException {
		
		LOG.debug("retrieveConnections: myTeid={}", myTeid);
		
		try {
			final Set<TrustRelationship> trs = this.trustBroker.retrieveTrustRelationships(
					new TrustQuery(myTeid).setTrusteeType(TrustedEntityType.CSS)
					.setTrustValueType(TrustValueType.DIRECT)).get();
			for (final TrustRelationship tr : trs) {
				if (!myTeid.equals(tr.getTrusteeId()) 
						&& !this.monitoredConnections.contains(tr.getTrusteeId())) {
					this.unmonitoredConnections.add(tr.getTrusteeId());
				}
			}
		} catch (Exception e) {
			throw new TrustEvidenceMonitorException(
					"Interrupted while retrieving DIRECT trust relationships of trustor '"
					+ myTeid + "'");
		}
	}
}