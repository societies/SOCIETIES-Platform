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

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.schema.privacytrust.trust.evidence.collector.AddDirectEvidenceRequestBean;
import org.societies.api.schema.privacytrust.trust.evidence.collector.AddIndirectEvidenceRequestBean;
import org.societies.api.schema.privacytrust.trust.evidence.collector.MethodName;
import org.societies.api.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorRequestBean;
import org.societies.api.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorResponseBean;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.impl.remote.util.TrustCommsClientTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
@Service
public class TrustEvidenceCollectorRemoteServer implements IFeatureServer {
	
	/** The logging facility. */
	private static Logger LOG = LoggerFactory.getLogger(TrustEvidenceCollectorRemoteServer.class);
	
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList(
					"http://societies.org/api/schema/identity",
					"http://societies.org/api/schema/privacytrust/trust/model",
					"http://societies.org/api/schema/privacytrust/trust/evidence/collector"));
	
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList(
					"org.societies.api.schema.identity",
					"org.societies.api.schema.privacytrust.trust.model",
					"org.societies.api.schema.privacytrust.trust.evidence.collector"));
	
	/** The Trust Evidence Collector service reference. */
	@Autowired(required=true)
	private ITrustEvidenceCollector trustEvidenceCollector;
	
	/** The Communications Mgr service reference. */
	@Autowired(required=true)
	private ICommManager commManager;
	
	TrustEvidenceCollectorRemoteServer() {
		
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
		
		if (!(payload instanceof TrustEvidenceCollectorRequestBean))
			throw new XMPPError(StanzaError.bad_request, "Unknown request bean class: "
					+ payload.getClass());
		
		final TrustEvidenceCollectorRequestBean requestBean = 
				(TrustEvidenceCollectorRequestBean) payload;
		final TrustEvidenceCollectorResponseBean responseBean = 
				new TrustEvidenceCollectorResponseBean();
		responseBean.setMethodName(requestBean.getMethodName());
		
		if (LOG.isDebugEnabled())
			LOG.debug("getQuery: requestBean.getMethodName()="
					+ requestBean.getMethodName());
		if (MethodName.ADD_DIRECT_EVIDENCE.equals(requestBean.getMethodName())) {
			
			final AddDirectEvidenceRequestBean addEvidenceRequestBean =
					requestBean.getAddDirectEvidence();
			if (addEvidenceRequestBean == null) {
				LOG.error("Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "AddDirectEvidenceRequestBean can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "AddDirectEvidenceRequestBean can't be null");
			}
			if (addEvidenceRequestBean.getRequestor() == null) {
				LOG.error("Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "requestor can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "requestor can't be null");
			}
			if (addEvidenceRequestBean.getSubjectId() == null) {
				LOG.error("Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "subject can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "subject can't be null");
			}
			if (addEvidenceRequestBean.getObjectId() == null) {
				LOG.error("Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "object can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "object can't be null");
			}
			if (addEvidenceRequestBean.getType() == null) {
				LOG.error("Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "type can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "type can't be null");
			}
			if (addEvidenceRequestBean.getTimestamp() == null) {
				LOG.error("Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "timestamp can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "timestamp can't be null");
			}
			
			try {
				// 1. requestor
				final Requestor requestor = RequestorUtils.toRequestor(
						addEvidenceRequestBean.getRequestor(), this.commManager.getIdManager());
				// 2. subjectId
				final TrustedEntityId subjectId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(addEvidenceRequestBean.getSubjectId());
				// 3. objectId
				final TrustedEntityId objectId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(addEvidenceRequestBean.getObjectId());
				// 4. type
				final TrustEvidenceType type = TrustModelBeanTranslator.getInstance().
						fromTrustEvidenceTypeBean(addEvidenceRequestBean.getType());
				// 5. timestamp
				final Date timestamp = addEvidenceRequestBean.getTimestamp();
				// 6. info
				final Serializable info;
				if (addEvidenceRequestBean.getInfo() != null)
					info = TrustCommsClientTranslator.getInstance().deserialise(
							addEvidenceRequestBean.getInfo(), this.getClass().getClassLoader());
				else
					info = null;
			
				if (LOG.isDebugEnabled())
					LOG.debug("addDirectTrustEvidence(requestor=" + requestor
							+ ", subjectId=" + subjectId + ", objectId=" + objectId 
							+ ", type=" + type + ", timestamp=" + timestamp	
							+ ", info=" + info + ")");
				this.trustEvidenceCollector.addDirectEvidence(requestor, 
						subjectId, objectId, type, timestamp, info);
			
			} catch (InvalidFormatException ife) {
				LOG.error("Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "Invalid requestor: " + ife.getLocalizedMessage(), ife);
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addDirectEvidence request: "
								+ "Invalid requestor: " + ife.getLocalizedMessage());
			} catch (MalformedTrustedEntityIdException mteide) {
				LOG.error("Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "Invalid teid: " + mteide.getLocalizedMessage(), mteide);
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "Invalid teid: " + mteide.getLocalizedMessage());
			} catch (IOException ioe) {
				LOG.error("Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "Could not deserialise info: " + ioe.getLocalizedMessage(), ioe);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "Could not deserialise info: " + ioe.getLocalizedMessage());
			} catch (ClassNotFoundException cnfe) {
				LOG.error("Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "Could not deserialise info: " + cnfe.getLocalizedMessage(), cnfe);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addDirectEvidence request: "
						+ "Could not deserialise info: " + cnfe.getLocalizedMessage());
			} catch (TrustException te) {
				LOG.error("Failed to add direct trust evidence: "
						+ te.getLocalizedMessage(), te);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, 
						te.getLocalizedMessage());
			}
			
		} else if (MethodName.ADD_INDIRECT_EVIDENCE.equals(requestBean.getMethodName())) {
			
			final AddIndirectEvidenceRequestBean addEvidenceRequestBean =
					requestBean.getAddIndirectEvidence();
			if (addEvidenceRequestBean == null) {
				LOG.error("Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "AddDirectEvidenceRequestBean can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "AddDirectEvidenceRequestBean can't be null");
			}
			if (addEvidenceRequestBean.getRequestor() == null) {
				LOG.error("Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "requestor can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "requestor can't be null");
			}
			if (addEvidenceRequestBean.getSubjectId() == null) {
				LOG.error("Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "subject can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "subject can't be null");
			}
			if (addEvidenceRequestBean.getObjectId() == null) {
				LOG.error("Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "object can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "object can't be null");
			}
			if (addEvidenceRequestBean.getType() == null) {
				LOG.error("Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "type can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "type can't be null");
			}
			if (addEvidenceRequestBean.getTimestamp() == null) {
				LOG.error("Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "timestamp can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "timestamp can't be null");
			}
			if (addEvidenceRequestBean.getSourceId() == null) {
				LOG.error("Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "source can't be null");
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "source can't be null");
			}
			
			try {
				// 1. requestor
				final Requestor requestor = RequestorUtils.toRequestor(
						addEvidenceRequestBean.getRequestor(), this.commManager.getIdManager());
				// 2. subjectId
				final TrustedEntityId subjectId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(addEvidenceRequestBean.getSubjectId()); 
				// 3. objectId
				final TrustedEntityId objectId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(addEvidenceRequestBean.getObjectId());
				// 4. type
				final TrustEvidenceType type = TrustModelBeanTranslator.getInstance().
						fromTrustEvidenceTypeBean(addEvidenceRequestBean.getType());
				// 5. timestamp
				final Date timestamp = addEvidenceRequestBean.getTimestamp();
				// 6. info
				final Serializable info;
				if (addEvidenceRequestBean.getInfo() != null)
					info = TrustCommsClientTranslator.getInstance().deserialise(
							addEvidenceRequestBean.getInfo(), this.getClass().getClassLoader());
				else
					info = null;
				// 7. sourceId
				final TrustedEntityId sourceId = TrustModelBeanTranslator.getInstance().
						fromTrustedEntityIdBean(addEvidenceRequestBean.getSourceId());
				
				if (LOG.isDebugEnabled())
					LOG.debug("addIndirectTrustEvidence(requestor=" + requestor
							+ ", subjectId=" + subjectId + ", objectId=" 
							+ objectId + ", type=" + type + ", timestamp=" 
							+ timestamp	+ ", info=" + info + ", sourceId=" 
							+ sourceId + ")");
				this.trustEvidenceCollector.addIndirectEvidence(requestor,
						subjectId, objectId, type, timestamp, info, sourceId);
				
			} catch (InvalidFormatException ife) {
				LOG.error("Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "Invalid requestor: " + ife.getLocalizedMessage(), ife);
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
								+ "Invalid requestor: " + ife.getLocalizedMessage());
			} catch (MalformedTrustedEntityIdException mteide) {
				LOG.error("Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "Invalid teid: " + mteide.getLocalizedMessage(), mteide);
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "Invalid teid: " + mteide.getLocalizedMessage());
			} catch (IOException ioe) {
				LOG.error("Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "Could not deserialise info: " + ioe.getLocalizedMessage(), ioe);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "Could not deserialise info: " + ioe.getLocalizedMessage());
			} catch (ClassNotFoundException cnfe) {
				LOG.error("Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "Could not deserialise info: " + cnfe.getLocalizedMessage(), cnfe);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.bad_request, 
						"Invalid TrustEvidenceCollector remote addIndirectEvidence request: "
						+ "Could not deserialise info: " + cnfe.getLocalizedMessage());
			} catch (TrustException te) {
				LOG.error("Failed to add indirect trust evidence: "
						+ te.getLocalizedMessage(), te);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, 
						te.getLocalizedMessage());
			}
			
		} else {
			LOG.error("Unsupported TrustEvidenceCollector remote request method: "
					+ requestBean.getMethodName());
			throw new XMPPError(StanzaError.unexpected_request, 
					"Unsupported TrustEvidenceCollector remote request method: "
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