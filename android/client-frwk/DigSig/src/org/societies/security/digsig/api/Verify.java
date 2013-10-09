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
		 * Endpoint for notifying the uploader about future events, e.g. when the resource is modified.
		 * Supported protocol is HTTP. On event, a HTTP GET is performed on the given endpoint (HTTP URL).
		 * 
		 * Type: {@link String}
		 */
		public static final String NOTIFICATION_ENDPOINT = "NOTIFICATION_ENDPOINT";

		/**
		 * Minimal number of signatures (threshold) for notifying the uploader about future sign events.
		 */
		public static final String NUM_SIGNERS_THRESHOLD = "NUM_SIGNERS_THRESHOLD";

		/**
		 * The URI to upload initial version of document by using HTTP PUT.
		 * This URI can be used only once (except if you delete the document, it can then be used
		 * again to upload a document).
		 * Any further uploads of the same document are to be done with {@link #DOWNLOAD_URI}.
		 * 
		 * Type: {@link String}
		 */
		public static final String UPLOAD_URI = "UPLOAD_URI";

		/**
		 * The URI to:</br>
		 * - download document by using HTTP GET</br>
		 * - upload new versions of document by using HTTP PUT</br>
		 * - delete document by using HTTP DELETE</br>
		 * In any case, the document had to be uploaded first with value of {@link #UPLOAD_URI}.
		 * 
		 * Type: {@link String}
		 */
		public static final String DOWNLOAD_URI = "DOWNLOAD_URI";

		/**
		 * True if the operation completed successfully, false if an error occurred.
		 * Type: boolean
		 */
		public static final String SUCCESS = "SUCCESS";
	}		
}
