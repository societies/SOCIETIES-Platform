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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.Init;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.societies.security.digsig.api.SigResult;
import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.api.Verify;
import org.societies.security.digsig.apiinternal.RestServer;
import org.societies.security.digsig.community.SharedPreferencesHelper;
import org.societies.security.digsig.trust.SecureStorage;
import org.societies.security.digsig.utility.KeyUtil;
import org.societies.security.digsig.utility.RandomString;
import org.societies.security.digsig.utility.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Service to be used by other processes (other apps) to:
 * <br>
 * - Get upload and download URIs.<br>
 * - Verify signatures (not yet implemented)<br>
 * - Get certificates (not yet implemented)<br>
 *
 * @author Mitja Vardjan
 *
 */
public class SignServiceRemote extends Service {

	private static final String TAG = SignServiceRemote.class.getSimpleName();

	public static final String ALGORITHM = "MD5WithRSA";
	public static final String ENCODING = "UTF8";

	private SecureStorage secureStorage;

	private DocumentBuilder db;

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

			Message reply;
			
			switch (msg.what) {
			case Verify.Methods.GET_CERTIFICATE:
				mService.get().getCertificate(0);  // FIXME
				break;
			case Verify.Methods.VERIFY:
				reply = Message.obtain(null, Verify.Methods.VERIFY, 0, 0);
				reply.setData(mService.get().verify(msg.getData()));
				if (msg.replyTo != null) {
					try {
						msg.replyTo.send(reply);
					} catch (RemoteException e) {
						Log.i(TAG, "handleMessage: sending return message", e);
					}
				}
				else {
					Log.w(TAG, "replyTo is null, cannot return the generated URI.");
				}
				break;
			case Verify.Methods.GENERATE_URIS:
				reply = Message.obtain(null, Verify.Methods.GENERATE_URIS, 0, 0);
				reply.setData(mService.get().generateUris(msg.getData()));
				if (msg.replyTo != null) {
					try {
						msg.replyTo.send(reply);
					} catch (RemoteException e) {
						Log.i(TAG, "handleMessage: sending return message", e);
					}
				}
				else {
					Log.w(TAG, "replyTo is null, cannot return the generated URI.");
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

	private void initXmlSecurityIfNecessary() throws ParserConfigurationException {

		if (!Init.isInitialized()) {
			Init.init();
		}

		if (db == null) {
			DocumentBuilderFactory dbf;
			dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			db = dbf.newDocumentBuilder();
		}
	}

	private Bundle verify(Bundle data) {

		try {
			initXmlSecurityIfNecessary();
		} catch (ParserConfigurationException e) {
			Log.w(TAG, "verify: failed", e);
		}

		String uriStr = data.getString(Verify.Params.DOC_TO_VERIFY_URI);
		Uri uri = Uri.parse(uriStr);

		if (uri == null) {
			Log.w(TAG, "No document to verify");
			return null;
		}

		InputStream is;
		try {
			is = getContentResolver().openInputStream(uri);
			return doCheckSignature(is);
		} catch (FileNotFoundException e) {
			Log.w(TAG, "File " + uriStr + " not found", e);
			return null;
		}
	}

	private Bundle doCheckSignature(InputStream is) {
		
		Bundle bundle = new Bundle();
		LinkedList<Element> signatures = null;
		ArrayList<SigResult> results;
		List<X509Certificate> certs;
		
		try {
			Document doc = db.parse(is);

			signatures = new LinkedList<Element>();
			NodeList nl = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");

			Log.i(TAG, String.format("Retrieved %d signatures from the document...", nl.getLength()));

			results = new ArrayList<SigResult>(nl.getLength());   
			certs = new ArrayList<X509Certificate>(nl.getLength());
			for (int i = 0; i < nl.getLength(); i++) {
				signatures.add((Element) nl.item(i));

				SigResult result = new SigResult(); // populate with unknown results

				result.setCert(new byte[0]);
				result.setSigStatus(-1);
				result.setTrustStatus(-1);

				results.add(result);
				certs.add(null);
			}
		} catch (Exception e) {
			Log.e(TAG, "Failed while parsing XML and extracting signatures.", e);

			bundle.putBoolean(Verify.Params.SUCCESS, false);
			return bundle;
		}

		int resultNum = 0;

		for ( Element sig : signatures ) {
			XMLSignature xmlSignature = null;
			SigResult result = results.get(resultNum++);

			try {
				xmlSignature = new XMLSignature(sig, null);

				X509Certificate sigCertificate = null;
				KeyInfo keyInfo = xmlSignature.getKeyInfo();
				if (keyInfo != null) sigCertificate = keyInfo.getX509Certificate();

				if (sigCertificate == null) {
					continue; // error
				}

				// Cache the certificate for the signature
				certs.set(resultNum-1, sigCertificate);

				boolean valid = xmlSignature.checkSignatureValue(sigCertificate);
				result.setSigStatus(valid ? 1 : 0);    

			} catch (Exception e) {
				// just continue, unknown data will be signaled in SigResult
				Log.e(TAG, String.format("Failed while verifying %d signature.", resultNum), e);
			}
		}

		// TODO handle trust checking for certificates
		// For now only fake it is ok:
		for (SigResult result : results) {
			result.setTrustStatus(1);
		}

		bundle.putParcelableArrayList(Verify.Params.RESULT, results);
		bundle.putBoolean(Verify.Params.SUCCESS, true);
		Log.i(TAG, "XML document signature verification completed successfully");
		return bundle;
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
		String title = data.getString(Sign.Params.DOC_TITLE);
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
			SharedPreferencesHelper preferences = new SharedPreferencesHelper(this);
			preferences.store(title, downloadUri);
			return bundle;
		} catch (Exception e) {
			Log.w(TAG, "generateUris: error", e);
			bundle.putBoolean(Verify.Params.SUCCESS, false);
			return bundle;
		}
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
