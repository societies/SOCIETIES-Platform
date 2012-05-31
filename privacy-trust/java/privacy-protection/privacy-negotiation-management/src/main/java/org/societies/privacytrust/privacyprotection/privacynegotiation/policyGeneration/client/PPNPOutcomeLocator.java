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
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.personalisation.IPersonalisationManager;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.personalisation.preference.IUserPreferenceManagement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.privacynegotiation.PrivacyPolicyNegotiationManager;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.PPNPOutcomeDialog;

/**
 * @author Elizabeth
 *
 */
public class PPNPOutcomeLocator {

	private IPrivacyPreferenceManager privPrefMgr;
	private PPNPOutcome outcome;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private IUserPreferenceManagement prefMgr;
	private IIdentityManager IDM;

	private enum SubjectConstant{
		IDENTITY_SERVICE_ID, 
		IDENTITY_GENERIC,
		IDENTITY_OTHER_SERVICE_ID,
		GENERIC,
		OTHER_IDENTITY_OTHER_SERVICE_ID,
		IDENTITY_CIS_ID,
		IDENTITY_OTHER_CIS_ID,
		OTHER_IDENTITY_OTHER_CIS_ID, 
		IDENTITY_OTHER_REQUESTOR_TYPE,
		OTHER_IDENTITY_OTHER_REQUESTOR_TYPE;
	}	

	public PPNPOutcomeLocator(PrivacyPolicyNegotiationManager policyMgr){
		this.privPrefMgr = policyMgr.getPrivacyPreferenceManager();
		this.prefMgr = policyMgr.getPrefMgr();
		this.IDM = policyMgr.getIdm();
	}

	/*
	 * ALGORITHM:
	 * Retrieve all PPN Preferences for the affected context TYPE
	 * 
	 * FOR each PPNP, evaluate and add the PPNPOutcome to the OutcomesList
	 * (EXIT LOOP)
	 * 
	 * define a hashtable<String, List<PPNPOutcome>>
	 * 
	 * FOR each outcome:
	 * IF it affects the specific DPI and serviceID
	 * 		add to the hashtable with key: IDENTITY_SERVICE_ID 
	 * IF it affects this specific DPI but not a specific serviceID
	 * 		add to the hashtable with key: IDENTITY_GENERIC
	 * IF it affects this specific DPI and a different SERVICE_ID
	 *  	add to the hashtable with key: IDENTITY_OTHER_SERVICE_ID
	 * IF it doesn't affect a specific DPI 
	 * 		add to the hashtable with key: GENERIC
	 * IF it affects another specific DPI
	 * 		add to the hashtable with key: OTHER_IDENTITY_OTHER_SERVICE_ID
	 * (EXIT LOOP)

	 * IF the hashtable contains key IDENTITY_SERVICE_ID
	 * 		use timedNotificationGUI
	 * 		IF aborted by the user, pop up the PPNPOutcomeGUI 
	 * 			after getting the outcome, use the feedback gui to ask the user if he wants to save this in their preferences for this specific DPI
	 * 			if yes
	 * 				use the PreferenceManager to store it and send it to the UIM 
	 * 			if no
	 *				just send it to the UIM
	 *			return the new outcome			
	 * 		ELSE
	 * 			return it 
	 * IF the hashtable contains key IDENTITY_GENERIC
	 * 		use timedNotificationGUI
	 * 		IF aborted by the user, pop up the PPNPOutcomeGUI 
	 * 			after getting the outcome, use the feedback gui to ask the user if he wants to save this in their preferences for this specific DPI
	 * 			if yes
	 * 				use the PreferenceManager to store it and send it to the UIM 
	 * 			if no
	 *				just send it to the UIM
	 *			return the new outcome			
	 * 		ELSE
	 * 			return it 
	 * ELSE IF the hashtable contains key IDENTITY_OTHER_SERVICE_ID
	 * 		use JOptionPane.showConfirmDialog
	 * 		IF true return it 
	 * 		ELSE 
	 * 			pop up the PPNPOutcomeGUI
	 * 			after getting the outcome, use the feedback gui to ask the user if he wants to save this in their preferences for this specific DPI and serviceID	 * 			if yes
	 * 				use the PreferenceManager to store it and send it to the UIM 
	 * 			if no
	 *				just send it to the UIM
	 *			return the new outcome
	 * ELSE IF the hashtable contains key GENERIC
	 * 		use feedbackGUI with explicit
	 * 		IF true return it 
	 * 		ELSE 
	 * 			pop up the PPNPOutcomeGUI
	 * 			after getting the outcome, use the feedback gui to ask the user if he wants to save this in their preferences for this specific DPI and serviceID	 * 			if yes
	 * 				use the PreferenceManager to store it and send it to the UIM 
	 * 			if no
	 *				just send it to the UIM
	 *			return the new outcome
	 * ELSE IF the hashtable contains key OTHER_IDENTITY_OTHER_SERVICE_ID
	 * 		use feedbackGUI with explicit
	 * 		IF true return it 
	 * 		ELSE 
	 * 			pop up the PPNPOutcomeGUI
	 * 			after getting the outcome, use the feedback gui to ask the user if he wants to save this in their preferences for this specific DPI and serviceID	 * 			if yes
	 * 				use the PreferenceManager to store it and send it to the UIM 
	 * 			if no
	 *				just send it to the UIM
	 *			return the new outcome
	 * ELSE 
	 * 			pop up the PPNPOutcomeGUI
	 * 			after getting the outcome, use the feedback gui to ask the user if he wants to save this in their preferences for this specific DPI and serviceID	 * 			if yes
	 * 				use the PreferenceManager to store it and send it to the UIM 
	 * 			if no
	 *				just send it to the UIM
	 *			return the new outcome 		
	 */
	PPNPOutcome getPPNPOutcome(Requestor theRequestor, RequestItem item){

		String contextType = item.getResource().getContextType();

		List<IPrivacyOutcome> outcomeList = this.privPrefMgr.evaluatePPNPreference(contextType);
		//JOptionPane.showMessageDialog(null, "PrivPrefMgr returned "+outcomeList.size()+" outcomes for "+contextType);
		//List<IPrivacyPreferenceTreeModel> modelList = this.privPrefMgr.getPPNPreferences(contextType);
		Hashtable<SubjectConstant,List<PPNPOutcome>> keyOutcome = new Hashtable<SubjectConstant,List<PPNPOutcome>>(); 

		for (IPrivacyOutcome outcome : outcomeList){
			PPNPOutcome out = (PPNPOutcome) outcome;
			if (null!=out){
				if (out.getRuleTarget()!=null){
					ArrayList<Requestor> subjects = (ArrayList<Requestor>) out.getRuleTarget().getRequestors();
					SubjectConstant situation = this.getCategory(theRequestor, subjects);
					if (keyOutcome.containsKey(situation)){
						keyOutcome.get(situation).add(out);
					}else{
						ArrayList<PPNPOutcome> temp = new ArrayList<PPNPOutcome>();
						temp.add(out);
						keyOutcome.put(situation, temp);
					}
				}
			}
		}
		/*		for (IPrivacyPreferenceTreeModel model : modelList){
			PPNPOutcome out = (PPNPOutcome) this.privPrefMgr.evaluatePreference(model.getRootPreference());
			if (null!=out){
				if (out.getRuleTarget()!=null){
					ArrayList<Subject> subjects = (ArrayList<Subject>) out.getRuleTarget().getSubjects();
					SubjectConstant situation = this.getCategory(theSubject.getDPI(), theSubject.getServiceID(), subjects);
					if (keyOutcome.containsKey(situation)){
						keyOutcome.get(situation).add(out);
					}else{
						ArrayList<PPNPOutcome> temp = new ArrayList<PPNPOutcome>();
						temp.add(out);
						keyOutcome.put(situation, temp);
					}
				}
			}

		}*/
		if (theRequestor instanceof RequestorService){
			if (keyOutcome.containsKey(SubjectConstant.IDENTITY_SERVICE_ID)){
				JOptionPane.showMessageDialog(null, SubjectConstant.IDENTITY_SERVICE_ID);
				TimedNotificationGUI gui = new TimedNotificationGUI();

				/*
				 * remove call to TimedNotifiactionGUI. should be moved to the checkPermissions method in PrivacypreferenceManager
				 */
				//boolean response = gui.showGUI(contextType);
				boolean response = true;
				if (response){
					List<PPNPOutcome> temp = keyOutcome.get(SubjectConstant.IDENTITY_SERVICE_ID);
					if (temp.size()>0){
						return temp.get(0);
					}
				}else{

					return this.popupPPPNPOutcomeDialog(item,theRequestor);
				}
			}


			if (keyOutcome.containsKey(SubjectConstant.IDENTITY_GENERIC)){
				JOptionPane.showMessageDialog(null, SubjectConstant.IDENTITY_GENERIC);

				TimedNotificationGUI gui = new TimedNotificationGUI();
				/*
				 * remove call to TimedNotifiactionGUI. the call to the gui should be moved to the checkPermissions method in PrivacypreferenceManager
				 */
				//boolean response = gui.showGUI(contextType);
				boolean response = true;
				if (response){
					List<PPNPOutcome> temp = keyOutcome.get(SubjectConstant.IDENTITY_GENERIC);
					if (temp.size()>0){
						return temp.get(0);
					}
				}else{
					return this.popupPPPNPOutcomeDialog(item,theRequestor);
				}
			}
			if (keyOutcome.containsKey(SubjectConstant.IDENTITY_OTHER_SERVICE_ID)){
				JOptionPane.showMessageDialog(null, SubjectConstant.IDENTITY_OTHER_SERVICE_ID);
				boolean response = this.showNotificationGUI(contextType);
				if (response){
					List<PPNPOutcome> temp = keyOutcome.get(SubjectConstant.IDENTITY_OTHER_SERVICE_ID);
					if (temp.size()>0){
						return temp.get(0);
					}
				}else{
					return this.popupPPPNPOutcomeDialog(item,theRequestor);
				}
			}

			if (keyOutcome.containsKey(SubjectConstant.GENERIC)){
				JOptionPane.showMessageDialog(null, SubjectConstant.GENERIC);
				boolean response = this.showNotificationGUI(contextType);
				if (response){
					List<PPNPOutcome> temp = keyOutcome.get(SubjectConstant.GENERIC);
					if (temp.size()>0){
						return temp.get(0);
					}
				}else{
					return this.popupPPPNPOutcomeDialog(item,theRequestor);
				}
			}
			if (keyOutcome.containsKey(SubjectConstant.OTHER_IDENTITY_OTHER_SERVICE_ID)){
				JOptionPane.showMessageDialog(null, SubjectConstant.OTHER_IDENTITY_OTHER_SERVICE_ID);
				boolean response = this.showNotificationGUI(contextType);
				if (response){
					List<PPNPOutcome> temp = keyOutcome.get(SubjectConstant.OTHER_IDENTITY_OTHER_SERVICE_ID);
					if (temp.size()>0){
						return temp.get(0);
					}
				}else{
					return this.popupPPPNPOutcomeDialog(item,theRequestor);
				}
			}
		}
		else if (theRequestor instanceof RequestorCis){
			if (keyOutcome.containsKey(SubjectConstant.IDENTITY_CIS_ID)){
				JOptionPane.showMessageDialog(null, SubjectConstant.IDENTITY_CIS_ID);

				TimedNotificationGUI gui = new TimedNotificationGUI();

				//boolean response = gui.showGUI(contextType);

				boolean response = true;
				if (response){
					List<PPNPOutcome> temp = keyOutcome.get(SubjectConstant.IDENTITY_CIS_ID);
					if (temp.size()>0){
						return temp.get(0);
					}
				}else{
					return this.popupPPPNPOutcomeDialog(item, theRequestor);
				}
			}

			if (keyOutcome.containsKey(SubjectConstant.IDENTITY_GENERIC)){
				JOptionPane.showMessageDialog(null, SubjectConstant.IDENTITY_GENERIC);
				TimedNotificationGUI gui = new TimedNotificationGUI();

				//boolean response = gui.showGUI(contextType);

				boolean response = true;
				if (response){
					List<PPNPOutcome> temp = keyOutcome.get(SubjectConstant.IDENTITY_GENERIC);
					if (temp.size()>0){
						return temp.get(0);
					}
				}else{
					return this.popupPPPNPOutcomeDialog(item, theRequestor);
				}
			}
			if (keyOutcome.containsKey(SubjectConstant.IDENTITY_OTHER_CIS_ID)){
				JOptionPane.showMessageDialog(null, SubjectConstant.IDENTITY_OTHER_CIS_ID);
				boolean response = this.showNotificationGUI(contextType);
				if (response){
					List<PPNPOutcome> temp = keyOutcome.get(SubjectConstant.IDENTITY_OTHER_CIS_ID);
					if (temp.size()>0){
						return temp.get(0);
					}
				}else{
					return this.popupPPPNPOutcomeDialog(item, theRequestor);
				}
			}

			if (keyOutcome.containsKey(SubjectConstant.OTHER_IDENTITY_OTHER_CIS_ID)){
				JOptionPane.showMessageDialog(null, SubjectConstant.OTHER_IDENTITY_OTHER_CIS_ID);
				boolean response = this.showNotificationGUI(contextType);
				if (response){
					List<PPNPOutcome> temp = keyOutcome.get(SubjectConstant.OTHER_IDENTITY_OTHER_CIS_ID);
					if (temp.size()>0){
						return temp.get(0);
					}
				}else{
					return this.popupPPPNPOutcomeDialog(item, theRequestor);
				}
			}
		}



		if (keyOutcome.containsKey(SubjectConstant.IDENTITY_OTHER_REQUESTOR_TYPE)){
			JOptionPane.showMessageDialog(null, SubjectConstant.IDENTITY_OTHER_REQUESTOR_TYPE);
			boolean response = this.showNotificationGUI(contextType);
			if (response){
				List<PPNPOutcome> temp = keyOutcome.get(SubjectConstant.IDENTITY_OTHER_REQUESTOR_TYPE);
				if (temp.size()>0){
					return temp.get(0);
				}
			}else{
				return this.popupPPPNPOutcomeDialog(item, theRequestor);
			}
		}

		if (keyOutcome.containsKey(SubjectConstant.OTHER_IDENTITY_OTHER_REQUESTOR_TYPE)){
			JOptionPane.showMessageDialog(null, SubjectConstant.OTHER_IDENTITY_OTHER_REQUESTOR_TYPE);
			boolean response = this.showNotificationGUI(contextType);
			if (response){
				List<PPNPOutcome> temp = keyOutcome.get(SubjectConstant.OTHER_IDENTITY_OTHER_REQUESTOR_TYPE);
				if (temp.size()>0){
					return temp.get(0);
				}
			}else{
				return this.popupPPPNPOutcomeDialog(item, theRequestor);
			}
		}
		return this.popupPPPNPOutcomeDialog(item,theRequestor);
	}

	private SubjectConstant getCategory(Requestor requestor, List<Requestor> requestorsFromPreferences){
		ArrayList<SubjectConstant> categories = new ArrayList<SubjectConstant>();
		if (requestorsFromPreferences.size()==0){
			return SubjectConstant.GENERIC;
		}
		for (Requestor requestorFromPreference : requestorsFromPreferences){
			if (requestor instanceof RequestorService ){
				categories.add(this.getServiceCategory((RequestorService) requestor, requestorFromPreference));
			}else if (requestor instanceof RequestorCis){
				categories.add(this.getCISCategory((RequestorCis) requestor, requestorFromPreference));
			}
		}
		if (requestor instanceof RequestorService){
			if (categories.contains(SubjectConstant.IDENTITY_SERVICE_ID)){
				return SubjectConstant.IDENTITY_SERVICE_ID;
			}else if(categories.contains(SubjectConstant.IDENTITY_GENERIC)){
				return SubjectConstant.IDENTITY_GENERIC;
			}else if (categories.contains(SubjectConstant.IDENTITY_OTHER_SERVICE_ID)){
				return SubjectConstant.IDENTITY_OTHER_SERVICE_ID;
			}else if (categories.contains(SubjectConstant.GENERIC)){
				return SubjectConstant.GENERIC;
			}else if (categories.contains(SubjectConstant.OTHER_IDENTITY_OTHER_SERVICE_ID)){
				return SubjectConstant.OTHER_IDENTITY_OTHER_SERVICE_ID;
			}
		}else if (requestor instanceof RequestorCis){
			if (categories.contains(SubjectConstant.IDENTITY_CIS_ID)){
				return SubjectConstant.IDENTITY_CIS_ID;
			}else if(categories.contains(SubjectConstant.IDENTITY_OTHER_CIS_ID)){
				return SubjectConstant.IDENTITY_OTHER_CIS_ID;
			}else if (categories.contains(SubjectConstant.OTHER_IDENTITY_OTHER_CIS_ID)){
				return SubjectConstant.OTHER_IDENTITY_OTHER_CIS_ID;
			}
		}

		if (categories.contains(SubjectConstant.IDENTITY_OTHER_REQUESTOR_TYPE)){
			return SubjectConstant.IDENTITY_OTHER_REQUESTOR_TYPE;
		}else{
			return SubjectConstant.OTHER_IDENTITY_OTHER_REQUESTOR_TYPE;
		}
	}

	private SubjectConstant getServiceCategory(RequestorService requestor, Requestor requestorFromPreferences){
		if (requestorFromPreferences.getRequestorId()==null){
			return SubjectConstant.GENERIC;
		}
		if (requestorFromPreferences instanceof RequestorService){
			if (requestorFromPreferences.getRequestorId().equals(requestor.getRequestorId())){
				if (((RequestorService) requestorFromPreferences).getRequestorServiceId().equals(requestor.getRequestorServiceId())){
					return SubjectConstant.IDENTITY_SERVICE_ID;
				}else{
					return SubjectConstant.IDENTITY_OTHER_SERVICE_ID;
				}
			}else{
				return SubjectConstant.OTHER_IDENTITY_OTHER_SERVICE_ID;
			}
		}else{
			if (requestorFromPreferences.getRequestorId().equals(requestor.getRequestorId())){
				return SubjectConstant.IDENTITY_OTHER_REQUESTOR_TYPE;
			}else{
				return SubjectConstant.OTHER_IDENTITY_OTHER_REQUESTOR_TYPE;
			}
		}
	}

	private SubjectConstant getCISCategory(RequestorCis requestor, Requestor requestorFromPreferences){
		if (requestorFromPreferences.getRequestorId()==null){
			return SubjectConstant.GENERIC;
		}

		if (requestorFromPreferences instanceof RequestorCis){
			if (requestorFromPreferences.getRequestorId().equals(requestor.getRequestorId())){
				if (((RequestorCis) requestorFromPreferences).getCisRequestorId().equals(requestor.getCisRequestorId())){
					return SubjectConstant.IDENTITY_CIS_ID;
				}else{
					return SubjectConstant.IDENTITY_OTHER_CIS_ID;
				}
			}else{
				return SubjectConstant.OTHER_IDENTITY_OTHER_CIS_ID;
			}
		}else{
			if (requestorFromPreferences.getRequestorId().equals(requestor.getRequestorId())){
				return SubjectConstant.IDENTITY_OTHER_REQUESTOR_TYPE;
			}else{
				return SubjectConstant.OTHER_IDENTITY_OTHER_REQUESTOR_TYPE;
			}
		}
	}

	private boolean showNotificationGUI(String contextType) {
		//TODO: we need a local identity for internal operations.  
		//		IOutcome outcome = this.prefMgr.getPreference(null, "PolicyMgmt", this.myServiceID, "showPPNPNotifications");
		boolean showNotif = true;
		/*if (outcome!=null){
			if (outcome.getvalue().equalsIgnoreCase("no")){
				showNotif = false;
			}
		}*/
		if (showNotif){
			int n = JOptionPane.showConfirmDialog(null, "Implement Effect: PERMIT\n"
					+ "for resource:"+contextType+"?", "PrivacyPreference Evaluation", JOptionPane.YES_NO_OPTION);
			if (n==JOptionPane.YES_OPTION){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}

	private PPNPOutcome popupPPPNPOutcomeDialog(RequestItem item, Requestor requestor){
		PPNPOutcomeDialog guiDialog = new PPNPOutcomeDialog(requestor, this.getClass().getName(), item, this.privPrefMgr);
		this.outcome = guiDialog.getOutcome();
		if (outcome==null){
			log("OUTCOME IS NULL :(");
		}else{
			log("PPNPOutcomeLocator: got outcome");
		}
		return outcome;

	}

	private void log(String message){
		this.logging.info(this.getClass().getName()+" : "+message);
	}
}
