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
package org.societies.privacytrust.trust.impl.broker.remote;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.schema.privacytrust.trust.broker.MethodName;
import org.societies.api.schema.privacytrust.trust.broker.TrustBrokerResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipsResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustValueResponseBean;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;
import org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback;
import org.springframework.stereotype.Service;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
@Service
public class TrustBrokerRemoteClientCallback implements ICommCallback {
	
	/** The logging facility. */
	private static Logger LOG = LoggerFactory.getLogger(TrustBrokerRemoteClientCallback.class);

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList(
					"http://societies.org/api/schema/identity",
					"http://societies.org/api/schema/privacytrust/trust/model",
					"http://societies.org/api/schema/privacytrust/trust/broker"));
	
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList(
					"org.societies.api.schema.identity",
					"org.societies.api.schema.privacytrust.trust.model",
					"org.societies.api.schema.privacytrust.trust.broker"));
	
	private final Map<String,ITrustBrokerRemoteClientCallback> clients =
			new ConcurrentHashMap<String, ITrustBrokerRemoteClientCallback>();

	TrustBrokerRemoteClientCallback() {
		
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
		
		final ITrustBrokerRemoteClientCallback callback = this.clients.remove(stanza.getId());
		if (callback == null) {
			LOG.error("Could not find client callback for TrustBroker remote '" 
					+ responseBean.getMethodName() + "' response with stanza: "
					+ stanza);
			return;
		}
		
		if (MethodName.RETRIEVE_TRUST_RELATIONSHIPS.equals(responseBean.getMethodName())) {
			
			final TrustRelationshipsResponseBean trustRelationshipsResponseBean =
					responseBean.getRetrieveTrustRelationships();
			if (trustRelationshipsResponseBean == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust relationships response: "
						+ "TrustRelationshipsResponseBean can't be null");
				callback.onException(new TrustBrokerCommsException(
						"Invalid TrustBroker remote retrieve trust relationships response: "
						+ "TrustRelationshipsResponseBean can't be null"));
				return;
			}
			
			try {
				final Set<TrustRelationship> result = new HashSet<TrustRelationship>();
				if (trustRelationshipsResponseBean.getResult() != null)
					for (final TrustRelationshipBean trustRelationshipBean : trustRelationshipsResponseBean.getResult())
						result.add(TrustModelBeanTranslator.getInstance().
								fromTrustRelationshipBean(trustRelationshipBean));
				callback.onRetrievedTrustRelationships(result);
			} catch (Exception e) {
				LOG.error("Invalid TrustBroker remote retrieve trust relationships result: "
						+ e.getLocalizedMessage(), e);
				callback.onException(new TrustBrokerCommsException(
						"Invalid TrustBroker remote retrieve trust relationships result: "
						+ e.getLocalizedMessage(), e));
			}
			
		} else if (MethodName.RETRIEVE_TRUST_RELATIONSHIP.equals(responseBean.getMethodName())) {
			
			final TrustRelationshipResponseBean trustRelationshipResponseBean =
					responseBean.getRetrieveTrustRelationship();
			if (trustRelationshipResponseBean == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust relationship response: "
						+ "TrustRelationshipResponseBean can't be null");
				callback.onException(new TrustBrokerCommsException(
						"Invalid TrustBroker remote retrieve trust relationship response: "
						+ "TrustRelationshipResponseBean can't be null"));
				return;
			}
			
			try {
				final TrustRelationship result = TrustModelBeanTranslator.getInstance().
						fromTrustRelationshipBean(trustRelationshipResponseBean.getResult());
				callback.onRetrievedTrustRelationship(result);
			} catch (Exception e) {
				LOG.error("Invalid TrustBroker remote retrieve trust relationship result: "
						+ e.getLocalizedMessage(), e);
				callback.onException(new TrustBrokerCommsException(
						"Invalid TrustBroker remote retrieve trust relationship result: "
						+ e.getLocalizedMessage(), e));
			}
			
		} else if (MethodName.RETRIEVE_TRUST_VALUE.equals(responseBean.getMethodName())) {
			
			final TrustValueResponseBean trustValueResponseBean =
					responseBean.getRetrieveTrustValue();
			if (trustValueResponseBean == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust value response: "
						+ "TrustValueResponseBean can't be null");
				callback.onException(new TrustBrokerCommsException(
						"Invalid TrustBroker remote retrieve trust value response: "
						+ "TrustValueResponseBean can't be null"));
				return;
			}
			
			callback.onRetrievedTrustValue(trustValueResponseBean.getResult());
			
		} else {
			
			LOG.error("Unsupported TrustBroker remote response method: "
					+ responseBean.getMethodName());
			callback.onException(new TrustBrokerCommsException(
					"Unsupported TrustBroker remote response method: "
					+ responseBean.getMethodName()));
		}
	}

	/**
	 * 
	 * @param id
	 * @param callback
	 */
	void addClient(String id, ITrustBrokerRemoteClientCallback callback) {
		
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