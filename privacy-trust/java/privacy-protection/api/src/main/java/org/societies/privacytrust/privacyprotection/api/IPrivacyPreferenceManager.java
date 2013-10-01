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
package org.societies.privacytrust.privacyprotection.api;


import java.util.HashMap;
import java.util.List;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;


/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 
 */
public interface IPrivacyPreferenceManager {

	/**
	 * Method to check the access control permission
	 * @return	responseItem that indicates the resource, the Actions and Conditions and the Decision to permit or deny
	 * 
	 * @param requestor the identity of the requestor (maybe RequestorService or RequestorCis)
	 * @param ctxId    the affected context identifier
	 * @param actions    the actions requested
	 * @exception PrivacyPreferenceException PrivacyPreferenceException
	 */
	@Deprecated
	public List<org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem> checkPermission(Requestor requestor, DataIdentifier dataId, List<org.societies.api.privacytrust.privacy.model.privacypolicy.Action> actions)
	  throws PrivacyException;

	/**
	 * Method to check the access control permission
	 * @return	responseItem that indicates the resource, the Actions and Conditions and the Decision to permit or deny
	 * 
	 * @param requestor the identity of the requestor (maybe RequestorService or RequestorCis)
	 * @param dataIds    Requested leaf data identififers
	 * @param actions    the actions requested
	 * @exception PrivacyPreferenceException PrivacyPreferenceException
	 */
	@Deprecated
	public List<ResponseItem> checkPermission(RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions)
	  throws PrivacyException;
	
	/**
	 * Method to check the access control permission
	 * @return	responseItems that indicate the resource, the Actions and Conditions and the Decision to permit or deny
	 * 
	 * @param requestor the identity of the requestor (maybe RequestorService or RequestorCis)
	 * @param dataIds    Requested leaf data identifiers
	 * @param action    the action requested
	 * @exception PrivacyPreferenceException PrivacyPreferenceException
	 */
	public List<ResponseItem> checkPermission(RequestorBean requestor, List<DataIdentifier> dataIds, Action action) throws PrivacyException;

	
	/**
	 * Method to check the access control permission
	 * @return	responseItems that indicate the resource, the Actions and Conditions and the Decision to permit or deny
	 * 
	 * @param requestor the identity of the requestor (maybe RequestorService or RequestorCis)
	 * @param dataIds    Requested leaf data identifiers
	 * @param action    the action requested
	 * @exception PrivacyPreferenceException PrivacyPreferenceException
	 */
	public ResponseItem checkPermission(RequestorBean requestor, DataIdentifier dataIds, Action action) throws PrivacyException;
	
	
	/**
	 * Method to evaluate the PPN preferences for the specified responsePolicy
	 * @param requestPolicy		the responsePolicy 
	 * @return				    a map of items in the requestPolicy and the corresponding response item the PrivacyPreference Manager was able to generate based on existing preferences without prompting the user. 
	 */
	public HashMap<RequestItem,ResponseItem> evaluatePPNPreferences(RequestPolicy requestPolicy);

	/**
	 * Method to retrieve the evaluated outcome of identity selection preferences based on 
	 * an agreement. 
	 * 
	 * @param agreement 	the privacy policy agreement document
	 * @param identities	the list of identities that the IdentitySelection module deemed appropriate for this transaction
	 * @return				the selected identity. is null if none of the identities passed to this method matched the preferences of the user.
	 */
	public IIdentity evaluateIDSPreferences(IAgreement agreement, List<IIdentity> identities);



	/**
	 * Method to evaluate the identity selection preferences of the user with regard to the specified details
	 * @param details		the details to which the preference refers to
	 * @return				the identity that should be used to interact with the entity specified in the details
	 */
	public IIdentity evaluateIDSPreference(IDSPreferenceDetailsBean details);
	
	/**
	 * Method to evaluate the data obfuscation preferences for the specified details
	 * @param details	the details to which the preference refers to
	 * @return			the obfuscation level that should be set 
	 */
	public double evaluateDObfPreference(DObfPreferenceDetailsBean details);

	/**
	 * Method to evaluate the access control preferences for the specified details
	 * this method should only be used from the webapp. this method does not use userFeedback. 
	 * @param details	the details to which the preference refers to
	 * @return			the ResponseItem that indicates the decision of the user about the disclosure of the specified resource.If a preference does not exist
	 * 					for the specified details parameter, this method will return null. 
	 */
	public ResponseItem evaluateAccCtrlPreference(AccessControlPreferenceDetailsBean details);
	
	
	/**
	 * Method to retrieve the list of PPN preferences (not the actual preference objects, only the details for which a preference exists)
	 * @return				the list of all PPN preference details. 
	 */
	public List<PPNPreferenceDetailsBean> getPPNPreferenceDetails();
	
	/**
	 * Method to retrieve the list of ISD preferences (not the actual preference objects, only the details for which a preference exists)
	 * @return				the list of all IDS preference details. 
	 */
	public List<IDSPreferenceDetailsBean> getIDSPreferenceDetails();


	/**
	 * Method to retrieve the list of Data obfuscation preferences (not the actual preference objects, only the details for which the preference exists)
	 * @return			the list of all DObf preference details
	 */
	public List<DObfPreferenceDetailsBean> getDObfPreferenceDetails();
	
	/**
	 * Method to retrieve the list of Access Control preferences  (not the actual preference objects, only the details for which the preference exists)
	 * @return
	 */
	public List<AccessControlPreferenceDetailsBean> getAccCtrlPreferenceDetails();
	
	
	/**
	 * Method to retrieve the PPN preferences based on the given parameters
	 * @param details		the specific details to which the preference requested relates to
	 * @return				the preference to which the details relate to
	 */
	public PPNPrivacyPreferenceTreeModel getPPNPreference(PPNPreferenceDetailsBean details);
	
	
	/**
	 * Method to retrieve the IDS preferences based on the given parameters
	 * @param details		the specific details to which the preference requested relates to
	 * @return				the preference to which the details refer to
	 */
	public IDSPrivacyPreferenceTreeModel getIDSPreference(IDSPreferenceDetailsBean details);


	/**
	 * Method to retrieve the Data Obfuscation preferences based on the given parameters
	 * @param details		the specific details to which the preference requested relates to
	 * @return				the preference to which the details relate to 
	 */
	public DObfPreferenceTreeModel getDObfPreference(DObfPreferenceDetailsBean details);
	
	/**
	 * Method to retrieve the Access Control preference based on the given parameters.  
	 * @param details		the specific details to which the preference requested relates to
	 * @return				the preference to which the details relate to
	 */
	public AccessControlPreferenceTreeModel getAccCtrlPreference(AccessControlPreferenceDetailsBean details);
	
	/**
	 * Method to store a PPN preference related to the specific details (param)
	 * @param details		the details to which the preference to be stored relates to
	 * @param model			the preference to be stored related to the provided details
	 * @return			true if successfully stored, false otherwise
	 */
	public boolean storePPNPreference(PPNPreferenceDetailsBean details, PPNPrivacyPreferenceTreeModel model);
	
	
	/**
	 * Method to store an IDS preference related to the specific details (param)
	 * @param details		the details to which the preference to be stored relates to
	 * @param model			the preference to be stored related to the provided details
	 * @return			true if successfully stored, false otherwise
	 */
	public boolean storeIDSPreference(IDSPreferenceDetailsBean  details, IDSPrivacyPreferenceTreeModel model);
	

	/**
	 * Method to store a Data obfuscation preference related to the specific details 
	 * @param details		the details to which the preference to be stored relates to
	 * @param model			the preference to be stored relating to the provided details
	 * @return			true if successfully stored, false otherwise
	 */
	public boolean storeDObfPreference(DObfPreferenceDetailsBean details, DObfPreferenceTreeModel model);
	
	/**
	 * Method to store an Access Control preference related to the specific details
	 * @param details		the details to which the preference to be stored relates to
	 * @param model			the preference to be stored relating to the provided details
	 * @return			true if successfully stored, false otherwise
	 */
	public boolean storeAccCtrlPreference(AccessControlPreferenceDetailsBean details, AccessControlPreferenceTreeModel model);
	/**
	 * Method to delete an existing PPN preference model
	 * @param details		the details related to this preference
	 * @return			true if successfully deleted, false otherwise
	 */
	public boolean deletePPNPreference(PPNPreferenceDetailsBean details);
	
	/**
	 * Method to delete the IDS preference referring to this details object.
	 * @param details		the details to which the preference to be deleted refers to
	 * @return			true if successfully deleted, false otherwise
	 */
	public boolean deleteIDSPreference(IDSPreferenceDetailsBean details);
	
	/**
	 * Method to delete the Dobf preference referring to this details object
	 * @param details	the details to which the preference to be deleted refers to
	 * @return			true if successfully deleted, false otherwise
	 */
	public boolean deleteDObfPreference(DObfPreferenceDetailsBean details);
	
	/**
	 * Method to delete the Access control preference referring to this details object
	 * @param details	the details to which the preference to be deleted refers to
	 * @return			true if successfully deleted, false otherwise
	 */
	public boolean deleteAccCtrlPreference(AccessControlPreferenceDetailsBean details);

	
}