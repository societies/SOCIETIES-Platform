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
package org.societies.privacytrust.remote.privacydatamanagement;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IDataObfuscationListener;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyDataManagerListener;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.ResponseItemUtils;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBeanResult;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyDataManagerCommClientCallback implements ICommCallback {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerCommClientCallback.class);
	
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/internal/schema/privacytrust/privacyprotection/privacydatamanagement",
					"http://societies.org/api/internal/schema/privacytrust/privacyprotection/model/privacypolicy",
					"http://societies.org/api/schema/identity"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement",
					"org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy",
					"org.societies.api.schema.identity"));

	public Map<String, IPrivacyDataManagerListener> privacyDataManagerlisteners; 
	public Map<String, IDataObfuscationListener> dataObfuscationlisteners;


	public PrivacyDataManagerCommClientCallback() {
		privacyDataManagerlisteners = new HashMap<String, IPrivacyDataManagerListener>();
		dataObfuscationlisteners = new HashMap<String, IDataObfuscationListener>();
	}
	
	@Override
	public void receiveResult(Stanza stanza, Object payload) {
		LOG.info("$$$$ PrivacyTrustCommManager response received");
		LOG.debug("receiveResult({}, {})", stanza, payload);
		LOG.debug("receiveResult(): stanza.id   = {}", stanza.getId());
		LOG.debug("receiveResult(): stanza.from = {}", stanza.getFrom());
		LOG.debug("receiveResult(): stanza.to   = {}", stanza.getTo());
		if (payload instanceof PrivacyDataManagerBeanResult){
			this.receiveResult(stanza, (PrivacyDataManagerBeanResult) payload);
			return;
		}
	}

	public void receiveResult(Stanza stanza, PrivacyDataManagerBeanResult bean) {
		// -- Check Permission
		if (bean.getMethod().equals(MethodType.CHECK_PERMISSION)) {
			LOG.info("$$$$ checkPermission response received");
			IPrivacyDataManagerListener listener = privacyDataManagerlisteners.get(stanza.getId());
			privacyDataManagerlisteners.remove(stanza.getId());
			if (bean.isAck()) {
				listener.onAccessControlChecked(ResponseItemUtils.toResponseItem(bean.getPermission()));
			}
			else {
				listener.onAccessControlCancelled(bean.getAckMessage());
			}
			return;
		}

		// -- Obfuscate Data
		else if (bean.getMethod().equals(MethodType.OBFUSCATE_DATA)) {
			LOG.info("$$$$ obfuscateData response received");
			IDataObfuscationListener listener = dataObfuscationlisteners.get(stanza.getId());
			dataObfuscationlisteners.remove(stanza.getId());
			if (bean.isAck()) {
				listener.onObfuscationCancelled(bean.getAckMessage());
			}
			else {
				listener.onObfuscationCancelled(bean.getAckMessage());
			}
			return;
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveError(org.societies.comm.xmpp.datatypes.Stanza, org.societies.comm.xmpp.datatypes.XMPPError)
	 */
	@Override
	public void receiveError(Stanza returnStanza, XMPPError info) {
		LOG.error(info.getMessage(), info);
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveInfo(org.societies.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.comm.xmpp.datatypes.XMPPInfo)
	 */
	@Override
	public void receiveInfo(Stanza returnStanza, String node, XMPPInfo info) {
		LOG.info(info.getIdentityName(), info);
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveMessage(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		LOG.info("Message received from "+stanza.getFrom()+" to "+stanza.getTo());
		LOG.debug("receiveMessage({}, {})", stanza, payload);
		LOG.debug("receiveMessage(): stanza.id   = {}", stanza.getId());
		LOG.debug("receiveMessage(): stanza.from = {}", stanza.getFrom());
		LOG.debug("receiveMessage(): stanza.to   = {}", stanza.getTo());
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
	 */
	@Override
	public void receiveItems(Stanza stanza, String arg1, List<String> arg2) {
		LOG.info("Items received from "+stanza.getFrom()+" to "+stanza.getTo());
	}
	
	



	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#getJavaPackages() */
	@Override
	public List<String> getJavaPackages() { return PACKAGES; }

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#getXMLNamespaces() */
	@Override
	public List<String> getXMLNamespaces() { return NAMESPACES; }
}
