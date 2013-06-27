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
package org.societies.privacytrust.remote.privacydatamanagement;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.DataIdentifierUtils;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IDataObfuscationListener;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyDataManagerListener;
import org.societies.api.internal.privacytrust.privacyprotection.remote.IPrivacyDataManagerRemote;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBean;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.privacytrust.remote.PrivacyCommClientCallback;

/**
 * Comms Client that initiates the remote communication
 *
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyDataManagerCommClient implements IPrivacyDataManagerRemote {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerCommClient.class);

	private ICommManager commManager;
	private PrivacyDataManagerCommClientCallback listeners;
	private PrivacyCommClientCallback privacyCommClientCallback;


	public PrivacyDataManagerCommClient() {	
	}


	@Override
	public void checkPermission(RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions, IPrivacyDataManagerListener listener) throws PrivacyException {
		// -- Search receiver
		IIdentity toIdentity = commManager.getIdManager().getThisNetworkNode();
		Stanza stanza = new Stanza(toIdentity);

		// -- Register listener
		listeners.privacyDataManagerlisteners.put(stanza.getId(), listener);

		// -- Create message
		PrivacyDataManagerBean bean = new PrivacyDataManagerBean();
		bean.setMethod(MethodType.CHECK_PERMISSION);
		bean.setRequestor(requestor);
		List<String> dataUris = new ArrayList<String>();
		for(DataIdentifier dataId : dataIds) {
			dataUris.add(DataIdentifierUtils.toUriString(dataId));
		}
		bean.setDataIdUris(dataUris);
		bean.setActions(actions);
		try {
			this.commManager.sendIQGet(stanza, bean, privacyCommClientCallback);
		} catch (CommunicationException e) {
			LOG.error("CommunicationException: "+MethodType.CHECK_PERMISSION, e);
			throw new PrivacyException("CommunicationException: "+MethodType.CHECK_PERMISSION, e);
		}
	}
	@Override
	@Deprecated
	public void checkPermission(Requestor requestor, DataIdentifier dataId, List<org.societies.api.privacytrust.privacy.model.privacypolicy.Action> actions, IPrivacyDataManagerListener listener) throws PrivacyException {
		List<DataIdentifier> dataIds = new ArrayList<DataIdentifier>();
		dataIds.add(dataId);
		checkPermission(RequestorUtils.toRequestorBean(requestor), dataIds, ActionUtils.toActionBeans(actions), listener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.remote.IPrivacyDataManagerRemote#obfuscateData(org.societies.api.schema.identity.RequestorBean, org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper, org.societies.api.internal.privacytrust.privacyprotection.model.listener.IDataObfuscationListener)
	 */
	@Override
	public void obfuscateData(RequestorBean requestor, DataWrapper dataWrapper, IDataObfuscationListener listener) throws PrivacyException {
		// -- Search receiver
		IIdentity toIdentity = commManager.getIdManager().getThisNetworkNode();
		Stanza stanza = new Stanza(toIdentity);

		// -- Register listener
		listeners.dataObfuscationlisteners.put(stanza.getId(), listener);

		// -- Create message
		PrivacyDataManagerBean bean = new PrivacyDataManagerBean();
		bean.setMethod(MethodType.OBFUSCATE_DATA);
		bean.setRequestor(requestor);
		bean.setDataWrapper(dataWrapper);
		try {
			this.commManager.sendIQGet(stanza, bean, privacyCommClientCallback);
		} catch (CommunicationException e) {
			LOG.error("CommunicationException: "+MethodType.OBFUSCATE_DATA, e);
			throw new PrivacyException("Error during remote call: "+MethodType.OBFUSCATE_DATA, e);
		}
	}



	// -- Dependency Injection

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	public void setListeners(PrivacyDataManagerCommClientCallback listeners) {
		this.listeners = listeners;
	}
	public void setPrivacyCommClientCallback(PrivacyCommClientCallback privacyCommClientCallback) {
		this.privacyCommClientCallback = privacyCommClientCallback;
	}
}
