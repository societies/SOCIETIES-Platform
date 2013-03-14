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
package org.societies.android.api.identity.util;

import org.societies.android.api.servicelifecycle.ServiceUtils;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class RequestorUtils {
	public static String toFormattedString(RequestorBean requestor) {
		StringBuilder sb = new StringBuilder();
		if (requestor instanceof RequestorCisBean) {
			sb.append("CIS:");
			sb.append(requestor.getRequestorId());
			sb.append("|");
			sb.append(((RequestorCisBean)requestor).getCisRequestorId());
		}
		else if (requestor instanceof RequestorServiceBean) {
			String serviceOwnerJid = ServiceUtils.getJidFromServiceIdentifier(((RequestorServiceBean)requestor).getRequestorServiceId());
			String serviceId = ServiceUtils.getServiceId64Encode((((RequestorServiceBean)requestor).getRequestorServiceId()));
			sb.append("Service:");
			sb.append(serviceOwnerJid);
			sb.append("|");
			sb.append(serviceId);
		}
		else {
			sb.append("CSS:");
			sb.append(requestor.getRequestorId());
		}
		return sb.toString();
	}

	public static RequestorBean fromFormattedString(String requestorString) {
		String[] requestorInfo = requestorString.split(":");
		// CIS
		if (requestorString.startsWith("CIS:")) {
			requestorInfo = requestorInfo[1].split("|");
			RequestorCisBean requestor = new RequestorCisBean();
			requestor.setRequestorId(requestorInfo[0]);
			requestor.setCisRequestorId(requestorInfo[1]);
			return requestor;
		}
		// Service
		else if (requestorString.startsWith("Service:")) {
			requestorInfo = requestorInfo[1].split("|");
			RequestorServiceBean requestor = new RequestorServiceBean();
			requestor.setRequestorId(requestorInfo[0]);
			requestor.setRequestorServiceId(ServiceUtils.generateServiceResourceIdentifierFromString(requestorInfo[1]));
			return requestor;
		}
		// CSS
		else {
			RequestorBean requestor = new RequestorBean();
			requestor.setRequestorId(requestorInfo[1]);
			return requestor;
		}
	}

	public static RequestorBean create(String requestorId) {
		RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(requestorId);
		return requestor;
	}

	public static RequestorCisBean create(String requestorId, String requestorCisId) {
		RequestorCisBean requestor = new RequestorCisBean();
		requestor.setRequestorId(requestorId);
		requestor.setCisRequestorId(requestorCisId);
		return requestor;
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

	public static boolean equals(RequestorBean o1, Object o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == o2) { return true; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		RequestorBean rhs = (RequestorBean) o2;
		boolean isEqual = true;
		isEqual = o1.getRequestorId().equals(rhs.getRequestorId());
		if (o1 instanceof RequestorCisBean) {
			isEqual &= ((RequestorCisBean)o1).getCisRequestorId().equals(((RequestorCisBean)rhs).getCisRequestorId());
		}
		if (o1 instanceof RequestorServiceBean) {
			isEqual &= ((RequestorServiceBean)o1).getRequestorServiceId().getIdentifier().equals(((RequestorServiceBean)rhs).getRequestorServiceId().getIdentifier());
			isEqual &= ((RequestorServiceBean)o1).getRequestorServiceId().getServiceInstanceIdentifier().equals(((RequestorServiceBean)rhs).getRequestorServiceId().getServiceInstanceIdentifier());
		}
		return isEqual;
	}
}
