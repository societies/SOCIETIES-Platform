package org.societies.security.digsig.sign;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.societies.security.digsig.trust.AndroidSecureStorage;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SignActivity extends Activity {
	
	private final static int SELECT_IDENTITY = 1;
	private static final String TAG = SignActivity.class.getSimpleName();

	private AndroidSecureStorage secureStorage;

	private KeyFactory keyFactory;
	private CertificateFactory certFactory;
	private DocumentBuilderFactory dbf;
	private DocumentBuilder docBuilder;
	private DOMImplementationImpl domImpl;
	private LSSerializer serializer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");

		try {
			secureStorage = AndroidSecureStorage.getInstance();
			keyFactory = KeyFactory.getInstance("RSA");
			certFactory = CertificateFactory.getInstance("X.509");

			if (!Init.isInitialized())
				Init.init();

			dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);

			docBuilder = dbf.newDocumentBuilder();

			domImpl = new DOMImplementationImpl();

			serializer = domImpl.createLSSerializer();
			DOMConfiguration config = serializer.getDomConfig();
			config.setParameter("comments", Boolean.valueOf(true));
		} catch (Exception e) {
			Log.e(TAG, "Failed to initialize", e);
			setResult(RESULT_CANCELED);
			finish();
			return;
		}

		Intent i = new Intent(this, ListIdentitiesActivity.class);
		startActivityForResult(i, SELECT_IDENTITY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(TAG, "onActivityResult");

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SELECT_IDENTITY && resultCode == RESULT_OK) {
			doSign(data);
		}
	}

	private void doSign(Intent data) {
		int selected = data.getIntExtra("SELECTED", -1);
		if (selected == -1)
			return;

		String certKey = String.format("CERT_%d", selected);
		String keyKey = String.format("KEY_%d", selected);

		byte[] encodedCert = secureStorage.getWithStringKey(certKey);
		byte[] encodedKey = secureStorage.getWithStringKey(keyKey);
		if (encodedCert == null || encodedKey == null)
			return;

		// Parse key and cert
		X509Certificate cert = null;
		Key key = null;

		try {
			cert = (X509Certificate) certFactory
					.generateCertificate(new ByteArrayInputStream(encodedCert));

			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(
					encodedKey);
			key = keyFactory.generatePrivate(privKeySpec);
		} catch (Exception e) {
			Log.e(TAG, "Failed while decoding identity!", e);
			setResult(RESULT_CANCELED);
			finish();
		}
		if (cert == null || key == null) {
			Log.e(TAG, "Retrieved empty identity from storage!");
			setResult(RESULT_CANCELED);
			finish();
			return;
		}

		Document doc;
		XMLSignature sig;
		try {
			byte[] val = getIntent().getByteArrayExtra("XML");

			doc = docBuilder.parse(new ByteArrayInputStream(val));
			sig = new XMLSignature(doc, null,
					XMLSignature.ALGO_ID_SIGNATURE_RSA);

			doc.getDocumentElement().appendChild(sig.getElement());

			Transforms transforms = new Transforms(doc);
			// Also must use c14n
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);

			ArrayList<String> idsToSign = getIntent().getStringArrayListExtra(
					"IDS_TO_SIGN");
			for (String id : idsToSign)
				sig.addDocument("#" + id, transforms,
						MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);

			sig.addKeyInfo(cert);

			sig.sign(key);

			sig.getElement().setAttribute("Id",
					"Signature-" + UUID.randomUUID().toString());

			LSOutput domOutput = domImpl.createLSOutput();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			domOutput.setByteStream(output);
			domOutput.setEncoding("UTF-8");
			serializer.write(doc, domOutput);

			Intent result = getIntent();
			result.putExtra("SIGNED_XML", output.toByteArray());

			setResult(RESULT_OK, result);
			finish();
		} catch (Exception e) {
			Log.e(TAG, "Failed while signing!", e);
			setResult(RESULT_CANCELED);
			finish();
		}
	}
}
