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

package org.societies.android.platform.interfaces;

import java.util.List;

import org.societies.api.android.internal.model.AndroidParcelable;

import android.app.ActivityManager;


public interface ICoreServiceMonitor {
	String methodsArray [] = {"activeTasks(String client)", 
								"activeTasks(String client, String taskFilter)",
								"activeServices(String client)",
								"activeServices(String client, String serviceFilter)",
								"startService(String client, String service)",
								"startActivity(String client, String activity)",
								"stopService(String client, String service)",
								"stopActivity(String client, String activity)",
								"getNodeDetails(String client, org.societies.api.android.internal.model.AndroidParcelable node)"};

	/**
	 * Generate a list of tasks currently "running" on the Android device
	 * 
	 * @param client package name of service caller 
	 * 
	 * @return List (ActivityManager.RunningTaskInfo)
	 */
	List<ActivityManager.RunningTaskInfo> activeTasks(String client);
	/**
	 * Generate a list of tasks currently "running" on the Android device
	 * which are filtered by their Component name
	 * 
	 * 
	 * @param client package name of service caller 
	 * @param taskFilter filters running tasks according to filter value
	 * @return List (ActivityManager.RunningTaskInfo)
	 */
	List<ActivityManager.RunningTaskInfo> activeTasks(String client, String taskFilter);
	/**
	 * Generate a list of services currently "running" on the Android device
	 * 
	 * @param client package name of service caller 
	 * @return List (ActivityManager.RunningServiceInfo)
	 */
	List<ActivityManager.RunningServiceInfo> activeServices(String client);
	/**
	 * Generate a list of services currently "running" on the Android device
	 * which are filtered by their Component name
	 * 
	 * @param client package name of service caller 
	 * @param serviceFilter filters running services according to filter value

	 * @return List (ActivityManager.RunningServiceInfo)
	 */

	List<ActivityManager.RunningServiceInfo> activeServices(String client, String serviceFilter);
	
	boolean startService(String client, String service);
	boolean startActivity(String client, String activity);
	boolean stopService(String client, String service);
	boolean stopActivity(String client, String activity);

	/**
	 * Parcelable example method
	 * Non primitive and String parameters must be fully qualified
	 * @param client
	 * @param node 
	 * @return AndroidParcelable
	 */
	AndroidParcelable getNodeDetails(String client, org.societies.api.android.internal.model.AndroidParcelable node);
}
