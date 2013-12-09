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
package org.societies.android.api.internal.privacytrust.trust;

import java.io.Serializable;

import org.societies.android.api.common.ADate;
import org.societies.android.api.privacytrust.trust.ITrustClient;
import org.societies.api.schema.privacytrust.trust.broker.TrustQueryBean;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
public interface IInternalTrustClient extends ITrustClient {
	
	/** The extra Intent field containing the result. */
	public static final String INTENT_RETURN_VALUE_KEY = 
			ITrustClient.INTENT_RETURN_VALUE_KEY;
	
	/** The extra Intent String field containing the exception message. */
	public static final String INTENT_EXCEPTION_KEY = 
			ITrustClient.INTENT_EXCEPTION_KEY;
    
    public static final String RETRIEVE_TRUST_RELATIONSHIPS =
    		ITrustClient.RETRIEVE_TRUST_RELATIONSHIPS;
    public static final String RETRIEVE_TRUST_RELATIONSHIP =
    		ITrustClient.RETRIEVE_TRUST_RELATIONSHIP;
    public static final String RETRIEVE_TRUST_VALUE = 
    		ITrustClient.RETRIEVE_TRUST_VALUE;
    public static final String RETRIEVE_EXT_TRUST_RELATIONSHIPS =
    		"org.societies.android.api.internal.privacytrust.trust.RETRIEVE_EXT_TRUST_RELATIONSHIPS";
    public static final String RETRIEVE_EXT_TRUST_RELATIONSHIP =
    		"org.societies.android.api.internal.privacytrust.trust.RETRIEVE_EXT_TRUST_RELATIONSHIP";
    public static final String ADD_DIRECT_TRUST_EVIDENCE = 
    		ITrustClient.ADD_DIRECT_TRUST_EVIDENCE;

    /* N.B. Methods inherited from the {@link ITrustClient} interface must be
     * declared first and in the same order! */
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
    		"addDirectTrustEvidence(String client, org.societies.api.schema.identity.RequestorBean requestor, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean subjectId, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean objectId, org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean type, org.societies.android.api.common.ADate timestamp, Serializable info)",
    		"retrieveTrustRelationships(String client, org.societies.api.schema.privacytrust.trust.broker.TrustQueryBean query)",
    		"retrieveTrustRelationship(String client, org.societies.api.schema.privacytrust.trust.broker.TrustQueryBean query)",
    		"retrieveTrustValue(String client, org.societies.api.schema.privacytrust.trust.broker.TrustQueryBean query)",
    		"retrieveExtTrustRelationships(String client, org.societies.api.schema.privacytrust.trust.broker.TrustQueryBean query)",
    		"retrieveExtTrustRelationship(String client, org.societies.api.schema.privacytrust.trust.broker.TrustQueryBean query)",
            "addDirectTrustEvidence(String client, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean subjectId, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean objectId, org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean type, org.societies.android.api.common.ADate timestamp, Serializable info)"
            };

    /**
	 * Retrieves all trust relationships matching the specified query. The 
	 * method returns an <i>empty</i> array if no matching trust relationship 
	 * is found.  
	 *
	 * @param client
	 *            (required) the package name of the client application.
	 * @param query
	 *            (required) the query encapsulating the request for the trust
	 *            relationships.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 2.0
	 */
    public void retrieveTrustRelationships(final String client,
			final TrustQueryBean query);
    
    /**
	 * Retrieves the trust relationship matching the specified query. The 
	 * method returns <code>null</code> if no matching trust relationship is
	 * found. 
	 *
	 * @param client
	 *            (required) the package name of the client application.
	 * @param query
	 *            (required) the query encapsulating the request for the trust
	 *            relationship.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 2.0
	 */
    public void retrieveTrustRelationship(final String client,
			final TrustQueryBean query);
    
    /**
	 * Retrieves the trust value matching the supplied trust query. The method
	 * returns <code>null</code> if no matching trust value is found. 
	 * 
	 * @param client
	 *            (required) the package name of the client application.
	 * @param query
	 *            (required) the query encapsulating the request for the trust
	 *            value.
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>.
	 * @since 2.0
	 */
	public void retrieveTrustValue(final String client, 
			final TrustQueryBean query);
	
	/**
	 * Retrieves all trust relationships matching the specified query. Compared
	 * to {@link #retrieveTrustRelationships}, the relationships returned by
	 * this method also include the related trust evidence. However, the
	 * trustor specified in the trust query <i>must</i> identity the local CSS,
	 * otherwise an exception will be thrown.The method returns an <i>empty</i>
	 * array if no matching trust relationship is found.  
	 *
	 * @param client
	 *            (required) the package name of the client application.
	 * @param query
	 *            (required) the query encapsulating the request for the 
	 *            extended trust relationships.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 2.0
	 */
    public void retrieveExtTrustRelationships(final String client,
			final TrustQueryBean query);
    
    /**
	 * Retrieves the trust relationship matching the specified query. Compared
	 * to {@link #retrieveTrustRelationship}, the relationship returned by this
	 * method also includes the related trust evidence. However, the trustor 
	 * specified in the trust query <i>must</i> identity the local CSS, 
	 * otherwise an exception will be thrown. The method returns 
	 * <code>null</code> if no matching trust relationship is found. 
	 *
	 * @param client
	 *            (required) the package name of the client application.
	 * @param query
	 *            (required) the query encapsulating the request for the trust
	 *            relationship.
	 * @throws NullPointerException if any of the specified parameters is 
	 *         <code>null</code>.
	 * @since 2.0
	 */
    public void retrieveExtTrustRelationship(final String client,
			final TrustQueryBean query);
	
	/**
	 * Adds the specified piece of direct trust evidence. The
	 * {@link TrustedEntityIdBean TrustedEntityIdBeanIds} of the subject and
	 * the object this piece of evidence refers to, its type, as well as, the
	 * time the evidence was recorded are also supplied. Finally, depending on
	 * the evidence type, the method allows specifying supplementary 
	 * information.
	 * 
	 * @param client
	 *            (required) the package name of the client application.
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
			final TrustedEntityIdBean subjectId,
			final TrustedEntityIdBean objectId,	
			final TrustEvidenceTypeBean type, final ADate timestamp, 
			final Serializable info);
}