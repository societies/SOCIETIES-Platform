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
package org.societies.android.privacytrust.policymanagement.callback;


import java.io.Serializable;

import org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager;
import org.societies.android.privacytrust.callback.PrivacyIntentSender;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.PrivacyPolicyManagerBeanResult;

import android.content.Context;
import android.content.Intent;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyPolicyIntentSender extends PrivacyIntentSender {
	public PrivacyPolicyIntentSender(Context context) {
		super(context);
		TAG = PrivacyPolicyIntentSender.class.getSimpleName();
		returnStatusKey = IPrivacyPolicyManager.INTENT_RETURN_STATUS_KEY;
		returnStatusMsgKey = IPrivacyPolicyManager.INTENT_RETURN_STATUS_MSG_KEY;
		returnValueKey = IPrivacyPolicyManager.INTENT_RETURN_VALUE_KEY;
	}

	public boolean sendIntentGetPrivacyPolicy(String clientPackage, PrivacyPolicyManagerBeanResult bean) {
		Intent intent = prepareIntent(clientPackage, MethodType.GET_PRIVACY_POLICY.name(), bean.isAck(), bean.getAckMessage());
		intent.putExtra(IPrivacyPolicyManager.INTENT_RETURN_VALUE_KEY, (Serializable) bean.getPrivacyPolicy());
		context.sendBroadcast(intent);
		return true;
	}

	public boolean sendIntentSuccess(String clientPackage, PrivacyPolicyManagerBeanResult bean) {
		Intent intent = prepareIntent(clientPackage, bean.getMethod().name(), bean.isAck(), bean.getAckMessage());
		if (bean.getMethod().name().equals(MethodType.GET_PRIVACY_POLICY.name())) {
			intent.putExtra(IPrivacyPolicyManager.INTENT_RETURN_VALUE_KEY, (Serializable) bean.getPrivacyPolicy());
		}
		context.sendBroadcast(intent);
		return true;
	}

	public boolean sendIntentGetPrivacyPolicy(String clientPackage, RequestPolicy privacyPolicy) {
		Intent intent = prepareIntent(clientPackage, MethodType.GET_PRIVACY_POLICY.name(), true, null);
		intent.putExtra(IPrivacyPolicyManager.INTENT_RETURN_VALUE_KEY, (Serializable) privacyPolicy);
		context.sendBroadcast(intent);
		return true;
	}

	public boolean sendIntentSuccess(String clientPackage, String action) {
		Intent intent = prepareIntent(clientPackage, action, true, null);
		context.sendBroadcast(intent);
		return true;
	}

	public boolean sendIntentError(String clientPackage, String action, String errorMsg) {
		Intent intent = prepareIntent(clientPackage, action, false, errorMsg);
		context.sendBroadcast(intent);
		return true;
	}
}
