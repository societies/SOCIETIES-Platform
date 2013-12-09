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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestorUtils;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IdentitySelectionPreferenceOutcome;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PreferenceEvaluator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PrivateContextCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.PrivatePreferenceCache;

/**
 * @author Eliza
 *
 */
public class IDSPreferenceManager {

	private final PrivatePreferenceCache prefCache;
	private final PrivateContextCache contextCache;
	private ITrustBroker trustBroker;


	public IDSPreferenceManager(PrivatePreferenceCache prefCache, PrivateContextCache contextCache, ITrustBroker trustBroker){
		this.prefCache = prefCache;
		this.contextCache = contextCache;
		this.trustBroker = trustBroker;
		
	}
	private IIdentity evaluateIDSPreferenceIrrespectiveOfRequestor(IAgreement agreement, List<IIdentity> identities){
		ArrayList<IdentitySelectionPreferenceOutcome> outcomes = new ArrayList<IdentitySelectionPreferenceOutcome>();
		for (int i=0; i<identities.size(); i++){
			IDSPreferenceDetailsBean details = new IDSPreferenceDetailsBean();
			details.setAffectedIdentity(identities.get(i).getJid());
			IPrivacyPreferenceTreeModel model = prefCache.getIDSPreference(details);

			if (model!=null){
				IdentitySelectionPreferenceOutcome outcome = (IdentitySelectionPreferenceOutcome) this.evaluatePreference(model.getRootPreference());
				if (null!=outcome){
					outcomes.add(outcome);
				}
			}
		}	

		if (outcomes.size()==0){
			return null;
		}

		if (outcomes.size()==1){
			return outcomes.get(0).getIdentity();
		}

		if (outcomes.size()>1){
			List<IIdentity> dpiList = new ArrayList<IIdentity>();
			for (IdentitySelectionPreferenceOutcome out: outcomes){
				dpiList.add(out.getIdentity());
			}
			return this.askUserToSelectIIdentity(agreement, dpiList);
		}
		return null;
	}

	private IIdentity askUserToSelectIIdentity(IAgreement agreement, List<IIdentity> candidateIdentities){


		List<String> strCandidateIDs = new ArrayList<String>();
		strCandidateIDs.add((new InvalidIdentity()).toString());

		for (IIdentity userId : candidateIdentities){

			strCandidateIDs.add(userId.toString());
		}

		String s = "";
		
		if (agreement.getRequestor() instanceof RequestorServiceBean){
			s = this.askUserToSelectIdentityForStartingService((RequestorServiceBean) agreement.getRequestor(), strCandidateIDs);
		}else if (agreement.getRequestor() instanceof RequestorCisBean){
			s = this.askUserToSelectIdentityForJoiningCIS((RequestorCisBean) agreement.getRequestor(), strCandidateIDs);
		}
		


		for (IIdentity id : candidateIdentities){
			if (s.equalsIgnoreCase(id.toString())){
				return id;
			}
		}
		return new InvalidIdentity();
	}
	
	private String askUserToSelectIdentityForStartingService(RequestorServiceBean requestor, List<String> strCandidates){
		return strCandidates.get(0);
		/*return (String) myMessageBox.showInputDialog(
				"Select an IIdentity for starting session with service:\n",
						"provided by: "+requestor.getRequestorId().toString()+
						"\nwith serviceID: "+requestor.getRequestorServiceId().toString(),
						JOptionPane.QUESTION_MESSAGE, 
						strCandidates.toArray(), strCandidates.get(0));*/
	}
	
	private String askUserToSelectIdentityForJoiningCIS(RequestorCisBean requestor, List<String> strCandidates){
		return strCandidates.get(0);
/*		return (String) myMessageBox.showInputDialog(
				"Select an IIdentity for joining CIS:\n", 
						"CIS id: "+requestor.getCisRequestorId().toString()+
						 "\nadministered by: "+requestor.getRequestorId().toString(),
						JOptionPane.QUESTION_MESSAGE, 
						strCandidates.toArray(), strCandidates.get(0));*/
	}
	
	private String askUserToSelectIdentityForInteractingWithCSS(RequestorBean requestor, List<String> strCandidates){
		return strCandidates.get(0);
/*		return (String) myMessageBox.showInputDialog(
				"Select an IIdentity for interacting with  CSS:\n", 
						"CSS id: "+requestor.getRequestorId().toString(),
						JOptionPane.QUESTION_MESSAGE, 
						strCandidates.toArray(), strCandidates.get(0));
*/	}
	private IIdentity evaluateIDSPreferenceBasedOnProviderDPI(IAgreement agreement, List<IIdentity> identities){
		ArrayList<IdentitySelectionPreferenceOutcome> outcomes = new ArrayList<IdentitySelectionPreferenceOutcome>();
		for (int i=0; i<identities.size(); i++){
			IDSPreferenceDetailsBean details = new IDSPreferenceDetailsBean();
			details.setAffectedIdentity(identities.get(i).getJid());
			details.setRequestor(agreement.getRequestor());
			IPrivacyPreferenceTreeModel model = prefCache.getIDSPreference(details);
			if (model!=null){
				IdentitySelectionPreferenceOutcome outcome = (IdentitySelectionPreferenceOutcome) this.evaluatePreference(model.getRootPreference());
				if (null!=outcome){
					outcomes.add(outcome);
				}

			}
		}	

		if (outcomes.size()==0){
			return null;
		}

		if (outcomes.size()==1){
			return outcomes.get(0).getIdentity();
		}

		if (outcomes.size()>1){
			List<IIdentity> dpiList = new ArrayList<IIdentity>();
			for (IdentitySelectionPreferenceOutcome out: outcomes){
				dpiList.add(out.getIdentity());
			}
			return this.askUserToSelectIIdentity(agreement, dpiList);
		}
		return null;
	}
	private IIdentity evaluateIDSPreferenceBasedOnAllInfo(IAgreement agreement, List<IIdentity> identities){
		ArrayList<IdentitySelectionPreferenceOutcome> outcomes = new ArrayList<IdentitySelectionPreferenceOutcome>();
		for (int i=0; i<identities.size(); i++){
			IDSPreferenceDetailsBean details = new IDSPreferenceDetailsBean();
			details.setAffectedIdentity(identities.get(i).getJid());
			details.setRequestor(agreement.getRequestor());
			IPrivacyPreferenceTreeModel model = prefCache.getIDSPreference(details);
			/*			if (model == null){
				JOptionPane.showMessageDialog(null, "prefCache returned null model for details:"+details.toString());
			}*/
			if (model!=null){
				
			
				IdentitySelectionPreferenceOutcome outcome = (IdentitySelectionPreferenceOutcome) this.evaluatePreference(model.getRootPreference());
				if (null!=outcome){
					//JOptionPane.showMessageDialog(null, "Evaluation returned non-null outcome");
					outcomes.add(outcome);
				}
			}
		}	

		if (outcomes.size()==0){
			//JOptionPane.showMessageDialog(null, "No preference was applicable. returning null dpi");
			return null;
		}

		if (outcomes.size()==1){
			//JOptionPane.showMessageDialog(null, "ONE preference was applicable. returning dpi:"+outcomes.get(0).getIIdentity());
			return outcomes.get(0).getIdentity();
		}

		if (outcomes.size()>1){
			//JOptionPane.showMessageDialog(null, "MULTIPLE preferences were applicable. asking the user");
			List<IIdentity> dpiList = new ArrayList<IIdentity>();
			for (IdentitySelectionPreferenceOutcome out: outcomes){
				dpiList.add(out.getIdentity());
			}
			return this.askUserToSelectIIdentity(agreement, dpiList);
		}
		return null;
	}
	
	private IPrivacyOutcome evaluatePreference(IPrivacyPreference privPref){
		PreferenceEvaluator ppE = new PreferenceEvaluator(this.contextCache, trustBroker);
		Hashtable<IPrivacyOutcome, List<CtxIdentifier>> results = ppE.evaluatePreference(privPref);
		Enumeration<IPrivacyOutcome> outcomes = results.keys();
		if (outcomes.hasMoreElements()){
			return outcomes.nextElement();
		}

		return null;

	}



	public IIdentity evaluateIDSPreference(IDSPreferenceDetailsBean details) {
		IDSPrivacyPreferenceTreeModel model = this.prefCache.getIDSPreference(details);
		IPrivacyOutcome out = this.evaluatePreference(model.getRootPreference());
		if (out instanceof IdentitySelectionPreferenceOutcome){
			return ((IdentitySelectionPreferenceOutcome) out).getIdentity();
		}
		return null;
	}

	public IIdentity evaluateIDSPreference(RequestorBean requestor){
		List<IDSPreferenceDetailsBean> details = this.prefCache.getIDSPreferenceDetails();
		List<IIdentity> identities = new ArrayList<IIdentity>();
		for (IDSPreferenceDetailsBean detail : details){
			if (detail.getRequestor().equals(requestor)){
				identities.add(this.evaluateIDSPreference(detail));
			}
		}
		
		if (identities.size()==0){
			return null;
		}
		if (identities.size()==1){
			return identities.get(0);
		}
		
		List<String> strCandidateIDs = new ArrayList<String>();
		strCandidateIDs.add((new InvalidIdentity()).toString());

		for (IIdentity userId : identities){

			strCandidateIDs.add(userId.toString());
		}
		
		String createNew = "Create new Identity";
		strCandidateIDs.add(createNew);
		String s = "";
		if (requestor instanceof RequestorServiceBean){
			s = this.askUserToSelectIdentityForStartingService((RequestorServiceBean) requestor, strCandidateIDs);
		}else if (requestor instanceof RequestorCisBean){
			s = this.askUserToSelectIdentityForJoiningCIS((RequestorCisBean) requestor, strCandidateIDs);
		}else{
			s = this.askUserToSelectIdentityForInteractingWithCSS(requestor, strCandidateIDs);
		}
		
		for (IIdentity id : identities){
			if (s.equalsIgnoreCase(id.toString())){
				return id;
			}
		}
		return new InvalidIdentity();
	}
	
	public IIdentity evaluateIDSPreferences(IAgreement agreement, List<IIdentity> identities){

		IIdentity selectedIIdentity = this.evaluateIDSPreferenceBasedOnAllInfo(agreement, identities);

		if (selectedIIdentity==null){
			selectedIIdentity = this.evaluateIDSPreferenceBasedOnProviderDPI(agreement, identities);
			if (selectedIIdentity == null){
				selectedIIdentity = this.evaluateIDSPreferenceIrrespectiveOfRequestor(agreement, identities);
				if (selectedIIdentity == null){
					return null;
				}else if (selectedIIdentity instanceof InvalidIdentity){
					//user wants to use a new identity
					return null;
				}else{
					return selectedIIdentity;
				}
			}else if (selectedIIdentity instanceof InvalidIdentity){
				//user wants to use a new identity
				return null;
			}else{
				return selectedIIdentity;
			}
		}else if (selectedIIdentity instanceof InvalidIdentity){
			//user wants to use a new identity
			return null;
		}else{
			return selectedIIdentity;
		}

	}
	
	public void addIDSDecision(RequestorBean requestor, String selectedIdentity) {
		IDSPreferenceDetailsBean details = new IDSPreferenceDetailsBean();
		details.setAffectedIdentity(selectedIdentity);
		details.setRequestor(requestor);
		IPrivacyPreferenceTreeModel model = this.getIDSPreference(details);
	}


	private class InvalidIdentity implements IIdentity{

		public InvalidIdentity(){
			
		}

		@Override
		public String getJid() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getBareJid() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDomain() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getIdentifier() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IdentityType getType() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	/**
	 * new methods;
	 */
	
	public boolean deleteIDSPreference(IDSPreferenceDetailsBean details) {
		return this.prefCache.removeIDSPreference(details);
	}


	public IDSPrivacyPreferenceTreeModel getIDSPreference(
			IDSPreferenceDetailsBean details) {
		return this.prefCache.getIDSPreference(details);
	}
	
	public List<IDSPreferenceDetailsBean> getIDSPreferenceDetails() {
		return this.prefCache.getIDSPreferenceDetails();
	}

	public boolean storeIDSPreference(IDSPreferenceDetailsBean details,
			IDSPrivacyPreferenceTreeModel model) throws PrivacyException {
		if (model.getDetails().equals(details)){
			return this.prefCache.addIDSPreference(details, model);
		}
		
		throw new PrivacyException("IDSPreferenceDetailsBean parameter did not match IDSPrivacyPreferenceTreeModel.getDetails()");		
	}

}
