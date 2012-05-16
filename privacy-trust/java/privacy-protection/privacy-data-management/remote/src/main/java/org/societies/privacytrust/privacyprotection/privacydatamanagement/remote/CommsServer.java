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

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
package org.societies.privacytrust.privacyprotection.privacydatamanagement.remote;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.DataWrapperFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBeanResult;
import org.societies.privacytrust.privacyprotection.model.util.ActionUtils;
import org.societies.privacytrust.privacyprotection.model.util.ResponseItemUtils;


public class CommsServer implements IFeatureServer {
	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/internal/schema/privacytrust/privacyprotection/privacydatamanagement",
					"http://societies.org/api/internal/schema/privacytrust/privacyprotection/model/privacypolicy",
					"http://societies.org/api/schema/identity"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement",
					"org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy",
					"org.societies.api.schema.identity"));

	private ICommManager commManager;
	private IPrivacyDataManager privacyDataManager;
	
	//METHODS
	public CommsServer() {
	}

	public void InitService() {
		LOG.info("init(): commMgr = {}", commManager.toString());
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			commManager.register(this);
			LOG.info("init(): commManager registered");
		} catch (CommunicationException e) {
			LOG.error("init(): ", e);
		}
	}

	/* Put your functionality here if there IS a return object
	 */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		LOG.info("**** PrivacyTrustCommManager request received");
		LOG.info("getQuery({}, {})", stanza, payload);
		LOG.info("getQuery(): stanza.id   = {}", stanza.getId());
		LOG.info("getQuery(): stanza.from = {}", stanza.getFrom());
		LOG.info("getQuery(): stanza.to   = {}", stanza.getTo());
		if (payload instanceof PrivacyDataManagerBean){
			return this.getQuery(stanza, (PrivacyDataManagerBean) payload);
		}
		return null;
	}

	public Object getQuery(Stanza stanza, PrivacyDataManagerBean bean){
		PrivacyDataManagerBeanResult beanResult = new PrivacyDataManagerBeanResult();
		boolean ack = true;

		// -- Check Permission
		if (bean.getMethod().equals(MethodType.CHECK_PERMISSION)) {
			LOG.info("**** checkPermission remote received");
			beanResult.setMethod(MethodType.CHECK_PERMISSION);
			try {
				Requestor requestor = Util.getRequestorFromBean(bean.getRequestor(), commManager);
				IIdentity ownerId = commManager.getIdManager().fromJid(bean.getOwnerId());
				Action action = ActionUtils.toAction(bean.getAction());
				CtxIdentifier dataId = CtxIdentifierFactory.getInstance().fromString(bean.getDataId());
				ResponseItem permission = privacyDataManager.checkPermission(requestor, ownerId, dataId, action);
				beanResult.setPermission(ResponseItemUtils.toResponseItemBean(permission));
			} catch (MalformedCtxIdentifierException e) {
				ack = false;
				beanResult.setAckMessage("Error MalformedCtxIdentifierException: "+e.getMessage());
			}
			catch (PrivacyException e) {
				ack = false;
				beanResult.setAckMessage("Error PrivacyException: "+e.getMessage());
			} catch (InvalidFormatException e) {
				ack = false;
				beanResult.setAckMessage("Error InvalidFormatException: "+e.getMessage());
			}
			LOG.info("**** checkPermission remote response sent");
		}

		// -- Obfuscate Data
		else if (bean.getMethod().equals(MethodType.OBFUSCATE_DATA)) {
			LOG.info("**** obfuscateData remote received");
			beanResult.setMethod(MethodType.OBFUSCATE_DATA);
			try {
				Requestor requestor = Util.getRequestorFromBean(bean.getRequestor(), commManager);
				IIdentity ownerId = commManager.getIdManager().fromJid(bean.getOwnerId());
				CtxIdentifier dataId = CtxIdentifierFactory.getInstance().fromString(bean.getDataId());
				Future<IDataWrapper> obfuscatedDataWrapperAsync = privacyDataManager.obfuscateData(requestor, ownerId, DataWrapperFactory.selectDataWrapper(dataId));
				ack = false;
				beanResult.setAckMessage("Sorry, the obfuscation is available, but not emotely yet.");
			} catch (MalformedCtxIdentifierException e) {
				ack = false;
				beanResult.setAckMessage("Error MalformedCtxIdentifierException: "+e.getMessage());
			}
			catch (PrivacyException e) {
				ack = false;
				beanResult.setAckMessage("Error PrivacyException: "+e.getMessage());
			} catch (InvalidFormatException e) {
				ack = false;
				beanResult.setAckMessage("Error InvalidFormatException: "+e.getMessage());
			}
			LOG.info("**** obfuscateData remote response sent");
		}

		beanResult.setAck(ack);
		return beanResult;
	}
	
	/* Put your functionality here if there is NO return object, ie, VOID 
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		LOG.info("**** PrivacyTrustCommManager message received");
		LOG.info("getQuery({}, {})", stanza, payload);
		LOG.info("getQuery(): stanza.id   = {}", stanza.getId());
		LOG.info("getQuery(): stanza.from = {}", stanza.getFrom());
		LOG.info("getQuery(): stanza.to   = {}", stanza.getTo());
	}


	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#setQuery(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}
	
	public ICommManager getCommManager() {
		return commManager;
	}
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		LOG.info("**** CommManager injected");
	}

	public IPrivacyDataManager getPrivacyDataManager() {
		return privacyDataManager;
	}
	public void setPrivacyDataManager(IPrivacyDataManager privacyDataManager) {
		this.privacyDataManager = privacyDataManager;
		LOG.info("**** PrivacyDataManager injected");
	}
}
