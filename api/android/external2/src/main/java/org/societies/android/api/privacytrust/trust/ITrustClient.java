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
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean;

/**
 * This interface provides access to the trust values associated with 
 * individuals, communities and services. These values define trust 
 * relationships between a given <i>trustor</i> and a <i>trustee</i>. More
 * specifically, a trustor is an entity which assigns a trust value to another
 * entity, i.e. the trustee, in order to express the trustworthiness of that 
 * entity. Such a trust value ranges from <i>0</i> to <i>1</i>, where zero 
 * expresses full distrust, while one denotes full trust. There are three 
 * different types of values that can be assigned:
 * {@link TrustValueTypeBean#DIRECT DIRECT}, 
 * {@link TrustValueTypeBean#INDIRECT INDIRECT}, or 
 * {@link TrustValueTypeBean#USER_PERCEIVED USER_PERCEIVED}. A 
 * <code>TrustRelationshipBean</code> also contains the date and time
 * (timestamp) when the trust value was last evaluated.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
public interface ITrustClient extends IServiceManager {
	
	/** The extra Intent field containing the result. */
	public static final String INTENT_RETURN_VALUE_KEY = 
			"org.societies.android.api.privacytrust.trust.ReturnValue";
	
	/** The extra Intent String field containing the exception message. */
	public static final String INTENT_EXCEPTION_KEY = 
			"org.societies.android.api.privacytrust.trust.Exception";
    
    public static final String RETRIEVE_TRUST_RELATIONSHIPS = 
    		"org.societies.android.api.privacytrust.trust.RETRIEVE_TRUST_RELATIONSHIPS";
    public static final String RETRIEVE_TRUST_RELATIONSHIP = 
    		"org.societies.android.api.privacytrust.trust.RETRIEVE_TRUST_RELATIONSHIP";
    public static final String RETRIEVE_TRUST_VALUE = 
    		"org.societies.android.api.privacytrust.trust.RETRIEVE_TRUST_VALUE";
    public static final String ADD_DIRECT_TRUST_EVIDENCE = 
    		"org.societies.android.api.privacytrust.trust.ADD_DIRECT_TRUST_EVIDENCE";

    String methodsArray [] = {
    		"startService()",
    		"stopService()",
    		"retrieveTrustRelationships(String client, org.societies.api.schema.identity.RequestorBean requestor, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean trustorId)",
    		"retrieveTrustRelationships(String client, org.societies.api.schema.identity.RequestorBean requestor, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean trustorId, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean trusteeId)",
    		"retrieveTrustRelationship(String client, org.societies.api.schema.identity.RequestorBean requestor, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean trustorId, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean trusteeId, org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean trustValueType)",
    		"retrieveTrustValue(String client, org.societies.api.schema.identity.RequestorBean requestor, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean trustorId, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean trusteeId, org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean trustValueType)",
    		"retrieveTrustRelationships(String client, org.societies.api.schema.identity.RequestorBean requestor, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean trustorId, org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean trusteeType)",
    		"retrieveTrustRelationships(String client, org.societies.api.schema.identity.RequestorBean requestor, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean trustorId, org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean trustValueType)",
    		"retrieveTrustRelationships(String client, org.societies.api.schema.identity.RequestorBean requestor, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean trustorId, org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean trusteeType, org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean trustValueType)",
    		"addDirectTrustEvidence(String client, org.societies.api.schema.identity.RequestorBean requestor, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean subjectId, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean objectId, org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean type, org.societies.android.api.common.ADate timestamp, Serializable info)"
    		};

    /**
	 * Retrieves all trust relationships of the specified trustor. The method
	 * returns an <i>empty</i> array if the identified trustor has not 
	 * established any trust relationships. 
	 *
	 * @param client
	 *            (required) the package name of the client application.
	 * @param requestor 
	 *            (required) the requestor on whose behalf to retrieve the 
	 *            trust relationships.
	 * @param trustorId
	 *            (required) the identifier of the entity whose trust
	 *            relationships to retrieve.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 */
    public void retrieveTrustRelationships(final String client,
			final RequestorBean requestor, final TrustedEntityIdBean trustorId);
    
    /**
	 * Retrieves the trust relationships of the specified trustor with the
	 * supplied trustee. The method returns an <i>empty</i> array if no trust
	 * relationships exist between the identified trustor and trustee.
	 *
	 * @param client
	 *            (required) the package name of the client application.
	 * @param requestor 
	 *            (required) the requestor on whose behalf to retrieve the 
	 *            trust relationships.
	 * @param trustorId
	 *            (required) the identifier of the entity whose trust 
	 *            relationships to retrieve.
	 * @param trusteeId
	 *            (required) the identifier of the entity trusted by the 
	 *            specified trustor.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 */
    public void retrieveTrustRelationships(final String client,
			final RequestorBean requestor, final TrustedEntityIdBean trustorId,
			final TrustedEntityIdBean trusteeId);
    
    /**
	 * Retrieves the trust relationship of the specified type which the given
	 * trustor has established with the supplied trustee. The method returns 
	 * <code>null</code> if no trust relationship of the specified type has
	 * been established with the supplied trustee by the given trustor.
	 * 
	 * @param client
	 *            (required) the package name of the client application.
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
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>.
	 * @since 1.0
	 */
	public void retrieveTrustRelationship(final String client, 
			final RequestorBean requestor, 
			final TrustedEntityIdBean trustorId, 
			final TrustedEntityIdBean trusteeId, 
			final TrustValueTypeBean trustValueType);
    
    /**
	 * Retrieves the trust value of the given type which the specified trustor
	 * has assigned to the supplied trustee. The method returns <code>null</code>
	 * if no trust value has been assigned to the specified trustee by the given
	 * trustor.
	 * 
	 * @param client
	 *            (required) the package name of the client application.
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
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>.
	 * @since 1.0
	 */
	public void retrieveTrustValue(final String client, 
			final RequestorBean requestor, 
			final TrustedEntityIdBean trustorId, 
			final TrustedEntityIdBean trusteeId, 
			final TrustValueTypeBean trustValueType);
	
	/**
	 * Retrieves the trust relationships of the specified trustor matching the
	 * supplied criteria. More specifically, the {@link TrustedEntityTypeBean 
	 * type} of the entities trusted by the trustor is also specified. The 
	 * method returns an <i>empty</i> array if no trust relationships match the
	 * supplied criteria.
	 *
	 * @param client
	 *            (required) the package name of the client application.
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
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 */
	public void retrieveTrustRelationships(final String client, 
			final RequestorBean requestor, 
			final TrustedEntityIdBean trustorId, 
			final TrustedEntityTypeBean trusteeType);
	
	/**
	 * Retrieves the trust relationships of the specified trustor matching the
	 * supplied criteria. More specifically, the trust value type, i.e. one of
	 * {@link TrustValueTypeBean#DIRECT DIRECT}, 
	 * {@link TrustValueTypeBean#INDIRECT INDIRECT}, or
	 * {@link TrustValueTypeBean#USER_PERCEIVED USER_PERCEIVED}, is also specified.
	 * The method returns an <i>empty</i> array if no trust relationships match
	 * the supplied criteria.
	 *
	 * @param client
	 *            (required) the package name of the client application.
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
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 */
	public void retrieveTrustRelationships(final String client, 
			final RequestorBean requestor, 
			final TrustedEntityIdBean trustorId, 
			final TrustValueTypeBean trustValueType);
	
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
	 * @param client
	 *            (required) the package name of the client application.
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
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 1.0
	 */
	public void retrieveTrustRelationships(final String client, 
			final RequestorBean requestor, 
			final TrustedEntityIdBean trustorId,
			final TrustedEntityTypeBean trusteeType,
			final TrustValueTypeBean trustValueType);
	
	/**
	 * Adds the specified piece of direct trust evidence. The
	 * {@link ATrustedEntityId ATrustedEntityIds} of the subject and the object
	 * this piece of evidence refers to, its type, as well as, the time the
	 * evidence was recorded are also supplied. Finally, depending on the
	 * evidence type, the method allows specifying supplementary information.
	 * 
	 * @param client
	 *            (required) the package name of the client application.
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
	 * @throws NullPointerException
	 *            if any of the required parameters is <code>null</code>.
	 * @since 1.0
	 */
	public void addDirectTrustEvidence(final String client, 
			final RequestorBean requestor, final TrustedEntityIdBean subjectId,
			final TrustedEntityIdBean objectId,	
			final TrustEvidenceTypeBean type, final ADate timestamp, 
			final Serializable info);
}
