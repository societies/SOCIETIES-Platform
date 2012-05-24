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
package org.societies.domainauthority.webapp.controller;

import java.util.HashMap;
import java.util.Map;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.comm.ICommManagerController;
import org.societies.domainauthority.registry.DaRegistry;
import org.societies.domainauthority.registry.DaUserRecord;
import org.societies.domainauthority.webapp.models.LoginForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;
import javax.validation.Valid;


import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.comm.ICommManagerController;
/**
 * This class shows the example of annotated controller 
 * @author Perumal Kuppuudaiyar
 *
 */
@Controller
public class NewAccountLoginController {
	
	@Autowired
	private ICommManager commManager;
	@Autowired
	private ICommManagerController commManagerControl;
	@Autowired
	ICISCommunicationMgrFactory ccmFactory;
	@Autowired
	DaRegistry daRegistry;
	
	/**
	 * @return the ccmFactory
	 */
	public ICISCommunicationMgrFactory getCcmFactory() {
		return ccmFactory;
	}

	/**
	 * @param ccmFactory
	 *            the ccmFactory to set
	 */
	public void setCcmFactory(ICISCommunicationMgrFactory ccmFactory) {
		this.ccmFactory = ccmFactory;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	/**
	 * @return the commManagerControl
	 */
	public ICommManagerController getCommManagerControl() {
		return commManagerControl;
	}

	/**
	 * @param commManagerControl
	 *            the commManagerControl to set
	 */
	public void setCommManagerControl(ICommManagerController commManagerControl) {
		this.commManagerControl = commManagerControl;
	}
	/**
	 * Displays login Page
	 * @return
	 */
	public  NewAccountLoginController() {
				
	}
	/**
	 * This method get called when user request for login page by using
	 * url http://localhost:8080/societies/login.html
	 * @return login jsp page and model object
	 */
	@RequestMapping(value="/newaccount.html",method = RequestMethod.GET)
	public ModelAndView login() {
		//model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please login to your Societies account");
		//data model object to be used for displaying form in html page 
		LoginForm loginForm = new LoginForm();
		model.put("loginForm", loginForm);		
		/*return modelandview object and passing login (jsp page name) and model object as
		 constructor */
		return new ModelAndView("newaccount", model) ;
	}
	/**
	 * This method get called when user submit the login page using submit button	
	 * @param loginForm java object with data entered by user
	 * @param result boolean result to check the data binding with object 
	 * @param model Map object passed to login page.
	 * @return loginsuccess page if sucess or login page for retry if failed
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/newaccount.html", method = RequestMethod.POST)
	public ModelAndView processLogin(@Valid LoginForm loginForm, BindingResult result,
    Map model) {

		if (result.hasErrors()) {
			model.put("error", "Login form error");
			return new ModelAndView("login", model);
		}
		String userName = loginForm.getUserName();
		String subDomain = loginForm.getSubDomain();
		String password = loginForm.getPassword();
		boolean isAuthenticated = true;
		DaUserRecord userRecord  = null;
		

		
		
		//NOTE : Temporary measure until connection to openfire database is in place
		INetworkNode nodeDetails = getCommManager().getIdManager()
				.getDomainAuthorityNode();

		String jid = userName + "." + nodeDetails.getDomain();
		IIdentity newNodeDetails = null;
		try {
			newNodeDetails = getCommManager().getIdManager().fromJid(jid);
			
			// Check that the account exists
			userRecord = daRegistry.getXmppIdentityDetails(userName);
			
			if (userRecord != null)
			{
				if (userRecord.getId() != null)
				{	
					//account alreadt exists exist, direct to new user page
					model.put("error", "account already exists");
					return new ModelAndView("newaccount", model);
				}
			}
		/**	if (userRecord.getId().contains(jid))
			{
				//account alreadt exists exist, direct to new user page
				model.put("error", "account already exists");
				return new ModelAndView("newaccount", model);
				
			}
		*/
			userRecord = new DaUserRecord();
			// create new account
			userRecord.setName(userName);
			userRecord.setId(jid);
			userRecord.setHost("");
			userRecord.setPort("");
			userRecord.setStatus("new");
			userRecord.setUserType("user");
			userRecord.setPassword(password);
			
			daRegistry.addXmppIdentityDetails(userRecord);
			
			
			
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			isAuthenticated = false;

		}

		model.put("name", userName);

		model.put("result", "Account Created  - Adminstrator now needs to do their stuff");
		return new ModelAndView("newaccountsuccess", model);	
						
	}	
}