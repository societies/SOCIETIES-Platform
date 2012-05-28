/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
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
package org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy;



/**
 * This class is used after the negotiation succeeds to seal the contract between the negotiating parties. 
 * The envelope contains the negotiation agreement in encrypted format, the user's signature and the public 
 * key with which the provider is able to decrypt the agreement. The AgreementEnvelope object is prepared by
 *  the user's negotiation agent and sent to the provider's negotiation agent.
 * @author Olivier Maridat (Trialog)
 *
 */
public class AgreementEnvelope {
	private Agreement agreement;
	private long agreementCheckSum;
	private byte[] signature;
	private byte[] publicKey;
	
	/**
	 * @return the agreement
	 */
	public Agreement getAgreement() {
		return agreement;
	}
	/**
	 * @param agreement the agreement to set
	 */
	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}
	/**
	 * @return the agreementCheckSum
	 */
	public long getAgreementCheckSum() {
		return agreementCheckSum;
	}
	/**
	 * @param agreementCheckSum the agreementCheckSum to set
	 */
	public void setAgreementCheckSum(long agreementCheckSum) {
		this.agreementCheckSum = agreementCheckSum;
	}
	/**
	 * @return the signature
	 */
	public byte[] getSignature() {
		return signature;
	}
	/**
	 * @param signature the signature to set
	 */
	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
	/**
	 * @return the publicKey
	 */
	public byte[] getPublicKey() {
		return publicKey;
	}
	/**
	 * @param publicKey the publicKey to set
	 */
	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}
	
	
}
