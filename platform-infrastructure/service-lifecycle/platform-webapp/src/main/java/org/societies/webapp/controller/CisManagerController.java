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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.webapp.models.CisManagerForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;

import java.security.InvalidParameterException;


@Controller
public class CisManagerController {

	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private ICisManager cisManager;

	/**
	 * @return the cisManager */
	public ICisManager getCisManager() {
		return cisManager;
	}

	/**
	 * @param cisManager the cisManager to set */
	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	// store the interfaces of remote and local CISs
	//private ArrayList<ICis> remoteCISs;
	//private ArrayList<ICisOwned> localCISs;

	//for the callback
	private String resultCallback;
	private Community remoteCommunity;
	private HttpSession m_session;
	private static Logger LOG = LoggerFactory.getLogger(CisManagerController.class);
	
	@RequestMapping(value = "/cismanager.html", method = RequestMethod.GET)
	public ModelAndView cssManager() {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Welcome to the CIS Manager");
		CisManagerForm cisForm = new CisManagerForm();
		
		//LIST METHODS AVAIABLE TO TEST
		Map<String, String> methods = new LinkedHashMap<String, String>();
		methods.put("CreateCis", "Create a CIS ");
		methods.put("GetCisList", "List my CISs");
		methods.put("JoinRemoteCIS", "Join a remote CIS");
		methods.put("LeaveRemoteCIS", "Leave a remote CIS");
		methods.put("GetMemberList", "Get list of members of a CIS");
		methods.put("GetMemberListRemote", "Get list of members from remote CIS");
		methods.put("AddMember", "Add member to a CIS");
		methods.put("RemoveMemberFromCIS", "Remove member from a CIS");
		model.put("methods", methods);
		
		model.put("cmForm", cisForm);
		//remoteCISs = new ArrayList<ICis>();
		
		//localCISs = new ArrayList<ICisOwned>();
		
		/*localCISs.addAll(this.getCisManager().getListOfOwnedCis());
		remoteCISs.addAll(this.getCisManager().getRemoteCis());
		
		Iterator<ICisOwned> it = localCISs.iterator();
		ICisOwned thisCis = null;
		String res = "";
		String log = "Log ";
		while(it.hasNext() && thisCis != null){
			ICisOwned element = it.next();
			log.concat(("CIS initially added with jid = " + element.getCisId()));
	     }
		*/
		// criteria
		
		// TODO: get from CtxAttributeTypes
		String [] attributeList = {"addressHomeCity", "interests","music","religiouslViews"};
		
		String [] operatorList = {"equals", "differentFrom"};
		
		model.put("attributeList", attributeList);
		model.put("operatorList", operatorList);
		
		// end of criteria
		
		
		//model.put("remoteCISsArray", remoteCISs);
		//model.put("localCISsArray", localCISs);
		//model.put("log", log);
		model.put("cismanagerResult", "CIS Management Result :");
		return new ModelAndView("cismanager", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/cismanager.html", method = RequestMethod.POST)
	public ModelAndView cssManager(@Valid CisManagerForm cisForm, BindingResult result, Map model, HttpSession session) {

		m_session = session;
		model.put("message", "Welcome to the CIS Manager Page");

		if (result.hasErrors()) {
			model.put("res", "CIS Manager form error");
			return new ModelAndView("cismanager", model);
		}

		if (getCisManager() == null) {
			model.put("errormsg", "CIS Manager Service reference not avaiable");
			return new ModelAndView("error", model);
		}
		String res = "Starting...";
		String method = cisForm.getMethod();
		res = "Method: " + method;

		try {
			if (method.equalsIgnoreCase("CreateCis")) {
				model.put("methodcalled", "CreateCis");
				res = "Creating CIS...";
				
				Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria> (); 
				MembershipCriteria m = new MembershipCriteria();
				try{
					Rule r = new Rule(cisForm.getOperator(),new ArrayList(Arrays.asList(cisForm.getValue())));
					m.setRule(r);
					cisCriteria.put(cisForm.getAttribute(), m);
				}
				catch(InvalidParameterException e){
					// TODO: treat expection
					res += " excepation of invalid param " + cisForm.getAttribute() + ", " + cisForm.getOperator()+ ", " + cisForm.getValue();
				}
				
				Future<ICisOwned> cisResult = this.getCisManager().createCis(
						cisForm.getCisName(),
						cisForm.getCisType(),cisCriteria,""
						); // for some strange reason null instead of cisCriteria did not work

				res = "Successfully created CIS: " + cisResult.get().getCisId();
				//localCISs.add(cisResult.get());

			} else if (method.equalsIgnoreCase("GetCisList")) {
				model.put("methodcalled", "GetCisList");

				//ICisRecord searchRecord = null;
				//ICisRecord[] records = this.getCisManager().getCisList(searchRecord);
				List<ICis> records = this.getCisManager().getCisList();
				model.put("cisrecords", records);

			} else if (method.equalsIgnoreCase("JoinRemoteCIS")) {
				model.put("methodcalled", "JoinRemoteCIS");

				// TODO: get a real advertisement
				CisAdvertisementRecord ad = new CisAdvertisementRecord();
				ad.setId(cisForm.getCisJid());
				// in order to force the join to send qualifications, Ill add some criteria to the AdRecord
				MembershipCrit membershipCrit = new MembershipCrit();
				List<Criteria> criteria = new ArrayList<Criteria>();
				Criteria c1 = new Criteria();c1.setAttrib(CtxAttributeTypes.ADDRESS_HOME_CITY);c1.setOperator("equals");c1.setValue1("something");
				Criteria c2 = new Criteria();c2.setAttrib(CtxAttributeTypes.RELIGIOUS_VIEWS);c2.setOperator("equals");c2.setValue1("something");
				criteria.add(c1);criteria.add(c2);membershipCrit.setCriteria(criteria);
				ad.setMembershipCrit(membershipCrit);
				// done setting membership crit
				
				this.getCisManager().joinRemoteCIS(ad, icall);
				
				Thread.sleep(5 * 1000);
				model.put("joinStatus", resultCallback);
				if(!resultCallback.startsWith("Failure") ){
					ICis i = getCisManager().getCis(cisForm.getCisJid());
					model.put("cis", i);
				}

			} else if (method.equalsIgnoreCase("LeaveRemoteCIS")) {
				model.put("methodcalled", "LeaveRemoteCIS");

				this.getCisManager().leaveRemoteCIS(cisForm.getCisJid(), icall);
				res = "left CIS: ";

			} else if (method.equalsIgnoreCase("GetMemberList")) {
				model.put("methodcalled", "GetMemberList");
				model.put("cisid", cisForm.getCisJid());

				ICisOwned thisCis = null;
				List<ICisOwned> ownedCISs = this.getCisManager().getListOfOwnedCis();
				if (ownedCISs.size() > 0) {
//					res = "before addall";
//					localCISs.addAll(ownedCISs);
//					res = "AfteraddAll";
					Iterator<ICisOwned> it = ownedCISs.iterator();
					
					res = "Beforewhile";
					while(it.hasNext() && thisCis == null){
						ICisOwned element = it.next();
						res = "BeforeIf";
						if (element.getCisId().equalsIgnoreCase(cisForm.getCisJid())) {
							thisCis = element;
							//break;
						}
				    }
				}
				if(thisCis == null){
					res = "thisCIS is null";
					//NOT LOCAL CIS, SO CALL REMOTE
					ICis remoteCIS = this.getCisManager().getCis(cisForm.getCisJid().trim());
					if (remoteCIS != null) {
						res = cisForm.getCisJid().trim();
						remoteCIS.getListOfMembers(icall);
						res = "After getList";
					}
					cisForm.setMethod("GetMemberListRemote");
					model.put("methodcalled", "GetMemberListRemote");
					res = "CIS==null: " + cisForm.getMethod();
				} else {
					Set<ICisParticipant> records = thisCis.getMemberList().get();
					model.put("memberRecords", records);
					res = "CIS is not null";
				}
				
			} else if (method.equalsIgnoreCase("GetMemberListRemote")) {
				model.put("methodcalled", "GetMemberListRemote");
				model.put("cisid", cisForm.getCisJid());
				//CALL REMOTE
				res += cisForm.getCisJid();
				res += "Before Remote";
				ICis remoteCIS = this.getCisManager().getCis(cisForm.getCisJid());
				remoteCIS.getListOfMembers(icall);
				res += "After Remote";
				
			} else if (method.equalsIgnoreCase("RefreshRemoteMembers")) {
				model.put("methodcalled", "RefreshRemoteMembers");
				model.put("cisid", cisForm.getCisJid());
				
				Community remoteCommunity = (Community)m_session.getAttribute("community");
				List<Participant> membersRemote = (List<Participant>) m_session.getAttribute("remoteMemberRecords");					
				model.put("remoteMemberRecords", membersRemote);				
				model.put("community", remoteCommunity);
				Set<ICisParticipant> records = null;
				model.put("memberRecords", records);

				
			} else if (method.equalsIgnoreCase("AddMember")) {
				model.put("methodcalled", "AddMember");
				// TODO
				res = "";
				List<ICisOwned> listOwned = this.cisManager.getListOfOwnedCis();
				Iterator<ICisOwned> it = listOwned.iterator();
				ICisOwned thisCis = null;
				res +=" owned size " + listOwned.size();
				while(it.hasNext() ){//&& (thisCis != null)){
					res +="got in the loop";
					ICisOwned element = it.next();
					 if (element.getCisId().equalsIgnoreCase(cisForm.getCisJid())){
						 res +="found a match";
						 thisCis = element;
					 }
					 else{
						  res +="CIS being compared = " + element.getCisId() + "and form = " + cisForm.getCssId();
					 }
			     }
				res += " interactor done ";
				if(thisCis == null){
					res +="CIS not found: " + cisForm.getCssId();
				}else{
					if (thisCis.addMember(cisForm.getCssId(), cisForm.getRole()).get())
						res += "member added ";
					else
						res += "error when adding member";					
				}
				model.put("res", res);

			} else if (method.equalsIgnoreCase("RemoveMemberFromCIS")) {
				model.put("methodcalled", "RemoveMemberFromCIS");
				// TODO
				// model.put("cisrecords", records);
	
			} else {
				model.put("methodcalled", "Unknown");
				res = "error unknown metod";
			}
			
			//ALWAYS RETURN THE LIST OF CIS'S I OWN OR AM MEMBER OF
			List<ICis> records = this.getCisManager().getCisList();
			model.put("cisrecords", records);
			
		} catch (Exception ex) {
			res += "Oops!!!! <br/>" + ex.getLocalizedMessage();//.getMessage();
		}

		model.put("res", res);
		model.put("cmForm", cisForm);
		return new ModelAndView("cismanagerresult", model);
	}

	// callback
	ICisManagerCallback icall = new ICisManagerCallback(){


		public void receiveResult(CommunityMethods communityResultObject) {
			if(communityResultObject == null){
				resultCallback = "Failure getting result from remote node!";
			}
			else {
				if(communityResultObject.getJoinResponse() != null){
					if(communityResultObject.getJoinResponse().isResult()){
						resultCallback = "Joined CIS: " + communityResultObject.getJoinResponse().getCommunity().getCommunityJid();
		
						remoteCommunity = communityResultObject.getJoinResponse().getCommunity();
						m_session.setAttribute("community", remoteCommunity);
					}else{
						resultCallback = "Failure when trying to joined CIS: " + communityResultObject.getJoinResponse().getCommunity().getCommunityJid();
					}
					
				}
				if(communityResultObject.getWho() != null){
					LOG.debug("### " + communityResultObject.getWho().getParticipant().size());

					m_session.setAttribute("community", remoteCommunity);
					List<org.societies.api.schema.cis.community.Participant> l = communityResultObject.getWho().getParticipant();					
					m_session.setAttribute("remoteMemberRecords", l);
				}

			}
		}
	};
	
}
