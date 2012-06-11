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
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.privacytrust.trust.TrustException;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.societies.privacytrust.trust.impl.evidence.repo.model.DirectTrustEvidence;
import org.societies.privacytrust.trust.impl.evidence.repo.model.IndirectTrustEvidence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.2
 */
@Service
public class TrustEvidenceCollector implements ITrustEvidenceCollector {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(TrustEvidenceCollector.class);
	
	/** The Trust Evidence Repository service reference. */
	@Autowired(required = true)
	private ITrustEvidenceRepository evidenceRepo;
	
	TrustEvidenceCollector() {
		
		LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addTrustRating(org.societies.api.identity.IIdentity, org.societies.api.identity.IIdentity, double, java.util.Date)
	 */
	@Override
	public void addTrustRating(final IIdentity trustor, final IIdentity trustee,
			final double rating, Date timestamp) throws TrustException {
		
		if (trustor == null)
			throw new NullPointerException("trustor can't be null");
		if (trustee == null)
			throw new NullPointerException("trustee can't be null");
		
		if (!IdentityType.CSS.equals(trustor.getType()))
			throw new IllegalArgumentException("trustor is not a CSS");
		if (!IdentityType.CSS.equals(trustee.getType()) && !IdentityType.CIS.equals(trustee.getType()))
			throw new IllegalArgumentException("trustee is neither a CSS nor a CIS");
		if (rating < 0d || rating > 1d)
			throw new IllegalArgumentException("rating is not in the range of [0,1]");
		
		// if timestamp is null assign current time
		if (timestamp == null)
			timestamp = new Date();
		
		final TrustedEntityType entityType;
		if (IdentityType.CSS.equals(trustee.getType()))
			entityType = TrustedEntityType.CSS;
		else // if (IdentityType.CIS.equals(trustee.getType()))
			entityType = TrustedEntityType.CIS;
		final TrustedEntityId teid = new TrustedEntityId(trustor.toString(), entityType, trustee.toString());
		this.addDirectEvidence(teid, TrustEvidenceType.RATED, timestamp, new Double(rating));
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addTrustRating(org.societies.api.identity.IIdentity, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, double, java.util.Date)
	 */
	@Override
	public void addTrustRating(final IIdentity trustor,
			final ServiceResourceIdentifier trustee, final double rating, 
			Date timestamp)	throws TrustException {
		
		if (trustor == null)
			throw new NullPointerException("trustor can't be null");
		if (trustee == null)
			throw new NullPointerException("trustee can't be null");
		
		if (!IdentityType.CSS.equals(trustor.getType()))
			throw new IllegalArgumentException("trustor is not a CSS");
		if (rating < 0d || rating > 1d)
			throw new IllegalArgumentException("rating is not in the range of [0,1]");
		
		// if timestamp is null assign current time
		if (timestamp == null)
			timestamp = new Date();
		
		final TrustedEntityType entityType = TrustedEntityType.SVC;
		final TrustedEntityId teid = new TrustedEntityId(trustor.toString(), entityType, trustee.toString());
		this.addDirectEvidence(teid, TrustEvidenceType.RATED, timestamp, new Double(rating));
	}

	/*
	 * @see org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addDirectEvidence(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId, org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable)
	 */
	@Override
	public void addDirectEvidence(final TrustedEntityId teid, final TrustEvidenceType type,
			final Date timestamp, final Serializable info) throws TrustException {
		
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		
		final DirectTrustEvidence evidence = new DirectTrustEvidence(
				teid, type, timestamp, info);
		this.evidenceRepo.addEvidence(evidence);
	}

	/*
	 * @see org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addIndirectEvidence(java.lang.String, org.societies.api.internal.privacytrust.trust.model.TrustedEntityId, org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable)
	 */
	@Override
	public void addIndirectEvidence(final String source, final TrustedEntityId teid,
			final TrustEvidenceType type, final Date timestamp, final Serializable info)
			throws TrustException {
		
		if (source == null)
			throw new NullPointerException("source can't be null");
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		
		final IndirectTrustEvidence evidence = new IndirectTrustEvidence(
				teid, type, timestamp, info, source);
		this.evidenceRepo.addEvidence(evidence);
	}
}