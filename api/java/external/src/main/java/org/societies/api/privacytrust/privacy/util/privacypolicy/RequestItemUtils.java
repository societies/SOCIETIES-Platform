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
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class RequestItemUtils {
	public static RequestItem toRequestItem(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem requestItemBean)
	{
		if (null == requestItemBean) {
			return null;
		}
		return new RequestItem(ResourceUtils.toResource(requestItemBean.getResource()), ActionUtils.toActions(requestItemBean.getActions()), ConditionUtils.toConditions(requestItemBean.getConditions()));
	}
	public static List<RequestItem> toRequestItems(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem> requestItemBeans)
	{
		if (null == requestItemBeans) {
			return null;
		}
		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem requestItemBean : requestItemBeans) {
			requestItems.add(RequestItemUtils.toRequestItem(requestItemBean));
		}
		return requestItems;
	}

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem toRequestItemBean(RequestItem requestItem)
	{
		if (null == requestItem) {
			return null;
		}
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem requestItemBean = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem();
		requestItemBean.setResource(ResourceUtils.toResourceBean(requestItem.getResource()));
		requestItemBean.setOptional(requestItem.isOptional());
		requestItemBean.setActions(ActionUtils.toActionBeans(requestItem.getActions()));
		requestItemBean.setConditions(ConditionUtils.toConditionBeans(requestItem.getConditions()));
		return requestItemBean;
	}
	public static List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem> toRequestItemBeans(List<RequestItem> requestItems)
	{
		if (null == requestItems) {
			return null;
		}
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem> requestItemBeans = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem>();
		for(RequestItem requestItem : requestItems) {
			requestItemBeans.add(RequestItemUtils.toRequestItemBean(requestItem));
		}
		return requestItemBeans;
	}

	public static String toXmlString(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem requestItem){
		StringBuilder sb = new StringBuilder();
		if (null != requestItem) {
			sb.append("\n<Target>\n");
			sb.append(ResourceUtils.toXmlString(requestItem.getResource()));
			sb.append(ActionUtils.toXmlString(requestItem.getActions()));
			sb.append(ConditionUtils.toXmlString(requestItem.getConditions()));
			sb.append("\t<optional>"+requestItem.isOptional()+"</optional>\n");
			sb.append("</Target>");
		}
		return sb.toString();
	}

	public static String toXmlString(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem> requestItems){
		StringBuilder sb = new StringBuilder();
		if (null != requestItems) {
			for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem requestItem : requestItems) {
				sb.append(toXmlString(requestItem));
			}
		}
		return sb.toString();
	}

	public static boolean equals(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem o1, Object o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == o2) { return true; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem rhs = (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem) o2;
		return new EqualsBuilder()
		.append(o1.getActions(), rhs.getActions())
		.append(o1.getConditions(), rhs.getConditions())
		.append(o1.getResource(), rhs.getResource())
		.append(o1.isOptional(), rhs.isOptional())
		.isEquals();
	}
}
