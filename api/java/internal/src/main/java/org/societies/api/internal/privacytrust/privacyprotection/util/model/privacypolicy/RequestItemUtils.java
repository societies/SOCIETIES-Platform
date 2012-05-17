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

import java.util.ArrayList;
import java.util.List;

import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class RequestItemUtils {
	public static RequestItem toRequestItem(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestItem requestItemBean)
	{
		if (null == requestItemBean) {
			return null;
		}
		return new RequestItem(ResourceUtils.toResource(requestItemBean.getResource()), ActionUtils.toActions(requestItemBean.getActions()), ConditionUtils.toConditions(requestItemBean.getConditions()));
	}
	public static List<RequestItem> toRequestItems(List<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestItem> requestItemBeans)
	{
		if (null == requestItemBeans) {
			return null;
		}
		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		for(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestItem requestItemBean : requestItemBeans) {
			requestItems.add(RequestItemUtils.toRequestItem(requestItemBean));
		}
		return requestItems;
	}
	
	public static org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestItem toRequestItemBean(RequestItem requestItem)
	{
		if (null == requestItem) {
			return null;
		}
		org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestItem requestItemBean = new org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestItem();
		requestItemBean.setResource(ResourceUtils.toResourceBean(requestItem.getResource()));
		requestItemBean.setOptional(requestItem.isOptional());
		// TODO: manage list
		return requestItemBean;
	}
	public static List<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestItem> toRequestItemBeans(List<RequestItem> requestItems)
	{
		if (null == requestItems) {
			return null;
		}
		List<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestItem> requestItemBeans = new ArrayList<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestItem>();
		for(RequestItem requestItem : requestItems) {
			requestItemBeans.add(RequestItemUtils.toRequestItemBean(requestItem));
		}
		return requestItemBeans;
	}
}
