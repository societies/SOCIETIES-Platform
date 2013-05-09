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
import org.societies.privacytrust.trust.impl.broker.remote.TrustBrokerRemoteClientCallback;
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
	private TrustBrokerRemoteClientCallback trustBrokerRemoteClientCallback;
	
	@Autowired(required=true)
	private TrustEvidenceCollectorRemoteClientCallback trustEvidenceCollectorRemoteClientCallback;

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
		
		if (stanza == null)
			throw new NullPointerException("stanza can't be null");
		if (payload == null)
			throw new NullPointerException("payload can't be null");
		
		if (LOG.isDebugEnabled())
			LOG.debug("receiveResult: stanza=" + stanza + ", payload=" + payload);
		if (stanza.getId() == null) {
			LOG.error("Received result with null stanza id");
			return;
		}
		if (this.trustBrokerRemoteClientCallback.containsClient(stanza.getId()))
			this.trustBrokerRemoteClientCallback.receiveResult(stanza, payload);
		else if (this.trustEvidenceCollectorRemoteClientCallback.containsClient(stanza.getId()))
			this.trustEvidenceCollectorRemoteClientCallback.receiveResult(stanza, payload);
		else
			LOG.error("Received result with unexpected stanza id: " + stanza.getId());
	}		

	/*
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveMessage(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("receiveMessage: stanza=" + stanza + ", payload=" + payload);
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
	 */
	@Override
	public void receiveItems(Stanza stanza, String item, List<String> items) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("receiveMessage: stanza=" + stanza + ", item=" + item
					+ ", items=" + items);
	}

	/*
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveInfo(org.societies.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.comm.xmpp.datatypes.XMPPInfo)
	 */
	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("receiveInfo: stanza=" + stanza + ", node=" + node
					+ ", info=" + info);
	}

	/*
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveError(org.societies.comm.xmpp.datatypes.Stanza, org.societies.comm.xmpp.datatypes.XMPPError)
	 */
	@Override
	public void receiveError(Stanza stanza, XMPPError error) {
		
		if (stanza == null)
			throw new NullPointerException("stanza can't be null");
		if (error == null)
			throw new NullPointerException("error can't be null");
		
		if (LOG.isDebugEnabled())
			LOG.debug("receiveError: stanza=" + stanza + ", error=" + error);
		if (stanza.getId() == null) {
			LOG.error("Received error with null stanza id");
			return;
		}
		if (this.trustBrokerRemoteClientCallback.containsClient(stanza.getId()))
			this.trustBrokerRemoteClientCallback.receiveError(stanza, error);
		else if (this.trustEvidenceCollectorRemoteClientCallback.containsClient(stanza.getId()))
			this.trustEvidenceCollectorRemoteClientCallback.receiveError(stanza, error);
		else
			LOG.error("Received error with unexpected stanza id: " + stanza.getId());
	}
}