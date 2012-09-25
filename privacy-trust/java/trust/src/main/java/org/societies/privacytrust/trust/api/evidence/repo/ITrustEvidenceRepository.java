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
	 * Retrieves all the direct trust evidence associated with the specified 
	 * {@link TrustedEntityId}.
	 * 
	 * @param teid
	 *            the {@link TrustedEntityId} whose direct trust evidence to
	 *            retrieve 
	 * @return a set containing the direct trust evidence associated with the
	 *         specified trusted entity
	 * @throws TrustEvidenceRepositoryException
	 *             if there is a problem accessing the Trust Evidence Repository
	 * @throws NullPointerException
	 *             if the specified teid is <code>null</code>
	 */
	public Set<IDirectTrustEvidence> retrieveAllDirectEvidence(
			final TrustedEntityId teid) throws TrustEvidenceRepositoryException;
	
	/**
	 * Retrieves the direct trust evidence associated with the specified 
	 * {@link TrustedEntityId}. The method allows specifying optional search 
	 * criteria, such as, the {@link TrustEvidenceType trust evidence type}, as
	 * well as, the time range in which the evidence was collected.
	 * 
	 * @param teid
	 *            the {@link TrustedEntityId} whose direct trust evidence to
	 *            retrieve 
	 * @param type
	 *            the type of the direct trust evidence to retrieve (optional) 
	 * @param startDate
	 *            the start time (optional) 
	 * @param endDate
	 *            the end time (optional)
	 * @return the direct trust evidence matching the specified search criteria
	 * @throws TrustEvidenceRepositoryException
	 *             if there is a problem accessing the Trust Evidence Repository
	 * @throws NullPointerException
	 *             if the specified teid is <code>null</code>
	 * @since 0.3
	 */
	public Set<IDirectTrustEvidence> retrieveDirectEvidence(
			final TrustedEntityId teid, final TrustEvidenceType type,			
			final Date startDate, final Date endDate) throws TrustEvidenceRepositoryException;
	
	/**
	 * Retrieves all the indirect trust evidence associated with the specified 
	 * {@link TrustedEntityId}.
	 * 
	 * @param teid
	 *            the {@link TrustedEntityId} whose indirect trust evidence to
	 *            retrieve 
	 * @return a set containing the indirect trust evidence associated with the
	 *         specified trusted entity
	 * @throws TrustEvidenceRepositoryException
	 *             if there is a problem accessing the Trust Evidence Repository
	 * @throws NullPointerException
	 *             if the specified teid is <code>null</code>
	 */
	public Set<IIndirectTrustEvidence> retrieveAllIndirectEvidence(
			final TrustedEntityId teid)	throws TrustEvidenceRepositoryException;
	
	/**
	 * Retrieves the indirect trust evidence associated with the specified 
	 * {@link TrustedEntityId}. The method allows specifying optional search 
	 * criteria, such as, the {@link TrustEvidenceType trust evidence type}, as
	 * well as, the time range in which the evidence was collected.
	 * 
	 * @param teid
	 *            the {@link TrustedEntityId} whose indirect trust evidence to
	 *            retrieve 
	 * @param type
	 *            the type of the indirect trust evidence to retrieve (optional) 
	 * @param startDate
	 *            the start time (optional) 
	 * @param endDate
	 *            the end time (optional)
	 * @return the indirect trust evidence matching the specified search criteria
	 * @throws TrustEvidenceRepositoryException
	 *             if there is a problem accessing the Trust Evidence Repository
	 * @throws NullPointerException
	 *             if the specified teid is <code>null</code>
	 * @since 0.3
	 */
	public Set<IIndirectTrustEvidence> retrieveIndirectEvidence(
			final TrustedEntityId teid, final TrustEvidenceType type,
			final Date startDate, final Date endDate) throws TrustEvidenceRepositoryException;
	
	/**
	 * Removes all the direct trust evidence associated with the specified 
	 * {@link TrustedEntityId}.
	 * 
	 * @param teid
	 *            the {@link TrustedEntityId} whose direct trust evidence to
	 *            remove
	 * @throws TrustEvidenceRepositoryException
	 *             if there is a problem accessing the Trust Evidence Repository
	 * @throws NullPointerException
	 *             if the specified teid is <code>null</code>
	 */
	public void removeAllDirectEvidence(final TrustedEntityId teid)
			throws TrustEvidenceRepositoryException;
	
	/**
	 * Removes the direct trust evidence associated with the specified 
	 * {@link TrustedEntityId}. The method allows specifying optional search 
	 * criteria, such as, the {@link TrustEvidenceType trust evidence type}, as
	 * well as, the time range in which the evidence was collected.
	 * 
	 * @param teid
	 *            the {@link TrustedEntityId} whose direct trust evidence to
	 *            remove
	 * @param type
	 *            the type of the direct trust evidence to remove (optional) 
	 * @param startDate
	 *            the start time (optional) 
	 * @param endDate
	 *            the end time (optional)
	 * @throws TrustEvidenceRepositoryException
	 *             if there is a problem accessing the Trust Evidence Repository
	 * @throws NullPointerException
	 *             if the specified teid is <code>null</code>
	 * @since 0.3
	 */
	public void removeDirectEvidence(final TrustedEntityId teid, 
			final TrustEvidenceType type, final Date startDate,
			final Date endDate) throws TrustEvidenceRepositoryException;
	
	/**
	 * Removes all the indirect trust evidence associated with the specified 
	 * {@link TrustedEntityId}.
	 * 
	 * @param teid
	 *            the {@link TrustedEntityId} whose indirect trust evidence to
	 *            remove
	 * @throws TrustEvidenceRepositoryException
	 *             if there is a problem accessing the Trust Evidence Repository
	 * @throws NullPointerException
	 *             if the specified teid is <code>null</code>
	 */
	public void removeAllIndirectEvidence(final TrustedEntityId teid)
			throws TrustEvidenceRepositoryException;
	
	/**
	 * Removes the indirect trust evidence associated with the specified 
	 * {@link TrustedEntityId}. The method allows specifying optional search 
	 * criteria, such as, the {@link TrustEvidenceType trust evidence type}, as
	 * well as, the time range in which the evidence was collected.
	 * 
	 * @param teid
	 *            the {@link TrustedEntityId} whose indirect trust evidence to
	 *            remove
	 * @param type
	 *            the type of the indirect trust evidence to remove (optional) 
	 * @param startDate
	 *            the start time (optional) 
	 * @param endDate
	 *            the end time (optional)
	 * @throws TrustEvidenceRepositoryException
	 *             if there is a problem accessing the Trust Evidence Repository
	 * @throws NullPointerException
	 *             if the specified teid is <code>null</code>
	 * @since 0.3
	 */
	public void removeIndirectEvidence(final TrustedEntityId teid,
			final TrustEvidenceType type, final Date startDate, 
			final Date endDate) throws TrustEvidenceRepositoryException;
}