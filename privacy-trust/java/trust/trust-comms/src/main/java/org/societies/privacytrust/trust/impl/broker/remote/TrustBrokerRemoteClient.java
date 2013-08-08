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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.schema.privacytrust.trust.broker.ExtTrustRelationshipRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.ExtTrustRelationshipsRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.MethodName;
import org.societies.api.schema.privacytrust.trust.broker.TrustBrokerRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipsRemoveRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipsRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustValueRequestBean;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient;
import org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback;
import org.societies.privacytrust.trust.impl.remote.TrustCommsClientCallback;
import org.societies.privacytrust.trust.impl.remote.util.TrustCommsClientTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
@Service
public class TrustBrokerRemoteClient implements ITrustBrokerRemoteClient {
	
	/** The logging facility. */
	private static Logger LOG = LoggerFactory.getLogger(TrustBrokerRemoteClient.class);
	
	/** The Communications Mgr service reference. */
	@Autowired(required=true)
	private ICommManager commManager; 
	
	/** The Trust Communications client callback reference. */
	@Autowired(required=true)
	private TrustCommsClientCallback trustCommsClientCallback;
	
	/** The Trust Broker remote client callback. */
	@Autowired(required=true)
	private TrustBrokerRemoteClientCallback trustBrokerRemoteClientCallback;

	TrustBrokerRemoteClient() {
		
		LOG.info("{} instantiated", this.getClass());
	}

	/*
	 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.TrustQuery, org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback)
	 */
	@Override
	public void retrieveTrustRelationships(final Requestor requestor, 
			final TrustQuery query,	final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		LOG.debug("Retrieving trust relationships matching query '{}'" 
				+ " on behalf of requestor '{}'", query, requestor);
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().fromJid(query.getTrustorId().getEntityId()); 
			final Stanza stanza = new Stanza(toIdentity);
			
			this.trustBrokerRemoteClientCallback.addClient(stanza.getId(), callback);
			
			final TrustRelationshipsRequestBean retrieveBean = 
					new TrustRelationshipsRequestBean();
			// (required) requestor
			retrieveBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			// (required) query
			retrieveBean.setQuery(TrustCommsClientTranslator.getInstance()
					.fromTrustQuery(query));
			
			final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
			requestBean.setMethodName(MethodName.RETRIEVE_TRUST_RELATIONSHIPS);
			requestBean.setRetrieveTrustRelationships(retrieveBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.trustCommsClientCallback);
			
		} catch (InvalidFormatException ife) {
			
			throw new TrustBrokerCommsException(
					"Invalid trustorId IIdentity: " 
					+ ife.getLocalizedMessage(), ife);
		} catch (CommunicationException ce) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust relationships of trustor '"
					+ query.getTrustorId() + "': " + ce.getLocalizedMessage(), ce);
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient#retrieveExtTrustRelationships(org.societies.api.privacytrust.trust.TrustQuery, org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback)
	 */
	@Override
	public void retrieveExtTrustRelationships(final TrustQuery query,
			final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException {
		
		if (query == null)
			throw new NullPointerException("query can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		LOG.debug("Retrieving extended trust relationships matching query '{}'", 
				query);
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().getCloudNode(); 
			final Stanza stanza = new Stanza(toIdentity);
			
			this.trustBrokerRemoteClientCallback.addClient(stanza.getId(), callback);
			
			final ExtTrustRelationshipsRequestBean retrieveBean = 
					new ExtTrustRelationshipsRequestBean();
			// (required) query
			retrieveBean.setQuery(TrustCommsClientTranslator.getInstance()
					.fromTrustQuery(query));
			
			final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
			requestBean.setMethodName(MethodName.RETRIEVE_EXT_TRUST_RELATIONSHIPS);
			requestBean.setRetrieveExtTrustRelationships(retrieveBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.trustCommsClientCallback);
			
		} catch (CommunicationException ce) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve extended trust relationships of trustor '"
					+ query.getTrustorId() + "': " + ce.getLocalizedMessage(), ce);
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient#retrieveTrustRelationship(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.TrustQuery, org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback)
	 */
	@Override
	public void retrieveTrustRelationship(final Requestor requestor, 
			final TrustQuery query, final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		LOG.debug("Retrieving trust relationship matching query '{}'" 
				+ " on behalf of requestor '{}'", query, requestor);
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().fromJid(query.getTrustorId().getEntityId()); 
			final Stanza stanza = new Stanza(toIdentity);
			
			this.trustBrokerRemoteClientCallback.addClient(stanza.getId(), callback);
			
			final TrustRelationshipRequestBean retrieveBean = 
					new TrustRelationshipRequestBean();
			// (required) requestor
			retrieveBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			// (required) query
			retrieveBean.setQuery(TrustCommsClientTranslator.getInstance()
					.fromTrustQuery(query));
			
			final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
			requestBean.setMethodName(MethodName.RETRIEVE_TRUST_RELATIONSHIP);
			requestBean.setRetrieveTrustRelationship(retrieveBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.trustCommsClientCallback);
			
		} catch (InvalidFormatException ife) {
			
			throw new TrustBrokerCommsException(
					"Invalid trustorId IIdentity: " 
					+ ife.getLocalizedMessage(), ife);
		} catch (CommunicationException ce) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust relationships of trustor '"
					+ query.getTrustorId() + "': " + ce.getLocalizedMessage(), ce);
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient#retrieveExtTrustRelationship(org.societies.api.privacytrust.trust.TrustQuery, org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback)
	 */
	@Override
	public void retrieveExtTrustRelationship(final TrustQuery query,
			final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException {
		
		if (query == null)
			throw new NullPointerException("query can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		LOG.debug("Retrieving extended trust relationship matching query '{}'", 
				query);
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().getCloudNode(); 
			final Stanza stanza = new Stanza(toIdentity);
			
			this.trustBrokerRemoteClientCallback.addClient(stanza.getId(), callback);
			
			final ExtTrustRelationshipRequestBean retrieveBean = 
					new ExtTrustRelationshipRequestBean();
			// (required) query
			retrieveBean.setQuery(TrustCommsClientTranslator.getInstance()
					.fromTrustQuery(query));
			
			final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
			requestBean.setMethodName(MethodName.RETRIEVE_EXT_TRUST_RELATIONSHIP);
			requestBean.setRetrieveExtTrustRelationship(retrieveBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.trustCommsClientCallback);
			
		} catch (CommunicationException ce) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve extended trust relationship of trustor '"
					+ query.getTrustorId() + "': " + ce.getLocalizedMessage(), ce);
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient#retrieveTrustValue(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.TrustQuery, org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback)
	 */
	@Override
	public void retrieveTrustValue(final Requestor requestor, 
			final TrustQuery query, final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		LOG.debug("Retrieving trust value matching query '{}'" 
				+ " on behalf of requestor '{}'", query, requestor);
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().fromJid(query.getTrustorId().getEntityId()); 
			final Stanza stanza = new Stanza(toIdentity);
			
			this.trustBrokerRemoteClientCallback.addClient(stanza.getId(), callback);
			
			final TrustValueRequestBean retrieveBean = 
					new TrustValueRequestBean();
			// (required) requestor
			retrieveBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			// (required) query
			retrieveBean.setQuery(TrustCommsClientTranslator.getInstance()
					.fromTrustQuery(query));
			
			final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
			requestBean.setMethodName(MethodName.RETRIEVE_TRUST_VALUE);
			requestBean.setRetrieveTrustValue(retrieveBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.trustCommsClientCallback);
			
		} catch (InvalidFormatException ife) {
			
			throw new TrustBrokerCommsException(
					"Invalid trustorId IIdentity: " 
					+ ife.getLocalizedMessage(), ife);
		} catch (CommunicationException ce) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust value assigned by trustor '"
					+ query.getTrustorId() + "': " + ce.getLocalizedMessage(), ce);
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient#removeTrustRelationships(org.societies.api.privacytrust.trust.TrustQuery, org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback)
	 */
	@Override
	public void removeTrustRelationships(final TrustQuery query,
			final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException {
		
		if (query == null) {
			throw new NullPointerException("query can't be null");
		}
		if (callback == null) {
			throw new NullPointerException("callback can't be null");
		}
		
		LOG.debug("Removing trust relationships matching query '{}'", query);
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().getCloudNode(); 
			final Stanza stanza = new Stanza(toIdentity);
			
			this.trustBrokerRemoteClientCallback.addClient(stanza.getId(), callback);
			
			final TrustRelationshipsRemoveRequestBean removeBean = 
					new TrustRelationshipsRemoveRequestBean();
			// (required) query
			removeBean.setQuery(TrustCommsClientTranslator.getInstance()
					.fromTrustQuery(query));
			
			final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
			requestBean.setMethodName(MethodName.REMOVE_TRUST_RELATIONSHIPS);
			requestBean.setRemoveTrustRelationships(removeBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.trustCommsClientCallback);
			
		} catch (CommunicationException ce) {
			
			throw new TrustBrokerCommsException(
					"Could not remove trust relationships matching query '"
					+ query + "': " + ce.getLocalizedMessage(), ce);
		}
	}
}