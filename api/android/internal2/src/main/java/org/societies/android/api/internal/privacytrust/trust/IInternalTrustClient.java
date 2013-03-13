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

import org.societies.android.api.privacytrust.trust.ITrustClient;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
public interface IInternalTrustClient extends ITrustClient {
	
	public static final String INTENT_RETURN_VALUE_KEY = 
			"org.societies.android.api.internal.privacytrust.trust.ReturnValue";
    public static final String INTENT_RETURN_STATUS_KEY = 
    		"org.societies.android.api.internal.privacytrust.trust.ReturnStatus";
    
    public static final String RETRIEVE_TRUST_VALUE = 
    		"org.societies.android.api.internal.privacytrust.trust.RETRIEVE_TRUST_VALUE";
    public static final String ADD_TRUST_EVIDENCE = 
    		"org.societies.android.api.internal.privacytrust.trust.ADD_TRUST_EVIDENCE";

    String methodsArray [] = {
    		"retrieveTrust(String client, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean trustorId, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean trusteeId)",
            "addTrustEvidence(String client, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean subjectId, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean objectId, org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean type, org.societies.android.api.privacytrust.trust.ADate timestamp, Serializable info"
            };
}
