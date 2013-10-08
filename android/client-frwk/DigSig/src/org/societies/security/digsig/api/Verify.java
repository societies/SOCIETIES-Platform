package org.societies.security.digsig.api;

import android.app.Service;
import android.content.Intent;

/**
 * External API.
 * Parameters to invoke the {@link Service} for verification and other actions that do not require user approval.
 * 
 * @author Mitja Vardjan
 */
public class Verify {
	
	/**
	 * The action to use when building {@link Intent} to invoke the signature service.
	 */
	public static final String ACTION = "org.societies.security.digsig.action.SignServiceRemote";
	
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

	/**
	 * Intent extras
	 */
	public class Params {
		
		/**
		 * The URI to upload initial version of document. Any further uploads
		 * of same document are to be done with {@link #DOWNLOAD_URI}.
		 * 
		 * Type: {@link String}
		 */
		public static final String UPLOAD_URI = "UPLOAD_URI";

		/**
		 * The URI to download and upload new versions of document that has been
		 * initially uploaded with {@link #UPLOAD_URI}.
		 * 
		 * Type: {@link String}
		 */
		public static final String DOWNLOAD_URI = "DOWNLOAD_URI";
	}		
}
