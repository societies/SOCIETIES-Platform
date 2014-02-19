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
package org.societies.security.digsig.sign;

import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.apiinternal.ISecureStorage;
import org.societies.security.digsig.trust.AndroidSecureStorageConstants;
import org.societies.security.digsig.trust.SecureStorage;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class ListIdentitiesActivity extends ListActivity {
	
	private static final String TAG = ListIdentitiesActivity.class.getSimpleName();

	private ISecureStorage secureStorage;
		
	private ArrayList<String> certNames;
	private ArrayList<Integer> certNumbers;
	
	private final static int UNLOCK_AND_LIST_IDENTITY = 3;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        
		Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
		
		try {
			secureStorage = new SecureStorage();
		} catch (DigSigException e) {
			Log.e(TAG, "Failed to initialize", e);
			setResult(RESULT_CANCELED);
			finish();
			return;
		}
		
		if (!secureStorage.isReady()) {
			
			Intent intent = new Intent(AndroidSecureStorageConstants.getUnlockAction());	
		    startActivityForResult(intent, UNLOCK_AND_LIST_IDENTITY);		    
			return;
		}
		
		setList();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
		Log.i(TAG, "onActivityResult");

        super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == UNLOCK_AND_LIST_IDENTITY) {
			Log.i(TAG, "UNLOCK_AND_LIST_IDENTITY");
			setList();
		}
	}
	
	
	private void setList() {
		if (!secureStorage.isReady()) {
			return;
		}
		
		certNames = new ArrayList<String>();
		certNumbers = new ArrayList<Integer>();
		
		int count = 0;
		X509Certificate cert;
		while ((secureStorage.containsCertificateAndKey(count))) {
			cert = secureStorage.getCertificate(count);
			// Add cert to list
			certNames.add(cert.getSubjectDN().toString());
			certNumbers.add(count);
			++count;
		}
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_files, certNames));	
				
		getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long itemPos) {				
				Intent intent = getIntent();
				intent.putExtra(Sign.Params.IDENTITY, certNumbers.get((int)itemPos));				
				setResult(RESULT_OK,intent);
				finish();				
			}
		});
	}	
}
