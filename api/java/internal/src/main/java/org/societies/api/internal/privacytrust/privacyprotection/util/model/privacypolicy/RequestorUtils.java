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

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class RequestorUtils {
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
