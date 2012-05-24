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

/**
 * Privacy Assessment result for a particular sender.
 * Based on all data packets that were sent by this sender.
 *
 * @author Mitja Vardjan
 *
 */
public abstract class AssessmentResult {

	protected Object sender;
	private long numAllPackets = 0;
	private double numPacketsPerMonth = 0;
	private double corrWithDataAccessBySender = 0;
	private double corrWithDataAccessByAll = 0;
	private double corrWithDataAccessBySenderDev = 0;
	private double corrWithDataAccessByAllDev = 0;
	
	public AssessmentResult(Object sender) {
		this.sender = sender;
	}
	
	/**
	 * @return Number of all packets transmitted by this sender
	 */
	public long getNumAllPackets() {
		return numAllPackets;
	}

	/**
	 * @param numAllPackets Number of all packets transmitted by this sender
	 */
	public void setNumAllPackets(long numAllPackets) {
		this.numAllPackets = numAllPackets;
	}

	/**
	 * @return Number of packets transmitted per month by this sender
	 */
	public double getNumPacketsPerMonth() {
		return numPacketsPerMonth;
	}

	/**
	 * @param numPacketsPerMonth Number of packets transmitted per month by this sender
	 */
	public void setNumPacketsPerMonth(double numPacketsPerMonth) {
		this.numPacketsPerMonth = numPacketsPerMonth;
	}

	/**
	 * @return Correlation of:<br/>
	 * - all data transmissions by this sender<br/>
	 * - data access by this sender
	 */
	public double getCorrWithDataAccessBySender() {
		return corrWithDataAccessBySender;
	}

	/**
	 * @param corrWithDataAccessBySender Correlation of:<br/>
	 * - all data transmissions by this sender<br/>
	 * - data access by this sender
	 */
	public void setCorrWithDataAccessBySender(double corrWithDataAccessBySender) {
		this.corrWithDataAccessBySender = corrWithDataAccessBySender;
	}

	/**
	 * @return Correlation of:<br/>
	 * - all data transmissions by this sender<br/>
	 * - data access by anyone
	 */
	public double getCorrWithDataAccessByAll() {
		return corrWithDataAccessByAll;
	}

	/**
	 * @param corrWithDataAccessByAll Correlation of:<br/>
	 * - all data transmissions by this sender<br/>
	 * - data access by anyone
	 */
	public void setCorrWithDataAccessByAll(double corrWithDataAccessByAll) {
		this.corrWithDataAccessByAll = corrWithDataAccessByAll;
	}
	
	/**
	 * @return Median absolute deviation of correlation of:<br/>
	 * - all data transmissions by this sender<br/>
	 * - data access by this sender
	 */
	public double getCorrWithDataAccessBySenderDev() {
		return corrWithDataAccessBySenderDev;
	}

	/**
	 * @param corrWithDataAccessBySenderDev Median absolute deviation of correlation of:<br/>
	 * - all data transmissions by this sender<br/>
	 * - data access by this sender
	 */
	public void setCorrWithDataAccessBySenderDev(
			double corrWithDataAccessBySenderDev) {
		this.corrWithDataAccessBySenderDev = corrWithDataAccessBySenderDev;
	}

	/**
	 * @return Median absolute deviation of correlation of:<br/>
	 * - all data transmissions by this sender<br/>
	 * - data access by anyone
	 */
	public double getCorrWithDataAccessByAllDev() {
		return corrWithDataAccessByAllDev;
	}

	/**
	 * @param corrWithDataAccessByAllDev Median absolute deviation of correlation of:<br/>
	 * - all data transmissions by this sender<br/>
	 * - data access by anyone
	 */
	public void setCorrWithDataAccessByAllDev(double corrWithDataAccessByAllDev) {
		this.corrWithDataAccessByAllDev = corrWithDataAccessByAllDev;
	}

	/**
	 * @return the sender
	 */
	public Object getSender() {
		return sender;
	}
}
