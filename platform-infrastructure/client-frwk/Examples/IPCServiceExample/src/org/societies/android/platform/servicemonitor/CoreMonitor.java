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
package org.societies.android.platform.servicemonitor;

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.internal.examples.AndroidParcelable;
import org.societies.android.api.internal.servicemonitor.ICoreServiceMonitor;
import org.societies.android.api.utilities.RemoteServiceHandler;


import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

public class CoreMonitor extends Service implements ICoreServiceMonitor {
	private Messenger inMessenger;
	private static final int MAX_SERVICES = 30;
	private static final int MAX_TASKS = 30;
	
	public static final String ACTIVE_SERVICES = "org.societies.android.platform.servicemonitor.ACTIVE_SERVICES";
	public static final String ACTIVE_TASKS = "org.societies.android.platform.servicemonitor.ACTIVE_TASKS";
	public static final String GET_NODE_DETAILS = "org.societies.android.platform.servicemonitor.GET_NODE_DETAILS";
	public static final String INTENT_RETURN_KEY = "org.societies.android.platform.servicemonitor.ReturnValue";
	
	private static final String LOG_TAG = CoreMonitor.class.getName();
	
	@Override
	public void onCreate () {
		this.inMessenger = new Messenger(new RemoteServiceHandler(this.getClass(), this, ICoreServiceMonitor.methodsArray));
		Log.i(LOG_TAG, "Service starting");
	}
	
//	class IncomingHandler extends Handler {
//		
//		@Override
//		public void handleMessage(Message message) {
//			String targetMethod = ServiceMethodTranslator.getMethodSignature(ICoreServiceMonitor.methodsArray, message.what);
//			
//			if (targetMethod != null) {
//				try {
//					Log.d(LOG_TAG, "Target method: " + targetMethod);
//					
//					Class parameterClasses [] = ServiceMethodTranslator.getParameterClasses(targetMethod);
//					for (Class element : parameterClasses) {
//						Log.d(LOG_TAG, "Target method param types: " + element.getName());
//			
//					}
//								
//					Method method = CoreMonitor.this.getClass().getMethod(ServiceMethodTranslator.getMethodName(ICoreServiceMonitor.methodsArray, message.what), parameterClasses);
//					Log.d(LOG_TAG, "Found method: " + method.getName());
//					try {
//						Object params [] = new Object [ServiceMethodTranslator.getParameterNumber(targetMethod)];
//						Log.d(LOG_TAG,"Number of parameters: " + params.length); 
//
//						String paramTypeList [] = ServiceMethodTranslator.getMethodParameterTypesCapitalised(targetMethod);
//
//						for (String type : paramTypeList) {
//							Log.i(LOG_TAG, "Parameter type: " + type);
//						}
//						
//						String paramNameList [] = ServiceMethodTranslator.getMethodParameterNames(targetMethod);
//						
//						//unless the class loader is set bad things happen
//						Bundle bundle = message.getData();
//						bundle.setClassLoader(getClassLoader());
//						
//						for (int i = 0; i < paramTypeList.length; i++) {
//							Class bundleParam [] = {String.class};
//							Log.i(LOG_TAG, "param list: " + paramNameList[i]);
//							Object bundleValue [] = {paramNameList[i]};
//							
//							Method bundleMethod = null;
//
//							if (implementsParcelable(parameterClasses[i])) {
//								Log.i(LOG_TAG, "Class: " + parameterClasses[i] + " is an instance of Parcelable");
//								bundleMethod = Bundle.class.getMethod("getParcelable", bundleParam);
//							} else {
//								bundleMethod = Bundle.class.getMethod("get" + paramTypeList[i], bundleParam);
//							}
//							Log.i(LOG_TAG, "Method invoked: " + bundleMethod.getName());
//							
//							params[i] = bundleMethod.invoke(bundle, bundleValue);
//							Log.d(LOG_TAG, "parameter i = " + i + " value: " + params[i]);
//						}
//						method.invoke(CoreMonitor.this, params);
//					} catch (IllegalArgumentException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IllegalAccessException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (InvocationTargetException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} 
//				} catch (SecurityException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (NoSuchMethodException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//		/**
//		 * Determine if a class implements the Parcelable interface
//		 * @param clazz
//		 * @return boolean
//		 */
//		private boolean implementsParcelable(Class clazz) {
//			boolean retValue = false;
//			
//			Class interfaces [] = clazz.getInterfaces();
//			for (Class interfaze : interfaces) {
//				Log.i(LOG_TAG, "interface: " + interfaze.getSimpleName());
//				if (interfaze.getSimpleName().equals("Parcelable")) {
//					retValue = true;
//					break;
//				}
//			}
//			
//			return retValue;
//		}
//	}

	@Override
	public IBinder onBind(Intent arg0) {
		return inMessenger.getBinder();
	}
	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "Service terminating");
	}
	/**
	 * {@link ICoreServiceMonitor}
	 * 
	 * @return List - maybe empty if no services found
	 */
	public List<ActivityManager.RunningServiceInfo> activeServices(String client) {
		ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		/**
		 * This can also be achieved using the internal IActivityManager interface and ActivityManagerNative class
		 * IActivityManager manager = ActivityManagerNative.getDefault();
		 * manager.getTasks
		 * 
		 * but it requires access to hidden and internal components
		 */
		List <ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(MAX_SERVICES);

		/**
		 * Create intent to broadcast results to interested receivers
		 */
		Intent intent = new Intent(ACTIVE_SERVICES);
		int i = 0;
		ActivityManager.RunningServiceInfo arrayServices [] = new ActivityManager.RunningServiceInfo[runningServices.size()];
		
		for (ActivityManager.RunningServiceInfo service : runningServices) {
			arrayServices[i] = service;
			i++;
		}
		intent.putExtra(INTENT_RETURN_KEY, arrayServices);
		/**
		 * Intentionally restricting potential intent receiver to client 
		 */
		intent.setPackage(client);
		Log.d(LOG_TAG, "Intent package name: " + client);
		this.sendBroadcast(intent);
		
		for (ActivityManager.RunningServiceInfo service : runningServices) {
			
			Log.d(LOG_TAG, "Service name: " + service.service.flattenToString());
		}
		
		return runningServices;
	}

	/**
	 * {@link ICoreServiceMonitor}
	 * 
	 * @return List - maybe empty if no services found
	 */
	public List<ActivityManager.RunningServiceInfo> activeServices(String client, String serviceFilter) {
		List <ActivityManager.RunningServiceInfo> filteredServices = new ArrayList<ActivityManager.RunningServiceInfo>();
		List <ActivityManager.RunningServiceInfo> runningServices = this.activeServices(client);
		for (ActivityManager.RunningServiceInfo service : runningServices) {
			if (service.service.flattenToString().contains(serviceFilter)) {
				filteredServices.add(service);
			}
		}
		return filteredServices; 
	}

	/**
	 * {@link ICoreServiceMonitor}
	 * 
	 * @return List - maybe empty if no tasks found
	 */
	public List<ActivityManager.RunningTaskInfo> activeTasks(String client) {
		ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List <ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(MAX_TASKS);		
		
		/**
		 * Create intent to broadcast results to interested receivers
		 */
		Intent intent = new Intent(ACTIVE_TASKS);
		int i = 0;
		ActivityManager.RunningTaskInfo arrayTasks [] = new ActivityManager.RunningTaskInfo[runningTasks.size()];
		
		for (ActivityManager.RunningTaskInfo task : runningTasks) {
			arrayTasks[i] = task;
			i++;
		}

		
		intent.putExtra(INTENT_RETURN_KEY, arrayTasks);
		this.sendBroadcast(intent);

		for (ActivityManager.RunningTaskInfo task : runningTasks) {
			Log.d(LOG_TAG, "Task name: " + task.baseActivity.flattenToString());
		}
		return runningTasks;
	}

	/**
	 * {@link ICoreServiceMonitor}
	 * 
	 * @return List - maybe empty if no services found
	 */
	public List<ActivityManager.RunningTaskInfo> activeTasks(String client, String taskFilter) {
		List <ActivityManager.RunningTaskInfo> filteredTasks = new ArrayList<ActivityManager.RunningTaskInfo>();
		List <ActivityManager.RunningTaskInfo> runningTasks = this.activeTasks(client);
		for (ActivityManager.RunningTaskInfo task : runningTasks) {
			if (task.baseActivity.flattenToString().contains(taskFilter)) {
				filteredTasks.add(task);
			}
		}
		return filteredTasks; 
	}

	@Override
	public AndroidParcelable getNodeDetails(String client, org.societies.android.api.internal.examples.AndroidParcelable node) {
		Log.d(LOG_TAG, "getNodeDetails being invoked");
		Log.d(LOG_TAG, "client: " + client);
		Log.d(LOG_TAG, "node: " + node.getIdentity());
		
		node.setIdentity("some@one.com");
		node.setStatus(1);
		node.setType(2);
		
		/**
		 * Create intent to broadcast results to interested receivers
		 */
		if (client != null) {
			Intent intent = new Intent(GET_NODE_DETAILS);

			
			intent.putExtra(INTENT_RETURN_KEY, node);
			this.sendBroadcast(intent);
			
		}

		return node;
	}


	public boolean stopService(String client, String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean startActivity(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean startService(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean stopActivity(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

		


}
