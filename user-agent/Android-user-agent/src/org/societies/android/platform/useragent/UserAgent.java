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

package org.societies.android.platform.useragent;

import java.util.Arrays;
import java.util.List;

import org.societies.api.identity.IIdentity;
import org.societies.api.personalisation.model.IAction;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class UserAgent extends Service /*implements IAndroidUserAgent*/{
	
	private static final String LOG_TAG = UserAgent.class.getName();
	private static final List<String> ELEMENT_NAMES = Arrays.asList("userActionMonitorBean");
    private static final List<String> NAME_SPACES = Arrays.asList(
    		"http://societies.org/api/schema/useragent/monitoring");
    private static final List<String> PACKAGES = Arrays.asList(
		"org.societies.api.schema.useragent.monitoring");
    
    //currently hard coded but should be injected
    private static final String DESTINATION = "xcmanager.societies.local";
        
    private IIdentity toXCManager = null;
    //private ClientCommunicationMgr ccm;

	private IBinder binder = null;
	
	@Override
	public void onCreate () {
		
		this.binder = new LocalBinder();

		//this.ccm = new ClientCommunicationMgr(this);
		
    	/*try {
			toXCManager = IdentityManagerImpl.staticfromJid(DESTINATION);
		} catch (InvalidFormatException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			throw new RuntimeException(e);
		} */    
		Log.d(LOG_TAG, "User Agent service starting");
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "User Agent service terminating");
	}

	/**
	 * Create Binder object for local service invocation
	 */
	 public class LocalBinder extends Binder {
		 public UserAgent getService() {
	            return UserAgent.this;
	        }
	    }
	
	@Override
	public IBinder onBind(Intent arg0) {
		return this.binder;
	}
	
	public void monitor(IIdentity identity, IAction action){
		Log.d(LOG_TAG, "User Agent received monitored user action: "+action.getparameterName()+" = "+action.getvalue());
	}
}
