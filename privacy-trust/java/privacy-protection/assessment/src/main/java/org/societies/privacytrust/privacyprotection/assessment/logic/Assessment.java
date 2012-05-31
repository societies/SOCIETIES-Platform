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

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultClassName;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultIIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IAssessment;
import org.societies.privacytrust.privacyprotection.assessment.log.PrivacyLog;

/**
 * A wrapper around {@link DataTransferAnalyzer}
 * 
 * Parses the log and tries to find potential privacy breaches that occurred in the past.
 * This can be used for the a-posteriori assessment.
 * 
 * Estimates whether a particular data transmission is a potential privacy breach or not.
 * This can be used for the a-priori assessment.
 *
 * @author Mitja Vardjan
 *
 */
public class Assessment implements IAssessment {

	private static Logger LOG = LoggerFactory.getLogger(Assessment.class);

	private PrivacyLog privacyLog;
	private DataTransferAnalyzer dataTransferAnalyzer;
	
	private HashMap<IIdentity, AssessmentResultIIdentity> assessmentById = new HashMap<IIdentity, AssessmentResultIIdentity>();
	private HashMap<String, AssessmentResultClassName> assessmentByClass = new HashMap<String, AssessmentResultClassName>();
	
	public Assessment() {
		LOG.info("Constructor");
	}
	
	// Getters and setters for beans
	public PrivacyLog getPrivacyLog() {
		LOG.debug("getPrivacyLog()");
		return privacyLog;
	}
	public void setPrivacyLog(PrivacyLog privacyLog) {
		LOG.debug("setPrivacyLog()");
		this.privacyLog = privacyLog;
	}

	public void init() {
		LOG.debug("init()");
		dataTransferAnalyzer = new DataTransferAnalyzer(privacyLog);
		assessAllNow();
	}
	
	@Override
	public void setAutoPeriod(int seconds) {
		LOG.warn("setAutoPeriod({}): not implemented yet", seconds);
		// TODO
	}
	
	@Override
	public void assessAllNow() {
		
		LOG.info("assessAllNow()");
		
		try {
			// For each sender identity: calculate result and update value in assessmentById
			for (IIdentity sender : privacyLog.getSenderIds()) {
				AssessmentResultIIdentity ass = dataTransferAnalyzer.estimatePrivacyBreach(sender);
				assessmentById.put(sender, ass);
			}
			// For each sender class: calculate result and update value in assessmentByClass
			for (String sender : privacyLog.getSenderClassNames()) {
				AssessmentResultClassName ass = dataTransferAnalyzer.estimatePrivacyBreach(sender);
				assessmentByClass.put(sender, ass);
			}
		}
		catch (AssessmentException e) {
			LOG.warn("assessAllNow(): Skipped a sender", e);
		}
	}
	
	@Override
	public HashMap<IIdentity, AssessmentResultIIdentity> getAssessmentAllIds() {
		
		LOG.info("getAssessmentAllIds()");

		return assessmentById;
	}
	
	@Override
	public HashMap<String, AssessmentResultClassName> getAssessmentAllClasses() {

		LOG.info("getAssessmentAllClasses()");
		
		return assessmentByClass;
	}

	@Override
	public AssessmentResultIIdentity getAssessment(IIdentity sender) {
		
		LOG.info("getAssessment({})", sender);
		
		if (sender == null || sender.getJid() == null) {
			LOG.warn("getAssessment({}): invalid argument", sender);
			return null;
		}
		return assessmentById.get(sender);
	}

	@Override
	public AssessmentResultClassName getAssessment(String sender) {

		LOG.info("getAssessment({})", sender);

		if (sender == null) {
			LOG.warn("getAssessment({}): invalid argument", sender);
			return null;
		}
		return assessmentByClass.get(sender);
	}
	
	@Override
	public long getNumDataTransmissionEvents() {

		LOG.info("getNumDataTransmissionEvents()");
		
		return privacyLog.getDataTransmission().size();
	}
	
	@Override
	public long getNumDataAccessEvents() {

		LOG.info("getNumDataAccessEvents()");
		
		return privacyLog.getDataAccess().size();
	}
}
