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
package org.societies.android.privacytrust.datamanagement.callback;

import org.societies.android.api.internal.privacytrust.IPrivacyDataManager;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBeanResult;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyDataIntentSender {
	private final static String TAG = PrivacyDataIntentSender.class.getSimpleName();
	
	private Context context;
	private String clientPackage;
	
	public PrivacyDataIntentSender(Context context, String clientPackage) {
		this.context = context;
		this.clientPackage = clientPackage;
	}
	
	public boolean sendIntentCheckPermission(PrivacyDataManagerBeanResult bean) {
		Intent intent = prepareIntent(MethodType.CHECK_PERMISSION, bean.isAck(), bean.getAckMessage());
		intent.putExtra(IPrivacyDataManager.INTENT_RETURN_VALUE_KEY, bean.getPermission());
		context.sendBroadcast(intent);
		return true;
	}
	
	public boolean sendIntentCheckPermission(ResponseItem privacyPermission) {
		Intent intent = prepareIntent(MethodType.CHECK_PERMISSION, true, null);
		intent.putExtra(IPrivacyDataManager.INTENT_RETURN_VALUE_KEY, privacyPermission);
		context.sendBroadcast(intent);
		return true;
	}
	
	public boolean sendIntentCheckPermission(String errorMsg) {
		Intent intent = prepareIntent(MethodType.CHECK_PERMISSION, false, errorMsg);
		context.sendBroadcast(intent);
		return true;
	}
	
	
	private Intent prepareIntent(MethodType action, boolean ack, String ackMessage) {
		return prepareIntent(action.name(), ack, ackMessage);
	}
	
	private Intent prepareIntent(String action, boolean ack, String ackMessage) {
		Log.d(TAG, "Send Intent("+action+") to "+clientPackage+": "+(ack ? "Success" : "Error "+(null != ackMessage ? ackMessage : "")));
		Intent intent = new Intent();
		intent.setPackage(clientPackage);
		intent.setAction(action);
		intent.putExtra(IPrivacyDataManager.INTENT_RETURN_STATUS_KEY, ack);
		if (null != ackMessage) {
			intent.putExtra(IPrivacyDataManager.INTENT_RETURN_STATUS_MSG_KEY, ackMessage);
		}
		return intent;
	}
}
