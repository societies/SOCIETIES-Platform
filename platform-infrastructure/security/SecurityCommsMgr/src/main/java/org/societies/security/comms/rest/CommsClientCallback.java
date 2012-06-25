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
package org.societies.security.comms.rest;

import java.net.URI;
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
import org.societies.api.internal.domainauthority.IClientJarServerCallback;
import org.societies.api.internal.schema.domainauthority.rest.ClientJarBeanResult;
import org.societies.api.internal.schema.domainauthority.rest.UrlBean;

/**
 * Comms Client Callback that receives results from remote method invocations
 * 
 * @author Mitja Vardjan
 */
public class CommsClientCallback implements ICommCallback {

	private static final List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays.asList(
					"http://societies.org/api/internal/schema/domainauthority/rest"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays.asList(
					"org.societies.api.internal.schema.domainauthority.rest"));

	/**
	 * Callbacks provided by original invokers. These need to be invoked in this class.
	 */
	private Map<String, IClientJarServerCallback> callbacks = new HashMap<String, IClientJarServerCallback>();
	
	private static Logger LOG = LoggerFactory.getLogger(CommsClientCallback.class);

	public CommsClientCallback() {
		LOG.info("CommsClientCallback()");
	}

	public void init() {
	}

	public void addCallback(String stanzaId, IClientJarServerCallback callback) {
		LOG.debug("addCallback({}, {})", stanzaId, callback);
		callbacks.put(stanzaId, callback);
	}
	
	public void removeCallback(String stanzaId) {
		
		Object previous;
		
		previous = callbacks.remove(stanzaId);
		LOG.debug("removeCallback({}): success = {}", stanzaId, previous != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		LOG.debug("getJavaPackages()");
		return PACKAGES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		LOG.debug("getXMLNamespaces()");
		return NAMESPACES;
	}

	@Override
	public void receiveError(Stanza stanza, XMPPError error) {
		LOG.debug("receiveError()");
	}

	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		LOG.debug("receiveInfo()");
	}

	@Override
	public void receiveMessage(Stanza stanza, Object payload) {

		LOG.debug("receiveMessage({}, {})", stanza, payload);
		LOG.debug("receiveMessage(): stanza.id   = ", stanza.getId());
		LOG.debug("receiveMessage(): stanza.from = ", stanza.getFrom());
		LOG.debug("receiveMessage(): stanza.to   = ", stanza.getTo());

		if (payload instanceof ClientJarBeanResult) {
			
			ClientJarBeanResult payloadResult = (ClientJarBeanResult) payload;
			UrlBean result = payloadResult.getUrlBean();
			
			int sessionId = result.getSessionId();
			URI url = result.getUrl();
			boolean success = result.isSuccess();
			
			LOG.debug("receiveMessage(): sessionId = {}, success = {}, url = " + url,
					sessionId, success);
		}
		else {
			LOG.warn("receiveMessage(): unexpected payload type {}", payload);
		}
	}

	@Override
	public void receiveResult(Stanza stanza, Object payload) {

		String stanzaId = stanza.getId();
		
		LOG.debug("receiveResult({}, {})", stanza, payload);
		LOG.debug("receiveResult(): stanza.id   = {}", stanzaId);
		LOG.debug("receiveResult(): stanza.from = {}", stanza.getFrom());
		LOG.debug("receiveResult(): stanza.to   = {}", stanza.getTo());
		
		if (payload instanceof ClientJarBeanResult) {
			
			ClientJarBeanResult payloadResult = (ClientJarBeanResult) payload;
			UrlBean result = payloadResult.getUrlBean();

			int sessionId = result.getSessionId();
			URI url = result.getUrl();
			boolean success = result.isSuccess();

			LOG.debug("receiveMessage(): sessionId = {}, success = {}, url = " + url,
					sessionId, success);
			
			IClientJarServerCallback cb = callbacks.get(stanzaId);
			if (cb != null) {
				cb.receiveResult(result);
			}
			else {
				LOG.warn("receiveResult(): There is no callback for stanza ID {}", stanzaId);
			}
			callbacks.remove(stanzaId);
		}
		else {
			LOG.warn("receiveResult(): unexpected payload type {}", payload);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(
	 * org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String,
	 * java.util.List)
	 */
	@Override
	public void receiveItems(Stanza stanza, String node, List<String> items) {
		LOG.debug("receiveItems()");
	}
}
