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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.internal.servicemonitor.ICoreServiceMonitor;
import org.societies.android.api.internal.servicemonitor.InstalledAppInfo;
import org.societies.utilities.DBC.Dbc;


import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

/**
 * This Android Service monitors services and tasks and can optionally filter
 * for given filter values.
 *
 */
public class CoreServiceMonitor extends Service implements ICoreServiceMonitor {
	//Logging tag
	private static final String LOG_TAG = CoreServiceMonitor.class.getName();
	
	private static final int MAX_SERVICES = 30;
	private static final int MAX_TASKS = 30;
	
	/**
	 * CoreServiceMonitor intents
	 * Used to create to create Intents to signal return values of a called method
	 * If the method is locally bound it is possible to directly return a value but is discouraged
	 * as called methods usually involve making asynchronous calls. 
	 */
	public static final String ACTIVE_SERVICES = "org.societies.android.platform.servicemonitor.ACTIVE_SERVICES";
	public static final String ACTIVE_FILTERED_SERVICES = "org.societies.android.platform.servicemonitor.ACTIVE_FILTERED_SERVICES";
	public static final String ACTIVE_TASKS = "org.societies.android.platform.servicemonitor.ACTIVE_TASKS";
	public static final String ACTIVE_FILTERED_TASKS = "org.societies.android.platform.servicemonitor.ACTIVE_FILTERED_TASKS";
	public static final String INSTALLED_APPLICATIONS = "org.societies.android.platform.servicemonitor.INSTALLED_APPLICATIONS";
	public static final String START_ACTIVITY = "org.societies.android.platform.servicemonitor.START_ACTIVITY";
	public static final String INTENT_RETURN_KEY = "org.societies.android.platform.servicemonitor.ReturnValue";


	private IBinder binder = null;

	@Override
	public void onCreate () {
		this.binder = new LocalBinder();

		Log.d(LOG_TAG, "CoreServiceMonitor service starting");
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "CoreServiceMonitor service terminating");
	}

	/**
	 * Create Binder object for local service invocation
	 */
	public class LocalBinder extends Binder {
		public CoreServiceMonitor getService() {
			return CoreServiceMonitor.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return this.binder;
	}

	//Service API
	public List<RunningServiceInfo> activeServices(String client) {
		Log.d(LOG_TAG, "Calling activeServices");
		Dbc.require("client cannot be null", client != null && client.length() > 0);
		
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
		 * If client is not null then broadcast result as an intent 
		 */
		if (client != null) {
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
			Log.d(this.getClass().getName(), "Intent package name: " + client);
			this.sendBroadcast(intent);
			
		}
		
		for (ActivityManager.RunningServiceInfo service : runningServices) {
			
			Log.d(this.getClass().getName(), "Service name: " + service.service.flattenToString());
		}
		
		return null;
	}
	
	public List<RunningServiceInfo> activeServices(String client, String serviceFilter) {
		Log.d(LOG_TAG, "Calling activeServices with filter: " + serviceFilter);
		
		Dbc.require("client cannot be null", client != null && client.length() > 0);
		Dbc.require("serviceFilter cannot be null", serviceFilter != null && serviceFilter.length() > 0);

		List <ActivityManager.RunningServiceInfo> filteredServices = new ArrayList<ActivityManager.RunningServiceInfo>();
		List <ActivityManager.RunningServiceInfo> runningServices = this.activeServices(client);
		for (ActivityManager.RunningServiceInfo service : runningServices) {
			if (service.service.flattenToString().contains(serviceFilter)) {
				filteredServices.add(service);
			}
		}
		/**
		 * If client is not null then broadcast result as an intent 
		 */
		if (client != null) {
			/**
			 * Create intent to broadcast results to interested receivers
			 */
			Intent intent = new Intent(ACTIVE_FILTERED_SERVICES);
			int i = 0;
			ActivityManager.RunningServiceInfo arrayServices [] = new ActivityManager.RunningServiceInfo[filteredServices.size()];
			
			for (ActivityManager.RunningServiceInfo service : filteredServices) {
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
			
		}

		
		return filteredServices; 
	}

	public List<RunningTaskInfo> activeTasks(String client) {
		Log.d(LOG_TAG, "Calling activeTasks");
		
		Dbc.require("client cannot be null", client != null && client.length() > 0);
		
		ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List <ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(MAX_TASKS);		
		
		/**
		 * If client is not null then broadcast result as an intent 
		 */
		if (client != null) {
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
			intent.setPackage(client);
			Log.d(LOG_TAG, "Intent package name: " + client);
			this.sendBroadcast(intent);
			
		}

		for (ActivityManager.RunningTaskInfo task : runningTasks) {
			Log.d(this.getClass().getName(), "Task name: " + task.baseActivity.flattenToString());
		}
		return runningTasks;
	}

	public List<RunningTaskInfo> activeTasks(String client, String taskFilter) {
		Log.d(LOG_TAG, "Calling activeTasks with filter: " + taskFilter);
		
		Dbc.require("client cannot be null", client != null && client.length() > 0);
		Dbc.require("taskFilter cannot be null", taskFilter != null && taskFilter.length() > 0);
		
		List <ActivityManager.RunningTaskInfo> filteredTasks = new ArrayList<ActivityManager.RunningTaskInfo>();
		List <ActivityManager.RunningTaskInfo> runningTasks = this.activeTasks(client);
		for (ActivityManager.RunningTaskInfo task : runningTasks) {
			if (task.baseActivity.flattenToString().contains(taskFilter)) {
				filteredTasks.add(task);
			}
		}
		/**
		 * If client is not null then broadcast result as an intent 
		 */
		if (client != null) {
			/**
			 * Create intent to broadcast results to interested receivers
			 */
			Intent intent = new Intent(ACTIVE_FILTERED_TASKS);
			int i = 0;
			ActivityManager.RunningTaskInfo arrayTasks [] = new ActivityManager.RunningTaskInfo[filteredTasks.size()];
			
			for (ActivityManager.RunningTaskInfo task : filteredTasks) {
				arrayTasks[i] = task;
				i++;
			}

			
			intent.putExtra(INTENT_RETURN_KEY, arrayTasks);
			this.sendBroadcast(intent);
			
		}
		return filteredTasks; 
	}

	/* @see org.societies.android.api.internal.servicemonitor.ICoreServiceMonitor#getInstalledApplications(java.lang.String)*/
	public InstalledAppInfo[] getInstalledApplications(String client) {
		Log.d(LOG_TAG, "Calling getInstalledApplications from client: " + client);
		
		List<InstalledAppInfo> apps = getInstalledApps(false, "org.societies.thirdpartyservice"); /* false = no system packages */
		List<InstalledAppInfo> appsIbm = getInstalledApps(false, "com.ibm.hrl.ms.pz"); //HARDCODING FOR Context Aware Wall AS PACKAGE NAME DOES NOT MATCH
		
		int countIBM = 0;
		if (appsIbm.size() > 0)
			countIBM = 1;
		//DECLARE RETURNING ARRAY
		Parcelable returnArray[] = new Parcelable[apps.size() + countIBM];
		for (int i=0; i<apps.size(); i++) {
			InstalledAppInfo tmpApp = apps.get(i); 
			returnArray[i] = tmpApp;
		}
		//ADD Context Aware Wall IF FOUND - ASSUME FIRST POSITION
		if (appsIbm.size() > 0) {
			InstalledAppInfo tmpApp = appsIbm.get(0);
			returnArray[returnArray.length-1] = tmpApp;
		}
		//SETUP RETURN INTENT STUFF
		if (client != null) {
			Intent intent = new Intent(INSTALLED_APPLICATIONS);
			intent.putExtra(INTENT_RETURN_KEY, returnArray);
			intent.setPackage(client);
			this.sendBroadcast(intent);
		}
		return null;
	}
	
	/**
	 * 
	 * @param getSysPackages true if 
	 * @return
	 */
	private List<InstalledAppInfo> getInstalledApps(boolean getSysPackages, String packageNameFilter) { 
		List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
		List<InstalledAppInfo> res = new ArrayList<InstalledAppInfo>();
		
		for(int i=0;i<packs.size();i++) {
			PackageInfo p = packs.get(i);
			if ((!getSysPackages) && (p.versionName == null)) {
				continue ;
			}
			//FILTER PACKAGES
			String pack = p.packageName;
			if (!packageNameFilter.equals(""))
				if (!pack.contains(packageNameFilter))
					continue;
			
			//CONVERT ICON TO BASE64 STRING
			Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
			Bitmap bitmap = ((BitmapDrawable)icon).getBitmap(); 
			ByteArrayOutputStream stream = new ByteArrayOutputStream(); 
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); 
			byte[] bitmapdata = stream.toByteArray();
			String base64String = "data:image/png;base64," + Base64.encodeToString(bitmapdata, 0); 

			//ADD PACKAGE INFO TO RETURNED ARRAY
			InstalledAppInfo newInfo = new InstalledAppInfo();
			newInfo.setApplicationName(p.applicationInfo.loadLabel(getPackageManager()).toString());
			newInfo.setPackageName(p.packageName);
			String desc = "";
			try {
				desc = p.applicationInfo.loadDescription(getPackageManager()).toString();
			}
			catch(Exception ex) {
			}
			newInfo.setApplicationDescription(desc);
			newInfo.setVersionName(p.versionName);
			newInfo.setVersionCode(p.versionCode);
			newInfo.setIcon(base64String);
			res.add(newInfo);
		}
		return res;
	}
	
	
	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.servicemonitor.ICoreServiceMonitor#getNodeDetails(java.lang.String, org.societies.android.api.internal.examples.AndroidParcelable)
	 */
	//public AndroidParcelable getNodeDetails(String arg0, AndroidParcelable arg1) {
	//	return null;
	//}

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.servicemonitor.ICoreServiceMonitor#startActivity(java.lang.String, java.lang.String)
	 */
	public boolean startActivity(String client, String packageName) {
		Log.d(LOG_TAG, "Calling startActivity from client: " + client + " with activity: " + packageName);
		boolean appLaunched = true;
		try {
			Intent LaunchApp = getPackageManager().getLaunchIntentForPackage(packageName);
			startActivity( LaunchApp );
		} catch (Exception ex) {
			appLaunched = false;
			Log.d(LOG_TAG, "Exception occurred trying to launch app:" + ex.getMessage());
		}
		//SETUP RETURN INTENT STUFF
		if (client != null) {
			Intent intent = new Intent(START_ACTIVITY);
			intent.putExtra(INTENT_RETURN_KEY, appLaunched);
			intent.setPackage(client);
			this.sendBroadcast(intent);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.servicemonitor.ICoreServiceMonitor#startService(java.lang.String, java.lang.String)
	 */
	public boolean startService(String arg0, String arg1) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.servicemonitor.ICoreServiceMonitor#stopActivity(java.lang.String, java.lang.String)
	 */
	public boolean stopActivity(String arg0, String arg1) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.servicemonitor.ICoreServiceMonitor#stopService(java.lang.String, java.lang.String)
	 */
	public boolean stopService(String arg0, String arg1) {
		return false;
	}
}
