package org.societies.webapp.controller;
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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IAssessment;
import org.societies.webapp.models.PrivacyAssessmentForm;
import org.societies.webapp.service.PrivacyAssessmentService;


@ManagedBean(name = "assessmentController")
@RequestScoped
public class PrivacyAssessmentController extends BasePageController {

	private static final long serialVersionUID = 1073106046087768688L;
	private static Logger log = LoggerFactory.getLogger(PrivacyAssessmentController.class);
	
	@ManagedProperty(value = "#{privacyAssessmentForm}")
	private PrivacyAssessmentForm model;
	
	public PrivacyAssessmentController() {
		super();
		log.info("constructor");
	}
	
	/**
	 * OSGI service get auto injected
	 */
	@ManagedProperty(value = "#{privacyAssessment}")
	private IAssessment assessment;

	@ManagedProperty(value = "#{privacyAssessmentService}")
	private PrivacyAssessmentService paService;

	// Getters and setters for services
	
	public IAssessment getAssessment() {
		return assessment;
	}
	public void setAssessment(IAssessment sdService) {
		log.debug("setAssessment()");
		this.assessment = sdService;
	}
	public PrivacyAssessmentService getPaService() {
		return paService;
	}
	public void setPaService(PrivacyAssessmentService paService) {
		this.paService = paService;
	}
	public PrivacyAssessmentForm getModel() {
		return model;
	}
	public void setModel(PrivacyAssessmentForm model) {
		log.debug("Model set");
		this.model = model;
	}

	// Methods called from View
	
	public void assessNow() {
		log.debug("AssessNow button clicked");
		assessment.assessAllNow(model.getStartDate(), model.getEndDate());
		paService.generateImages();
	}
	
}
