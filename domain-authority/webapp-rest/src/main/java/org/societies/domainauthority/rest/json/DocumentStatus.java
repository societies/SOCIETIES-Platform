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
package org.societies.domainauthority.rest.json;

import java.util.List;

import org.societies.domainauthority.rest.model.Document;

import com.google.gson.Gson;

/**
 * Representation of current status of {@link Document}
 *
 * @author Mitja Vardjan
 *
 */
public class DocumentStatus {

	/**
	 * List of parties that have signed the document so far.
	 * A party is listed as CN (common name) field value from the certificate.
	 */
	private List<String> signers;
	
	private int numSigners;

	private int minNumSigners;
	
	/**
	 * Default constructor
	 */
	public DocumentStatus() {
	}

	/**
	 * Constructor
	 */
	public DocumentStatus(List<String> signers, int numSigners, int minNumSigners) {
		this.signers = signers;
		this.numSigners = numSigners;
		this.minNumSigners = minNumSigners;
	}

	/**
	 * @return the signers
	 */
	public List<String> getSigners() {
		return signers;
	}

	/**
	 * @param signers the signers to set
	 */
	public void setSigners(List<String> signers) {
		this.signers = signers;
	}

	/**
	 * @return the minNumSigners
	 */
	public int getMinNumSigners() {
		return minNumSigners;
	}

	/**
	 * @param minNumSigners the minNumSigners to set
	 */
	public void setMinNumSigners(int minNumSigners) {
		this.minNumSigners = minNumSigners;
	}
	
	/**
	 * @return the numSigners
	 */
	public int getNumSigners() {
		return numSigners;
	}

	/**
	 * @param numSigners the numSigners to set
	 */
	public void setNumSigners(int numSigners) {
		this.numSigners = numSigners;
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();

		sb.append("Threshold: " + minNumSigners + System.getProperty("line.separator"));
		sb.append("Number of signers: " + numSigners + System.getProperty("line.separator"));
		sb.append("Signers: ");
		for (String signer : signers) {
			sb.append(System.getProperty("line.separator") + "    " + signer);
		}

		return sb.toString();
	}
	
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
