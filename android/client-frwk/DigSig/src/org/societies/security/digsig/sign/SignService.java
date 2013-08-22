package org.societies.security.digsig.sign;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
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
import org.societies.security.digsig.trust.AndroidSecureStorage;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SignService extends IntentService {

	private static final String TAG = SignService.class.getSimpleName();

	private static final String RESPONSE_MESSAGE = "myResponseMessage";

	public SignService() {
		super(TAG);
	}

	@Override
	public android.os.IBinder onBind(Intent intent) {
		return null;  // FIXME
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.i(TAG, "intent received");

		byte[] doc = intent.getByteArrayExtra(Sign.DOC_TO_SIGN);
		String identity = intent.getStringExtra(Sign.IDENTITY);
		List<String> ids = intent.getStringArrayListExtra(Sign.IDS_TO_SIGN);
		String outputType = intent.getStringExtra(Sign.OUTPUT_TYPE);

		Log.i(TAG, "DOC_TO_SIGN = " + doc);
		Log.i(TAG, "IDENTITY = " + identity);
		Log.i(TAG, "IDS_TO_SIGN = " + ids);
		Log.i(TAG, "OUTPUT_TYPE = " + outputType);

		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("TODO");  // FIXME
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(RESPONSE_MESSAGE, "TODO");  // FIXME
		sendBroadcast(broadcastIntent);
	}



	private AndroidSecureStorage secureStorage;

	private KeyFactory keyFactory;
	private CertificateFactory certFactory;
	private DocumentBuilderFactory dbf;
	private DocumentBuilder docBuilder;	
	private DOMImplementationImpl domImpl;
	private LSSerializer serializer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");

		try {
			secureStorage = AndroidSecureStorage.getInstance();
			keyFactory = KeyFactory.getInstance("RSA");
			certFactory = CertificateFactory.getInstance("X.509");

			if (!Init.isInitialized()) Init.init();

			dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);

			docBuilder = dbf.newDocumentBuilder();

			domImpl = new DOMImplementationImpl();

			serializer = domImpl.createLSSerializer();
			DOMConfiguration config = serializer.getDomConfig();
			config.setParameter("comments", Boolean.valueOf(true));
		} catch(Exception e) {
			Log.e(TAG, "Failed to initialize", e);
			return;
		}
	}

	private void doSign(Intent data) {
//		int selected = data.getIntExtra("SELECTED", -1);
//		if (selected==-1) return;
//
//		String certKey = String.format("CERT_%d", selected);
//		String keyKey = String.format("KEY_%d", selected);
//
//		byte[] encodedCert = secureStorage.getWithStringKey(certKey);
//		byte[] encodedKey = secureStorage.getWithStringKey(keyKey);
//		if (encodedCert==null || encodedKey==null) return;
//
//		// Parse key and cert
//		X509Certificate cert = null;
//		Key key = null;
//
//		try {
//			cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(encodedCert));
//
//			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encodedKey );            
//			key = keyFactory.generatePrivate(privKeySpec);
//		} catch (Exception e) { 
//			Log.e(TAG, "Failed while decoding identity!", e);
//		}
//		if (cert==null || key==null) {
//			Log.e(TAG, "Retrieved empty identity from storage!");
//			return;
//		}
//
//		Document doc;
//		XMLSignature sig;
//		try	{	    
//			byte[] val = getIntent().getByteArrayExtra("XML");        
//
//			doc = docBuilder.parse(new ByteArrayInputStream(val));
//			sig = new XMLSignature(doc,null,XMLSignature.ALGO_ID_SIGNATURE_RSA);
//
//			doc.getDocumentElement().appendChild(sig.getElement());
//
//			Transforms transforms = new Transforms(doc);            
//			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS); // Also must use c14n
//
//			ArrayList<String> idsToSign = getIntent().getStringArrayListExtra("IDS_TO_SIGN");            
//			for (String id : idsToSign)             
//				sig.addDocument("#"+id,transforms,MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);            
//
//			sig.addKeyInfo(cert);
//
//			sig.sign(key);
//
//			sig.getElement().setAttribute("Id", "Signature-"+UUID.randomUUID().toString());
//
//			LSOutput domOutput = domImpl.createLSOutput();
//			ByteArrayOutputStream output = new ByteArrayOutputStream();
//			domOutput.setByteStream(output);
//			domOutput.setEncoding("UTF-8");	        
//			serializer.write(doc, domOutput);
//
//			Intent result = getIntent();
//			result.putExtra("SIGNED_XML", output.toByteArray());	
//		} catch (Exception e) {  
//			Log.e(TAG, "Failed while signing!", e);
//		}	    		    		    		
	}
}
