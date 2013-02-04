/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM ISRAEL
 * SCIENCE AND TECHNOLOGY LTD (IBM), INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA
 * PERIORISMENIS EFTHINIS (AMITEC), TELECOM ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD
 * (NEC))
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
package org.societies.android.api.comms.xmpp;

import java.util.List;


// TODO change this to interface that Identity and Node implement
/**
 * The Class XMPPInfo.
 */
public class XMPPInfo {
	
	/** The Constant INFO_NAMESPACE. */
	public static final String INFO_NAMESPACE = "http://jabber.org/protocol/disco#info";

	/** The identity category. */
	private String identityCategory;
	
	/** The identity type. */
	private String identityType;
	
	/** The identity name. */
	private String identityName;
	
	/** The feature namespaces. */
	private List<String> featureNamespaces;
	
	/**
	 * Instantiates a new XMPP info.
	 *
	 * @param identityCategory the identity category
	 * @param identityType the identity type
	 * @param identityName the identity name
	 * @param featureNamespaces the feature namespaces
	 */
	public XMPPInfo(String identityCategory, String identityType,
			String identityName, List<String> featureNamespaces) {
		this.identityCategory = identityCategory;
		this.identityType = identityType;
		this.identityName = identityName;
		this.featureNamespaces = featureNamespaces;
	}


	/**
	 * Gets the identity category.
	 *
	 * @return the identity category
	 */
	public String getIdentityCategory() {
		return identityCategory;
	}

	/**
	 * Gets the identity type.
	 *
	 * @return the identity type
	 */
	public String getIdentityType() {
		return identityType;
	}

	/**
	 * Gets the identity name.
	 *
	 * @return the identity name
	 */
	public String getIdentityName() {
		return identityName;
	}

	/**
	 * Gets the feature namespaces.
	 *
	 * @return the feature namespaces
	 */
	public List<String> getFeatureNamespaces() {
		return featureNamespaces;
	}
}
