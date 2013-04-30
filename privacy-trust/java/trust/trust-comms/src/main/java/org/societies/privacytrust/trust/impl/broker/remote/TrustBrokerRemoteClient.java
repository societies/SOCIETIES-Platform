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
import org.societies.api.schema.privacytrust.trust.broker.MethodName;
import org.societies.api.schema.privacytrust.trust.broker.TrustBrokerRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipsRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustValueRequestBean;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient;
import org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback;
import org.societies.privacytrust.trust.impl.remote.TrustCommsClientCallback;
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
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
	}

	/*
	 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback)
	 */
	@Override
	public void retrieveTrustRelationships(final Requestor requestor, 
			final TrustedEntityId trustorId,
			final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Retrieving trust relationships of trustor '"	+ trustorId 
					+ "' on behalf of requestor '" + requestor + "'");
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().fromJid(trustorId.getEntityId()); 
			final Stanza stanza = new Stanza(toIdentity);
			
			this.trustBrokerRemoteClientCallback.addClient(stanza.getId(), callback);
			
			final TrustRelationshipsRequestBean retrieveBean = new TrustRelationshipsRequestBean();
			retrieveBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			retrieveBean.setTrustorId(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(trustorId));
			
			final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
			requestBean.setMethodName(MethodName.RETRIEVE_TRUST_RELATIONSHIPS);
			requestBean.setRetrieveTrustRelationships(retrieveBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.trustCommsClientCallback);
			
		} catch (InvalidFormatException ife) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust relationships of trustor '"
					+ trustorId + "': Invalid trustorId IIdentity: " 
					+ ife.getLocalizedMessage(), ife);
		} catch (CommunicationException ce) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust relationships of trustor '"
					+ trustorId	+ "': " + ce.getLocalizedMessage(), ce);
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback)
	 */
	@Override
	public void retrieveTrustRelationships(final Requestor requestor, 
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Retrieving trust relationships between trustor '" 
					+ trustorId + "' and trustee '" + trusteeId 
					+ "' on behalf of requestor '" + requestor + "'");
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().fromJid(trustorId.getEntityId()); 
			final Stanza stanza = new Stanza(toIdentity);
			
			this.trustBrokerRemoteClientCallback.addClient(stanza.getId(), callback);
			
			final TrustRelationshipsRequestBean retrieveBean = new TrustRelationshipsRequestBean();
			retrieveBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			retrieveBean.setTrustorId(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(trustorId));
			retrieveBean.setTrusteeId(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(trusteeId));
			
			final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
			requestBean.setMethodName(MethodName.RETRIEVE_TRUST_RELATIONSHIPS);
			requestBean.setRetrieveTrustRelationships(retrieveBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.trustCommsClientCallback);
			
		} catch (InvalidFormatException ife) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust relationships between trustor '"
					+ trustorId + "' and trustee '" + trusteeId 
					+ "': Invalid IIdentity: " 
					+ ife.getLocalizedMessage(), ife);
		} catch (CommunicationException ce) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust relationships between trustor '"
					+ trustorId + "' and trustee '" + trusteeId 
					+ "': " + ce.getLocalizedMessage(), ce);
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient#retrieveTrustRelationship(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType, org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback)
	 */
	@Override
	public void retrieveTrustRelationship(final Requestor requestor, 
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType,
			final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Retrieving trust relationship of type '" + trustValueType
					+ "' assigned to entity '" + trusteeId	
					+ "' by '" + trustorId + "' on behalf of requestor '"
					+ requestor + "'");
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().fromJid(trustorId.getEntityId()); 
			final Stanza stanza = new Stanza(toIdentity);
			
			this.trustBrokerRemoteClientCallback.addClient(stanza.getId(), callback);
			
			final TrustRelationshipRequestBean retrieveBean = new TrustRelationshipRequestBean();
			retrieveBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			retrieveBean.setTrustorId(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(trustorId));
			retrieveBean.setTrusteeId(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(trusteeId));
			retrieveBean.setTrustValueType(
					TrustModelBeanTranslator.getInstance().fromTrustValueType(trustValueType));
			
			final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
			requestBean.setMethodName(MethodName.RETRIEVE_TRUST_RELATIONSHIP);
			requestBean.setRetrieveTrustRelationship(retrieveBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.trustCommsClientCallback);
			
		} catch (InvalidFormatException ife) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust relationship of type '"
					+ trustValueType + "' assigned to entity '" 
					+ trusteeId	+ "' by '" + trustorId 
					+ "': Invalid trustorId IIdentity: " 
					+ ife.getLocalizedMessage(), ife);
		} catch (CommunicationException ce) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust relationship of type '"
					+ trustValueType + "' assigned to entity '" 
					+ trusteeId	+ "' by '" + trustorId 
					+ "': " + ce.getLocalizedMessage(), ce);
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient#retrieveTrustValue(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType, org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback)
	 */
	@Override
	public void retrieveTrustValue(final Requestor requestor, 
			final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final TrustValueType trustValueType,
			final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Retrieving trust value of type '" + trustValueType
					+ "' assigned to entity '" + trusteeId	
					+ "' by '" + trustorId + "' on behalf of requestor '"
					+ requestor + "'");
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().fromJid(trustorId.getEntityId()); 
			final Stanza stanza = new Stanza(toIdentity);
			
			this.trustBrokerRemoteClientCallback.addClient(stanza.getId(), callback);
			
			final TrustValueRequestBean retrieveBean = new TrustValueRequestBean();
			retrieveBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			retrieveBean.setTrustorId(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(trustorId));
			retrieveBean.setTrusteeId(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(trusteeId));
			retrieveBean.setTrustValueType(
					TrustModelBeanTranslator.getInstance().fromTrustValueType(trustValueType));
			
			final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
			requestBean.setMethodName(MethodName.RETRIEVE_TRUST_VALUE);
			requestBean.setRetrieveTrustValue(retrieveBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.trustCommsClientCallback);
			
		} catch (InvalidFormatException ife) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust value of type '"
					+ trustValueType + "' assigned to entity '" 
					+ trusteeId	+ "' by '" + trustorId 
					+ "': Invalid trustorId IIdentity: " 
					+ ife.getLocalizedMessage(), ife);
		} catch (CommunicationException ce) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust value of type '"
					+ trustValueType + "' assigned to entity '" 
					+ trusteeId	+ "' by '" + trustorId 
					+ "': " + ce.getLocalizedMessage(), ce);
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType, org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback)
	 */
	@Override
	public void retrieveTrustRelationships(final Requestor requestor, 
			final TrustedEntityId trustorId, 
			final TrustedEntityType trusteeType,
			final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Retrieving trust relationships of trustor '" 
					+ trustorId + "' with entities of type '" + trusteeType 
					+ "' on behalf of requestor '" + requestor + "'");
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().fromJid(trustorId.getEntityId()); 
			final Stanza stanza = new Stanza(toIdentity);
			
			this.trustBrokerRemoteClientCallback.addClient(stanza.getId(), callback);
			
			final TrustRelationshipsRequestBean retrieveBean = new TrustRelationshipsRequestBean();
			retrieveBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			retrieveBean.setTrustorId(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(trustorId));
			retrieveBean.setTrusteeType(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityType(trusteeType));
			
			final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
			requestBean.setMethodName(MethodName.RETRIEVE_TRUST_RELATIONSHIPS);
			requestBean.setRetrieveTrustRelationships(retrieveBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.trustCommsClientCallback);
			
		} catch (InvalidFormatException ife) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust relationships of trustor '"
					+ trustorId + "' with entities of type '" + trusteeType 
					+ "': Invalid trustor IIdentity: " 
					+ ife.getLocalizedMessage(), ife);
		} catch (CommunicationException ce) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust relationships of trustor '"
					+ trustorId + "' with entities of type '" + trusteeType 
					+ "': " + ce.getLocalizedMessage(), ce);
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustValueType, org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback)
	 */
	@Override
	public void retrieveTrustRelationships(final Requestor requestor, 
			final TrustedEntityId trustorId, 
			final TrustValueType trustValueType,
			final ITrustBrokerRemoteClientCallback callback) 
					throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Retrieving trust relationships of trustor '" 
					+ trustorId + "' with values of type '" + trustValueType 
					+ "' on behalf of requestor '" + requestor + "'");
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().fromJid(trustorId.getEntityId()); 
			final Stanza stanza = new Stanza(toIdentity);
			
			this.trustBrokerRemoteClientCallback.addClient(stanza.getId(), callback);
			
			final TrustRelationshipsRequestBean retrieveBean = new TrustRelationshipsRequestBean();
			retrieveBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			retrieveBean.setTrustorId(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(trustorId));
			retrieveBean.setTrustValueType(
					TrustModelBeanTranslator.getInstance().fromTrustValueType(trustValueType));
			
			final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
			requestBean.setMethodName(MethodName.RETRIEVE_TRUST_RELATIONSHIPS);
			requestBean.setRetrieveTrustRelationships(retrieveBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.trustCommsClientCallback);
			
		} catch (InvalidFormatException ife) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust relationships of trustor '"
					+ trustorId + "' with values of type '" + trustValueType 
					+ "': Invalid trustor IIdentity: " 
					+ ife.getLocalizedMessage(), ife);
		} catch (CommunicationException ce) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust relationships of trustor '"
					+ trustorId + "' with values of type '" + trustValueType
					+ "': " + ce.getLocalizedMessage(), ce);
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClient#retrieveTrustRelationships(org.societies.api.identity.Requestor, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityType, org.societies.api.privacytrust.trust.model.TrustValueType, org.societies.privacytrust.trust.api.broker.remote.ITrustBrokerRemoteClientCallback)
	 */
	@Override
	public void retrieveTrustRelationships(
			final Requestor requestor, final TrustedEntityId trustorId,
			final TrustedEntityType trusteeType, 
			final TrustValueType trustValueType,
			final ITrustBrokerRemoteClientCallback callback) throws TrustException {
		
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Retrieving trust relationships of trustor '" 
					+ trustorId + "' with entities of type '" + trusteeType
					+ "' and values of type '" + trustValueType
					+ "' on behalf of requestor '" + requestor + "'");
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().fromJid(trustorId.getEntityId()); 
			final Stanza stanza = new Stanza(toIdentity);
			
			this.trustBrokerRemoteClientCallback.addClient(stanza.getId(), callback);
			
			final TrustRelationshipsRequestBean retrieveBean = new TrustRelationshipsRequestBean();
			retrieveBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			retrieveBean.setTrustorId(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(trustorId));
			retrieveBean.setTrusteeType(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityType(trusteeType));
			retrieveBean.setTrustValueType(
					TrustModelBeanTranslator.getInstance().fromTrustValueType(trustValueType));
			
			final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
			requestBean.setMethodName(MethodName.RETRIEVE_TRUST_RELATIONSHIPS);
			requestBean.setRetrieveTrustRelationships(retrieveBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.trustCommsClientCallback);
			
		} catch (InvalidFormatException ife) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust relationships of trustor '"
					+ trustorId + "' with entities of type '" + trusteeType
					+ "' and values of type '" + trustValueType
					+ "': Invalid trustor IIdentity: " 
					+ ife.getLocalizedMessage(), ife);
		} catch (CommunicationException ce) {
			
			throw new TrustBrokerCommsException(
					"Could not retrieve trust relationships of trustor '"
					+ trustorId + "' with entities of type '" + trusteeType
					+ "' and values of type '" + trustValueType
					+ "': " + ce.getLocalizedMessage(), ce);
		}
	}
}