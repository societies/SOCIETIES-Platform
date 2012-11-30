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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.privacytrust.trust.remote.ITrustBrokerRemote;
import org.societies.api.internal.privacytrust.trust.remote.ITrustBrokerRemoteCallback;
import org.societies.api.internal.privacytrust.trust.remote.TrustModelBeanTranslator;
import org.societies.api.internal.schema.privacytrust.trust.broker.MethodName;
import org.societies.api.internal.schema.privacytrust.trust.broker.RetrieveTrustBrokerRequestBean;
import org.societies.api.internal.schema.privacytrust.trust.broker.TrustBrokerRequestBean;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.remote.PrivacyTrustCommClientCallback;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
public class TrustBrokerCommClient implements ITrustBrokerRemote {
	
	/** The logging facility. */
	private static Logger LOG = LoggerFactory.getLogger(TrustBrokerCommClient.class);
	
	/** The Communications Mgr service reference. */
	private ICommManager commManager; 
	
	private PrivacyTrustCommClientCallback privacyTrustCommClientCallback;
	
	private TrustBrokerCommClientCallback trustBrokerCommClientCallback;

	TrustBrokerCommClient() {
		
		LOG.info(this.getClass() + " instantiated");
	}

	/*
	 * @see org.societies.api.internal.privacytrust.trust.remote.ITrustBrokerRemote#retrieveTrust(org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.privacytrust.trust.model.TrustedEntityId, org.societies.api.internal.privacytrust.trust.remote.ITrustBrokerRemoteCallback)
	 */
	@Override
	public void retrieveTrust(final TrustedEntityId trustorId,
			final TrustedEntityId trusteeId,
			final ITrustBrokerRemoteCallback callback) throws TrustException {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Retrieving trust value for entity (" + trustorId	+ ", "
					+ trusteeId + ")");
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().fromJid(trustorId.getEntityId()); 
			final Stanza stanza = new Stanza(toIdentity);
			
			this.trustBrokerCommClientCallback.addClient(stanza.getId(), callback);
			
			final RetrieveTrustBrokerRequestBean retrieveBean = new RetrieveTrustBrokerRequestBean();
			retrieveBean.setTrustorId(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(trustorId));
			retrieveBean.setTrusteeId(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(trusteeId));
			
			final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
			requestBean.setMethodName(MethodName.RETRIEVE);
			requestBean.setRetrieve(retrieveBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.privacyTrustCommClientCallback);
			
		} catch (InvalidFormatException ife) {
			
			throw new TrustBrokerCommException("Could not retrieve trust for entity (" 
					+ trustorId + ", " + trusteeId 
					+ "): Invalid trustorId IIdentity: " 
					+ ife.getLocalizedMessage(), ife);
		} catch (CommunicationException ce) {
			
			throw new TrustBrokerCommException("Could not retrieve trust for entity (" 
					+ trustorId + ", " + trusteeId 
					+ "): " + ce.getLocalizedMessage(), ce);
		}
	}
	
	public void setCommManager(ICommManager commManager) {
		
		this.commManager = commManager;
	}

	public void setPrivacyTrustCommClientCallback(
			PrivacyTrustCommClientCallback privacyTrustCommClientCallback) {
		
		this.privacyTrustCommClientCallback = privacyTrustCommClientCallback;
	}
	
	public void setTrustBrokerCommClientCallback(
			TrustBrokerCommClientCallback trustBrokerCommClientCallback) {
		
		this.trustBrokerCommClientCallback = trustBrokerCommClientCallback;
	}
}