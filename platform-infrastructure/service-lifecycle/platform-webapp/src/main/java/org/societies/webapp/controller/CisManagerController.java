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
package org.societies.webapp.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.societies.webapp.models.CisManagerForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisRecord;

@Controller
public class CisManagerController {

	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private ICisManager cisManager;

	/**
	 * @return the cisManager */
	public ICisManager getCisManager() {
		return cisManager;
	}

	/**
	 * @param cisManager the cisManager to set */
	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	@RequestMapping(value = "/cismanager.html", method = RequestMethod.GET)
	public ModelAndView cssManager() {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Welcome to the CIS Manager");
		CisManagerForm cisForm = new CisManagerForm();
		
		//LIST METHODS AVAIABLE TO TEST
		Map<String, String> methods = new LinkedHashMap<String, String>();
		methods.put("CreateCis", "Create a CIS ");
		methods.put("GetCisList", "Search my CIS's");
		model.put("methods", methods);
		
		model.put("cmForm", cisForm);
		model.put("cismanagerResult", "CIS Management Result :");
		return new ModelAndView("cismanager", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/cismanager.html", method = RequestMethod.POST)
	public ModelAndView cssManager(@Valid CisManagerForm cisForm, BindingResult result, Map model) {

		model.put("message", "Welcome to the CIS Manager Page");

		if (result.hasErrors()) {
			model.put("result", "CIS Manager form error");
			return new ModelAndView("cismanager", model);
		}

		if (getCisManager() == null) {
			model.put("errormsg", "CIS Manager Service reference not avaiable");
			return new ModelAndView("error", model);
		}

		String method = cisForm.getMethod();
		String res;

		try {
			if (method.equalsIgnoreCase("CreateCis")) {
				model.put("methodcalled", "CreateCis");

				Future<ICisOwned> cisResult = this.getCisManager().createCis(
						cisForm.getCssId(), 
						cisForm.getCisPassword(), 
						cisForm.getCisName(),
						cisForm.getCisType(),
						cisForm.getCisMode());

				res = "Successfully created CIS: " + cisResult.get().getCisId();
				model.put("res", res);

			} else if (method.equalsIgnoreCase("GetCisList")) {
				model.put("methodcalled", "GetCisList");

				//ICisRecord searchRecord = null;
				//ICisRecord[] records = this.getCisManager().getCisList(searchRecord);
				List<ICisRecord> records = this.getCisManager().getCisList();
				model.put("cisrecords", records);

			} else {
				model.put("methodcalled", "Unknown");
				res = "error unknown metod";
			}
		} catch (Exception ex) {
			res = "Oops!!!! <br/>";
		}

		model.put("cmForm", cisForm);
		return new ModelAndView("cismanagerresult", model);
	}
}
