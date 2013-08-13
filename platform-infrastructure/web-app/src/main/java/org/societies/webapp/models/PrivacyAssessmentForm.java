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
/**
 * 
 */
package org.societies.webapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Mitja Vardjan
 */
@Service
@Scope("Session")
@SessionScoped
@ManagedBean
public class PrivacyAssessmentForm implements Serializable {

	private static final long serialVersionUID = 3196618675483151078L;
    private static final Logger log = LoggerFactory.getLogger(PrivacyAssessmentForm.class);
    
    /**
     * Perform periodic assessment in background
     */
	private boolean autoAssessment;
    
    /**
     * True to get results not only for 3rd party bundles, but
	 * for the SOCIETIES platform bundles, too.
	 * False to get results for 3rd party bundles only.
     */
	private boolean includePlatformBundles;
	
	/**
	 * Period in seconds for periodic assessment
	 */
	private int autoAssessmentInSecs;

	/**
	 * Selected assessment subject type
	 */
	private String assessmentSubject;
	
	/**
	 * List of all assessment subject types
	 */
	private List<String> assessmentSubjects;
	
    /**
	 * Relative path to chart image to show
	 */
	private String chart;
	
	private Date startDate;

	private Date endDate;

	public class SubjectTypes {
		public static final String RECEIVER_IDS = "Receiver identities";
		public static final String SENDER_IDS = "Sender identities";
		public static final String SENDER_CLASSES = "Sender classes";
		public static final String DATA_ACCESS_IDS = "Data access by identities";
		public static final String DATA_ACCESS_CLASSES = "Data access by classes";
	}
	
	public PrivacyAssessmentForm() {

		log.info("constructor");

		assessmentSubjects = new ArrayList<String>();
		assessmentSubjects.add(SubjectTypes.RECEIVER_IDS);
		assessmentSubjects.add(SubjectTypes.SENDER_IDS);
		assessmentSubjects.add(SubjectTypes.SENDER_CLASSES);
		assessmentSubjects.add(SubjectTypes.DATA_ACCESS_IDS);
		assessmentSubjects.add(SubjectTypes.DATA_ACCESS_CLASSES);
	}

	/**
	 * @return List of all assessment subject types
	 */
	public List<String> getAssessmentSubjects() {
		return assessmentSubjects;
	}

	/**
	 * @param assessmentSubjects List of all assessment subject types
	 */
	public void setAssessmentSubjects(List<String> assessmentSubjects) {
		log.debug("assessmentSubjects = {}", autoAssessment);
		this.assessmentSubjects = assessmentSubjects;
	}

	/**
	 * @return periodic assessment in background
	 */
	public boolean isAutoAssessment() {
		return autoAssessment;
	}

	/**
	 * @param autoReassessment periodic assessment in background
	 */
	public void setAutoAssessment(boolean autoAssessment) {
		log.debug("autoAssessment = {}", autoAssessment);
		this.autoAssessment = autoAssessment;
	}

	/**
	 * @return the includePlatformBundles
	 */
	public boolean isIncludePlatformBundles() {
		return includePlatformBundles;
	}

	/**
	 * @param includePlatformBundles the includePlatformBundles to set
	 */
	public void setIncludePlatformBundles(boolean includePlatformBundles) {
		this.includePlatformBundles = includePlatformBundles;
	}

	/**
	 * @return Period in seconds for periodic assessment
	 */
	public int getAutoAssessmentInSecs() {
		return autoAssessmentInSecs;
	}

	/**
	 * @param autoReassessmentInSecs Period in seconds for periodic assessment
	 */
	public void setAutoAssessmentInSecs(int autoAssessmentInSecs) {
		log.debug("autoAssessmentInSecs = {}", autoAssessmentInSecs);
		this.autoAssessmentInSecs = autoAssessmentInSecs;
	}

	/**
	 * @return Selected assessment subject type
	 */
	public String getAssessmentSubject() {
		return assessmentSubject;
	}

	/**
	 * @param assessmentSubject Selected assessment subject type
	 */
	public void setAssessmentSubject(String assessmentSubject) {
		log.debug("assessmentSubject = {}", assessmentSubject);
		this.assessmentSubject = assessmentSubject;
	}

	/**
	 * @return Relative path to chart image to show
	 */
	public String getChart() {
		return chart;
	}

	/**
	 * @param chart Relative path to chart image to show
	 */
	public void setChart(String chart) {
		log.debug("chart = {}", chart);
		this.chart = chart;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		log.debug("getStartDate(): {}", startDate);
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		log.debug("setStartDate({})", startDate);
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		log.debug("getEndDate(): {}", endDate);
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		log.debug("setEndDate({})", endDate);
		this.endDate = endDate;
	}
}
