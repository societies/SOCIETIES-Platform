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
package org.societies.privacytrust.privacyprotection.assessment.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResult;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultBundle;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultClassName;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultIIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataTransmissionLogEntry;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.PrivacyLogFilter;
import org.societies.privacytrust.privacyprotection.assessment.log.PrivacyLog;

/**
 * Parses the log and creates report about data that has been transmitted between CSSs and CISs.
 * The report should be suitable to be displayed in web browser.
 *
 * @author Mitja Vardjan
 *
 */
public class DataTransferAnalyzer {

	private static Logger LOG = LoggerFactory.getLogger(DataTransferAnalyzer.class);

	private PrivacyLog privacyLog;
	private Correlation correlation;
	
	public DataTransferAnalyzer(PrivacyLog privacyLog) {
		LOG.info("Constructor");
		this.privacyLog = privacyLog;
		correlation = new Correlation(privacyLog);
	}
	
	public List<DataTransmissionLogEntry> getDataTransmission(IIdentity receiver, Date start, Date end) {
		
		List<DataTransmissionLogEntry> matchedEntries = new ArrayList<DataTransmissionLogEntry>();
		String jid;
		IIdentity identity;
		
		if (receiver == null || receiver.getJid() == null) {
			LOG.warn("getDataTransmission({}): receiver or receiver JID is null", receiver);
			return matchedEntries;
		}
		
		jid = receiver.getJid();
		for (DataTransmissionLogEntry d : privacyLog.getDataTransmission()) {
			identity = d.getReceiver();
			if (identity == null) {
				continue;
			}
			if (jid.equals(identity.getJid()) &&
					d.getTime().after(start) && d.getTime().before(end)) {
				matchedEntries.add(d);
			}
		}
		return matchedEntries;
	}
	
	
	public Map<IIdentity, Integer> getNumDataTransmissionEventsForAllReceivers(Date start, Date end) {
		
		Map<IIdentity, Integer> result = new HashMap<IIdentity, Integer>();
		List<IIdentity> receivers = getDataTransmissionReceivers();
		
		for (IIdentity receiver : receivers) {
			result.put(receiver, getNumDataTransmissionEvents(receiver, start, end));
		}
		
		return result;
	}
	
	/**
	 * Get number of events in certain time period where data has been sent to given receiver.
	 * 
	 * @param receiver Identity of the receiver (the one data has been sent to)
	 * @param start Match only events after this time
	 * @param end Match only events before this time
	 * @return All events where receiver matches
	 */
	public int getNumDataTransmissionEvents(IIdentity receiver, Date start, Date end) {
		
		List<DataTransmissionLogEntry> matchedEntries = getDataTransmission(receiver, start, end);
		
		return matchedEntries.size();
	}

	public List<IIdentity> getDataTransmissionReceivers() {
		
		List<IIdentity> matches = new ArrayList<IIdentity>();
		IIdentity receiver;
		
		for (DataTransmissionLogEntry d : privacyLog.getDataTransmission()) {
			receiver = d.getReceiver();
			if (!matches.contains(receiver) && receiver != null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("getDataTransmissionReceivers(): Adding identity {}", receiver);
				}
				matches.add(receiver);
			}
		}
		return matches;
	}

	public AssessmentResultIIdentity estimatePrivacyBreach(IIdentity sender, Date start, Date end) throws AssessmentException {
		
		if (sender == null || sender.getJid() == null) {
			LOG.warn("estimatePrivacyBreach({}): sender or sender JID is null", sender);
			throw new AssessmentException("sender or sender JID is null");
		}
		
		correlation.run();

		AssessmentResultIIdentity result = new AssessmentResultIIdentity(sender);
		PrivacyLogFilter filter = new PrivacyLogFilter();
		filter.setSender(sender);
		filter.setStart(start);
		filter.setEnd(end);
		
		double corrByAll = 0;
		double corrBySender = 0;
		List<DataTransmissionLogEntry> matchedTransmissions = privacyLog.search(filter);
		
		for (DataTransmissionLogEntry tr : matchedTransmissions) {
			corrByAll += tr.getCorrelationWithDataAccess();
			corrBySender += tr.getCorrelationWithDataAccessBySender();
		}
		fillResult(result, corrByAll, corrBySender, matchedTransmissions);
		return result;
	}
	
	public AssessmentResultClassName estimatePrivacyBreach(String sender, Date start, Date end) throws AssessmentException {
		
		if (sender == null) {
			LOG.warn("estimatePrivacyBreach({}): sender is null", sender);
			throw new AssessmentException("sender is null");
		}
		
		correlation.run();

		AssessmentResultClassName result = new AssessmentResultClassName(sender);
		PrivacyLogFilter filter = new PrivacyLogFilter();
		filter.setSenderClass(sender);
		filter.setStart(start);
		filter.setEnd(end);
		
		double corrByAll = 0;
		double corrBySender = 0;
		List<DataTransmissionLogEntry> matchedTransmissions = privacyLog.search(filter);
		
		for (DataTransmissionLogEntry tr : matchedTransmissions) {
			corrByAll += tr.getCorrelationWithDataAccess();
			corrBySender += tr.getCorrelationWithDataAccessBySenderClass();
		}
		fillResult(result, corrByAll, corrBySender, matchedTransmissions);
		return result;
	}
	
	public AssessmentResultBundle estimatePrivacyBreachForBundle(String sender, Date start, Date end)
			throws AssessmentException {
		
		if (sender == null) {
			LOG.warn("estimatePrivacyBreachForBundle({}): sender is null", sender);
			throw new AssessmentException("sender is null");
		}
		
		correlation.run();

		AssessmentResultBundle result = new AssessmentResultBundle(sender);
		PrivacyLogFilter filter = new PrivacyLogFilter();
		filter.setSenderBundle(sender);
		filter.setStart(start);
		filter.setEnd(end);
		
		double corrByAll = 0;
		double corrBySender = 0;
		List<DataTransmissionLogEntry> matchedTransmissions = privacyLog.search(filter);
		
		for (DataTransmissionLogEntry tr : matchedTransmissions) {
			corrByAll += tr.getCorrelationWithDataAccess();
			corrBySender += tr.getCorrelationWithDataAccessBySenderBundle();
		}
		fillResult(result, corrByAll, corrBySender, matchedTransmissions);
		return result;
	}
	
	private void fillResult(AssessmentResult result, double corrByAll, double corrBySender,
			List<DataTransmissionLogEntry> matchedTransmissions) {
		
		result.setCorrWithDataAccessByAll(corrByAll);
		result.setCorrWithDataAccessBySender(corrBySender);
		result.setNumAllPackets(matchedTransmissions.size());
		if (matchedTransmissions.size() < 1) {
			result.setNumPacketsPerMonth(0);
		}
		else {
			long timePeriod;
			// Calculate time period in ms
			timePeriod = new Date().getTime() - matchedTransmissions.get(0).getTimeInMs();
			// Convert time period to months
			timePeriod /= 1e3 * 3600 * 24 * 30.5;
			result.setNumPacketsPerMonth((double) matchedTransmissions.size() / timePeriod);
		}
	}
}
