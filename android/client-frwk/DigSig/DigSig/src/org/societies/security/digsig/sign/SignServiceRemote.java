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

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Map;

import org.societies.security.digsig.api.Verify;
import org.societies.security.digsig.apiinternal.Community;
import org.societies.security.digsig.apiinternal.RestServer;
import org.societies.security.digsig.trust.SecureStorage;
import org.societies.security.digsig.utility.KeyUtil;
import org.societies.security.digsig.utility.RandomString;
import org.societies.security.digsig.utility.StringUtil;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Service to be used by other processes (other apps).
 *
 * @author mitjav
 *
 */
public class SignServiceRemote extends Service {

	private static final String TAG = SignServiceRemote.class.getSimpleName();

	public static final String ALGORITHM = "MD5WithRSA";
	public static final String ENCODING = "UTF8";

	private SecureStorage secureStorage;

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler(this));

	/**
	 * When binding to the service, we return an interface to our messenger
	 * for sending messages to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind");
		return mMessenger.getBinder();
	}

	/**
	 * Handler of incoming messages from clients.
	 */
	static class IncomingHandler extends Handler {

		private final WeakReference<SignServiceRemote> mService;

		IncomingHandler(SignServiceRemote service) {
			mService = new WeakReference<SignServiceRemote>(service);
		}

		@Override
		public void handleMessage(Message msg) {

			Log.i(TAG, "handleMessage: msg.what = " + msg.what + ", replyTo = " + msg.replyTo);
			try {
				mService.get().secureStorage = new SecureStorage();
			} catch (DigSigException e) {
				Log.w(TAG, "handleMessage: Could not initialize", e);
			}

			switch (msg.what) {
			case Verify.Methods.GET_CERTIFICATE:
				mService.get().getCertificate(0);  // FIXME
				break;
			case Verify.Methods.VERIFY:
				mService.get().verify();
				break;
			case Verify.Methods.GENERATE_URIS:
				Message reply = Message.obtain(null, Verify.Methods.GENERATE_URIS, 0, 0);
				reply.setData(mService.get().generateUris(msg.getData()));
				try {
					msg.replyTo.send(reply);
				} catch (RemoteException e) {
					Log.i(TAG, "handleMessage: sending return message", e);
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	private void getCertificate(int index) {
		Log.w(TAG, "getCertificate: Not yet implemented");
		// TODO
		secureStorage.getCertificate(index);
	}

	private void verify() {
		Log.w(TAG, "verify: Not yet implemented");
		// TODO
	}

	private Bundle generateUris(Bundle data) {

		Log.i(TAG, "generateUris");

		Bundle bundle = new Bundle();

		String host = getText(R.string.docServerUrl).toString();
		String resourceName = RandomString.getRandomNumberString();
		X509Certificate cert = secureStorage.getCertificate(0);
		PrivateKey key = secureStorage.getPrivateKey(0);
		if (cert == null || key == null) {
			Log.w(TAG, "generateUris: cert = " + cert + ", key = " + key);
			bundle.putBoolean(Verify.Params.SUCCESS, false);
			return bundle;
		}
		
		String notificationEndpoint = data.getString(Verify.Params.NOTIFICATION_ENDPOINT);
		int numSignersThreshold = data.getInt(Verify.Params.NUM_SIGNERS_THRESHOLD, -1);
		String title = data.getString(Verify.Params.DOC_TITLE);
		if (title == null) {
			title = resourceName;
		}
		Log.d(TAG, "generateUris: notificationEndpoint = " + notificationEndpoint +
				", numSignersThreshold = " + numSignersThreshold);

		try {
			String signature = sign(resourceName.getBytes(ENCODING), key);
			String uploadUri = uriForFileUpload(host, resourceName, KeyUtil.cert2str(cert),
					notificationEndpoint, numSignersThreshold);
			String downloadUri = uriForFileDownload(host, resourceName, signature);
			bundle.putString(Verify.Params.UPLOAD_URI, uploadUri);
			bundle.putString(Verify.Params.DOWNLOAD_URI, downloadUri);
			bundle.putBoolean(Verify.Params.SUCCESS, true);
			store(title, downloadUri);
			return bundle;
		} catch (Exception e) {
			Log.w(TAG, "generateUris: error", e);
			bundle.putBoolean(Verify.Params.SUCCESS, false);
			return bundle;
		}
	}
	
	private void store(String key, String value) {
		SharedPreferences preferences = getSharedPreferences(Community.Preferences.DOWNLOAD_URIS, MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
		Log.d(TAG, "Stored key value pair: " + key + " = " + value);
	}

	/**
	 * URI for file download and file merge
	 */
	private String uriForFileDownload(String host, String path, String signature) {

		String uriStr;

		Log.d(TAG, "uriForFileDownload: host = " + host + ", path = " + path);

		uriStr = host + RestServer.BASE + RestServer.PATH_XML_DOCUMENTS + "/" + path.replaceAll(".*/", "") +
				"?" + RestServer.URL_PARAM_SIGNATURE + "=" + signature;

		Log.d(TAG, "uriForFileDownload(): uri = " + uriStr);
		return uriStr;
	}

	private String uriForFileUpload(String host, String path, String cert,
			String notificationEndpoint, int numSignersThreshold) {

		String uriStr;

		Log.d(TAG, "uriForFileUpload: host = " + host + ", path = " + path);

		try {
			cert = URLEncoder.encode(cert, ENCODING);
		} catch (UnsupportedEncodingException e) {
			Log.w(TAG, "Could not URL encode certificate", e);
		}

		uriStr = host + RestServer.BASE + RestServer.PATH_XML_DOCUMENTS + "/" + path.replaceAll(".*/", "") +
				"?" + RestServer.URL_PARAM_CERT + "=" + cert;

		if (notificationEndpoint != null) {
			try {
				notificationEndpoint = URLEncoder.encode(notificationEndpoint, ENCODING);
				uriStr +=
						"&" + RestServer.URL_PARAM_NOTIFICATION_ENDPOINT + "=" + notificationEndpoint +
						"&" + RestServer.URL_PARAM_NUM_SIGNERS_THRESHOLD + "=" + numSignersThreshold;
			} catch (UnsupportedEncodingException e) {
				Log.w(TAG, "Could not append notification endpoint parameter", e);
			}
		}

		Log.d(TAG, "uriForFileUpload(): uri = " + uriStr);
		return uriStr;
	}


	public String sign(byte[] dataToSign, PrivateKey privateKey) throws DigSigException {

		Log.d(TAG, "Signing " + dataToSign + " with " + privateKey);

		Signature sig;
		byte[] signature;
		String signatureStr;

		if (dataToSign == null || privateKey == null) {
			Log.w(TAG, "verify(): All parameters must be non-null");
			return null;
		}

		try {
			sig = Signature.getInstance(ALGORITHM);
			sig.initSign(privateKey);
			sig.update(dataToSign);
			signature = sig.sign();
			signatureStr = new StringUtil().bytesToHexString(signature);
		} catch (Exception e) {
			Log.w(TAG, "Signing failed", e);
			throw new DigSigException(e);
		}

		Log.d(TAG, "Signature provider: " + sig.getProvider().getInfo());
		Log.d(TAG, "Signature: " + signatureStr);

		return signatureStr;
	}
}
