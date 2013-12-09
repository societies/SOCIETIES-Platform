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
package org.societies.android.api.privacytrust.privacy.util.privacypolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.android.api.cis.model.CisAttributeTypes;
import org.societies.android.api.context.model.CtxAttributeTypes;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.android.api.context.model.CtxAssociationTypes;
import org.societies.android.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.android.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyBehaviourConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyTypeConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;


/**
 * Interface exposed to Societies components and 3P services in order to do actions relative to a privacy policy
 * creation and manipulation
 * 
 * @author Olivier Maridat (Trialog)
 * @created 18-dec.-2012 19:41:29
 */
public class PrivacyPolicyUtils {
	/**
	 * Generic function to help a developer or a user to create a privacy policy by inferring a default
	 * one using information about the CIS or the 3P service. The privacy policy in
	 * result will be slightly completed but still need to be filled.
	 * E.g. if a CIS membership criteria engine requires access to geolocation data,
	 * the inference engine will add geolocation data line to the privacy policy.
	 * @param privacyPolicyType Type of the privacy policy: for a CIS or a 3P service
	 * @param configuration Configuration of the CIS or the 3P service
	 * @return A slightly completed privacy policy
	 */
	@SuppressWarnings("rawtypes")
	public static RequestPolicy inferPrivacyPolicy(PrivacyPolicyTypeConstants privacyPolicyType, Map configuration) throws PrivacyException {
		RequestPolicy privacyPolicy = new RequestPolicy();
		List<RequestItem> requestItems = new ArrayList<RequestItem>();

		// ---- Add privacy policy type
		privacyPolicy.setPrivacyPolicyType(privacyPolicyType);

		// ---- Add configured request items
		if (configuration.containsKey("requestItems")) {
			requestItems.addAll((List<RequestItem>) configuration.get("requestItems"));
		}

		// ---- Add common request items
		//		// --- Prepare common data
		//		PrivacyPolicyBehaviourConstants globalBaheviour = PrivacyPolicyBehaviourConstants.PRIVATE;
		//		if (configuration.containsKey("globalBehaviour")) {
		//			globalBaheviour = (PrivacyPolicyBehaviourConstants) configuration.get("globalBehaviour");
		//		}
		//		// -- Actions: read
		//		List<Action> actions = ActionUtils.createList(ActionConstants.READ, ActionConstants.CREATE);
		//		// -- Conditions
		//		List<Condition> conditions = new ArrayList<Condition>();
		//		// - Common
		//		conditions.add(ConditionUtils.create(ConditionConstants.STORE_IN_SECURE_STORAGE, "Yes"));
		//		// - Visibility
		//		// Public
		//		if (PrivacyPolicyBehaviourConstants.PUBLIC.name().equals(globalBaheviour.name())) {
		//			conditions.add(ConditionUtils.createPublic());
		//		}
		//		// Members only
		//		else if (PrivacyPolicyBehaviourConstants.MEMBERS_ONLY.name().equals(globalBaheviour.name())) {
		//			conditions.add(ConditionUtils.createMembersOnly());
		//		}
		//		// Private
		//		else {
		//			conditions.add(ConditionUtils.createPrivate());
		//		}

		privacyPolicy.setRequestItems(requestItems);
		return privacyPolicy;
	}

	/**
	 * Help a developer or a user to create a CIS privacy policy by inferring a default
	 * one using information about the CIS. The privacy policy in
	 * result will be slightly completed but still need to be filled.
	 * E.g. if a CIS membership criteria engine requires access to geolocation data,
	 * the inference engine will add geolocation data line to the privacy policy.
	 * @param globalBehaviour Global behavior of the privacy policy: private (default), members only, public or custom
	 * @param membershipCriteria Membership criteria of the CIS (optional)
	 * @param configuration Other optional configuration
	 * @return A slightly completed privacy policy
	 */
	public static RequestPolicy inferCisPrivacyPolicy(
			PrivacyPolicyBehaviourConstants globalBehaviour,
			MembershipCrit membershipCriteria,
			Map<String, String> configuration) throws PrivacyException {
		// --- Prepare common data
		// -- Actions: read
		List<Action> actions = ActionUtils.createList(ActionConstants.READ, ActionConstants.CREATE);
		// -- Conditions
		List<Condition> conditions = new ArrayList<Condition>();
		// - Common
		conditions.add(ConditionUtils.create(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));
		// - Visibility
		// Public
		if (PrivacyPolicyBehaviourConstants.PUBLIC.name().equals(globalBehaviour.name())) {
			conditions.add(ConditionUtils.createPublic());
		}
		// Members only
		else if (PrivacyPolicyBehaviourConstants.MEMBERS_ONLY.name().equals(globalBehaviour.name())) {
			conditions.add(ConditionUtils.createMembersOnly());
		}
		// Private
		else {
			conditions.add(ConditionUtils.createPrivate());
		}

		// --- Prepare request item list
		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		boolean optional = false;
		// - CIS Member list
		{
			Resource resource = ResourceUtils.create(DataIdentifierScheme.CIS, CisAttributeTypes.MEMBER_LIST);
			RequestItem requestItem = RequestItemUtils.create(resource, actions, conditions, optional);
			requestItems.add(requestItem);
		}
		{
			Resource resource = ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAssociationTypes.HAS_MEMBERS);
			RequestItem requestItem = RequestItemUtils.create(resource, actions, conditions, optional);
			requestItems.add(requestItem);
		}
		// - Location symbolic
		{
			Resource resource = ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_SYMBOLIC);
			RequestItem requestItem = RequestItemUtils.create(resource, actions, conditions, optional);
			requestItems.add(requestItem);
		}
		optional = true;
		// - Location coordinates
		{
			Resource resource = ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_COORDINATES);
			RequestItem requestItem = RequestItemUtils.create(resource, actions, conditions, optional);
			requestItems.add(requestItem);
		}
		// - Interests
		{
			Resource resource = ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.INTERESTS);
			RequestItem requestItem = RequestItemUtils.create(resource, actions, conditions, optional);
			requestItems.add(requestItem);
		}
		// - Email
		{
			Resource resource = ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.EMAIL);
			RequestItem requestItem = RequestItemUtils.create(resource, actions, conditions, optional);
			requestItems.add(requestItem);
		}
		// - Occupation
		{
			Resource resource = ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.OCCUPATION);
			RequestItem requestItem = RequestItemUtils.create(resource, actions, conditions, optional);
			requestItems.add(requestItem);
		}
		// - Languages
		{
			Resource resource = ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LANGUAGES);
			RequestItem requestItem = RequestItemUtils.create(resource, actions, conditions, optional);
			requestItems.add(requestItem);
		}
		// - CACI Model
		{
			Resource resource = ResourceUtils.create(DataIdentifierScheme.CONTEXT, "caci_model");
			RequestItem requestItem = RequestItemUtils.create(resource, actions, conditions, optional);
			requestItems.add(requestItem);
		}

		// --- Prepare parameters
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("globalBehaviour", globalBehaviour);
		parameters.put("membershipCriteria", membershipCriteria);
		parameters.put("requestItems", requestItems);
		if (null != configuration) {
			parameters.putAll(configuration);
		}
		return inferPrivacyPolicy(PrivacyPolicyTypeConstants.CIS, parameters);
	}

	/**
	 * Help a developer or a user to create a CIS privacy policy by inferring a default
	 * one using information about the CIS. The privacy policy in
	 * result will be slightly completed but still need to be filled.
	 * E.g. if a CIS membership criteria engine requires access to geolocation data,
	 * the inference engine will add geolocation data line to the privacy policy.
	 * @param globalBehaviour Global behavior of the privacy policy: private (default), members only, public or custom
	 * @param membershipCriteria Membership criteria of the CIS
	 * @return A slightly completed privacy policy
	 */
	public static RequestPolicy inferCisPrivacyPolicy(
			PrivacyPolicyBehaviourConstants globalBehaviour,
			MembershipCrit membershipCriteria) throws PrivacyException {
		return inferCisPrivacyPolicy(globalBehaviour, membershipCriteria, null);
	}

	/**
	 * Help a developer or a user to create a 3P-service privacy policy by inferring a default
	 * one using information about the 3P-service. The privacy policy in
	 * result will be slightly completed but still need to be filled.
	 * E.g. if a CIS membership criteria engine requires access to geolocation data,
	 * the inference engine will add geolocation data line to the privacy policy.
	 * @param configuration Configuration of the 3P service
	 * @return A slightly completed privacy policy
	 */
	public static RequestPolicy infer3pServicePrivacyPolicy(
			Map<String, String> configuration) throws PrivacyException {
		return inferPrivacyPolicy(PrivacyPolicyTypeConstants.SERVICE, configuration);
	}

	/**
	 * Create a Privacy Policy in an XML format from a Java format Privacy Policy
	 * The format of the privacy policy follows the XACML specification
	 * @param privacyPolicy Privacy policy as a Java object
	 * @return A string containing the XML version the privacy policy
	 */	
	public static String toXmlString(RequestPolicy privacyPolicy) {
		String encoding = "UTF-8";
		String header = "<?xml version=\"1.0\" encoding=\""+encoding+"\"?>";
		StringBuilder sb = new StringBuilder();
		// -- Empty Privacy Policy
		if (null == privacyPolicy) {
			return sb.append(header).append("<RequestPolicy></RequestPolicy>").toString();
		}

		// -- Generate XML privacy policy
		String privacyPolicyXml = RequestPolicyUtils.toXmlString(privacyPolicy);
		// Fill XML header if necessary
		if (!privacyPolicyXml.startsWith("<?xml")) {
			sb.append(header);
		}
		sb.append(privacyPolicyXml);
		return sb.toString();
	}
}