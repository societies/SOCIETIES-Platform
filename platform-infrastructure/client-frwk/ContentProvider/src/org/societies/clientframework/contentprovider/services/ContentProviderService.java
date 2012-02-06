/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.clientframework.contentprovider.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.android.platform.interfaces.IContentProvider;
import org.societies.android.platform.interfaces.ServiceMethodTranslator;
import org.societies.clientframework.contentprovider.Constants;
import org.societies.clientframework.contentprovider.Settings;
import org.societies.clientframework.contentprovider.database.StoreResultsDB;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class ContentProviderService extends Service implements IContentProvider {

	private Messenger inMessenger;
	StoreResultsDB  storeDB;
	
	
	@Override
	public void onCreate () {
		this.inMessenger = new Messenger(new IncomingHandler());	
		Log.i(this.getClass().getName(), "CONTENT PROVIDER Service starting");
		 
		storeDB = new StoreResultsDB(this);
	    Log.v(Constants.TAG, "DB initialized to store societies data");
		
	}
	
	class IncomingHandler extends Handler {
		
		@Override
		public void handleMessage(Message message) {
			String targetMethod = ServiceMethodTranslator.getMethodSignature(IContentProvider.methodsArray, message.what);
			
			if (targetMethod != null) {
				try {
					Log.d(this.getClass().getName(), "Target method: " + targetMethod);
					
					Class parameters [] = ServiceMethodTranslator.getParameterClasses(targetMethod);
					for (Class element : parameters) {
						Log.d(this.getClass().getName(), "Target method param types: " + element.getName());
			
					}
								
					Method method = ContentProviderService.this.getClass().getMethod(ServiceMethodTranslator.getMethodName(IContentProvider.methodsArray, message.what), parameters);
					Log.d(this.getClass().getName(), "Found method: " + method.getName());
					try {
						Object params [] = new Object [ServiceMethodTranslator.getParameterNumber(targetMethod)];
						Log.d(this.getClass().getName(),"Number of parameters: " + params.length); 

						String paramTypeList [] = ServiceMethodTranslator.getMethodParameterTypesCapitalised(targetMethod);

						String paramNameList [] = ServiceMethodTranslator.getMethodParameterNames(targetMethod);
						for (int i = 0; i < paramTypeList.length; i++) {
							Class bundleParam [] = {String.class};
							Object bundleValue [] = {paramNameList[i]};
							Method bundleMethod = Bundle.class.getMethod("get" + paramTypeList[i], bundleParam);
							
							params[i] = bundleMethod.invoke(message.getData(), bundleValue);
							Log.d(this.getClass().getName(), "parameter i = " + i + " value: " + params[i]);
						}
						method.invoke(ContentProviderService.this, params);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void onDestroy() {
		
		storeDB.close();
		Log.i(this.getClass().getName(), "Service terminating");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return inMessenger.getBinder();
	}

	public void setCredential(String username, String password, String serviceName) {
		ContentValues map  = new ContentValues();
		map.put(Constants.TABLE_KEY, 		IContentProvider.CREDENTIAL_USERNAME);
		map.put(Constants.TABLE_SERVICE, 	IContentProvider.SERVICE_COMM_FWK);
		map.put(Constants.TABLE_TYPE, 		username.getClass().toString());
		map.put(Constants.TABLE_VALUE, 		username);
		storeDB.addElementInTable(map);
		
		map  = new ContentValues();
		map.put(Constants.TABLE_KEY, 		IContentProvider.CREDENTIAL_PASSWORD);
		map.put(Constants.TABLE_SERVICE, 	IContentProvider.SERVICE_COMM_FWK);
		map.put(Constants.TABLE_TYPE, 		password.getClass().toString());
		map.put(Constants.TABLE_VALUE, 		password);
		storeDB.addElementInTable(map);
		
	}

	public List<?> getCredential(String serviceName) {
		Map<String, ?> data = storeDB.getData(serviceName);
		ArrayList <Object> credential = new ArrayList<Object>();
		credential.add(data.get(IContentProvider.CREDENTIAL_USERNAME));
		credential.add(data.get(IContentProvider.CREDENTIAL_PASSWORD));
		return credential;
	}

	public void setCommFwkEndpoint(String hostname, int port) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(IContentProvider.COMM_FWK_HOSTNAME, hostname);
		map.put(IContentProvider.COMM_FWK_PORT, port);
		storeDB.storeData(map, IContentProvider.SERVICE_COMM_FWK);
	}

	public List<?> getCommFwkEndpoint() {
		Map<String, ?> data = storeDB.getData(IContentProvider.SERVICE_COMM_FWK);
		ArrayList <Object> endpoint = new ArrayList<Object>();
		endpoint.add( data.get(IContentProvider.COMM_FWK_HOSTNAME));
		endpoint.add( data.get(IContentProvider.COMM_FWK_PORT));
		return endpoint;
	}

	public void storeData(Map<String, ?> data, String serviceName) {
		storeDB.storeData(data, serviceName);
		
	}

	public Map<String, ?> getData(String serviceName) {
		return storeDB.getData(serviceName);
	}

	public String[] getServices() {
		return storeDB.getServices();
	}

	public void resetDB(){
		storeDB.resetDB(Settings.DATABASE_NAME);
	}

	

}
