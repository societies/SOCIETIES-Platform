/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.api.internal.cssmanager;

/**
 * Contains CSS Manager Enum classes
 *  and PubSub nodes
 *
 */
public class CSSManagerEnums {
	//CSS Manager Eventing (a.k.a Pubsub) nodes
    public static final String ADD_CSS_NODE = "addCSSNode";
    public static final String ADD_CSS_NODE_DESC = "Additional node available on CSS";

    public static final String DEPART_CSS_NODE = "departCSSNode";
    public static final String DEPART_CSS_NODE_DESC = "Existing node no longer available on CSS";

	/**
	 * Enum for device status types
	 */
	public enum nodeStatus {
		
		Available("Available for Use"), 
		Unavailable("Unavailable"), 
		Hibernating("Not active but on alert");
		
	    private String name;

		nodeStatus(String name) {
	        this.name = name;
	    }

	    public String getName() {
	        return this.name;
	    }
	
	};
	/**
	 * Enum for device node types
	 */
	public enum nodeType {
		Android("Android based client"), 
		Cloud("Cloud Node"), 
		Rich("JVM based client");
		
		
	    private String name;

		nodeType(String name) {
	        this.name = name;
	    }

	    public String getName() {
	        return this.name;
	    }
	};

	/**
	 * Enum for CSS status
	 */
	public enum cssStatus {
		Active("Active"), 
		Inactive("Inactive");
	
	    private String name;

	    cssStatus(String name) {
	        this.name = name;
	    }

	    public String getName() {
	        return this.name;
	    }

	};
	/**
	 * Enum for entity types
	 */
	public enum entityType {
		Person("Personal CSS"), 
		Organisation("Organisational CSS");
		
		
	    private String name;

	    entityType(String name) {
	        this.name = name;
	    }

	    public String getName() {
	        return this.name;
	    }

	};
	/**
	 * Enum for gender types
	 */
	public enum genderType {
		Male("Male"), 
		Female("Female"), 
		Unspecified("Unspecified");
		
	    private String name;

	    genderType(String name) {
	        this.name = name;
	    }

	    public String getName() {
	        return this.name;
	    }
	
	};
	/**
	 * Enum for presence types
	 */
	public enum presenceType {
		Available("Available"), 
		DoNotDisturb("Do not disturb"), 
		Offline("Offline"), 
		Away("Away"), 
		ExtendedAway("Extended Away");
		
	    private String name;

	    presenceType(String name) {
	        this.name = name;
	    }

	    public String getName() {
	        return this.name;
	    }
	
	};

	
}
