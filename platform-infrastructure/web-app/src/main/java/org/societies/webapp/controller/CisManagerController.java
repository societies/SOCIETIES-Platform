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
package org.societies.webapp.controller;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.ActivityFeedClient;
import org.societies.api.activity.IActivity;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.schema.activityfeed.Activityfeed;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.cis.mgmtClient.CisManagerClient;
import org.societies.webapp.models.AddActivityForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Describe your class here...
 *
 * @author tcarlyle
 */

@Controller
public class CisManagerController {

	
	@Autowired
	private ICisManager cisManager;
	@Autowired
	private ICommManager commMngrRef;
	
	
	private static Logger LOG = LoggerFactory.getLogger(CisManagerController.class);
	/**
	 *http://localhost:8080/societies/your_communities_list.html
	 */
	
	
	@RequestMapping(value="/your_communities_list.html",method = RequestMethod.GET)
	public ModelAndView yourCommunitiesListPage() {
		//model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please login to your Societies account");

		List<ICis> records = this.getCisManager().getCisList();
		model.put("cisrecords", records);
		
		return new ModelAndView("your_communities_list", model) ;
	}
	
	
	
	
	
	/*
	 * COMMUNITY PROFILE PAGE
	 * 
	 * 
	 * 
	 *
	 */
	
	@RequestMapping(value="/community_profile.html",method = RequestMethod.GET)
	public ModelAndView communityProfilePage(@RequestParam(value="cisId", required=true) String cisId){
		Map<String, Object> model = new HashMap<String, Object>();
		
		ICis icis = this.getCisManager().getCis(cisId);
		CisManagerClient getInfoCallback = new CisManagerClient();
		Requestor req = new Requestor(this.commMngrRef.getIdManager().getThisNetworkNode());
		icis.getInfo(req,getInfoCallback);
		
		// TODO, add null checks
		
		CommunityMethods getInfResp = new CommunityMethods();
		getInfResp = getInfoCallback.getComMethObj();
		model.put("cisInfo", getInfResp.getGetInfoResponse().getCommunity());
		
		//getInfResp.getGetInfoResponse().getCommunity().getOwnerJid()
		
		ActivityFeedClient activityFeedCallback = new ActivityFeedClient();
		icis.getActivityFeed().getActivities(0 + " " + System.currentTimeMillis(), activityFeedCallback);
		org.societies.api.schema.activityfeed.Activityfeed actFeedResponse = activityFeedCallback.getActivityFeed();
		model.put("activities",actFeedResponse.getGetActivitiesResponse().getActivity());
		
		//actFeedResponse.getGetActivitiesResponse().getActivity().get(0).ge
		AddActivityForm form = new AddActivityForm();
		model.put("activityForm",form);
		
		
		return new ModelAndView("community_profile", model) ;
	}

	
	// TODO: perhaps adapt this so it does not reload the full page
	//@SuppressWarnings("unchecked")
	@RequestMapping(value = "/community_profile.html", method = RequestMethod.POST)
	public ModelAndView addActivityInProfilePage(@Valid AddActivityForm addActForm,  BindingResult result, Map model){
		
		if(result.hasErrors()){
			model.put("acitivityAddError", "Error Adding Activity");
			return new ModelAndView("community_profile", model);
		}
		
		ICis icis = this.getCisManager().getCis(addActForm.getCisId());
		
		ActivityFeedClient activityFeedCallback = new ActivityFeedClient();
		
		IActivity act = icis.getActivityFeed().getEmptyIActivity();
		act.setActor(this.getCommMngrRef().getIdManager().getThisNetworkNode().getBareJid());
		act.setObject(addActForm.getObject());
		act.setVerb(addActForm.getVerb());
		act.setTarget(addActForm.getCisId());
		
		icis.getActivityFeed().addActivity(act, activityFeedCallback);
		LOG.info("add act");
		Activityfeed ac= activityFeedCallback.getActivityFeed();
		if(null != ac && ac.getAddActivityResponse().isResult()){
			return communityProfilePage(addActForm.getCisId());
		}
		else{
			model.put("acitivityAddError", "Error Adding Activity");
			return new ModelAndView("community_profile", model);
		}

			
	}
	
	
	
	// AUTOWIRING GETTERS AND SETTERS
	public ICisManager getCisManager() {
		return cisManager;
	}
	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}
	
	public ICommManager getCommMngrRef() {
		return this.commMngrRef;
	}
	
	
	public void setCommMngrRef(ICommManager commMngrRef) {
		this.commMngrRef = commMngrRef;
	}
}
