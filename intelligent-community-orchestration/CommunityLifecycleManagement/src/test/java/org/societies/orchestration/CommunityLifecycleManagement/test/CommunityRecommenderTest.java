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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Future;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**import org.societies.css.cssdirectory.api.ICssDirectoryCloud;
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
*/

import org.societies.orchestration.CommunityLifecycleManagement.impl.CommunityRecommender;
//import org.societies.orchestration.EgocentricCommunityAnalyser.test.ownerId;

import org.societies.api.identity.IIdentity;
//import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;

//import org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier;

import org.societies.api.internal.context.broker.ICtxBroker;
//import org.societies.api.internal.context.broker.IUserCtxDBMgr;
//import org.societies.api.internal.context.broker.ICtxBrokerCallback;
//import org.societies.api.internal.context.user.db.IUserCtxDBMgr;
//import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;
//import org.societies.api.mock.EntityIdentifier;
//import org.societies.api.internal.context.broker.IUserCtxDBMgrCallback;

import org.societies.api.context.model.CtxAttributeValueType;
/**import org.societies.api.cis.management.ICisRecord;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisEditor;
*/

import org.societies.api.cis.management.ICisSubscribed;

import org.societies.orchestration.api.ICisRecord;
import org.societies.orchestration.api.ICisManager;
import org.societies.orchestration.api.ICisOwned;
import org.societies.orchestration.api.ICisEditor;

import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
//import org.societies.api.cis.management.ICis;



/**
 * This is the test class for the Community Recommender component
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class CommunityRecommenderTest {
	
	private CommunityRecommender communityRecommender;
	private ICtxBroker userCtxBroker;
	//private IUserCtxDBMgr userCtxDBMgr;
	private CtxEntityIdentifier entityId;
	//private IUserCtxBrokerCallback userCtxBrokerCallback;
	private ICisManager cisManager;
	
	@Test
	public void testEgocentricCreationSuggestion() {
		
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		
		ArrayList<String> metadata = new ArrayList<String>();
		
    	communityRecommender = new CommunityRecommender(ownerId, "CSS");
		
    	ICisRecord theNewCis = mock(ICisRecord.class);
    	HashMap<String, ArrayList<ArrayList<ICisRecord>>> suggestions = new HashMap<String, ArrayList<ArrayList<ICisRecord>>>();
		ArrayList<ArrayList<ICisRecord>> theSuggestions = new ArrayList<ArrayList<ICisRecord>>();
		ArrayList<ICisRecord> oneSuggestion = new ArrayList<ICisRecord>();
		theSuggestions.add(oneSuggestion);
    	suggestions.put("Create CISs", theSuggestions);
    	communityRecommender.identifyCisActionForEgocentricCommunityAnalyser(suggestions, new ArrayList<String>());
    	
		//Assert.assertNull(cisManager.getCisList(new ICisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	@Test
	public void testCSCWSuggestion() {
		
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		
		
    	communityRecommender = new CommunityRecommender(ownerId, "CSS");
		
    	ICisRecord theNewCis = mock(ICisRecord.class);
    	HashMap<String, ArrayList<ArrayList<ICisRecord>>> suggestions = new HashMap<String, ArrayList<ArrayList<ICisRecord>>>();
		ArrayList<ArrayList<ICisRecord>> theSuggestions = new ArrayList<ArrayList<ICisRecord>>();
		ArrayList<ICisRecord> oneSuggestion = new ArrayList<ICisRecord>();
		theSuggestions.add(oneSuggestion);
    	suggestions.put("Create CISs", theSuggestions);
    	communityRecommender.identifyCisActionForEgocentricCommunityAnalyser(suggestions, new ArrayList<String>());
    	
		//Assert.assertNull(cisManager.getCisList(new ICisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	@Test
	public void testCSMAnalyserCreationSuggestion() {
		
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		
		
    	communityRecommender = new CommunityRecommender(ownerId, "CSS");
		
    	ICisRecord theNewCis = mock(ICisRecord.class);
    	HashMap<String, ArrayList<ArrayList<ICisRecord>>> suggestions = new HashMap<String, ArrayList<ArrayList<ICisRecord>>>();
		ArrayList<ArrayList<ICisRecord>> theSuggestions = new ArrayList<ArrayList<ICisRecord>>();
		ArrayList<ICisRecord> oneSuggestion = new ArrayList<ICisRecord>();
		theSuggestions.add(oneSuggestion);
    	suggestions.put("Create CISs", theSuggestions);
    	communityRecommender.identifyCisActionForEgocentricCommunityAnalyser(suggestions, new ArrayList<String>());
    	
		//Assert.assertNull(cisManager.getCisList(new ICisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	@Test
	public void testEgocentricConfigurationSuggestion() {
		
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		
		
    	communityRecommender = new CommunityRecommender(ownerId, "CSS");
		
    	ICisRecord theNewCis = mock(ICisRecord.class);
    	HashMap<String, ArrayList<ArrayList<ICisRecord>>> suggestions = new HashMap<String, ArrayList<ArrayList<ICisRecord>>>();
		ArrayList<ArrayList<ICisRecord>> theSuggestions = new ArrayList<ArrayList<ICisRecord>>();
		ArrayList<ICisRecord> oneSuggestion = new ArrayList<ICisRecord>();
		theSuggestions.add(oneSuggestion);
    	suggestions.put("Create CISs", theSuggestions);
    	communityRecommender.identifyCisActionForEgocentricCommunityAnalyser(suggestions, new ArrayList<String>());
    	
		//Assert.assertNull(cisManager.getCisList(new ICisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	@Test
	public void testEgocentricMergeSuggestion() {
		
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		
		
    	communityRecommender = new CommunityRecommender(ownerId, "CSS");
		
    	ICisRecord theNewCis = mock(ICisRecord.class);
    	HashMap<String, ArrayList<ArrayList<ICisRecord>>> suggestions = new HashMap<String, ArrayList<ArrayList<ICisRecord>>>();
		ArrayList<ArrayList<ICisRecord>> theSuggestions = new ArrayList<ArrayList<ICisRecord>>();
		ArrayList<ICisRecord> oneSuggestion = new ArrayList<ICisRecord>();
		theSuggestions.add(oneSuggestion);
    	suggestions.put("Create CISs", theSuggestions);
    	communityRecommender.identifyCisActionForEgocentricCommunityAnalyser(suggestions, new ArrayList<String>());
    	
		//Assert.assertNull(cisManager.getCisList(new ICisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	@Test
	public void testEgocentricSplitSuggestion() {
		
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		
		
    	communityRecommender = new CommunityRecommender(ownerId, "CSS");
		
    	ICisRecord theNewCis = mock(ICisRecord.class);
    	HashMap<String, ArrayList<ArrayList<ICisRecord>>> suggestions = new HashMap<String, ArrayList<ArrayList<ICisRecord>>>();
		ArrayList<ArrayList<ICisRecord>> theSuggestions = new ArrayList<ArrayList<ICisRecord>>();
		ArrayList<ICisRecord> oneSuggestion = new ArrayList<ICisRecord>();
		theSuggestions.add(oneSuggestion);
    	suggestions.put("Create CISs", theSuggestions);
    	communityRecommender.identifyCisActionForEgocentricCommunityAnalyser(suggestions, new ArrayList<String>());
    	
		//Assert.assertNull(cisManager.getCisList(new ICisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	@Test
	public void testEgocentricConfigurationsOnAllAttributesSuggestions() {
		
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		
		
    	communityRecommender = new CommunityRecommender(ownerId, "CSS");
		
    	ICisRecord theNewCis = mock(ICisRecord.class);
    	HashMap<String, ArrayList<ArrayList<ICisRecord>>> suggestions = new HashMap<String, ArrayList<ArrayList<ICisRecord>>>();
		ArrayList<ArrayList<ICisRecord>> theSuggestions = new ArrayList<ArrayList<ICisRecord>>();
		ArrayList<ICisRecord> oneSuggestion = new ArrayList<ICisRecord>();
		theSuggestions.add(oneSuggestion);
    	suggestions.put("Create CISs", theSuggestions);
    	communityRecommender.identifyCisActionForEgocentricCommunityAnalyser(suggestions, new ArrayList<String>());
    	
		//Assert.assertNull(cisManager.getCisList(new ICisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	@Test
	public void testEgocentricDeletionSuggestion() {
		
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		
		
    	communityRecommender = new CommunityRecommender(ownerId, "CSS");
		
    	ICisRecord theNewCis = mock(ICisRecord.class);
    	HashMap<String, ArrayList<ArrayList<ICisRecord>>> suggestions = new HashMap<String, ArrayList<ArrayList<ICisRecord>>>();
		ArrayList<ArrayList<ICisRecord>> theSuggestions = new ArrayList<ArrayList<ICisRecord>>();
		ArrayList<ICisRecord> oneSuggestion = new ArrayList<ICisRecord>();
		theSuggestions.add(oneSuggestion);
    	suggestions.put("Create CISs", theSuggestions);
    	communityRecommender.identifyCisActionForEgocentricCommunityAnalyser(suggestions, new ArrayList<String>());
    	
		//Assert.assertNull(cisManager.getCisList(new ICisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	@Test
    public void testExtensiveCreationCheck() {
    	
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		cisManager = mock(ICisManager.class);
		userCtxBroker = mock(ICtxBroker.class);
		
    	communityRecommender = new CommunityRecommender(ownerId, "CSS");
		
    	
    	
    	//create some test context data for the CSS?
    	//ServiceResourceIdentifier userInterfacedService = new ServiceResourceIdentifier(/**new URI("Chat system")*/);
    
    	Future <CtxEntity> theEntity = null;
    	try {
			theEntity = userCtxBroker.createEntity("CSS User");
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	//userCtxBroker.addAttribute(ownerIdContextEntity, CtxAttributeValueType.INDIVIDUAL, "friends", IUserCtxBrokerCallback);
    	try {
    		userCtxBroker.createAttribute(entityId, "friends");
		} catch (CtxException e) {
			e.printStackTrace();
		}
    	
    	//create some test context data for the CSS?	
    	//ServiceResourceIdentifier userInterfacedService = new ServiceResourceIdentifier(/**new URI("Chat system")*/);
    	
    	//userCtxBroker = new userCtxBroker();
    	//userCtxBroker.createEntity("CSS User", IUserCtxBrokerCallback);
    	//userCtxBroker.addAttribute(ownerIdContextEntity, CtxAttributeValueType.INDIVIDUAL, "CSS proximity", IUserCtxBrokerCallback);
    	
    	//check user joined CISs before
		communityRecommender.identifyCissToCreate(new ArrayList<ICisRecord>(), new ArrayList<String>());
		//check and compare user joined CISs after
		
		//Assert.assertNotNull(/**User's joined CISs*/);
		
		
		String[] members = new String[1];
		members[0] = "James";
		//Assert.assertNull(cisManager.getCisList(new CisRecord(null, null, null, null, null, members, null, null, null)));
	}
    
    public void setCtxBroker(ICtxBroker userCtxBroker){
		this.userCtxBroker = userCtxBroker;
	}
    
    /**public void setUserCtxBrokerCallback(IUserCtxBrokerCallback userCtxBrokerCallback) {
    	this.userCtxBrokerCallback = userCtxBrokerCallback;
    }*/
    
    public void callbackForUserCtxEntity(CtxEntityIdentifier contextEntityIdentifier) {
    	entityId = contextEntityIdentifier;
    }
    
    public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}
    
    public void contextCallback(CtxEntityIdentifier context) {
    	
    }
    
}
