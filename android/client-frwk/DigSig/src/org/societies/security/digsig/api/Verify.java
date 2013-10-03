package org.societies.security.digsig.api;

import android.app.Service;

/**
 * External API.
 * Parameters to invoke the {@link Service} for verification and other actions that do not require user approval.
 * 
 * @author Mitja Vardjan
 */
public class Verify {
	
	/**
	 * Intent extras
	 */
	public class Methods {
		
		/**
		 * Get public certificate
		 */
		public static final int GET_CERTIFICATE = 1;
		
		/**
		 * Verify a digital signature
		 */
		public static final int VERIFY = 2;
		
		/**
		 * Generate URIs for community signatures
		 * 
		 * Required parameters: hostname, notification endpoint
		 */
		public static final int GENERATE_URIS = 3;
	}
}
