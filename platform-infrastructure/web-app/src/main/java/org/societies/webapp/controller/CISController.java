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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.orchestration.communitylifecyclemanagementbean.Cis;
import org.societies.webapp.controller.privacy.PrivacyPolicyUtils;
import org.societies.webapp.models.CisInfo;
import org.societies.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy; 

import javax.servlet.http.HttpSession;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.client.ActivityFeedClient;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.attributes.Rule.OperationType;
//import org.societies.api.cis.directory.ICisDirectory;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.cis.management.ICisRemote;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.css.directory.ICssDirectoryRemote;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener;
//import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.remote.IPrivacyPolicyManagerRemote;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
//import org.societies.api.privacytrust.privacy.util.privacypolicy.PrivacyPolicyUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.JoinResponse;
import org.societies.api.schema.cis.community.LeaveResponse;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.community.WhoResponse;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.cis.manager.Create;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssAdvertisementRecordDetailed;
import org.societies.api.schema.cssmanagement.CssManagerResultActivities;
import org.societies.api.schema.cssmanagement.CssRequest;
import org.societies.api.schema.cssmanagement.CssRequestOrigin;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyBehaviourConstants;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.cis.directory.client.CisDirectoryRemoteClient;
import org.societies.cis.mgmtClient.CisManagerClient;
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
import org.societies.webapp.models.WebAppParticipant;


@Controller
@ManagedBean(name = "cismanager")
@RequestScoped
public class CISController extends BasePageController{
	
	
	@ManagedProperty(value = "#{userService}")
	private UserService userService;
	
	@ManagedProperty(value = "#{cssLocalManager}")
	private ICSSInternalManager cssLocalManager;
		
	
	@ManagedProperty(value = "#{cisDirectoryRemote}")
	private ICisDirectoryRemote  cisDirectory;
	
	@ManagedProperty(value = "#{cisManager}")
	
	@Autowired
	private ICisManager cisManager;

	@Autowired
	@ManagedProperty(value = "#{commMngrRef}")
	private ICommManager commMngrRef;
	
	private IActivityFeed actFeed;
	
	
	@Autowired
	@ManagedProperty(value = "#{ciscallback}")
	private ICisManagerCallback ciscallback;
	
	public ICisManagerCallback getCiscallback() {
		return ciscallback;
	}
	public void setCiscallback(ICisManagerCallback ciscallback) {
		this.ciscallback = ciscallback;
	}
		
	@Autowired
	private ICisDirectoryRemote cisDirectoryRemote;
		
	@Autowired
	private IPrivacyPolicyManager privacyPolicyManager;
	@Autowired
	private IPrivacyPolicyManagerRemote privacyPolicyManagerRemote;
	
	//@Autowired
	//@ManagedProperty(value = "#{privacypol}")
//	private PrivacyPolicyUtils privacypol;
	
//	public PrivacyPolicyUtils getPrivacypol() {
//		return privacypol;
//	}
//	public void setPrivacypol(PrivacyPolicyUtils privacypol) {
//		this.privacypol = privacypol;
//	}

	private String cisname;

	public String getCisname() {
		return cisname;
	}
	public void setCisname(String cisname) {
		this.cisname = cisname;
	}
	public String getCistype() {
		return cistype;
	}
	public void setCistype(String cistype) {
		this.cistype = cistype;
	}
	public String getCisdesc() {
		return cisdesc;
	}
	public void setCisdesc(String cisdesc) {
		this.cisdesc = cisdesc;
	}

	List<Participant> m_remoteMemberRecords = new ArrayList<Participant>();
	
	private String cistype;
	private String cisdesc;
	private int mode;
	
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	

	//for the callback
	private String resultCallback;
	private Community remoteCommunity;
	private HttpSession m_session;
	
	// AUTOWIRING GETTERS AND SETTERS
		public ICisManager getCisManager() {
			return cisManager;
		}
		public void setCisManager(ICisManager cisManager) {
			this.cisManager = cisManager;
		}
		
		public IActivityFeed getactFeed() {
			return actFeed;
		}
		public void setCis(IActivityFeed actFeed) {
			this.actFeed = actFeed;
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
		
		
		
		public ICisDirectoryRemote getcisDirectory() {
			return this.cisDirectory;
		}


		public void setcisDirectory(ICisDirectoryRemote cisDirectory) {
			this.cisDirectory = cisDirectory;
		}


		public UserService getUserService() {
			return userService;
		}
		public void setUserService(UserService userService) {
			this.userService = userService;
		}
		
		public ICSSInternalManager getCssLocalManager() {
			return cssLocalManager;
		}
		
		public void setCssLocalManager(ICSSInternalManager cssLocalManager) {
			this.cssLocalManager = cssLocalManager;
		}
	
	
	
	public CISController() {
	    log.info("CISController constructor called");
	}
	
	
	
	public void createcommunity(){
		log.info("CISController create community called");
		Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria>();
		
		PrivacyPolicyBehaviourConstants policyType = null;
		String pPolicy = null;
		
		log.info("create community cisname: " +cisname);
		log.info("create community cistype: " +cistype);
		log.info("create community cisdesc: " +cisdesc);
		log.info("create community mode: " +mode);
		log.info("create community cisCriteria: " +cisCriteria);
		
		if(mode == 0){
			policyType = PrivacyPolicyBehaviourConstants.PRIVATE;
			pPolicy = "private";
		}
		if(mode == 1){
			policyType = PrivacyPolicyBehaviourConstants.MEMBERS_ONLY;
			pPolicy = "membersOnly";
		}
		if(mode == 2){
			policyType = PrivacyPolicyBehaviourConstants.PUBLIC;
			pPolicy = "public";
		}
		
		log.info("create community mode is now: " +policyType);
		
		
		
		
		//GENERATE MEMBERSHIP CRITERIA
		Hashtable<String, MembershipCriteria> h = null;
		MembershipCrit m = new MembershipCrit();
		log.info("create MembershipCrit: " +m);
		
		//MembershipCrit m = create.getCommunity().getMembershipCrit();
		if (m!=null && m.getCriteria() != null && m.getCriteria().size()>0){
			h =new Hashtable<String, MembershipCriteria>();
			
			// populate the hashtable
			for (Criteria crit : m.getCriteria()) {
				MembershipCriteria meb = new MembershipCriteria();
				log.info("create MembershipCriteria: " +meb);
				meb.setRank(crit.getRank());
				Rule r = new Rule();
				if( r.setOperation(crit.getOperator()) == false);
				ArrayList<String> a = new ArrayList<String>();
				a.add(crit.getValue1());
				
				
				if (crit.getValue2() != null && !crit.getValue2().isEmpty()) 
					a.add(crit.getValue2()); 
				if( r.setValues(a) == false){
					meb.setRule(r);
					h.put(crit.getAttrib(), meb);
				}
				
			}
		}
		log.info("about to create Create");
		Create create = new Create(); 
		create.setPrivacyPolicy(pPolicy);
		//POLICY RECEIVED IS ENUM VALUE, CONVERT TO POLICY XML
		//String pPolicy = "membersOnly"; //DEFAULT VALUE
		String privacyPolicyXml = "<RequestPolicy />";
		if(create.getPrivacyPolicy() != null && create.getPrivacyPolicy().isEmpty() == false){
			pPolicy = create.getPrivacyPolicy();
			log.info("pPolicy = " +pPolicy);
		} 
		//PrivacyPolicyBehaviourConstants policyType = PrivacyPolicyBehaviourConstants.MEMBERS_ONLY; //DEFAULT
		PrivacyPolicyUtils utility = new PrivacyPolicyUtils();
		log.info("Create new PrivacyPolicyUtils: " +utility );
		RequestPolicy policyObj = null;
		log.info("Create RequestPolicy: " +policyObj );
		try {
			policyType = PrivacyPolicyBehaviourConstants.fromValue(pPolicy);
		} catch (IllegalArgumentException ex) {
			//IGNORE - DEFAULT TO MEMBERS_ONLY
			log.error("Exception parsing: " + pPolicy + ". " + ex);
		}
		//CALL POLICY UTILS TO CREATE XML FOR STORAGE
		try {
			log.info("call to PrivacyPolicyUtils: " );
			//org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy policyObj = PrivacyPolicyUtils.inferCisPrivacyPolicy(policyType, m);
			policyObj = PrivacyPolicyUtils.inferCisPrivacyPolicy(policyType, m);
			privacyPolicyXml =  PrivacyPolicyUtils.toXacmlString(policyObj);
			log.info("@@@@@@@########### privacyPolicyXml contains: " +privacyPolicyXml);
		} catch (PrivacyException pEx) {
			pEx.printStackTrace();
		}
		
		// real create
		this.cisManager.createCis(cisname, cistype, h, cisdesc, privacyPolicyXml);
		//Cis icis = (Cis) localCreateCis(cisname, cistype, cisdesc, h, privacyPolicyXml);
		//cisManager.createCis(cisname, cistype, cisCriteria, cisdesc);
		//SET CIS CREATED NAME TO SHOW IN DIALOG
		cisname = "";
		cistype = "";
		cisdesc = "";
		
		RequestContext.getCurrentInstance().execute("createDlg.show();");
	}
	
	private class WhoCallback implements ICisManagerCallback{

		private List<Participant> participant;
		private boolean done = false;

		@Override
		public void receiveResult(CommunityMethods comMethod) {
			// TODO Auto-generated method stub
			WhoResponse whoResponse = comMethod.getWhoResponse();
			setParticipant(whoResponse.getParticipant());
			done = true;
		}

		public List<Participant> getParticipant() {
			return participant;
		}

		public void setParticipant(List<Participant> participant) {
			this.participant = participant;
		}

		public boolean isDone() {
			return done;
		}

		
		
	}
	/**
	 * @author Eliza
	 */
	public void deleteCommunity(CisInfo cisInfo){
		
		//FacesMessage message = new FacesMessage("User wants to delete this CIS: "+cisInfo.getCisid());
		//FacesContext.getCurrentInstance().addMessage(null, message);
		
		try {
			WhoCallback callback = new WhoCallback();
			this.cisManager.getListOfMembers(new Requestor(this.userService.getIdentity()), this.commMngrRef.getIdManager().fromJid(cisInfo.getCisid()), callback);
			while (!callback.isDone()){
				Thread.sleep(500);
			}
			this.log.debug("Received members of cis ");
		
			List<Participant> participant = callback.getParticipant();
			if (null!=participant){
				if (participant.size()>1){
					this.log.debug("Retrieved "+participant.size()+" members of CIS: "+cisInfo.getCisid());
					for (Participant p: participant){
						this.log.debug("Participant: "+p.getJid());
					}
					RequestContext.getCurrentInstance().execute("cantDeleteDlg.show();");
					FacesMessage message = new FacesMessage("Sorry, this CIS has other members. To delete this CIS, please ask the members to leave the CIS.");
					
					FacesContext.getCurrentInstance().addMessage(null, message);
					
					return;
				}
			}
			FacesMessage message; 
			if (this.cisManager.deleteCis(cisInfo.getCisid())){
				message = new FacesMessage(cisInfo.getCisname()+" community was deleted. ", "CIS with ID: "+cisInfo.getCisid()+" was successfully deleted. ");
				this.log.info("#CODE2#: Deleted community: "+cisInfo.getCisname()+" cisID: "+cisInfo.getCisid());
			}else{
				message = new FacesMessage(cisInfo.getCisname()+" community was not deleted. ", "Unable to delete CIS with ID: "+cisInfo.getCisid()+"");
			}
			
			 FacesContext.getCurrentInstance().addMessage(null, message);
			 
		} catch (InvalidFormatException e) {
			
			FacesMessage message = new FacesMessage("An error occurred while trying to delete the CIS");
			FacesContext.getCurrentInstance().addMessage(null, message );
			
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 
	}
	public List<CisInfo> getsuggestedcommunities(){
		log.info("CISController get suggested communities called");
		
		CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();
		
		ICisDirectoryRemote remote = getcisDirectory();
		
		log.info("ICisDirectoryRemote : " +remote);
		
		List<CisInfo> cisinfoList = new ArrayList<CisInfo>();
		List<ICis> cisList = new ArrayList<ICis>();
		cisList = this.cisManager.getCisList();
		log.info("cisList size is " +cisList.size());
		log.info("cisList contains " +cisList);
		
		getcisDirectory().findAllCisAdvertisementRecords(callback);
		List<CisAdvertisementRecord> adverts = callback.getResultList();
		

		List<String> listIds = new ArrayList<String>();
		for(int i = 0; i < cisList.size(); i++){
			listIds.add(cisList.get(i).getCisId());
		}
		
		for (CisAdvertisementRecord cisAdd : adverts){

			log.info(" @@@@@@@@@ listIds contains : " +listIds);
				if (listIds.contains(cisAdd.getId())){
					log.info("Already a member of CIS not adding it");
				}else {
					CisInfo cisInfo = new CisInfo();
					cisInfo.setCisid(cisAdd.getId());
					cisInfo.setCisname(cisAdd.getName());
					cisinfoList.add(cisInfo);
					log.info("===== adding cisid " +cisInfo.getCisid() +" and name " +cisInfo.getCisname());
					log.info("===== cisinfoList size is " +cisinfoList.size());
					//cisinfoList.add((cisAdd.getId()));
				}
		}
		
		
		log.info("cisinfoList size is " +cisinfoList.size());
		log.info("cisinfoList contains " +cisinfoList);
		
		//return adverts;
		return cisinfoList;
		
		
	}
	
	
	public List<MarshaledActivity> getactivities(){
		log.debug("getActivities called");
		Date date = new Date();
		long longDate=date.getTime();
		String timespan = "1262304000000 " + longDate;
		List<MarshaledActivity> listSchemaActivities = new ArrayList<MarshaledActivity>();  
		List<MarshaledActivity> Result = new ArrayList<MarshaledActivity>();
						
		Future<List<MarshaledActivity>> asyncActivitiesResult = this.cssLocalManager.getActivities(timespan, 20);
		//Future<List<MarshaledActivity>> asyncActivitiesResult = this.cisManager.getActivities(timespan, 20);
		CssManagerResultActivities results = new CssManagerResultActivities();
		try {
			results.setMarshaledActivity(asyncActivitiesResult.get());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
		
		log.debug("Activities : " +results);
		log.debug("Activities Size: " +results.getMarshaledActivity().size());
		
		for(MarshaledActivity result : results.getMarshaledActivity()){
			log.debug("MarshaledActivity Published " +result.getPublished());
			log.debug("MarshaledActivity Verb " +result.getVerb());
			log.debug("MarshaledActivity Actor" +result.getActor());
			Result.add(result);
			
		}
		
		return Result;
	}
	
	public List<CisInfo> getownedcommunities(){
		
		log.info("getownedcommunities method called");
		List<CisInfo> cisinfoList = new ArrayList<CisInfo>();
		
		List<ICisOwned> ownedCISs = cisManager.getListOfOwnedCis();
			
		log.info("ownedCISs SIZE is now " +ownedCISs.size());
		
		for(ICisOwned entry : ownedCISs){
			log.info("entry id is " +entry.getCisId());
			log.info("entry name is " +entry.getName());
		}
			
		CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();
		getcisDirectory().findAllCisAdvertisementRecords(callback);
		List<CisAdvertisementRecord> adverts = callback.getResultList();
		
		
		for ( int i = 0; i < ownedCISs.size(); i++)
		{
			//find ad for this id
			boolean bFound = false;
			int j = 0;
			do
			{
				
				if (adverts.get(j).getId().contains(ownedCISs.get(i).getCisId())){
						bFound = true;
				}else
					j++;
			} while ((bFound == false) && (j < adverts.size()));
			
			if (bFound)
			{
				CisInfo cisInfo = new CisInfo();
				cisInfo.setCisid(ownedCISs.get(i).getCisId());
				cisInfo.setCisname(adverts.get(j).getName());
				cisinfoList.add(cisInfo);
			}
			
		}
		return cisinfoList;
		
		
	}
	
	public List<CisInfo> getcommunities(){
		
		log.info("getcommunities method called");
		List<ICis> cisList = getCisManager().getCisList();
		
		
		log.info("cisList size :" +cisList.size());
		
		//Future<List<CssAdvertisementRecord>> asynchFriends = getCssLocalManager().getCssFriends();
		List<CisInfo> cisinfoList = new ArrayList<CisInfo>();
		List<ICis> comList = new ArrayList<ICis>();
		//comList = cisManager.getCisList();
		
		CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();
		getcisDirectory().findAllCisAdvertisementRecords(callback);
		List<CisAdvertisementRecord> adverts = callback.getResultList();
		
		
		for ( int i = 0; i < cisList.size(); i++)
		{
			//find ad for this id
			boolean bFound = false;
			int j = 0;
			do
			{
				
				if (adverts.get(j).getId().contains(cisList.get(i).getCisId())){
						bFound = true;
				}else
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
		
		log.info("cisinfoList contains " +cisinfoList);	
		return cisinfoList;
		
		
	}
	
	public List<CisInfo> getmembercommunities(){
		
		log.info("getmembercommunities method called");
		
		CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();
		getcisDirectory().findAllCisAdvertisementRecords(callback);
		List<CisAdvertisementRecord> adverts = callback.getResultList();
		
		List<CisInfo> ownedCISs = this.getownedcommunities();
		log.info("ownedCISs SIZE is now " +ownedCISs.size());
		List<CisInfo> allCiss = this.getcommunities();
		log.info("allCiss SIZE is now " +allCiss.size());
		
		
		List<CisInfo> memberCISs = new ArrayList<CisInfo>();
		memberCISs = allCiss;
		log.info("memberCISs size is now " +memberCISs.size());
		//now compare the two lists
		
		for(int i = 0; i < allCiss.size(); i++){
			for(CisInfo entry : ownedCISs){
				if(allCiss.get(i).getCisid().contains(entry.getCisid())){
					memberCISs.remove(allCiss.get(i));
				}else{
					log.info("Not on the owned list adding to the members list " +allCiss.get(i).getCisid());
				}
			}
		}
		
		log.info("memCISs SIZE is now " +memberCISs.size());
			
		return memberCISs;
		
		
	}
	
	public void joincis(CisInfo adv){
		
		log.info("JoinCIS method called");
		log.info("JoinCIS method called CIS ID " +adv.getCisid());
		
		CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();
		
		cisDirectory.searchByID(adv.getCisid(), callback);
		List<CisAdvertisementRecord> adverts = callback.getResultList();
		log.info("callback.getResultList() size is " +adverts.size());
		log.info("callback.getResultList() is " +adverts);
		
		CisManagerClient joinCallback = new CisManagerClient();
		log.info("CisManagerClient joinCallback is " +joinCallback);
		log.info("cisManager instance is " +cisManager);
		cisManager.joinRemoteCIS(adverts.get(0), joinCallback);
		log.info("and we're back :( ");
		
	}
	
	public void leavecis(CisInfo adv){
		
		log.info("leaveCIS method called");
		log.info("leaveCIS method called CIS ID " +adv.getCisid());
		CisManagerClient leaveCallback = new CisManagerClient();
		log.info("CisManagerClient joinCallback is " +leaveCallback);
		String CISID = adv.getCisid();
		
		
		
		cisManager.leaveRemoteCIS(CISID, leaveCallback);
		
		
	}
	
	public List<String> getsuggestedlistmembers(CisInfo adv){
		log.info("getsuggestedlistmembers method called");
		List<String> suggestedMembers = new ArrayList<String>();
		List<WebAppParticipant> membersDetails = new ArrayList<WebAppParticipant>();
		String placeholder = "placeholder";
		String Id = adv.getCisid();
		IIdentity cisID = null;
		
		try {
		cisID = getCommMngrRef().getIdManager().fromJid(Id);
		log.info("cisID is : " +cisID);
		} catch (InvalidFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	
//	List<ICis> cisList = getCisManager().getCisList();
//	log.info("cisList size :" +cisList.size());
	Requestor requestor = new Requestor(cisID);
	
	ServiceResourceIdentifier myServiceID = new ServiceResourceIdentifier();
	RequestorService service = new RequestorService(cisID, myServiceID);
	
	cisManager.getListOfMembers(requestor , cisID, icall);
		
	if (m_remoteMemberRecords != null)
	{
		for ( int memberIndex  = 0; memberIndex < m_remoteMemberRecords.size(); memberIndex++)
		{
			WebAppParticipant memberDetail = new WebAppParticipant();
			
			memberDetail.setMembersJid(m_remoteMemberRecords.get(memberIndex).getJid());
			memberDetail.setMembershipType(m_remoteMemberRecords.get(memberIndex).getRole());
			log.info("membersDetail jid is: " +memberDetail.getMembersJid());
			log.info("membersDetail type is: " +memberDetail.getMembershipType());
			
			membersDetails.add(memberDetail);
			suggestedMembers.add(m_remoteMemberRecords.get(memberIndex).getJid());
			
		}
	}	
	log.info(" >>>>>>>>>> suggestedMembers size is: " +suggestedMembers.size());
		
		return suggestedMembers;
	}
	
	public List<String> getmembers(CisInfo adv){
		log.info("getmembers method called");
		log.info("CisAdvertisementRecord is: " +adv.getCisid());
		String Id = adv.getCisid();
		IIdentity cisID = null;
		log.info("Id is: " +Id);
		List<String> remotemembers = new ArrayList<String>();
		List<WebAppParticipant> membersDetails = new ArrayList<WebAppParticipant>();
		ICisOwned thisCis = null;
		List<ICisOwned> ownedCISs = this.getCisManager().getListOfOwnedCis();
		log.info("ownedCISs size: " +ownedCISs.size());
		
		if (ownedCISs.size() > 0) 
		{
			
			Iterator<ICisOwned> it = ownedCISs.iterator();

			while(it.hasNext() && thisCis == null)
			{
				ICisOwned element = it.next();
				if (element.getCisId().equalsIgnoreCase(Id)) 
				{
					thisCis = element;
					
				}
			}
		}
		
		if(thisCis == null)
		{
			
			// "thisCIS is null";
			//NOT LOCAL CIS, SO CALL REMOTE
			ICis remoteCIS = this.getCisManager().getCis(Id);
			if (remoteCIS != null) 
			{
				Requestor req = new Requestor(this.commMngrRef.getIdManager().getThisNetworkNode());
				remoteCIS.getListOfMembers(req, icall);
				
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (m_remoteMemberRecords != null)
				{
					for ( int memberIndex  = 0; memberIndex < m_remoteMemberRecords.size(); memberIndex++)
					{
						WebAppParticipant memberDetail = new WebAppParticipant();
						
						memberDetail.setMembersJid(m_remoteMemberRecords.get(memberIndex).getJid());
						memberDetail.setMembershipType(m_remoteMemberRecords.get(memberIndex).getRole());
						log.info("membersDetail jid is: " +memberDetail.getMembersJid());
						log.info("membersDetail type is: " +memberDetail.getMembershipType());
						
						membersDetails.add(memberDetail);
						remotemembers.add(m_remoteMemberRecords.get(memberIndex).getJid());
						
					}
				}
				
			}
		
		} else {
			Set<ICisParticipant> records = thisCis.getMemberList();
			List<String> localmembersDetails = new ArrayList<String>();
			log.info("else Participant records size is: " +records.size());
			
//			CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();
//			getcisDirectory().findAllCisAdvertisementRecords(callback);
//			List<CisAdvertisementRecord> adverts = callback.getResultList();
		    
		    for (ICisParticipant s : records) {
	            log.info("else Participant jid is: " +s.getMembersJid());
	    	    localmembersDetails.add(s.getMembersJid());
	            }
			
			
			return localmembersDetails;
			//return null;
			
		}
		log.info("membersDetails: " +Id);
		return remotemembers;
	}
	

	
	WebAppCISCallback icall = new WebAppCISCallback();//this.userFeedback);
	
	public class WebAppCISCallback implements ICisManagerCallback{
		public WebAppCISCallback(){
			super();
			
		}
		
		
		public void receiveResult(CommunityMethods communityResultObject) {
			if(communityResultObject == null){
				resultCallback = "Failure getting result from remote node!";
			}
			else {
				if(communityResultObject.getJoinResponse() != null){
					if(communityResultObject.getJoinResponse().isResult()){
						
						if(null != communityResultObject.getJoinResponse().getCommunity()  && null != communityResultObject.getJoinResponse().getCommunity().getCommunityJid()
								&& communityResultObject.getJoinResponse().getCommunity().getCommunityJid().isEmpty() == false){							
							String community = communityResultObject.getJoinResponse().getCommunity().getCommunityJid(); 
							log.info("callback for join regarindg community " + community);
							//this.userFeedback.showNotification("Joined CIS: " + community);
							resultCallback = "Joined CIS " + community;
							remoteCommunity = communityResultObject.getJoinResponse().getCommunity();
							m_session.setAttribute("community", remoteCommunity);
							log.info("done at join response");
						}
						else{
							resultCallback = "Joined CIS work but community xsd was bogus ";
						}
					}else{
						resultCallback = "Failure when trying to joined CIS: " + communityResultObject.getJoinResponse().getCommunity().getCommunityJid();
						//this.userFeedback.showNotification("failed to join " + communityResultObject.getJoinResponse().getCommunity().getCommunityJid());
					}

				}
				if(communityResultObject.getWhoResponse() != null){
					log.info("### " + communityResultObject.getWhoResponse().getParticipant().size());

					//m_session.setAttribute("community", remoteCommunity);
					m_remoteMemberRecords = communityResultObject.getWhoResponse().getParticipant();					
					//m_session.setAttribute("remoteMemberRecords", m_remoteMemberRecords);
				}

			}
		}
	}
		
}
