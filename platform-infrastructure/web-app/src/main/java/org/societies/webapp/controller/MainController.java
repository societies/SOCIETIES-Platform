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
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.societies.webapp.models.LoginForm;
import org.societies.webapp.service.OpenfireLoginService;
import org.societies.webapp.service.UserService;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
/**
 * 
 * @author Maria
 * 
 */
@Controller
@Scope("request")   
public class MainController {

	private static Logger log = LoggerFactory.getLogger(MainController.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private OpenfireLoginService openfireLoginService;
	@Autowired
	private ICommManager commManager;
	

	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public OpenfireLoginService getOpenfireLoginService() {
		return openfireLoginService;
	}
	public void setOpenfireLoginService(OpenfireLoginService openfireLoginService) {
		this.openfireLoginService = openfireLoginService;
	}
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	/**
	 * Displays login Page
	 * 
	 * @return
	 */
	public MainController() {
		log.info("MainController constructor");
	}

	/**
	 * This method get called when user request for login page by using url
	 * http://localhost:8080/societies/login.html
	 * 
	 * @return login jsp page and model object
	 */
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public ModelAndView initMainController() {
		//GET XMPP SERVER DOMAIN
		Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("loggedIn", getUserService().isUserLoggedIn());
		
		String userID = new String(getCommManager().getIdManager().getThisNetworkNode().getBareJid());
		
		LoginForm loginform = new LoginForm();
		loginform.setUsername(userID.substring(0,userID.indexOf('.')));
		model.put("loginform", loginform );

		return new ModelAndView("index", model) ;
	}
	
	@RequestMapping(value = "/login.html", method = RequestMethod.POST)
	public ModelAndView processLogin(@Valid LoginForm loginForm,
			BindingResult result,  Map model) {
		LoginForm loginform = new LoginForm();
		model.put("loginform", loginform );
		
		
		// Check if a user is already logged in
		if (getUserService().isUserLoggedIn())
			return new ModelAndView("index", model); //TODO : return error string
		
		//TODO Do check
		if (getOpenfireLoginService().doLogin(loginForm.getUsername(), loginForm.getPassword()) != null)
			getUserService().login();
		
//		if (getUserService().getUserjid() == null)
//			return new ModelAndView("index", model); //TODO : return error string
		
		// if we get this far we can set up the user session comm manager
//		getUserService().getLocalCommManager();
		
		
		/*
		 * return modelandview object and passing login (jsp page name) and
		 * model object as constructor
		 */
		return new ModelAndView("index", model);
	}

	
}
