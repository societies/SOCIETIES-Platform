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
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4.1
 */
@Service
@Lazy(value = false)
public class TrustEvidenceCollector implements 
	org.societies.api.privacytrust.trust.evidence.ITrustEvidenceCollector {
	
	private static final Logger LOG = LoggerFactory.getLogger(TrustEvidenceCollector.class);
	
	@Autowired(required=true)
	private ITrustEvidenceCollector internalTrustEvidenceCollector;
	
	TrustEvidenceCollector() {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
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
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Adding direct trust evidence with subjectId '"
					+ subjectId	+ "', objectId '" + objectId + "', type '" 
					+ type + "', timestamp '" + timestamp + "' and info '"
					+ info + "' on behalf of requestor '" + requestor + "'");
		
		// TODO access control
		
		this.internalTrustEvidenceCollector.addDirectEvidence(requestor,
				subjectId, objectId, type, timestamp, info);
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
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Adding indirect trust evidence with subjectId '"
					+ subjectId	+ "', objectId '" + objectId + "', type '" 
					+ type + "', timestamp '" + timestamp + "', info '" + info
					+ "' and sourceId '" + sourceId 
					+ "' on behalf of requestor '" + requestor + "'");
		
		// TODO access control
		
		this.internalTrustEvidenceCollector.addIndirectEvidence(requestor,
				subjectId, objectId, type, timestamp, info, sourceId);
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.evidence.ITrustEvidenceCollector#addDirectEvidence(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable)
	 */
	@Override
	public void addDirectEvidence(final TrustedEntityId subjectId,
			final TrustedEntityId objectId, final TrustEvidenceType type,
			Date timestamp, Serializable info) throws TrustException {
		
		if (subjectId == null)
			throw new NullPointerException("subjectId can't be null");
		if (objectId == null)
			throw new NullPointerException("objectId can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Adding direct trust evidence with  subjectId '"
					+ subjectId	+ "', objectId '" + objectId + "', type '" 
					+ type + "', timestamp '" + timestamp + "' and info '"
					+ info + "'");
		this.internalTrustEvidenceCollector.addDirectEvidence(subjectId,
				objectId, type, timestamp, info);
	}

	/*
	 * @see org.societies.api.privacytrust.trust.evidence.ITrustEvidenceCollector#addIndirectEvidence(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable, org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public void addIndirectEvidence(final TrustedEntityId subjectId,
			final TrustedEntityId objectId, TrustEvidenceType type, Date timestamp,
			Serializable info, final TrustedEntityId sourceId) throws TrustException {
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Adding indirect trust evidence with  subjectId '"
					+ subjectId	+ "', objectId '" + objectId + "', type '" 
					+ type + "', timestamp '" + timestamp + "', info '" + info
					+ "' and sourceId '" + sourceId + "'");
		this.internalTrustEvidenceCollector.addIndirectEvidence(subjectId,
				objectId, type, timestamp, info, sourceId);
	}
}