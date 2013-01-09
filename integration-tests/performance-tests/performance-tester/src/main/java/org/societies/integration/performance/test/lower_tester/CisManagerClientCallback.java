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
package org.societies.integration.performance.test.lower_tester;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.schema.cis.community.CommunityMethods;

import com.restfb.json.JsonObject;

/**
 *
 * @author Rafik
 *
 */
public class CisManagerClientCallback implements ICisManagerCallback{

	private static Logger LOG = LoggerFactory.getLogger(CisManagerClientCallback.class);
	private String communityJid = null;
	private Calendar calendar;
	private long startTestDate = 0;
	private long endTestDate = 0;	
	private JsonObject jsonObject;
	private String cssId = null;

	@Override
	public void receiveResult(CommunityMethods communityResultObject) 
	{
		calendar = Calendar.getInstance();
		endTestDate = calendar.getTime().getTime();
		
		LOG.info("### [CIS Lower Tester] receiveResult callback: ");
		
		//TODO call a rest web service to inform the wamp serveur about the result.
		communityJid = communityResultObject.getJoinResponse().getCommunity().getCommunityJid();
		
		if (communityJid != null && !communityJid.equals("")) 
		{
			LOG.info("### [CIS Lower Tester] join successfully done");
			
			jsonObject = new JsonObject();
			
			jsonObject.put("state", "success");
			jsonObject.put("message", "Join CIS has been done successfully");
			jsonObject.put("startTestDate", startTestDate);
			jsonObject.put("endTestDate", endTestDate);
			jsonObject.put("nodeId", cssId);
			jsonObject.put("cisId", communityJid);
			
			LOG.info("### [CIS Lower Tester] result: " + jsonObject.toString());
		}
		else
		{
			jsonObject = new JsonObject();
			
			jsonObject.put("state", "failed");
			jsonObject.put("message", "Join CIS failed");
			jsonObject.put("startTestDate", startTestDate);
			jsonObject.put("endTestDate", endTestDate);
			jsonObject.put("nodeId", cssId);
			jsonObject.put("cisId", communityJid);
			
			LOG.info("### [CIS Lower Tester] result: " + jsonObject.toString());
		}
	}
	
	public void setCssId(String cssId) {
		this.cssId = cssId;
	}
	
	public void setStartTestDate(long startTestDate) {
		this.startTestDate = startTestDate;
	}

}
