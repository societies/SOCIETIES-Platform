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
package org.societies.api.identity.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.ServiceUtils;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class RequestorUtils {

	public static RequestorBean create(String requestorId) {
		RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(requestorId);
		return requestor;
	}

	/**
	 * Create a RequestorCisBean or RequestorServiceBean from two string containing the identifiers
	 * @param requestorId Owner id
	 * @param requestorCisOrServiceId CIS ID or 3P service identifier (stringified). A CIS id begins with "cis".
	 * @return the relevant RequestorCisBean or RequestorServiceBean
	 */
	public static RequestorBean create(String requestorId, String requestorCisOrServiceId) {
		if (requestorCisOrServiceId.startsWith("cis-")) {
			RequestorCisBean requestor = new RequestorCisBean();
			requestor.setRequestorId(requestorId);
			requestor.setCisRequestorId(requestorCisOrServiceId);
			return requestor;
		}
		else {
			RequestorServiceBean requestor = new RequestorServiceBean();
			requestor.setRequestorId(requestorId);
			try {
				requestor.setRequestorServiceId(ServiceUtils.generateServiceResourceIdentifierFromString(requestorCisOrServiceId));
			} catch(Exception e) {
				return null;
			}
			return requestor;
		}
	}

	public static RequestorServiceBean create(String requestorId, ServiceResourceIdentifier requestorServiceId) {
		RequestorServiceBean requestor = new RequestorServiceBean();
		requestor.setRequestorId(requestorId);
		requestor.setRequestorServiceId(requestorServiceId);
		return requestor;
	}


	public static String toXmlString(RequestorBean requestor){
		StringBuilder sb = new StringBuilder();
		if (null != requestor) {
			sb.append("<Subject>");
			sb.append("\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\" DataType=\""+IIdentity.class.getName()+"\">\n");
			sb.append("\t\t<AttributeValue>"+requestor.getRequestorId()+"</AttributeValue>\n");
			sb.append("\t</Attribute>\n");
			if (requestor instanceof RequestorCisBean) {
				sb.append("\t<Attribute AttributeId=\"CisId\" DataType=\""+IIdentity.class.getName()+"\">\n");
				sb.append("\t\t<AttributeValue>"+((RequestorCisBean)requestor).getCisRequestorId()+"</AttributeValue>\n");
				sb.append("\t</Attribute>\n");
			}
			if (requestor instanceof RequestorServiceBean) {
				sb.append("\t<Attribute AttributeId=\"serviceId\" DataType=\""+ServiceResourceIdentifier.class.getName()+"\">\n");
				sb.append("\t\t<AttributeValue>"+((RequestorServiceBean)requestor).getRequestorServiceId()+"</AttributeValue>\n");
				sb.append("\t</Attribute>\n");
			}
			sb.append("</Subject>");
		}
		return sb.toString();
	}

	public static String toString(RequestorBean bean){
		StringBuilder builder = new StringBuilder();
		if (null != bean) {
			if (bean instanceof RequestorCisBean){
				builder.append("RequestorCisBean [");
				builder.append("getRequestorId()=");
				builder.append(bean.getRequestorId());
				builder.append(", getCisRequestorId()=");
				builder.append(((RequestorCisBean) bean).getCisRequestorId());
			}
			else if (bean instanceof RequestorServiceBean){
				builder.append("RequestorServiceBean [");
				builder.append("getRequestorId()=");
				builder.append(bean.getRequestorId());
				builder.append(", getRequestorServiceId()=");
				builder.append(((RequestorServiceBean) bean).getRequestorServiceId());
			}
			else{
				builder.append("RequestorBean [");
				builder.append("getRequestorId()=");
				builder.append(bean.getRequestorId());
			}
		}
		builder.append("]");
		return builder.toString();
	}

	public static String toUriString(RequestorBean requestor){
		StringBuilder sb = new StringBuilder();
		sb.append("requestor://");
		if (null != requestor) {
			sb.append(requestor.getRequestorId());
			if (requestor instanceof RequestorCisBean) {
				sb.append("/"+((RequestorCisBean)requestor).getCisRequestorId());
			}
			if (requestor instanceof RequestorServiceBean) {
				if (null != ((RequestorServiceBean)requestor).getRequestorServiceId()) {
					sb.append("/"+((RequestorServiceBean)requestor).getRequestorServiceId().getServiceInstanceIdentifier());
					if (null != ((RequestorServiceBean)requestor).getRequestorServiceId().getIdentifier()) {
						sb.append("/"+((RequestorServiceBean)requestor).getRequestorServiceId().getIdentifier().toString());
					}
				}
			}
		}
		// Return the md5 of this URI
		return DigestUtils.md5Hex(sb.toString());
	}


	public static boolean equal(RequestorBean o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (!(o2 instanceof RequestorBean)) { return false; }
		// -- Verify obj type
		RequestorBean ro2 = (RequestorBean) o2;
		if (!StringUtils.equals(o1.getRequestorId(), ro2.getRequestorId())) {
			return false;
		}
		if (o1 instanceof RequestorCisBean) {
			if (!(o2 instanceof RequestorCisBean)) {
				return false;
			}
			if (!StringUtils.equals(((RequestorCisBean)o1).getCisRequestorId(), ((RequestorCisBean)ro2).getCisRequestorId())) {
				return false;
			}
		}
		else if (o1 instanceof RequestorServiceBean) {
			if (!(o2 instanceof RequestorServiceBean)) {
				return false;
			}
			if (!ServiceUtils.compare(((RequestorServiceBean)o1).getRequestorServiceId(), ((RequestorServiceBean)ro2).getRequestorServiceId())) {
				return false;
			}
		}
		else {
			if (o2 instanceof RequestorCisBean
					|| o2 instanceof RequestorServiceBean) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Use equal instead
	 */
	@Deprecated
	public static boolean equals(RequestorBean o1, Object o2) {
		return equal(o1, o2);
	}

	public static boolean equal(List<RequestorBean> o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		List<RequestorBean> ro2 = (List<RequestorBean>) o2;
		if (o1.size() != ro2.size()) {
			return false;
		}
		boolean result = true;
		for(RequestorBean o1Entry : o1) {
			result &= contain(o1Entry, ro2);
		}
		return result;
	}

	public static boolean contain(RequestorBean needle, List<RequestorBean> haystack) {
		if (null == haystack || haystack.size() <= 0 || null == needle) {
			return false;
		}
		for(RequestorBean entry : haystack) {
			if (equal(needle, entry)) {
				return true;
			}
		}
		return false;
	}

	public static Requestor toRequestor(RequestorBean requestorBean, IIdentityManager identityManager) throws InvalidFormatException
	{
		if (null == requestorBean) {
			return null;
		}
		IIdentity requestorId = identityManager.fromJid(requestorBean.getRequestorId());
		if (requestorBean instanceof RequestorCisBean) {
			return new RequestorCis(requestorId, identityManager.fromJid(((RequestorCisBean) requestorBean).getCisRequestorId()));

		}
		else if (requestorBean instanceof RequestorServiceBean) {
			RequestorService requestor = new RequestorService(requestorId, ((RequestorServiceBean) requestorBean).getRequestorServiceId());
			return requestor;
		}
		return new Requestor(requestorId);
	}
	public static List<Requestor> toRequestors(List<RequestorBean> requestorBeans, IIdentityManager identityManager) throws InvalidFormatException
	{
		if (null == requestorBeans) {
			return null;
		}
		List<Requestor> requestors = new ArrayList<Requestor>();
		for(RequestorBean requestorBean : requestorBeans) {
			requestors.add(RequestorUtils.toRequestor(requestorBean, identityManager));
		}
		return requestors;
	}

	public static RequestorBean toRequestorBean(Requestor requestor)
	{
		if (null == requestor) {
			return null;
		}
		String requestorId = requestor.getRequestorId().getJid();
		if (requestor instanceof RequestorCis) {
			RequestorCisBean requestorBean = new RequestorCisBean();
			requestorBean.setRequestorId(requestorId);
			requestorBean.setCisRequestorId(((RequestorCis)requestor).getCisRequestorId().getJid());
			return requestorBean;
		}
		else if (requestor instanceof RequestorService) {
			RequestorServiceBean requestorBean = new RequestorServiceBean();
			requestorBean.setRequestorId(requestorId);
			requestorBean.setRequestorServiceId(((RequestorService)requestor).getRequestorServiceId());
			return requestorBean;
		}
		RequestorBean requestorBean = new RequestorBean();
		requestorBean.setRequestorId(requestorId);
		return requestorBean;
	}
	public static List<RequestorBean> toRequestorBeans(List<Requestor> requestors)
	{
		if (null == requestors) {
			return null;
		}
		List<RequestorBean> requestorBeans = new ArrayList<RequestorBean>();
		for(Requestor requestor : requestors) {
			requestorBeans.add(RequestorUtils.toRequestorBean(requestor));
		}
		return requestorBeans;
	}
}
