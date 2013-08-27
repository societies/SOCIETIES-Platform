package org.societies.security.digsig.sign;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
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
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class SignService extends IntentService {

	private static final String TAG = SignService.class.getSimpleName();

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
	public android.os.IBinder onBind(Intent intent) {
		return null;  // FIXME
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.i(TAG, "intent received");

		byte[] doc = intent.getByteArrayExtra(Sign.Params.DOC_TO_SIGN);
		int identity = intent.getIntExtra(Sign.Params.IDENTITY, -1);
		List<String> ids = intent.getStringArrayListExtra(Sign.Params.IDS_TO_SIGN);
		String outputType = intent.getStringExtra(Sign.Params.OUTPUT_TYPE);

		Log.i(TAG, "DOC_TO_SIGN = " + doc);
		Log.i(TAG, "IDENTITY = " + identity);
		Log.i(TAG, "IDS_TO_SIGN = " + ids);
		Log.i(TAG, "OUTPUT_TYPE = " + outputType);

//		Intent broadcastIntent = new Intent();
//		broadcastIntent.setAction("TODO");  // FIXME
//		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
//		broadcastIntent.putExtra(RESPONSE_MESSAGE, "TODO");  // FIXME
//		sendBroadcast(broadcastIntent);
		
		try {
			initSecureStorage();
			doSign(intent);
		} catch (DigSigException e) {
			// TODO
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
		int selected = intent.getIntExtra("SELECTED", -1);
		if (selected==-1) {
			return;
		}

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
			byte[] val = intent.getByteArrayExtra("XML");        

			doc = docBuilder.parse(new ByteArrayInputStream(val));
			sig = new XMLSignature(doc,null,XMLSignature.ALGO_ID_SIGNATURE_RSA);

			doc.getDocumentElement().appendChild(sig.getElement());

			Transforms transforms = new Transforms(doc);            
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS); // Also must use c14n

			ArrayList<String> idsToSign = intent.getStringArrayListExtra("IDS_TO_SIGN");            
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

			intent.putExtra("SIGNED_XML", output.toByteArray());
			testWrite(intent);
		} catch (Exception e) {  
			Log.e(TAG, "Failed while signing!", e);
		}
	}
	
	private void testWrite(Intent resultIntent) {
		try	{
			byte[] signedXml = resultIntent.getByteArrayExtra("SIGNED_XML");
			FileOutputStream os = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/signed2.xml");
			os.write(signedXml);
			os.close();
		
			Toast.makeText(this, "File signed sucessfully.\nOutput is in signed2.xml on SD CARD!", Toast.LENGTH_LONG).show();
		} catch(Exception e) {
		}
	}
}
