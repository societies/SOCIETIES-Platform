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
package org.societies.webapp.controller;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Describe your class here...
 *
 * @author tcarlyle
 */

@Controller
public class CisManagerController {

	
	@Autowired
	private ICisManager cisManager;
	
	CommunityMethods callbackComMethRed = null;
	/**
	 *http://localhost:8080/societies/your_communities_list.html
	 */
	
	
	// COMMUNITY PROFILE CALLBACK
	
	commCallBack icall = new commCallBack();
	class commCallBack implements ICisManagerCallback{
			
		public commCallBack(){
		}
		@Override
		public void receiveResult(CommunityMethods communityResultObject) {
			callbackComMethRed = communityResultObject;
		}

	}	
	
	@RequestMapping(value="/your_communities_list.html",method = RequestMethod.GET)
	public ModelAndView yourCommunitiesListPage() {
		//model is nothing but a standard Map object
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please login to your Societies account");

		List<ICis> records = this.getCisManager().getCisList();
		model.put("cisrecords", records);
		
		return new ModelAndView("your_communities_list", model) ;
	}
	
	@RequestMapping(value="/community_profile.html",method = RequestMethod.GET)
	public ModelAndView communityPofilePage(@RequestParam(value="cisId", required=true) String cisId){
		Map<String, Object> model = new HashMap<String, Object>();
		
		ICis icis = this.getCisManager().getCis(cisId);
		
		icis.getInfo(icall);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null == callbackComMethRed){
			model.put("message", "timeout");
		}else{
			
		}
		
		return new ModelAndView("community_profile", model) ;
	}

	
	
	
	
	// AUTOWIRING GETTERS AND SETTERS
	public ICisManager getCisManager() {
		return cisManager;
	}
	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}
}
