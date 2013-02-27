/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM Corp., 
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
package org.societies.android.platform.personalisation.container.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.societies.android.api.personalisation.IPersonalisationManagerAndroid;
import org.societies.android.platform.personalisation.container.PersonalisationAndroidTest;
import org.societies.android.platform.personalisation.container.PersonalisationAndroidTest.TestPersonalisationBinder;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * @author Eliza
 *
 */
public class TestPersonalisation extends ServiceTestCase <PersonalisationAndroidTest>{

	private static final String LOG_TAG = TestPersonalisation.class.getName();
	private static final String CLIENT_ID = "org.societies.android.platform.personalisation.test";

	private Boolean receivedResult = false;
	
	public TestPersonalisation() {
		super(PersonalisationAndroidTest.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	private class PersonalisationBroadcastReceiver extends BroadcastReceiver{
		private final String LOG_TAG = PersonalisationBroadcastReceiver.class.getName();
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			TestPersonalisation.this.receivedResult = true;
			assertNotNull(intent.getParcelableExtra(IPersonalisationManagerAndroid.INTENT_RETURN_VALUE));
			Log.d(LOG_TAG, "OnReceive finished");
		}

	}
	
	@MediumTest
	public void testGetPreference() throws URISyntaxException{
		BroadcastReceiver receiver = setupBroadcastReceiver();
		Intent personalisationIntent = new Intent(getContext(), PersonalisationAndroidTest.class);
		
		TestPersonalisationBinder binder =  (TestPersonalisationBinder) bindService(personalisationIntent);
		
		
		IPersonalisationManagerAndroid persoService = binder.getService();
		
		ServiceResourceIdentifier serviceID = new ServiceResourceIdentifier();
		serviceID.setIdentifier(new URI("http://service.societies.local"));
		serviceID.setServiceInstanceIdentifier("service_1232");
		RequestorServiceBean requestorService = new RequestorServiceBean();
		requestorService.setRequestorId("service@societies.local");
		requestorService.setRequestorServiceId(serviceID);
		persoService.getPreference(CLIENT_ID, requestorService, "emma@societies.local", "media", serviceID, "volume");
		
		while (!this.receivedResult){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Log.d(LOG_TAG, "Received result");
	}
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
    	BroadcastReceiver receiver = null;
    	
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        receiver = new PersonalisationBroadcastReceiver();
        getContext().registerReceiver(receiver, createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    
	
    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createTestIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction(IPersonalisationManagerAndroid.GET_INTENT_ACTION);
        intentFilter.addAction(IPersonalisationManagerAndroid.GET_PREFERENCE);
        intentFilter.addAction(IPersonalisationManagerAndroid.INTENT_RETURN_VALUE);
        return intentFilter;
    }
}
