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
import java.util.Date;

import org.societies.security.digsig.sign.MainActivity;
import org.societies.security.digsig.utility.Net;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * Android test case for simultaneously uploading signed XML document to the REST server from multiple threads.
 * This test simulates multiple users signing the same document simultaneously.
 *
 * @author Mitja Vardjan
 *
 */
public class CommunitySignatureServerMultipleMergeTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private static final String TAG = CommunitySignatureServerMultipleMergeTest.class.getSimpleName();
	
	private static final String signedDocUri = "http://192.168.1.73/tmp/societies/signed-once.xml";
	
	private Activity mActivity;
	
	long start;
	String contents;
	Net download;
	
	public CommunitySignatureServerMultipleMergeTest() {
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

	class UploadThread implements Runnable {
		Thread t;
		UploadThread(String s) {
			t = new Thread(this, s);
			t.start();
		}
		public void run() {
			long uploadStart = new Date().getTime() - start;
//			Log.i(TAG, "Thread " + Thread.currentThread().getName() + ": starting upload after " + 
//					uploadStart + " ms");

			assertTrue(download.put(contents));
			long finish = new Date().getTime() - start;
//			Log.i(TAG, "Thread " + Thread.currentThread().getName() + ": uploaded successfully, time = " +
//					finish + " ms");
			Log.i(TAG, "Thread " + Thread.currentThread().getName() + ": started, finished uploading at: " +
					uploadStart + ", " + finish);
		}
	}
	
	@MediumTest
	public void testInitialDocumentUpload() throws Exception {
		
		Log.i(TAG, "testInitialDocumentUpload");

		Net source = new Net(new URI(signedDocUri));
		download = new Net(new URI(CommunitySignatureServerUploadTest.downloadUri));

		contents = source.getString();
		assertTrue(contents.length() > 0);
		start = new Date().getTime();
		
		for (int k = 0; k < 20; k++) {
			UploadThread t = new UploadThread("t-" + k);
		}
	}
}
