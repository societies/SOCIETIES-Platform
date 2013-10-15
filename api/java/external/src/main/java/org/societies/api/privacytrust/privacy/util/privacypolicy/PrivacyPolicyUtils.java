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
package org.societies.api.privacytrust.privacy.util.privacypolicy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.model.CisAttributeTypes;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyBehaviourConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyTypeConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Interface exposed to Societies components and 3P services in order to do actions relative to a privacy policy
 * creation and manipulation
 * 
 * @author Olivier Maridat (Trialog)
 * @created 18-dec.-2012 19:41:29
 */
public class PrivacyPolicyUtils {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyUtils.class.getName());

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
	public static RequestPolicy inferPrivacyPolicy(PrivacyPolicyTypeConstants privacyPolicyType, Map configuration) throws PrivacyException {
		RequestPolicy privacyPolicy = new RequestPolicy();
		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		
		// ---- Add privacy policy type
		privacyPolicy.setPrivacyPolicyType(privacyPolicyType);

		// ---- Add configured request items
		if (null != configuration && configuration.containsKey("requestItems")) {
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
	 * Retrieve all data types requested in a privacy policy
	 * Warning: data types are stored without scheme, so these data can be from several schemes (cis, context)
	 * 
	 * @param privacyPolicy Privacy policy
	 * @return A list of data types requested, or null if the privacy policy is null or empty
	 */
	public static List<String> getDataTypes(RequestPolicy privacyPolicy) {
		List<String> dataTypes = null;
		// -- Empty privacy policy
		if (null == privacyPolicy || null == privacyPolicy.getRequestItems() || privacyPolicy.getRequestItems().size() <= 0) {
			return dataTypes;
		}

		// -- Retrieve data type list
		dataTypes = new ArrayList<String>();
		for(RequestItem requestItem : privacyPolicy.getRequestItems()) {
			dataTypes.add(ResourceUtils.getDataType(requestItem.getResource()));
		}
		return dataTypes;
	}

	/**
	 * Retrieve all data types of a peculiar scheme (cis, context, ...) in a privacy policy
	 * 
	 * @param schemeFilter Scheme of the data types
	 * @param privacyPolicy Privacy policy
	 * @return A list of data types of the peculiar scheme, or null if the privacy policy is null or empty
	 */
	public static List<String> getDataTypes(DataIdentifierScheme schemeFilter, RequestPolicy privacyPolicy) {
		List<String> dataTypes = null;
		// -- Empty privacy policy
		if (null == privacyPolicy || null == privacyPolicy.getRequestItems() || privacyPolicy.getRequestItems().size() <= 0 || null == schemeFilter) {
			return dataTypes;
		}

		// -- Retrieve data type list
		dataTypes = new ArrayList<String>();
		for(RequestItem requestItem : privacyPolicy.getRequestItems()) {
			try {
				DataIdentifier dataId = ResourceUtils.getDataIdentifier(requestItem.getResource());
				if (schemeFilter.name().equals(dataId.getScheme().name())) {
					dataTypes.add(dataId.getType());
				}
			} catch (MalformedCtxIdentifierException e) {
				// "Too bad: can't retrieve the data identifier. Privacy policy must be badly formatted.", e);
				return dataTypes;
			}
		}
		return dataTypes;
	}


	/**
	 * Create a XACML string representing the Privacy Policy from a Java Privacy Policy
	 * The format of the privacy policy follows the XACML specification
	 * @param privacyPolicy Privacy policy as a Java object
	 * @return A string containing the XACML version the privacy policy
	 */
	public static String toXacmlString(RequestPolicy privacyPolicy) {
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

	/**
	 * Replaced by toXacmlString to avoid misunderstanding
	 * @param privacyPolicy Privacy policy as a Java object
	 * @return A string containing the XML version the privacy policy
	 */
	@Deprecated
	public static String toXmlString(RequestPolicy privacyPolicy) {
		return toXacmlString(privacyPolicy);
	}

	/**
	 * Create a Java Privacy Policy from a XACML string representing the Privacy Policy
	 * The format of the privacy policy follows the XACML specification
	 * @param xmlPrivacyPolicy Privacy policy as a XACML string
	 * @return Java privacy policy
	 */
	public static RequestPolicy fromXacmlString(String xmlPrivacyPolicy) {
		// -- Verify
		// Empty privacy policy
		if (null == xmlPrivacyPolicy || xmlPrivacyPolicy.equals("")) {
			//"Empty privacy policy. Return a null java object.");
			return null;
		}
		// Fill XML header if necessary
		String[] encoding = {"UTF-8", "ISO-8859-1"};
		if (!xmlPrivacyPolicy.startsWith("<?xml")) {
			xmlPrivacyPolicy = "<?xml version=\"1.0\" encoding=\""+encoding[0]+"\"?>\n"+xmlPrivacyPolicy;
		}
		// If only contains the XML header: empty privacy policy
		if (xmlPrivacyPolicy.endsWith("?>")) {
			//"Empty privacy policy. Return a null java object.");
			return null;
		}

		// -- Convert Xml to Java
		// - Create XMLDocument version of the privacy policy
		Document docPrivacyPolicy = null;
		try {
			DocumentBuilder xmlDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			for(int i=0; i<encoding.length; i++) {
				try {
					// Try parse with default charset encoding
					if (0 == i) {
						docPrivacyPolicy = xmlDocumentBuilder.parse(new ByteArrayInputStream(xmlPrivacyPolicy.getBytes()));
						if (null != docPrivacyPolicy) {
							break;
						}
					}
					// Try parse with a specified charset encoding
					docPrivacyPolicy = xmlDocumentBuilder.parse(new ByteArrayInputStream(xmlPrivacyPolicy.getBytes(encoding[i])));
					if (null != docPrivacyPolicy) {
						break;
					}
				}
				catch (UnsupportedEncodingException e) {
				}
			}
		}
		catch (ParserConfigurationException e) {
			LOG.error("Error during transformation of the string to an XML document: "+e.getMessage(), e);
		}
		catch (SAXException e) {
			LOG.error("Error during transformation of the string to an XML document: "+e.getMessage(), e);
		}
		catch (IOException e) {
			LOG.error("Error during transformation of the string to an XML document: "+e.getMessage(), e);
		}
		// - Put the XML document into a RequestPolicy object
		RequestPolicy result = fromXmlDocument(docPrivacyPolicy);
		return result;
	}

	private static RequestPolicy fromXmlDocument(Document docPrivacyPolicy) {
		RequestPolicy privacyPolicy = null;
		// -- Empty XML Document
		if (null == docPrivacyPolicy) {
			return privacyPolicy;
		}

		// -- Parse XML String
		// - Init
		RequestorBean subject = null;
		ArrayList<RequestItem> targets = new ArrayList<RequestItem>();
		docPrivacyPolicy.getDocumentElement().normalize();
		LOG.debug("Root element: " + docPrivacyPolicy.getDocumentElement().getNodeName());
		// - Parse Subject
		NodeList subjectXML = docPrivacyPolicy.getElementsByTagName("Subject");
		if (null != subjectXML && subjectXML.getLength()>0) {
			subject = readSubject(subjectXML);
		}
		// - Parse Target
		NodeList targetXML = docPrivacyPolicy.getElementsByTagName("Target");
		if (null != targetXML && targetXML.getLength()>0) {
			targets = readTargets(targetXML);
		}

		// -- Construct Java privacy Policy
		privacyPolicy = RequestPolicyUtils.create(subject, targets);
		return privacyPolicy;
	}

	private static RequestorBean readSubject(NodeList subjectList){
		RequestorBean subject = null;
		Element subjectElement = (Element) subjectList.item(0);
		log("subjectElement.getTagName: "+subjectElement.getTagName());
		try {
			String providerIdentity = null;
			String requestorIdentity = null;
			NodeList attributeList = subjectElement.getElementsByTagName("Attribute");
			LOG.debug("attributeList.getLength: "+attributeList.getLength());

			// - Search all data
			for (int i=0; i < attributeList.getLength(); i++) {
				Element attributeElement = (Element) attributeList.item(i);
				LOG.debug("reading Subject Attribute: "+attributeElement.getTextContent());
				String attributeIdAttribute = attributeElement.getAttribute("AttributeId");
				LOG.debug("Element.getTagName: "+attributeElement.getTagName());
				// CSS
				if (attributeIdAttribute.compareToIgnoreCase("urn:oasis:names:tc:xacml:1.0:subject:subject-id")==0){
					LOG.debug("Reading: "+attributeIdAttribute);
					String dataType = attributeElement.getAttribute("DataType");
					if (dataType.compareToIgnoreCase(IIdentity.class.getName())==0){
						LOG.debug("Reading: "+dataType);
						NodeList attributeValueList =  attributeElement.getElementsByTagName("AttributeValue");
						if (attributeValueList==null){
							LOG.debug("attributeValue = null");
						}
						else{
							LOG.debug("nl.item(0).getTextContent(): "+attributeValueList.item(0).getTextContent());
						}
						Element attributeValueElement = (Element) attributeValueList.item(0);

						providerIdentity = attributeValueElement.getFirstChild().getNodeValue();
						LOG.debug("attributeValue.getNodeValue() : "+providerIdentity);
					}

				}
				// Service
				else if (attributeIdAttribute.compareToIgnoreCase("serviceID")==0){
					LOG.debug("Reading: "+attributeIdAttribute);
					String dataType = attributeElement.getAttribute("DataType");
					if (dataType.compareToIgnoreCase(ServiceResourceIdentifier.class.getName())==0){
						LOG.debug("Reading: "+dataType);
						NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
						Element attributeValueElement = (Element) attributeValueList.item(0);
						requestorIdentity = attributeValueElement.getFirstChild().getNodeValue();
					}

				}
				// CIS
				else if (attributeIdAttribute.compareToIgnoreCase("CisId")==0){
					LOG.debug("Reading: "+attributeIdAttribute);
					String dataType = attributeElement.getAttribute("DataType");
					if (dataType.compareToIgnoreCase(IIdentity.class.getName())==0){
						LOG.debug("Reading: "+dataType);
						NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
						Element attributeValueElement = (Element) attributeValueList.item(0);
						requestorIdentity = attributeValueElement.getFirstChild().getNodeValue();
					}
				}
			}

			// - Create the RequestorBean
			if (providerIdentity != null) {
				subject = RequestorUtils.create(providerIdentity, requestorIdentity);
			}
		}
		catch (DOMException e) {
			LOG.error("Invalid structure in XML file: "+e.getMessage(), e);
		}
		return subject;
	}

	private static  ArrayList<RequestItem> readTargets(NodeList target){
		ArrayList<RequestItem> items = new ArrayList<RequestItem>();
		for (int i=0; i < target.getLength(); i++) {
			log("In a new target ("+(i+1)+"/"+target.getLength()+")");
			RequestItem item = readTarget((Element) target.item(i));
			if (null != item) {
				items.add(item);
			}
		}
		return items;
	}

	private static RequestItem readTarget(Element targetElement){
		NodeList resources = targetElement.getElementsByTagName("Resource");
		Resource r = readResource((Element) resources.item(0));
		if (r == null){
			log("No resource");
			return null;
		}

		NodeList actions = targetElement.getElementsByTagName("Action");
		ArrayList<Action> actionsList = readActions(actions);
		if (actionsList == null || actionsList.size()==0){
			log("No action");
			return null;
		}

		NodeList conditions = targetElement.getElementsByTagName("Condition");
		ArrayList<Condition> conditionsList = new ArrayList<Condition>(); 
		if (conditions.getLength()!=0){
			conditionsList = readConditions(conditions);
		}

		boolean isOptional = false;
		NodeList optionalNodeList = targetElement.getElementsByTagName("optional");

		if (optionalNodeList!=null){
			if (optionalNodeList.getLength()>0){
				Element valueOptional = (Element) optionalNodeList.item(optionalNodeList.getLength()-1);
				String value = valueOptional.getFirstChild().getNodeValue();
				if (value.equalsIgnoreCase("true")) {
					isOptional = true;
				}
			}
		}
		RequestItem rItem = RequestItemUtils.create(r,actionsList, conditionsList, isOptional);
		return rItem;

	}

	private static Resource readResource(Element resourceElement) {
		DataIdentifierScheme scheme = DataIdentifierScheme.CONTEXT;
		NodeList attributeList = resourceElement.getElementsByTagName("Attribute");
		DataIdentifier dataId = null;
		String dataType = null;
		for (int i = 0; i < attributeList.getLength(); i++){
			Element attributeElement = (Element) attributeList.item(i);
			String attributeId = attributeElement.getAttribute("AttributeId");
			if (attributeId.compareToIgnoreCase("urn:oasis:names:tc:xacml:1.0:subject:resource-id")==0){
				String xmlDataType = attributeElement.getAttribute("DataType");
				if (xmlDataType.compareToIgnoreCase(org.societies.api.schema.identity.DataIdentifier.class.getName())==0
						|| xmlDataType.compareToIgnoreCase("org.societies.api.context.model.CtxIdentifier")==0){
					NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
					Element attributeValueElement = (Element) attributeValueList.item(0);

					String strCtxId = attributeValueElement.getFirstChild().getNodeValue();
					try {
						dataId = DataIdentifierFactory.fromUri(strCtxId);
					} catch (CtxException e) {
						log("Could not parse Ctx identifier: "+strCtxId);
						e.printStackTrace();
					}
				}
			}
			else if (DataIdentifierScheme.CONTEXT.value().equals(attributeId)
					|| DataIdentifierScheme.CIS.value().equals(attributeId)
					|| DataIdentifierScheme.DEVICE.value().equals(attributeId)
					|| DataIdentifierScheme.ACTIVITY.value().equals(attributeId)) {
				scheme = DataIdentifierScheme.fromValue(attributeId);
				String xmlDataType = attributeElement.getAttribute("DataType");
				if (xmlDataType.compareToIgnoreCase("http://www.w3.org/2001/XMLSchema#string")==0){
					NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
					Element attributeValueElement = (Element) attributeValueList.item(0);
					dataType = attributeValueElement.getFirstChild().getNodeValue();
				}
			}
		}

		if (dataType == null) {
			if (dataId == null) {
				return null;
			}
			else{
				return ResourceUtils.create(scheme, dataId.getType());
			}
		}
		else {
			return ResourceUtils.create(scheme, dataType);
		}
	}

	private static ArrayList<Action> readActions(NodeList actionList){
		ArrayList<Action> actions = new ArrayList<Action>();
		for (int i = 0; i<actionList.getLength(); i++){
			Action a = readAction((Element) actionList.item(i));
			if (a!=null){
				actions.add(a);
			}
		}
		return actions;
	}
	private static Action readAction(Element actionElement){
		NodeList attributeList = actionElement.getElementsByTagName("Attribute");
		Action a = null;
		for (int i = 0; i< attributeList.getLength(); i++){
			Element attributeElement = (Element) attributeList.item(i);
			String attributeId = attributeElement.getAttribute("AttributeId");
			if (attributeId.compareTo("urn:oasis:names:tc:xacml:1.0:action:action-id")==0){
				String dataType = attributeElement.getAttribute("DataType");
				if (dataType.compareToIgnoreCase(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.class.getName())==0
						|| dataType.compareToIgnoreCase("org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants")==0){
					NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
					Element attributeValueElement = (Element) attributeValueList.item(0);
					ActionConstants ac = ActionConstants.valueOf(attributeValueElement.getFirstChild().getNodeValue().toUpperCase());
					a = ActionUtils.create(ac);
				}
			}
		}

		if (a!=null){
			NodeList optionalNodeList = actionElement.getElementsByTagName("optional");
			if (optionalNodeList!=null){
				if (optionalNodeList.getLength()>0){
					Element valueOptional = (Element) optionalNodeList.item(0);
					String value = valueOptional.getFirstChild().getNodeValue();
					if (value.equalsIgnoreCase("true")){
						a.setOptional(true);
					}
				}
			}
		}
		return a;
	}

	private static ArrayList<Condition> readConditions(NodeList conditionList){
		ArrayList<Condition> conditions = new ArrayList<Condition>();
		for (int i = 0; i<conditionList.getLength(); i++){
			Condition c = readCondition((Element) conditionList.item(i));
			if (c!=null){
				conditions.add(c);
			}
		}
		return conditions;		
	}

	private static Condition readCondition(Element conditionElement){
		NodeList attributeList = conditionElement.getElementsByTagName("Attribute");
		Condition c = null;
		String conditionValue = null;
		for (int i = 0; i< attributeList.getLength(); i++){
			Element attributeElement = (Element) attributeList.item(i);
			String attributeId = attributeElement.getAttribute("AttributeId");
			if (attributeId.compareTo("urn:oasis:names:tc:xacml:1.0:action:condition-id")==0){
				String dataType = attributeElement.getAttribute("DataType");
				if (dataType.compareToIgnoreCase(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants.class.getName())==0
						|| dataType.compareToIgnoreCase("org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants")==0){
					NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
					Element attributeValueElement = (Element) attributeValueList.item(0);
					ConditionConstants cc = ConditionConstants.valueOf(attributeValueElement.getAttribute("DataType"));

					conditionValue = attributeValueElement.getFirstChild().getNodeValue();
					c = ConditionUtils.create(cc,conditionValue);
				}
			}
		}
		if (c!=null){
			NodeList optionalNodeList = conditionElement.getElementsByTagName("optional");
			if (optionalNodeList!=null){
				if (optionalNodeList.getLength()>0){
					Element valueOptional = (Element) optionalNodeList.item(0);
					String value = valueOptional.getFirstChild().getNodeValue();
					if (value.equalsIgnoreCase("true")){
						c.setOptional(true);
					}
//					if (value.equalsIgnoreCase("false")){
//						c.setOptional(false);
//					}
				}
			}
		}
		return c;		
	}

	private static void log(String message){
		//logging.info(message);
	}
}