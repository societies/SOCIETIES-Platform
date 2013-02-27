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
package org.societies.platform.slm.container;


import java.lang.ref.WeakReference;
import org.societies.android.platform.servicemonitor.ServiceManagementBase;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * This wrapper class acts as a local wrapper for the Service Utilities Android service.
 * It uses the base service implementation {@link ServiceUtilitiesBase} provide the service functionality
 * This service is purely for testing purposes but will exercise the base service functionality
 */
public class ServiceManagementTest extends Service {
	
    private static final String LOG_TAG = ServiceManagementTest.class.getName();
    private LocalSLMBinder binder = null;
    
    @Override
	public void onCreate () {
		this.binder = new LocalSLMBinder(new ServiceManagementBase(this, false));
		Log.d(LOG_TAG, "ServiceManagementTest service starting");
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "TestServiceCSSManagerLocal service terminating");
	}

	
	@Override
	public IBinder onBind(Intent arg0) {
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
	public static class LocalSLMBinder extends Binder {
		private WeakReference<ServiceManagementBase> outerClassReference = null;
		 
		public LocalSLMBinder(ServiceManagementBase instance) {
			super();
			this.outerClassReference = new WeakReference<ServiceManagementBase>(instance);
		}
				 
		public ServiceManagementBase getService() {
			return outerClassReference.get();
		}
	}
}
