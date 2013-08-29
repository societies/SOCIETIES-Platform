package org.societies.security.digsig.api;

import android.content.Intent;

/**
 * Intertnal API.
 * {@link Intent} parameters for internal app use.
 * 
 * @author Mitja Vardjan
 */
public class Trust {
	
	/**
	 * Intent extras
	 */
	public class Params {
		
		public static final String PASSWORD = "PASSWORD";
		public static final String PKCS12 = "PKCS12";
		public static final String EXTENSIONS = "EXTENSIONS";
		public static final String RESULT = "RESULT";
	}
}
