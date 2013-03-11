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

package org.societies.api.comm.xmpp.exceptions;

import org.societies.api.comm.xmpp.datatypes.StanzaError;

/**
 * The Class XMPPError.
 *
 * @author Joao M. Goncalves (PTIN)
 * 
 * TODO
 */

// TODO parse error (client side)
public class XMPPError extends Exception {
	
	/** The Constant STANZA_ERROR_NAMESPACE_DECL. */
	public static final String STANZA_ERROR_NAMESPACE_DECL = "urn:ietf:params:xml:ns:xmpp-stanzas";
	
	/** The Constant CLOSE_ERROR. */
	public static final String CLOSE_ERROR = "</error>";
	
	/** The Constant CLOSE_ERROR_BYTES. */
	public static final byte[] CLOSE_ERROR_BYTES = CLOSE_ERROR.getBytes();
	
	/** The stanza error. */
	private StanzaError stanzaError;
	
	/** The application error. */
	private Object applicationError;
	
	/** The stanza error text. */
	private String stanzaErrorText;
	
	/** The stanza error bytes with text. */
	private byte[] stanzaErrorBytesWithText;
	
	/** The generic text. */
	private String genericText; // TODO support output of this
	
	/**
	 * Instantiates a new XMPP error.
	 *
	 * @param stanzaError the stanza error {@link org.societies.api.comm.xmpp.datatypes.StanzaError}
	 */
	public XMPPError(StanzaError stanzaError) {
		this.stanzaError = stanzaError;
	}
	
	/**
	 * Instantiates a new XMPP error.
	 *
	 * @param stanzaError the stanza error {@link org.societies.api.comm.xmpp.datatypes.StanzaError}
	 * @param genericText the generic text
	 */
	public XMPPError(StanzaError stanzaError, String genericText) {
		super(stanzaError+": "+genericText);
		this.stanzaError = stanzaError;
		this.genericText = genericText;
	}
	
	/**
	 * Instantiates a new xMPP error.
	 *
	 * @param stanzaError the stanza error {@link org.societies.api.comm.xmpp.datatypes.StanzaError}
	 * @param stanzaErrorText the stanza error text
	 * @param applicationError the application error
	 */
	public XMPPError(StanzaError stanzaError, String stanzaErrorText, Object applicationError) {
		super(stanzaError+": "+stanzaErrorText);
		this.applicationError = applicationError;
		this.stanzaError = stanzaError;
		if (stanzaError.hasText() && stanzaErrorText!=null) {
			this.stanzaErrorText = stanzaErrorText+"\n</"+stanzaError.toString()+">\n";
			byte[] b1 = this.stanzaErrorText.getBytes();
			stanzaErrorBytesWithText = new byte[stanzaError.getBytes().length+b1.length];
			System.arraycopy(stanzaError.getBytes(), 0, stanzaErrorBytesWithText, 0, stanzaError.getBytes().length);
			System.arraycopy(b1, 0, stanzaErrorBytesWithText, stanzaError.getBytes().length, b1.length);
		}
	}
	
	/**
	 * Gets the generic text.
	 *
	 * @return the generic text
	 */
	public String getGenericText() {
		return genericText;
	}
	
	/**
	 * Gets the application error.
	 *
	 * @return the application error
	 */
	public Object getApplicationError() {
		return applicationError;
	}
	
	/**
	 * Gets the stanza error string.
	 *
	 * @return the stanza error string
	 */
	public String getStanzaErrorString() {
		if (stanzaError.hasText() && stanzaErrorText!=null)
			return stanzaError.toString()+stanzaErrorText;
		else
			return stanzaError.toString();
	}
	
	/**
	 * Gets the stanza error bytes.
	 *
	 * @return the stanza error bytes
	 */
	public byte[] getStanzaErrorBytes(){
		if (stanzaError.hasText() && stanzaErrorBytesWithText!=null)
			return stanzaErrorBytesWithText;
		else
			return stanzaError.getBytes();
	}
}
