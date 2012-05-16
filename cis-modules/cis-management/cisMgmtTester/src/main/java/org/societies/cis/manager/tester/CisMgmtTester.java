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

package org.societies.cis.manager.tester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.IcisManagerClient;
import org.societies.api.internal.css.management.ICSSManagerCallback;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author Thomas Vilarinho (Sintef)
*/


public class CisMgmtTester {

	private IcisManagerClient cisClient;
	
	private static Logger LOG = LoggerFactory
			.getLogger(CisMgmtTester.class);
	
	private String targetCisId = null;
	
	
	public CisMgmtTester(IcisManagerClient cisClient, String targetCisId){
		LOG.info("starting CIS MGMT tester");
		this.cisClient = cisClient;
		LOG.info("got autowired reference, target cisId is " + targetCisId);

		ICisManagerCallback icall = new ICisManagerCallback()
		 {
			public void receiveResult(boolean result){
				LOG.info("boolean return on CIS Mgmgt tester");
				LOG.info("Result Status: " + result);

			}; 

			public void receiveResult(int result) {};
			
			public void receiveResult(String result){}

			public void receiveResult(Community communityResultObject) {
				if(communityResultObject == null){
					LOG.info("boolean return on CIS Mgmgt tester");
				}
				else{
					LOG.info("boolean return on CIS Mgmgt tester");
					LOG.info("Result Status: joined CIS " + communityResultObject.getCommunityJid());					
				}
				
				
			};
			
		 };
		
		 LOG.info("created callback");
	
	
		this.cisClient.joinRemoteCIS(targetCisId, icall);

		LOG.info("join sent");
	
	}

	
}
