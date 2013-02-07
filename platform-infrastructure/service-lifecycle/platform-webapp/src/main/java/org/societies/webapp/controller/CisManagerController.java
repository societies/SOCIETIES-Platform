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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.webapp.models.CisCreationForm;
import org.societies.webapp.models.CisManagerForm;
import org.societies.webapp.models.PrivacyActionForm;
import org.societies.webapp.models.PrivacyConditionForm;
import org.societies.webapp.models.PrivacyPolicyResourceForm;
import org.societies.webapp.models.WebAppParticipant;
import org.societies.webapp.models.privacy.CisCtxAttributeHumanTypes;
import org.societies.webapp.models.privacy.CisCtxAttributeTypes;
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
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.comm.ICommManagerController;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.identity.DataIdentifierScheme;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;


@Controller
public class CisManagerController {

	/**
	 * OSGI service get auto injected
	 */

	
	@Autowired
	private ICisManager cisManager;
	@Autowired(required=false)
	private IPrivacyPolicyManager privacyPolicyManager;
	@Autowired
	private ICommManager commMngrRef;
	//@Autowired
	//private IUserFeedback userFeedback;
	

	// -- Data for the Privacy Policy Form
	private static String[] resourceList;
	private static String[] resourceHumanList;
	private static String[] resourceSchemeList;
	
	
	List<Participant> m_remoteMemberRecords = new ArrayList<Participant>();

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

	@RequestMapping(value = "/createnewcis.html", method = RequestMethod.GET)
	public ModelAndView createNewCis() {

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
		
		//Auto populate 
		cisForm.setCssId(getCommMngrRef().getIdManager().getThisNetworkNode().getBareJid());
		cisForm.setCisType("RICH");
		cisForm.setCisMode(0);
		
		model.put("cmForm", cisForm);
		
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
		return new ModelAndView("createNewCis", model);
	}

	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/cismanager.html", method = RequestMethod.POST)
	public ModelAndView cssManager(@Valid CisManagerForm cisForm, BindingResult result, Map model, HttpSession session) {

		boolean displayFormAtResult = true;
		
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

				// -- Retrieve CIS Configuration
				Hashtable<String, MembershipCriteria> cisCriteria = null;
				if(null !=cisForm && null != cisForm.getValue() && false == cisForm.getValue().isEmpty()){
					cisCriteria = new Hashtable<String, MembershipCriteria> (); 
					MembershipCriteria m = new MembershipCriteria();
					try{
						Rule r = new Rule(cisForm.getOperator(),new ArrayList(Arrays.asList(cisForm.getValue())));
						m.setRule(r);
						cisCriteria.put(cisForm.getAttribute(), m);
						LOG.info("Membership criteria rule #1: "+ cisForm.getAttribute() + ", " + cisForm.getOperator()+ ", " + cisForm.getValue());
					}
					catch(InvalidParameterException e){
						throw new Exception(" excepation of invalid param " + cisForm.getAttribute() + ", " + cisForm.getOperator()+ ", " + cisForm.getValue());
					}
				}

				// -- Fill CisCreationForm for next step
				CisCreationForm cisCreationForm = generateCisCreationForm(cisForm, cisCriteria);
				cisCreationForm.setMode("SHARED");
				LOG.info("Proposed CIS Privacy Policy");
				LOG.info(cisCreationForm.toString());


				// Send page to generate the privacy policy
				generateResourceLists();
				model.put("res", res);
				model.put("cisCreationForm", cisCreationForm);
				model.put("ActionList", new String[]{"READ", "WRITE", "CREATE", "DELETE"});//ActionConstants.values());
				model.put("ConditionList", ConditionConstants.values());
				model.put("ResourceList", resourceList);
				model.put("ResourceHumanList", resourceHumanList);
				model.put("ResourceSchemeList", resourceSchemeList);
				return new ModelAndView("privacy/privacy-policy/create-cis-step-2", model);

				//				model.put("methodcalled", "CreateCis");
				//				res = "Creating CIS...";
				//				
				//				Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria> (); 
				//				MembershipCriteria m = new MembershipCriteria();
				//				try{
				//					Rule r = new Rule(cisForm.getOperator(),new ArrayList(Arrays.asList(cisForm.getValue())));
				//					m.setRule(r);
				//					cisCriteria.put(cisForm.getAttribute(), m);
				//				}
				//				catch(InvalidParameterException e){
				//					// TODO: treat expection
				//					res += " excepation of invalid param " + cisForm.getAttribute() + ", " + cisForm.getOperator()+ ", " + cisForm.getValue();
				//				}
				//				
				//				Future<ICisOwned> cisResult = this.getCisManager().createCis(
				//						cisForm.getCisName(),
				//						cisForm.getCisType(),cisCriteria,""
				//						); // for some strange reason null instead of cisCriteria did not work
				//
				//				res = "Successfully created CIS: " + cisResult.get().getCisId();
				//				//localCISs.add(cisResult.get());

			} else if (method.equalsIgnoreCase("GetCisList")) {
				model.put("methodcalled", "GetCisList");

				//ICisRecord searchRecord = null;
				//ICisRecord[] records = this.getCisManager().getCisList(searchRecord);
				List<ICis> records = this.getCisManager().getCisList();
				IIdentity currentNodeId = commMngrRef.getIdManager().getThisNetworkNode();
				model.put("cisrecords", records);
				model.put("currentNodeId", currentNodeId);

			} else if (method.equalsIgnoreCase("JoinRemoteCIS")) {
				model.put("methodcalled", "JoinRemoteCIS");

				// TODO: get a real advertisement
				LOG.info("[CisManagerController] "+cisForm);
				CisAdvertisementRecord ad = new CisAdvertisementRecord();
				ad.setId(cisForm.getCisJid());
				LOG.info(" cisForm.getCssId() is " +  cisForm.getCssId());
				ad.setCssownerid((null != cisForm.getCssId() && !"".equals(cisForm.getCssId())) ? cisForm.getCssId() : "university.societies.local");
				// in order to force the join to send qualifications, Ill add some criteria to the AdRecord
				//MembershipCrit membershipCrit = new MembershipCrit();
				//List<Criteria> criteria = new ArrayList<Criteria>();
				//Criteria c1 = new Criteria();c1.setAttrib(CtxAttributeTypes.ADDRESS_HOME_CITY);c1.setOperator("equals");c1.setValue1("something");
				//Criteria c2 = new Criteria();c2.setAttrib(CtxAttributeTypes.RELIGIOUS_VIEWS);c2.setOperator("equals");c2.setValue1("something");
				//criteria.add(c1);criteria.add(c2);membershipCrit.setCriteria(criteria);
				//ad.setMembershipCrit(membershipCrit);
				// done setting membership crit

				this.getCisManager().joinRemoteCIS(ad, icall);

				displayFormAtResult = false;
				
				//Thread.sleep(5 * 1000);
				//model.put("joinStatus", "pending");//resultCallback);
				/*if(!resultCallback.startsWith("Failure") ){
					ICis i = getCisManager().getCis(cisForm.getCisJid());
					model.put("cis", i);
				}*/
				res = "Join in progress...";
				model.put("res", res);

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
						Requestor req = new Requestor(this.commMngrRef.getIdManager().getThisNetworkNode());
						remoteCIS.getListOfMembers(req, icall);
						//remoteCIS.getListOfMembers(icall);
						res = "After getList";
						
						Thread.sleep(5000);
						List<WebAppParticipant> membersDetails = new ArrayList<WebAppParticipant>();
						if (m_remoteMemberRecords != null)
						{
							for ( int memberIndex  = 0; memberIndex < m_remoteMemberRecords.size(); memberIndex++)
							{
								WebAppParticipant memberDetail = new WebAppParticipant();
								memberDetail.setMembersJid(m_remoteMemberRecords.get(memberIndex).getJid());
								memberDetail.setMembershipType(m_remoteMemberRecords.get(memberIndex).getRole());
								
								membersDetails.add(memberDetail);
								
							}
						}
						model.put("memberRecords", membersDetails);
						
					}
					cisForm.setMethod("GetMemberListRemote");
					model.put("methodcalled", "GetMemberListRemote");
					model.put("method", "GetMemberListRemote");
					res = "CIS==null: " + cisForm.getMethod();
				} else {
					Set<ICisParticipant> records = thisCis.getMemberList();
					model.put("memberRecords", records);
					res = "CIS is not null";
				}

			} else if (method.equalsIgnoreCase("GetMemberListRemote")) {
				model.put("methodcalled", "GetMemberListRemote");
				model.put("method", "GetMemberListRemote");
				model.put("cisid", cisForm.getCisJid());
				//CALL REMOTE
				res += cisForm.getCisJid();
				res += "Before Remote";
				ICis remoteCIS = this.getCisManager().getCis(cisForm.getCisJid());
				Requestor req = new Requestor(this.commMngrRef.getIdManager().getThisNetworkNode());
				remoteCIS.getListOfMembers(req, icall);
				//remoteCIS.getListOfMembers(icall);
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
					if (thisCis.addMember(cisForm.getCssId(), cisForm.getRole()))
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
			// UPDATE, not always
			if(displayFormAtResult){
				IIdentity currentNodeId = commMngrRef.getIdManager().getThisNetworkNode();
				model.put("currentNodeId", currentNodeId);
				List<ICis> records = this.getCisManager().getCisList();
				model.put("cisrecords", records);
			}
		} catch (Exception ex) {
			LOG.error("Error when managing CIS", ex);
			res += "Oops!!!! <br/>" + ex.getMessage();//.getMessage();
		}

		//model.put("res", res);
		if(displayFormAtResult){
			LOG.info("going to put form");
			model.put("cmForm", cisForm);
		}
		return new ModelAndView("cismanagerresult", model);
	}


	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/createnewcis.html", method = RequestMethod.POST)
	public ModelAndView createNewCis(@Valid CisManagerForm cisForm, BindingResult result, Map model, HttpSession session) {

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
			

				model.put("methodcalled", "CreateCis");
				res = "Creating CIS...";

				// -- Retrieve CIS Configuration
				Hashtable<String, MembershipCriteria> cisCriteria = null;
				if(null !=cisForm && null != cisForm.getValue() && false == cisForm.getValue().isEmpty()){
					cisCriteria = new Hashtable<String, MembershipCriteria> (); 
					MembershipCriteria m = new MembershipCriteria();
					try{
						Rule r = new Rule(cisForm.getOperator(),new ArrayList(Arrays.asList(cisForm.getValue())));
						m.setRule(r);
						cisCriteria.put(cisForm.getAttribute(), m);
						LOG.info("Membership criteria rule #1: "+ cisForm.getAttribute() + ", " + cisForm.getOperator()+ ", " + cisForm.getValue());
					}
					catch(InvalidParameterException e){
						throw new Exception(" excepation of invalid param " + cisForm.getAttribute() + ", " + cisForm.getOperator()+ ", " + cisForm.getValue());
					}
				}

				// -- Fill CisCreationForm for next step
				CisCreationForm cisCreationForm = generateCisCreationForm(cisForm, cisCriteria);
				cisCreationForm.setMode("SHARED");
				LOG.info("Proposed CIS Privacy Policy");
				LOG.info(cisCreationForm.toString());


				// Send page to generate the privacy policy
				generateResourceLists();
				model.put("res", res);
				model.put("cisCreationForm", cisCreationForm);
				model.put("ActionList", new String[]{"READ", "WRITE", "CREATE", "DELETE"});//ActionConstants.values());
				model.put("ConditionList", ConditionConstants.values());
				model.put("ResourceList", resourceList);
				model.put("ResourceHumanList", resourceHumanList);
				model.put("ResourceSchemeList", resourceSchemeList);
	} catch (Exception ex) {
		LOG.error("Error when managing CIS", ex);
		res += "Oops!!!! <br/>" + ex.getMessage();//.getMessage();
	}
		
		return new ModelAndView("privacy/privacy-policy/create-cis-step-2", model);

			
	}

	
	@RequestMapping(value = "/create-cis-step-3.html", method = RequestMethod.POST)
	public ModelAndView createCisEnd(@Valid CisCreationForm cisCreationForm, BindingResult result, Map model, HttpSession session) {
		LOG.debug("Create CIS Step 3: CIS creation");

		// -- Verification
		if (result.hasErrors()) {
			LOG.warn("BindingResult has errors");
			model.put("errormsg", "Create CIS Step 2: privacy policy form error<br />"+result.toString()+"<br />"+result.getFieldErrors().get(0).getObjectName());
			return new ModelAndView("error", model);
		}
		LOG.debug(cisCreationForm.toString());

		// -- Storage
		RequestPolicy privacyPolicy;
		StringBuffer resultMsg = new StringBuffer();
		if (!isDepencyInjectionDone()) {
			resultMsg.append("Error with dependency injection");
			LOG.error("Error with dependency injection");
		}
		else {
			try {
				// -- CIS Configuration
				Hashtable<String, MembershipCriteria> cisCriteria = null;
				if (null !=cisCreationForm && null != cisCreationForm.getValue() && false == cisCreationForm.getValue().isEmpty()){ 
					cisCriteria = new Hashtable<String, MembershipCriteria> (); 
					MembershipCriteria m = new MembershipCriteria();
					try {
						Rule r = new Rule(cisCreationForm.getOperator(), new ArrayList(Arrays.asList(cisCreationForm.getValue())));
						m.setRule(r);
						cisCriteria.put(cisCreationForm.getAttribute(), m);
					}
					catch(InvalidParameterException e){
						resultMsg.append("Warning: Can't retrieve the membership criterii.");
					}
				}

				// -- Generate Privacy Policy
				CisCreationForm cisData;
				// If custom: keep like it is
				// Else: generate a pre-configured privacy policy
				if (!"CUSTOM".equals(cisCreationForm.getMode())) {
					LOG.info("Not CUSTOM mode, but "+cisCreationForm.getMode()+" mode");
					cisData = generateCisCreationForm(cisCreationForm, cisCriteria, cisCreationForm.getMode());
				}
				else {
					cisData = cisCreationForm;
				}

				// Generate a real privacy policy
				privacyPolicy = cisData.toRequestPolicy(commMngrRef.getIdManager());

				// -- CIS creation
				Future<ICisOwned> cisResult = this.getCisManager().createCis(
						cisData.getCisName(),
						cisData.getCisType(),
						cisCriteria,
						"",
						privacyPolicy.toXMLString()
						); // for some strange reason null instead of cisCriteria did not work

				ICisOwned newCis = cisResult.get();
				if (null == newCis) {
					throw new NullPointerException("Can't create the CIS");
				}

				// Result
				resultMsg.append("The CIS '"+cisCreationForm.getCisName()+"' has been successfully created!");
				resultMsg.append("New CIS Id "+newCis.getCisId());
			} catch(InvalidParameterException e){
				resultMsg.append("Error during the CIS creation: "+e.getMessage());
				LOG.error("Error during the CIS creation", e);
			} catch (InterruptedException e) {
				resultMsg.append("Error during the CIS creation: "+e.getMessage());
				LOG.error("Error during the CIS creation (interrupted)", e);
			} catch (ExecutionException e) {
				resultMsg.append("Error during the CIS creation: "+e.getMessage());
				LOG.error("Error during the CIS creation (excecution)", e);
			} catch (InvalidFormatException e) {
				resultMsg.append("Error during the CIS privacy policy saving: "+e.getMessage());
				LOG.error("Error during the CIS privacy policy saving", e);
			} catch (MalformedCtxIdentifierException e) {
				resultMsg.append("Error during the CIS privacy policy (retrieve resource) saving: "+e.getMessage());
				LOG.error("Error during the CIS privacy policy (retrieve resource) saving", e);
			} catch (IllegalArgumentException e) {
				resultMsg.append("Error during the CIS privacy policy (illegal argument) saving: "+e.getMessage());
				LOG.error("Error during the CIS privacy policy (illegal argument) saving", e);
			} catch (IllegalAccessException e) {
				resultMsg.append("Error during the CIS privacy policy (illegal access) saving: "+e.getMessage());
				LOG.error("Error during the CIS privacy policy (illegal access) saving", e);
			}
		}

		// -- Display the result
		model.put("res", resultMsg.toString());
		model.put("methodcalled", "CreateCis");
		return new ModelAndView("cismanagerresult", model);
	}

	public static void generateResourceLists() throws IllegalArgumentException, IllegalAccessException {
		Field[] resourceTypeArray = CisCtxAttributeTypes.class.getDeclaredFields();
		Field[] resourceHumanTypeArray = CisCtxAttributeHumanTypes.class.getDeclaredFields();
		resourceList = new String[resourceTypeArray.length];
		resourceHumanList = new String[resourceTypeArray.length];
		for(int i=0; i<resourceTypeArray.length; i++) {
			resourceList[i] = DataIdentifierScheme.CONTEXT+":///"+((String)resourceTypeArray[i].get(null));
			resourceHumanList[i] = DataIdentifierScheme.CONTEXT+": "+((String)resourceHumanTypeArray[i].get(null));
		}

		DataIdentifierScheme[] schemes = DataIdentifierScheme.values();
		resourceSchemeList = new String[schemes.length];
		for(int j=0; j<schemes.length; j++) {
			resourceSchemeList[j] = schemes[j].value();
		}
	}

	public CisCreationForm generateCisCreationForm(CisManagerForm cisForm, Map<String, MembershipCriteria> cisCriteria) throws IllegalArgumentException, IllegalAccessException {
		return generateCisCreationForm(cisForm, cisCriteria, "SHARED");
	}
	public CisCreationForm generateCisCreationForm(CisManagerForm cisForm, Map<String, MembershipCriteria> cisCriteria, String mode) throws IllegalArgumentException, IllegalAccessException {
		generateResourceLists();
		// -- Fill CisCreationForm for next step
		CisCreationForm cisCreationForm = new CisCreationForm();
		if (null != cisForm) {
			cisCreationForm.fillCisConfiguration(cisForm);
		}

		// -- Prepare condition list
		List<PrivacyConditionForm> conditionsCisMemberList = new ArrayList<PrivacyConditionForm>();
		List<PrivacyConditionForm> conditionsCisMembershipCriteria = new ArrayList<PrivacyConditionForm>();
		List<PrivacyConditionForm> conditionsCisCommunityContext = new ArrayList<PrivacyConditionForm>();
		if ("SHARED".equals(mode)) {
			LOG.info("SHARED mode");
			conditionsCisMemberList.add(new PrivacyConditionForm(ConditionConstants.RIGHT_TO_OPTOUT, "1", false));
			conditionsCisMemberList.add(new PrivacyConditionForm(ConditionConstants.STORE_IN_SECURE_STORAGE, "1", false));
			conditionsCisMemberList.add(new PrivacyConditionForm(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY, "1", false));

			conditionsCisCommunityContext.add(new PrivacyConditionForm(ConditionConstants.RIGHT_TO_OPTOUT, "1", false));
			conditionsCisCommunityContext.add(new PrivacyConditionForm(ConditionConstants.STORE_IN_SECURE_STORAGE, "1", false));
			conditionsCisCommunityContext.add(new PrivacyConditionForm(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY, "1", false));
		}
		else if ("PUBLIC".equals(mode)) {
			LOG.info("PUBLIC mode");
			conditionsCisMemberList.add(new PrivacyConditionForm(ConditionConstants.RIGHT_TO_OPTOUT, "1", false));
			conditionsCisMemberList.add(new PrivacyConditionForm(ConditionConstants.STORE_IN_SECURE_STORAGE, "1", false));
			conditionsCisMemberList.add(new PrivacyConditionForm(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1", false));


			conditionsCisCommunityContext.add(new PrivacyConditionForm(ConditionConstants.RIGHT_TO_OPTOUT, "1", false));
			conditionsCisCommunityContext.add(new PrivacyConditionForm(ConditionConstants.STORE_IN_SECURE_STORAGE, "1", false));
			conditionsCisCommunityContext.add(new PrivacyConditionForm(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1", false));
		}
		else {
			LOG.info("PRIVATE mode");
			conditionsCisMemberList.add(new PrivacyConditionForm(ConditionConstants.RIGHT_TO_OPTOUT, "1", false));
			conditionsCisMemberList.add(new PrivacyConditionForm(ConditionConstants.STORE_IN_SECURE_STORAGE, "1", false));
			conditionsCisMemberList.add(new PrivacyConditionForm(ConditionConstants.SHARE_WITH_CIS_OWNER_ONLY, "1", false));

			conditionsCisCommunityContext.add(new PrivacyConditionForm(ConditionConstants.RIGHT_TO_OPTOUT, "1", false));
			conditionsCisCommunityContext.add(new PrivacyConditionForm(ConditionConstants.STORE_IN_SECURE_STORAGE, "1", false));
			conditionsCisCommunityContext.add(new PrivacyConditionForm(ConditionConstants.SHARE_WITH_CIS_OWNER_ONLY, "1", false));
		}
		conditionsCisMembershipCriteria.add(new PrivacyConditionForm(ConditionConstants.RIGHT_TO_OPTOUT, "1", false));
		conditionsCisMembershipCriteria.add(new PrivacyConditionForm(ConditionConstants.MAY_BE_INFERRED, "1", false));
		conditionsCisMembershipCriteria.add(new PrivacyConditionForm(ConditionConstants.SHARE_WITH_CIS_OWNER_ONLY, "1", false));

		// -- Infer first version of the privacy policy: using CIS member list
		PrivacyPolicyResourceForm resourceCisMemberList = new PrivacyPolicyResourceForm();
		resourceCisMemberList.setResourceSchemeCustom(DataIdentifierScheme.CIS.value());
		resourceCisMemberList.setResourceTypeCustom("cis-member-list");
		resourceCisMemberList.addAction(new PrivacyActionForm(ActionConstants.READ));
		resourceCisMemberList.addAction(new PrivacyActionForm(ActionConstants.CREATE));
		resourceCisMemberList.setConditions(conditionsCisMemberList);
		cisCreationForm.addResource(resourceCisMemberList);
		// -- Infer first version of the privacy policy: using membership criteria
		if (null != cisCriteria && cisCriteria.size() > 0) {
			PrivacyPolicyResourceForm resource = new PrivacyPolicyResourceForm();
			resource.setResourceType(DataIdentifierScheme.CONTEXT+":///"+cisCreationForm.getAttribute());
			resource.addAction(new PrivacyActionForm(ActionConstants.READ));
			resource.addAction(new PrivacyActionForm(ActionConstants.CREATE));
			resource.setConditions(conditionsCisMembershipCriteria);
			cisCreationForm.addResource(resource);
		}
		// -- Infer first version of the privacy policy: using Community Context Data
		for(int i=0; i<resourceList.length; i++) {
			PrivacyPolicyResourceForm resource = new PrivacyPolicyResourceForm();
			resource.setResourceType(resourceList[i]);
			resource.addAction(new PrivacyActionForm(ActionConstants.READ));
			resource.addAction(new PrivacyActionForm(ActionConstants.CREATE));
			resource.setConditions(conditionsCisCommunityContext);
			cisCreationForm.addResource(resource);
		}
		return cisCreationForm;
	}

	// callback
	WebAppCISCallback icall = new WebAppCISCallback();//this.userFeedback);

	
	public class WebAppCISCallback implements ICisManagerCallback{
		public WebAppCISCallback(){//IUserFeedback userFeedback){
			super();
			//this.userFeedback = userFeedback;
		}
		
		//IUserFeedback userFeedback;
		
		

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
							LOG.info("callback for join regarindg community " + community);
							//this.userFeedback.showNotification("Joined CIS: " + community);
							resultCallback = "Joined CIS " + community;
							remoteCommunity = communityResultObject.getJoinResponse().getCommunity();
							m_session.setAttribute("community", remoteCommunity);
							LOG.info("done at join response");
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
					LOG.debug("### " + communityResultObject.getWhoResponse().getParticipant().size());

					m_session.setAttribute("community", remoteCommunity);
					m_remoteMemberRecords = communityResultObject.getWhoResponse().getParticipant();					
					m_session.setAttribute("remoteMemberRecords", m_remoteMemberRecords);
				}

			}
		}
	}
	
	// -- Dependency Injection
	private boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	private boolean isDepencyInjectionDone(int level) {
		if (null == commMngrRef) {
			LOG.info("[Dependency Injection] Missing ICommManager");
			return false;
		}
		if (null == commMngrRef.getIdManager()) {
			LOG.info("[Dependency Injection] Missing IIdentityManager");
			return false;
		}
		if (null == cisManager) {
			LOG.info("[Dependency Injection] Missing ICisManager");
			return false;
		}
		return true;
	}

	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
		LOG.info("[DepencyInjection] IPrivacyPolicyManager injected");
	}
	
	public ICommManager getCommMngrRef() {
		return this.commMngrRef;
	}
	
	
	public void setCommMngrRef(ICommManager commMngrRef) {
		this.commMngrRef = commMngrRef;
		LOG.info("[DepencyInjection] ICommManager injected");
	}
	
	public ICisManager getCisManager() {
		return cisManager;
	}
	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/pilotmycommunities.html", method = RequestMethod.GET)
	public ModelAndView myCommumities()

	{
		Map<String, Object> model = new HashMap<String, Object>();
		
		
		model.put("message", "Welcome to the CIS Manager Page");
		// cismanager result page seems to want this for some reason!??
		CisManagerForm cisForm = new CisManagerForm();
		cisForm.setMethod("GetCisList");
		model.put("cisForm", cisForm);
		List<String> methodList = new ArrayList<String>();
		methodList.add(new String("GetCisList"));
		model.put("method", methodList);
		model.put("methodcalled", "GetCisList");

		if (getCisManager() == null) {
			model.put("errormsg", "CIS Manager Service reference not avaiable");
			return new ModelAndView("error", model);
		}
		String res = "Starting...";
		

		try {
			
				//ICisRecord searchRecord = null;
				//ICisRecord[] records = this.getCisManager().getCisList(searchRecord);
				List<ICis> records = this.getCisManager().getCisList();
				IIdentity currentNodeId = commMngrRef.getIdManager().getThisNetworkNode();
				model.put("cisrecords", records);
				model.put("currentNodeId", currentNodeId);

		} catch (Exception ex) {
			LOG.error("Error when managing CIS", ex);
			res += "Oops!!!! <br/>" + ex.getMessage();//.getMessage();
		}

		model.put("res", res);
		
		return new ModelAndView("pilotmycommunities", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/mycisappspilot.html", method = RequestMethod.GET)
	public ModelAndView myCommumityApps()

	{
		Map<String, Object> model = new HashMap<String, Object>();
		
		
		model.put("message", "Welcome to the CIS Apps Page");
		// cismanager result page seems to want this for some reason!??
		CisManagerForm cisForm = new CisManagerForm();
		cisForm.setMethod("GetCisList");
		model.put("cisForm", cisForm);
		List<String> methodList = new ArrayList<String>();
		methodList.add(new String("GetCisList"));
		model.put("method", methodList);
		model.put("methodcalled", "GetCisList");

		if (getCisManager() == null) {
			model.put("errormsg", "CIS Manager Service reference not avaiable");
			return new ModelAndView("error", model);
		}
		String res = "Starting...";
		

		try {
			
				//ICisRecord searchRecord = null;
				//ICisRecord[] records = this.getCisManager().getCisList(searchRecord);
				List<ICis> records = this.getCisManager().getCisList();
				IIdentity currentNodeId = commMngrRef.getIdManager().getThisNetworkNode();
				model.put("cisrecords", records);
				model.put("currentNodeId", currentNodeId);

		} catch (Exception ex) {
			LOG.error("Error when managing CIS", ex);
			res += "Oops!!!! <br/>" + ex.getMessage();//.getMessage();
		}

		model.put("res", res);
		
		return new ModelAndView("mycisappspilot", model);
	}


}
