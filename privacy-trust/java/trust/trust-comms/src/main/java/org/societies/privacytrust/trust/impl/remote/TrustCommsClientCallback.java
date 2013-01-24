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
package org.societies.privacytrust.trust.impl.remote;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.schema.privacytrust.trust.broker.TrustBrokerResponseBean;
import org.societies.api.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorResponseBean;
import org.societies.privacytrust.trust.impl.broker.remote.InternalTrustBrokerRemoteClientCallback;
import org.societies.privacytrust.trust.impl.broker.remote.TrustBrokerRemoteClientCallback;
import org.societies.privacytrust.trust.impl.evidence.remote.InternalTrustEvidenceCollectorRemoteClientCallback;
import org.societies.privacytrust.trust.impl.evidence.remote.TrustEvidenceCollectorRemoteClientCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.0
 */
@Service
public class TrustCommsClientCallback implements ICommCallback {
	
	private static Logger LOG = LoggerFactory.getLogger(TrustCommsClientCallback.class);

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList(
					"http://societies.org/api/schema/privacytrust/trust/model",
					"http://societies.org/api/schema/privacytrust/trust/broker",
					"http://societies.org/api/schema/privacytrust/trust/evidence/collector",
					"http://societies.org/api/internal/schema/privacytrust/trust/broker",
					"http://societies.org/api/internal/schema/privacytrust/trust/evidence/collector"));
	
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList(
					"org.societies.api.schema.privacytrust.trust.model",
					"org.societies.api.schema.privacytrust.trust.broker",
					"org.societies.api.schema.privacytrust.trust.evidence.collector",
					"org.societies.api.internal.schema.privacytrust.trust.broker",
					"org.societies.api.internal.schema.privacytrust.trust.evidence.collector"));

	@Autowired(required=true)
	private TrustBrokerRemoteClientCallback trustBrokerRemoteClientCallback;
	
	@Autowired(required=true)
	private InternalTrustBrokerRemoteClientCallback internalTrustBrokerRemoteClientCallback;
	
	@Autowired(required=true)
	private TrustEvidenceCollectorRemoteClientCallback trustEvidenceCollectorRemoteClientCallback;
	
	@Autowired(required=true)
	private InternalTrustEvidenceCollectorRemoteClientCallback internalTrustEvidenceCollectorRemoteClientCallback;

	@Autowired(required=true)
	public TrustCommsClientCallback(ICommManager commsMgr) throws Exception {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		try {
			// Register with the Societies Comms Manager
			if (LOG.isInfoEnabled())
				LOG.info("Registering with Comms Mgr...");
			commsMgr.register(this);
			
		} catch (Exception e) {
			LOG.error("Could not register with Comms Mgr:"
					+ e.getLocalizedMessage() , e);
			throw e;
		}
	}

	/*
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() { 
		
		return PACKAGES; 
	}
	
	/*
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		
		return NAMESPACES; 
	}
	
	/*
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveResult(org.societies.comm.xmpp.datatypes.Stanza, Object payload)
	 */
	@Override
	public void receiveResult(Stanza stanza, Object payload) {
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("receiveResult: stanza.id=" + stanza.getId());
			LOG.debug("receiveResult: stanza.from=" + stanza.getFrom());
			LOG.debug("receiveResult: stanza.to=" + stanza.getTo());
			LOG.debug("receiveResult: payload=" + payload);
		}

		if (payload instanceof org.societies.api.internal.schema.privacytrust.trust.broker.TrustBrokerResponseBean) {
			this.internalTrustBrokerRemoteClientCallback.receiveResult(stanza, (TrustBrokerResponseBean) payload);
		} else if (payload instanceof TrustBrokerResponseBean) {
			this.trustBrokerRemoteClientCallback.receiveResult(stanza, (TrustBrokerResponseBean) payload);
		} else if (payload instanceof org.societies.api.internal.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorResponseBean) {
			this.internalTrustEvidenceCollectorRemoteClientCallback.receiveResult(stanza, (TrustEvidenceCollectorResponseBean) payload);
		} else if (payload instanceof TrustEvidenceCollectorResponseBean) {
			this.trustEvidenceCollectorRemoteClientCallback.receiveResult(stanza, (TrustEvidenceCollectorResponseBean) payload);
		} else {
			LOG.error("Unexpected result payload: "
					+ ((payload != null) ? payload.getClass() : "null"));
		}
	}		

	/*
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveMessage(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("receiveMessage: stanza.id=" + stanza.getId());
			LOG.debug("receiveMessage: stanza.from=" + stanza.getFrom());
			LOG.debug("receiveMessage: stanza.to=" + stanza.getTo());
			LOG.debug("receiveMessage: payload=" + payload);
		}
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
	 */
	@Override
	public void receiveItems(Stanza stanza, String arg1, List<String> arg2) {
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("receiveMessage: stanza.id=" + stanza.getId());
			LOG.debug("receiveMessage: stanza.from=" + stanza.getFrom());
			LOG.debug("receiveMessage: stanza.to=" + stanza.getTo());
			LOG.debug("receiveMessage: item=" + arg1);
			LOG.debug("receiveMessage: items=" + arg2);
		}
	}

	/*
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveInfo(org.societies.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.comm.xmpp.datatypes.XMPPInfo)
	 */
	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("receiveInfo: stanza.id=" + stanza.getId());
			LOG.debug("receiveInfo: stanza.from=" + stanza.getFrom());
			LOG.debug("receiveInfo: stanza.to=" + stanza.getTo());
			LOG.debug("receiveInfo: node=" + node);
			LOG.debug("receiveInfo: info=" + info);
		}
	}

	/*
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveError(org.societies.comm.xmpp.datatypes.Stanza, org.societies.comm.xmpp.datatypes.XMPPError)
	 */
	@Override
	public void receiveError(Stanza stanza, XMPPError info) {
		
		LOG.error("receiveError: stanza.id=" + stanza.getId());
		LOG.error("receiveError: stanza.from=" + stanza.getFrom());
		LOG.error("receiveError: stanza.to=" + stanza.getTo());
		LOG.error("receiveError: info=" + info);
	}
}