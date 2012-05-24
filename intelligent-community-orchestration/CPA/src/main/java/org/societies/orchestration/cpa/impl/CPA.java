/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp., 
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

package org.societies.orchestration.cpa.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


//import org.societies.api.internal.cis.management.ICisManager;
//import org.societies.api.cis.management.ICisManager;
//import org.societies.api.cis.management.ICis;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
//import org.societies.api.internal.cis.management.ICis;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.css.devicemgmt.devicemanager.IDeviceManager;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryCallback;

import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
//import org.societies.api.cis.management.ICis;

/**import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisEditor;*/

import org.societies.orchestration.api.ICis;
import org.societies.orchestration.api.ICisManager;
import org.societies.orchestration.api.ICisOwned;
import org.societies.orchestration.api.ICisParticipant;
import org.societies.orchestration.api.ICisProposal;
//import org.societies.orchestration.api.ICisEditor;

//import org.societies.api.internal.context.broker.ICommunityCtxBroker;

import org.societies.api.identity.IIdentity;
//import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.identity.IIdentityManager;

//import org.societies.api.comm.xmpp.datatypes.Identity;
//import org.societies.comm.examples.commsmanager.impl.CommsServer;
//import org.societies.comm.xmpp.interfaces.ICommCallback;

/**
 * This is the class for the Egocentric Community Analyser component
 * 
 * Driver code for the CPA process of analysing CIS activity, 
 * and trigger suggestions for new CISes if applicable.
 * 
 * @author Bjørn Magnus Mathisen, based on the work by Fraser Blackmun
 * @version 0
 * 
 */

public class CPA
{
	
	private CPACreationPatterns egocentricCreationManager;
	
	private Date lastTemporaryCheck;
	private Date lastOngoingCheck;
	private List<ICis> currentCises;
	/*
     * Constructor for EgocentricCommunityAnalyser
     * 
	 * Description: The constructor creates the EgocentricCommunityAnalyser
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedEntity - the non-CIS entity, either a user CSS or a domain deployment,
	 *              that this object will operate on behalf of. (Currently can only be a user CSS)
	 */
	
	public CPA(IIdentity linkedEntity, String linkType) {
		
		lastTemporaryCheck = new Date();
		lastOngoingCheck = new Date();
		
		//else
		//	this.linkedDomain = linkedEntity;
	}
	private void sendToCSM(List<ICisProposal> list){
	}
	private void process() {
		
		sendToCSM(egocentricCreationManager.analyze(currentCises));

	}
	
	
	public void loop() {
		
		new SleepThread().start();
		
	}
	
	class SleepThread extends Thread {
		
		public void run() {
			while (true) {
				try {
					Date date = new Date();
					if (date.getTime() >= (lastTemporaryCheck.getTime() + (1000 * 180))) {
						process();
						lastTemporaryCheck.setTime(date.getTime());
					}
					Thread.sleep(100);
				} catch (InterruptedException e) {
					//TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		    }
		}
	}
	
    
    public void initialiseEgocentricCommunityAnalyser() {
    	loop();
    }
    
    public CPACreationPatterns getEgocentricCreationManager() {
    	return egocentricCreationManager;
    }
    
    public void setEgocentricCreationManager(CPACreationPatterns egocentricCreationManager) {
    	this.egocentricCreationManager = egocentricCreationManager;
    }
    
    
    
}