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
package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.privacynegotiation.PrivacyPolicyNegotiationManager;

/**
 * @author Elizabeth
 *
 */
public class ClientResponsePolicyGenerator_Deprecated {
/*
	private PPNPOutcomeLocator locator;
	private IPrivacyPreferenceManager privPrefMgr;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private ICtxBroker ctxBroker;
	private final PrivacyPolicyNegotiationManager policyMgr;
	private CtxEntityIdentifier personEntityID;
	private IUserFeedback userFeedback;
	public ClientResponsePolicyGenerator_Deprecated(PrivacyPolicyNegotiationManager policyMgr){
		this.policyMgr = policyMgr;
		this.privPrefMgr = policyMgr.getPrivacyPreferenceManager();
		this.ctxBroker = policyMgr.getCtxBroker();
		this.locator = new PPNPOutcomeLocator(policyMgr);
		this.userFeedback = policyMgr.getUserFeedback();
	}

	
	 * ALGORITHM:
	 * FOR each RequestItem in the policy:
	 * 		get the most appropriate PPNPOutcome using the PPNPOutcomeLocator 
	 * 		IF the returned PPNPOutcome has effect BLOCK
	 * 			IF the requestItem is optional 
	 * 				add the item with a Decision.DENY to my responses
	 * 			ELSE
	 * 				notify the user that the located PPNPOutcome will fail the negotiation and request to add a new PPNPOutcome
	 * 				IF the user replies YES
	 * 					popup the PPNPOutcomeDialog and get a new PPNPOutcome
	 * 				ELSE 
	 * 					add the requestItem with a Decision.DENY to my responses
	 * 		ELSE (the returned PPNPOutcome has effect ALLOW)
	 * 			A) first check the Actions:
	 * 			FOR each action in the request
	 * 				IF action doesn't exist in the PPNPOutcome.getRuleTarget().getActions() list
	 * 					IF the action is optional
	 * 						remove the action from the RequestItem
	 * 						add the responseItem with the updated RequestItem to my responses
	 * 					ELSE
	 * 						ask the user
	 * 						IF the user says NO
	 * 							remove the action from the RequestItem
	 * 			B) check the Conditions
	 * 			FOR each Condition in the request
	 * 				IF condition exists in the PPNPOutcome.getConditions
	 * 					IF the values are different
	 * 						IF CONDITION OPTIONAL
	 * 							remove the condition from the item and add the responseItem with the updated RequestItem in my responses
	 * 						ELSE
	 * 							ask the user (use FeedbackGUI)
	 * 							IF the user says NO
	 * 								remove the condition from the item and add the responseItem with the updated RequestItem in my responses
	 * 
	 * 				ELSE
	 * 					IF CONDITION OPTIONAL
	 * 							remove the condition from the item and add the responseItem with the updated RequestItem in my responses
	 * 					ELSE
	 * 							ask the user
	 * 							IF the user says NO
	 * 								remove the condition from the item and add the responseItem with the updated RequestItem in my responses
	 * 					 
	 
	public ResponsePolicy generatePolicy(RequestPolicy providerPolicy){

		//JOptionPane.showMessageDialog(null, "Generating privacy Policy");
		RequestorBean requestor = providerPolicy.getRequestor();
		ResponsePolicy myResponse = new ResponsePolicy();
		myResponse.setRequestor(requestor);
		myResponse.setNegotiationStatus(NegotiationStatus.ONGOING);
		myResponse.setResponseItems(new ArrayList<ResponseItem>());
		List<RequestItem> requestItems = providerPolicy.getRequestItems();

		boolean isExactMatch = true;

		for (RequestItem item : requestItems){

			//if this item DOES NOT have a CREATE action
			if (!this.hasCreate(item.getActions())){
				//if this attribute DOES NOT exist in the context DB
				if (!this.attrExistsInContext(item.getResource().getDataType())){

					//boolean attrCreated = this.createAttribute(requestor,item.getResource().getContextType());
					//if (!attrCreated){
					ResponseItem tempItem = new ResponseItem();
					tempItem.setDecision(Decision.NOT_APPLICABLE);
					tempItem.setRequestItem(item);
					myResponse.getResponseItems().add(tempItem));
					isExactMatch = false;
					continue;
					//}
				}
			}
			PPNPOutcome outcome = this.locator.getPPNPOutcome(requestor, item);
						if (null==outcome){
				JOptionPane.showMessageDialog(null, "Retrieved NULL outcome for:"+item.getResource().getContextType());
			}else{
				JOptionPane.showMessageDialog(null, "Retrieved outcome for:"+item.getResource().getContextType());
			}
			//JOptionPane.showMessageDialog(null, outcome.toString());
			if (outcome.getDecision().equals(Decision.DENY)){
				if(item.isOptional()){
					ResponseItem tempItem = new ResponseItem();
					tempItem.setDecision(Decision.DENY);
					tempItem.setRequestItem(item);
					myResponse.getResponseItems().add(tempItem);
					isExactMatch = false;
				}else{
					String fail = "Fail negotiation";
					String noFail = "Ignore my preference once";
					String proposalText = "Preference for item: "+item.getResource().getDataType()+"\nfound but will most probably fail the negotiation.";
					List<String> response = new ArrayList<String>();
					try {
						response = this.userFeedback.getExplicitFB(ExpProposalType.ACKNACK, new ExpProposalContent(proposalText, new String[]{fail,noFail})).get();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int n = JOptionPane.showConfirmDialog(null, "Preference for item: "+item.getResource().getDataType()
							+"\nfound but will most probably fail the negotiation.\n"
							+"\nWould you like to setup a different Privacy Preference?", 
							"Privacy Policy Negotiation",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
							);
					
					if (response.contains(noFail)){
						ResponseItem tempItem = new ResponseItem();
						tempItem.setDecision(Decision.PERMIT);
						tempItem.setRequestItem(item);
						myResponse.getResponseItems().add(tempItem);
						
					}else{
						ResponseItem tempItem = new ResponseItem();
						tempItem.setDecision(Decision.DENY);
						tempItem.setRequestItem(item);
						myResponse.getResponseItems().add(tempItem);
					}
					if (n==JOptionPane.YES_OPTION){
						PPNPOutcome temp = this.getOutcomeUsingGUI(item, requestor);
						if (temp==null){
							myResponse.addResponseItem(new ResponseItem(item, Decision.DENY));
							isExactMatch = false;
						}else{
							myResponse.addResponseItem(new ResponseItem(item, Decision.PERMIT));
						}
					}else{
						myResponse.addResponseItem(new ResponseItem(item, Decision.DENY));
						isExactMatch = false;
					}
				}
			}else{
				boolean changed = false;
				List<Action> newActions = new ArrayList<Action>();
				List<Action> requestedActions = item.getActions();
				for (Action action : requestedActions){
					List<Action> myActions = outcome.getRuleTarget().getActions();
					if (containsAction(myActions,action)){
						newActions.add(action);
					}else{
						if (action.isOptional()){
							changed = true;
							isExactMatch = false;
						}else{
							String question = "";
							if (requestor instanceof RequestorCis){
								question = "The Administrator :"+requestor.getRequestorId().toString()+"\nof CIS: "+((RequestorCis) requestor).getCisRequestorId()+"\n"
										+"requests access for a "+action.getActionConstant()+" operation\n"
										+"to resource: "+item.getResource().getDataType()
										+"\nAllow?";
							}else if (requestor instanceof RequestorService){
								question = "The Provider :"+requestor.getRequestorId().toString()+"\nof service: "+((RequestorService) requestor).getRequestorServiceId()+"\n"
										+"requests access for a "+action.getActionConstant()+" operation\n"
										+"to resource: "+item.getResource().getDataType()
										+"\nAllow?";
							}

							try {
								List<String> responses = this.userFeedback.getExplicitFB(ExpProposalType.ACKNACK, new ExpProposalContent(question, new String[]{"Yes","No"})).get();
								if (responses.get(0).equalsIgnoreCase("yes")){
									newActions.add(action);

								}else{
									changed = true;
									isExactMatch = false;
								}
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							int n = JOptionPane.showConfirmDialog(null, question, 
									"Privacy Policy Negotiation",
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	

						}
					}	
				}
				item.setActions(newActions);
				//CHECKING CONDITIONS

				//can't remove conditions inside the for loop
				//make a new list and then replace this list 
				List<Condition> newConditionsList = new ArrayList<Condition>();
				List<Condition> conditionsInRequest = item.getConditions();
				List<Condition> myConditions = outcome.getConditions();
				for (Condition condition : conditionsInRequest){
					Condition myCondition = containsIgnoreValue(myConditions, condition); 
					if (myCondition==null){//requested condition doesn't exist in my conditions list
						if (!condition.isOptional()){//if it's not optional, ask the user
							String question = "";
							if (requestor instanceof RequestorCis){
								question = "The Administrator: "+requestor.getRequestorId().toString()
										+"\n of the CIS: "+((RequestorCis) requestor).getCisRequestorId()+"\n"
										+"requests to add the condition: "+condition.getConditionName()+"\n"
										+" to the Negotiation Agreement. Agree?";
							}else if (requestor instanceof RequestorService){
								question = "The Provider "+requestor.getRequestorId().toString()
										+"\nof service: "+((RequestorService) requestor).getRequestorServiceId().toString()+"\n"
										+"requests to add the condition: "+condition.getConditionName()+"\n"
										+" to the Negotiation Agreement. Agree?";
							}
							
							try {
								List<String> responses = this.userFeedback.getExplicitFB(ExpProposalType.ACKNACK, new ExpProposalContent(question, new String[]{"Yes", "No"})).get();
								if (responses.get(0).equalsIgnoreCase("yes")){
									newConditionsList.add(condition);
								}else{
									changed=true;
									isExactMatch=false;
								}
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							int n = JOptionPane.showConfirmDialog(null, question, 
									"Privacy Policy Negotiation",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							if (n == JOptionPane.YES_OPTION){//if the user accepts the provider's condition, add it to the new list
								newConditionsList.add(condition);
							}else{//else don't add it to the list but flag that the provider's policy and the user's are not 100% match
								changed = true;
								isExactMatch = false;
							}
						}else{//if it's optional, don't add it to the list but flag that the provider's policy and the user's are not 100% match
							changed = true;
							isExactMatch = false;
						}
					}else{//condition exists in both provider and user policies
						//compare the values
						if (!(condition.getValueAsString().equalsIgnoreCase(myCondition.getValueAsString()))){
							//values don't match so, if it's optional, we add the user's version of the condition to the list (changed condition)
							if (condition.isOptional()){
								newConditionsList.add(myCondition);
								changed = true;
								isExactMatch = false;
							}else{ //it's not optional, so wer'e going to ask the user
								String question = "";
								if (requestor instanceof RequestorCis){
									question = "The Administrator: "+requestor.getRequestorId().toString()
											+"\nof the CIS: "+((RequestorCis) requestor).getCisRequestorId()+"\n"
											+"requests to replace the value of condition: "+condition.getConditionName()+"\n"
											+"with current value: "+myCondition.getValueAsString()+" with the value of: "+condition.getValueAsString();
								}else if (requestor instanceof RequestorService){
									question = "The Provider :"+requestor.getRequestorId().toString()
											+"\nof the service: "+((RequestorService) requestor).getRequestorServiceId()+"\n"
											+"requests to replace the value of condition: "+condition.getConditionName()+"\n"
											+"with current value: "+myCondition.getValueAsString()+" with the value of: "+condition.getValueAsString();
								}
								
								List<String> responses;
								try {
									responses = this.userFeedback.getExplicitFB(ExpProposalType.ACKNACK, new ExpProposalContent(question, new String[]{"Yes", "No"})).get();
									if (responses.get(0).equalsIgnoreCase("Yes")){ //if the user agrees, add the provider's condition to the list
										newConditionsList.add(condition);
									}else{ //otherwise, add the user's version of the condition
										newConditionsList.add(myCondition);
										changed = true;
										isExactMatch = false;
									}
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ExecutionException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								int n = JOptionPane.showConfirmDialog(null, question, 
										"Privacy Policy Negotiation",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
								if (n==JOptionPane.YES_OPTION){ //if the user agrees, add the provider's condition to the list
									newConditionsList.add(condition);
								}else{ //otherwise, add the user's version of the condition
									newConditionsList.add(myCondition);
									changed = true;
									isExactMatch = false;
								}
							}
						}
					}
				}
				//check whether any of my conditions don't exist in their conditions list and add it to the conditions list
				for (Condition myCondition : myConditions){
					Condition theirCondition = containsIgnoreValue(conditionsInRequest, myCondition);
					if (theirCondition==null){ //my condition doesn't exist in their conditions list
						newConditionsList.add(myCondition);
					}
				}
				item.setConditions(newConditionsList);
				if (changed){
					myResponse.addResponseItem(new ResponseItem(item, Decision.INDETERMINATE));
				}else{
					myResponse.addResponseItem(new ResponseItem(item, Decision.PERMIT));
				}
			}
		}

		if (isExactMatch){
			myResponse.setStatus(NegotiationStatus.SUCCESSFUL);
		}else{
			myResponse.setStatus(NegotiationStatus.ONGOING);
		}
		//JOptionPane.showMessageDialog(null, "Created responsePolicy and returning");
		return myResponse;

	}
	private Condition containsIgnoreValue(List<Condition> list, Condition c){
		for (Condition con : list){
			if (c.getConditionName().equals(con.getConditionName())){
				return con;
			}
		}
		return null;
	}

	private boolean containsAction(List<Action> actions, Action a){
		for (Action action : actions){
			if (action.getActionConstant().equals(a.getActionConstant())){
				return true;
			}
		}

		return false;
	}

	private PPNPOutcome getOutcomeUsingGUI(RequestItem item, Requestor subject){
		PPNPOutcomeDialog guiDialog = new PPNPOutcomeDialog(subject, this.getClass().getName(), item,this.privPrefMgr);
		if (guiDialog.wasAccepted()){
			PPNPOutcome outcome = guiDialog.getOutcome();
			if (outcome==null){
				this.logging.debug("OUTCOME IS NULL :(");
			}else{
				this.logging.debug("PPNPOutcomeLocator: got outcome");
			}
			return outcome;
		}
		this.logging.debug("Outcome not accepted.");
		return  null;

		//return outcome;
	}


	private boolean attrExistsInContext(String type){
		try {

			Future<IndividualCtxEntity> futurePerson = ctxBroker.retrieveCssOperator();
			CtxEntity person = futurePerson.get();


			Set<CtxAttribute> attrs = person.getAttributes(type);
			if (attrs.size()>0){
				return true;
			}
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}


	private boolean hasCreate(List<Action> actions){
		Iterator<Action> it = actions.iterator();
		while (it.hasNext()){
			Action a = it.next();
			if (a.getActionConstant().equals(ActionConstants.CREATE)){
				return true;
			}
		}
		return false;
	}

	private boolean createAttribute(Requestor requestor, String type){
		String question="";
		if (requestor instanceof RequestorCis){
			question = "The CIS: "+((RequestorCis) requestor).getCisRequestorId().toString()+" requests access to a non-existing attribute: "+type+"\nCreate the attribute and set its value or abort service?";
		}else if (requestor instanceof RequestorService){
			question = "Service: "+((RequestorService) requestor).getRequestorServiceId()+" requests access to a non-existing attribute: "+type+"\nCreate the attribute and set its value or abort service?";
		}
		String permit = "Allow access";
		String deny = "Deny access";
		try {
			List<String> response = this.userFeedback.getExplicitFB(ExpProposalType.ACKNACK, new ExpProposalContent(question, new String[]{permit,deny})).get();
			//int n = JOptionPane.showConfirmDialog(null, question, "Non-Existing Attribute", JOptionPane.YES_NO_OPTION);
			if (response.contains(permit)){

				CtxEntityIdentifier personEntityIdentifier = this.getPersonEntity(); 
				if (personEntityIdentifier==null){
					this.logging.debug("Unable to find Person entity");
					return false;
				}
				CtxAttribute attr = this.ctxBroker.createAttribute(personEntityIdentifier, type).get();
				
				
				String value = (String)JOptionPane.showInputDialog(
						null,
						"Enter a value for "+ type,
						"New Context Attribute :"+type,
						JOptionPane.PLAIN_MESSAGE,
						null,
						null,
						"");
				String value= "";
				if (value!=null){
					attr.setStringValue(value);
					ctxBroker.update(attr);
				}
				return true;
			}
		}catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	private CtxEntityIdentifier getPersonEntity(){

		if (null==personEntityID){


			try {
				
				Future<IndividualCtxEntity> futurePerson = this.ctxBroker.retrieveIndividualEntity(this.policyMgr.getIdm().getThisNetworkNode());
				IndividualCtxEntity personEntity = futurePerson.get();
				if (null==personEntity){
					return null;
				}
				personEntityID = personEntity.getId();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return personEntityID;
	}*/
}
