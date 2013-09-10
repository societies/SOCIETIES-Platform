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
package org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy;

import java.util.List;

import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.schema.identity.RequestorBean;


/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class NegotiationDetailsUtils {

	public static NegotiationDetailsBean create(int negotiationId, RequestorBean requestor) {
		NegotiationDetailsBean negotiationDetails = new NegotiationDetailsBean();
		negotiationDetails.setNegotiationID(negotiationId);
		negotiationDetails.setRequestor(requestor);
		return negotiationDetails;
	}

	public static String toString(NegotiationDetailsBean negotiationDetails){
		StringBuilder builder = new StringBuilder();
		builder.append("NegotiationDetails [");
		if (null != negotiationDetails) {
			builder.append("getNegotiationID()=");
			builder.append(negotiationDetails.getNegotiationID());
			builder.append(", getRequestor()=");
			builder.append(RequestorUtils.toString(negotiationDetails.getRequestor()));
		}
		builder.append("]");
		return builder.toString();
	}

	public static String toString(List<NegotiationDetailsBean> negotiationDetailsList){
		StringBuilder sb = new StringBuilder();
		if (null != negotiationDetailsList) {
			for(NegotiationDetailsBean entry : negotiationDetailsList) {
				sb.append(toString(entry));
			}
		}
		return sb.toString();
	}


	public static boolean equal(NegotiationDetailsBean o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		NegotiationDetailsBean ro2 = (NegotiationDetailsBean) o2;
		return (o1.getNegotiationID() == ro2.getNegotiationID()
				&& RequestorUtils.equal(o1.getRequestor(), ro2.getRequestor())
				);
	}

	public static boolean equal(List<NegotiationDetailsBean> o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (!(o2 instanceof List)) { return false; }
		// -- Verify obj type
		List<NegotiationDetailsBean> ro2 = (List<NegotiationDetailsBean>) o2;
		if (o1.size() != ro2.size()) {
			return false;
		}
		boolean result = true;
		for(NegotiationDetailsBean o1Entry : o1) {
			result &= contain(o1Entry, ro2);
		}
		return result;
	}

	public static boolean contain(NegotiationDetailsBean needle, List<NegotiationDetailsBean> haystack) {
		if (null == haystack || haystack.size() <= 0 || null == needle) {
			return false;
		}
		for(NegotiationDetailsBean entry : haystack) {
			if (equal(needle, entry)) {
				return true;
			}
		}
		return false;
	}
}
