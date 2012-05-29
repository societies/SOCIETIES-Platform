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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IDataObfuscationListener;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyDataManagerListener;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.RequestPolicyUtils;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.ResponseItemUtils;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.PrivacyPolicyManagerBeanResult;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyPolicyManagerCommClientCallback {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyManagerCommClientCallback.class);

	private ICommManager commManager;
	// -- Listeners list
	public Map<String, IPrivacyPolicyManagerListener> privacyPolicyManagerlisteners;


	public PrivacyPolicyManagerCommClientCallback() {
		privacyPolicyManagerlisteners = new HashMap<String, IPrivacyPolicyManagerListener>();
	}


	public void receiveResult(Stanza stanza, PrivacyPolicyManagerBeanResult bean) {
		// -- Get policy
		if (bean.getMethod().equals(MethodType.GET_PRIVACY_POLICY)) {
			LOG.info("$$$$ getPolicy response received");
			IPrivacyPolicyManagerListener listener = privacyPolicyManagerlisteners.get(stanza.getId());
			privacyPolicyManagerlisteners.remove(stanza.getId());
			if (bean.isAck()) {
				try {
					listener.onPrivacyPolicyRetrieved(RequestPolicyUtils.toRequestPolicy(bean.getPrivacyPolicy(), commManager.getIdManager()));
				} catch (InvalidFormatException e) {
					listener.onOperationAborted(e.getMessage(), e);
				}
			}
			else {
				listener.onOperationCancelled(bean.getAckMessage());
			}
			return;
		}

		// -- updatePolicy
		if (bean.getMethod().equals(MethodType.UPDATE_PRIVACY_POLICY)) {
			LOG.info("$$$$ updatePolicy response received");
			IPrivacyPolicyManagerListener listener = privacyPolicyManagerlisteners.get(stanza.getId());
			privacyPolicyManagerlisteners.remove(stanza.getId());
			if (bean.isAck()) {
				try {
					listener.onPrivacyPolicyRetrieved(RequestPolicyUtils.toRequestPolicy(bean.getPrivacyPolicy(), commManager.getIdManager()));
				} catch (InvalidFormatException e) {
					listener.onOperationAborted(e.getMessage(), e);
				}
			}
			else {
				listener.onOperationCancelled(bean.getAckMessage());
			}
			return;
		}

		// -- deletePolicy
		if (bean.getMethod().equals(MethodType.DELETE_PRIVACY_POLICY)) {
			LOG.info("$$$$ deletePolicy response received");
			IPrivacyPolicyManagerListener listener = privacyPolicyManagerlisteners.get(stanza.getId());
			privacyPolicyManagerlisteners.remove(stanza.getId());
			if (bean.isAck()) {
				listener.onOperationSucceed(bean.getAckMessage());
			}
			else {
				listener.onOperationCancelled(bean.getAckMessage());
			}
			return;
		}

		// -- inferPolicy
		if (bean.getMethod().equals(MethodType.INFER_PRIVACY_POLICY)) {
			LOG.info("$$$$ inferPolicy response received");
			IPrivacyPolicyManagerListener listener = privacyPolicyManagerlisteners.get(stanza.getId());
			privacyPolicyManagerlisteners.remove(stanza.getId());
			if (bean.isAck()) {
				try {
					listener.onPrivacyPolicyRetrieved(RequestPolicyUtils.toRequestPolicy(bean.getPrivacyPolicy(), commManager.getIdManager()));
				} catch (InvalidFormatException e) {
					listener.onOperationAborted(e.getMessage(), e);
				}
			}
			else {
				listener.onOperationCancelled(bean.getAckMessage());
			}
			return;
		}
	}


	// -- Dependency Injection

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		LOG.info("[DependencyInjection] CommManager injected");
	}
}
