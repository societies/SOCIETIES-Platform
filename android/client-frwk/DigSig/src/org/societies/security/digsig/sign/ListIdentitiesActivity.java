package org.societies.security.digsig.sign;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Locale;

import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.trust.AndroidSecureStorage;
import org.societies.security.digsig.trust.AndroidSecureStorageConstants;
import org.societies.security.digsig.trust.AndroidSecureStorage.State;

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

	private AndroidSecureStorage secureStorage;
	private CertificateFactory certFactory;
		
	private ArrayList<String> certNames;
	private ArrayList<Integer> certNumbers;
	
	private final static int UNLOCK_AND_LIST_IDENTITY = 3;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        
		Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
		
		secureStorage = AndroidSecureStorage.getInstance();
		KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance("AndroidKeyStore");
			keyStore.load(null);
			Log.i(TAG, "Key store type: " + keyStore.getType());
			Log.i(TAG, "Key store size: " + keyStore.size());
			Log.i(TAG, "Key store alises: " + keyStore.aliases());
		} catch (Exception e) {
			Log.w(TAG, "Could not get key store", e);
			return;
		}
		try {
			certFactory = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {}
		        	
		
		if (secureStorage.state()!= State.UNLOCKED) {
			
			String unlockAction;
//			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//				unlockAction = AndroidSecureStorageConstants.UNLOCK_ACTION_PRE_HONEYCOMB;
//			}
//			else {
				unlockAction = AndroidSecureStorageConstants.UNLOCK_ACTION_HONEYCOMB;
//			}

			Intent intent = new Intent(unlockAction);	
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
		if (secureStorage.state() != State.UNLOCKED) return;
		
		certNames = new ArrayList<String>();
		certNumbers = new ArrayList<Integer>();
		
		int count = 0;
		while (true) {
			String certKey = String.format(Locale.US, "CERT_%d", count);
			String keyKey = String.format(Locale.US, "KEY_%d", count++);
			
			if (!secureStorage.contains(certKey) && !secureStorage.contains(keyKey)) break;
			
			// Add cert to list
			byte[] encodedCert = secureStorage.getWithStringKey(certKey);					
			
			if (encodedCert==null) continue;
			
			X509Certificate cert = null;
			try {
				cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(encodedCert));
			} catch (CertificateException e) {
				Log.e("miki",e.getMessage());
			}
			if (cert==null) continue;
			
			certNames.add(cert.getSubjectDN().toString());
			certNumbers.add(count-1);
		}
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_files, certNames));	
				
		getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long itemPos) {				
				Intent intent = getIntent();
				intent.putExtra(Sign.Params.IDENTITY, certNumbers.get((int)itemPos));				
				setResult(RESULT_OK,intent);
				finish();				
			}
		});
	}	
}
