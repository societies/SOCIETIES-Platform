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
import java.util.Map;
import java.util.List;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.directory.ICssDirectory;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.domainauthority.webapp.models.LoginForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;
import javax.validation.Valid;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.societies.domainauthority.registry.DaRegistry;
import org.societies.domainauthority.registry.DaUserRecord;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Just a new TEMP controller for the pilot 
 * 
 * @author Maria
 * 
 */
@Controller
public class PilotController {

	private static final String OPENFIRE_PLUGIN = "http://%s:9090/plugins/societies/societies";
	private Map<String, String> domains = new LinkedHashMap<String, String>();
	private String xmppDomain;
	
	@Autowired
	private ICommManager commManager;
	@Autowired
	DaRegistry daRegistry;
	@Autowired
	ICssDirectory cssDir;
	
	public ICssDirectory getCssDir() { return cssDir; }
	public void setCssDir(ICssDirectory cssDir) { this.cssDir = cssDir; }
	
	public ICommManager getCommManager() { return commManager; }
	public void setCommManager(ICommManager commManager) { this.commManager = commManager; }

	/**
	 * Displays login Page
	 * 
	 * @return
	 */
	public PilotController() {
		
	}

	/**
	 * This method get called when user request for login page by using url
	 * http://localhost:8080/societies/login.html
	 * 
	 * @return login jsp page and model object
	 */
	@RequestMapping(value = "/pilot.html", method = RequestMethod.GET)
	public ModelAndView init() {
		//GET XMPP SERVER DOMAIN
		xmppDomain = commManager.getIdManager().getThisNetworkNode().getDomain();
		domains.put(xmppDomain, xmppDomain);
		
		// For the distribution ( 0.4) we will create a XCManager user login
		List<DaUserRecord> userRecords = daRegistry.getXmppIdentityDetails();
		
		boolean bDefaultAccFound = false;
		if ((userRecords != null) && (userRecords.size() > 0))
		{
			for ( int i = 0; i < userRecords.size(); i++)
			{
				if (userRecords.get(i).getUserType() != null)
				{
					if (userRecords.get(i).getName().contentEquals("XCManager"))
					{
						bDefaultAccFound = true;
						i = userRecords.size();
					}
				}
			}
		}
		
		if (bDefaultAccFound == false)
		{
			// create default admin account
			DaUserRecord defaultRecord = new DaUserRecord();
			defaultRecord.setName("XCManager");
			defaultRecord.setPassword("");
			defaultRecord.setUserType("user");
			defaultRecord.setStatus("active");
			defaultRecord.setHost("localhost");
			defaultRecord.setPort("8080");
			defaultRecord.setId("XCManager.societies.local");
			daRegistry.addXmppIdentityDetails(defaultRecord);
		}
		
		// model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		// data model object to be used for displaying form in html page
		LoginForm loginForm = new LoginForm();
		loginForm.setMethod("login");
		model.put("loginForm", loginForm);
		model.put("domains", domains);
		/*
		 * return modelandview object and passing login (jsp page name) and
		 * model object as constructor
		 */
		return new ModelAndView("pilot", model);
	}

	/**
	 * This method get called when user submit the login page using submit
	 * button
	 * 
	 * @param loginForm
	 *            java object with data entered by user
	 * @param result
	 *            boolean result to check the data binding with object
	 * @param model
	 *            Map object passed to login page.
	 * @return loginsuccess page if sucess or login page for retry if failed
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/pilot.html", method = RequestMethod.POST)
	public ModelAndView processLogin(@Valid LoginForm loginForm, BindingResult result,  Map model) {

		//GET XMPP SERVER DOMAIN
		xmppDomain = commManager.getIdManager().getThisNetworkNode().getDomain();
		domains.put(xmppDomain, xmppDomain);
		model.put("domains", domains);
		if (result.hasErrors()) {
			model.put("result", "Login form error");
			return new ModelAndView("login", model);
		}
		//FORM VALUES
		String userName = loginForm.getUserName();
		String password = loginForm.getPassword();
		String Name = loginForm.getName();
		String subDomain = loginForm.getSubDomain();
		String method = loginForm.getMethod();
		
	
		////////////////// LOGIN ////////////////
		
			// AUTHENTICATE JABBER ID
			Map<String, String> params = new LinkedHashMap<String, String>();
			params.put("username", userName);
			params.put("password", password);
			params.put("secret", "defaultSecret");
			
			String xmppUrl = new String();
			xmppUrl = String.format(OPENFIRE_PLUGIN, xmppDomain);
			String resp = postData(MethodType.LOGIN, xmppUrl, params);
			try {
				if (resp.isEmpty())
				{
					model.put("loginError", "Error logging onto openfire Account. Please try again");
					return new ModelAndView("pilot", model);
				}
				// CHECK RESPONSE - DOES ACCOUNT ALREADY EXIST
				Document respDoc = loadXMLFromString(resp);
				if (respDoc.getDocumentElement().getNodeName().equals("error")) {
					model.put("loginError", "Username/password incorrect. Please try again");
					return new ModelAndView("pilot", model);
				}
			} catch (Exception e) {
				e.printStackTrace();
				model.put("loginError", "Error logging onto openfire Account. Please try again");
				return new ModelAndView("pilot", model);
			}
		
			//GET CONTAINER INFO
			DaUserRecord userRecord = new DaUserRecord();
			userRecord = daRegistry.getXmppIdentityDetails(userName);
			
			if ((userRecord != null) && (userRecord.getStatus() != null))
			{
				if (!userRecord.getStatus().contentEquals("active") 
					|| userRecord.getHost().isEmpty() 
					|| userRecord.getPort().isEmpty()) 
				{
					//CLOUD NODE NOT SETUP YET
					model.put("loginError", "Cloud node creation not completed");
					return new ModelAndView("pilot", model);
				}
			} else { // account doesn't exist
				//CLOUD NODE NOT SETUP YET
				model.put("loginError", "User Acount Not Found");
				return new ModelAndView("pilot", model);
			}
			
			// GET SERVER/PORT NUMBER FROM REGISTRY
			String redirectUrl = new String();
			redirectUrl = String.format("http://%s:%s/societies/%s/loginviada.html", userRecord.getHost(), userRecord.getPort(), userRecord.getId());
			model.put("webappurl", redirectUrl);
			model.put("name", userName);	
			return new ModelAndView("loginsuccess", model);
	
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
}