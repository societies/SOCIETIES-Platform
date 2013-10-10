package org.societies.security.digsig.sign;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;

import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.apiinternal.Trust;
import org.societies.security.digsig.trust.AndroidSecureStorage;
import org.societies.security.digsig.trust.AndroidSecureStorage.State;
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
	PrivateKey privateKey = null;
	Certificate certificate = null;

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
		
		Log.i(TAG, "onActivityResult: requestCode = " + requestCode + ", resultCode = " + resultCode);
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == LIST_PFX_FILES && resultCode==RESULT_OK) {
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

					this.privateKey = (PrivateKey) key;
					this.certificate = cert;

					doInstallIdentity();
					return;																			
				}							

			} catch (Exception e) {
				Log.w(TAG, "Exception, identity installation failed", e);
				reportFailedInstall();				
			}			
		} else if (requestCode == UNLOCK_AND_INSTALL_IDENTITY) {
			Log.i(TAG, "UNLOCK_AND_LIST_IDENTITY");
			installIdentity();						  
		}
	}

	private void doInstallIdentity() {
		
		Log.i(TAG, "doInstallIdentity");

//		State code = secureStorage.state();
//		Log.i(TAG, "doInstallIdentity: secure storage status = " + code);
//
//		if (code == State.LOCKED || code == State.UNINITIALIZED) {
//			String unlockAction;
////			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
////				unlockAction = AndroidSecureStorageConstants.UNLOCK_ACTION_PRE_HONEYCOMB;
////			}
////			else {
//			unlockAction = AndroidSecureStorageConstants.UNLOCK_ACTION_HONEYCOMB;
//			//			}
//			Intent intent = new Intent(unlockAction);
//			Log.i(TAG, "Trying to unlock secure storage");
//			startActivityForResult(intent, UNLOCK_AND_INSTALL_IDENTITY);
//			return;
//		} 

		installIdentity();
	}

	private void reportFailedInstall() {
		Log.w(TAG, "Identity installation failed");
		Toast.makeText(this, "Identity installation failed !", Toast.LENGTH_SHORT).show();
		setResult(RESULT_CANCELED);
		finish();		
	}

	private void installIdentity() {

		Log.i(TAG, "installIdentity");
		
//		State secureStorageState = secureStorage.state();
//		if (encodedCert == null || encodedKey == null || secureStorageState != State.UNLOCKED) { 
//			Log.w(TAG, "installIdentity: encodedCert = " + encodedCert + ", encodedKey = " + encodedKey +
//					", secureStorageState = " + secureStorageState);
//			reportFailedInstall();
//			return;
//		}

		KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance("AndroidKeyStore");
			keyStore.load(null);
			Log.i(TAG, "Key store type: " + keyStore.getType());
			Log.i(TAG, "Key store size: " + keyStore.size());
			Log.i(TAG, "Key store alises: " + keyStore.aliases());

			keyStore.setCertificateEntry("CERT_0", certificate);
			Log.i(TAG, "Stored certificate of type " + certificate.getType());
			keyStore.setKeyEntry("KEY_0", privateKey, null, new Certificate[] {certificate});
			Log.i(TAG, "Stored key of format " + privateKey.getFormat());

			
			keyStore = KeyStore.getInstance("AndroidKeyStore");
			keyStore.load(null);
			Log.d(TAG, "Retrieved certificate of type " + keyStore.getCertificate("CERT_0").getType());
			Log.d(TAG, "Retrieved key of format " + keyStore.getKey("KEY_0", null));
			
			Toast.makeText(this, "Digital identity installed sucessfully", Toast.LENGTH_SHORT).show();
			setResult(RESULT_OK);
			finish();				
			return;
		} catch (Exception e) {
			Log.w(TAG, "installIdentity", e);
			setResult(RESULT_CANCELED);
			finish();				
			return;
		}

//		int count = 0;
//
//		count = 0;
//		while (true) {
//			String certKey = String.format(Locale.US, "CERT_%d", count);
//			String keyKey = String.format(Locale.US, "KEY_%d", count++);
//
//			byte[] alreadyStoredCert = secureStorage.getWithStringKey(certKey);
//			byte[] alreadyStoredKey = secureStorage.getWithStringKey(keyKey);
//			try {
//				if (Arrays.equals(alreadyStoredCert, certificate.getEncoded()) &&
//						Arrays.equals(alreadyStoredKey, privateKey.getEncoded())) {
//					Toast.makeText(this, "This digital identity is already installed.", Toast.LENGTH_SHORT).show();
//					setResult(RESULT_OK);
//					finish();				
//					return;
//				}
//	
//				if (!secureStorage.contains(certKey) && !secureStorage.contains(keyKey)) {
//					secureStorage.put(certKey, certificate.getEncoded());
//					secureStorage.put(keyKey, privateKey.getEncoded());
//	
//					Log.i(TAG, "Digital identity installed sucessfully");
//					Toast.makeText(this, "Digital identity installed sucessfully", Toast.LENGTH_SHORT).show();
//					setResult(RESULT_OK);
//					finish();				
//					return;
//				}
//			} catch (CertificateEncodingException e) {
//				Log.w(TAG, "Skipping certificate " + certKey, e);
//				continue;
//			}
//		}
	}
}
