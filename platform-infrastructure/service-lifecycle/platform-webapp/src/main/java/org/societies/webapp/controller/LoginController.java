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

import org.societies.webapp.models.LoginForm;
import org.societies.webapp.service.UserAuthentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
/**
 * This class shows the example of annotated controller 
 * @author Perumal Kuppuudaiyar
 *
 */
@Controller
public class LoginController {
	
	
	@Autowired
	private ICommManager commManager;
	
	
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	/**
	 * Displays login Page
	 * @return
	 */
	public  LoginController() {
				
	}
	/**
	 * This method get called when user request for login page by using
	 * url http://localhost:8080/societies/login.html
	 * @return login jsp page and model object
	 */
	@RequestMapping(value="/login.html",method = RequestMethod.GET)
	public ModelAndView login() {
		//model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please login to your Societies account");
		//data model object to be used for displaying form in html page 
		LoginForm loginForm = new LoginForm();
		model.put("loginForm", loginForm);		
		/*return modelandview object and passing login (jsp page name) and model object as
		 constructor */
		return new ModelAndView("login", model) ;
	}
	/**
	 * This method get called when user submit the login page using submit button	
	 * @param loginForm java object with data entered by user
	 * @param result boolean result to check the data binding with object 
	 * @param model Map object passed to login page.
	 * @return loginsuccess page if sucess or login page for retry if failed
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/login.html", method = RequestMethod.POST)
	public ModelAndView processLogin(@Valid LoginForm loginForm, BindingResult result,
    Map model) {

			if (result.hasErrors()) {
				model.put("result", "Login form error");
				return new ModelAndView("login", model);
			}			
			String userName = loginForm.getUserName();
			String password = loginForm.getPassword();
			UserAuthentication userAuth=new UserAuthentication();
			model.put("name", userName);
			boolean isAuthenticated=userAuth.authenticate(userName,password);
			if(isAuthenticated){
				model.put("result", "Login Successfull");
				return new ModelAndView("loginsuccess", model);	
			}else{					
				model.put("result", "Login UnSuccessfull");
				return new ModelAndView("login", model);
			}			
	}	
	
	/**
	 * This method get called when user request for login page by using
	 * url http://localhost:8080/societies/login.html
	 * @return login jsp page and model object
	 */
	@RequestMapping(value="/{cssId}/loginviada.html",method = RequestMethod.GET)
	public ModelAndView loginviada(@PathVariable(value="cssId") String cssId, HttpSession session) {
		//model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		
		// check that user was redirected to correct webapp by cloud controller
		
		if ((cssId != null) && (cssId.toLowerCase().contains(this.getCommManager().getIdManager().getThisNetworkNode().getBareJid().toLowerCase())))
		{
			//all okay, we're in the correct container
			model.put("message", "Welcome to your Societies account");
			session.setAttribute("User", this.getCommManager().getIdManager().getThisNetworkNode().getBareJid());
			return new ModelAndView("loginviada", model) ;
		}
		/*return modelandview object and passing login (jsp page name) and model object as
		 constructor */
		model.put("errormsg", " Problem with account setup, contact administrator");
		return new ModelAndView("error", model) ;
	}
}