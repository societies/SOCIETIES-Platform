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

import org.societies.security.digsig.sign.SignActivity;
import org.societies.security.digsig.utility.Net;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * Android test case for uploading an XML document to the REST server, then downloading it,
 * signing it and sending it back to the server.
 *
 * @author Mitja Vardjan
 *
 */
public class CommunitySignatureServerDownloadTest extends ActivityInstrumentationTestCase2<SignActivity> {

	private static final String TAG = CommunitySignatureServerDownloadTest.class.getSimpleName();
	
	private Activity mActivity;
	
	public CommunitySignatureServerDownloadTest() {
		super(SignActivity.class);
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
	public void testInitialDocumentDownload() throws Exception {
		
		Log.i(TAG, "testInitialDocumentDownload");

		Net source = new Net(new URI("http://192.168.1.73/tmp/societies/doc-large.xml"));
		Net download = new Net(new URI(CommunitySignatureServerUploadTest.downloadUri));

		String contents = source.getString();
		String result = download.getString();
		assertEquals(contents, result);
		
		Log.i(TAG, "testInitialDocumentDownload: downloaded successfully");
	}
}
