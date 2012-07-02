package org.societies.security.storage;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import org.societies.security.digsig.util.StreamUtil;


public class CertStorage {
	private static CertStorage instance;

//	private XmlManipulator xml = Config.getInstance().getXml();
	private X509Certificate ourCert;
	private Key ourKey;
	
	
	private CertStorage() {
		initOurIdentity();
	}
		
	private void initOurIdentity() {
		InputStream ksStream = null; 
		try {
			
			KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
			
//			String fileName = xml.getElementContent("/ServiceProviderConfig/Certificates/OurIdentity/PKCS12");
//			String pass = xml.getElementContent("/ServiceProviderConfig/Certificates/OurIdentity/Password");
			String fileName = "filename";
			String pass = "password";
			
			ksStream = CertStorage.class.getClassLoader().getResourceAsStream(fileName);
			ks.load(ksStream,pass.toCharArray());
						
			String alias = ks.aliases().nextElement();
			ourCert = (X509Certificate) ks.getCertificate(alias);
			ourKey = ks.getKey(alias, pass.toCharArray());
			
			if (ourCert==null || ourKey==null) 
				throw new NullPointerException();
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize identity information", e);
		} finally {
			StreamUtil.closeStream(ksStream);
		}
	}
		
	public X509Certificate getOurCert() {
		return ourCert;
	}
	
	public Key getOurKey() {
		return ourKey;
	}

	public static synchronized CertStorage getInstance() {
		if (instance==null) instance = new CertStorage();
		return instance;
	}	
}
