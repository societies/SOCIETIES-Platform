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

package org.societies.orchestration.CisRecommender.impl;

import org.societies.css.cssdirectory.api.ICssDirectoryCloud;
import org.societies.css.cssdirectory.api.ICssDirectoryRich;
import org.societies.css.cssdirectory.api.ICssDirectoryLight;

import org.societies.cssmgmt.cssdiscovery.api.ICssDiscovery;

import org.societies.cis.management.api.CisAcitivityFeed;
import org.societies.cis.management.api.ServiceSharingRecord;
import org.societies.cis.management.api.CisActivity;
import org.societies.cis.management.api.CisRecord;

import org.societies.context.user.similarity.api.platform.IUserCtxSimilarityEvaluator;

import org.societies.context.user.prediction.api.platform.IUserCtxPredictionMgr;

import org.societies.context.user.db.api.platform.IUserCtxDBMgr;

import org.societies.context.user.history.api.platform.IUserCtxHistoryMgr;

import org.societies.api.mock.EntityIdentifier;

import org.societies.api.mock.ServiceResourceIdentifier;

import org.societies.orchestration.api.ICommunityLifecycleManagement;

/**
 * The generic CIS Recommender, which acts as a 'gateway' to components for
 * specific recommendation types, and triggers them to act. It performs initial checks 
 * for recommendations for the various functionalities of Intelligent Community Orchestration,
 * according to the kind of recommender involved, and alerts the 
 * appropriate components for further analysis.
 * 
 */

public class CisRecommender {
	
	//private Css linkedCss; // No datatype yet defined for CSS
	private EntityIdentifier linkedCssId;
	
	private ServiceResourceIdentifier linkedServiceId;
	
	private ICommunityLifecycleManagement communityLifecycleManagement;
	//intelligent community membership engine
	//collaboration pattern analyser
	//etc
	
	/*
     * Constructor for CisRecommender
     * 
	 * Description: The constructor creates the CisRecommender
	 *              component on a given CSS or on behalf of a service.
	 * Parameters: 
	 * 				linkedCssId - the CSS that this object will operate on behalf of.
	 */
	
	public CisRecommender(EntityIdentifier linkedCssId) {
		this.linkedCssId = linkedCssId;
	}
	
	/*
     * Constructor for CisRecommender 
     * 
	 * Description: The constructor creates the CisRecommender
	 *              component on behalf of a service.
	 * Parameters: 
	 * 				linkedServiceId - the service on behalf of which this object is to operate.
	 */
	
	public CisRecommender(ServiceResourceIdentifier linkedServiceId) {
		this.linkedServidId = linkedServiceId;
	}
	
	/**
	 * COMMUNITY LIFECYCLE MANAGEMENT
	 * At both short and long regular time intervals, call Community Lifecycle Management 
	 * with non-extensive or extensive check request, optionally forwarding some input
	 * as a base.
	 */
	
	public void communityLifecycleManagementCaller() {
		
		new ShortSleepThread().start();
        new LongSleepThread().start();
		
	}
	
	class ShortSleepThread extends Thread {
		
		public void run() {
			while (true) {
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				communityLifecycleManagement.processPreviousShortTimeCycle();
		    }
		}
	}
	
    class LongSleepThread extends Thread {
		
		public void run() {
			while (true) {
				try {
					Thread.sleep(86400000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				communityLifecycleManagement.processPreviousLongTimeCycle();
		    }
		}
	}
		
	/**
	 * COLLABORATION PATTERN ANALYZER
	 * ??
	 */
    
     public void collaborationPatternAnalyserCaller() {
		
		new ShortSleepThread().start();
        new LongSleepThread().start();
		
	}
		
	/**
	 * INTELLIGENT COMMUNITY MEMBERSHIP ENGINE
	 * ??
	 */

    public void intelligentCommunityMembershipEngineCaller() {
	
    }
		
	/**
	 * ...
	 */
}