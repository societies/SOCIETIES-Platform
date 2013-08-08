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
import org.societies.api.internal.privacytrust.trust.model.ExtTrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.schema.privacytrust.trust.broker.ExtTrustRelationshipResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.ExtTrustRelationshipsResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.MethodName;
import org.societies.api.schema.privacytrust.trust.broker.TrustBrokerResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipsRemoveResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipsResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustValueResponseBean;
import org.societies.api.schema.privacytrust.trust.model.ExtTrustRelationshipBean;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;
import org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback;
import org.societies.privacytrust.trust.impl.remote.util.TrustCommsClientTranslator;
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
		
		LOG.info("{} instantiated", this.getClass());
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
		
		if (stanza == null) {
			throw new NullPointerException("stanza can't be null");
		}
		if (error == null) {
			throw new NullPointerException("error can't be null");
		}
		
		LOG.debug("Received error: stanza={}, error={}", stanza, error);
		if (stanza.getId() == null) {
			LOG.error("Received error with null stanza id");
			return;
		}
		final ITrustBrokerRemoteClientCallback callbackClient = 
				this.removeClient(stanza.getId());
		if (callbackClient == null) {
			LOG.error("Received error with stanza id '" + stanza.getId()
					+ "' but no matching callback was found");
			return;
		}
		final String errorMessage = (error.getGenericText() != null)
				? error.getGenericText() : error.getStanzaErrorString();
		final TrustBrokerCommsException exception = 
				new TrustBrokerCommsException(errorMessage);
		callbackClient.onException(exception);
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveInfo(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.api.comm.xmpp.datatypes.XMPPInfo)
	 */
	@Override
	public void receiveInfo(Stanza stanza, String arg1, XMPPInfo arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
	 */
	@Override
	public void receiveItems(Stanza stanza, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		
		LOG.warn("Received unexpected message: staza={}, payload={}", stanza, payload);
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveResult(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveResult(final Stanza stanza, final Object payload) {
		
		if (stanza == null) {
			throw new NullPointerException("stanza can't be null");
		}
		if (payload == null) {
			throw new NullPointerException("payload can't be null");
		}
		
		LOG.debug("receiveResult: stanza={}, payload={}", stanza, payload);
		if (stanza.getId() == null) {
			LOG.error("Received result with null stanza id");
			return;
		}
		final ITrustBrokerRemoteClientCallback callback =
				this.removeClient(stanza.getId());
		if (callback == null) {
			LOG.error("Could not handle result bean: No callback client found for stanza with id: " 
					+ stanza.getId());
			return;
		}
		
		if (!(payload instanceof TrustBrokerResponseBean)) {
			callback.onException(new TrustBrokerCommsException(
					"Could not handle result bean: Unexpected type: "
					+ payload.getClass()));
			return;
		}
		
		final TrustBrokerResponseBean responseBean = (TrustBrokerResponseBean) payload;
		LOG.debug("receiveResult: responseBean.getMethodName()={}",
				responseBean.getMethodName());
		if (MethodName.RETRIEVE_TRUST_RELATIONSHIPS == responseBean.getMethodName()) {
			
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
			
		} else if (MethodName.RETRIEVE_EXT_TRUST_RELATIONSHIPS == responseBean.getMethodName()) {
			
			final ExtTrustRelationshipsResponseBean extTrustRelationshipsResponseBean =
					responseBean.getRetrieveExtTrustRelationships();
			if (extTrustRelationshipsResponseBean == null) {
				LOG.error("Invalid TrustBroker remote retrieve extended trust relationships response: "
						+ "ExtTrustRelationshipsResponseBean can't be null");
				callback.onException(new TrustBrokerCommsException(
						"Invalid TrustBroker remote retrieve extended trust relationships response: "
						+ "ExtTrustRelationshipsResponseBean can't be null"));
				return;
			}
			
			try {
				final Set<ExtTrustRelationship> result = new HashSet<ExtTrustRelationship>();
				if (extTrustRelationshipsResponseBean.getResult() != null) {
					for (final ExtTrustRelationshipBean extTrustRelationshipBean : extTrustRelationshipsResponseBean.getResult()) {
						result.add(TrustCommsClientTranslator.getInstance().
								fromExtTrustRelationshipBean(extTrustRelationshipBean));
					}
				}
				callback.onRetrievedExtTrustRelationships(result);
			} catch (Exception e) {
				LOG.error("Invalid TrustBroker remote retrieve extended trust relationships result: "
						+ e.getLocalizedMessage(), e);
				callback.onException(new TrustBrokerCommsException(
						"Invalid TrustBroker remote retrieve extended trust relationships result: "
						+ e.getLocalizedMessage(), e));
			}
			
		} else if (MethodName.RETRIEVE_TRUST_RELATIONSHIP == responseBean.getMethodName()) {
			
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
				final TrustRelationship result;
				if (trustRelationshipResponseBean.getResult() != null) {
					result = TrustModelBeanTranslator.getInstance().
						fromTrustRelationshipBean(trustRelationshipResponseBean.getResult());
				} else {
					result = null;
				}
				callback.onRetrievedTrustRelationship(result);
			} catch (Exception e) {
				LOG.error("Invalid TrustBroker remote retrieve trust relationship result: "
						+ e.getLocalizedMessage(), e);
				callback.onException(new TrustBrokerCommsException(
						"Invalid TrustBroker remote retrieve trust relationship result: "
						+ e.getLocalizedMessage(), e));
			}
			
		} else if (MethodName.RETRIEVE_EXT_TRUST_RELATIONSHIP == responseBean.getMethodName()) {

			final ExtTrustRelationshipResponseBean extTrustRelationshipResponseBean =
					responseBean.getRetrieveExtTrustRelationship();
			if (extTrustRelationshipResponseBean == null) {
				LOG.error("Invalid TrustBroker remote retrieve extended trust relationship response: "
						+ "ExtTrustRelationshipResponseBean can't be null");
				callback.onException(new TrustBrokerCommsException(
						"Invalid TrustBroker remote retrieve extended trust relationship response: "
						+ "ExtTrustRelationshipResponseBean can't be null"));
				return;
			}

			try {
				final ExtTrustRelationship result;
				if (extTrustRelationshipResponseBean.getResult() != null) {
					result = TrustCommsClientTranslator.getInstance().
							fromExtTrustRelationshipBean(extTrustRelationshipResponseBean.getResult());
				} else {
					result = null;
				}
				callback.onRetrievedExtTrustRelationship(result);
			} catch (Exception e) {
				LOG.error("Invalid TrustBroker remote retrieve extended trust relationship result: "
						+ e.getLocalizedMessage(), e);
				callback.onException(new TrustBrokerCommsException(
						"Invalid TrustBroker remote retrieve extended trust relationship result: "
								+ e.getLocalizedMessage(), e));
			}

		} else if (MethodName.RETRIEVE_TRUST_VALUE == responseBean.getMethodName()) {
			
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
			
		} else if (MethodName.REMOVE_TRUST_RELATIONSHIPS == responseBean.getMethodName()) {

			final TrustRelationshipsRemoveResponseBean removeResponseBean =
					responseBean.getRemoveTrustRelationships();
			if (removeResponseBean == null) {
				LOG.error("Invalid TrustBroker remote remove trust relationships response: "
						+ "TrustRelationshipsRemoveResponseBean can't be null");
				callback.onException(new TrustBrokerCommsException(
						"Invalid TrustBroker remote remove trust relationships response: "
								+ "TrustRelationshipsRemoveResponseBean can't be null"));
				return;
			}

			try {
				// This conversion can throw a NPE but we'll catch it 
				callback.onRemovedTrustRelationships(removeResponseBean.isQueryMatched());
			} catch (Exception e) {
				LOG.error("Invalid TrustBroker remote remove trust relationships result: "
						+ e.getLocalizedMessage(), e);
				callback.onException(new TrustBrokerCommsException(
						"Invalid TrustBroker remote retrieve extended trust relationships result: "
								+ e.getLocalizedMessage(), e));
			}
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
	void addClient(String id, ITrustBrokerRemoteClientCallback callback) {
		
		this.clients.put(id, callback);
	}
	
	/**
	 * 
	 * @param id
	 * @return 
	 */
	ITrustBrokerRemoteClientCallback removeClient(String id) {
		
		return this.clients.remove(id);
	}
}