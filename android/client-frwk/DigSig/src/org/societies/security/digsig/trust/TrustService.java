package org.societies.security.digsig.trust;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.societies.security.digsig.api.Trust;
import org.societies.security.digsig.utility.Hash;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * @author Miroslav Pavleski
 *
 * ToDo
 * Add protection token in the secure storage to be used for importing CACerts
 *
 * This is to be used only for CA certificates management (trusted certs)
 *
 */
public class TrustService extends Service {
	public static final String SERVICE_INSTALL_TRUSTED = "org.societies.security.digsig.trust.InstallTrusted";
	public static final String SERVICE_REMOVE_TRUSTED = "org.societies.security.digsig.trust.RemoveTrusted";
	
	public static final String SERVICE_INSTALL_IDENTITY = "org.societies.security.digsig.trust.InstallIdentity";
	
	
	// These are not needed for certificate management
	public static final String SERVICE_GET_IDENTITIES = "org.societies.security.digsig.trust.ListIdentities";
	
	public static final String SERVICE_GET_IDENTITY_CERT = "org.societies.security.digsig.trust.GetIdentityCert";
	
	public static final String SERVICE_ENCRYPT_WITH_IDENTITY = "org.societies.security.digsig.trust.EncryptWithIdentity";
	
	private static final String USER_STORE_FILE = "cacerts.bks";
	
	private static final String CERTIFICATE = "CERTIFICATE";
	
	private KeyStore systemStore; 	// Stores trusted system certs
	private KeyStore userStore;		// Stores trusted user certs	
	
	private CertificateFactory certFactory;
	
	private AndroidSecureStorage secureStorage;
	
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			default:
				super.handleMessage(msg);
			}
		}
	}	
	
		
	
	private void installTrusted(Intent intent) {
		init();
		
		byte[] x509Data = intent.getByteArrayExtra(CERTIFICATE);
		if (x509Data==null) return;
		
		X509Certificate cert = null;
		try {
			cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(x509Data));
		} catch (CertificateException e) {}
		
		if (cert==null) return;
		
		String hashAlias = Hash.doHashToString(x509Data);
		boolean isAlreadyPresent=false;
		try {
			isAlreadyPresent = userStore.isCertificateEntry(hashAlias);
		} catch (KeyStoreException e) {}
		
		if (isAlreadyPresent) return;
				
		try {
			userStore.setCertificateEntry(hashAlias, cert);			
			userStore.store(openFileOutput(USER_STORE_FILE,Context.MODE_PRIVATE), "changeit".toCharArray());
		} catch (Exception e) {}			
	}
	
	private void removeTrusted(Intent intent) {
		init();
		
		byte[] x509Data = intent.getByteArrayExtra(CERTIFICATE);
		if (x509Data==null) return;
		
		X509Certificate cert = null;
		try {
			cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(x509Data));
		} catch (CertificateException e) {}
		
		if (cert==null) return;
		
		String hashAlias = Hash.doHashToString(x509Data);
		boolean isAlreadyPresent=false;
		try {
			isAlreadyPresent = userStore.isCertificateEntry(hashAlias);
		} catch (KeyStoreException e) {}
		
		if (!isAlreadyPresent) return;
				
		try {
			userStore.deleteEntry(hashAlias);					
			userStore.store(openFileOutput(USER_STORE_FILE,Context.MODE_PRIVATE), "changeit".toCharArray());
		} catch (Exception e) {}			
	}
	
	private void installIdentity(Intent intent) {
	
		try {
			KeyStore signStore = KeyStore.getInstance("pkcs12");
			
			byte[] pkcs12Data = intent.getByteArrayExtra(Trust.Params.PKCS12);
			char[] password = intent.getCharArrayExtra(Trust.Params.PASSWORD);
			if (password == null || pkcs12Data == null) return;		
			
			
			signStore.load(new ByteArrayInputStream(pkcs12Data),password);
			
			Enumeration<String> aliases = signStore.aliases();
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				
				Key key = signStore.getKey(alias, password);
				X509Certificate cert = (X509Certificate) signStore.getCertificate(alias);
				if (key==null || cert==null) continue;
				Log.i("miki",String.format("Alias |%s|, format %s, algorithm %s", alias,key.getFormat(),key.getAlgorithm()));
				
				byte[] encodedKey = key.getEncoded();				
				byte[] encodedCert = cert.getTBSCertificate();
													
				
			}
		} catch(Exception e) {}
	}
		
	
		
	/**
	 * TODO do error handling 
	 */
	private void init() {
		if (systemStore!=null && userStore!=null) return;

		try {
			secureStorage = AndroidSecureStorage.getInstance();
			
			systemStore = KeyStore.getInstance("BKS");			
			systemStore.load(new FileInputStream(new File("/system/etc/security/cacerts.bks")), "changeit".toCharArray());
			
			userStore = KeyStore.getInstance("BKS");								
			userStore.load(openFileInput(USER_STORE_FILE), "changeit".toCharArray());		
			
			certFactory = CertificateFactory.getInstance("X.509");
		} catch(Exception e) {			
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
