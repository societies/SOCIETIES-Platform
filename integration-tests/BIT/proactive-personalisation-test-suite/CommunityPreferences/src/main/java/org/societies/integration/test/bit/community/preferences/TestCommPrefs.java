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
package org.societies.integration.test.bit.community.preferences;

import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.integration.test.IntegrationTestCase;
import org.societies.personalisation.preference.api.IUserPreferenceManagement;
import org.societies.personalisation.preference.api.CommunityPreferenceManagement.ICommunityPreferenceManager;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;

/**
 * Class that tests Context Triggered Personalisation. 
 *
 * @author Eliza
 *
 */
public class TestCommPrefs extends IntegrationTestCase{

	private static ICtxBroker ctxBroker;
	private static ICommManager commsMgr;
	private static IUserPreferenceConditionMonitor userPCM;
	private static ICommunityPreferenceManager commPrefMgr;
	private static IUserPreferenceManagement preferenceManager;
	private static ICisManager cisManager;
	
	
	public TestCommPrefs(){
		super(1874, new Class[]{Tester.class});
	}

	/**
	 * @return the ctxBroker
	 */
	public static ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	/**
	 * @param ctxBroker the ctxBroker to set
	 */
	public  void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}


	/**
	 * @return the commsMgr
	 */
	public static ICommManager getCommsMgr() {
		return commsMgr;
	}

	/**
	 * @param commsMgr the commsMgr to set
	 */
	public void setCommsMgr(ICommManager commsMgr) {
		this.commsMgr = commsMgr;
	}


	public static IUserPreferenceConditionMonitor getUserPCM() {
		return userPCM;
	}

	public  void setUserPCM(IUserPreferenceConditionMonitor userPCM) {
		TestCommPrefs.userPCM = userPCM;
		preferenceManager = TestCommPrefs.userPCM.getPreferenceManager();
	}

	public static ICommunityPreferenceManager getCommPrefMgr() {
		return commPrefMgr;
	}

	public  void setCommPrefMgr(ICommunityPreferenceManager commPrefMgr) {
		TestCommPrefs.commPrefMgr = commPrefMgr;
		
	}

	public static IUserPreferenceManagement getPreferenceManager() {
		return preferenceManager;
	}

	public  void setPreferenceManager(IUserPreferenceManagement preferenceManager) {
		TestCommPrefs.preferenceManager = preferenceManager;
	}

	public static ICisManager getCisManager() {
		return cisManager;
	}

	public  void setCisManager(ICisManager cisManager) {
		TestCommPrefs.cisManager = cisManager;
	}
}
