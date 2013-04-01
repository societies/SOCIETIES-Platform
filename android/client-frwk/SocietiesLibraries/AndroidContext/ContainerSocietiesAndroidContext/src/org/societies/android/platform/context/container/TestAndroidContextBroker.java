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
package org.societies.android.platform.context.container;

import java.lang.ref.WeakReference;

import org.societies.android.platform.context.ContextBrokerBase;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * This wrapper class acts as a test wrapper for the ContextBroker Android service.
 * It uses the base service implementation {@link ContextBrokerBase} to provide the service functionality
 *
 * @author pkosmides
 */
public class TestAndroidContextBroker extends Service{

	private static final String LOG_TAG = TestAndroidContextBroker.class.getName();
	private TestContextBrokerBinder binder;
	
	@Override
	public IBinder onBind(Intent intent) {

		return this.binder;
	}

	/* ONE WAY */
    @Override
	public void onCreate () {
//		this.binder = new TestContextBrokerBinder(new ContextBrokerBase(this, false));
    	this.binder = new TestContextBrokerBinder();
    	this.binder.addouterClassreference(new ContextBrokerBase(this));
		Log.d(LOG_TAG, "TestAndroidContextBroker service starting");
	}
	
	/*THE OTHER WAY
    @Override
	public void onCreate () {
		this.binder = new TestContextBrokerBinder();
		Log.d(LOG_TAG, "TestAndroidContextBroker service starting");
	}*/

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "TestAndroidContextBroker service terminating");
	}

	/*ONE WAY */
	public static class TestContextBrokerBinder extends Binder {
		private WeakReference<ContextBrokerBase> outerClassReference = null;
		
		public void addouterClassreference(ContextBrokerBase instance) {
			this.outerClassReference = new WeakReference<ContextBrokerBase>(instance);
		}
		
		public ContextBrokerBase getService() {
			return outerClassReference.get();
		}
	}
	
	
	/*THE OTHER WAY
	//Create Binder object for local service invocation 
	public class TestContextBrokerBinder extends Binder {
		public ICtxClient getService(){
//			ClientCommunicationMgr ccm = createClientCommunicationMgr();
			ContextBrokerBase ctxBroker = new ContextBrokerBase(TestAndroidContextBroker.this, createCCM(), false);
			return ctxBroker;
		}
	}

	protected ClientCommunicationMgr createCCM() {
		return new ClientCommunicationMgr(this, true);
	}*/
//	protected ClientCommunicationMgr createClientCommunicationMgr() {
//		return new MockClientCommunicationMgr(getApplicationContext(), "emma", "societies.local");
//	}
}
