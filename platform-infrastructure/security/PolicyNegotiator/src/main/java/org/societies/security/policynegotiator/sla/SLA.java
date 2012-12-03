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
package org.societies.security.policynegotiator.sla;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.security.policynegotiator.xml.Xml;
import org.w3c.dom.NodeList;

/**
 * Various XPath expressions, ... in Service License Agreement (SLA) or
 * Service Operation Policy (SOP).
 * 
 * @author Mitja Vardjan
 */
public class SLA {

	private static Logger Log = LoggerFactory.getLogger(SLA.class);
	
	private Xml xml;

	/**
	 * Constructor
	 * 
	 * @param xml XML source for SLA. Must not be null.
	 */
	public SLA(Xml xml) {
		this.xml = xml;
		if (xml == null) {
			Log.error("Xml parameter is null");
		}
	}

	public static class XPath {
		public static final String SOP = "/societies/serviceOperationPolicy/sop";
		public static final String PROVIDER = "SLA/Parties/ServiceProvider";
		public static final String SLA_ROOT = "SLA";
		//public static final String RESPONSE_TIME = "SLA/Obligations/ServiceLevelObjective/Expression/Predicate/Value";
	}
	
	public static class Attribute {

		/**
		 * The Id attribute of XML node that contains all SOPs
		 */
		public static final String SOPS_ID_VALUE = "Container";
		public static final String SOP_ID = "Id";
		public static final String NAME = "name";
	}
	
	/**
	 * @return Array of Strings.
	 * If there are no valid SOPs, then Array of length 0 is returned, never null
	 */
    public String[] getSopNames() {
    	
        String[] sopOptionEntries;
        NodeList nodes = xml.getNodes(SLA.XPath.SOP);
        
        sopOptionEntries = new String[nodes.getLength()];
        for (int k = 0; k < sopOptionEntries.length; k++) {
        	sopOptionEntries[k] = Xml.getAttribute(nodes.item(k), SLA.Attribute.SOP_ID);
        	Log.debug("getSopNames(): added SOP option " + sopOptionEntries[k]);
        }
        return sopOptionEntries;
    }
	
    public String getSopContent(String sopName) {
    	
        String xpath = SLA.XPath.SOP + "[@" + SLA.Attribute.SOP_ID + "=\"" + sopName + "\"]";
        NodeList nodes = xml.getNodes(xpath);
        
        if (nodes == null || nodes.getLength() < 1) {
        	Log.warn("getSopContent(): no XML nodes found with XPath " + xpath);
        	return null;
        }
        if (nodes.getLength() > 1) {
        	Log.warn("getSopContent(): Multiple XML nodes found with XPath " + xpath);
        }
        return nodes.item(0).getTextContent();
    }
    
    public String getProviderName(String sopOption) {
    	String xpath = XPath.SOP + "[@" + SLA.Attribute.SOP_ID + "=\"" + sopOption + "\"]" +
    			"/" + XPath.PROVIDER;
    	return xml.getAttributeValue(xpath, Attribute.NAME);
    }
    
    /*
    public double getResponseTime(String sopOption) {
    	String xpath = XPath.SOP + "[@" + SLA.Attribute.SOP_ID + "=\"" + sopOption + "\"]" +
				"/" + XPath.RESPONSE_TIME;
    	return Double.parseDouble(xml.getValue(xpath));
    }
    */
    
    public String generateSla(String sopName) {
    	
        String xpath = SLA.XPath.SOP + "[@" + SLA.Attribute.SOP_ID + "!=\"" + sopName + "\"]";
        xml.removeNodes(xpath);
        return xml.toString();
    }
    
    @Override
    public String toString() {
    	return xml.toString();
    }
}
