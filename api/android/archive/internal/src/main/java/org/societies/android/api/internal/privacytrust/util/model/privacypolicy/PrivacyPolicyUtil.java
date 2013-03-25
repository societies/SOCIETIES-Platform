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
package org.societies.android.api.internal.privacytrust.util.model.privacypolicy;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.android.api.privacytrust.privacy.util.privacypolicy.PrivacyPolicyUtils;
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

import android.util.Log;


/**
 * Interface exposed to Societies components in order to do actions relative
 * to a privacy policy
 * 
 * @author Olivier Maridat (Trialog)
 * @created 18-dec.-2012 19:41:29
 */
public class PrivacyPolicyUtil {
	private final static String TAG = PrivacyPolicyUtil.class.getSimpleName();

	/**
	 * General function to help a developer or a user to create a privacy policy by inferring a default
	 * one using information about the CIS or the service. The privacy policy in
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
		// Not private
		if (configuration.containsKey("globalBehaviour")) {
			// CIS Member list
			RequestItem requestItem = new RequestItem();
			Resource cisMemberList = new Resource();
			cisMemberList.setScheme(DataIdentifierScheme.CIS);
			cisMemberList.setDataType("cis-member-list");
			requestItem.setResource(cisMemberList);
			List<Action> actions = new ArrayList<Action>();
			Action action = new Action();
			action.setActionConstant(ActionConstants.READ);
			actions.add(action);
			requestItem.setActions(actions);
			List<Condition> conditions = new ArrayList<Condition>();
			// Public
			PrivacyPolicyBehaviourConstants globalBaheviour = (PrivacyPolicyBehaviourConstants) configuration.get("globalBehaviour");
			if (null != globalBaheviour && PrivacyPolicyBehaviourConstants.PUBLIC.name().equals(globalBaheviour)) {
				Condition condition = new Condition();
				condition.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
				condition.setValue("1");
			}
			// Members only
			else if (null != globalBaheviour && PrivacyPolicyBehaviourConstants.MEMBERS_ONLY.name().equals(globalBaheviour)) {
				Condition condition = new Condition();
				condition.setConditionConstant(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY);
				condition.setValue("1");
			}
			requestItem.setConditions(conditions);
			requestItems.add(requestItem);
		}
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
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("globalBehaviour", globalBehaviour);
		parameters.put("membershipCriteria", membershipCriteria);
		parameters.putAll(configuration);
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
	 * @param privacyPolicy Privacy policy
	 * @return A string containing the XML version the privacy policy
	 */	
	public static String toXmlString(RequestPolicy privacyPolicy) {
		String encoding = "UTF-8";
		// -- Empty Privacy Policy
		if (null == privacyPolicy) {
			return "<?xml version=\"1.0\" encoding=\""+encoding+"\"?><RequestPolicy></RequestPolicy>";
		}

		Serializer serializer = new Persister(); 
		Writer result = new StringWriter();
		try {
			serializer.write(privacyPolicy, result);
		} catch (Exception e) {
			Log.e(TAG, "Can't serialize this privacy policy to an XML string.", e);
			return null;
		}
		return result.toString();
	}

	/**
	 * Create a Privacy Policy in a Java format from a XML format Privacy Policy
	 * @param privacyPolicy Privacy policy
	 * @return A Java object containing the privacy policy
	 * @throws PrivacyException 
	 */
	public static RequestPolicy fromXmlString(String privacyPolicy) throws PrivacyException {
		// -- Verify
		// Empty privacy policy
		if (null == privacyPolicy || privacyPolicy.equals("")) {
			Log.d(TAG, "Empty privacy policy. Return a null java object.");
			return null;
		}
		// Fill XML header if necessary
		String encoding = "UTF-8";
		if (!privacyPolicy.startsWith("<?xml")) {
			privacyPolicy = "<?xml version=\"1.0\" encoding=\""+encoding+"\"?>\n"+privacyPolicy;
		}
		// If only contains the XML header: empty privacy policy
		if (privacyPolicy.endsWith("?>")) {
			Log.d(TAG, "Empty privacy policy. Return a null java object.");
			return null;
		}

		// -- Convert Xml to Java
		RequestPolicy result = null;
		Serializer serializer = new Persister();       
		Reader reader = new StringReader(privacyPolicy);
		try {
			result = serializer.read(RequestPolicy.class, reader, false);
		} catch (Exception e) {
			Log.e(TAG, "[Error fromXMLString] Can't parse the privacy policy.", e);
		}
		return result;
	}
}