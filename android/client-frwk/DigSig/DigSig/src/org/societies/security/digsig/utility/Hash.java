package org.societies.security.digsig.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Base64;

public class Hash {
	static MessageDigest md;
		
	private static MessageDigest getMD() {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {}
		return md;
	}
	
	public static byte[] doHash(byte[] input) {
		md =  getMD();
		if (md==null) return null;
		return md.digest(input);
	}
	
	public static String doHashToString(byte[] input) {
		md = getMD();
		if (md==null) return null;
		byte[] hash = md.digest(input);	
		
		return Base64.encodeToString(hash, Base64.DEFAULT);
	}
}
