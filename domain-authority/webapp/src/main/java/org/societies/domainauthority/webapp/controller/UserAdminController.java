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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.comm.ICommManagerController;
import org.societies.domainauthority.registry.DaRegistry;
import org.societies.domainauthority.registry.DaUserRecord;
import org.societies.domainauthority.webapp.models.LoginForm;
import org.societies.domainauthority.webapp.models.UserAdminForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes("suid")
public class UserAdminController {

	@Autowired
	private ICommManager commManager;
	@Autowired
	private ICommManagerController commManagerControl;
	@Autowired
	DaRegistry daRegistry;

	private String xmppDomain;


	public UserAdminController() {
	}
	@InitBinder
	public void init() {
		xmppDomain = commManager.getIdManager().getThisNetworkNode().getDomain();

		// Check to ensure that at least one admin account exists, if it doesn't create a default admin account
		List<DaUserRecord> userRecords = daRegistry.getXmppIdentityDetails();
		boolean bAdminfound = false;
		if ((userRecords != null) && (userRecords.size() > 0)) {
			for (int i = 0; i < userRecords.size(); i++) {
				if (userRecords.get(i).getUserType() != null) {
					if ("admin".contentEquals(userRecords.get(i).getUserType()))
						bAdminfound = true;
				}
			}
		}

		// Create default admin account
		if (!bAdminfound) {
			DaUserRecord adminRecord = new DaUserRecord();
			String username = commManager.getIdManager().getDomainAuthorityNode().getIdentifier();
			adminRecord.setName(username);
			adminRecord.setPassword("defaultpassword");
			adminRecord.setUserType("admin");
			adminRecord.setStatus("active");
			adminRecord.setHost(xmppDomain);
			adminRecord.setPort("50000");
			adminRecord.setId(username+"."+xmppDomain);
			daRegistry.addXmppIdentityDetails(adminRecord);
		}
	}

	@ModelAttribute("suid")
	public String populateHotelForm() {
		return "";
	}

	@RequestMapping(value = "/admin-logout.html", method = RequestMethod.GET)
	public ModelAndView adminLogout() {
		return adminlogin("");
	}

	@RequestMapping(value = "/admin.html", method = RequestMethod.GET)
	public ModelAndView adminlogin(@ModelAttribute("suid") String suid) {
		// Already logged
		Map<String, Object> model = new HashMap<String, Object>();
		if (null != suid && "logged".equals(suid)) {
			// Save
			//			model.put("debugmsg", "adminlogin: suid is not null");
			model.put("suid", suid);
			// Display user admin
			List<DaUserRecord> userRecords = daRegistry.getXmppIdentityDetails();
			UserAdminForm userForm = new UserAdminForm();
			userForm.setUserDetails(userRecords);
			model.put("userForm", userForm);
			model.put("userrecords", userRecords);
			Map<String, String> userTypes = new LinkedHashMap<String, String>();
			userTypes.put("user", "user");
			userTypes.put("admin", "admin");
			model.put("userTypes", userTypes);
			Map<String, String> userStatusTypes = new LinkedHashMap<String, String>();
			userStatusTypes.put("new", "new");
			userStatusTypes.put("active", "active");
			userStatusTypes.put("deleted", "deleted");
			model.put("userStatusTypes", userStatusTypes);
			return new ModelAndView("useradmin", model);
		}

		// Not logged
		//		model.put("debugmsg", "adminlogin: suid is null");
		UserAdminForm userForm = new UserAdminForm();
		model.put("loginForm", userForm);
		model.put("suid", "");
		return new ModelAndView("adminlogin", model);
	}

	@RequestMapping(value = "/useradmin.html", method = RequestMethod.GET)
	public ModelAndView useradmin(@ModelAttribute("suid") String suid) {
		return adminlogin(suid);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/admin.html", method = RequestMethod.POST)
	public ModelAndView processAdminLogin(@Valid LoginForm loginForm, BindingResult result, Map model, @ModelAttribute("suid") String suid) {
		// Already logged
		if (null != suid && "logged".equals(suid)) {
			return adminlogin(suid);
		}

		// -- Retrieve params
		String userName = loginForm.getUserName();
		String password = loginForm.getPassword();
		Map<String, String> currentValues = new LinkedHashMap<String, String>();
		currentValues.put("username", userName);
		currentValues.put("password", password);

		// -- Check params
		// Error
		if (result.hasErrors()) {
			model.put("errormsg", "Some errors occured.");
			model.putAll(currentValues);
			return new ModelAndView("adminlogin", model);
		}
		// Empty params
		if (null == userName || "".equals(userName)
				|| password == null || "".equals(password)) {
			model.put("errormsg", "Some required information are missing.");
			model.putAll(currentValues);
			return new ModelAndView("adminlogin", model);
		}

		// Check that the account exists
		DaUserRecord userRecord = daRegistry.getXmppIdentityDetails(userName);
		if (null == userRecord) {
			model.put("errormsg", "Incorrect user name or password, please try again.");//don't say to the user the account doesn't exist. 
			return new ModelAndView("adminlogin", model);
		}
		if (null == userRecord.getPassword() || !password.equals(userRecord.getPassword())) {
			model.put("errormsg", "Incorrect user name or password, please try again.");
			return new ModelAndView("adminlogin", model);
		}
		if (null == userRecord.getUserType() || "user".contentEquals(userRecord.getUserType())) {
			model.put("errormsg", "Access denied");
			return new ModelAndView("adminlogin", model);
		}

		// Login and redirect to useradmin
		return adminlogin("logged");
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/useradmin.html", method = RequestMethod.POST)
	public ModelAndView processLogin(@Valid UserAdminForm userForm, BindingResult result, Map model, @ModelAttribute("suid") String suid) {
		// Not logged
		if (null == suid || !"logged".equals(suid)) {
			//			model.put("debugmsg", "processLogin: suid is null");
			adminlogin(suid);
		}
		//		model.put("debugmsg", "processLogin: suid is not null: "+suid);

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
			if (!(currentDBRec.getUserType().contentEquals(updatedRec.getUserType())))
			{
				currentDBRec.setUserType(updatedRec.getUserType());
				bUpdated = true;
			}
			if (bUpdated)// changed
			{
				daRegistry.updateXmppIdentityDetails(currentDBRec);
				reload = true;
			}
		}


		//		Map<String, Object> modelnew = new HashMap<String, Object>();
		//		if (reload)
		//			userRecords = daRegistry.getXmppIdentityDetails();
		//		UserAdminForm userFormNew = new UserAdminForm();
		//		userFormNew.setUserDetails(userRecords);
		//
		//
		//		modelnew.put("userForm", userFormNew);
		//		modelnew.put("userrecords", userRecords);
		//
		//		Map<String, String> userTypes = new LinkedHashMap<String, String>();
		//		userTypes.put("user", "user");
		//		userTypes.put("admin", "admin");
		//		modelnew.put("userTypes", userTypes);
		//
		//		Map<String, String> userStatusTypes = new LinkedHashMap<String, String>();
		//		userStatusTypes.put("new", "new");
		//		userStatusTypes.put("active", "active");
		//		userStatusTypes.put("deleted", "deleted");
		//		modelnew.put("userStatusTypes", userStatusTypes);
		return new ModelAndView("redirect:admin.html");
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
}