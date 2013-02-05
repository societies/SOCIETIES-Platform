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
package org.societies.personalisation.UserPreferenceManagement.impl.merging;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cis.community.Community;

/**
 * @author Eliza
 *
 */
public class CommunityMergingManager extends EventListener{

	private IEventMgr eventMgr;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private IUserFeedback userFeedbackManager;
	private List<Community> communities; 
	public CommunityMergingManager(){
		
	}
	
	public void initialiseCommunityMergingManager(){
		this.registerForCISEvents();
	}
	
	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleInternalEvent(InternalEvent event) {
		if (event.geteventType().equals(EventTypes.CIS_SUBS)){
			//ask user if they want to upload anonymous data
			Community community = (Community) event.geteventInfo();
			try {
				String yes = "Yes";
				String no = "No";
				List<String> reply = this.userFeedbackManager.getExplicitFB(ExpProposalType.ACKNACK, new ExpProposalContent("Do you want to upload anonymous preference information to the CIS "+community.getCommunityName()+"? (Recommended)", new String[]{yes, no})).get();
				if (reply.contains(yes)){
					this.communities.add(community);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else if (event.geteventType().equals(EventTypes.CIS_UNSUBS)){
			Community community = (Community) event.geteventInfo();
			if(removeCommunity(community)){
				this.userFeedbackManager.showNotification("Your preferences will no longer be uploaded to CIS: "+community.getCommunityName());
			}else{
				this.logging.debug("CIS: "+community.getCommunityName()+" not in the list of CISs to which data is uploaded");

			}
		}
		
	}

	private void registerForCISEvents() {
		String evtTypes[] = new String[]{EventTypes.CIS_SUBS, EventTypes.CIS_UNSUBS};
		this.eventMgr.subscribeInternalEvent(this, evtTypes, null);
		this.logging.debug("Subscribed to "+EventTypes.CIS_SUBS+" events");

	}

	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}

	public void setUserFeedbackManager(IUserFeedback userFeedbackManager) {
		this.userFeedbackManager = userFeedbackManager;
	}
	
	private boolean removeCommunity(Community cis){
		boolean found = false;
		for (Community community : communities){
			if (cis.getCommunityJid().equals(community.getCommunityJid())){
				communities.remove(community);
				found = true;
				this.logging.debug("Removed "+cis.getCommunityName()+" from the list of CISs to which data is uploaded.");
				break;
			}
		}
		
		return found;
	}
}
