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
package org.societies.android.privacytrust.policymanagement;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager;
import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.android.privacytrust.policymanagement.reader.XMLPolicyReader;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;


/**
 * @author Olivier Maridat (Trialog)
 * @date 5 déc. 2011
 */
public class PrivacyPolicyManager implements IPrivacyPolicyManager {
	private final static String TAG = PrivacyPolicyManager.class.getSimpleName();

	private PrivacyPolicyManagerRemote privacyPolicyManagerRemote;


	public PrivacyPolicyManager(Context context)  {
		privacyPolicyManagerRemote = new PrivacyPolicyManagerRemote(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager#getPrivacyPolicy(org.societies.api.schema.identity.RequestorBean)
	 */
	public RequestPolicy getPrivacyPolicy(RequestorBean requestor) throws PrivacyException {
		// -- Verify
		if (null == requestor || null == requestor.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy. Requestor needed.");
		}

		return privacyPolicyManagerRemote.getPrivacyPolicy(requestor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)
	 */
	public RequestPolicy updatePrivacyPolicy(RequestPolicy privacyPolicy) throws PrivacyException {
		// -- Verify
		if (null == privacyPolicy) {
			throw new PrivacyException("The privacy policy to update is empty.");
		}
		if (null == privacyPolicy.getRequestor() || null == privacyPolicy.getRequestor().getRequestorId()) {
			throw new PrivacyException("Not enought information to update a privacy policy. Requestor needed.");
		}

		// -- Add
		return privacyPolicyManagerRemote.updatePrivacyPolicy(privacyPolicy);
	}
	public RequestPolicy updatePrivacyPolicy(String privacyPolicyXml, RequestorBean requestor) throws PrivacyException {
		// Retrieve the privacy policy
		RequestPolicy privacyPolicy = fromXmlString(privacyPolicyXml);
		if (null == privacyPolicy) {
			throw new PrivacyException("Ths XML formatted string of the privacy policy can not be parsed as a privacy policy.");
		}
		// Fill the requestor id
		privacyPolicy.setRequestor(requestor);
		// Create / Store it
		return updatePrivacyPolicy(privacyPolicy);
	}

	public boolean deletePrivacyPolicy(RequestorBean requestor) throws PrivacyException {
		// -- Verify
		if (null == requestor || null == requestor.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy. Requestor needed.");
		}

		// -- Delete
		return privacyPolicyManagerRemote.deletePrivacyPolicy(requestor);
	}

	public RequestPolicy inferPrivacyPolicy(int privacyPolicyType, Map configuration) throws PrivacyException {
		List<RequestItem> requests = new ArrayList<RequestItem>();
		RequestPolicy privacyPolicy = new RequestPolicy();
		privacyPolicy.setRequestItems(requests);
		return privacyPolicy;
	}

	public String toXmlString(RequestPolicy privacyPolicy) {
		String encoding = "UTF-8";
		// -- Empty Privacy Policy
		if (null == privacyPolicy) {
			return "<?xml version=\"1.0\" encoding=\""+encoding+"\"?><RequestPolicy></RequestPolicy>";
		}

		// -- Generate the XML Privacy Policy
//		StringBuilder str = new StringBuilder("<?xml version=\"1.0\" encoding=\""+encoding+"\"?>\n<RequestPolicy>");
//		// - Requestor
//		if (null != privacyPolicy.getRequestor()) {
//			str.append("\n<Subject>");
//			str.append("\n\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\" DataType=\""+IIdentity.class.getCanonicalName()+"\">");
//			str.append("\n\t\t<AttributeValue>"+privacyPolicy.getRequestor().getRequestorId()+"</AttributeValue>");
//			str.append("\n\t</Attribute>");
//			// CIS
//			if (privacyPolicy.getRequestor() instanceof RequestorCisBean) {
//				str.append("\n\t<Attribute AttributeId=\"CisId\" DataType=\""+IIdentity.class.getCanonicalName()+"\">");
//				str.append("\n\t\t<AttributeValue>"+((RequestorCisBean)privacyPolicy.getRequestor()).getCisRequestorId()+"</AttributeValue>");
//				str.append("\n\t</Attribute>");
//			}
//			// Service
//			if (privacyPolicy.getRequestor() instanceof RequestorServiceBean) {
//				str.append("\n\t<Attribute AttributeId=\"serviceID\" DataType=\""+ServiceResourceIdentifier.class.getCanonicalName()+"\">");
//				str.append("\n\t\t<AttributeValue>"+((RequestorServiceBean)privacyPolicy.getRequestor()).getRequestorServiceId().getServiceInstanceIdentifier()+"</AttributeValue>");
//				str.append("\n\t</Attribute>");
//			}
//			str.append("</Subject>");
//		}
//		// Requested Items
//		for (RequestItem requestItem : privacyPolicy.getRequestItems()) {
//			str.append("\n<Target>");
//			str.append("\n\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:resource-id\" DataType=\"org.societies.api.context.model.CtxIdentifier\">");
//			str.append("\n\t\t<AttributeValue>"+requestItem.getResource().getDataIdUri()+"</AttributeValue>");
//			str.append("\n\t</Attribute>");
//			for (Action action : requestItem.getActions()){
//				str.append("\n\t<Action>");
//				str.append("\n\t\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" DataType=\"org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants\">");
//				str.append("\n\t\t\t<AttributeValue>"+action.getActionConstant().name()+"</AttributeValue>");
//				str.append("\n\t\t</Attribute>");
//				str.append("\n\t\t<optional>"+action.isOptional()+"</optional>");
//				str.append("\n\t</Action>");
//			}
//			for (Condition condition : requestItem.getConditions()){
//				str.append("\n\t<Condition>");
//				str.append("\n\t\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\" DataType=\"org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants\">");
//				str.append("\n\t\t\t<AttributeValue DataType=\""+condition.getConditionConstant().name()+"\">"+condition.getValue()+"</AttributeValue>");
//				str.append("\n\t\t</Attribute>");
//				str.append("\n\t\t<optional>"+condition.isOptional()+"</optional>"); 
//				str.append("\n\t</Condition>");
//			}
//			str.append("\n\t<optional>"+requestItem.isOptional()+"</optional>");
//			str.append("\n</Target>");
//		}
//		str.append("</RequestPolicy>");
//		return str.toString();

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

	public RequestPolicy fromXmlString(String privacyPolicy) throws PrivacyException {
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
//		XMLPolicyReader xmlPolicyReader = new XMLPolicyReader();
//		try {
//			// -- Create XMLDocument version of the privacy policy
//			DocumentBuilder xmlDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//			Document privacyPolicyDocument = xmlDocumentBuilder.parse(new ByteArrayInputStream(privacyPolicy.getBytes(encoding)));
//			// -- Transform XML Privacy Policy to Java Privacy Policy
//			result = xmlPolicyReader.readPolicyFromFile(privacyPolicyDocument);
//		} catch (ParserConfigurationException e) {
//			Log.e(TAG, "[Error fromXMLString] Can't parse the privacy policy.", e);
//		} catch (SAXException e) {
//			Log.e(TAG, "[Error fromXMLString] Can't parse the privacy policy. SAX error.", e);
//		} catch (IOException e) {
//			Log.e(TAG, "[Error fromXMLString] Can't parse the privacy policy. IO error.", e);
//		}
		return result;
	}
}
