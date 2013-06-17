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
package org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment;

import java.util.Date;
import java.util.List;

import org.societies.api.identity.IIdentity;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class DataAccessLogEntry {
	
	private final Date time;
	private final long timeInMs;
	private final IIdentity requestor;	
	private final String requestorClass;
	private final List<String> requestorStack;
	private final List<String> requestorBundles;
	private final IIdentity owner;
	private final long dataSize;
	
	public DataAccessLogEntry(Date time, IIdentity requestor, String requestorClass,
			List<String> requestorStack, List<String> requestorBundles, IIdentity owner) {
		
		this.time = time;
		this.timeInMs = time.getTime();
		this.requestor = requestor;
		this.requestorClass = requestorClass;
		this.requestorStack = requestorStack;
		this.requestorBundles = requestorBundles;
		this.owner = owner;
		this.dataSize = -1;
	}
	
	public DataAccessLogEntry(Date time, IIdentity requestor, String requestorClass,
			List<String> requestorStack, List<String> requestorBundles, IIdentity owner, long payloadSize) {
		
		this.time = time;
		this.timeInMs = time.getTime();
		this.requestor = requestor;
		this.requestorClass = requestorClass;
		this.requestorStack = requestorStack;
		this.requestorBundles = requestorBundles;
		this.owner = owner;
		this.dataSize = payloadSize;
	}
	
	/**
	 * @return List of all non-system classes in stack
	 */
	public List<String> getRequestorStack() {
		return requestorStack;
	}

	public Date getTime() {
		return time;
	}
	
	public long getTimeInMs() {
		return timeInMs;
	}
	
	public IIdentity getRequestor() {
		return requestor;
	}
	
	public String getRequestorClass() {
		return requestorClass;
	}
	
	/**
	 * @return the requestorBundles
	 */
	public List<String> getRequestorBundles() {
		return requestorBundles;
	}

	public IIdentity getOwner() {
		return owner;
	}
	
	public long getPayloadSize() {
		return dataSize;
	}
}
