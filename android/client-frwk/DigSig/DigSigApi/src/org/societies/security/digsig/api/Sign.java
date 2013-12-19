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
		 * Optional human readable name or title of the document that is to be signed.
		 * Type: {@link String}
		 */
		public static final String DOC_TITLE = "DOC_TITLE";
	
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
		 * If the document is to be signed by a community, it can be automatically uploaded to the
		 * REST server that merges signatures from various members of the community.
		 * <br><br>
		 * If this parameter is specified, its value should be the document URI on the REST server.
		 * Usually this is the same as value of {@link #DOC_TO_SIGN_URL}.
		 * The success of upload operation {@link #UPLOAD_SUCCESS} is independent of {@link #SUCCESS}
		 * which represents only the success of signing operation.
		 * <br><br>
		 * If this parameter is not specified, the signed document will be available only on local
		 * Android device via the returned content provider URI and has to be manually retrieved
		 * and uploaded by your app.
		 * <br><br>
		 * Type: {@link String}
		 */
		public static final String COMMUNITY_SIGNATURE_SERVER_URI = "COMMUNITY_SIGNATURE_SERVER_URI";
		
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
		 * True if the document has been successfully uploaded to the remote REST server.
		 * The signing operation may finish successfully and {@link #SUCCESS} is true, but
		 * the upload may still fail. In that case, your app can still get the signed document
		 * from local content provider and upload it manually.
		 * Type: boolean
		 */
		public static final String UPLOAD_SUCCESS = "UPLOAD_SUCCESS";
	
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