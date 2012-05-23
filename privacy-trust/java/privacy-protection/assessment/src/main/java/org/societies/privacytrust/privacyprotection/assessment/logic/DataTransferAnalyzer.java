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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentException;
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
//	private Correlation correlation;
	
//	private List<AssessmentResultClassName> assessmentResultClassName = new ArrayList<AssessmentResultClassName>();
//	private List<AssessmentResultIIdentity> assessmentResultIIdentity = new ArrayList<AssessmentResultIIdentity>();
	
	public DataTransferAnalyzer(PrivacyLog privacyLog) {
		LOG.info("Constructor");
		this.privacyLog = privacyLog;
//		this.correlation = new Correlation(privacyLog.getDataAccess(), privacyLog.getDataTransmission());
	}
	
	/**
	 * 
	 * @param sender The sender to assess
	 * 
	 * @return Values in array:<br/>
	 * - sum of correlations with all data access events<br/>
	 * - sum of correlations with those data access events where access was performed
	 * by the sender identity<br/>
	 * - sum of correlations with those data access events where access was performed
	 * by the sender class<br/>
	 * 
	 * @throws AssessmentException
	 */
	public double[] estimatePrivacyBreach(IIdentity sender) throws AssessmentException {
		
		if (sender == null || sender.getJid() == null) {
			LOG.warn("estimatePrivacyBreach({}): sender or sender JID is null", sender);
			throw new AssessmentException("sender or sender JID is null");
		}
		
		PrivacyLogFilter filter = new PrivacyLogFilter();
		filter.setSender(sender);
		
		return estimatePrivacyBreach(filter);
	}
	
	/**
	 * 
	 * @param sender The sender to assess
	 * 
	 * @return Values in array:<br/>
	 * - sum of correlations with all data access events<br/>
	 * - sum of correlations with those data access events where access was performed
	 * by the sender identity<br/>
	 * - sum of correlations with those data access events where access was performed
	 * by the sender class<br/>
	 * 
	 * @throws AssessmentException
	 */
	public double[] estimatePrivacyBreach(String sender) throws AssessmentException {

		if (sender == null) {
			LOG.warn("estimatePrivacyBreach({}): sender is null", sender);
			throw new AssessmentException("sender is null");
		}
		
		PrivacyLogFilter filter = new PrivacyLogFilter();
		filter.setSenderClass(sender);
		
		return estimatePrivacyBreach(filter);
	}
	
	private double[] estimatePrivacyBreach(PrivacyLogFilter filter) throws AssessmentException {

		double[] corr = new double[] {0, 0, 0};
		
		for (DataTransmissionLogEntry tr : privacyLog.search(filter)) {
			corr[0] += tr.getCorrelationWithDataAccess();
			corr[1] += tr.getCorrelationWithDataAccessBySender();
			corr[2] += tr.getCorrelationWithDataAccessBySenderClass();
		}
		return corr;
	}
}
