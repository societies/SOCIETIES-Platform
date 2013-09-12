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
package org.societies.integration.tests.bit.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.identity.*;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.*;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Elizabeth
 */


public class XMLPolicyReader {

    private static final Logger log = LoggerFactory.getLogger(XMLPolicyReader.class);

    private ICtxBroker ctxBroker;
    private IIdentityManager iDM;

    public XMLPolicyReader(ICtxBroker broker, IIdentityManager idm) {
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

    public RequestPolicy readPolicyFromFile(InputStream is) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            return this.readPolicyFromFile(doc);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public RequestPolicy readPolicyFromFile(File file) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            return this.readPolicyFromFile(doc);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;

    }

    public RequestPolicy readPolicyFromFile(Document doc) {
        try {
            Requestor subject = null;
            ArrayList<RequestItem> targets;


            doc.getDocumentElement().normalize();
            log.debug("Root element " + doc.getDocumentElement().getNodeName());
            NodeList subjectXML = doc.getElementsByTagName("Subject");
            NodeList targetXML = doc.getElementsByTagName("Target");
            //NodeList actionsXML = doc.getElementsByTagName("Action");
            //NodeList conditionsXML = doc.getElementsByTagName("Condition");
            //NodeList nodeLst = doc.getElementsByTagName("employee");
            //log("Information of all employees");

            if (subjectXML.getLength() > 0) {
                subject = this.readSubject(subjectXML);
            }
            //			else{
            //				log("No Subject in XML file");
            //				return null;
            //			}
            if (targetXML.getLength() > 0) {
                targets = readTargets(targetXML);
            } else {
                log.debug("No requested Targets in XML file");
                targets = new ArrayList<RequestItem>();
            }

            //			if (subject == null){
            //				return null;
            //			}
            //			if (targets == null){
            //				return null;
            //			}

            return new RequestPolicy(subject, targets);

        } catch (Exception e) {
            log.error("", e);
        }
        //return policy;
        return null;
    }

    private Requestor readSubject(NodeList subjectList) {
        Element subjectElement = (Element) subjectList.item(0);
        log.debug("subjectElement.getTagName: " + subjectElement.getTagName());
        try {
            Requestor subject;
            log.debug("reading Subject");
            IIdentity providerIdentity = null;
            IIdentity cisIdentity = null;
            ServiceResourceIdentifier serviceID = null;
            NodeList attributeList = subjectElement.getElementsByTagName("Attribute");
            log.debug("attributeList.getLength: " + attributeList.getLength());

            for (int i = 0; i < attributeList.getLength(); i++) {
                //NodeList elementNodeList = attributeList.item(i);
                Element attributeElement = (Element) attributeList.item(i);
                log.debug("reading Subject Attribute: " + attributeElement.getTextContent());
                String attributeIdAttribute = attributeElement.getAttribute("AttributeId");
                log.debug("Element.getTagName: " + attributeElement.getTagName());

                if (attributeIdAttribute.compareToIgnoreCase("urn:oasis:names:tc:xacml:1.0:subject:subject-id") == 0) {
                    log.debug("Reading: " + attributeIdAttribute);
                    String dataType = attributeElement.getAttribute("DataType");
                    if (dataType.compareToIgnoreCase(IIdentity.class.getName()) == 0) {
                        log.debug("Reading: " + dataType);
                        NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
                        if (attributeValueList == null) {
                            log.debug("attributeValue = null");
                        } else {
                            log.debug("nl.item(0).getTextContent(): " + attributeValueList.item(0).getTextContent());
                        }
                        Element attributeValueElement = (Element) attributeValueList.item(0);

                        String dpiStr = attributeValueElement.getFirstChild().getNodeValue();
                        log.debug("attributeValue.getNodeValue() : " + dpiStr);
                        try {

                            providerIdentity = iDM.fromJid(dpiStr);

                        } catch (InvalidFormatException e) {
                            log.debug("Could not parse identity from xml policy. Replacing with current Public Identity from IDM");
                            //TODO: Ask the user!
                            providerIdentity = (IIdentity) iDM.getPublicIdentities().toArray()[0];
                        }
                        log.debug("2. Read Identity : " + providerIdentity.toString());

                    }

                } else if (attributeIdAttribute.compareToIgnoreCase("serviceID") == 0) {
                    log.debug("Reading: " + attributeIdAttribute);
                    String dataType = attributeElement.getAttribute("DataType");

                    if (dataType.compareToIgnoreCase(ServiceResourceIdentifier.class.getName()) == 0) {
                        log.debug("Reading: " + dataType);
                        //NodeList nl = attributeElement.getElementsByTagName("AttributeValue");
                        NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");

                        Element attributeValueElement = (Element) attributeValueList.item(0);

                        serviceID = new ServiceResourceIdentifier();
                        serviceID.setServiceInstanceIdentifier(attributeValueElement.getFirstChild().getNodeValue());
                    }

                } else if (attributeIdAttribute.compareToIgnoreCase("CisId") == 0) {
                    log.debug("Reading: " + attributeIdAttribute);
                    String dataType = attributeElement.getAttribute("DataType");

                    if (dataType.compareToIgnoreCase(IIdentity.class.getName()) == 0) {
                        log.debug("Reading: " + dataType);
                        NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");

                        Element attributeValueElement = (Element) attributeValueList.item(0);

                        String identityStr = attributeValueElement.getFirstChild().getNodeValue();
                        try {
                            cisIdentity = iDM.fromJid(identityStr);
                        } catch (InvalidFormatException e) {
                            log.debug("Could not parse CIS identity from xml policy. Asking the user");
                            cisIdentity = this.getCISIdentityFromUser(cisIdentity);

                        }
                    }
                }
            }
            if (providerIdentity != null) {
                if (serviceID != null) {
                    return new RequestorService(providerIdentity, serviceID);
                } else if (cisIdentity != null) {
                    return new RequestorCis(providerIdentity, cisIdentity);

                }
            } else {
                throw new PrivacyException("Could not parse IIdentity");
            }
        } catch (PrivacyException e) {
            log.error("Could not parse IIdentity", e);
            return null;
        } catch (DOMException e) {
            log.error("Invalid structure in XML file", e);
            return null;
        }

        return null;
    }


    private IIdentity getCISIdentityFromUser(IIdentity cisIdentity) {
        while (cisIdentity == null) {
            String s = (String) JOptionPane.showInputDialog(null, "Could not read CIS ID from XML file. Please type the CIS ID below", "Error reading CIS Identity", JOptionPane.PLAIN_MESSAGE, null, null, "");
            try {
                cisIdentity = iDM.fromJid(s);
            } catch (InvalidFormatException e) {
                JOptionPane.showConfirmDialog(null, "Invalid CIS identity entered");
                cisIdentity = null;
                log.error("", e);
            }
        }
        return cisIdentity;
    }

    private static ArrayList<RequestItem> readTargets(NodeList target) {
        ArrayList<RequestItem> items = new ArrayList<RequestItem>();
        for (int i = 0; i < target.getLength(); i++) {
            log.info("In a new target (" + (i + 1) + "/" + target.getLength() + ")");
            RequestItem item = readTarget((Element) target.item(i));
            if (null != item) {
                items.add(item);
            }
        }

        return items;
    }

    private static RequestItem readTarget(Element targetElement) {
        NodeList resources = targetElement.getElementsByTagName("Resource");
        Resource r = readResource((Element) resources.item(0));
        if (r == null) {
            log.info("No resource");
            return null;
        }

        NodeList actions = targetElement.getElementsByTagName("Action");
        ArrayList<Action> actionsList = readActions(actions);
        if (actionsList == null || actionsList.size() == 0) {
            log.info("No action");
            return null;
        }

        NodeList conditions = targetElement.getElementsByTagName("Condition");
        ArrayList<Condition> conditionsList = new ArrayList<Condition>();
        if (conditions.getLength() != 0) {
            conditionsList = readConditions(conditions);
        }

        boolean isOptional = false;
        NodeList optionalNodeList = targetElement.getElementsByTagName("optional");

        //JOptionPane.showMessageDialog(null, "in Target: "+optionalNodeList.getLength());
        if (optionalNodeList != null) {
            if (optionalNodeList.getLength() > 0) {
                Element valueOptional = (Element) optionalNodeList.item(optionalNodeList.getLength() - 1);
                String value = valueOptional.getFirstChild().getNodeValue();
                //JOptionPane.showMessageDialog(null, "value of optional:"+value);
                if (value.equalsIgnoreCase("true")) {
                    isOptional = true;
                }
            }
        }
        return new RequestItem(r, actionsList, conditionsList, isOptional);

    }

    private static Resource readResource(Element resourceElement) {
        DataIdentifierScheme scheme = DataIdentifierScheme.CONTEXT;
        NodeList attributeList = resourceElement.getElementsByTagName("Attribute");
        CtxIdentifier ctxID = null;
        String ctxType = null;
        for (int i = 0; i < attributeList.getLength(); i++) {
            Element attributeElement = (Element) attributeList.item(i);
            String attributeId = attributeElement.getAttribute("AttributeId");
            if (attributeId.compareToIgnoreCase("urn:oasis:names:tc:xacml:1.0:subject:resource-id") == 0) {
                String dataType = attributeElement.getAttribute("DataType");
                if (dataType.compareToIgnoreCase("org.societies.api.context.model.CtxIdentifier") == 0) {
                    NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
                    Element attributeValueElement = (Element) attributeValueList.item(0);

                    String strCtxId = attributeValueElement.getFirstChild().getNodeValue();
                    try {
                        ctxID = CtxIdentifierFactory.getInstance().fromString(strCtxId);
                        //ctxID = this.getBroker().parseIdentifier(strCtxId);
                    } catch (CtxException e) {
                        log.error("Could not parse Ctx identifier: " + strCtxId, e);
                    }
                }
            } else if (DataIdentifierScheme.CONTEXT.value().equals(attributeId)
                    || DataIdentifierScheme.CIS.value().equals(attributeId)
                    || DataIdentifierScheme.DEVICE.value().equals(attributeId)
                    || DataIdentifierScheme.ACTIVITY.value().equals(attributeId)) {
                scheme = DataIdentifierScheme.fromValue(attributeId);
                String dataType = attributeElement.getAttribute("DataType");
                if (dataType.compareToIgnoreCase("http://www.w3.org/2001/XMLSchema#string") == 0) {
                    NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
                    Element attributeValueElement = (Element) attributeValueList.item(0);
                    ctxType = attributeValueElement.getFirstChild().getNodeValue();

                }
            }
        }

        if (ctxType == null) {
            if (ctxID == null) {
                return null;
            } else {

                return new Resource(scheme, ctxID.getType());
            }
        } else {
            //TODO: make the necessary changes to include the scheme in the privacy policy
            return new Resource(scheme, ctxType);
        }
    }

    private static ArrayList<Action> readActions(NodeList actionList) {
        ArrayList<Action> actions = new ArrayList<Action>();
        for (int i = 0; i < actionList.getLength(); i++) {
            Action a = readAction((Element) actionList.item(i));
            if (a != null) {
                actions.add(a);
            }
        }
        return actions;
    }

    private static Action readAction(Element actionElement) {
        NodeList attributeList = actionElement.getElementsByTagName("Attribute");
        Action a = null;
        for (int i = 0; i < attributeList.getLength(); i++) {
            Element attributeElement = (Element) attributeList.item(i);
            String attributeId = attributeElement.getAttribute("AttributeId");
            if (attributeId.compareTo("urn:oasis:names:tc:xacml:1.0:action:action-id") == 0) {
                String dataType = attributeElement.getAttribute("DataType");
                if (dataType.compareToIgnoreCase(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.class.getName()) == 0
                        || dataType.compareToIgnoreCase("org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants") == 0) {
                    NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
                    Element attributeValueElement = (Element) attributeValueList.item(0);
                    ActionConstants ac = ActionConstants.valueOf(attributeValueElement.getFirstChild().getNodeValue().toUpperCase());
                    a = new Action(ac);
                }
            }
        }

        if (a != null) {
            NodeList optionalNodeList = actionElement.getElementsByTagName("optional");
            //JOptionPane.showMessageDialog(null, "in Action: "+optionalNodeList.getLength());
            if (optionalNodeList != null) {
                if (optionalNodeList.getLength() > 0) {
                    Element valueOptional = (Element) optionalNodeList.item(0);
                    String value = valueOptional.getFirstChild().getNodeValue();
                    if (value.equalsIgnoreCase("true")) {
                        a.setOptional(true);
                    }
                }
            }
        }
        return a;
    }

    private static ArrayList<Condition> readConditions(NodeList conditionList) {
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        for (int i = 0; i < conditionList.getLength(); i++) {
            Condition c = readCondition((Element) conditionList.item(i));
            if (c != null) {
                conditions.add(c);
            }
        }
        return conditions;
    }

    private static Condition readCondition(Element conditionElement) {
        NodeList attributeList = conditionElement.getElementsByTagName("Attribute");
        Condition c = null;
        String conditionValue = null;
        for (int i = 0; i < attributeList.getLength(); i++) {
            Element attributeElement = (Element) attributeList.item(i);
            String attributeId = attributeElement.getAttribute("AttributeId");
            if (attributeId.compareTo("urn:oasis:names:tc:xacml:1.0:action:condition-id") == 0) {
                String dataType = attributeElement.getAttribute("DataType");
                if (dataType.compareToIgnoreCase(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants.class.getName()) == 0
                        || dataType.compareToIgnoreCase("org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants") == 0) {
                    NodeList attributeValueList = attributeElement.getElementsByTagName("AttributeValue");
                    Element attributeValueElement = (Element) attributeValueList.item(0);
                    ConditionConstants cc = ConditionConstants.valueOf(attributeValueElement.getAttribute("DataType"));

                    conditionValue = attributeValueElement.getFirstChild().getNodeValue();
                    c = new Condition(cc, conditionValue);
                }
            }
        }
        if (c != null) {
            NodeList optionalNodeList = conditionElement.getElementsByTagName("optional");
            //JOptionPane.showMessageDialog(null, "in Condition: "+optionalNodeList.getLength());
            if (optionalNodeList != null) {
                if (optionalNodeList.getLength() > 0) {
                    Element valueOptional = (Element) optionalNodeList.item(0);
                    String value = valueOptional.getFirstChild().getNodeValue();
                    if (value.equalsIgnoreCase("false")) {
                        c.setOptional(false);
                    }
                }
            }
        }
        return c;
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            String filePath = "";

            try {
                filePath = args[0];
                XMLPolicyReader reader = new XMLPolicyReader(null, null);
                reader.read(filePath);
                //JOptionPane.showMessageDialog(null, "Read "+filePath);
            } catch (Exception e) {
                //JOptionPane.showMessageDialog(null, "Failed: "+filePath);
                log.error("", e);
            }
        }


    }

    private void read(String filepath) {
        log.debug(filepath);
        File file = new File(filepath);

        RequestPolicy policy = readPolicyFromFile(file);
        log.debug("Policy read OK: \n");
        log.debug(policy.toString());
    }


}
