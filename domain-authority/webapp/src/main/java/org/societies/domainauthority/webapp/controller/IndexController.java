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
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import org.apache.commons.codec.binary.Base64;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.directory.ICssDirectory;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.domainauthority.registry.DaRegistry;
import org.societies.domainauthority.registry.DaUserRecord;
import org.societies.domainauthority.webapp.models.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
	@Autowired
	ServletContext context;

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

		// Login and redirect to User Webapp
		String serializedPassword = toBytesString(password);
		String redirectUrlIndex = "http://"+userRecord.getHost()+":"+userRecord.getPort()+"/societies/index.xhtml";
		String redirectUrlLogin = "http://"+userRecord.getHost()+":"+userRecord.getPort()+"/societies/rest_login.xhtml?username="+userName+"&passworddigest="+serializedPassword+"&redirect=true";
		//		model.put("debugmsg", "Request:"+redirectUrlLogin+", Response:"+loginToUserWebapp(userRecord.getHost(), userRecord.getPort(), userName, password, false)+", Unserialized password:"+fromBytesString(serializedPassword));
		if (loginToUserWebapp(userRecord.getHost(), userRecord.getPort(), userName, password, false)) {
			return new ModelAndView("redirect:"+redirectUrlLogin);
		}
		model.put("errormsg", "Your are authenticated by the SOCIETIES Domain Authority. Unfortunately, sign in failed on your SOCIETIES webapp. Please, <a href=\""+redirectUrlIndex+"\">go to your SOCIETIES webapp</a> and sign in manually, or contact a SOCIETIES administrator.");
		return new ModelAndView("index", model);
	}

	private boolean loginToUserWebapp(String hostname, String port, String username, String password, boolean redirect) {
		StringBuffer data = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		String stringUrl = null;
		try {
			// NOTE: serializing the password is not sufficient. But the whole login system has to be refactored due to high security issues.
			String serializedPassword = toBytesString(password);
			stringUrl = "http://"+hostname+":"+port+"/societies/rest_login.xhtml?username="+username+"&passworddigest="+serializedPassword+"&redirect="+(redirect ? "true" : "false");
			URL url = new URL(stringUrl.trim()); 
			URLConnection conn = url.openConnection(); 
			conn.setDoOutput(true); 
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
			wr.write(data.toString()); 
			wr.flush(); 

			// Get the response 
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line; 
			while ((line = rd.readLine()) != null) {  
				sb.append(line);
			} 
			wr.close(); 
			rd.close();
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return "200".equals(sb.toString().trim());
	}

	private String toBytesString(String str) {
		StringBuilder sb = new StringBuilder();
		byte[] bytes = str.getBytes();
		for (int i=0; i<bytes.length; i++) {
			sb.append(bytes[i]);
			if ((i+1)!=bytes.length) {
				sb.append(":");
			}
		}
		return sb.toString();		
	}
	private String fromBytesString(String bytesStr) {
		String[] byteValues = bytesStr.split(":");
		byte[] bytes = new byte[byteValues.length];
		for (int i=0, len=bytes.length; i<len; i++) {
			bytes[i] = Byte.valueOf(byteValues[i].trim());     
		}
		return new String(bytes);
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

	@RequestMapping(value = "/download.html")
	public ModelAndView downloadInit() {
		Map<String, Object> model = new HashMap<String, Object>();
		// Retrieve current URL
		DaUserRecord adminUserRecord = new DaUserRecord(commManager.getIdManager().getThisNetworkNode().getIdentifier(), commManager.getIdManager().getThisNetworkNode().getIdentifier()+commManager.getIdManager().getThisNetworkNode().getDomain(), commManager.getIdManager().getThisNetworkNode().getDomain(), "50000", "active", "admin", "defaultpassword");
		List<DaUserRecord> userRecords = daRegistry.getXmppIdentityDetails();
		for(Iterator<DaUserRecord> it = userRecords.iterator(); it.hasNext();) {
			DaUserRecord current = it.next();
			if ("admin".equals(current.getUserType())) {
				adminUserRecord = current;
				break;
			}
		}
		// APK access
		String downloadPath = context.getContextPath()+"/download/";
		String downloadUrl = "http://"+adminUserRecord.getHost()+":"+adminUserRecord.getPort()+downloadPath;
		String societiesAndroidCommsAppFilename = "SocietiesAndroidCommsApp.apk";
		String societiesAndroidCommsAppPath = downloadPath+societiesAndroidCommsAppFilename;
		String societiesAndroidCommsAppUrl = downloadUrl+societiesAndroidCommsAppFilename;
		String societiesAndroidAppFilename = "SocietiesAndroidApp.apk";
		String societiesAndroidAppUrl = downloadUrl+societiesAndroidAppFilename;
		String societiesAndroidAppPath = downloadPath+societiesAndroidAppFilename;

		model.put("societiesAndroidCommsAppQrCodePath", getQrCodeDataUri(societiesAndroidCommsAppUrl));
		model.put("societiesAndroidCommsAppPath", societiesAndroidCommsAppPath);
		model.put("societiesAndroidAppQrCodePath", getQrCodeDataUri(societiesAndroidAppUrl));
		model.put("societiesAndroidAppPath", societiesAndroidAppPath);
		return new ModelAndView("download", model);	
	}

	private String getQrCodeDataUri(String data) {
		ByteArrayOutputStream  qrCodeData = QRCode.from(data).to(ImageType.PNG).withSize(250, 250).stream();
		return "data:image/png;base64,"+new String(Base64.encodeBase64(qrCodeData.toByteArray()));
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
			e.printStackTrace();
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