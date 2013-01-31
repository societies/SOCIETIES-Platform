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
package org.societies.android.api.internal.privacytrust.util.model.privacypolicy;

import org.societies.android.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;

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
			String serviceOwnerJid = ServiceModelUtils.getJidFromServiceIdentifier(((RequestorServiceBean)requestor).getRequestorServiceId());
			String serviceId = ServiceModelUtils.getServiceId64Encode((((RequestorServiceBean)requestor).getRequestorServiceId()));
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
			requestor.setRequestorServiceId(ServiceModelUtils.generateServiceResourceIdentifierFromString(requestorInfo[1]));
			return requestor;
		}
		// CSS
		else {
			RequestorBean requestor = new RequestorBean();
			requestor.setRequestorId(requestorInfo[1]);
			return requestor;
		}
	}
}
