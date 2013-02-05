/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM ISRAEL
 * SCIENCE AND TECHNOLOGY LTD (IBM), INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA
 * PERIORISMENIS EFTHINIS (AMITEC), TELECOM ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD
 * (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.api.comms.xmpp;

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
			errorString = "<error type='"+type.toString()+"'><"+error+" xmlns='"+XMPPError.STANZA_ERROR_NAMESPACE_DECL+"'>"; // TODO removed line breaks for debug
		else
			errorString = "<error type='"+type.toString()+"'><"+error+" xmlns='"+XMPPError.STANZA_ERROR_NAMESPACE_DECL+"'/>";
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