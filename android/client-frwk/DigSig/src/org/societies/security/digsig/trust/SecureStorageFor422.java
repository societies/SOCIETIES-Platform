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
package org.societies.security.digsig.trust;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Locale;

import org.societies.security.digsig.apiinternal.ISecureStorage;
import org.societies.security.digsig.sign.DigSigException;

import android.util.Log;

/**
 * Helper class for retrieving X.509 certificates and private keys from android secure storage.
 *
 * @author Mitja Vardjan, based on code from Miroslav Pavleski
 *
 */
public class SecureStorageFor422 implements ISecureStorage {

	private static final String TAG = SecureStorageFor422.class.getSimpleName();
	
	private AndroidSecureStorage secureStorage;
	private KeyFactory keyFactory;
	private CertificateFactory certFactory;

	public SecureStorageFor422() throws DigSigException {
		try {
			secureStorage = AndroidSecureStorage.getInstance();
			keyFactory = KeyFactory.getInstance("RSA");
			certFactory = CertificateFactory.getInstance("X.509");
		} catch (Exception e) {
			Log.e(TAG, "Failed to initialize", e);
			throw new DigSigException(e);
		}
	}

	@Override
	public X509Certificate getCertificate(int index) {
		
		String certKey = String.format(Locale.US, "CERT_%d", index);

		byte[] encodedCert = secureStorage.getWithStringKey(certKey);
		if (encodedCert == null) {
			Log.e(TAG, "Could not get certificate for identity No. " + index);
			return null;
		}

		X509Certificate cert = null;

		try {
			cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(encodedCert));
		} catch (Exception e) { 
			Log.e(TAG, "Failed while decoding certificate for identity No. " + index, e);
		}
		return cert;
	}
	
	@Override
	public PrivateKey getPrivateKey(int index) {
		
		String keyKey = String.format(Locale.US, "KEY_%d", index);

		byte[] encodedKey = secureStorage.getWithStringKey(keyKey);
		if (encodedKey == null) {
			Log.e(TAG, "Could not get private key for identity No. " + index);
			return null;
		}

		PrivateKey key = null;

		try {
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encodedKey);            
			key = keyFactory.generatePrivate(privKeySpec);
		} catch (Exception e) { 
			Log.e(TAG, "Failed while decoding private key for identity No. " + index, e);
		}
		return key;
	}

	@Override
	public int setIdentity(X509Certificate certificate, PrivateKey key) {
		// TODO Auto-generated method stub
		return 0;
	}
}
