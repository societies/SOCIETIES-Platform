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
package org.societies.privacytrust.trust.impl.evidence;

import java.io.Serializable;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustEvidence;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.ITrustNodeMgr;
import org.societies.privacytrust.trust.api.evidence.remote.ITrustEvidenceCollectorRemoteClient;
import org.societies.privacytrust.trust.api.evidence.remote.ITrustEvidenceCollectorRemoteClientCallback;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.osgi.service.ServiceUnavailableException;
import org.springframework.stereotype.Service;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.2
 */
@Service
public class InternalTrustEvidenceCollector implements ITrustEvidenceCollector {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(InternalTrustEvidenceCollector.class);
	
	/** The Trust Mgr service reference. */
	@Autowired(required=true)
	private ITrustNodeMgr trustNodeMgr;
	
	/** The Trust Evidence Repository service reference. */
	@Autowired(required=false)
	private ITrustEvidenceRepository trustEvidenceRepository;
	
	/** The internal Trust Evidence Collector Client service reference. */
	@Autowired(required=false)
	private ITrustEvidenceCollectorRemoteClient trustEvidenceCollectorRemoteClient;
	
	@Autowired(required=true)
	InternalTrustEvidenceCollector(IEventMgr eventMgr) throws Exception {
		
		LOG.info("{} instantiated", this.getClass());
		
		try {
			LOG.info("Registering for trust evidence events...");
			eventMgr.subscribeInternalEvent(new TrustEvidenceEventListener(), 
					new String[] { EventTypes.TRUST_EVIDENCE_EVENT }, null);
		} catch (Exception e) {
			LOG.error(this.getClass() + " could not be initialised: "
					+ e.getLocalizedMessage(), e);
			throw e;
		}
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.evidence.ITrustEvidenceCollector#addDirectEvidence(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable)
	 */
	@Override
	public void addDirectEvidence(final Requestor requestor, 
			final TrustedEntityId subjectId, final TrustedEntityId objectId, 
			final TrustEvidenceType type, final Date timestamp, 
			final Serializable info) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (subjectId == null)
			throw new NullPointerException("subjectId can't be null");
		if (objectId == null)
			throw new NullPointerException("objectId can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		
		this.doAddEvidence(requestor, subjectId, objectId, type, 
				timestamp, info, null);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addDirectEvidence(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable)
	 */
	@Override
	public void addDirectEvidence(final TrustedEntityId subjectId, 
			final TrustedEntityId objectId, final TrustEvidenceType type,
			final Date timestamp, final Serializable info) 
					throws TrustException {
		
		if (subjectId == null)
			throw new NullPointerException("subjectId can't be null");
		if (objectId == null)
			throw new NullPointerException("objectId can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		
		this.doAddEvidence(null, subjectId, objectId, type, timestamp,
				info, null);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.evidence.ITrustEvidenceCollector#addIndirectEvidence(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void addIndirectEvidence(final Requestor requestor, 
			final TrustedEntityId subjectId, final TrustedEntityId objectId, 
			final TrustEvidenceType type, final Date timestamp, 
			final Serializable info, final TrustedEntityId sourceId)
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (subjectId == null)
			throw new NullPointerException("subjectId can't be null");
		if (objectId == null)
			throw new NullPointerException("objectId can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		if (sourceId == null)
			throw new NullPointerException("sourceId can't be null");
		
		this.doAddEvidence(requestor, subjectId, objectId, type, 
				timestamp, info, sourceId);
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addIndirectEvidence(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void addIndirectEvidence(final TrustedEntityId subjectId, 
			final TrustedEntityId objectId, final TrustEvidenceType type,
			final Date timestamp, final Serializable info,
			final TrustedEntityId sourceId)	throws TrustException {
		
		if (subjectId == null)
			throw new NullPointerException("subjectId can't be null");
		if (objectId == null)
			throw new NullPointerException("objectId can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		
		this.doAddEvidence(null, subjectId, objectId, type, timestamp,
				info, sourceId);
	}

	private void doAddEvidence(Requestor requestor,
			final TrustedEntityId subjectId, final TrustedEntityId objectId,
			final TrustEvidenceType type, final Date timestamp, 
			final Serializable info, final TrustedEntityId sourceId)
					throws TrustException {
		
		if (requestor == null)
			requestor = this.trustNodeMgr.getLocalRequestor();
		
		LOG.debug("Adding trust evidence with subjectId '{}'"
					+ ", objectId '{}', type '{}', timestamp '{}'"
				    + ", info '{}' and sourceId '{}'"
					+ "' on behalf of requestor '{}'", new Object[] {
							subjectId, objectId, type, timestamp, info, sourceId, requestor });
		
		try {
			final boolean doLocal = this.trustNodeMgr.isMaster();
			LOG.debug("doLocal is {}", doLocal);
			if (doLocal) {

				if (this.trustEvidenceRepository == null)
					throw new TrustEvidenceCollectorException(
							"ITrustEvidenceRepository service is not available");
				this.trustEvidenceRepository.addEvidence(
						subjectId, objectId, type, timestamp, info, sourceId);
				
			} else {

				final TrustEvidenceCollectorRemoteCallback callback = 
						new TrustEvidenceCollectorRemoteCallback();
				if (this.trustEvidenceCollectorRemoteClient == null)
					throw new TrustEvidenceCollectorException(
							"ITrustEvidenceCollectorRemote service is not available");
				this.trustEvidenceCollectorRemoteClient.addIndirectEvidence(
						requestor, subjectId, objectId, type, timestamp, info,
						sourceId, callback);
				synchronized (callback) {
					try {
						callback.wait();
						if (callback.getException() != null)
							throw callback.getException();
						
					} catch (InterruptedException ie) {
						throw new TrustEvidenceCollectorException(
								"Interrupted while adding indirect trust evidence with subjectId '"
										+ subjectId	+ "', objectId '" + objectId 
										+ "', type '" + type + "', timestamp '" 
										+ timestamp + "', info '" + info
										+ "' and sourceId '" + sourceId 
										+ "' on behalf of requestor '" + requestor + "'");
					}
				}
			}
			
		} catch (ServiceUnavailableException sue) {
			throw new TrustEvidenceCollectorException(
					sue.getLocalizedMessage(), sue);
		}
	}
	
	private class TrustEvidenceCollectorRemoteCallback 
		implements ITrustEvidenceCollectorRemoteClientCallback {
		
		private TrustException trustException;

		/*
		 * @see org.societies.privacytrust.trust.api.evidence.remote.ITrustEvidenceCollectorRemoteClientCallback#onAddedDirectEvidence()
		 */
		@Override
		public void onAddedDirectEvidence() {
			
			synchronized (this) {
	            notifyAll();
	        }
		}

		/*
		 * @see org.societies.privacytrust.trust.api.evidence.remote.ITrustEvidenceCollectorRemoteClientCallback#onAddedIndirectEvidence()
		 */
		@Override
		public void onAddedIndirectEvidence() {
			
			synchronized (this) {
	            notifyAll();
	        }
		}

		/*
		 * @see org.societies.privacytrust.trust.api.evidence.remote.ITrustEvidenceCollectorRemoteClientCallback#onException(org.societies.api.privacytrust.trust.TrustException)
		 */
		@Override
		public void onException(TrustException trustException) {
			
			this.trustException = trustException;
			synchronized (this) {
	            notifyAll();
	        }
		}
		
		private TrustException getException() {
			
			return this.trustException;
		}
	}
	
	private class TrustEvidenceEventListener extends EventListener {
	
		/*
		 * @see org.societies.api.osgi.event.EventListener#handleExternalEvent(org.societies.api.osgi.event.CSSEvent)
		 */
		@Override
		public void handleExternalEvent(CSSEvent event) {
			
			LOG.warn("Received unexpected external event {}", event);
		}

		/*
		 * @see org.societies.api.osgi.event.EventListener#handleInternalEvent(org.societies.api.osgi.event.InternalEvent)
		 */
		@Override
		public void handleInternalEvent(InternalEvent event) {
			
			LOG.debug("Received internal event {}", event);
			
			if (!(event.geteventInfo() instanceof TrustEvidence)) {
				LOG.error("Could not handle internal '" + event.geteventType() + "'"
						+ "event: Expected event info of type " 
						+ TrustEvidence.class + " but was " + event.geteventInfo());
				return;
			}
			final TrustEvidence evidence = (TrustEvidence) event.geteventInfo();
			final Requestor requestor = trustNodeMgr.getLocalRequestor();
			try {
				doAddEvidence(requestor, evidence.getSubjectId(),
						evidence.getObjectId(), evidence.getType(), 
						evidence.getTimestamp(), evidence.getInfo(),
						evidence.getSourceId());
			} catch (Exception e) {
				LOG.error("Could not add trust evidence '"
						+ evidence + "': " + e.getLocalizedMessage(), e);
			}
		}
	}
}