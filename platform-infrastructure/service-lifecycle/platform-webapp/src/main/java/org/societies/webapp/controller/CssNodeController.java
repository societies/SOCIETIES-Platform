package org.societies.webapp.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.comm.ICommManagerController;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.webapp.models.CssNodeForm;



@Controller
public class CssNodeController {
	
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


	@RequestMapping(value = "/cssnodemgmt.html", method = RequestMethod.GET)
	public ModelAndView cssManager() {

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		
		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
		CssNodeForm cnForm = new CssNodeForm();
		model.put("cnForm", cnForm);
		
		//ADD ALL THE SELECT BOX VALUES USED ON THE FORM
		Map<String, String> methods = new LinkedHashMap<String, String>();
		methods.put("addnode", "Add a Node");
		methods.put("removenode", "Remove a Node");
		methods.put("returnnodetype", "Find Node Type");
		//methods.put("removenode", "Remove a Node");
		model.put("methods", methods);
		
		model.put("cssnodemgmtResult", "CSS Node Mgmt Result :");
		return new ModelAndView("cssnodemgmt", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/cssnodemgmt.html", method = RequestMethod.POST)
	public ModelAndView cssManager(@Valid CssNodeForm cnForm,
			BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "CSS Node manager form error");
			return new ModelAndView("cssnodemgmt", model);
		}

		if (getCssLocalManager() == null) {
			model.put("errormsg", "CSS Manager reference not available");
			return new ModelAndView("error", model);
		}

		
		String method = cnForm.getMethod();
		CssRecord cssrecord = null;
		List<CssNode> cssNodes = new ArrayList<CssNode>();
		List<Service> nodes =  new ArrayList<Service>();
		Future<CssInterfaceResult> cssDetails = null; 
		cssDetails = getCssLocalManager().getCssRecord();
		try {
			cssrecord = cssDetails.get().getProfile();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String res = null;
		
		try {
		
			if (method.equalsIgnoreCase("addnode")) {
				res="Node Added";
				CssNode cssnode = new CssNode();
				cssnode.setIdentity(cnForm.getCssNodeId());
				cssnode.setStatus(cnForm.getcssNodeStatus());
				cssnode.setType(cnForm.getcssNodeType());
				
				cssNodes = cssrecord.getCssNodes();
				//cssNodes.add(0, cssnode);
				//cssrecord.setCssNodes(cssNodes);
				//cssLocalManager.registerCSSNode(cssrecord);


				cssLocalManager.setNodeType(cssrecord, cnForm.getCssNodeId(), cnForm.getcssNodeStatus(), cnForm.getcssNodeType());
				//asynchResult=this.cssLocalManager.registerCSSNode(cssrecord); 
				res="CSS Node Result ";
				
				//nodes = asynchResult.get();
				model.put("cssNodes", cssNodes);
				
			}else if (method.equalsIgnoreCase("removenode")) {
				
				
				res="Node Removed";
				CssNode cssnode = new CssNode();
				cssnode.setIdentity(cnForm.getCssNodeId());
				//cssnode.setStatus(cnForm.getcssNodeStatus());
				//cssnode.setType(cnForm.getcssNodeType());
				
				cssNodes = cssrecord.getCssNodes();
				
				//cssLocalManager.removeNode(cssrecord, cnForm.getCssNodeId());
				
				res="CSS Node Result ";
				
				//nodes = asynchResult.get();
				model.put("cssNodes", cssNodes);
					
				
				//model.put("services", nodes);
			
			}else if (method.equalsIgnoreCase("returnnodetype")) {
				Future<String> Type;
				Type = cssLocalManager.getthisNodeType();
				res="CSS Node Type Result ";
				
				model.put("cssNodes", Type);
					
			}else{
				res="error unknown metod";
			}
		
			model.put("result", res);
			
		}
		catch (Exception ex)
		{
			res = "Oops!!!! <br/>";
		};
		
		
		return new ModelAndView("cssnodemgmtresult", model);
		

	}

}