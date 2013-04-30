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
package org.societies.privacytrust.trust.impl.evidence.remote;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.schema.privacytrust.trust.evidence.collector.MethodName;
import org.societies.api.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorResponseBean;
import org.societies.privacytrust.trust.api.evidence.remote.ITrustEvidenceCollectorRemoteClientCallback;
import org.springframework.stereotype.Service;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
@Service
public class TrustEvidenceCollectorRemoteClientCallback implements ICommCallback {
	
	/** The logging facility. */
	private static Logger LOG = LoggerFactory.getLogger(TrustEvidenceCollectorRemoteClientCallback.class);

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList(
					"http://societies.org/api/schema/identity",
					"http://societies.org/api/schema/privacytrust/trust/model",
					"http://societies.org/api/schema/privacytrust/trust/evidence/collector"));
	
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList(
					"org.societies.api.schema.identity",
					"org.societies.api.internal.schema.privacytrust.trust.model",
					"org.societies.api.internal.schema.privacytrust.trust.evidence.collector"));
	
	private final Map<String, ITrustEvidenceCollectorRemoteClientCallback> clients =
			new ConcurrentHashMap<String, ITrustEvidenceCollectorRemoteClientCallback>();

	TrustEvidenceCollectorRemoteClientCallback() {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		
		return PACKAGES;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		
		return NAMESPACES;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveError(org.societies.api.comm.xmpp.datatypes.Stanza, org.societies.api.comm.xmpp.exceptions.XMPPError)
	 */
	@Override
	public void receiveError(Stanza stanza, XMPPError error) {
		
		if (stanza == null)
			throw new NullPointerException("stanza can't be null");
		if (error == null)
			throw new NullPointerException("error can't be null");
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received error: stanza=" + stanza + ", error=" + error);
		if (stanza.getId() == null) {
			LOG.error("Received error with null stanza id");
			return;
		}
		final ITrustEvidenceCollectorRemoteClientCallback callbackClient = 
				this.removeClient(stanza.getId());
		if (callbackClient == null) {
			LOG.error("Received error with stanza id '" + stanza.getId()
					+ "' but no matching callback was found");
			return;
		}
		final TrustEvidenceCollectorCommsException exception = 
				new TrustEvidenceCollectorCommsException(error.getGenericText());
		callbackClient.onException(exception);
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveInfo(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.api.comm.xmpp.datatypes.XMPPInfo)
	 */
	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
	 */
	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveResult(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveResult(final Stanza stanza, final Object payload) {
		
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
		final ITrustEvidenceCollectorRemoteClientCallback callback = 
				this.removeClient(stanza.getId());
		if (callback == null) {
			LOG.error("Could not handle result bean: No callback client found for stanza with id: " 
					+ stanza.getId());
			return;
		}
		
		if (!(payload instanceof TrustEvidenceCollectorResponseBean)) {
			callback.onException(new TrustEvidenceCollectorCommsException(
					"Could not handle result bean: Unexpected type: "
							+ payload.getClass()));
			return;
		}
		
		final TrustEvidenceCollectorResponseBean responseBean = (TrustEvidenceCollectorResponseBean) payload;
		if (LOG.isDebugEnabled())
			LOG.debug("receiveResult: responseBean.getMethodName()="
					+ responseBean.getMethodName());	
		if (MethodName.ADD_DIRECT_EVIDENCE.equals(responseBean.getMethodName())) {	
			
			callback.onAddedDirectEvidence();
			
		} else if (MethodName.ADD_INDIRECT_EVIDENCE.equals(responseBean.getMethodName())) {	
				
			callback.onAddedIndirectEvidence();
			
		} else {
			
			LOG.error("Unsupported TrustEvidenceCollector remote response method: "
					+ responseBean.getMethodName());
			callback.onException(new TrustEvidenceCollectorCommsException(
					"Unsupported TrustEvidenceCollector remote response method: "
					+ responseBean.getMethodName()));
		}
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public boolean containsClient(String id) {
		
		return this.clients.containsKey(id);
	}
	
	/**
	 * 
	 * @param id
	 * @param callback
	 */
	void addClient(String id, ITrustEvidenceCollectorRemoteClientCallback callback) {
		
		this.clients.put(id, callback);
	}
	
	/**
	 * 
	 * @param id
	 * @return 
	 */
	ITrustEvidenceCollectorRemoteClientCallback removeClient(String id) {
		
		return this.clients.remove(id);
	}
}