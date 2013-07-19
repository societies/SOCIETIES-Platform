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
import java.util.Iterator;
import java.util.List;

import org.societies.api.identity.util.DataIdentifierUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;


/**
 * Utility class to manage actions over ResponseItem
 * E.g. conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class ResponseItemUtils {

	public static ResponseItem create(Decision decision, RequestItem requestItem) {
		ResponseItem responseItem = new ResponseItem();
		responseItem.setDecision(decision);
		responseItem.setRequestItem(requestItem);
		return responseItem;
	}

	/**
	 * Will generate a list of ResponseItem (same size as requestItems), on per requestItem, with decision as Decision
	 * @post If cleanActions is true, the requestItems list will be modified
	 * @param decision Decision to apply to every request items
	 * @param requestItems Items to add in the response
	 * @param cleanActions Actions will be removed from requestItem if true
	 * @return List of ResponseItem
	 */
	public static List<ResponseItem> createList(Decision decision, List<RequestItem> requestItems, boolean cleanActions) {
		List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
		for(Iterator<RequestItem> it = requestItems.iterator(); it.hasNext();) {
			RequestItem requestItem = it.next();
			if (cleanActions) {
				requestItem.setActions(new ArrayList<Action>());
			}
			responseItems.add(create(decision, requestItem));
		}
		return responseItems;
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

	/**
	 * Retrieve a list of dataId from a list of ResponseItem
	 * @param responseItems List of response items
	 * @return Data id list contained in responseItems
	 */
	public static List<DataIdentifier> getDataIdentifiers(List<ResponseItem> responseItems) {
		if (null == responseItems || responseItems.size() <= 0) {
			return null;
		}
		List<DataIdentifier> dataIds = new ArrayList<DataIdentifier>();
		for(ResponseItem entry : responseItems) {
			try {
				dataIds.add(ResourceUtils.getDataIdentifier(entry.getRequestItem().getResource()));
			} catch (Exception e) {
				// NULL or Malformed. Skip it.
			}
		}
		if (dataIds.size() <= 0) {
			return null;
		}
		return dataIds;
	}


	/**
	 * Retrieve the ResponseItems for this data id
	 * @param dataId Data id to match
	 * @param responseItems List of response items
	 * @return ResponseItems matching this data id
	 */
	public static List<ResponseItem> findResponseItem(DataIdentifier dataId, List<ResponseItem> responseItems) {
		if (null == responseItems || responseItems.size() <= 0) {
			return null;
		}
		List<ResponseItem> permissions = new ArrayList<ResponseItem>();
		for(Iterator<ResponseItem> it = responseItems.iterator(); it.hasNext();) {
			ResponseItem permission = it.next();
			if (DataIdentifierUtils.equal(dataId, permission.getRequestItem().getResource())) {
				permissions.add(permission);
			}
		}
		if (permissions.size() <= 0) {
			return null;
		}
		return permissions;
	}


	public static org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem toResponseItem(ResponseItem responseItemBean)
	{
		if (null == responseItemBean) {
			return null;
		}
		org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem requestItem = RequestItemUtils.toRequestItem(responseItemBean.getRequestItem());
		org.societies.api.privacytrust.privacy.model.privacypolicy.Decision decision = DecisionUtils.toDecision(responseItemBean.getDecision());
		return new org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem(requestItem, decision);
	}
	public static List<org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem> toResponseItems(List<ResponseItem> responseItemBeans)
	{
		if (null == responseItemBeans) {
			return null;
		}
		List<org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem> responseItems = new ArrayList<org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem>();
		for(ResponseItem responseItemBean : responseItemBeans) {
			responseItems.add(ResponseItemUtils.toResponseItem(responseItemBean));
		}
		return responseItems;
	}

	public static ResponseItem toResponseItemBean(org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem responseItem)
	{
		if (null == responseItem) {
			return null;
		}
		ResponseItem responseItemBean = new ResponseItem();
		responseItemBean.setDecision(DecisionUtils.toDecisionBean(responseItem.getDecision()));
		responseItemBean.setRequestItem(RequestItemUtils.toRequestItemBean(responseItem.getRequestItem()));
		return responseItemBean;
	}
	public static List<ResponseItem> toResponseItemBeans(List<org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem> responseItems)
	{
		if (null == responseItems) {
			return null;
		}
		List<ResponseItem> responseItemBeans = new ArrayList<ResponseItem>();
		for(org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem responseItem : responseItems) {
			responseItemBeans.add(ResponseItemUtils.toResponseItemBean(responseItem));
		}
		return responseItemBeans;
	}
}
