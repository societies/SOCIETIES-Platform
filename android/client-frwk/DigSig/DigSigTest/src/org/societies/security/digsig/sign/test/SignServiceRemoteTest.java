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
package org.societies.security.digsig.sign.test;

import java.net.URI;
import java.net.URL;
import java.util.Random;

import org.societies.security.digsig.api.Verify;
import org.societies.security.digsig.sign.MainActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author mitjav
 *
 */
public class SignServiceRemoteTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private static final String TAG = SignServiceRemoteTest.class.getSimpleName();

	private static final int TIME_TO_WAIT = 3000;

	private Activity mActivity;

	/** Messenger for communicating with the service. */
	private Messenger mService = null;

	/** Flag indicating whether we have called bind on the service. */
	private boolean mBound = false;

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	private final Messenger mMessenger = new Messenger(new IncomingHandler());

	private static class Results {
		public static boolean methodGenerateUrisCalled = false;
	}

	/**
	 * Handler of incoming messages from clients.
	 */
	static class IncomingHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "handleMessage: msg.what = " + msg.what + ", replyTo = " + msg.replyTo);
			switch (msg.what) {
			case Verify.Methods.GENERATE_URIS:
				Results.methodGenerateUrisCalled = true;
				assertTrue(msg.getData().getBoolean(Verify.Params.SUCCESS));
				String uploadUri = msg.getData().getString(Verify.Params.UPLOAD_URI);
				String downloadUri = msg.getData().getString(Verify.Params.DOWNLOAD_URI);
				assertNotNull(uploadUri);
				assertNotNull(downloadUri);
				try {
					new URI(uploadUri);
					new URI(downloadUri);
					new URL(uploadUri);
					new URL(downloadUri);
					Log.i(TAG, "GENERATE_URIS completed successfully");
				} catch (Exception e) {
					fail(e.getMessage());
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	public SignServiceRemoteTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {

		Log.i(TAG, "setUp");

		// Required by JUnit
		super.setUp();

		setActivityInitialTouchMode(false);
		mActivity = getActivity();
	}

	public void testPreConditions() {
		assertNotNull(mActivity);
	}

	public void testSignServiceRemote() throws Exception {

		Log.i(TAG, "testSignServiceRemote");

		// Bind to the service
		Intent intent = new Intent(Verify.ACTION);
		mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		Thread.sleep(TIME_TO_WAIT);
		assertTrue(mBound);
		assertTrue(Results.methodGenerateUrisCalled);
	}

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using a Messenger, so here we get a client-side
			// representation of that from the raw IBinder object.
			
			Log.i(TAG, "onServiceConnected");
			
			mService = new Messenger(service);
			mBound = true;
			generateUris();
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			
			Log.i(TAG, "onServiceDisconnected");

			mService = null;
			mBound = false;
		}
	};

	private void generateUris() {
		if (!mBound) return;
		// Create and send a message to the service, using a supported 'what' value
		Message msg = Message.obtain(null, Verify.Methods.GENERATE_URIS, 0, 0);
		Bundle data = new Bundle();
		data.putString(Verify.Params.DOC_TITLE, "Android JUnit test " + new Random().nextInt());
		data.putString(Verify.Params.NOTIFICATION_ENDPOINT, "http://192.168.1.92/societies/community-signature/notify");
		data.putInt(Verify.Params.NUM_SIGNERS_THRESHOLD, 2);
		msg.setData(data);
		msg.replyTo = mMessenger;
		try {
			Log.i(TAG, "Sending message to service");
			mService.send(msg);
			Log.i(TAG, "Message sent to service");
		} catch (Exception e) {
			Log.e(TAG, "generateUris", e);
		}
	}

}
