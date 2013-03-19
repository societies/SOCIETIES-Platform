package org.societies.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.FriendFilter;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.webapp.models.CssNodeForm;
import org.societies.webapp.models.Friendfilterform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FriendfilterController {
	/**
	 * OSGI service get auto injected
	 */
	
	private static Logger LOG = LoggerFactory.getLogger(CssSuggestedFriendsController.class);
	
	@Autowired
	private ICSSInternalManager cssLocalManager;
	@Autowired
	private ICommManager commManager;
	@Autowired
	private IServiceDiscovery sdService;
	
	private CssSuggestedFriendsController friendcontroller;
	
	public CssSuggestedFriendsController getfriendcontroller(){
		return friendcontroller;
	}
	
	public void setfriendcontroller(){
		this.friendcontroller = friendcontroller;
	}
	
	public IServiceDiscovery getSDService() {
		return sdService;
	}

	public void getSDService(IServiceDiscovery sdService) {
		this.sdService = sdService;
	}
	
	public ICSSInternalManager getCssLocalManager() {
		return cssLocalManager;
	}

	public void setCssLocalManager(ICSSInternalManager cssLocalManager) {
		this.cssLocalManager = cssLocalManager;
	}


	@RequestMapping(value = "/friendfilter1.html", method = RequestMethod.GET)
	public ModelAndView cssManager() {

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		
		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
		Friendfilterform ffForm = new Friendfilterform();
		model.put("ffForm", ffForm);
		
		//ADD ALL THE SELECT BOX VALUES USED ON THE FORM
		Map<String, String> filters = new LinkedHashMap<String, String>();
		
		
		
		
		filters.put("facebook", "show facebook friends");
		filters.put("twitter", "show twitter friends");
		filters.put("linkedin", "show linkedin friends");
		filters.put("foursquare", "show foursquare friends");
		filters.put("googleplus", "show googleplus friends");
		filters.put("CISMember", "show CIS Members friends");
		filters.put("all", "show all friends");
		
	
		
		model.put("filters", filters);
		
		model.put("friendsfilterresult", "friends filter Mgmt Result :");
		return new ModelAndView("friendfilter1", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/friendfilter1.html", method = RequestMethod.POST)
	public ModelAndView cssManager(@Valid Friendfilterform ffForm,
			BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "friends filter manager form error");
			return new ModelAndView("friendfilter1", model);
		}

		if (getCssLocalManager() == null) {
			model.put("errormsg", "CSS Manager reference not available");
			return new ModelAndView("error", model);
		}
		int facebook =		0x0000000001;
		int twitter = 		0x0000000010;
		int linkedin =		0x0000000100;
		int foursquare = 	0x0000001000;
		int googleplus = 	0x0000010000;
		int all = 			0x0000011111;
		int CISMemeber = 	0x0000100000;
		
		int filterType = 0;
		
		FriendFilter filter = new FriendFilter();
		
		filter.setFilterFlag(facebook);
		
		String filters = ffForm.getfilters();
		
		String res = null;
		
		try {
				
				filter = new FriendFilter();
								
				if (filters.equalsIgnoreCase("facebook")) {
					filterType = facebook;
					filter.setFilterFlag(facebook);
					LOG.info("setting friend filter with filterflag as : " +filter.getFilterFlag());
					cssLocalManager.setFriendfilter(filter);
					
				} 
				
				if (filters.equalsIgnoreCase("twitter")) {
					filterType = twitter;
					filter.setFilterFlag(twitter);
					LOG.info("setting friend filter with filterflag as : " +filter.getFilterFlag());
					cssLocalManager.setFriendfilter(filter);
				} 
				
				if (filters.equalsIgnoreCase("linkedin")) {
					filterType = linkedin;
					filter.setFilterFlag(linkedin);
					LOG.info("setting friend filter with filterflag as : " +filter.getFilterFlag());
					cssLocalManager.setFriendfilter(filter);
				} 
				
				if (filters.equalsIgnoreCase("foursquare")) {
					filterType = foursquare;
					filter.setFilterFlag(foursquare);
					LOG.info("setting friend filter with filterflag as : " +filter.getFilterFlag());
					cssLocalManager.setFriendfilter(filter);
				} 
				
				if (filters.equalsIgnoreCase("googleplus")) {
					filterType = googleplus;
					filter.setFilterFlag(googleplus);
					LOG.info("setting friend filter with filterflag as : " +filter.getFilterFlag());
					cssLocalManager.setFriendfilter(filter);
				} 
				
				if (filters.equalsIgnoreCase("all")) {
					filterType = all;
					filter.setFilterFlag(all);
					LOG.info("setting friend filter with filterflag as : " +filter.getFilterFlag());
					cssLocalManager.setFriendfilter(filter);
				} 
				if (filters.equalsIgnoreCase("CISMember")) {
					filterType = CISMemeber;
					filter.setFilterFlag(CISMemeber);
					LOG.info("setting friend filter with filterflag as : " +filter.getFilterFlag());
					cssLocalManager.setFriendfilter(filter);
					
				}
				
				

								
				res="friends filter Result ";
				
				model.put("filters", filters);
				
		
		
			model.put("result", res);
			
		}
		catch (Exception ex)
		{
			res = "Oops!!!! <br/>";
		};
		
		
		return new ModelAndView("friendsfilterresult", model);
		

	}



}
