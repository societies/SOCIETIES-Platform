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
package org.societies.privacytrust.privacyprotection.api;

import java.util.List;
import java.util.Map;

import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.NegotiationAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;

/**
 * @author olivierm
 * @version 1.0
 * @created 17-nov.-2011 11:12:31
 */
public interface IPolicyAgreementManager {
	/**
	 * Delete and agreement
	 * @param id
	 */
	public boolean deleteAgreement(String id);

	/**
	 * Delete agreements following criteria
	 * @param criteria
	 */
	public boolean deleteAgreements(Map criteria);

	/**
	 * Retrieve an agreement
	 * @param id
	 */
	public NegotiationAgreement getAgreement(String id);

	/**
	 * Retrieve agreements following criteria
	 * @param criteria
	 */
	public List<NegotiationAgreement> getAgreements(Map criteria);

	/**
	 * The objective here is to retrieve the part of the SERVICE negotiation agreement
	 * which is relevant about this data for this consumer, in order to know usage
	 * conditions of this data. E.g. obfuscation or not, disclosure conditions...
	 * 
	 * @param dataId
	 * @param ownerId
	 * @param requestorId
	 * @param serviceId
	 */
	public ResponseItem getPermissionConditionsInAgreement(CtxIdentifier dataId, IIdentity ownerId, IIdentity requestorId, IServiceResourceIdentifier serviceId);

	/**
	 * The objective here is to retrieve the part of the CIS negotiation agreement
	 * which is relevant about this data for this consumer, in order to know usage
	 * conditions of this data. E.g. obfuscation or not, disclosure conditions...
	 * 
	 * @param dataId
	 * @param ownerId
	 * @param requestorId
	 * @param cisId
	 */
	public ResponseItem getPermissionConditionsInAgreement(CtxIdentifier dataId, IIdentity ownerId, IIdentity requestorId, IIdentity cisId);

	/**
	 * Update Negotiation Agreement (with a Service)
	 * 
	 * @param agreement
	 * @param myId
	 * @param serviceId
	 */
	public NegotiationAgreement updateAgreement(NegotiationAgreement agreement, IIdentity myId, IServiceResourceIdentifier serviceId);

	/**
	 * Update Negotiation Agreement (with a CIS)
	 * 
	 * @param agreement
	 * @param myId
	 * @param cisId
	 */
	public NegotiationAgreement updateAgreement(NegotiationAgreement agreement, IIdentity myId, IIdentity cisId);

}
