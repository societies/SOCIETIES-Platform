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
package org.societies.android.platform.comms.container;

import org.societies.android.api.comms.XMPPAgent;
import org.societies.android.platform.comms.AndroidCommsBase;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * This wrapper class acts as a local wrapper for the Android Communications (XMPP) service.
 * It uses the base service implementation {@link AndroidCommsBase} provide the service functionality
 */

public class ServicePlatformCommsTest extends Service {
	
    private static final String LOG_TAG = ServicePlatformCommsTest.class.getName();
    private IBinder binder = null;
    private AndroidCommsBase serviceBase;
    
    @Override
	public void onCreate () {
    	serviceBase = new AndroidCommsBase(ServicePlatformCommsTest.this, false);
    	
		this.binder = new TestPlatformCommsBinder(serviceBase);
		Log.d(LOG_TAG, "ServicePlatformCommsTest service starting");
	}

	/**Create Binder object for local service invocation */
	public class TestPlatformCommsBinder extends Binder {
		private AndroidCommsBase service;
		
		TestPlatformCommsBinder(AndroidCommsBase service) {
			super();
			this.service = service;
		}
		public XMPPAgent getService() {
			
			return this.service;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(LOG_TAG, "Return Binder for intent: " + intent.getAction());
		return this.binder;
	}
	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "ServicePlatformCommsRemote terminating");
		serviceBase.serviceCleanup();
	}

}
