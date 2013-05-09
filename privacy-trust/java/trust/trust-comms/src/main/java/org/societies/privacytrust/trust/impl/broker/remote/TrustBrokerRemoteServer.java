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
import org.societies.api.schema.privacytrust.trust.broker.MethodName;
import org.societies.api.schema.privacytrust.trust.broker.TrustBrokerRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustBrokerResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipsRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipsResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustValueRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustValueResponseBean;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;
import org.societies.api.privacytrust.trust.ITrustBroker;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
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
	
	/** The Communications Mgr service reference. */
	@Autowired(required=true)
	private ICommManager commManager;
	
	TrustBrokerRemoteServer() {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
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
		
		if (LOG.isDebugEnabled())
			LOG.debug("getQuery: stanza=" + stanza + ", payload=" + payload);
		
		if (!(payload instanceof TrustBrokerRequestBean))
			throw new XMPPError(StanzaError.bad_request, "Unknown request bean class: "
					+ payload.getClass());
		
		final TrustBrokerRequestBean requestBean = (TrustBrokerRequestBean) payload;
		final TrustBrokerResponseBean responseBean = new TrustBrokerResponseBean();
		responseBean.setMethodName(requestBean.getMethodName());
		
		if (LOG.isDebugEnabled())
			LOG.debug("getQuery: requestBean.getMethodName()=" + requestBean.getMethodName());
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
			if (trustRelationshipsRequestBean.getRequestor() == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust relationships request: "
						+ "requestor can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve trust relationships request: "
						+ "requestor can't be null");
			}
			if (trustRelationshipsRequestBean.getTrustorId() == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust relationships request: "
						+ "trustorId can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve trust relationships request: "
						+ "trustorId can't be null");
			}
			
			try {
				final Requestor requestor = RequestorUtils.toRequestor(
						trustRelationshipsRequestBean.getRequestor(), this.commManager.getIdManager());
				final TrustedEntityId trustorId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(trustRelationshipsRequestBean.getTrustorId());
				final Set<TrustRelationship> result; 
				if (trustRelationshipsRequestBean.getTrusteeId() == null
						&& trustRelationshipsRequestBean.getTrusteeType() == null
						&& trustRelationshipsRequestBean.getTrustValueType() == null) {
					
					if (LOG.isDebugEnabled())
						LOG.debug("getQuery: requestor=" + requestor + ", trustorId=" + trustorId);
					result = this.trustBroker.retrieveTrustRelationships(
							requestor, trustorId).get();
					
				} else if (trustRelationshipsRequestBean.getTrusteeId() != null) {
				
					final TrustedEntityId trusteeId = TrustModelBeanTranslator.getInstance().
							fromTrustedEntityIdBean(trustRelationshipsRequestBean.getTrusteeId());
					if (LOG.isDebugEnabled())
						LOG.debug("getQuery: requestor=" + requestor + ", trustorId=" + trustorId + ", trusteeId=" + trusteeId);
					result = this.trustBroker.retrieveTrustRelationships(
							requestor, trustorId, trusteeId).get();
					
				} else if (trustRelationshipsRequestBean.getTrusteeType() != null) {
				
					final TrustedEntityType trusteeType = TrustModelBeanTranslator.getInstance().
							fromTrustedEntityTypeBean(trustRelationshipsRequestBean.getTrusteeType());
					if (trustRelationshipsRequestBean.getTrustValueType() != null) {
				
						final TrustValueType trustValueType = TrustModelBeanTranslator.getInstance().
								fromTrustValueTypeBean(trustRelationshipsRequestBean.getTrustValueType());
						if (LOG.isDebugEnabled())
							LOG.debug("getQuery: requestor=" + requestor + ", trustorId=" + trustorId + ", trusteeType=" + trusteeType + ", trustValueType=" + trustValueType);
						result = this.trustBroker.retrieveTrustRelationships(
								requestor, trustorId, trusteeType, trustValueType).get();
						
					} else { // if (trustRelationshipsRequestBean.getTrustValueType() == null)
				
						if (LOG.isDebugEnabled())
							LOG.debug("getQuery: requestor=" + requestor + ", trustorId=" + trustorId + ", trusteeType=" + trusteeType);
						result = this.trustBroker.retrieveTrustRelationships(
								requestor, trustorId, trusteeType).get();
					}
				} else if (trustRelationshipsRequestBean.getTrustValueType() != null) {

					final TrustValueType trustValueType = TrustModelBeanTranslator.getInstance().
							fromTrustValueTypeBean(trustRelationshipsRequestBean.getTrustValueType());
					if (LOG.isDebugEnabled())
							LOG.debug("getQuery: requestor=" + requestor + ", trustorId=" + trustorId + ", trustValueType=" + trustValueType);
					result = this.trustBroker.retrieveTrustRelationships(
							requestor, trustorId, trustValueType).get();
				} else {
					LOG.error("Invalid TrustBroker remote retrieve trust relationships request: "
							+ "Missing parameters");
					throw new XMPPError(StanzaError.bad_request, 
							"Invalid TrustBroker remote retrieve trust relationships request: "
							+ "Missing parameters");
				}
				
				final TrustRelationshipsResponseBean trustRelationshipsResponseBean = 
						new TrustRelationshipsResponseBean();
				final List<TrustRelationshipBean> resultBean = new ArrayList<TrustRelationshipBean>();
				for (final TrustRelationship trustRelationship : result)
					resultBean.add(TrustModelBeanTranslator.getInstance().
							fromTrustRelationship(trustRelationship));
				trustRelationshipsResponseBean.setResult(resultBean);
				responseBean.setRetrieveTrustRelationships(trustRelationshipsResponseBean);
				
			} catch (MalformedTrustedEntityIdException mteide) {
				
				LOG.error("Invalid TrustBroker remote retrieve trust relationships request: "
						+ mteide.getLocalizedMessage(), mteide);
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve trust relationships request: "
						+ mteide.getLocalizedMessage());
			} catch (XMPPError xmppe) {
				
				throw xmppe;
			} catch (Exception e) {
				
				LOG.error("Could not retrieve trust relationships: "
						+ e.getLocalizedMessage(), e);
				throw new XMPPError(StanzaError.internal_server_error, 
						"Could not retrieve trust relationships: "
						+ e.getLocalizedMessage());
			} 
		
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
			if (trustRelationshipRequestBean.getRequestor() == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust relationship request: "
						+ "requestor can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve trust relationship request: "
						+ "requestor can't be null");
			}
			if (trustRelationshipRequestBean.getTrustorId() == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust relationship request: "
						+ "trustorId can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve trust relationship request: "
						+ "trustorId can't be null");
			}
			if (trustRelationshipRequestBean.getTrusteeId() == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust relationship request: "
						+ "trusteeId can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve trust relationship request: "
						+ "trusteeId can't be null");
			}
			if (trustRelationshipRequestBean.getTrustValueType() == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust relationship request: "
						+ "trustValueType can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve trust relationship request: "
						+ "trustValueType can't be null");
			}
			
			try {
				final Requestor requestor = RequestorUtils.toRequestor(
						trustRelationshipRequestBean.getRequestor(), this.commManager.getIdManager());
				final TrustedEntityId trustorId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(trustRelationshipRequestBean.getTrustorId());
				final TrustedEntityId trusteeId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(trustRelationshipRequestBean.getTrusteeId());
				final TrustValueType trustValueType = TrustModelBeanTranslator.getInstance().
						fromTrustValueTypeBean(trustRelationshipRequestBean.getTrustValueType());
				if (LOG.isDebugEnabled())
					LOG.debug("getQuery: requestor=" + requestor + ", trustorId=" + trustorId + ", trusteeId=" + trusteeId + ", trustValueType=" + trustValueType);
				final TrustRelationship result = this.trustBroker.retrieveTrustRelationship(
						requestor, trustorId, trusteeId, trustValueType).get();
				
				final TrustRelationshipResponseBean trustRelationshipResponseBean = 
						new TrustRelationshipResponseBean(); 
				if (result != null)
					trustRelationshipResponseBean.setResult(
							TrustModelBeanTranslator.getInstance().fromTrustRelationship(result));
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
			if (trustValueRequestBean.getRequestor() == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust value request: "
						+ "requestor can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve trust value request: "
						+ "requestor can't be null");
			}
			if (trustValueRequestBean.getTrustorId() == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust value request: "
						+ "trustorId can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve trust value request: "
						+ "trustorId can't be null");
			}
			if (trustValueRequestBean.getTrusteeId() == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust value request: "
						+ "trusteeId can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve trust value request: "
						+ "trusteeId can't be null");
			}
			if (trustValueRequestBean.getTrustValueType() == null) {
				LOG.error("Invalid TrustBroker remote retrieve trust value request: "
						+ "trustValueType can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustBroker remote retrieve trust value request: "
						+ "trustValueType can't be null");
			}
			
			try {
				final Requestor requestor = RequestorUtils.toRequestor(
						trustValueRequestBean.getRequestor(), this.commManager.getIdManager());
				final TrustedEntityId trustorId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(trustValueRequestBean.getTrustorId());
				final TrustedEntityId trusteeId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(trustValueRequestBean.getTrusteeId());
				final TrustValueType trustValueType = TrustModelBeanTranslator.getInstance().
						fromTrustValueTypeBean(trustValueRequestBean.getTrustValueType());
				final Double result = this.trustBroker.retrieveTrustValue(
						requestor, trustorId, trusteeId, trustValueType).get();
				
				final TrustValueResponseBean trustValueResponseBean = 
						new TrustValueResponseBean(); 
				if (result != null)
					trustValueResponseBean.setResult(result);
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
			
		} else {
			LOG.error("Unsupported TrustBroker remote request method: "
					+ requestBean.getMethodName());
			throw new XMPPError(StanzaError.unexpected_request, 
					"Unsupported TrustBroker remote request method: "
					+ requestBean.getMethodName());
		}
		
		return responseBean;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#setQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza stanza, Object payload) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}
}