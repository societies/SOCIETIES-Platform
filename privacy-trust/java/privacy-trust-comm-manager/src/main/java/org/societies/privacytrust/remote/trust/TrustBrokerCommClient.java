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
import org.societies.api.identity.Requestor;
import org.societies.privacytrust.remote.PrivacyTrustCommClientCallback;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
public class TrustBrokerCommClient /* implements IPrivacyDataManagerRemote */ {
	
	/** The logging facility. */
	private static Logger LOG = LoggerFactory.getLogger(TrustBrokerCommClient.class);
	
	/** The Communications Mgr service reference. */
	private ICommManager commManager;
/*	
	private PrivacyDataManagerCommClientCallback listeners;
	
	private PrivacyTrustCommClientCallback privacyTrustCommClientCallback;
*/
	public TrustBrokerCommClient() {
		
		LOG.info(this.getClass() + " instantiated");
	}

	/*
	 * @see org.societies.api.internal.privacytrust.privacyprotection.remote.IPrivacyDataManagerRemote#checkPermission(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.api.context.model.CtxIdentifier, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action, org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyDataManagerListener)
	 *
	@Override
	public void checkPermission(Requestor requestor, IIdentity ownerId, CtxIdentifier dataId, Action action, IPrivacyDataManagerListener listener) throws PrivacyException {
		LOG.info("#### checkPermission remote called");
		IIdentity toIdentity = commManager.getIdManager().getThisNetworkNode();
		Stanza stanza = new Stanza(toIdentity);
//		Stanza stanza = new Stanza(ownerId);
		
		listeners.privacyDataManagerlisteners.put(stanza.getId(), listener);
		
		PrivacyDataManagerBean bean = new PrivacyDataManagerBean();
		bean.setMethod(MethodType.CHECK_PERMISSION);
		bean.setRequestor(Util.createRequestorBean(requestor));
		bean.setOwnerId(ownerId.getJid());
		bean.setDataId(dataId.toUriString());
		bean.setAction(ActionUtils.toActionBean(action));
		try {
			this.commManager.sendIQGet(stanza, bean, privacyTrustCommClientCallback);
		} catch (CommunicationException e) {
			LOG.error("CommunicationException: "+MethodType.CHECK_PERMISSION, e);
			throw new PrivacyException("CommunicationException: "+MethodType.CHECK_PERMISSION, e);
		}
		LOG.info("#### checkPermission remote sent");
	}

	/*
	 * @see org.societies.api.internal.privacytrust.privacyprotection.remote.IPrivacyDataManagerRemote#obfuscateData(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, org.societies.api.internal.privacytrust.privacyprotection.model.listener.IDataObfuscationListener)
	 *
	@Override
	public void obfuscateData(Requestor requestor, IIdentity ownerId, IDataWrapper dataWrapper, IDataObfuscationListener listener) throws PrivacyException {
		LOG.info("#### obfuscateData remote called");
		
		IIdentity toIdentity = commManager.getIdManager().getThisNetworkNode();
		Stanza stanza = new Stanza(toIdentity);
//		Stanza stanza = new Stanza(ownerId);
		
		listeners.dataObfuscationlisteners.put(stanza.getId(), listener);
		
		PrivacyDataManagerBean bean = new PrivacyDataManagerBean();
		bean.setMethod(MethodType.OBFUSCATE_DATA);
		bean.setRequestor(Util.createRequestorBean(requestor));
		bean.setOwnerId(ownerId.getJid());
		bean.setDataId(dataWrapper.getDataId().toUriString());
		try {
			this.commManager.sendIQGet(stanza, bean, privacyTrustCommClientCallback);
		} catch (CommunicationException e) {
			LOG.error("CommunicationException: "+MethodType.OBFUSCATE_DATA, e);
			throw new PrivacyException("CommunicationException: "+MethodType.OBFUSCATE_DATA, e);
		}
		LOG.info("#### obfuscateData remote sent");
	}
*/
	// -- Dependency Injection
	
	public void setCommManager(ICommManager commManager) {
		
		this.commManager = commManager;
	}
/*	
	public void setPrivacyTrustCommClientCallback(
			PrivacyTrustCommClientCallback privacyTrustCommClientCallback) {
		
		this.privacyTrustCommClientCallback = privacyTrustCommClientCallback;
	}
	
	public void setListeners(PrivacyDataManagerCommClientCallback listeners) {
		
		this.listeners = listeners;
	}*/
}