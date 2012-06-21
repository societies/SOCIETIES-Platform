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

package org.societies.android.platform.context;

//import org.societies.android.api.context.CtxException;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxEntity;
import org.societies.android.platform.context.ContextManagement.LocalBinder;

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

public class GUI_UserContext extends Activity implements OnClickListener{

	private static final String LOG_TAG = GUI_UserContext.class.getName();
	ContextManagement cmService = null;
	boolean connectedToService = false;
	CtxEntity entity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//bind to ContextManagement service
		this.bindToService();

		setContentView(R.layout.user_context);
		
		Button createEntity = (Button)findViewById(R.id.button3);
		createEntity.setOnClickListener(this);

		Button createAttribute = (Button)findViewById(R.id.button4);
		createAttribute.setOnClickListener(this);

//		setContentView(R.layout.create_entity);
		
//		Log.d(LOG_TAG, "Running Create Entity method.");

	}
	
	public void onClick(View v){
		
		try {
			if(v.getId() == R.id.button3){
				Log.d(LOG_TAG, "Running Create Entity method.");
				if(connectedToService){
					cmService.createEntity("person");
					Log.d(LOG_TAG, "Successfully Created Entity.");
				}
				else {
					Log.d(LOG_TAG, "Not Connected!!!");
				}
			} else if(v.getId() == R.id.button4){
				Log.d(LOG_TAG, "Running Create Attribute method.");
				if(connectedToService){
					entity = cmService.createEntity("house");
					cmService.createAttribute(entity.getId(), "flat");
					Log.d(LOG_TAG, "Successfully Created Attribute.");
				}
				else {
					Log.d(LOG_TAG, "Not Connected!!!");
				}
			} 
		}catch (CtxException e) {
			e.printStackTrace();
		} 
	}
	
/*	private void createEntity() {
		if(connectedToService){
			Log.d(LOG_TAG, "Running Create Entity method.");
			try {
				cmService.createEntity("person");
				Log.d(LOG_TAG, "Successfully Created Entity.");
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(LOG_TAG, "Or NotSuccessfully Created Entity.");
		}
		else {
			Log.d(LOG_TAG, "Not Connected!!!");
		}
	}
	*/
	private void bindToService(){
		//Create intent to select service to bind to
		Intent bindIntent = new Intent(this, ContextManagement.class);
		//bind to service
		bindService(bindIntent, cmConnection, Context.BIND_AUTO_CREATE);
	}
	
	private ServiceConnection cmConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			cmService = ((LocalBinder) service).getService();
			connectedToService = true;
			Log.d(LOG_TAG, "UserContext GUI connected to ContextManagement service");
		}

		public void onServiceDisconnected(ComponentName className) {
			// As our service is in the same process, this should never be called
			connectedToService = false;
			Log.d(LOG_TAG, "UserContext GUI disconnected from ContextManagement service");
		}
	};
	
}
