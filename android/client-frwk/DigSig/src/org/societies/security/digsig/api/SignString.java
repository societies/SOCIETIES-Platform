//package org.societies.security.digsig.api;
//
//import android.app.Activity;
//import android.content.Intent;
//
///**
// * External API.
// * {@link Intent} parameters to invoke signature {@link Activity}
// * 
// * @author Mitja Vardjan
// */
//public class SignString {
//	
//	/**
//	 * The action to use when building {@link Intent} to invoke the signature app.
//	 */
//	public static final String ACTION = "org.societies.security.digsig.action.SignString";
//
//	/**
//	 * Intent extras
//	 */
//	public class Params {
//		
//		/**
//		 * The {@link String} to sign.
//		 * The {@link String} should be small, e.g. less than 1 MiB.
//		 * Type: {@link String}
//		 */
//		public static final String STRING_TO_SIGN = "STRING_TO_SIGN";
//		
//		/**
//		 * The signature of given {@link String}.
//		 * Type: {@link String}
//		 */
//		public static final String SIGNATURE = "SIGNATURE";
//	
//		/**
//		 * Serialized public part of X.509 certificate whose private key was used to create the
//		 * signature available as extra with name {@link #SIGNATURE}.
//		 * Type: {@link String}
//		 */
//		public static final String CERTIFICATE = "CERTIFICATE";
//	
//		/**
//		 * The identity to use when signing.
//		 * Type: {@link String}
//		 */
//		public static final String IDENTITY = "IDENTITY";
//
//		/**
//		 * True if the operation completed successfully, false if an error occurred.
//		 * Type: boolean
//		 */
//		public static final String SUCCESS = "SUCCESS";
//	}
//}