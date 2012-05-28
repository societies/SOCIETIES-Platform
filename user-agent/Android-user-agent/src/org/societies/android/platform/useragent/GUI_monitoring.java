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

import java.net.URI;
import java.net.URISyntaxException;

import org.societies.android.platform.useragent.UserAgent.LocalBinder;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.personalisation.model.Action;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.identity.IdentityManagerImpl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GUI_monitoring extends Activity implements OnClickListener{

	private static final String LOG_TAG = GUI_monitoring.class.getName();
	UserAgent uaService = null;
	boolean connectedToService = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//bind to UserAgent service
		this.bindToService();

		//set layout
		setContentView(R.layout.uam);

		Button sayHello = (Button)findViewById(R.id.sayHello);
		sayHello.setOnClickListener(this);

		Button sayGoodbye = (Button)findViewById(R.id.sayGoodbye);
		sayGoodbye.setOnClickListener(this);
	}

	public void onClick(View v){
		IIdentity identity;
		try {
			identity = IdentityManagerImpl.staticfromJid("sarah.societies.org");

			ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
			serviceId.setIdentifier(new URI("http://test/useragent/activity"));
			serviceId.setServiceInstanceIdentifier("http://test/useragent/activity");
			String serviceType = "tester";
			String paramName = "saying";

			if(v.getId() == R.id.sayHello){
				if(connectedToService){
					uaService.monitor(identity, new Action(serviceId, serviceType, paramName, "Hello!!"));
				}
			}else if(v.getId() == R.id.sayGoodbye){
				if(connectedToService){
					uaService.monitor(identity, new Action(serviceId, serviceType, paramName, "Goodbye!!"));	
				}		
			} 
		}catch (InvalidFormatException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}


	private void bindToService(){
		//Create intent to select service to bind to
		Intent bindIntent = new Intent(this, UserAgent.class);
		//bind to service
		bindService(bindIntent, uaConnection, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection uaConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			uaService = ((LocalBinder) service).getService();
			connectedToService = true;
			Log.d(LOG_TAG, "Monitoring GUI connected to User Agent service");
		}

		public void onServiceDisconnected(ComponentName className) {
			// As our service is in the same process, this should never be called
			connectedToService = false;
			Log.d(LOG_TAG, "Monitoring GUI disconnected from User Agent service");
		}
	};


}
