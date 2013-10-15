package org.societies.webapp.controller;

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
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.jfree.util.Log;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;
import org.societies.api.sns.Message;
import org.societies.platform.socialdata.SocialData;
import org.societies.webapp.models.SociaDataResultModel;
import org.societies.webapp.models.SocialDataForm;
import org.societies.webapp.models.SocialDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SocialDataController {

	@Autowired
	private ISocialData socialdata;
	private static final Logger logger = LoggerFactory.getLogger(SocialDataController.class);
	OAuthService service;

	private static final String ADD = "add";
	private static final String REMOVE = "remove";
	private static final String FRIENDS = "friends";
	private static final String PROFILES = "profiles";
	private static final String GROUPS = "groups";
	private static final String ACTIVITIES = "activities";
	private static final String UPDATE = "update";
	private static final String POST_MSG = "postMessage";
	//private static final String CONNECT_TW  = "connect_tw";
	//private static final String CONNECT_FQ	= "connect_fq";
	//private static final String CONNECT_LK	= "connect_lk";
	//private static final String CONNECT_FB	= "connect_fb";

	private static final String LIST = "list";
	//private static final String ID = "id";
	//private static final String SNNAME = "snName";
	//private static final String TOKEN = "token";
	//private String lastUpdate = "-- NA -- ";

	public ISocialData getSocialData() {
		return socialdata;
	}

	public void getSocialData(ISocialData socialData) {
		this.socialdata = socialData;

	}

	

	/*private String getIcon(String data) {

		if (data == null)
			return "images/social_network.png";
		if (data == "")
			return "images/social_network.png";

		if (data.contains("facebook"))
			return "images/Facebook.png";
		else if (data.contains("twitter"))
			return "images/Twitter.jpg";
		else if (data.contains("linkedin"))
			return "images/Linkedin.png";
		else if (data.contains("foursquare"))
			return "images/Foursquare.png";
		else
			return "images/social_network.png";
	}*/

	/*private String getBaseURL(String data, String id) {
		if (data == null)
			return "#";
		if (data == "")
			return "#";

		data = data.toLowerCase();
		if (data.contains("facebook"))
			return "http://facebook.com/" + id;
		else if (data.contains("twitter"))
			return "http://api.twitter.com/1/users/lookup.json?user_id=" + id;
		else if (data.contains("linkedin"))
			return "http://linkedin.com/" + id;
		else if (data.contains("foursquare"))
			return "http://foursquare.com/" + id;
		else
			return "#";
	}*/


	@RequestMapping(value = "/socialdata.html", method = RequestMethod.GET)
	public ModelAndView SocialDataForm(HttpServletRequest request) {

		
		SocialDataModel model = new SocialDataModel(socialdata);
		return new ModelAndView("socialdata","model",model);
	}

	/*private String getSNIcon(ISocialConnector conn) {
		try {
			if (conn.getConnectorName().equalsIgnoreCase("facebook"))
				return "images/Facebook.png";
			else if (conn.getConnectorName().equalsIgnoreCase("twitter"))
				return "images/Twitter.jpg";
			else if (conn.getConnectorName().equalsIgnoreCase("linkedin"))
				return "images/Linkedin.png";
			else if (conn.getConnectorName().equalsIgnoreCase("foursquare"))
				return "images/Foursquare.png";
			else
				return "images/social_network.png";
		} catch (Exception ex) {
		}
		return "images/social_network.png";

	}*/

	private SocialNetwork getSocialNetowkName(String name) {

		if ("facebook".equalsIgnoreCase(name))
			return SocialNetwork.FACEBOOK;
		if ("FB".equalsIgnoreCase(name))
			return SocialNetwork.FACEBOOK;
		if ("twitter".equalsIgnoreCase(name))
			return SocialNetwork.TWITTER;
		if ("TW".equalsIgnoreCase(name))
			return SocialNetwork.TWITTER;
		if ("foursquare".equalsIgnoreCase(name))
			return SocialNetwork.FOURSQUARE;
		if ("FQ".equalsIgnoreCase(name))
			return SocialNetwork.FOURSQUARE;
		if ("linkedin".equalsIgnoreCase(name))
			return SocialNetwork.LINKEDIN;
		if ("LK".equalsIgnoreCase(name))
			return SocialNetwork.LINKEDIN;
		if ("LN".equalsIgnoreCase(name))
			return SocialNetwork.LINKEDIN;
		if ("googleplus".equalsIgnoreCase(name))
			return SocialNetwork.GOOGLEPLUS;
		if ("G+".equalsIgnoreCase(name))
			return SocialNetwork.GOOGLEPLUS;

		return null;
	}

	@RequestMapping(value = "/socialdata.html", method = RequestMethod.POST)
	public ModelAndView executeAction(@Valid SocialDataForm sdForm,
			BindingResult result, /*Map model,*/ HttpServletResponse response,HttpServletRequest request) {

		logger.debug("Sono dentro la POST");
		SociaDataResultModel model = new SociaDataResultModel();

		/*if (result.hasErrors()) {

			model.put("lastupdate", lastUpdate);
			model.put("result", "Social Data Form error");
			return new ModelAndView("socialdata", model);
		}

		if (getSocialData() == null) {
			model.put("lastupdate", lastUpdate);
			model.put("errormsg", "Social Data reference not avaiable");
			return new ModelAndView("error", model);
		}*/

		String method = sdForm.getMethod();
		//String res = "This method is not handled yet";
		//String content = " --- ";
		logger.debug("Method:" + method);

		if (ADD.equalsIgnoreCase(method)) {

			//			logger.debug("Enter method ADD:");
			//
			//			// DO add Connectore HERE
			//			res = "[" + method + "] new Social Connector ";
			//			HashMap<String, String> params = new HashMap<String, String>();
			//			params.put(ISocialConnector.AUTH_TOKEN, sdForm.getToken());
			//
			//			String error = "";
			//			try {
			//
			//				ISocialConnector con = socialdata.createConnector(getSocialNetowkName(sdForm.getSnName()), params);
			//				error = "We are not able to create " + con.getConnectorName()
			//						+ " connector!";
			//				socialdata.addSocialConnector(con);
			//
			//				content = "<b>Connector</b> ID:" + sdForm.getId() + " for "
			//						+ sdForm.getSnName() + " with token: "
			//						+ sdForm.getToken() + "<br>";
			//				model.put("sdForm", sdForm);
			//
			//				Iterator<ISocialConnector> it = socialdata
			//						.getSocialConnectors().iterator();
			//				String connLI = "";
			//
			//				while (it.hasNext()) {
			//					ISocialConnector conn = it.next();
			//
			//					connLI += "<li><img src='" + getSNIcon(conn) + "'> "
			//							+ conn.getConnectorName()
			//							+ " <a href=\"#\" onclick=\"disconnect('"
			//							+ conn.getID()
			//							+ "');\">Click here to disconnect</a></li>";
			//
			//				}
			//
			//				socialdata.updateSocialData(); // this is required to read all
			//												// the SN Data.... (can take a
			//												// while).
			//				lastUpdate = dateFormat.format(new Date());
			//				model.put("lastupdate", lastUpdate);
			//				model.put("connectors", connLI);
			//				return new ModelAndView("socialdata", model);
			//			}
			//
			//			catch (Exception e) {
			//				res = "Internal Error";
			//				content = "<p> Unable to generate a connecotor with those parameters <p>";
			//				content += "Error type is " + error + " trace: "
			//						+ e.getMessage();
			//				content += "<ul><li> Social Network:" + sdForm.getSnName()
			//						+ "</li>";
			//				content += "<li> Method:" + sdForm.getMethod() + "</li>";
			//				Iterator<String> it = params.keySet().iterator();
			//				while (it.hasNext()) {
			//					String k = it.next();
			//					content += "<li>" + k + ": " + params.get(k) + "</li>";
			//				}
			//				content += "</ul>";
			//				e.printStackTrace();
			//			}

			//content ="Metodo non piu usato";

		} else if (UPDATE.equalsIgnoreCase(method)) {
			/*lastUpdate = dateFormat.format(new Date());
			socialdata.updateSocialData();

			Iterator<ISocialConnector> it = socialdata.getSocialConnectors()
					.iterator();
			String connLI = "";
			while (it.hasNext()) {
				ISocialConnector conn = it.next();
				connLI += "<li><img src='" + getSNIcon(conn) + "'> "
						+ conn.getConnectorName()
						+ " <a href=\"#\" onclick=\"disconnect('"
						+ conn.getID()
						+ "');\">Click here to disconnect</a></li>";

			}

			model.put("lastupdate", lastUpdate);
			model.put("connectors", connLI);*/
		}
		// This should be deprecated
		else if (LIST.equalsIgnoreCase(method)) {
			/*
			// DO add Connectore HERE
			res = "<h4>Connector List  </h4>";
			Iterator<ISocialConnector> it = socialdata.getSocialConnectors()
					.iterator();

			content = "<ul>";
			while (it.hasNext()) {
				ISocialConnector conn = it.next();
				content += "<li>" + conn.getConnectorName() + "- ID: "
						+ conn.getID() + "</li>";

			}
			content += "<br>";
*/
		} else if (REMOVE.equalsIgnoreCase(method)) {
			try{
				logger.debug("removing social network: "+sdForm.getId());
    			socialdata.removeSocialConnector(sdForm.getId());
    			SocialDataModel model2 = new SocialDataModel(socialdata);
    			return new ModelAndView("socialdata", "model",model2);
			} catch(Exception e){
				e.printStackTrace();
			}

		}  else if (PROFILES.equalsIgnoreCase(method)) {
/*
			// DO add Connectore HERE
			res = "<a href='socialdata.html'> Back to my Social Area </a>";

			List<Person> list = (List<Person>) socialdata.getSocialProfiles();

			content = "<h2> My Social Profiles </h2>";
			content += "<ul>";
			for (Person p : list) {

				String link = "";
				String img = " --- ";
				String domain = "";
				try {

					if (p.getAccounts() != null) {
						if (p.getAccounts().size() > 0) {
							Account account = p.getAccounts().get(0);
							if (account.getDomain() != null)
								domain = account.getDomain();
						}
					}
					String name = p.getId();
					if (p.getName() != null) {
						if (p.getName().getFormatted() != null)
							name = p.getName().getFormatted();
					} else if (p.getNickname() != null)
						name = p.getNickname();

					String id = p.getId();
					if (p.getId().contains(":")) {
						id = p.getId().split(":")[1];
					}



					img = "<img width='20px' src='" + getIcon(p.getId()) + "'>";
					link = "<a href='" + getBaseURL(domain, id) + "'>" + name
							+ "</a>";

					content += "<li> " + img + link + "</li>";
				} catch (Exception ex) {
					ex.printStackTrace();
					content += "<li> " + img + link + "</li>";
				}

			}
			content += "</ul>";*/

		} else if (FRIENDS.equalsIgnoreCase(method)) {
			model.setFiendsList(socialdata);

		} else if (GROUPS.equalsIgnoreCase(method)) {
			model.setGroupList(socialdata);
			
		} else if (ACTIVITIES.equalsIgnoreCase(method)) {
			model.setActivityList(socialdata);
			
		} else if (POST_MSG.equalsIgnoreCase(method)) {
			
			logger.debug("social Network strings: "+ sdForm.getSnName());
			logger.debug("messsage do send: "+sdForm.getParams());
			String[] sns = sdForm.getSnName().split("\\|");
			for(String social : sns){
				if(!social.endsWith("all")){
					logger.debug("name to transform: "+social +" --> "+social.replaceAll("ck_", ""));
					SocialNetwork socialNetowkName = getSocialNetowkName(social.replaceAll("ck_", ""));
					logger.debug("SocialNetworkName found: "+socialNetowkName);
					if(socialNetowkName != null){
    					Message message = new Message();
    					message.setData(sdForm.getParams());
    					logger.debug("Social Network: "+socialNetowkName.name()+" trying to post:\""+message.getData()+"\"");
    					socialdata.postMessage(socialNetowkName, message);
					}
				}
			}
			
			SocialDataModel model2 = new SocialDataModel(socialdata);
			return new ModelAndView("socialdata", "model",model2);
			
		} else if (method.contains("connect_")) {
			try {
				
				String path =  "http://" + request.getServerName() + ":" +request.getServerPort()  +  request.getContextPath();
				String sn_name= method.replace("connect_", "");
				response.sendRedirect("http://societies.lucasimone.eu/connect.php?sn="+sn_name+"&from="+ path + "/doConnect2.html");
			} 
			catch (IOException e) {
				logger.error("IO Exception LK:", e);
				e.printStackTrace();
			}

		} else {

			//content = "<p>Method:" + method + " NOT IMPLEMENTED/p>";
		}		

		//model.put("lastupdate", lastUpdate);
		//model.put("result_title", res);
		//model.put("result_content", content);

		return new ModelAndView("socialdataresult", "model",model);
		//return new ModelAndView("socialdataresult", model);

	}

	




	/**
	 * Query social data to check the 
	 * @return HTML Data with the list of available connectors
	 *
	private String getConnectorsHTML() {
		// read list of CONNECTOR

		Iterator<ISocialConnector> it = socialdata.getSocialConnectors().iterator();
		String connLI = "";

		while (it.hasNext()) {
			ISocialConnector conn = it.next();
			conn.getConnectorName();
			connLI += "<li><img src='" + getSNIcon(conn) + "'> "
					+ conn.getConnectorName()
					+ " <a href=\"#\" onclick=\"disconnect('" + conn.getID()
					+ "');\">Click here to disconnect</a></li>";

		}
		return connLI;
	}
*/


	@RequestMapping(value = "/doConnect2.html", method = RequestMethod.GET)
	public ModelAndView doConnect2(HttpServletRequest request) {

		logger.debug("Entering  /doConnect2.html");
		logger.debug("params: ");
		String socialNetworkType 	=request.getParameter("type");
		String token 				=request.getParameter("token");
		logger.debug("type: "+socialNetworkType);
		logger.debug("token: "+token);
		

		//		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		//		
		//		String socialNetworkType 	=request.getParameter("type");
		//		String token 				=request.getParameter("token");
		//		
		//		
		//		Map<String, Object> model = new HashMap<String, Object>();
		//		lastUpdate = dateFormat.format(new Date());
		//		model.put("lastupdate", lastUpdate);			
		//		SocialDataForm sdForm = new SocialDataForm();
		//		
		//		sdForm.setToken(token);
		//		sdForm.setMethod(ADD);
		//		sdForm.setSnName(socialNetworkType);
		//		HashMap<String, String> params = new HashMap<String, String>();
		//		params.put(ISocialConnector.AUTH_TOKEN, sdForm.getToken());
		//		sdForm.setSnName(socialNetworkType);
		//		
		//		String res = "Unable to add the "+socialNetworkType+ " Connector";
		//		String content = " --- ";
		//		String error = "";
		try {

			HashMap<String, String> params = new HashMap<String, String>();
			params.put(ISocialConnector.AUTH_TOKEN, token);

			//ISocialConnector con = socialdata.createConnector(getSocialNetowkName(sdForm.getSnName()), params);
			ISocialConnector con = socialdata.createConnector(getSocialNetowkName(socialNetworkType), params);
			//error = "We are not able to create " + con.getConnectorName() + " connector!";

			socialdata.addSocialConnector(con);
			socialdata.updateSocialData();
			//				content = "<b>Connector</b> ID:" + sdForm.getId() + " for "
			// 						+ sdForm.getSnName() + " with token: "
			//						+ sdForm.getToken() + "<br>";
			//				model.put("sdForm", sdForm);
		}
		catch (Exception ex) {
			logger.error(" Errore creazione token :", ex);
		}

		//		socialdata.updateSocialData(); // this is required to read all the SN
		//		model.put("connectors", getConnectorsHTML());
		//		model.put("result_title", res);

		SocialDataModel model_new = new SocialDataModel(socialdata);

		return new ModelAndView("socialdata","model", model_new);

	}
}