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

package org.societies.android.platform.androidutils;

import org.societies.api.schema.cssmanagement.CssEvent;
import org.societies.utilities.DBC.Dbc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

	
public class AndroidNotifier {
	//Logging tag
	private static final String LOG_TAG = AndroidNotifier.class.getName();

	private int defaultSound;
	private int notifierFlags [];
	private NotificationManager notifyMgr;
	private Context context;

	/**
	 * Constructor for Android Notifier
	 * 
	 * @param context Android Service
	 * @param sound
	 * @param notifierFlags
	 */
	public AndroidNotifier(Context context, int sound, int notifierFlags []){
		this.defaultSound = sound;
		this.notifierFlags = notifierFlags;
		this.context = context;
		
		//create Android notifications
		this.notifyMgr = (NotificationManager) this.context.getSystemService(this.context.NOTIFICATION_SERVICE);
		
		Log.d(LOG_TAG, "Android Notifier created");
	}
	/**
	 * Create a notification for CSS Events
	 * 
	 * @param event
	 * @param notificationTag
	 */
	public void notifyEvent(CssEvent event, String notificationTag) {
		Dbc.require("CSS event cannot be null", null != event);
		Dbc.require("Notification tag must be valid", null != notificationTag && notificationTag.length() > 0);
		Log.d(LOG_TAG, "notifyEvent: " + event.getType() + " tag: " + notificationTag);

		// Create Notification
		Notification notification = new Notification(R.drawable.ic_launcher,
				  event.getDescription(), System.currentTimeMillis());
		//apply notification flags
		for (int i : this.notifierFlags) {
			notification.flags |= i;
		}
		//apply sound flag
		notification.defaults |= this.defaultSound;
		notification.number = 1;
		
		this.notifyMgr.notify(notificationTag, 1, notification);
	}
	
	/**
	 * Create a notification for Societies Events with no re-direction intent
	 * @param message
	 * @param notificationTag
	 * @param clazz
	 */
	public void notifyMessage(String message, String notificationTag, Class clazz) {
		Dbc.require("Message cannot be null", null != message && message.length() > 0);
		Dbc.require("Notification tag must be valid", null != notificationTag && notificationTag.length() > 0);
		Log.d(LOG_TAG, "message: " + message + " tag: " + notificationTag);
		
		this.notifyMessage(message, notificationTag, clazz, new Intent());
	}
	
	/**
	 * Create a notification for Societies Events
	 * @param message
	 * @param notificationTag
	 * @param clazz
	 * @param intent of activity to be displayed when notification is viewed
	 */
	public void notifyMessage(String message, String notificationTag, Class clazz, Intent intent) {
		Dbc.require("Message cannot be null", null != message && message.length() > 0);
		Dbc.require("Notification tag must be valid", null != notificationTag && notificationTag.length() > 0);
		Log.d(LOG_TAG, "message: " + message + " tag: " + notificationTag);

		// Create Notification
		Notification notification = new Notification(R.drawable.ic_launcher,
				  message, System.currentTimeMillis());
		//apply notification flags
		for (int i : this.notifierFlags) {
			notification.flags |= i;
		}
		//apply sound flag
		notification.defaults |= this.defaultSound;
		notification.number = 1;
		
		Intent notificationIntent = new Intent(this.context, clazz);
		
		PendingIntent contentIntent = PendingIntent.getActivity(this.context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		notification.setLatestEventInfo(context, "CSSManager", message, contentIntent);
		this.notifyMgr.notify(notificationTag, 1, notification);
	}

}
