package org.societies.security.digsig.api;

import android.app.Service;
import android.content.Intent;

/**
 * {@link Intent} parameters to invoke signature {@link Service}
 * 
 * @author Mitja Vardjan
 */
public class Sign {
	
	/**
	 * The action to use when building {@link Intent} to invoke the signature app.
	 */
	public static final String ACTION = "org.societies.security.digsig.action.Sign";

	/**
	 * Intent extras
	 */
	public class Params {
		
		/**
		 * The xml document to sign.
		 * Type: byte[]
		 */
		public static final String DOC_TO_SIGN = "XML";
	
		/**
		 * The signed xml document.
		 * Type: byte[]
		 */
		public static final String SIGNED_DOC = "SIGNED_DOC";
	
		/**
		 * Type of the signature in output document
		 * Type: String
		 */
		public static final String OUTPUT_TYPE = "OUTPUT_TYPE";
	
		/**
		 * XML nodes to sign, specified as values of "Id" attribute.
		 * Type: ArrayList of String
		 */
		public static final String IDS_TO_SIGN = "IDS_TO_SIGN";
	
		/**
		 * The identity to use when signing.
		 * Type: String
		 */
		public static final String IDENTITY = "SELECTED";
		
		/**
		 * All supported types of the signature in output document
		 */
		public class OutputType {
			public static final String SIG_TYPE_ENVELOPED = "SIG_TYPE_ENVELOPED";
		}
	}
}