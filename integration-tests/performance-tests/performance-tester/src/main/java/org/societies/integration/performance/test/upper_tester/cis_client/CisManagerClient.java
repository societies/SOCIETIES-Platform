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
package org.societies.integration.performance.test.upper_tester.cis_client;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.integration.performance.test.lower_tester.CisManagerClientCallback;


/**
 * @author Rafik
 *
 */
public class CisManagerClient{

	private static ICisManager cisManager;
	private static CisManagerClientCallback cisManagerClientCallback;
	private static Calendar calendar;
	
	private static Logger LOG = LoggerFactory.getLogger(CisManagerClient.class);
	
	public CisManagerClient() 
	{	
		cisManagerClientCallback = new CisManagerClientCallback();
	}
		
	//CIS Manager service Injection
	public void setCisManager(ICisManager cisManager) {
		if (null != cisManager) 
		{
			LOG.info("############ cisManager injected");
			this.cisManager = cisManager;
		}
	}
	
	public static ICisManager getCisManager(){
		
		if (null != cisManager) {
			LOG.info("############ this.cisManager non null");
			return cisManager;
		}
		else {
			LOG.info("############ this.cisManager null");
			return null;
		}
	}
	
	public static String joinCis (String cssOwnerId, String cisId, String cssId)
	{
		LOG.info("cssOwnerId: " + cssOwnerId + "  cisId: "+ cisId);
		
		if (null != cssOwnerId && null != cisId && !"".equals(cssOwnerId) && !"".equals(cisId)) 
		{
			CisAdvertisementRecord adv = new CisAdvertisementRecord();
			
			adv.setCssownerid(cssOwnerId);
			adv.setId(cisId);
			
			if(null != getCisManager()) 
			{
				LOG.info("CIS Manager not null");
				
				calendar = Calendar.getInstance();
				cisManagerClientCallback.setStartTestDate(calendar.getTime().getTime());
				cisManagerClientCallback.setCssId(cssId);
				
				getCisManager().joinRemoteCIS(adv, cisManagerClientCallback);
				
				LOG.info("return pending");
				
				return "pending";
			}
			LOG.info("CIS Manager null");
			return null;	
		}
		LOG.info("cssOwnerId=null OR cisId=null");
		return null;
	}
}
