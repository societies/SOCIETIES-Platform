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

package org.societies.orchestration.CommunityLifecycleManagement.test;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisRecord;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.orchestration.CommunityLifecycleManagement.impl.CommunityRecommender;
import org.societies.orchestration.CommunityLifecycleManagement.impl.SuggestedCommunityAnalyser;


/**
 * This is the test class for the Suggested Community Analyser component
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class SuggestedCommunityAnalyserTest {
	
	private SuggestedCommunityAnalyser suggestedCommunityAnalyser;
	private ICisManager cisManager;
	private ICtxBroker userCtxBroker;
	private CommunityRecommender communityRecommender;
	
	@Test
    public void testProcessEgocentricRecommendations() {

		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		communityRecommender = mock(CommunityRecommender.class);
		
		
		
		//create CIS for James where James himself has been inactive for 1 year.
	    
		//CisRecord jamesCisRecord = cisManager.createCis("James", "James CIS");
		
    	suggestedCommunityAnalyser = new SuggestedCommunityAnalyser(ownerId, "CSS");
    	suggestedCommunityAnalyser.setCommunityRecommender(communityRecommender);
    	suggestedCommunityAnalyser.setUserContextBroker(userCtxBroker);
    	suggestedCommunityAnalyser.setCisManager(cisManager);
    	
    	HashMap<String, ArrayList<ICisRecord>> recommendations = new HashMap<String, ArrayList<ICisRecord>>();
		suggestedCommunityAnalyser.processEgocentricRecommendations(recommendations, new ArrayList<String>());
		
		//James should have been suggested to leave the CIS.
		// (No members list function in CisRecord API yet)
		
		//Assert.assertNull(cisManager.getCis("James", "James CIS").membersCss[0].equals("James"));
		
	}
	
	@Test
    public void testProcessCSCWRecommendations() {

		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		communityRecommender = mock(CommunityRecommender.class);
		
		//create CIS for James where James himself has been inactive for 1 year.
	    
		//CisRecord jamesCisRecord = cisManager.createCis("James", "James CIS");
		
    	suggestedCommunityAnalyser = new SuggestedCommunityAnalyser(ownerId, "CSS");
    	suggestedCommunityAnalyser.setCommunityRecommender(communityRecommender);
    	suggestedCommunityAnalyser.setUserContextBroker(userCtxBroker);
    	suggestedCommunityAnalyser.setCisManager(cisManager);
		HashMap<String, ArrayList<ICisRecord>> recommendations = new HashMap<String, ArrayList<ICisRecord>>();
		suggestedCommunityAnalyser.processEgocentricRecommendations(recommendations, new ArrayList<String>());
		
		//James should have been suggested to leave the CIS.
		// (No members list function in CisRecord API yet)
		
		//Assert.assertNull(cisManager.getCis("James", "James CIS").membersCss[0].equals("James"));
		
	}
	
	@Test
    public void testProcessCSMAnalyserRecommendations() {

		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		communityRecommender = mock(CommunityRecommender.class);
		
		//create CIS for James where James himself has been inactive for 1 year.
	    
		//CisRecord jamesCisRecord = cisManager.createCis("James", "James CIS");
		
    	suggestedCommunityAnalyser = new SuggestedCommunityAnalyser(ownerId, "CSS");
    	suggestedCommunityAnalyser.setCommunityRecommender(communityRecommender);
    	suggestedCommunityAnalyser.setUserContextBroker(userCtxBroker);
    	suggestedCommunityAnalyser.setCisManager(cisManager);
		HashMap<String, ArrayList<ICisRecord>> recommendations = new HashMap<String, ArrayList<ICisRecord>>();
		suggestedCommunityAnalyser.processEgocentricRecommendations(recommendations, new ArrayList<String>());
		
		//James should have been suggested to leave the CIS.
		// (No members list function in CisRecord API yet)
		
		//Assert.assertNull(cisManager.getCis("James", "James CIS").membersCss[0].equals("James"));
		
	}
    
    public void setCisManager(ICisManager cisManager){
		this.cisManager = cisManager;
	}
	
}
