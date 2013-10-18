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

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataAccessLogEntry;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataTransmissionLogEntry;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.PrivacyLogFilter;
import org.societies.privacytrust.privacyprotection.assessment.log.PrivacyLog;

/**
 * Worker class.
 * Correlations between a single data access event and a single data transmission event,
 * calculated for every data transmission event.
 *
 * @author Mitja Vardjan
 *
 */
public class Correlation {

	private static Logger LOG = LoggerFactory.getLogger(Correlation.class);

	private List<DataAccessLogEntry> dataAccess;
	private PrivacyLog privacyLog;
	
	private CorrelationInData correlationInData;
	private CorrelationInTime correlationInTime;
	
	private Date lastRun = null;
	
	public Correlation(PrivacyLog privacyLog) {
		this.privacyLog = privacyLog;
		this.dataAccess = privacyLog.getDataAccess();
		correlationInData = new CorrelationInData();
		correlationInTime = new CorrelationInTime();
	}
	
	/**
	 * Perform all calculations. May take a long time.
	 */
	public void run() {
		
		LOG.info("run()");
		
		long size1;
		long size2;
		long time1;
		long time2;
		double corr;
		double corrByAll;
		double corrBySender;
		double corrBySenderClass;
		double corrBySenderBundle;
		List<DataTransmissionLogEntry> newDataTransmissions = getNewDataTransmissions();
		
		for (DataTransmissionLogEntry tr : newDataTransmissions) {
			
			size2 = tr.getPayloadSize();
			time2 = tr.getTimeInMs();
			corrByAll = 0;
			corrBySender = 0;
			corrBySenderClass = 0;
			corrBySenderBundle = 0;
			
			for (DataAccessLogEntry ac : dataAccess) {
				
				size1 = ac.getPayloadSize();
				time1 = ac.getTimeInMs();
				
				corr = correlation(size2 - size1, time2 - time1);
				corrByAll += corr;
				
				if (ac.getRequestor() == null || tr.getSender() == null) {
					//LOG.warn("Requestor or sender is null");
				}
				else if (ac.getRequestor().getJid().equals(tr.getSender().getJid())) {
					corrBySender += corr;
				}
				
				if (ac.getRequestorClass() == null || tr.getSenderClass() == null) {
					//LOG.warn("Requestor or sender class is null");
				}
//				else if (tr.getSenderStack().contains(ac.getRequestorClass())) {
				else if (isAnyMemberEqual(tr.getSenderStack(), ac.getRequestorStack())) {
					corrBySenderClass += corr;
				}

				if (ac.getRequestorBundles() == null || tr.getSenderBundles() == null) {
					//LOG.warn("Requestor or sender bundles is null");
				}
				else if (isAnyMemberEqual(tr.getSenderBundles(), ac.getRequestorBundles())) {
					// TODO: this IF condition can result in overestimated correlation for events with multiple bundles
					corrBySenderBundle += corr;
				}
			}
			tr.setCorrelationWithDataAccess(corrByAll);
			tr.setCorrelationWithDataAccessBySender(corrBySender);
			tr.setCorrelationWithDataAccessBySenderClass(corrBySenderClass);
			tr.setCorrelationWithDataAccessBySenderBundle(corrBySenderBundle);
		}
		lastRun = new Date();
	}
	
	public boolean isAnyMemberEqual(List<String> list1, List<String> list2) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("isAnyMemberEqual({}, {})", list1, list2);
		}
		for (String needle : list1) {
			if (list2.contains(needle)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("isAnyMemberEqual(): true");
				}
				return true;
			}
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("isAnyMemberEqual(): false");
		}
		return false;
	}
	
	/**
	 * If {@link #run()} has not been invoked yet, it is invoked to populate the
	 * {@link DataTransmissionLogEntry} instances with results. If it has been
	 * invoked before, it is not invoked again and the results may be old in
	 * that case.
	 * Then all {@link DataTransmissionLogEntry} instances are returned.
	 * 
	 * @return All data transmissions
	 */
//	public List<DataTransmissionLogEntry> getDataTransmission() {
//		
//		if (lastRun == null) {
//			run();
//		}
//		return dataTransmission;
//	}
	
	/**
	 * Correlation between a single data transmission and a single data access.
	 *
	 * @param deltaSize difference in data size
	 * @param dt difference in time
	 * 
	 * @return correlation value, combined from correlation based on time and
	 * correlation based on data
	 */
	private double correlation(long deltaSize, long dt) {
		
		double cData;
		double cTime;
		
		cData = correlationInData.correlation(deltaSize);
		cTime = correlationInTime.correlation(dt);
		
		return cData * cTime;
	}
	
	private List<DataTransmissionLogEntry> getNewDataTransmissions() {
		if (lastRun == null) {
			return privacyLog.getDataTransmission();
		}
		else {
			PrivacyLogFilter filter = new PrivacyLogFilter();
			filter.setStart(lastRun);
			return privacyLog.search(filter);
		}
	}
}
