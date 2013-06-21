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
package org.societies.api.privacytrust.trust;

import java.io.Serializable;

import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;

/**
 * This class is used to encapsulate a query regarding trust relationships. It 
 * supports specifying criteria on various properties, such as, querying by
 * trustor id (required), trustee id/type, and/or trust value type, i.e.
 * {@link TrustValueType#DIRECT direct}, {@link TrustValueType#INDIRECT indirect},
 * or {@link TrustValueType#USER_PERCEIVED user-perceived}.
 * <p>
 * For example:
 * <pre>
 * // Bob the trustor
 * TrustedEntityId trustorId = new TrustedEntityId(TrustedEntityType.CSS, "bob.ict-societies.eu");
 * 
 * TrustQuery query;
 *		
 * // Matches all trust relationships of Bob (trustor)
 * query = new TrustQuery(trustorId);
 *	
 * // Matches all trust relationships between Bob (trustor) and Alice (trustee)
 * TrustedEntityId trusteeId = new TrustedEntityId(TrustedEntityType.CSS, "alice.ict-societies.eu");
 * query = new TrustQuery(trustorId).setTrusteeId(trusteeId);
 * 	
 * // Matches all trust relationships of Bob (trustor) with communities
 * query = new TrustQuery(trustorId).setTrusteeType(TrustedEntityType.CIS);
 *	
 * // Matches all user-perceived trust relationships of Bob (trustor) with services.
 * query = new TrustQuery(trustorId).setTrusteeType(TrustedEntityType.SVC)
 *                 .setTrustValueType(TrustValueType.USER_PERCEIVED);
 * </pre>
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @see ITrustBroker
 * @since 1.1
 */
public class TrustQuery implements Serializable {

	private static final long serialVersionUID = 1182823283175431278L;

	/** The identifier of the entity whose trust relationships to query. */
	private final TrustedEntityId trustorId;
	
	/** The identifier of the trusted entity to match with this trust query. */
	private TrustedEntityId trusteeId;
	
	/** The type of the trust values to match with this trust query. */
	private TrustValueType trustValueType;
	
	/** The type of the trusted entities to match with this trust query. */
	private TrustedEntityType trusteeType;
	
	/**
	 * Constructs a trust query for the identified trustor.
	 * 
	 * @param trustorId
	 *            the identifier of the entity whose trust relationships to 
	 *            query.
	 * @throws NullPointerException if the specified trustorId is
	 *         <code>null</code>.
	 */
	public TrustQuery(final TrustedEntityId trustorId) {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
			
		this.trustorId = trustorId;
	}
	
	/**
	 * Returns the identifier of the entity whose trust relationships to query.
	 * 
	 * @return the identifier of the entity whose trust relationships to query. 
	 *         query.
	 */
	public TrustedEntityId getTrustorId() {
		
		return this.trustorId;
	}
	
	/**
	 * Returns the identifier of the trusted entity to match with this trust 
	 * query.
	 * 
	 * @return the identifier of the trusted entity to match with this trust 
	 *         query.
	 */
	public TrustedEntityId getTrusteeId() {
		
		return this.trusteeId;
	}
	
	/**
	 * Sets the identifier of the trusted entity to match with this trust 
	 * query.
	 * 
	 * @param trusteeId 
	 *            the identifier of the trusted entity to match with this trust 
	 *            query.
	 * @return this trust query instance (to allow chaining).
	 */
	public TrustQuery setTrusteeId(TrustedEntityId trusteeId) {
		
		this.trusteeId = trusteeId;
		
		return this;
	}
	
	/**
	 * Returns the type of the trust values to match with this trust query.
	 * 
	 * @return the type of the trust values to match with this trust query.
	 */
	public TrustValueType getTrustValueType() {
		
		return this.trustValueType;
	}
	
	/**
	 * Sets the type of the trust values to match with this trust query.
	 * 
	 * @param trustValueType 
	 *            the type of the trust values to match with this trust 
	 *            query.
	 * @return this trust query instance (to allow chaining).
	 */
	public TrustQuery setTrustValueType(TrustValueType trustValueType) {
		
		this.trustValueType = trustValueType;
		
		return this;
	}
	
	/**
	 * Returns the type of the trusted entities to match with this trust query.
	 * 
	 * @return the type of the trusted entity to match with this trust query.
	 */
	public TrustedEntityType getTrusteeType() {
		
		return this.trusteeType;
	}
	
	/**
	 * Sets the type of the trusted entities to match with this trust query.
	 * 
	 * @param trusteeType 
	 *            the type of the trusted entities to match with this trust 
	 *            query.
	 * @return this trust query instance (to allow chaining).
	 */
	public TrustQuery setTrusteeType(TrustedEntityType trusteeType) {
		
		this.trusteeType = trusteeType;
		
		return this;
	}
	
	/**
	 * Returns a <code>String</code> representation of this trust query.
	 * 
	 * @return a <code>String</code> representation of this trust query.
	 */
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("TrustQuery (trustorId=");
		sb.append(this.trustorId);
		sb.append(", trusteeId=");
		sb.append(this.trusteeId);
		sb.append(", trustValueType=");
		sb.append(this.trustValueType);
		sb.append(", trusteeType=");
		sb.append(this.trusteeType);
		sb.append(")");
		
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
		
		
	}
}