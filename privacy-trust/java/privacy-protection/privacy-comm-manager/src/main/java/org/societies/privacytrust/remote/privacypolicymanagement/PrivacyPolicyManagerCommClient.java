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
package org.societies.privacytrust.remote.privacypolicymanagement;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener;
import org.societies.api.internal.privacytrust.privacyprotection.remote.IPrivacyPolicyManagerRemote;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.PrivacyPolicyManagerBean;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.privacytrust.remote.PrivacyCommClientCallback;
/**
 * Comms Client that initiates the remote communication
 *
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyPolicyManagerCommClient implements IPrivacyPolicyManagerRemote {
	private static final Logger LOG = LoggerFactory.getLogger(PrivacyPolicyManagerCommClient.class);

	private ICommManager commManager;
	private PrivacyPolicyManagerCommClientCallback listeners;
	private PrivacyCommClientCallback privacyCommClientCallback;


	@Override
	public void getPrivacyPolicy(RequestorBean requestor, IIdentity targetedNode, IPrivacyPolicyManagerListener listener) throws PrivacyException {
		Stanza stanza = new Stanza(targetedNode);
		listeners.privacyPolicyManagerlisteners.put(stanza.getId(), listener);

		PrivacyPolicyManagerBean bean = new PrivacyPolicyManagerBean();
		bean.setMethod(MethodType.GET_PRIVACY_POLICY);
		bean.setRequestor(requestor);
		try {
			commManager.sendIQGet(stanza, bean, privacyCommClientCallback);
		}
		catch (CommunicationException e) {
			LOG.error("CommunicationException: "+MethodType.GET_PRIVACY_POLICY, e);
			throw new PrivacyException("CommunicationException: "+MethodType.GET_PRIVACY_POLICY, e);
		}
		catch (Exception e) {
			LOG.error("Exception: "+MethodType.GET_PRIVACY_POLICY, e);
			throw new PrivacyException("Exception: "+MethodType.GET_PRIVACY_POLICY, e);
		}
	}
	@Deprecated
	public void getPrivacyPolicy(Requestor requestor, IIdentity targetedNode, IPrivacyPolicyManagerListener listener) throws PrivacyException {
		getPrivacyPolicy(RequestorUtils.toRequestorBean(requestor), targetedNode, listener);
	}

	@Override
	public void updatePrivacyPolicy(RequestPolicy privacyPolicy, IIdentity targetedNode, IPrivacyPolicyManagerListener listener) throws PrivacyException {
		Stanza stanza = new Stanza(targetedNode);
		listeners.privacyPolicyManagerlisteners.put(stanza.getId(), listener);

		PrivacyPolicyManagerBean bean = new PrivacyPolicyManagerBean();
		bean.setMethod(MethodType.UPDATE_PRIVACY_POLICY);
		bean.setPrivacyPolicy(privacyPolicy);
		try {
			commManager.sendIQGet(stanza, bean, privacyCommClientCallback);
		}
		catch (CommunicationException e) {
			LOG.error("CommunicationException: "+MethodType.UPDATE_PRIVACY_POLICY, e);
			throw new PrivacyException("CommunicationException: "+MethodType.UPDATE_PRIVACY_POLICY, e);
		}
		catch (Exception e) {
			LOG.error("Exception: "+MethodType.UPDATE_PRIVACY_POLICY, e);
			throw new PrivacyException("Exception: "+MethodType.UPDATE_PRIVACY_POLICY, e);
		}
	}
	@Deprecated
	public void updatePrivacyPolicy(org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy privacyPolicy, IIdentity targetedNode, IPrivacyPolicyManagerListener listener) throws PrivacyException {
		updatePrivacyPolicy(RequestPolicyUtils.toRequestPolicyBean(privacyPolicy), targetedNode, listener);
	}

	@Override
	public void deletePrivacyPolicy(RequestorBean requestor, IIdentity targetedNode, IPrivacyPolicyManagerListener listener) throws PrivacyException {
		Stanza stanza = new Stanza(targetedNode);
		listeners.privacyPolicyManagerlisteners.put(stanza.getId(), listener);

		PrivacyPolicyManagerBean bean = new PrivacyPolicyManagerBean();
		bean.setMethod(MethodType.DELETE_PRIVACY_POLICY);
		bean.setRequestor(requestor);
		try {
			commManager.sendIQGet(stanza, bean, privacyCommClientCallback);
		}
		catch (CommunicationException e) {
			LOG.error("CommunicationException: "+MethodType.DELETE_PRIVACY_POLICY, e);
			throw new PrivacyException("CommunicationException: "+MethodType.DELETE_PRIVACY_POLICY, e);
		}
		catch (Exception e) {
			LOG.error("Exception: "+MethodType.DELETE_PRIVACY_POLICY, e);
			throw new PrivacyException("Exception: "+MethodType.DELETE_PRIVACY_POLICY, e);
		}
	}
	@Deprecated
	public void deletePrivacyPolicy(Requestor requestor, IIdentity targetedNode, IPrivacyPolicyManagerListener listener) throws PrivacyException {
		deletePrivacyPolicy(RequestorUtils.toRequestorBean(requestor), targetedNode, listener);
	}

	@SuppressWarnings("rawtypes")
	@Deprecated
	public void inferPrivacyPolicy(int privacyPolicyType, Map configuration, IIdentity targetedNode, IPrivacyPolicyManagerListener listener) throws PrivacyException {
		Stanza stanza = new Stanza(targetedNode);
		listeners.privacyPolicyManagerlisteners.put(stanza.getId(), listener);

		PrivacyPolicyManagerBean bean = new PrivacyPolicyManagerBean();
		bean.setMethod(MethodType.INFER_PRIVACY_POLICY);
		bean.setPrivacyPolicyType(privacyPolicyType);
		try {
			commManager.sendIQGet(stanza, bean, privacyCommClientCallback);
		}
		catch (CommunicationException e) {
			LOG.error("CommunicationException: "+MethodType.INFER_PRIVACY_POLICY, e);
			throw new PrivacyException("CommunicationException: "+MethodType.INFER_PRIVACY_POLICY, e);
		}
		catch (Exception e) {
			LOG.error("Exception: "+MethodType.INFER_PRIVACY_POLICY, e);
			throw new PrivacyException("Exception: "+MethodType.INFER_PRIVACY_POLICY, e);
		}
	}



	// -- Dependency Injection
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	public void setListeners(PrivacyPolicyManagerCommClientCallback listeners) {
		this.listeners = listeners;
	}
	public void setPrivacyCommClientCallback(PrivacyCommClientCallback privacyCommClientCallback) {
		this.privacyCommClientCallback = privacyCommClientCallback;
	}
}
