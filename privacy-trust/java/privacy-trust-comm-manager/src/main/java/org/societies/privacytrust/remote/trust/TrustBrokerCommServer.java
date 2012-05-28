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
package org.societies.privacytrust.remote.trust;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
public class TrustBrokerCommServer implements IFeatureServer {
	
	/** The logging facility. */
	private static Logger LOG = LoggerFactory.getLogger(TrustBrokerCommServer.class);

	/** The Communications Mgr service reference. */
	private ICommManager commManager;
	
	/** The Trust Broker service reference. */
	private ITrustBroker trustBroker;
	
	public TrustBrokerCommServer() {
		
		LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object getQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#setQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}
	
/*
	public Object getQuery(Stanza stanza, PrivacyDataManagerBean bean){
		PrivacyDataManagerBeanResult beanResult = new PrivacyDataManagerBeanResult();
		boolean ack = true;

		// -- Check Permission
		if (bean.getMethod().equals(MethodType.CHECK_PERMISSION)) {
			LOG.info("getQuery(): CheckPermission remote called");
			beanResult.setMethod(MethodType.CHECK_PERMISSION);
			ack = checkPermission(bean, beanResult);
			LOG.info("getQuery(): CheckPermission remote response sending");
		}

		// -- Obfuscate Data
		else if (bean.getMethod().equals(MethodType.OBFUSCATE_DATA)) {
			LOG.info("getQuery(): ObfuscateData remote called");
			beanResult.setMethod(MethodType.OBFUSCATE_DATA);
			ack = obfuscateData(bean, beanResult);
			LOG.info("getQuery(): ObfuscateData remote response sending");
		}
		else {
			LOG.info("getQuery(): Unknown method "+bean.getMethod().name());
			beanResult.setAckMessage("Error Unknown method "+bean.getMethod().name());
		}

		beanResult.setAck(ack);
		return beanResult;
	}
	
	private boolean checkPermission(PrivacyDataManagerBean bean, PrivacyDataManagerBeanResult beanResult) {
		try {
			Requestor requestor = Util.getRequestorFromBean(bean.getRequestor(), commManager);
			IIdentity ownerId = commManager.getIdManager().fromJid(bean.getOwnerId());
			Action action = ActionUtils.toAction(bean.getAction());
			CtxIdentifier dataId = CtxIdentifierFactory.getInstance().fromString(bean.getDataId());
			ResponseItem permission = privacyDataManager.checkPermission(requestor, ownerId, dataId, action);
			beanResult.setPermission(ResponseItemUtils.toResponseItemBean(permission));
		} catch (MalformedCtxIdentifierException e) {
			beanResult.setAckMessage("Error MalformedCtxIdentifierException: "+e.getMessage());
			return false;
		}
		catch (PrivacyException e) {
			beanResult.setAckMessage("Error PrivacyException: "+e.getMessage());
			return false;
		} catch (InvalidFormatException e) {
			beanResult.setAckMessage("Error InvalidFormatException: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean obfuscateData(PrivacyDataManagerBean bean, PrivacyDataManagerBeanResult beanResult) {
		try {
			Requestor requestor = Util.getRequestorFromBean(bean.getRequestor(), commManager);
			IIdentity ownerId = commManager.getIdManager().fromJid(bean.getOwnerId());
			CtxIdentifier dataId = CtxIdentifierFactory.getInstance().fromString(bean.getDataId());
			Future<IDataWrapper> obfuscatedDataWrapperAsync = privacyDataManager.obfuscateData(requestor, ownerId, DataWrapperFactory.selectDataWrapper(dataId));
			beanResult.setAckMessage("Sorry, the obfuscation is available, but not emotely yet.");
			return false;
		} catch (MalformedCtxIdentifierException e) {
			beanResult.setAckMessage("Error MalformedCtxIdentifierException: "+e.getMessage());
			return false;
		}
		catch (PrivacyException e) {
			beanResult.setAckMessage("Error PrivacyException: "+e.getMessage());
			return false;
		} catch (InvalidFormatException e) {
			beanResult.setAckMessage("Error InvalidFormatException: "+e.getMessage());
			return false;
		}
//		return true;
	}	
*/	
	// -- Dependency Injection

	public void setCommManager(ICommManager commManager) {
		
		this.commManager = commManager;
	}
	
	public void setTrustBroker(ITrustBroker trustBroker) {
		
		this.trustBroker = trustBroker;
	}
}