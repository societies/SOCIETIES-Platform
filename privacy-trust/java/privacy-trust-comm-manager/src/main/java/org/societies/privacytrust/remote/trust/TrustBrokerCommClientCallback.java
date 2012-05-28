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
package org.societies.privacytrust.remote.trust;

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
import org.societies.api.internal.privacytrust.trust.remote.ITrustBrokerRemoteCallback;
import org.societies.api.internal.schema.privacytrust.trust.broker.MethodName;
import org.societies.api.internal.schema.privacytrust.trust.broker.RetrieveTrustBrokerResponseBean;
import org.societies.api.internal.schema.privacytrust.trust.broker.TrustBrokerResponseBean;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
public class TrustBrokerCommClientCallback implements ICommCallback {
	
	/** The logging facility. */
	private static Logger LOG = LoggerFactory.getLogger(TrustBrokerCommClientCallback.class);

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList(
					"http://societies.org/api/internal/schema/privacytrust/trust/model",
					"http://societies.org/api/internal/schema/privacytrust/trust/broker"));
	
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList(
					"org.societies.api.internal.schema.privacytrust.trust.model",
					"org.societies.api.internal.schema.privacytrust.trust.broker"));
	
	private final Map<String,ITrustBrokerRemoteCallback> clients =
			new ConcurrentHashMap<String, ITrustBrokerRemoteCallback>();

	TrustBrokerCommClientCallback() {
		
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
	public void receiveError(Stanza arg0, XMPPError arg1) {
		// TODO Auto-generated method stub

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
	public void receiveResult(final Stanza stanza, final Object bean) {
		
		if (!(bean instanceof TrustBrokerResponseBean))
			throw new IllegalArgumentException("bean is not instance of TrustBrokerResponseBean");
		
		final TrustBrokerResponseBean responseBean = (TrustBrokerResponseBean) bean;
		
		if (MethodName.RETRIEVE.equals(responseBean.getMethodName())) {
			
			final RetrieveTrustBrokerResponseBean retrieveResponseBean =
					responseBean.getRetrieve();
			if (retrieveResponseBean == null) {
				LOG.error("Invalid TrustBroker remote retrieve response: "
						+ "RetrieveTrustBrokerResponseBean can't be null");
				return;
			}
			ITrustBrokerRemoteCallback callback = this.clients.remove(stanza.getId());
			if (callback == null) {
				LOG.error("Could not find client callback for TrustBroker remote retrieve response: "
						+ stanza.getId());
				return;
			}
			
			callback.onRetrieveTrust(retrieveResponseBean.getResult());
			
		} else {
			
			LOG.error("Unsupported TrustBroker remote response method: " + responseBean.getMethodName());
		}
	}

	/**
	 * 
	 * @param id
	 * @param callback
	 */
	void addClient(String id, ITrustBrokerRemoteCallback callback) {
		
		this.clients.put(id, callback);
	}
	
	/**
	 * 
	 * @param id
	 */
	void removeClient(String id) {
		
		this.clients.remove(id);
	}
}