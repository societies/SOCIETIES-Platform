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
package org.societies.privacytrust.trust.api.evidence.repo;

import java.util.Date;
import java.util.Set;

import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.privacytrust.trust.api.evidence.model.IDirectTrustEvidence;
import org.societies.privacytrust.trust.api.evidence.model.IIndirectTrustEvidence;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
public interface ITrustEvidenceRepository {

	/**
	 * Adds the specified {@link ITrustEvidence} to the trust evidence
	 * repository.
	 * 
	 * @param evidence
	 *            the evidence to be added to the repository
	 * @throws TrustEvidenceRepositoryException
	 *             if the specified evidence cannot be added to the repository
	 * @throws NullPointerException
	 *             if the specified evidence is <code>null</code>
	 */
	public void addEvidence(final ITrustEvidence evidence) throws TrustEvidenceRepositoryException;
	
	/**
	 * Retrieves the direct trust evidence matching the specified criteria. 
	 * The method allows specifying the subject, object, 
	 * {@link TrustEvidenceType trust evidence type}, as well as, the time
	 * range in which the evidence was collected.
	 * 
	 * @param subjectId
	 *            (optional) the {@link TrustedEntityId} of the subject associated with 
	 *            the direct trust evidence to retrieve.
	 * @param objectId
	 *            (optional) the {@link TrustedEntityId} of the object associated with 
	 *            the direct trust evidence to retrieve.
	 * @param type
	 *            (optional) the type of the direct trust evidence to retrieve. 
	 * @param startDate
	 *            (optional) the start time. 
	 * @param endDate
	 *            (optional) the end time.
	 * @return the direct trust evidence matching the specified search criteria
	 * @throws TrustEvidenceRepositoryException
	 *             if there is a problem accessing the Trust Evidence Repository
	 * @since 0.5
	 */
	public Set<IDirectTrustEvidence> retrieveDirectEvidence(
			final TrustedEntityId subjectId, final TrustedEntityId objectId,
			final TrustEvidenceType type, final Date startDate, 
			final Date endDate) throws TrustEvidenceRepositoryException;
	
	/**
	 * Retrieves the indirect trust evidence matching the specified criteria. 
	 * The method allows specifying the subject, object, 
	 * {@link TrustEvidenceType trust evidence type}, as well as, the time
	 * range in which the evidence was collected.
	 * 
	 * @param subjectId
	 *            (optional) the {@link TrustedEntityId} of the subject associated with 
	 *            the indirect trust evidence to retrieve.
	 * @param objectId
	 *            (optional) the {@link TrustedEntityId} of the object associated with 
	 *            the indirect trust evidence to retrieve.
	 * @param type
	 *            (optional) the type of the indirect trust evidence to retrieve. 
	 * @param startDate
	 *            (optional) the start time. 
	 * @param endDate
	 *            (optional) the end time. 
	 * @param sourceId
	 *            (optional) the {@link TrustedEntityId} of the source associated with 
	 *            the indirect trust evidence to retrieve.
	 * @return the indirect trust evidence matching the specified search criteria
	 * @throws TrustEvidenceRepositoryException
	 *             if there is a problem accessing the Trust Evidence Repository
	 * @since 0.5
	 */
	public Set<IIndirectTrustEvidence> retrieveIndirectEvidence(
			final TrustedEntityId subjectId, final TrustedEntityId objectId,
			final TrustEvidenceType type, final Date startDate, 
			final Date endDate, final TrustedEntityId sourceId)
					throws TrustEvidenceRepositoryException;
	
	/**
	 * Removes the direct trust evidence matching the specified criteria. 
	 * The method allows specifying the subject, object, 
	 * {@link TrustEvidenceType trust evidence type}, as well as, the time
	 * range in which the evidence was collected.
	 * 
	 * @param subjectId
	 *            (optional) the {@link TrustedEntityId} of the subject associated with 
	 *            the direct trust evidence to remove.
	 * @param objectId
	 *            (optional) the {@link TrustedEntityId} of the object associated with 
	 *            the direct trust evidence to remove.
	 * @param type
	 *            (optional) the type of the direct trust evidence to remove. 
	 * @param startDate
	 *            (optional) the start time. 
	 * @param endDate
	 *            (optional) the end time.
	 * @since 0.5
	 */
	public void removeDirectEvidence(final TrustedEntityId subjectId,
			final TrustedEntityId objectId,	final TrustEvidenceType type, 
			final Date startDate, final Date endDate) 
					throws TrustEvidenceRepositoryException;
	
	/**
	 * Removes the direct trust evidence matching the specified criteria. 
	 * The method allows specifying the subject, object, 
	 * {@link TrustEvidenceType trust evidence type}, as well as, the time
	 * range in which the evidence was collected.
	 * 
	 * @param subjectId
	 *            (optional) the {@link TrustedEntityId} of the subject associated with 
	 *            the direct trust evidence to remove.
	 * @param objectId
	 *            (optional) the {@link TrustedEntityId} of the object associated with 
	 *            the direct trust evidence to remove.
	 * @param type
	 *            (optional) the type of the direct trust evidence to remove. 
	 * @param startDate
	 *            (optional) the start time. 
	 * @param endDate
	 *            (optional) the end time.
	 * @param sourceId
	 *            (optional) the {@link TrustedEntityId} of the source associated with 
	 *            the indirect trust evidence to remove.
	 * @since 0.5
	 */
	public void removeIndirectEvidence(final TrustedEntityId subjectId,
			final TrustedEntityId objectId, final TrustEvidenceType type,
			final Date startDate, final Date endDate, 
			final TrustedEntityId sourceId)	
					throws TrustEvidenceRepositoryException;
}