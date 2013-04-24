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
package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.provider;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;


/**
 * @author Elizabeth
 *
 */
public class ProviderResponsePolicyGenerator {


	//private IFeedbackMgmt feedbackMgr;

	public ProviderResponsePolicyGenerator(){
	}
	
	/**
	 * TODO urgent: fix this method that didn't accept to deny optional RequestItem, Action or Condition!!!
	 * @param clientResponse
	 * @param myPolicy
	 * @return
	 */
	public ResponsePolicy generateResponse(ResponsePolicy clientResponse, RequestPolicy myPolicy){
		if (clientResponse.getNegotiationStatus().equals(NegotiationStatus.FAILED)){
			//JOptionPane.showMessageDialog(null, "Provider: Negotiation Failed 1");
			ResponsePolicy toReturn = new ResponsePolicy();
			toReturn.setNegotiationStatus(NegotiationStatus.FAILED);
			toReturn.setRequestor(myPolicy.getRequestor());
			toReturn.setResponseItems(new ArrayList<ResponseItem>());
			return toReturn;
		}

		/*
		 * Algorithm: 
		 * for every response Item in the policy:
		 * IF Decision.PERMIT 
		 * 		then leave as is
		 * else IF Decision.DENY OR Decision.NOT_APPLICABLE
		 * 		IF responseItem.getRequestItem.isOptional()
		 * 			remove from ResponsePolicy
		 * 		ELSE
		 * 			set NegotiationStatus.FAILED and return ResponsePolicy with empty ResponseItems  list
		 * else IF Decision.INDETERMINATE 
		 * 		a) compare list of actions from client and list of actions from my policy
		 * 			IF action exists in my policy but not in client list
		 * 				IF Action.isOptional()
		 * 					leave as is
		 * 				ELSE 
		 * 					set negotiationStatus.FAILED and return ResponsePolicy with empty ResponseItems list
		 * 		b) compare list of conditions from client and list of conditions from my policy
		 * 
		 * 			IF condition exists in client but not in my policy
		 * 				use FeedbackGUI /later use preferences
		 * 				IF feedback
		 * 					leave as is
		 * 				ELSE
		 * 					set negotiationStatus.FAILED and return ResponsePolicy with empty ResponseItems list
		 * 			ELSE
		 * 				IF condition exists in my policy but not in client:
		 * 					IF condition.isOptional()
		 * 						leave as is
		 * 					ELSE
		 * 						use FeedbackGUI /later use preferences
		 * 						IF feedback
		 * 							leave as is
		 * 						ELSE
		 * 						set negotiationStatus.FAILED and return ResponsePolicy with empty ResponseItems list
		 * 			ELSE 
		 * 				IF condition exists in both policies but the value is different
		 * 					use FeedbackGUI /later use preferences
		 * 					IF feedback
		 * 						leave as is
		 * 					ELSE
		 * 						set negotiationStatus.FAILED and return ResponsePolicy with empty ResponseItems list
		 * 			ELSE
		 * 				IF condition exists in both policies and values are the same
		 * 					leave as is			 
		 */

		List<ResponseItem> clientResponseItems = clientResponse.getResponseItems();
		List<ResponseItem> itemsToRemove = new ArrayList<ResponseItem>();
		for (ResponseItem responseItem : clientResponseItems){
			if (null == responseItem.getDecision() || responseItem.getDecision().equals(Decision.DENY)  || responseItem.getDecision().equals(Decision.NOT_APPLICABLE)){
				if (responseItem.getRequestItem().isOptional()){
					//clientResponseItems.remove(responseItem);
					itemsToRemove.add(responseItem);
				}else{
					//JOptionPane.showMessageDialog(null, "Provider: Negotiation Failed 2");
					ResponsePolicy toReturn = new ResponsePolicy();
					toReturn.setNegotiationStatus(NegotiationStatus.FAILED);
					toReturn.setRequestor(myPolicy.getRequestor());
					toReturn.setResponseItems(new ArrayList<ResponseItem>());
					return toReturn;
				}
			}else if (responseItem.getDecision().equals(Decision.INDETERMINATE)){
				Resource resource = responseItem.getRequestItem().getResource(); 
				List<RequestItem> myRequests = myPolicy.getRequestItems();
				RequestItem myRequest = null;
				//get the Actions I have stated in my service privacy policy for this particular resource 
				for (RequestItem item : myRequests){
					if (item.getResource().getDataType().equals(resource.getDataType())){
						myRequest = item;
					}
				}
				if (myRequest!=null){
					List<Action> myActions = myRequest.getActions();

					List<Action> clientActions = responseItem.getRequestItem().getActions();
					//COMPARE ACTIONS * START *
					for (Action action : myActions){ 
						if (!(containsAction(clientActions,action))){
							if (!(action.isOptional())){
								//JOptionPane.showMessageDialog(null, "Provider: Negotiation Failed 3");
								ResponsePolicy toReturn = new ResponsePolicy();
								toReturn.setNegotiationStatus(NegotiationStatus.FAILED);
								toReturn.setRequestor(myPolicy.getRequestor());
								toReturn.setResponseItems(new ArrayList<ResponseItem>());
								return toReturn;
							}
						}
					}

					//COMPARE ACTIONS * END *


					//COMPARE CONDITIONS * START *
					List<Condition> clientConditions = responseItem.getRequestItem().getConditions();
					List<Condition> myConditions = myRequest.getConditions();

					//for every client condition
					for (Condition clientCondition : clientConditions){

						//check if the client condition exists in my conditions list
						Condition con = this.containsIgnoreValue(myConditions, clientCondition);
						//if condition exists in both policies
						if (con!=null){
							if (con.getValue().equalsIgnoreCase(clientCondition.getValue())){
								//value is the smae 
							}else{
								//check IF OPTIONAL
								if (clientCondition.isOptional()){
									//condition is optional so we can ignore it without bothering the user
								}
								else{
									//condition is mandatory so we're going to ask the user
									Hashtable<String,Object> params = new Hashtable<String,Object>();
									params.put("localPolicyDetails", con.getConditionConstant()+": "+con.getValue());
									params.put("remotePolicyDetails", clientCondition.getConditionConstant()+": "+clientCondition.getValue());
									//Boolean response = (Boolean) this.getFeedbackManager().getExplicitFB(FeedbackGUITypes.NEGOTIATION, params);
									//TODO: use rules - no user
									Boolean response = true;
									if (!response.booleanValue()){
										//JOptionPane.showMessageDialog(null, "Provider: Negotiation Failed 4");
										ResponsePolicy toReturn = new ResponsePolicy();
										toReturn.setNegotiationStatus(NegotiationStatus.FAILED);
										toReturn.setRequestor(myPolicy.getRequestor());
										toReturn.setResponseItems(new ArrayList<ResponseItem>());
										return toReturn;
									}
								}
							}
						}else{//condition only exists in client
							if (clientCondition.isOptional()){
								//condition is optional so we can ignore it without bothering the user
							}else{
								//condition is mandatory so we're going to ask the user
								Hashtable<String, Object> params = new Hashtable<String, Object>();
								params.put("localPolicyDetails", "You have not included this condition in your policy");
								params.put("remotePolicyDetails",clientCondition.getConditionConstant()+": "+clientCondition.getValue());
								//TODO: use rules - no user
								Boolean response = true;
								//Boolean response = (Boolean) this.getFeedbackManager().getExplicitFB(FeedbackGUITypes.NEGOTIATION, params);
								if (!response){
									//JOptionPane.showMessageDialog(null, "Provider: Negotiation Failed 5");
									ResponsePolicy toReturn = new ResponsePolicy();
									toReturn.setNegotiationStatus(NegotiationStatus.FAILED);
									toReturn.setRequestor(myPolicy.getRequestor());
									toReturn.setResponseItems(new ArrayList<ResponseItem>());
									return toReturn;
								}
							}
						}
					}

					//now we're going to check if conditions exist in my policy and removed in the response
					for (Condition myCondition : myConditions){
						Condition clientCondition = this.containsIgnoreValue(clientConditions, myCondition);
						//if myCondition is not included in the 
						if (clientCondition==null){
							//if it's not optional ask the user
							if (!myCondition.isOptional()){
								Hashtable<String, Object> params = new Hashtable<String, Object>();
								params.put("localPolicyDetails",myCondition.getConditionConstant()+": "+myCondition.getValue());
								params.put("remotePolicyDetails", "The client has not included this condition in his policy");
								//TODO: use rules - no user
								Boolean response = true;
								//Boolean response = (Boolean) this.getFeedbackManager().getExplicitFB(FeedbackGUITypes.NEGOTIATION, params);
								if (!response.booleanValue()){
									//JOptionPane.showMessageDialog(null, "Provider: Negotiation Failed 6");
									ResponsePolicy toReturn = new ResponsePolicy();
									toReturn.setNegotiationStatus(NegotiationStatus.FAILED);
									toReturn.setRequestor(myPolicy.getRequestor());
									toReturn.setResponseItems(new ArrayList<ResponseItem>());
									return toReturn;
								}
							}
						}
					}

					//COMPARE CONDITIONS * END *
				}
			}
		}

		for (ResponseItem r : itemsToRemove){
			if (clientResponse.getResponseItems().contains(r)){
				clientResponse.getResponseItems().remove(r);
			}
		}
		clientResponse.setNegotiationStatus(NegotiationStatus.SUCCESSFUL);


		return clientResponse;
	}

	private Condition containsIgnoreValue(List<Condition> list, Condition c){
		for (Condition con : list){
			if (c.getConditionConstant().equals(con.getConditionConstant())){
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
	
}
