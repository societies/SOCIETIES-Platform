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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.trust.model.ExtTrustRelationship;
import org.societies.api.schema.privacytrust.trust.broker.ExtTrustRelationshipRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.ExtTrustRelationshipResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.ExtTrustRelationshipsRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.ExtTrustRelationshipsResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.MethodName;
import org.societies.api.schema.privacytrust.trust.broker.TrustBrokerRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustBrokerResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipsRemoveRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipsRemoveResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipsRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipsResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustValueRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustValueResponseBean;
import org.societies.api.schema.privacytrust.trust.model.ExtTrustRelationshipBean;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;
import org.societies.api.privacytrust.trust.ITrustBroker;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.privacytrust.trust.impl.remote.util.TrustCommsClientTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
@Service
public class TrustBrokerRemoteServer implements IFeatureServer {
	
	/** The logging facility. */
	private static Logger LOG = LoggerFactory.getLogger(TrustBrokerRemoteServer.class);
	
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
	
	/** The Trust Broker service reference. */
	@Autowired(required=true)
	private ITrustBroker trustBroker;
	
	/** The internal Trust Broker service reference. */
	@Autowired(required=true)
	private org.societies.api.internal.privacytrust.trust.ITrustBroker internalTrustBroker;
	
	/** The Communications Mgr service reference. */
	@Autowired(required=true)
	private ICommManager commManager;
	
	TrustBrokerRemoteServer() {
		
		LOG.info("{} instantiated", this.getClass());
	}
	
	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		
		return PACKAGES;
	}
	
	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		
		return NAMESPACES;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		
		if (stanza == null)
			throw new NullPointerException("stanza can't be null");
		if (payload == null)
			throw new NullPointerException("payload can't be null");
		
		LOG.debug("getQuery: stanza={}, payload={}", stanza, payload); 
		
		if (!(payload instanceof TrustBrokerRequestBean)) {
			throw new XMPPError(StanzaError.bad_request, "Unknown request bean class: "
					+ payload.getClass());
		}
		
		final TrustBrokerRequestBean requestBean = (TrustBrokerRequestBean) payload;
		
		LOG.debug("getQuery: requestBean.getMethodName()={}", requestBean.getMethodName());
		if (MethodName.RETRIEVE_TRUST_RELATIONSHIPS.equals(requestBean.getMethodName())) {
			
			final TrustRelationshipsRequestBean trustRelationshipsRequestBean =
					requestBean.getRetrieveTrustRelationships();
			if (trustRelationshipsRequestBean == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust relationships request: "
						+ "TrustRelationshipsRequestBean can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve trust relationships request: "
						+ "TrustRelationshipsRequestBean can't be null");
			}
			return this.handleRequest(trustRelationshipsRequestBean);
		
		} else if (MethodName.RETRIEVE_EXT_TRUST_RELATIONSHIPS.equals(requestBean.getMethodName())) {
			
			final ExtTrustRelationshipsRequestBean extTrustRelationshipsRequestBean =
					requestBean.getRetrieveExtTrustRelationships();
			if (extTrustRelationshipsRequestBean == null) {
				LOG.error("Invalid TrustBroker remote retrieve extended trust relationships request: "
						+ "ExtTrustRelationshipsRequestBean can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve extended trust relationships request: "
						+ "ExtTrustRelationshipsRequestBean can't be null");
			}
			if (!this.commManager.getIdManager().isMine(stanza.getFrom())) {
				LOG.error("Invalid TrustBroker remote retrieve extended trust relationships request: "
						+ "stanza source '" + stanza.getFrom() + "' is not recognised as a local CSS");
				throw new XMPPError(StanzaError.not_authorized, 
						"Invalid TrustBroker remote retrieve extended trust relationships request: "
						+ "stanza source '" + stanza.getFrom() + "' is not recognised as a local CSS");
			}
			return this.handleRequest(extTrustRelationshipsRequestBean);
			
		} else if (MethodName.RETRIEVE_TRUST_RELATIONSHIP.equals(requestBean.getMethodName())) {
			
			final TrustRelationshipRequestBean trustRelationshipRequestBean =
					requestBean.getRetrieveTrustRelationship();
			if (trustRelationshipRequestBean == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust relationship request: "
						+ "TrustRelationshipRequestBean can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve trust relationship request: "
						+ "TrustRelationshipRequestBean can't be null");
			}
			return this.handleRequest(trustRelationshipRequestBean);
			
		} else if (MethodName.RETRIEVE_EXT_TRUST_RELATIONSHIP.equals(requestBean.getMethodName())) {
			
			final ExtTrustRelationshipRequestBean extTrustRelationshipRequestBean =
					requestBean.getRetrieveExtTrustRelationship();
			if (extTrustRelationshipRequestBean == null) {
				LOG.error("Invalid TrustBroker remote retrieve extended trust relationship request: "
						+ "ExtTrustRelationshipRequestBean can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve extended trust relationship request: "
						+ "ExtTrustRelationshipRequestBean can't be null");
			}
			if (!this.commManager.getIdManager().isMine(stanza.getFrom())) {
				LOG.error("Invalid TrustBroker remote retrieve extended trust relationship request: "
						+ "stanza source '" + stanza.getFrom() + "' is not recognised as a local CSS");
				throw new XMPPError(StanzaError.not_authorized, 
						"Invalid TrustBroker remote retrieve extended trust relationship request: "
						+ "stanza source '" + stanza.getFrom() + "' is not recognised as a local CSS");
			}
			return this.handleRequest(extTrustRelationshipRequestBean);
		
		} else if (MethodName.RETRIEVE_TRUST_VALUE.equals(requestBean.getMethodName())) {
			
			final TrustValueRequestBean trustValueRequestBean =
					requestBean.getRetrieveTrustValue();
			if (trustValueRequestBean == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust value request: "
						+ "TrustValueRequestBean can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve trust value request: "
						+ "TrustValueRequestBean can't be null");
			}
			return this.handleRequest(trustValueRequestBean);
			
		} else if (MethodName.REMOVE_TRUST_RELATIONSHIPS.equals(requestBean.getMethodName())) {

			final TrustRelationshipsRemoveRequestBean removeRequestBean =
					requestBean.getRemoveTrustRelationships();
			if (removeRequestBean == null) {
				LOG.error("Invalid TrustBroker remote remove trust relationships request: "
						+ "TrustRelationshipsRemoveRequestBean can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote remove trust relationships request: "
								+ "TrustRelationshipsRemoveRequestBean can't be null");
			}
			if (!this.commManager.getIdManager().isMine(stanza.getFrom())) {
				LOG.error("Invalid TrustBroker remote remove trust relationships request: "
						+ "stanza source '" + stanza.getFrom() + "' is not recognised as a local CSS");
				throw new XMPPError(StanzaError.not_authorized, 
						"Invalid TrustBroker remote remove trust relationships request: "
								+ "stanza source '" + stanza.getFrom() + "' is not recognised as a local CSS");
			}
			return this.handleRequest(removeRequestBean);
			
		} else {
			LOG.error("Unsupported TrustBroker remote request method: "
					+ requestBean.getMethodName());
			throw new XMPPError(StanzaError.unexpected_request, 
					"Unsupported TrustBroker remote request method: "
					+ requestBean.getMethodName());
		}
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		
		LOG.warn("Received unexpected message: staza={}, payload={}", stanza, payload);
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#setQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza stanza, Object payload) throws XMPPError {
		
		LOG.warn("Received unexpected setQuery request: staza={}, payload={}", stanza, payload);
		
		return null;
	}
	
	private TrustBrokerResponseBean handleRequest(
			TrustRelationshipsRequestBean requestBean) throws XMPPError {
		
		final TrustBrokerResponseBean responseBean = new TrustBrokerResponseBean();
		responseBean.setMethodName(MethodName.RETRIEVE_TRUST_RELATIONSHIPS);
		
		if (requestBean.getRequestor() == null) {
			LOG.error("Invalid TrustBroker remote retrieve trust relationships request: "
					+ "requestor can't be null");
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote retrieve trust relationships request: "
					+ "requestor can't be null");
		}
		if (requestBean.getQuery() == null) {
			LOG.error("Invalid TrustBroker remote retrieve trust relationships request: "
					+ "query can't be null");
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote retrieve trust relationships request: "
					+ "query can't be null");
		}
		
		try {
			// (required) requestor
			final Requestor requestor = RequestorUtils.toRequestor(
					requestBean.getRequestor(), this.commManager.getIdManager());
			// (required) query
			final TrustQuery query = TrustCommsClientTranslator.getInstance().
					fromTrustQueryBean(requestBean.getQuery());
			
			LOG.debug("handleRequest: requestor={}, query={}", requestor, query);
			final Set<TrustRelationship> result = this.trustBroker.retrieveTrustRelationships(
					requestor, query).get();
			
			final TrustRelationshipsResponseBean trustRelationshipsResponseBean = 
					new TrustRelationshipsResponseBean();
			final List<TrustRelationshipBean> resultBean = new ArrayList<TrustRelationshipBean>();
			for (final TrustRelationship trustRelationship : result) {
				resultBean.add(TrustModelBeanTranslator.getInstance().
						fromTrustRelationship(trustRelationship));
			}
			trustRelationshipsResponseBean.setResult(resultBean);
			responseBean.setRetrieveTrustRelationships(trustRelationshipsResponseBean);
			
		} catch (MalformedTrustedEntityIdException mteide) {
			
			LOG.error("Invalid TrustBroker remote retrieve trust relationships request: "
					+ mteide.getLocalizedMessage(), mteide);
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote retrieve trust relationships request: "
					+ mteide.getLocalizedMessage());
			
		} catch (Exception e) {
			
			LOG.error("Could not retrieve trust relationships: "
					+ e.getLocalizedMessage(), e);
			throw new XMPPError(StanzaError.internal_server_error, 
					"Could not retrieve trust relationships: "
					+ e.getLocalizedMessage());
		} 
		
		return responseBean;
	}
	
	private TrustBrokerResponseBean handleRequest(
			ExtTrustRelationshipsRequestBean requestBean) throws XMPPError {
		
		final TrustBrokerResponseBean responseBean = new TrustBrokerResponseBean();
		responseBean.setMethodName(MethodName.RETRIEVE_EXT_TRUST_RELATIONSHIPS);
		
		if (requestBean.getQuery() == null) {
			LOG.error("Invalid TrustBroker remote retrieve extended trust relationships request: "
					+ "query can't be null");
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote retrieve extended trust relationships request: "
					+ "query can't be null");
		}
		
		try {
			// (required) query
			final TrustQuery query = TrustCommsClientTranslator.getInstance().
					fromTrustQueryBean(requestBean.getQuery());
			
			LOG.debug("handleRequest: query={}", query);
			final Set<ExtTrustRelationship> result = this.internalTrustBroker
					.retrieveExtTrustRelationships(query).get();
			
			final ExtTrustRelationshipsResponseBean extTrustRelationshipsResponseBean = 
					new ExtTrustRelationshipsResponseBean();
			final List<ExtTrustRelationshipBean> resultBean = new ArrayList<ExtTrustRelationshipBean>();
			for (final ExtTrustRelationship extTrustRelationship : result) {
				resultBean.add(TrustCommsClientTranslator.getInstance().
						fromExtTrustRelationship(extTrustRelationship));
			}
			extTrustRelationshipsResponseBean.setResult(resultBean);
			responseBean.setRetrieveExtTrustRelationships(extTrustRelationshipsResponseBean);
			
		} catch (MalformedTrustedEntityIdException mteide) {
			
			LOG.error("Invalid TrustBroker remote retrieve extended trust relationships request: "
					+ mteide.getLocalizedMessage(), mteide);
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote retrieve extended trust relationships request: "
					+ mteide.getLocalizedMessage());
			
		} catch (Exception e) {
			
			LOG.error("Could not retrieve extended trust relationships: "
					+ e.getLocalizedMessage(), e);
			throw new XMPPError(StanzaError.internal_server_error, 
					"Could not retrieve extended trust relationships: "
					+ e.getLocalizedMessage());
		} 
		
		return responseBean;
	}
	
	private TrustBrokerResponseBean handleRequest(
			TrustRelationshipRequestBean requestBean) throws XMPPError {
		
		final TrustBrokerResponseBean responseBean = new TrustBrokerResponseBean();
		responseBean.setMethodName(MethodName.RETRIEVE_TRUST_RELATIONSHIP);
		
		if (requestBean.getRequestor() == null) {
			LOG.error("Invalid TrustBroker remote retrieve trust relationship request: "
					+ "requestor can't be null");
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote retrieve trust relationship request: "
					+ "requestor can't be null");
		}
		if (requestBean.getQuery() == null) {
			LOG.error("Invalid TrustBroker remote retrieve trust relationship request: "
					+ "query can't be null");
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote retrieve trust relationship request: "
					+ "query can't be null");
		}
		
		try {
			// (required) requestor
			final Requestor requestor = RequestorUtils.toRequestor(
					requestBean.getRequestor(), this.commManager.getIdManager());
			// (required) query
			final TrustQuery query = TrustCommsClientTranslator.getInstance().
					fromTrustQueryBean(requestBean.getQuery());
			
			LOG.debug("handleRequest: requestor={}, query={}", requestor, query);
			final TrustRelationship result = this.trustBroker.retrieveTrustRelationship(
					requestor, query).get();
			
			final TrustRelationshipResponseBean trustRelationshipResponseBean = 
					new TrustRelationshipResponseBean(); 
			if (result != null) {
				trustRelationshipResponseBean.setResult(
						TrustModelBeanTranslator.getInstance().fromTrustRelationship(result));
			}
			responseBean.setRetrieveTrustRelationship(trustRelationshipResponseBean);
			
		} catch (MalformedTrustedEntityIdException mteide) {
			
			LOG.error("Invalid TrustBroker remote retrieve trust relationship request: "
					+ mteide.getLocalizedMessage(), mteide);
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote retrieve trust relationship request: "
					+ mteide.getLocalizedMessage());
		} catch (Exception e) {
			
			LOG.error("Could not retrieve trust relationship: "
					+ e.getLocalizedMessage(), e);
			throw new XMPPError(StanzaError.internal_server_error, 
					"Could not retrieve trust relationship: "
					+ e.getLocalizedMessage());
		}
		
		return responseBean;
	}
	
	private TrustBrokerResponseBean handleRequest(
			ExtTrustRelationshipRequestBean requestBean) throws XMPPError {
		
		final TrustBrokerResponseBean responseBean = new TrustBrokerResponseBean();
		responseBean.setMethodName(MethodName.RETRIEVE_EXT_TRUST_RELATIONSHIP);
		
		if (requestBean.getQuery() == null) {
			LOG.error("Invalid TrustBroker remote retrieve extended trust relationship request: "
					+ "query can't be null");
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote retrieve extended trust relationship request: "
					+ "query can't be null");
		}
		
		try {
			// (required) query
			final TrustQuery query = TrustCommsClientTranslator.getInstance().
					fromTrustQueryBean(requestBean.getQuery());
			
			LOG.debug("handleRequest: query={}", query);
			final ExtTrustRelationship result = this.internalTrustBroker
					.retrieveExtTrustRelationship(query).get();
			
			final ExtTrustRelationshipResponseBean extTrustRelationshipResponseBean = 
					new ExtTrustRelationshipResponseBean(); 
			if (result != null) {
				extTrustRelationshipResponseBean.setResult(
						TrustCommsClientTranslator.getInstance().fromExtTrustRelationship(result));
			}
			responseBean.setRetrieveExtTrustRelationship(extTrustRelationshipResponseBean);
			
		} catch (MalformedTrustedEntityIdException mteide) {
			
			LOG.error("Invalid TrustBroker remote retrieve exteded trust relationship request: "
					+ mteide.getLocalizedMessage(), mteide);
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote retrieve extended trust relationship request: "
					+ mteide.getLocalizedMessage());
		} catch (Exception e) {
			
			LOG.error("Could not retrieve extended trust relationship: "
					+ e.getLocalizedMessage(), e);
			throw new XMPPError(StanzaError.internal_server_error, 
					"Could not retrieve extended trust relationship: "
					+ e.getLocalizedMessage());
		}
		
		return responseBean;
	}
	
	private TrustBrokerResponseBean handleRequest(
			TrustValueRequestBean requestBean) throws XMPPError {
		
		final TrustBrokerResponseBean responseBean = new TrustBrokerResponseBean();
		responseBean.setMethodName(MethodName.RETRIEVE_TRUST_VALUE);
		
		if (requestBean.getRequestor() == null) {
			LOG.error("Invalid TrustBroker remote retrieve trust value request: "
					+ "requestor can't be null");
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote retrieve trust value request: "
					+ "requestor can't be null");
		}
		if (requestBean.getQuery() == null) {
			LOG.error("Invalid TrustBroker remote retrieve trust value request: "
					+ "query can't be null");
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote retrieve trust value request: "
					+ "query can't be null");
		}
		
		try {
			// (required) requestor
			final Requestor requestor = RequestorUtils.toRequestor(
					requestBean.getRequestor(), this.commManager.getIdManager());
			// (required) query
			final TrustQuery query = TrustCommsClientTranslator.getInstance().
					fromTrustQueryBean(requestBean.getQuery());
			
			LOG.debug("handleRequest: requestor={}, query={}", requestor, query);
			final Double result = this.trustBroker.retrieveTrustValue(
					requestor,query).get();
			
			final TrustValueResponseBean trustValueResponseBean = 
					new TrustValueResponseBean(); 
			if (result != null) {
				trustValueResponseBean.setResult(result);
			}
			responseBean.setRetrieveTrustValue(trustValueResponseBean);
			
		} catch (MalformedTrustedEntityIdException mteide) {
			
			LOG.error("Invalid TrustBroker remote retrieve trust value request: "
					+ mteide.getLocalizedMessage(), mteide);
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote retrieve trust value request: "
					+ mteide.getLocalizedMessage());
		} catch (Exception e) {
			
			LOG.error("Could not retrieve trust value: "
					+ e.getLocalizedMessage(), e);
			throw new XMPPError(StanzaError.internal_server_error, 
					"Could not retrieve trust value: "
					+ e.getLocalizedMessage());
		}
		
		return responseBean;
	}
	
	private TrustBrokerResponseBean handleRequest(
			TrustRelationshipsRemoveRequestBean requestBean) throws XMPPError {
		
		final TrustBrokerResponseBean responseBean = new TrustBrokerResponseBean();
		responseBean.setMethodName(MethodName.REMOVE_TRUST_RELATIONSHIPS);
		
		if (requestBean.getQuery() == null) {
			LOG.error("Invalid TrustBroker remote remove trust relationships request: "
					+ "query can't be null");
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote remove trust relationships request: "
					+ "query can't be null");
		}
		
		try {
			// (required) query
			final TrustQuery query = TrustCommsClientTranslator.getInstance().
					fromTrustQueryBean(requestBean.getQuery());
			
			LOG.debug("handleRequest: query={}", query);
			final boolean result = this.internalTrustBroker
					.removeTrustRelationships(query).get();
			
			final TrustRelationshipsRemoveResponseBean removeResponseBean = 
					new TrustRelationshipsRemoveResponseBean(); 
			removeResponseBean.setQueryMatched(result);
			responseBean.setRemoveTrustRelationships(removeResponseBean);
			
		} catch (MalformedTrustedEntityIdException mteide) {
			
			LOG.error("Invalid TrustBroker remote remove trust relationships request: "
					+ mteide.getLocalizedMessage(), mteide);
			throw new XMPPError(StanzaError.bad_request, 
					"Invalid TrustBroker remote remove trust relationships request: "
					+ mteide.getLocalizedMessage());
		} catch (Exception e) {
			
			LOG.error("Could not remove trust relationships: "
					+ e.getLocalizedMessage(), e);
			throw new XMPPError(StanzaError.internal_server_error, 
					"Could not remove trust relationships: "
					+ e.getLocalizedMessage());
		}
		
		return responseBean;
	}
}