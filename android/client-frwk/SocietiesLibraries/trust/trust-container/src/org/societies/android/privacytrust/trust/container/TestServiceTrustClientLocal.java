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
package org.societies.android.privacytrust.trust.container;

import java.lang.ref.WeakReference;

import org.societies.android.privacytrust.trust.TrustClientBase;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * This wrapper class acts as a test wrapper for the Trust Client Android
 * service. It uses the base service implementation 
 * {@link TrustClientBase} to provide the service functionality.
 */
public class TestServiceTrustClientLocal extends Service {

	private static final String TAG = TestServiceTrustClientLocal.class.getName();
	
	private LocalTrustClientBinder binder;

	/*
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate () {
		
		Log.d(TAG, "onCreate");
		this.binder = new LocalTrustClientBinder();
		// inject reference to current service
		this.binder.addouterClassreference(new TrustClientBase(this));
	}

	/*
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		
		Log.d(TAG, "onDestroy");
	}

	/*
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		
		return this.binder;
	}

	/**
	 * Create Binder object for local service invocation
	 * 
	 * N.B. In order to prevent the exporting of the Service (outer class) via the
	 * Binder extended class, the Binder reference to the service object is via 
	 * a {@link WeakReference} instead of the normal inner class "strong" reference.
	 * This allows the service (outer) class object to be garbage collected (GC) when it
	 * ceases to exist. Using a "strong" reference prevents the GC removing the object as
	 * any clients that have a Binder reference, indirectly hold the Service object reference.
	 * This prevents a common Android Service memory leak.
	 */
	public static class LocalTrustClientBinder extends Binder {
		
		private WeakReference<TrustClientBase> outerClassReference = null;

		public void addouterClassreference(TrustClientBase instance) {
			
			this.outerClassReference = new WeakReference<TrustClientBase>(instance);
		}

		public TrustClientBase getService() {
			
			return outerClassReference.get();
		}
	}
}