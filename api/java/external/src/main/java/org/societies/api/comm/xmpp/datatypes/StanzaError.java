package org.societies.api.comm.xmpp.datatypes;

import org.societies.api.comm.xmpp.datatypes.ErrorType;
import org.societies.api.comm.xmpp.exceptions.XMPPError;

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
			errorString = "<error type='"+type.toString()+"'>\n<"+error+" xmlns='"+XMPPError.STANZA_ERROR_NAMESPACE_DECL+"'>\n";
		else
			errorString = "<error type='"+type.toString()+"'>\n<"+error+" xmlns='"+XMPPError.STANZA_ERROR_NAMESPACE_DECL+"'/>\n";
		errorBytes = errorString.getBytes();
		this.hasText = hasText;
	}

	@Override
	public String toString() {
		return errorName;
	}
	
	public byte[] getBytes() {
		return errorBytes;
	}
	
	public boolean hasText() {
		return hasText;
	}
}