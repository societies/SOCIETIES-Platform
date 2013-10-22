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

import java.util.ArrayList;

import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.sign.MainActivity;
import org.societies.security.digsig.sign.R;
import org.societies.security.digsig.sign.SignActivity;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;
import android.widget.Button;

/**
 * Android test case for {@link MainActivity}
 *
 * @author Mitja Vardjan
 *
 */
public class SignActivityTest extends ActivityInstrumentationTestCase2<SignActivity> {

	private static final String TAG = SignActivityTest.class.getSimpleName();
	
	private final static int SIGN = 1;

	private Activity mActivity;
	
	public SignActivityTest() {
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

	@UiThreadTest
	public void testSigningDocInIntent() {
		
		Log.i(TAG, "testSigningDocInIntent");
		
		byte[] val = null;
		try {
			val = "<xml><miki Id='Miki1'>aadsads</miki></xml>".getBytes("UTF-8");
		} catch (Exception e) {}
						
		Intent i = new Intent("org.societies.security.digsig.action.Sign");
		i.putExtra(Sign.Params.DOC_TO_SIGN, val);
		
		ArrayList<String> idsToSign = new ArrayList<String>();
		idsToSign.add("Miki1");
		i.putStringArrayListExtra(Sign.Params.IDS_TO_SIGN, idsToSign);
		
		mActivity.startActivityForResult(i, SIGN);
		Button okButton = (Button) mActivity.findViewById(R.id.buttonSignOk);
		okButton.performClick();
	}
}
