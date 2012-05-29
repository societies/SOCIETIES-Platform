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
package org.societies.privacytrust.remote;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation.NegotiationAgentBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.PrivacyAgreementManagerBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.PrivacyPolicyManagerBean;
import org.societies.api.internal.schema.privacytrust.trust.broker.TrustBrokerRequestBean;
import org.societies.privacytrust.remote.privacydatamanagement.PrivacyDataManagerCommServer;
import org.societies.privacytrust.remote.privacynegotiationmanagement.PrivacyNegotiationManagerCommServer;
import org.societies.privacytrust.remote.privacypolicymanagement.PrivacyAgreementManagerCommServer;
import org.societies.privacytrust.remote.privacypolicymanagement.PrivacyPolicyManagerCommServer;
import org.societies.privacytrust.remote.trust.TrustBrokerCommServer;


public class PrivacyTrustCommServer implements IFeatureServer {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyTrustCommServer.class);

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/internal/schema/privacytrust/privacyprotection/privacydatamanagement",
					"http://societies.org/api/internal/schema/privacytrust/privacyprotection/privacypolicymanagement",
					"http://societies.org/api/internal/schema/privacytrust/privacyprotection/model/privacypolicy",
					"http://societies.org/api/schema/identity",
					"http://societies.org/api/internal/schema/privacytrust/privacyprotection/negotiation", 
			  		"http://societies.org/api/schema/servicelifecycle/model",
			  		"http://societies.org/api/internal/schema/privacytrust/trust/model",
			  		"http://societies.org/api/internal/schema/privacytrust/trust/broker"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement",
					"org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement",
					"org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy",
					"org.societies.api.schema.identity",
					"org.societies.api.internal.schema.privacytrust.privacyprotection.negotiation",
			  		"org.societies.api.schema.servicelifecycle.model",
			  		"org.societies.api.internal.schema.privacytrust.trust.model",
			  		"org.societies.api.internal.schema.privacytrust.trust.broker"));

	private ICommManager commManager;
	private PrivacyDataManagerCommServer privacyDataManagerCommServer;
	private PrivacyPolicyManagerCommServer privacyPolicyManagerCommServer;
	private PrivacyAgreementManagerCommServer privacyAgreementManagerCommServer;
	private PrivacyNegotiationManagerCommServer privacyNegotiationManagerCommServer;
	private TrustBrokerCommServer trustBrokerCommServer;
	
	public PrivacyTrustCommServer() {
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
			LOG.info("initBean(): PrivacyTrustCommServer registered to the Societies Comm Manager");
		} catch (CommunicationException e) {
			LOG.error("initBean(): ", e);
		}
	}


	/**
	 * Get Request Received
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#getQuery(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		LOG.info("getQuery({}, {})", stanza, payload);
		LOG.info("getQuery(): stanza.id   = {}", stanza.getId());
		LOG.info("getQuery(): stanza.from = {}", stanza.getFrom());
		LOG.info("getQuery(): stanza.to   = {}", stanza.getTo());

		// -- Privacy Data Management
		if (payload instanceof PrivacyDataManagerBean) {
			return privacyDataManagerCommServer.getQuery(stanza, (PrivacyDataManagerBean) payload);
		}
		
		// -- Privacy Policy Management
		if (payload instanceof PrivacyPolicyManagerBean) {
			return privacyPolicyManagerCommServer.getQuery(stanza, (PrivacyPolicyManagerBean) payload);
		}
		
		if (payload instanceof PrivacyAgreementManagerBean) {
			return privacyAgreementManagerCommServer.getQuery(stanza, (PrivacyAgreementManagerBean) payload);
		}
		
		// -- Privacy Preference Management

		// -- Privacy Policy Negotiation Management
		if (payload instanceof NegotiationAgentBean){
			return privacyNegotiationManagerCommServer.getQuery(stanza, (NegotiationAgentBean) payload);
		}

		// -- Assessment Management

		// -- Trust Management
		/* else */ if (payload instanceof TrustBrokerRequestBean){
			return this.trustBrokerCommServer.getQuery(stanza, (TrustBrokerRequestBean) payload);
		}
		
		return null;
	}
	

	/**
	 * Get Request Received
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#setQuery(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza stanza, Object payload) throws XMPPError {
		LOG.info("setQuery({}, {})", stanza, payload);
		LOG.info("setQuery(): stanza.id   = {}", stanza.getId());
		LOG.info("setQuery(): stanza.from = {}", stanza.getFrom());
		LOG.info("setQuery(): stanza.to   = {}", stanza.getTo());

		// -- Privacy Data Management

		// -- Privacy Policy Management

		// -- Privacy Preference Management

		// -- Privacy Policy Negotiation Management

		// -- Assessment Management

		// -- Trust Management
		
		return null;
	}

	/**
	 * Message Received
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#receiveMessage(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		LOG.info("receiveMessage({}, {})", stanza, payload);
		LOG.info("receiveMessage(): stanza.id   = {}", stanza.getId());
		LOG.info("receiveMessage(): stanza.from = {}", stanza.getFrom());
		LOG.info("receiveMessage(): stanza.to   = {}", stanza.getTo());

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
	public void setPrivacyDataManagerCommServer(
			PrivacyDataManagerCommServer privacyDataManagerCommServer) {
		this.privacyDataManagerCommServer = privacyDataManagerCommServer;
		LOG.info("[DependencyInjection] PrivacyDataManagerCommServer injected");
	}
	public void setPrivacyNegotiationManagerCommServer(
			PrivacyNegotiationManagerCommServer privacyNegotiationManagerCommServer) {
		this.privacyNegotiationManagerCommServer = privacyNegotiationManagerCommServer;
		LOG.info("[DependencyInjection] PrivacyNegotiationManagerCommServer injected");
	}
	public void setTrustBrokerCommServer(TrustBrokerCommServer trustBrokerCommServer) {
		this.trustBrokerCommServer = trustBrokerCommServer;
		LOG.info("[DependencyInjection] TrustBrokerCommServer injected");
	}
	public void setPrivacyPolicyManagerCommServer(
			PrivacyPolicyManagerCommServer privacyPolicyManagerCommServer) {
		this.privacyPolicyManagerCommServer = privacyPolicyManagerCommServer;
		LOG.info("[DependencyInjection] PrivacyPolicyManagerCommServer injected");
	}
	public void setPrivacyAgreementManagerCommServer(
			PrivacyAgreementManagerCommServer privacyAgreementManagerCommServer) {
		this.privacyAgreementManagerCommServer = privacyAgreementManagerCommServer;
		LOG.info("[DependencyInjection] PrivacyAgreementManagerCommServer injected");
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
