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
package org.societies.privacytrust.privacyprotection.assessment.log;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.ChannelType;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataAccessLogEntry;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataTransmissionLogEntry;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLog;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.PrivacyLogFilter;

/**
 * Storage of raw events (data access, data transmission)
 *
 * @author Mitja Vardjan
 *
 */
public class PrivacyLog implements IPrivacyLog {

	private static Logger LOG = LoggerFactory.getLogger(PrivacyLog.class);

	private List<DataAccessLogEntry> dataAccess = new ArrayList<DataAccessLogEntry>();
	private List<DataTransmissionLogEntry> dataTransmission = new ArrayList<DataTransmissionLogEntry>();;

	private List<IIdentity> senderIds = new ArrayList<IIdentity>();
	private List<String> senderClassNames = new ArrayList<String>();
	private List<String> senderBundles = new ArrayList<String>();

	public PrivacyLog() {

		LOG.info("Constructor");
	}

	/**
	 * Gets all data access recorded so far.
	 * If you need to add any new element, then use
	 * {@link #append(DataAccessLogEntry)} instead.
	 * This method shall be used only to change or add information to existing elements.
	 * 
	 * @return all data access entries
	 */
	public List<DataAccessLogEntry> getDataAccess() {
		return dataAccess;
	}

	/**
	 * Gets all data transmissions recorded so far.
	 * If you need to add any new transmission, then use
	 * {@link #append(DataTransmissionLogEntry)} instead.
	 * This method shall be used only to change or add information to existing elements.
	 * 
	 * @return all data transmissions
	 */
	public List<DataTransmissionLogEntry> getDataTransmission() {
		return dataTransmission;
	}

	public void append(DataAccessLogEntry entry) {
		dataAccess.add(entry);
	}

	public void append(DataTransmissionLogEntry entry) {
		dataTransmission.add(entry);
		
		IIdentity sender = entry.getSender();
		if (!senderIds.contains(sender)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("append(): Adding new transmission identity {}", sender);
			}
			senderIds.add(sender);
		}
		
		String senderClass = entry.getSenderClass();
		if (!senderClassNames.contains(senderClass)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("append(): Adding new transmission class {}", senderClass);
			}
			senderClassNames.add(senderClass);
		}
		
		List<String> existingSenderBundles = entry.getSenderBundles();
		for (String senderBundle : existingSenderBundles) { 
			if (!this.senderBundles.contains(senderBundle)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("append(): Adding new transmission bundle {}", senderBundle);
				}
				this.senderBundles.add(senderBundle);
			}
		}
	}

	/**
	 * @return the senderIds
	 */
	public List<IIdentity> getSenderIds() {
		return senderIds;
	}

	/**
	 * @return the senderClassNames
	 */
	public List<String> getSenderClassNames() {
		return senderClassNames;
	}

	/**
	 * @return the senderBundles
	 */
	public List<String> getSenderBundles() {
		return senderBundles;
	}

	@Override
	public List<DataTransmissionLogEntry> search(PrivacyLogFilter f) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("search({})", f);
		}

		if (f == null) {
			return getAll();
		}
		List<DataTransmissionLogEntry> match = new ArrayList<DataTransmissionLogEntry>();

		for (DataTransmissionLogEntry l : dataTransmission) {
			if (isMatching(l, f)) {
				match.add(l);
			}
		}

		return match;
	}

	private boolean isMatching(DataTransmissionLogEntry l, PrivacyLogFilter f) {
		if (f.getSentToGroup() != null) {
			if (f.getSentToGroup() != l.isSentToGroup()) {
				return false;
			}
		}
		if (f.getSentToLocalCss() != null) {
			if (f.getSentToLocalCss() != l.isSentToLocalCss()) {
				return false;
			}
		}
		if (f.getChannelId() != null) {
			for (ChannelType channel : f.getChannelId()) {
				if (channel == l.getChannelId()) {
					break;
				}
				return false;
			}
		}
		if (f.getDataType() != null) {
			for (String dataType : f.getDataType()) {
				if (dataType == l.getDataType()) {
					break;
				}
				return false;
			}
		}
		if (f.getEnd() != null) {
			if (f.getEnd().before(l.getTime())) {
				return false;
			}
		}
		if (f.getReceiver() != null) {
			if (f.getReceiver() != l.getReceiver()) {
				return false;
			}
		}
		if (f.getSender() != null) {
			if (f.getSender() != l.getSender()) {
				return false;
			}
		}
		if (f.getSenderClass() != null) {
			if (f.getSenderClass() != l.getSenderClass()) {
				return false;
			}
		}
		if (f.getSenderBundle() != null) {
			if (!l.getSenderBundles().contains(f.getSenderBundle())) {
				return false;
			}
		}
		if (f.getStart() != null) {
			if (f.getStart().after(l.getTime())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public List<DataTransmissionLogEntry> getAll() {

		if (LOG.isDebugEnabled()) {
			LOG.debug("getAll()");
		}

		return dataTransmission;
	}
}
