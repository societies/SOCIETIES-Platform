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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * @author Elizabeth
 *
 */


public class XMLPolicyReader {

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private ICtxBroker ctxBroker;
	private IIdentityManager iDM;
	
	public XMLPolicyReader(ICtxBroker broker, IIdentityManager idm){
		iDM = idm;
		this.ctxBroker = broker;
		
	}
	/**
	 * @return the ctxBroker
	 */
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	/**
	 * @param ctxBroker the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
	
	/**
	 * @return the iDM
	 */
	public IIdentityManager getiDM() {
		return iDM;
	}
	/**
	 * @param iDM the iDM to set
	 */
	public void setiDM(IIdentityManager iDM) {
		this.iDM = iDM;
	}
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
			Requestor subject;
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
			}else{
				this.log("No Subject in XML file");
				return null;
			
			}
			if (targetXML.getLength()>0){
				targets = this.readTargets(targetXML);
			}else{
				this.log("No requested Targets in XML file");
				return null;
			}
			
			if (subject == null){
				return null;
			}
			if (targets == null){
				return null;
			}
			
			return new RequestPolicy(subject,targets);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//return policy;
		return null;
	}
	
	private Requestor readSubject(NodeList subjectList){
		Element subjectElement = (Element) subjectList.item(0);
		log("subjectElement.getTagName: "+subjectElement.getTagName());
		try {
			Requestor subject;
			this.log("reading Subject");
			IIdentity providerIdentity = null;
			IIdentity cisIdentity = null;
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
						try{
							
							providerIdentity = iDM.fromJid(dpiStr); 
									
						}catch(InvalidFormatException e){
							log("Could not parse identity from xml policy. Replacing with current Public Identity from IDM");
							//TODO: Ask the user!
							providerIdentity = (IIdentity) iDM.getPublicIdentities().toArray()[0];
						}
						this.log("2. Read Identity : "+providerIdentity.toString());
						
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
						try{
							cisIdentity = iDM.fromJid(identityStr);
						}catch (InvalidFormatException e){
							log("Could not parse CIS identity from xml policy. Asking the user");
							cisIdentity = this.getCISIdentityFromUser(cisIdentity);
							
						}
					}
				}
			}
			if (providerIdentity!=null){
				if (serviceID!=null){
					return new RequestorService(providerIdentity, serviceID);
				}else if (cisIdentity!=null){
					return new RequestorCis(providerIdentity, cisIdentity);
					
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
	
	
	private IIdentity getCISIdentityFromUser(IIdentity cisIdentity){
		while (cisIdentity==null){
			String s = (String) JOptionPane.showInputDialog(null, "Could not read CIS ID from XML file. Please type the CIS ID below", "Error reading CIS Identity", JOptionPane.PLAIN_MESSAGE, null, null, "");
			try {
				cisIdentity = iDM.fromJid(s);
			} catch (InvalidFormatException e) {
				JOptionPane.showConfirmDialog(null, "Invalid CIS identity entered");
				cisIdentity = null;
				e.printStackTrace();
			}
		}
		return cisIdentity;
	}
	
	private  ArrayList<RequestItem> readTargets(NodeList target){
		ArrayList<RequestItem> items = new ArrayList<RequestItem>();
		for (int i=0; i < target.getLength(); i++){
			RequestItem item = this.readTarget((Element) target.item(i));
			if (item!=null){
				items.add(item);
			}else{
				return new ArrayList<RequestItem>();
			}
		}
		
		return items;
	}
	
	private RequestItem readTarget(Element targetElement){
		NodeList resources = targetElement.getElementsByTagName("Resource");
		Resource r = this.readResource((Element) resources.item(0));
		if (r == null){
			return null;
		}
		
		NodeList actions = targetElement.getElementsByTagName("Action");
		ArrayList<Action> actionsList = this.readActions(actions);
		if (actionsList == null){
			return null;
		}
		if (actionsList.size()==0){
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
		RequestItem rItem = new RequestItem(r,actionsList, conditionsList, isOptional);
		return rItem;
		
	}
	
	private Resource readResource(Element resourceElement){
		NodeList attributeList = resourceElement.getElementsByTagName("Attribute");
		CtxIdentifier ctxID = null;
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
					try {
						ctxID = CtxIdentifierFactory.getInstance().fromString(strCtxId);
						//ctxID = this.getBroker().parseIdentifier(strCtxId);
					} catch (CtxException e) {
						this.log("Could not parse Ctx identifier: "+strCtxId);
						e.printStackTrace();
					}
				}
			}
			else if (attributeId.compareToIgnoreCase("contextType")==0){
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
				//hardcoded context type until the policy editor is modified
				return new Resource(DataIdentifierScheme.CONTEXT,ctxID.getType());
			}
		}else{
			//hardcoded context type until the policy editor is modified			
			return new Resource(DataIdentifierScheme.CONTEXT, ctxType);
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
				if (dataType.compareToIgnoreCase("org.personalsmartspace.spm.preference.api.platform.constants.ActionConstants")==0){
					NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
					Element attributeValueElement = (Element) attributeValueList.item(0);
					ActionConstants ac = ActionConstants.valueOf(attributeValueElement.getFirstChild().getNodeValue().toUpperCase());
					a = new Action(ac);
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
				if (dataType.compareToIgnoreCase("org.personalsmartspace.spm.preference.api.platform.constants.ConditionConstants")==0){
					NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
					Element attributeValueElement = (Element) attributeValueList.item(0);
					ConditionConstants cc = ConditionConstants.valueOf(attributeValueElement.getAttribute("DataType"));
					
					conditionValue = attributeValueElement.getFirstChild().getNodeValue();
					c = new Condition(cc,conditionValue);
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
		this.logging.info(this.getClass().getName()+" : "+message);
		
	}

	public static void main(String[] args) throws IOException{
		if (args.length>0){
			String filePath = "";

			try{
				filePath = args[0];
				XMLPolicyReader reader = new XMLPolicyReader(null, null);
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