package org.societies.security.digsig.sign;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
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

import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.societies.security.digsig.api.Sign;
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
 * Service to be used internally, by this app only.
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

		Log.i(TAG, "DOC_TO_SIGN = " + (doc != null ? new String(doc) : null));
		Log.i(TAG, "DOC_TO_SIGN_URL = " + docUrl);
		Log.i(TAG, "IDENTITY = " + identity);
		Log.i(TAG, "IDS_TO_SIGN = " + ids);
		Log.i(TAG, "OUTPUT_TYPE = " + outputType);

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
			bcIntent.putExtra(Sign.Params.SUCCESS, true);
			
		} catch (Exception e) {  
			Log.e(TAG, "Failed while signing!", e);
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

	private void download(URI uri, OutputStream os) {
		
		Log.d(TAG, "download(" + uri + ")");
		
		Net net = new Net(uri);
		net.get(os);
	}
}
