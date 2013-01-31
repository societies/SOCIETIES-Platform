/**
 * Copyright (c) 2011-2013, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
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
package org.societies.api.css;

public class FlagUtility {

	public static int FILTER_FACEBOOK 		= 0x0000000001;
	public static int FILTER_TWITTER 		= 0x0000000010;
	public static int FILTER_LINKEDIN 		= 0x0000000100;
	public static int FILTER_FOURSQUARE 	= 0x0000001000;
	public static int FILTER_GOOGLEPLUS 	= 0x0000010000;
	public static int FILTER_CIS_MEMBERS 	= 0x0000100000;
	
	public static boolean isFacebookFlagged(int flag) {
		  return (flag & FILTER_FACEBOOK) == FILTER_FACEBOOK; 
	}
	
	public static boolean isTwitterFlagged(int flag) {
		  return (flag & FILTER_TWITTER) == FILTER_TWITTER; 
	}
	
	public static boolean isLinkedinFlagged(int flag) {
		  return (flag & FILTER_LINKEDIN) == FILTER_LINKEDIN; 
	}
	
	public static boolean isFoursquareFlagged(int flag) {
		  return (flag & FILTER_FOURSQUARE) == FILTER_FOURSQUARE; 
	}
	
	public static boolean isGooglePlusFlagged(int flag) {
		  return (flag & FILTER_GOOGLEPLUS) == FILTER_GOOGLEPLUS; 
	}
	
	public static boolean isCisMembersFlagged(int flag) {
		  return (flag & FILTER_CIS_MEMBERS) == FILTER_CIS_MEMBERS; 
	}
	
}
