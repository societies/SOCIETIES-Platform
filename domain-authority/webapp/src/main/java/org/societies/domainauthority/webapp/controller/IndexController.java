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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.directory.ICssDirectory;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.domainauthority.registry.DaRegistry;
import org.societies.domainauthority.registry.DaUserRecord;
import org.societies.domainauthority.webapp.models.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


@Controller
public class IndexController {
	private static final String OPENFIRE_PLUGIN = "http://%s:9090/plugins/societies/societies";
	private Map<String, String> domains = new LinkedHashMap<String, String>();
	private String xmppDomain;

	@Autowired
	private ICommManager commManager;
	@Autowired
	DaRegistry daRegistry;
	@Autowired
	ICssDirectory cssDir;

	public IndexController() {
	}
	@InitBinder
	public void init() {
		xmppDomain = commManager.getIdManager().getThisNetworkNode().getDomain();
		domains.put(xmppDomain, xmppDomain);
	}
	
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public ModelAndView signInInit() {
		Map<String, Object> model = new HashMap<String, Object>();
		LoginForm loginForm = new LoginForm();
		loginForm.setMethod("login");
		model.put("loginForm", loginForm);
		model.put("domains", domains);
		return new ModelAndView("index", model);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/index.html", method = RequestMethod.POST)
	public ModelAndView processSignIn(@Valid LoginForm loginForm, BindingResult result,  Map model) {
		model.put("domains", domains);

		// -- Retrieve params
		String userName = loginForm.getUserName();
		String password = loginForm.getPassword();
		String subDomain = loginForm.getSubDomain();
		Map<String, String> currentValues = new LinkedHashMap<String, String>();
		currentValues.put("subDomain", subDomain);
		currentValues.put("username", userName);
		currentValues.put("password", password);

		// -- Check params
		// Error
		if (result.hasErrors()) {
			model.put("errormsg", "Some errors occured.");
			model.putAll(currentValues);
			return new ModelAndView("signup", model);
		}
		// Empty params
		if (null == userName || "".equals(userName)
				|| password == null || "".equals(password)
				|| subDomain == null || "".equals(subDomain)) {
			model.put("errormsg", "Some required information are missing.");
			model.putAll(currentValues);
			return new ModelAndView("signup", model);
		}
		
		// -- AUTHENTICATE JABBER ID
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("username", userName);
		params.put("password", password);
		params.put("subDomain", subDomain);
		params.put("secret", "defaultSecret");

		String xmppUrl = new String();
		xmppUrl = String.format(OPENFIRE_PLUGIN, subDomain);
		String resp = postData(MethodType.LOGIN, xmppUrl, params);
		try {
			// Error: empty result
			if (resp.isEmpty()) {
				model.put("errormsg", "Error logging onto SOCIETIES account. Please try again."); 
				return new ModelAndView("index", model);
			}
			// Result: but error in result
			Document respDoc = loadXMLFromString(resp);
			if (respDoc.getDocumentElement().getNodeName().equals("error")) {
				model.put("errormsg", "Username or password incorrect, please try again.");
				return new ModelAndView("index", model);
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.put("errormsg", "Error logging onto SOCIETIES account. Please try again.");
			return new ModelAndView("index", model);
		}

		// -- GET CONTAINER INFO
		DaUserRecord userRecord = new DaUserRecord();
		userRecord = daRegistry.getXmppIdentityDetails(userName);

		if ((userRecord != null) && (userRecord.getStatus() != null)) {
			if (!userRecord.getStatus().contentEquals("active") 
					|| userRecord.getHost().isEmpty() 
					|| userRecord.getPort().isEmpty())  {
				//CLOUD NODE NOT SETUP YET
				model.put("errormsg", "Account creation not completed. Contact a SOCIETIES administrator to activate your account.");
				return new ModelAndView("index", model);
			}
		} else { // account doesn't exist
			//CLOUD NODE NOT SETUP YET
			model.put("errormsg", "User Acount Not Found");
			return new ModelAndView("index", model);
		}

		// GET SERVER/PORT NUMBER FROM REGISTRY
		String redirectUrl = "http://"+userRecord.getHost()+":"+userRecord.getPort()+"/societies/login.xhtml?username="+userName+"&passworddigest="+DigestUtils.md5DigestAsHex(userName.getBytes());
		return new ModelAndView("redirect:"+redirectUrl);

	}

	@RequestMapping(value = "/signup.html", method = RequestMethod.GET)
	public ModelAndView signupInit() {
		Map<String, Object> model = new HashMap<String, Object>();
		LoginForm signupForm = new LoginForm();
		model.put("loginForm", signupForm);
		model.put("domains", domains);
		return new ModelAndView("signup", model);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/signup.html", method = RequestMethod.POST)
	public ModelAndView processSignup(@Valid LoginForm signupForm, BindingResult result,  Map model) {
		model.put("domains", domains);

		// -- Retrieve params
		String userName = signupForm.getUserName();
		String password = signupForm.getPassword();
		String passwordConfirmation = signupForm.getPasswordConfirm();
		String Name = signupForm.getName();
		String subDomain = signupForm.getSubDomain();
		Map<String, String> currentValues = new LinkedHashMap<String, String>();
		currentValues.put("subDomain", subDomain);
		currentValues.put("name", Name);
		currentValues.put("username", userName);
		currentValues.put("password", password);
		currentValues.put("passwordConfirm", passwordConfirmation);

		// -- Check new account params
		// Error
		if (result.hasErrors()) {
			model.put("errormsg", "Some errors occured.");
			model.putAll(currentValues);
			return new ModelAndView("signup", model);
		}
		// Empty params
		if (null == userName || "".equals(userName)
				|| password == null || "".equals(password)
				|| subDomain == null || "".equals(subDomain)) {
			model.put("errormsg", "Some required information are missing.");
			model.putAll(currentValues);
			return new ModelAndView("signup", model);
		}
		// Password
		if ((password.contentEquals(passwordConfirmation)) == false) {
			//account doesn't exist, direct to new user page
			model.put("errormsg", "The password confirmation is different from the choosen password.");
			model.putAll(currentValues);
			return new ModelAndView("signup", model);
		}
		// Already exists
		String newUserUri = userName+"."+subDomain;
		try {
			List<CssAdvertisementRecord> existingAccounts = cssDir.findAllCssAdvertisementRecords().get();
			System.out.println("### Existing accounts: "+existingAccounts);
			if (null != existingAccounts && existingAccounts.size() > 0) {
				for(CssAdvertisementRecord record : existingAccounts) {
					if (newUserUri.equals(record.getUri())) {
						model.put("errormsg", "A such username already exists. Please select an other one.");
						model.putAll(currentValues);
						return new ModelAndView("signup", model);
					}
				}
			}
		} catch (Exception e) {
			// too bad... Let's continue to create the account, if it already exists the user will think it has created an account but it has not.
			e.printStackTrace();
		}

		// -- Create account
		// Create Openfire Account
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("username", userName);
		params.put("password", password);
		params.put("name", Name);
		params.put("secret", "defaultSecret");
		String xmppUrl = new String();
		xmppUrl = String.format(OPENFIRE_PLUGIN, xmppDomain);
		String resp = postData(MethodType.ADD, xmppUrl, params);
		try {
			// CHECK RESPONSE - DOES ACCOUNT ALREADY EXIST
			Document respDoc = loadXMLFromString(resp);
			if (respDoc.getDocumentElement().getNodeName().equals("error")) {
				System.out.println("IndexController::processLogin: can't the account on Openfire.");
				model.put("infomsg", "The account has not been created on Openfire. It may already exist.");
				//					return new ModelAndView("index", model);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Create account in da node
		String jid = userName + "." + subDomain;
		DaUserRecord userRecord = new DaUserRecord();
		userRecord.setName(userName);
		userRecord.setId(jid);
		userRecord.setHost("");
		userRecord.setPort("");
		userRecord.setStatus("new");
		userRecord.setUserType("user");
		userRecord.setPassword(password);
		daRegistry.addXmppIdentityDetails(userRecord);
		CssAdvertisementRecord cssAdvert = new CssAdvertisementRecord();
		cssAdvert.setId(jid);
		cssAdvert.setUri(jid);
		cssAdvert.setName(Name);
		cssDir.addCssAdvertisementRecord(cssAdvert);

		model.put("result", "Account Created - Your cloud node will be created by a SOCIETIES administrator in next 24 hours.");
		return new ModelAndView("signup", model);
	}

	private static String postData(MethodType method, String openfireUrl, Map<String, String> params) {
		try { 
			StringBuffer data = new StringBuffer();
			for(String s : params.keySet()) {
				String tmp = URLEncoder.encode(s, "UTF-8") + "=" + URLEncoder.encode((String)params.get(s), "UTF-8") +  "&";
				data.append(tmp);
			}
			//ADD METHOD
			String methodStr = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(method.toString().toLowerCase(), "UTF-8");
			data.append(methodStr);

			// Send data 
			URL url = new URL(openfireUrl); 
			URLConnection conn = url.openConnection(); 
			conn.setDoOutput(true); 
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
			wr.write(data.toString()); 
			wr.flush(); 

			// Get the response 
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line; 
			while ((line = rd.readLine()) != null) {  
				sb.append(line);
			} 
			wr.close(); 
			rd.close();

			//RESPONSE CODE
			return sb.toString();

		} catch (Exception e) { 

		}
		return ""; 
	}

	private Document loadXMLFromString(String xml) throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

	private enum MethodType {
		ADD,
		DELETE,
		ENABLE,
		DISABLE,
		UPDATE,
		LOGIN;
	}
	
	public ICssDirectory getCssDir() { return cssDir; }
	public void setCssDir(ICssDirectory cssDir) { this.cssDir = cssDir; }

	public ICommManager getCommManager() { return commManager; }
	public void setCommManager(ICommManager commManager) { this.commManager = commManager; }
}