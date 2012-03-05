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

package org.societies.orchestration.CommunityLifecycleManagement.test;

//import org.societies.context.broker.api.IUserCtxBroker;
//import org.societies.context.broker.api.ICommunityCtxBroker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;


import org.societies.orchestration.CommunityLifecycleManagement.impl.CommunityLifecycleManagement;
import org.societies.api.identity.IIdentity;
//import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxModelType;
//import org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.internal.cis.management.CisRecord;
//import org.societies.api.mock.EntityIdentifier;
import org.societies.api.identity.IdentityType;
//import org.societies.api.comm.xmpp.datatypes.IdentityType;

/**
 * This is the test class for the Community Lifecycle Management component
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class CommunityLifecycleManagementTest {
	
	private CommunityLifecycleManagement communityLifecycleManagement;
	//@Test
	//public void testLoop() {
	//	CommunityLifecycleManagement.loop();
	//}
	
	@Test  
	  public void setUp() {  
	          IIdentity linkedCss = mock(IIdentity.class); 
	          //classUnderTest = new MathServiceConsumer(1,1);
	          //classUnderTest.setLinkedCss(linkedCss);   
	      }  

	
	@Test
	public void testSetup() {
		IIdentity linkedCss = mock(IIdentity.class); 
		CommunityLifecycleManagement communityLifecycleManagement = new CommunityLifecycleManagement(linkedCss, "CSS");
		//communityLifecycleManagement = new CommunityLifecycleManagement(new Identity(IdentityType.CSS, "Test", "TestDomain"), "Domain");
		//communityLifecycleManagement = new CommunityLifecycleManagement(new CisRecord(null, null, null, null, null, null, null, null));
	}
	
	@Test
	public void testProcessPreviousShortTimeCycle() {
		IIdentity linkedCss = mock(IIdentity.class); 
		//new CommunityLifecycleManagement(new Identity(IdentityType.CSS, "Test", "TestDomain").processPreviousShortTimeCycle();
	}
	
	@Test
	public void testProcessPreviousLongTimeCycle() {
		IIdentity linkedCss = mock(IIdentity.class); 
		//new CommunityLifecycleManagement(new Identity(), "Test").processPreviousLongTimeCycle();
	}
	
}