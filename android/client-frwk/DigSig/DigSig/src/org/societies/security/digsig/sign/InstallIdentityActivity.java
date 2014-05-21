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

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
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

/**
 * The {@link Activity} for installing new identities, e.g., new X.509 certificates
 * that contain also private keys.
 * 
 * @author Mitja Vardjan and Miroslav Pavleski
 */
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

		if (requestCode == LIST_PFX_FILES && resultCode == RESULT_OK) {
			inputFileName = data.getStringExtra(Sign.Params.IDENTITY);
			Log.i(TAG, String.format("Returned this file name %s", inputFileName));

			Intent i = new Intent(this,PassEntryActivity.class);
			startActivityForResult(i,PASS_ENTRY);
		} else if (resultCode == RESULT_CANCELED) {
			Log.i(TAG, "Cancelled");
			setResult(RESULT_CANCELED);
			finish();
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
		
		int count = -1;
		while (secureStorage.containsCertificateAndKey(++count)) {

			X509Certificate alreadyStoredCert = secureStorage.getCertificate(count);
			PrivateKey alreadyStoredKey = secureStorage.getPrivateKey(count);
			
			Log.d(TAG, "Comparing new certificate " + certificate.getSubjectDN().getName() +
					" to already stored certificate " + alreadyStoredCert.getSubjectDN().getName());
			Log.d(TAG, "Certificates equal = " + certificate.equals(alreadyStoredCert));
			Log.d(TAG, "Keys equal = " + Arrays.equals(privateKey.getEncoded(), alreadyStoredKey.getEncoded()));
			
			// From Android 4.3, keys and certificates are somewhat changed when stored.
			// When retrieved from Android's they still perform the same, but are not byte to byte
			// equal to the ones that were stored.
			// Certificates can still be compared with java.security.cert.Certificate.equals() method
			// but there is no such method for keys.
			// When comparing byte array representations of keys (or certificates), they will always seem different.
			// So only certificates can be reliably compared with Certificate.equals() method.
			if (certificate.equals(alreadyStoredCert)) {
				
				Log.i(TAG, "Identity " + certificate.getSubjectDN().getName() + " is already installed.");
				Toast.makeText(this,  "Identity is already installed.", Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK);
				finish();				
				return;
			}
		}

		int index = secureStorage.put(certificate, privateKey);
		Log.i(TAG, "Digital identity " + certificate.getSubjectDN().getName() + " installed under index " + index);
		Toast.makeText(this, "Identity installed successfully", Toast.LENGTH_SHORT).show();
		setResult(RESULT_OK);
		finish();				
		return;
	}
}
