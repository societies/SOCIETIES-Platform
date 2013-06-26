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
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.client.ActivityFeedClient;
import org.societies.api.activity.IActivity;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.attributes.Rule.OperationType;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisRemote;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.remote.IPrivacyPolicyManagerRemote;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.JoinResponse;
import org.societies.api.schema.cis.community.LeaveResponse;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.cis.directory.client.CisDirectoryRemoteClient;
import org.societies.cis.mgmtClient.CisManagerClient;
import org.societies.webapp.models.AddActivityForm;
import org.societies.webapp.models.AddMemberForm;
import org.societies.webapp.models.CisInfo;
import org.societies.webapp.models.CreateCISForm;
import org.societies.webapp.models.JoinCISForm;
import org.societies.webapp.models.MembershipCriteriaForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.societies.webapp.service.UserService;

/**
 * Describe your class here...
 *
 * @author tcarlyle
 */

@Controller
@SessionAttributes("cisAdverts")
public class CisManagerController {

	
	//@Autowired
	private ICisManager cisManager;
	//@Autowired
	private ICommManager commMngrRef;
	
	@Autowired
	private ICisDirectoryRemote cisDirectoryRemote;
	
	@Autowired
	private IPrivacyPolicyManager privacyPolicyManager;
	@Autowired
	private IPrivacyPolicyManagerRemote privacyPolicyManagerRemote;
	
	@Autowired
	UserService userService;
	
	//IPrivacyPolicyManager
	
	private static Logger LOG = LoggerFactory.getLogger(CisManagerController.class);
	/**
	 *http://localhost:8080/societies/your_communities_list.html
	 */
	
	
	// probably going to be deleted
	@RequestMapping(value="/manage_communities.html",method = RequestMethod.GET)
	public ModelAndView showManagerCommunitiesPage() {
		Map<String, Object> model = new HashMap<String, Object>();

		// Check if a user is already logged in
		if (getUserService().isUserLoggedIn() == false)
			return new ModelAndView("index", model); //TODO : return error string
		
		
		return new ModelAndView("manage_communities", model) ;
		
	}
	
	// SHOW CREATE COMMUNITY
	
	@RequestMapping(value="/create_community.html",method = RequestMethod.GET)
	public ModelAndView showCreateCommunityPage() {
		Map<String, Object> model = new HashMap<String, Object>();
		
		// Check if a user is already logged in
		if (getUserService().isUserLoggedIn() == false)
			return new ModelAndView("index", model); //TODO : return error string
		
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
		

		return new ModelAndView("create_community", model) ;
		
	}
	

	// CREATE COMMUNITY POST
	
		@RequestMapping(value = "/create_community.html", method = RequestMethod.POST)
		public ModelAndView createCommunityPost(@Valid CreateCISForm createCISform,  BindingResult result, Map model){
			
			// Check if a user is already logged in
			if (getUserService().isUserLoggedIn() == false)
				return new ModelAndView("index", model); //TODO : return error string
			
			String response = "model error creating community";
			
			if(result.hasErrors()){
				
				return new ModelAndView("create_community", model) ;
			}
			


			
			String cisName = createCISform.getCisName();
			String cisDescription = createCISform.getCisDescription();
			String cisType = createCISform.getCisType();
			
			// building the criteria
			Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria> ();
			
			if( createCISform.getRuleArray().size() >0){
				for (MembershipCriteriaForm temp :  createCISform.getRuleArray()) {
					if(false == temp.isDeleted()){
						// adding the criteria to the list
						MembershipCriteria m = new MembershipCriteria();
						try{
							ArrayList<String> values = new ArrayList<String>();
							values.add(temp.getValue1());
							if (null != temp.getValue2() && false == temp.getValue2().isEmpty())
								values.add(temp.getValue2());
							
							Rule r = new Rule(temp.getOperator(),values);
							m.setRule(r);
							cisCriteria.put(temp.getAttribute(), m);
						}catch(InvalidParameterException e){
							LOG.debug("error addng rule on webapp!");
							e.printStackTrace();
						}
						
					}
				}
			}else{
				LOG.debug("empty rules on webapp cis manager controller");
			}

			// real creation of the CIS
			ICisOwned icis = null;
			try {
				icis = this.cisManager.createCis(cisName, cisType, cisCriteria, cisDescription).get();
			} catch (InterruptedException e) {
				response = "failure creating cis";
				e.printStackTrace();
			} catch (ExecutionException e) {
				response = "failure creating cis";
				e.printStackTrace();
			}
			if(null != icis)
				response = "CIS " + icis.getCisId() + " created";
			else
				response = "failure creating cis";
			

			return yourCommunitiesListPage(response);

		}

	
	
	@RequestMapping(value="/your_communities_list.html",method = RequestMethod.GET)
	public ModelAndView yourCommunitiesListPage(@RequestParam(value="response", required=false) String incomingResponse) {
		//model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		
		// Check if a user is already logged in
		if (getUserService().isUserLoggedIn() == false)
			return new ModelAndView("index", model); //TODO : return error string
		
		model.put("response", incomingResponse);

		CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();

		getCisDirectoryRemote().findAllCisAdvertisementRecords(callback);
		List<CisAdvertisementRecord> adverts = callback.getResultList();
		if(null == adverts) adverts = new ArrayList<CisAdvertisementRecord>();//create an empty list if it doesnt exist or spring will fail 
		model.put("cisAdverts", adverts);
		

		
		List<ICis> records = this.getCisManager().getCisList();
		model.put("cisrecords", records);
		
		
		//JoinCISForm jform = new JoinCISForm();
		//model.put("joinForm", jform);
		
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
		
		// Check if a user is already logged in
		if (getUserService().isUserLoggedIn() == false)
			return new ModelAndView("index", model); //TODO : return error string
		
		
		// TODO, add null checks
		if(null!= response && false == response.isEmpty())
			model.put("response", response);
			
		// GET INFO
		ICis icis = this.getCisManager().getCis(cisId); // handler in case it is a owned/joined CIS
		ICisRemote icisRemote= icis;
		if(null == icisRemote){
			icisRemote = this.getCisManager().getHandlerToRemoteCis(cisId); // basic CIS handler
		}
		CisManagerClient getInfoCallback = new CisManagerClient();
		Requestor req = new Requestor(this.commMngrRef.getIdManager().getThisNetworkNode());
		icisRemote.getInfo(req,getInfoCallback);
		
		CommunityMethods res = 	getInfoCallback.getComMethObj();

		if(res.getGetInfoResponse().isResult() == false){
			model.put("response", "could not fetch information about the community");
		}else{

			
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
			
			
			// if its a CIS which we own or participate
			
			if( null != icis){
			
				// GET ACIVITIES
				ActivityFeedClient activityFeedCallback = new ActivityFeedClient();
				icis.getActivityFeed().getActivities(0 + " " + System.currentTimeMillis(), activityFeedCallback);
				org.societies.api.schema.activityfeed.MarshaledActivityFeed actFeedResponse = activityFeedCallback.getActivityFeed();
				model.put("activities",actFeedResponse.getGetActivitiesResponse().getMarshaledActivity());
						
				
				//actFeedResponse.getGetActivitiesResponse().getActivity().get(0).ge
				AddActivityForm form = new AddActivityForm();
				model.put("activityForm",form);
		
				AddMemberForm form2 = new AddMemberForm();
				model.put("memberForm",form2);
			}

		}
		return new ModelAndView("community_profile", model) ;
	}

	
	// TODO: perhaps adapt this so it does not reload the full page
	//@SuppressWarnings("unchecked")
	@RequestMapping(value = "/add_activity_cis_profile_page.html", method = RequestMethod.POST)
	public ModelAndView addActivityInProfilePage(@Valid AddActivityForm addActForm,  BindingResult result, Map model){
		
		// Check if a user is already logged in
		if (getUserService().isUserLoggedIn() == false)
			return new ModelAndView("index", model); //TODO : return error string
		
		
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
		MarshaledActivityFeed ac= activityFeedCallback.getActivityFeed();
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
		
		// Check if a user is already logged in
		if (getUserService().isUserLoggedIn() == false)
			return new ModelAndView("index", model); //TODO : return error string
		
		
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
	
	
	// join CIS 
	@RequestMapping(value = "/join_cis.html", method = RequestMethod.POST)
	public ModelAndView joinCISfromCommunitiesPage(@RequestParam("position") final int position, @ModelAttribute("cisAdverts") List<CisAdvertisementRecord> adverts,  BindingResult result,  Map model){
		
		// Check if a user is already logged in
		if (getUserService().isUserLoggedIn() == false)
			return new ModelAndView("index", model); //TODO : return error string
		
		
		//if(result.hasErrors()){
		//	return yourCommunitiesListPage("Error joining");
		//}
		
		if(null != adverts && null!= adverts.get(position)){
			CisManagerClient joinCallback = new CisManagerClient();
			this.getCisManager().joinRemoteCIS(adverts.get(position), joinCallback);
			
			JoinResponse j = joinCallback.getComMethObj().getJoinResponse();
			
			return yourCommunitiesListPage("joining towards " + j.getCommunity() + " is " + j.isResult());
		}			
		else{
			return yourCommunitiesListPage("null advertisement on join");
		}

			
	}
	
	//////////////////////// LEAVE COMMUNITY PAGE
	
	@RequestMapping(value="/leave_community.html",method = RequestMethod.GET)
	public ModelAndView leaveCommunity(@RequestParam(value="cisId", required=true) String cisId, Map model){
		
		// Check if a user is already logged in
		if (getUserService().isUserLoggedIn() == false)
			return new ModelAndView("index", model); //TODO : return error string
		
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
		
		// Check if a user is already logged in
		if (getUserService().isUserLoggedIn() == false)
			return new ModelAndView("index", model); //TODO : return error string
		
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
	
	// Check if a user is already logged in
	if (getUserService().isUserLoggedIn() == false)
		return new ModelAndView("index", model); //TODO : return error string
	
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
	

@RequestMapping(value = "/get_suggested_communities.html", method = RequestMethod.POST)
public @ResponseBody JsonResponse getSuggestedCommunities( ){
	JsonResponse res = new JsonResponse();
	List<CisInfo> cisinfoList = new ArrayList<CisInfo>();
	
	// Check if a user is already logged in
		if (getUserService().isUserLoggedIn() == false)
			res.setStatus("FAILURE");
		else	
		{
		
		List<ICis> cisList = getCisManager().getCisList();
		
		
		//TODO : Should be able to give cisdorectory a list of id's!!
		//For now, just get them all 
		CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();

		getCisDirectoryRemote().findAllCisAdvertisementRecords(callback);
		List<CisAdvertisementRecord> adverts = callback.getResultList();

		
		for ( int i = 0; i < cisList.size(); i++)
		{
			//find ad for this id
			boolean bFound = false;
			int j = 0;
			do
			{
				if (adverts.get(j).getId().contains(cisList.get(i).getCisId()))
						bFound = true;
				else
					j++;
			} while ((bFound == false) && (j < adverts.size()));
			
			if (!bFound)
			{
				CisInfo cisInfo = new CisInfo();
				cisInfo.setCisid(cisList.get(i).getCisId());
				cisInfo.setCisname(adverts.get(j).getName());
				cisinfoList.add(cisInfo);
			}
			
		}
	 
		res.setStatus("SUCCESS");
		res.setResult(cisinfoList);
	}
	
	
	return res;
}

@RequestMapping(value = "/get_my_communities.html", method = RequestMethod.POST)
public @ResponseBody JsonResponse getMyCommunities( ){
	JsonResponse res = new JsonResponse();
	List<CisInfo> cisinfoList = new ArrayList<CisInfo>();
	
	if (getUserService().isUserLoggedIn())
	{
		
		List<ICis> cisList = getCisManager().getCisList();
		
		
		//TODO : Should be able to give cisdorectory a list of id's!!
		//For now, just get them all 
		CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();

		getCisDirectoryRemote().findAllCisAdvertisementRecords(callback);
		List<CisAdvertisementRecord> adverts = callback.getResultList();

		
		for ( int i = 0; i < cisList.size(); i++)
		{
			//find ad for this id
			boolean bFound = false;
			int j = 0;
			do
			{
				if (adverts.get(j).getId().contains(cisList.get(i).getCisId()))
						bFound = true;
				else
					j++;
			} while ((bFound == false) && (j < adverts.size()));
			
			if (bFound)
			{
				CisInfo cisInfo = new CisInfo();
				cisInfo.setCisid(cisList.get(i).getCisId());
				cisInfo.setCisname(adverts.get(j).getName());
				cisinfoList.add(cisInfo);
			}
			
		}
	 
		res.setStatus("SUCCESS");
		res.setResult(cisinfoList);
	}
	else
		res.setStatus("FAILURE");
	
	return res;
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
	
	
	
	public ICisDirectoryRemote getCisDirectoryRemote() {
		return cisDirectoryRemote;
	}


	public void setCisDirectoryRemote(ICisDirectoryRemote cisDirectoryRemote) {
		this.cisDirectoryRemote = cisDirectoryRemote;
	}


	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
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
		public void onPrivacyPolicyRetrieved(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy p) {
			try {
				this.privacyPolicy = RequestPolicyUtils.toRequestPolicy(p, commMngrRef.getIdManager());
				insertComObjInQueue(this.privacyPolicy);
			} catch (InvalidFormatException e) {
				onOperationAborted("Privacy policy retrieved, but we can't understand it.", e);
			}
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
