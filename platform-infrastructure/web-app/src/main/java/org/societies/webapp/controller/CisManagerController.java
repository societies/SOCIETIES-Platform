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



import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.ActivityFeedClient;
import org.societies.api.activity.IActivity;
import org.societies.api.cis.attributes.Rule.OperationType;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.remote.IPrivacyPolicyManagerRemote;
import org.societies.api.schema.activityfeed.Activityfeed;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.LeaveResponse;
import org.societies.cis.mgmtClient.CisManagerClient;
import org.societies.webapp.models.AddActivityForm;
import org.societies.webapp.models.AddMemberForm;
import org.societies.webapp.models.CreateCISForm;
import org.societies.webapp.models.MembershipCriteriaForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
	
	@Autowired
	private IPrivacyPolicyManager privacyPolicyManager;
	@Autowired
	private IPrivacyPolicyManagerRemote privacyPolicyManagerRemote;
	
	//IPrivacyPolicyManager
	
	private static Logger LOG = LoggerFactory.getLogger(CisManagerController.class);
	/**
	 *http://localhost:8080/societies/your_communities_list.html
	 */
	
	@RequestMapping(value="/create_community.html",method = RequestMethod.GET)
	public ModelAndView showCreateCommunityPage() {
		Map<String, Object> model = new HashMap<String, Object>();
		
		CreateCISForm form = new CreateCISForm();
		model.put("createCISform", form);
		
		List<OperationType> nonFormatedOpList = new ArrayList<OperationType>( Arrays.asList(OperationType.values() ) );
		List<String> operatorsList = new ArrayList<String>(nonFormatedOpList.size());
		for (int i=0; i<nonFormatedOpList.size(); i++) operatorsList.add(nonFormatedOpList.get(i).name());

		model.put("listOfOperators", operatorsList);
		
		Field[] f = CtxAttributeTypes.class.getDeclaredFields();
		List<String> attributes = new ArrayList<String>(f.length); 
		for (int i=0; i<f.length; i++) attributes.add(f[i].getName());
		model.put("listOfAttributes", attributes);
		
		//AutoPopulatingList<MembershipCriteriaForm> ruleArray = new AutoPopulatingList<MembershipCriteriaForm>(MembershipCriteriaForm.class);
		//ruleArray.add( new MembershipCriteriaForm(attributes.get(0), operatorsList.get(0),
		//	"","", true));
		//model.put("ruleArray",ruleArray);
		return new ModelAndView("create_community", model) ;
		
	}
	

		@RequestMapping(value = "/create_community.html", method = RequestMethod.POST)
		public ModelAndView createCommunityPost(@Valid CreateCISForm createCISform,  BindingResult result, Map model){
			
			if(result.hasErrors()){
				
				//model.put("acitivityAddError", "Error Adding Activity");
				return new ModelAndView("create_community", model) ;
			}
			String str = createCISform.getCisName();
			
			if( createCISform.getRuleArray().size() >0){
				for (MembershipCriteriaForm temp :  createCISform.getRuleArray()) {
					if(false == temp.isDeleted())
						str += ", " + temp.getAttribute();
				}
			}else{
				LOG.debug("empty rules");
				str += ", empty rules";
			}

			return yourCommunitiesListPage(str);
			//return new ModelAndView("create_community", model) ;
		}

	
	
	@RequestMapping(value="/your_communities_list.html",method = RequestMethod.GET)
	public ModelAndView yourCommunitiesListPage(@RequestParam(value="response", required=false) String incomingResponse) {
		//model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("response", incomingResponse);


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
	public ModelAndView communityProfilePage(@RequestParam(value="cisId", required=true) String cisId,@RequestParam(value="response", required=false) String response){
		Map<String, Object> model = new HashMap<String, Object>();
		
		
		// TODO, add null checks
		if(null!= response && false == response.isEmpty())
			model.put("response", response);
			
		// GET INFO
		ICis icis = this.getCisManager().getCis(cisId);
		CisManagerClient getInfoCallback = new CisManagerClient();
		Requestor req = new Requestor(this.commMngrRef.getIdManager().getThisNetworkNode());
		icis.getInfo(req,getInfoCallback);
		
		CommunityMethods res = 	getInfoCallback.getComMethObj();	
		Community getInfResp = res.getGetInfoResponse().getCommunity();
		model.put("cisInfo", getInfResp);
		

		// CHECK IF IF IM THE OWNER
		if(this.commMngrRef.getIdManager().getThisNetworkNode().getBareJid().equalsIgnoreCase(getInfResp.getOwnerJid()))
			model.put("isOwner", true);
		else
			model.put("isOwner", false);
		// GET PRIVACY
		RequestorCis requestor = null;
		RequestPolicy privacyPolicy = null;
		try {
			requestor = new RequestorCis(this.commMngrRef.getIdManager().fromJid(getInfResp.getOwnerJid())
					,this.commMngrRef.getIdManager().fromJid(getInfResp.getCommunityJid())
					);
			privacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestor);
			// GET POLICY
			if(null != privacyPolicy){
				// got policy locally
			}else{
				PrivPolCallBack privCallback = new PrivPolCallBack();
				this.getPrivacyPolicyManagerRemote().getPrivacyPolicy(requestor, this.commMngrRef.getIdManager().fromJid(getInfResp.getOwnerJid())
						, privCallback);
				privacyPolicy = privCallback.getPrivacyPolicy();
			}
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO: find the best way to display the policy
		//if( null != privacyPolicy)
		//	model.put("priacyPolicyString",privacyPolicy.toXMLString());
		//else
		//	model.put("priacyPolicyString","no policy");
		
		// GET ACIVITIES
		ActivityFeedClient activityFeedCallback = new ActivityFeedClient();
		icis.getActivityFeed().getActivities(0 + " " + System.currentTimeMillis(), activityFeedCallback);
		org.societies.api.schema.activityfeed.Activityfeed actFeedResponse = activityFeedCallback.getActivityFeed();
		model.put("activities",actFeedResponse.getGetActivitiesResponse().getActivity());
		
		//actFeedResponse.getGetActivitiesResponse().getActivity().get(0).ge
		AddActivityForm form = new AddActivityForm();
		model.put("activityForm",form);

		AddMemberForm form2 = new AddMemberForm();
		model.put("memberForm",form2);
		
		return new ModelAndView("community_profile", model) ;
	}

	
	// TODO: perhaps adapt this so it does not reload the full page
	//@SuppressWarnings("unchecked")
	@RequestMapping(value = "/add_activity_cis_profile_page.html", method = RequestMethod.POST)
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
			return communityProfilePage(addActForm.getCisId(),null);
		}
		else{
			model.put("acitivityAddError", "Error Adding Activity");
			return new ModelAndView("community_profile", model);
		}

			
	}
	
	
	@RequestMapping(value = "/add_member_cis_profile_page.html", method = RequestMethod.POST)
	public ModelAndView addMemberInProfilePage(@Valid AddMemberForm memberForm,  BindingResult result, Map model){
		
		if(result.hasErrors()){
			model.put("response", "Error Adding Member");
			return new ModelAndView("community_profile", model);
		}
		boolean operation = false;
		ICisOwned icis = this.getCisManager().getOwnedCis(memberForm.getCisJid());
		if(null != icis)
			if(icis.addMember(memberForm.getCssJid(), "participant"))
				operation = true;
		if(operation){
			return communityProfilePage(memberForm.getCisJid(),null);
		}
		else{
			model.put("acitivityAddError", "Error Adding Activity");
			return new ModelAndView("community_profile", model);
		}

			
	}
	
	//////////////////////// LEAVE COMMUNITY PAGE
	
	@RequestMapping(value="/leave_community.html",method = RequestMethod.GET)
	public ModelAndView leaveCommunity(@RequestParam(value="cisId", required=true) String cisId, Map model){
		
		
		// Leave
		CisManagerClient leaveCisCallback = new CisManagerClient();
		this.getCisManager().leaveRemoteCIS(cisId, leaveCisCallback);
		LeaveResponse l = leaveCisCallback.getComMethObj().getLeaveResponse();
		String response;
		if(l.isResult()){
			response = "You just left the CIS " + l.getCommunityJid();
		}else{
			response = "An error occurred when trying to leave the CIS " + cisId + ". Try again";
		}
	
		return yourCommunitiesListPage(response);
	}
	
		//////////////////////// DELETE COMMUNITY PAGE
	
	@RequestMapping(value="/delete_community.html",method = RequestMethod.GET)
	public ModelAndView deleteCommunity(@RequestParam(value="cisId", required=true) String cisId, Map model){
		
		
		// delete
		boolean ret = this.getCisManager().deleteCis(cisId);
		String response;
		if(ret){
			response = "You just deleted the CIS " + cisId;
		}else{
			response = "An error occurred when trying to delete the CIS " + cisId + ". Try again";
		}
	
		return yourCommunitiesListPage(response);
	}
	
	//////////////////////// DELETE COMMUNITY PAGE
	
@RequestMapping(value="/delete_member.html",method = RequestMethod.GET)
public ModelAndView deleteMember(@RequestParam(value="cisId", required=true) String cisId,@RequestParam(value="cssId", required=true) String cssId, Map model){
	
	boolean ret = false;
	ICisOwned i = this.getCisManager().getOwnedCis(cisId);
	if(null != i)
		ret = i.removeMemberFromCIS(cssId);
	String response;
	if(ret){
		response = "You just deleted the member " + cssId;
	}else{
		response = "An error occurred when trying to delete the member " + cssId + ". Try again";
	}

	return communityProfilePage(cisId,response);
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





	public IPrivacyPolicyManager getPrivacyPolicyManager() {
		return privacyPolicyManager;
	}





	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
	}





	public IPrivacyPolicyManagerRemote getPrivacyPolicyManagerRemote() {
		return privacyPolicyManagerRemote;
	}





	public void setPrivacyPolicyManagerRemote(
			IPrivacyPolicyManagerRemote privacyPolicyManagerRemote) {
		this.privacyPolicyManagerRemote = privacyPolicyManagerRemote;
	}
	
	
	// callbacks
	private class PrivPolCallBack implements IPrivacyPolicyManagerListener {
		private RequestPolicy privacyPolicy;

		private final long TIMEOUT = 50;
		private BlockingQueue<RequestPolicy> returnList;	
		
		public PrivPolCallBack(){
			returnList = new ArrayBlockingQueue<RequestPolicy>(1);
		}
		@Override
		public void onPrivacyPolicyRetrieved(RequestPolicy p) {
			this.privacyPolicy = p;
			insertComObjInQueue(this.privacyPolicy);
		}

		public RequestPolicy getPrivacyPolicy(){
			try {
				return returnList.poll(TIMEOUT, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		private void insertComObjInQueue(RequestPolicy obj){
			try {
				returnList.put(obj);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		@Override
		public void onOperationSucceed(String msg) {
			LOG.error("privCallback onOperationSucceed");

		}

		@Override
		public void onOperationCancelled(String msg) {
			LOG.error("privCallback onOperationCancelled");

		}

		@Override
		public void onOperationAborted(String msg, Exception e) {
			LOG.error("privCallback onOperationAborted: "+e.getMessage(), e);

		}
	}
	
}
