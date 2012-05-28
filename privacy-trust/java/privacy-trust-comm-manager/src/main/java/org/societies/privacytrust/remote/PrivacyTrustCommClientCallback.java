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
package org.societies.privacytrust.remote;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBeanResult;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.PrivacyAgreementManagerBeanResult;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.PrivacyPolicyManagerBeanResult;
import org.societies.api.internal.schema.privacytrust.trust.broker.TrustBrokerResponseBean;
import org.societies.privacytrust.remote.privacydatamanagement.PrivacyDataManagerCommClientCallback;
import org.societies.privacytrust.remote.privacypolicymanagement.PrivacyAgreementManagerCommClientCallback;
import org.societies.privacytrust.remote.privacypolicymanagement.PrivacyPolicyManagerCommClientCallback;
import org.societies.privacytrust.remote.trust.TrustBrokerCommClientCallback;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyTrustCommClientCallback implements ICommCallback {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyTrustCommClientCallback.class);

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/internal/schema/privacytrust/privacyprotection/privacydatamanagement",
					"http://societies.org/api/internal/schema/privacytrust/privacyprotection/privacypolicymanagement",
					"http://societies.org/api/internal/schema/privacytrust/privacyprotection/model/privacypolicy",
					"http://societies.org/api/schema/identity",
					"http://societies.org/api/internal/schema/privacytrust/trust/model",
					"http://societies.org/api/internal/schema/privacytrust/trust/broker"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement",
					"org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement",
					"org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy",
					"org.societies.api.schema.identity",
					"org.societies.api.internal.schema.privacytrust.trust.model",
					"org.societies.api.internal.schema.privacytrust.trust.broker"));

	// Dependencies
	private ICommManager commManager;
	private PrivacyDataManagerCommClientCallback privacyDataManagerCommClientCallback;
	private PrivacyPolicyManagerCommClientCallback privacyPolicyManagerCommClientCallback;
	private PrivacyAgreementManagerCommClientCallback privacyAgreementManagerCommClientCallback;
	private TrustBrokerCommClientCallback trustBrokerCommClientCallback; 

	public PrivacyTrustCommClientCallback() {
	}

	/**
	 * Register this server to the Societies Communication Manager
	 * Entry point of the Privacy and Trust Comm Manager
	 */
	public void initBean() {
		LOG.info("initBean(): commMgr = {}", commManager.toString());

		try {
			// Register to the Societies Comm Manager
			commManager.register(this);
			LOG.info("initBean(): PrivacyTrustCommClientCallback registered to the Societies Comm Manager");
		} catch (CommunicationException e) {
			LOG.error("initBean(): ", e);
		}
	}


	/**
	 * Received a result
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveResult(org.societies.comm.xmpp.datatypes.Stanza, Object payload)
	 */
	@Override
	public void receiveResult(Stanza stanza, Object payload) {
		LOG.info("receiveResult({}, {})", stanza, payload);
		LOG.info("receiveResult(): stanza.id   = {}", stanza.getId());
		LOG.info("receiveResult(): stanza.from = {}", stanza.getFrom());
		LOG.info("receiveResult(): stanza.to   = {}", stanza.getTo());

		// -- Privacy Data Management
		if (payload instanceof PrivacyDataManagerBeanResult) {
			privacyDataManagerCommClientCallback.receiveResult(stanza, (PrivacyDataManagerBeanResult) payload);
		}
		// -- Privacy Policy Management
		if (payload instanceof PrivacyPolicyManagerBeanResult) {
			privacyPolicyManagerCommClientCallback.receiveResult(stanza, (PrivacyPolicyManagerBeanResult) payload);
		}
		
		if (payload instanceof PrivacyAgreementManagerBeanResult) {
			privacyAgreementManagerCommClientCallback.receiveResult(stanza, (PrivacyAgreementManagerBeanResult) payload);
		}

		// -- Privacy Preference Management

		// -- Privacy Policy Negotiation Management

		// -- Assessment Management

		// -- Trust Management
		/* else */ if (payload instanceof TrustBrokerResponseBean)
			this.trustBrokerCommClientCallback.receiveResult(stanza, (TrustBrokerResponseBean) payload);
	}		

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveMessage(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		LOG.debug("receiveMessage({}, {})", stanza, payload);
		LOG.debug("receiveMessage(): stanza.id   = {}", stanza.getId());
		LOG.debug("receiveMessage(): stanza.from = {}", stanza.getFrom());
		LOG.debug("receiveMessage(): stanza.to   = {}", stanza.getTo());

		// -- Privacy Data Management

		// -- Privacy Policy Management

		// -- Privacy Preference Management

		// -- Privacy Policy Negotiation Management

		// -- Assessment Management

		// -- Trust Management

	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
	 */
	@Override
	public void receiveItems(Stanza stanza, String arg1, List<String> arg2) {
		LOG.debug("receiveItems({}, {}, {})", stanza, arg1);
		LOG.debug("receiveItems(): stanza.id   = {}", stanza.getId());
		LOG.debug("receiveItems(): stanza.from = {}", stanza.getFrom());
		LOG.debug("receiveItems(): stanza.to   = {}", stanza.getTo());

		// -- Privacy Data Management

		// -- Privacy Policy Management

		// -- Privacy Preference Management

		// -- Privacy Policy Negotiation Management

		// -- Assessment Management

		// -- Trust Management

	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveInfo(org.societies.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.comm.xmpp.datatypes.XMPPInfo)
	 */
	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		LOG.info("receiveInfo({}, {})", stanza, info);
		LOG.info("receiveInfo(): stanza.id   = {}", stanza.getId());
		LOG.info("receiveInfo(): stanza.from = {}", stanza.getFrom());
		LOG.info("receiveInfo(): stanza.to   = {}", stanza.getTo());

		// -- Privacy Data Management

		// -- Privacy Policy Management

		// -- Privacy Preference Management

		// -- Privacy Policy Negotiation Management

		// -- Assessment Management

		// -- Trust Management

	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveError(org.societies.comm.xmpp.datatypes.Stanza, org.societies.comm.xmpp.datatypes.XMPPError)
	 */
	@Override
	public void receiveError(Stanza stanza, XMPPError info) {
		LOG.info("receiveError({}, {})", stanza, info);
		LOG.info("receiveError(): stanza.id   = {}", stanza.getId());
		LOG.info("receiveError(): stanza.from = {}", stanza.getFrom());
		LOG.info("receiveError(): stanza.to   = {}", stanza.getTo());

		// -- Privacy Data Management

		// -- Privacy Policy Management

		// -- Privacy Preference Management

		// -- Privacy Policy Negotiation Management

		// -- Assessment Management

		// -- Trust Management

	}



	// -- Dependency Injection

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		LOG.info("[DependencyInjection] CommManager injected");
	}
	public void setPrivacyDataManagerCommClientCallback(
			PrivacyDataManagerCommClientCallback privacyDataManagerCommClientCallback) {
		this.privacyDataManagerCommClientCallback = privacyDataManagerCommClientCallback;
		LOG.info("[DependencyInjection] PrivacyDataManagerCommClientCallback injected");
	}
	public void setPrivacyPolicyManagerCommClientCallback(
			PrivacyPolicyManagerCommClientCallback privacyPolicyManagerCommClientCallback) {
		this.privacyPolicyManagerCommClientCallback = privacyPolicyManagerCommClientCallback;
		LOG.info("[DependencyInjection] PrivacyPolicyManagerCommClientCallback injected");
	}
	public void setPrivacyAgreementManagerCommClientCallback(
			PrivacyAgreementManagerCommClientCallback privacyAgreementManagerCommClientCallback) {
		this.privacyAgreementManagerCommClientCallback = privacyAgreementManagerCommClientCallback;
		LOG.info("[DependencyInjection] PrivacyAgreementManagerCommClientCallback injected");
	}
	public void setTrustBrokerCommClientCallback(
			TrustBrokerCommClientCallback trustBrokerCommClientCallback) {
		this.trustBrokerCommClientCallback = trustBrokerCommClientCallback;
		LOG.info("[DependencyInjection] TrustBrokerCommClientCallback injected");
	}



	// -- Getters / Setters

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() { return PACKAGES; }
	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() { return NAMESPACES; }
}
