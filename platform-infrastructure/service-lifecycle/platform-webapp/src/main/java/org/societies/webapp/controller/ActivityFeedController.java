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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.societies.webapp.models.CisFeedForm;
import org.societies.webapp.models.ServiceDiscoveryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.schema.servicelifecycle.model.Service;


@Controller
public class ActivityFeedController {

	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private IActivityFeed activtyFeed;

	/**
	 * @return the activtyFeed*/
	public IActivityFeed getActivtyFeed() {
		return activtyFeed;
	}

	/**
	 * @param activtyFeed the activtyFeed to set*/
	public void setActivtyFeed(IActivityFeed activtyFeed) {
		this.activtyFeed = activtyFeed;
	}

	@RequestMapping(value = "/cisfeed.html", method = RequestMethod.GET)
	public ModelAndView Servicediscovery() {

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		
		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
		CisFeedForm activityForm = new CisFeedForm();
		model.put("activityForm", activityForm);
		
		//ADD ALL THE SELECT BOX VALUES USED ON THE FORM
		Map<String, String> methods = new LinkedHashMap<String, String>();
		methods.put("addCisActivity", "Add Activity");
		methods.put("getActivities", "Get Activities");
		model.put("methods", methods);

		return new ModelAndView("cisfeed", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/cisfeed.html", method = RequestMethod.POST)
	public ModelAndView serviceDiscovery(@Valid CisFeedForm activityForm, BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "service discovery form error");
			return new ModelAndView("cisfeed", model);
		}

		if (getActivtyFeed() == null) {
			model.put("errormsg", "Activity Feed Service reference not avaiable");
			return new ModelAndView("error", model);
		}

		String cssId = activityForm.getCssId();
		String method = activityForm.getMethod();
		String res;
		try {
		
			if (method.equalsIgnoreCase("addCisActivity")) {
				IActivity activity = null; 
				getActivtyFeed().addCisActivity(activity);
				res="Activty added for cssID: " + cssId;
				
			}else if (method.equalsIgnoreCase("getActivities")) {
				
				String timePeriod = activityForm.getTimePeriod();
				getActivtyFeed().getActivities(cssId, timePeriod);
				res="Activities for cssID: " + cssId;
					
			}else{
				res="error unknown metod";
			}
		
			model.put("result", res);
			
		}
		catch (Exception e)
		{
			res = "Oops!!!!<br/>"+ e.getMessage();
		}
		
		return new ModelAndView("cisfeedResult", model);
	}
}