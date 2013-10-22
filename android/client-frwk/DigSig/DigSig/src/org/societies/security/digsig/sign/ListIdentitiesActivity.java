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
		while ((cert = secureStorage.getCertificate(count)) != null) {

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
