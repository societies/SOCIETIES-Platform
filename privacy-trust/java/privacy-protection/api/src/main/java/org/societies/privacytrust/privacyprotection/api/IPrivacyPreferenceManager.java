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


import java.util.List;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.DObfOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IDSPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;


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
	public ResponseItem checkPermission(Requestor requestor, CtxAttributeIdentifier ctxId, List<Action> actions)
	  throws PrivacyException;

	/**
	 * Method to check the access control permission
	 * @return	responseItem that indicates the resource, the Actions and Conditions and the Decision to permit or deny
	 * 
	 * @param requestor the identity of the requestor (maybe RequestorService or RequestorCis)
	 * @param contextType    the affected context identifier
	 * @param actions    the actions requested
	 * @exception PrivacyPreferenceException PrivacyPreferenceException
	 */
	public ResponseItem checkPermission(Requestor requestor, String contextType, List<Action> actions)
	  throws PrivacyException;

	
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
	 * Method to generate a privacy policy as a response to a service or CIS RequestPolicy. 
	 * 
	 * @param 		request the privacy policy of the service or CIS with which the CSS is currently negotiating with. 
	 * @return 		the privacy policy of the user as a response to the RequestPolicy. 
	 */
	public ResponsePolicy evaluatePPNP(RequestPolicy request);

	/**
	 * Method to retrieve the outcomes of all PPN preferences that affect the given
	 * context type
	 * @return
	 * 
	 * @param contextType    the affected context type
	 */
	
	/**
	 *  Method to retrieve the outcomes of all PPN preferences that affect the given
	 * context type
	 * @param contextType	the data type in context
	 * @return				a list of IPrivacyOutcome objects for each of the evaluated PPN preferences.
	 */
	public List<IPrivacyOutcome> evaluatePPNPreference(String contextType);

	/**
	 * Method to evaluate the identity selection preferences of the user with regard to the specified requestor (CSS, CIS or service)
	 * @param requestor		the identity of the requestor (maybe RequestorService or RequestorCis)
	 * @return				the Identity that should be used to interact with this requestor under the current context. Is null if 
	 * 						the user selected to create new identity instead of using one of his existing identities. 
	 */
	public IIdentity evaluateIdSPreference(Requestor requestor);

	/**
	 * Method to evaluate the identity selection preferences of the user with regard to the specified details
	 * @param details		the details to which the preference refers to
	 * @return				the identity that should be used to interact with the entity specified in the details
	 */
	public IIdentity evaluateIDSPreference(IDSPreferenceDetails details);
	
	
	/**
	 * Method to evaluate the data obfuscation preferences based on the user's identity, the requestor and the data type requested
	 * @param requestor		the identity of the requestor (maybe RequestorService or RequestorCis)
	 * @param owner			the identity of the user 
	 * @param contextType	the data type in context
	 * @return				the evaluated outcome of the data obfuscation preferences for this specific request. Indicates the level
	 * 						of obfuscation that must be applied
	 */
	public DObfOutcome evaluateDObfPreference(Requestor requestor, IIdentity owner, String contextType);
	
	/**
	 * Method to evaluate the data obfuscation preferences based on the context identifier. 
	 * @param contextID		the identifier of the data held in context
	 * @return				the evaluated outcome of the data obfuscation preferences for this specific request. Indicates the level
	 * 						of obfuscation that must be applied
	 */
	public DObfOutcome evaluateDObfOutcome(CtxIdentifier contextID);
	
	/**
	 * Method to retrieve the list of ISD preferences (not the actual preference objects, only the details for which a preference exists)
	 * @return				the list of all IDS preference details. 
	 */
	public List<IDSPreferenceDetails> getIDSPreferenceDetails();
	/**
	 * Method to retrieve the list of IDSPreferences that affect the user's identity based on the requestor
	 * @param affectedIIdentity    the affected user identity in the preference
	 * @param requestor    the identity of the requestor (maybe RequestorService or RequestorCis)
	 * @return				the list of preference models (each one containing the preference)
	 */
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences( Requestor requestor, IIdentity affectedIIdentity);

	/**
	 * Method to retrieve the list of IDSPreferences that affect the user's identity
	 * 
	 * @param affectedIIdentity    the affected user identity in the preference
	 * @return				the list of preference models (each one containing the preference)
	 */
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences(IIdentity affectedIIdentity);

	/**
	 * Method to retrieve the IDS preferences based on the given parameters
	 * @param details		the specific details to which the preference requested relates to
	 * @return				the preference to which the details refer to
	 */
	public IPrivacyPreferenceTreeModel getIDSPreference(IDSPreferenceDetails details);
	/**
	 * Method to retrieve the list of PPN preferences (not the actual preference objects, only the details for which a preference exists)
	 * @return				the list of all PPN preference details. 
	 */
	public List<PPNPreferenceDetails> getPPNPreferenceDetails();
	
	/**
	 * Method to retrieve the PPNP preferences for a context type
	 * 
	 * @param contextType   the context data type
	 * @return				the list of PPN models that affect this context type
	 */
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType);

	/**
	 * Method to retrieve the PPN preferences based on the given parameters
	 * 
	 * @param contextType   the context data type
	 * @param ctxID    		the context identifier of the data type
	 * @return				the list of PPNP models related to the supplied parameters 
	 */
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, CtxAttributeIdentifier ctxID);

	/**
	 * 
	 * @param requestor		the identity of the requestor (maybe RequestorService or RequestorCis)
	 * @param contextType	the context data type
	 * @return				the list of PPNP models related to the supplied parameters
	 */
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(Requestor requestor, String contextType);

	/**
	 *  Method to retrieve the PPN preferences based on the given parameters
	 * @param requestor		the identity of the requestor (maybe RequestorService or RequestorCis)
	 * @param contextType	the data type in context
	 * @param ctxID    		the context identifier of the data type
	 * @return				the list of PPNP models related to the supplied parameters
	 */
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(Requestor requestor, String contextType, CtxAttributeIdentifier ctxID);

	/**
	 * Method to retrieve the PPN preferences based on the given parameters
	 * @param details		the specific details to which the preference requested relates to
	 * @return				the preference to which the details relate to
	 */
	public IPrivacyPreferenceTreeModel getPPNPreference(PPNPreferenceDetails details);
	
	/**
	 * Method to store an IDS preference related to the specific details (param)
	 * @param details		the details to which the preference to be stored relates to
	 * @param preference	the preference to be stored related to the provided details
	 */
	public void storeIDSPreference(IDSPreferenceDetails details, IPrivacyPreference preference);
	
	/**
	 * Method to store a PPN preference related to the specific details (param)
	 * @param details		the details to which the preference to be stored relates to
	 * @param preference	the preference to be stored related to the provided details
	 */
	public void storePPNPreference(PPNPreferenceDetails details, IPrivacyPreference preference);
	
	/**
	 * Method to delete the IDS preference referring to this identity (only deletes the
	 * generic IDS preference)
	 * 
	 * @param userId    the identity of the user to which the preference refers
	 */
	public void deleteIDSPreference(IIdentity userId);

	/**
	 * Method to delete the IDS preference referring to the provided parameters
	 * 
	 * @param requestor the identity of the requestor (maybe RequestorService or RequestorCis)
	 * @param userIIdentity    the identity of the user to which the preference refers
	 */
	public void deleteIDSPreference(Requestor requestor, IIdentity userIIdentity);


	/**
	 * Method to delete an existing PPN preference model (generic to a context type)
	 * 
	 * @param contextType    the context type to which the preference refers
	 */
	public void deletePPNPreference(String contextType);
	
	/**
	 * Method to delete an existing PPN preference model
	 * @param contextType	the context data type
	 * @param id			the identifier of the data held in context
	 */
	public void deletePPNPreference(String contextType, CtxAttributeIdentifier id);
	
	/**
	 * Method to delete an existing PPN preference model
	 * @param contextType    the context data type
	 * @param affectedCtxID	the identifier of the data held in context
	 * @param requestor    the identity of the requestor (maybe RequestorService or RequestorCis)
	 */
	public void deletePPNPreference(Requestor requestor, String contextType, CtxAttributeIdentifier affectedCtxID);

	/**
	 * Method to delete an existing PPN preference model
	 * @param details		the details related to this preference
	 */
	public void deletePPNPreference(PPNPreferenceDetails details);

}