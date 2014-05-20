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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.apiinternal.Trust;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Display files from external storage root directory that have one of the given extensions.
 * Return the file selected by the user.
 */
public class ListFilesActivity extends ListActivity {
	
	private static final String TAG = ListFilesActivity.class.getSimpleName();
	
	private ArrayList<String> exts = new ArrayList<String>(2);
	private String[] fileArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StringTokenizer tokenizer = new StringTokenizer(getIntent().getStringExtra(Trust.Params.EXTENSIONS), ";");
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();			
			if (token==null || token.length()==0) continue;
			exts.add("."+token);
		}
		
		/* populate list view with certificate list */
		Log.d(TAG, "External storage state = " + Environment.getExternalStorageState());
        File sdCard = new File(Environment.getExternalStorageDirectory().getPath());
        if (sdCard.exists() && sdCard.isDirectory()) {
        	fileArray = sdCard.list(new FilenameFilter() {				
				public boolean accept(File dir, String filename) {					
					for (String ext : exts) 
						if (filename.endsWith(ext)) return true;
					
					return false;
				}
			});
        	
        	setListAdapter(new ArrayAdapter<String>(this, R.layout.list_files, fileArray));
        }
        
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long itemPos) {
				if (fileArray==null || itemPos<0  || itemPos>=fileArray.length) {
					setResult(RESULT_CANCELED);
					finish();
					return;
				}
				
				Log.i(TAG, String.format("Selected item %d", itemPos));
				
				Intent intent = getIntent();
				intent.putExtra(Sign.Params.IDENTITY, Environment.getExternalStorageDirectory().getPath() + "/" + fileArray[(int) itemPos]);
				setResult(RESULT_OK,intent);
				finish();				
			}
		});
	}
}
