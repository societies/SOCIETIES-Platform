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
public class PrivacyLogFilter {
	
	private String[] dataType;
	private Date start;
	private Date end;
	private Boolean sentToGroup;
	private Boolean sentToLocalCss;
	private IIdentity receiver;
	private IIdentity sender;
	private String senderClass;
	private String senderBundle;
	private ChannelType[] channelId;
	
	/**
	 * Constructor. Sets all filter options to null, meaning that all log entries will match.
	 */
	public PrivacyLogFilter() {
		this.dataType = null;
		this.start = null;
		this.end = null;
		this.sentToGroup = null;
		this.sentToLocalCss = null;
		this.receiver = null;
		this.sender = null;
		this.senderClass = null;
		this.senderBundle = null;
		this.channelId = null;
	}

	/**
	 * Constructor. Creates a new filter. A null parameter will match any value.
	 * 
	 * @param dataType Filter by these data types.
	 * @param start Select entries on and after this time
	 * @param end Select entries before and on this time
	 * @param sentToGroup True to select only multicast entries. False for singlecast entries
	 * @param sentToLocalCss True to select only data sent to nodes in local CSS.
	 * False to select only data sent outside CSS.
	 * @param receiver  CSS ID of the receiver
	 * @param sender    CSS ID of the sender
	 * @param senderClass Class name of the component that invoked sending.
	 * @param senderBundle Symbolic name of bundle that invoked sending.
	 * @param channelId   IDs of the possible channels.
	 */
	public PrivacyLogFilter(
			String[] dataType,
			Date start,
			Date end,
			Boolean sentToGroup,
			Boolean sentToLocalCss,
			IIdentity receiver,
			IIdentity sender,
			String senderClass,
			String senderBundle,
			ChannelType[] channelId
			) {
		this.dataType = dataType;
		this.start = start;
		this.end = end;
		this.sentToGroup = sentToGroup;
		this.sentToLocalCss = sentToLocalCss;
		this.receiver = receiver;
		this.sender = sender;
		this.senderClass = senderClass;
		this.senderBundle = senderBundle;
		this.channelId = channelId;
	}
	
	public String[] getDataType() {
		return dataType;
	}

	public void setDataType(String[] dataType) {
		this.dataType = dataType;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Boolean getSentToGroup() {
		return sentToGroup;
	}

	public void setSentToGroup(Boolean sentToGroup) {
		this.sentToGroup = sentToGroup;
	}

	public Boolean getSentToLocalCss() {
		return sentToLocalCss;
	}

	public void setSentToLocalCss(Boolean sentToLocalCss) {
		this.sentToLocalCss = sentToLocalCss;
	}

	public IIdentity getReceiver() {
		return receiver;
	}

	public void setReceiver(IIdentity receiver) {
		this.receiver = receiver;
	}

	public IIdentity getSender() {
		return sender;
	}

	public void setSender(IIdentity sender) {
		this.sender = sender;
	}

	public String getSenderClass() {
		return senderClass;
	}

	public void setSenderClass(String senderClass) {
		this.senderClass = senderClass;
	}

	/**
	 * @return the senderBundle
	 */
	public String getSenderBundle() {
		return senderBundle;
	}

	/**
	 * @param senderBundle the senderBundle to set
	 */
	public void setSenderBundle(String senderBundle) {
		this.senderBundle = senderBundle;
	}

	public ChannelType[] getChannelId() {
		return channelId;
	}

	public void setChannelId(ChannelType[] channelId) {
		this.channelId = channelId;
	}
}
