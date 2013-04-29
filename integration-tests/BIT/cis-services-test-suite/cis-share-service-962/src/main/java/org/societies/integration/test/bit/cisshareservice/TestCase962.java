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
package org.societies.integration.test.bit.cisshareservice;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.integration.test.IntegrationTestCase;

/**
 * Test case 1852 aims to test attaching a service to a CIS
 * 
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */

public class TestCase962 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase962.class);

	/**
	 * CIS control manager (injected)
	 */
	private static ICisManager	cisManager;
	
	/**
	 * Service control manager (injected)
	 */
	private static IServiceControl serviceControl;

	/**
	 * Service Registry manager (injected)
	 */
	private static IServiceRegistry serviceRegistry;

	/**
	 * Communication Framework (injected)
	 */
	private static ICommManager commManager;
	
	public TestCase962() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		//super(713, new Class[] {SpecificTestCaseUpperTester.class, NominalTestCaseLowerTester.class});
		super(1852, new Class[] {NominalTestCaseUpperTester.class});
		NominalTestCaseUpperTester.testCaseNumber = 1852;
	}


	public void setCisManager(ICisManager cisManager) {
		if(LOG.isDebugEnabled()) LOG.debug("[#1852] setCisManager()");
		TestCase962.cisManager = cisManager;
	}
	
	public void setServiceControl(IServiceControl serviceControl) {
		if(LOG.isDebugEnabled()) LOG.debug("[#1852] setServiceControl()");
		TestCase962.serviceControl = serviceControl;
	}
	
	public void setServiceRegistry(IServiceRegistry serviceRegistry) {
		if(LOG.isDebugEnabled()) LOG.debug("[#1852] setServiceRegistry()");
		TestCase962.serviceRegistry = serviceRegistry;
	}

	public void setCommManager(ICommManager commManager) {
		if(LOG.isDebugEnabled()) LOG.debug("[#1852] setCommManager()");
		TestCase962.commManager = commManager;
	}
	
	protected static ICisManager getCisManager() {
		return cisManager;
	}
	
	protected static IServiceControl getServiceControl() {
		return serviceControl;
	}
	
	protected static IServiceRegistry getServiceRegistry(){
		return serviceRegistry;
	}

	protected static ICommManager getCommManager() {
		return commManager;
	}
}
