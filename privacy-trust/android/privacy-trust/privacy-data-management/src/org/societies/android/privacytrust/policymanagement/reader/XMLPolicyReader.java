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
package org.societies.android.privacytrust.policymanagement.reader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ActionConstants;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ConditionConstants;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;
/**
 * @author Elizabeth, Olivier
 *
 */


public class XMLPolicyReader {
	private final static String TAG = XMLPolicyReader.class.getSimpleName();

	public RequestPolicy readPolicyFromFile(InputStream is){
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			return this.readPolicyFromFile(doc);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException ioe){

		}catch (SAXException se){

		}
		return null;		
	}
	public RequestPolicy readPolicyFromFile(File file){

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			return this.readPolicyFromFile(doc);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException ioe){

		}catch (SAXException se){

		}
		return null;

	}

	public RequestPolicy readPolicyFromFile(Document doc){
		try {
			RequestorBean subject = null;
			ArrayList<RequestItem> targets;


			doc.getDocumentElement().normalize();
			log("Root element " + doc.getDocumentElement().getNodeName());
			NodeList subjectXML = doc.getElementsByTagName("Subject");
			NodeList targetXML = doc.getElementsByTagName("Target");
			//NodeList actionsXML = doc.getElementsByTagName("Action");
			//NodeList conditionsXML = doc.getElementsByTagName("Condition");
			//NodeList nodeLst = doc.getElementsByTagName("employee");
			//log("Information of all employees");

			if (subjectXML.getLength()>0){
				subject = this.readSubject(subjectXML);
			}
			//			else{
			//				this.log("No Subject in XML file");
			//				return null;
			//			}
			if (targetXML.getLength()>0){
				targets = this.readTargets(targetXML);
			}else{
				this.log("No requested Targets in XML file");
				targets = new ArrayList<RequestItem>();
			}

			//			if (subject == null){
			//				return null;
			//			}
			//			if (targets == null){
			//				return null;
			//			}
			RequestPolicy privacyPolicy = new RequestPolicy();
			privacyPolicy.setRequestor(subject);
			privacyPolicy.setRequestItems(targets);
			return privacyPolicy;

		} catch (Exception e) {
			e.printStackTrace();
		}
		//return policy;
		return null;
	}

	private RequestorBean readSubject(NodeList subjectList){
		Element subjectElement = (Element) subjectList.item(0);
		log("subjectElement.getTagName: "+subjectElement.getTagName());
		try {
			RequestorBean subject;
			this.log("reading Subject");
			String providerIdentity = null;
			String cisIdentity = null;
			ServiceResourceIdentifier serviceID = null;
			NodeList attributeList = subjectElement.getElementsByTagName("Attribute");
			log("attributeList.getLength: "+attributeList.getLength());

			for (int i=0; i < attributeList.getLength(); i++){
				//NodeList elementNodeList = attributeList.item(i);
				Element attributeElement = (Element) attributeList.item(i);
				this.log("reading Subject Attribute: "+attributeElement.getTextContent());
				String attributeIdAttribute = attributeElement.getAttribute("AttributeId");
				this.log("Element.getTagName: "+attributeElement.getTagName());

				if (attributeIdAttribute.compareToIgnoreCase("urn:oasis:names:tc:xacml:1.0:subject:subject-id")==0){
					this.log("Reading: "+attributeIdAttribute);
					String dataType = attributeElement.getAttribute("DataType");
					if (dataType.compareToIgnoreCase("org.societies.api.identity.IIdentity")==0){
						this.log("Reading: "+dataType);
						NodeList attributeValueList =  attributeElement.getElementsByTagName("AttributeValue");
						if (attributeValueList==null){
							this.log("attributeValue = null");
						}else{
							this.log("nl.item(0).getTextContent(): "+attributeValueList.item(0).getTextContent());
						}
						Element attributeValueElement = (Element) attributeValueList.item(0);

						String dpiStr = attributeValueElement.getFirstChild().getNodeValue();
						log("attributeValue.getNodeValue() : "+dpiStr);
						providerIdentity = dpiStr; 
						this.log("2. Read Identity : "+providerIdentity);

					}

				}else if (attributeIdAttribute.compareToIgnoreCase("serviceID")==0){
					this.log("Reading: "+attributeIdAttribute);
					String dataType = attributeElement.getAttribute("DataType");

					if (dataType.compareToIgnoreCase("org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier")==0){
						this.log("Reading: "+dataType);
						//NodeList nl = attributeElement.getElementsByTagName("AttributeValue");
						NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");

						Element attributeValueElement = (Element) attributeValueList.item(0);

						serviceID = new ServiceResourceIdentifier();
						serviceID.setServiceInstanceIdentifier(attributeValueElement.getFirstChild().getNodeValue());
					}

				}else if (attributeIdAttribute.compareToIgnoreCase("CisId")==0){
					this.log("Reading: "+attributeIdAttribute);
					String dataType = attributeElement.getAttribute("DataType");

					if (dataType.compareToIgnoreCase("org.societies.api.identity.IIdentity")==0){
						this.log("Reading: "+dataType);
						NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");

						Element attributeValueElement = (Element) attributeValueList.item(0);

						String identityStr = attributeValueElement.getFirstChild().getNodeValue();
						cisIdentity = identityStr;
					}
				}
			}
			if (providerIdentity!=null){
				if (serviceID!=null){
					RequestorServiceBean requestorService = new RequestorServiceBean();
					requestorService.setRequestorId(providerIdentity);
					requestorService.setRequestorServiceId(serviceID);
					return requestorService;
				}else if (cisIdentity!=null){
					RequestorCisBean requestorCis = new RequestorCisBean();
					requestorCis.setRequestorId(providerIdentity);
					requestorCis.setCisRequestorId(cisIdentity);
					return requestorCis;

				}
			}else{
				throw new PrivacyException("Could not parse IIdentity");
			}
		} catch (PrivacyException e) {

			e.printStackTrace();
			this.log("Could not parse IIdentity");
			return null;
		} catch (DOMException e) {
			e.printStackTrace();
			this.log("Invalid structure in XML file");
			return null;
		}

		return null;
	}


	private String getCISIdentityFromUser(String cisIdentity){
		while (cisIdentity==null){
			cisIdentity = (String) JOptionPane.showInputDialog(null, "Could not read CIS ID from XML file. Please type the CIS ID below", "Error reading CIS Identity", JOptionPane.PLAIN_MESSAGE, null, null, "");
		}
		return cisIdentity;
	}

	private  ArrayList<RequestItem> readTargets(NodeList target){
		ArrayList<RequestItem> items = new ArrayList<RequestItem>();
		for (int i=0; i < target.getLength(); i++){
			Log.i(TAG, "In a new target ("+(i+1)+"/"+target.getLength()+")");
			RequestItem item = this.readTarget((Element) target.item(i));
			if (null != item) {
				items.add(item);
			}
		}

		return items;
	}

	private RequestItem readTarget(Element targetElement){
		NodeList resources = targetElement.getElementsByTagName("Resource");
		Resource r = this.readResource((Element) resources.item(0));
		if (r == null){
			Log.i(TAG, "No resource");
			return null;
		}

		NodeList actions = targetElement.getElementsByTagName("Action");
		ArrayList<Action> actionsList = this.readActions(actions);
		if (actionsList == null || actionsList.size()==0){
			Log.i(TAG, "No action");
			return null;
		}

		NodeList conditions = targetElement.getElementsByTagName("Condition");
		ArrayList<Condition> conditionsList = new ArrayList<Condition>(); 
		if (conditions.getLength()!=0){
			conditionsList = this.readConditions(conditions);
		}

		boolean isOptional = false;
		NodeList optionalNodeList = targetElement.getElementsByTagName("optional");

		//JOptionPane.showMessageDialog(null, "in Target: "+optionalNodeList.getLength());
		if (optionalNodeList!=null){
			if (optionalNodeList.getLength()>0){
				Element valueOptional = (Element) optionalNodeList.item(optionalNodeList.getLength()-1);
				String value = valueOptional.getFirstChild().getNodeValue();
				//JOptionPane.showMessageDialog(null, "value of optional:"+value);
				if (value.equalsIgnoreCase("true")){
					isOptional = true;
				}
			}
		}
		RequestItem rItem = new RequestItem();
		rItem.setResource(r);
		rItem.setActions(actionsList);
		rItem.setConditions(conditionsList);
		rItem.setOptional(isOptional);
		return rItem;

	}

	private Resource readResource(Element resourceElement){
		DataIdentifierScheme scheme = DataIdentifierScheme.CONTEXT;
		NodeList attributeList = resourceElement.getElementsByTagName("Attribute");
		String ctxID = null;
		String ctxType = null;
		for (int i = 0; i < attributeList.getLength(); i++){
			Element attributeElement = (Element) attributeList.item(i);
			String attributeId = attributeElement.getAttribute("AttributeId");
			if (attributeId.compareToIgnoreCase("urn:oasis:names:tc:xacml:1.0:subject:resource-id")==0){
				String dataType = attributeElement.getAttribute("DataType");
				if (dataType.compareToIgnoreCase("org.societies.api.context.model.CtxIdentifier")==0){
					NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
					Element attributeValueElement = (Element) attributeValueList.item(0);

					String strCtxId = attributeValueElement.getFirstChild().getNodeValue();
					ctxID = strCtxId;
				}
			}
			else if (DataIdentifierScheme.CONTEXT.value().equals(attributeId)
					|| DataIdentifierScheme.CIS.value().equals(attributeId)
					|| DataIdentifierScheme.DEVICE.value().equals(attributeId)
					|| DataIdentifierScheme.ACTIVITY.value().equals(attributeId)) {
				scheme = DataIdentifierScheme.fromValue(attributeId);
				String dataType = attributeElement.getAttribute("DataType");
				if (dataType.compareToIgnoreCase("http://www.w3.org/2001/XMLSchema#string")==0){
					NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
					Element attributeValueElement = (Element) attributeValueList.item(0);
					ctxType = attributeValueElement.getFirstChild().getNodeValue();

				}
			}
		}

		if (ctxType == null){
			if (ctxID == null){
				return null;
			}else{
				Resource resource = new Resource();
				resource.setDataIdUri(ctxID);
				return resource;
			}
		}else{
			//TODO: make the necessary changes to include the scheme in the privacy policy			
			Resource resource = new Resource();
			resource.setScheme(scheme);
			resource.setDataType(ctxType);
			return resource;
		}
	}

	private ArrayList<Action> readActions(NodeList actionList){
		ArrayList<Action> actions = new ArrayList<Action>();
		for (int i = 0; i<actionList.getLength(); i++){
			Action a = this.readAction((Element) actionList.item(i));
			if (a!=null){
				actions.add(a);
			}
		}
		return actions;
	}
	private Action readAction(Element actionElement){
		NodeList attributeList = actionElement.getElementsByTagName("Attribute");
		Action a = null;
		for (int i = 0; i< attributeList.getLength(); i++){
			Element attributeElement = (Element) attributeList.item(i);
			String attributeId = attributeElement.getAttribute("AttributeId");
			if (attributeId.compareTo("urn:oasis:names:tc:xacml:1.0:action:action-id")==0){
				String dataType = attributeElement.getAttribute("DataType");
				if (dataType.compareToIgnoreCase("org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants")==0){
					NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
					Element attributeValueElement = (Element) attributeValueList.item(0);
					ActionConstants ac = ActionConstants.valueOf(attributeValueElement.getFirstChild().getNodeValue().toUpperCase());
					a = new Action();
					a.setActionConstant(ac);
				}
			}
		}

		if (a!=null){
			NodeList optionalNodeList = actionElement.getElementsByTagName("optional");
			//JOptionPane.showMessageDialog(null, "in Action: "+optionalNodeList.getLength());
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

	private ArrayList<Condition> readConditions(NodeList conditionList){
		ArrayList<Condition> conditions = new ArrayList<Condition>();
		for (int i = 0; i<conditionList.getLength(); i++){
			Condition c = this.readCondition((Element) conditionList.item(i));
			if (c!=null){
				conditions.add(c);
			}
		}
		return conditions;		
	}

	private Condition readCondition(Element conditionElement){
		NodeList attributeList = conditionElement.getElementsByTagName("Attribute");
		Condition c = null;
		String conditionValue = null;
		for (int i = 0; i< attributeList.getLength(); i++){
			Element attributeElement = (Element) attributeList.item(i);
			String attributeId = attributeElement.getAttribute("AttributeId");
			if (attributeId.compareTo("urn:oasis:names:tc:xacml:1.0:action:condition-id")==0){
				String dataType = attributeElement.getAttribute("DataType");
				if (dataType.compareToIgnoreCase("org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants")==0){
					NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
					Element attributeValueElement = (Element) attributeValueList.item(0);
					ConditionConstants cc = ConditionConstants.valueOf(attributeValueElement.getAttribute("DataType"));

					conditionValue = attributeValueElement.getFirstChild().getNodeValue();
					c = new Condition();
					c.setConditionConstant(cc);
					c.setValue(conditionValue);
				}
			}
		}
		if (c!=null){
			NodeList optionalNodeList = conditionElement.getElementsByTagName("optional");
			//JOptionPane.showMessageDialog(null, "in Condition: "+optionalNodeList.getLength());
			if (optionalNodeList!=null){
				if (optionalNodeList.getLength()>0){
					Element valueOptional = (Element) optionalNodeList.item(0);
					String value = valueOptional.getFirstChild().getNodeValue();
					if (value.equalsIgnoreCase("false")){
						c.setOptional(false);
					}
				}
			}
		}
		return c;		
	}

	private void log(String message){
		Log.i(TAG, message);

	}

	public static void main(String[] args) throws IOException{
		if (args.length>0){
			String filePath = "";

			try{
				filePath = args[0];
				XMLPolicyReader reader = new XMLPolicyReader();
				reader.read(filePath);
				//JOptionPane.showMessageDialog(null, "Read "+filePath);
			}
			catch(Exception e){
				//JOptionPane.showMessageDialog(null, "Failed: "+filePath);
				e.printStackTrace();
			}
		}


	}

	private void read(String filepath){
		System.out.println(filepath);
		File file = new File(filepath);

		RequestPolicy policy = readPolicyFromFile(file);
		System.out.println("Policy read OK: \n");
		System.out.println(policy.toString());		
	}


}