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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.societies.security.digsig.api.SigResult;
import org.societies.security.digsig.api.Verify;
import org.societies.security.digsig.sign.MainActivity;
import org.societies.security.digsig.utility.Storage;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author mitjav
 *
 */
public class SignServiceRemoteSpeedTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private static final String TAG = SignServiceRemoteSpeedTest.class.getSimpleName();
	private static final int TIME_TO_WAIT = 30000;
	private static final int TIME_STEP_TO_WAIT = 2000;

	private static final String fileName = Environment.getExternalStorageDirectory().getPath() + "/test-verify.xml";

	private static Activity mActivity;

	/** Messenger for communicating with the service. */
	private Messenger mService = null;

	/** Flag indicating whether we have called bind on the service. */
	private boolean mBound = false;

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	private final Messenger mMessenger = new Messenger(new IncomingHandler());

	private static class Results {
		public static boolean methodVerifyCalled = false;
	}

	private static long timeStart;

	/**
	 * Handler of incoming messages from clients.
	 */
	static class IncomingHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "handleMessage: msg.what = " + msg.what + ", replyTo = " + msg.replyTo);
			switch (msg.what) {
			case Verify.Methods.VERIFY:
				boolean success = msg.getData().getBoolean(Verify.Params.SUCCESS);
				long docSize = new File(fileName).length() / 1024;
				String record = String.valueOf(new Date().getTime() - timeStart) + "," + docSize + "," + success + "\n";
				new Storage(mActivity).writeToExternalStorage("DigSig speed test.csv", record, true);
				ArrayList<SigResult> results = msg.getData().getParcelableArrayList(Verify.Params.RESULT);
				assertNotNull(results);
				assertTrue(results.size() >= 1);
				Log.i(TAG, "VERIFY completed successfully");
				Results.methodVerifyCalled = true;
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	public SignServiceRemoteSpeedTest() {
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

	@MediumTest
	public void testSignServiceRemote() throws Exception {

		Log.i(TAG, "testSignServiceRemote");

		// Bind to the service
		Intent intent = new Intent(Verify.ACTION);
		mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		for (int k = 0; k < TIME_TO_WAIT / TIME_STEP_TO_WAIT; k++) {
			if (Results.methodVerifyCalled) {
				return;
			}
			Thread.sleep(TIME_STEP_TO_WAIT);
		}
		fail();
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
			verifySigs();
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			
			Log.i(TAG, "onServiceDisconnected");

			mService = null;
			mBound = false;
		}
	};

	private void verifySigs() {
		if (!mBound) return;
		// Create and send a message to the service, using a supported 'what' value
		
		timeStart = new Date().getTime();
		
		Message msg = Message.obtain(null, Verify.Methods.VERIFY, 0, 0);
		Bundle data = new Bundle();
		Uri uri = Uri.fromFile(new File(fileName));
		data.putString(Verify.Params.DOC_TO_VERIFY_URI, uri.toString());
		msg.setData(data);
		msg.replyTo = mMessenger;
		try {
			Log.i(TAG, "verifySigs: Sending message to service");
			mService.send(msg);
			Log.i(TAG, "verifySigs: Message sent to service");
		} catch (Exception e) {
			Log.e(TAG, "verifySigs", e);
		}
	}
}
