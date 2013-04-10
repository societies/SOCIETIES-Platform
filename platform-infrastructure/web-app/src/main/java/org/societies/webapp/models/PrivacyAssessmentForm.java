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
/**
 * 
 * @author Mitja Vardjan
 */
public class PrivacyAssessmentForm {

	private String method;
	
	// Settings and control
	private boolean autoReassessment;
	private int autoReassessmentInSecs;
	private boolean assessNow;
	
	// Showing assessment results
	private String assessmentSubjectType;
	private String presentationFormat;
	private String assessmentSubject;
	
	private String chart;

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	
	/**
	 * @return the autoReassessment
	 */
	public boolean isAutoReassessment() {
		return autoReassessment;
	}

	/**
	 * @param autoReassessment the autoReassessment to set
	 */
	public void setAutoReassessment(boolean autoReassessment) {
		this.autoReassessment = autoReassessment;
	}

	/**
	 * @return the autoReassessmentInSecs
	 */
	public int getAutoReassessmentInSecs() {
		return autoReassessmentInSecs;
	}

	/**
	 * @param autoReassessmentInSecs the autoReassessmentInSecs to set
	 */
	public void setAutoReassessmentInSecs(int autoReassessmentInSecs) {
		this.autoReassessmentInSecs = autoReassessmentInSecs;
	}

	/**
	 * @return the assessNow
	 */
	public boolean isAssessNow() {
		return assessNow;
	}

	/**
	 * @param assessNow the assessNow to set
	 */
	public void setAssessNow(boolean assessNow) {
		this.assessNow = assessNow;
	}

	/**
	 * @return the assessmentSubjectType
	 */
	public String getAssessmentSubjectType() {
		return assessmentSubjectType;
	}

	/**
	 * @param assessmentSubjectType the assessmentSubjectType to set
	 */
	public void setAssessmentSubjectType(String assessmentSubjectType) {
		this.assessmentSubjectType = assessmentSubjectType;
	}

	/**
	 * @return the presentationFormat
	 */
	public String getPresentationFormat() {
		return presentationFormat;
	}

	/**
	 * @param presentationFormat the presentationFormat to set
	 */
	public void setPresentationFormat(String presentationFormat) {
		this.presentationFormat = presentationFormat;
	}

	/**
	 * @return the assessmentSubject
	 */
	public String getAssessmentSubject() {
		return assessmentSubject;
	}

	/**
	 * @param assessmentSubject the assessmentSubject to set
	 */
	public void setAssessmentSubject(String assessmentSubject) {
		this.assessmentSubject = assessmentSubject;
	}

	/**
	 * @return the chart
	 */
	public String getChart() {
		return chart;
	}

	/**
	 * @param chart the chart to set
	 */
	public void setChart(String chart) {
		this.chart = chart;
	}
}
