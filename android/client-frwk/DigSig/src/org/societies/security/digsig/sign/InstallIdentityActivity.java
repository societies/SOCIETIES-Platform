package org.societies.security.digsig.sign;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.apiinternal.ISecureStorage;
import org.societies.security.digsig.apiinternal.Trust;
import org.societies.security.digsig.trust.AndroidSecureStorageConstants;
import org.societies.security.digsig.trust.SecureStorage;

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

	private ISecureStorage secureStorage;

	private String inputFileName = null;
	PrivateKey privateKey = null;
	X509Certificate certificate = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			secureStorage = new SecureStorage();
		} catch (DigSigException e) {
			Log.e(TAG, "Failed to initialize", e);
			setResult(RESULT_CANCELED);
			finish();
			return;
		}

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

		if (!secureStorage.isReady()) {
			Intent intent = new Intent(AndroidSecureStorageConstants.getUnlockAction());
			Log.i(TAG, "Trying to unlock secure storage");
			startActivityForResult(intent, UNLOCK_AND_INSTALL_IDENTITY);
		} else {
			installIdentity();
		}
	}

	private void reportFailedInstall() {
		Log.w(TAG, "Identity installation failed");
		Toast.makeText(this, "Identity installation failed !", Toast.LENGTH_SHORT).show();
		setResult(RESULT_CANCELED);
		finish();		
	}

	private void installIdentity() {

		Log.i(TAG, "installIdentity");
		
		if (certificate == null || privateKey == null || !secureStorage.isReady()) { 
			Log.w(TAG, "installIdentity: encodedCert = " + certificate + ", encodedKey = " + privateKey +
					", secureStorage.isReady = " + secureStorage.isReady());
			reportFailedInstall();
			return;
		}
		
		
		

//		KeyStore keyStore;
//		try {
//			keyStore = KeyStore.getInstance("AndroidKeyStore");
//			keyStore.load(null);
//			Log.i(TAG, "Key store type: " + keyStore.getType());
//			Log.i(TAG, "Key store size: " + keyStore.size());
//			Log.i(TAG, "Key store alises: " + keyStore.aliases());
//
//			keyStore.setCertificateEntry("CERT_0", certificate);
//			Log.i(TAG, "Stored certificate of type " + certificate.getType());
//			keyStore.setKeyEntry("KEY_0", privateKey, null, new Certificate[] {certificate});
//			Log.i(TAG, "Stored key of format " + privateKey.getFormat());
//
//			
//			keyStore = KeyStore.getInstance("AndroidKeyStore");
//			keyStore.load(null);
//			Log.d(TAG, "Retrieved certificate of type " + keyStore.getCertificate("CERT_0").getType());
//			Log.d(TAG, "Retrieved key of format " + keyStore.getKey("KEY_0", null));
//			
//			Toast.makeText(this, "Digital identity installed sucessfully", Toast.LENGTH_SHORT).show();
//			setResult(RESULT_OK);
//			finish();				
//			return;
//		} catch (Exception e) {
//			Log.w(TAG, "installIdentity", e);
//			setResult(RESULT_CANCELED);
//			finish();				
//			return;
//		}

		
		
		
		int count = 0;

		count = -1;
		while (secureStorage.containsCertificateAndKey(++count)) {

			X509Certificate alreadyStoredCert = secureStorage.getCertificate(count);
			PrivateKey alreadyStoredKey = secureStorage.getPrivateKey(count);
			if (certificate.equals(alreadyStoredCert) && privateKey.equals(alreadyStoredKey)) {
				String msg = "Digital identity " + certificate.getSubjectDN().getName() + " is already installed.";
				Log.i(TAG, msg);
				Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK);
				finish();				
				return;
			}
		}

		int index = secureStorage.put(certificate, privateKey);
		String msg = "Digital identity " + certificate.getSubjectDN().getName() + " installed under index " + index;
		Log.i(TAG, msg);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		setResult(RESULT_OK);
		finish();				
		return;
	}
}
