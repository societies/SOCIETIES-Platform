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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.community.SharedPreferencesHelper;
import org.societies.security.digsig.sign.contentprovider.DocContentProvider;
import org.societies.security.digsig.trust.SecureStorage;
import org.societies.security.digsig.utility.Net;
import org.societies.security.digsig.utility.Storage;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Service to be used internally, by this app only, to sign XML documents.
 * 
 * Signing of any binary data is handled by {@link DigSig}.
 */
public class SignService extends IntentService {

	private static final String TAG = SignService.class.getSimpleName();

	private static final String TMP_FILE_PATH = "tmpFile";
	
	private SecureStorage secureStorage;
	private DocumentBuilder docBuilder;	
	private DOMImplementationImpl domImpl;
	private LSSerializer serializer;

	/** 
	 * A constructor is required, and must call the super IntentService(String)
	 * constructor with a name for the worker thread.
	 */
	public SignService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.i(TAG, "intent received");

		byte[] doc = intent.getByteArrayExtra(Sign.Params.DOC_TO_SIGN);
		String docUrl = intent.getStringExtra(Sign.Params.DOC_TO_SIGN_URL);
		int identity = intent.getIntExtra(Sign.Params.IDENTITY, -1);
		List<String> ids = intent.getStringArrayListExtra(Sign.Params.IDS_TO_SIGN);
		String outputType = intent.getStringExtra(Sign.Params.OUTPUT_TYPE);
		String serverUri = intent.getStringExtra(Sign.Params.COMMUNITY_SIGNATURE_SERVER_URI);

		Log.i(TAG, "DOC_TO_SIGN = " + (doc != null ? new String(doc) : null));
		Log.i(TAG, "DOC_TO_SIGN_URL = " + docUrl);
		Log.i(TAG, "IDENTITY = " + identity);
		Log.i(TAG, "IDS_TO_SIGN = " + ids);
		Log.i(TAG, "OUTPUT_TYPE = " + outputType);
		Log.i(TAG, "COMMUNITY_SIGNATURE_SERVER_URI = " + serverUri);

		try {
			init();
			doSign(intent);
		} catch (DigSigException e) {
			Log.w(TAG, e);
		}
	}

	private void init() throws DigSigException {
		try {
			secureStorage = new SecureStorage();
			
			if (!Init.isInitialized()) {
				Init.init();
			}
	
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
	
			docBuilder = dbf.newDocumentBuilder();
	
			domImpl = new DOMImplementationImpl();
	
			serializer = domImpl.createLSSerializer();
			DOMConfiguration config = serializer.getDomConfig();
			config.setParameter("comments", Boolean.valueOf(true));
		} catch (Exception e) {
			Log.e(TAG, "Failed to initialize", e);
			throw new DigSigException(e);
		}
	}

	private void doSign(Intent intent) {
		int selected = intent.getIntExtra(Sign.Params.IDENTITY, -1);
		if (selected==-1) {
			return;
		}
		Intent bcIntent = new Intent();

		X509Certificate cert = secureStorage.getCertificate(selected);
		PrivateKey key = secureStorage.getPrivateKey(selected);

		if (cert == null || key == null) {
			Log.e(TAG, "Retrieved empty identity from storage!");
			return;
		}

		Document doc;
		XMLSignature sig;
		try	{
			InputStream is = getDocToSign(intent);
			doc = docBuilder.parse(is);
			sig = new XMLSignature(doc,null,XMLSignature.ALGO_ID_SIGNATURE_RSA);

			doc.getDocumentElement().appendChild(sig.getElement());

			Transforms transforms = new Transforms(doc);            
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS); // Also must use c14n

			ArrayList<String> idsToSign = intent.getStringArrayListExtra(Sign.Params.IDS_TO_SIGN);            
			for (String id : idsToSign)             
				sig.addDocument("#"+id,transforms,MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);            

			sig.addKeyInfo(cert);

			sig.sign(key);

			sig.getElement().setAttribute("Id", "Signature-"+UUID.randomUUID().toString());

			LSOutput domOutput = domImpl.createLSOutput();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			domOutput.setByteStream(output);
			domOutput.setEncoding("UTF-8");	        
			serializer.write(doc, domOutput);

			String path = intent.getStringExtra(Sign.Params.SIGNED_DOC_URL);
			Log.d(TAG, "Writing signed doc to internal storage file " + path);
			new Storage(this).writeToInternalStorage(path, output.toByteArray());
			Log.d(TAG, "Written signed doc to internal storage file " + path);
			
			String serverUri = intent.getStringExtra(Sign.Params.COMMUNITY_SIGNATURE_SERVER_URI);
			if (serverUri != null) {
				InputStream signedIs = getContentResolver().openInputStream(DocContentProvider.localPath2Uri(path));
				bcIntent.putExtra(Sign.Params.UPLOAD_SUCCESS, upload(new URI(serverUri), signedIs));
				String title = intent.getStringExtra(Sign.Params.DOC_TITLE);
				if (title == null) {
					title = path;
				}
				SharedPreferencesHelper preferences = new SharedPreferencesHelper(this);
				preferences.store(title, serverUri);
			}
			
			bcIntent.putExtra(Sign.Params.SUCCESS, true);
			
		} catch (Exception e) {  
			Log.e(TAG, "Failed while signing: " + e.getMessage());
			bcIntent.putExtra(Sign.Params.SUCCESS, false);
		}
		int sessionId = intent.getIntExtra(Sign.Params.SESSION_ID, -1);
		if (sessionId < 0) {
			Log.w(TAG, "Missing session ID");
		}
		bcIntent.putExtra(Sign.Params.SESSION_ID, sessionId);
		bcIntent.setAction(Sign.ACTION_FINISHED);
		Log.d(TAG, "Sending broadcast to announce finished signing");
		sendBroadcast(bcIntent);
	}
	
	private InputStream getDocToSign(Intent intent) throws DigSigException {
		
		byte[] val = intent.getByteArrayExtra(Sign.Params.DOC_TO_SIGN);
		String docUrl = intent.getStringExtra(Sign.Params.DOC_TO_SIGN_URL);
		if (val != null) {
			Log.v(TAG, "Document is passed in the intent");
			return new ByteArrayInputStream(val);
		}
		else if (docUrl != null) {
			Log.v(TAG, "Document is passed as URL: " + docUrl);
			try {
				URL url = new URL(docUrl);
				String protocol = url.getProtocol();
				if (protocol.equals("file")) {
					return new FileInputStream(url.getPath());
				} else if (protocol.startsWith("http") || protocol.startsWith("ftp")) {
					OutputStream os = openFileOutput(TMP_FILE_PATH, MODE_PRIVATE);
					download(url.toURI(), os);
					return openFileInput(TMP_FILE_PATH);
				} else {
					throw new DigSigException("Unsupported protocol: " + url.getProtocol());
				}
			} catch (Exception e) {
				throw new DigSigException(e);
			}
		}
		else {
			throw new DigSigException("Invalid intent, missing either " +
					Sign.Params.DOC_TO_SIGN + " extra, or " + Sign.Params.DOC_TO_SIGN_URL + " extra.");
		}
	}

	private void download(URI uri, OutputStream os) throws FileNotFoundException, IOException {
		
		Log.d(TAG, "download(" + uri + ")");
		
		Net net = new Net(uri);
		net.get(os);
	}
	
	private boolean upload(URI uri, InputStream is) throws FileNotFoundException, IOException {
		
		Log.d(TAG, "upload(" + uri + ")");
		Log.d(TAG, "upload: input stream = " + is);
		
		Net net = new Net(uri);
		HttpEntity entity = new InputStreamEntity(is, -1);
		return net.put(entity);
	}
}
