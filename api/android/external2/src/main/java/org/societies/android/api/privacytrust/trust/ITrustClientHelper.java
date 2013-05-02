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
package org.societies.android.api.privacytrust.trust;

import java.io.Serializable;

import org.societies.android.api.common.ADate;
import org.societies.android.api.services.ICoreSocietiesServices;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.1
 */
public interface ITrustClientHelper extends ICoreSocietiesServices {
	
    /**
	 * Retrieves all trust relationships of the specified trustor. The method
	 * returns an <i>empty</i> array if the identified trustor has not 
	 * established any trust relationships. 
	 *
	 * @param requestor 
	 *            (required) the requestor on whose behalf to retrieve the 
	 *            trust relationships.
	 * @param trustorId
	 *            (required) the identifier of the entity whose trust
	 *            relationships to retrieve.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 */
    public void retrieveTrustRelationships(final RequestorBean requestor,
    		final TrustedEntityIdBean trustorId, 
    		final ITrustClientCallback callback);
    
    /**
	 * Retrieves the trust relationships of the specified trustor with the
	 * supplied trustee. The method returns an <i>empty</i> array if no trust
	 * relationships exist between the identified trustor and trustee.
	 *
	 * @param requestor 
	 *            (required) the requestor on whose behalf to retrieve the 
	 *            trust relationships.
	 * @param trustorId
	 *            (required) the identifier of the entity whose trust 
	 *            relationships to retrieve.
	 * @param trusteeId
	 *            (required) the identifier of the entity trusted by the 
	 *            specified trustor.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 */
    public void retrieveTrustRelationships(final RequestorBean requestor,
    		final TrustedEntityIdBean trustorId, 
    		final TrustedEntityIdBean trusteeId,
    		final ITrustClientCallback callback);
    
    /**
	 * Retrieves the trust relationship of the specified type which the given
	 * trustor has established with the supplied trustee. The method returns 
	 * <code>null</code> if no trust relationship of the specified type has
	 * been established with the supplied trustee by the given trustor.
	 * 
	 * @param requestor
	 *            (required) the requestor on whose behalf to retrieve the 
	 *            trust relationship.
	 * @param trustorId
	 *            (required) the identifier of the entity whose trust 
	 *            relationship to retrieve.
	 * @param trusteeId
	 *            (required) the identifier of the entity trusted by the 
	 *            specified trustor.
	 * @param trustValueType
	 *            (required) the type of the trust relationship to retrieve,
	 *            i.e. one of {@link TrustValueTypeBean#DIRECT}, 
	 *            {@link TrustValueTypeBean#INDIRECT}, or
	 *            {@link TrustValueTypeBean#USER_PERCEIVED}.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>.
	 */
	public void retrieveTrustRelationship(final RequestorBean requestor, 
			final TrustedEntityIdBean trustorId, 
			final TrustedEntityIdBean trusteeId, 
			final TrustValueTypeBean trustValueType,
			final ITrustClientCallback callback);
    
    /**
	 * Retrieves the trust value of the given type which the specified trustor
	 * has assigned to the supplied trustee. The method returns <code>null</code>
	 * if no trust value has been assigned to the specified trustee by the given
	 * trustor.
	 * 
	 * @param requestor
	 *            (required) the requestor on whose behalf to retrieve the 
	 *            trust value.
	 * @param trustorId
	 *            (required) the identifier of the entity which has assigned 
	 *            the trust value to retrieve.
	 * @param trusteeId
	 *            the identifier of the entity whose trust value to retrieve.
	 * @param trustValueType
	 *            the type of the trust value to retrieve, i.e. one of
	 *            {@link TrustValueTypeBean#DIRECT}, 
	 *            {@link TrustValueTypeBean#INDIRECT}, or
	 *            {@link TrustValueTypeBean#USER_PERCEIVED}.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>.
	 */
	public void retrieveTrustValue(final RequestorBean requestor, 
			final TrustedEntityIdBean trustorId, 
			final TrustedEntityIdBean trusteeId, 
			final TrustValueTypeBean trustValueType,
			final ITrustClientCallback callback);
	
	/**
	 * Retrieves the trust relationships of the specified trustor matching the
	 * supplied criteria. More specifically, the {@link TrustedEntityTypeBean 
	 * type} of the entities trusted by the trustor is also specified. The 
	 * method returns an <i>empty</i> array if no trust relationships match the
	 * supplied criteria.
	 *
	 * @param requestor
	 *            (required) the requestor on whose behalf to retrieve the 
	 *            trust relationships.
	 * @param trustorId
	 *            (required) the identifier of the entity which has established
	 *            the trust relationships to retrieve.
	 * @param trusteeType
	 *            (required) the {@link TrustedEntityTypeBean type} of the
	 *            trusted entities to match, e.g. 
	 *            {@link TrustedEntityTypeBean#CSS CSS}.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 */
	public void retrieveTrustRelationships(final RequestorBean requestor, 
			final TrustedEntityIdBean trustorId, 
			final TrustedEntityTypeBean trusteeType,
			final ITrustClientCallback callback);
	
	/**
	 * Retrieves the trust relationships of the specified trustor matching the
	 * supplied criteria. More specifically, the trust value type, i.e. one of
	 * {@link TrustValueTypeBean#DIRECT DIRECT}, 
	 * {@link TrustValueTypeBean#INDIRECT INDIRECT}, or
	 * {@link TrustValueTypeBean#USER_PERCEIVED USER_PERCEIVED}, is also specified.
	 * The method returns an <i>empty</i> array if no trust relationships match
	 * the supplied criteria.
	 *
	 * @param requestor
	 *            (required) the requestor on whose behalf to retrieve the 
	 *            trust relationships.
	 * @param trustorId
	 *            (required) the identifier of the entity which has established
	 *            the trust relationships to retrieve.
	 * @param trustValueType
	 *            (required) the type of the trust value, i.e. one of 
	 *            {@link TrustValueTypeBean#DIRECT DIRECT},
	 *            {@link TrustValueTypeBean#INDIRECT INDIRECT}, or
	 *            {@link TrustValueTypeBean#USER_PERCEIVED USER_PERCEIVED}.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 */
	public void retrieveTrustRelationships(final RequestorBean requestor, 
			final TrustedEntityIdBean trustorId, 
			final TrustValueTypeBean trustValueType,
			final ITrustClientCallback callback);
	
	/**
	 * Retrieves the trust relationships of the specified trustor matching the
	 * supplied criteria. More specifically, the {@link TrustedEntityTypeBean 
	 * type} of the entities trusted by the trustor and the trust value type,
	 * i.e. one of {@link TrustValueTypeBean#DIRECT DIRECT}, 
	 * {@link TrustValueTypeBean#INDIRECT INDIRECT}, or
	 * {@link TrustValueTypeBean#USER_PERCEIVED USER_PERCEIVED}, are also 
	 * specified. The method returns an <i>empty</i> array if no trust 
	 * relationships match the supplied criteria.
	 *
	 * @param requestor
	 *            (required) the requestor on whose behalf to retrieve the 
	 *            trust relationships.
	 * @param trustorId
	 *            (required) the identifier of the entity which has established
	 *            the trust relationships to retrieve.
	 * @param trusteeType
	 *            (required) the {@link TrustedEntityTypeBean type} of the 
	 *            trusted entities to match, e.g. 
	 *            {@link TrustedEntityTypeBean#CSS CSS}.
	 * @param trustValueType
	 *            (required) the type of the trust value, i.e. one of 
	 *            {@link TrustValueTypeBean#DIRECT DIRECT},
	 *            {@link TrustValueTypeBean#INDIRECT INDIRECT}, or
	 *            {@link TrustValueTypeBean#USER_PERCEIVED USER_PERCEIVED}.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 */
	public void retrieveTrustRelationships(final RequestorBean requestor, 
			final TrustedEntityIdBean trustorId,
			final TrustedEntityTypeBean trusteeType,
			final TrustValueTypeBean trustValueType,
			final ITrustClientCallback callback);
	
	/**
	 * Adds the specified piece of direct trust evidence. The
	 * {@link ATrustedEntityId ATrustedEntityIds} of the subject and the object
	 * this piece of evidence refers to, its type, as well as, the time the
	 * evidence was recorded are also supplied. Finally, depending on the
	 * evidence type, the method allows specifying supplementary information.
	 * 
	 * @param requestor
	 *            (required) the requestor on whose behalf to add the direct 
	 *            trust evidence.
	 * @param subjectId
	 *            (required) the {@link ATrustedEntityId} of the subject the
	 *            piece of evidence refers to.
	 * @param objectId
	 *            (required) the {@link ATrustedEntityId} of the object the
	 *            piece of evidence refers to.
	 * @param type
	 *            (required) the type of the evidence to be added.
	 * @param timestamp
	 *            (required) the time the evidence was recorded.
	 * @param info
	 *            (optional) supplementary information if applicable; 
	 *            <code>null</code> otherwise.
	 * @param callback
	 *            (required) the callback to receive the result.
	 * @throws NullPointerException
	 *            if any of the required parameters is <code>null</code>.
	 */
	public void addDirectTrustEvidence(final RequestorBean requestor, 
			final TrustedEntityIdBean subjectId,
			final TrustedEntityIdBean objectId,	
			final TrustEvidenceTypeBean type, final ADate timestamp, 
			final Serializable info,
			final ITrustClientCallback callback);
}