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
package org.societies.android.api.privacytrust.privacy.util.privacypolicy;

import java.util.List;

import org.societies.android.api.identity.util.RequestorUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;


/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class ResponsePolicyUtils {

	public static ResponsePolicy create(NegotiationStatus negotiationStatus, RequestorBean requestor, List<ResponseItem> responseItems) {
		ResponsePolicy responsePolicy = new ResponsePolicy();
		responsePolicy.setNegotiationStatus(negotiationStatus);
		responsePolicy.setRequestor(requestor);
		responsePolicy.setResponseItems(responseItems);
		return responsePolicy;
	}

	public static String toString(ResponsePolicy item){
		StringBuilder builder = new StringBuilder();
		builder.append("ResponsePolicy [");
		if (null != item) {
			builder.append("getNegotiationStatus()=");
			builder.append(item.getNegotiationStatus());
			builder.append(", getRequestItem()=");
			builder.append(RequestorUtils.toString(item.getRequestor()));
			builder.append(", getRequestItem()=");
			builder.append(ResponseItemUtils.toString(item.getResponseItems()));
		}
		builder.append("]");
		return builder.toString();
	}

	public static String toString(List<ResponsePolicy> responsePolicies){
		StringBuilder sb = new StringBuilder();
		if (null != responsePolicies) {
			for(ResponsePolicy responsePolicy : responsePolicies) {
				sb.append(toString(responsePolicy));
			}
		}
		return sb.toString();
	}


	public static boolean equal(ResponsePolicy o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		ResponsePolicy ro2 = (ResponsePolicy) o2;
		return (NegotiationStatusUtils.equal(o1.getNegotiationStatus(), ro2.getNegotiationStatus())
				&& RequestorUtils.equal(o1.getRequestor(), ro2.getRequestor())
				&& ResponseItemUtils.equal(o1.getResponseItems(), ro2.getResponseItems())
				);
	}

	public static boolean equal(List<ResponsePolicy> o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (!(o2 instanceof List)) { return false; }
		// -- Verify obj type
		List<ResponsePolicy> ro2 = (List<ResponsePolicy>) o2;
		if (o1.size() != ro2.size()) {
			return false;
		}
		boolean result = true;
		for(ResponsePolicy o1Entry : o1) {
			result &= contain(o1Entry, ro2);
		}
		return result;
	}

	public static boolean contain(ResponsePolicy needle, List<ResponsePolicy> haystack) {
		if (null == haystack || haystack.size() <= 0 || null == needle) {
			return false;
		}
		for(ResponsePolicy entry : haystack) {
			if (equal(needle, entry)) {
				return true;
			}
		}
		return false;
	}


	public static boolean hasOptionalResponseItemsOnly(ResponsePolicy responsePolicy) {
		if (null == responsePolicy || null == responsePolicy.getResponseItems() || responsePolicy.getResponseItems().size() <= 0) {
			return true;
		}
		for(ResponseItem responseItem : responsePolicy.getResponseItems()) {
			// At least one requested item is mandatory
			if (null != responseItem.getRequestItem() && !responseItem.getRequestItem().isOptional()) {
				return false;
			}
		}
		return true;
	}
}
