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
import java.util.List;
import java.util.Map;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.comm.ICommManagerController;

import org.societies.domainauthority.webapp.models.LoginForm;
import org.societies.domainauthority.webapp.models.UserAdminForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;
import javax.validation.Valid;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.domainauthority.registry.DaRegistry;
import org.societies.domainauthority.registry.DaUserRecord;


/**
 * This class shows the example of annotated controller
 * 
 * @author Perumal Kuppuudaiyar
 * 
 */
@Controller
public class UserAdminController {

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
	 * 
	 * @return
	 */
	public UserAdminController() {

	}

	
	


	  
	/**
	 * This method get called when user request for login page by using url
	 * http://localhost:8080/societies/login.html
	 * 
	 * @return login jsp page and model object
	 */
	@RequestMapping(value = "/useradmin.html", method = RequestMethod.GET)
	public ModelAndView useradmin() {
		// model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		List<DaUserRecord> userRecords = daRegistry.getXmppIdentityDetails();
		UserAdminForm userForm = new UserAdminForm();
		userForm.setUserDetails(userRecords);
		
		model.put("userForm", userForm);
		model.put("userrecords", userRecords);
		
		/*
		 * return modelandview object and passing login (jsp page name) and
		 * model object as constructor
		 */
		return new ModelAndView("useradmin", model);
	}

	@RequestMapping(value = "/useradmin.html", method = RequestMethod.POST)
	public ModelAndView processLogin(@Valid UserAdminForm userForm,
			BindingResult result, Map model) {

		List<DaUserRecord> userRecords = daRegistry.getXmppIdentityDetails();
		
		// check was has changed!
		DaUserRecord currentDBRec = null;
		DaUserRecord updatedRec = null;
		boolean reload = false;
		for(int i=0; i<userRecords.size(); i++){
			boolean bUpdated = false;
			
			 currentDBRec = userRecords.get(i);
			 updatedRec = userForm.getUserDetails().get(i);
			 
			 if (!(currentDBRec.getHost().contentEquals(updatedRec.getHost())))
			{
				 currentDBRec.setHost(updatedRec.getHost());
				 bUpdated = true;
			}
			if (!(currentDBRec.getPort().contentEquals(updatedRec.getPort())))
			{
				 currentDBRec.setPort(updatedRec.getPort());
				 bUpdated = true;
			}			 
			if (!(currentDBRec.getStatus().contentEquals(updatedRec.getStatus())))
			{
				 currentDBRec.setStatus(updatedRec.getStatus());
				 bUpdated = true;
			}
			if (bUpdated)// changed
			{
				 daRegistry.updateXmppIdentityDetails(currentDBRec);
				 reload = true;
			}
		 }

		Map<String, Object> modelnew = new HashMap<String, Object>();
		if (reload)
			userRecords = daRegistry.getXmppIdentityDetails();
		UserAdminForm userFormNew = new UserAdminForm();
		userFormNew.setUserDetails(userRecords);
		
		modelnew.put("userForm", userFormNew);
		modelnew.put("userrecords", userRecords);
		
		/*
		 * return modelandview object and passing login (jsp page name) and
		 * model object as constructor
		 */
		return new ModelAndView("useradmin", modelnew);
	}
	
	
    @RequestMapping(value="/useradmin_old.html",method=RequestMethod.POST)
    public @ResponseBody JsonResponse UpdateUserAccount(
    		@ModelAttribute(value="userName") String userName,
    		@ModelAttribute(value="userHost") String userHost,
    		@ModelAttribute(value="userPort") String userPort,
    		@ModelAttribute(value="userStatus") String userStatus,
    		BindingResult result ){
    	JsonResponse res = new JsonResponse();
              if(!result.hasErrors()){
            	  DaUserRecord userDetails = new DaUserRecord();
            	  userDetails = daRegistry.getXmppIdentityDetails(userName);
            	  userDetails.setHost(userHost);
            	  userDetails.setPort(userPort);
            	  userDetails.setStatus(userStatus);
            	  daRegistry.updateXmppIdentityDetails(userDetails);
                  res.setStatus("SUCCESS");
                  res.setResult(userDetails);
            }else{
                    res.setStatus("FAIL");
                    //res.setResult(result.getAllErrors());
            }

            return res;
    }
    
	
}