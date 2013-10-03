package org.societies.security.digsig.sign;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.trust.AndroidSecureStorage;
import org.societies.security.digsig.utility.Storage;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SignService extends IntentService {

	private static final String TAG = SignService.class.getSimpleName();

	private static final String TMP_FILE_PATH = "tmpFile";
	
	private AndroidSecureStorage secureStorage;
	private KeyFactory keyFactory;
	private CertificateFactory certFactory;
	private DocumentBuilderFactory dbf;
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
	public IBinder onBind(Intent intent) {
		return null;  // FIXME
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
			initSecureStorage();
			doSign(intent);
		} catch (DigSigException e) {
			Log.w(TAG, e);
		}
	}

	private void initSecureStorage() throws DigSigException {
		try {
			secureStorage = AndroidSecureStorage.getInstance();
			keyFactory = KeyFactory.getInstance("RSA");
			certFactory = CertificateFactory.getInstance("X.509");
	
			if (!Init.isInitialized()) {
				Init.init();
			}
	
			dbf = DocumentBuilderFactory.newInstance();
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

		String certKey = String.format(Locale.US, "CERT_%d", selected);
		String keyKey = String.format(Locale.US, "KEY_%d", selected);

		byte[] encodedCert = secureStorage.getWithStringKey(certKey);
		byte[] encodedKey = secureStorage.getWithStringKey(keyKey);
		if (encodedCert==null || encodedKey==null) return;

		// Parse key and cert
		X509Certificate cert = null;
		Key key = null;

		try {
			cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(encodedCert));

			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encodedKey );            
			key = keyFactory.generatePrivate(privKeySpec);
		} catch (Exception e) { 
			Log.e(TAG, "Failed while decoding identity!", e);
		}
		if (cert==null || key==null) {
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
		sendBroadcast(bcIntent);
	}
	
	private InputStream getDocToSign(Intent intent) throws DigSigException {
		
		try {
			byte[] val = intent.getByteArrayExtra(Sign.Params.DOC_TO_SIGN);
			if (val != null) {
				return new ByteArrayInputStream(val);
			}
			else {
				String docUrl = intent.getStringExtra(Sign.Params.DOC_TO_SIGN_URL);
				URL url = new URL(docUrl);
				String protocol = url.getProtocol();
				if (protocol.equals("file")) {
					return new FileInputStream(url.getPath());
				} else if (protocol.startsWith("http") || protocol.startsWith("ftp")) {
					download((url), TMP_FILE_PATH);
					return openFileInput(TMP_FILE_PATH);
				} else {
					throw new DigSigException("Unsupported protocol: " + url.getProtocol());
				}
			}
		} catch (Exception e) {
			throw new DigSigException(e);
		}
	}

	private void download(URL url, String path) throws DigSigException {
		
		Log.d(TAG, "download(" + url + ", " + path + ")");

		FileOutputStream fos;
		
		try {
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();
			InputStream is = c.getInputStream();

			fos = openFileOutput(path, MODE_PRIVATE);

			byte[] buffer = new byte[1024 * 1024];
			int len = 0;
			while ( (len = is.read(buffer)) > 0 ) {
				fos.write(buffer, 0, len);
			}
			fos.close();

		} catch (Exception e) {
			throw new DigSigException(e);
		}
	}
}
