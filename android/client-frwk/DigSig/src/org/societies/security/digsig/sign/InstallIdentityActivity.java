package org.societies.security.digsig.sign;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Locale;

import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.apiinternal.Trust;
import org.societies.security.digsig.trust.AndroidSecureStorage;
import org.societies.security.digsig.trust.AndroidSecureStorageConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class InstallIdentityActivity extends Activity {
	
	private static final String TAG = InstallIdentityActivity.class.getSimpleName();

	private final static int LIST_PFX_FILES = 1;
	private final static int PASS_ENTRY 	= 2;
	private final static int UNLOCK_AND_INSTALL_IDENTITY = 3;
		
	private AndroidSecureStorage secureStorage;
		
	private String inputFileName = null;
	byte[] encodedKey = null;
	byte[] encodedCert = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		secureStorage = AndroidSecureStorage.getInstance();
		
		Intent i = new Intent(this,ListFilesActivity.class);
		i.putExtra(Trust.Params.EXTENSIONS, "p12;pfx");
		startActivityForResult(i, LIST_PFX_FILES);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == LIST_PFX_FILES && resultCode==RESULT_OK)
		{			
			inputFileName = data.getStringExtra(Sign.Params.IDENTITY);
			Log.i("InstallIdentityActivity",String.format("Returned this file name %s", inputFileName));
			
			Intent i = new Intent(this,PassEntryActivity.class);
			startActivityForResult(i,PASS_ENTRY);
		} else if (requestCode == PASS_ENTRY && resultCode==RESULT_OK) {
			String pass = data.getStringExtra(Trust.Params.PASSWORD);
			
			try {
				KeyStore ks = KeyStore.getInstance("PKCS12");			
				ks.load(new FileInputStream(inputFileName),pass.toCharArray());		
								
				Enumeration<String> aliases = ks.aliases();
				while (aliases.hasMoreElements()) {
					String alias = aliases.nextElement();
					
					Key key = ks.getKey(alias, pass.toCharArray());
					X509Certificate cert = (X509Certificate) ks.getCertificate(alias);					
					if (key==null || cert==null) continue;
					
					encodedKey = key.getEncoded();
					encodedCert = cert.getEncoded();
														
					doInstallIdentity();
					return;																			
				}							
				
			} catch (Exception e) {
				reportFailedInstall();				
			}			
		} else if (requestCode == UNLOCK_AND_INSTALL_IDENTITY) {
			Log.i(TAG, "UNLOCK_AND_LIST_IDENTITY");
			installIdentity();						  
		}
	}
	
	private void doInstallIdentity() {
		int code = secureStorage.test();
		
		if (code == AndroidSecureStorage.LOCKED || code == AndroidSecureStorage.UNINITIALIZED) {
			String unlockAction;
//			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//				unlockAction = AndroidSecureStorageConstants.UNLOCK_ACTION_PRE_HONEYCOMB;
//			}
//			else {
				unlockAction = AndroidSecureStorageConstants.UNLOCK_ACTION_HONEYCOMB;
//			}
			Intent intent = new Intent(unlockAction);
		    startActivityForResult(intent, UNLOCK_AND_INSTALL_IDENTITY);
		    return;
		} 
		
		installIdentity();
	}
	
	private void reportFailedInstall() {
		Toast.makeText(this, "Identity installation failed !", Toast.LENGTH_SHORT).show();
		setResult(RESULT_CANCELED);
		finish();		
	}
	
	private void installIdentity() {
		if (encodedCert==null || encodedKey==null || secureStorage.test()!=AndroidSecureStorage.NO_ERROR) { 
			reportFailedInstall();
			return;
		}
				
		int count = 0;
			
		count = 0;
		while (true) {
			String certKey = String.format(Locale.US, "CERT_%d", count);
			String keyKey = String.format(Locale.US, "KEY_%d", count++);
			
			if (!secureStorage.contains(certKey) && !secureStorage.contains(keyKey)) {
				secureStorage.put(certKey,encodedCert);
				secureStorage.put(keyKey, encodedKey);
				
				Toast.makeText(this, "Digital identity installed sucessfully !", Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK);
				finish();				
				return;
			}			
		}
	}
}
