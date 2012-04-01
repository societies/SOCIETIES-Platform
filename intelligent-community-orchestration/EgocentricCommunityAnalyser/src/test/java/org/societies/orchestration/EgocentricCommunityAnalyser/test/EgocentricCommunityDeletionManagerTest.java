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

package org.societies.orchestration.EgocentricCommunityAnalyser.test;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import org.societies.orchestration.EgocentricCommunityAnalyser.impl.EgocentricCommunityDeletionManager;
import org.societies.api.identity.IIdentity;
//import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.context.model.CtxEntityIdentifier;
//import org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.mock.EntityIdentifier;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import org.societies.api.cis.management.ICisRecord;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisSubscribed;
import org.societies.api.cis.management.ICisEditor;
import org.societies.api.cis.management.ICisActivity;
import org.societies.api.cis.management.ICisActivityFeed;

import org.societies.api.css.directory.ICssDirectory;
import org.societies.api.css.directory.ICssAdvertisementRecord;

import org.societies.api.css.management.ICssRecord;

import org.societies.orchestration.api.ISuggestedCommunityAnalyser;

/**
 * This is the test class for the Egocentric Community Deletion Manager component
 * 
 * @author Fraser Blackmun
 * @version 1
 * 
 */

public class EgocentricCommunityDeletionManagerTest {
	
	private EgocentricCommunityDeletionManager autoCommunityDeletionManager;
	private ICisManager cisManager;
	private ISuggestedCommunityAnalyser suggestedCommunityAnalyser;
	
	@Test
	public void testIdentifyCissToDelete() {
		
		suggestedCommunityAnalyser = mock(ISuggestedCommunityAnalyser.class);
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		//create CIS for James, with last activity being 1 year ago

		cisManager = mock(ICisManager.class);
		ICisRecord cisRecord = mock(ICisRecord.class);
		//ICisRecord jamesCis = cisManager.createCis("James", "James CIS");
		
    	autoCommunityDeletionManager = new EgocentricCommunityDeletionManager(ownerId, "CSS");
    	autoCommunityDeletionManager.setCisManager(cisManager);
    	autoCommunityDeletionManager.setLinkedCss(ownerId);
    	autoCommunityDeletionManager.setSuggestedCommunityAnalyser(suggestedCommunityAnalyser);
    	
    	//when(cisManager.getCiss()).thenReturn(cisRecord);
    	
		autoCommunityDeletionManager.identifyCissToDelete(null);
		
		String[] members = new String[1];
		members[0] = "James";
		//the CIS should have been deleted
		//verify(cisManager.getCiss());
		//Assert.assertNull(cisManager.getCisList(new CisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	@Test
	public void testIdentifyShortTermCissToDelete() {
		
		suggestedCommunityAnalyser = mock(ISuggestedCommunityAnalyser.class);
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		//create CIS for James, with last activity being 2 hours ago

		cisManager = mock(ICisManager.class);
		//ICisRecord jamesCis = cisManager.createCis("James", "James CIS");
		
		//Date date = new Date();
		//Timestamp time = new Timestamp(date.getTime() - (1000 * 60 * 60 * 2) - 1);
		//jamesCis.setActivityFeed(new CisActivityFeed(new Activity(time + "")));
		
    	autoCommunityDeletionManager = new EgocentricCommunityDeletionManager(ownerId, "CSS");
		
    	autoCommunityDeletionManager.setCisManager(cisManager);
    	autoCommunityDeletionManager.setLinkedCss(ownerId);
    	autoCommunityDeletionManager.setSuggestedCommunityAnalyser(suggestedCommunityAnalyser);
    	
		autoCommunityDeletionManager.identifyCissToDelete(null);
		
		String[] members = new String[1];
		members[0] = "James";
		//the CIS should have been deleted
		
		//Assert.assertNull(cisManager.getCisList(new CisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	@Test
	public void testIdentifyMediumTermCissToDelete() {
		
		suggestedCommunityAnalyser = mock(ISuggestedCommunityAnalyser.class);
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		//create CIS for James, with last activity being 2 weeks ago

		cisManager = mock(ICisManager.class);
		//ICisRecord jamesCis = cisManager.createCis("James", "James CIS");
		
		//Date date = new Date();
				//Timestamp time = new Timestamp(date.getTime() - (1000 * 60 * 60 * * 24 * 14) - 1);
				//jamesCis.setActivityFeed(new CisActivityFeed(new Activity(time + "")));
		
    	autoCommunityDeletionManager = new EgocentricCommunityDeletionManager(ownerId, "CSS");
		
    	autoCommunityDeletionManager.setCisManager(cisManager);
    	autoCommunityDeletionManager.setLinkedCss(ownerId);
    	autoCommunityDeletionManager.setSuggestedCommunityAnalyser(suggestedCommunityAnalyser);
    	
		autoCommunityDeletionManager.identifyCissToDelete(null);
		
		String[] members = new String[1];
		members[0] = "James";
		//the CIS should have been deleted
		
		//Assert.assertNull(cisManager.getCisList(new CisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	@Test
	public void testIdentifyLongTermCissToDelete() {
		
		suggestedCommunityAnalyser = mock(ISuggestedCommunityAnalyser.class);
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		//create CIS for James, with last activity being 6 months year ago

		cisManager = mock(ICisManager.class);
		//ICisRecord jamesCis = cisManager.createCis("James", "James CIS");
		
		//Date date = new Date();
				//Timestamp time = new Timestamp(date.getTime() - (1000 * 60 * 60 * 24 * 4 * 6) - 1);
				//jamesCis.setActivityFeed(new CisActivityFeed(new Activity(time + "")));
		
    	autoCommunityDeletionManager = new EgocentricCommunityDeletionManager(ownerId, "CSS");
		
    	autoCommunityDeletionManager.setCisManager(cisManager);
    	autoCommunityDeletionManager.setLinkedCss(ownerId);
    	autoCommunityDeletionManager.setSuggestedCommunityAnalyser(suggestedCommunityAnalyser);
    	
		autoCommunityDeletionManager.identifyCissToDelete(null);
		
		String[] members = new String[1];
		members[0] = "James";
		//the CIS should have been deleted
		
		//Assert.assertNull(cisManager.getCisList(new CisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	@Test
	public void testIdentifyOngoingCissToDelete() {
		
		suggestedCommunityAnalyser = mock(ISuggestedCommunityAnalyser.class);
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		//create CIS for James, with no members for the first time in its history since creation

		cisManager = mock(ICisManager.class);
		//ICisRecord jamesCis = cisManager.createCis("James", "James CIS");
		
		//Date date = new Date();
		//Timestamp time = new Timestamp(date.getTime() - (1000 * 60 * 60 * 2));
		//jamesCis.setActivityFeed(new CisActivityFeed(new Activity(time + "")));
		
    	autoCommunityDeletionManager = new EgocentricCommunityDeletionManager(ownerId, "CSS");
		
    	autoCommunityDeletionManager.setCisManager(cisManager);
    	autoCommunityDeletionManager.setLinkedCss(ownerId);
    	autoCommunityDeletionManager.setSuggestedCommunityAnalyser(suggestedCommunityAnalyser);
    	
		autoCommunityDeletionManager.identifyCissToDelete(null);
		
		String[] members = new String[1];
		members[0] = "James";
		//the CIS should have been deleted
		
		//Assert.assertNull(cisManager.getCisList(new CisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	@Test
	public void testIdentifyNoCissToDelete() {
		
		suggestedCommunityAnalyser = mock(ISuggestedCommunityAnalyser.class);
		IIdentity ownerId = mock(IIdentity.class); //James Jents CSS
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId.toString(), "James Jents", new Long(1));
    	
		//create CIS for James, that match none of deletion criteria but come close.

		cisManager = mock(ICisManager.class);
		//ICisRecord jamesCis = cisManager.createCis("James", "James CIS");
		
    	autoCommunityDeletionManager = new EgocentricCommunityDeletionManager(ownerId, "CSS");
		
    	autoCommunityDeletionManager.setCisManager(cisManager);
    	autoCommunityDeletionManager.setLinkedCss(ownerId);
    	autoCommunityDeletionManager.setSuggestedCommunityAnalyser(suggestedCommunityAnalyser);
    	
		autoCommunityDeletionManager.identifyCissToDelete(null);
		
		String[] members = new String[1];
		members[0] = "James";
		//the CIS should have been deleted
		
		//Assert.assertNull(cisManager.getCisList(new CisRecord(null, null, null, null, null, members, null, null, null)));
	}
	
	public void setCisManager(ICisManager cisManager){
		this.cisManager = cisManager;
	}
	
}
