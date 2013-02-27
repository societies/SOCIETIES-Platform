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

/**
 * 
 * Util class to define and compare bit flags for filter and ranking properties.
 *
 * @author David McKitterick
 */
public class BitCompareUtil {

	public static int FACEBOOK_BIT 		= 0x0000000001;
	public static int TWITTER_BIT 		= 0x0000000010;
	public static int LINKEDIN_BIT 		= 0x0000000100;
	public static int FOURSQUARE_BIT 	= 0x0000001000;
	public static int GOOGLEPLUS_BIT 	= 0x0000010000;
	public static int CIS_MEMBERS_BIT 	= 0x0000100000;
	
	public static boolean isFacebookFlagged(int flag) {
		  return (flag & FACEBOOK_BIT) == FACEBOOK_BIT; 
	}
	
	public static boolean isTwitterFlagged(int flag) {
		  return (flag & TWITTER_BIT) == TWITTER_BIT; 
	}
	
	public static boolean isLinkedinFlagged(int flag) {
		  return (flag & LINKEDIN_BIT) == LINKEDIN_BIT; 
	}
	
	public static boolean isFoursquareFlagged(int flag) {
		  return (flag & FOURSQUARE_BIT) == FOURSQUARE_BIT; 
	}
	
	public static boolean isGooglePlusFlagged(int flag) {
		  return (flag & GOOGLEPLUS_BIT) == GOOGLEPLUS_BIT; 
	}
	
	public static boolean isCisMembersFlagged(int flag) {
		  return (flag & CIS_MEMBERS_BIT) == CIS_MEMBERS_BIT; 
	}
	
}
