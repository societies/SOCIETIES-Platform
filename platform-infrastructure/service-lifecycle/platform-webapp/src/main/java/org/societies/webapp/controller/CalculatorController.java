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

import org.societies.service.api.IMathService;
import org.societies.webapp.models.CalculatorForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CalculatorController {

	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private IMathService calService;
	
	public IMathService getCalService() {
		return calService;
	}

	public void setCalService(IMathService calService) {
		this.calService = calService;
	}

	@RequestMapping(value = "/calculator.html", method = RequestMethod.GET)
	public ModelAndView calcultor() {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		CalculatorForm calForm = new CalculatorForm();
		Map<String, String> methods = new LinkedHashMap<String, String>();
		methods.put("ADD", "add number");
		methods.put("MULTIPLY", "Mutiply");
		methods.put("SUBTRACT", "Subtract");
		model.put("methods", methods);
		model.put("calForm", calForm);
		model.put("calculatorResult", "Calculator Result :");
		return new ModelAndView("calculator", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/calculator.html", method = RequestMethod.POST)
	public ModelAndView calcultor(@Valid CalculatorForm calForm,
			BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "calculator form error");
			return new ModelAndView("calculator", model);
		}

		if (getCalService() == null) {
			model.put("errormsg", "Calculator Service referece not avaiable");
			return new ModelAndView("error", model);
		}

		int numA = calForm.getNumberA();
		int numB = calForm.getNumberB();
		String method = calForm.getMethod();
		String res;
		int calresult;
		if (method.equalsIgnoreCase("ADD")) {
			calresult=this.getCalService().add(numA, numB);
			res="Calculator Result :"+numA+" "+method+" "+numB+" = "+calresult;
		} else if(method.equalsIgnoreCase("MULTIPLY")) {
			calresult=this.getCalService().multiply(numA, numB); 
			res="Calculator Result :"+numA+" "+method+" "+numB+" = "+calresult;
		}else if(method.equalsIgnoreCase("SUBTRACT")){
			calresult=this.getCalService().subtract(numA, numB); 
			res="Calculator Result :"+numA+" "+method+" "+numB+" = "+calresult;
		}else{
			res="error unknown metod";
		}
		
		model.put("calculatorResult", res);		
		return new ModelAndView("calculatorresult", model);

	}
}