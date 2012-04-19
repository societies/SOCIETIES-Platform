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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.shindig.social.opensocial.model.Person;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
import org.societies.webapp.models.SocialDataForm;
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
	
	private static final String ADD				= "add";
	private static final String REMOVE			= "remove";
	private static final String FRIENDS			= "friends";
	private static final String PROFILES		= "profiles";
	private static final String GROUPS			= "groups";
	private static final String ACTIVITIES		= "activities";
	private static final String LIST			= "list";
	private static final String ID  	= "id";
	private static final String SNNAME  = "snName";
	private static final String TOKEN   = "token";
	
	/**
	 * OSGI service get auto injected
	 */
	
	

	
	public ISocialData getSocialData() {
		return socialdata;
	}
	
	public void getSocialData(ISocialData socialData) {
		this.socialdata = socialData;
	}

	@RequestMapping(value = "/socialdata.html", method = RequestMethod.GET)
	public ModelAndView SocialDataForm() {

		
		
		
		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Select a Social Newtork");
		
		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
		SocialDataForm sdForm = new SocialDataForm();
		model.put("sdForm", sdForm);
		
		//ADD ALL THE SELECT BOX VALUES USED ON THE FORM
		Map<String, String> methods = new LinkedHashMap<String, String>();
		methods.put(ADD, 		ADD);
		methods.put(REMOVE, 	REMOVE);
		methods.put(FRIENDS,    FRIENDS);
		methods.put(PROFILES,   PROFILES);
		methods.put(ACTIVITIES, ACTIVITIES);
		methods.put(GROUPS, 	GROUPS);
		methods.put(LIST, 	  	LIST);
		
		model.put("methods",  methods);
		
		Map<String, String> snName = new LinkedHashMap<String, String>();
		snName.put("FB", "Facebook");
		snName.put("TW", "Twitter");
		snName.put("FQ", "Foursquare");
		model.put(SNNAME, snName);
		
		model.put(TOKEN,  "");
		model.put(ID, 	"last update " + socialdata.getLastUpdate());
		return new ModelAndView("socialdata", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/socialdata.html", method = RequestMethod.POST)
	public ModelAndView serviceDiscovery(@Valid SocialDataForm sdForm, BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "Social Data Form error");
			return new ModelAndView("socialdata", model);
		}

		if (getSocialData() == null) {
			model.put("errormsg", "Social Data reference not avaiable");
			return new ModelAndView("error", model);
		}

		
		String method = sdForm.getMethod();
		String res		 = "This method is not handled yet";
		String content	 = " --- ";
		
		
			if (ADD.equalsIgnoreCase(method)) {
				
				// DO add Connectore HERE
				res       = "[" + method+"] Connector ";
				HashMap <String, String> params = new HashMap<String, String>();
				params.put(ISocialConnector.AUTH_TOKEN, sdForm.getToken());
				
				try {
					ISocialConnector con = socialdata.createConnector(sdForm.getSnName(), params);
					socialdata.addSocialConnector(con);
				} catch (Exception e) {
					res       = "Internal Error";
					content  = "<p> Unable to generate a connecotor with those parameters <p>";
					content  +="Error type is "+e.getMessage();
					content  += "<ul><li> Social Network:"+sdForm.getSnName()+"</li>";
					content  += "<li> Method:"+sdForm.getMethod() + "</li>";
					Iterator<String>  it = params.keySet().iterator();
					while(it.hasNext()){
						String k = it.next();
						content  += "<li>"+ k +": " +params.get(k)+"</li>";		
					}
					content  += "</ul>";
					e.printStackTrace();
				}
				
				//content   = "<b>Connector</b> ID:"+sdForm.getId() + " for " + sdForm.getSnName() +" with token: "+ sdForm.getToken() + "<br>";
					
			}
			else if (LIST.equalsIgnoreCase(method)) {
					
					// DO add Connectore HERE
					res       = "<h4>Connector List  </h4>";
					Iterator<ISocialConnector> it = socialdata.getSocialConnectors().iterator();
					
					content   = "<ul>";
					while (it.hasNext()){
					  ISocialConnector conn = it.next();
				  	  content   +="<li>" +conn.getConnectorName() +"- ID: "+conn.getID()+"</li>";
				  	  
					}
				    content+= "<br>";
						
			}
			else if (REMOVE.equalsIgnoreCase(method)) {
				
				// DO add Connectore HERE
				res       = "<h2> Removed Connector </h2>";
				if ("null".equals(sdForm.getId())){
					content = "<p> Please set a valid Connector ID</p>";
				}
				else {
					try {
						socialdata.removeSocialConnector(sdForm.getId());
						content   = "<p> Connector ID:"+sdForm.getId()+  "has been removed correctly</p>";
					} catch (Exception e) {
						res       = "Internal Error";
						content = "<p> Unable to remove this connector due to:</p>";
						content +="<h1>"+e.getMessage()+"</h1>";
						e.printStackTrace();
					}
					
				}
			}
			else if (FRIENDS.equalsIgnoreCase(method)) {
				
				// DO add Connectore HERE
				res       = "Social Friends";
				
				List<Person>friends = (List<Person>)socialdata.getSocialPeople();
				
				Iterator<Person> it = friends.iterator();
				content ="<h4> My Social Friends </h4>";
				content +="<ul>";
				while(it.hasNext()){
					
					//////// IN THIS PART YOU SHOULD PUT THE RIGHT CODE
					Person p= it.next();
					content +="<li>" + p.getName().getFormatted() + "</li>" ;
				}
				content   += "</ul>";
					
			}
			else if (PROFILES.equalsIgnoreCase(method)) {
				
				// DO add Connectore HERE
				res       = "Social Profiles";
				
				
				ArrayList<String> list = new ArrayList<String>();
				list.add("Luca Facebook Profile");
				Iterator<String> it = list.iterator();
				content ="<h4> My Social Profiles </h4>";
				content +="<ul>";
				while(it.hasNext()){
					
					//////// IN THIS PART YOU SHOULD PUT THE RIGHT CODE
					String s= it.next();
					content +="<li>" + s + "</li>" ;
				}
				content   += "</ul>";
					
			}
			else if (GROUPS.equalsIgnoreCase(method)) {
				
				// DO add Connectore HERE
				res       = "Social Groups";
				
				//List<?>friends = socialdata.getSocialPeople();
				ArrayList<String> list = new ArrayList<String>();
				list.add("SOCIETIES");
				list.add("Telecomitalia");
				list.add("Google");
				list.add("HOME SWEET HOME");
				Iterator<String> it = list.iterator();
				content ="<h4> My Social Groups </h4>";
				content +="<ul>";
				while(it.hasNext()){
					
					//////// IN THIS PART YOU SHOULD PUT THE RIGHT CODE
					String s= it.next();
					content +="<li>" + s + "</li>" ;
				}
				content   += "</ul>";
					
			}
			else if (ACTIVITIES.equalsIgnoreCase(method)) {
				
				// DO add Connectore HERE
				res       = "Social Activities";
				
				//List<?>friends = socialdata.getSocialPeople();
				ArrayList<String> list = new ArrayList<String>();
				list.add("X1 have done Y1 to Z1");
				list.add("X2 have done Y2 to Z2");
				content ="<h4> My Social Activities </h4>";
				content +="<ul>";
				Iterator<String> it = list.iterator();
				while(it.hasNext()){
					
					//////// IN THIS PART YOU SHOULD PUT THE RIGHT CODE
					String s= it.next();
					content +="<li>" + s + "</li>" ;
				}
				content   += "</ul>";
			}
			else {
				content = "<p> Request method:"+method+ " that is not yet implmented [TBD]</p>";
			}

		
			model.put("result_title", 	res);
			model.put("result_content", content);
			
		
		
		
		
		return new ModelAndView("socialdataresult", model);
		

	}
}