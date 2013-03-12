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

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.identity.util.RequestorUtils;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyTypeConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class RequestPolicyUtils {

	public static RequestPolicy create(PrivacyPolicyTypeConstants privacyPolicyType, RequestorBean requestor, List<RequestItem> requestItems) {
		RequestPolicy requestPolicy = new RequestPolicy();
		requestPolicy.setPrivacyPolicyType(privacyPolicyType);
		requestPolicy.setRequestor(requestor);
		requestPolicy.setRequestItems(requestItems);
		return requestPolicy;
	}

	public static String toXmlString(RequestPolicy requestPolicy){
		StringBuilder sb = new StringBuilder();
		if (null != requestPolicy) {
			sb.append("<RequestPolicy>");
			sb.append(RequestorUtils.toXmlString(requestPolicy.getRequestor()));
			for (RequestItem requestItem : requestPolicy.getRequestItems()){
				sb.append(RequestItemUtils.toXmlString(requestItem));
			}
			sb.append("</RequestPolicy>");
		}
		return sb.toString();
	}

	public static boolean equals(RequestPolicy o1, Object o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == o2) { return true; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		RequestPolicy rhs = (RequestPolicy) o2;
		return (o1.getPrivacyPolicyType().name().equals(rhs.getPrivacyPolicyType().name())
				&& RequestItemUtils.equals(o1.getRequestItems(), rhs.getRequestItems())
				&& RequestorUtils.equals(o1.getRequestor(), rhs.getRequestor())
				);
	}

	public static boolean equals(List<RequestPolicy> o1, Object o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == o2) { return true; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		List<RequestPolicy> rhs = (List<RequestPolicy>) o2;
		boolean result = true;
		int i = 0;
		for(RequestPolicy o1Element : o1) {
			result &= equals(o1Element, rhs.get(i++));
		}
		return result;
	}
	

	/**
	 * Retrieve all data types requested in a privacy policy
	 * Warning: data types are stored without scheme, so these data can be from several schemes (cis, context)
	 * 
	 * @param privacyPolicy Privacy policy
	 * @return A list of data types requested, or null if the privacy policy is null or empty
	 */
	public static List<String> getDataTypes(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy privacyPolicy) {
		List<String> dataTypes = null;
		// -- Empty privacy policy
		if (null == privacyPolicy || null == privacyPolicy.getRequestItems() || privacyPolicy.getRequestItems().size() <= 0) {
			return dataTypes;
		}

		// -- Retrieve data type list
		dataTypes = new ArrayList<String>();
		for(RequestItem requestItem : privacyPolicy.getRequestItems()) {
			dataTypes.add(ResourceUtils.getDataType(requestItem.getResource()));
		}
		return dataTypes;
	}

	/**
	 * Retrieve all data types of a peculiar scheme (cis, context, ...) in a privacy policy
	 * 
	 * @param schemeFilter Scheme of the data types
	 * @param privacyPolicy Privacy policy
	 * @return A list of data types of the peculiar scheme, or null if the privacy policy is null or empty
	 */
	public static List<String> getDataTypes(DataIdentifierScheme schemeFilter, org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy privacyPolicy) {
		List<String> dataTypes = null;
		// -- Empty privacy policy
		if (null == privacyPolicy || null == privacyPolicy.getRequestItems() || privacyPolicy.getRequestItems().size() <= 0) {
			return dataTypes;
		}

		// -- Retrieve data type list
		dataTypes = new ArrayList<String>();
		for(RequestItem requestItem : privacyPolicy.getRequestItems()) {
			try {
				DataIdentifier dataId = ResourceUtils.getDataIdentifier(requestItem.getResource());
				if (schemeFilter.name().equals(dataId.getScheme().name())) {
					dataTypes.add(dataId.getType());
				}
			} catch (MalformedCtxIdentifierException e) {
				// Too bad: can't retrieve the data identifier. Privacy policy must be badly formatted."
				return dataTypes;
			}
		}
		return dataTypes;
	}
}
