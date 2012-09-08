package org.societies.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssAdvertisementRecordDetailed;
import org.societies.cis.directory.client.CisDirectoryRemoteClient;
//import org.societies.webapp.models.CISDirectoryForm;
import org.societies.webapp.models.SuggestedFriendsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class CssSuggestedFriendsController {
	
	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	
	private ICSSLocalManager cssLocalManager;
	@Autowired
	private ICommManager commManager;
	@Autowired
	private IServiceDiscovery sdService;
	
	public IServiceDiscovery getSDService() {
		return sdService;
	}

	public void getSDService(IServiceDiscovery sdService) {
		this.sdService = sdService;
	}
	
	public ICSSLocalManager getCssLocalManager() {
		return cssLocalManager;
	}

	public void setCssLocalManager(ICSSLocalManager cssLocalManager) {
		this.cssLocalManager = cssLocalManager;
	}
	private ICisDirectoryRemote cisDirectoryRemote;

	
	
	public ICisDirectoryRemote getCisDirectoryRemote() {
		return cisDirectoryRemote;
	}

	public void setCisDirectoryRemote(ICisDirectoryRemote cisDirectoryRemote) {
		this.cisDirectoryRemote = cisDirectoryRemote;
	}

	@RequestMapping(value = "/suggestedfriends.html", method = RequestMethod.GET)
	public ModelAndView SuggestedFriends() {

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		
		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
		SuggestedFriendsForm sfForm = new SuggestedFriendsForm();
		model.put("sfForm", sfForm);
		
		//ADD ALL THE SELECT BOX VALUES USED ON THE FORM
		Map<String, String> methods = new LinkedHashMap<String, String>();
		
		methods.put("findFriends", "Find Friends");
		methods.put("sendfriendreq", "Send Friend Request");
		model.put("methods", methods);
		
		model.put("suggestedfriendsresult", "CSS Suggested Friends Result :");
		return new ModelAndView("suggestedfriends", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/suggestedfriends.html", method = RequestMethod.POST)
	public ModelAndView SuggestedFriends(@Valid SuggestedFriendsForm sfForm,
			BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "CSS Suggested Friends form error");
			return new ModelAndView("suggestedfriends", model);
		}

		if (getCssLocalManager() == null) {
			model.put("errormsg", "CSS Suggested Friends reference not avaiable");
			return new ModelAndView("error", model);
		}

		
		String method = sfForm.getMethod();
		
		String res = null;
		
		try {
		
			if (method.equalsIgnoreCase("findFriends")) {
				res="CSS Suggested Friends Result ";
				
				List<CssAdvertisementRecord> cssfriends = getCssLocalManager().suggestedFriends();
				
				model.put("result", res);
				model.put("cssfriends", cssfriends);
				
			}else{
				res="error unknown metod";
			}
		
			model.put("result", res);
			
		}
		catch (Exception e)
		{
			res = "Oops!!!!<br/>";
		};
		
		
		return new ModelAndView("suggestedfriendsresult", model);
	}
	

}
