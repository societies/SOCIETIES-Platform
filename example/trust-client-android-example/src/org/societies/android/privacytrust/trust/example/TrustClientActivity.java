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
package org.societies.android.privacytrust.trust.example;

import java.util.Date;

import org.societies.android.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.android.privacytrust.trust.evidence.TrustEvidenceCollector;
import org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityType;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

public class TrustClientActivity extends Activity {
	
	private static final String TAG = TrustClientActivity.class.getName();
	
	/** The ITrustEvidenceCollector service reference. */
	private ITrustEvidenceCollector collector;
	
	/** The TrustEvidenceCollector service connection. */
	private ServiceConnection collectorServiceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
					
			TrustClientActivity.this.collector = 
					((TrustEvidenceCollector.LocalBinder)service).getService();
	    	Log.i(TrustClientActivity.TAG, "ITrustEvidenceCollector service bound");
		}
		
		public void onServiceDisconnected(ComponentName name) {
			
			TrustClientActivity.this.collector = null;
			Log.i(TrustClientActivity.TAG, "ITrustEvidenceCollector service unbound");
		}
	};
	
	private EditText etTrustor;
	private EditText etTrustee;
	private RatingBar rbTrust;
	private Button buttonSubmit;
	private Button buttonClear;
	private ProgressBar pbProgress;
	
    /*
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.etTrustor = (EditText) findViewById(R.id.etTrustor);
        this.etTrustee = (EditText) findViewById(R.id.etTrustee);
        this.rbTrust = (RatingBar) findViewById(R.id.rbTrust);
        this.buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        this.buttonClear = (Button) findViewById(R.id.buttonClear);
        this.pbProgress = (ProgressBar) findViewById(R.id.pbProgress);
        
        this.etTrustor.addTextChangedListener(new TextWatcher() {
			
        	/*
        	 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
        	 */
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				if (s.length() > 0) {
					TrustClientActivity.this.buttonClear.setEnabled(true);
					if (TrustClientActivity.this.etTrustee.getText().length() > 0)
						TrustClientActivity.this.buttonSubmit.setEnabled(true);
					else
						TrustClientActivity.this.buttonSubmit.setEnabled(false);
				} else {
					TrustClientActivity.this.buttonSubmit.setEnabled(false);
					if (TrustClientActivity.this.etTrustee.getText().length() > 0)
						TrustClientActivity.this.buttonClear.setEnabled(true);
					else
						TrustClientActivity.this.buttonClear.setEnabled(false);
				}
					
			}
			
			/*
			 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
			 */
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// Do nothing
			}
			
			/*
			 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
			 */
			public void afterTextChanged(Editable s) {
				// Do nothing
			}
		});
        
        this.etTrustee.addTextChangedListener(new TextWatcher() {
			
        	/*
        	 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
        	 */
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				if (s.length() > 0) {
					TrustClientActivity.this.buttonClear.setEnabled(true);
					if (TrustClientActivity.this.etTrustor.getText().length() > 0)
						TrustClientActivity.this.buttonSubmit.setEnabled(true);
					else
						TrustClientActivity.this.buttonSubmit.setEnabled(false);
				} else {
					TrustClientActivity.this.buttonSubmit.setEnabled(false);
					if (TrustClientActivity.this.etTrustor.getText().length() > 0)
						TrustClientActivity.this.buttonClear.setEnabled(true);
					else
						TrustClientActivity.this.buttonClear.setEnabled(false);
				}
					
			}
			
			/*
			 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
			 */
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// Do nothing
			}
			
			/*
			 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
			 */
			public void afterTextChanged(Editable s) {
				// Do nothing
			}
		});
    }
    
    /*
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
    	
    	Log.i(TAG, "Starting");
    	super.onStart();
    	this.doBindCollectorService();    	
    }
    
    /*
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
    	
    	Log.i(TAG, "Stopping");
    	super.onStop();
    	this.doUnbindCollectorService();
    }
    
    /*
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
    	
    	Log.i(TAG, "Destroying");
        super.onDestroy();
        this.doUnbindCollectorService();
    }
    
    /**
     * Called when the Submit button is clicked
     * 
     * @param view
     */
    public void onButtonSubmitClick(View view) {
    	
    	Log.d(TAG, "onButtonSubmitClick");
    	if (this.collector != null) {

    		new AddTrustEvidenceTask().execute();
    	} else {
    		
    		Toast.makeText(this, 
    				"No-ooooooo ITrustEvidenceCollector service is not available", 
    				Toast.LENGTH_LONG).show();
    	}
    }
    
    /**
     * Called when the Submit button is clicked
     * 
     * @param view
     */
    public void onButtonClearClick(View view) {
    	
    	Log.d(TAG, "onButtonClearClick");
    	this.etTrustor.setText("");
    	this.etTrustee.setText("");
    	this.rbTrust.setRating(0f);
    }
    
    private void doBindCollectorService() {
    	
        bindService(new Intent(TrustClientActivity.this, 
                TrustEvidenceCollector.class), 
                this.collectorServiceConnection, 
                Context.BIND_AUTO_CREATE);
    }

    private void doUnbindCollectorService() {
    	
        if (this.collector != null) {
            // Detach our existing connection.
            unbindService(this.collectorServiceConnection);
            this.collector = null;
        }
    }
    
    private class AddTrustEvidenceTask extends AsyncTask<Void, Void, String> {

    	/*
    	 * @see android.os.AsyncTask#onPreExecute()
    	 */
    	@Override
    	protected void onPreExecute() {
    		
    		Log.d(TrustClientActivity.TAG, "AddTrustEvidenceTask.onPreExecute");
    		TrustClientActivity.this.pbProgress.setVisibility(View.VISIBLE);
    	}
    	
		/*
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(Void... arg0) {
			
			Log.d(TrustClientActivity.TAG, "AddTrustEvidenceTask.doInBackground");
			final String trustor = TrustClientActivity.this.etTrustor.getText().toString();
    		final String trustee = TrustClientActivity.this.etTrustee.getText().toString();
    		final Double rating = new Double(TrustClientActivity.this.rbTrust.getRating());
    		
    		String result = "Thanks for your feedback!";
    		try {
    			final TrustedEntityId teid = new TrustedEntityId(
    					trustor, TrustedEntityType.CSS, trustee);
    			TrustClientActivity.this.collector.addDirectEvidence(teid, TrustEvidenceType.RATED,
    				new Date(), rating);
    		} catch (Exception e) {
    			
    			result = "Oops! " + e.getLocalizedMessage();
    		}
    	
			return result;
		}
    	
		/*
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			
			Log.d(TrustClientActivity.TAG, "AddTrustEvidenceTask.onPostExecute");
			TrustClientActivity.this.pbProgress.setVisibility(View.INVISIBLE);
			Toast.makeText(TrustClientActivity.this, result, Toast.LENGTH_LONG).show();
		}
    }
}