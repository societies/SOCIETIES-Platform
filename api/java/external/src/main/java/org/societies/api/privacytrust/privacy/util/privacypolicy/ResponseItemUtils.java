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
package org.societies.api.privacytrust.privacy.util.privacypolicy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class ResponseItemUtils {
	public static ResponseItem toResponseItem(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem responseItemBean)
	{
		if (null == responseItemBean) {
			return null;
		}
		RequestItem requestItem = RequestItemUtils.toRequestItem(responseItemBean.getRequestItem());
		Decision decision = DecisionUtils.toDecision(responseItemBean.getDecision());
		return new ResponseItem(requestItem, decision);
	}
	public static List<ResponseItem> toResponseItems(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> responseItemBeans)
	{
		if (null == responseItemBeans) {
			return null;
		}
		List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem responseItemBean : responseItemBeans) {
			responseItems.add(ResponseItemUtils.toResponseItem(responseItemBean));
		}
		return responseItems;
	}

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem toResponseItemBean(ResponseItem responseItem)
	{
		if (null == responseItem) {
			return null;
		}
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem responseItemBean = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem();
		responseItemBean.setDecision(DecisionUtils.toDecisionBean(responseItem.getDecision()));
		responseItemBean.setRequestItem(RequestItemUtils.toRequestItemBean(responseItem.getRequestItem()));
		return responseItemBean;
	}
	public static List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> toResponseItemBeans(List<ResponseItem> responseItems)
	{
		if (null == responseItems) {
			return null;
		}
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> responseItemBeans = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem>();
		for(ResponseItem responseItem : responseItems) {
			responseItemBeans.add(ResponseItemUtils.toResponseItemBean(responseItem));
		}
		return responseItemBeans;
	}

	public static String toXmlString(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem responseItem){
		StringBuilder sb = new StringBuilder();
		if (null != responseItem) {
			sb.append("\n<Response>\n");
			sb.append(DecisionUtils.toXmlString(responseItem.getDecision()));
			sb.append(RequestItemUtils.toXmlString(responseItem.getRequestItem()));
			sb.append("</Response>");
		}
		return sb.toString();
	}

	public static boolean equals(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem o1, Object o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == o2) { return true; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem rhs = (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem) o2;
		return new EqualsBuilder()
		.append(o1.getDecision(), rhs.getDecision())
		.append(o1.getRequestItem(), rhs.getRequestItem())
		.isEquals();
	}
}
