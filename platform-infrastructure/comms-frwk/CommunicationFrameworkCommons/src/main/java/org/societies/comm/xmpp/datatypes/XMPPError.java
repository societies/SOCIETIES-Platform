package org.societies.comm.xmpp.datatypes;

// TODO force UTF-8
public class XMPPError {
	
	public static final String STANZA_ERROR_NAMESPACE_DECL = "urn:ietf:params:xml:ns:xmpp-stanzas";
	public static final String CLOSE_ERROR = "</error>";
	public static final byte[] CLOSE_ERROR_BYTES = CLOSE_ERROR.getBytes();
	
	private StanzaError stanzaError;
	private Object applicationError;
	private String stanzaErrorText;
	private byte[] stanzaErrorBytesWithText;
	
	public XMPPError(StanzaError stanzaError) {
		this.stanzaError = stanzaError;
	}
	
	public XMPPError(StanzaError stanzaError, String stanzaErrorText, Object applicationError) {
		this.applicationError = applicationError;
		this.stanzaError = stanzaError;
		if (stanzaError.hasText && stanzaErrorText!=null) {
			this.stanzaErrorText = stanzaErrorText+"\n</"+stanzaError.errorName+">\n";
			byte[] b1 = this.stanzaErrorText.getBytes();
			stanzaErrorBytesWithText = new byte[stanzaError.errorBytes.length+b1.length];
			System.arraycopy(stanzaError.errorBytes, 0, stanzaErrorBytesWithText, 0, stanzaError.errorBytes.length);
			System.arraycopy(b1, 0, stanzaErrorBytesWithText, stanzaError.errorBytes.length, b1.length);
		}
	}
	
	public Object getApplicationError() {
		return applicationError;
	}
	
	public String getStanzaErrorString() {
		if (stanzaError.hasText && stanzaErrorText!=null)
			return stanzaError.errorString+stanzaErrorText;
		else
			return stanzaError.errorString;
	}
	
	public byte[] getStanzaErrorBytes(){
		if (stanzaError.hasText && stanzaErrorBytesWithText!=null)
			return stanzaErrorBytesWithText;
		else
			return stanzaError.errorBytes;
	}
	
	public enum StanzaError {
		bad_request("bad-request",ErrorType.MODIFY,false),
		conflict("conflict",ErrorType.CANCEL,false),
		feature_not_implemented("feature-not-implemented",ErrorType.CANCEL,false), // TODO modify
		forbidden("forbidden",ErrorType.AUTH,false),
		gone("gone",ErrorType.CANCEL,true),
		internal_server_error("internal-server-error",ErrorType.CANCEL,false),
		item_not_found("item-not-found",ErrorType.CANCEL,false),
		jid_malformed("jid-malformed",ErrorType.MODIFY,false),
		not_acceptable("not-acceptable",ErrorType.MODIFY,false),
		not_allowed("not-allowed",ErrorType.CANCEL,false),
		not_authorized("not-authorized",ErrorType.AUTH,false),
		policy_violation("policy-violation",ErrorType.MODIFY,false), // TODO wait
		recipient_unavailable("recipient-unavailable",ErrorType.WAIT,false),
		redirect("redirect",ErrorType.MODIFY,true),
		registration_required("registration-required",ErrorType.AUTH,false),
		remote_server_not_found("remote-server-not-found",ErrorType.CANCEL,false),
		remote_server_timeout("remote-server-timeout",ErrorType.WAIT,false),
		resource_constraint("resource-constraint",ErrorType.WAIT,false),
		service_unavailable("service-unavailable",ErrorType.CANCEL,false),
		subscription_required("subscription-required",ErrorType.AUTH,false),
		undefined_condition("undefined-condition",ErrorType.MODIFY,false),
		unexpected_request("unexpected-request",ErrorType.WAIT,false); // TODO modify
		
		private final String errorName;
		private final String errorString;
		private final byte[] errorBytes;
		private final boolean hasText;
		
		private StanzaError(String error, ErrorType type, boolean hasText) {
			this.errorName = error;
			if (hasText)
				errorString = "<error type='"+type.typeString+"'>\n<"+error+" xmlns='"+STANZA_ERROR_NAMESPACE_DECL+"'>\n";
			else
				errorString = "<error type='"+type.typeString+"'>\n<"+error+" xmlns='"+STANZA_ERROR_NAMESPACE_DECL+"'/>\n";
			errorBytes = error.getBytes();
			this.hasText = hasText;
		}
	}
	
	public enum ErrorType {
		AUTH("auth"),			// -- retry after providing credentials
		CANCEL("cancel"),		// -- do not retry (the error cannot be remedied)
		CONTINUE("continue"),	// -- proceed (the condition was only a warning)
		MODIFY("modify"),		// -- retry after changing the data sent
		WAIT("wait");			// -- retry after waiting (the error is temporary)
		
		private String typeString;
		
		private ErrorType(String typeString) {
			this.typeString = typeString;
		}
	}
}
