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
package org.societies.privacytrust.trust.impl.remote;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.schema.privacytrust.trust.broker.TrustBrokerRequestBean;
import org.societies.api.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorRequestBean;
import org.societies.privacytrust.trust.impl.broker.remote.TrustBrokerRemoteServer;
import org.societies.privacytrust.trust.impl.evidence.remote.TrustEvidenceCollectorRemoteServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.0
 */
@Service
public class TrustCommsServer implements IFeatureServer {
	
	private static Logger LOG = LoggerFactory.getLogger(TrustCommsServer.class);

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList(
					"http://societies.org/api/schema/identity",
			  		"http://societies.org/api/schema/privacytrust/trust/model",
			  		"http://societies.org/api/schema/privacytrust/trust/broker",
			  		"http://societies.org/api/schema/privacytrust/trust/evidence/collector"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList(
					"org.societies.api.schema.identity",
			  		"org.societies.api.schema.privacytrust.trust.model",
			  		"org.societies.api.schema.privacytrust.trust.broker",
			  		"org.societies.api.schema.privacytrust.trust.evidence.collector"));
	
	@Autowired(required=true)
	private TrustBrokerRemoteServer trustBrokerRemoteServer;
	
	@Autowired(required=true)
	private TrustEvidenceCollectorRemoteServer trustEvidenceCollectorRemoteServer;

	@Autowired(required=true)
	public TrustCommsServer(ICommManager commsMgr) throws Exception {
		
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
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#getQuery(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		
		if (LOG.isDebugEnabled())
			LOG.debug("getQuery: stanza=" + stanza + ", payload=" + payload);

		if (payload instanceof TrustBrokerRequestBean) {
			return this.trustBrokerRemoteServer.getQuery(stanza, payload);
		} else if (payload instanceof TrustEvidenceCollectorRequestBean) {
			return this.trustEvidenceCollectorRemoteServer.getQuery(stanza, payload);
		} else {
			final String errorMsg = "Unexpected query payload: "
					+ ((payload != null) ? payload.getClass() : "null"); 
			LOG.error(errorMsg);
			throw new XMPPError(StanzaError.bad_request, errorMsg);
		}
	}
	

	/*
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#setQuery(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza stanza, Object payload) throws XMPPError {
		
		if (LOG.isDebugEnabled())
			LOG.debug("setQuery: stanza=" + stanza + ", payload=" + payload);
		
		return null;
	}

	/*
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#receiveMessage(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("receiveMessage: stanza=" + stanza + ", payload=" + payload);
	}
}