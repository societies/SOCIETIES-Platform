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

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
package org.societies.privacytrust.remote.privacypolicymanagement;

import java.util.HashMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.DataWrapperFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyPolicyTypeConstants;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.ActionUtils;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.RequestPolicyUtils;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.ResponseItemUtils;
import org.societies.api.internal.privacytrust.privacyprotection.util.remote.Util;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.PrivacyPolicyManagerBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.PrivacyPolicyManagerBeanResult;


public class PrivacyPolicyManagerCommServer {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyManagerCommServer.class);

	private ICommManager commManager;
	private IPrivacyPolicyManager privacyPolicyManager;


	public PrivacyPolicyManagerCommServer() {
	}

	
	public Object getQuery(Stanza stanza, PrivacyPolicyManagerBean bean){
		PrivacyPolicyManagerBeanResult beanResult = new PrivacyPolicyManagerBeanResult();
		boolean ack = true;

		// -- getPrivacyPolicy
		if (bean.getMethod().equals(MethodType.GET_PRIVACY_POLICY)) {
			LOG.info("getQuery(): GetPrivacyPolicy remote called");
			beanResult.setMethod(MethodType.GET_PRIVACY_POLICY);
			ack = getPrivacyPolicy(bean, beanResult);
			LOG.info("getQuery(): GetPrivacyPolicy remote response sending");
		}

		// -- updatePrivacyPolicy
		else if (bean.getMethod().equals(MethodType.UPDATE_PRIVACY_POLICY)) {
			LOG.info("getQuery(): updatePrivacyPolicy remote called");
			beanResult.setMethod(MethodType.UPDATE_PRIVACY_POLICY);
			ack = updatePrivacyPolicy(bean, beanResult);
			LOG.info("getQuery(): updatePrivacyPolicy remote response sending");
		}
		
		// -- deletePrivacyPolicy
		else if (bean.getMethod().equals(MethodType.DELETE_PRIVACY_POLICY)) {
			LOG.info("getQuery(): deletePrivacyPolicy remote called");
			beanResult.setMethod(MethodType.DELETE_PRIVACY_POLICY);
			ack = deletePrivacyPolicy(bean, beanResult);
			LOG.info("getQuery(): deletePrivacyPolicy remote response sending");
		}
		
		// -- inferPrivacyPolicy
		else if (bean.getMethod().equals(MethodType.INFER_PRIVACY_POLICY)) {
			LOG.info("getQuery(): inferPrivacyPolicy remote called");
			beanResult.setMethod(MethodType.DELETE_PRIVACY_POLICY);
			ack = inferPrivacyPolicy(bean, beanResult);
			LOG.info("getQuery(): inferPrivacyPolicy remote response sending");
		}
		
		else {
			LOG.info("getQuery(): Unknown method "+bean.getMethod().name());
			beanResult.setAckMessage("Error Unknown method "+bean.getMethod().name());
		}

		beanResult.setAck(ack);
		return beanResult;
	}
	
	private boolean getPrivacyPolicy(PrivacyPolicyManagerBean bean, PrivacyPolicyManagerBeanResult beanResult) {
		try {
			Requestor requestor = RequestorUtils.toRequestor(bean.getRequestor(), commManager.getIdManager());
			RequestPolicy privacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestor);
			beanResult.setPrivacyPolicy(RequestPolicyUtils.toRequestPolicyBean(privacyPolicy));
		} catch (PrivacyException e) {
			beanResult.setAckMessage("Error PrivacyException: "+e.getMessage());
			return false;
		} catch (InvalidFormatException e) {
			beanResult.setAckMessage("Error InvalidFormatException: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean updatePrivacyPolicy(PrivacyPolicyManagerBean bean, PrivacyPolicyManagerBeanResult beanResult) {
		try {
			RequestPolicy privacyPolicy = RequestPolicyUtils.toRequestPolicy(bean.getPrivacyPolicy(), commManager.getIdManager());
			privacyPolicy = privacyPolicyManager.updatePrivacyPolicy(privacyPolicy);
			beanResult.setPrivacyPolicy(RequestPolicyUtils.toRequestPolicyBean(privacyPolicy));
		} catch (PrivacyException e) {
			beanResult.setAckMessage("Error PrivacyException: "+e.getMessage());
			return false;
		} catch (InvalidFormatException e) {
			beanResult.setAckMessage("Error InvalidFormatException: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean inferPrivacyPolicy(PrivacyPolicyManagerBean bean, PrivacyPolicyManagerBeanResult beanResult) {
		try {
			if (0 != bean.getPrivacyPolicyType() && 1 != bean.getPrivacyPolicyType()) {
				throw new PrivacyException("This is not a CIS or a 3P service privacy policy");
			}
			PrivacyPolicyTypeConstants privacyPolicyType = PrivacyPolicyTypeConstants.values()[bean.getPrivacyPolicyType()];
			RequestPolicy privacyPolicy = privacyPolicyManager.inferPrivacyPolicy(privacyPolicyType, new HashMap());
				beanResult.setPrivacyPolicy(RequestPolicyUtils.toRequestPolicyBean(privacyPolicy));
		} catch (PrivacyException e) {
			beanResult.setAckMessage("Error PrivacyException: "+e.getMessage());
			return false;
		}
		return true;
	}

	private boolean deletePrivacyPolicy(PrivacyPolicyManagerBean bean, PrivacyPolicyManagerBeanResult beanResult) {
		try {
			Requestor requestor = RequestorUtils.toRequestor(bean.getRequestor(), commManager.getIdManager());
			if (privacyPolicyManager.deletePrivacyPolicy(requestor)) {
				beanResult.setAckMessage("Privacy Policy deleted");
			}
		} catch (PrivacyException e) {
			beanResult.setAckMessage("Error PrivacyException: "+e.getMessage());
			return false;
		} catch (InvalidFormatException e) {
			beanResult.setAckMessage("Error InvalidFormatException: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	
	
	
	// -- Dependency Injection

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		LOG.info("[DepencyInjection] CommManager injected");
	}
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
		LOG.info("[DepencyInjection] IPrivacyPolicyManager injected");
	}
}
