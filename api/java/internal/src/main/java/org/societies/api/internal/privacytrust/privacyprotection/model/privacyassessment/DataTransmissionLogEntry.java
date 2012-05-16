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

import org.societies.api.identity.IIdentity;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class DataTransmissionLogEntry {

	private final String dataType;
	private final Date time;
	private final long timeInMs;
	private final boolean sentToGroup;
	private final boolean sentToLocalCss;
	private final IIdentity receiver;	
	private final IIdentity sender;
	private final long payloadSize;
	private final ChannelType channelId;
	
	private double correlationWithDataAccess = 0;

	public DataTransmissionLogEntry(String dataType, Date time, IIdentity receiver,
			IIdentity sender, long payloadSize, ChannelType channelId) {
		
		this.dataType = dataType;
		this.time = time;
		this.timeInMs = time.getTime();
		
		switch (receiver.getType()) {
		case CIS:
			// Some CIS
			this.sentToGroup = true;
			this.sentToLocalCss = false;
			break;
		case CSS:
			// Some other CSS
			this.sentToGroup = false;
			this.sentToLocalCss = false;
			break;
		case CSS_LIGHT:
			// User's own CSS
			this.sentToGroup = false;
			this.sentToLocalCss = true;
			break;
		case CSS_RICH:
			// User's own CSS
			this.sentToGroup = false;
			this.sentToLocalCss = true;
			break;
		default:
			//LOG.warn("isSentToLocalCss(): unrecognized receiver type: {}", receiver.getType());
			
			// Assume most problematic cases
			this.sentToGroup = true;
			this.sentToLocalCss = false;
		}

		this.receiver = receiver;
		this.sender = sender;
		this.payloadSize = payloadSize;
		this.channelId = channelId;
	}
	
	public String getDataType() {
		return dataType;
	}
	
	public Date getTime() {
		return time;
	}
	
	public long getTimeInMs() {
		return timeInMs;
	}
	
	public boolean isSentToGroup() {
		return sentToGroup;
	}
	
	public IIdentity getReceiver() {
		return receiver;
	}
	
	public IIdentity getSender() {
		return sender;
	}
	
	public long getPayloadSize() {
		return payloadSize;
	}
	
	public ChannelType getChannelId() {
		return channelId;
	}
	
	/**
	 * Checks if the message has been sent only to local CSS.
	 * 
	 * @return True if receiver's IIdentity.getType() returns either CSS_LIGHT or CSS_RICH.
	 * False if CIS or CSS.
	 */
	public boolean isSentToLocalCss() {
		return sentToLocalCss;
	}
	
	/**
	 * Get correlation with all data access events
	 * 
	 * @return Correlation with all data access events
	 */
	public double getCorrelationWithDataAccess() {
		return correlationWithDataAccess;
	}

	/**
	 * Set correlation with all data access events
	 * 
	 * @param correlationWithDataAccess Correlation with all data access events
	 */
	public void setCorrelationWithDataAccess(double correlationWithDataAccess) {
		this.correlationWithDataAccess = correlationWithDataAccess;
	}
}
