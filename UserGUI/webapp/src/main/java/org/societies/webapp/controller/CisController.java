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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.societies.api.cis.directory.ICisDirectory;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.webapp.comms.UserGuiCommsClient;
import org.societies.webapp.model.CisInfo;
import org.societies.webapp.model.LoginForm;
import org.societies.webapp.service.OpenfireLoginService;
import org.societies.webapp.service.UserService;

import org.societies.cis.directory.client.CisDirectoryRemoteClient;

/**
 * 
 * @author Maria
 * 
 */
@Controller
@Scope("request")   
public class CisController {

	private static Logger log = LoggerFactory.getLogger(CisController.class);
	
	@Autowired
	UserService userService;
	@Autowired
	ICisManager  cisManager;
	@Autowired
	ICisDirectoryRemote cisDirectory;
	

	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}
	
	public ICisDirectoryRemote getCisDirectory() {
		return cisDirectory;
	}
	public void setCisDirectory(ICisDirectoryRemote cisDirectory) {
		this.cisDirectory = cisDirectory;
	}
	/**
	 * Displays login Page
	 * 
	 * @return
	 */
	public CisController() {
		log.info("CisController constructor");
	}

	/**
	 * This method get called when user request for login page by using url
	 * http://localhost:8080/societies/login.html
	 * 
	 * @return login jsp page and model object
	 */
	@RequestMapping(value = "/your_communities_list.html", method = RequestMethod.GET)
	public ModelAndView initCisController() {
		//GET XMPP SERVER DOMAIN
		
		
		// model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		LoginForm loginform = new LoginForm();
		model.put("loginform", loginform );
		// data model object to be used for displaying form in html page
		/*
		 * return modelandview object and passing login (jsp page name) and
		 * model object as constructor
		 */
		return new ModelAndView("your_communities_list", model);
	}
	
	@RequestMapping(value = "/get_my_communities.html", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getMyCommunities( ){
		JsonResponse res = new JsonResponse();
		List<CisInfo> cisinfoList = new ArrayList<CisInfo>();
		
		if (getUserService().isUserLoggedIn())
		{
			
			List<ICis> cisList = getCisManager().getCisList();
			
			
			//TODO : Should be able to give cisdorectory a list of id's!!
			//For now, just get them all 
			CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();

			getCisDirectory().findAllCisAdvertisementRecords(callback);
			List<CisAdvertisementRecord> adverts = callback.getResultList();

			
			for ( int i = 0; i < cisList.size(); i++)
			{
				//find ad for this id
				boolean bFound = false;
				int j = 0;
				do
				{
					if (adverts.get(j).getId().contains(cisList.get(i).getCisId()))
							bFound = true;
					else
						j++;
				} while ((bFound == false) && (j < adverts.size()));
				
				if (bFound)
				{
					CisInfo cisInfo = new CisInfo();
					cisInfo.setCisid(cisList.get(i).getCisId());
					cisInfo.setCisname(adverts.get(j).getName());
					cisinfoList.add(cisInfo);
				}
				
			}
		 
			res.setStatus("SUCCESS");
			res.setResult(cisinfoList);
		}
		else
			res.setStatus("FAILURE");
		
		return res;
	}
	
	@RequestMapping(value = "/get_suggested_communities.html", method = RequestMethod.POST)
	public @ResponseBody JsonResponse getSuggestedCommunities( ){
		JsonResponse res = new JsonResponse();
		List<CisInfo> cisinfoList = new ArrayList<CisInfo>();
		
		if (getUserService().isUserLoggedIn())
		{
			
			List<ICis> cisList = getCisManager().getCisList();
			
			
			//TODO : Should be able to give cisdorectory a list of id's!!
			//For now, just get them all 
			CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();

			getCisDirectory().findAllCisAdvertisementRecords(callback);
			List<CisAdvertisementRecord> adverts = callback.getResultList();

			
			for ( int i = 0; i < cisList.size(); i++)
			{
				//find ad for this id
				boolean bFound = false;
				int j = 0;
				do
				{
					if (adverts.get(j).getId().contains(cisList.get(i).getCisId()))
							bFound = true;
					else
						j++;
				} while ((bFound == false) && (j < adverts.size()));
				
				if (!bFound)
				{
					CisInfo cisInfo = new CisInfo();
					cisInfo.setCisid(cisList.get(i).getCisId());
					cisInfo.setCisname(adverts.get(j).getName());
					cisinfoList.add(cisInfo);
				}
				
			}
		 
			res.setStatus("SUCCESS");
			res.setResult(cisinfoList);
		}
		else
			res.setStatus("FAILURE");
		
		return res;
	}
	
}
