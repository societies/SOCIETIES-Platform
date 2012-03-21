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

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class PrivacyLogFilter {
	
	private String[] dataType;
	
	private Date start;
	private Date end;
	
	private Boolean sentToGroup;
	
	private String receiverId;
	
	private String senderId;
	
	private ChannelType[] channelId;
	
	/**
	 * Constructor. Sets all filter options to null, meaning that all log entries will match.
	 */
	public PrivacyLogFilter() {
		this.dataType = null;
		this.start = null;
		this.end = null;
		this.sentToGroup = null;
		this.receiverId = null;
		this.senderId = null;
		this.channelId = null;
	}
	
	/**
	 * Constructor. Creates a new filter. A null parameter will match any value.
	 * 
	 * @param dataType Filter by these data types.
	 * @param start Select entries on and after this time
	 * @param end Select entries before and on this time
	 * @param sentToGroup True to select only multicast entries, False for singlecast entries 
	 * @param receiverId  CSS ID of the receiver
	 * @param senderId    CSS ID of the sender
	 * @param channelId   IDs of the possible channels.
	 */
	public PrivacyLogFilter(
			String[] dataType,
			Date start,
			Date end,
			Boolean sentToGroup,
			String receiverId,
			String senderId,
			ChannelType[] channelId
			) {
		this.dataType = dataType;
		this.start = start;
		this.end = end;
		this.sentToGroup = sentToGroup;
		this.receiverId = receiverId;
		this.senderId = senderId;
		this.channelId = channelId;
	}

	public void setDataType(String[] dataType) {
		this.dataType = dataType;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public void setend(Date end) {
		this.end = end;
	}

	public void setsentToGroup(Boolean sentToGroup) {
		this.sentToGroup = sentToGroup;
	}

	public void setreceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public void setsenderId(String senderId) {
		this.senderId = senderId;
	}

	public void setchannelId(ChannelType[] channelId) {
		this.channelId = channelId;
	}
}
