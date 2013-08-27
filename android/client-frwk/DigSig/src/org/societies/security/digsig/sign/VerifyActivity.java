package org.societies.security.digsig.sign;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.Init;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.societies.security.digsig.common.SigResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class VerifyActivity extends Activity {
	private static final String TAG = VerifyActivity.class.getSimpleName();
	
	private List<X509Certificate> certs;
	private ArrayList<SigResult> results;
	
	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;
    	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			if (!Init.isInitialized()) Init.init();
			
			dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			
			db = dbf.newDocumentBuilder();
		} catch(Exception e) {
			
		}
	}
			
	@Override
	protected void onStart() {
		super.onStart();
		
		certs = null;
		results = null;
		
		byte[] val = getIntent().getByteArrayExtra("XML");     
							
		if (val==null) {
			setResult(RESULT_CANCELED);
	    	finish();
		}
		
		doCheckSignature(val);	
	}

	private void doCheckSignature(byte[] val) {
		LinkedList<Element> signatures = null;
		try
        {            
            Document doc = db.parse(new ByteArrayInputStream(val));
            
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/verify.xml");
            fos.write(val);
            fos.close();

            signatures = new LinkedList<Element>();
            NodeList nl = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
            
            Log.i(TAG, String.format("Retrieved %d signatures from the document...",nl.getLength()));
            
            results = new ArrayList<SigResult>(nl.getLength());   
            certs = new ArrayList<X509Certificate>(nl.getLength());
            for (int i = 0; i < nl.getLength(); i++)
            {
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
        	
        	setResult(RESULT_CANCELED);
        	finish();
        	return;
        }
                                            
        int resultNum=0;
        	
        for ( Element sig : signatures )
        {                        	
        	XMLSignature xmlSignature=null;
                
            SigResult result = results.get(resultNum++);
              
            try {
                xmlSignature = new XMLSignature(sig, null);
                
                X509Certificate sigCertificate = null;
                KeyInfo keyInfo = xmlSignature.getKeyInfo();
                if (keyInfo != null) sigCertificate = keyInfo.getX509Certificate();
                
                if (sigCertificate == null) continue; // error
                
                // Cache the certificate for the signature
                certs.set(resultNum-1, sigCertificate);
                                              
                boolean valid = xmlSignature.checkSignatureValue(sigCertificate);
                result.setSigStatus(valid ? 1 : 0);    
                                
            } catch (Exception e) {
            	// just continue, unknown data will be signaled in SigResult
            	Log.e(TAG, String.format("Failed while verifying %d signature.",resultNum), e); 
            }                                	
        } 
        
        // TODO handle trust checking for certificates
        // For now only fake it is ok:
        for (SigResult result : results) {
        	result.setTrustStatus(1);
        }
        
        // Write out to the intent
        Intent data = new Intent();
        data.putParcelableArrayListExtra("RESULT", results);
        setResult(RESULT_OK, data);
        
        
        finish();
	}
}
