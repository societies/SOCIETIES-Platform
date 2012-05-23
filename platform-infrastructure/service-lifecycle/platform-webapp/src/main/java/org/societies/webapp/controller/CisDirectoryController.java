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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.webapp.models.CISDirectoryForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.comm.ICommManagerController;
import javax.validation.Valid;


import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.cis.directory.ICisAdvertisementRecord;
import org.societies.api.cis.directory.ICisDirectory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

@Controller
public class CisDirectoryController {
	
	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private ICisDirectory cisDirectory;
	@Autowired
	private ICommManager commManager;
	
	@Autowired
	private ICommManagerController commManagerControl;
	
	
	public ICisDirectory getCisDirectory() {
		return cisDirectory;
	}

	public void setCisDirectory(ICisDirectory cisDirectory) {
		this.cisDirectory = cisDirectory;
	}

	@RequestMapping(value = "/cisdirectory.html", method = RequestMethod.GET)
	public ModelAndView CISDirectory() {

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		
		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
		CISDirectoryForm cdForm = new CISDirectoryForm();
		model.put("cdForm", cdForm);
		
		//ADD ALL THE SELECT BOX VALUES USED ON THE FORM
		Map<String, String> methods = new LinkedHashMap<String, String>();
		methods.put("GetCisAdverts", "Get CIS Advertisements");
		methods.put("GetFindAdvert", "Find Specific CIS Advert");
		model.put("methods", methods);
		
		model.put("cisdirectoryresult", "CIS Directory Result :");
		return new ModelAndView("cisdirectory", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/cisdirectory.html", method = RequestMethod.POST)
	public ModelAndView CISDirectory(@Valid CISDirectoryForm cdForm,
			BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "CIS Directory form error");
			return new ModelAndView("cisdirectory", model);
		}

		if (getCisDirectory() == null) {
			model.put("errormsg", "CIS Directory reference not avaiable");
			return new ModelAndView("error", model);
		}

		
		String method = cdForm.getMethod();
		Future<List<CisAdvertisementRecord>> asynchResult = null;
		List<CisAdvertisementRecord> adverts =  new ArrayList<CisAdvertisementRecord>();
		
		String res = null;
		
		try {
		
			if (method.equalsIgnoreCase("GetCisAdverts")) {
				asynchResult=this.getCisDirectory().findAllCisAdvertisementRecords();
				res="CIS Directory Result ";
				
				adverts = asynchResult.get();
				model.put("adverts", adverts);
				
			}else if (method.equalsIgnoreCase("Find Specific CIS Advert")) {
				
				//asynchResult=this.getCisDirectory()
				
					
				//adverts = asynchResult.get();
				//model.put("adverts", adverts);
					
			}else{
				res="error unknown metod";
			}
		
			model.put("result", res);
			
		}
		catch (Exception e)
		{
			res = "Oops!!!!<br/>";
		};
		
		
		return new ModelAndView("cisdirectoryresult", model);
	}

}
