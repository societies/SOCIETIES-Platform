package org.societies.security.digsig.api;

import android.app.Activity;
import android.content.Intent;

/**
 * External API.
 * {@link Intent} parameters to invoke signature {@link Activity}
 * 
 * @author Mitja Vardjan
 */
public class Sign {
	
	/**
	 * The action to use when building {@link Intent} to invoke the signature app.
	 */
	public static final String ACTION = "org.societies.security.digsig.action.Sign";

	/**
	 * The action in the broadcast to notify about finished signing.
	 */
	public static final String ACTION_FINISHED = "org.societies.security.digsig.action.SignFinished";

	/**
	 * Intent extras
	 */
	public class Params {
		
		/**
		 * The xml document to sign.
		 * Set this extra only if the document is small, e.g. less than 1 MiB.
		 * If the document is large, then do not set this extra and use {@link Params#DOC_TO_SIGN_URL} instead.
		 * Type: byte[]
		 */
		public static final String DOC_TO_SIGN = "XML";
		
		/**
		 * URL of the xml document to sign.
		 * This approach can be used for any document size.
		 * If the document is small (e.g. less than 1 MiB), you can use the simpler approach with
		 * {@link Params#DOC_TO_SIGN} instead.
		 * Type: String
		 */
		public static final String DOC_TO_SIGN_URL = "XML_URL";
	
		/**
		 * URL of the signed xml document.
		 * Type: {@link String}
		 */
		public static final String SIGNED_DOC_URL = "SIGNED_XML";
	
		/**
		 * Type of the signature in output document
		 * Type: {@link String}
		 */
		public static final String OUTPUT_TYPE = "OUTPUT_TYPE";
	
		/**
		 * XML nodes to sign, specified as values of "Id" attribute.
		 * Type: {@link ArrayList} of {@link String}
		 */
		public static final String IDS_TO_SIGN = "IDS_TO_SIGN";
	
		/**
		 * The identity to use when signing.
		 * Type: {@link String}
		 */
		public static final String IDENTITY = "IDENTITY";

		/**
		 * True if the operation completed successfully, false if an error occurred.
		 * Type: boolean
		 */
		public static final String SUCCESS = "SUCCESS";
		
		/**
		 * Session number. Should be non-negative. A negative value indicates a serious error.
		 * Type: int
		 */
		public static final String SESSION_ID = "SESSION_ID";
		
		/**
		 * All supported types of the signature in output document
		 */
		public class OutputType {
			public static final String SIG_TYPE_ENVELOPED = "SIG_TYPE_ENVELOPED";
		}
	}
	
	/**
	 * Content provider for writing the document to sign and to retrieve the signed document.
	 */
	public class ContentUrl {
		
		/**
		 * Key for URL parameter "password"
		 */
		public static final String PARAM_PASSWORD = "pass";
	}
}