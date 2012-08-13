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
		Map<String, String> nodetypes = new LinkedHashMap<String, String>();
		Map<String, String> nodestatus = new LinkedHashMap<String, String>();
		Map<String, String> nodeinteractable = new LinkedHashMap<String, String>();
		
		
		
		methods.put("addnode", "Add a Node");
		methods.put("removenode", "Remove a Node");
		methods.put("returnnodetype", "Find Node Type");
				
		nodetypes.put("Android", "Android");
		nodetypes.put("Rich", "Rich");
		nodetypes.put("Cloud", "Cloud");
		
		nodestatus.put("Available", "Available");
		nodestatus.put("Unavailable", "Unavailable");
		nodestatus.put("Hibernating", "Hibernating");
		
		nodeinteractable.put("True", "True");
		nodeinteractable.put("False", "False");
		
		model.put("methods", methods);
		model.put("nodetypes", nodetypes);
		model.put("nodestatus", nodestatus);
		model.put("nodeinteractable", nodeinteractable);
		
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
		int Android = 0;
		int Cloud = 1;
		int Rich = 2;
		int nodeType = 0;
		int nodestates = 0;
		
		String nodetypes = cnForm.getnodetypes();
		String method = cnForm.getMethod();
		String nodestatus = cnForm.getnodestatus();
		String nodeinteractable = cnForm.getInteractable();
		CssRecord cssrecord = null;
		List<CssNode> cssNodes = new ArrayList<CssNode>();
		List<Service> nodes =  new ArrayList<Service>();
		Future<CssInterfaceResult> cssDetails = null; 
		
		//put getrecord back in here
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
				//cssnode.setStatus(cnForm.getcssNodeStatus());
				//cssnode.setType(cnForm.getcssNodeType());
				if (nodetypes.equalsIgnoreCase("Android")) {
					 nodeType = Android;
				} 
				
				if (nodetypes.equalsIgnoreCase("Rich")) {
					nodeType = Rich;
				} 
				
				if (nodetypes.equalsIgnoreCase("Cloud")) {
					nodeType = Cloud;
				}
				
				if (nodestatus.equalsIgnoreCase("Available")) {
					nodestates = 0;
				} 
				
				if (nodestatus.equalsIgnoreCase("Unavailable")) {
					nodestates = 1;
				} 
				
				if (nodestatus.equalsIgnoreCase("Hibernating")) {
					nodestates = 2;
				}
				cssNodes = cssrecord.getCssNodes();
				

				cssLocalManager.setNodeType(cssrecord, cnForm.getCssNodeId(), nodestates, nodeType, cnForm.getcssNodeMAC(), cnForm.getInteractable());
				
				res="CSS Node Result ";
				
				model.put("cssNodes", cssNodes);
				
			}else if (method.equalsIgnoreCase("removenode")) {
				
				
				res="Node Removed";
				CssNode cssnode = new CssNode();
				cssnode.setIdentity(cnForm.getCssNodeId());
				//cssnode.setStatus(cnForm.getcssNodeStatus());
				//cssnode.setType(cnForm.getcssNodeType());
				
				cssNodes = cssrecord.getCssNodes();
				
				cssLocalManager.removeNode(cssrecord, cnForm.getCssNodeId());
				
				res="CSS Node Removed ";
				
				//nodes = asynchResult.get();
				model.put("cssNodes", cssNodes);
					
				
				//model.put("services", nodes);
			
			}else if (method.equalsIgnoreCase("returnnodetype")) {
				Future<String> Type;
				Type = cssLocalManager.getthisNodeType(cnForm.getCssNodeId());
				//res="CSS Node Type Result ";
				res = "CSS Node Type is: " +Type.get().toString();
				//model.put("cssNodes", Type);
					
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