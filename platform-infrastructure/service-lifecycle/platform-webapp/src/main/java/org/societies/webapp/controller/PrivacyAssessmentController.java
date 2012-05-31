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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;

import org.societies.webapp.models.PrivacyAssessmentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultClassName;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultIIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IAssessment;


@Controller
public class PrivacyAssessmentController {

	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private IAssessment assessment;
	
	public IAssessment getAssessment() {
		return assessment;
	}

	public void setAssessment(IAssessment sdService) {
		this.assessment = sdService;
	}

	@RequestMapping(value = "/privacyassessment.html", method = RequestMethod.GET)
	public ModelAndView Privacyassessment() {

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		
		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
		PrivacyAssessmentForm assForm = new PrivacyAssessmentForm();
		model.put("assForm", assForm);
		
		//ADD ALL THE SELECT BOX VALUES USED ON THE FORM
		Map<String, String> methods = new LinkedHashMap<String, String>();
		methods.put("assessAllNow", "Perform assessment for all data transmissions");
		methods.put("getAssessmentAllIds", "Get a-posteriori assessment for all sender identities");
		methods.put("getAssessmentAllClasses", "Get a-posteriori assessment for all sender classes");
		model.put("methods", methods);
		
		model.put("privacyassessmentresult", "Privacy Assessment Result :");
		return new ModelAndView("privacyassessment", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/privacyassessment.html", method = RequestMethod.POST)
	public ModelAndView privacyAssessment(@Valid PrivacyAssessmentForm assForm,
			BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "privacy assessment form error");
			return new ModelAndView("privacyassessment", model);
		}

		if (getAssessment() == null) {
			model.put("errormsg", "Privacy Assessment Service reference not avaiable");
			return new ModelAndView("error", model);
		}

		String method = assForm.getMethod();
		String res;
		
		try {
			
			if (method.equalsIgnoreCase("getAssessmentAllIds")) {

				HashMap<IIdentity, AssessmentResultIIdentity> assResult;
				assResult = assessment.getAssessmentAllIds();
				res="Privacy Assessment Result for all identities";
				model.put("services", assResult.values());
			}
			else if (method.equalsIgnoreCase("getAssessmentAllClasses")) {
				
				HashMap<String, AssessmentResultClassName> assResult;
				assResult = assessment.getAssessmentAllClasses();
				res="Privacy Assessment Result for all classes";
				model.put("services", assResult.values());
			}
			else if (method.equalsIgnoreCase("assessAllNow")) {
				
				assessment.assessAllNow();
				res="Privacy Assessment Result";
			}
			else {
				res="error unknown metod";
			}
		
			model.put("result", res);
		}
		catch (Exception ex)
		{
			res = "Oops!!!! <br/>";
		};
		
		return new ModelAndView("privacyassessmentresult", model);
	}
}