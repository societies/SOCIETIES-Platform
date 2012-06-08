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
package org.societies.integration.test.bit.automatically_merge_ciss;

import java.util.ArrayList;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.integration.test.IntegrationTestCase;

import org.societies.orchestration.api.ICisProposal;

/**
 * Test class for automatically creating a CIS via T5.1 components
 *
 */
public class Test1023 extends IntegrationTestCase{

	private static ICtxBroker ctxBroker;
	private static IUserActionMonitor uam;
	private static ICommManager commsMgr;
	private static IHelloWorld helloWorld;
	
	public Test1023(){
		super(1023, new Class[]{CreateUserData.class});
		mergeCiss();
	}
	
	public void init() {
		
	}
	
	public void mergeCiss() {
		ArrayList<String> sharedData = addSharedAddress();
		mergeWithECA(sharedData);
		mergeWithCSCW(sharedData);
		mergeWithCSM(sharedData);
	}
	
	
	public void mergeCissForSharedAddress() {
		ArrayList<String> sharedData = addSharedAddress();
		mergeWithECA(sharedData);
		mergeWithCSCW(sharedData);
		mergeWithCSM(sharedData);
	}
	
    public ArrayList<String> addSharedAddress() {
		return new ArrayList<String>();
	}
    
    public void createCsss() {
    	
    }
    
    public void addRecentSharedProximityHistory() {
    	
    }
    
    public void addLongTermSharedProximityHistory() {
    	
    }
    
    public void addSharedCssDirectoryMembers() {
    	
    }
	
	public void mergeWithECA(ArrayList<String> sharedData) {

		//EgocentricCommunityAnalyser eca = new EgocentricCommunityAnalyser();
		//eca.identifyCissToCreate();
	}
	
    public void mergeWithCSCW(ArrayList<String> sharedData) {
    	//CollaborationPatternAnalyser cpa = new CollaborationPatternAnalyser();
    	//cpa.triggerAlgorithm();
	}

    public void mergeWithCSM(ArrayList<String> sharedData) {

		//ContextStateModelsAnalyser csm = new ContextStateModelsAnalyser();
		//csm.triggerModelChecking();
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
	 * @return the uam
	 */
	public static IUserActionMonitor getUam() {
		return uam;
	}

	/**
	 * @param uam the uam to set
	 */
	public void setUam(IUserActionMonitor uam) {
		this.uam = uam;
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

	/**
	 * @return the helloWorld
	 */
	public static IHelloWorld getHelloWorld() {
		return helloWorld;
	}

	/**
	 * @param helloWorld the helloWorld to set
	 */
	public void setHelloWorld(IHelloWorld helloWorld) {
		this.helloWorld = helloWorld;
	}
}
