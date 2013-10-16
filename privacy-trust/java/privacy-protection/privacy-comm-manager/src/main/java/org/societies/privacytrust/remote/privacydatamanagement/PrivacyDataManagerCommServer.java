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
package org.societies.privacytrust.remote.privacydatamanagement;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBeanResult;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;


public class PrivacyDataManagerCommServer {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerCommServer.class);

	private ICommManager commManager;
	private IPrivacyDataManager privacyDataManager;


	public PrivacyDataManagerCommServer() {
	}


	public Object getQuery(Stanza stanza, PrivacyDataManagerBean bean){
		PrivacyDataManagerBeanResult beanResult = new PrivacyDataManagerBeanResult();
		boolean ack = true;

		// -- Check Permission
		if (bean.getMethod().equals(MethodType.CHECK_PERMISSION)) {
			beanResult.setMethod(MethodType.CHECK_PERMISSION);
			ack = checkPermission(bean, beanResult);
		}

		// -- Obfuscate Data
		else if (bean.getMethod().equals(MethodType.OBFUSCATE_DATA)) {
			beanResult.setMethod(MethodType.OBFUSCATE_DATA);
			ack = obfuscateData(bean, beanResult);
		}
		else {
			LOG.error("getQuery(): Unknown method "+bean.getMethod().name());
			beanResult.setAckMessage("Error Unknown method "+bean.getMethod().name());
		}

		beanResult.setAck(ack);
		return beanResult;
	}

	private boolean checkPermission(PrivacyDataManagerBean bean, PrivacyDataManagerBeanResult beanResult) {
		try {
			List<DataIdentifier> dataIds = DataIdentifierFactory.fromUris(bean.getDataIdUris());
			List<ResponseItem> permissions = privacyDataManager.checkPermission(bean.getRequestor(), dataIds, bean.getActions());
			if (null != permissions && permissions.size() > 0) {
				beanResult.setPermissions(permissions);
			}
			else {
				beanResult.setAck(false);
				beanResult.setAckMessage("No permission retrieved");
			}

		}
		catch (MalformedCtxIdentifierException e) {
			LOG.error("MalformedCtxIdentifierException: "+MethodType.CHECK_PERMISSION, e);
			beanResult.setAckMessage("Error MalformedCtxIdentifierException: "+e.getMessage());
			return false;
		}
		catch (PrivacyException e) {
			LOG.error("PrivacyException: "+MethodType.CHECK_PERMISSION, e);
			beanResult.setAckMessage("Error PrivacyException: "+e.getMessage());
			return false;
		}
		return true;
	}

	private boolean obfuscateData(PrivacyDataManagerBean bean, PrivacyDataManagerBeanResult beanResult) {
		try {
			Future<DataWrapper> obfuscatedDataWrapperAsync = privacyDataManager.obfuscateData(bean.getRequestor(), bean.getDataWrapper());
			beanResult.setDataWrapper(obfuscatedDataWrapperAsync.get());
		}
		catch (PrivacyException e) {
			LOG.error("PrivacyException: "+MethodType.OBFUSCATE_DATA, e);
			beanResult.setAckMessage("Error during data obfuscation: "+e.getMessage());
			return false;
		} catch (InterruptedException e) {
			LOG.error("InterruptedException: "+MethodType.OBFUSCATE_DATA, e);
			beanResult.setAckMessage("Error during waiting for data obfuscation: "+e.getMessage());
			return false;
		} catch (ExecutionException e) {
			LOG.error("ExecutionException: "+MethodType.OBFUSCATE_DATA, e);
			beanResult.setAckMessage("Error during executation of data obfuscation: "+e.getMessage());
			return false;
		}
		return true;
	}



	// -- Dependency Injection

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	public void setPrivacyDataManager(IPrivacyDataManager privacyDataManager) {
		this.privacyDataManager = privacyDataManager;
	}
}
