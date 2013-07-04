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
package org.societies.android.security;

import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.internal.privacytrust.trust.IInternalTrustClient;
import org.societies.android.api.security.digsig.IDigSigClient;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SecurityClientBase implements IDigSigClient {
	
	private static final String TAG = SecurityClientBase.class.getName();
	
	private final boolean restrictBroadcast;
	
	private Context androidContext;
	
	public SecurityClientBase(Context androidContext) {
		
    	this(androidContext, true);
    }
    
    public SecurityClientBase(Context androidContext, boolean restrictBroadcast) {
    	
    	Log.i(TAG, this.getClass().getName() + " instantiated");
    	
    	this.androidContext = androidContext;
    	this.restrictBroadcast = restrictBroadcast;
    }
    
	@Override
    public String signXml(String xml, String xmlNodeId) {

		Log.d(SecurityClientBase.TAG, "signXml: xmlNodeId = " + xmlNodeId);

		if (xml == null)
			throw new NullPointerException("xml can't be null");
		if (xmlNodeId == null)
			throw new NullPointerException("xmlNodeId can't be null");
		
		return xml;  // FIXME
	}

    @Override
    public void verifyXml(String xml) {
    	// TODO
    }
    
	@Override
	public boolean startService() {

		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
		SecurityClientBase.this.androidContext.sendBroadcast(intent);

		return true;
	}

	@Override
	public boolean stopService() {

		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
		SecurityClientBase.this.androidContext.sendBroadcast(intent);

		return true;
	}
    
	private void broadcastException(String client, String method, String message) {

		final Intent intent = new Intent(method);
		intent.putExtra(IInternalTrustClient.INTENT_EXCEPTION_KEY, message);
		if (this.restrictBroadcast)
			intent.setPackage(client); 
		this.androidContext.sendBroadcast(intent);
	}

	private void receiveResult(String returnIntent, String client, Object payload) {

		Log.d(SecurityClientBase.TAG, "receiveResult: payload=" + payload);

		final Intent intent = new Intent(returnIntent);
		boolean everythingOk = true;
		
		if (everythingOk) {
			// TrustBroker response bean
				intent.putExtra(IInternalTrustClient.INTENT_RETURN_VALUE_KEY, "");
		} else {
			Log.e(SecurityClientBase.TAG, "Received unexpected response bean in result: "
					+ ((payload != null) ? payload.getClass() : "null"));
			SecurityClientBase.this.broadcastException(client,
					returnIntent, 
					"Received unexpected response bean in result: "
					+ ((payload != null) ? payload.getClass() : "null"));
			return;
		}
		
		if (SecurityClientBase.this.restrictBroadcast)
			intent.setPackage(client);
		intent.addFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
		Log.d(SecurityClientBase.TAG, "receiveResult: broadcasting intent " + intent); 
		SecurityClientBase.this.androidContext.sendBroadcast(intent);
	}
}
