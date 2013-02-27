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
package org.societies.integration.performance.test.upper_tester.rafik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.directory.ICssDirectoryCallback;
import org.societies.api.css.directory.ICssDirectoryRemote;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.integration.performance.test.lower_tester.PerformanceLowerTester;
import org.societies.integration.performance.test.lower_tester.PerformanceTestMgmtInfo;
import org.societies.integration.performance.test.lower_tester.PerformanceTestResult;
import org.societies.integration.performance.test.upper_tester.rafik.cssmgmt.CssDirectoryCallback;
import org.societies.integration.performance.test.upper_tester.rafik.cssmgmt.ICssMgmtPerfromanceTest;
import org.societies.integration.performance.test.upper_tester.rafik.cssmgmt.SendMultiFriendInvParameters;

/**
 *
 * @author Rafik
 *
 */
public class CssMgmtPerformanceTestImpl implements ICssMgmtPerfromanceTest {

	private static Logger LOG = LoggerFactory.getLogger(CssMgmtPerformanceTestImpl.class);
	
	private ICSSInternalManager cssManager;
	private ICssDirectoryRemote cssDirectoryRemote;
	private ICssDirectoryCallback cssDirectoryCallback;
	
	private ICommManager commManager;
	
	private PerformanceLowerTester performanceLowerTester;
	private PerformanceTestResult performanceTestResult;
	
	public CssMgmtPerformanceTestImpl(){	
	}
	
	public void setCommManager(ICommManager commManager) {
		if (null != commManager) 
		{
			LOG.info("### [PerformanceTestImpl] commManager injected");
			this.commManager = commManager;
		}
	}
	
	public ICommManager getCommManager()
	{
		if (null != commManager) {
			LOG.info("### [PerformanceTestImpl] this.commManager not null");
			return commManager;
		}
		else {
			LOG.info("### [PerformanceTestImpl] this.commManager null");
			return null;
		}
	}
	
	public ICssDirectoryRemote getCssDirectoryRemote() {
		return cssDirectoryRemote;
	}

	public void setCssDirectoryRemote(ICssDirectoryRemote cssDirectoryRemote) {
		this.cssDirectoryRemote = cssDirectoryRemote;
	}
	
	public ICSSInternalManager getCssManager() 
	{
		if (null != cssManager) {
			LOG.info("### [PerformanceTestImpl] this.cssManager not null");
			return cssManager;
		}
		else {
			LOG.info("### [PerformanceTestImpl] this.cssManager null");
			return null;
		}
	}
	
	public void setCssManager(ICSSInternalManager cssManager) {
		if (null != cssManager) 
		{
			LOG.info("### [PerformanceTestImpl] cssManager injected");
			this.cssManager = cssManager;
		}
	}
	
	@Override
	public void sendMultiFriendInvTest(PerformanceTestMgmtInfo performanceTestMgmtInfo, SendMultiFriendInvParameters sendMultiFriendInvParameters) {
		
		//The following 2 lines are mandatory in the beginning of the test 
		performanceLowerTester = new PerformanceLowerTester(performanceTestMgmtInfo);
		performanceLowerTester.testStart(this.getClass().getName(), getCommManager());
		
		if (null != sendMultiFriendInvParameters) 
		{
			String cssInvInitiatorId = sendMultiFriendInvParameters.getCssInvInitiatorId();
			
			String thisCss = null;
			if (null != getCommManager()) 
			{ 
				thisCss = getCommManager().getIdManager().getThisNetworkNode().getBareJid();
			}

			if (null != thisCss && null != cssInvInitiatorId && !"".equals(cssInvInitiatorId)) 
			{
				//This node is the initiator or the invitation
				if (thisCss.equals(cssInvInitiatorId)) 
				{	
					cssDirectoryCallback = new CssDirectoryCallback(performanceLowerTester, cssManager); 
					
					cssDirectoryRemote.findAllCssAdvertisementRecords(cssDirectoryCallback);
					
				}
//				//This node will receive the invitation
//				else
//				{
//					
//				}
			}
			else
			{
				performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The cssInvInitiatorId is null or empty and/or commsManager is null !",
						PerformanceTestResult.ERROR_STATUS);
				performanceLowerTester.testFinish(performanceTestResult);
			}
		}
		else
		{
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "sendMultiFriendInvParameters are null!",
					PerformanceTestResult.ERROR_STATUS);
			performanceLowerTester.testFinish(performanceTestResult);
		}
		
		
	}

}
