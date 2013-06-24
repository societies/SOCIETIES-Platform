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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IDataObfuscationListener;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyDataManagerListener;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBeanResult;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyDataManagerCommClientCallback {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerCommClientCallback.class);

	// -- Listeners list
	public Map<String, IPrivacyDataManagerListener> privacyDataManagerlisteners; 
	public Map<String, IDataObfuscationListener> dataObfuscationlisteners;


	public PrivacyDataManagerCommClientCallback() {
		privacyDataManagerlisteners = new HashMap<String, IPrivacyDataManagerListener>();
		dataObfuscationlisteners = new HashMap<String, IDataObfuscationListener>();
	}
	

	public void receiveResult(Stanza stanza, PrivacyDataManagerBeanResult bean) {
		// -- Check Permission
		if (bean.getMethod().equals(MethodType.CHECK_PERMISSION)) {
			IPrivacyDataManagerListener listener = privacyDataManagerlisteners.get(stanza.getId());
			privacyDataManagerlisteners.remove(stanza.getId());
			if (bean.isAck()) {
				listener.onAccessControlChecked(bean.getPermissions());
				if (null != bean.getPermissions() && bean.getPermissions().size() > 0) {
					listener.onAccessControlChecked(ResponseItemUtils.toResponseItem(bean.getPermissions().get(0)));
				}
			}
			else {
				listener.onAccessControlCancelled(bean.getAckMessage());
			}
			return;
		}

		// -- Obfuscate Data
		else if (bean.getMethod().equals(MethodType.OBFUSCATE_DATA)) {
			IDataObfuscationListener listener = dataObfuscationlisteners.get(stanza.getId());
			dataObfuscationlisteners.remove(stanza.getId());
			if (bean.isAck()) {
				listener.onObfuscationDone(bean.getDataWrapper());
			}
			else {
				listener.onObfuscationCancelled(bean.getAckMessage());
			}
			return;
		}
	}
}
