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
import org.societies.android.api.internal.examples.ICoreServiceExample;
import org.societies.android.api.internal.servicemonitor.ICoreServiceMonitor;
import org.societies.utilities.DBC.Dbc;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

public class SameProcessService extends Service implements ICoreServiceExample, ICoreServiceMonitor {
	private static final int MAX_SERVICES = 30;
	private static final int MAX_TASKS = 30;


	private IBinder binder = null;
	
	
	@Override
	public void onCreate () {
		this.binder = new LocalBinder();
		Log.i(this.getClass().getName(), "Service starting");

	}

	
	public class LocalBinder extends Binder {
		SameProcessService getService() {
			return SameProcessService.this;
		}
	}
	
	@Override
	/**
	 * Return binder object to allow calling component access to service's
	 * public methods
	 */
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return this.binder;
	}

	@Override
	public void onDestroy() {
		Log.i(this.getClass().getName(), "Service terminating");
	}

	public String getGreeting() {
		return "Bah humbug";
	}

	public String getGreeting(String appendToMessage) {
		Dbc.require("Message string required", appendToMessage != null && appendToMessage.length() > 0);
		return ("Bah humbug " + appendToMessage);
	}

	public String getNumberGreeting(String appendToMessage, int index) {
		Dbc.require("Message string required", appendToMessage != null && appendToMessage.length() > 0);
		Dbc.require("Index number required", index > 0);
		return ("Bah humbug " + appendToMessage + " index: " + index);
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

		ActivityManager.RunningServiceInfo arrayServices [] = new ActivityManager.RunningServiceInfo[runningServices.size()];
		
		for (ActivityManager.RunningServiceInfo service : runningServices) {
			
			Log.d(this.getClass().getName(), "Service name: " + service.service.flattenToString());
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
		

		for (ActivityManager.RunningTaskInfo task : runningTasks) {
			Log.d(this.getClass().getName(), "Task name: " + task.baseActivity.flattenToString());
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

	public boolean startActivity(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean startService(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean stopActivity(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean stopService(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AndroidParcelable getNodeDetails(String client, org.societies.android.api.internal.examples.AndroidParcelable arg1) {
		// TODO Auto-generated method stub
		return null;
	}


}
