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

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class ResponseItemUtils {

	public static ResponseItem create(Decision decision, RequestItem requestItem) {
		ResponseItem responseItem = new ResponseItem();
		responseItem.setDecision(decision);
		responseItem.setRequestItem(requestItem);
		return responseItem;
	}

	
	public static String toXmlString(ResponseItem responseItem){
		StringBuilder sb = new StringBuilder();
		if (null != responseItem) {
			sb.append("\n<Response>\n");
			sb.append(DecisionUtils.toXmlString(responseItem.getDecision()));
			sb.append(RequestItemUtils.toXmlString(responseItem.getRequestItem()));
			sb.append("</Response>");
		}
		return sb.toString();
	}

	public static String toXmlString(List<ResponseItem> responseItems){
		StringBuilder sb = new StringBuilder();
		if (null != responseItems) {
			for(ResponseItem responseItem : responseItems) {
				sb.append(toXmlString(responseItem));
			}
		}
		return sb.toString();
	}

	public static String toString(ResponseItem value){
		StringBuilder builder = new StringBuilder();
		builder.append("ResponseItem [");
		if (null != value) {
			builder.append("getDecision()=");
			builder.append(value.getDecision());
			builder.append(", getRequestItem()=");
			builder.append(RequestItemUtils.toString(value.getRequestItem()));
		}
		builder.append("]");
		return builder.toString();
	}

	public static String toString(List<ResponseItem> values){
		StringBuilder sb = new StringBuilder();
		if (null != values) {
			for(ResponseItem entry : values) {
				sb.append(toString(entry));
			}
		}
		return sb.toString();
	}

	
	public static boolean equal(ResponseItem o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		ResponseItem ro2 = (ResponseItem) o2;
		return (DecisionUtils.equal(o1.getDecision(), ro2.getDecision())
				&& RequestItemUtils.equal(o1.getRequestItem(), ro2.getRequestItem())
				);
	}
	@Deprecated
	public static boolean equals(ResponseItem o1, Object o2) {
		return equal(o1, o2);
	}

	public static boolean equal(List<ResponseItem> o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (!(o2 instanceof List)) { return false; }
		// -- Verify obj type
		List<ResponseItem> ro2 = (List<ResponseItem>) o2;
		if (o1.size() != ro2.size()) {
			return false;
		}
		boolean result = true;
		for(ResponseItem o1Entry : o1) {
			result &= contain(o1Entry, ro2);
		}
		return result;
	}
	@Deprecated
	public static boolean equals(List<ResponseItem> o1, Object o2) {
		return equal(o1, o2);
	}

	public static boolean contain(ResponseItem needle, List<ResponseItem> haystack) {
		if (null == haystack || haystack.size() <= 0 || null == needle) {
			return false;
		}
		for(ResponseItem entry : haystack) {
			if (equal(needle, entry)) {
				return true;
			}
		}
		return false;
	}

	
	public static ResponseItem containSameResource(ResponseItem needle, List<ResponseItem> haystack) {
		if (null == haystack || haystack.size() <= 0 || null == needle) {
			return null;
		}
		for(ResponseItem entry : haystack) {
			if (ResourceUtils.equal(needle.getRequestItem().getResource(), entry.getRequestItem().getResource())) {
				return entry;
			}
		}
		return null;
	}
}
